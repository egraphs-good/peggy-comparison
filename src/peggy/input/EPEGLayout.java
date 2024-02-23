package peggy.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class provides a Layout for EPEGs, where each valuegroup can be 
 * expanded incrementally.
 */
public class EPEGLayout<L,P> extends Layout {
	private static final long serialVersionUID = 43278065342L;
	private final CPeggyAxiomEngine<L,P> engine;
	private final Set<? extends CPEGValue<L,P>> roots;
	private final boolean buildAll;
	public EPEGLayout(
			CPeggyAxiomEngine<L,P> _engine,
			Set<? extends CPEGValue<L,P>> _roots,
			boolean all) {
		this.engine = _engine;
		this.roots = _roots;
		this.buildAll = all;
		this.build();
	}
	protected Set<? extends Value> getValues() {
		final Map<CPEGValue<L,P>,Value> value2value = 
			new HashMap<CPEGValue<L,P>, Value>();
		int counter = 0;
		
		for (CPEGValue<L,P> value : this.engine.getEGraph().getValueManager().getValues()) {
			Value newvalue = new Value(counter++, this.roots.contains(value));
			value2value.put(value, newvalue);
		}
		
		for (Map.Entry<CPEGValue<L,P>,Value> entry : value2value.entrySet()) {
			for (CPEGTerm<L,P> term : entry.getKey().getTerms()) {
				Value[] children = new Value[term.getArity()];
				for (int i = 0; i < term.getArity(); i++)
					children[i] = value2value.get(term.getChild(i).getValue());
				entry.getValue().addTerm(term.getOp().toString(), children);
			}
		}
		
		if (buildAll) {
			return new HashSet<Value>(value2value.values());
		} else {
			Set<Value> result = new HashSet<Value>();
			for (CPEGValue<L,P> root : roots) {
				result.add(value2value.get(root));
			}
			return result;
		}
	}
	public static <L,P> void showEPEG(
			CPeggyAxiomEngine<L,P> engine,
			Set<? extends CPEGValue<L,P>> roots,
			boolean all) {
		Layout.run(new EPEGLayout<L,P>(engine, roots, all));
	}
}
