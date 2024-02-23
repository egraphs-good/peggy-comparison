package peggy.pb;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a pseudo boolean formulation that is explicitly written to disk and
 * backed by a specific file.
 * Must have the objective function set before adding any constraints.
 */
public abstract class BackedPseudoBooleanFormulation<N> extends PseudoBooleanFormulation<N> {
	protected final Set<Variable<N>> variables;
	protected final File backingFile;
	protected final PrintStream backingStream;
	protected final CounterOutputStream counterStream;
	protected boolean closed = false;
	private boolean writtenObjectiveFunction = false;
	private final int maxSize;
	
	protected BackedPseudoBooleanFormulation(File _backingFile, int _maxSize) throws IOException {
		this.backingFile = _backingFile;
		this.variables = new HashSet<Variable<N>>();
		this.backingStream = new PrintStream(
				counterStream = new CounterOutputStream(new FileOutputStream(this.backingFile)));
		this.maxSize = _maxSize;
	}
	protected BackedPseudoBooleanFormulation(File _backingFile) throws IOException {
		this(_backingFile, 0);
	}
	
	public final File getBackingFile() {
		return this.backingFile;
	}
	public final Set<Variable<N>> getVariables() {
		return this.variables;
	}
	public boolean isClosed() {return this.closed;}
	
	protected abstract void writeObjectiveFunction(ObjectiveFunction<N> objFunction);
	protected abstract boolean writeConstraint(Constraint<N> cons, String comment);

	/**
	 * Called at the end to flush and close the backing stream.
	 */
	public void close() {
		if (this.closed) throw new RuntimeException("Formulation is closed");
		this.backingStream.flush();
		this.backingStream.close();
		this.closed = true;
	}
	
	/**
	 * Can only be called once.
	 */
	public final void setObjectiveFunction(ObjectiveFunction<N> objFunction) {
		if (this.closed) throw new RuntimeException("Formulation is closed");
		if (this.writtenObjectiveFunction)
			throw new IllegalArgumentException("Already written objective function");

		if (this.maxSize > 0 && counterStream.getWrittenByteCount() >= maxSize) {
			throw new RuntimeException("Exceeded maximum file size");
		}
		
		for (Variable<N> v : objFunction.getWeightMap().getAssignedVariables()) {
			this.variables.add(v);
		}
		this.writeObjectiveFunction(objFunction);
		this.writtenObjectiveFunction = true;
	}
	
	/** 
	 * Cannot be called until after setObjectiveFunction has been called.
	 */
	public final void addConstraint(Constraint<N> cons) {
		this.addConstraint(cons, null);
	}
	/** 
	 * Cannot be called until after setObjectiveFunction has been called.
	 */
	public final void addConstraint(Constraint<N> cons, String comment) {
		if (this.closed) throw new RuntimeException("Formulation is closed");

		if (this.maxSize > 0 && counterStream.getWrittenByteCount() >= maxSize) {
			throw new RuntimeException("Exceeded maximum file size");
		}

		this.writeConstraint(cons, comment);
		for (Variable<N> v : cons.getWeightMap().getAssignedVariables()) {
			this.variables.add(v);
		}
	}
}
