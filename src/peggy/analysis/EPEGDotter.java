package peggy.analysis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

/**
 * This class is used to produce a DOT graph description of an EPEG.
 * The EPEG is defined abstractly, through the abstract methods of this class.
 */
public abstract class EPEGDotter<T,V> {
	protected abstract String getLabel(T term);
	protected abstract Collection<? extends T> getTerms(V value);
	protected abstract List<V> getValues();
	protected abstract int getArity(T term);
	protected abstract V getChildValue(T term, int child);
	protected abstract boolean isRoot(V value);

	public void dot(PrintStream out) {
		final List<V> values = getValues();
		
		out.println("digraph {");
		out.println("ordering=out;");
		out.println("compound=true;");
		
		for (V value : values) {
			final int vhash = value.hashCode();
			final int myindex = values.indexOf(value);
			out.println("subgraph cluster" + vhash + " {");
			out.println("  name" + vhash + " [label=\"Value " + myindex + "\", rank=source, shape=box];");
			
			for (T term : getTerms(value)) {
				final int thash = term.hashCode();
				out.println("  term" + thash + " [label=\"" + getLabel(term) + "\"];");
				for (int i = 0; i < getArity(term); i++) {
					final int cvindex = values.indexOf(getChildValue(term, i));
					out.println("  sink_" + thash + "_" + i + " [label=\"" +  cvindex + "\"];");
					out.println("  term" + thash + " -> sink_" + thash + "_" + i + " ;");
				}
			}
			out.println("}");
		}
		out.println("}");
	}
}
