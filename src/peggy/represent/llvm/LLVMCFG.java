package peggy.represent.llvm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import llvm.bitcode.ReferenceResolver;
import llvm.values.FunctionValue;
import llvm.values.VirtualRegister;
import util.Function;
import util.VariaticFunction;
import util.graph.AbstractGraph;
import eqsat.CFG;
import eqsat.CFGTranslator;

/**
 * This class implements the CFG interface for LLVM programs.
 * 
 * For branch blocks, true child = 0, false child = 1
 */
public abstract class LLVMCFG extends AbstractGraph<LLVMCFG, LLVMBlock> 
implements CFG<LLVMCFG, LLVMBlock, LLVMVariable, LLVMLabel, LLVMParameter, LLVMReturn> { 

	protected final List<LLVMBlock> vertices;
	protected final Map<LLVMBlock,List<LLVMBlock>> successorMap;
	protected LLVMBlock start;
	protected LLVMBlock end;
	protected final Map<VirtualRegister,RegisterLLVMVariable> register2variable;
	
	protected final List<LLVMVariable> variables;
	
	protected final Function<VirtualRegister,LLVMVariable> registerMap;
	protected final Function<FunctionValue.ArgumentValue,LLVMVariable> argumentMap;
	protected final LLVMOpAmbassador ambassador;
	protected final ReferenceResolver resolver;
	protected GEPForcingPolicy forcingPolicy = GEPForcingPolicy.NONE; 

	public LLVMCFG(LLVMOpAmbassador _ambassador, ReferenceResolver _resolver) {
		this.ambassador = _ambassador;
		this.resolver = _resolver;
		this.vertices = new ArrayList<LLVMBlock>();
		this.successorMap = new HashMap<LLVMBlock,List<LLVMBlock>>();
		this.register2variable = new HashMap<VirtualRegister,RegisterLLVMVariable>();
		
		this.variables = new ArrayList<LLVMVariable>();
		this.variables.add(LLVMVariable.RETURN);
		this.variables.add(LLVMVariable.SIGMA);
		
		this.registerMap = new Function<VirtualRegister,LLVMVariable>() {
			public LLVMVariable get(VirtualRegister reg) {
				return register2variable.get(reg);
			}
		};
		this.argumentMap = new Function<FunctionValue.ArgumentValue,LLVMVariable>() {
			public LLVMVariable get(FunctionValue.ArgumentValue arg) {
				FunctionLLVMLabel functionLabel = 
					new FunctionLLVMLabel(
							arg.getParent().getType().getPointeeType().getFunctionSelf(),
							resolver.getFunctionName(arg.getParent()));
				return new ArgumentLLVMVariable(functionLabel, arg.getIndex(), arg.getType());
			}
		};
	}
	
	public GEPForcingPolicy getGEPForcingPolicy() {return this.forcingPolicy;}
	public void setGEPForcingPolicy(GEPForcingPolicy gfp) {
		if (gfp==null)
			this.forcingPolicy = GEPForcingPolicy.NONE;
		else
			this.forcingPolicy = gfp;
	}
	
	public final LLVMOpAmbassador getOpAmbassador() {return this.ambassador;}
	public final ReferenceResolver getResolver() {return this.resolver;}
	
	/**
	 * Translates from V to P
	 */
	public LLVMParameter getParameter(LLVMVariable var) throws IllegalArgumentException {
		if (var.isArgument()) {
			ArgumentLLVMVariable ar = var.getArgumentSelf();
			return new ArgumentLLVMParameter(ar);
		} else if (var.isSigma()) {
			return LLVMParameter.SIGMA;
		} else {
			throw new IllegalArgumentException("Variable " + var + " not defined");
		}
	}
	
	/**
	 * Returns all variables in this CFG.
	 */
	public final Collection<? extends LLVMVariable> getVariables() {return this.variables;}

	/**
	 * Returns the vertices of this graph.
	 */
	public final Collection<? extends LLVMBlock> getVertices() {return this.vertices;}
	
	/**
	 * Returns the "returns" of this CFG.
	 * Will always be VALUE and SIGMA.
	 */
	public final Collection<? extends LLVMReturn> getReturns() {
		List<LLVMReturn> result = new ArrayList<LLVMReturn>();
		result.add(LLVMReturn.SIGMA);
		result.add(LLVMReturn.VALUE);
		return result;
	}
	
	/** 
	 * Translates from R to V
	 */
	public final LLVMVariable getReturnVariable(LLVMReturn ret) {
		return ret.getVariableVersion();
	}

	/**
	 * Returns the translator for this CFG.
	 */
	public final <E> CFGTranslator<LLVMBlock,LLVMVariable,E> getTranslator(Function<LLVMParameter,E> paramConverter,
			VariaticFunction<LLVMLabel,E,E> converter, Collection<? super E> known) {
		LLVMCFGTranslator2<E> result = 
			new LLVMCFGTranslator2<E>(
				this, 
				converter, 
				paramConverter, 
				this.getResolver(), 
				this.registerMap, 
				this.argumentMap);
		result.setGEPForcingPolicy(this.forcingPolicy);
		return result;
	}

	public final LLVMCFG getSelf() {return this;}
	public LLVMBlock getStart() {return this.start;}
	public LLVMBlock getEnd() {return this.end;}
	
	////////////////////////////////////////////////////

	/**
	 * Helper function to create and return a new RegisterLLVMVariable,
	 * and add the variable to the variables list, and add a mapping
	 * in the register2variable map.
	 */
	protected RegisterLLVMVariable newRegisterVariable(VirtualRegister reg) {
		RegisterLLVMVariable result = new RegisterLLVMVariable(reg);
		this.variables.add(result);
		this.register2variable.put(reg, result);
		return result;
	}
	
	
	/**
	 * Helper function to create and return a new LLVMBlock, 
	 * and add it to the vertices list.
	 */
	protected LLVMBlock newBlock() {
		LLVMBlock result = new LLVMBlock(this);
		this.vertices.add(result);
		return result;
	}
	
	public void toDot(PrintStream out) {
		out.println("digraph CFG {");
		
		for (LLVMBlock block : this.vertices) {
			out.print("  " + block.hashCode() + " [shape=box,label=\"");
			
			for (int i = 0; i < block.getNumInstructions(); i++) {
				if (i > 0) out.print("\\n");
				CFGInstruction cfg = block.getInstruction(i);
				if (block.hasVariable(cfg)) {
					out.print(block.getAssignment(cfg));
					out.print(" = ");
				}
				out.print(cfg);
			}
			
			out.println("\"];");
			
			List<LLVMBlock> succs = this.successorMap.get(block);
			if (succs == null)
				continue;
			
			for (LLVMBlock succ : succs) {
				out.println("  " + block.hashCode() + " -> " + succ.hashCode() + " ;");
			}
		}
		
		out.println("}");
	}
}
