package eqsat.meminfer.engine.generalize;

import eqsat.FlowValue;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG.EGenOp;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG.ELoopDepth;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG.ENode;
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
import util.pair.Couple;

public class PostMultiGenEPEGAxiomizer {
	public static <O> AxiomNode<O,? extends PEGNode<O>> axiomize(
			Network network, final PostMultiGenEPEG<O,?,?> epeg, String name,
			ENode<O,?,?> trigger, ENode<O,?,?> result, boolean resultExists) {
		final PEGNetwork<O> pegNetwork = new PEGNetwork<O>(network);
		final PEGExpressionizer<EGenOp<O>,ELoopDepth,O,ENode<O,?,?>>
				expressionizer
				= new PEGExpressionizer<EGenOp<O>,ELoopDepth,O,ENode<O,?,?>>() {
			public PEGNetwork<O> getNetwork() {return pegNetwork;}
			protected PEGLabelAmbassador<EGenOp<O>,ELoopDepth,O> getAmbassador()
					{
				return epeg.getEGenOpAmbassador();
			}
			protected boolean isParameter(ENode<O,?,?> vertex) {
				return vertex.getLabel() == null;
			}
			protected EGenOp<O> getOperator(ENode<O,?,?> vertex) {
				return vertex.getOp();
			}
			protected boolean mustBeInvariant(ENode<O,?,?> vertex,
					ELoopDepth depth) {
				return vertex.isInvariant(depth);
			}
		};
		expressionizer.addExpression(trigger);
		if (resultExists)
			expressionizer.addExpression(result);
		for (Couple<? extends ENode<O,?,?>> equality : epeg.getEqualities())
			expressionizer.makeEqual(equality.getLeft(), equality.getRight());
		PEGNode<O> pegNode = expressionizer.getPEGExpression();
		final PeggyAxiomNetwork<O> peggyAxiomNetwork
				= new PeggyAxiomNetwork<O>(network);
		final PEGOpMaker<EGenOp<O>,ELoopDepth,O,AddOpNode<O>,ENode<O,?,?>> maker
				= new PEGOpMaker
				<EGenOp<O>,ELoopDepth,O,AddOpNode<O>,ENode<O,?,?>>() {
			public AddPEGOpNetwork<O> getNetwork() {return peggyAxiomNetwork;}
			protected PEGLabelAmbassador<EGenOp<O>,ELoopDepth,O> getAmbassador()
					{
				return epeg.getEGenOpAmbassador();
			}
			public Structurizer<ENode<O,?,?>> getStructurizer() {
				return expressionizer;
			}
			
			protected EGenOp<O> getOperator(ENode<O,?,?> vertex) {
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
