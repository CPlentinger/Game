import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class HumanPlayer implements Observer {
  
  public Board board;
  public TUIView view;
  public String name;
  public int ID;
  public Mark mark;
  
  public HumanPlayer() {
    this.view = new TUIView();
  }
  
  public void setName(String pName) {
    this.name = pName;
  }
  
  public void setID(int id) {
    this.ID = id;
  }
  
  public void setMark(Mark m) {
    this.mark = m;
  }
  
  public int getID() {
    return ID;
  }
  
  public Mark getMark() {
    return mark;
  }
  
  public String getName() {
    return name;
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
  
  public String endGameCheck() {
    String result = Protocol.Server.NOTIFYEND;
    if (gameOver()) {
      if(board.hasWinner()) {
        if (board.isWinner(Mark.X)) {
          result += " " + 1 + " " + 2;
        } else {
          result += " " + 1 + " " + 1;
        }
      } else {
        result += " " + 2;
      }
      return result;
    }
    return null;
  }
  
  public boolean gameOver() {
    return board.isFull() || board.hasWinner();
  }
  public String getResponse(String message) {
    return view.getResponse(message);
  }
  
  @Override
  public void update(Observable arg0, Object arg1) {
    view.createView(board);
  }
}
