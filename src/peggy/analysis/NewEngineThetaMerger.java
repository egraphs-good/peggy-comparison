package peggy.analysis;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIsLoopLifted;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class runs periodically on the engine and looks for 
 * theta loops that can be merged.
 * @author mstepp
 */
public class NewEngineThetaMerger<L,P> extends EngineThetaMerger<L,P> {
	private static boolean DEBUG = true;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("NewEngineThetaMerger: " + message);
	}
	
	public NewEngineThetaMerger(CPeggyAxiomEngine<L,P> _engine) {
		super(_engine);
	}
	
	public void mergeThetas() {
		if (getLogger() != null)
			getLogger().log("Begin theta merger");
		startTime = System.currentTimeMillis();
		
		// find all thetas and sort into bins by depth
		List<List<CPEGTerm<L,P>>> thetas = new ArrayList<List<CPEGTerm<L,P>>>();
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				if (term.getOp().isTheta()) {
					int depth = term.getOp().getLoopDepth();
					while (thetas.size() <= depth)
						thetas.add(new ArrayList<CPEGTerm<L,P>>());
					thetas.get(depth).add(term);
				}
			}
		}

		// match up thetas with same 0-th child, then call find
		toploop:
		for (List<CPEGTerm<L,P>> tI : thetas) {
			for (int j = 0; j < tI.size(); j++) {
				for (int k = j+1; k < tI.size(); k++) {
					CPEGTerm<L,P> termJ = tI.get(j);
					CPEGTerm<L,P> termK = tI.get(k);
					if (termJ.getValue().equals(termK.getValue()))
						continue;
					
					CPEGValue<L,P> firstChildJ = termJ.getChild(0).getValue();
					CPEGValue<L,P> firstChildK = termK.getChild(0).getValue();
					if (firstChildJ.equals(firstChildK)) {
						// try to merge!
						LinkedList<TermPair> path = new LinkedList<TermPair>();
						LinkedList<Integer> pathIndexes = new LinkedList<Integer>();
						PairInfo info = new PairInfo(new TermPair(termJ, termK));
						final boolean[] found = {false};
						InfoListener listener = new InfoListener() {
							public void trigger(CPEGValue<L,P> value) {
								debug("Should not happen!");
							}
							public void trigger(PairInfo pair) {
								debug("Toplevel finished!");
								found[0] = true;
							}
						};
						info.addListener(listener);
						
						try {
							find(info, path, pathIndexes);
						} catch(IllegalStateException ex) {
							if (getLogger() != null)
								getLogger().log("Theta merger timed out after " + timeoutMillis + " milliseconds");
							break toploop;
						}
							
						if (found[0]) {
							if (DEBUG)
							try {
								PrintStream out = new PrintStream(
										new FileOutputStream("merger" + (fileCounter++) + ".dot"));
								dotMerge(out, info);
							} catch (Throwable t) {}
							
							if (getLogger() != null)
								getLogger().log("Theta merger successfully merged 2 thetas");
							
							proveIt(info);
						}
					}
				}
			}
		}
		
		if (getLogger() != null)
			getLogger().log("Begin theta merger");
	}
	private int fileCounter = 0;
	
	private void dotMerge(PrintStream out, PairInfo root) {
		out.println("digraph {");
		out.println("   ordering=out;");
		Set<String> ids = new HashSet<String>();
		dotHelper(out, root, true, ids);
		dotHelper(out, root, false, ids);
		out.println("}");
	}
	private String dotHelper(PrintStream out, PairInfo root, boolean left, Set<String> ids) {
		final TermPair terms = root.terms;
		
		if (left) {
			final String myid = "term" + terms.left.hashCode();
			if (ids.contains(myid))
				return myid;
			ids.add(myid);
			out.println("   " + myid + " [label=\"" + terms.left.getOp() + "\"];");
			for (int i = 0; i < root.children.size(); i++) {
				Child ci = root.children.get(i);
				String child;
				if (ci.isValue()) {
					child = "value" + ci.getValue().hashCode();
					out.println("   " + child + " [label=\"" + child + "\"];"); 
				} else {
					child = dotHelper(out, ci.getPairInfo(), left, ids);
				}
				out.println("   " + myid + " -> " + child + " ;");
			}
			return myid;
		} else {
			final String myid = "term" + terms.right.hashCode();
			if (ids.contains(myid))
				return myid;
			ids.add(myid);
			out.println("   " + myid + " [label=\"" + terms.right.getOp() + "\"];");
			for (int i = 0; i < root.children.size(); i++) {
				Child ci = root.children.get(i);
				String child;
				if (ci.isValue()) {
					child = "value" + ci.getValue().hashCode();
					out.println("   " + child + " [label=\"" + child + "\"];"); 
				} else {
					child = dotHelper(out, ci.getPairInfo(), left, ids);
				}
				out.println("   " + myid + " -> " + child + " ;");
			}
			return myid;
		}
	}
	
	
	class TermPair {
		public final CPEGTerm<L,P> left, right;
		TermPair(CPEGTerm<L,P> _left, CPEGTerm<L,P> _right) {
			this.left = _left;
			this.right = _right;
		}
	}
	abstract class Child {
		public boolean isPair() {return false;}
		public PairInfo getPairInfo() {throw new UnsupportedOperationException();}
		
		public boolean isValue() {return false;}
		public CPEGValue<L,P> getValue() {throw new UnsupportedOperationException();}
	}
	class PairChild extends Child {
		private final PairInfo info;
		PairChild(PairInfo _info) {
			info = _info;
		}
		public boolean isPair() {return true;}
		public PairInfo getPairInfo() {return info;}
	}
	class ValueChild extends Child {
		private final CPEGValue<L,P> value;
		ValueChild(CPEGValue<L,P> _value) {
			this.value = _value;
		}
		public boolean isValue() {return true;}
		public CPEGValue<L,P> getValue() {return value;}
	}
	abstract class InfoListener {
		public abstract void trigger(PairInfo pair);
		public abstract void trigger(CPEGValue<L,P> value);
	}
	class PairInfo {
		final TermPair terms;
		final List<Child> children;
		private final List<InfoListener> listeners = 
			new ArrayList<InfoListener>();
		boolean done = false;
		public PairInfo(TermPair _terms) {
			terms = _terms;
			children = new ArrayList<Child>();
			for (int i = 0; i < terms.left.getArity(); i++)
				children.add(null);
		}
		public void addListener(InfoListener list) {
			listeners.add(list);
		}
		public void sendTrigger() {
			done = true;
			for (InfoListener l : listeners)
				l.trigger(this);
		}
		private class Listener extends InfoListener {
			final int index;
			public Listener(int _index) {
				this.index = _index;
			}
			public void trigger(CPEGValue<L,P> value) {
				if (children.get(index) != null)
					return;
				Child child = new ValueChild(value);
				children.set(index, child);
				if (!done) {
					for (Child c : children)
						if (c==null) return;
					sendTrigger();
				}
			}
			public void trigger(PairInfo pair) {
				if (children.get(index) != null)
					return;
				Child child = new PairChild(pair);
				children.set(index, child);
				if (!done) {
					for (Child c : children)
						if (c==null) return;
					sendTrigger();
				}
			}
		}
		public InfoListener makeListener(int index) {
			return new Listener(index);
		}
	}
	
	private void proveIt(PairInfo root) {
		Proof proof = (enableProofs ? new Proof("Loop congruence") : null);
		Set<TermPair> pairs = new HashSet<TermPair>();
		proofHelper(root, proof, new LinkedList<CPEGTerm<L,P>>(), pairs);
		for (TermPair p : pairs) {
			engine.getEGraph().makeEqual(p.left, p.right, proof);
		}
		engine.getEGraph().processEqualities();
	}
	private void proofHelper(
			PairInfo root, 
			Proof proof, 
			LinkedList<CPEGTerm<L,P>> thetas,
			Set<TermPair> pairs) {
		pairs.add(root.terms);
		
		if (enableProofs)
			proof.addProperties(
					new OpsEqual<FlowValue<P,L>,CPEGTerm<L,P>>(
							root.terms.left, root.terms.right),
					new ArityIs<CPEGTerm<L,P>>(root.terms.left, root.terms.left.getArity()),
					new ArityIs<CPEGTerm<L,P>>(root.terms.right, root.terms.right.getArity()));

		boolean isTheta = false;
		if (root.terms.left.getOp().isTheta()) {
			if (enableProofs)
				proof.addProperties(
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.left, PEGLoopOp.Theta),
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.right, PEGLoopOp.Theta));
			isTheta = true;
		} else if (root.terms.left.getOp().isEval()) {
			if (enableProofs)
				proof.addProperties(
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.left, PEGLoopOp.Eval),
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.right, PEGLoopOp.Eval));
		} else if (root.terms.left.getOp().isPass()) {
			if (enableProofs)
				proof.addProperties(
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.left, PEGLoopOp.Pass),
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.right, PEGLoopOp.Pass));
		} else if (root.terms.left.getOp().isShift()) {
			if (enableProofs)
				proof.addProperties(
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.left, PEGLoopOp.Shift),
					new OpIsLoopOp<CPEGTerm<L,P>>(root.terms.right, PEGLoopOp.Shift));
		} else {
			// add looplifted property
			for (CPEGTerm<L,P> theta : thetas) {
				if (enableProofs)
					proof.addProperties(
						new OpIsLoopLifted<CPEGTerm<L,P>>(root.terms.left, theta),
						new OpIsLoopLifted<CPEGTerm<L,P>>(root.terms.right, theta));
			}
		}
		
		// do children
		final int arity = root.terms.left.getArity();
		for (int i = 0; i < arity; i++) {
			Child ci = root.children.get(i);
			if (ci==null) continue;
			
			if (ci.isValue()) {
				if (enableProofs)
					proof.addProperty(
						new EquivalentChildren<CPEGTerm<L,P>,CPEGValue<L,P>>(
								root.terms.left, i, root.terms.right, i));
			} else {
				TermPair childpair = ci.getPairInfo().terms;
				if (enableProofs)
					proof.addProperties(
						new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
								root.terms.left, i, childpair.left),
						new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
								root.terms.right, i, childpair.right));

				// recurse
				if (isTheta) thetas.addLast(root.terms.left);
				proofHelper(ci.getPairInfo(), proof, thetas, pairs);
				if (isTheta) thetas.removeLast();
			}
		}
	}
	
	// the terms in 'info' will already be alpha-similar
	private void find(
			PairInfo info,
			LinkedList<TermPair> path,
			LinkedList<Integer> pathIndexes) {
		checkTime();
		
		for (TermPair pp : path) {
			checkTime();
			if (pp.left.equals(info.terms.left)) {
				// left same
				if (pp.right.equals(info.terms.right)) {
					// right same, trigger
					// check for a theta[1] parent
					if (path.size() < 2)
						return;
					for (int i = 0; i < path.size(); i++) {
						CPEGTerm<L,P> t = path.get(i).left;
						int index = pathIndexes.get(i);
						if (t.getOp().isTheta() && index==1) {
							// found one!
							info.sendTrigger();
							return;
						}
					}
					// didn't find one, bail
				}
				// else, not true
				return;
			} else if (pp.right.equals(info.terms.right)) {
				// not true
				return;
			}
		}
		
		// check that term is either theta or loop-lifted by all thetas along path
		if (!info.terms.left.getOp().isTheta()) {
			for (TermPair pair : path) {
				CPEGTerm<L,P> tp = pair.left;
				if (tp.getOp().isTheta() && 
					!info.terms.left.getOp().isLoopLifted(tp.getOp().getLoopDepth(), 1)) {
					// not loop-lifted, bail
					return;
				}
			}
		}

		checkTime();
		
		// not seen either before
		final int arity = info.terms.left.getArity();
		List<Set<TermPair>> children = new ArrayList<Set<TermPair>>(); 
		for (int i = 0; i < arity; i++) {
			checkTime();
			CPEGValue<L,P> leftChildV = info.terms.left.getChild(i).getValue();
			CPEGValue<L,P> rightChildV = info.terms.right.getChild(i).getValue();
			if (leftChildV.equals(rightChildV)) {
				info.makeListener(i).trigger(leftChildV);
				children.add(null);
				continue;
			}
			
			// different values
			Set<TermPair> childpairs = new HashSet<TermPair>();
			for (CPEGTerm<L,P> leftChild : leftChildV.getTerms()) {
				checkTime();
				for (CPEGTerm<L,P> rightChild : rightChildV.getTerms()) {
					// check alpha similarity
					if (leftChild.getOp().equals(rightChild.getOp()) &&
						leftChild.getArity() == rightChild.getArity()) {
						childpairs.add(new TermPair(leftChild, rightChild)); 
					}
				}
			}

			if (childpairs.size() == 0) {
				// no compatible children along index i, bail
				return;
			}

			children.add(childpairs);
		}
		
		
		// recursive calls
		path.addLast(info.terms);
		for (int i = 0; i < children.size(); i++) {
			pathIndexes.addLast(i);
			Set<TermPair> pairset = children.get(i);
			if (pairset!=null) {
				for (TermPair pair : pairset) {
					// add listener from info, then recurse
					PairInfo childinfo = new PairInfo(pair);
					childinfo.addListener(info.makeListener(i));
					find(childinfo, path, pathIndexes);
				}
			}
			pathIndexes.removeLast();
		}
		path.removeLast();
	}
}
