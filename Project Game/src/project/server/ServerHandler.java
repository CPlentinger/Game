package project.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

import project.game.HumanPlayer;
import project.game.Mark;
import project.game.Player;
import project.server.Protocol.Client;
import project.server.Protocol.Server;

public class ServerHandler extends Thread {
  private Socket client1;
  private Socket client2;
  private String gameCapabilities;
  private int c1ID;
  private int c2ID;
  private BufferedReader in;
  private BufferedWriter out;
  private int curTurnID;
  private int curStreams;
  private Player serverGame;
  private boolean gameEnd;
  
  public ServerHandler(Socket sock1, Socket sock2, String capabilities) throws IOException {
    this.client1 = sock1;
    this.client2 = sock2;
    this.gameCapabilities = capabilities;
    this.c1ID = Integer.parseInt((gameCapabilities.split(" ")[2].split("\\|")[0]));
    this.c2ID = Integer.parseInt((gameCapabilities.split(" ")[3].split("\\|")[0]));
    this.curStreams = c1ID;
    this.in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
    this.curTurnID = c1ID;
    this.gameEnd = false;
  }
  
  public void run() {
    startGame(gameCapabilities);
    while (!client1.isClosed() || !client2.isClosed()) {
      writeBoth(Protocol.Server.TURNOFPLAYER + " " + curTurnID);
      readInput();
      if (gameEnd) {
        shutdown();
        break;
      }
      changeStreams();
      changeTurn();
    }
  }

  private void readInput() {
    String clientInput;
    try {
      if ((clientInput = in.readLine()) != null) {
        System.out.println("in player " + curStreams + ": " + clientInput);
        handleInput(clientInput);
      }
    } catch (SocketTimeoutException e) {
      writeBoth(Protocol.Server.NOTIFYEND + " 4 " + curTurnID);
      gameEnd = true;
    } catch (SocketException e) {
      changeStreams();
      writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnID);
      gameEnd = true;
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

  private void handleInput(String input) {
      Scanner inScanner = new Scanner(input);
      switch (inScanner.next()) {
        case Protocol.Client.MAKEMOVE:
          makeMove(inScanner.nextInt(), inScanner.nextInt());
          break;
      }
      inScanner.close();
  }
  
  private void writeOutput(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
      System.out.println("out player " + curStreams + ": " + message);
    } catch (SocketException e) {
      changeStreams();
      writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnID);
      gameEnd = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void writeBoth(String message) {
    writeOutput(message);
    changeStreams();
    writeOutput(message);
    changeStreams();
  }
  
  private void startGame(String gameCapabilities) {
    System.out.println("Starting new game.");
    writeBoth(gameCapabilities);
    serverGame = new HumanPlayer();
    serverGame.buildBoard(gameCapabilities.substring(10, 15));
    try {
      client1.setSoTimeout(120000);
      client2.setSoTimeout(120000);
    } catch (SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private Mark getMark() {
    if (curTurnID == c1ID) {
      return Mark.O;
    } else {
      return Mark.X;
    }
  }
  
  
  private void makeMove(int x, int y) {
    if (serverGame.checkMove(x, y)) {
      serverGame.setField(x, y, getMark());
      writeBoth(Protocol.Server.NOTIFYMOVE + " " + curTurnID + " " + x + " " + y);
      String endMessage;
      if ((endMessage = serverGame.endGameCheck(curTurnID)) != null) {
        writeBoth(endMessage);
        gameEnd = true;
      }
    } else {
      writeOutput(Protocol.Server.TURNOFPLAYER + " " + curTurnID);
      readInput();
    }
  }
  
  private void changeStreams() {
    if (curStreams == c1ID) {
      try {
        in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      curStreams = c2ID;
    } else {
      try {
      in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      curStreams = c1ID;
    }
  }
  
  private void changeTurn() {
    if (curTurnID == c1ID) {
      curTurnID = c2ID;
      System.out.println("Changed turn to: Player " + curTurnID);
    } else {
      curTurnID = c1ID;
      System.out.println("Changed turn to: Player " + curTurnID);
    }
  }
}
