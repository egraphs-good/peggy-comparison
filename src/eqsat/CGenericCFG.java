package eqsat;


public class CGenericCFG<V, L>
		extends GenericCFG<CGenericCFG<V,L>,CGenericCFG.Block<V,L>,V,L> {
	protected final OpAmbassador<L> mAmbassador;
	
	public CGenericCFG(int size, OpAmbassador<L> ambassador) {
		super(size);
		mAmbassador = ambassador;
	}
	
	public CGenericCFG<V,L> getSelf() {return this;}
	
	protected Block<V,L>[] makeBlockArray(int size) {return new Block[size];}
	
	protected Block<V,L> makeBlock(int index) {return new EndBlock(index);}
	protected Block<V,L> makeBlock(int index, int child) {
		return new FallBlock(index, child);
	}
	protected Block<V,L> makeBlock(int index, int trueChild, int falseChild) {
		return new BranchBlock(index, trueChild, falseChild);
	}
	
	public interface Block<V,L>
			extends GenericBlock<CGenericCFG<V,L>,Block<V,L>,V,L> {
	}
	
	protected class EndBlock extends
			GenericCFG<CGenericCFG<V,L>,Block<V,L>,V,L>.EndBlock
			implements Block<V,L> {
		public EndBlock(int index) {super(index);}
		
		public CGenericCFG<V,L> getGraph() {return CGenericCFG.this;}
		
		public Block<V,L> getSelf() {return this;}
	}
	
	protected class FallBlock
			extends GenericCFG<CGenericCFG<V,L>,Block<V,L>,V,L>.FallBlock
			implements Block<V,L> {
		public FallBlock(int index, int child) {super(index, child);}
		
		public CGenericCFG<V,L> getGraph() {return CGenericCFG.this;}
		
		public Block<V,L> getSelf() {return this;}
	}
	
	protected class BranchBlock extends
			GenericCFG<CGenericCFG<V,L>,Block<V,L>,V,L>.BranchBlock
			implements Block<V,L> {		
		public BranchBlock(int index, int trueChild, int falseChild) {
			super(index, trueChild, falseChild);
		}
		
		public CGenericCFG<V,L> getGraph() {return CGenericCFG.this;}
		
		public Block<V,L> getSelf() {return this;}
	}

	public OpAmbassador<L> getOpAmbassador() {return mAmbassador;}
}
