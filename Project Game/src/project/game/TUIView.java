package project.game;

public class TUIView {
  
  /**
   * Prints the <code>Board</code> to the console.
   * Consists of: y-axis, fields and last line.
   * @param board , instance that will be printed.
   */
  public void createView(Board board) {
    System.out.print("\n" + makeYAxis(board));
    System.out.print(makeFields(board));
    System.out.print(makeLastLine(board) + "\n");
  }
  
  /**
   * Creates a string, representing the two first lines of the view, containing the y-axes.
   * The length of the two first depend on how many layers will be printed (that aren't empty).
   * @param board , instance for which the y-axes will be created.
   * @return string containing the y-axes.
   */
  public String makeYAxis(Board board) {
    String yaxis = " | 0 1 2 3 Y";
    String delim = "--|--------  ";
    String space = "        ";
    String firstLine = "0" + yaxis + space;
    String secLine = delim + space;
    for (int i = 1; i < board.dim; i++) {
      if (!board.isEmptyLayer(i)) {
        firstLine += i + yaxis + space;
        secLine += delim + space;
      }
    }
    firstLine += "\n";
    secLine += "\n";
    return firstLine + secLine;
  }
  
  /**
   * Makes an array of strings out of the <code>fields</code> array of the input <code>Board</code>.
   * This will be used in the <code>makeFields()</code> method.
   * @param board , instance of which the <code>fields</code> array will be used.
   * @return string array containing all the rows of the board that will be printed.
   */
  public String[] makeFieldsArray(Board board) {
    String[] result = new String[board.dim * board.dim];
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
        result[z * board.dim + x] = thisx;
      }
    }
    return result;
  }
  
  /**
   * Creates a string representing the playing field with x-axes at the left.
   * The array of <code>makeFieldsArrayM</code> will be used.
   * @param board , instance of which the playing area will be created.
   * @return string containing all rows of the usable playing field including x-axes.
   */
  public String makeFields(Board board) {
    String[] lines = makeFieldsArray(board);
    String space = "        ";
    String line = "";
    for (int i = 0; i < board.dim; i++) {
      String xaxis = i + " |"; 
      for (int j = 0; j < board.dim; j++) {
        if (j == 0) {
          line += xaxis + lines[i + 4 * j] + space;
        } else if (!board.isEmptyLayer(j)) {
          line += xaxis + lines[i + 4 * j] + space;
        }
      }
      line += "\n";
    }
    return line;
  }
  
  /**
   * Creates a string containing the last line of the view,
   * this line only contains the label for the x-axis (x).
   * @param board , instance of which the last line will be created.
   * @return line containing the labels for the x-axes of this view.
   */
  public String makeLastLine(Board board) {
    String space = "                    ";
    String result = "X" + space;
    for (int i = 1; i < board.dim; i++) {
      if (!board.isEmptyLayer(i)) {
        result += "X" + space;
      }
    }
    result += "\n";
    return result;
  }
}
