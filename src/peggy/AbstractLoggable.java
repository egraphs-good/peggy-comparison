package peggy;

/**
 * This is an abstract class that implements the Loggable interface,
 * and provides basic state and implementations of the methods.
 */
public abstract class AbstractLoggable implements Loggable {
	protected Logger logger;
	public void setLogger(Logger _logger) {this.logger = _logger;}
	public Logger getLogger() {return this.logger;}
}
