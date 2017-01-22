
public enum Mark {
	E, O, X;
	
	public Mark Other(Mark m) {
		if (m.equals(O)) {
			return X;
		} else if (m.equals(X)) {
			return O;
		} else {
			return E;
		}
	}
}
