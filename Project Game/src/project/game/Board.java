package project.game;

import java.util.Observable;

public class Board extends Observable {
  public int dim;
  public Mark[][][] fields;
  public static void main(String[] args) {
  }
  
  /**
   * creates a <code>Board</code> with a three dimensional array <code>fields</code> based on the <code>dim</code>. 
   * The array is filled with an empty <code>Mark</code>
   * @param dim, an integer that indicates the dimensions of the board.
   */ 
  public Board(int dim) {
    this.dim = dim;
    fields = new Mark[dim][dim][dim];
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        for (int k = 0; k < dim; k++) {
          fields[i][j][k] = Mark.E;
        }
      }
    }
  }
  
  /**
   * Returns a copy of this <code>Board</code>.
   * Creates a new instance of <code>Board</code> with the same <code>dim</code> and copies <code>fields</code>.
   * @return a deep copy of this <code>Board</code> with the same references.
   */
  public Board deepCopy() {
    Board result = new Board(dim);
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        for (int k = 0; k < dim; k++) {
          result.fields[i][j][k] = this.fields[i][j][k];  
        }
      }
    }
    return result;
  }
  /**
   * Returns whether or not the parameter coordinates correspond with a field on the <code>board</code>.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @param z, integer representing z coordinate.
   * @return whether or not the parameter coordinates correspond with a field.
   */
  public boolean isField(int x, int y, int z) {
    return x >= 0 && x < dim && y >= 0 && y < dim && z >= 0 && z < dim;
  }
  
  /**
   * Returns whether or not the parameter coordinates correspond with a field on the <code>board</code>.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @return whether or not the parameter coordinates correspond with a field.
   */
  public boolean isField(int x, int y) {
    return x >= 0 && x < dim && y >= 0 && y < dim;
  }
  
  /**
   * Checks the the parameters using <code>isField()</code> and returns if the corresponding field is empty.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @param z, integer representing z coordinate.
   * @return whether or not the parameter coordinates point to a empty field. 
   */
  public boolean isEmptyField(int x, int y, int z) {
      return getField(x,y,z).equals(Mark.E) && isField(x, y, z);
  }
  
  /**
   * returns whether or not the corresponding field has place for another <code>Mark</code>.
   * If maximum height is reached it will return false. 
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @return returns whether or not the corresponding field has place for another <code>Mark</code>.
   */
  public boolean isEmptyField(int x, int y) {
    for (int z = 0; z < dim; z++) {
      if (isEmptyField(x,y,z)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether or not the input layer contains only empty fields.
   * @param z, integer representing layer to check.
   * @return whether or not the whole layer is empty.
   */
  public boolean isEmptyLayer(int z) {
    for (int x = 0; x < dim; x++) {
      for (int y = 0; y < dim; y++) {
        if (!isEmptyField(x,y,z)) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Returns <code>Mark</code> at the corresponding field if the field exists.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @param z, integer representing z coordinate.
   * @return <code>Mark</code> at the corresponding field.
   */
  public Mark getField(int x, int y, int z) {
    if (isField(x, y, z)) {
      return fields[x][y][z];
    } else {
      return null;
    }
  }
  
  /**
   * Sets the input <code>Mark</code> at the input field if the field exists.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @param z, integer representing z coordinate.
   * @param m, <code>Mark</code> to put at the corresponding field.
   */
  public void setField(int x, int y, int z, Mark m) {
    if (isField(x, y, z)) {
      fields[x][y][z] = m;
      setChanged();
      notifyObservers();
    }
  }
  /**
   * Sets input <code>Mark</code> at the top of input field if the field is valid and empty.
   * @param x, integer representing x coordinate.
   * @param y, integer representing y coordinate.
   * @param m, <code>Mark</code> to put at the top of corresponding field.
   */
  public void setTopField(int x, int y, Mark m) {
    if (isField(x, y)) {
      for (int z = 0; z < dim; z++) {
        if (isEmptyField(x,y,z)) {
          fields[x][y][z] = m;
          break;
        }
      }
      setChanged();
      notifyObservers();
    }
  }
  
  /**
   * Checks whether or not the <code>Board</code> contains a one-dimensional line containing four of the input <code>Mark</code>.
   * Examples of a one dimensional line: x = 1, y = 3, z = 2. 
   * @param m, <code>Mark</code> that the line should contain.
   * @return whether or not the <code>Board</code> contains a one-dimensional line containing four of the input <code>Mark</code>.
   */
  public boolean has1DLine(Mark m) {
    int yseq;
    int zseq;
    int xseq;
    for (int z = 0; z < dim; z++) {
      for (int x = 0; x < dim; x++) {
        yseq = 0;
        for (int y = 0; y < dim; y++) {
          if (getField(x,y,z).equals(m)) {
            yseq++;
            if (yseq == 4) {
              return true;
            }
            if (z == 0) {
              zseq = 0;
              for (int i = 0; i < dim; i++) {
                if (getField(x,y,i).equals(m)) {
                  zseq++;
                }
                if (zseq == 4) {
                  return true;
                }
              }
            }
            if (x == 0) {
              xseq = 0;
              for (int j = 0; j < dim; j++) {
                if (getField(j,y,z).equals(m)) {
                  xseq++;
                }
                if (xseq == 4) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  /**
   * Checks whether or not the <code>Board</code> contains a two-dimensional line containing four of the input <code>Mark</code>.
   * With a two dimensional line we mean a line in a two-dimensional plane.
   * @param m, <code>Mark</code> that the line should contain.
   * @return whether or not the <code>Board</code> contains a two-dimensional line containing four of the input <code>Mark</code>.
   */
  public boolean has2DLine(Mark m) {
    for (int x = 0, y = 0; x < dim; y++) {
      if (!getField(x,y,y).equals(m)) {
        y = 0;
        x++;
      } else if (y == 3) {
        return true;
      }
    }
    for (int x = 0, y = 0; y < dim; x++) {
      if (!getField(x,y,x).equals(m)) {
        x = 0;
        y++;
      } else if (x == 3) {
        return true;
      }
    }
    for (int x = 3, y = 0; y < dim; x--) {
      if (!getField(x,y,3 - x).equals(m)) {
        x = 3;
        y++;
      } else if (x == 0) {
        return true;
      }
    }
    for (int x = 0, y = 3; x < dim; y--) {
      if (!getField(x,y,3 - y).equals(m)) {
        y = 3;
        x++;
      } else if (y == 0) {
        return true;
      }
    }
    for (int z = 0; z < dim; z++) {
      for (int x = 0, y = 0; y < dim; x++,y++) {
        if (!getField(x,y,z).equals(m)) {
          break;
        } else if (x == 3) {
          return true;
        }
      }
    }
    for (int z = 0; z < dim; z++) {
      for (int x = 3, y = 0; y < dim; x--,y++) {
        if (!getField(x,y,z).equals(m)) {
          break;
        } else if (x == 0) {
          return true;
        }
      }
    }
    return false;
  }
  /**
   *  Checks whether or not the <code>Board</code> contains a three-dimensional line containing four of the input <code>Mark</code>.
   *  With a three-dimensional line we mean the diagonals of the cube that represents the playing field.
   * @param m, <code>Mark</code> that the line should contain.
   * @return whether or not the <code>Board</code> contains a three-dimensional line containing four of the input <code>Mark</code>.
   */
  public boolean has3DLine(Mark m) {
    int one = 0;
    int two = 0;
    int three = 0;
    int four = 0;
    for (int i = 3; i >= 0; i--) {
      if (getField(-i + 3,-i + 3,-i + 3).equals(m)) {
        one++;
      }
      if (getField(-i + 3,i,i).equals(m)) {
        two++;
      }
      if (getField(-i + 3,-i + 3,i).equals(m)) {
        three++;
      }
      if (getField(-i + 3,i,-i + 3).equals(m)) {
        four++;
      }
      if (i == 0 && (one == 4 || two == 4 || three == 4 || four == 4 )) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether or not the input <code>Mark</code> has a line on the <code>Board</code> of four pieces.
   * @param m, the winning <code>Mark</code> to check for.
   * @return whether or not the input <code>Mark</code> has won the game.
   */
  public boolean isWinner(Mark m) {
      return has1DLine(m) || has2DLine(m) || has3DLine(m);
  }
  
  /**
   * Checks whether or not the board has a winner using both <code>Mark</code>s in <code>isWinner()</code>.
   * @return whether or not the board has a winner.
   */
  public boolean hasWinner() {
    return isWinner(Mark.O) || isWinner(Mark.X);
  }
  
  /**
   * Checks whether or not all fields are full using <code>isEmptyField()</code>.
   * @return whether or not all fields are full.
   */
  public boolean isFull() {
    for (int z = 0; z < dim; z++) {
      for (int y = 0; y < dim; y++) {
        for (int x = 0; x < dim; x++) {
          if (isEmptyField(x,y,z)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}