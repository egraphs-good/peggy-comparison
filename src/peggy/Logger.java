package peggy;

/**
 * A logger is anything that produces a log of execution as it proceeds.
 * A logger should be able to produce a sublogger for discrete sub-sections
 * of its execution.
 */
public interface Logger {
	public Logger getSubLogger();
	public void log(String message);
	public void logException(String message, Throwable t);
}
