package peggy;

/**
 * This represents any object that has a logger.
 */
public interface Loggable {
	public Logger getLogger();
	public void setLogger(Logger logger);
}
