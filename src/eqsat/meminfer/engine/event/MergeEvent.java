package eqsat.meminfer.engine.event;

import util.pair.Pair;

public abstract class MergeEvent<L, R, J, C> extends AbstractEvent<C> {
	protected final FunctionEvent<L,J> mLeft;
	protected final FunctionEvent<R,J> mRight;
	
	public MergeEvent(FunctionEvent<L,J> left, FunctionEvent<R,J> right) {
		mLeft = left;
		mRight = right;
		mLeft.addListener(new EventListener<Pair<L,J>>() {
			public boolean notify(Pair<L,J> parameter) {
				J join = parameter.getSecond();
				if (mRight.hasPreimage(join))
					for (R match : mRight.getPreimage(join))
						trigger(combine(parameter.getFirst(), match));
				return true;
			}
			public boolean canUse(Pair<L,J> parameter) {
				return canCombineLeft(parameter.getFirst())
						&& listenersCanUse(combineLeft(parameter.getFirst()));
			}
			public String toString() {
				return "Left " + MergeEvent.this.toString();
			}
		});
		mRight.addListener(new EventListener<Pair<R,J>>() {
			public boolean notify(Pair<R,J> parameter) {
				J join = parameter.getSecond();
				if (mLeft.hasPreimage(join))
					for (L match : mLeft.getPreimage(join))
						trigger(combine(match, parameter.getFirst()));
				return true;
			}
			public boolean canUse(Pair<R,J> parameter) {
				return canCombineRight(parameter.getFirst())
						&& listenersCanUse(combineRight(parameter.getFirst()));
			}
			public String toString() {
				return "Right " + MergeEvent.this.toString();
			}
		});
	}
	
	public FunctionEvent<L,J> getLeft() {return mLeft;}
	public FunctionEvent<R,J> getRight() {return mRight;}

	protected boolean canCombine(L left, R right) {return true;}
	protected abstract C combine(L left, R right);

	protected boolean canCombineLeft(L left) {return false;}
	protected C combineLeft(L left) {throw new IllegalArgumentException();}
	protected boolean canCombineRight(R right) {return false;}
	protected C combineRight(R right) {throw new IllegalArgumentException();}
}