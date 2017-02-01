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

/**
 * The <code>ClientHandler</code> handles communication between the server and player.
 */
public class ClientHandler extends Thread {

  private /*@ spec_public @*/ Socket server;
  private BufferedReader in;
  private BufferedWriter out;
  private /*@ spec_public @*/ Player player;
  private Player hintBot;
  private /*@ spec_public @*/ boolean gameOver;
  
  /**
   * <code>ClientHandler</code> is created and parameters are stored.
   * A buffered reader and writer gets assigned to the input/output stream of the server socket.
   * A new <code>ComputerPlayer</code> is created to suggest hints to the user.
   * @param sock , the socket connection with the server.
   * @param username , the name of the player. Gets assigned using <code>setName()</code>.
   * @param tplayer , the player either ComputerPlayer or HumanPlayer.
   */
  /*@ requires sock.isConnected() && !username.contains(" ") &&
    @ tplayer instanceof HumanPlayer || tplayer instanceof ComputerPlayer;
    @ ensures server == sock && player == tplayer &&
    @ player.getName() == username && gameOver == false;
    @*/
  public ClientHandler(Socket sock, String username, Player tplayer) {
    this.server = sock;
    try {
      this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
      this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      shutdown();
    }
    this.player = tplayer;
    player.setName(username);
    this.hintBot = new ComputerPlayer(1);
    this.gameOver = false;
  }
  
  /**
   * Start of the thread.
   * Starts <code>readInput()</code>.
   */
  // JML not applicable here.
  public void run() {
    System.out.println("Waiting for opponent...");
    readInput();
  }

  /**
   * Reads input from the <code>BufferedReader</code> (message from the server),
   * the message gets passed over to <code>handleInput()</code> where it will be handled.
   * If the <code>BufferedReader</code> returns null or there is a <code>socketException</code>,
   * the client is disconnected and <code>handleEnd()</code> is used.
   */
  //requires in.readLine() != null && !gameOver;
  private void readInput() {
    String message;
    try {
      while ((message = in.readLine()) != null) {
        handleInput(message);
        if (gameOver) {
          break;
        }
      }
    } catch (NullPointerException exc) {
      handleEnd(Protocol.Server.NOTIFYEND + " 3 ");
    } catch (SocketException exc) {
      handleEnd(Protocol.Server.NOTIFYEND + " 3 " + player.getId());
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      shutdown();
    }
  }
  
  /**
   * writes the input string to the server as a new line.
   * In case writing the message results in a <code>SocketException</code>,
   * the <code>client</code> will handle it as a disconnect and end the game.
   * @param message, text to write to the server as new line.
   */
  //@ requires message != null && server.isConnected();
  private void writeOutput(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
    } catch (SocketException exc) {
      System.out.println(exc.getMessage());
      handleEnd(Protocol.Server.NOTIFYEND + " 3 " + player.getId());
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      shutdown();
    }
  }
  
  /**
   * Handles messages received from <code>readInput</code>.
   * If the server capabilities are received,
   * the client sends his capabilities using <code>writeOutput()</code>.
   * If the server assigns an ID to this client, the client sets the ID of the <code>Player</code>
   * If the server sends the game start message, <code>startGame()</code> is going to handle that.
   * If the server sends the player turn broadcast, 
   * the client checks if the <code>ID</code> in the message matches the one of the player:
   *                                                if it's equal: run <code>askMove()</code>.
   *                                                if it isn't equal: print a waiting message.
   * If the server sends a notify move message, 
   * the client runs <code>setMove()</code> with the message.
   * If the server sends the end game message,
   * <code>handleEnd</code> is going to handle the message.
   * If the message doesn't match any of the above, it will be printed to the console.
   * @param message, message received from the <code>Server</code>.
   */
  //@ requires message != null; 
  private void handleInput(String message) {
    Scanner inScanner = new Scanner(message);
    switch (inScanner.next()) {
      case Protocol.Server.SERVERCAPABILITIES:
        writeOutput(Protocol.Client.SENDCAPABILITIES + " 2 " + player.getName() + " 0 4 4 4 4 0 0");
        break;
      case Protocol.Server.ASSIGNID:
        player.setId(inScanner.nextInt());
        break;
      case Protocol.Server.STARTGAME: startGame(message);
        break;
      case Protocol.Server.TURNOFPLAYER:
        if (inScanner.nextInt() == player.getId()) {
          askMove();
        } else {
          StringBuilder waitmsg = new StringBuilder();
          waitmsg.append("Waiting for player ");
          waitmsg.append(player.getOpponentName());
          waitmsg.append(" (");
          waitmsg.append(player.getMark().other().toString());
          waitmsg.append(")...");
          System.out.println(waitmsg.toString());
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
  
  /**
   * starts the local client game by first reading the start message,
   * it retrieves the <code>mark</code> and <code>opponentName</code> of this <code>Player</code>.
   * The <code>mark</code> is also set for the <code>hintBot</code>,
   * because the hintBot should be able to calculate a move that's beneficial for the player.
   * After that, the <code>Board</code> gets initialized for the <code>Player</code>.
   * This is done by using a part of the start message that contains the dimensions.
   * @param message, the start message received from the <code>Server</code>.
   */
  /*@ requires message.length() >= 18 && message.contains(String.valueOf(player.getId())) &&
    @ message.contains("ff0000") && message.contains("0000ff");
    @ ensures player.getBoard() != null && player.getMark() != null &&
    @ player.getOpponentName() != null;
    @*/
  private void startGame(String message) {
    System.out.println("Starting new game.");
    String[] players = message.substring(18).split(" ");
    for (String playerInfo : players) {
      if (playerInfo.startsWith(String.valueOf(player.getId()))) {
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
  
  /**
   * Asks the <code>Player</code> for a move by creating a question,
   * the question gets send to the <code>makeMove()</code> method of the <code>Player</code>.
   * That method will return a string containing the x and y-coordinates of the move.
   * If the <code>Player</code> is an instance of <code>HumanPlayer</code>,
   * the <code>hintBot</code> copies the board and prints a hint.
   * If the <code>Player</code> is an instance of <code>ComputerPlayer</code>,
   * the move gets printed to the console to let the user know what move it made.
   * The string is split into a x and y-coordinate and gets validated by <code>Player</code>.
   * If it's valid, the make move message is sent to the <code>Server</code>.
   */
  //JML not applicable.
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
    } catch (ArrayIndexOutOfBoundsException exc) {
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
  
  /**
   * Places move on local <code>Board</code> of <code>Player</code>.
   * Splits the notify move message into a x and y-coordinate and puts it on the <code>board</code>.
   * The message is checked for the ID to set field to the appropriate <code>Mark</code>.
   * @param message, notify move message from the server.
   */
  /*@ requires player.getBoard() != null && player.getMark() != null &&
    @ player.getBoard().isEmptyField(Integer.parseInt(message.split(" ")[2]),Integer.parseInt(message.split(" ")[3]));
    @*/
  private void setMove(String message) {
    int xpos = Integer.parseInt(message.split(" ")[2]);
    int ypos = Integer.parseInt(message.split(" ")[3]);
    if (message.split(" ")[1].equals(String.valueOf(player.getId()))) {
      player.setField(xpos, ypos, player.getMark());
    } else {
      player.setField(xpos, ypos, player.getMark().other());
    }
  }
  
  /**
   * Prints the appropriate end message based on the <code>winCode</code> out of input string.
   * The message gets checked if it contains an <code>Player</code> <code>ID</code>,
   * this will be used to determine the outcome of the game.
   * More basic messages get printed using the <code>getWin()</code> method.
   * @param endMessage, message received from the <code>Server</code> containing end reason.
   */
  //@ requires endMessage.contains(" ");
  private void handleEnd(String endMessage) {
    String winCode = endMessage.split(" ")[1];
    int playerid = 0;
    if (endMessage.split(" ").length == 3) {
      playerid = Integer.parseInt(endMessage.split(" ")[2]);
    }
    switch (winCode) {
      case "1":
        if (playerid == player.getId()) {
          System.out.println(Protocol.getWin("1") + " Good job " + player.getName() + "!");
        } else {
          System.out.println("You lost the game. Better luck next time.");
        }
        break;
      case "2":
        System.out.println(Protocol.getWin("2"));
        break;
      case "3":
        System.out.println(Protocol.getWin("3"));
        if (playerid == player.getId()) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Good job " + player.getOpponentName() + "!");
        }
        break;
      case "4":
        System.out.println("\n" + Protocol.getWin("4"));
        if (playerid == player.getId()) {
          System.out.println("You lost the game. Better luck next time.");
        } else {
          System.out.println(Protocol.getWin("1") + " Good job " + player.getOpponentName() + "!");
        }
        break;
      default: System.out.println(Protocol.getWin("unknown"));
        break;
    }
    String nextGame = player.makeMove("Do you want to play another game? (y/n)");
    if (nextGame.equals("y")) {
      try {
        new ClientHandler(new Socket(server.getInetAddress(),
            server.getPort()),player.getName(), player).start();
        shutdown();
      } catch (ConnectException exc) {
        System.out.println(exc.getMessage());
        System.out.println("Couldn't connect to the server.");
      } catch (IOException exc) {
        System.out.println(exc.getMessage());
        shutdown();
      }
    } else {
      System.out.println("Game ended, thanks for playing!");
      shutdown();
    }
  }

  /**
   * Closes the connection with the server and breaks the <code>readInput()</code> loop.
   * This will end the current <code>Thread</code> of <code>ClientHandler</code>.
   */
  //@ requires server.isConnected();
  //@ ensures gameOver == true && server.isClosed();
  private void shutdown() {
    try {
      server.close();
      gameOver = true;
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      gameOver = true;
    }
  }
}
