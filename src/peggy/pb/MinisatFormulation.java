package peggy.pb;

import java.io.*;

public class MinisatFormulation<N> extends OPBFormulation<N> {
	public MinisatFormulation(File _backingFile) throws IOException {
		super(_backingFile);
	}
	public MinisatFormulation(File _backingFile, int _maxSize) throws IOException {
		super(_backingFile, _maxSize);
	}
	protected void wroteConstraint(Constraint<N> cons) {}
}
