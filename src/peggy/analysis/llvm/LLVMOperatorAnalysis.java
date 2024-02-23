package peggy.analysis.llvm;

import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.instructions.ComparisonPredicate;
import llvm.instructions.FloatingPointComparisonPredicate;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.types.Type;
import llvm.values.IntegerValue;
import peggy.analysis.Analysis;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.CmpLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
import util.AbstractPattern;
import util.Pattern;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis has axioms that refer to LLVM operators.
 */
public abstract class LLVMOperatorAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMOperatorAnalysis: " + message);
	}
	
	public LLVMOperatorAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine); 
	}

	/**
	 * This is the top-level call that should be made to add all the axioms.
	 */
	public void addAll() {
		addCallASMAxiom();
		addICMPAxioms();
		addCommutativeAxioms();
		addConditionalNegationAxioms();
		addAssociativityAxioms();
		addDistributeOpThroughOpAxioms();
		addEliminateBitcastAxioms();
	}
	
	/**
	 * Calling an ASM is the same as tailcalling an ASM.
	 */
	public void addCallASMAxiom() {
		final String name = "call ASM = tailcall ASM";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call",
					SimpleLLVMLabel.get(LLVMOperator.CALL),
					helper.getVariable("sigma"), // sigma
					helper.get("asm", null), // function
					helper.getVariable("cc"), // cc
					helper.getVariable("params")); // params

		helper.mustExist(call);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				CPEGTerm<LLVMLabel,LLVMParameter> call = bundle.getTerm("call");
				CPEGTerm<LLVMLabel,LLVMParameter> asm = bundle.getTerm("asm");
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) 
					proof.addProperties(
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(call, call.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(call, call.getArity()),
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(asm, asm.getOp()));
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.TAILCALL),
						steal(call, 0),
						concOld(asm),
						steal(call, 2),
						steal(call, 3));
				result.finish(call, proof, futureGraph);
				
				return asm.getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> asm = bundle.getTerm("asm");
				if (asm.getOp().isDomain() && 
					asm.getOp().getDomain().isInlineASM()) {
					return true;
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * Adds axioms of the form (icmp[pred] A B) bop (icmp[pred] A B) = ?
	 */
	public void addICMPAxioms() {
		addICMPAxioms(
				IntegerComparisonPredicate.ICMP_ULT,
				IntegerComparisonPredicate.ICMP_ULE,
				IntegerComparisonPredicate.ICMP_UGT,
				IntegerComparisonPredicate.ICMP_UGE);
		addICMPAxioms(
				IntegerComparisonPredicate.ICMP_SLT,
				IntegerComparisonPredicate.ICMP_SLE,
				IntegerComparisonPredicate.ICMP_SGT,
				IntegerComparisonPredicate.ICMP_SGE);
	}

	private void addICMPAxioms(
			IntegerComparisonPredicate LT, IntegerComparisonPredicate LE,
			IntegerComparisonPredicate GT, IntegerComparisonPredicate GE) {

		final IntegerComparisonPredicate 
		NE = IntegerComparisonPredicate.ICMP_ULT, 
		EQ = IntegerComparisonPredicate.ICMP_ULT;

		final IntegerValue TRUE = IntegerValue.TRUE;
		final IntegerValue FALSE = IntegerValue.FALSE;

		// OR
		Object[][] data = { 
				{LT,LE,LE}, 
				{LT,GT,NE}, 
				{LT,GE,TRUE},
				{LT,NE,NE}, 
				{LT,EQ,LE},
				
				{LE,GT,TRUE}, 
				{LE,GE,TRUE}, 
				{LE,NE,TRUE},
				{LE,EQ,LE},

				{GT,GE,GE}, 
				{GT,NE,NE}, 
				{GT,EQ,GE},

				{GE,NE,TRUE}, 
				{GE,EQ,GE},

				{NE,EQ,TRUE} 
		};
		for (Object[] row : data) {
			addICMPBool(Binop.Or,
					(IntegerComparisonPredicate) row[0],
					(IntegerComparisonPredicate) row[1], row[2]);
		}

		// AND
		data = new Object[][] { 
				{ LT, LE, LT }, 
				{ LT, GT, FALSE },
				{ LT, GE, FALSE }, 
				{ LT, NE, LT }, 
				{ LT, EQ, FALSE },

				{ LE, GT, FALSE }, 
				{ LE, GE, EQ }, 
				{ LE, NE, LT },
				{ LE, EQ, EQ },

				{ GT, GE, GT }, 
				{ GT, NE, GT }, 
				{ GT, EQ, FALSE },

				{ GE, NE, GT }, 
				{ GE, EQ, EQ },

				{ NE, EQ, FALSE } 
		};
		for (Object[] row : data) {
			addICMPBool(Binop.And,
					(IntegerComparisonPredicate) row[0],
					(IntegerComparisonPredicate) row[1], row[2]);
		}

		// XOR
		data = new Object[][] { 
				{ LT, LE, EQ }, 
				{ LT, GT, NE },
				{ LT, GE, TRUE }, 
				{ LT, NE, GT }, 
				{ LT, EQ, LE },

				{ LE, GT, TRUE }, 
				{ LE, GE, NE }, 
				{ LE, NE, GE },
				{ LE, EQ, LT },

				{ GT, GE, EQ }, 
				{ GT, NE, LT }, 
				{ GT, EQ, GE },

				{ GE, NE, LE }, 
				{ GE, EQ, GT },

				{ NE, EQ, TRUE } 
		};
		for (Object[] row : data) {
			addICMPBool(Binop.Xor,
					(IntegerComparisonPredicate) row[0],
					(IntegerComparisonPredicate) row[1], row[2]);
		}
	}

	private void addICMPBool(
			Binop binop, IntegerComparisonPredicate lhs,
			IntegerComparisonPredicate rhs, Object result) {
		final String name = binop + "(icmp[" + lhs + "] A B) (icmp[" + rhs + "] A B)";
		
		PeggyAxiomizer<LLVMLabel, Integer> axiomizer = new PeggyAxiomizer<LLVMLabel, Integer>(
				name, getNetwork(), getAmbassador());
		PeggyVertex<LLVMLabel, Integer> A = axiomizer.getVariable(1);
		PeggyVertex<LLVMLabel, Integer> B = axiomizer.getVariable(2);
		PeggyVertex<LLVMLabel, Integer> binopnode = axiomizer.get(
				new BinopLLVMLabel(binop), 
				axiomizer.get(new CmpLLVMLabel(lhs), A, B), 
				axiomizer.get(new CmpLLVMLabel(rhs), A, B));
		axiomizer.mustExist(binopnode);

		if (result instanceof IntegerComparisonPredicate) {
			axiomizer.makeEqual(
					binopnode, 
					axiomizer.get(
							new CmpLLVMLabel((IntegerComparisonPredicate) result), 
							A, B));
		} else if (result instanceof IntegerValue) {
			axiomizer.makeEqual(
					binopnode, 
					axiomizer.get(
							new ConstantValueLLVMLabel((IntegerValue) result)));
		} else
			throw new IllegalArgumentException("Unknown value: " + result);

		addProofListener(
				getEngine().addPEGAxiom(axiomizer.getAxiom()),
				name);
	}

	
	
	public void addCommutativeAxioms() {
		addCommutativeAxiom(new BinopLLVMLabel(Binop.Add));
		addCommutativeAxiom(new BinopLLVMLabel(Binop.Mul));
		addCommutativeAxiom(new BinopLLVMLabel(Binop.And));
		addCommutativeAxiom(new BinopLLVMLabel(Binop.Or));
		addCommutativeAxiom(new BinopLLVMLabel(Binop.Xor));

		addCommutativeAxiom(new CmpLLVMLabel(IntegerComparisonPredicate.ICMP_EQ));
		addCommutativeAxiom(new CmpLLVMLabel(IntegerComparisonPredicate.ICMP_NE));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_OEQ));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_ONE));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_ORD));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_UEQ));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_UNE));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_TRUE));
		addCommutativeAxiom(new CmpLLVMLabel(FloatingPointComparisonPredicate.FCMP_FALSE));
	}
	public void addCommutativeAxiom(LLVMLabel operator) {
		final String name = operator + " A B = " + operator + " B A";
		
		PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
			new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
		PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
		PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
		
		PeggyVertex<LLVMLabel,Integer> op1 = axiomizer.get(operator, A, B);
		PeggyVertex<LLVMLabel,Integer> op2 = axiomizer.get(operator, B, A);
		axiomizer.mustExist(op1);
		axiomizer.makeEqual(op1,op2);
		addProofListener(
				getEngine().addPEGAxiom(axiomizer.getAxiom()), 
				name);
	}
	

	
	
	public void addConditionalNegationAxioms() {
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_SLT, 
				IntegerComparisonPredicate.ICMP_SGE);
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_ULT, 
				IntegerComparisonPredicate.ICMP_UGE); 
		
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_SGT, 
				IntegerComparisonPredicate.ICMP_SLE); 
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_UGT, 
				IntegerComparisonPredicate.ICMP_ULE); 
		
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_SLE, 
				IntegerComparisonPredicate.ICMP_SGT); 
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_ULE, 
				IntegerComparisonPredicate.ICMP_UGT); 

		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_SGE, 
				IntegerComparisonPredicate.ICMP_SLT); 
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_UGE, 
				IntegerComparisonPredicate.ICMP_ULT); 
		
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_EQ, 
				IntegerComparisonPredicate.ICMP_NE); 
		addConditionalNegationAxiom(
				IntegerComparisonPredicate.ICMP_NE, 
				IntegerComparisonPredicate.ICMP_EQ); 
	}
	public void addConditionalNegationAxiom(
			ComparisonPredicate lhs, 
			ComparisonPredicate rhs) {
		final String name = "negate(cmp[" + lhs + "] A B) = cmp[" + rhs + "] A B";
		PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
			new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
		PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
		PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
		
		PeggyVertex<LLVMLabel,Integer> negate = axiomizer.getNegate(
				axiomizer.get(new CmpLLVMLabel(lhs), A, B));
		PeggyVertex<LLVMLabel,Integer> result = 
				axiomizer.get(new CmpLLVMLabel(rhs), A, B);
				
		axiomizer.mustExist(negate);
		axiomizer.makeEqual(negate,result);
		addProofListener(
				getEngine().addPEGAxiom(axiomizer.getAxiom()),
				name);
	}
	

	
	public void addAssociativityAxioms() {
		addAssociativityAxiom(new BinopLLVMLabel(Binop.Add));
		addAssociativityAxiom(new BinopLLVMLabel(Binop.Mul));
		addAssociativityAxiom(new BinopLLVMLabel(Binop.And));
		addAssociativityAxiom(new BinopLLVMLabel(Binop.Or));
		addAssociativityAxiom(new BinopLLVMLabel(Binop.Xor));
	}
	// op(A,op(B,C)) == op(op(A,B),C)
	public void addAssociativityAxiom(LLVMLabel operator) {
		{
			final String name = operator + " A (" + operator + " B C) = " + operator + " (" + operator + " A B) C";
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(operator,
					A,
					axiomizer.get(operator,B,C));
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(operator,
						axiomizer.get(operator,A,B),
						C);
					
			axiomizer.mustExist(lhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}
		
		{
			final String name = operator + " (" + operator + " A B) C) = " + operator + " A (" + operator + " B C)";
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(operator,
					A,
					axiomizer.get(operator,B,C));
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(operator,
						axiomizer.get(operator,A,B),
						C);
					
			axiomizer.mustExist(rhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}
	}
	

	public void addDistributeOpThroughOpAxioms() {
		addDistributeOpThroughOpLeft(
				new BinopLLVMLabel(Binop.Mul), 
				new BinopLLVMLabel(Binop.Add)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.Mul), 
				new BinopLLVMLabel(Binop.Add)); 

		addDistributeOpThroughOpLeft(
				new BinopLLVMLabel(Binop.Mul), 
				new BinopLLVMLabel(Binop.Sub)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.Mul), 
				new BinopLLVMLabel(Binop.Sub)); 

		addDistributeOpThroughOpLeft(
				new BinopLLVMLabel(Binop.And), 
				new BinopLLVMLabel(Binop.Or)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.And), 
				new BinopLLVMLabel(Binop.Or)); 

		addDistributeOpThroughOpLeft(
				new BinopLLVMLabel(Binop.Or), 
				new BinopLLVMLabel(Binop.And)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.Or), 
				new BinopLLVMLabel(Binop.And)); 
		
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.LShr), 
				new BinopLLVMLabel(Binop.Or)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.AShr), 
				new BinopLLVMLabel(Binop.Or)); 

		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.LShr), 
				new BinopLLVMLabel(Binop.And)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.AShr), 
				new BinopLLVMLabel(Binop.And)); 

		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.LShr), 
				new BinopLLVMLabel(Binop.Xor)); 
		addDistributeOpThroughOpRight(
				new BinopLLVMLabel(Binop.AShr), 
				new BinopLLVMLabel(Binop.Xor)); 
	}
	
	// (outer A (inner B C)) <==> (inner (outer A B) (outer A C))
	public void addDistributeOpThroughOpLeft(
			LLVMLabel outer, LLVMLabel inner) {
		{
			final String name = outer + " A (" + inner + " B C) = " + inner + " (" + outer + " A B) (" + outer + " A C))";
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(outer,
					A,
					axiomizer.get(inner,B,C));
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(inner,
						axiomizer.get(outer,A,B),
						axiomizer.get(outer,A,C));
					
			axiomizer.mustExist(lhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}
		
		{
			final String name = inner + " (" + outer + " A B) (" + outer + " A C)) = " + outer + " A (" + inner + " B C)"; 
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(outer,
					A,
					axiomizer.get(inner,B,C));
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(inner,
						axiomizer.get(outer,A,B),
						axiomizer.get(outer,A,C));
					
			axiomizer.mustExist(rhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}
	}
	
	
	// (outer (inner B C) A) <==> (inner (outer B A) (outer C A))
	public void addDistributeOpThroughOpRight(
			LLVMLabel outer, LLVMLabel inner) {
		{
			final String name = outer + " (" + inner + " B C) A = " + inner + " (" + outer + " B A) (" + outer + " C A)";
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(outer,
					axiomizer.get(inner,B,C),
					A);
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(inner,
						axiomizer.get(outer,B,A),
						axiomizer.get(outer,C,A));
					
			axiomizer.mustExist(lhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}

		{
			final String name = inner + " (" + outer + " B A) (" + outer + " C A) = " + outer + " (" + inner + " B C) A";
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
			PeggyVertex<LLVMLabel,Integer> A = axiomizer.getVariable(1);
			PeggyVertex<LLVMLabel,Integer> B = axiomizer.getVariable(2);
			PeggyVertex<LLVMLabel,Integer> C = axiomizer.getVariable(3);
			
			PeggyVertex<LLVMLabel,Integer> lhs = 
				axiomizer.get(outer,
					axiomizer.get(inner,B,C),
					A);
			
			PeggyVertex<LLVMLabel,Integer> rhs =  
				axiomizer.get(inner,
						axiomizer.get(outer,B,A),
						axiomizer.get(outer,C,A));
					
			axiomizer.mustExist(rhs);
			axiomizer.makeEqual(lhs,rhs);
			addProofListener(
					getEngine().addPEGAxiom(axiomizer.getAxiom()),
					name);
		}
	}
	
	

	
	
	
	public void addEliminateBitcastAxioms() {
		Pattern<Type> intpattern = new AbstractPattern<Type>() {
			public boolean matches(Type type) {
				return type.isInteger();
			}
		};
		Pattern<Type> fppattern = new AbstractPattern<Type>() {
			public boolean matches(Type type) {
				return type.isFloatingPoint();
			}
		};
		Pattern<Type> ptrpattern = new AbstractPattern<Type>() {
			public boolean matches(Type type) {
				return type.isComposite() && type.getCompositeSelf().isPointer();
			}
		};
		
		addEliminateBitcastAxiom(Cast.Trunc, intpattern);
		addEliminateBitcastAxiom(Cast.ZExt, intpattern);
		addEliminateBitcastAxiom(Cast.SExt, intpattern);
		addEliminateBitcastAxiom(Cast.FPToUI, intpattern);
		addEliminateBitcastAxiom(Cast.FPToSI, intpattern);
		addEliminateBitcastAxiom(Cast.PtrToInt, intpattern);

		addEliminateBitcastAxiom(Cast.UIToFP, fppattern);
		addEliminateBitcastAxiom(Cast.SIToFP, fppattern);
		addEliminateBitcastAxiom(Cast.FPTrunc, fppattern);
		addEliminateBitcastAxiom(Cast.FPExt, fppattern);
		
		addEliminateBitcastAxiom(Cast.IntToPtr, ptrpattern);
	}
	/**
	 * bitcast T1 (inner T2 X) = inner T1 X
	 * 
	 * conditions: T1 is int, T1 is fp
	 */
	public void addEliminateBitcastAxiom(
			final Cast inner, final Pattern<Type> pattern) {
		final String name = "bitcast T1 (" + inner + " T2 X) = " + inner + " T1 X";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> bitcast = 
			helper.get("bitcast",
					new CastLLVMLabel(Cast.Bitcast),
					helper.get("T1", null),
					helper.get("inner", 
							new CastLLVMLabel(inner),
							helper.getVariable("T2"),
							helper.getVariable("X")));
		helper.mustExist(bitcast);
		
		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				Node result = node(
						new CastLLVMLabel(inner),
						steal(bundle.getTerm("bitcast"), 0),
						steal(bundle.getTerm("inner"), 1));
				result.finish(bundle.getTerm("bitcast"), proof, futureGraph);
				
				return bundle.getTerm("T1").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> t1Term = bundle.getTerm("T1");
				return t1Term.getOp().isDomain() &&
					t1Term.getOp().getDomain().isType() &&
					pattern.matches(t1Term.getOp().getDomain().getTypeSelf().getType());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
