package project.game;

public class ComputerPlayer extends Player {
  
  private int thinkingTime;
  
  /**
   * Creates a <code>ComputerPlayer</code> using the constructor of <code>Player</code>,
   * in addition a <code>thinkingTime</code> will be defined for this <code>Player</code>.
   * @param time, double representing the <code>thinkingTime</code> in seconds,
   *              this will be converted to an integer in milliseconds.
   */
  public ComputerPlayer(double time) {
    super();
    this.thinkingTime = (int) (time * 1000);
  }
  
  /**
   * Defines the <code>makeMove</code> method for this player.
   * For the <code>ComputerPlayer</code> this returns:
   * if the question indicates to calculate a move: A winning move, independently from which <code>Mark</code>.
   *                                                If this move isn't possible, <code>randomMove()</code> will be used.
   * if the question doesn't indicate to make a move: <code>getInput()</code> will be used to get input from the console.
   */
  @Override
  public String makeMove(String question) {
    System.out.print(question);
    long startTime = System.currentTimeMillis();
    if (question.contains("make your move") || question.equals("hint: ")) {
      Board copy = deepCopy();
      winMoveCheck:
      for (int z = 0; z < copy.dim; z++) {
        for (int x = 0; x < copy.dim; x++) {
          for (int y = 0; y < copy.dim; y++) {
            if (System.currentTimeMillis() - startTime >= thinkingTime) {
              break winMoveCheck;
            }
            if (copy.isEmptyField(x, y, z)) {
              copy.setField(x, y, z, getMark());
              if (copy.hasWinner()) {
                return x + " " + y; 
              }
              copy.setField(x, y, z, getMark().other());
              if (copy.hasWinner()) {
                return x + " " + y;
              }
              copy.setField(x, y, z, Mark.E);
            }
          }
        }
      }
      return randomMove();
    } else {
      return getInput(question);
    }
  }
  
  /**
   * Calculates random x and y coordinates for the next move.
   * @return a random move.
   */
  public String randomMove() {
    Board copy = deepCopy();
    int xpos = (int) Math.round(Math.random() * (copy.dim -1));
    int ypos = (int) Math.round(Math.random() * (copy.dim -1));
    while (!copy.isEmptyField(xpos, ypos)) {
      xpos = (int) Math.round(Math.random() * (copy.dim - 1));
      ypos = (int) Math.round(Math.random() * (copy.dim - 1));
    }
    return xpos + " " + ypos;
  }
}
