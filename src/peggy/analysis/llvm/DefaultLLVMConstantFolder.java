package peggy.analysis.llvm;

import static llvm.instructions.IntegerComparisonPredicate.ICMP_EQ;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_NE;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_SGE;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_SGT;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_SLE;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_SLT;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_UGE;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_UGT;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_ULE;
import static llvm.instructions.IntegerComparisonPredicate.ICMP_ULT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import llvm.bitcode.DataLayout;
import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.types.CompositeType;
import llvm.types.FloatingPointType;
import llvm.types.IntegerType;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.types.FloatingPointType.Kind;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantExplicitArrayValue;
import llvm.values.ConstantExplicitVectorValue;
import llvm.values.ConstantNullPointerValue;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FloatingPointValue;
import llvm.values.IntegerValue;
import llvm.values.UndefValue;
import llvm.values.Value;
import peggy.analysis.ConstantFolder;
import peggy.represent.llvm.BasicOpLLVMLabel;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.CmpLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.TypeLLVMLabel;
import eqsat.BasicOp;

/**
 * This class performs the constant folding for LLVM. It is used by the 
 * built-in engine constant folder.
 */
public class DefaultLLVMConstantFolder implements ConstantFolder<LLVMLabel> {
	private static final FloatingPointType FLOAT = 
		new FloatingPointType(Kind.FLOAT);
	private static final FloatingPointType DOUBLE = 
		new FloatingPointType(Kind.DOUBLE);
	
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("DefaultLLVMConstantFolder: " + message);
	}
	
	private DataLayout layout;
	
	public DefaultLLVMConstantFolder(DataLayout _layout) {
		this.layout = _layout;
	}
	
	public void setDataLayout(DataLayout _layout) {
		this.layout = _layout;
	}
	
	public boolean canFold(LLVMLabel root, List<? extends LLVMLabel> children) {
		boolean result = (fold(root, children) != null);
		return result;
	}
	public LLVMLabel fold(LLVMLabel root, List<? extends LLVMLabel> children) {
		debug("Calling fold on " + root + children);
		
		if (root.isBasicOp()) {
			final BasicOpLLVMLabel TRUE = new BasicOpLLVMLabel(BasicOp.True);
			final BasicOpLLVMLabel FALSE = new BasicOpLLVMLabel(BasicOp.False);
			
			final BasicOp op = root.getBasicOpSelf().getOperator();
			switch (op) {
			case Equals: {
				LLVMLabel child1 = children.get(0);
				LLVMLabel child2 = children.get(1);
				if (child1.isConstantValue() && child1.getConstantValueSelf().getValue().isInteger() &&
					child2.isConstantValue() && child2.getConstantValueSelf().getValue().isInteger()) {
					Value result = doICMP(
							IntegerComparisonPredicate.ICMP_EQ, 
							child1.getConstantValueSelf().getValue().getIntegerSelf(), 
							child2.getConstantValueSelf().getValue().getIntegerSelf());

					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				}
				break;
			}
				
			case False: {
				LLVMLabel result = 
					new ConstantValueLLVMLabel(IntegerValue.FALSE);
				debug("Folding " + root + children + " = " + result);
				return result;
			}
			case True: { 
				LLVMLabel result = 
					new ConstantValueLLVMLabel(IntegerValue.TRUE);
				debug("Folding " + root + children + " = " + result);
				return result;
			}
			
			case Negate: {
				LLVMLabel child = children.get(0);
				if (child.isConstantValue() && child.getConstantValueSelf().getValue().isInteger()) {
					boolean which = child.getConstantValueSelf().getValue().getIntegerSelf().equals(IntegerValue.TRUE);
					LLVMLabel result = new BasicOpLLVMLabel(which ? BasicOp.False : BasicOp.True);

					debug("Folding " + root + children + " = " + result);
							
					return result;
				} else if (child.equalsLabel(TRUE)) {
					LLVMLabel result = new BasicOpLLVMLabel(BasicOp.False);
					
					debug("Folding " + root + children + " = " + result);
					
					return result;
				} else if (child.equalsLabel(FALSE)) {
					LLVMLabel result = new BasicOpLLVMLabel(BasicOp.True);
					
					debug("Folding " + root + children + " = " + result);
					
					return result;
				}
				break;
			}
			
			case And: {
				LLVMLabel child1 = children.get(0);
				LLVMLabel child2 = children.get(1);
				Boolean lhs = null, rhs = null;
				if (child1.equalsLabel(TRUE)) {
					lhs = true;
				} else if (child1.equalsLabel(FALSE)) {
					lhs = false;
				} else if (child1.isConstantValue() && child1.getConstantValueSelf().getValue().isInteger()) {
					lhs = child1.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE);
				}
				
				if (child2.equalsLabel(TRUE)) {
					rhs = true;
				} else if (child2.equalsLabel(FALSE)) {
					rhs = false;
				} else if (child2.isConstantValue() && child2.getConstantValueSelf().getValue().isInteger()) {
					rhs = child2.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE);
				}
				
				if (lhs!=null && rhs!=null) {
					LLVMLabel result = (lhs&&rhs) ? TRUE : FALSE;
						
					debug("Folding " + root + children + " = " + result);
					
					return result;
				}
				break;
			}
				
			case Or: {
				LLVMLabel child1 = children.get(0);
				LLVMLabel child2 = children.get(1);
				Boolean lhs = null, rhs = null;
				if (child1.equalsLabel(TRUE)) {
					lhs = true;
				} else if (child1.equalsLabel(FALSE)) {
					lhs = false;
				} else if (child1.isConstantValue() && child1.getConstantValueSelf().getValue().isInteger()) {
					lhs = child1.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE);
				}
				
				if (child2.equalsLabel(TRUE)) {
					rhs = true;
				} else if (child2.equalsLabel(FALSE)) {
					rhs = false;
				} else if (child2.isConstantValue() && child2.getConstantValueSelf().getValue().isInteger()) {
					rhs = child2.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE);
				}
				
				if (lhs!=null && rhs!=null) {
					LLVMLabel result = (lhs||rhs) ? TRUE : FALSE;
						
					debug("Folding " + root + children + " = " + result);
					
					return result;
				}
				break;
			}
			
			default:
				return null;	
			}
		}
		
		if (root.isCmp()) {
			// icmp of ints
			final LLVMLabel lhs = children.get(0);
			final LLVMLabel rhs = children.get(1);
			if (lhs.isConstantValue() && 
				(lhs.getConstantValueSelf().getValue().isInteger() || lhs.getConstantValueSelf().getValue().isUndef()) &&
				rhs.isConstantValue() &&
				(rhs.getConstantValueSelf().getValue().isInteger() || rhs.getConstantValueSelf().getValue().isUndef())) {
				final CmpLLVMLabel cmp = root.getCmpSelf();
				if (cmp.getPredicate().isInteger()) {
					final Value lhsV = lhs.getConstantValueSelf().getValue();
					final Value rhsV = rhs.getConstantValueSelf().getValue();
					if (lhsV.isUndef() || rhsV.isUndef()) {
						LLVMLabel result = 
							new ConstantValueLLVMLabel(new UndefValue(Type.getIntegerType(1)));
						
						debug("Folding " + root + children + " = " + result);
						
						return result;
					}
					
					final IntegerComparisonPredicate icmp = cmp.getPredicate().getIntegerSelf();
					Value result = doICMP(
							icmp, 
							lhsV.getIntegerSelf(), 
							rhsV.getIntegerSelf());
					
					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				}
			}
			
			if (lhs.isConstantValue() && 
				(lhs.getConstantValueSelf().getValue().isConstantNullPointer() ||
				 lhs.getConstantValueSelf().getValue().isUndef()) &&
				rhs.isConstantValue() &&
				(rhs.getConstantValueSelf().getValue().isConstantNullPointer() ||
				 rhs.getConstantValueSelf().getValue().isUndef())) {
				final CmpLLVMLabel cmp = root.getCmpSelf();
				if (cmp.getPredicate().isInteger()) {
					final IntegerComparisonPredicate icmp = cmp.getPredicate().getIntegerSelf();
					Value result = doPointerICMP(
							icmp, 
							lhs.getConstantValueSelf().getValue().getConstantNullPointerSelf(), 
							rhs.getConstantValueSelf().getValue().getConstantNullPointerSelf());
					
					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				}
			}
		}
		
		if (root.isCast()) {
			// no-op cast
			final TypeLLVMLabel type = children.get(0).getTypeSelf();
			final LLVMLabel child1 = children.get(1);
			if (child1.isConstantValue()) {
				if (type.getType().equalsType(child1.getConstantValueSelf().getValue().getType())) {
					
					debug("Folding " + root + children + " = " + child1);
					
					return child1;
				}
			}
		}
		
		if (root.isCast()) {
			// all bitcasts of constant values
			final CastLLVMLabel cast = root.getCastSelf();
			final LLVMLabel target = children.get(1);
			if (cast.getOperator().equals(Cast.Bitcast) &&
				target.isConstantValue()) {
				
				Value result = doBitcast(
						children.get(0).getTypeSelf().getType(), 
						target.getConstantValueSelf().getValue());
				
				debug("Folding " + root + children + " = " + result);
				
				if (result != null)
					return new ConstantValueLLVMLabel(result);
			}
		}
		
		if (root.isCast()) {
			// int -> int|ptr
			CastLLVMLabel cast = root.getCastSelf();
			TypeLLVMLabel type = children.get(0).getTypeSelf();
			LLVMLabel child1 = children.get(1);
			
			if ((type.getType().isInteger() || 
				(type.getType().isComposite() && type.getType().getCompositeSelf().isPointer())) &&
				child1.isConstantValue() &&
				child1.getConstantValueSelf().getValue().isInteger()) {
				Value result = doCast(
						cast.getOperator(), 
						type.getType(), 
						child1.getConstantValueSelf().getValue());

				debug("Folding " + root + children + " = " + result);
				
				if (result != null)
					return new ConstantValueLLVMLabel(result);
			}
		}
		
		if (root.isCast()) {
			// int -> double or float
			// (UItoFP or SItoFP)
			CastLLVMLabel cast = root.getCastSelf();
			TypeLLVMLabel type = children.get(0).getTypeSelf();
			LLVMLabel child1 = children.get(1);
			
			if (type.getType().isFloatingPoint() &&
				(cast.getOperator().equals(Cast.UIToFP) ||
				 cast.getOperator().equals(Cast.SIToFP)) &&
				child1.isConstantValue() &&
				child1.getConstantValueSelf().getValue().isInteger()) {
				FloatingPointType fptype = type.getType().getFloatingPointSelf();
				if (fptype.getKind().equals(FloatingPointType.Kind.DOUBLE) ||
					fptype.getKind().equals(FloatingPointType.Kind.FLOAT)) {
					Value result = doCast(
							cast.getOperator(), 
							type.getType(), 
							child1.getConstantValueSelf().getValue());

					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				}
			}
		}
		
		if (root.isCast()) {
			// float->double or double->float
			CastLLVMLabel cast = root.getCastSelf();
			TypeLLVMLabel type = children.get(0).getTypeSelf();
			LLVMLabel child1 = children.get(1);
			if (cast.getOperator().equals(Cast.FPExt) &&
				type.getType().equalsType(DOUBLE) &&
				child1.isConstantValue() &&
				child1.getConstantValueSelf().getValue().isFloatingPoint() &&
				child1.getConstantValueSelf().getValue().getType().equalsType(FLOAT)) {
				// float -> double
				FloatingPointValue value = child1.getConstantValueSelf().getValue().getFloatingPointSelf();
				FloatingPointValue doubleValue = FloatingPointValue.fromDouble((double)value.getFloatBits());

				debug("Folding " + root + children + " = " + doubleValue);
				
				return new ConstantValueLLVMLabel(doubleValue);
			}
			else if (cast.getOperator().equals(Cast.FPTrunc) &&
					 type.getType().equalsType(FLOAT) &&
					 child1.isConstantValue() &&
					 child1.getConstantValueSelf().getValue().isFloatingPoint() &&
					 child1.getConstantValueSelf().getValue().getType().equalsType(DOUBLE)) {
				// double -> float
				FloatingPointValue value = child1.getConstantValueSelf().getValue().getFloatingPointSelf();
				FloatingPointValue floatValue = FloatingPointValue.fromFloat((float)value.getDoubleBits());

				debug("Folding " + root + children + " = " + floatValue);

				return new ConstantValueLLVMLabel(floatValue);
			}
		}
		
		if (root.isBinop()) {
			
			debug("folding a binop: " + root + children);
			
			final Binop binop = root.getBinopSelf().getOperator();
			LLVMLabel lhs = children.get(0);
			LLVMLabel rhs = children.get(1);
			if (lhs.isConstantValue() && lhs.getConstantValueSelf().getValue().isInteger() &&
				rhs.isConstantValue() && rhs.getConstantValueSelf().getValue().isInteger()) {
				debug("binop on ints: " + root + ", " + children);
				Value result = doBinopInteger(
						binop,
						lhs.getConstantValueSelf().getValue().getIntegerSelf(),
						rhs.getConstantValueSelf().getValue().getIntegerSelf());
				
				debug("Folding " + root + children + " = " + result);
				
				if (result != null) {
					return new ConstantValueLLVMLabel(result);
				}
			}
			
			if (lhs.isConstantValue() && lhs.getConstantValueSelf().getValue().isFloatingPoint() &&
				rhs.isConstantValue() && rhs.getConstantValueSelf().getValue().isFloatingPoint()) {
				FloatingPointType type = lhs.getConstantValueSelf().getValue().getType().getFloatingPointSelf();
				if (type.getKind().equals(FloatingPointType.Kind.DOUBLE)) {
					Value result = doBinopDouble(binop, 
							lhs.getConstantValueSelf().getValue().getFloatingPointSelf(),
							rhs.getConstantValueSelf().getValue().getFloatingPointSelf());
					
					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				} else if (type.getKind().equals(FloatingPointType.Kind.FLOAT)) {
					Value result = doBinopFloat(binop, 
							lhs.getConstantValueSelf().getValue().getFloatingPointSelf(),
							rhs.getConstantValueSelf().getValue().getFloatingPointSelf());
					
					debug("Folding " + root + children + " = " + result);
					
					if (result != null)
						return new ConstantValueLLVMLabel(result);
				}				
			}
		}
		
		if (root.isSimple()) {
			switch (root.getSimpleSelf().getOperator()) {
			case EXTRACTELEMENT: {
				if (children.size() != 2)
					throw new IllegalArgumentException("EXTRACTELEMENT should have 2 children");
				Value result = doExtractElement(children.get(0), children.get(1));

				debug("Folding " + root + children + " = " + result);
				
				if (result != null) 
					return new ConstantValueLLVMLabel(result);
				break;
			}
			}
		}

		debug("Cannot fold " + root + children);
		
		return null;
	}
	
	
	private Value doBinopDouble(
			Binop binop,
			FloatingPointValue lhs,
			FloatingPointValue rhs) {
		debug("doBinopDouble");
		final double lhsD = lhs.getDoubleBits();
		final double rhsD = rhs.getDoubleBits();
		switch (binop) {
		case Add:
			return FloatingPointValue.fromDouble(lhsD+rhsD);
		case Sub:
			return FloatingPointValue.fromDouble(lhsD-rhsD);
		case Mul:
			return FloatingPointValue.fromDouble(lhsD*rhsD);
		case FDiv:
			return FloatingPointValue.fromDouble(lhsD/rhsD);
		case FRem:
			return FloatingPointValue.fromDouble(lhsD%rhsD);
		default:
			throw new RuntimeException("This binop not valid on doubles: " + binop);
		}
	}
			
	private Value doBinopFloat(
			Binop binop,
			FloatingPointValue lhs,
			FloatingPointValue rhs) {
		debug("doBinopFloat");
		final float lhsD = lhs.getFloatBits();
		final float rhsD = rhs.getFloatBits();
		switch (binop) {
		case Add:
			return FloatingPointValue.fromFloat(lhsD+rhsD);
		case Sub:
			return FloatingPointValue.fromFloat(lhsD-rhsD);
		case Mul:
			return FloatingPointValue.fromFloat(lhsD*rhsD);
		case FDiv:
			return FloatingPointValue.fromFloat(lhsD/rhsD);
		case FRem: {
			float f = lhsD%rhsD;
			debug("float result = " + f);
			Value v = FloatingPointValue.fromFloat(f);
			debug("value result = " + v);
			return v;
		}
		default:
			throw new RuntimeException("This binop not valid on floats: " + binop);
		}
	}
	
	private Value doBitcast(Type toType, Value fromValue) {
		debug("doBitcast");
		if (fromValue.isUndef())
			return new UndefValue(toType);
		
		final boolean[] bits = getBits(fromValue);
		final int[] index = {0};
		Value result = makeValue(bits, index, toType);
		if (index[0] != bits.length)
			throw new RuntimeException("Didn't use all the bits");
		
		return result;
	}
	
	/**
	 * Values are stored little-endian.
	 */
	private boolean[] getBits(Value value) {
		if (value.isInteger()) {
			final IntegerValue iv = value.getIntegerSelf(); 
			boolean[] result = new boolean[iv.getWidth()];
			for (int i = 0; i < result.length; i++)
				result[i] = iv.getBit(i);
			return result;
		} else if (value.isFloatingPoint()) {
			final FloatingPointValue fp = value.getFloatingPointSelf();
			final int width = fp.getType().getKind().getTypeSize();
			boolean[] result = new boolean[width];
			for (int i = 0; i < width; i++)
				result[i] = fp.getBit(i);
			return result;
		} else if (value.isConstantArray()) {
			final ConstantArrayValue array = value.getConstantArraySelf();
			final List<boolean[]> fields = 
				new ArrayList<boolean[]>((int)array.getNumElements().signedValue());
			int totalSize = 0;
			for (int i = 0; i < array.getNumElements().signedValue(); i++) {
				boolean[] bits = getBits(array.getElement(i));
				totalSize += bits.length;
				fields.add(bits);
			}
			boolean[] result = new boolean[totalSize];
			int index = 0;
			for (boolean[] bits : fields) {
				for (int i = 0; i < bits.length; i++)
					result[index++] = bits[i];
			}
			return result;
		} else if (value.isConstantNullPointer()) {
			int width = layout.getTypeSizeInBits(value.getType());
			return new boolean[width];
		} else if (value.isConstantStructure()) {
			final ConstantStructureValue struct = value.getConstantStructureSelf();
			final List<boolean[]> fields = 
				new ArrayList<boolean[]>((int)struct.getNumFields());
			int totalSize = 0;
			for (int i = 0; i < struct.getNumFields(); i++) {
				boolean[] bits = getBits(struct.getFieldValue(i));
				totalSize += bits.length;
				fields.add(bits);
			}
			boolean[] result = new boolean[totalSize];
			int index = 0;
			for (boolean[] bits : fields) {
				for (int i = 0; i < bits.length; i++)
					result[index++] = bits[i];
			}
			return result;
		} else if (value.isConstantVector()) {
			final ConstantVectorValue vector = value.getConstantVectorSelf();
			final List<boolean[]> fields = 
				new ArrayList<boolean[]>((int)vector.getNumElements().signedValue());
			int totalSize = 0;
			for (int i = 0; i < vector.getNumElements().signedValue(); i++) {
				boolean[] bits = getBits(vector.getElement(i));
				totalSize += bits.length;
				fields.add(bits);
			}
			boolean[] result = new boolean[totalSize];
			int index = 0;
			for (boolean[] bits : fields) {
				for (int i = 0; i < bits.length; i++)
					result[index++] = bits[i];
			}
			return result;
		} else {
			throw new RuntimeException("Value has no defined bit structure: " + value);
		}
	}

	private Value makeValue(boolean[] bits, int[] index, Type type) {
		if (type.isInteger()) {
			final IntegerType itype = type.getIntegerSelf();
			final int width = itype.getWidth();
			if (index[0] + width > bits.length)
				throw new RuntimeException("Premature EOB");
			final BitSet bitset = new BitSet();
			for (int i = 0; i < width; i++) {
				if (bits[index[0]+i])
					bitset.set(i);
			}
			index[0] += width;
			return new IntegerValue(width, bitset); 
		} else if (type.isFloatingPoint()) {
			final FloatingPointType fptype = type.getFloatingPointSelf();
			final int width = fptype.getKind().getTypeSize();
			if (index[0] + width > bits.length)
				throw new RuntimeException("Premature EOB");
			final BitSet bitset = new BitSet();
			for (int i = 0; i < width; i++) {
				if (bits[index[0]+i])
					bitset.set(i);
			}
			index[0] += width;
			return FloatingPointValue.get(fptype, bitset); 
		} else if (type.isComposite()) {
			final CompositeType ctype = type.getCompositeSelf();
			if (ctype.isPointer()) {
				final int typewidth = layout.getTypeSizeInBits(ctype);
				if (index[0] + typewidth > bits.length)
					throw new RuntimeException("Premature EOB");
				index[0] += typewidth;
				return new ConstantNullPointerValue(ctype.getPointerSelf());
			} else if (ctype.isArray()) {
				final int length = (int)ctype.getArraySelf().getNumElements().signedValue();
				final List<Value> elements = new ArrayList<Value>(length);
				final Type elementType = ctype.getArraySelf().getElementType();
				for (int i = 0; i < length; i++)
					elements.add(makeValue(bits, index, elementType));
				return new ConstantExplicitArrayValue(ctype.getArraySelf(), elements);
			} else if (ctype.isVector()) {
				final int length = (int)ctype.getVectorSelf().getNumElements().signedValue();
				final List<Value> elements = new ArrayList<Value>(length);
				final Type elementType = ctype.getVectorSelf().getElementType();
				for (int i = 0; i < length; i++)
					elements.add(makeValue(bits, index, elementType));
				return new ConstantExplicitVectorValue(ctype.getVectorSelf(), elements);
			} else if (ctype.isStructure()) {
				final StructureType stype = ctype.getStructureSelf();
				final List<Value> fields = new ArrayList<Value>(stype.getNumFields());
				for (int i = 0; i < stype.getNumFields(); i++) {
					fields.add(makeValue(bits, index, stype.getFieldType(i)));
				}
				return new ConstantStructureValue(ctype.getStructureSelf(), fields);				
			} else {
				throw new RuntimeException("Unknown composite type: " + type);
			}
		} else {
			throw new RuntimeException("Unknown type: " + type);
		}
	}

	private BigInteger getMask(int width) {
		final StringBuffer buffer = new StringBuffer(width);
		for (int i = 0; i < width; i++)
			buffer.append("1");
		return new BigInteger(buffer.toString(), 2);
	}
	
	private Value doICMP(
			IntegerComparisonPredicate icmp, 
			IntegerValue lhs,
			IntegerValue rhs) {
		debug("doICMP");
		
		BigInteger lhsB = lhs.getAsBigInteger();
		BigInteger rhsB = rhs.getAsBigInteger();

		if (icmp.equals(ICMP_EQ)) {
			return lhsB.equals(rhsB) ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_NE)) {
			return (!lhsB.equals(rhsB)) ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SGT)) {
			return lhsB.compareTo(rhsB) > 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SGE)) {
			return lhsB.compareTo(rhsB) >= 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SLT)) {
			return lhsB.compareTo(rhsB) < 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SLE)) {
			return lhsB.compareTo(rhsB) <= 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_UGE)) {
			BigInteger mask = getMask(lhs.getType().getWidth());
			lhsB = lhsB.and(mask);
			rhsB = rhsB.and(mask);
			return lhsB.compareTo(rhsB) >= 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_UGT)) {
			BigInteger mask = getMask(lhs.getType().getWidth());
			lhsB = lhsB.and(mask);
			rhsB = rhsB.and(mask);
			return lhsB.compareTo(rhsB) > 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_ULE)) {
			BigInteger mask = getMask(lhs.getType().getWidth());
			lhsB = lhsB.and(mask);
			rhsB = rhsB.and(mask);
			return lhsB.compareTo(rhsB) <= 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_ULT)) {
			BigInteger mask = getMask(lhs.getType().getWidth());
			lhsB = lhsB.and(mask);
			rhsB = rhsB.and(mask);
			return lhsB.compareTo(rhsB) < 0 ? IntegerValue.TRUE : IntegerValue.FALSE;
		} else 
			return null;
	}

	private Value doPointerICMP(
			IntegerComparisonPredicate icmp, 
			ConstantNullPointerValue lhs,
			ConstantNullPointerValue rhs) {
		debug("doPointerICMP");
		
		if (icmp.equals(ICMP_EQ)) {
			return IntegerValue.TRUE;
		} else if (icmp.equals(ICMP_NE)) {
			return IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SGT)) {
			return IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SGE)) {
			return IntegerValue.TRUE;
		} else if (icmp.equals(ICMP_SLT)) {
			return IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_SLE)) {
			return IntegerValue.TRUE;
		} else if (icmp.equals(ICMP_UGE)) {
			return IntegerValue.TRUE;
		} else if (icmp.equals(ICMP_UGT)) {
			return IntegerValue.FALSE;
		} else if (icmp.equals(ICMP_ULE)) {
			return IntegerValue.TRUE;
		} else if (icmp.equals(ICMP_ULT)) {
			return IntegerValue.FALSE;
		} else 
			return null;
	}
	
	private Value doExtractElement(LLVMLabel vec, LLVMLabel idx) {
		debug("doExtractElement");
		if (vec.isConstantValue() && vec.getConstantValueSelf().getValue().isConstantVector()) {
			ConstantVectorValue vector = vec.getConstantValueSelf().getValue().getConstantVectorSelf();
			if (idx.isConstantValue() && idx.getConstantValueSelf().getValue().isInteger()) {
				// extractelement <c1,c2,c3,...,cN> i32
				int index = idx.getConstantValueSelf().getValue().getIntegerSelf().getIntBits();
				return vector.getElement(index);
			}
		}
		
		return null;
	}
	
	
	private IntegerValue doBinopInteger(
			Binop binop,
			IntegerValue lhs,
			IntegerValue rhs) {
		debug("doBinopInteger");
		switch (binop) {
		case AddNsw:
		case AddNuw:
		case AddNswNuw:
		case Add: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			BigInteger result = biglhs.add(bigrhs);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case SubNsw:
		case SubNuw:
		case SubNswNuw:
		case Sub: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			BigInteger result = biglhs.subtract(bigrhs);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case MulNsw:
		case MulNuw:
		case MulNswNuw:
		case Mul: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			BigInteger result = biglhs.multiply(bigrhs);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case UDiv: {
			BigInteger mask = getMask(lhs.getType().getWidth());
			BigInteger lhsB = lhs.getAsBigInteger().and(mask);
			BigInteger rhsB = rhs.getAsBigInteger().and(mask);
			if (rhsB.equals(BigInteger.ZERO))
				return null;
			return IntegerValue.get(lhs.getWidth(), lhsB.divide(rhsB));
		}
		case SDivExact:
		case SDiv: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			if (bigrhs.equals(BigInteger.ZERO))
				return null;
			BigInteger result = biglhs.divide(bigrhs);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case URem: {
			BigInteger mask = getMask(lhs.getType().getWidth());
			BigInteger lhsB = lhs.getAsBigInteger().and(mask);
			BigInteger rhsB = rhs.getAsBigInteger().and(mask);
			if (rhsB.equals(BigInteger.ZERO))
				return null;
			BigInteger[] dr = lhsB.divideAndRemainder(rhsB);
			return IntegerValue.get(lhs.getWidth(), dr[1]);
		}
		case SRem: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			if (bigrhs.equals(BigInteger.ZERO))
				return null;
			BigInteger result = biglhs.remainder(bigrhs);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case Shl: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			int shiftamt;
			try {shiftamt = bigrhs.intValue();}
			catch (Throwable t) {return null;}
			BigInteger result = biglhs.shiftLeft(shiftamt);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case LShr: {
			BigInteger biglhs = lhs.getAsBigInteger();
			biglhs = biglhs.and(getMask(lhs.getWidth()));
			BigInteger bigrhs = rhs.getAsBigInteger();
			int shiftamt;
			try {shiftamt = bigrhs.intValue();}
			catch (Throwable t) {return null;}
			BigInteger result = biglhs.shiftRight(shiftamt);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case AShr: {
			BigInteger biglhs = lhs.getAsBigInteger();
			BigInteger bigrhs = rhs.getAsBigInteger();
			int shiftamt;
			try {shiftamt = bigrhs.intValue();}
			catch (Throwable t) {return null;}
			BigInteger result = biglhs.shiftRight(shiftamt);
			return IntegerValue.get(lhs.getWidth(), result);
		}
		case And: {
			long[] lhswords = lhs.getAsLongArray();
			long[] rhswords = rhs.getAsLongArray();
			for (int i = 0; i < lhswords.length; i++) {
				lhswords[i] &= rhswords[i];
			}
			return IntegerValue.get(lhs.getWidth(), lhswords);
		}
		case Or: {
			long[] lhswords = lhs.getAsLongArray();
			long[] rhswords = rhs.getAsLongArray();
			for (int i = 0; i < lhswords.length && i < rhswords.length; i++) {
				lhswords[i] |= rhswords[i];
			}
			return IntegerValue.get(lhs.getWidth(), lhswords);
		}
		case Xor: {
			long[] lhswords = lhs.getAsLongArray();
			long[] rhswords = rhs.getAsLongArray();
			for (int i = 0; i < lhswords.length && i < rhswords.length; i++) {
				lhswords[i] ^= rhswords[i];
			}
			return IntegerValue.get(lhs.getWidth(), lhswords);
		}
		default:
			return null;
		}
	}
	
	
	private Value doCast(Cast cast, Type totype, Value input) {
		debug("doCast");
		if (cast.equals(Cast.IntToPtr)) {
			// can only cast null values!
			if (!(totype.isComposite() && totype.getCompositeSelf().isPointer()))
				throw new RuntimeException("IntToPtr should only name a pointer type: " + totype);
			if (!input.isInteger())
				return null;
			if (!input.getIntegerSelf().isZero())
				return null;
			return Value.getNullValue(totype);
		}
		else if (cast.equals(Cast.UIToFP)) {
			// unsigned int -> double or float
			BigInteger b = input.getIntegerSelf().getAsBigInteger();
			b = b.and(getMask(input.getIntegerSelf().getWidth()));
			if (totype.getFloatingPointSelf().getKind().equals(Kind.DOUBLE)) {
				return FloatingPointValue.fromDouble(b.doubleValue());
			} else if (totype.getFloatingPointSelf().getKind().equals(Kind.FLOAT)) {
				return FloatingPointValue.fromFloat(b.floatValue());
			} else
				return null;
		}
		else if (cast.equals(Cast.SIToFP)) {
			// signed int -> double or float
			BigInteger b = input.getIntegerSelf().getAsBigInteger();
			if (totype.getFloatingPointSelf().getKind().equals(Kind.DOUBLE)) {
				return FloatingPointValue.fromDouble(b.doubleValue());
			} else if (totype.getFloatingPointSelf().getKind().equals(Kind.FLOAT)) {
				return FloatingPointValue.fromFloat(b.floatValue());
			} else
				return null;
		}
		else if (cast.equals(Cast.PtrToInt)) {
			// can only cast null pointer to int
			if (!input.isConstantNullPointer())
				return null;
			if (!totype.isInteger())
				throw new RuntimeException("PtrToInt should only name an integer type: " + totype);
			return Value.getNullValue(totype);
		}
		else if (totype.isInteger() && input.isInteger()) {
			IntegerType itype = totype.getIntegerSelf();
			IntegerValue ivalue = input.getIntegerSelf();
			
			// int to int casting
			// only handle Trunc, SExt, ZExt, and Bitcast
			switch(cast) {
			case Trunc: {
				long[] longbits = ivalue.getAsLongArray();
				return IntegerValue.get(itype.getWidth(), longbits);
			}
				
			case SExt: {
				long[] oldbits = ivalue.getAsLongArray();
				if (ivalue.getBit(ivalue.getWidth()-1)) {
					// sign extend
					int numlongs = (itype.getWidth()+63)>>6;
					long[] newbits = new long[numlongs];
					for (int i = 0; i < oldbits.length; i++) {
						newbits[i] = oldbits[i];
					}

					// sign-extend the last long from oldbits
					int startBit = (ivalue.getWidth()-1)&63;
					int lastLong = oldbits.length-1;
					for (int i = startBit+1; i < 64; i++) {
						newbits[lastLong] |= (1L<<i);
					}
					
					// put -1L into the longs beyond oldbits
					for (int i = lastLong+1; i < newbits.length; i++) {
						newbits[i] = -1L;
					}
					
					return IntegerValue.get(itype.getWidth(), newbits);
				} else {
					// zero extend, do nothing
					return IntegerValue.get(itype.getWidth(), oldbits);
				}
			}
			
			case ZExt: {
				long[] longbits = ivalue.getAsLongArray();
				return IntegerValue.get(itype.getWidth(), longbits);
			}
			
			case Bitcast: {
				// should be a no-op, right?
				return ivalue;
			}
			
			default:
				return null;
			}
		}
		
		return null;
	}
}
