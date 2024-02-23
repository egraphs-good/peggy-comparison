package peggy.pb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MinisatRunner<N> implements PBRunner<N,MinisatFormulation<N>> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("MinisatRunner: " + message);
	}
	
	protected abstract String getPBCommandPath();

	/**
	 * This method will write out the given PB formulation to a file, then run Minisat 
	 * This method will return null if a timeout occurred, or if the subprocess has an exit value != 0.
	 *  
	 * @param pb The PB formulation
	 * @return The set of variables that the PB solver chose. If there is an error or a timeout, null is returned.
	 * @throws PBRunnerException If the PB output file cannot be read
	 */
	public Set<Variable<N>> run(MinisatFormulation<N> pb) throws PBRunnerException {
		if (!pb.isClosed()) pb.close();
		
		final List<String> lines = new ArrayList<String>();
		final boolean[] gotResult = new boolean[1]; 
		
		ProcessListener listener = new ProcessListener() {
			public void processTerminated(int exitValue) {}
			public void processTimedOut() {}
			public void processWroteStdout(String line) {
				lines.add(line);
				if (line.startsWith("v "))
					gotResult[0] = true;
			}
			public void processWroteStderr(String line) {}
		};
		
		ProcessRunner runner = new ProcessRunner(new String[]{
				this.getPBCommandPath(), "-v0", pb.getBackingFile().getAbsolutePath()
		},getTimeout());
		
		runner.addListener(listener);
		try {
			runner.start();
		} catch (Throwable t) {
			debug("error starting thread");
			if (DEBUG)
				t.printStackTrace();
			return null;
		}
		runner.waitFor();

		if (!gotResult[0])
			return null;

		Set<String> truenodes = new HashSet<String>();
		for (String line : lines) {
			if (line.startsWith("v ")) {
				String[] tokens = line.split("\\s+");
				for (String token : tokens) {
					if (token.equals("") || 
						token.equals("v") ||
						token.startsWith("-"))
						continue;
					truenodes.add(token);
				}
			}
		}
		
		Set<Variable<N>> result = new HashSet<Variable<N>>();
		for (Variable<N> var : pb.getVariables()){
			if (truenodes.contains(var.name))
				result.add(var);
		}
		return result;
	}
}
