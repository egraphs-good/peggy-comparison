package peggy.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import util.HashMultiMap;
import util.MultiMap;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.IsInvariant;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class runs on the EPEG occasionally during saturation and attempts
 * to collapse nested phis that share the same branch condition.
 * If you have phi(A,phi(A,B,C),D) then that equals phi(A,B,D), since the inner phi
 * will be in a context where A must be true. This same optimization can be
 * performed on a larger scale, where the inner phi is not necessarily the
 * immediate child of the outer phi. That's what this class tries to do.
 */
public abstract class DynamicPhiCollapser<L,P> extends Analysis<L,P> {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("DynamicPhiCollapser: " + message);
	}

	private int threshold = 8;
	
	protected DynamicPhiCollapser(
			Network _network,
			CPeggyAxiomEngine<L, P> _engine) {
		super(_network, _engine);
	}
	
	public void setThreshold(int t) {this.threshold = t;}
	public int getThreshold() {return this.threshold;}
	
	private class PathInfo {
		final LinkedList<CPEGTerm<L,P>> path;
		final LinkedList<Integer> indexes;
		final CPEGTerm<L,P> subphi;
		PathInfo(
				CPEGTerm<L,P> _subphi,
				LinkedList<CPEGTerm<L,P>> _path, 
				LinkedList<Integer> _indexes) {
			this.subphi = _subphi;
			this.path = new LinkedList<CPEGTerm<L,P>>(_path);
			this.indexes = new LinkedList<Integer>(_indexes);
		}
		private Integer hashCache = null;
		public int hashCode() {
			if (hashCache == null) {
				hashCache = this.path.hashCode()*3 + this.indexes.hashCode()*5 + this.subphi.hashCode()*7;
			}
			return hashCache;
		}
		public boolean equals(Object o) {
			if (!(o instanceof DynamicPhiCollapser.PathInfo))
				return false;
			PathInfo info = (PathInfo)o;
			return 
				this.subphi.equals(info.subphi) &&
				this.path.equals(info.path) && 
				this.indexes.equals(info.indexes);
		}
	}
	
	private final MultiMap<CPEGTerm<L,P>,PathInfo> phi2subphis = 
		new HashMultiMap<CPEGTerm<L,P>,PathInfo>();
	
	public void run() {
		debug("Running dynamic phi collapser...");
		long start = System.currentTimeMillis();
		
		for (CPEGValue<L,P> value : 
				new HashSet<CPEGValue<L,P>>(getEngine().getEGraph().getValueManager().getValues())) {
			for (CPEGTerm<L,P> term : 
					new HashSet<CPEGTerm<L,P>>(value.getTerms())) {
				if (term.getOp().isPhi()) {
					Helper trueHelper = new Helper(term, true, term.getValue().getInvariance());
					if (trueHelper.run(1, term.getChild(1).getValue())) {
						debug("Collapsed a phi on the true side!");
					}
					
					Helper falseHelper = new Helper(term, false, term.getValue().getInvariance());
					if (falseHelper.run(2, term.getChild(2).getValue())) {
						debug("Collapsed a phi on the false side!");
					}
				}
			}
		}

		long end = System.currentTimeMillis();

		debug("Finished running dynamic phi collapser");
		debug("Took " + (end-start) + " ms");
	}
	
	private static boolean variesLess(int invariance1, int invariance2) {
		int max1 = 0, max2 = 0;
		for (int i = 0; i < 32; i++) {
			if ((invariance1&(1<<i)) == 0)
				max1 = i;
			if ((invariance2&(1<<i)) == 0)
				max2 = i;
		}
		return max1 < max2;
	}
	
	
	private class Helper {
		final CPEGTerm<L,P> phi;
		final boolean which;
		final LinkedList<CPEGTerm<L,P>> path = new LinkedList<CPEGTerm<L,P>>();
		final LinkedList<CPEGValue<L,P>> valuepath = new LinkedList<CPEGValue<L,P>>();
		final LinkedList<Integer> indexes = new LinkedList<Integer>();
		final int invariance;
		
		Helper(CPEGTerm<L,P> _phi, boolean _which, int _invariance) {
			this.phi = _phi;
			this.which = _which;
			this.invariance = _invariance;
		}
		
		public boolean run(int index, CPEGValue<L,P> current) {
			if (valuepath.contains(current) || phi.getValue().equals(current))
				return false;
			if (threshold > 0 && path.size() >= threshold)
				return false;
			
			if (variesLess(current.getInvariance(), invariance))
				return false;

			for (CPEGTerm<L,P> childTerm : current.getTerms()) {
				if (childTerm.getOp().isTheta() &&
					(invariance&(1<<childTerm.getOp().getLoopDepth())) == 0) {
					// can't go below thetas in which I vary
					continue;
				}
				
				if (childTerm.getOp().isPhi()) {
					PathInfo newinfo = new PathInfo(childTerm, path, indexes);
							
					if (phi2subphis.containsEntry(phi, newinfo)) {
						debug("Skipping this subphi (seen already!)");
						continue; // already done this one! skip
					}

					if (childTerm.getChild(0).getValue().equals(phi.getChild(0).getValue())) {
						// same condition! collapse!
						collapse(index, childTerm);
						phi2subphis.putValue(phi, newinfo);
						return true;
					} else {
						// not same condition, treat as default node
						indexes.addLast(index);
						path.addLast(childTerm);
						valuepath.addLast(current);

						for (int i = 0; i < childTerm.getArity(); i++) {
							if (run(i, childTerm.getChild(i).getValue())) {
								indexes.removeLast();
								path.removeLast();
								valuepath.removeLast();
								return true;
							}
						}

						indexes.removeLast();
						path.removeLast();
						valuepath.removeLast();
					}
				} else {
					indexes.addLast(index);
					path.addLast(childTerm);
					valuepath.addLast(current);
					
					for (int i = 0; i < childTerm.getArity(); i++) {
						if (run(i, childTerm.getChild(i).getValue())) {
							indexes.removeLast();
							path.removeLast();
							valuepath.removeLast();
							return true;
						}
					}

					indexes.removeLast();
					path.removeLast();
					valuepath.removeLast();
				}
			}
			
			return false;
		}
		
		private void collapse(int lastChildIndex, CPEGTerm<L,P> subphi) {
			// add the proof and equivalence to the engine
			// neither phi nor subphi are in the path
			
			Proof proof = (enableProofs ? new Proof("Dynamic phi collapsing") : null);
			
			CPEGTerm<L,P> lastchild = subphi;
			int lastindex = lastChildIndex;
			Iterator<CPEGTerm<L,P>> pathIter = path.descendingIterator();
			Iterator<Integer> indexIter = indexes.descendingIterator();
			Map<Integer,CPEGTerm<L,P>> thetas = new HashMap<Integer,CPEGTerm<L,P>>();
			Set<CPEGTerm<L,P>> nonthetas = new HashSet<CPEGTerm<L,P>>();
			nonthetas.add(phi);
			nonthetas.add(subphi);
			
			// start making the new phi path
			FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph = 
				new FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>>();
			FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> bottom = 
				futureGraph.getVertex(subphi.getChild(which ? 1 : 2));

			// add path properties backwards
			while (pathIter.hasNext()) {
				CPEGTerm<L,P> currparent = pathIter.next();
				if (enableProofs) {
					proof.addProperty(new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(currparent, lastindex, lastchild));
					proof.addProperty(new ArityIs<CPEGTerm<L,P>>(currparent, currparent.getArity()));
				}

				if (currparent.getOp().isTheta()) {
					if (enableProofs) proof.addProperty(new OpIsLoopOp<CPEGTerm<L,P>>(currparent, PEGLoopOp.Theta));
					thetas.put(currparent.getOp().getLoopDepth(), currparent);
				} else {
					if (enableProofs) proof.addProperty(new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(currparent, currparent.getOp()));
					nonthetas.add(currparent);
				}

				// build the new future expression
				FutureExpressionGraph.Vertex[] children = 
					new FutureExpressionGraph.Vertex[currparent.getArity()];
				for (int i = 0; i < children.length; i++) {
					if (i==lastindex) {
						children[i] = bottom;
					} else {
						children[i] = futureGraph.getVertex(currparent.getChild(i));
					}
				}
				bottom = futureGraph.getExpression(currparent.getOp(), children);
				
				lastchild = currparent;
				lastindex = indexIter.next();
			}
			
			if (enableProofs) {
				// last child equiv property (with phi)
				proof.addProperty(new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(phi, lastindex, lastchild));
	
				// add top phi properties
				proof.addProperty(new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(phi, phi.getOp()));
				proof.addProperty(new ArityIs<CPEGTerm<L,P>>(phi, phi.getArity()));
	
				// add bottom phi properties
				proof.addProperty(new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(subphi, subphi.getOp()));
				proof.addProperty(new ArityIs<CPEGTerm<L,P>>(subphi, subphi.getArity()));
			
				// add the theta invariance properties
				for (int depth : thetas.keySet()) {
					for (CPEGTerm<L,P> nontheta : nonthetas) {
						proof.addProperty(new IsInvariant<CPEGTerm<L,P>,CPEGValue<L,P>>(nontheta, thetas.get(depth)));
					}
				}
			}

			// get future top phi expression
			FutureExpression<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> newphi = 
				futureGraph.getExpression(
					phi.getOp(), 
					futureGraph.getVertex(phi.getChild(0)),
					(which ? bottom : futureGraph.getVertex(phi.getChild(1))),
					(which ? futureGraph.getVertex(phi.getChild(2)) : bottom));

			getEngine().getEGraph().addExpressions(futureGraph);
			getEngine().getEGraph().makeEqual(phi, newphi.getTerm(), proof);
			getEngine().getEGraph().processEqualities();
		}
	}
}
