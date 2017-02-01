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
  /*@ requires dim >= 0;
    @ ensures (\forall int i, j, k;
    @ 0 <= i && i < dim && 0 <= j && j < dim && 0 <= k && k < dim; fields[i][j][k] == Mark.E);
    @*/
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
  //@ requires this != null;
  //@ ensures \result == this;
  public /*@ pure @*/ Board deepCopy() {
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
  /*@ ensures (xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim && zpos >= 0 && zpos < dim)
    @ ==> \result == true;
    @*/
  public /*@ pure @*/ boolean isField(int xpos, int ypos, int zpos) {
    return xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim && zpos >= 0 && zpos < dim;
  }
  
  /**
   * Returns whether or not the input coordinates map to a field on the <code>board</code>.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @return whether or not the parameter coordinates correspond with a field.
   */
  //@ ensures (xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim) ==> \result == true;
  public /*@ pure @*/ boolean isField(int xpos, int ypos) {
    return xpos >= 0 && xpos < dim && ypos >= 0 && ypos < dim;
  }
  
  /**
   * Checks the input using <code>isField()</code> and returns if the corresponding field is empty.
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @param zpos , integer representing z coordinate.
   * @return whether or not the parameter coordinates point to a empty field. 
   */
  //@ requires isField(xpos, ypos, zpos);
  //@ ensures getField(xpos,ypos,zpos) == Mark.E ==> \result == true;
  public /*@ pure @*/ boolean isEmptyField(int xpos, int ypos, int zpos) {
    return getField(xpos,ypos,zpos).equals(Mark.E) && isField(xpos, ypos, zpos);
  }
  
  /**
   * returns whether or not the corresponding field has place for another <code>Mark</code>.
   * If maximum height is reached it will return false. 
   * @param xpos , integer representing x coordinate.
   * @param ypos , integer representing y coordinate.
   * @return returns whether or not the corresponding field has place for another <code>Mark</code>.
   */
  //@ requires isField(xpos, ypos);
  //@ ensures (\exists int z; 0 <= z && z < dim; isEmptyField(xpos, ypos, z)) ==> \result == true;
  public /*@ pure @*/ boolean isEmptyField(int xpos, int ypos) {
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
  /*@ requires 0 >= zpos && zpos < dim;
    @ ensures (\forall int x, y; 0 >= x && x < dim && 0 >= y && y < dim;
    @ isEmptyField(x, y, zpos)) ==> \result == true;
    @*/
  public /*@ pure @*/ boolean isEmptyLayer(int zpos) {
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
  //@ requires isField(xpos, ypos, zpos);
  //@ ensures fields[xpos][ypos][zpos] == \result;
  public /*@ pure @*/ Mark getField(int xpos, int ypos, int zpos) {
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
  //@ requires isField(xpos, ypos, zpos);
  //@ ensures fields[xpos][ypos][zpos] == mark;
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
  //@ requires isEmptyField(xpos, ypos);
  /*@ ensures (\exists int z; 0 <= z && z < dim; getField(xpos, ypos, z) == mark &&
    @ (\forall int q; 0 <= q && q < z; !isEmptyField(xpos, ypos, q)));
    @*/
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
  /*@ ensures (\exists int z; 0 <= z && z < dim; 
    @ (\exists int x; 0 <= x && x < dim;
    @ (\forall int y; 0 <= y && y < dim; getField(x, y, z) == mark))) ||
    @ (\exists int z; 0 <= z && z < dim; (\exists int y; 0 <= y && y < dim;
    @ (\forall int x; 0 <= x && x < dim; getField(x, y, z) == mark))) ||
    @ (\exists int y; 0 <= y && y < dim;
    @ (\exists int x; 0 <= x && x < dim;
    @ (\forall int z; 0 <= z && z < dim; getField(x, y, z) == mark))) ==> \result == true;
    @*/
  public /*@ pure @*/ boolean has1DLine(Mark mark) {
    for (int z = 0; z < dim; z++) {
      for (int x = 0; x < dim; x++) {
        for (int y = 0; y < dim; y++) {
          if (x == 0 && fields[x][y][z] == mark && fields[x + 1][y][z] == mark 
              && fields[x + 2][y][z] == mark && fields[x + 3][y][z] == mark) {
            return true;
          }
          if (y == 0 && fields[x][y][z] == mark && fields[x][y + 1][z] == mark 
              && fields[x][y + 2][z] == mark && fields[x][y + 3][z] == mark) {
            return true;
          }
          if (z == 0 && fields[x][y][z] == mark && fields[x][y][z + 1] == mark 
              && fields[x][y][z + 2] == mark && fields[x][y][z + 3] == mark) {
            return true;
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
  // JML not applicable here.
  public /*@ pure @*/ boolean has2DLine(Mark mark) {    
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
  // JML not applicable here.
  public /*@ pure @*/ boolean has3DLine(Mark mark) {
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
  //@ ensures true ==> has1DLine(mark) || has2DLine(mark) || has3DLine(mark);
  public /*@ pure @*/ boolean isWinner(Mark mark) {
    return has1DLine(mark) || has2DLine(mark) || has3DLine(mark);
  }
  
  /**
   * Checks whether or not the board has a winner using both marks in <code>isWinner()</code>.
   * @return whether or not the board has a winner.
   */
  //@ ensures true ==> isWinner(Mark.O) || isWinner(Mark.X);
  public /*@ pure @*/ boolean hasWinner() {
    return isWinner(Mark.O) || isWinner(Mark.X);
  }
  
  /**
   * Checks whether or not all fields are full using <code>isEmptyField()</code>.
   * @return whether or not all fields are full.
   */
  /*@ ensures true ==> (\forall int x, y, z;
    @ 0 <= x && x < dim && 0 <= y && y < dim && 0 <= z && z < dim; isEmptyField(x, y, z));
    @*/
  public /*@ pure @*/ boolean isFull() {
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