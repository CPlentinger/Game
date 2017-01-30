package project.game;
import java.io.Serializable;

public class HumanPlayer extends Player {
  
  public HumanPlayer()  {
    super();
  }
  
  public String getMove(String question) {
    System.out.print(question);
    return getInput(question);
  }
  
}
