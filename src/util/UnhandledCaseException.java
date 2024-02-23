package util;

/** An exception to be used for handling disjoint unions. */
public class UnhandledCaseException extends RuntimeException {
	private static final long serialVersionUID = 4431274777939085271L;
	
	public UnhandledCaseException() {}
	public UnhandledCaseException(Object object) {
		super(object == null ? "<null>" : object.getClass().toString());
	}
}
