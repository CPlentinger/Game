package project.game;

public class HumanPlayer extends Player {
  
  /**
   * Uses the constructor of <code>Player</code> to create a <code>HumanPlayer</code>.
   */
  public HumanPlayer()  {
    super();
  }
  
  /**
   * Defines the <code>makeMove</code> method for this player.
   * For the <code>HumanPlayer</code> this is the input from the console.
   */
  @Override
  public String makeMove(String question) {
    return getInput(question);
  }
  
}
