package peggy.revert.llvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.ReferenceResolver;
import llvm.instructions.AllocaInstruction;
import llvm.instructions.BasicBlock;
import llvm.instructions.BasicBlock.Handle;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.BrInstruction;
import llvm.instructions.CallInstruction;
import llvm.instructions.Cast;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ComparisonPredicate;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.FloatingPointComparisonPredicate;
import llvm.instructions.FreeInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.GEPInstruction;
import llvm.instructions.GetResultInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.LoadInstruction;
import llvm.instructions.MallocInstruction;
import llvm.instructions.RegisterAssignment;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVecInstruction;
import llvm.instructions.StoreInstruction;
import llvm.instructions.UnwindInstruction;
import llvm.instructions.VaargInstruction;
import llvm.types.CompositeType;
import llvm.types.FunctionType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.VectorType;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.FunctionValue.ArgumentValue;
import llvm.values.GlobalVariable;
import llvm.values.IntegerValue;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import peggy.represent.FlowValueStickyPredicate;
import peggy.represent.StickyPredicate;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.ArgumentLLVMVariable;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMLabelStickyPredicate;
import peggy.represent.llvm.LLVMOpAmbassador;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.revert.BlockVerticesIterator;
import peggy.revert.Item;
import peggy.revert.MiniPEG.Vertex;
import peggy.revert.PEGCFGUtils;
import peggy.revert.llvm.LLVMReachingDefs.LLVMDef;
import peggy.revert.llvm.LLVMReachingDefs.LLVMUse;
import util.AbstractPattern;
import util.Function;
import util.Pattern;
import eqsat.BasicOp;
import eqsat.FlowValue;

/**
 * This class converts an LLVMPEGCFG back to instructions in a FunctionBody.
 */
public class LLVMPEGCFGEncoder {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMPEGCFGEncoder: " + message);
	}
	
	
	private static final LabelOperatorPattern INVOKE_PATTERN = 
		new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.INVOKE));
	private static final LabelOperatorPattern RETURNSTRUCTURE_PATTERN = 
		new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.RETURNSTRUCTURE));
	private static final LabelOperatorPattern ISEXCEPTION_PATTERN = 
		new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.IS_EXCEPTION));
	private static final LabelOperatorPattern CALL_PATTERN =
		new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.CALL));
	private static final LabelOperatorPattern TAILCALL_PATTERN =
		new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.TAILCALL));

	private static class LabelOnVarPattern 
	extends AbstractPattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> {
		private LLVMLabel label;
		private Object var;
		public LabelOnVarPattern(LLVMLabel _label, Object _var) {
			this.label = _label;
			this.var = _var;
		}
		public boolean matches(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
			return vertex.getLabel().isLabel() &&
			vertex.getLabel().getLabel().equalsLabel(this.label) &&
			vertex.getChildCount() == 1 &&
			vertex.getChild(0).getLabel().isVariable() &&
			vertex.getChild(0).getLabel().getVariable().equals(this.var);
		}
	}
	
	private static class LabelOperatorPattern extends AbstractPattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> {
		private final LLVMLabel label;
		public LabelOperatorPattern(LLVMLabel _label) {
			this.label = _label;
		}
		public boolean matches(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
			Item<LLVMLabel,LLVMParameter,Object> item = vertex.getLabel();
			return item.isLabel() && item.getLabel().equalsLabel(this.label);
		}
	}
	
	private static final Pattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> ISEXCEPTION_ON_CALL =
		new AbstractPattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
			public boolean matches(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
				return ISEXCEPTION_PATTERN.matches(vertex) &&
				(CALL_PATTERN.matches(vertex.getChild(0)) ||
				 TAILCALL_PATTERN.matches(vertex.getChild(0)));
			}
		};

	private static final Pattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> ISEXCEPTION_ON_INVOKE =
		new AbstractPattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
			public boolean matches(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
				return ISEXCEPTION_PATTERN.matches(vertex) &&
				INVOKE_PATTERN.matches(vertex.getChild(0));
			}
		};
		
	private static final Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
								  Vertex<Item<LLVMLabel,LLVMParameter,Object>>> MAKE_FALSE = 
		new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
					 Vertex<Item<LLVMLabel,LLVMParameter,Object>>> () {
			public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(
					Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
				return vertex.getGraph().getVertex(
						Item.<LLVMLabel,LLVMParameter,Object>getLabel(
								new ConstantValueLLVMLabel(IntegerValue.FALSE)));
			}
		};
	
	//////////////////////////////////////////////////
	
	private final LLVMOpAmbassador ambassador;
	private final LLVMPEGCFG cfg;
	private final FunctionBody body;
	private final ReferenceResolver resolver;
	
	public LLVMPEGCFGEncoder(
			LLVMPEGCFG _cfg, 
			FunctionBody _body, 
			LLVMOpAmbassador _ambassador,
			ReferenceResolver _resolver) {
		this.cfg = _cfg;
		this.body = _body;
		this.resolver = _resolver;
		this.ambassador = _ambassador;
	}

	/**
	 * This function encodes the input CFG as an LLVM FunctionBody.
	 * This method will modify the given CFG!
	 * The function body is returned.
	 */
	public FunctionBody encode() {
		long start = System.currentTimeMillis();
		boolean sticky = checkSticky();
		long check = System.currentTimeMillis();
		debug("***checkSticky:"  + (check-start) + "***");
		
		if (!sticky)
			throw new RuntimeException("Input CFG violates sticky constraints");

		start = System.currentTimeMillis();
		this.removeIsExceptions();
		check = System.currentTimeMillis();
		debug("***removeIsExceptions: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		this.removeINJR();
		check = System.currentTimeMillis();
		debug("***removeINJR: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		this.removeINJL();
		check = System.currentTimeMillis();
		debug("***removeINJL: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		this.removeUnwindFollowers();
		check = System.currentTimeMillis();
		debug("***removeUnwindFollowers: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		boolean hasAssigns = this.makeReturnLast();
		check = System.currentTimeMillis();
		debug("***makeReturnLast: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		this.mergeReturnStructures(hasAssigns);
		check = System.currentTimeMillis();
		debug("***mergeReturnStructures: " + (check-start) + "***");

		start = System.currentTimeMillis();
		PEGCFGUtils.foldUnconditionalBranches(
				this.cfg,
				new AbstractPattern<LLVMLabel>() {
					public boolean matches(LLVMLabel label) {
						return label.isConstantValue() &&
						label.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE);
					}
				},
				new AbstractPattern<LLVMLabel>() {
					public boolean matches(LLVMLabel label) {
						return label.isConstantValue() &&
						label.getConstantValueSelf().getValue().equalsValue(IntegerValue.FALSE);
					}
				});
		check = System.currentTimeMillis();

		debug("***foldUnconditionalBranches: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		check = System.currentTimeMillis();

		debug("***removeUnreachableBlocks: " + (check-start) + "***");
		
		Map<Handle,VirtualRegister> regmap = 
			new HashMap<Handle,VirtualRegister>();
		List<BasicBlock> newblocks = new ArrayList<BasicBlock>();


		start = System.currentTimeMillis();
		this.buildBlocks(regmap, newblocks);
		check = System.currentTimeMillis();

		debug("***buildBlocks: " + (check-start) + "***");
		
		return this.body;
	}
	
	private void buildBlocks(
			Map<Handle,VirtualRegister> regmap,
			List<BasicBlock> newblocks) {
		// build type map
		Map<Object,VirtualRegister> varmap = 
			new HashMap<Object,VirtualRegister>();
		
		long startTime = System.currentTimeMillis();
		this.buildTypeMap(varmap);
		long checkTime = System.currentTimeMillis();
		
		debug("***buildTypeMap: " + (checkTime-startTime) + "***");

		// build basic blocks
		Map<LLVMPEGCFGBlock,BasicBlock> blockmap = 
			new HashMap<LLVMPEGCFGBlock,BasicBlock>();
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			BasicBlock newblock = new BasicBlock();
			newblocks.add(newblock);
			blockmap.put(block, newblock);
		}
		
		// build successor map
		Map<BasicBlock,List<BasicBlock>> successorMap = 
			new HashMap<BasicBlock,List<BasicBlock>>();
		for (LLVMPEGCFGBlock block : blockmap.keySet()) {
			BasicBlock newblock = blockmap.get(block);
			List<BasicBlock> succs = new ArrayList<BasicBlock>(block.getNumSuccs());
			for (LLVMPEGCFGBlock succ : block.getSuccs()) {
				succs.add(blockmap.get(succ));
			}
			successorMap.put(newblock, succs);
		}
		
		startTime = System.currentTimeMillis();
		// build instructions
		for (LLVMPEGCFGBlock block : blockmap.keySet()) {
			BasicBlock newblock = blockmap.get(block);
			this.buildInstructions(block, newblock, blockmap, successorMap, varmap, regmap);
		}
		checkTime = System.currentTimeMillis();
		
		debug("***buildInstructions: " + (checkTime-startTime) + "***");

		
		BasicBlock start = blockmap.get(this.cfg.getStartBlock());
		
		
		startTime = System.currentTimeMillis();
		ReversionUtils.addTrailingGotos(newblocks, successorMap);
		ReversionUtils.pruneUnreachable(start, newblocks);

		// this MUST be done before SSA!
		ReversionUtils.insertDummyAssignments(start, regmap);
		checkTime = System.currentTimeMillis();
		
		debug("***atg + pu + ida: " + (checkTime-startTime) + "***");
		

		startTime = System.currentTimeMillis();
		// build the dominator graph
		LLVMDominatorGraph domgraph = new LLVMDominatorGraph(start, newblocks);
		checkTime = System.currentTimeMillis();
		
		debug("***build domgraph: " + (checkTime-startTime) + "***");
		
		
		{// put into SSA form
			startTime = System.currentTimeMillis();
			LLVMSSAConverter converter = new LLVMSSAConverter(domgraph, regmap);
			converter.run();
			domgraph = null;
			converter = null;
			// clean up some memory
			
			checkTime = System.currentTimeMillis();
			
			debug("***run SSA converter: " + (checkTime-startTime) + "***");
		}

		
		startTime = System.currentTimeMillis();
		ReversionUtils.removeSpuriousCopies(newblocks, regmap);
		checkTime = System.currentTimeMillis();
		
		debug("***removeSpuriousCopies: " + (checkTime-startTime) + "***");

		startTime = System.currentTimeMillis();
		ReversionUtils.removeDeadAssignments(newblocks, regmap);
		checkTime = System.currentTimeMillis();
		
		debug("***removeDeadAssignments: " + (checkTime-startTime) + "***");

		
		// clear out the function body
		this.body.clearValueNameMap();
		this.body.getRegisterAssignment().clear();
		while (this.body.getNumBlocks() > 0)
			this.body.removeBlock(0);
		this.body.setStart(null);
		

		// if there is an exception after HERE, then we're boned
		
		// put stuff back into FunctionBody
		for (BasicBlock bb : newblocks)
			this.body.addBlock(bb);
		if (start == null)
			throw new NullPointerException("Null start block!");
		this.body.setStart(start);
		RegisterAssignment assignment = this.body.getRegisterAssignment();
		for (Handle handle : regmap.keySet()) {
			assignment.set(regmap.get(handle), handle);
		}
	}
	
	
	/**
	 * Converts the minipeg in the LLVM block to instructions
	 * in the basicblock.
	 * All the maps that are passed in are already full.
	 */
	private void buildInstructions(
			LLVMPEGCFGBlock block,
			BasicBlock newblock,
			Map<LLVMPEGCFGBlock,BasicBlock> blockmap,
			Map<BasicBlock,List<BasicBlock>> successorMap,
			Map<Object,VirtualRegister> varmap,
			Map<Handle,VirtualRegister> regmap) {
		// build the substitution map
		Map<Object,Object> submap = new HashMap<Object,Object>();
		for (LLVMIterator iter=new LLVMIterator(block); iter.hasNext(); ) {
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> next = iter.next();
			if (next.getLabel().isVariable() && 
				block.getAssignedVars().contains(next.getLabel().getVariable())) {
				// both get and set, add to submap
				Object newvar = this.cfg.makeNewTemporary();
				Object oldvar = next.getLabel().getVariable();
				submap.put(oldvar, newvar);
				
				// update the varmap
				if (varmap.get(oldvar) == null) {
					varmap.put(newvar, null);
				} else {
					varmap.put(newvar, VirtualRegister.getVirtualRegister(varmap.get(oldvar).getType()));
				}
			}
		}
		
		
		
		Object unwindVar = null;
		for (Object assigned : block.getAssignedVars()) {
			// unwind should have to be the root (I think!)
			if (block.getAssignment(assigned).getLabel().isLabel() &&
				block.getAssignment(assigned).getLabel().getLabel().equalsLabel(SimpleLLVMLabel.get(LLVMOperator.UNWIND))) {
				unwindVar = assigned;
				break;
			}
		}
		
		boolean hasInvoke = 
			(block.getBranchCondition() != null &&
			 INVOKE_PATTERN.matches(block.getBranchCondition()));
		Object invokeVar = null;
		if (hasInvoke) {
			// find invoke var
			for (Object assigned : block.getAssignedVars()) {
				if (block.getAssignment(assigned).equals(block.getBranchCondition())) {
					invokeVar = assigned;
					break;
				}
			}
			if (invokeVar == null)
				throw new RuntimeException("Cannot find invoke var");
		}

		if (unwindVar != null && hasInvoke)
			throw new RuntimeException("I don't think this should ever happen");
		
		Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,VirtualRegister> tempmap =
			new HashMap<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,VirtualRegister>();
		
		Emitter emitter = new Emitter(newblock, tempmap, regmap, varmap, successorMap, submap);

		// insert the copies from the submap
		for (Object oldvar : submap.keySet()) {
			if (varmap.get(oldvar) == null)
				continue;
			Instruction inst = new SelectInstruction(IntegerValue.TRUE, varmap.get(oldvar), varmap.get(oldvar));
			Handle handle = newblock.addInstruction(inst);
			regmap.put(handle, varmap.get(submap.get(oldvar)));
			// no update to tempmap
		}
		
		final Object returnVar = this.cfg.getReturnVariable(LLVMReturn.VALUE);
		
		for (Object assigned : block.getAssignedVars()) {
			// do the invokevar last (if there is one)
			if (assigned == invokeVar || assigned == unwindVar || assigned.equals(returnVar))
				continue;
			
			Value value = emitter.emitInstructions(block.getAssignment(assigned));
			if (value != null) {
				SelectInstruction select = new SelectInstruction(IntegerValue.TRUE, value, value);
				regmap.put(newblock.addInstruction(select), varmap.get(assigned));
			}
		}

		
		if (successorMap.get(newblock).size() == 0 && unwindVar == null) {
			// return block
			FunctionType funcType = this.body.getHeader().getType().getPointeeType().getFunctionSelf();
			if (funcType.getReturnType().isVoid()) {
				// emit return void
				Instruction inst = new RetInstruction();
				newblock.addInstruction(inst);
			} else if (block.getAssignedVars().contains(returnVar)) {
				// non void and actually assigned in this block
				if (funcType.getReturnType().isComposite() && 
					funcType.getReturnType().getCompositeSelf().isStructure()) {
					// look for returnstructures
					Vertex<Item<LLVMLabel,LLVMParameter,Object>> returnstructure = 
						block.getAssignment(returnVar);
					if (!RETURNSTRUCTURE_PATTERN.matches(returnstructure))
						throw new RuntimeException("This should not be assigned to the return var: " + returnstructure.getLabel());
					int nfields = funcType.getReturnType().getCompositeSelf().getStructureSelf().getNumFields();
					List<Value> values = new ArrayList<Value>(nfields);
					for (int i = 0; i < nfields; i++) {
						Value field = emitter.emitInstructions(returnstructure.getChild(i));
						values.add(field);
					}
					Instruction inst = new RetInstruction(values);
					newblock.addInstruction(inst);
				} else {
					// normal return value
					Value result = emitter.emitInstructions(block.getAssignment(returnVar));
					Instruction inst = new RetInstruction(Collections.singletonList(result));
					newblock.addInstruction(inst);
				}
			}
		}
		
		
		if (invokeVar != null) {
			emitter.emitInstructions(block.getAssignment(invokeVar));
			if (varmap.get(invokeVar)!=null) {
				VirtualRegister newvar = varmap.get(invokeVar);
				tempmap.put(block.getAssignment(invokeVar), newvar);
				regmap.put(newblock.getLastHandle(), newvar);
			}
		} else if (block.getBranchCondition() != null) {
			// non-invoke branching
			Value value = emitter.emitInstructions(block.getBranchCondition());
			BrInstruction br = new BrInstruction(value, blockmap.get(block.getSucc(0)), blockmap.get(block.getSucc(1)));
			newblock.addInstruction(br);
		} else if (unwindVar != null) {
			// do the unwind var last
			emitter.emitInstructions(block.getAssignment(unwindVar));
		}
	}
		

	private class Emitter {
		private final BasicBlock newblock;
		private final Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,VirtualRegister> tempmap;
		private final Map<Handle,VirtualRegister> regmap;
		private final Map<? extends Object,? extends VirtualRegister> varmap;
		private final Map<BasicBlock,List<BasicBlock>> successorMap;
		private final Map<Object,Object> submap;

		Emitter(
				BasicBlock _newblock,
				Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,VirtualRegister> _tempmap,
				Map<Handle,VirtualRegister> _regmap,
				Map<? extends Object,? extends VirtualRegister> _varmap,
				Map<BasicBlock,List<BasicBlock>> _successorMap,
				Map<Object,Object> _submap) {
			this.newblock = _newblock;
			this.tempmap = _tempmap;
			this.regmap = _regmap;
			this.varmap = _varmap;
			this.successorMap = _successorMap;
			this.submap = _submap;
		}

		private Value helper(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex, Instruction inst) {
			Handle handle = newblock.addInstruction(inst);
			VirtualRegister newvar = VirtualRegister.getVirtualRegister(inst.getType());
			tempmap.put(vertex, newvar);
			regmap.put(handle, newvar);
			return newvar;
		}
		
		public Value emitInstructions(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
			if (tempmap.containsKey(vertex))
				return tempmap.get(vertex);

			/* heuristic: whenever you add instructions, you must add to tempmap
			 */

			Item<LLVMLabel,LLVMParameter,Object> item = vertex.getLabel();

			if (item.isParameter()) {
				LLVMParameter param = item.getParameter();
				if (param.isSigma())
					return null;
				else if (param.isArgument()) {
					ArgumentLLVMVariable arg = param.getArgumentSelf().getVariableVersion();
					FunctionValue header = resolver.resolveFunction(
							arg.getFunction().getFunctionName(), arg.getFunction().getType());
					ArgumentValue argValue = header.getArgument(arg.getIndex());
					if (!argValue.getType().equalsType(arg.getType()))
						throw new RuntimeException("Argument value has wrong type");
					return argValue;
				}
				else
					throw new IllegalArgumentException("Mike didn't handle: " + item);
			} else if (item.isVariable()) {
				Object var = item.getVariable();
				if (this.submap.containsKey(var))
					return varmap.get(this.submap.get(var));
				else
					return varmap.get(var);
			} else {
				LLVMLabel label = item.getLabel();
				if (label.isSimple()) {
					switch (label.getSimpleSelf().getOperator()) {
					case INJR:
						// should not exist
						throw new IllegalArgumentException("Should not have INJR");

					case INJL:
						emitInstructions(vertex.getChild(0));
						return null;

					case INVOKE:
					case CALL:
					case TAILCALL: {
						emitInstructions(vertex.getChild(0));
						Value function = emitInstructions(vertex.getChild(1));
						int cc = vertex.getChild(2).getLabel().getLabel().getNumeralSelf().getValue();

						List<Value> params = new ArrayList<Value>();
						Vertex<Item<LLVMLabel,LLVMParameter,Object>> paramsVertex = vertex.getChild(3);
						for (int i = 0; i < paramsVertex.getChildCount(); i++) {
							Value param = emitInstructions(paramsVertex.getChild(i));
							params.add(param);
						}

//						Map<Integer,ParameterAttributes> attrmap = 
//							new HashMap<Integer,ParameterAttributes>();
//						Vertex<Item<LLVMLabel,LLVMParameter,Object>> paramAttrVertex = vertex.getChild(4);
//						for (int i = 0; i < paramAttrVertex.getChildCount(); i++) {
//							ParameterAttributes attrs = paramAttrVertex.getChild(i).getLabel().getLabel().getParamAttrSelf().getAttributes();
//							attrmap.put(i, attrs);
//						}
						ParameterAttributeMap map = new ParameterAttributeMap();


						Instruction inst;
						if (label.getSimpleSelf().getOperator().equals(LLVMOperator.CALL)) {
							inst = new CallInstruction(
									false, 
									cc, 
									function,
									map,
									params);
						} else if (label.getSimpleSelf().getOperator().equals(LLVMOperator.TAILCALL)) {
							inst = new CallInstruction(
									true, 
									cc, 
									function,
									map,
									params);
						} else {
							inst = new InvokeInstruction(
									cc, 
									function,
									map,
									successorMap.get(newblock).get(1),
									successorMap.get(newblock).get(0),
									params);
						}

						Handle handle = newblock.addInstruction(inst);
						VirtualRegister newvar = (inst.getType().isVoid() ? null : VirtualRegister.getVirtualRegister(inst.getType()));
						tempmap.put(vertex, newvar);
						if (newvar != null)
							regmap.put(handle, newvar);
						return newvar;
					}

					case RHO_VALUE: {
						return emitInstructions(vertex.getChild(0));
					}
					
					case NONSTACK: {
						emitInstructions(vertex.getChild(0));
						return null;
					}
					
					case RHO_SIGMA: {
						emitInstructions(vertex.getChild(0));
						return null;
					}
					
					case RHO_EXCEPTION: {
						emitInstructions(vertex.getChild(0));
						return null;
					}
					
					case SHUFFLEVECTOR: {
						Value vec1 = emitInstructions(vertex.getChild(0));
						Value vec2 = emitInstructions(vertex.getChild(1));
						Value mask = emitInstructions(vertex.getChild(2));
						Instruction inst = new ShuffleVecInstruction(vec1, vec2, mask);
						return helper(vertex, inst);
					}
					
					case INSERTELEMENT: {
						Value vector = emitInstructions(vertex.getChild(0));
						Value elt = emitInstructions(vertex.getChild(1));
						Value index = emitInstructions(vertex.getChild(2));
						Instruction inst = new InsertEltInstruction(vector, elt, index);
						return helper(vertex, inst);
					}
					
					case GETELEMENTPTR: {
						Value base = emitInstructions(vertex.getChild(0));
						Vertex<Item<LLVMLabel,LLVMParameter,Object>> indexesVertex = 
							vertex.getChild(2);
						List<Value> indexes = new ArrayList<Value>(indexesVertex.getChildCount());
						for (int i = 0; i < indexesVertex.getChildCount(); i++) {
							indexes.add(emitInstructions(indexesVertex.getChild(i)));
						}
						Instruction inst = new GEPInstruction(base, indexes);
						return helper(vertex, inst);
					}
						
					case INDEXES:
						return null;
						
					case SELECT: {
						Value cond = emitInstructions(vertex.getChild(0));
						Value v1 = emitInstructions(vertex.getChild(1));
						Value v2 = emitInstructions(vertex.getChild(2));
						Instruction inst = new SelectInstruction(cond, v1, v2);
						return helper(vertex, inst);
					}
						
					case EXTRACTELEMENT: {
						Value vec = emitInstructions(vertex.getChild(0));
						Value index = emitInstructions(vertex.getChild(1));
						Instruction inst = new ExtractEltInstruction(vec, index);
						return helper(vertex, inst);
					}
					
					case GETRESULT: {
						Value ret = emitInstructions(vertex.getChild(0));
						int index = vertex.getChild(1).getLabel().getLabel().getNumeralSelf().getValue();
						Instruction inst = new GetResultInstruction(ret, index);
						return helper(vertex, inst);
					}
					
					case MALLOC: {
						emitInstructions(vertex.getChild(0));
						Type type = vertex.getChild(1).getLabel().getLabel().getTypeSelf().getType();
						Value numElts = emitInstructions(vertex.getChild(2));
						int align = vertex.getChild(3).getLabel().getLabel().getNumeralSelf().getValue();
						Instruction inst = new MallocInstruction(type, numElts, align);
						return helper(vertex, inst);
					}
					
					case FREE: {
						emitInstructions(vertex.getChild(0));
						Value freeee = emitInstructions(vertex.getChild(1));
						Instruction inst = new FreeInstruction(freeee);
						
						newblock.addInstruction(inst);
						tempmap.put(vertex, null);
						return null;
					}
					
					case ALLOCA: {
						emitInstructions(vertex.getChild(0));
						Type type = vertex.getChild(1).getLabel().getLabel().getTypeSelf().getType();
						Value numElts = emitInstructions(vertex.getChild(2));
						int align = vertex.getChild(3).getLabel().getLabel().getNumeralSelf().getValue();
						Instruction inst = new AllocaInstruction(type, numElts, align);
						return helper(vertex, inst);
					}
					
					case VOLATILE_LOAD:
					case LOAD: {
						boolean isVolatile = label.getSimpleSelf().getOperator().equals(LLVMOperator.VOLATILE_LOAD);
						emitInstructions(vertex.getChild(0));
						Value loadee = emitInstructions(vertex.getChild(1));
						int align = vertex.getChild(2).getLabel().getLabel().getNumeralSelf().getValue();
						Instruction inst = new LoadInstruction(loadee, align, isVolatile);
						return helper(vertex, inst);
					}
					
					case VOLATILE_STORE:
					case STORE: {
						boolean isVolatile = label.getSimpleSelf().getOperator().equals(LLVMOperator.VOLATILE_STORE);
						emitInstructions(vertex.getChild(0));
						Value ptr = emitInstructions(vertex.getChild(1));
						Value value = emitInstructions(vertex.getChild(2));
						int align = vertex.getChild(3).getLabel().getLabel().getNumeralSelf().getValue();
						Instruction inst = new StoreInstruction(ptr, value, align, isVolatile);
						
						newblock.addInstruction(inst);
						tempmap.put(vertex, null);
						return null;
					}
					
					case PARAMS:
						return null;
						
//					case PARAMATTRMAP:
//						return null;
						
					case UNWIND: {
						emitInstructions(vertex.getChild(0));
						Instruction inst = UnwindInstruction.INSTANCE;
						newblock.addInstruction(inst);
						tempmap.put(vertex, null);
						return null;
					}
						
					case VOID:
						return null;
						
					case RETURNSTRUCTURE:
						return null;
						
					case VAARG: {
						emitInstructions(vertex.getChild(0));
						Value list = emitInstructions(vertex.getChild(1));
						Type type = vertex.getChild(2).getLabel().getLabel().getTypeSelf().getType();
						Instruction inst = new VaargInstruction(list, type);
						return helper(vertex, inst);
					}
						
					case IS_EXCEPTION:
						throw new IllegalArgumentException("These should not exist anymore");
					default:
						throw new IllegalArgumentException("Unhandled case: " + label);
					}
				} else if (label.isType()) {
					return null;
				} else if (label.isBinop()) {
					Binop binop = label.getBinopSelf().getOperator();
					Value lhs = emitInstructions(vertex.getChild(0));
					Value rhs = emitInstructions(vertex.getChild(1));
					BinopInstruction inst = new BinopInstruction(binop, lhs, rhs);
					return helper(vertex, inst);
				} else if (label.isCast()) {
					Cast cast = label.getCastSelf().getOperator();
					Type casttype = vertex.getChild(0).getLabel().getLabel().getTypeSelf().getType();
					Value castee = emitInstructions(vertex.getChild(1));
					CastInstruction inst = new CastInstruction(cast, casttype, castee);
					return helper(vertex, inst);
				} else if (label.isCmp()) {
					ComparisonPredicate cmp = label.getCmpSelf().getPredicate();
					Value lhs = emitInstructions(vertex.getChild(0));
					Value rhs = emitInstructions(vertex.getChild(1));
					CmpInstruction inst = new CmpInstruction(cmp, lhs, rhs);
					return helper(vertex, inst);
				} else if (label.isNumeral()) {
					return null;
				} else if (label.isParamAttr()) {
					return null;
				} else if (label.isFunction()) {
					FunctionLLVMLabel funclabel = label.getFunctionSelf();
					FunctionValue value = resolver.resolveFunction(funclabel.getFunctionName(), funclabel.getType());
					return value;
				} else if (label.isGlobal()) {
					GlobalLLVMLabel globallabel = label.getGlobalSelf();
					GlobalVariable global = resolver.resolveGlobal(globallabel.getName(), globallabel.getType());
					return global;
				} else if (label.isAlias()) {
					AliasLLVMLabel aliaslabel = label.getAliasSelf();
					AliasValue alias = resolver.resolveAlias(aliaslabel.getName(), aliaslabel.getType());
					return alias;
				} else if (label.isConstantValue()) {
					return label.getConstantValueSelf().getValue();
				} else if (label.isInlineASM()) {
					return label.getInlineASMSelf().getASM();
				} else if (label.isBasicOp()) {
					switch (label.getBasicOpSelf().getOperator()) {
					case Negate: {
						Value child = emitInstructions(vertex.getChild(0));
						BinopInstruction inst = new BinopInstruction(Binop.Xor, IntegerValue.TRUE, child);
						return helper(vertex, inst);
					}

					case And:
					case Or: {
						Value lhs = emitInstructions(vertex.getChild(0));
						Value rhs = emitInstructions(vertex.getChild(1));
						BinopInstruction inst = new BinopInstruction(
								(label.getBasicOpSelf().getOperator().equals(BasicOp.And) ? 
										Binop.And : Binop.Or),
										lhs, rhs);
						return helper(vertex, inst);
					}

					case Equals: {
						Value lhs = emitInstructions(vertex.getChild(0));
						Value rhs = emitInstructions(vertex.getChild(1));
						ComparisonPredicate pred;
						if (lhs.getType().isVectorOfFloatingPoint() || lhs.getType().isFloatingPoint()) {
							pred = FloatingPointComparisonPredicate.FCMP_OEQ;
						} else if (lhs.getType().isInteger() || 
								lhs.getType().isVectorOfInteger() || 
								(lhs.getType().isComposite() && 
										lhs.getType().getCompositeSelf().isPointer())) {
							pred = IntegerComparisonPredicate.ICMP_EQ;
						} else {
							throw new IllegalArgumentException("Cannot do comparison on type: " + lhs.getType());
						}

						CmpInstruction inst = new CmpInstruction(pred, lhs, rhs);
						return helper(vertex, inst);
					}

					default:
						throw new IllegalArgumentException("Invalid basicop: " + label.getBasicOpSelf().getOperator());
					}
				} else {
					throw new IllegalArgumentException("Invalid label: " + label);
				}
			}
		}
	}
	
	/**
	 * Convenience class to make using BlockVerticesIterator easier. 
	 */
	private static class LLVMIterator 
	extends BlockVerticesIterator<LLVMLabel,LLVMParameter,LLVMReturn,Object,LLVMPEGCFG,LLVMPEGCFGBlock>{
		public LLVMIterator(LLVMPEGCFGBlock _block) {super(_block);}
	}

	/**
	 * Assigns types to all the minipeg nodes in the entire CFG, 
	 * but only retain enough info for the variables.
	 */
	private void buildTypeMap(Map<Object,VirtualRegister> varmap) {
		// gather all vars first
		Set<Object> allvars = new HashSet<Object>();
		Map<LLVMPEGCFGBlock,Collection<?>> block2vars = 
			new HashMap<LLVMPEGCFGBlock,Collection<?>>();
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			Collection<?> vars = block.getReferencedVars();
			block2vars.put(block, vars);
			allvars.addAll(vars);
		}
		
		// set the type of the return var preemptively
		Object returnVar = this.cfg.getReturnVariable(LLVMReturn.VALUE);
		Type returnType = this.body.getHeader().getType().getPointeeType().getFunctionSelf().getReturnType();
		if (returnType.isVoid()) {
			varmap.put(returnVar, null);
		} else {
			VirtualRegister var = VirtualRegister.getVirtualRegister(returnType);
			varmap.put(returnVar, var);
		}

		// iteratively assign types to the vars
		// type of null means it's not an LLVM type, but a PEG type

		Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Type> typemap = 
			new HashMap<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Type>();
		
		while (varmap.size() < allvars.size()) {
			int varmapSize = varmap.size();
			int typemapSize = typemap.size();

			// iterate over all blocks
			for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
				Collection<?> vars = block2vars.get(block);
				if (varmap.keySet().containsAll(vars))
					continue;

				// loop over all vertices in the block
				Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> getvars = 
					new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
				for (LLVMIterator iter = new LLVMIterator(block); iter.hasNext(); ) {
					Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex = iter.next();

					Item<LLVMLabel,LLVMParameter,Object> item = vertex.getLabel();
					if (item.isVariable()) {
						getvars.add(vertex);
						if (varmap.containsKey(item.getVariable())) {
							VirtualRegister reg = varmap.get(item.getVariable());
							if (reg == null)
								setType(typemap, vertex, null);
							else
								setType(typemap, vertex, reg.getType());
						}
						continue;
					} else if (item.isParameter()) {
						// assign type (sigma has type null)
						LLVMParameter param = item.getParameter();
						if (param.isArgument()) {
							Type type = param.getArgumentSelf().getType();
							setType(typemap, vertex, type);
						} else {
							setType(typemap, vertex, null);
						}
					} else {
						// label! infer type
						this.inferLabelType(vertex, typemap);
					}
				}
				
				// check if any setvars have new types
				for (Object assigned : block.getAssignedVars()) {
					if (varmap.containsKey(assigned))
						continue;
					if (typemap.containsKey(block.getAssignment(assigned))) {
						Type type = typemap.get(block.getAssignment(assigned));
						if (type == null) {
							varmap.put(assigned, null);
						} else {
							varmap.put(
									assigned, 
									VirtualRegister.getVirtualRegister(type));
						}
					}
				}
				
				// check if any getvars have new types
				for (Vertex<Item<LLVMLabel,LLVMParameter,Object>> get : getvars) {
					Object var = get.getLabel().getVariable();
					if (varmap.containsKey(var))
						continue;
					if (typemap.containsKey(get)) {
						Type type = typemap.get(get);
						if (type == null) {
							varmap.put(var, null);
						} else {
							varmap.put(
									var, 
									VirtualRegister.getVirtualRegister(typemap.get(get)));
						}
					}
				}
				
				getvars = null;
			}
			
			if (varmap.size() <= varmapSize && typemap.size() <= typemapSize)
				throw new RuntimeException("Loop made no progress, cannot derive all types");
		}
	}
	
	
	private void setType(
			Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Type> typemap,
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex,
			Type type) {
		if (typemap.containsKey(vertex)) {
			Type old = typemap.get(vertex);
			if (old == null && type == null)
				return;
			else if (old == null || type == null)
				throw new IllegalArgumentException("Mismatched types for vertex " + vertex.getLabel());
			else if (!old.equalsType(type))
				throw new IllegalArgumentException("Mismatched types for vertex " + vertex.getLabel());
		} else {
			typemap.put(vertex, type);
		}
	}
	
	
	private void inferLabelType(
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex,
			Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Type> typemap) {

		LLVMLabel label = vertex.getLabel().getLabel();
		if (label.isSimple()) {
			switch (label.getSimpleSelf().getOperator()) {
			case INJR:
			case INJL:
			case NONSTACK:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
				setType(typemap, vertex, null);
				break;
				
			case CALL:
			case TAILCALL:
			case INVOKE: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(2), null);
				setType(typemap, vertex.getChild(3), null);
				setType(typemap, vertex.getChild(4), null);
				
				// set return type
				if (typemap.containsKey(vertex.getChild(1))) {
					Type funcType = typemap.get(vertex.getChild(1));
					Type returnType = funcType.getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf().getReturnType();
					if (returnType.isVoid())
						setType(typemap, vertex, null);
					else
						setType(typemap, vertex, returnType);
				}
				break;
			}
				
			case RHO_VALUE: {
				// this will just copy through the type of the child
				boolean root = typemap.containsKey(vertex);
				boolean child = typemap.containsKey(vertex.getChild(0));
				if (root && !child) {
					setType(typemap, vertex.getChild(0), typemap.get(vertex));
				} else if (child && !root) {
					setType(typemap, vertex, typemap.get(vertex.getChild(0)));
				}
				break;
			}
				
			case SHUFFLEVECTOR: {
				boolean root = typemap.containsKey(vertex);
				boolean v0 = typemap.containsKey(vertex.getChild(0));
				boolean v1 = typemap.containsKey(vertex.getChild(0));
				
				if (root | v0 | v1) {
					Type type = 
						(root ? typemap.get(vertex) :
							(v0 ? typemap.get(vertex.getChild(0)) :
								typemap.get(vertex.getChild(1))));
					setType(typemap, vertex, type);
					setType(typemap, vertex.getChild(0), type);
					setType(typemap, vertex.getChild(1), type);
					
					Type maskType = new VectorType(Type.getIntegerType(32), type.getCompositeSelf().getVectorSelf().getNumElements());
					setType(typemap, vertex.getChild(2), maskType);
				}
				break;
			}
			
			case INSERTELEMENT: {
				boolean root = typemap.containsKey(vertex);
				boolean v0 = typemap.containsKey(vertex.getChild(0));
				
				if (root | v0) {
					Type type = (root ? typemap.get(vertex) : typemap.get(vertex.getChild(0)));
					setType(typemap, vertex, type);
					setType(typemap, vertex.getChild(0), type);
					setType(typemap, vertex.getChild(1), type.getCompositeSelf().getVectorSelf().getElementType());
				}
				setType(typemap, vertex.getChild(2), Type.getIntegerType(32));
				break;
			}
			
			case GETELEMENTPTR: {
				setType(typemap, vertex.getChild(1), null);
				setType(typemap, vertex.getChild(2), null);

				Type type = vertex.getChild(1).getLabel().getLabel().getTypeSelf().getType();
				
				setType(typemap, vertex.getChild(0), type);
				
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> indexes = 
					vertex.getChild(2);
				for (int i = 0; i < indexes.getChildCount(); i++) {
					if (type.isComposite()) {
						CompositeType ctype = type.getCompositeSelf();
						if (ctype.isPointer()) {
							if (i != 0)
								throw new RuntimeException("point must only be base");
							type = type.getCompositeSelf().getPointerSelf().getPointeeType();
						} else if (ctype.isStructure()) {
							if (!(indexes.getChild(i).getLabel().getLabel().isConstantValue() &&
									indexes.getChild(i).getLabel().getLabel().getConstantValueSelf().getValue().isInteger()))
								throw new RuntimeException("Must be constant int");
							
							IntegerValue ivalue = indexes.getChild(i).getLabel().getLabel().getConstantValueSelf().getValue().getIntegerSelf();
							if (!ivalue.getType().equalsType(Type.getIntegerType(32)))
								throw new RuntimeException("Integer constant has wrong type");
							long index = ivalue.getLongBits();
							type = ctype.getStructureSelf().getElementType((int)index);
						} else if (ctype.isArray()) {
							type = ctype.getArraySelf().getElementType();
						} else if (ctype.isVector()) {
							type = ctype.getVectorSelf().getElementType();
						} else {
							throw new RuntimeException("Invalid composite type: " + ctype);
						}
					} else {
						throw new RuntimeException("Invalid type: " + type);
					}
				}
				
				if (indexes.getChildCount() > 0)
					type = new PointerType(type);

				setType(typemap, vertex, type);
				break;
			}
			
			case INDEXES:
				setType(typemap, vertex, null);
				break;
				
			case SELECT: {
				boolean root = typemap.containsKey(vertex);
				boolean v1 = typemap.containsKey(vertex.getChild(1));
				boolean v2 = typemap.containsKey(vertex.getChild(2));

				setType(typemap, vertex.getChild(0), Type.BOOLEAN_TYPE);
				if (root | v1 | v2) {
					Type type = 
						(root ? typemap.get(vertex) : 
							(v1 ? typemap.get(vertex.getChild(1)) :
								typemap.get(vertex.getChild(2))));
					setType(typemap, vertex, type);
					setType(typemap, vertex.getChild(1), type);
					setType(typemap, vertex.getChild(2), type);
				}
				break;
			}
			
			case EXTRACTELEMENT: {
				setType(typemap, vertex.getChild(1), Type.getIntegerType(32));
				if (typemap.containsKey(vertex.getChild(0))) {
					Type type = typemap.get(vertex.getChild(0));
					setType(
							typemap, 
							vertex, 
							type.getCompositeSelf().getVectorSelf().getElementType());
				}
				break;
			}
			
			case GETRESULT: {
				setType(typemap, vertex.getChild(1), null);
				if (typemap.containsKey(vertex.getChild(0))) {
					Type type = typemap.get(vertex.getChild(0));
					int numeral = vertex.getChild(1).getLabel().getLabel().getNumeralSelf().getValue();
					Type eltType = type.getCompositeSelf().getStructureSelf().getElementType(numeral);
					setType(typemap, vertex, eltType);
				}
				break;
			}
			
			case ALLOCA:
			case MALLOC: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(1), null);
				setType(typemap, vertex.getChild(2), Type.getIntegerType(32));
				setType(typemap, vertex.getChild(3), null);

				Type result = 
					vertex.getChild(1).getLabel().getLabel().getTypeSelf().getType();
				setType(typemap, vertex, new PointerType(result));
				break;
			}
			
			case FREE:
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				break;
			
			case VOLATILE_LOAD:
			case LOAD: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(2), null);
				if (typemap.containsKey(vertex.getChild(1))) {
					Type type = typemap.get(vertex.getChild(1));
					setType(typemap, vertex, type.getCompositeSelf().getPointerSelf().getPointeeType());
				}
				break;
			}
			
			case VOLATILE_STORE:
			case STORE: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(3), null);
				setType(typemap, vertex, null);
				if (typemap.containsKey(vertex.getChild(1))) {
					Type ptrType = typemap.get(vertex.getChild(1));
					Type pointeeType = ptrType.getCompositeSelf().getPointerSelf().getPointeeType();
					setType(typemap, vertex.getChild(2), pointeeType);
				}
				break;
			}
			
			case PARAMS:
				setType(typemap, vertex, null);
				break;
				
//			case PARAMATTRMAP: {
//				setType(typemap, vertex, null);
//				for (int i = 0; i < vertex.getChildCount(); i++)
//					setType(typemap, vertex.getChild(i), null);
//				break;
//			}
				
			case UNWIND:
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				break;
			
			case VOID:
				setType(typemap, vertex, null);
				break;
				
			case RETURNSTRUCTURE: {
				// this should only be used as a return value, never as a value
				setType(typemap, vertex, null);
				break;
			}
			
			case VAARG: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(2), null);
				
				Type type = vertex.getChild(2).getLabel().getLabel().getTypeSelf().getType();
				setType(typemap, vertex, type);
				break;
			}
			
			case IS_EXCEPTION:
				throw new RuntimeException("Should not be any IS_EXCEPTIONs by now");
				
			default:
				throw new RuntimeException("Mike forgot to handle: " + label.getSimpleSelf().getOperator());
			}
		} else if (label.isType() ||
				   label.isNumeral() ||
				   label.isParamAttr()) {
			setType(typemap, vertex, null);
		} else if (label.isBinop()) {
			boolean lhs = typemap.containsKey(vertex.getChild(0));
			boolean rhs = typemap.containsKey(vertex.getChild(1));
			if (lhs | rhs) {
				Type type = (lhs ? typemap.get(vertex.getChild(0)) : typemap.get(vertex.getChild(1)));
				setType(typemap, vertex, type);
				if (!lhs) setType(typemap, vertex.getChild(0), type);
				if (!rhs) setType(typemap, vertex.getChild(1), type);
			}
		} else if (label.isCast()) {
			// child0 is sticky, type node
			Type type = vertex.getChild(0).getLabel().getLabel().getTypeSelf().getType();
			setType(typemap, vertex, type);
		} else if (label.isCmp()) {
			// vertex is boolean
			setType(typemap, vertex, Type.BOOLEAN_TYPE);
			
			// now do args
			boolean lhs = typemap.containsKey(vertex.getChild(0));
			boolean rhs = typemap.containsKey(vertex.getChild(1));
			if (lhs & !rhs) {
				setType(
						typemap,
						vertex.getChild(1), 
						typemap.get(vertex.getChild(0)));
			} else if (rhs & !lhs) {
				setType(
						typemap,
						vertex.getChild(0), 
						typemap.get(vertex.getChild(1)));
			}
		} else if (label.isFunction()) {
			Type type = label.getFunctionSelf().getType();
			setType(typemap, vertex, new PointerType(type));
		} else if (label.isGlobal()) {
			Type type = label.getGlobalSelf().getType();
			setType(typemap, vertex, new PointerType(type));
		} else if (label.isInlineASM()) {
			Type type = label.getInlineASMSelf().getASM().getType();
			setType(typemap, vertex, type);
		} else if (label.isConstantValue()) {
			Type type = label.getConstantValueSelf().getValue().getType();
			setType(typemap, vertex, type);
		} else if (label.isBasicOp()) {
			switch (label.getBasicOpSelf().getOperator()) {
			case Negate:
				setType(typemap, vertex, Type.BOOLEAN_TYPE);
				setType(typemap, vertex.getChild(0), Type.BOOLEAN_TYPE);
				break;
				
			case And:
			case Or:
				setType(typemap, vertex, Type.BOOLEAN_TYPE);
				setType(typemap, vertex.getChild(0), Type.BOOLEAN_TYPE);
				setType(typemap, vertex.getChild(1), Type.BOOLEAN_TYPE);
				break;
				
			case Equals:
				setType(typemap, vertex, Type.BOOLEAN_TYPE);
				// can say anything about child types??
				break;
			
			default:
				throw new RuntimeException("Should not happen");
			}
		} else {
			// error
			throw new RuntimeException("Invalid label type: " + label);
		}
	}
	
	
	/**
	 * Check each block to make sure it has only 1 unwind,
	 * and remove the successors of any block with an unwind.
	 */
	private void removeUnwindFollowers() {
		LabelOperatorPattern pattern = 
			new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.UNWIND));
		
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> all = 
				new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
			
			// check the vars for unwinds
			for (Object assigned : block.getAssignedVars()) {
				Collection<? extends Vertex<Item<LLVMLabel,LLVMParameter,Object>>> matching = 
					block.getAssignment(assigned).findSatisfyingDescendents(pattern);
				if (matching.size() == 1) {
					block.setAssignment(
							assigned,
							matching.iterator().next());
				}
				all.addAll(matching);
			}
			
			// check if the bc has any unwinds in it
			if (block.getBranchCondition() != null) {
				Collection<? extends Vertex<Item<LLVMLabel,LLVMParameter,Object>>> matching =
					block.getBranchCondition().findSatisfyingDescendents(pattern);
				if (matching.size() > 0)
					throw new RuntimeException("Cannot branch after an unwind");
				all.addAll(matching);
			}

			// if there was an unwind, change the block
			if (all.size() == 1) {
				block.setBranchCondition(null);
				for (int i = block.getNumSuccs()-1; i>=0; i--)
					block.removeSucc(i);
			} else if (all.size() > 1) {
				// there should really be at most one unwind in a given block
				throw new RuntimeException("Block cannot have multiple unwinds in it");
			}
		}
	}

	
	
	
	
	/**
	 * For functions that return structs, this will make sure that there is only 
	 * 1 returnstructure. 
	 */
	private void mergeReturnStructures(boolean hasAssigns) {
		if (!hasAssigns)
			return;
		Type returnType = this.body.getHeader().getType().getPointeeType().getFunctionSelf().getReturnType();
		if (!(returnType.isComposite() && returnType.getCompositeSelf().isStructure()))
			return;
		
		LLVMReachingDefs reaching = new LLVMReachingDefs(this.cfg);
		Object getvar = this.cfg.getEndBlock().getAssignment(this.cfg.getReturnVariable(LLVMReturn.VALUE)).getLabel().getVariable();
		List<LLVMDef> returns = reaching.getTransitiveDefs(reaching.getUse(this.cfg.getEndBlock(), getvar));
		for (LLVMDef def : returns) {
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex = 
				def.getBlock().getAssignment(def.getVariable());
			if (!RETURNSTRUCTURE_PATTERN.matches(vertex))
				throw new IllegalArgumentException("Should only be returnstructures: " + vertex.getLabel());
		}
		if (returns.size() == 0)
			throw new RuntimeException("Should have some returns");
		
		// now we have multiple assigns that are all returnstructures
		int nfields = returnType.getCompositeSelf().getStructureSelf().getNumFields();
		List<Object> fieldvars = new ArrayList<Object>(nfields);
		for (int i = 0; i < nfields; i++)
			fieldvars.add(this.cfg.makeNewTemporary());
		
		// assign the pieces to the new vars
		for (LLVMDef def : returns) {
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> returnstructure = 
				def.getBlock().getAssignment(def.getVariable());
			// assign each piece to the right var
			for (int i = 0; i < nfields; i++) {
				def.getBlock().setAssignment(fieldvars.get(i), returnstructure.getChild(i));
			}
			def.getBlock().removeAssignment(def.getVariable());
		}

		// make a new returnstructure in the end block
		Object returnVar = this.cfg.getReturnVariable(LLVMReturn.VALUE);
		List<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> fieldgets = 
			new ArrayList<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>(nfields);
		LLVMPEGCFGBlock end = this.cfg.getEndBlock();
		for (int i = 0; i < nfields; i++)
			fieldgets.add(
					end.getMiniPEG().getVertex(
							Item.<LLVMLabel,LLVMParameter,Object>getVariable(fieldvars.get(i))));
		
		Vertex<Item<LLVMLabel,LLVMParameter,Object>> returnvalue = 
			end.getMiniPEG().getVertex(
					Item.<LLVMLabel,LLVMParameter,Object>getLabel(SimpleLLVMLabel.get(LLVMOperator.RETURNSTRUCTURE)),
					fieldgets);
		end.setAssignment(returnVar, returnvalue);
	}
	
	
	/**
	 * This function will look for all the assignments
	 * to the return var that are topped by INJL and remove the
	 * assignments. The actual nodes will stay (but may be GC'ed)
	 */
	private void removeINJL() {
		LLVMReachingDefs reaching = new LLVMReachingDefs(this.cfg);
		Object returnVar = this.cfg.getReturnVariable(LLVMReturn.VALUE);
		
		LabelOperatorPattern pattern = 
			new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.INJL));
		
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			if (!block.getAssignedVars().contains(returnVar))
				continue;
			
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex = 
				block.getAssignment(returnVar);
			if (pattern.matches(vertex)) {
				// INJL
				block.removeAssignment(returnVar);
				reaching = new LLVMReachingDefs(this.cfg);
			} else if (vertex.getLabel().isVariable()) {
				// variable, find transitive defs
				List<LLVMDef> defs = reaching.getTransitiveDefs(block, vertex.getLabel().getVariable());
				
				boolean any = false;
				for (LLVMDef def : defs) {
					Vertex<Item<LLVMLabel,LLVMParameter,Object>> defvertex = 
						def.getBlock().getAssignment(def.getVariable());
					if (pattern.matches(defvertex)) {
						any = true;
						def.getBlock().removeAssignment(def.getVariable());
					}
				}
				
				if (any)
					reaching = new LLVMReachingDefs(this.cfg);
			}
		}
	}
	
	private void removeINJR() {
		LabelOperatorPattern pattern = new LabelOperatorPattern(SimpleLLVMLabel.get(LLVMOperator.INJR));
		
		Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
				 Vertex<Item<LLVMLabel,LLVMParameter,Object>>> builder = 
			new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
						 Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
				public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(
						Vertex<Item<LLVMLabel,LLVMParameter,Object>> param) {
					return param.getChild(0);
				}
			};

		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			replaceInBlock(pattern, block, builder);
			//block.pruneUnreachableVertices();
		}
	}
	
	
	

	private void removeIsExceptions() {
		long start = System.currentTimeMillis();
		final Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Object> invoke2var = 
			this.makeInvokesLast();
		long check = System.currentTimeMillis();
		
		debug("***makeInvokesLast: " + (check-start) + "***");
		
		// remove all occurrences of ISEXCEPTION(CALL(...)) 
		// and replace them with FALSE
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			this.replaceInBlock(ISEXCEPTION_ON_CALL, block, MAKE_FALSE);
		}
		
		// now remove all occurrences of ISEXCEPTION(INVOKE(...))
		{
			Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
					 Vertex<Item<LLVMLabel,LLVMParameter,Object>>> builder = 
				new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
							 Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
					public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(
							Vertex<Item<LLVMLabel,LLVMParameter,Object>> param) {
						Object var = invoke2var.get(param.getChild(0));
						return param.getGraph().getVertex(
								Item.<LLVMLabel,LLVMParameter,Object>getVariable(var));
					}
				};
				
			for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
				this.replaceInBlock(ISEXCEPTION_ON_INVOKE, block, builder);
				//block.pruneUnreachableVertices();
			}
		}
		
		// only things left are ISEXCEPTION(var)
		
		LLVMReachingDefs reachingDefs = 
			new LLVMReachingDefs(this.cfg);
		
		List<LLVMUse> isExceptions = this.getIsExceptions(reachingDefs);
		
		Map<LLVMUse,List<LLVMDef>> fromCalls = 
			new HashMap<LLVMUse,List<LLVMDef>>();
		Map<LLVMUse,List<LLVMDef>> fromInvokes = 
			new HashMap<LLVMUse,List<LLVMDef>>();
		
		// find the entries that resulted from CALL or TAILCALL
		for (LLVMUse use : isExceptions) {
			boolean sawCall = false;
			boolean sawInvoke = false;

			List<LLVMDef> sources = 
				reachingDefs.getTransitiveDefs(use);
			for (LLVMDef def : sources) {
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> defvertex = 
					def.getBlock().getAssignment(def.getVariable());
				
				if (defvertex.getLabel().isLabel()) {
					if (CALL_PATTERN.matches(defvertex) ||
						TAILCALL_PATTERN.matches(defvertex)) {
						sawCall = true;
					} else if (INVOKE_PATTERN.matches(defvertex)) {
						sawInvoke = true;
					} else {
						throw new RuntimeException("Invalid source for isException: " + defvertex.getLabel().getLabel());
					}
				} else {
					throw new RuntimeException("Invalid source for isException: " + defvertex.getLabel());
				}
			}
			
			if (sawCall & (!sawInvoke)) {
				// call!
				fromCalls.put(use, sources);
			} else if (sawInvoke & (!sawCall)) {
				// invoke!
				fromInvokes.put(use, sources);
			} else {
				// invalid!
				throw new RuntimeException("sawCall & sawInvoke = " + (sawCall & sawInvoke));
			}
		}

		start = System.currentTimeMillis();
		this.removeIndirectIsExceptionCalls(fromCalls);
		check = System.currentTimeMillis();
		
		debug("***removeIndirectIsExceptionCalls: " + (check-start) + "***");
		
		start = System.currentTimeMillis();
		this.removeIndirectIsExceptionInvokes(fromInvokes, invoke2var);
		check = System.currentTimeMillis();

		debug("***removeIndirectIsExceptionInvokes: " + (check-start) + "***");
	}
	
	
	
	/**
	 * This only needs to handle ISEXCEPTIONS on variables
	 * whose defs are CALLs or TAILCALLs.
	 * Turn them into FALSEs.
	 */
	private void removeIndirectIsExceptionCalls(
			Map<LLVMUse,List<LLVMDef>> fromCalls) {
		for (LLVMUse use : fromCalls.keySet()) {
			Object var = use.getVariable();
			LabelOnVarPattern pattern = 
				new LabelOnVarPattern(SimpleLLVMLabel.get(LLVMOperator.IS_EXCEPTION), var);
			replaceInBlock(pattern, use.getBlock(), MAKE_FALSE);
		}
	}
	
	
	/**
	 * Removes ISEXCEPTION(var) where var is defined by an INVOKE.
	 * Replaces the ISEXCEPTION(var) with a get of the INVOKE's 
	 * exception var from makeInvokesLast.
	 */
	private void removeIndirectIsExceptionInvokes(
			Map<LLVMUse,List<LLVMDef>> fromInvokes,
			Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Object> invoke2var) {
		// replace every invoke with its variable
		
		// do the 1-defs first
		for (LLVMUse use : fromInvokes.keySet()) {
			List<LLVMDef> defs = fromInvokes.get(use);
			if (defs.size() != 1)
				continue;
			
			// replace with getvar
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> invoke = 
				defs.get(0).getBlock().getAssignment(defs.get(0).getVariable());

			final Object isexceptionVar = use.getVariable();
			final Object invokeVar = invoke2var.get(invoke);

			replaceInBlock(
					new LabelOnVarPattern(
							SimpleLLVMLabel.get(LLVMOperator.IS_EXCEPTION), 
							isexceptionVar),
					use.getBlock(), 
					new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
								 Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
						public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(
								Vertex<Item<LLVMLabel,LLVMParameter,Object>> param) {
							return param.getGraph().getVertex(
									Item.<LLVMLabel,LLVMParameter,Object>getVariable(invokeVar));
						}
					});
		}

		// now handle the multiply assigned ones
		
		Map<LLVMPEGCFGBlock,List<LLVMPEGCFGBlock>> block2newsuccs = 
			new HashMap<LLVMPEGCFGBlock,List<LLVMPEGCFGBlock>> ();
		for (LLVMUse use : fromInvokes.keySet()) {
			List<LLVMDef> defs = fromInvokes.get(use);
			if (defs.size() <= 1)
				continue;
			
			// multiple INVOKEs! define a merge var and add assignments
			final Object mergeVar = this.cfg.makeNewTemporary();
			
			for (LLVMDef def : defs) {
				LLVMPEGCFGBlock block = def.getBlock();
				List<LLVMPEGCFGBlock> newsuccs = block2newsuccs.get(block);
				
				if (newsuccs == null) {
					// build newsuccs
					newsuccs = new ArrayList<LLVMPEGCFGBlock>(block.getNumSuccs());
					block2newsuccs.put(block, newsuccs);
					
					// build new successors
					for (int i = 0; i < block.getNumSuccs(); i++) {
						LLVMPEGCFGBlock newsucc = this.cfg.makeNewBlock();
						newsuccs.add(newsucc);
						newsucc.addSucc(block.getSucc(i));
					}

					// set block to have new successors
					for (int i = block.getNumSuccs()-1; i>=0; i--) {
						block.removeSucc(i);
					}
					for (LLVMPEGCFGBlock newsucc : newsuccs) {
						block.addSucc(newsucc);
					}
				}
				
				// add assignment to mergeVar
				for (LLVMPEGCFGBlock newsucc : newsuccs) {
					newsucc.setAssignment(
							mergeVar,
							newsucc.getMiniPEG().getVertex(
									Item.<LLVMLabel,LLVMParameter,Object>getVariable(def.getVariable())));
				}
			}
			
			// now replace the original use of ISEXCEPTION(var)
			replaceInBlock(
					new LabelOnVarPattern(
							SimpleLLVMLabel.get(LLVMOperator.IS_EXCEPTION), 
							use.getVariable()),
					use.getBlock(), 
					new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
								 Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
						public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(
								Vertex<Item<LLVMLabel,LLVMParameter,Object>> param) {
							return param.getGraph().getVertex(
									Item.<LLVMLabel,LLVMParameter,Object>getVariable(mergeVar));
						}
					});
		}
	}
	
	
	
	/**
	 * This method will attempt to replace all the nodes in 
	 * the given block with ones created by
	 * builder.get(block), but only if they match pattern.
	 * This will proceed by recursive descent on the assigned 
	 * variable vertices, and recursively rebuild them if necessary.
	 * Will also do the branch condition.
	 */
	private void replaceInBlock(
			Pattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> pattern,
			LLVMPEGCFGBlock block,
			Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,
					 Vertex<Item<LLVMLabel,LLVMParameter,Object>>> builder) {
		
		Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Vertex<Item<LLVMLabel,LLVMParameter,Object>>> changed = 
			new HashMap<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
		
		for (LLVMIterator iter = new LLVMIterator(block); iter.hasNext(); ) {
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> next = iter.next();
			if (changed.containsKey(next))
				continue;
			
			if (pattern.matches(next)) {
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> newnext = 
					builder.get(next);
				changed.put(next, newnext);
				
				for (Vertex<Item<LLVMLabel,LLVMParameter,Object>> parent : next.getParents()) {
					parent.replaceChild(next, newnext);
				}
			}
			
		}
		
		for (Object assigned : block.getAssignedVars()) {
			if (changed.containsKey(block.getAssignment(assigned))) {
				block.setAssignment(
						assigned, 
						changed.get(block.getAssignment(assigned)));
			}
		}
		if (block.getBranchCondition() != null) {
			if (changed.containsKey(block.getBranchCondition())) {
				block.setBranchCondition(changed.get(block.getBranchCondition()));
			}
		}
	}
	

	
	
	/**
	 * Result is a list of Uses, where the use actually refers to the
	 * argument of the IS_EXCEPTION, but it still gives the location
	 * of the term.
	 */
	private List<LLVMUse> getIsExceptions(
			LLVMReachingDefs reachingDefs) {
		List<LLVMUse> result = new ArrayList<LLVMUse>();
		
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> vertices = 
				new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
			for (Object assigned : block.getAssignedVars()) {
				vertices.addAll(block.getAssignment(assigned).findSatisfyingDescendents(ISEXCEPTION_PATTERN));
			}
			if (block.getBranchCondition() != null) {
				vertices.addAll(block.getBranchCondition().findSatisfyingDescendents(ISEXCEPTION_PATTERN));
			}

			// make uses out of the children of the ISEXCEPTIONS
			for (Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex : vertices) {
				if (!vertex.getChild(0).getLabel().isVariable())
					throw new RuntimeException("ISEXCEPTION child must be a variable");
				result.add(reachingDefs.getUse(block, vertex.getChild(0).getLabel().getVariable()));
			}
		}
		
		return result;
	}
	
	
	
	
	

	
	/**
	 * Makes all INVOKE nodes be the last thing in their blocks.
	 * This requires splitting blocks at the INVOKES and making new variables for them.
	 * This will also create exception condition variables for every INVOKE and 
	 * return a map from INVOKE vertices to condition variables.
	 */
	private Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Object> makeInvokesLast() {
		LinkedList<LLVMPEGCFGBlock> worklist = 
			new LinkedList<LLVMPEGCFGBlock>(this.cfg.getBlocks());
		
		Map<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Object> invoke2var = 
			new HashMap<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Object>();

		worklistLoop:
		while (!worklist.isEmpty()) {
			LLVMPEGCFGBlock block = worklist.removeFirst();
			
			Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> invokes = 
				new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
			for (Object assigned : block.getAssignedVars()) {
				invokes.addAll(
						block.getAssignment(assigned).findSatisfyingDescendents(INVOKE_PATTERN));
			}
			if (block.getBranchCondition()!=null) {
				invokes.addAll(block.getBranchCondition().findSatisfyingDescendents(INVOKE_PATTERN));
			}
			if (invokes.size() == 0)
				continue;
			
			// found an invoke! check if it needs to be split
			
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> minimalInvoke = 
				findMinimalInvoke(invokes);
			Collection<? extends Vertex<Item<LLVMLabel,LLVMParameter,Object>>> ancestors = 
				minimalInvoke.getAncestors();
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> bc =
				block.getBranchCondition();
			
			// check for condition when you don't split
			if (invokes.size() == 1 &&
				ancestors.size() == 2 &&
				bc != null &&
				ISEXCEPTION_PATTERN.matches(bc) &&
				ancestors.contains(bc)) {
				// block is already good, don't need to split.
				// just add the condition variable and assignments to
				//   the return and unwind blocks
				
				LLVMPEGCFGBlock unwindBlock = block.getSucc(0);
				LLVMPEGCFGBlock returnBlock = block.getSucc(1);
				Object newvar = this.cfg.makeNewTemporary();
				
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> unwindTrue = 
					unwindBlock.getMiniPEG().getVertex(
							Item.<LLVMLabel,LLVMParameter,Object>getLabel(
									new ConstantValueLLVMLabel(IntegerValue.TRUE)));
				unwindBlock.setAssignment(newvar, unwindTrue);
				
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> returnFalse = 
					returnBlock.getMiniPEG().getVertex(
							Item.<LLVMLabel,LLVMParameter,Object>getLabel(
									new ConstantValueLLVMLabel(IntegerValue.FALSE)));
				returnBlock.setAssignment(newvar, returnFalse);
				
				invoke2var.put(minimalInvoke, newvar);
				
				block.getMiniPEG().removeVertex(bc);
				block.setBranchCondition(minimalInvoke);
				
				continue worklistLoop;
			}
			
			// need to split
			
			Object invokevar = null;
			for (Object assigned : block.getAssignedVars()) {
				if (block.getAssignment(assigned).equals(minimalInvoke)) {
					invokevar = assigned;
					break;
				}
			}
			if (invokevar == null) {
				invokevar = this.cfg.makeNewTemporary();
				block.setAssignment(invokevar, minimalInvoke);
			}
			
			LLVMPEGCFGBlock newblock = this.cfg.makeNewBlock();
			
			// for each var, check to see if it needs to be split
			Collection<Object> copy = new ArrayList<Object>(block.getAssignedVars());
			for (Object assigned : copy) {
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> assignment = 
					block.getAssignment(assigned);
				if (assignment.equals(minimalInvoke))
					continue;
				
				if (!assignment.getDescendents().contains(minimalInvoke))
					continue;
				
				// need to split this one!
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> newassignment =  
					copyUpTo(assignment, minimalInvoke, invokevar, newblock);
				block.removeAssignment(assigned);
				newblock.setAssignment(assigned, newassignment);
			}
			
			// now do bc!
			if (bc != null) {
				if (bc.getDescendents().contains(minimalInvoke)) {
					// split!
					Vertex<Item<LLVMLabel,LLVMParameter,Object>> newbc =  
						copyUpTo(bc, minimalInvoke, invokevar, newblock);
					newblock.setBranchCondition(newbc);
					block.setBranchCondition(null);
				}
			}
			
			// now establish the new BC
			bc = minimalInvoke;
			block.setBranchCondition(bc);
			
			// now make the return/unwind blocks
			LLVMPEGCFGBlock returnBlock = this.cfg.makeNewBlock();
			LLVMPEGCFGBlock unwindBlock = this.cfg.makeNewBlock();
			Object newvar = this.cfg.makeNewTemporary();

			Vertex<Item<LLVMLabel,LLVMParameter,Object>> unwindTrue = 
				unwindBlock.getMiniPEG().getVertex(
						Item.<LLVMLabel,LLVMParameter,Object>getLabel(
								new ConstantValueLLVMLabel(IntegerValue.TRUE)));
			unwindBlock.setAssignment(newvar, unwindTrue);
			
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> returnFalse = 
				returnBlock.getMiniPEG().getVertex(
						Item.<LLVMLabel,LLVMParameter,Object>getLabel(
								new ConstantValueLLVMLabel(IntegerValue.FALSE)));
			returnBlock.setAssignment(newvar, returnFalse);
			
			invoke2var.put(minimalInvoke, newvar);
			
			// reset the successors
			for (LLVMPEGCFGBlock succ : block.getSuccs())
				newblock.addSucc(succ);
			for (int i = block.getNumSuccs()-1; i >= 0; i--)
				block.removeSucc(i);
			returnBlock.addSucc(newblock);
			unwindBlock.addSucc(newblock);
			block.addSucc(unwindBlock);
			block.addSucc(returnBlock);
			
			// add to worklist
			worklist.addLast(newblock);
		}
		
		return invoke2var;
	}
	
	
	/**
	 * Assumes that 'bottom' is some descendant of 'root', 
	 * will rebuild 'root' in block, but replacing 'bottom' 
	 * with a get of 'replaceVar'.
	 * Returns the resulting new root.
	 * IMPORTANT: Copies nodes into a different block than they came from 
	 */
	private Vertex<Item<LLVMLabel,LLVMParameter,Object>> copyUpTo(
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> root,
			Vertex<Item<LLVMLabel,LLVMParameter,Object>> bottom,
			Object replaceVar,
			LLVMPEGCFGBlock block) {
		if (root.equals(bottom)) {
			return block.getMiniPEG().getVertex(
					Item.<LLVMLabel,LLVMParameter,Object>getVariable(replaceVar));
		} else {
			// not bottom, build new
			List<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> children = 
				new ArrayList<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> ();
			for (int i = 0; i < root.getChildCount(); i++) {
				children.add(copyUpTo(root.getChild(i), bottom, replaceVar, block));
			}
			return block.getMiniPEG().getVertex(root.getLabel(), children);
		}
	}
	
	
	
	private Vertex<Item<LLVMLabel,LLVMParameter,Object>> findMinimalInvoke(
			Collection<? extends Vertex<Item<LLVMLabel,LLVMParameter,Object>>> invokes) {
		for (Vertex<Item<LLVMLabel,LLVMParameter,Object>> invoke : invokes) {
			if (invoke.findSatisfyingDescendents(INVOKE_PATTERN).size() == 1)
				return invoke;
		}
		throw new IllegalArgumentException("Cannot find minimal invoke");
	}
	
	
	
	/**
	 * Checks the whole CFG and makes sure that every operator that
	 * has sticky children also has allowable children.
	 * Returns true iff all stickiness constraints are met.
	 */
	private boolean checkSticky() {
		StickyPredicate<LLVMLabel> sticky = LLVMLabelStickyPredicate.INSTANCE;
		StickyPredicate<FlowValue<LLVMParameter,LLVMLabel>> flowsticky = 
			new FlowValueStickyPredicate<LLVMLabel,LLVMParameter>(sticky);

		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> roots = 
				new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
			for (Object setvar : block.getAssignedVars()) {
				roots.add(block.getAssignment(setvar));
			}
			if (block.getBranchCondition() != null)
				roots.add(block.getBranchCondition());

			// now look for sticky parents
			
			LinkedList<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> worklist = 
				new LinkedList<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>(roots);
			roots = null;
			Set<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> seen = 
				new HashSet<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>();
			while (!worklist.isEmpty()) {
				Vertex<Item<LLVMLabel,LLVMParameter,Object>> next = 
					worklist.removeFirst();
				if (seen.contains(next))
					continue;
				seen.add(next);
				
				if (!next.getLabel().isLabel())
					continue;
				
				LLVMLabel label = next.getLabel().getLabel();
				for (int i = 0; i < next.getChildCount(); i++) {
					if (!sticky.isSticky(label, i))
						continue;
					
					FlowValue<LLVMParameter,LLVMLabel> flowchild;
					if (next.getChild(i).getLabel().isVariable()) {
						// hack: replace variable with sigma parameter
						flowchild = FlowValue.<LLVMParameter,LLVMLabel>createParameter(LLVMParameter.SIGMA);
					}
					else if (next.getChild(i).getLabel().isParameter())
						flowchild = FlowValue.<LLVMParameter,LLVMLabel>createParameter(
								next.getChild(i).getLabel().getParameter());
					else if (next.getChild(i).getLabel().isLabel())
						flowchild = FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								next.getChild(i).getLabel().getLabel(), this.ambassador);
					else
						throw new RuntimeException("This should never happen");
					
					FlowValue<LLVMParameter,LLVMLabel> flowparent = 
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								label, this.ambassador);
					
					if (!flowsticky.allowsChild(flowparent, i, flowchild))
						return false;
				}

				worklist.addAll(next.getChildren());
			}
		}
		
		return true;
	}
	
	
	
	
	
	
	
	/**
	 * Creates a new variable and reroutes all assignments to 
	 * the return variable to be assignments to the new variable.
	 * Then makes a new end block that sets the return var to the new var.
	 */
	private boolean makeReturnLast() {
		final Object newvar = this.cfg.makeNewTemporary();
		final Object returnVar = this.cfg.getReturnVariable(LLVMReturn.VALUE);
		
		Pattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>> returnVarPattern = 
			new AbstractPattern<Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
				public boolean matches(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
					return vertex.getLabel().isVariable() &&
					vertex.getLabel().getVariable().equals(returnVar);
				}
			};
			
		Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Vertex<Item<LLVMLabel,LLVMParameter,Object>>> builder = 
			new Function<Vertex<Item<LLVMLabel,LLVMParameter,Object>>,Vertex<Item<LLVMLabel,LLVMParameter,Object>>>() {
				public Vertex<Item<LLVMLabel,LLVMParameter,Object>> get(Vertex<Item<LLVMLabel,LLVMParameter,Object>> vertex) {
					return vertex.getGraph().getVertex(
							Item.<LLVMLabel,LLVMParameter,Object>getVariable(newvar));
				}
			};
			
		boolean hasAssigns = false;
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			if (block.getAssignedVars().contains(returnVar)) {
				hasAssigns = true;
				break;
			}
		}
		if (!hasAssigns) {
			return false;
		}
			
		for (LLVMPEGCFGBlock block : this.cfg.getBlocks()) {
			if (block.getAssignedVars().contains(returnVar)) {
				block.setAssignment(newvar, block.getAssignment(returnVar));
				block.removeAssignment(returnVar);
			}
			replaceInBlock(returnVarPattern, block, builder);
			//block.pruneUnreachableVertices();
		}
		
		LLVMPEGCFGBlock newend = this.cfg.makeNewBlock();
		this.cfg.getEndBlock().addSucc(newend);
		newend.setAssignment(
				returnVar,
				newend.getMiniPEG().getVertex(
						Item.<LLVMLabel,LLVMParameter,Object>getVariable(newvar)));
		this.cfg.setEndBlock(newend);
		return true;
	}
}
