package peggy.analysis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import peggy.Loggable;
import peggy.Logger;
import peggy.pb.Digraph;
import peggy.pb.SCCAnalysis;
import util.AbstractPattern;
import util.pair.Pair;
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
 * This class runs on the EPEG occasionally during saturation and attempts to 
 * equate equivalent theta-loops (by congruence closure). This cannot be done
 * by the normal engine congruence closure axioms, because of the cycles involved.
 */
public class EngineThetaMerger<L,P> implements Loggable {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("EngineThetaMerger: " + message);
	}
	
	protected Logger logger;
	protected long timeoutMillis = -1L;
	protected long startTime;
	protected final CPeggyAxiomEngine<L,P> engine;
	protected boolean enableProofs = true;
	
	public EngineThetaMerger(CPeggyAxiomEngine<L,P> _engine) {
		this.engine = _engine;
	}
	
	public void setEnabledProofs(boolean p) {
		this.enableProofs = p;
	}
	
	public void setLogger(Logger log) {
		this.logger = log;
	}
	public Logger getLogger() {
		return this.logger;
	}
	
	public void setTimeout(long millis) {
		this.timeoutMillis = millis;
	}
	
	public void mergeThetas() {
		startTime = System.currentTimeMillis();
		if (getLogger() != null)
			getLogger().log("Begin theta merging");
		
		Map<Integer,List<CPEGTerm<L,P>>> index2thetas = 
			new HashMap<Integer,List<CPEGTerm<L,P>>>();
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				if (term.getOp().isTheta()) {
					int index = term.getOp().getLoopDepth();
					List<CPEGTerm<L,P>> thetas = index2thetas.get(index);
					if (thetas == null) {
						thetas = new ArrayList<CPEGTerm<L,P>>();
						index2thetas.put(index, thetas);
					}
					thetas.add(term);
				}
			}
		}

		boolean progress = true;
		toploop:
		while (progress) {
			progress = false;
			for (int index : index2thetas.keySet()) {
				List<CPEGTerm<L,P>> thetas = index2thetas.get(index);
				for (int i = 0; i < thetas.size(); i++) {
					for (int j = i+1; j < thetas.size(); j++) {
						if (thetas.get(i).getValue().equals(thetas.get(j).getValue())) {
							continue;
						}
						
						boolean tried;
						try {
							tried = tryToMerge(thetas.get(i), thetas.get(j));
						} catch (IllegalStateException ex) {
							if (getLogger() != null)
								getLogger().log("Theta merger timed out after " + timeoutMillis + " milliseconds");
							break toploop;
						}
						
						if (tried) {
							if (getLogger() != null)
								getLogger().log("Theta merger successfully merged 2 thetas");
							thetas.remove(j);
							j--;
							progress = true;
						}
					}
				}
			}
		}

		long end = System.currentTimeMillis();
		
		if (getLogger() != null) {
			getLogger().log("End theta merging");
			getLogger().log("Theta merging took " + (end-startTime) + " milliseconds");
		}
	}
	
	private boolean tryToMerge(
			CPEGTerm<L,P> theta1, CPEGTerm<L,P> theta2) {
		if (!theta1.getChild(0).getValue().equals(theta2.getChild(0).getValue()))
			return false;
		
		Digraph<CPEGValue<L,P>> valueGraph = 
			new EngineValueDigraph<L,P>(engine);
		List<Set<CPEGValue<L,P>>> sccs = SCCAnalysis.computeSCC(valueGraph);
		
		Set<CPEGValue<L,P>> scc1 = null, scc2 = null;
		for (Set<CPEGValue<L,P>> scc : sccs) {
			if (scc.contains(theta1.getValue()))
				scc1 = scc;
			if (scc.contains(theta2.getValue()))
				scc2 = scc;
		}
		if (scc1 == null || scc2 == null)
			throw new RuntimeException("This should never happen!");
		
		Info info = new Info(scc1, scc2);

		if (findPath(info, theta1, theta2)) {
			// found a merge!
			proveMerge(info);
			return true;
		} else {
			return false;
		}
	}

	
	private static int COUNTER = 0;
	private void dumpDot(Info info, Set<CPEGTerm<L,P>> alldone) throws IOException {
		PrintStream out1 = new PrintStream(new FileOutputStream("loopA_" + COUNTER + ".dot"));
		//PrintStream out2 = new PrintStream(new FileOutputStream("loopB_" + COUNTER + ".dot"));
		COUNTER++;
		
		out1.println("digraph loopA {");
		for (Map.Entry<CPEGValue<L,P>,CPEGTerm<L,P>> entry1 : info.valueToTerm1.entrySet()) {
			CPEGTerm<L,P> term1 = entry1.getValue();
			CPEGValue<L,P> value1 = term1.getValue();
			if (!alldone.contains(term1))
				continue;
			
			out1.println("  " + value1.hashCode() + " [label=\"" + value1.hashCode() + ":" + 
					term1.getOp().toString() + "\"];");
			for (int i = 0; i < term1.getArity(); i++) {
				CPEGValue<L,P> childValue = term1.getChild(i).getValue();
				out1.println("  " + value1.hashCode() + " -> " + childValue.hashCode() + " [label=\"" + i + "\"];");
			}
		}
		out1.println("}");
		out1.close();
	}
	
	// will not include 'root' unless it's in a loop
	private Set<CPEGTerm<L,P>> descendants(
			CPEGTerm<L,P> root,
			StackMap<CPEGValue<L,P>,CPEGTerm<L,P>> stackmap) {
		Set<CPEGTerm<L,P>> result = new HashSet<CPEGTerm<L,P>>();
		LinkedList<CPEGTerm<L,P>> queue = new LinkedList<CPEGTerm<L,P>>();
		for (int i = 0; i < root.getArity(); i++) {
			if (stackmap.containsKey(root.getChild(i).getValue())) {
				queue.addLast(
						stackmap.get(root.getChild(i).getValue()));
			}
		}
				
		while (!queue.isEmpty()) {
			CPEGTerm<L,P> next = queue.removeFirst();
			if (result.contains(next))
				continue;
			result.add(next);
			
			for (int i = 0; i < next.getArity(); i++) {
				if (stackmap.containsKey(next.getChild(i).getValue())) {
					queue.addLast(
							stackmap.get(next.getChild(i).getValue()));
				}
			}
		}
		return result;
	}
	
	private void proveMerge(final Info info) {
		LinkedList<CPEGTerm<L,P>> thetas = new LinkedList<CPEGTerm<L,P>>();
		for (CPEGTerm<L,P> term1 : info.valueToTerm1.values()) {
			if (term1.getOp().isTheta()) {
				thetas.add(term1);
			}
		}
		
		// TODO remove
		Set<CPEGTerm<L,P>> alldone = new HashSet<CPEGTerm<L,P>>();
		
		// do the cycles
		while (!thetas.isEmpty()) {
			CPEGTerm<L,P> theta = thetas.removeFirst();
			Set<CPEGTerm<L,P>> descendants1 = descendants(theta, info.valueToTerm1);
			if (!descendants1.contains(theta))
				continue; // not a loop

			// TODO remove
			alldone.addAll(descendants1);
			
			final Proof proof = (enableProofs ? new Proof("Loop Congruence") : null);
			Set<CPEGTerm<L,P>> thetasInThisCycle = 
				new HashSet<CPEGTerm<L,P>>();
			for (CPEGTerm<L,P> term1 : descendants1) {
				CPEGValue<L,P> value1 = term1.getValue();
				CPEGValue<L,P> value2 = info.valueToValue.get(value1);
				CPEGTerm<L,P> term2 = info.valueToTerm2.get(value2);
				
				if (enableProofs)
					proof.addProperties(
							new OpsEqual<FlowValue<P,L>,CPEGTerm<L,P>>(
									term1, term2),
							new ArityIs<CPEGTerm<L,P>>(term1, term1.getArity()),
							new ArityIs<CPEGTerm<L,P>>(term2, term2.getArity()));
				
				if (enableProofs)
					for (int j = 0; j < term1.getArity(); j++) {
						CPEGValue<L,P> childValue1 = term1.getChild(j).getValue();
						CPEGValue<L,P> childValue2 = term2.getChild(j).getValue();
						if (childValue1.equals(childValue2)) {
							proof.addProperty(
									new EquivalentChildren<CPEGTerm<L,P>,CPEGValue<L,P>>(
											term1, j, term2, j));
						} else {
							proof.addProperties(
									new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
											term1, j, info.valueToTerm1.get(childValue1)),
									new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
											term2, j, info.valueToTerm2.get(childValue2)));
						}
					}

				if (term1.getOp().isTheta()) {
					if (enableProofs)
						proof.addProperties(
							new OpIsLoopOp<CPEGTerm<L,P>>(term1, PEGLoopOp.Theta));
					thetasInThisCycle.add(term1);
				} else if (term1.getOp().isEval()) {
					if (enableProofs)
						proof.addProperties(
							new OpIsLoopOp<CPEGTerm<L,P>>(term1, PEGLoopOp.Eval));
				} else if (term1.getOp().isPass()) {
					if (enableProofs)
						proof.addProperties(
							new OpIsLoopOp<CPEGTerm<L,P>>(term1, PEGLoopOp.Pass));
				} else if (term1.getOp().isShift()) {
					if (enableProofs)
						proof.addProperties(
							new OpIsLoopOp<CPEGTerm<L,P>>(term1, PEGLoopOp.Shift));
				}
			}
			
			// add the looplifted property
			for (final CPEGTerm<L,P> localTheta : thetasInThisCycle) {
				AbstractPattern<CPEGTerm<L,P>> pattern = 
					new AbstractPattern<CPEGTerm<L,P>>() {
						Map<CPEGTerm<L,P>,Boolean> cache = 
							new HashMap<CPEGTerm<L,P>,Boolean>();
					
						public boolean matches(CPEGTerm<L,P> term) {
							if (term.equals(localTheta))
								return true;
							if (cache.containsKey(term))
								return cache.get(term);
							
							boolean match = false;
							cache.put(term, false);
							for (int i = 0; i < term.getArity(); i++) {
								CPEGValue<L,P> childValue = term.getChild(i).getValue();
								if (!info.valueToTerm1.containsKey(childValue))
									continue;
								CPEGTerm<L,P> childTerm = info.valueToTerm1.get(childValue);
								match |= matches(childTerm);
							}
							cache.put(term, match);
							if (match) {
								// add looplifted property
								if (!term.getOp().isLoopFunction()) {
									if (enableProofs)
										proof.addProperty(
											new OpIsLoopLifted<CPEGTerm<L,P>>(
													term, localTheta));
								}
								return true;
							} else {
								return false;
							}
						}
					};
					
				pattern.matches(info.valueToTerm1.get(localTheta.getChild(1).getValue()));
			}
			
			// make equalities
			for (CPEGTerm<L,P> term1 : descendants1) {
				CPEGValue<L,P> value1 = term1.getValue();
				CPEGValue<L,P> value2 = info.valueToValue.get(value1);
				CPEGTerm<L,P> term2 = info.valueToTerm2.get(value2);

				engine.getEGraph().makeEqual(term1, term2, proof);
			}
			
			// remove from leftover and thetas
			thetas.removeAll(thetasInThisCycle);
		}

		// print out the info
		if (DEBUG) {
			try {
				dumpDot(info, alldone);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		engine.getEGraph().processEqualities();
	}
	
	class Info {
		final StackMap<CPEGValue<L,P>,CPEGTerm<L,P>> valueToTerm1, valueToTerm2;
		final StackMap<CPEGValue<L,P>,CPEGValue<L,P>> valueToValue;
		final Stack<Pair<CPEGTerm<L,P>,Integer>> path1;
		final Stack<Pair<CPEGTerm<L,P>,Integer>> path2;
		final Set<CPEGValue<L,P>> scc1, scc2;
		
		Info(Set<CPEGValue<L,P>> _scc1, Set<CPEGValue<L,P>> _scc2) {
			valueToTerm1 =  
				new StackMap<CPEGValue<L,P>,CPEGTerm<L,P>>();
			valueToTerm2 =  
				new StackMap<CPEGValue<L,P>,CPEGTerm<L,P>>();
			valueToValue =  
				new StackMap<CPEGValue<L,P>,CPEGValue<L,P>>();
			path1 = new Stack<Pair<CPEGTerm<L,P>,Integer>>();
			path2 = new Stack<Pair<CPEGTerm<L,P>,Integer>>();
			scc1 = _scc1;
			scc2 = _scc2;
		}
		public void popToHeight(int height) {
			valueToTerm1.popToHeight(height);
			valueToTerm2.popToHeight(height);
			valueToValue.popToHeight(height);
		}
		
		public void popPathsToHeight(int height) {
			while (path1.size() > height) {
				path1.pop();
				path2.pop();
			}
		}
	}
	
	private boolean findPath(
			Info info,
			CPEGTerm<L,P> term1,
			CPEGTerm<L,P> term2) {
		checkTime();
		
		CPEGValue<L,P> value1 = term1.getValue();
		CPEGValue<L,P> value2 = term2.getValue();
		
		if (info.valueToTerm1.containsKey(value1)) {
			// value1 is mapped
			if (info.valueToValue.get(value1).equals(value2)) {
				// value1 mapped to value2
				boolean result = info.valueToTerm1.get(value1).equals(term1) &&
					info.valueToTerm2.get(value2).equals(term2);
				return result;
			} else {
				return false;
			}
		} else if (info.valueToTerm2.containsKey(value2)) {
			// value2 is mapped but value1 is not, error
			return false;
		}
		// neither value mapped!

		// check ops
		if (!term1.getOp().equals(term2.getOp())) {
			return false;
		}
		
		// check that op is either theta or loop lifted by all thetas along child 1
		if (term1.getOp().isTheta()) {/*skip*/}
		else {
			// check that we're lifted by all thetas with child 1 on the current path
			for (int i = info.path1.size()-1; i >= 0; i--) {
				Pair<CPEGTerm<L,P>,Integer> pair = 
					info.path1.get(i);
				if (pair.getFirst().getOp().isTheta() &&
					pair.getSecond().intValue() == 1) {
					// theta1
					if (!term1.getOp().isLoopLifted(
							pair.getFirst().getOp().getLoopDepth(),1)) {
						return false;
					}
				}
			}
		}

		checkTime();
		
		// check arities
		if (term1.getArity() != term2.getArity()) {
			return false;
		}
		
		if (value1.equals(value2)) {
			return true;
		}
		
		// check children
		final int height = info.valueToTerm1.getHeight();
		info.valueToTerm1.push(value1, term1);
		info.valueToTerm2.push(value2, term2);
		info.valueToValue.push(value1, value2);

		if (doChildren(info, 0, term1, term2)) {
			return true;
		} else {
			info.popToHeight(height);
			return false;
		}
	}

	protected void checkTime() {
		if (timeoutMillis > 0) {
			if (System.currentTimeMillis()-startTime > timeoutMillis)
				throw new IllegalStateException();
		}
	}
	
	private boolean doChildren(
			Info info, int index, CPEGTerm<L,P> term1, CPEGTerm<L,P> term2) {
		checkTime();
		
		if (index >= term1.getArity()) {
			return true;
		}
		
		CPEGValue<L,P> childValue1 = term1.getChild(index).getValue();
		CPEGValue<L,P> childValue2 = term2.getChild(index).getValue();
		if (childValue1.equals(childValue2)) {
			return doChildren(info, index+1, term1, term2);
		}

		if (!(info.scc1.contains(childValue1) && info.scc2.contains(childValue2))) {
			// values outside the SCCs must be equal
			return false;
		}
		// both in the loop

		// if child value already mapped, only use its term
		Iterable<? extends CPEGTerm<L,P>> childTerms1;
		if (info.valueToTerm1.containsKey(childValue1)) {
			// loopback, better go through a theta!
			CPEGTerm<L,P> childTerm = info.valueToTerm1.get(childValue1);
			boolean foundTheta1 = false;
			for (int i = info.path1.size()-1; i >= 0; i--) {
				Pair<CPEGTerm<L,P>,Integer> pair = 
					info.path1.get(i);
				if (pair.getFirst().getOp().isTheta() &&
					pair.getSecond().intValue() == 1) {
					foundTheta1 = true;
					break;
				}
				if (pair.getFirst().equals(childTerm))
					break;
			}
			if (!foundTheta1) {
				return false;
			}
			childTerms1 = Collections.singleton(childTerm);
		} else {
			childTerms1 = childValue1.getTerms();
		}

		checkTime();
		
		// if child value already mapped, only use its term
		Iterable<? extends CPEGTerm<L,P>> childTerms2;
		if (info.valueToTerm2.containsKey(childValue2)) {
			CPEGTerm<L,P> childTerm = info.valueToTerm2.get(childValue2);
			boolean foundTheta1 = false;
			for (int i = info.path2.size()-1; i >= 0; i--) {
				Pair<CPEGTerm<L,P>,Integer> pair = 
					info.path2.get(i);
				if (pair.getFirst().getOp().isTheta() &&
					pair.getSecond().intValue() == 1) {
					foundTheta1 = true;
					break;
				}
				if (pair.getFirst().equals(childTerm))
					break;
			}
			if (!foundTheta1) {
				return false;
			}
			childTerms2 = Collections.singleton(childTerm);
		} else {
			childTerms2 = childValue2.getTerms();
		}
		
		checkTime();
		
		final int height = info.valueToTerm1.getHeight();
		final int pathHeight = info.path1.size();
		for (CPEGTerm<L,P> childTerm1 : childTerms1) {
			for (CPEGTerm<L,P> childTerm2 : childTerms2) {
				info.path1.push(new Pair<CPEGTerm<L,P>,Integer>(term1, index));
				info.path2.push(new Pair<CPEGTerm<L,P>,Integer>(term2, index));
				// for each pair of terms from the child values
				if (findPath(info, childTerm1, childTerm2)) {
					info.popPathsToHeight(pathHeight);
					if (doChildren(info, index+1, term1, term2)) {
						return true;
					}
				}
				info.popPathsToHeight(pathHeight);
				info.popToHeight(height);
			}
		}
		
		return false;
	}
}
