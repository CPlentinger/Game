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
  private int ID;
  private Mark mark;
  private Board board;
  private TUIView view;
  
  public Player() {
    this.view = new TUIView();
  }
  
  public void setName(String pName) {
    this.name = pName;
  }
  
  public void setOpponentName(String oppName) {
    this.opponentName = oppName;
  }
  
  public void setID(int id) {
    this.ID = id;
  }
  
  public void setMark(Mark m) {
    this.mark = m;
  }
  
  public void setField(int x, int y, Mark m) {
    board.setField(x, y, m);
  }
  
  public void setBoard(Board b) {
    this.board = b;
  }
  
  public String getName() {
    return name;
  }
  
  public String getOpponentName() {
    return opponentName;
  }
  
  public int getID() {
    return ID;
  }
  
  public Mark getMark() {
    return mark;
  }

  public Board getBoard() {
    return board;
  }
  
  public void buildBoard(String startMessage) {
    String dims = startMessage.replaceAll("\\|", " ");
    Scanner dimScan = new Scanner(dims);
    int dim = Math.min(dimScan.nextInt(), Math.min(dimScan.nextInt(), dimScan.nextInt()));
    dimScan.close();
    this.board = new Board(dim);
    board.addObserver(this);
    view.createView(board);
  }
  
  public abstract String makeMove(String question);
  
  public boolean checkMove(int x, int y) {
    if (board.isEmptyField(x, y)) {
      return true;
    } else {
      return false;
    }
  }
  
  public String endGameCheck(int id) {
    String result = Protocol.Server.NOTIFYEND;
    if (gameOver()) {
      if(board.hasWinner()) {
          result += " " + 1 + " " + id;
      } else {
        result += " " + 2;
      }
      return result;
    }
    return null;
  }
  
  public Board deepCopy() {
    return board.deepCopy();
  }
  
  public boolean gameOver() {
    return board.isFull() || board.hasWinner();
  }
  
  public String getInput(String question) {
    System.out.print(question);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String result = null;
    try {
      result = in.readLine();
    } catch (IOException e) {
      
    }
    if (result != null) {
      return result;
    } else {
      return "";
    }
  }
  
  @Override
  public void update(Observable o, Object arg) {
    view.createView(board);
  }
}
