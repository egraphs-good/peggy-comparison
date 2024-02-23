package peggy.pb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is a valuemap specifically for CPEGValues and CPEGTerms.
 */
public class EPEGValueMap<O,P> implements ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> {
	private final Map<CPEGValue<O,P>,Set<CPEGTerm<O,P>>> value2parents;
	
	public EPEGValueMap(ValueManager<CPEGValue<O,P>> _vm) {
		Map<CPEGValue<O,P>,Set<CPEGTerm<O,P>>> v2p = 
			new HashMap<CPEGValue<O,P>,Set<CPEGTerm<O,P>>>();
		for (CPEGValue<O,P> value : _vm.getValues())
			v2p.put(value, new HashSet<CPEGTerm<O,P>>());
		for (CPEGValue<O,P> value : _vm.getValues()) {
			for (CPEGTerm<O,P> node : value.getTerms()) {
				for (int i = 0; i < node.getArity(); i++) {
					CPEGValue<O,P> childValue = node.getChild(i).getValue();
					Set<CPEGTerm<O,P>> parents = v2p.get(childValue);
					parents.add(node);
				}
			}
		}
		this.value2parents = v2p;
	}
	
	/** Should always be true unless this node is from another
	 *  EPEG.
	 */
	public boolean containsNode(CPEGTerm<O,P> node) {
		return this.value2parents.containsKey(node.getValue());
	}

	public CPEGValue<O,P> getValue(CPEGTerm<O,P> node) {
		CPEGValue<O,P> value = node.getValue();
		if (this.value2parents.containsKey(value))
			return value;
		else
			throw new NoSuchElementException("Node not in graph");
	}
	public int getArity(CPEGTerm<O,P> node) {
		if (!this.containsNode(node))
			throw new NoSuchElementException("Node not in graph");
		else
			return node.getArity();
	}
	public CPEGValue<O,P> getChildValue(
			CPEGTerm<O,P> node, int index) {
		if (!this.containsNode(node))
			throw new NoSuchElementException("Node not in graph");
		else
			return node.getChild(index).getValue();
	}
	
	public Iterable<? extends CPEGTerm<O,P>> getParentNodes(CPEGValue<O,P> value) {
		return this.value2parents.get(value);
	}
	public Iterable<? extends CPEGTerm<O,P>> getNodes(CPEGValue<O,P> value) {
		return value.getTerms();
	}
}
