package peggy.pb;

public class PBRunnerException extends Exception {
	public static final long serialVersionUID = 54307653L;
	public PBRunnerException(String message) {
		super(message);
	}
	public PBRunnerException(String message, Throwable t) {
		super(message, t);
	}
}
