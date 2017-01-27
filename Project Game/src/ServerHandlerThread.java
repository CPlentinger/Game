

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

public class ServerHandlerThread extends Thread {

  private Socket client1;
  private Socket client2;
  private String capabilitiesC1;
  private String capabilitiesC2;
  private BufferedReader c1in;
  private BufferedWriter c1out;
  private BufferedReader c2in;
  private BufferedWriter c2out;
  private BufferedReader curTurnIn;
  private BufferedWriter curTurnOut;
  private int curTurnID;
  private String gameCapabilities;
  private Controller serverGame;
  
  public ServerHandlerThread(Socket sock1, Socket sock2) throws IOException {
    this.client1 = sock1;
    this.client2 = sock2;
    this.c1in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.c1out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
    this.c2in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
    this.c2out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
    this.curTurnIn = c1in;
    this.curTurnOut = c1out;
    this.curTurnID = 1;
  }
  
  public void run() {
    writeBoth(Protocol.Server.SERVERCAPABILITIES + " 2 0 4 4 4 4 0");
    readInput();
    changeTurn();
    readInput();
    endGameCheck();
    startGame();
    while (true) {
      writeBoth(Protocol.Server.TURNOFPLAYER + " " + curTurnID);
      readInput();
      changeTurn();
    }
    
  }
  
  private void endGameCheck() {
  }

  public void readInput() {
    String clientInput;
    try {
      while ((clientInput = curTurnIn.readLine()) != null) {
        handleInput(clientInput);
        break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void handleInput(String input) throws IOException {
      Scanner inScanner = new Scanner(input);
      switch (inScanner.next()) {
        case "sendCapabilities": handleCapabilities(input);
                                 break;
        case "makeMove":         makeMove(inScanner.nextInt(), inScanner.nextInt());
                                 break;
      }
      inScanner.close();
  }
  
  public void writeOutput(String message) {
    try {
      curTurnOut.write(message);
      curTurnOut.newLine();
      curTurnOut.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void writeBoth(String message) {
    try {
      c1out.write(message);
      c1out.newLine();
      c1out.flush();
      c2out.write(message);
      c2out.newLine();
      c2out.flush();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void handleCapabilities(String capabilities) {
    if (curTurnIn.equals(c1in)) {
      capabilitiesC1 = capabilities.substring(16);
    } else {
      capabilitiesC2 = capabilities.substring(16);
      Scanner C1 = new Scanner(capabilitiesC1);
      Scanner C2 = new Scanner(capabilitiesC2);
      int amountOfPlayers = Math.min(C1.nextInt(), C2.nextInt());
      String p1Name = C1.next();
      String p2Name = C2.next();
      int p1ID = 1;
      int p2ID = 2;
      int roomSupport = Math.min(C1.nextInt(), C2.nextInt());
      int maxRoomDimensionX = Math.min(C1.nextInt(), C2.nextInt());
      int maxRoomDimensionY = Math.min(C1.nextInt(), C2.nextInt());
      int maxRoomDimensionZ = Math.min(C1.nextInt(), C2.nextInt());  
      int lengthToWin = Math.min(C1.nextInt(), C2.nextInt());
      int chatSupport = Math.min(C1.nextInt(), C2.nextInt());
      int autoRefresh = Math.min(C1.nextInt(), C2.nextInt());
      StringJoiner joiner = new StringJoiner("|");
      joiner.add(" " + maxRoomDimensionX).add(String.valueOf(maxRoomDimensionY)).add(String.valueOf(maxRoomDimensionZ)).add(lengthToWin + " " + p1ID).add(p1Name).add("0000ff 2").add(p2Name).add("ff0000");
      gameCapabilities = joiner.toString();
    }
  }
  
  public void startGame() {
    writeBoth(Protocol.Server.STARTGAME + gameCapabilities);
    serverGame = new Controller(gameCapabilities.substring(1, 6));
    changeTurn();
  }
  
  public Mark getMark() {
    if (curTurnID == 1) {
      return Mark.O;
    } else {
      return Mark.X;
    }
  }
  
  
  public void makeMove(int x, int y) {
    if (serverGame.checkMove(x, y)) {
      serverGame.makeMove(x, y, getMark());
      writeBoth(Protocol.Server.NOTIFYMOVE + " " + curTurnID + " " + x + " " + y);
    } else {
      writeOutput("Wrong Move");
    }
  }
  public void changeTurn() {
    if (curTurnID == 1) {
      curTurnIn = c2in;
      curTurnOut = c2out;
      curTurnID = 2;
    } else {
      curTurnIn = c1in;
      curTurnOut = c1out;
      curTurnID = 1;
    }
  }
}
