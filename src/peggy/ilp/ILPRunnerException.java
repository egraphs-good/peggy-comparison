package peggy.ilp;

public class ILPRunnerException extends Exception {
	private static final long serialVersionUID = 5439763497L;
	public ILPRunnerException(String message) {
		super(message);
	}
	public ILPRunnerException(String message, Throwable t) {
		super(message, t);
	}
}
