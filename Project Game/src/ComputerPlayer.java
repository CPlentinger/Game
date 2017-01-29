public class ComputerPlayer extends Player {
  
  private int thinkingTime;
  
  
  public ComputerPlayer(double time) {
    super();
    this.thinkingTime = (int) (time * 1000);
  }
  
  public String getMove(String question) {
    System.out.print(question);    
    long startTime = System.currentTimeMillis();
    if (question.contains("make your move") || question.equals("hint: ")) {
      Board copy = controller.deepCopy();
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
              copy.setField(x, y, z, getMark().Other());
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
  
  public String randomMove() {
    Board copy = controller.deepCopy();
    int xpos = (int) Math.round(Math.random() * copy.dim);
    int ypos = (int) Math.round(Math.random() * copy.dim);
    while (!copy.isEmptyField(xpos, ypos)) {
      xpos = (int) Math.round(Math.random() * copy.dim);
      ypos = (int) Math.round(Math.random() * copy.dim);
    }
    return xpos + " " + ypos;
  }
}
