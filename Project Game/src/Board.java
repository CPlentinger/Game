import java.util.Arrays;

public class Board {
  public int dim;
  public Mark[][][] fields;
  
  public static void main(String[] args) {
    Board one = new Board(4);
    one.setField(0, 0, 0, Mark.X);
    one.setField(1, 1, 1, Mark.X);
    one.setField(2, 2, 1, Mark.X);
    one.setField(3, 3, 1, Mark.X);
    System.out.println(one.getField(0, 0, 0));
    System.out.println(one.getField(1, 1, 0));
    System.out.println(one.getField(2, 2, 0));
    System.out.println(one.getField(3, 3, 0));
    System.out.println(one.hasLayerDiagonal(Mark.X));
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
  
  public boolean hasLayerDiagonal(Mark m) {
    for (int z = 0; z < dim; z++) {
      for (int y = 0; y < dim; y++) {
        for (int x = 0; x < dim; x++) {
          if (getField(x,y,z)==m) {
            if (getField(x-1,y-1,z)==m) {
              if (getField(x-2,y-2,z)==m) {
                if (getField(x-3,y-3,z)==m) {
                  return true;
                }
              }
            }
            if (getField(x-1,y+1,z)==m) {
              if (getField(x-2,y+2,z)==m) {
                if (getField(x-3,y+3,z)==m) {
                  return true;
                }
              }              
            }
            if (getField(x+1,y-1,z)==m) {
              if (getField(x+2,y-2,z)==m) {
                if (getField(x+3,y-3,z)==m) {
                  return true;
                }
              }
            }
            if (getField(x+1,y+1,z)==m) {
              if (getField(x+2,y+2,z)==m) {
                if (getField(x+3,y+3,z)==m) {
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
  public boolean hasLayerColumn(Mark m) {
    int streak = 0;
    for (int z = 0; z < dim; z++) {
      for (int y = 0; y < dim; y++) {
        for (int x = 0; x < dim; x++) {
          if (getField(x,y,z) == m) {
            if (streak < 4) {
              streak++;
            }
          } else {
            streak = 0;
          }
        }
        if (streak == 4) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean hasLayerRow(Mark m) {
    int streak = 0;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        for (int k = 0; k < dim; k++) {
          if (getField(j,k,i) == m) {
            if (streak < 4) {
              streak++;
            }
          } else {
            streak = 0;
          }
          if (streak == 4) {
            return true;
          }
        }
      }
    }
    return false;
  }
}