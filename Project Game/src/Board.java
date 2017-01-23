
public class Board {
  public int dim;
  public Mark[][][] fields;
  
  public static void main(String[] args) {
    Board one = new Board(4);
//    one.setField(0, 0, 3, Mark.X);
//    one.setField(1, 1, 2, Mark.X);
//    one.setField(2, 2, 1, Mark.X);
//    one.setField(3, 3, 0, Mark.X);
//    
    one.setField(0, 3, 0, Mark.X);
    one.setField(1, 2, 1, Mark.X);
    one.setField(2, 1, 2, Mark.X);
    one.setField(3, 0, 3, Mark.X);
    System.out.println(one.has3DLine(Mark.X));
  }
  
  public Board(int tDIM) {
    this.dim = tDIM;
    fields = new Mark[dim][dim][dim];
    for (int i = 0; i < fields.length; i++) {
      for (int j = 0; j < fields[i].length; j++) {
        for (int k = 0; k < fields[i][j].length; k++) {
          fields[i][j][k] = Mark.E;
        }
      }
    }
  }
  
  public boolean isField(int x, int y, int z) {
    return x >= 0 && x <= dim && y >= 0 && y <= dim && z >= 0 && z <= dim;
  }
  
  public boolean isField(int x, int y) {
    return x >= 0 && x <= dim && y >= 0 && y <= dim;
  }
  
  public boolean isEmptyField(int x, int y, int z) {
    if (isField(x,y,z)) {
      return getField(x,y,z).equals(Mark.E);
    } else {
      return false;
    }
  }
    
  public Mark getField(int x, int y, int z) {
    if (isField(x, y, z)) {
      return fields[x][y][z];
    } else {
      return null;
    }
  }
    
  public void setField(int x, int y, int z, Mark m) {
    if (isField(x, y, z)) {
      fields[x][y][z] = m;
    }
  }
  
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
    for (int x = 0, y = 0, z = 0; y < dim; x++,y++,z++) {
      if (!getField(x,y,z).equals(m)) {
        break;
      } else if (x == 3) {
        return true;
      }
    }
    for (int x = 3, y = 0,z = 0; y < dim; x--,y++,z++) {
      if (!getField(x,y,z).equals(m)) {
        break;
      } else if (x == 3) {
        return true;
      }
    }
  
    return false;
  }
  
  public boolean has3DLine(Mark m) {
    int one = 0;
    int two = 0;
    int three = 0;
    int four = 0;
    for (int i = 3; i >= 0; i--) {
      if (getField(-i+3,-i+3,-i+3).equals(m)) {
        one++;
      }
      if (getField(-i+3,i,i).equals(m)) {
        two++;
      }
      if (getField(-i+3,-i+3,i).equals(m)) {
        three++;
      }
      if (getField(-i+3,i,-i+3).equals(m)) {
        four++;
      }
      if (i == 0 && (one == 4 || two == 4 || three == 4 || four == 4 )) {
        return true;
      }
    }
    return false;
  }
}