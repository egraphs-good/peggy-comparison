package peggy.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.Action;
import util.DisjointUnion;
import util.HashMultiMap;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.integer.IntCouple;
import util.pair.ArrayPairedList;
import util.pair.PairedList;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.Structurizer;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddExistingOpNode;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddNewOpNode;
import eqsat.meminfer.network.peg.PEGExpressionizer;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddLoopOpNode;
import eqsat.meminfer.network.peg.axiom.PEGOpMaker;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AddOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This is a re-implementation of the PeggyAxiomizer. It allows more than one 
 * type of unlabelled domain node in the same axiom. The different label types
 * are specified by integers. 
 */
public class WildcardPeggyAxiomizer<O,P> {
	public static class AxiomValue<O,P> {
		private final DisjointUnion<FlowValue<P,O>,Integer> value;
		public AxiomValue(FlowValue<P,O> flow) {
			this.value = DisjointUnion.<FlowValue<P,O>, Integer>injectLeft(flow);
		}
		public AxiomValue(Integer wildcard) {
			this.value = DisjointUnion.<FlowValue<P,O>, Integer>injectRight(wildcard);
		}
		public boolean isFlow() {return this.value.isLeft();}
		public boolean isWildcard() {return this.value.isRight();}
		public FlowValue<P,O> getFlowValue() {return this.value.getLeft();}
		public Integer getWildcard() {return this.value.getRight();}
		public boolean equals(Object o) {
			if (!(o instanceof WildcardPeggyAxiomizer.AxiomValue))
				return false;
			AxiomValue<O,P> av = (AxiomValue<O,P>)o;
			return av.value.equals(this.value);
		}
		public int hashCode() {
			return this.value.hashCode();
		}
		public String toString() {
			if (this.value.isLeft()) 
				return this.value.getLeft().toString();
			else
				return "Wildcard " + this.value.getRight().toString();
		}
	}
	
	protected final String mName;
	protected final OpAmbassador<? super O> mAmbassador;
	protected final CRecursiveExpressionGraph<AxiomValue<O,P>> mGraph
		= new CRecursiveExpressionGraph<AxiomValue<O,P>>();
	protected Set<IntCouple> mDistinct = new HashSet<IntCouple>();
	protected MultiMap<Vertex<AxiomValue<O,P>>,Integer> mInvariance
		= new HashMultiMap<Vertex<AxiomValue<O,P>>,Integer>();
	protected final PEGLabelAmbassador<AxiomValue<O,P>,Integer,O> mLabelAmbassador =
		new PEGLabelAmbassador<AxiomValue<O,P>,Integer,O>() {
		public boolean isExtendedDomain(AxiomValue<O,P> label) {
			return label.isFlow() && label.getFlowValue().isExtendedDomain();
		}

		public FlowValue<?,O> getExtendedDomain(AxiomValue<O,P> label) {
			if (isExtendedDomain(label))
				return label.getFlowValue();
			else
				throw new IllegalArgumentException();
		}

		public boolean isPhi(AxiomValue<O,P> label) {
			return label.isFlow() && label.getFlowValue().isPhi();
		}

		public boolean isZero(AxiomValue<O,P> label) {
			return label.isFlow() && label.getFlowValue().isZero();
		}

		public boolean isSuccessor(AxiomValue<O,P> label) {
			return label.isFlow() && label.getFlowValue().isSuccessor();
		}

		public boolean isLoopOp(AxiomValue<O,P> label) {
			return label.isFlow() && label.getFlowValue().isLoopFunction();
		}

		public PEGLoopOp getLoopOp(AxiomValue<O,P> label) {
			return PEGLoopOp.getLoopOp(label.getFlowValue());
		}

		public Integer getLoopDepth(AxiomValue<O,P> label) {
			return label.getFlowValue().getLoopDepth();
		}

		public boolean mustBeDistinctLoops(Integer left, Integer right) {
			return mDistinct.contains(new IntCouple(left, right));
		}
		public boolean mustBeExtendedDomain(AxiomValue<O,P> label) {
			return label.isWildcard();
		}
		public boolean mustBeLoopLifted(AxiomValue<O,P> op, Integer depth) {
			return false;
		}
	};
	
	protected final PEGNetwork<O> mPEGNetwork;
	protected final PEGExpressionizer<AxiomValue<O,P>,Integer,O,Vertex<AxiomValue<O,P>>>
	mExpressionizer
		= new PEGExpressionizer<AxiomValue<O,P>,Integer,O,Vertex<AxiomValue<O,P>>>()
	{
		public PEGNetwork<O> getNetwork() {return mPEGNetwork;}
		protected PEGLabelAmbassador<AxiomValue<O,P>,Integer,O> getAmbassador() {
			return mLabelAmbassador;
		}

		protected boolean isParameter(Vertex<AxiomValue<O,P>> vertex) {
			return vertex.getLabel().isFlow() && vertex.getLabel().getFlowValue().isParameter();
		}
		protected AxiomValue<O,P> getOperator(Vertex<AxiomValue<O,P>> vertex) {
			if (isParameter(vertex))
				throw new IllegalArgumentException();
			return vertex.getLabel();
		}

		protected boolean mustBeInvariant(
				Vertex<AxiomValue<O,P>> vertex,
				Integer depth) {
			return mInvariance.containsEntry(vertex, depth);
		}
	};
	protected final PeggyAxiomNetwork<O> mPeggyAxiomNetwork;
	protected final PEGOpMaker
	<AxiomValue<O,P>,Integer,O,AddOpNode<O>,Vertex<AxiomValue<O,P>>> mMaker
	= new PEGOpMaker<AxiomValue<O,P>,Integer,O,AddOpNode<O>,Vertex<AxiomValue<O,P>>>() {
		public PeggyAxiomNetwork<O> getNetwork() {return mPeggyAxiomNetwork;}
		public Structurizer<Vertex<AxiomValue<O,P>>> getStructurizer() {
			return mExpressionizer;
		}
		protected PEGLabelAmbassador<AxiomValue<O,P>,Integer,O> getAmbassador() {
			return mLabelAmbassador;
		}

		protected AxiomValue<O,P> getOperator(Vertex<AxiomValue<O,P>> vertex) {
			if (vertex.getLabel().isFlow() && vertex.getLabel().getFlowValue().isParameter())
				throw new IllegalArgumentException(
						"All parameters must be in the trigger");
			return vertex.getLabel();
		}

		protected AddOpNode<O> convertAddLoopOpNode(AddLoopOpNode node) {
			return getNetwork().adaptAddLoopOp(node);
		}
		protected AddOpNode<O> convertAddExistingOpNode(
				AddExistingOpNode node) {
			return getNetwork().adaptAddExistingOp(node);
		}
		protected AddOpNode<O> convertAddNewOpNode(
				AddNewOpNode<FlowValue<?,O>> node) {
			return getNetwork().adaptAddNewOp(node);
		}
	};
	protected final Tag<Void> mExistsTag = new NamedTag<Void>("Must Exist");
	protected final List<Vertex<AxiomValue<O,P>>> mCreated = new ArrayList();
	protected final PairedList<Vertex<AxiomValue<O,P>>,Vertex<AxiomValue<O,P>>> mEqualities
		= new ArrayPairedList<Vertex<AxiomValue<O,P>>,Vertex<AxiomValue<O,P>>>();
	protected final List<Vertex<AxiomValue<O,P>>> mTruths = new ArrayList();
	protected final List<Vertex<AxiomValue<O,P>>> mFalsities = new ArrayList();

	public WildcardPeggyAxiomizer(
			String name, 
			Network network,
			OpAmbassador<? super O> ambassador) {
		mName = name;
		mPEGNetwork = new PEGNetwork<O>(network);
		mPeggyAxiomNetwork = new PeggyAxiomNetwork<O>(network);
		mAmbassador = ambassador;
	}

	public Vertex<AxiomValue<O,P>> createPlaceHolder() {
		return mGraph.createPlaceHolder();
	}

	public Vertex<AxiomValue<O,P>> getVariable(P variable) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createParameter(variable)));
	}

	public Vertex<AxiomValue<O,P>> getAnyArity(O op) {
		Vertex<AxiomValue<O,P>> vertex = get(op);
		mExpressionizer.allowAnyArity(vertex);
		return vertex;
	}
	public Vertex<AxiomValue<O,P>> get(O op) {
		return mGraph.getVertex(
				new AxiomValue<O,P>(FlowValue.<P,O>createDomain(op, mAmbassador)));
	}
	public Vertex<AxiomValue<O,P>> get(O op, Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(
				new AxiomValue<O,P>(FlowValue.<P,O>createDomain(op, mAmbassador)), child);
	}
	public Vertex<AxiomValue<O,P>> get(O op, Vertex<AxiomValue<O,P>>... children) {
		return mGraph.getVertex(
				new AxiomValue<O,P>(FlowValue.<P,O>createDomain(op, mAmbassador)), children);
	}
	public Vertex<AxiomValue<O,P>> get(O op,
			List<? extends Vertex<AxiomValue<O,P>>> children) {
		return mGraph.getVertex(
				new AxiomValue<O,P>(FlowValue.<P,O>createDomain(op, mAmbassador)), children);
	}

	public Vertex<AxiomValue<O,P>> getAnyArity(Integer wildcard) {
		Vertex<AxiomValue<O,P>> vertex = get(wildcard);
		mExpressionizer.allowAnyArity(vertex);
		return vertex;
	}
	public Vertex<AxiomValue<O,P>> get(Integer wildcard) {
		return mGraph.getVertex(new AxiomValue<O,P>(wildcard));
	}
	public Vertex<AxiomValue<O,P>> get(Integer wildcard, Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(new AxiomValue<O,P>(wildcard), child);
	}
	public Vertex<AxiomValue<O,P>> get(Integer wildcard, Vertex<AxiomValue<O,P>>... children) {
		return mGraph.getVertex(new AxiomValue<O,P>(wildcard), children);
	}
	public Vertex<AxiomValue<O,P>> get(
			Integer wildcard,
			List<? extends Vertex<AxiomValue<O,P>>> children) {
		return mGraph.getVertex(new AxiomValue<O,P>(wildcard), children);
	}
	
	public Vertex<AxiomValue<O,P>> getNegate(Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createNegate()), child);
	}
	public Vertex<AxiomValue<O,P>> getEquals(
			Vertex<AxiomValue<O,P>> left,
			Vertex<AxiomValue<O,P>> right) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createEquals()), left, right);
	}

	public Vertex<AxiomValue<O,P>> getPhi(
			Vertex<AxiomValue<O,P>> condition,
			Vertex<AxiomValue<O,P>> trueCase, 
			Vertex<AxiomValue<O,P>> falseCase) {
		return mGraph.getVertex(
				new AxiomValue<O,P>(FlowValue.<P,O>createPhi()), 
				condition,
				trueCase, 
				falseCase);
	}
	public Vertex<AxiomValue<O,P>> getOr(
			Vertex<AxiomValue<O,P>> child1, 
			Vertex<AxiomValue<O,P>> child2) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createOr()), child1, child2);
	}
	public Vertex<AxiomValue<O,P>> getAnd(
			Vertex<AxiomValue<O,P>> child1, 
			Vertex<AxiomValue<O,P>> child2) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createAnd()), child1, child2);
	}
	public Vertex<AxiomValue<O,P>> getZero() {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createZero()));
	}
	public Vertex<AxiomValue<O,P>> getSuccessor(Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createSuccessor()), child);
	}

	public Vertex<AxiomValue<O,P>> getTheta(
			int depth, 
			Vertex<AxiomValue<O,P>> base,
			Vertex<AxiomValue<O,P>> shift) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createTheta(depth)),
				base, shift);
	}
	public Vertex<AxiomValue<O,P>> getShift(int depth, Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createShift(depth)), child);
	}
	public Vertex<AxiomValue<O,P>> getPass(int depth, Vertex<AxiomValue<O,P>> child) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createPass(depth)), child);
	}
	public Vertex<AxiomValue<O,P>> getEval(
			int depth, 
			Vertex<AxiomValue<O,P>> child,
			Vertex<AxiomValue<O,P>> index) {
		return mGraph.getVertex(new AxiomValue<O,P>(FlowValue.<P,O>createEval(depth)),
				child, index);
	}

	public void mustExist(Vertex<AxiomValue<O,P>> expression) {
		expression.setTag(mExistsTag);
	}
	public void mustBeTrue(Vertex<AxiomValue<O,P>> expression) {
		mustExist(expression);
		mExpressionizer.makeKnown(expression);
	}
	public void mustBeFalse(Vertex<AxiomValue<O,P>> expression) {
		mustExist(expression);
		mExpressionizer.makeFalse(expression);
	}

	public void mustBeDistinctLoops(int left, int right) {
		if (left == right)
			throw new IllegalArgumentException();
		mDistinct.add(new IntCouple(left, right));
	}
	public void mustBeInvariant(int depth, Vertex<AxiomValue<O,P>> expression) {
		mInvariance.addValue(expression, depth);
	}

	public void create(Vertex<AxiomValue<O,P>> expression) {
		mCreated.add(expression);
	}
	public void makeEqual(Vertex<AxiomValue<O,P>> left, Vertex<AxiomValue<O,P>> right) {
		create(left);
		create(right);
		mEqualities.add(left, right);
	}
	public void makeTrue(Vertex<AxiomValue<O,P>> expression) {
		create(expression);
		mTruths.add(expression);
	}
	public void makeFalse(Vertex<AxiomValue<O,P>> expression) {
		create(expression);
		mFalsities.add(expression);
	}

	public PEGNode<O> getTrigger() {
		Action<Vertex<AxiomValue<O,P>>> makeExists = new Action<Vertex<AxiomValue<O,P>>>() {
			final Tag<Void> mTag = new NamedTag<Void>("Processed");
			public void execute(Vertex<AxiomValue<O,P>> parameter) {
				if (parameter.hasTag(mTag))
					return;
				parameter.setTag(mTag);
				if (parameter.hasTag(mExistsTag))
					mExpressionizer.addExpression(parameter);
				for (Vertex<AxiomValue<O,P>> child : parameter.getChildren())
					execute(child);
			}
		};
		for (Vertex<AxiomValue<O,P>> root : mGraph.getRoots())
			makeExists.execute(root);
		for (Vertex<AxiomValue<O,P>> vertex : mGraph.getVertices())
			makeExists.execute(vertex);
		return mExpressionizer.getPEGExpression();
	}
	public Structurizer<Vertex<AxiomValue<O,P>>> getStructurizer() {
		return mExpressionizer;
	}

	public AxiomNode<O,? extends PEGNode<O>> getAxiom() {
		PEGNode<O> pegNode = getTrigger();
		for (Vertex<AxiomValue<O,P>> expression : mCreated)
			mMaker.addVertex(expression);
		for (Vertex<AxiomValue<O,P>> truth : mTruths)
			mMaker.makeInferred(truth);
		for (Vertex<AxiomValue<O,P>> falsity : mFalsities)
			mMaker.makeInconsistent(falsity);
		for (int i = 0; i < mEqualities.size(); i++)
			mMaker.makeEqual(mEqualities.getFirst(i), mEqualities.getSecond(i));
		return mPeggyAxiomNetwork.<PEGNode<O>>axiom(mName, pegNode,
				mMaker.getAddOps(), mMaker.getPlaceHolders(),
				mMaker.getConstructs(), mMaker.getMerges());
	}
}
