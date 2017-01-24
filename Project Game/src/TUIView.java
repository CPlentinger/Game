import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class TUIView implements Observer {
  
  public Controller controller;
  public Board board;
  
  public TUIView(Controller controller) {
    this.controller = controller;
    this.board = controller.board;
  }
  
  public void start() {
    View();
  }
  
  public void View() {
    System.out.print(makeYAxis());
    System.out.print(makeFields());
    System.out.print(makeXAxis());
  }
  
  public String makeYAxis() {
    String Yaxis = " | 0 1 2 3 Y";
    String Delim = "──┼────────  ";
    String Space = "        ";
    String firstLine = "0" + Yaxis + Space;
    String secLine = Delim + Space;
    for (int i = 1; i < board.dim; i++) {
      if (!board.isEmptyLayer(i)) {
        firstLine += i + Yaxis + Space;
        secLine += Delim + Space;
      }
    }
    firstLine += "\n";
    secLine += "\n";
    return firstLine + secLine;
  }
  
  public String[] makeFieldsArray() {
    String[] result = new String[board.dim*board.dim];
    for (int z = 0; z < board.dim; z++) {
      for (int x = 0; x < board.dim; x++) {
        String thisx = "";
        for (int y = 0; y < board.dim; y++) {
          if (!board.isEmptyField(x, y, z)) {
            thisx += " " + board.getField(x, y, z);
          } else {
            thisx  += " ·";
          }
        }
        thisx += "  ";
        result[z*board.dim+x] = thisx;
      }
    }
    return result;
  }
  
  public String makeFields() {
    String[] lines = makeFieldsArray();
    String Space = "        ";
    String line = "";
    for (int i = 0; i < board.dim; i++) {
      String Xaxis = i + " │"; 
      for (int j = 0; j < board.dim; j++) {
        if (!board.isEmptyLayer(j)) {
          line += Xaxis + lines[i+4*j] + Space;
        }
      }
      line += "\n";
    }
    return line;
  }
  
  public String makeXAxis() {
    String Space = "                    ";
    String result = "X" + Space;
    for (int i = 1; i < board.dim; i++) {
      if (!board.isEmptyLayer(i)) {
        result += "X" + Space;
      }
    }
    result += "\n";
    return result;
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    View();
  }
  
}
