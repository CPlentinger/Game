

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

public class ServerHandler extends Thread {

  private Socket client1;
  private Socket client2;
  private String capabilitiesC1;
  private String capabilitiesC2;
  private BufferedReader in;
  private BufferedWriter out;
  private int curTurnID;
  private String gameCapabilities;
  private HumanPlayer serverGame;
  private boolean timeout;
  
  public ServerHandler(Socket sock1, Socket sock2) throws IOException {
    this.client1 = sock1;
    this.client2 = sock2;
    this.in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
    this.curTurnID = 1;
    this.timeout = false;
  }
  
  public void run() {
    writeBoth(Protocol.Server.SERVERCAPABILITIES + " 2 0 4 4 4 4 0");
    readInput();
    while (!client1.isClosed() || !client2.isClosed()) {
      writeBoth(Protocol.Server.TURNOFPLAYER + " " + curTurnID);
      readInput();
      String endMessage;
      if ((endMessage = serverGame.endGameCheck()) != null) {
        writeBoth(endMessage);
        shutdown();
      }
      if (timeout) {
        shutdown();
      }
    }
  }

  public void readInput() {
    String clientInput;
    try {
      while ((clientInput = in.readLine()) != null) {
        handleInput(clientInput);
        break;
      }
    } catch (SocketTimeoutException e) {
      writeBoth(Protocol.Server.NOTIFYEND + " 4 " + curTurnID);
      timeout = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void shutdown() {
    try {
      client1.close();
      client2.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void handleInput(String input) {
      Scanner inScanner = new Scanner(input);
      switch (inScanner.next()) {
        case Protocol.Client.SENDCAPABILITIES: handleCapabilities(input);
        break;
        case Protocol.Client.MAKEMOVE:         makeMove(inScanner.nextInt(), inScanner.nextInt());
                                 break;
        case Protocol.Server.NOTIFYEND: writeOutput("Game ended, thanks for playing!");
                                        break;
      }
      inScanner.close();
  }
  
  public void writeOutput(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void writeBoth(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
      changeTurn();
      out.write(message);
      out.newLine();
      out.flush();
      changeTurn();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void handleCapabilities(String capabilities) {
    if (curTurnID == 1) {
      capabilitiesC1 = capabilities.substring(16);
      changeTurn();
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
      joiner.add(" " + maxRoomDimensionX);
      joiner.add(String.valueOf(maxRoomDimensionY));
      joiner.add(String.valueOf(maxRoomDimensionZ));
      joiner.add(lengthToWin + " " + p1ID);
      joiner.add(p1Name);
      joiner.add("0000ff 2");
      joiner.add(p2Name);
      joiner.add("ff0000");
      gameCapabilities = joiner.toString();
      C1.close();
      C2.close();
      startGame(gameCapabilities);
    }
  }
  
  public void startGame(String gameCapabilities) {
    writeBoth("Starting new game.");
    writeBoth(Protocol.Server.STARTGAME + gameCapabilities);
    serverGame = new HumanPlayer();
    serverGame.buildBoard(gameCapabilities.substring(1, 6));
    try {
      client1.setSoTimeout(120000);
      client2.setSoTimeout(120000);
    } catch (SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
      changeTurn();
    } else {
      writeOutput(Protocol.Server.TURNOFPLAYER + " " + curTurnID);
      readInput();
    }
  }
  public void changeTurn() {
    if (curTurnID == 1) {
      try {
        in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      curTurnID = 2;
    } else {
      try {
      in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      curTurnID = 1;
    }
  }
}
