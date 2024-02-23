package peggy.pb;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessRunner {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("ProcessRunner: " + message);
	}
	
	private Process process;
	private boolean running;
	private ReadThread readThread;
	private BufferedReader stdout, stderr;
	private final List<ProcessListener> listeners = 
		new ArrayList<ProcessListener>();
	private final long timeout;
	private final Object mutex = new Object();
	private final Object cond = new Object();

	private class ReadThread extends Thread {
		public void run() {
			List<String> lines = new ArrayList<String>(100);
			long start = System.currentTimeMillis();
			boolean runningnow;
			synchronized(mutex) {runningnow = running;}
			Integer exit = null;
			boolean terminated = true;
			while (runningnow) {
				// check for termination
				exit = null;
				try {
					exit = new Integer(process.exitValue());
				} catch (Throwable t) {}
				if (exit != null) {
					// if i got here, then it has exited (or died?)
					synchronized(mutex) {running = false;}
					terminated = true;
					break;
				}
				
				// check for timeout
				long now = System.currentTimeMillis();
				if (timeout > 0 && now-start > timeout) {
					try {
						process.destroy();
					} catch (Throwable t) {}
					synchronized(mutex) {running = false;}
					terminated = false;
					break;
				}

				// check for data on stdout
				String line;
				boolean read = false;
				lines.clear();
				try {
					while (stdout.ready()) {
						line = stdout.readLine();
						read = true;
						lines.add(line);
					}
				} catch (Throwable t) {}
				if (read) {
					for (String l : lines) 
						for (ProcessListener listener : listeners)
							listener.processWroteStdout(l);
				}

				// check for data on stderr
				read = false;
				lines.clear();
				try {
					while (stderr.ready()) {
						line = stderr.readLine();
						read = true;
						lines.add(line);
					}
				} catch (Throwable t) {}
				if (read) {
					for (String l : lines)
						for (ProcessListener listener : listeners)
							listener.processWroteStderr(l);
				}
				
				try {Thread.sleep(100);}
				catch (Throwable t) {}
			}

			// flush remaining output
			try {
				while (stdout.ready()) {
					String line = stdout.readLine();
					for (ProcessListener listener : listeners) {
						listener.processWroteStdout(line);
					}
				}
			} catch (Throwable t) {}
			try {
				while (stderr.ready()) {
					String line = stderr.readLine();
					for (ProcessListener listener : listeners) {
						listener.processWroteStderr(line);
					}
				}
			} catch (Throwable t) {}
			
			// notify terminated or timedout
			if (terminated) {
				for (ProcessListener listener : listeners) {
					listener.processTerminated(exit);
				}
			} else {
				for (ProcessListener listener : listeners) {
					listener.processTimedOut();
				}
			}
			synchronized(cond) {cond.notifyAll();}
			
			debug("called notify");
		}
	}
	
	private String[] cmds;
	private String[] envp;
	private File dir;
	private boolean started = false;
	
	public ProcessRunner(String[] _cmds, String[] _envp, File _dir, long _timeout) {
		this.timeout = _timeout;
		this.cmds = _cmds;
		this.envp = _envp;
		this.dir = _dir;
	}
	public ProcessRunner(String[] _cmds, String[] _envp, long _timeout) {
		this(_cmds, _envp, null, _timeout); 
	}
	public ProcessRunner(String[] _cmds, long _timeout) {
		this(_cmds, null, null, _timeout);
	}
	
	public void addListener(ProcessListener listener) {
		synchronized(mutex) {if (running) return;}
		if (!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public void start() {
		synchronized(mutex) {
			if (started)
				return;
			started = true;
			running = true;
		}

		try {
			if (DEBUG) {
				for (int i = 0; i < cmds.length; i++) {
					debug("cmds[" + i + "] = " + cmds[i]);
				}
				for (int i = 0; i < envp.length; i++) {
					debug("envp[" + i + "] = " + envp[i]);
				}
				debug("dir = " + dir);
			}
			
			this.process = Runtime.getRuntime().exec(cmds, envp, dir);
			this.stdout = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
			this.stderr = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
		} catch (Throwable t) {
			synchronized(mutex) {
				started = false;
				running = false;
			}
			throw new IllegalArgumentException("Cannot create process", t);
		}
		this.readThread = new ReadThread();
		this.readThread.start();
	}
	
	public void waitFor() {
		boolean runningnow;
		synchronized(mutex) {runningnow = running;}
		if (!runningnow) return;
		while (runningnow) {
			synchronized(cond) {
				try {cond.wait();} catch (Throwable t) {}
			}
			synchronized(mutex) {runningnow = running;}
		}
	}
}
