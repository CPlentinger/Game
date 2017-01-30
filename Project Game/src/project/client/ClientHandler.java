package project.client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import project.game.ComputerPlayer;
import project.game.HumanPlayer;
import project.game.Mark;
import project.game.Player;
import project.server.Protocol;

public class ClientHandler extends Thread {

  private Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  private Player player;
  private Player hintBot;
  private boolean gameOver;
  
  public ClientHandler(Socket sock, String username, Player p) throws IOException {
    this.server = sock;
    this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    this.player = p;
    player.setName(username);
    this.hintBot = new ComputerPlayer(1);
    this.gameOver = false;
  }
  
  public void run() {
    System.out.println("Waiting for opponent...");
    readInput();
  }

  private void readInput() {
    String message;
    try {
      while ((message = in.readLine()) != null) {
        handleInput(message);
        if (gameOver) {
          break;
        }
      }
    } catch (NullPointerException e) {
      handleEnd(Protocol.Server.NOTIFYEND + " 3 ");
    } catch (SocketException e) {
      handleEnd(Protocol.Server.NOTIFYEND + " 3 " + player.getID());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void handleInput(String message) {
    Scanner inScanner = new Scanner(message);
    switch (inScanner.next()) {
      case Protocol.Server.SERVERCAPABILITIES:
        writeOutput(Protocol.Client.SENDCAPABILITIES + " 2 " + player.getName() + " 0 4 4 4 4 0 0");
        break;
      case Protocol.Server.ASSIGNID:
        player.setID(inScanner.nextInt());
        hintBot.setID(player.getID());
        break;
      case Protocol.Server.STARTGAME: startGame(message);
        break;
      case Protocol.Server.TURNOFPLAYER:
        if (inScanner.nextInt() == player.getID()) {
          askMove();
        } else {
          System.out.println("Waiting for player " + player.getOpponentName() + " (" + player.getMark().Other().toString() + ")...");
        }
        break;
      case Protocol.Server.NOTIFYMOVE: 
        setMove(message);
        break;
      case Protocol.Server.NOTIFYEND:
        handleEnd(message);
        break;
      default: System.out.println(message);
        break;
    }
    inScanner.close();
  }

  private void handleEnd(String endMessage) {
    String winCode = endMessage.split(" ")[1];
    int playerid = 0;
    if (endMessage.split(" ").length == 3) {
      playerid = Integer.parseInt(endMessage.split(" ")[2]);
    }
    switch (winCode) {
      case "1":
        if (playerid == player.getID()) {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getName() + "!");
        } else {
          System.out.println("You lost the game. Better luck next time.");
        }
        break;
      case "2":
        System.out.println(Protocol.getWin("2"));
        break;
      case "3":
        System.out.println(Protocol.getWin("3"));
        if (playerid == player.getID()) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getOpponentName() + "!");
        }
        break;
      case "4":
        System.out.println("\n" + Protocol.getWin("4"));
        if (playerid == player.getID()) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Congratulations " + player.getOpponentName() + "!");
        }
      default: break;
    }
    String nextGame = player.makeMove("Do you want to play another game? (y/n)");
    if (nextGame.equals("y")) {
      try {
        new ClientHandler(new Socket(server.getInetAddress(), server.getPort()),player.getName(), player).start();
        shutdown();
      } catch (ConnectException e) {
        System.out.println("Couldn't connect to the server.");
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Game ended, thanks for playing!");
    }
  }

  private void shutdown() {
    try {
      server.close();
      gameOver = true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void askMove() {
    if (player instanceof HumanPlayer) {
      hintBot.setBoard(player.getBoard());
      System.out.println(hintBot.makeMove("hint: "));
    }
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Player ");
    stringBuilder.append(player.getName());
    stringBuilder.append(" (");
    stringBuilder.append(player.getMark().toString());
    stringBuilder.append("), ");
    stringBuilder.append("make your move (i.e.: x y):");
    String move = player.makeMove(stringBuilder.toString());
    if (player instanceof ComputerPlayer) {
      System.out.println(move);
    }
    int xpos = 0;
    int ypos = 0;
    try {
      xpos = Integer.parseInt(move.split(" ")[0]);
      ypos = Integer.parseInt(move.split(" ")[1]);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: x and y value seperated by a space.");
      askMove();
    }
    if (player.checkMove(xpos, ypos)) {
      writeOutput(Protocol.Client.MAKEMOVE + " " + move);
    } else {
      System.out.println("Please choose a empty field on the board!");
      askMove();
    }
    
  }
  
  private void setMove(String message) {
    int xpos = Integer.parseInt(message.split(" ")[2]);
    int ypos = Integer.parseInt(message.split(" ")[3]);
    if (message.split(" ")[1].equals(String.valueOf(player.getID()))) {
      player.setField(xpos, ypos, player.getMark());
    } else {
      player.setField(xpos, ypos, player.getMark().Other());
    }
  }
  
  private void writeOutput(String message) {
      try {
        out.write(message);
        out.newLine();
        out.flush();
      } catch (SocketException e) {
        handleEnd(Protocol.Server.NOTIFYEND + " 4 " + player.getID());
      } catch (IOException e) {
        e.printStackTrace();
      }
  }
  
  private void startGame(String message) {
    System.out.println("Starting new game.");
    String[] players = message.substring(18).split(" ");
    for (String playerInfo : players) {
      if (playerInfo.startsWith(String.valueOf(player.getID()))) {
        if (playerInfo.split("\\|")[2].equals("ff0000")) {
          player.setMark(Mark.X);
          hintBot.setMark(Mark.X);
        } else {
          player.setMark(Mark.O);
          hintBot.setMark(Mark.O);
        }
      } else {
        player.setOpponentName(playerInfo.split("\\|")[1]);
      }
    }
    player.buildBoard(message.substring(10,15));
  }
}
