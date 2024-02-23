package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.generalize.MultiGenEPEG.Node;
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
import util.UnhandledCaseException;
import util.pair.Couple;
import util.pair.PairedList;

public class ProofMultiGeneralizer
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	private final OpAmbassador<O> mAmbassador;
	private final ProofManager<T,V> mProofs;
	private final List<MultiGenEPEG<O,T,V>> mEPEGs = new ArrayList();
	private final Set<Couple<TermOrTermChild<T,V>>> mProcessed = new HashSet();
	private final Stack<Couple<TermOrTermChild<T,V>>> mEqualities = new Stack();
	
	public ProofMultiGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs,// Collection<? extends T> terms,
			TermOrTermChild<T,V> left, TermOrTermChild<T,V> right) {
		mAmbassador = ambassador;
		mProofs = proofs;
		mEqualities.add(new Couple<TermOrTermChild<T,V>>(left, right));
		while (!mEqualities.isEmpty()) {
			Couple<TermOrTermChild<T,V>> equality = mEqualities.pop();
			generalizeEquality(equality.getLeft(), equality.getRight());
		}
	}
	
	public ProofMultiGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs,// Collection<? extends T> terms,
			TermOrTermChild<T,V> leftValue, TermOrTermChild<T,V> rightValue,
			TermOrTermChild<T,V> leftState, TermOrTermChild<T,V> rightState) {
		mAmbassador = ambassador;
		mProofs = proofs;
		mEqualities.add(
				new Couple<TermOrTermChild<T,V>>(leftValue, rightValue));
		mEqualities.add(
				new Couple<TermOrTermChild<T,V>>(leftState, rightState));
		while (!mEqualities.isEmpty()) {
			Couple<TermOrTermChild<T,V>> equality = mEqualities.pop();
			generalizeEquality(equality.getLeft(), equality.getRight());
		}
	}
	
	public List<? extends MultiGenEPEG<O,T,V>> getEPEGs() {return mEPEGs;}
	
	private void generalizeEquality(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right) {
		if (!mProcessed.add(new Couple<TermOrTermChild<T,V>>(left, right)))
			return;
		if (left.asTerm().equals(right.asTerm()))
			return;
		MultiGenEPEG<O,T,V> epeg = new MultiGenEPEG<O,T,V>(mAmbassador);
		Node<O,T,V> leftNode = epeg.createNode(left);
		Node<O,T,V> rightNode = epeg.createNode(right);
		leftNode.mark();
		rightNode.mark();
		List<Couple<Node<O,T,V>>> equalities = new LinkedList();
		generalizeEquality(epeg, equalities, leftNode, rightNode);
		Collection<Couple<Node<O,T,V>>> assumptions = new ArrayList();
		while (!equalities.isEmpty()) {
			for (Iterator<Couple<Node<O,T,V>>> itr = equalities.iterator();
					itr.hasNext(); ) {
				Couple<Node<O,T,V>> equality = itr.next();
				if (equality.getLeft().getAnchor().asTerm().equals(
						equality.getRight().getAnchor().asTerm())) {
					equality.getLeft().unifyWith(equality.getRight());
					itr.remove();
				}
			}
			for (Iterator<Couple<Node<O,T,V>>> itr = equalities.iterator();
					itr.hasNext(); ) {
				Couple<Node<O,T,V>> equality = itr.next();
				if (equality.getLeft().isReachable()
						&& equality.getRight().isReachable()) {
					assumptions.add(equality);
					itr.remove();
				}
			}
			if (!equalities.isEmpty()) {
				Couple<Node<O,T,V>> equality = equalities.remove(0);
				generalizeEquality(epeg, equalities,
						equality.getLeft(), equality.getRight());
			}
		}
		for (Couple<Node<O,T,V>> assumption : assumptions)
			if (!assumption.getLeft().hasArity()
					|| !assumption.getRight().hasArity())
				assumption.getLeft().unifyWith(assumption.getRight());
		for (Couple<Node<O,T,V>> assumption : assumptions) {
			if (!assumption.getLeft().equals(assumption.getRight()))
				epeg.addEquality(assumption.getLeft(), assumption.getRight());
			mEqualities.add(new Couple<TermOrTermChild<T,V>>(
					assumption.getLeft().getAnchor(),
					assumption.getRight().getAnchor()));
		}
		if (!leftNode.equals(rightNode))
			mEPEGs.add(epeg);
	}
	
	private void generalizeEquality(MultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<Node<O,T,V>>> equalities,
			Node<O,T,V> left, Node<O,T,V> right) {
		if (right.isReachable() && !left.isReachable()) {
			Node<O,T,V> node = left;
			left = right;
			right = node;
		}
		PairedList<TermOrTermChild<T,V>,Proof> path
				= mProofs.getProofPath(left.getAnchor(), right.getAnchor());
		while (path.size() > 1 && path.getSecond(0) == null) {
			Node<O,T,V> node = epeg.createNode(path.getFirst(1));
			left.unifyWith(node);
			left = node;
			path.removeAt(0);
		}
		while (path.size() > 1 && path.getSecond(path.size() - 2) == null) {
			Node<O,T,V> node = epeg.createNode(path.getFirst(path.size() - 2));
			right.unifyWith(node);
			right = node;
			path.removeLast();
		}
		if (path.size() == 1)
			return;
		else if (path.size() == 2) {
			generalizeEquality(epeg, equalities, left, right,
					path.getSecond(0));
			return;
		}
		Node<O,T,V> node = epeg.createNode(path.getFirst(1));
		generalizeEquality(epeg, equalities, left, node, path.getSecond(0));
		equalities.add(new Couple<Node<O,T,V>>(node, right));
	}

	private void generalizeEquality(MultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<Node<O,T,V>>> equalities,
			Node<O,T,V> left, Node<O,T,V> right, Proof proof) {
		Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap = new HashMap();
		nodeMap.put(left.getAnchor(), left);
		nodeMap.put(right.getAnchor(), right);
		generalizeProof(epeg, equalities, proof, nodeMap);
	}
	
	private void generalizeProof(MultiGenEPEG<O,T,V> epeg,
			Collection<? super Couple<Node<O,T,V>>> equalities, Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		generalizeArities(epeg, proof, nodeMap);
		generalizeOps(proof, nodeMap);
		generalizeLoops(proof, nodeMap);
		generalizeInvariance(proof, nodeMap);
		generalizeEqualities(equalities, proof, nodeMap);
	}
	
	private void generalizeArities(MultiGenEPEG<O,T,V> epeg, Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof ArityIs) {
				ArityIs<T> arityIs = (ArityIs<T>)property;
				setArity(epeg, arityIs.getTerm(), arityIs.getArity(), nodeMap);
			}
	}
	
	private void setArity(MultiGenEPEG<O,T,V> epeg, T term, int arity,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		Node<O,T,V> node;
		if (!nodeMap.containsKey(term))
			nodeMap.put(term, node = epeg.createNode(term));
		else
			node = nodeMap.get(term);
		node.setArity(arity);
		for (int i = 0; i < arity; i++) {
			TermChild<T,V> child = new TermChild<T,V>(term, i);
			if (nodeMap.containsKey(child))
				node.getChild(i).unifyWith(nodeMap.get(child));
			else
				nodeMap.put(child, node.getChild(i));
		}
	}
	
	private void generalizeOps(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIs) {
				OpIs<FlowValue<?,O>,T> opIs = (OpIs)property; 
				Node<O,T,V> node = nodeMap.get(opIs.getTerm());
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
				nodeMap.get(opIsLoopOp.getTerm()).getOp().setLoopOp(
						opIsLoopOp.getOp());
			} else if (property instanceof OpsEqual) {
				OpsEqual<FlowValue<?,O>,T> opsEqual = (OpsEqual)property;
				nodeMap.get(opsEqual.getLeft()).getOp().unifyWith(
						nodeMap.get(opsEqual.getRight()).getOp());
			}
	}
	
	private void generalizeLoops(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof OpIsSameLoop) {
				OpIsSameLoop<T> opIsSameLoop = (OpIsSameLoop)property;
				nodeMap.get(opIsSameLoop.getLeft()).getOp().getLoopDepth()
						.unifyWith(nodeMap.get(opIsSameLoop.getRight()).getOp()
								.getLoopDepth());
			} else if (property instanceof OpIsDifferentLoop) {
				OpIsDifferentLoop<T> opIsDifferentLoop
						= (OpIsDifferentLoop)property;
				nodeMap.get(opIsDifferentLoop.getLeft()).getOp().getLoopDepth()
						.distinctFrom(nodeMap.get(opIsDifferentLoop.getRight())
								.getOp().getLoopDepth());
			} else if (property instanceof OpIsAllLoopLifted)
				nodeMap.get(((OpIsAllLoopLifted<T>)property).getTerm())
						.getOp().setAllLoopLifted();
			else if (property instanceof OpIsLoopLifted) {
				OpIsLoopLifted<T> opIsLoopLifted = (OpIsLoopLifted)property;
				nodeMap.get(opIsLoopLifted.getTerm()).getOp().setLoopLifted(
						nodeMap.get(opIsLoopLifted.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeInvariance(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof IsInvariant) {
				IsInvariant<T,V> isInvariant = (IsInvariant)property;
				nodeMap.get(isInvariant.getTerm()).setInvariant(
						nodeMap.get(isInvariant.getLoop())
								.getOp().getLoopDepth());
			} else if (property instanceof ChildIsInvariant) {
				ChildIsInvariant<T,V> childIsInvariant
						= (ChildIsInvariant)property;
				nodeMap.get(childIsInvariant.getTerm())
						.getChild(childIsInvariant.getChild())
						.setInvariant(nodeMap.get(childIsInvariant.getLoop())
								.getOp().getLoopDepth());
			}
	}
	
	private void generalizeEqualities(
			Collection<? super Couple<Node<O,T,V>>> equalities, Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof AreEquivalent) {
				AreEquivalent<T,V> areEquivalent = (AreEquivalent)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(areEquivalent.getLeft()),
						nodeMap.get(areEquivalent.getRight()));
				equalities.add(equal);
			} else if (property instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> childIsEquivalentTo
						= (ChildIsEquivalentTo)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(childIsEquivalentTo.getParentTerm())
								.getChild(childIsEquivalentTo.getChild()),
						nodeMap.get(childIsEquivalentTo.getTerm()));
				equalities.add(equal);
			} else if (property instanceof EquivalentChildren) {
				EquivalentChildren<T,V> equivalentChildren
						= (EquivalentChildren)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(equivalentChildren.getLeftTerm())
								.getChild(equivalentChildren.getLeftChild()),
						nodeMap.get(equivalentChildren.getRightTerm())
								.getChild(equivalentChildren.getRightChild()));
				equalities.add(equal);
			}
	}
}
