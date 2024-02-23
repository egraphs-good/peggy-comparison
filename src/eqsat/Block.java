package eqsat;

import java.util.List;

import util.graph.Vertex;

/** A Block in a CFG is viewed as a function from input variable values
 * to output variable values using expressions in L
 *
 * @param <G> The type of the CFG of this block
 * @param <B> The type of the blocks of G
 * @param <V> The type used to represent the variables of the CFG
 * @param <L> The type used to represent values and functions inside this block
 */
public interface Block<G, B, V, L> extends Vertex<G,B> {
	/** A block is assumed to have no children if they are the unique End block
	 * for this CFG, 1 child if it is a fall-through block,
	 * and 2 children if it is a branching block
	 * (the first being the branch taken if the branch condition is true;
	 * the second being the branch taken if the branch condition is false).
	 */
	public List<? extends B> getChildren();
	/** Provided as a possible more efficient version of
	 * getChildren().get(child).
	 */
	public B getChild(int child);
	
	/** Exactly one block in this CFG has no parents, known as the Start block.
	 * @return True if this is the unique Start block of this CFG.
	 */
	public boolean isStart();
	/** Exactly one block in this CFG has no children, known as the End block.
	 * @return True if this is the unique End block of this CFG.
	 */
	public boolean isEnd();
	
	/** A variable is "modified" by this block if the output value of that
	 * variable is not necessarily equal to the input value of that variable
	 * @param variable The variable in question
	 * @return True if it is possible that this block modifies variable
	 */
	public boolean modifies(V variable);
}
