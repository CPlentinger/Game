import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class TUIView implements Observer {
  
  public Board board;
  
  public TUIView(Board board) {
    this.board = board;
    board.setField(1, 2, 0, Mark.X);
    board.setField(3, 2, 1, Mark.O);
    board.setField(1, 2, 2, Mark.X);
    board.setField(3, 2, 0, Mark.O);
  }
  
  public void start() {
    View();
  }
  
  public void View() {
    String[] lines = new String[board.dim*board.dim];
    String Yaxis = " │ 0 1 2 3 Y";
    String Delim = "      ──┼────────";
    for (int z = 0; z < board.dim; z++) {
      for (int x = 0; x < board.dim; x++) {
        String thisx = "";
        for (int y = 0; y < board.dim; y++) {
          thisx += " " + board.getField(x, y, z);
        }
        lines[z*board.dim+x] = thisx;
      }
    }
    if (board.isEmptyLayer(1)) {
      System.out.println("      0" + Yaxis);
      System.out.println(Delim);
    } else if (board.isEmptyLayer(2)) {
      System.out.println("      0" + Yaxis + "    1" +Yaxis);
      System.out.println(Delim + Delim);
    } else if (board.isEmptyLayer(3)) {
      System.out.println("      0" + Yaxis + "    1" + Yaxis + "    2" + Yaxis);
      System.out.println(Delim + Delim + Delim);
    } else {
      System.out.println("      0" + Yaxis + "    1" + Yaxis + "    2" + Yaxis + "    3" + Yaxis);
      System.out.println(Delim + Delim + Delim + Delim);
    }
    for (int i = 0; i < board.dim; i++) {
      String line = "";
      String Xaxis = "      " + i + " │"; 
      if (board.isEmptyLayer(1)) {
        line = Xaxis + lines[i];
      } else if (board.isEmptyLayer(2)) {
        line = Xaxis + lines[i] + Xaxis + lines[4+i];
      } else if (board.isEmptyLayer(3)){
        line = Xaxis + lines[i] + Xaxis + lines[4+i] + Xaxis + lines[8+i];
      } else {
        line = Xaxis + lines[i] + Xaxis + lines[4+i] + Xaxis + lines[8+i] + Xaxis + lines[12+i];
      }
      System.out.println(line);
    }
    if (board.isEmptyLayer(1)) {
      System.out.print("      X");
    } else if (board.isEmptyLayer(2)) {
      System.out.print("      X                X");
    } else if (board.isEmptyLayer(3)) {
      System.out.print("      X                X                X");
    } else {
      System.out.print("      X                X                X                X");
    }
  }
  
  @Override
  public void update(Observable o, Object arg) {
    View();
  }

}
