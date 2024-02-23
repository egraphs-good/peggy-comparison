package peggy;

/**
 * This is thrown when an error occurs while parsing command line options.
 */
public class OptionParsingException extends RuntimeException {
	private static final long serialVersionUID = 3276432L;
	public OptionParsingException(String msg) {
		super(msg);
	}
	public OptionParsingException(String msg, Throwable t) {
		super(msg, t);
	}
}
