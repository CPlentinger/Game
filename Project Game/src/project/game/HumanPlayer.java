package project.game;

public class HumanPlayer extends Player {
  
  public HumanPlayer()  {
    super();
  }
  

  @Override
  public String makeMove(String question) {
    return getInput(question);
  }
  
}
