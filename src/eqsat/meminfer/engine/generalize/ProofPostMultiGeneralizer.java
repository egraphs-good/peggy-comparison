package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG.ENode;
import eqsat.meminfer.engine.generalize.PostMultiGenPEG.Node;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.engine.proof.AreEquivalent;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.ChildIsInvariant;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.IsInvariant;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsAllLoopLifted;
import eqsat.meminfer.engine.proof.OpIsDifferentLoop;
import eqsat.meminfer.engine.proof.OpIsLoopLifted;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpIsSameLoop;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.engine.proof.ProofManager;
import eqsat.meminfer.engine.proof.Property;
import util.PriorityHeap;
import util.UnhandledCaseException;
import util.pair.ArrayPairedList;
import util.pair.Couple;
import util.pair.DoublePairedList;
import util.pair.Pair;
import util.pair.PairedList;

public class ProofPostMultiGeneralizer
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	private static final class ProofLink
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
		private static final Comparator<? super ProofLink> mPrioritizer
				= new Comparator<ProofLink>() {
			public int compare(ProofLink first, ProofLink second) {
				return second.mLength - first.mLength;
			}
		};
		private static final ProofLink mNil = new ProofLink();
		
		protected final Node<O,T,V> mRepresentative;
		protected final
				Pair<Proof,ProofPostMultiGeneralizer<O,T,V>.Context> mProof;
		protected final ProofLink mTail;
		private final int mLength;
		
		private ProofLink() {
			mRepresentative = null;
			mProof = null;
			mTail = null;
			mLength = 0;
		}
		protected ProofLink(Node<O,T,V> representative,
				Pair<Proof,ProofPostMultiGeneralizer<O,T,V>.Context> proof,
				ProofLink tail) {
			mRepresentative = representative;
			mProof = proof;
			mTail = tail;
			mLength = mTail.mLength + 1;
		}
		
		protected int size() {return mLength;}
		
		protected void addToVertices(
				Collection<? super Node<O,T,V>> vertices) {
			if (mTail == null)
				return;
			mTail.addToVertices(vertices);
			vertices.add(mRepresentative);
		}
		
		protected void addToProofs(Collection<? super
				Pair<Proof,ProofPostMultiGeneralizer<O,T,V>.Context>> proofs) {
			if (mTail == null)
				return;
			mTail.addToProofs(proofs);
			proofs.add(mProof);
		}
		
		public String toString() {
			if (mTail == null)
				return "[]";
			else
				return mTail + ":(" + mRepresentative + "," + mProof + ")";
		}

		protected static final
				<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
				Comparator<? super ProofLink<O,T,V>> prioritizer() {
			return mPrioritizer;
		}
		protected static final
				<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
				ProofLink<O,T,V> nil() {
			return mNil;
		}
	}
	
	private class Context extends HashMap<TermOrTermChild<T,V>,Node<O,T,V>> {}
	
	private final OpAmbassador<O> mAmbassador;
	private final ProofManager<T,V> mProofs;
	private final PostMultiGenPEG<O,T,V> mPEG;
	private final Node<O,T,V> mTrigger, mResult;
	private final List<PostMultiGenEPEG<O,T,V>> mEPEGs;
	private final List<Pair<ENode<O,T,V>,ENode<O,T,V>>> mTriggers;
	private final Stack<Couple<Node<O,T,V>>> mEqualities = new Stack();
	private final PairedList<Couple<Node<O,T,V>>,Pair<Proof,Context>>
			mLiftedProofs = new ArrayPairedList();

	public ProofPostMultiGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs,
			TermOrTermChild<T,V> trigger, TermOrTermChild<T,V> result,
			boolean decompose) {
		mAmbassador = ambassador;
		mProofs = proofs;
		mPEG = new PostMultiGenPEG<O,T,V>(ambassador);
		mTrigger = mPEG.createMarkedNode(trigger, true, false);
		mResult = mPEG.createMarkedNode(result, false, true);
		if (trigger.equals(result))
			mTrigger.unifyWith(mResult);
		else {
			mEqualities.add(new Couple<Node<O,T,V>>(mTrigger, mResult));
			while (!mEqualities.isEmpty()) {
				Couple<Node<O,T,V>> equality = mEqualities.pop();
				generalizeEquality(equality.getLeft(), equality.getRight());
			}
			mPEG.simplify();
		}
		mEPEGs = decompose ? new ArrayList() : null;
		mTriggers = decompose ? new ArrayList() : null;
		if (!decompose || trigger.equals(result)) {
			mPEG.clearAnchors();
			return;
		}
		mEqualities.add(new Couple<Node<O,T,V>>(mTrigger, mResult));
		while (!mEqualities.isEmpty()) {
			Couple<Node<O,T,V>> equality = mEqualities.pop();
			PostMultiGenEPEG<O,T,V> epeg
					= new PostMultiGenEPEG<O,T,V>(mAmbassador);
			Stack<Couple<ENode<O,T,V>>> equalities = new Stack();
			List<Couple<ENode<O,T,V>>> assumptions = new ArrayList();
			ENode<O,T,V> leftNode = epeg.createMarkedNode(equality.getLeft(),
					equality.getLeft().isTrigger()
							|| !equality.getRight().isTrigger(),
					equality.getLeft().isResult());
			ENode<O,T,V> rightNode = epeg.createMarkedNode(equality.getRight(),
					equality.getRight().isTrigger()
							|| !equality.getLeft().isTrigger(),
					equality.getRight().isResult());
			equalities.add(new Couple<ENode<O,T,V>>(leftNode, rightNode));
			while (!equalities.isEmpty()) {
				Couple<ENode<O,T,V>> eequality = equalities.pop();
				generalizeEquality(epeg, equalities, assumptions,
						eequality.getLeft(), eequality.getRight());
			}
			for (Couple<ENode<O,T,V>> assumption : assumptions)
				if (!assumption.getLeft().hasArity()
						|| !assumption.getRight().hasArity())
					assumption.getLeft().unifyWith(assumption.getRight());
			epeg.simplify();
			for (Couple<ENode<O,T,V>> assumption : assumptions) {
				if (!assumption.getLeft().equals(assumption.getRight()))
					epeg.addEquality(assumption.getLeft(),
							assumption.getRight());
				mEqualities.add(new Couple<Node<O,T,V>>(
						assumption.getLeft().getAnchor(),
						assumption.getRight().getAnchor()));
			}
			if (!leftNode.equals(rightNode)) {
				mEPEGs.add(epeg);
				if (leftNode.isTrigger())
					mTriggers.add(new Pair<ENode<O,T,V>,ENode<O,T,V>>(
							leftNode, rightNode));
				else
					mTriggers.add(new Pair<ENode<O,T,V>,ENode<O,T,V>>(
							rightNode, leftNode));
			}
		}
		mPEG.clearAnchors();
		for (PostMultiGenEPEG<O,T,V> epeg : mEPEGs)
			epeg.clearAnchors();
	}

	public PostMultiGenPEG<O,T,V> getPEG() {return mPEG;}
	public List<? extends PostMultiGenEPEG<O,T,V>> getEPEGs() {return mEPEGs;}
	public PairedList<? extends PostMultiGenEPEG<O,T,V>,
			Pair<ENode<O,T,V>,ENode<O,T,V>>> getEPEGsWithTriggers() {
		return new DoublePairedList<PostMultiGenEPEG<O,T,V>,
				Pair<ENode<O,T,V>,ENode<O,T,V>>>(mEPEGs, mTriggers);
	}
	
	private void generalizeEquality(Node<O,T,V> left, Node<O,T,V> right) {
		PairedList<TermOrTermChild<T,V>,Proof> path
				= mProofs.getProofPath(left.getAnchor(), right.getAnchor());
		for (int i = 0; i < path.size() - 2; )
			if (path.getSecond(i) == null && path.getSecond(i + 1) == null)
				path.removeAt(i + 1);
			else
				i++;
		Node<O,T,V>[] anchors = new Node[path.size()];
		anchors[0] = left;
		anchors[anchors.length - 1] = right;
		for (int i = 1; i < anchors.length - 1; i++)
			anchors[i] = mPEG.createNode(path.getFirst(i));
		for (int i = 0; i < path.size() - 1; i++)
			if (path.getSecond(i) == null)
				anchors[i].unifyWith(anchors[i + 1]);
			else
				generalizeEquality(anchors[i], anchors[i + 1],
						path.getSecond(i));
	}

	private void generalizeEquality(Node<O,T,V> left, Node<O,T,V> right,
			Proof proof) {
		Context context = new Context();
		context.put(left.getAnchor(), left);
		context.put(right.getAnchor(), right);
		generalizeProof(proof, context);
		mLiftedProofs.add(new Couple<Node<O,T,V>>(left, right),
				new Pair<Proof,Context>(proof, context));
	}
	
	private void generalizeProof(Proof proof, Context context) {
		generalizeArities(proof, context);
		generalizeOps(proof, context);
		generalizeLoops(proof, context);
		generalizeInvariance(proof, context);
		generalizeEqualities(proof, context);
	}
	
	private void generalizeArities(Proof proof, Context context) {
		for (Property property : proof.getProperties())
			if (property instanceof ArityIs) {
				ArityIs<T> arityIs = (ArityIs<T>)property;
				setArity(arityIs.getTerm(), arityIs.getArity(), context);
			}
	}
	
	private void setArity(T term, int arity, Context context) {
		Node<O,T,V> node;
		if (!context.containsKey(term))
			context.put(term, node = mPEG.createNode(term));
		else
			node = context.get(term);
		node.setArity(arity);
		for (int i = 0; i < arity; i++) {
			Node<O,T,V> child = context.get(new TermChild<T,V>(term, i));
			if (child != null)
				node.getChild(i).unifyWith(child);
		}
	}
	
	private void generalizeOps(Proof proof, Context context) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIs) {
				OpIs<FlowValue<?,O>,T> opIs = (OpIs)property; 
				Node<O,T,V> node = context.get(opIs.getTerm());
				FlowValue<?,O> op = opIs.getOp();
				if (op.isPhi())
					node.getOp().setPhi();
				else if (op.isZero())
					node.getOp().setZero();
				else if (op.isSuccessor())
					node.getOp().setSuccessor();
				else if (op.isExtendedDomain())
					node.getOp().setExtendedDomainOp(op.getDomain(mAmbassador));
				else if (op.isLoopFunction())
					throw new IllegalArgumentException();
				else
					throw new UnhandledCaseException();
			} else if (property instanceof OpIsLoopOp) {
				OpIsLoopOp<T> opIsLoopOp = (OpIsLoopOp)property;
				context.get(opIsLoopOp.getTerm()).getOp().setLoopOp(
						opIsLoopOp.getOp());
			} else if (property instanceof OpsEqual) {
				OpsEqual<FlowValue<?,O>,T> opsEqual = (OpsEqual)property;
				context.get(opsEqual.getLeft()).getOp().unifyWith(
						context.get(opsEqual.getRight()).getOp());
			}
	}
	
	private void generalizeLoops(Proof proof, Context context) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIsSameLoop) {
				OpIsSameLoop<T> opIsSameLoop = (OpIsSameLoop)property;
				context.get(opIsSameLoop.getLeft()).getOp().getLoopDepth()
						.unifyWith(context.get(opIsSameLoop.getRight()).getOp()
								.getLoopDepth());
			} else if (property instanceof OpIsDifferentLoop) {
				OpIsDifferentLoop<T> opIsDifferentLoop
						= (OpIsDifferentLoop)property;
				context.get(opIsDifferentLoop.getLeft()).getOp().getLoopDepth()
						.setDistinctFrom(context.get(opIsDifferentLoop
								.getRight()).getOp().getLoopDepth());
			} else if (property instanceof OpIsAllLoopLifted)
				context.get(((OpIsAllLoopLifted<T>)property).getTerm())
						.getOp().setAllLoopLifted();
			else if (property instanceof OpIsLoopLifted) {
				OpIsLoopLifted<T> opIsLoopLifted = (OpIsLoopLifted)property;
				context.get(opIsLoopLifted.getTerm()).getOp().setLoopLifted(
						context.get(opIsLoopLifted.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeInvariance(Proof proof, Context context) {
		for (Property property : proof.getProperties())
			if (property instanceof IsInvariant) {
				IsInvariant<T,V> isInvariant = (IsInvariant)property;
				context.get(isInvariant.getTerm()).setInvariant(
						context.get(isInvariant.getLoop())
								.getOp().getLoopDepth());
			} else if (property instanceof ChildIsInvariant) {
				ChildIsInvariant<T,V> childIsInvariant
						= (ChildIsInvariant)property;
				context.get(childIsInvariant.getTerm())
						.getChild(childIsInvariant.getChild())
						.setInvariant(context.get(childIsInvariant.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeEqualities(Proof proof, Context context) {
		for (Property property : proof.getProperties())
			if (property instanceof AreEquivalent) {
				AreEquivalent<T,V> areEquivalent = (AreEquivalent)property;
				addEquality(context.get(areEquivalent.getLeft()),
						context.get(areEquivalent.getRight()));
			} else if (property instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> childIsEquivalentTo
						= (ChildIsEquivalentTo)property;
				addEquality(context.get(childIsEquivalentTo.getParentTerm())
								.getChild(childIsEquivalentTo.getChild()),
						context.get(childIsEquivalentTo.getTerm()));
			} else if (property instanceof EquivalentChildren) {
				EquivalentChildren<T,V> equivalentChildren
						= (EquivalentChildren)property;
				addEquality(context.get(equivalentChildren.getLeftTerm())
								.getChild(equivalentChildren.getLeftChild()),
						context.get(equivalentChildren.getRightTerm())
								.getChild(equivalentChildren.getRightChild()));
			}
	}
	
	private void addEquality(Node<O,T,V> left, Node<O,T,V> right) {
		if (left.getAnchor().asTerm().equals(right.getAnchor().asTerm()))
			left.unifyWith(right);
		else
			mEqualities.add(new Couple<Node<O,T,V>>(left, right));
	}
	
	private Iterator<Pair<Node<O,T,V>,Pair<Proof,Context>>>
			getNeighbors(final Node<O,T,V> vertex) {
		List<Pair<Node<O,T,V>,Pair<Proof,Context>>> neighbors
				= new ArrayList();
		for (int i = 0; i < mLiftedProofs.size(); i++)
			if (mLiftedProofs.getFirst(i).contains(vertex))
				neighbors.add(
						new Pair<Node<O,T,V>,Pair<Proof,Context>>(
						mLiftedProofs.getFirst(i).getLeft().equals(vertex)
								? mLiftedProofs.getFirst(i).getRight()
								: mLiftedProofs.getFirst(i).getLeft(),
						mLiftedProofs.getSecond(i)));
		return neighbors.iterator();
	}

	private PairedList<Node<O,T,V>,Pair<Proof,Context>> getProofPath(
			Node<O,T,V> from, Node<O,T,V> to) {
		Set<Node<O,T,V>> visited = new HashSet();
		PriorityHeap<Node<O,T,V>,ProofLink<O,T,V>> fringe
				= new PriorityHeap(ProofLink.<O,T,V>prioritizer());
		fringe.improvePriority(from, ProofLink.<O,T,V>nil());
		ProofLink<O,T,V> path = null;
		while (!fringe.isEmpty()) {
			path = fringe.peekPriority();
			Node<O,T,V> rep = fringe.pop();
			if (rep.equals(to))
				break;
			visited.add(rep);
			for (Iterator<Pair<Node<O,T,V>,Pair<Proof,Context>>>
					neighbors = getNeighbors(rep); neighbors.hasNext(); ) {
				Pair<Node<O,T,V>,Pair<Proof,Context>> link = neighbors.next();
				Node<O,T,V> neighbor = link.getFirst();
				if (!visited.contains(neighbor))
					fringe.improvePriority(neighbor, new ProofLink<O,T,V>(
							neighbor, link.getSecond(), path));
			}
		}
		List<Node<O,T,V>> vertices = new ArrayList(path.size() + 1);
		vertices.add(from);
		path.addToVertices(vertices);
		List<Pair<Proof,Context>> proofs = new ArrayList(path.size() + 1);
		path.addToProofs(proofs);
		proofs.add(null);
		int assume = -1;
		for (int i = 0; i < vertices.size(); i++)
			if (vertices.get(i).isReachable()) {
				if (assume != -1 && !(assume == 0 && i == vertices.size() - 1)){
					for (int j = assume; j < i; j++)
						proofs.set(j, null);
				}
				assume = i;
			}
		return new DoublePairedList<Node<O,T,V>,Pair<Proof,Context>>(
				vertices, proofs);
	}
	
	private void generalizeEquality(PostMultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<ENode<O,T,V>>> equalities,
			Collection<? super Couple<ENode<O,T,V>>> assumptions,
			ENode<O,T,V> left, ENode<O,T,V> right) {
		PairedList<Node<O,T,V>,Pair<Proof,Context>> path
				= getProofPath(left.getAnchor(), right.getAnchor());
		Pair<Proof,Context>[] proofs = new Pair[path.size() - 1];
		for (int i = 0; i < proofs.length; i++)
			proofs[i] = path.getSecond(i);
		ENode<O,T,V>[] anchors = new ENode[path.size()];
		anchors[0] = left;
		anchors[anchors.length - 1] = right;
		int assume = -1;
		for (int i = 0; i < anchors.length; i++)
			if (path.getFirst(i).isReachable()) {
				if (anchors[i] == null)
					anchors[i] = epeg.createNode(path.getFirst(i));
				if (assume != -1 && !(assume == 0 && i == anchors.length - 1)) {
					for (int j = assume; j < i; j++) {
						proofs[j] = null;
						anchors[j] = anchors[assume];
					}
					assumptions.add(new Couple<ENode<O,T,V>>(
							anchors[assume], anchors[i]));
				}
				assume = i;
			}
		for (int i = 0; i < anchors.length; i++)
			if (anchors[i] == null)
				anchors[i] = epeg.createNode(path.getFirst(i));
		for (int i = 0; i < path.size() - 1; i++)
			if (proofs[i] == null)
				anchors[i].unifyWith(anchors[i+1]);
			else
				generalizeEquality(epeg, equalities, assumptions,
						anchors[i], anchors[i + 1],
						proofs[i].getFirst(), proofs[i].getSecond());
	}

	private void generalizeEquality(PostMultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<ENode<O,T,V>>> equalities,
			Collection<? super Couple<ENode<O,T,V>>> assumptions,
			ENode<O,T,V> left, ENode<O,T,V> right, Proof proof,
			Context lift) {
		Map<TermOrTermChild<T,V>,ENode<O,T,V>> context = new HashMap();
		boolean leftDone = false, rightDone = false;
		for (Entry<TermOrTermChild<T,V>,Node<O,T,V>> entry : lift.entrySet())
			if (entry.getKey().isTermChild()) {
				if (!leftDone && entry.getValue().equals(left.getAnchor())) {
					leftDone = true;
					context.put(entry.getKey(), left);
				} else if (!rightDone
						&& entry.getValue().equals(right.getAnchor())) {
					rightDone = true;
					context.put(entry.getKey(), right);
				}
			}
		if (!leftDone)
			context.put(left.getAnchor().getTermAnchor(), left);
		if (!rightDone)
			context.put(right.getAnchor().getTermAnchor(), right);
		generalizeProof(epeg, equalities, assumptions, proof, lift, context);
	}
	
	private void generalizeProof(PostMultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<ENode<O,T,V>>> equalities,
			Collection<? super Couple<ENode<O,T,V>>> assumptions, Proof proof,
			Context lift, Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		generalizeArities(epeg, proof, lift, context);
		generalizeOps(proof, context);
		generalizeLoops(proof, context);
		generalizeInvariance(proof, context);
		generalizeEqualities(equalities, assumptions, proof, context);
	}
	
	private void generalizeArities(PostMultiGenEPEG<O,T,V> epeg, Proof proof,
			Context lift, Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		for (Property property : proof.getProperties())
			if (property instanceof ArityIs) {
				ArityIs<T> arityIs = (ArityIs<T>)property;
				setArity(epeg, arityIs.getTerm(), arityIs.getArity(),
						lift, context);
			}
	}
	
	private void setArity(PostMultiGenEPEG<O,T,V> epeg, T term, int arity,
			Context lift, Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		ENode<O,T,V> node;
		if (!context.containsKey(term))
			context.put(term, node = epeg.createNode(lift.get(term)));
		else
			node = context.get(term);
		node.setArity(arity);
		for (int i = 0; i < arity; i++) {
			ENode<O,T,V> child = context.get(new TermChild<T,V>(term, i));
			if (child != null)
				node.getChild(i).unifyWith(child);
		}
	}
	
	private void generalizeOps(Proof proof,
			Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIs) {
				OpIs<FlowValue<?,O>,T> opIs = (OpIs)property; 
				ENode<O,T,V> node = context.get(opIs.getTerm());
				FlowValue<?,O> op = opIs.getOp();
				if (op.isPhi())
					node.getOp().setPhi();
				else if (op.isZero())
					node.getOp().setZero();
				else if (op.isSuccessor())
					node.getOp().setSuccessor();
				else if (op.isExtendedDomain())
					node.getOp().setExtendedDomainOp(op.getDomain(mAmbassador));
				else if (op.isLoopFunction())
					throw new IllegalArgumentException();
				else
					throw new UnhandledCaseException();
			} else if (property instanceof OpIsLoopOp) {
				OpIsLoopOp<T> opIsLoopOp = (OpIsLoopOp)property;
				context.get(opIsLoopOp.getTerm()).getOp().setLoopOp(
						opIsLoopOp.getOp());
			} else if (property instanceof OpsEqual) {
				OpsEqual<FlowValue<?,O>,T> opsEqual = (OpsEqual)property;
				context.get(opsEqual.getLeft()).getOp().unifyWith(
						context.get(opsEqual.getRight()).getOp());
			}
	}
	
	private void generalizeLoops(Proof proof,
			Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIsSameLoop) {
				OpIsSameLoop<T> opIsSameLoop = (OpIsSameLoop)property;
				context.get(opIsSameLoop.getLeft()).getOp().getLoopDepth()
						.unifyWith(context.get(opIsSameLoop.getRight()).getOp()
								.getLoopDepth());
			} else if (property instanceof OpIsDifferentLoop) {
				OpIsDifferentLoop<T> opIsDifferentLoop
						= (OpIsDifferentLoop)property;
				context.get(opIsDifferentLoop.getLeft()).getOp().getLoopDepth()
						.setDistinctFrom(context.get(opIsDifferentLoop
								.getRight()).getOp().getLoopDepth());
			} else if (property instanceof OpIsAllLoopLifted)
				context.get(((OpIsAllLoopLifted<T>)property).getTerm())
						.getOp().setAllLoopLifted();
			else if (property instanceof OpIsLoopLifted) {
				OpIsLoopLifted<T> opIsLoopLifted = (OpIsLoopLifted)property;
				context.get(opIsLoopLifted.getTerm()).getOp().setLoopLifted(
						context.get(opIsLoopLifted.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeInvariance(Proof proof,
			Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		for (Property property : proof.getProperties())
			if (property instanceof IsInvariant) {
				IsInvariant<T,V> isInvariant = (IsInvariant)property;
				context.get(isInvariant.getTerm()).setInvariant(
						context.get(isInvariant.getLoop())
								.getOp().getLoopDepth());
			} else if (property instanceof ChildIsInvariant) {
				ChildIsInvariant<T,V> childIsInvariant
						= (ChildIsInvariant)property;
				context.get(childIsInvariant.getTerm())
						.getChild(childIsInvariant.getChild())
						.setInvariant(context.get(childIsInvariant.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeEqualities(
			Collection<? super Couple<ENode<O,T,V>>> equalities,
			Collection<? super Couple<ENode<O,T,V>>> assumptions,
			Proof proof,
			Map<TermOrTermChild<T,V>,ENode<O,T,V>> context) {
		for (Property property : proof.getProperties())
			if (property instanceof AreEquivalent) {
				AreEquivalent<T,V> areEquivalent = (AreEquivalent)property;
				addEquality(equalities, assumptions,
						context.get(areEquivalent.getLeft()),
						context.get(areEquivalent.getRight()));
			} else if (property instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> childIsEquivalentTo
						= (ChildIsEquivalentTo)property;
				addEquality(equalities, assumptions,
						context.get(childIsEquivalentTo.getParentTerm())
								.getChild(childIsEquivalentTo.getChild()),
						context.get(childIsEquivalentTo.getTerm()));
			} else if (property instanceof EquivalentChildren) {
				EquivalentChildren<T,V> equivalentChildren
						= (EquivalentChildren)property;
				addEquality(equalities, assumptions,
						context.get(equivalentChildren.getLeftTerm())
								.getChild(equivalentChildren.getLeftChild()),
						context.get(equivalentChildren.getRightTerm())
								.getChild(equivalentChildren.getRightChild()));
			}
	}
	
	private void addEquality(
			Collection<? super Couple<ENode<O,T,V>>> equalities,
			Collection<? super Couple<ENode<O,T,V>>> assumptions,
			ENode<O,T,V> left, ENode<O,T,V> right) {
		if (left.getAnchor().equals(right.getAnchor()))
			left.unifyWith(right);
		else if (left.getAnchor().isReachable()
				&& right.getAnchor().isReachable())
			assumptions.add(new Couple<ENode<O,T,V>>(left, right));
		else
			equalities.add(new Couple<ENode<O,T,V>>(left, right));
	}
}
