package project.game;


public enum Mark {
  E, O, X;
  
  /**
   * Returns the opposite of this <code>Mark</code>.
   * @return the opposite of this <code>Mark</code>.
   */
  public Mark other() {
    if (this.equals(O)) {
      return X;
    } else if (this.equals(X)) {
      return O;
    } else {
      return E;
    }
  }
}
