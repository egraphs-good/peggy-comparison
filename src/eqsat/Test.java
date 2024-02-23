package eqsat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.UnhandledCaseException;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;
import eqsat.revert.RevertCFG;

public final class Test {
	private static final OpAmbassador<Object> mAmbassador
			= new AbstractOpAmbassador<Object>() {
		public Object getBasicOp(BasicOp op) {
			switch (op) {
			case True:
				return true;
			case False:
				return false;
			case Negate:
				return "!";
			case And:
				return "&";
			case Or:
				return "|";
			case Equals:
				return "=";
			default:
				throw new UnhandledCaseException();
			}
		}
		
		public boolean canPreEvaluate(Object op) {return true;}
		public boolean isFree(Object op) {return false;}
		public boolean needsAnyChild(Object op) {return false;}
		public boolean needsChild(Object op, int child) {return false;}

		public BasicOp getBasicOp(Object op) {
			if (op instanceof Boolean)
				return (Boolean)op ? BasicOp.True : BasicOp.False;
			if (op instanceof String && ((String)op).length() == 1) {
				switch (((String)op).charAt(0)) {
				case '!':
					return BasicOp.Negate;
				case '&':
					return BasicOp.And;
				case '|':
					return BasicOp.Or;
				case '=':
					return BasicOp.Equals;
				}
			}
			return null;
		}

		public boolean isAnyVolatile(Object op) {return false;}
		public boolean isVolatile(Object op, int child) {return false;}
		public Object getChainVersion(Object op, int child) {
			throw new IllegalArgumentException();
		}
		public Object getChainProjectValue(Object op, int child) {
			throw new IllegalArgumentException();
		}
		public Object getChainProjectVolatile(Object op, int child) {
			throw new IllegalArgumentException();
		}
		public boolean isEquivalent(OpExpression<Object> first, int firstChild,
				OpExpression<Object> second, int secondChild) {
			throw new IllegalArgumentException();
		}
		
		public Object get(Object op, List<? extends Object> operands) {
			return null;
		}
	};
	
	private static final class CFG extends GenericCFG<CFG,Block,String,Object> {
		public CFG(int size) {super(size);}
		
		public CFG getSelf() {return this;}
		
		protected Test.Block[] makeBlockArray(int size) {
			return new Test.Block[size];
		}
		
		protected Test.Block makeBlock(int index) {return new EndBlock(index);}
		protected Test.Block makeBlock(int index, int child) {
			return new FallBlock(index, child);
		}
		protected Test.Block makeBlock(int index, int trueChild, int falseChild)
				{
			return new BranchBlock(index, trueChild, falseChild);
		}
		
		protected class EndBlock
				extends GenericCFG<CFG,Test.Block,String,Object>.EndBlock
				implements Test.Block {
			public EndBlock(int index) {super(index);}
			public CFG getGraph() {return CFG.this;}
			public Test.Block getSelf() {return this;}
		}
		
		protected class FallBlock
				extends GenericCFG<CFG,Test.Block,String,Object>.FallBlock
				implements Test.Block {
			public FallBlock(int index, int child) {super(index, child);}
			public CFG getGraph() {return CFG.this;}
			public Test.Block getSelf() {return this;}
		}
		
		protected class BranchBlock
				extends GenericCFG<CFG,Test.Block,String,Object>.BranchBlock
				implements Test.Block {		
			public BranchBlock(int index, int trueChild, int falseChild) {
				super(index, trueChild, falseChild);
			}
			public CFG getGraph() {return CFG.this;}
			public Test.Block getSelf() {return this;}
		}

		public OpAmbassador<Object> getOpAmbassador() {return mAmbassador;}
	}
	
	private interface Block extends GenericBlock<CFG,Block,String,Object> {}
	
	public static void main(String[] args) {testNewPrint();}
	
	public static void testNewPrint() {
		CRecursiveExpressionGraph<FlowValue<Object,Object>> peg
				= new CRecursiveExpressionGraph<FlowValue<Object,Object>>();
		FlowValue<Object,Object> zero
				= FlowValue.<Object,Object>createDomain(0, mAmbassador);
		FlowValue<Object,Object> plus
				= FlowValue.<Object,Object>createDomain("+", mAmbassador);
		Vertex<FlowValue<Object,Object>> loop = peg.createPlaceHolder();
		loop.replaceWith(peg.getVertex(FlowValue.createTheta(1),
				peg.getVertex(zero),
				peg.getVertex(plus,
				peg.getVertex(plus, peg.getVertex(zero), peg.getVertex(zero)),
				loop)));
		System.out.println(peg);
	}
	
	public static void testLoopUnrolling() {
		CRecursiveExpressionGraph<FlowValue<Object,Object>> peg
				= new CRecursiveExpressionGraph<FlowValue<Object,Object>>();
		FlowValue<Object,Object> zero
				= FlowValue.<Object,Object>createDomain(0, mAmbassador);
		FlowValue<Object,Object> one
				= FlowValue.<Object,Object>createDomain(1, mAmbassador);
		FlowValue<Object,Object> ten
				= FlowValue.<Object,Object>createDomain(10, mAmbassador);
		FlowValue<Object,Object> plus
				= FlowValue.<Object,Object>createDomain("+", mAmbassador);
		FlowValue<Object,Object> ge
				= FlowValue.<Object,Object>createDomain(">=", mAmbassador);
		FlowValue<Object,Object> x
				= FlowValue.<Object,Object>createParameter("x");
		Vertex<FlowValue<Object,Object>> loop = peg.createPlaceHolder();
		loop.replaceWith(peg.getVertex(FlowValue.createTheta(1),
				peg.getVertex(zero),
				peg.getVertex(plus,
				peg.getVertex(plus, peg.getVertex(one), peg.getVertex(x)),
				loop)));
		Map<String,Vertex<FlowValue<Object,Object>>> outputs = new HashMap();
		outputs.put("r", peg.getVertex(FlowValue.createEval(1), loop,
				peg.getVertex(FlowValue.createPass(1),
				peg.getVertex(ge, loop, peg.getVertex(ten)))));
		Map<String,ReversionGraph<Object,Object>.Vertex> returns
				= new HashMap();
		ReversionGraph<Object,Object> result = new ReversionGraph(mAmbassador,
				peg, outputs, returns);
		RevertCFG<Object,Object,String> revert
				= new CFGReverter(result, returns, mAmbassador).getCFG();
		System.out.println(revert);
	}
	
	public static void testLoopHangers() {
		CRecursiveExpressionGraph<FlowValue<Object,Object>> peg
				= new CRecursiveExpressionGraph<FlowValue<Object,Object>>();
		FlowValue<Object,Object> zero
				= FlowValue.<Object,Object>createDomain(0, mAmbassador);
		FlowValue<Object,Object> one
				= FlowValue.<Object,Object>createDomain(1, mAmbassador);
		FlowValue<Object,Object> ten
				= FlowValue.<Object,Object>createDomain(10, mAmbassador);
		FlowValue<Object,Object> plus
				= FlowValue.<Object,Object>createDomain("+", mAmbassador);
		FlowValue<Object,Object> ge
				= FlowValue.<Object,Object>createDomain(">=", mAmbassador);
		FlowValue<Object,Object> x
				= FlowValue.<Object,Object>createParameter("x");
		Vertex<FlowValue<Object,Object>> loop = peg.createPlaceHolder();
		loop.replaceWith(peg.getVertex(FlowValue.createTheta(1),
				peg.getVertex(zero),
				peg.getVertex(plus,
				peg.getVertex(FlowValue.createTheta(1),
						peg.getVertex(x), peg.getVertex(one)),
				loop)));
		Map<String,Vertex<FlowValue<Object,Object>>> outputs = new HashMap();
		outputs.put("r", peg.getVertex(FlowValue.createEval(1), loop,
				peg.getVertex(FlowValue.createPass(1),
				peg.getVertex(ge, loop, peg.getVertex(ten)))));
		Map<String,ReversionGraph<Object,Object>.Vertex> returns
				= new HashMap();
		ReversionGraph<Object,Object> result = new ReversionGraph(mAmbassador,
				peg, outputs, returns);
		RevertCFG<Object,Object,String> revert
				= new CFGReverter(result, returns, mAmbassador).getCFG();
		System.out.println(revert);
	}
	
	public static void testConstantLoopRevert() {
		CFG cfg = new CFG(4);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 1, 2);
		cfg.setBlock(2, 3);
		cfg.addVariable("r");
		Block block = cfg.getBlock(0);
		block = cfg.getBlock(1);
		block.setModification("r", cfg.getValue("+", cfg.getValue(1),
				block.getInput("r")));
		block.setBranchCondition(cfg.getValue("<=", cfg.getValue(0),
				cfg.getValue(5)));
		block = cfg.getBlock(2);
		block.setModification("r", cfg.getValue("+", cfg.getValue(-1),
				block.getInput("r")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		System.out.println(flow);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap();
		ReversionGraph<String,Object> result = new ReversionGraph(mAmbassador,
				apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert
				= new CFGReverter(result, returns, mAmbassador).getCFG();
		System.out.println(revert);
		Flow flowCheck = new Flow(revert, true);
		APEG apegCheck = new APEG(revert);
		flowCheck.getReturn(apegCheck);
		Map outputsCheck = new HashMap();
		for (String ret : revert.getReturns())
			outputsCheck.put(ret, apegCheck.getReturn().evaluate(
					revert.getReturnVariable(ret)));
		ReversionGraph<String,Object> simpler
				= new ReversionGraph(mAmbassador, apegCheck.getValues(),
				outputsCheck, new HashMap());
		CFGReverter.rewrite(simpler);
		System.out.println(simpler);
	}
	
	public static void testDoubleLoopRevert() {
		CFG cfg = new CFG(10);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 2, 9);
		cfg.setBlock(2, 3);
		cfg.setBlock(3, 4, 8);
		cfg.setBlock(4, 5, 6);
		cfg.setBlock(5, 7);
		cfg.setBlock(6, 7);
		cfg.setBlock(7, 3);
		cfg.setBlock(8, 1);
		cfg.addVariable("n");
		cfg.addVariable("M");
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.addVariable("r");
		Block block = cfg.getBlock(0);
		block.setModification("j", cfg.getValue(0));
		block = cfg.getBlock(1);
		block.setBranchCondition(cfg.getValue("<",
				block.getOutput("j"), block.getOutput("n")));
		block = cfg.getBlock(2);
		block.setModification("i", cfg.getValue(0));
		block = cfg.getBlock(3);
		block.setBranchCondition(cfg.getValue("<",
				block.getOutput("i"), block.getOutput("n")));
		block = cfg.getBlock(4);
		block.setBranchCondition(cfg.getValue("==",
				cfg.getValue("%", cfg.getValue("/", cfg.getValue("+",
				block.getInput("i"),
				cfg.getValue(1)), cfg.getValue(2)), cfg.getValue(2)),
				cfg.getValue("%", cfg.getValue("/", cfg.getValue("+",
				block.getInput("j"),
				cfg.getValue(1)), cfg.getValue(2)), cfg.getValue(2))));
		block = cfg.getBlock(5);
		block.setModification("M", cfg.getValue("[][]=",
				block.getInput("M"), block.getInput("i"), block.getInput("j"),
				cfg.getValue("-",
				cfg.getValue("*", block.getInput("n"), block.getInput("n")),
				cfg.getValue("-",
				cfg.getValue("*", block.getInput("n"), block.getInput("i")),
				block.getInput("j")))));
		block = cfg.getBlock(6);
		block.setModification("M", cfg.getValue("[][]=",
				block.getInput("M"), block.getInput("i"), block.getInput("j"),
				cfg.getValue("+",
				cfg.getValue("*", block.getInput("n"), block.getInput("i")),
				cfg.getValue("+", block.getInput("j"), cfg.getValue(1)))));
		block = cfg.getBlock(7);
		block.setModification("i", cfg.getValue("+",
				block.getInput("i"), cfg.getValue(1)));
		block = cfg.getBlock(8);
		block.setModification("j", cfg.getValue("+",
				block.getInput("j"), cfg.getValue(1)));
		block = cfg.getBlock(9);
		block.setModification("r", cfg.getValue("new Matrix",
				block.getInput("M")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret, apeg.getReturn().evaluateSignificant(
					cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap();
		ReversionGraph<String,Object> result = new ReversionGraph(mAmbassador,
				apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert
				= new CFGReverter(result, returns, mAmbassador).getCFG();
		System.out.println(revert);
		Flow flowCheck = new Flow(revert, true);
		APEG apegCheck = new APEG(revert);
		flowCheck.getReturn(apegCheck);
		Map outputsCheck = new HashMap();
		for (String ret : revert.getReturns())
			outputsCheck.put(ret, apegCheck.getReturn().evaluateSignificant(
					revert.getReturnVariable(ret)));
		ReversionGraph<String,Object> simpler
				= new ReversionGraph(mAmbassador, apegCheck.getValues(),
				outputsCheck, new HashMap());
		CFGReverter.rewrite(simpler);
		System.out.println(simpler.getSignificant());
		System.out.println(simpler);
	}
	
	public static void testCatchException() {
		CFG cfg = new CFG(5);
		cfg.setBlock(0, 2, 1);
		cfg.setBlock(1, 2, 3);
		cfg.setBlock(2, 4);
		cfg.setBlock(3, 4);
		cfg.addVariable("i");
		cfg.addVariable("r");
		cfg.getBlock(0).setBranchCondition(cfg.getValue(">",
				cfg.getBlock(0).getOutput("i"), cfg.getValue(0)));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("==",
				cfg.getBlock(0).getOutput("i"), cfg.getValue(0)));
		cfg.getBlock(2).setModification("r", cfg.getBlock(0).getInput("i"));
		cfg.getBlock(3).setModification("r",
				cfg.getValue("-", cfg.getBlock(0).getInput("i")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
	}
	
	public static void testSimplifiedInputs() {
		CFG cfg = new CFG(7);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 2, 1);
		cfg.setBlock(2, 3, 5);
		cfg.setBlock(3, 4, 6);
		cfg.setBlock(4, 4, 3);
		cfg.setBlock(5, 6, 2);
		cfg.addVariable("i");
		for (int i = 0; i < 7; i++)
			cfg.getBlock(i).setModification("i",
					cfg.getValue(Integer.toString(i),
					cfg.getBlock(i).getInput("i")));
		for (int i = 0; i < 7; i++)
			if (cfg.getBlock(i).getChildCount() == 2)
				cfg.getBlock(i).setBranchCondition(
						cfg.getValue(i + "c", cfg.getBlock(i).getOutput("i")));
		cfg.addReturn("i");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
	}

	public static void testLoopFusion() {
		CFG cfg = new CFG(5);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 1, 2);
		cfg.setBlock(2, 3);
		cfg.setBlock(3, 3, 4);
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.getBlock(0).setModification("i", cfg.getValue(0));
		cfg.getBlock(1).setModification("i", cfg.getValue("+",
				cfg.getBlock(1).getInput("i"), cfg.getValue(1)));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("<=",
				cfg.getBlock(1).getOutput("i"), cfg.getValue(10)));
		cfg.getBlock(2).setModification("j", cfg.getValue(0));
		cfg.getBlock(3).setModification("j", cfg.getValue("+",
				cfg.getBlock(3).getInput("j"), cfg.getValue(1)));
		cfg.getBlock(3).setBranchCondition(cfg.getValue("<=",
				cfg.getBlock(3).getOutput("j"), cfg.getValue(10)));
		cfg.getBlock(4).setModification("r", cfg.getValue("*",
				cfg.getBlock(4).getInput("j"), cfg.getBlock(4).getInput("i")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		System.out.println(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
	}

	public static void testLoopFusionRevert() {
		CFG cfg = new CFG(5);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 1, 2);
		cfg.setBlock(2, 3);
		cfg.setBlock(3, 3, 4);
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.getBlock(0).setModification("i", cfg.getValue(0));
		cfg.getBlock(1).setModification("i", cfg.getValue("+",
				cfg.getBlock(1).getInput("i"), cfg.getValue(1)));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("<=",
				cfg.getBlock(1).getOutput("i"), cfg.getValue(10)));
		cfg.getBlock(2).setModification("j", cfg.getValue(0));
		cfg.getBlock(3).setModification("j", cfg.getValue("+",
				cfg.getBlock(3).getInput("j"), cfg.getValue(1)));
		cfg.getBlock(3).setBranchCondition(cfg.getValue("<=",
				cfg.getBlock(3).getOutput("j"), cfg.getValue(10)));
		cfg.getBlock(4).setModification("r", cfg.getValue("*",
				cfg.getBlock(4).getInput("j"), cfg.getBlock(4).getInput("i")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret, apeg.getReturn().evaluateSignificant(
					cfg.getReturnVariable(ret)));
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap<String,ReversionGraph<String,Object>.Vertex>();
		System.out.println(apeg.getValues());
		ReversionGraph<String,Object> result
				= new ReversionGraph<String,Object>(mAmbassador,
						apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert = new
			CFGReverter<String,Object,String>(result, returns, mAmbassador)
			.getCFG();
		System.out.println(revert);
	}

	public static void testLoopWithBreakRevert() {
		CFG cfg = new CFG(4);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 2, 3);
		cfg.setBlock(2, 3, 1);
		cfg.addVariable("i");
		cfg.getBlock(0).setModification("i", cfg.getValue(0));
		cfg.getBlock(1).setModification("i", cfg.getValue("+",
				cfg.getBlock(1).getInput("i"), cfg.getValue(1)));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("<=",
				cfg.getBlock(1).getOutput("i"), cfg.getValue(10)));
		cfg.getBlock(2).setBranchCondition(cfg.getValue(">=",
				cfg.getBlock(2).getOutput("i"), cfg.getValue(5)));
		cfg.getBlock(3).setModification("r", cfg.getValue("*",
				cfg.getBlock(3).getInput("i"), cfg.getBlock(3).getInput("i")));
		cfg.addReturn("r");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret, apeg.getReturn().evaluateSignificant(
					cfg.getReturnVariable(ret)));
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap<String,ReversionGraph<String,Object>.Vertex>();
		System.out.println(apeg.getValues());
		ReversionGraph<String,Object> result
				= new ReversionGraph<String,Object>(mAmbassador,
						apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert = new
			CFGReverter<String,Object,String>(result, returns, mAmbassador)
			.getCFG();
		System.out.println(revert);
	}
	
	public static void testSimpleRevert() {
		CFG cfg = new CFG(1);
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.addVariable("a");
		cfg.getBlock(0).setModification("a", cfg.getValue("[]=",
				cfg.getBlock(0).getInput("a"), cfg.getBlock(0).getInput("i"),
				cfg.getBlock(0).getInput("j")));
		cfg.addReturn("a");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap<String,ReversionGraph<String,Object>.Vertex>();
		ReversionGraph<String,Object> result
				= new ReversionGraph<String,Object>(mAmbassador,
						apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert = new
			CFGReverter<String,Object,String>(result, returns, mAmbassador)
			.getCFG();
		System.out.println(revert);
	}
	
	public static void testSemiDoubleBreakReversion() {
		CFG cfg = new CFG(9);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 2, 8);
		cfg.setBlock(2, 3);
		cfg.setBlock(3, 5, 4);
		cfg.setBlock(4, 1);
		cfg.setBlock(5, 6, 7);
//		cfg.setBlock(6, 8, 7);
		cfg.setBlock(6, 8);
		cfg.setBlock(7, 3);
		//System.out.println(cfg);
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.addVariable("a");
		cfg.addVariable("break");
		cfg.getBlock(0).setModification("i", cfg.getValue(0));
		cfg.getBlock(0).setModification("j", cfg.getValue(0));
		cfg.getBlock(0).setModification("break", cfg.getValue("false"));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("&&", cfg.getValue("!",
				cfg.getBlock(1).getOutput("break")), cfg.getValue("<",
				cfg.getValue("*", cfg.getBlock(1).getOutput("i"),
				cfg.getBlock(1).getOutput("j")), cfg.getValue(100))));
		cfg.getBlock(2).setModification("j", cfg.getValue(0));
		cfg.getBlock(3).setBranchCondition(cfg.getValue("<",
				cfg.getBlock(3).getInput("j"), cfg.getValue(10)));
		cfg.getBlock(4).setModification("i", cfg.getValue("+",
				cfg.getBlock(4).getInput("i"), cfg.getValue(1)));
		cfg.getBlock(5).setModification("a", cfg.getValue("map",
				cfg.getBlock(5).getInput("a"), cfg.getValue("pair",
						cfg.getBlock(5).getInput("i"),
						cfg.getBlock(5).getInput("j")),
				cfg.getValue("*", cfg.getBlock(5).getInput("i"),
						cfg.getBlock(5).getInput("j"))));
		cfg.getBlock(5).setBranchCondition(cfg.getValue("==",
				cfg.getValue("*", cfg.getBlock(5).getOutput("i"),
						cfg.getBlock(5).getOutput("j")), cfg.getValue(100)));
		cfg.getBlock(6).setModification("break", cfg.getValue("true"));
		cfg.getBlock(7).setModification("j", cfg.getValue("+",
				cfg.getBlock(7).getInput("j"), cfg.getValue(1)));
		cfg.addReturn("a");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg, true);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
		Map<String,ReversionGraph<String,Object>.Vertex> returns
				= new HashMap();
		ReversionGraph<String,Object> result = new ReversionGraph(mAmbassador,
				apeg.getValues(), outputs, returns);
		RevertCFG<Object,String,String> revert
				= new CFGReverter(result, returns, mAmbassador).getCFG();
		System.out.println(revert);
		Flow flowCheck = new Flow(revert, true);
		APEG apegCheck = new APEG(revert);
		flowCheck.getReturn(apegCheck);
		Map outputsCheck = new HashMap();
		for (String ret : revert.getReturns())
			outputsCheck.put(ret, apegCheck.getReturn().evaluate(
					revert.getReturnVariable(ret)));
		ReversionGraph<String,Object> simpler
				= new ReversionGraph(mAmbassador, apegCheck.getValues(),
				outputsCheck, new HashMap());
		CFGReverter.rewrite(simpler);
		System.out.println(simpler);
	}
	
	public static void testDoubleBreak() {
		CFG cfg = new CFG(9);
		cfg.setBlock(0, 1);
		cfg.setBlock(1, 2, 8);
		cfg.setBlock(2, 3);
		cfg.setBlock(3, 5, 4);
		cfg.setBlock(4, 1);
		cfg.setBlock(5, 6);
		cfg.setBlock(6, 8, 7);
		cfg.setBlock(7, 3);
		//System.out.println(cfg);
		cfg.addVariable("i");
		cfg.addVariable("j");
		cfg.addVariable("a");
		cfg.getBlock(0).setModification("i", cfg.getValue(0));
		cfg.getBlock(1).setBranchCondition(cfg.getValue("<",
				cfg.getBlock(1).getOutput("i"), cfg.getValue(10)));
		cfg.getBlock(2).setModification("j", cfg.getValue(0));
		cfg.getBlock(3).setBranchCondition(cfg.getValue("<",
				cfg.getBlock(3).getInput("j"), cfg.getValue(10)));
		cfg.getBlock(4).setModification("i", cfg.getValue("+",
				cfg.getBlock(4).getInput("i"), cfg.getValue(1)));
		cfg.getBlock(5).setModification("a", cfg.getValue("map",
				cfg.getBlock(5).getInput("a"), cfg.getValue("pair",
						cfg.getBlock(5).getInput("i"),
						cfg.getBlock(5).getInput("j")),
				cfg.getValue("*", cfg.getBlock(5).getInput("i"),
						cfg.getBlock(5).getInput("j"))));
		cfg.getBlock(6).setBranchCondition(cfg.getValue("==",
				cfg.getValue("*", cfg.getBlock(6).getInput("i"),
						cfg.getBlock(6).getInput("j")), cfg.getValue(100)));
		cfg.getBlock(7).setModification("j", cfg.getValue("+",
				cfg.getBlock(7).getInput("j"), cfg.getValue(1)));
		cfg.addReturn("a");
		Flow<CFG,Block,String> flow = new Flow<CFG,Block,String>(cfg);
		APEG<CFG,Block,String,Object,String> apeg
				= new APEG<CFG,Block,String,Object,String>(cfg);
		flow.getReturn(apeg);
		Map<String,Vertex<FlowValue<String,Object>>> outputs = new HashMap();
		for (String ret : cfg.getReturns())
			outputs.put(ret,
					apeg.getReturn().evaluate(cfg.getReturnVariable(ret)));
		System.out.println(apeg.getValues());
	}
	
	//Can't do cuz parameters are always invariant now
	public static void testInfiniteNonConstantInduction() {
		/*RuleNetwork<String> network = new RuleNetwork();
		CRecursiveExpressionGraph<FlowValue<Integer,String>> model
				= new CRecursiveExpressionGraph<FlowValue<Integer,String>>();
		Vertex<FlowValue<Integer,String>> f = model.getVertex(
				FlowValue.<Integer,String>createDomain("False", mAmbassador));
		FlowValue<Integer,String> and
				= FlowValue.createDomain("&", mAmbassador);
		FlowValue<Integer,String> theta
				= FlowValue.createDomain("Theta", mAmbassador);
		FlowValue<Integer,String> shift
				= FlowValue.createDomain("Shift", mAmbassador);
		Vertex<FlowValue<Integer,String>> a = model.getVertex(
				FlowValue.<Integer,String>createParameter(0));
		Vertex<FlowValue<Integer,String>> b = model.getVertex(
				FlowValue.<Integer,String>createParameter(1));
		Vertex<FlowValue<Integer,String>> c = model.getVertex(
				FlowValue.<Integer,String>createParameter(2));
		Vertex<FlowValue<Integer,String>> original = model.getVertex(shift, f);
		Vertex<FlowValue<Integer,String>> transformed = f;
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and,
						model.getVertex(shift, a), model.getVertex(shift, b));
		transformed = model.getVertex(shift, model.getVertex(and, a, b));
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, model.getVertex(theta, a, b), c);
		transformed = model.getVertex(theta, model.getVertex(and, a, c),
				model.getVertex(and, b, c));
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, f, a);
		transformed = f;
		TransformRule.getTransformRule(network, original, transformed);
		original = model.getVertex(and, model.getVertex(and, a, b), b);
		transformed = model.getVertex(and, a, b);
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, model.getVertex(and, a, b), c);
		transformed = model.getVertex(and, a, model.getVertex(and, b, c));
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, a, a);
		transformed = a;
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
		original = model.createPlaceHolder();
		original.replaceWith(model.getVertex(
				theta, a, model.getVertex(shift, original)));
		transformed = a;
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
		CRecursiveExpressionGraph<FlowValue<String,String>> graph
				= new CRecursiveExpressionGraph<FlowValue<String,String>>();
		Vertex<FlowValue<String,String>> F = graph.getVertex(
				FlowValue.<String,String>createDomain("False", mAmbassador));
		FlowValue<String,String> And = FlowValue.createDomain("&", mAmbassador);
		FlowValue<String,String> Theta
				= FlowValue.createDomain("Theta", mAmbassador);
		FlowValue<String,String> Shift
				= FlowValue.createDomain("Shift", mAmbassador);
		Vertex<FlowValue<String,String>> C = graph.getVertex(
				FlowValue.<String,String>createParameter("c"));
		Vertex<FlowValue<String,String>> loop = graph.createPlaceHolder();
		loop.replaceWith(graph.getVertex(Theta, F,
				graph.getVertex(Shift, graph.getVertex(And, loop, C))));
		loop.makeSignificant();
		Engine<String,String> engine
				= new Engine<String,String>(network, graph);
		engine.run(100);
		System.out.println(engine.toClusteredValueString(false));*/
	}
	
	//Can't do cuz parameters are always invariant now
	public static void testInfiniteNonConstantUnInduction() {
		/*RuleNetwork<Object> network = new RuleNetwork();
		CRecursiveExpressionGraph<Object> model
				= new CRecursiveExpressionGraph<Object>();
		Vertex<Object> f = model.getVertex("False");
		Vertex<Object> a = model.getVertex(
				FlowValue.<Integer,String>createParameter(0));
		Vertex<Object> b = model.getVertex(
				FlowValue.<Integer,String>createParameter(1));
		Vertex<Object> c = model.getVertex(
				FlowValue.<Integer,String>createParameter(2));
		Vertex<Object> original
				= model.getVertex(shift, f);
		Vertex<Object> transformed = f;
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.createPlaceHolder();
		original.replaceWith(model.getVertex(theta, a, original));
		transformed = a;
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and,
						model.getVertex(shift, a),
						model.getVertex(shift, b));
		transformed = model.getVertex(shift, model.getVertex(and, a, b));
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, model.getVertex(theta, a, b), c);
		transformed = model.getVertex(theta, model.getVertex(and, a, c),
				model.getVertex(and, b, model.getVertex(shift, c)));
		TransformRule.getTransformRule(network, original, transformed);
		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(and, f, a);
		transformed = f;
		TransformRule.getTransformRule(network, original, transformed);
		original = model.getVertex(and, model.getVertex(and, a, b), b);
		transformed = model.getVertex(and, a, b);
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
//		original = model.getVertex(and, model.getVertex(and, a, b), c);
//		transformed = model.getVertex(and, a, model.getVertex(and, b, c));
//		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
//		original = model.getVertex(and, a, a);
		transformed = a;
		TransformRule.getTransformRule(network, original, transformed);
//		TransformRule.getTransformRule(network, transformed, original);
		original = model.getVertex(shift, model.getVertex(theta, a, b));
		transformed = b;
		TransformRule.getTransformRule(network, original, transformed);
		CRecursiveExpressionGraph<FlowValue<String,String>> graph
				= new CRecursiveExpressionGraph<FlowValue<String,String>>();
		Vertex<FlowValue<String,String>> F = graph.getVertex(
				FlowValue.<String,String>createDomain("False", mAmbassador));
		FlowValue<String,String> And = FlowValue.createDomain("&", mAmbassador);
		FlowValue<String,String> Theta
				= FlowValue.createDomain("Theta", mAmbassador);
//		FlowValue<String,String> Shift = FlowValue.createDomain("Shift");
		Vertex<FlowValue<String,String>> C = graph.getVertex(
				FlowValue.<String,String>createParameter("c"));
		Vertex<FlowValue<String,String>> loop = graph.createPlaceHolder();
		loop.replaceWith(graph.getVertex(Theta, F,
				graph.getVertex(And, loop, C)));
		loop.makeSignificant();
		Engine<String,String> engine
				= new Engine<String,String>(network, graph);
		engine.run(100);
		System.out.println(engine.toClusteredValueString(false));*/
	}
	
	public static void testSticky() {
		ReversionGraph<String,String> graph = new ReversionGraph(mAmbassador);
		ReversionGraph<String,String>.Vertex sigma = graph.getVertex(
				FlowValue.<String,String>createParameter("SIGMA"));
		ReversionGraph<String,String>.Vertex This = graph.getVertex(
				FlowValue.<String,String>createParameter("this"));
		ReversionGraph<String,String>.Vertex invoke
				= graph.getVertex("invokevirtual", graph.getVertex("getValue",
				graph.getVertex("getfield", sigma, This,
				graph.getVertex("name"))), graph.getVertex("hashCode"),
				graph.getVertex("params", sigma));
		graph.getVertex("injr", graph.getVertex("+", graph.getVertex("+",
				graph.getVertex("<<", graph.getVertex("getValue",
				graph.getVertex("getfield", sigma, This,
				graph.getVertex("arity"))), graph.getVertex("10")),
				graph.getVertex("<<", graph.getVertex("getValue",
				graph.getVertex("rho1", invoke)), graph.getVertex("8"))),
				graph.getVertex(FlowValue.<String,String>createPhi(),
				graph.getVertex("=", graph.getVertex("getValue",
				graph.getVertex("getField", graph.getVertex("rho2", invoke))),
				graph.getVertex("0")), graph.getVertex("6666"),
				graph.getVertex("8888")))).makeSignificant();
		Map<Integer,ReversionGraph<String,String>.Vertex> returns
				= new HashMap();
		returns.put(0, graph.getSignificant().iterator().next());
		System.out.println(new CFGReverter<String,String,Integer>(graph,
				returns, new AbstractOpAmbassador<String>() {
			public boolean canPreEvaluate(String op) {return false;}
			public String getBasicOp(BasicOp op) {
				return op.toString();
			}
			public BasicOp getBasicOp(String op) {return null;}
			public boolean isFree(String op) {
				return op.equals("getValue") || op.equals("params")
						|| op.equals("rho1") || op.equals("rho2");
			}
			public boolean needsAnyChild(String op) {
				return op.equals("invokevirtual") || op.equals("getfield");
			}
			public boolean needsChild(String op, int child) {
				if (op.equals("invokevirtual"))
					return child > 0;
				else if (op.equals("getfield"))
					return true;
				else
					return false;
			}

			public boolean isAnyVolatile(String op) {return false;}
			public boolean isVolatile(String op, int child) {return false;}
			public String getChainVersion(String op, int child) {
				throw new IllegalArgumentException();
			}
			public String getChainProjectValue(String op, int child) {
				throw new IllegalArgumentException();
			}
			public String getChainProjectVolatile(String op, int child) {
				throw new IllegalArgumentException();
			}
			public boolean isEquivalent(
					OpExpression<String> first, int firstChild,
					OpExpression<String> second, int secondChild) {
				throw new IllegalArgumentException();
			}
			
			public String get(String op, List<? extends String> operands) {
				throw new IllegalArgumentException();
			}
		}).getCFG());
	}
}
