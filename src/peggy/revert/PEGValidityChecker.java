package peggy.revert;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import peggy.analysis.StackMap;
import util.pair.Pair;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This class tests the results of a reversion heuristic to see
 * if it creates a valid PEG.
 */
public class PEGValidityChecker {
	private static boolean DEBUG = false;
	private static final void debug(String message) {
		if (DEBUG)
			System.err.println("PEGValidityChecker: " + message);
	}

	/**
	 * Useful for debugging.
	 */
	public static <O,P> void map2dot(
			Map<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			Set<CPEGValue<O,P>> roots,
			PrintStream out) {
		
		if (DEBUG) {
			if (!value2term.keySet().containsAll(roots))
				debug("Not all roots contained!");
		}
		
		Set<CPEGValue<O,P>> seen = new HashSet<CPEGValue<O,P>>();
		LinkedList<CPEGValue<O,P>> worklist = new LinkedList<CPEGValue<O,P>>();
		worklist.addAll(roots);
		
		out.println("digraph {");
		while (!worklist.isEmpty()) {
			CPEGValue<O,P> value = worklist.removeFirst();
			if (seen.contains(value))
				continue;
			seen.add(value);
			CPEGTerm<O,P> term = value2term.get(value);
			int myid = term.hashCode();
			String color = (roots.contains(value) ? "red" : "black");
			out.println("   " + myid + " [color=" + color + ", label=\"" + term.getOp().toString() + "\"];");
			for (int i = 0; i < term.getArity(); i++) {
				CPEGValue<O,P> childValue = term.getChild(i).getValue();
				int childid = value2term.get(childValue).hashCode();
				out.println("   " + myid + " -> " + childid + " [label=\"" + i + "\"];");
				worklist.addLast(childValue);
			}
		}
		out.println("}");
	}
	
	public static <O,P> boolean isValid(
			Map<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			Set<CPEGValue<O,P>> roots) {
		
		if (DEBUG) {
			PrintStream out = null;
			try {
				out = new PrintStream(new FileOutputStream("optpeg.dot"));
			} catch (Throwable t) {
				debug("Cannot open output stream");
				t.printStackTrace();
			}
			if (out != null)
				map2dot(value2term, roots, out);
		}

		StackMap<CPEGTerm<O,P>,Pair<CPEGTerm<O,P>,Integer>> node2child =
			new StackMap<CPEGTerm<O,P>,Pair<CPEGTerm<O,P>,Integer>>();
		for (CPEGValue<O,P> root : roots) {
			CPEGTerm<O,P> rootterm = value2term.get(root);
			if (rootterm == null) {
				debug("Root value unmapped");
				return false;
			}
			node2child.clear();
			if (!isValidHelper(rootterm, value2term, node2child))
				return false;
		}

		debug("All valid!");
		return true;
	}
	

	/* Constraints:
	 * 	- every node must have all its children
	 * 	- every loop must go through a theta on child 1
	 * 	- theta_i must be invariant beyond i (maxvariance <= i)
	 * 	- eval_i/pass_i must have 0th child be invariant beyond i
	 * 	- eval_i must have pass_i as 1st child
	 */
	private static <O,P> boolean isValidHelper(
			CPEGTerm<O,P> term,
			Map<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			StackMap<CPEGTerm<O,P>,Pair<CPEGTerm<O,P>,Integer>> node2child) {
		if (node2child.containsKey(term)) {
			// closed a loop
			// check for right child of theta
				// look for any theta[1] between occurrences of term 
			CPEGTerm<O,P> parent = term;
			Pair<CPEGTerm<O,P>,Integer> child = node2child.get(term);

			debug("Checking recursive term: " + term);

			Set<CPEGTerm<O,P>> thispath = new HashSet<CPEGTerm<O,P>>();

			while (child != null) {

				debug("child = " + child);

				thispath.add(child.getFirst());

				if (parent.getOp().isTheta() &&
						child.getSecond().intValue() == 1) {
					debug("Loop through nontheta found theta[1]");
					return true;
				} else if (child.getFirst() == term) {
					debug("Weird self-loop through " + term);

					debug("path: " + thispath.toString());
					debug("stackmap: " + node2child);

					return false;
				}
				parent = child.getFirst();
				child = node2child.get(parent);
			}
			debug("Loop through nontheta found NO theta[1]");
			return false;

		}
		
		// check label
		if (term.getOp().isEval()) {
			// - eval_i/pass_i must have 0th child be invariant beyond i
			// - eval_i must have pass_i as 1st child
			int depth = term.getOp().getLoopDepth();
			CPEGTerm<O,P> left = value2term.get(term.getChild(0).getValue());
			CPEGTerm<O,P> right = value2term.get(term.getChild(1).getValue());
			if (left == null || right == null) {
				debug("Eval is missing child " + node2child.size());
				return false;
			}
			
			if (!right.getOp().isPass() && 
				right.getOp().getLoopDepth() == depth) {
				debug("Eval's right child is invalid: " + right);
				return false;
			}

			if (left.getValue().getMaxVariance() > depth) {
				debug("Eval's left child has bad variance");
				return false;
			}
		} else if (term.getOp().isPass()) {
			// - eval_i/pass_i must have 0th child be invariant beyond i
			int depth = term.getOp().getLoopDepth();
			CPEGTerm<O,P> child = value2term.get(term.getChild(0).getValue());
			if (child == null) {
				debug("Pass is missing its child");
				return false;
			}
			
			if (child.getValue().getMaxVariance() > depth) {
				debug("Pass' child has bad variance");
				return false;
			}
		} else if (term.getOp().isTheta()) {
			// - theta_i must be invariant beyond i (maxvariance <= i)
			int depth = term.getOp().getLoopDepth();
			if (term.getValue().getMaxVariance() > depth) {
				debug("Theta has bad variance");
				return false;
			}
		}
		
		
		// check children
		for (int i = 0; i < term.getArity(); i++) {
			CPEGTerm<O,P> childTerm = 
				value2term.get(term.getChild(i).getValue());
			if (childTerm == null) {
				debug("Term is missing child " + node2child.size());
				return false;
			}
			
			node2child.push(
					term, 
					new Pair<CPEGTerm<O,P>,Integer>(childTerm, i));
			if (!isValidHelper(childTerm, value2term, node2child)) {
				return false;
			}
			node2child.pop();
		}
		
		return true;
	}
}
