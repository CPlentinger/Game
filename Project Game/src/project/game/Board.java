package project.game;

import java.util.Observable;

public class Board extends Observable {
  public int dim;
  public Mark[][][] fields;
  
  /**
   * creates a <code>Board</code> with a three dimensional array <code>fields</code>. 
   * The array is filled with an empty <code>Mark</code>
   * @param dim , an integer that indicates the dimensions of the board.
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
   * Creates a new instance of <code>Board</code> with the same <code>dim</code>.
   * <code>fields</code> of this <code>Board</code> gets copied.
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
   * Returns whether or not the input coordinates map to a field on the <code>board</code>.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param zpos , integer representing z coordinate.
   * @return whether or not the parameter coordinates correspond with a field.
   */
  public boolean isField(int xpos, int ypos, int zpos) {
    return xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim && zpos >= 0 && zpos < dim;
  }
  
  /**
   * Returns whether or not the input coordinates map to a field on the <code>board</code>.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @return whether or not the parameter coordinates correspond with a field.
   */
  public boolean isField(int xpos, int ypos) {
    return xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim;
  }
  
  /**
   * Checks the input using <code>isField()</code> and returns if the corresponding field is empty.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param zpos , integer representing z coordinate.
   * @return whether or not the parameter coordinates point to a empty field. 
   */
  public boolean isEmptyField(int xpos, int ypos, int zpos) {
    return getField(xpos,ypos,zpos).equals(Mark.E) && isField(xpos, ypos, zpos);
  }
  
  /**
   * returns whether or not the corresponding field has place for another <code>Mark</code>.
   * If maximum height is reached it will return false. 
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @return returns whether or not the corresponding field has place for another <code>Mark</code>.
   */
  public boolean isEmptyField(int xpos, int ypos) {
    for (int z = 0; z < dim; z++) {
      if (isEmptyField(xpos,ypos,z)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether or not the input layer contains only empty fields.
   * @param zpos , integer representing layer to check.
   * @return whether or not the whole layer is empty.
   */
  public boolean isEmptyLayer(int zpos) {
    for (int x = 0; x < dim; x++) {
      for (int y = 0; y < dim; y++) {
        if (!isEmptyField(x,y,zpos)) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Returns <code>Mark</code> at the corresponding field if the field exists.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param zpos , integer representing z coordinate.
   * @return <code>Mark</code> at the corresponding field.
   */
  public Mark getField(int xpos, int ypos, int zpos) {
    if (isField(xpos, ypos, zpos)) {
      return fields[xpos][ypos][zpos];
    } else {
      return null;
    }
  }
  
  /**
   * Sets the input <code>Mark</code> at the input field if the field exists.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param zpos , integer representing z coordinate.
   * @param mark , <code>Mark</code> to put at the corresponding field.
   */
  public void setField(int xpos, int ypos, int zpos, Mark mark) {
    if (isField(xpos, ypos, zpos)) {
      fields[xpos][ypos][zpos] = mark;
      setChanged();
      notifyObservers();
    }
  }
  
  /**
   * Sets input <code>Mark</code> at the top of input field if the field is valid and empty.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param mark , <code>Mark</code> to put at the top of corresponding field.
   */
  public void setTopField(int xpos, int ypos, Mark mark) {
    if (isField(xpos, ypos)) {
      for (int z = 0; z < dim; z++) {
        if (isEmptyField(xpos,ypos,z)) {
          fields[xpos][ypos][z] = mark;
          break;
        }
      }
      setChanged();
      notifyObservers();
    }
  }
  
  /**
   * Checks whether or not the Board contains a one-dimensional line with four of the input Mark.
   * Examples of a one dimensional line: x = 1, y = 3, z = 2. 
   * @param mark , <code>Mark</code> that the line should contain.
   * @return whether or not the board contains a one-dimensional line with four of the input mark.
   */
  public boolean has1DLine(Mark mark) {
    int yseq;
    int zseq;
    int xseq;
    for (int z = 0; z < dim; z++) {
      for (int x = 0; x < dim; x++) {
        yseq = 0;
        for (int y = 0; y < dim; y++) {
          if (getField(x,y,z).equals(mark)) {
            yseq++;
            if (yseq == 4) {
              return true;
            }
            if (z == 0) {
              zseq = 0;
              for (int i = 0; i < dim; i++) {
                if (getField(x,y,i).equals(mark)) {
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
                if (getField(j,y,z).equals(mark)) {
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
   * Checks whether or not the Board contains a two-dimensional line with four of the input Mark.
   * With a two dimensional line we mean a line in a two-dimensional plane.
   * @param mark , <code>Mark</code> that the line should contain.
   * @return whether or not the Board contains a two-dimensional line with four of the input Mark.
   */
  public boolean has2DLine(Mark mark) {
    for (int x = 0, y = 0; x < dim; y++) {
      if (!getField(x,y,y).equals(mark)) {
        y = 0;
        x++;
      } else if (y == 3) {
        return true;
      }
    }
    for (int x = 0, y = 0; y < dim; x++) {
      if (!getField(x,y,x).equals(mark)) {
        x = 0;
        y++;
      } else if (x == 3) {
        return true;
      }
    }
    for (int x = 3, y = 0; y < dim; x--) {
      if (!getField(x,y,3 - x).equals(mark)) {
        x = 3;
        y++;
      } else if (x == 0) {
        return true;
      }
    }
    for (int x = 0, y = 3; x < dim; y--) {
      if (!getField(x,y,3 - y).equals(mark)) {
        y = 3;
        x++;
      } else if (y == 0) {
        return true;
      }
    }
    for (int z = 0; z < dim; z++) {
      for (int x = 0, y = 0; y < dim; x++,y++) {
        if (!getField(x,y,z).equals(mark)) {
          break;
        } else if (x == 3) {
          return true;
        }
      }
    }
    for (int z = 0; z < dim; z++) {
      for (int x = 3, y = 0; y < dim; x--,y++) {
        if (!getField(x,y,z).equals(mark)) {
          break;
        } else if (x == 0) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   *  Checks whether or not the board contains a three-dimensional line with four of the input Mark.
   *  With a three-dimensional line we mean the diagonals of a cube.
   * @param mark , <code>Mark</code> that the line should contain.
   * @return whether or not the board contains a three-dimensional line with four of the input Mark.
   */
  public boolean has3DLine(Mark mark) {
    int one = 0;
    int two = 0;
    int three = 0;
    int four = 0;
    for (int i = 3; i >= 0; i--) {
      if (getField(-i + 3,-i + 3,-i + 3).equals(mark)) {
        one++;
      }
      if (getField(-i + 3,i,i).equals(mark)) {
        two++;
      }
      if (getField(-i + 3,-i + 3,i).equals(mark)) {
        three++;
      }
      if (getField(-i + 3,i,-i + 3).equals(mark)) {
        four++;
      }
      if (i == 0 && (one == 4 || two == 4 || three == 4 || four == 4 )) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether or not the input mark has a line on the Board of four pieces.
   * @param mark , the winning <code>Mark</code> to check for.
   * @return whether or not the input <code>Mark</code> has won the game.
   */
  public boolean isWinner(Mark mark) {
    return has1DLine(mark) || has2DLine(mark) || has3DLine(mark);
  }
  
  /**
   * Checks whether or not the board has a winner using both marks in <code>isWinner()</code>.
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