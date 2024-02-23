package peggy.analysis.llvm;

import peggy.analysis.Analysis;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This analysis has axioms that refer to the select instruction.
 */
public abstract class SelectAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("SelectAnalysis: " + message);
	}

	public SelectAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		// TODO
		
//		addDistributeThroughSelect(2, 0);
//		addDistributeThroughSelect(2, 1);
//
//		addDistributeThroughSelect(3, 0);
//		addDistributeThroughSelect(3, 1);
//		addDistributeThroughSelect(3, 2);
//		
//		addFactorOutSelect(2, 0);
//		addFactorOutSelect(2, 1);
//
//		addFactorOutSelect(3, 0);
//		addFactorOutSelect(3, 1);
//		addFactorOutSelect(3, 2);
//		// TODO more!
	}

//	/**
//	 * op(A1,A2,...,select(C,T,F),Ai,...,An) =
//	 * select(C,op(A1,A2,...,T,Ai,....An),op(A1,A2,...,F,Ai,...An))
//	 */
//	private void addDistributeThroughSelect(
//			int opArity, int selectIndex) {
//		{
//			PeggyAxiomizer<LLVMLabel, Integer> axiomizer = 
//				new PeggyAxiomizer<LLVMLabel, Integer>(getNetwork(), getAmbassador());
//			PeggyVertex<LLVMLabel, Integer> C = axiomizer.getVariable(1);
//			PeggyVertex<LLVMLabel, Integer> T = axiomizer.getVariable(2);
//			PeggyVertex<LLVMLabel, Integer> F = axiomizer.getVariable(3);
//			PeggyVertex<LLVMLabel, Integer> select = axiomizer.get(
//					SimpleLLVMLabel.get(LLVMOperator.SELECT), C, T, F);
//			int next = 4;
//
//			List<PeggyVertex<LLVMLabel, Integer>> opArgs = 
//				new ArrayList<PeggyVertex<LLVMLabel, Integer>>(opArity);
//			for (int i = 0; i < opArity; i++) {
//				if (i == selectIndex) {
//					opArgs.add(select);
//				} else {
//					opArgs.add(axiomizer.getVariable(next++));
//				}
//			}
//
//			PeggyVertex<LLVMLabel, Integer> op = axiomizer.get(null, opArgs);
//			axiomizer.mustExist(op);
//
//			PEGNode<LLVMLabel> trigger = axiomizer.getTrigger();
//			Event<? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> triggerEvent = 
//				getEngine().getEGraph().processPEGNode(trigger);
//			Structurizer<PeggyVertex<LLVMLabel, Integer>> structurizer = 
//				axiomizer.getStructurizer();
//
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> opFunction = 
//				getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(op));
//
//			List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> opVarFunctions = 
//				new ArrayList<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>>();
//			for (int i = 0; i < opArity; i++) {
//				if (i != selectIndex) {
//					opVarFunctions.add(getEngine().getEGraph().processValueNode(
//							structurizer.getValue(opArgs.get(i))));
//				}
//			}
//
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> cFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(C));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> tFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(T));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> fFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(F));
//
//			SelectDistributeListener listener = 
//				new SelectDistributeListener(
//						selectIndex, 
//						opFunction, 
//						opVarFunctions, 
//						cFunction,
//						tFunction, 
//						fFunction);
//			StringBuilder strlhs = new StringBuilder();
//			StringBuilder strrhs1 = new StringBuilder();
//			StringBuilder strrhs2 = new StringBuilder();
//			strlhs.append("op(");
//			strrhs1.append("op(");
//			strrhs2.append("op(");
//			for (int i = 0; i < opArity; i++) {
//				if (i>0) {
//					strlhs.append(",");
//					strrhs1.append(",");
//					strrhs2.append(",");
//				}
//				if (i==selectIndex) {
//					strlhs.append("select(C,T,F)");
//					strrhs1.append("T");
//					strrhs2.append("F");
//				} else {
//					String var = "A" + i;
//					strlhs.append(var);
//					strrhs1.append(var);
//					strrhs2.append(var);
//				}
//			}
//			strlhs.append(")");
//			strrhs1.append(")");
//			strrhs2.append(")");
//			listener.addListener(new PrintListener(strlhs + " = select(C," + strrhs1 + "," + strrhs2 + ")"));
//			triggerEvent.addListener(listener);
//		}
//	}
//
//	class SelectDistributeListener 
//	extends AbstractChainEvent<Structure<CPEGTerm<LLVMLabel, LLVMParameter>>,Void> {
//		final private int selectIndex;
//		final private Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> opFunction;
//		final private List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> opVarFunctions;
//		final private Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> cFunction,
//				tFunction, fFunction;
//
//		SelectDistributeListener(
//				int _selectIndex,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> _opFunction,
//				List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> _opVarFunctions,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _cFunction,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _tFunction,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _fFunction) {
//			this.selectIndex = _selectIndex;
//			this.opFunction = _opFunction;
//			this.opVarFunctions = _opVarFunctions;
//			this.cFunction = _cFunction;
//			this.tFunction = _tFunction;
//			this.fFunction = _fFunction;
//		}
//
//		public boolean notify(
//				Structure<CPEGTerm<LLVMLabel, LLVMParameter>> structure) {
//			if (!this.canUse(structure))
//				return true;
//
//			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
//				new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>>();
//
//			CPEGTerm<LLVMLabel, LLVMParameter> opTerm = opFunction.get(structure);
//			LLVMLabel opLabel = opTerm.getOp().getDomain();
//
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> cVertex = 
//				futureGraph.getVertex(cFunction.get(structure));
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> tVertex = 
//				futureGraph.getVertex(tFunction.get(structure));
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> fVertex = 
//				futureGraph.getVertex(fFunction.get(structure));
//
//			Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> list1[] = 
//				new Vertex[opVarFunctions.size()+1];
//			Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> list2[] = 
//				new Vertex[opVarFunctions.size()+1];
//
//			int counter = 0;
//			for (Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> func : opVarFunctions) {
//				Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> vertex = 
//					futureGraph.getVertex(func.get(structure));
//				
//				if (counter == selectIndex) {
//					list1[counter] = tVertex;
//					list2[counter] = fVertex;
//					counter++;
//				}
//
//				list1[counter] = vertex;
//				list2[counter] = vertex;
//				counter++;
//			}
//			if (counter == selectIndex) {
//				list1[counter] = tVertex;
//				list2[counter] = fVertex;
//				counter++;
//			}
//
//			FlowValue<LLVMParameter, LLVMLabel> opFlow = getDomain(opLabel);
//
//			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> result = 
//				futureGraph.getExpression(
//						getDomain(SimpleLLVMLabel.get(LLVMOperator.SELECT)), 
//						cVertex, 
//						futureGraph.getExpression(opFlow, list1), 
//						futureGraph.getExpression(opFlow, list2));
//
//			result.setValue(opTerm);
//
//			getEngine().getEGraph().addExpressions(futureGraph);
//			
//			debug("triggered distribute select");
//			
//			trigger(null);
//			return true;
//		}
//
//		public boolean canUse(
//				Structure<CPEGTerm<LLVMLabel, LLVMParameter>> structure) {
//			// check basetype term
//			CPEGTerm<LLVMLabel, LLVMParameter> opTerm = opFunction.get(structure);
//			if (opTerm != null) {
//				if (!opTerm.getOp().isDomain())
//					return false;
//				if (!distributesThroughSelect(
//						opTerm.getOp().getDomain(),
//						selectIndex))
//					return false;
//			}
//			return true;
//		}
//	}
//
//	/**
//	 * select(C,op(A1,A2,...,T,Ai,....An),op(A1,A2,...,F,Ai,...An))
//	 * op(A1,A2,...,select(C,T,F),Ai,...,An) =
//	 */
//	private void addFactorOutSelect(
//			int opArity, int selectIndex) {
//		{
//			PeggyAxiomizer<LLVMLabel, Integer> axiomizer = 
//				new PeggyAxiomizer<LLVMLabel, Integer>(getNetwork(), getAmbassador());
//			PeggyVertex<LLVMLabel, Integer> C = axiomizer.getVariable(1);
//			PeggyVertex<LLVMLabel, Integer> T = axiomizer.getVariable(2);
//			PeggyVertex<LLVMLabel, Integer> F = axiomizer.getVariable(3);
//			int next = 4;
//
//			List<PeggyVertex<LLVMLabel, Integer>> opArgs1 = 
//				new ArrayList<PeggyVertex<LLVMLabel, Integer>>(opArity);
//			List<PeggyVertex<LLVMLabel, Integer>> opArgs2 = 
//				new ArrayList<PeggyVertex<LLVMLabel, Integer>>(opArity);
//			for (int i = 0; i < opArity; i++) {
//				if (i == selectIndex) {
//					opArgs1.add(T);
//					opArgs2.add(F);
//				} else {
//					PeggyVertex<LLVMLabel, Integer> var = 
//						axiomizer.getVariable(next++);
//					opArgs1.add(var);
//					opArgs2.add(var);
//				}
//			}
//			
//			PeggyVertex<LLVMLabel, Integer> op1 = axiomizer.get(null, opArgs1);
//			PeggyVertex<LLVMLabel, Integer> op2 = axiomizer.get(null, opArgs2);
//			PeggyVertex<LLVMLabel, Integer> select = axiomizer.get(
//					SimpleLLVMLabel.get(LLVMOperator.SELECT), C, op1, op2);
//			axiomizer.mustExist(select);
//
//			PEGNode<LLVMLabel> trigger = axiomizer.getTrigger();
//			Event<? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> triggerEvent = 
//				getEngine().getEGraph().processPEGNode(trigger);
//			Structurizer<PeggyVertex<LLVMLabel, Integer>> structurizer = 
//				axiomizer.getStructurizer();
//
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> selectFunction = 
//				getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(select));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> opFunction1 = 
//				getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(op1));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> opFunction2 = 
//				getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(op2));
//
//			List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> opVarFunctions = 
//				new ArrayList<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>>();
//			for (int i = 0; i < opArity; i++) {
//				if (i != selectIndex) {
//					opVarFunctions.add(getEngine().getEGraph().processValueNode(
//							structurizer.getValue(opArgs1.get(i))));
//				}
//			}
//
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> cFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(C));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> tFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(T));
//			Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> fFunction = 
//				getEngine().getEGraph().processValueNode(structurizer.getValue(F));
//
//			SelectFactorListener listener = 
//				new SelectFactorListener(
//						selectIndex,
//						opFunction1,
//						opFunction2,
//						selectFunction,
//						opVarFunctions,
//						cFunction,
//						tFunction,
//						fFunction);
//			StringBuilder strlhs = new StringBuilder();
//			StringBuilder strrhs1 = new StringBuilder();
//			StringBuilder strrhs2 = new StringBuilder();
//			strlhs.append("op(");
//			strrhs1.append("op(");
//			strrhs2.append("op(");
//			for (int i = 0; i < opArity; i++) {
//				if (i>0) {
//					strlhs.append(",");
//					strrhs1.append(",");
//					strrhs2.append(",");
//				}
//				if (i==selectIndex) {
//					strlhs.append("select(C,T,F)");
//					strrhs1.append("T");
//					strrhs2.append("F");
//				} else {
//					String var = "A" + i;
//					strlhs.append(var);
//					strrhs1.append(var);
//					strrhs2.append(var);
//				}
//			}
//			strlhs.append(")");
//			strrhs1.append(")");
//			strrhs2.append(")");
//			listener.addListener(new PrintListener("select(C," + strrhs1 + "," + strrhs2 + ") = " + strlhs));
//			triggerEvent.addListener(listener);
//		}
//	}
//	
//	
//	class SelectFactorListener 
//	extends AbstractChainEvent<Structure<CPEGTerm<LLVMLabel, LLVMParameter>>,Void> {
//		final private int selectIndex;
//		final private Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> opFunction1, opFunction2, selectFunction;
//		final private List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> opVarFunctions;
//		final private Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> cFunction,
//				tFunction, fFunction;
//
//		SelectFactorListener(
//				int _selectIndex,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> _opFunction1,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> _opFunction2,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends CPEGTerm<LLVMLabel, LLVMParameter>> _selectFunction,
//				List<Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>>> _opVarFunctions,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _cFunction,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _tFunction,
//				Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> _fFunction) {
//			this.selectIndex = _selectIndex;
//			this.opFunction1 = _opFunction1;
//			this.opFunction2 = _opFunction2;
//			this.selectFunction = _selectFunction;
//			this.opVarFunctions = _opVarFunctions;
//			this.cFunction = _cFunction;
//			this.tFunction = _tFunction;
//			this.fFunction = _fFunction;
//		}
//
//		public boolean notify(
//				Structure<CPEGTerm<LLVMLabel, LLVMParameter>> structure) {
//			if (!this.canUse(structure))
//				return true;
//
//			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
//				new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>>();
//
//			CPEGTerm<LLVMLabel, LLVMParameter> selectTerm = selectFunction.get(structure);
//			CPEGTerm<LLVMLabel, LLVMParameter> opTerm1 = opFunction1.get(structure);
//			LLVMLabel opLabel = opTerm1.getOp().getDomain();
//
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> cVertex = 
//				futureGraph.getVertex(cFunction.get(structure));
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> tVertex = 
//				futureGraph.getVertex(tFunction.get(structure));
//			FutureExpressionGraph.Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> fVertex = 
//				futureGraph.getVertex(fFunction.get(structure));
//			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> select = 
//				futureGraph.getExpression(
//						getDomain(SimpleLLVMLabel.get(LLVMOperator.SELECT)),
//						cVertex,tVertex,fVertex);
//
//			Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> list[] = 
//				new Vertex[opVarFunctions.size()+1];
//
//			int counter = 0;
//			for (Function<? super Structure<CPEGTerm<LLVMLabel, LLVMParameter>>, ? extends Representative<CPEGValue<LLVMLabel, LLVMParameter>>> func : opVarFunctions) {
//				Vertex<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> vertex = 
//					futureGraph.getVertex(func.get(structure));
//				if (counter == selectIndex) {
//					list[counter] = select;
//					counter++;
//				}
//				list[counter] = vertex;
//				counter++;
//			}
//			if (counter == selectIndex) {
//				list[counter] = select;
//				counter++;
//			}
//
//			FlowValue<LLVMParameter, LLVMLabel> opFlow = getDomain(opLabel);
//
//			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGValue<LLVMLabel, LLVMParameter>> result = 
//				futureGraph.getExpression(opFlow, list);
//
//			result.setValue(selectTerm);
//
//			getEngine().getEGraph().addExpressions(futureGraph);
//			
//			debug("triggered factor select");
//			
//			trigger(null);
//			return true;
//		}
//
//		public boolean canUse(
//				Structure<CPEGTerm<LLVMLabel, LLVMParameter>> structure) {
//			// check basetype term
//			CPEGTerm<LLVMLabel, LLVMParameter> opTerm1 = opFunction1.get(structure);
//			CPEGTerm<LLVMLabel, LLVMParameter> opTerm2 = opFunction2.get(structure);
//			if (opTerm1 != null) {
//				if (!opTerm1.getOp().isDomain())
//					return false;
//				if (!distributesThroughSelect(
//						opTerm1.getOp().getDomain(),
//						selectIndex))
//					return false;
//			}
//			if (opTerm2 != null) {
//				if (!opTerm2.getOp().isDomain())
//					return false;
//				if (opTerm1 != null) {
//					if (!opTerm1.getOp().getDomain().equalsLabel(opTerm2.getOp().getDomain()))
//						return false;
//				}
//			}
//			
//			return true;
//		}
//	}
//	
//	
//	private static boolean distributesThroughSelect(
//			LLVMLabel label, int selectIndex) {
//		if (label.isBinop())
//			return true;
//		else if (label.isCmp())
//			return true;
//		else if (label.isCast())
//			return selectIndex==1;
//		else if (label.isSimple()) {
//			switch (label.getSimpleSelf().getOperator()) {
//			case SELECT: return true; 
//			case SHUFFLEVECTOR: return true;
//			case INSERTELEMENT: return true;
//			case GETELEMENTPTR: return selectIndex==0;
//			case EXTRACTELEMENT: return true;
//			case MALLOC: return selectIndex==2;
//			case ALLOCA: return selectIndex==2;
//			case LOAD: 
//				return selectIndex==1;
//			
//			default: return false;
//			}
//		}
//		else
//			return false;
//	}
}
