package peggy.ilp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import peggy.pb.ProcessListener;
import peggy.pb.ProcessRunner;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This class runs GLPK on the GLPK formulation in a spawned process, and 
 * returns the results.
 */
public abstract class GLPKRunner<L,P> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("GLPKRunner: " + message);
	}
	
	
	protected abstract String getCommandPath();
	protected abstract int getTimeout();
	
	/**
	 * This method will write out the given PB formulation to a file, then run Pueblo 
	 * This method will return null if a timeout occurred, or if the subprocess has an exit value != 0.
	 */
	public Set<CPEGTerm<L,P>> run(GLPKFormulation<L,P> form) throws ILPRunnerException {
		final List<String> lines = new ArrayList<String>();
		final boolean[] gotResult = new boolean[1]; 
		
		ProcessListener listener = new ProcessListener() {
			public void processTerminated(int exitValue) {}
			public void processTimedOut() {}
			public void processWroteStdout(String line) {
				lines.add(line);
				debug("got line: " + line);
				if (line.trim().equals("INTEGER OPTIMAL SOLUTION FOUND"))
					gotResult[0] = true;
			}
			public void processWroteStderr(String line) {}
		};
		
		ProcessRunner runner = new ProcessRunner(new String[]{
				this.getCommandPath(), "--intopt", "-m", form.getBackingFile().getAbsolutePath()
		},getTimeout());
		
		runner.addListener(listener);
		try {
			runner.start();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
		runner.waitFor();

		if (!gotResult[0]) {
			debug("no result");
			return null;
		}

		Set<Integer> Ns = new HashSet<Integer>();
		Pattern pattern = Pattern.compile("^OUTPUT N\\[([0-9]+)\\]$");
		for (String line : lines) {
			
			debug("Found line: " + line);
			
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				int index = Integer.parseInt(m.group(1));
				debug("found index : " + index);
				Ns.add(index);
			}
		}
		
		Set<CPEGTerm<L,P>> result = new HashSet<CPEGTerm<L,P>>();
		for (int index : Ns) {
			result.add(form.getTerm(index));
		}
		return result;
	}
}
