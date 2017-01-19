package Project;

public enum Mark {
	E, R, B;
	
	public Mark other() {
		if (this == B) {
			return R;
		} else if (this == R) {
			return B;
		} else {
			return E;
		}
	}
}
