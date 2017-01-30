package project.game;


public enum Mark {
	E, O, X;
	
	public Mark Other() {
		if (this.equals(O)) {
			return X;
		} else if (this.equals(X)) {
			return O;
		} else {
			return E;
		}
	}
}
