package peggy.revert;

import peggy.revert.MiniPEG.Vertex;
import util.Pattern;

/**
 * This class has some helper methods when dealing with a PEGCFG.
 */
public class PEGCFGUtils {
	/**
	 * Checks every block to see if it has a branch condition of TRUE or FALSE.
	 * If so, the branch condition can be removed and one of the successors
	 * can also be removed.
	 * The isTrue and isFalse patterns are used to determine when a label is
	 * true or false (since the label has unknown type)
	 */
	public static <L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>,Y extends PEGCFGBlock<L,P,R,T,X,Y>> 
	void foldUnconditionalBranches(X cfg, Pattern<L> isTrue, Pattern<L> isFalse) {
		for (Y block : cfg.getBlocks()) {
			if (block.getBranchCondition() == null)
				continue;
			Vertex<Item<L,P,T>> bc = block.getBranchCondition();
			if (!bc.getLabel().isLabel())
				continue;
			if (isTrue.matches(bc.getLabel().getLabel())) {
				block.setBranchCondition(null);
				block.removeSucc(1);
			} else if (isFalse.matches(bc.getLabel().getLabel())) {
				block.setBranchCondition(null);
				block.removeSucc(0);
			}
		}
	}
}
