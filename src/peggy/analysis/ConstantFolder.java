package peggy.analysis;

import java.util.List;

/**
 * The general interface for a constant folder.
 * The L parameter is the label type of the node 
 * (generally, the same as the L type of FlowValue).
 */
public interface ConstantFolder<L> {
	public boolean canFold(L root, List<? extends L> children);
	public L fold(L root, List<? extends L> children);
}
