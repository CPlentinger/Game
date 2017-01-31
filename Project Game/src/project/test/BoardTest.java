package project.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import project.game.Board;
import project.game.Mark;

public class BoardTest {

  Board bConstructorTest;
  Board bDeepCopyTest;
  
  @Before
  public void setUp() {
    bConstructorTest = new Board(4);
    bDeepCopyTest = new Board(4);
  }

  @Test
  public void ConstructorTest() {
    int emptyFields = 0;
    for (int z = 0; z < 4; z++) {
      for (int y = 0; y < 4; z++) {
        for (int x = 0; x < 4; z++) {
          if (bConstructorTest.fields[x][y][z].equals(Mark.E)) {
            emptyFields++;
          }
        }
      }
    }
    assertEquals(bConstructorTest.fields.length, emptyFields, 64);
  }
  
  @Test
  public void deepCopyTest() {
    bDeepCopyTest.fields[0][1][3] = Mark.X;
    bDeepCopyTest.fields[2][0][1] = Mark.O;
    bDeepCopyTest.fields[3][1][2] = Mark.X;
    bDeepCopyTest.fields[1][2][0] = Mark.O;
    bDeepCopyTest.fields[3][3][2] = Mark.X;
    Board copy = bDeepCopyTest.deepCopy();
    assertEquals(copy.fields[0][1][3], Mark.X);
    assertEquals(copy.fields[2][0][1], Mark.O);
    assertEquals(copy.fields[3][1][2], Mark.X);
    assertEquals(copy.fields[1][2][0], Mark.O);
    assertEquals(copy.fields[3][3][2], Mark.X);
    assertEquals(bDeepCopyTest.dim, copy.dim);
  }
  
  @Test
  public void isEmptyFieldTest() {
    
  }
  

}
