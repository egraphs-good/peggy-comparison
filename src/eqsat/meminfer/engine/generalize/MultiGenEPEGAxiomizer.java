package eqsat.meminfer.engine.generalize;

import eqsat.FlowValue;
import eqsat.meminfer.engine.generalize.MultiGenEPEG.GenOp;
import eqsat.meminfer.engine.generalize.MultiGenEPEG.LoopDepth;
import eqsat.meminfer.engine.generalize.MultiGenEPEG.Node;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.Structurizer;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddExistingOpNode;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddNewOpNode;
import eqsat.meminfer.network.peg.PEGExpressionizer;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork;
import eqsat.meminfer.network.peg.axiom.PEGOpMaker;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddLoopOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AddOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

public class MultiGenEPEGAxiomizer {
	public static <O> AxiomNode<O,? extends PEGNode<O>> axiomize(
			Network network, final MultiGenEPEG<O,?,?> epeg, String name,
			Node<O,?,?> trigger, Node<O,?,?> result, boolean resultExists) {
		final PEGNetwork<O> pegNetwork = new PEGNetwork<O>(network);
		final PEGExpressionizer<GenOp<O>,LoopDepth,O,Node<O,?,?>> expressionizer
				= new PEGExpressionizer<GenOp<O>,LoopDepth,O,Node<O,?,?>>() {
			public PEGNetwork<O> getNetwork() {return pegNetwork;}
			protected PEGLabelAmbassador<GenOp<O>,LoopDepth,O> getAmbassador() {
				return epeg.getLabelAmbassador();
			}
			protected boolean isParameter(Node<O,?,?> vertex) {
				return vertex.getLabel() == null;
			}
			protected GenOp<O> getOperator(Node<O,?,?> vertex) {
				return vertex.getOp();
			}
			protected boolean mustBeInvariant(Node<O,?,?> vertex,
					LoopDepth depth) {
				return vertex.getInvariance().contains(depth);
			}
		};
		expressionizer.addExpression(trigger);
		if (resultExists)
			expressionizer.addExpression(result);
		PEGNode<O> pegNode = expressionizer.getPEGExpression();
		final PeggyAxiomNetwork<O> peggyAxiomNetwork
				= new PeggyAxiomNetwork<O>(network);
		final PEGOpMaker<GenOp<O>,LoopDepth,O,AddOpNode<O>,Node<O,?,?>> maker
				= new PEGOpMaker
				<GenOp<O>,LoopDepth,O,AddOpNode<O>,Node<O,?,?>>() {
			public AddPEGOpNetwork<O> getNetwork() {return peggyAxiomNetwork;}
			protected PEGLabelAmbassador<GenOp<O>,LoopDepth,O> getAmbassador() {
				return epeg.getLabelAmbassador();
			}
			public Structurizer<Node<O,?,?>> getStructurizer() {
				return expressionizer;
			}
			
			protected GenOp<O> getOperator(Node<O,?,?> vertex) {
				return vertex.getOp();
			}
			
			protected AddOpNode<O> convertAddLoopOpNode(AddLoopOpNode node) {
				return peggyAxiomNetwork.adaptAddLoopOp(node);
			}
			protected AddOpNode<O> convertAddExistingOpNode(
					AddExistingOpNode node) {
				return peggyAxiomNetwork.adaptAddExistingOp(node);
			}
			protected AddOpNode<O> convertAddNewOpNode(
					AddNewOpNode<FlowValue<?,O>> node) {
				return peggyAxiomNetwork.adaptAddNewOp(node);
			}
		};
		maker.addVertex(trigger);
		maker.addVertex(result);
		maker.makeEqual(result, trigger);
		return peggyAxiomNetwork.<PEGNode<O>>axiom(name, pegNode,
				maker.getAddOps(), maker.getPlaceHolders(),
				maker.getConstructs(), maker.getMerges());
	}
}
