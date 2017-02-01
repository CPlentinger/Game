package project.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import project.game.HumanPlayer;
import project.game.Mark;
import project.game.Player;

public class ServerHandler extends Thread {
  private /*@ spec_public @*/ Socket client1;
  private /*@ spec_public @*/ Socket client2;
  private /*@ spec_public @*/ String gameCapabilities;
  private /*@ spec_public @*/ int c1Id;
  private /*@ spec_public @*/ int c2Id;
  private BufferedReader in;
  private BufferedWriter out;
  private /*@ spec_public @*/ int curTurnId;
  //@ public invariant curTurnId == c1Id || curTurnId == c2Id;
  private /*@ spec_public @*/ int curStreams;
  //@ public invariant curStreams == c1Id || curTurnId == c2Id;
  private /*@ spec_public @*/ Player serverGame;
  private /*@ spec_public @*/ boolean gameEnd;

  /**
   * Creates a <code>ServerHandler</code>:
   * Stores both client sockets as <code>client1</code> and <code>client2</code>.
   * Stores the game capabilities.
   * Stores the ids of the clients by splitting the game capabilities string.
   * Assigns a buffered reader/writer to the input/output stream of <code>client1</code>. 
   * Sets <code>curStreams</code> to the first <code>Client</code>.
   *      Indicates where the <code>in</code> and <code>out</code> are currently connected to.
   *      Either the input and output stream of <code>client1</code> or <code>client2</code>.
   *      This can be switched using <code>changeStreams()</code>.
   * Sets <code>curTurnId</code> to <code>c1Id</code>,
   * this variable indicates which client is currently on turn.
   * Sets <code>gameEnd</code> to false, 
   * this boolean indicates whether the game is finished, used for breaking the game loop.
   * @param sock1 , the socket connection with <code>client1</code>.
   * @param sock2 , the socket connection with <code>client2</code>.
   * @param capabilities , the game capabilities retrieved from <code>CapabilitiesHandler</code>.
   */
  /*@ requires sock1.isConnected() && sock2.isConnected() &&
    @ capabilities.contains(Protocol.Server.STARTGAME);
    @ ensures client1 == sock1 && client2 == sock2 && 
    @ gameCapabilities == capabilities && curStreams == c1Id &&
    @ curTurnId == c1Id && gameEnd == false;
    @*/
  public ServerHandler(Socket sock1, Socket sock2, String capabilities) {
    this.client1 = sock1;
    this.client2 = sock2;
    this.gameCapabilities = capabilities;
    try {
      this.c1Id = Integer.parseInt((gameCapabilities.split(" ")[2].split("\\|")[0]));
      this.c2Id = Integer.parseInt((gameCapabilities.split(" ")[3].split("\\|")[0]));
    } catch (NumberFormatException exc) {
      System.out.println("Invalid game capabilities!");
      shutdown();
    }
    try {
      this.in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
      this.out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      shutdown();
    }
    this.curStreams = c1Id;
    this.curTurnId = c1Id;
    this.gameEnd = false;
  }
  
  /**
   * Start of the thread.
   * Starts a game using the game capabilities and starts game loop.
   * The game loop let clients alternately make moves until the game ends.
   */
  //JML not applicable.
  public void run() {
    startGame(gameCapabilities);
    while (!gameEnd) {
      writeBoth(Protocol.Server.TURNOFPLAYER + " " + curTurnId);
      readInput();
      changeStreams();
      changeTurn();
    }
    shutdown();
  }

  /**
   * Reads input from the BufferedReader (message from the current connected input stream/client),
   * the message gets passed over to <code>handleInput()</code> where it will be handled.
   * If there is a <code>SocketTimeoutException</code>,
   * the current client took to long to make his move and timed out.
   * If there is a <code>SocketException</code>, 
   * the other player gets notified of a disconnect and the game ends.
   */
  private void readInput() {
    String clientInput;
    try {
      if ((clientInput = in.readLine()) != null) {
        System.out.println("in player " + curStreams + ": " + clientInput);
        handleInput(clientInput);
      }
    } catch (SocketTimeoutException exc) {
      writeBoth(Protocol.Server.NOTIFYEND + " 4 " + curTurnId);
      gameEnd = true;
    } catch (SocketException exc) {
      changeStreams();
      writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
      gameEnd = true;
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      writeBoth(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
      gameEnd = true;
    }
  }

  /**
   * Closes the sockets of the clients. 
   */
  private void shutdown() {
    try {
      client1.close();
      client2.close();
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
    }
  }
  
  /**
   * Writes the input string to the currently connected output stream as a new line.
   * In case writing the message results in a <code>SocketException</code>,
   * the <code>Server</code> will handle it as a disconnect and end the game.
   * @param message, text to write to the <code>Client</code> as new line.
   */
  private void writeOutput(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
      System.out.println("out player " + curStreams + ": " + message);
    } catch (SocketException exc) {
      changeStreams();
      writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
      gameEnd = true;
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
      gameEnd = true;
    }
  }
  
  /**
   * Writes the input string to both clients by switching the streams.
   * @param message, text to write to both clients as new line.
   */
  private void writeBoth(String message) {
    writeOutput(message);
    changeStreams();
    writeOutput(message);
    changeStreams();
  }
  
  /**
   * Handles the input message of the <code>Client</code>.
   * If the message is a make move message,
   * the <code>Server</code> runs <code>makeMove()</code> using x and y coordinates in the message.
   * @param input, message from the client.
   */
  private void handleInput(String input) {
    Scanner inScanner = new Scanner(input);
    try {
      if (inScanner.next().equals(Protocol.Client.MAKEMOVE)) {
        makeMove(inScanner.nextInt(), inScanner.nextInt());
      }
    } catch (NoSuchElementException exc) {
      System.out.println("Invalid makeMove message!");
      writeBoth(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
      gameEnd = true;
    }
    inScanner.close();
  }
  
  /**
   * start game by first sending the game capabilities to the clients.
   * A new <code>HumanPlayer</code> gets created, this will handle the server side game.
   * The <code>Board</code> gets initialized for the <code>HumanPlayer</code>,
   * using a part of the start message that contains the dimensions.
   * Finally, a socket timeout of 2 minutes is set for both clients.
   * This will be used as game timeout.
   * If the 2 minutes expire,
   * <code>readInput()</code> will throw a <code>SocketTimeoutExcepiont</code>.
   * @param message, the start message created by the <code>CapabilitiesHandler</code>.
   */
  private void startGame(String gameCapabilities) {
    System.out.println("Starting new game.");
    writeBoth(gameCapabilities);
    serverGame = new HumanPlayer();
    serverGame.buildBoard(gameCapabilities.substring(10, 15));
    try {
      client1.setSoTimeout(120000);
      client2.setSoTimeout(120000);
    } catch (SocketException exc) {
      writeBoth(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
    }
  }
  
  /**
   * Gets the <code>Mark</code> of the client currently on turn.
   * @return the <code>Mark</code> of the client currently on turn. 
   */
  private Mark getMark() {
    if (curTurnId == c1Id) {
      return Mark.O;
    } else {
      return Mark.X;
    }
  }
  
  /**
   * Checks the make move message from the <code>Client</code>.
   * If the message contains a valid move: 
   *      The <code>Server</code> places the move at his own Board.
   *      Broadcasts a notify message to both clients.
   *      Uses <code>endGameCheck()</code> to check if the game has ended.
   *      If this is the case,
   *      the <code>Server</code> sends the clients the end game message and ends the game.
   * If the message contains a invalid move:
   *      The <code>Server</code> starts current player's move again.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   */
  private void makeMove(int xpos, int ypos) {
    if (serverGame.checkMove(xpos, ypos)) {
      serverGame.setField(xpos, ypos, getMark());
      writeBoth(Protocol.Server.NOTIFYMOVE + " " + curTurnId + " " + xpos + " " + ypos);
      String endMessage;
      if ((endMessage = serverGame.endGameCheck(curTurnId)) != null) {
        writeBoth(endMessage);
        gameEnd = true;
      }
    } else {
      writeOutput(Protocol.Server.TURNOFPLAYER + " " + curTurnId);
      readInput();
    }
  }
  
  /**
   * Switches the buffered reader and writer to the input/output stream of the other client.
   */
  private void changeStreams() {
    if (curStreams == c1Id) {
      try {
        in = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client2.getOutputStream()));
      } catch (IOException exc) {
        System.out.print(exc.getMessage());
        writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
        gameEnd = true;
      }
      curStreams = c2Id;
    } else {
      try {
        in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(client1.getOutputStream()));
      } catch (IOException exc) {
        System.out.print(exc.getMessage()); 
        writeOutput(Protocol.Server.NOTIFYEND + " 3 " + curTurnId);
        gameEnd = true;
      }
      curStreams = c1Id;
    }
  }
  
  /**
   * Switches the <code>curTurnId</code> variable to the id of the other client.
   */
  private void changeTurn() {
    if (curTurnId == c1Id) {
      curTurnId = c2Id;
      System.out.println("Changed turn to: Player " + curTurnId);
    } else {
      curTurnId = c1Id;
      System.out.println("Changed turn to: Player " + curTurnId);
    }
  }
}
