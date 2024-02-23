package peggy.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import peggy.pb.Digraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a Digraph that contains CPEGValues as its nodes.
 */
public class EngineValueDigraph<L,P> implements Digraph<CPEGValue<L,P>> {
	protected final CPeggyAxiomEngine<L,P> engine;
	
	public EngineValueDigraph(CPeggyAxiomEngine<L,P> _engine) {
		this.engine = _engine;
	}
	
	public int getNodeCount() {
		return this.engine.getEGraph().getValueManager().getValues().size();
	}
	public Iterable<? extends CPEGValue<L,P>> getNodes() {
		return Collections.unmodifiableCollection(
				this.engine.getEGraph().getValueManager().getValues());
	}
	public Iterable<? extends CPEGValue<L,P>> getSuccessors(
			CPEGValue<L,P> value) {
		Set<CPEGValue<L,P>> successors = 
			new HashSet<CPEGValue<L,P>>();
		for (CPEGTerm<L,P> term : value.getTerms()) {
			for (int i = 0; i < term.getArity(); i++)  
				successors.add(term.getChild(i).getValue());
		}
		return successors;
	}
	public Digraph<CPEGValue<L,P>> getReverseDigraph() {
		return new Digraph<CPEGValue<L,P>>() {
			public int getNodeCount() {
				return EngineValueDigraph.this.getNodeCount(); 
			}
			public Iterable<? extends CPEGValue<L,P>> getNodes() {
				return EngineValueDigraph.this.getNodes();
			}
			public Digraph<CPEGValue<L,P>> getReverseDigraph() {
				return EngineValueDigraph.this;
			}
			public Iterable<? extends CPEGValue<L,P>> getSuccessors(
					CPEGValue<L,P> value) {
				Set<CPEGValue<L,P>> successors = 
					new HashSet<CPEGValue<L,P>>();
				for (CPEGValue<L,P> other : 
					engine.getEGraph().getValueManager().getValues()) {
					otherloop:
					for (CPEGTerm<L,P> term : other.getTerms()) {
						for (int i = 0; i < term.getArity(); i++) {
							if (term.getChild(i).getValue().equals(value)) {
								successors.add(other);
								continue otherloop;
							}
						}
					}
				}
				return successors;
			}
		};
	}
}
