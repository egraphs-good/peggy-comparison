package eqsat.meminfer.peggy.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.Structurizer;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddExistingOpNode;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddNewOpNode;
import eqsat.meminfer.network.peg.DirectPEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGExpressionizer;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.network.peg.axiom.PEGOpMaker;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddLoopOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AddOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;
import util.Action;
import util.HashMultiMap;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.integer.IntCouple;
import util.pair.ArrayPairedList;
import util.pair.PairedList;

public class PeggyAxiomizer<O, P> {
	protected final String mName;
	protected final OpAmbassador<? super O> mAmbassador;
	protected final PeggyExpressionGraph<O,P> mGraph
			= new PeggyExpressionGraph<O,P>();
	protected Set<IntCouple> mDistinct = new HashSet<IntCouple>();
	protected MultiMap<PeggyVertex<O,P>,Integer> mInvariance
			= new HashMultiMap<PeggyVertex<O,P>,Integer>();
	protected final DirectPEGLabelAmbassador<O,P> mLabelAmbassador
			= new DirectPEGLabelAmbassador<O,P>() {
		public boolean mustBeDistinctLoops(Integer left, Integer right) {
			return mDistinct.contains(new IntCouple(left, right));
		}
		public boolean mustBeExtendedDomain(FlowValue<P,O> label) {
			return label == null;
		}
		public boolean mustBeLoopLifted(FlowValue<P,O> op, Integer depth) {
			return false;
		}
	};
	protected final PEGNetwork<O> mPEGNetwork;
	protected final PEGExpressionizer<FlowValue<P,O>,Integer,O,PeggyVertex<O,P>>
			mExpressionizer
			= new PEGExpressionizer<FlowValue<P,O>,Integer,O,PeggyVertex<O,P>>()
			{
		public PEGNetwork<O> getNetwork() {return mPEGNetwork;}
		protected PEGLabelAmbassador<FlowValue<P,O>,Integer,O> getAmbassador() {
			return mLabelAmbassador;
		}

		protected boolean isParameter(PeggyVertex<O,P> vertex) {
			return vertex.getLabel() != null && vertex.getLabel().isParameter();
		}
		protected FlowValue<P,O> getOperator(PeggyVertex<O,P> vertex) {
			if (isParameter(vertex))
				throw new IllegalArgumentException();
			return vertex.getLabel();
		}

		protected boolean mustBeInvariant(PeggyVertex<O,P> vertex,
				Integer depth) {
			return mInvariance.containsEntry(vertex, depth);
		}
	};
	protected final PeggyAxiomNetwork<O> mPeggyAxiomNetwork;
	protected final PEGOpMaker
			<FlowValue<P,O>,Integer,O,AddOpNode<O>,PeggyVertex<O,P>> mMaker
			= new PEGOpMaker
			<FlowValue<P,O>,Integer,O,AddOpNode<O>,PeggyVertex<O,P>>() {
		public PeggyAxiomNetwork<O> getNetwork() {return mPeggyAxiomNetwork;}
		public Structurizer<PeggyVertex<O,P>> getStructurizer() {
			return mExpressionizer;
		}
		protected PEGLabelAmbassador<FlowValue<P,O>,Integer,O> getAmbassador() {
			return mLabelAmbassador;
		}

		protected FlowValue<P,O> getOperator(PeggyVertex<O,P> vertex) {
			if (vertex.getLabel() != null && vertex.getLabel().isParameter())
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
	protected final List<PeggyVertex<O,P>> mCreated = new ArrayList();
	protected final PairedList<PeggyVertex<O,P>,PeggyVertex<O,P>> mEqualities
			= new ArrayPairedList<PeggyVertex<O,P>,PeggyVertex<O,P>>();
	protected final List<PeggyVertex<O,P>> mTruths = new ArrayList();
	protected final List<PeggyVertex<O,P>> mFalsities = new ArrayList();
	
	public PeggyAxiomizer(String name, Network network,
			OpAmbassador<? super O> ambassador) {
		mName = name;
		mPEGNetwork = new PEGNetwork<O>(network);
		mPeggyAxiomNetwork = new PeggyAxiomNetwork<O>(network);
		mAmbassador = ambassador;
	}
	
	public PeggyVertex<O,P> createPlaceHolder() {
		return mGraph.createPlaceHolder();
	}
	
	public PeggyVertex<O,P> getVariable(P variable) {
		return mGraph.getVertex(FlowValue.<P,O>createParameter(variable));
	}
	
	public PeggyVertex<O,P> getAnyArity(O op) {
		PeggyVertex<O,P> vertex = get(op);
		mExpressionizer.allowAnyArity(vertex);
		return vertex;
	}
	public PeggyVertex<O,P> get(O op) {
		if (op == null)
			return mGraph.getVertex(null);
		else
			return mGraph.getVertex(
					FlowValue.<P,O>createDomain(op, mAmbassador));
	}
	public PeggyVertex<O,P> get(O op, PeggyVertex<O,P> child) {
		if (op == null)
			return mGraph.getVertex(null, child);
		else
			return mGraph.getVertex(
					FlowValue.<P,O>createDomain(op, mAmbassador), child);
	}
	public PeggyVertex<O,P> get(O op, PeggyVertex<O,P>... children) {
		if (op == null)
			return mGraph.getVertex(null, children);
		else
			return mGraph.getVertex(
					FlowValue.<P,O>createDomain(op, mAmbassador), children);
	}
	public PeggyVertex<O,P> get(O op,
			List<? extends PeggyVertex<O,P>> children) {
		if (op == null)
			return mGraph.getVertex(null, children);
		else
			return mGraph.getVertex(
					FlowValue.<P,O>createDomain(op, mAmbassador), children);
	}

	public PeggyVertex<O,P> getNegate(PeggyVertex<O,P> child) {
		return mGraph.getVertex(FlowValue.<P,O>createNegate(), child);
	}
	public PeggyVertex<O,P> getEquals(PeggyVertex<O,P> left,
			PeggyVertex<O,P> right) {
		return mGraph.getVertex(FlowValue.<P,O>createEquals(), left, right);
	}
	
	public PeggyVertex<O,P> getPhi(PeggyVertex<O,P> condition,
			PeggyVertex<O,P> trueCase, PeggyVertex<O,P> falseCase) {
		return mGraph.getVertex(FlowValue.<P,O>createPhi(), condition,
				trueCase, falseCase);
	}
	public PeggyVertex<O,P> getOr(
			PeggyVertex<O,P> child1, 
			PeggyVertex<O,P> child2) {
		return mGraph.getVertex(FlowValue.<P,O>createOr(), child1, child2);
	}
	public PeggyVertex<O,P> getAnd(
			PeggyVertex<O,P> child1, 
			PeggyVertex<O,P> child2) {
		return mGraph.getVertex(FlowValue.<P,O>createAnd(), child1, child2);
	}
	public PeggyVertex<O,P> getZero() {
		return mGraph.getVertex(FlowValue.<P,O>createZero());
	}
	public PeggyVertex<O,P> getSuccessor(PeggyVertex<O,P> child) {
		return mGraph.getVertex(FlowValue.<P,O>createSuccessor(), child);
	}
	
	public PeggyVertex<O,P> getTheta(int depth, PeggyVertex<O,P> base,
			PeggyVertex<O,P> shift) {
		return mGraph.getVertex(FlowValue.<P,O>createTheta(depth),
				base, shift);
	}
	public PeggyVertex<O,P> getShift(int depth, PeggyVertex<O,P> child) {
		return mGraph.getVertex(FlowValue.<P,O>createShift(depth), child);
	}
	public PeggyVertex<O,P> getPass(int depth, PeggyVertex<O,P> child) {
		return mGraph.getVertex(FlowValue.<P,O>createPass(depth), child);
	}
	public PeggyVertex<O,P> getEval(int depth, PeggyVertex<O,P> child,
			PeggyVertex<O,P> index) {
		return mGraph.getVertex(FlowValue.<P,O>createEval(depth),
				child, index);
	}
	
	public void mustExist(PeggyVertex<O,P> expression) {
		expression.setTag(mExistsTag);
	}
	public void mustBeTrue(PeggyVertex<O,P> expression) {
		mustExist(expression);
		mExpressionizer.makeKnown(expression);
	}
	public void mustBeFalse(PeggyVertex<O,P> expression) {
		mustExist(expression);
		mExpressionizer.makeFalse(expression);
	}
	
	public void mustBeDistinctLoops(int left, int right) {
		if (left == right)
			throw new IllegalArgumentException();
		mDistinct.add(new IntCouple(left, right));
	}
	public void mustBeInvariant(int depth, PeggyVertex<O,P> expression) {
		mInvariance.addValue(expression, depth);
	}
	
	public void create(PeggyVertex<O,P> expression) {
		mCreated.add(expression);
	}
	public void makeEqual(PeggyVertex<O,P> left, PeggyVertex<O,P> right) {
		create(left);
		create(right);
		mEqualities.add(left, right);
	}
	public void makeTrue(PeggyVertex<O,P> expression) {
		create(expression);
		mTruths.add(expression);
	}
	public void makeFalse(PeggyVertex<O,P> expression) {
		create(expression);
		mFalsities.add(expression);
	}
	
	public PEGNode<O> getTrigger() {
		Action<PeggyVertex<O,P>> makeExists = new Action<PeggyVertex<O,P>>() {
			final Tag<Void> mTag = new NamedTag<Void>("Processed");
			public void execute(PeggyVertex<O,P> parameter) {
				if (parameter.hasTag(mTag))
					return;
				parameter.setTag(mTag);
				if (parameter.hasTag(mExistsTag))
					mExpressionizer.addExpression(parameter);
				for (PeggyVertex<O,P> child : parameter.getChildren())
					execute(child);
			}
		};
		for (PeggyVertex<O,P> root : mGraph.getRoots())
			makeExists.execute(root);
		for (PeggyVertex<O,P> vertex : mGraph.getVertices())
			makeExists.execute(vertex);
		return mExpressionizer.getPEGExpression();
	}
	public Structurizer<PeggyVertex<O,P>> getStructurizer() {
		return mExpressionizer;
	}
	
	public AxiomNode<O,? extends PEGNode<O>> getAxiom() {
		PEGNode<O> pegNode = getTrigger();
		for (PeggyVertex<O,P> expression : mCreated)
			mMaker.addVertex(expression);
		for (PeggyVertex<O,P> truth : mTruths)
			mMaker.makeInferred(truth);
		for (PeggyVertex<O,P> falsity : mFalsities)
			mMaker.makeInconsistent(falsity);
		for (int i = 0; i < mEqualities.size(); i++)
			mMaker.makeEqual(mEqualities.getFirst(i), mEqualities.getSecond(i));
		return mPeggyAxiomNetwork.<PEGNode<O>>axiom(mName, pegNode,
				mMaker.getAddOps(), mMaker.getPlaceHolders(),
				mMaker.getConstructs(), mMaker.getMerges());
	}
}
