package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

import llvm.bitcode.ModuleEncoder2_8.TypeTable;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.GEPInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVec2_8Instruction;
import llvm.instructions.ShuffleVecInstruction;
import llvm.types.FloatingPointType;
import llvm.types.Type;
import llvm.values.BlockAddressValue;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantExpr;
import llvm.values.ConstantInlineASM;
import llvm.values.ConstantNullArrayValue;
import llvm.values.ConstantNullPointerValue;
import llvm.values.ConstantNullVectorValue;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FloatingPointValue;
import llvm.values.IntegerValue;
import llvm.values.UndefValue;
import llvm.values.Value;
import util.Action;

/**
 * This class encodes and writes the global values of a module
 * (functions/aliases/globals) to an LLVM 2.8 bitcode file output stream.
 */
class GlobalConstantEncoder2_8 {
	public static final long OBO_NO_UNSIGNED_WRAP = 0;
	public static final long OBO_NO_SIGNED_WRAP = 1;
	public static final long SDIV_EXACT = 0;
	
	protected final BitcodeWriter writer;
	protected final int abbrevLength;
	protected final TypeTable typeTable;
	protected final List<Long> ops;
	
	public GlobalConstantEncoder2_8(BitcodeWriter _writer, int _abbrevLength, TypeTable _typeTable) {
		this.writer = _writer;
		this.abbrevLength = _abbrevLength;
		this.typeTable = _typeTable;
		this.ops = new ArrayList<Long>();
	}
	
	/**
	 * Takes a flattened constant value table and emits the ConstantsBlock
	 * at the top level (inside the ModuleBlock)
	 */
	public void writeConstantsBlock(HashList<Value> constants, int numNonconstGlobals) {
		final int innerAbbrevLength = 5;
		int patch = this.writer.writeEnterSubblock(this.abbrevLength, new EnterSubblock(ConstantsBlock2_8.CONSTANTS_BLOCK_ID, innerAbbrevLength, 0));

		// this action writes out the CST_CODE_SETTYPE record, if need be
		Action<Type> setType = new Action<Type>() {
			private Type currentType = Type.getIntegerType(32);
			public void execute(Type type) {
				if (type.equalsType(currentType))
					return;
				int index = typeTable.getTypeIndex(type);
				ops.clear();
				ops.add((long)index);
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_SETTYPE, ops));
				currentType = type;
			}
		};
		
		for (int i = numNonconstGlobals; i < constants.size(); i++) {
			Value constant = constants.getValue(i);
			
			if (constant.isUndef()) {
				UndefValue undef = constant.getUndefSelf();
				
				setType.execute(undef.getType());
				
				ops.clear();
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_UNDEF, ops));
			}
			
			else if (constant.isInteger()) {
				// INTEGER or WIDE_INTEGER
				IntegerValue iv = constant.getIntegerSelf();
				
				setType.execute(iv.getType());
				
				int width = iv.getType().getWidth();
				long[] words = new long[(width+63)>>6];
				for (int b = 0; b < width; b++) {
					if (iv.getBit(b)) {
						int bit = b&63;
						words[b>>6] |= (1L<<bit);
					}
				}
				// fix the negative 0 thing
				for (int w = 0; w < words.length; w++) {
					if (words[w] == (1L<<63))
						words[w] = 1L;
					else if (words[w] < 0)
						words[w] = ((-words[w])<<1) | 1L;
					else
						words[w] = words[w]<<1;
				}
				
				ops.clear();
				for (int j = 0; j < words.length; j++)
					ops.add(words[j]);

				writer.writeUnabbrevRecord(
						innerAbbrevLength, 
						new UnabbrevRecord(
								(words.length > 1 ? ConstantsBlock2_8.CST_CODE_WIDE_INTEGER : ConstantsBlock2_8.CST_CODE_WIDE_INTEGER),
								ops));
			}
			
			else if (constant.isFloatingPoint()) {
				// FLOAT
				FloatingPointValue fp = constant.getFloatingPointSelf();
				
				FloatingPointType type = fp.getType();
				setType.execute(type);
				
				int width = type.getKind().getTypeSize();
				long[] words = new long[(width+63)>>6];
				for (int b = 0; b < width; b++) {
					if (fp.getBit(b)) {
						int bit = b&63;
						words[b>>6] |= (1L<<bit);
					}
				}
				
				ops.clear();
				for (int j = 0; j < words.length; j++)
					ops.add(words[j]);
				
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_FLOAT, ops));
			}
			
			else if (constant.isConstantArray()) {
				ConstantArrayValue cav = constant.getConstantArraySelf();
				if (cav instanceof ConstantNullArrayValue) {
					// null array
					setType.execute(cav.getType());
					ops.clear();
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_NULL, ops));					
				} else {
					// explicit array
					setType.execute(cav.getType());
					
					ops.clear();
					for (int c = 0; c < cav.getNumElements().signedValue(); c++) {
						int childIndex = constants.getIndex(cav.getElement(c));
						ops.add((long)childIndex);
					}
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_AGGREGATE, ops));
				}
			} 
			
			else if (constant.isConstantVector()) {
				ConstantVectorValue cav = constant.getConstantVectorSelf();
				if (cav instanceof ConstantNullVectorValue) {
					// null vector
					setType.execute(cav.getType());
					ops.clear();
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_NULL, ops));					
				} else {
					// explicit vector
					setType.execute(cav.getType());
					
					ops.clear();
					for (int c = 0; c < cav.getNumElements().signedValue(); c++) {
						int childIndex = constants.getIndex(cav.getElement(c));
						ops.add((long)childIndex);
					}
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_AGGREGATE, ops));
				}
			}
			 
			else if (constant.isConstantStructure()) {
				ConstantStructureValue csv = constant.getConstantStructureSelf();
				setType.execute(csv.getType());
				
				if (Value.isNullConstant(csv)) {
					ops.clear();
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_NULL, ops));					
				} else {
					ops.clear();
					for (int c = 0; c < csv.getNumFields(); c++) {
						int childIndex = constants.getIndex(csv.getFieldValue(c));
						ops.add((long)childIndex);
					}
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_AGGREGATE, ops));
				}
			}
			
			else if (constant.isConstantNullPointer()) {
				ConstantNullPointerValue cnp = constant.getConstantNullPointerSelf();
				setType.execute(cnp.getType());
				
				ops.clear();
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_NULL, ops));
			}
			
			else if (constant.isInlineASM()) {
				ConstantInlineASM ciasm = constant.getInlineASMSelf();
				String asmString = ciasm.getASMString();
				String constraintString = ciasm.getConstraintString();
				
				setType.execute(ciasm.getType());
				
				ops.clear();
				ops.add(ciasm.hasSideEffects() ? 1L : 0L);
				
				ops.add((long)asmString.length());
				for (int c = 0; c < asmString.length(); c++) {
					ops.add((long)asmString.charAt(c));
				}
				ops.add((long)constraintString.length());
				for (int c = 0; c < constraintString.length(); c++) {
					ops.add((long)constraintString.charAt(c));
				}
				
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_INLINEASM, ops));
			}
			
			else if (constant.isBlockAddress()) {
				BlockAddressValue bav = constant.getBlockAddressSelf();
				
				ops.clear();
				ops.add((long)this.typeTable.getTypeIndex(bav.getFunction().getType()));
				ops.add((long)constants.getIndex(bav.getFunction()));
				ops.add((long)bav.getBlockNumber());
				
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_BLOCKADDRESS, ops));
			}
			
			else if (constant.isConstantExpr()) {
				ConstantExpr expr = constant.getConstantExprSelf();
				Instruction inst = expr.getInstruction();
				
				if (inst.isBinop()) {
					BinopInstruction binop = inst.getBinopSelf();
					setType.execute(binop.getType());

					ops.clear();
					switch (binop.getBinop()) {
					case AddNsw:
						ops.add((long)Binop.Add.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_SIGNED_WRAP);
						break;
					case AddNuw:
						ops.add((long)Binop.Add.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
						break;
					case AddNswNuw:
						ops.add((long)Binop.Add.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
						break;
						
					case SubNsw:
						ops.add((long)Binop.Sub.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_SIGNED_WRAP);
						break;
					case SubNuw:
						ops.add((long)Binop.Sub.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
						break;
					case SubNswNuw:
						ops.add((long)Binop.Sub.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
						break;

					case MulNsw:
						ops.add((long)Binop.Mul.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_SIGNED_WRAP);
						break;
					case MulNuw:
						ops.add((long)Binop.Mul.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
						break;
					case MulNswNuw:
						ops.add((long)Binop.Mul.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
						break;
						
					case SDivExact:
						ops.add((long)Binop.SDiv.getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						ops.add(1L<<SDIV_EXACT);
						break;
						
					default:
						ops.add((long)binop.getBinop().getValue());
						ops.add((long)constants.getIndex(binop.getLHS()));
						ops.add((long)constants.getIndex(binop.getRHS()));
						break;
					}
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_BINOP, ops));
				} else if (inst.isCast()) {
					CastInstruction cast = inst.getCastSelf();
					setType.execute(cast.getDestinationType());
					
					ops.clear();
					ops.add((long)cast.getCast().getValue());
					ops.add((long)this.typeTable.getTypeIndex(cast.getCastee().getType()));
					ops.add((long)constants.getIndex(cast.getCastee()));
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_CAST, ops));
				} else if (inst.isGEP()) {
					GEPInstruction gep = inst.getGEPSelf();
					
					// don't need to setType
					ops.clear();
					ops.add((long)this.typeTable.getTypeIndex(gep.getBaseValue().getType()));
					ops.add((long)constants.getIndex(gep.getBaseValue()));
					for (int c = 0; c < gep.getNumIndexes(); c++) {
						Value index = gep.getIndex(c);
						ops.add((long)this.typeTable.getTypeIndex(index.getType()));
						ops.add((long)constants.getIndex(index));
					}

					int code = gep.isInbounds() ? ConstantsBlock2_8.CST_CODE_CE_INBOUNDS_GEP : ConstantsBlock2_8.CST_CODE_CE_GEP;
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(code, ops));
				} else if (inst.isSelect()) {
					SelectInstruction select = inst.getSelectSelf();
					setType.execute(select.getType());
					
					ops.clear();
					ops.add((long)constants.getIndex(select.getCondition()));
					ops.add((long)constants.getIndex(select.getTrueValue()));
					ops.add((long)constants.getIndex(select.getFalseValue()));
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_SELECT, ops));
				} else if (inst.isExtractElt()) {
					ExtractEltInstruction extract = inst.getExtractEltSelf();
					// no setType
					
					ops.clear();
					ops.add((long)this.typeTable.getTypeIndex(extract.getVector().getType()));
					ops.add((long)constants.getIndex(extract.getVector()));
					ops.add((long)constants.getIndex(extract.getIndex()));
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_EXTRACTELT, ops));
				} else if (inst.isInsertElt()) {
					InsertEltInstruction insert = inst.getInsertEltSelf();
					setType.execute(insert.getVector().getType());
					
					ops.clear();
					ops.add((long)constants.getIndex(insert.getVector()));
					ops.add((long)constants.getIndex(insert.getElement()));
					ops.add((long)constants.getIndex(insert.getIndex()));
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_INSERTELT, ops));
				} else if (inst.isShuffleVec()) {
					ShuffleVecInstruction shuffle = inst.getShuffleVecSelf();
					setType.execute(shuffle.getVector1().getType());
					
					ops.clear();
					ops.add((long)constants.getIndex(shuffle.getVector1()));
					ops.add((long)constants.getIndex(shuffle.getVector2()));
					ops.add((long)constants.getIndex(shuffle.getShuffleVector()));
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_SHUFFLEVEC, ops));
				} else if (inst.isCmp()) {
					CmpInstruction cmp = inst.getCmpSelf();
					// no settype
					
					ops.clear();
					ops.add((long)this.typeTable.getTypeIndex(cmp.getLHS().getType()));
					ops.add((long)constants.getIndex(cmp.getLHS()));
					ops.add((long)constants.getIndex(cmp.getRHS()));
					ops.add((long)cmp.getPredicate().getValue());
					
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_CMP, ops));
				} else if (inst.isShuffleVec2_8()) {
					ShuffleVec2_8Instruction shuf = inst.getShuffleVec2_8Self();
					setType.execute(shuf.getMask().getType());
					
					ops.clear();
					ops.add((long)this.typeTable.getTypeIndex(shuf.getOperand1().getType()));
					ops.add((long)constants.getIndex(shuf.getOperand1()));
					ops.add((long)constants.getIndex(shuf.getOperand2()));
					ops.add((long)constants.getIndex(shuf.getMask()));

					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ConstantsBlock2_8.CST_CODE_CE_SHUFVEC_EX, ops));
				} else {
					throw new RuntimeException("This shouldn't happen: " + inst);
				}
			} 
			
			else {
				throw new RuntimeException("This should never happen: " + constant);
			}
		}
		
		this.writer.writeEndBlock(innerAbbrevLength);
		this.writer.patchEnterSubblockSize(patch);
	}
}
