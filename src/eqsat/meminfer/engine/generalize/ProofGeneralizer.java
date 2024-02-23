package eqsat.meminfer.engine.generalize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.generalize.GenEPEG.Node;
import eqsat.meminfer.engine.generalize.GenEPEG.NodeOrNodeChild;
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

public class ProofGeneralizer
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	private final OpAmbassador<O> mAmbassador;
	private final ProofManager<T,V> mProofs;
	private final GenEPEG<O,T,V> mEPEG = new GenEPEG<O,T,V>();
	private final Set<Couple<NodeOrNodeChild<O,T,V>>> mProcessed
			= new HashSet();
	private final Stack<Couple<NodeOrNodeChild<O,T,V>>> mEqualities
			= new Stack();
	
	public ProofGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs) {
		mAmbassador = ambassador;
		mProofs = proofs;
	}
	public ProofGeneralizer(OpAmbassador<O> ambassador,
			ProofManager<T,V> proofs, T left, T right) {
		this(ambassador, proofs);
		generalizeEquality(mEPEG.createNode(left), mEPEG.createNode(right));
		process();
	}
	
	public GenEPEG<O,T,V> getEPEG() {return mEPEG;}
	
	public void process() {
		while (!mEqualities.isEmpty()) {
			Couple<NodeOrNodeChild<O,T,V>> equality = mEqualities.pop();
			generalizeEquality(equality.getLeft(), equality.getRight());
		}
	}
	
	public void generalizeEquality(NodeOrNodeChild<O,T,V> left,
			NodeOrNodeChild<O,T,V> right) {
		Couple<NodeOrNodeChild<O,T,V>> equality
				= new Couple<NodeOrNodeChild<O,T,V>>(left, right);
		if (mProcessed.contains(equality))
			return;
		PairedList<TermOrTermChild<T,V>,Proof> path
				= mProofs.getProofPath(left.getTermOrTermChild(),
						right.getTermOrTermChild());
		for (int i = 0; i < path.size() - 2; )
			if (path.getSecond(i) == null && path.getSecond(i + 1) == null)
				path.removeAt(i + 1);
			else
				i++;
		NodeOrNodeChild<O,T,V>[] anchors = new NodeOrNodeChild[path.size()];
		anchors[0] = left;
		anchors[anchors.length - 1] = right;
		for (int i = 0; i < path.size() - 1; i++)
			if (path.getSecond(i) != null) {
				Map<T,Node<O,T,V>> nodeMap = new HashMap();
				if (anchors[i] != null) {
					if (anchors[i].isNode())
						nodeMap.put(anchors[i].getNode().getTerm(),
								anchors[i].getNode());
					else if (anchors[i].isNodeChild())
						nodeMap.put(
								anchors[i].getNodeChild().getParent().getTerm(),
								anchors[i].getNodeChild().getParent());
					else
						throw new UnhandledCaseException();
				}
				if (anchors[i + 1] != null) {
					if (anchors[i + 1].isNode())
						nodeMap.put(anchors[i + 1].getNode().getTerm(),
								anchors[i + 1].getNode());
					else if (anchors[i + 1].isNodeChild())
						nodeMap.put(anchors[i + 1].getNodeChild()
										.getParent().getTerm(),
								anchors[i + 1].getNodeChild().getParent());
					else
						throw new UnhandledCaseException();
				}
				generalizeProof(path.getSecond(i), nodeMap);
				if (anchors[i] == null) {
					if (path.getFirst(i).isTerm())
						anchors[i] = nodeMap.get(path.getFirst(i).getTerm());
					else if (path.getFirst(i).isTermChild())
						anchors[i] = nodeMap.get(
								path.getFirst(i).getParentTerm()).getChild(
										path.getFirst(i).getChildIndex());
					else
						throw new UnhandledCaseException();
				}
				if (anchors[i + 1] == null) {
					if (path.getFirst(i + 1).isTerm())
						anchors[i + 1]
						        = nodeMap.get(path.getFirst(i + 1).getTerm());
					else if (path.getFirst(i + 1).isTermChild())
						anchors[i + 1] = nodeMap.get(
								path.getFirst(i + 1).getParentTerm()).getChild(
										path.getFirst(i + 1).getChildIndex());
					else
						throw new UnhandledCaseException();
				}
			}
		for (int i = 0; i < path.size() - 1; i++)
			if (path.getSecond(i) == null)
				mEPEG.addEquality(anchors[i], anchors[i + 1]);
		mProcessed.add(equality);
	}
	
	public void generalizeMatch(NodeOrNodeChild<O,T,V> node, Proof proof) {
		HashMap<T,Node<O,T,V>> nodeMap = new HashMap();
		if (node.isNode())
			nodeMap.put(node.getNode().getTerm(), node.getNode());
		else if (node.isNodeChild())
			nodeMap.put(node.getNodeChild().getParent().getTerm(),
					node.getNodeChild().getParent());
		else
			throw new UnhandledCaseException();
		generalizeProof(proof, nodeMap);
	}
	
	private void generalizeProof(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
		generalizeArities(proof, nodeMap);
		generalizeOps(proof, nodeMap);
		generalizeLoops(proof, nodeMap);
		generalizeInvariance(proof, nodeMap);
		generalizeEqualities(proof, nodeMap);
	}
	
	private void generalizeArities(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof ArityIs) {
				ArityIs<T> arityIs = (ArityIs<T>)property;
				T term = arityIs.getTerm();
				if (!nodeMap.containsKey(term))
					nodeMap.put(term, mEPEG.createNode(term));
				nodeMap.get(term).setArity(arityIs.getArity());
			}
	}
	
	private void generalizeOps(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
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
	
	private void generalizeLoops(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
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
	
	private void generalizeInvariance(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
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
	
	private void generalizeEqualities(Proof proof, Map<T,Node<O,T,V>> nodeMap) {
		for (Property property : proof.getProperties())
			if (property instanceof AreEquivalent) {
				AreEquivalent<T,V> areEquivalent = (AreEquivalent)property;
				Couple<NodeOrNodeChild<O,T,V>> equal
						= new Couple<NodeOrNodeChild<O,T,V>>(
								nodeMap.get(areEquivalent.getLeft()),
								nodeMap.get(areEquivalent.getRight()));
				if (!mEqualities.contains(equal))
					mEqualities.push(equal);
			} else if (property instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> childIsEquivalentTo
						= (ChildIsEquivalentTo)property;
				Couple<NodeOrNodeChild<O,T,V>> equal
						= new Couple<NodeOrNodeChild<O,T,V>>(
								nodeMap.get(childIsEquivalentTo.getParentTerm())
									.getChild(childIsEquivalentTo.getChild()),
								nodeMap.get(childIsEquivalentTo.getTerm()));
				if (!mEqualities.contains(equal))
					mEqualities.push(equal);
			} else if (property instanceof EquivalentChildren) {
				EquivalentChildren<T,V> equivalentChildren
						= (EquivalentChildren)property;
				Couple<NodeOrNodeChild<O,T,V>> equal
						= new Couple<NodeOrNodeChild<O,T,V>>(
								nodeMap.get(equivalentChildren.getLeftTerm())
								.getChild(equivalentChildren.getLeftChild()),
								nodeMap.get(equivalentChildren.getRightTerm())
								.getChild(equivalentChildren.getRightChild()));
				if (!mEqualities.contains(equal))
					mEqualities.push(equal);
			}
	}
}
