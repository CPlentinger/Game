package project.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import project.server.Protocol;

public abstract class Player implements Observer {
  
  private String name;
  private String opponentName;
  private int id;
  private Mark mark;
  private Board board;
  private TUIView view;
  
  /**
   * Creates a <code>Player</code> by initializing the <code>view</code> of the MVC pattern.
   */
  public Player() {
    this.view = new TUIView();
  }
  
  /**
   * Sets the <code>name</code> of this <code>Player</code> to the input string.
   * @param newName , the new <code>name</code> of this <code>Player</code>.
   */
  public void setName(String newName) {
    this.name = newName;
  }
  
  /**
   * Sets the <code>opponentName</code> of this <code>Player</code> to the input string.
   * @param oppName , the new <code>opponentName</code> of this <code>Player</code>.
   */
  public void setOpponentName(String oppName) {
    this.opponentName = oppName;
  }
  
  /**
   * Sets the <code>ID</code> of this <code>Player</code> to the input integer.
   * @param newId , the new <code>ID</code> of this <code>Player</code>.
   */
  public void setId(int newId) {
    this.id = newId;
  }
  
  /**
   * Sets the <code>mark</code> of this <code>Player</code> to the input <code>Mark</code>.
   * @param newMark , the new <code>mark</code> of this <code>Player</code>.
   */
  public void setMark(Mark newMark) {
    this.mark = newMark;
  }
  
  /**
   * Sets the <code>board</code> of this <code>Player</code> to the input <code>Board</code>.
   * @param newBoard , the new <code>board</code> of this <code>Player</code>.
   */
  public void setBoard(Board newBoard) {
    this.board = newBoard;
  }
  
  /**
   * Gets the <code>name</code> of this <code>Player</code>.
   * @return the <code>name</code> of this <code>Player</code>.
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the <code>opponentName</code> of this <code>Player</code>.
   * @return the <code>opponentName</code> of this <code>Player</code>.
   */
  public String getOpponentName() {
    return opponentName;
  }
  
  /**
   * Gets the <code>id</code> of this <code>Player</code>.
   * @return the <code>id</code> of this <code>Player</code>.
   */
  public int getId() {
    return id;
  }
  
  /**
   * Gets the <code>mark</code> of this <code>Player</code>.
   * @return the <code>mark</code> of this <code>Player</code>.
   */
  public Mark getMark() {
    return mark;
  }

  /**
   * Gets the <code>board</code> of this <code>Player</code>.
   * @return the <code>board</code> of this <code>Player</code>.
   */
  public Board getBoard() {
    return board;
  }
  
  /**
   * Initializes <code>board</code> using the <code>dim</code> scanned out of the start message.
   * @param startMessage , a part of start message from the server containing dimensional values. 
   */
  public void buildBoard(String startMessage) {
    String dims = startMessage.replaceAll("\\|", " ");
    Scanner dimScan = new Scanner(dims);
    int dim = Math.min(dimScan.nextInt(), Math.min(dimScan.nextInt(), dimScan.nextInt()));
    if (dim > 4) {
      dim = 4;
    }
    dimScan.close();
    this.board = new Board(dim);
    board.addObserver(this);
    view.createView(board);
  }
  
  /**
   * Sets the input <code>Mark</code> at the top of the input field on the board.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param mark , <code>Mark</code> to put at the top of corresponding field.
   */
  public void setField(int xpos, int ypos, Mark mark) {
    board.setTopField(xpos, ypos, mark);
  }
  
  /**
   * Abstract method that asks the player for a move. 
   * @param question , question to be answered by the <code>Player</code>
   * @return a string containing the answer of the <code>Player</code>.
   */
  public abstract String makeMove(String question);
  
  /**
   * Checks whether or not the field corresponding to the input coordinates is empty.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @return whether or not there's a empty field at the input coordinates.
   */
  public boolean checkMove(int xpos, int ypos) {
    if (board.isEmptyField(xpos, ypos)) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Checks whether or not the board conflicts with one of the game ending conditions.
   * @param id , the <code>id</code> of the <code>Player</code> that made the last move.
   * @return the end game message for the <code>ServerHandler</code> to <code>writeOutput()</code>. 
   */
  public String endGameCheck(int id) {
    String result = Protocol.Server.NOTIFYEND;
    if (gameOver()) {
      if (board.hasWinner()) {
        result += " " + 1 + " " + id;
      } else {
        result += " " + 2;
      }
      return result;
    }
    return null;
  }
  
  /**
   * Makes a deep copy of the <code>board</board> of this <code>Player</code>.
   * @return a deep copy of the <code>board</board> of this <code>Player</code>
   */
  public Board deepCopy() {
    return board.deepCopy();
  }
  
  /**
   * Checks whether or not the board is full or has a winner.
   * @return whether or not the game is over.
   */
  public boolean gameOver() {
    return board.isFull() || board.hasWinner();
  }
  
  /**
   * Prints input question on the console and reads console input from the user.
   * @param question , string to print to the console.
   * @return answer of the question by the player.
   */
  public String getInput(String question) {
    System.out.print(question);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String result = null;
    try {
      result = in.readLine();
    } catch (IOException exc) {
      System.out.println(exc.getMessage());
    }
    if (result != null) {
      return result;
    } else {
      return "";
    }
  }
  
  /**
   * Invokes the view when this <code>Player</code> gets notified.
   */
  @Override
  public void update(Observable obs, Object arg) {
    view.createView(board);
  }
}
