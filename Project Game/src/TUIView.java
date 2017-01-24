import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class TUIView implements Observer {
  
  public Board board;
  
  public TUIView(Board board) {
    this.board = board;
    board.setField(1, 2, 0, Mark.X);
    board.setField(3, 2, 0, Mark.O);
  }
  
  public void start() {
    View();
  }

  
  
  public void View() {
    String[] lines = new String[(int) Math.pow(board.dim, 3) + 2];
    lines[0] = "  │0 1 2 3 Y  ";
    for (int z = 0; z < board.dim; z++) {
      for (int x = 0; x < board.dim; x++) {
        String thisx = x + " │";
        for (int y = 0; y < board.dim; y++) {
          thisx += board.getField(x, y, z) + " ";
        }
        lines[x+1] = thisx;
      }
    }
    for (int i = 0; i < lines.length; i++) {
      System.out.println(lines[i]);
    }
  }
  @Override
  public void update(Observable o, Object arg) {
    View();
  }

}
