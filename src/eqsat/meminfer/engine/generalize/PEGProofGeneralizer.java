package eqsat.meminfer.engine.generalize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.generalize.GenPEG.Node;
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

public class PEGProofGeneralizer
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	private final OpAmbassador<O> mAmbassador;
	private final ProofManager<T,V> mProofs;
	private final GenPEG<O,T,V> mPEG;
	private final Set<Couple<Node<O,T,V>>> mProcessed = new HashSet();
	private final Stack<Couple<Node<O,T,V>>> mEqualities = new Stack();
	
	public PEGProofGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs, T left, T right) {
		mAmbassador = ambassador;
		mProofs = proofs;
		mPEG = new GenPEG<O,T,V>(ambassador);
		Node<O,T,V> leftNode = mPEG.createNode(left);
		Node<O,T,V> rightNode = mPEG.createNode(right);
		leftNode.mark();
		rightNode.mark();
		generalizeEquality(leftNode, rightNode);
		process();
		mPEG.simplify();
	}
	
	public GenPEG<O,T,V> getPEG() {return mPEG;}
	
	private void process() {
		while (!mEqualities.isEmpty()) {
			Couple<Node<O,T,V>> equality = mEqualities.pop();
			generalizeEquality(equality.getLeft(), equality.getRight());
		}
	}
	
	private void generalizeEquality(Node<O,T,V> left, Node<O,T,V> right) {
		Couple<Node<O,T,V>> equality = new Couple<Node<O,T,V>>(left, right);
		if (mProcessed.contains(equality))
			return;
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
		mProcessed.add(equality);
	}
	
	private void generalizeEquality(
			Node<O,T,V> left, Node<O,T,V> right, Proof proof) {
		Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap = new HashMap();
		nodeMap.put(left.getAnchor(), left);
		nodeMap.put(right.getAnchor(), right);
		generalizeProof(proof, nodeMap);
	}
	
	private void generalizeProof(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		generalizeArities(proof, nodeMap);
		generalizeOps(proof, nodeMap);
		generalizeLoops(proof, nodeMap);
		generalizeInvariance(proof, nodeMap);
		generalizeEqualities(proof, nodeMap);
	}
	
	private void generalizeArities(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof ArityIs) {
				ArityIs<T> arityIs = (ArityIs<T>)property;
				setArity(arityIs.getTerm(), arityIs.getArity(), nodeMap);
			}
	}
	
	private void setArity(T term, int arity,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		Node<O,T,V> node;
		if (!nodeMap.containsKey(term))
			nodeMap.put(term, node = mPEG.createNode(term));
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
				Node node = nodeMap.get(opIs.getTerm());
				FlowValue<?,O> op = opIs.getOp();
				if (op.isPhi())
					node.getOp().setPhi();
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
	
	private void generalizeEqualities(Proof proof,
			Map<TermOrTermChild<T,V>,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof AreEquivalent) {
				AreEquivalent<T,V> areEquivalent = (AreEquivalent)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(areEquivalent.getLeft()),
						nodeMap.get(areEquivalent.getRight()));
				mEqualities.push(equal);
			} else if (property instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> childIsEquivalentTo
						= (ChildIsEquivalentTo)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(childIsEquivalentTo.getParentTerm())
								.getChild(childIsEquivalentTo.getChild()),
						nodeMap.get(childIsEquivalentTo.getTerm()));
				mEqualities.push(equal);
			} else if (property instanceof EquivalentChildren) {
				EquivalentChildren<T,V> equivalentChildren
						= (EquivalentChildren)property;
				Couple<Node<O,T,V>> equal = new Couple<Node<O,T,V>>(
						nodeMap.get(equivalentChildren.getLeftTerm())
								.getChild(equivalentChildren.getLeftChild()),
						nodeMap.get(equivalentChildren.getRightTerm())
								.getChild(equivalentChildren.getRightChild()));
				mEqualities.push(equal);
			}
	}
}
