package peggy.pb;

public interface ProcessListener {
	public void processWroteStdout(String data);
	public void processWroteStderr(String data);
	public void processTerminated(int exitValue);
	//public void processDestroyed();
	public void processTimedOut();
}
