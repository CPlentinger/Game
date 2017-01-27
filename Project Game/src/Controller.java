import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class Controller {
  
  public Board board;
  
  public Controller(String startMessage) {
    String dims = startMessage.replaceAll("\\|", " ");
    Scanner dimScan = new Scanner(dims);
    int dim = Math.min(dimScan.nextInt(), Math.min(dimScan.nextInt(), dimScan.nextInt()));
    dimScan.close();
    this.board = new Board(dim);
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
  
}
