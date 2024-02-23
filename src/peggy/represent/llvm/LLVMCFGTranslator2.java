package peggy.represent.llvm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import llvm.bitcode.ReferenceResolver;
import llvm.instructions.AllocaInstruction;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.CallInstruction;
import llvm.instructions.Cast;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.ExtractValueInstruction;
import llvm.instructions.FreeInstruction;
import llvm.instructions.GEPInstruction;
import llvm.instructions.GetResultInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.InsertValueInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.LoadInstruction;
import llvm.instructions.MallocInstruction;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVecInstruction;
import llvm.instructions.StoreInstruction;
import llvm.instructions.TerminatorInstruction;
import llvm.instructions.VSelectInstruction;
import llvm.instructions.VaargInstruction;
import llvm.types.CompositeType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.ConstantInlineASM;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import util.Function;
import util.VariaticFunction;
import eqsat.CFGTranslator;

/**
 * This class is the CFGTranslator for LLVMCFG's.
 */
public class LLVMCFGTranslator2<E> implements CFGTranslator<LLVMBlock, LLVMVariable, E> {
	private static boolean DEBUG = false;
	protected static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMCFGTranslator2: " + message);
	}
	
	protected final LLVMCFG cfg;
	protected final VariaticFunction<LLVMLabel,E,E> converter;
	protected final Function<LLVMParameter,E> paramConverter;
	protected final ReferenceResolver resolver;
	protected final Function<VirtualRegister,LLVMVariable> registerMap;
	protected final Function<FunctionValue.ArgumentValue,LLVMVariable> argumentMap;
	protected int anonymousFunctionCounter = 0;
	protected int anonymousGlobalCounter = 0;
	protected int anonymousAliasCounter = 0;
	protected GEPForcingPolicy forcingPolicy = GEPForcingPolicy.NONE;
	
	protected LLVMCFGTranslator2(
			LLVMCFG _cfg, 
			VariaticFunction<LLVMLabel,E,E> _converter,
			Function<LLVMParameter,E> _paramConverter,
			ReferenceResolver _resolver,
			Function<VirtualRegister,LLVMVariable> _registerMap,
			Function<FunctionValue.ArgumentValue,LLVMVariable> _argumentMap) {
		this.cfg = _cfg;
		this.converter = _converter;
		this.paramConverter = _paramConverter;
		this.resolver = _resolver;
		this.registerMap = _registerMap;
		this.argumentMap = _argumentMap;
	}
	
	public GEPForcingPolicy getGEPForcingPolicy() {return this.forcingPolicy;}
	public void setGEPForcingPolicy(GEPForcingPolicy b) {
		if (b==null)
			this.forcingPolicy = GEPForcingPolicy.NONE;
		else
			this.forcingPolicy = b;
	}
	
	private static final class ValueOrVariable {
		private final boolean isValue;
		private final Value value;
		private final LLVMVariable variable;
		private final int index;
		
		public ValueOrVariable(int _index, Value _value) {
			this.value = _value;
			this.isValue = true;
			this.variable = null;
			this.index = _index;
		}
		
		public ValueOrVariable(int _index, LLVMVariable _variable) {
			this.value = null;
			this.isValue = false;
			this.variable = _variable;
			this.index = _index;
		}
		
		public int getIndex() {return this.index;}
		public boolean isValue() {return this.isValue;}
		public Value getValue() {
			if (!this.isValue)
				throw new UnsupportedOperationException();
			return this.value;
		}
		public LLVMVariable getVariable() {
			if (this.isValue)
				throw new UnsupportedOperationException();
			return this.variable;
		}
		
		public String toString() {
			if (this.isValue)
				return "[" + this.value + ", " + this.index + "]";
			else
				return "[" + this.variable + ", " + this.index + "]";
		}
	}
	
	public Function<LLVMVariable,E> getOutputs(final LLVMBlock block, final Function<LLVMVariable,E> inputs) {
		return new Function<LLVMVariable,E>() {
			private final List<Map<Value,E>> valueCache;
			private final List<Map<LLVMVariable,E>> variableCache;
			private final LinkedList<ValueOrVariable> worklist;
			private final Map<LLVMVariable,E> inputVarCache;
			
			{// initializer
				this.worklist = new LinkedList<ValueOrVariable>();
				this.variableCache = new ArrayList<Map<LLVMVariable,E>>(block.getNumInstructions());
				this.valueCache = new ArrayList<Map<Value,E>>(block.getNumInstructions());
				for (int i = 0; i < block.getNumInstructions(); i++) {
					this.valueCache.add(new HashMap<Value,E>());
					this.variableCache.add(new HashMap<LLVMVariable,E>());
				}
				this.inputVarCache = new HashMap<LLVMVariable,E>();
			}
			
			private boolean isComputed(ValueOrVariable... vs) {
				return isComputed(Arrays.asList(vs));
			}
			private boolean isComputed(List<ValueOrVariable> vs) {
				for (ValueOrVariable v : vs) {
					if (v.getIndex() < 0)
						continue;
					if (v.isValue()) {
						if (!this.valueCache.get(v.getIndex()).containsKey(v.getValue()))
							return false;
					} else {
						if (!this.variableCache.get(v.getIndex()).containsKey(v.getVariable()))
							return false;
					}
				}
				return true;
			}
			
			private boolean addWorklist(ValueOrVariable... vs) {
				return addWorklist(Arrays.asList(vs));
			}
			private boolean addWorklist(List<ValueOrVariable> vs) {
				for (int i = vs.size()-1; i >= 0; i--) {
					if (!isComputed(vs.get(i)))
						this.worklist.addFirst(vs.get(i));
				}
				return false;
			}
			
			public E get(LLVMVariable var) {
				if (var == null) {
					// branch condition!
					return getBranchCondition();
				}
				
				if (block.getNumInstructions() == 0) {
					if (var.equals(LLVMVariable.SIGMA) && block.isEnd()) {
						E sigma = inputs.get(var);
						E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.NONSTACK), sigma);
						return result;
					} else {
						return inputs.get(var);
					}
				}
				
				this.worklist.addLast(new ValueOrVariable(block.getNumInstructions()-1, var));
				processWorklist();
				
				if (var.equals(LLVMVariable.SIGMA) && block.isEnd()) {
					// add on the nonstack
					E sigma = getOutputAt(block.getNumInstructions()-1, var);
					E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.NONSTACK), sigma);
					
					System.out.println("added the nonstack");
					
					return result;
				} else {
					return getOutputAt(block.getNumInstructions()-1, var);
				}
			}

			private E getBranchCondition() {
				CFGInstruction last = block.getLastInstruction();
				if (last.isIf()) {
					IfCFGInstruction ifinst = last.getIfSelf();
					this.worklist.add(new ValueOrVariable(block.getNumInstructions()-1, ifinst.getCondition()));
					processWorklist();
					return valueOf(block.getNumInstructions()-1, ifinst.getCondition());
				} else if (last.isIfException()) {
					IfExceptionCFGInstruction ifexception = last.getIfExceptionSelf();
					this.worklist.add(new ValueOrVariable(block.getNumInstructions()-1, ifexception.getSource()));
					processWorklist();
					E sourceE = getOutputAt(block.getNumInstructions()-1, ifexception.getSource());
					E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.IS_EXCEPTION), sourceE);
					return result;
				} else {
					throw new RuntimeException("Block is not a branch point");
				}
			}
			
			private void processWorklist() {
				while (!this.worklist.isEmpty()) {
					ValueOrVariable next = this.worklist.removeFirst();
					if (next.isValue()) {
						computeValueOf(next.getIndex(), next.getValue());
					} else {
						computeOutputAt(next.getIndex(), next.getVariable());
					}
				}
			}
			

			private E getOutputAt(int index, LLVMVariable var) {
				if (index < 0) {
					E result;
					if (this.inputVarCache.containsKey(var)) {
						result = this.inputVarCache.get(var);
					} else {
						result = inputs.get(var);
						this.inputVarCache.put(var, result);
					}
					return result;
				}
				else return this.variableCache.get(index).get(var);
			}
			private E valueOf(int index, Value value) {
				return this.valueCache.get(index).get(value);
			}
			
			private boolean computeOutputAt(final int index, final LLVMVariable var) {
				if (index < 0) return true;
				Map<LLVMVariable,E> cache = this.variableCache.get(index);
				if (cache.containsKey(var)) return true;

				ValueOrVariable me = new ValueOrVariable(index, var);
				
				CFGInstruction inst = block.getInstruction(index);
				// enumerate all instruction types
				if (inst.isSimple()) {
					return computeOutputAtSimple(index, var, inst.getSimpleSelf(), cache);
				} else if (inst.isExtractException()) {
					if (block.hasVariable(inst) && block.getAssignment(inst).equals(var)) {
						ExtractExceptionCFGInstruction extract = inst.getExtractExceptionSelf();
						
						ValueOrVariable varV = new ValueOrVariable(index-1, extract.getSource());
						if (isComputed(varV)) {
							E varE = getOutputAt(index-1, extract.getSource());
							E rhoExceptionE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_EXCEPTION), varE);
							E injlE = converter.get(SimpleLLVMLabel.get(LLVMOperator.INJL), rhoExceptionE);
							cache.put(var, injlE);
							return true;
						} else
							return addWorklist(varV, me);
					}
				} else if (inst.isExtractValue()) {
					if (block.hasVariable(inst) && block.getAssignment(inst).equals(var)) {
						ExtractValueCFGInstruction extract = inst.getExtractValueSelf();

						ValueOrVariable varV = new ValueOrVariable(index-1, extract.getSource());
						if (isComputed(varV)) {
							E varE = getOutputAt(index-1, extract.getSource());
							E rhoValueE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE), varE);
							cache.put(var, rhoValueE);
							return true;
						} else {
							return addWorklist(varV, me);
						}
					}
				} else if (inst.isIf()) {
					// skip!
				} else if (inst.isIfException()) {
					// skip!
				} else if (inst.isCopy()) {
					if (block.hasVariable(inst) && block.getAssignment(inst).equals(var)) {
						CopyCFGInstruction copy = inst.getCopySelf();
						ValueOrVariable resultV = new ValueOrVariable(index-1, copy.getValue());
						if (isComputed(resultV)) {
							E result = getOutputAt(index-1, copy.getValue());
							cache.put(var, result);
							return true;
						} else
							return addWorklist(resultV, me);
					}
				} else {
					throw new RuntimeException("Mike forgot to handle: " + inst.getClass());
				}
					
				// default fallthrough
				ValueOrVariable resultV = new ValueOrVariable(index-1, var);
				if (isComputed(resultV)) {
					E resultE = getOutputAt(index-1, var);
					cache.put(var, resultE);
					return true;
				} else
					return addWorklist(resultV, me);
			}
			


			// this code gets reused, so i factored it out
			private E computeGEP(GEPInstruction gep, int index) {
				E baseE = valueOf(index, gep.getBaseValue());
				List<E> indexes = new ArrayList<E>(gep.getNumIndexes());

				switch (forcingPolicy) {
				case NONE:
					for (int i = 0; i < gep.getNumIndexes(); i++) {
						indexes.add(valueOf(index, gep.getIndex(i)));
					}
					break;
				case FORCE_32: {
					Type i32 = Type.getIntegerType(32);
					for (int i = 0; i < gep.getNumIndexes(); i++) {
						E indexValue = valueOf(index, gep.getIndex(i));
						if (!gep.getIndex(i).getType().equalsType(i32)) {
							// is 64, trunc to 32
							E cast = converter.get(new CastLLVMLabel(Cast.Trunc), 
									converter.get(new TypeLLVMLabel(i32)),
									indexValue);
							indexes.add(cast);
						} else {
							indexes.add(indexValue);
						}
					}
					break;
				}
				case FORCE_64: {
					Type gepType = gep.getBaseValue().getType();
					Type i64 = Type.getIntegerType(64);
					for (int i = 0; i < gep.getNumIndexes(); i++) {
						Value indexI = gep.getIndex(i);
						E indexValue = valueOf(index, indexI);

						// if type has wrong size (and is not an i32 for a structure)
						// then cast it
						if (!indexI.getType().equalsType(i64) &&
								!(gepType.isComposite() && gepType.getCompositeSelf().isStructure())) {
							// is 32, sext to 64
							E cast = converter.get(new CastLLVMLabel(Cast.SExt), 
									converter.get(new TypeLLVMLabel(i64)),
									indexValue);
							indexes.add(cast);
						} else {
							indexes.add(indexValue);
						}

						// update gepType
						if (!gepType.isComposite())
							throw new RuntimeException("GEP type should be composite: " + gepType);
						CompositeType ctype = gepType.getCompositeSelf();
						if (ctype.isPointer() || ctype.isArray() || ctype.isVector()) {
							gepType = ctype.getElementType(0);
						} else if (ctype.isStructure()) {
							int bits = indexI.getIntegerSelf().getIntBits();
							gepType = ctype.getElementType(bits);
						} else {
							throw new RuntimeException("Unknown composite type: " + ctype);
						}
					}
					break;
				}
				default:
					throw new RuntimeException("Unknown GEP forcing policy: " + forcingPolicy);
				}

				E basetypeE = converter.get(new TypeLLVMLabel(gep.getBaseValue().getType()));
				E indexesE = converter.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES), indexes);
				E result = gep.isInbounds() ?
						converter.get(SimpleLLVMLabel.get(LLVMOperator.INBOUNDSGETELEMENTPTR), baseE, basetypeE, indexesE) :
						converter.get(SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR), baseE, basetypeE, indexesE);

				return result;
			}
					
			
			
			private boolean computeOutputAtSimple(
					final int index, final LLVMVariable var, 
					final SimpleCFGInstruction simple, 
					final Map<LLVMVariable,E> cache) {
				Instruction llvminst = simple.getInstruction();

				ValueOrVariable me = new ValueOrVariable(index, var);
				
				if (llvminst.isTerminator()) {
					TerminatorInstruction term = llvminst.getTerminatorSelf();
					if (term.isRet()) {
						if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
							RetInstruction ret = term.getRetSelf();
							
							E resultE = null;
							List<ValueOrVariable> resultVs = new ArrayList<ValueOrVariable>();
							boolean resultB;
							if (ret.isVoid()) {
								resultE = converter.get(SimpleLLVMLabel.get(LLVMOperator.VOID));
								resultB = true;
							} else if (ret.getNumReturnValues() == 1) {
								ValueOrVariable rv = new ValueOrVariable(index, ret.getReturnValue(0));
								resultVs.add(rv);
								if (resultB = isComputed(rv))
									resultE = valueOf(index, ret.getReturnValue(0));
							} else {
								List<E> returns = new ArrayList<E>();
								resultB = true;
								for (int i = 0; i < ret.getNumReturnValues(); i++) {
									ValueOrVariable rv = new ValueOrVariable(index, ret.getReturnValue(i));
									resultVs.add(rv);
									if (resultB = (isComputed(rv) & resultB))
										returns.add(valueOf(index, ret.getReturnValue(i)));
								}
								if (resultB)
									resultE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RETURNSTRUCTURE), returns);
							}
							
							if (resultB) {
								E injrE = converter.get(SimpleLLVMLabel.get(LLVMOperator.INJR), resultE);
								cache.put(var, injrE);
								return true;
							} else {
								resultVs.add(me);
								return addWorklist(resultVs);
							}
						}
					}
					else if (term.isIndirectBR()) {
						// should not occur!
						throw new RuntimeException("Cannot represent in PEG!");
					}
					else if (term.isBr() || term.isSwitch()) {
						// should not occur!
						throw new RuntimeException("These should have been replaced!");
					} 
					else if (term.isInvoke()) {
						// uses and modifies SIGMA!
						if (var.equals(LLVMVariable.SIGMA) ||
							block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
							InvokeInstruction invoke = term.getInvokeSelf();

							List<ValueOrVariable> vs = new ArrayList<ValueOrVariable>();
							vs.add(new ValueOrVariable(index-1, LLVMVariable.SIGMA));
							vs.add(new ValueOrVariable(index, invoke.getFunctionPointer()));
							for (int i = 0; i < invoke.getNumActuals(); i++) {
								vs.add(new ValueOrVariable(index, invoke.getActual(i)));
							}
							
							if (isComputed(vs)) {
								E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
								E functionPtrE = valueOf(index, invoke.getFunctionPointer());
								E ccE = converter.get(new NumeralLLVMLabel(invoke.getCallingConvention()));

								List<E> params = new ArrayList<E>();
								for (int i = 0; i < invoke.getNumActuals(); i++) {
									params.add(valueOf(index, invoke.getActual(i)));
								}
								E paramsE = converter.get(SimpleLLVMLabel.get(LLVMOperator.PARAMS), params);
//								E paramAttrMapE = getParamAttrMap(converter, invoke.getParameterAttributeMap());
								E invokeE = converter.get(SimpleLLVMLabel.get(LLVMOperator.INVOKE),
										sigmaE,
										functionPtrE,
										ccE,
										paramsE);
//										paramAttrMapE);

								if (var.equals(LLVMVariable.SIGMA)) {
									E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), invokeE);
									cache.put(var, rhoSigmaE);
								} else {
									cache.put(var, invokeE);
								}
								return true;
							} else {
								vs.add(me);
								return addWorklist(vs);
							}
						}
					} 
					else if (term.isUnwind()) {
						// uses and modifies SIGMA
						if (var.equals(LLVMVariable.SIGMA) || 
							(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
							ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);

							if (isComputed(sigmaV)) {
								E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
								E unwindE = converter.get(SimpleLLVMLabel.get(LLVMOperator.UNWIND), sigmaE);

								if (var.equals(LLVMVariable.SIGMA)) {
									E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), unwindE);
									cache.put(var, rhoSigmaE);
								} else {
									cache.put(var, unwindE);
								}
								return true;
							} else
								return addWorklist(sigmaV, me);
						}
					}
					else if (term.isUnreachable()) {
						throw new IllegalArgumentException("Unreachable statements are not allowed");
					}
					else 
						throw new RuntimeException("Mike forgot to handle: " + term.getClass());
				} else if (llvminst.isBinop()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						BinopInstruction binop = llvminst.getBinopSelf();
						Binop op = binop.getBinop();

						ValueOrVariable lhsV = new ValueOrVariable(index, binop.getLHS());
						ValueOrVariable rhsV = new ValueOrVariable(index, binop.getRHS());
						
						if (isComputed(lhsV, rhsV)) {
							E lhsE = valueOf(index, binop.getLHS());
							E rhsE = valueOf(index, binop.getRHS());
							E result = converter.get(new BinopLLVMLabel(op), lhsE, rhsE);
							cache.put(var, result);
							return true;
						} else 
							return addWorklist(lhsV, rhsV, me);
					}
				} else if (llvminst.isCast()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						CastInstruction castinst = llvminst.getCastSelf();
						Cast cast = castinst.getCast();

						ValueOrVariable casteeV = new ValueOrVariable(index, castinst.getCastee());
						
						if (isComputed(casteeV)) {
							E typeE = converter.get(new TypeLLVMLabel(castinst.getDestinationType()));
							E casteeE = valueOf(index, castinst.getCastee());
							E result = converter.get(new CastLLVMLabel(cast), typeE, casteeE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(casteeV, me);
					}
				} else if (llvminst.isShuffleVec() || llvminst.isShuffleVec2_8()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						ShuffleVecInstruction shuffle = llvminst.getShuffleVecSelf();

						ValueOrVariable vec1V = new ValueOrVariable(index, shuffle.getVector1());
						ValueOrVariable vec2V = new ValueOrVariable(index, shuffle.getVector2());
						ValueOrVariable shuffleV = new ValueOrVariable(index, shuffle.getShuffleVector());
						
						if (isComputed(vec1V, vec2V, shuffleV)) {
							E vec1E = valueOf(index, shuffle.getVector1());
							E vec2E = valueOf(index, shuffle.getVector2());
							E shuffleE = valueOf(index, shuffle.getShuffleVector());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.SHUFFLEVECTOR), vec1E, vec2E, shuffleE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(vec1V, vec2V, shuffleV, me);
					}
				} else if (llvminst.isInsertElt()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						InsertEltInstruction insert = llvminst.getInsertEltSelf();

						ValueOrVariable vectorV = new ValueOrVariable(index, insert.getVector());
						ValueOrVariable elementV = new ValueOrVariable(index, insert.getElement());
						ValueOrVariable indexV = new ValueOrVariable(index, insert.getIndex());

						if (isComputed(vectorV, elementV, indexV)) {
							E vectorE = valueOf(index, insert.getVector());
							E elementE = valueOf(index, insert.getElement());
							E indexE = valueOf(index, insert.getIndex());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.INSERTELEMENT), vectorE, elementE, indexE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(vectorV, elementV, indexV, me);
					}
				} else if (llvminst.isGEP()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						GEPInstruction gep = llvminst.getGEPSelf();

						List<ValueOrVariable> vs = new ArrayList<ValueOrVariable>();
						vs.add(new ValueOrVariable(index, gep.getBaseValue()));
						for (int i = 0; i < gep.getNumIndexes(); i++) {
							vs.add(new ValueOrVariable(index, gep.getIndex(i)));
						}
						
						if (isComputed(vs)) {
							E result = computeGEP(gep, index);
							cache.put(var, result);
							return true;
						} else {
							vs.add(me);
							return addWorklist(vs);
						}
					}
				} else if (llvminst.isSelect()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						SelectInstruction select = llvminst.getSelectSelf();
						
						if (select.getTrueValue().equals(select.getFalseValue())) {
							// optimization!
							ValueOrVariable resultV = new ValueOrVariable(index, select.getTrueValue());
							
							if (isComputed(resultV)) {
								E result = valueOf(index, select.getTrueValue());
								cache.put(var, result);
								return true;
							} else
								return addWorklist(resultV, me);
						} else {
							ValueOrVariable conditionV = new ValueOrVariable(index, select.getCondition());
							ValueOrVariable trueV = new ValueOrVariable(index, select.getTrueValue());
							ValueOrVariable falseV = new ValueOrVariable(index, select.getFalseValue());

							if (isComputed(conditionV, trueV, falseV)) {
								E conditionE = valueOf(index, select.getCondition());
								E trueE = valueOf(index, select.getTrueValue());
								E falseE = valueOf(index, select.getFalseValue());
								E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.SELECT), conditionE, trueE, falseE);
								cache.put(var, result);
								return true;
							} else
								return addWorklist(conditionV, trueV, falseV, me);
						}
					}
				} else if (llvminst.isExtractElt()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						ExtractEltInstruction extract = llvminst.getExtractEltSelf();

						ValueOrVariable vectorV = new ValueOrVariable(index, extract.getVector());
						ValueOrVariable indexV = new ValueOrVariable(index, extract.getIndex());
						
						if (isComputed(vectorV, indexV)) {
							E vectorE = valueOf(index, extract.getVector());
							E indexE = valueOf(index, extract.getIndex());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.EXTRACTELEMENT), vectorE, indexE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(vectorV, indexV, me);
					}
				} else if (llvminst.isCmp()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						CmpInstruction cmp = llvminst.getCmpSelf();

						ValueOrVariable lhsV = new ValueOrVariable(index, cmp.getLHS());
						ValueOrVariable rhsV = new ValueOrVariable(index, cmp.getRHS());
						
						if (isComputed(lhsV, rhsV)) {
							E lhsE = valueOf(index, cmp.getLHS());
							E rhsE = valueOf(index, cmp.getRHS());
							E result = converter.get(new CmpLLVMLabel(cmp.getPredicate()), lhsE, rhsE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(lhsV, rhsV, me);
					}
				} else if (llvminst.isPhi()) {
					throw new RuntimeException("Should not occur!");
				} else if (llvminst.isGetResult()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						GetResultInstruction getresult = llvminst.getGetResultSelf();
						
						ValueOrVariable baseV = new ValueOrVariable(index, getresult.getBase());
						
						if (isComputed(baseV)) {
							E baseE = valueOf(index, getresult.getBase());
							E numeralE = converter.get(new NumeralLLVMLabel(getresult.getIndex()));
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.GETRESULT), baseE, numeralE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(baseV, me);
					}
				} else if (llvminst.isMalloc()) {
					// modifies SIGMA!!
					if (var.equals(LLVMVariable.SIGMA) || 
						(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
						MallocInstruction malloc = llvminst.getMallocSelf();
						
						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable numElementsV = new ValueOrVariable(index, malloc.getNumElementsValue());
						
						if (isComputed(sigmaV, numElementsV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E typeE = converter.get(new TypeLLVMLabel(malloc.getElementType()));
							E numElementsE = valueOf(index, malloc.getNumElementsValue());
							E alignmentE = converter.get(new NumeralLLVMLabel(malloc.getAlignment()));
							E mallocE = converter.get(SimpleLLVMLabel.get(LLVMOperator.MALLOC), sigmaE, typeE, numElementsE, alignmentE);

							if (var.equals(LLVMVariable.SIGMA)) {
								E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), mallocE);
								cache.put(var, rhoSigmaE);
							} else {
								cache.put(var, mallocE);
							}
							return true;
						} else
							return addWorklist(sigmaV, numElementsV, me);
					}
				} else if (llvminst.isFree()) {
					// modifies sigma!
					if (var.equals(LLVMVariable.SIGMA)) {
						FreeInstruction free = llvminst.getFreeSelf();

						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable freeeeV = new ValueOrVariable(index, free.getFreedValue());
						
						if (isComputed(sigmaV, freeeeV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E freeeeE = valueOf(index, free.getFreedValue());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.FREE), sigmaE, freeeeE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(sigmaV, freeeeV, me);
					}
				} else if (llvminst.isAlloca()) {
					// modifies sigma!
					if (var.equals(LLVMVariable.SIGMA) || 
						(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
						AllocaInstruction alloca = llvminst.getAllocaSelf();

						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable numElementsV = new ValueOrVariable(index, alloca.getNumElementsValue());
						
						if (isComputed(sigmaV, numElementsV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E typeE = converter.get(new TypeLLVMLabel(alloca.getElementType()));
							E numElementsE = valueOf(index, alloca.getNumElementsValue());
							E alignmentE = converter.get(new NumeralLLVMLabel(alloca.getAlignment()));
							E allocaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.ALLOCA), sigmaE, typeE, numElementsE, alignmentE);

							if (var.equals(LLVMVariable.SIGMA)) {
								E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), allocaE);
								cache.put(var, rhoSigmaE);
							} else {
								cache.put(var, allocaE);
							}
							return true;
						} else
							return addWorklist(sigmaV, numElementsV, me);
					}
				} else if (llvminst.isLoad()) {
					// uses sigma, might modify it
					if ((cfg.getOpAmbassador().hasLinearLoads() && var.equals(LLVMVariable.SIGMA)) ||
						(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
						LoadInstruction load = llvminst.getLoadSelf();

						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable loadeeV = new ValueOrVariable(index, load.getLoadee());
						
						if (isComputed(sigmaV, loadeeV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E loadeeE = valueOf(index, load.getLoadee());
							E alignmentE = converter.get(new NumeralLLVMLabel(load.getAlignment()));
							E loadE = converter.get(
									SimpleLLVMLabel.get(load.isVolatile() ? LLVMOperator.VOLATILE_LOAD : LLVMOperator.LOAD),
									sigmaE,
									loadeeE,
									alignmentE);
							
							if (cfg.getOpAmbassador().hasLinearLoads() && var.equals(LLVMVariable.SIGMA)) {
								// sigma value for linear reads
								E result = converter.get(
										SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA),
										loadE);
								cache.put(var, result);
								return true;
							} else {
								// even with linear reads, this stays the same
								// (the rho_value will come from an ExtractValueCFGInstruction)
								cache.put(var, loadE);
								return true;
							}
						} else
							return addWorklist(sigmaV, loadeeV, me);
					}
				} else if (llvminst.isStore()) {
					// modifies SIGMA!
					if (var.equals(LLVMVariable.SIGMA)) { 
						StoreInstruction store = llvminst.getStoreSelf();

						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable addressV = new ValueOrVariable(index, store.getAddress());
						ValueOrVariable valueV = new ValueOrVariable(index, store.getValue());
						
						if (isComputed(sigmaV, addressV, valueV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E addressE = valueOf(index, store.getAddress());
							E valueE = valueOf(index, store.getValue());
							E alignmentE = converter.get(new NumeralLLVMLabel(store.getAlignment()));
							E storeE = converter.get(
									SimpleLLVMLabel.get(store.isVolatile() ? LLVMOperator.VOLATILE_STORE : LLVMOperator.STORE),
									sigmaE,
									addressE,
									valueE,
									alignmentE);

							cache.put(var, storeE);
							return true;
						} else
							return addWorklist(sigmaV, addressV, valueV, me);
					}
				} else if (llvminst.isCall()) {
					// both uses and modifies sigma
					if (var.equals(LLVMVariable.SIGMA) || 
						(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
						CallInstruction call = llvminst.getCallSelf();

						List<ValueOrVariable> vs = new ArrayList<ValueOrVariable>();
						vs.add(new ValueOrVariable(index-1, LLVMVariable.SIGMA));
						vs.add(new ValueOrVariable(index, call.getFunctionPointer()));
						for (int i = 0; i < call.getNumActuals(); i++) {
							vs.add(new ValueOrVariable(index, call.getActual(i)));
						}
						
						if (isComputed(vs)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E functionPtrE = valueOf(index, call.getFunctionPointer());
							E ccE = converter.get(new NumeralLLVMLabel(call.getCallingConvention()));

							List<E> params = new ArrayList<E>(call.getNumActuals());
							for (int i = 0; i < call.getNumActuals(); i++) {
								params.add(valueOf(index, call.getActual(i)));
							}
							E paramsE = converter.get(SimpleLLVMLabel.get(LLVMOperator.PARAMS), params);
//							E paramAttrMapE = getParamAttrMap(converter, call.getParameterAttributeMap());

							E callE = converter.get(
									SimpleLLVMLabel.get(call.isTailCall() ? LLVMOperator.TAILCALL : LLVMOperator.CALL), 
									sigmaE,
									functionPtrE,
									ccE,
									paramsE);
//									paramAttrMapE);

							if (var.equals(LLVMVariable.SIGMA)) {
								E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), callE);
								cache.put(var, rhoSigmaE);
							} else {
								cache.put(var, callE);
							}
							return true;
						} else {
							vs.add(me);
							return addWorklist(vs);
						}
					}
				} else if (llvminst.isVaarg()) {
					// uses and modifies SIGMA!
					if (var.equals(LLVMVariable.SIGMA) || 
						(block.hasVariable(simple) && var.equals(block.getAssignment(simple)))) {
						VaargInstruction vaarg = llvminst.getVaargSelf();

						ValueOrVariable sigmaV = new ValueOrVariable(index-1, LLVMVariable.SIGMA);
						ValueOrVariable valistV = new ValueOrVariable(index, vaarg.getVAList());

						if (isComputed(sigmaV, valistV)) {
							E sigmaE = getOutputAt(index-1, LLVMVariable.SIGMA);
							E valistE = valueOf(index, vaarg.getVAList());
							E typeE = converter.get(new TypeLLVMLabel(vaarg.getResultType()));
							E vaargE = converter.get(SimpleLLVMLabel.get(LLVMOperator.VAARG), sigmaE, valistE, typeE);

							if (var.equals(LLVMVariable.SIGMA)) {
								E rhoSigmaE = converter.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), vaargE);
								cache.put(var, rhoSigmaE);
							} else {
								cache.put(var, vaargE);
							}
							return true;
						} else
							return addWorklist(sigmaV, valistV, me);
					}
					
				} else if (llvminst.isVSelect()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						VSelectInstruction select = llvminst.getVSelectSelf();
						
						if (select.getTrueValue().equals(select.getFalseValue())) {
							// optimization!
							ValueOrVariable resultV = new ValueOrVariable(index, select.getTrueValue());
							
							if (isComputed(resultV)) {
								E result = valueOf(index, select.getTrueValue());
								cache.put(var, result);
								return true;
							} else
								return addWorklist(resultV, me);
						} else {
							ValueOrVariable conditionV = new ValueOrVariable(index, select.getCondition());
							ValueOrVariable trueV = new ValueOrVariable(index, select.getTrueValue());
							ValueOrVariable falseV = new ValueOrVariable(index, select.getFalseValue());

							if (isComputed(conditionV, trueV, falseV)) {
								E conditionE = valueOf(index, select.getCondition());
								E trueE = valueOf(index, select.getTrueValue());
								E falseE = valueOf(index, select.getFalseValue());
								E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.VSELECT), conditionE, trueE, falseE);
								cache.put(var, result);
								return true;
							} else
								return addWorklist(conditionV, trueV, falseV, me);
						}
					}
				} else if (llvminst.isExtractValue()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						ExtractValueInstruction extract = llvminst.getExtractValueSelf();
						
						ValueOrVariable aggV = new ValueOrVariable(index, extract.getAggregate());

						if (isComputed(aggV)) {
							E aggE = valueOf(index, extract.getAggregate());
							List<E> offsets = new ArrayList<E>();
							for (int ind : extract.getIndexes()) {
								offsets.add(converter.get(new NumeralLLVMLabel(ind)));
							}
							E offsetsE = converter.get(SimpleLLVMLabel.get(LLVMOperator.OFFSETS), offsets);
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.EXTRACTVALUE), aggE, offsetsE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(aggV, me);
					}
				} else if (llvminst.isInsertValue()) {
					if (block.hasVariable(simple) && var.equals(block.getAssignment(simple))) {
						InsertValueInstruction insert = llvminst.getInsertValueSelf();
						
						ValueOrVariable aggV = new ValueOrVariable(index, insert.getAggregate());
						ValueOrVariable eltV = new ValueOrVariable(index, insert.getElement());

						if (isComputed(aggV, eltV)) {
							E aggE = valueOf(index, insert.getAggregate());
							E eltE = valueOf(index, insert.getElement());
							
							List<E> offsets = new ArrayList<E>();
							for (int ind : insert.getIndexes()) {
								offsets.add(converter.get(new NumeralLLVMLabel(ind)));
							}
							E offsetsE = converter.get(SimpleLLVMLabel.get(LLVMOperator.OFFSETS), offsets);
							
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.INSERTVALUE), aggE, eltE, offsetsE);
							cache.put(var, result);
							return true;
						} else
							return addWorklist(aggV, eltV, me);
					}
				} else {
					throw new RuntimeException("Mike forgot to support: " + llvminst.getClass());
				}
				
				// default fallthrough
				ValueOrVariable resultV = new ValueOrVariable(index-1, var);
				if (isComputed(resultV)) {
					E resultE = getOutputAt(index-1, var);
					cache.put(var, resultE);
					return true;
				} else
					return addWorklist(resultV, me);
			}
			
//			private E getParamAttrMap(VariaticFunction<LLVMLabel,E,E> converter, ParameterAttributeMap map) {
//				List<E> paramAttrs = new ArrayList<E>();
//				ParameterAttributes defaultAttrs = new ParameterAttributes(0);
//				if (map.hasFunctionAttributes()) {
//					paramAttrs.add(converter.get(new ParamAttrLLVMLabel(map.getFunctionAttributes())));
//				} else if (map.getMaxIndex() > 0) {
//					paramAttrs.add(converter.get(new ParamAttrLLVMLabel(defaultAttrs)));
//				}
//				
//				for (int i = 0; i <= map.getMaxIndex(); i++) {
//					ParameterAttributes attrs;
//					if (map.hasParamAttributes(i)) {
//						attrs = map.getParamAttributes(i);
//					} else {
//						attrs = defaultAttrs;
//					}
//					paramAttrs.add(converter.get(new ParamAttrLLVMLabel(attrs)));
//				}
//
//				E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.PARAMATTRMAP), paramAttrs);
//				return result;
//			}
			
			private boolean computeValueOf(int index, Value value) {
				Map<Value,E> cache = this.valueCache.get(index);
				if (cache.containsKey(value)) return true;
				
				ValueOrVariable me = new ValueOrVariable(index, value);
				
				if (value.isInteger() ||
					value.isFloatingPoint() ||
					value.isUndef() ||
					value.isConstantNullPointer() ||
					value.isConstantStructure() ||
					value.isConstantArray() ||
					value.isConstantVector()) {
					E result = converter.get(new ConstantValueLLVMLabel(value));
					cache.put(value, result);
					return true;
				}
				else if (value.isConstantExpr()) {
					Instruction ci = value.getConstantExprSelf().getInstruction();
					if (ci.isBinop()) {
						BinopInstruction binop = ci.getBinopSelf();
						if (computeValueOf(index, binop.getLHS()) && computeValueOf(index, binop.getRHS())) {
							E lhsE = valueOf(index, binop.getLHS());
							E rhsE = valueOf(index, binop.getRHS());
							E result = converter.get(new BinopLLVMLabel(binop.getBinop()), lhsE, rhsE);
							cache.put(value, result);
							return true;
						} else 
							throw new RuntimeException("Constants should always be ready");
					} else if (ci.isCast()) {
						CastInstruction cast = ci.getCastSelf();
						if (computeValueOf(index, cast.getCastee())) {
							E casteeE = valueOf(index, cast.getCastee());
							E result = converter.get(new CastLLVMLabel(cast.getCast()), 
									converter.get(new TypeLLVMLabel(cast.getDestinationType())),
									casteeE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should always be ready");
					} else if (ci.isGEP()) {
						GEPInstruction gep = ci.getGEPSelf();
						List<E> indexes = new ArrayList<E>(gep.getNumIndexes());
						for (int i = 0; i < gep.getNumIndexes(); i++) {
							boolean computed = computeValueOf(index, gep.getIndex(i));
							if (!computed)
								throw new RuntimeException("Constants should all be computed");
							indexes.add(valueOf(index, gep.getIndex(i)));
						}
						if (computeValueOf(index, gep.getBaseValue())) {
							E result = computeGEP(gep, index);
							cache.put(value, result);
							return true;
						} else 
							throw new RuntimeException("Constants should all be computed");
					} else if (ci.isSelect()) {
						SelectInstruction select = ci.getSelectSelf();
						if (computeValueOf(index, select.getCondition()) &&
							computeValueOf(index, select.getTrueValue()) &&
							computeValueOf(index, select.getFalseValue())) {
							E condE = valueOf(index, select.getCondition());
							E trueE = valueOf(index, select.getTrueValue());
							E falseE = valueOf(index, select.getFalseValue());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.SELECT), condE, trueE, falseE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should all be computed");
					} else if (ci.isExtractElt()) {
						ExtractEltInstruction extract = ci.getExtractEltSelf();
						if (computeValueOf(index, extract.getVector()) && computeValueOf(index, extract.getIndex())) {
							E vectorE = valueOf(index, extract.getVector());
							E indexE = valueOf(index, extract.getIndex());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.EXTRACTELEMENT), vectorE, indexE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should all be computed");
					} else if (ci.isInsertElt()) {
						InsertEltInstruction insert = ci.getInsertEltSelf();
						if (computeValueOf(index, insert.getVector()) &&
							computeValueOf(index, insert.getElement()) &&
							computeValueOf(index, insert.getIndex())) {
							E vectorE = valueOf(index, insert.getVector());
							E elementE = valueOf(index, insert.getElement());
							E indexE = valueOf(index, insert.getIndex());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.INSERTELEMENT), vectorE, elementE, indexE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should all be computed");
					} else if (ci.isShuffleVec()) {
						ShuffleVecInstruction shuffle = ci.getShuffleVecSelf();
						if (computeValueOf(index, shuffle.getVector1()) &&
							computeValueOf(index, shuffle.getVector2()) &&
							computeValueOf(index, shuffle.getShuffleVector())) {
							E vec1E = valueOf(index, shuffle.getVector1());
							E vec2E = valueOf(index, shuffle.getVector2());
							E shuffleE = valueOf(index, shuffle.getShuffleVector());
							E result = converter.get(SimpleLLVMLabel.get(LLVMOperator.SHUFFLEVECTOR), vec1E, vec2E, shuffleE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should all be computed");
					} else if (ci.isCmp()) {
						CmpInstruction cmp = ci.getCmpSelf();
						if (computeValueOf(index, cmp.getLHS()) && computeValueOf(index, cmp.getRHS())) {
							E lhsE = valueOf(index, cmp.getLHS());
							E rhsE = valueOf(index, cmp.getRHS());
							E result = converter.get(
									new CmpLLVMLabel(cmp.getPredicate()),
									lhsE,
									rhsE);
							cache.put(value, result);
							return true;
						} else
							throw new RuntimeException("Constants should all be computed");
					} else {
						throw new IllegalArgumentException("Unexpected constant expression instruction: " + ci);
					}
				}
				else if (value.isLabel()) {
					throw new RuntimeException("This should never happen!");
				}
				else if (value.isGlobalVariable()) {
					GlobalVariable global = value.getGlobalVariableSelf();
					String name = resolver.getGlobalName(global);
					if (name == null)
						name = "#anonymousGlobal" + (anonymousGlobalCounter++);
					E result = converter.get(new GlobalLLVMLabel(global.getType().getPointeeType(), name));
					cache.put(value, result);
					return true;
				}
				else if (value.isAlias()) {
					AliasValue alias = value.getAliasSelf();
					String name = resolver.getAliasName(alias);
					if (name == null)
						name = "#anonymousAlias" + (anonymousAliasCounter++);
					E result = converter.get(new AliasLLVMLabel(alias.getType().getPointeeType(), name));
					cache.put(value, result);
					return true;
				}
				else if (value.isFunction()) {
					FunctionValue function = value.getFunctionSelf();
					String name = resolver.getFunctionName(function);
					if (name == null)
						name = "#anonymousFunction" + (anonymousFunctionCounter++);
					E result = converter.get(new FunctionLLVMLabel(function.getType().getPointeeType().getFunctionSelf(), name));
					cache.put(value, result);
					return true;
				}
				else if (value.isInlineASM()) {
					ConstantInlineASM asm = value.getInlineASMSelf();
					E result = converter.get(new InlineASMLLVMLabel(asm));
					cache.put(value, result);
					return true;
				}
				else if (value.isRegister()) {
					// actually a variable
					LLVMVariable regvar = registerMap.get(value.getRegisterSelf());
					if (regvar == null)
						throw new RuntimeException("VirtualRegister not associated with an LLVMVariable: " + regvar);
					
					ValueOrVariable resultV = new ValueOrVariable(index-1, regvar);
					if (isComputed(resultV)) {
						E resultE = getOutputAt(index-1, regvar);
						cache.put(value, resultE);
						return true;
					} else
						return addWorklist(resultV, me);
				}
				else if (value.isArgument()) {
					LLVMVariable argvar = argumentMap.get(value.getArgumentSelf());
					if (argvar == null)
						throw new RuntimeException("ArgumentValue not associated with an LLVMVariable: " + argvar);

					ValueOrVariable resultV = new ValueOrVariable(index, argvar);
					if (isComputed(resultV)) {
						E resultE = getOutputAt(index, argvar);
						cache.put(value, resultE);
						return true;
					} else
						return addWorklist(resultV, me);
				}
				else {
					throw new RuntimeException("Mike forgot to support: " + value);
				}
			}
		};
	}
}
