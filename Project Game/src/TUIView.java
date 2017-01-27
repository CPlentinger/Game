import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class TUIView {
  
  public void createView(Board board) {
    System.out.print("\n" + makeYAxis(board));
    System.out.print(makeFields(board));
    System.out.print(makeXAxis(board) + "\n");
  }
  
  public String makeYAxis(Board board) {
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
  
  public String[] makeFieldsArray(Board board) {
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
  
  public String makeFields(Board board) {
    String[] lines = makeFieldsArray(board);
    String Space = "        ";
    String line = "";
    for (int i = 0; i < board.dim; i++) {
      String Xaxis = i + " │"; 
      for (int j = 0; j < board.dim; j++) {
        if (j == 0) {
          line += Xaxis + lines[i+4*j] + Space;
        } else if (!board.isEmptyLayer(j)) {
          line += Xaxis + lines[i+4*j] + Space;
        }
      }
      line += "\n";
    }
    return line;
  }
  
  public String makeXAxis(Board board) {
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
  
  public String getResponse(String question) {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.print(question);
    String result = null;
    try {
        result = in.readLine();
    } catch (IOException e) {
    }

    if (result != null) {
      return result;
    } else {
      return null;
    }
  }
}
