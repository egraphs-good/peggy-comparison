package peggy.analysis.java.inlining;

import soot.jimple.internal.JimpleLocal;

/**
 * This class is a wrapper around JimpleLocals, and implements a more 
 * reasonable hashCode and equals method.
 */
public class JimpleWrapper {
	protected final JimpleLocal local;
	public JimpleWrapper(JimpleLocal _local) {
		this.local = _local;
	}
	public int hashCode() {
		return local.getName().hashCode()*31 + local.getType().hashCode()*37;
	}
	public boolean equals(Object o) {
		if (o == null || !(o instanceof JimpleWrapper))
			return false;
		JimpleWrapper w = (JimpleWrapper)o;
		return w.local.getName().equals(this.local.getName()) &&
			w.local.getType().equals(this.local.getType());
	}
	public String toString() {
		return this.local.getName();
	}
}
