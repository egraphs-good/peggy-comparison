package peggy.pb;

import java.io.File;
import java.util.Set;

public interface PBRunner<N, PBF extends PseudoBooleanFormulation<N>> {
	public File getPBOutputFile(File pbInputFile);
	public Set<Variable<N>> run(PBF formulation) throws PBRunnerException;
	public int getTimeout();
}
