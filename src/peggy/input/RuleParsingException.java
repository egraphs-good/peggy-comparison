package peggy.input;

/**
 * This exception should be thrown by a RuleParser when it reads an
 * invalid rule. This should NOT be thrown as a wrapper around an IOException.
 * 
 * @author steppm
 */
public class RuleParsingException extends RuntimeException {
	public static final long serialVersionUID = 0xBEEFBEEFBEEFL;
	public RuleParsingException(String message) {
		super(message);
	}
	public RuleParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
