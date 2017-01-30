package project.game;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import project.server.Protocol;
import project.server.Protocol.Server;

public class Controller implements Observer {
  
  public Board board;
  public TUIView view;
  
  public Controller() {
    this.view = new TUIView();
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
  
  public void setBoard(Board b) {
    this.board = b;
  }
  
  public Board getBoard() {
    return board;
  }
  
  public void makeMove(int x, int y, Mark m) {
      board.setField(x,y,m);
  }
  
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
  
  @Override
  public void update(Observable arg0, Object arg1) {
    view.createView(board);
  }
}
