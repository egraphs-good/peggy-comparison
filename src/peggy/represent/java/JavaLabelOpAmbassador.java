package peggy.represent.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import util.AbstractVariaticFunction;
import eqsat.BasicOp;
import eqsat.OpAmbassador;
import eqsat.OpExpression;
import static peggy.represent.java.JavaOperator.*;

/**
 * This is the opambassador class for Java.
 */
public class JavaLabelOpAmbassador 
extends AbstractVariaticFunction<JavaLabel,JavaLabel,JavaLabel> 
implements OpAmbassador<JavaLabel> {
	private final Set<MethodJavaLabel> sigmaInvariantMethods;
	private final AnnotationConstantFolder annotationFolder;
	
	public JavaLabelOpAmbassador(AnnotationConstantFolder acf) {
		this.annotationFolder = acf;
		this.sigmaInvariantMethods = new HashSet<MethodJavaLabel>();
	}
	
	public void addSigmaInvariantMethod(MethodJavaLabel method) {
		this.sigmaInvariantMethods.add(method);
	}
	
	public JavaLabel getBasicOp(BasicOp op) {
		switch(op) {
		case True: return new BasicJavaLabel(BasicOp.True);
		case False: return new BasicJavaLabel(BasicOp.False);
		case Negate: return new BasicJavaLabel(BasicOp.Negate);
		case And: return new BasicJavaLabel(BasicOp.And);
		case Or: return new BasicJavaLabel(BasicOp.Or);
		case Equals: return new BasicJavaLabel(BasicOp.Equals);
		default: return null;
		}
	}
	
	public boolean canPreEvaluate(JavaLabel op) {
		// means "can be statically evaluated" assuming the children are as well
		// i.e. "+" can be preevaluated if its children can
		if (op.isConstant())
			return true;
		else if (op.isField())
			return true;
		else if (op.isMethod())
			return true;
		else if (op.isAnnotation())
			return this.annotationFolder.canFold(op.getAnnotationSelf());
		else if (op.isBasic())
			return true;
		
		else if (op.isSimple()) {
			switch (op.getSimpleSelf().getOperator()) {
			case PRIMITIVECAST:
			case PLUS:
			case DIVIDE:
			case MINUS:
			case TIMES:
			case MOD:
			case CMP:
			case CMPL:
			case CMPG:
			case GREATER_THAN_EQUAL:
			case GREATER_THAN:
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case EQUAL:
			case NOT_EQUAL:
			case BITWISE_AND:
			case BITWISE_OR:
			case SHIFT_LEFT:
			case SHIFT_RIGHT:
			case UNSIGNED_SHIFT_RIGHT:
			case XOR:
			case NEG:
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isFree(JavaLabel op) {
		// means "costs nothing to evaluate" i.e. rho, getValue, getException, params, etc
		if (op.isSimple()) {
			switch (op.getSimpleSelf().getOperator()) {
			case INJL:
			case INJR:
			case VOID:
			case PARAMS:
			case DIMS:
				return true;
			default: 
				return false;
			}
		} else {
			return true;
		}
	}
	
	public boolean needsAnyChild(JavaLabel op) {
		return JavaLabelStickyPredicate.isSticky(-1, op);
	}
	
	public boolean needsChild(JavaLabel op, int index) {
		return JavaLabelStickyPredicate.isSticky(index, op);
	}
	
	public BasicOp getBasicOp(JavaLabel op) {
		if (op.isBasic())
			return op.getBasicSelf().getOperator();
		else
			return null;
	}

	public boolean isAnyVolatile(JavaLabel op) {
		boolean any = isVolatile(op, 0);
		if (any) return true;
		return false;
	}
	
	public boolean isVolatile(JavaLabel op, int child) {
		if (op.isSimple()) {
			JavaOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case INVOKESTATIC:
			case INVOKESPECIAL:
			case INVOKEVIRTUAL:
			case INVOKEINTERFACE:
			case SETARRAY:
			case CAST:
			case SETFIELD:
			case ARRAYLENGTH:
			case NEWARRAY:
			case SETSTATICFIELD:
			case NEWINSTANCE:
			case NEWMULTIARRAY:
			case GETFIELD:
			case GETSTATICFIELD:
			case ENTERMONITOR:
			case EXITMONITOR:
			case THROW:
			case INSTANCEOF: 
			case GETARRAY: 
				if (child==0)
					return true;
			}
		}		
		return false;
	}
	
	public JavaLabel getChainVersion(JavaLabel op, int child) {
		if (!isVolatile(op, child))
			throw new IllegalArgumentException("Child "+child+" is not volatile for operator "+op);
		return op;
	}
	
	public JavaLabel getChainProjectVolatile(JavaLabel op, int child) {
		if (!isVolatile(op, child))
			throw new IllegalArgumentException("Child "+child+" of operator "+op+" is not volatile");

		if (op.isSimple()) {
			JavaOperator operator = op.getSimpleSelf().getOperator();
			if ((operator.equals(INVOKESTATIC) && child==0) ||
				(operator.equals(INVOKESPECIAL) && child==0) ||
				(operator.equals(INVOKEVIRTUAL) && child==0) ||
				(operator.equals(INVOKEINTERFACE) && child==0)) {
				// (S,E|v) -> (S,E|v)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
//			else if ((operator.equals(INVOKESTATIC) && child==3) ||
//					 (operator.equals(INVOKESPECIAL) && child==4) ||
//					 (operator.equals(INVOKEVIRTUAL) && child==4) ||
//					 (operator.equals(INVOKEINTERFACE) && child==4)) {
//				// (S,E|v,m) -> (S,E|v,m)
//				return JavaLabel.makeProjection(JavaLabel.COUNTER_PROJECTION);
//			}
			else if ((operator.equals(SETFIELD) && child==0) ||
					 (operator.equals(SETSTATICFIELD) && child==0) ||
					 (operator.equals(ENTERMONITOR) && child==0) ||
					 (operator.equals(EXITMONITOR) && child==0) ||
					 (operator.equals(SETARRAY) && child==0)) {
				// (S,E) -> (S,E)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
			else if ((operator.equals(NEWINSTANCE) && child==0) ||
					  (operator.equals(NEWMULTIARRAY) && child==0) ||
					  (operator.equals(NEWARRAY) && child==0)) {
				// (s,E|v) -> (S,E|v)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
//			else if ((operator.equals(NEWINSTANCE) && child==2) ||
//					  (operator.equals(NEWMULTIARRAY) && child==3) ||
//					  (operator.equals(NEWARRAY) && child==3)) {
//				// (S,E|v,m) -> (S,E|v,m)
//				return JavaLabel.makeProjection(JavaLabel.COUNTER_PROJECTION);
//			}
			else if ((operator.equals(CAST) && child==0) ||
					  (operator.equals(ARRAYLENGTH) && child==0) ||
					  (operator.equals(GETFIELD) && child==0) ||
					  (operator.equals(GETSTATICFIELD) && child==0) ||
					  (operator.equals(GETARRAY) && child==0)) {
				// (S,E|v) -> (S,E|v)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
			else if (operator.equals(THROW) && child==0) {
				// (S,E) -> (S,E)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
			else if (operator.equals(INSTANCEOF) && child==0) {
				// (S,v) -> (S,v)
				return SimpleJavaLabel.create(JavaOperator.RHO_SIGMA);
			}
		}
		throw new RuntimeException("Mike forgot to support "+op);
	}
	
	public JavaLabel getChainProjectValue(JavaLabel op, int child) {
		if (!isVolatile(op, child))
			throw new IllegalArgumentException("Child "+child+" of operator "+op+" is not volatile");
		return null;
	}
	
	public boolean isEquivalent(OpExpression<JavaLabel> first, int firstChild,
								OpExpression<JavaLabel> second, int secondChild) {
		JavaLabel firstLabel = first.getOperation();
		if (firstLabel.isSimple()) {
			JavaOperator operator = firstLabel.getSimpleSelf().getOperator();
			if ((operator.equals(SETFIELD) && firstChild==0) ||
				(operator.equals(NEWARRAY) && firstChild==0) ||
				(operator.equals(SETSTATICFIELD) && firstChild==0) ||
				(operator.equals(NEWINSTANCE) && firstChild==0) ||
				(operator.equals(NEWMULTIARRAY) && firstChild==0) ||
				(operator.equals(ENTERMONITOR) && firstChild==0) ||
				(operator.equals(EXITMONITOR) && firstChild==0) ||
				(operator.equals(SETARRAY) && firstChild==0)) {
				return false;
			} else if ((operator.equals(INVOKESTATIC) && firstChild==0) ||
					   (operator.equals(INVOKESPECIAL) && firstChild==0) ||
					   (operator.equals(INVOKEVIRTUAL) && firstChild==0) ||
					   (operator.equals(INVOKEINTERFACE) && firstChild==0)) {
				int methodindex = operator.equals(INVOKESTATIC) ? 1 : 2;
				OpExpression<JavaLabel> methodchild = first.getOperand(methodindex);
				if (methodchild!=null && 
					methodchild.getOperation().isMethod() && 
					this.sigmaInvariantMethods.contains(methodchild.getOperation())) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	
	private Integer toInt(JavaLabel l) {
		if (l.isConstant() && l.getConstantSelf().getValue() instanceof IntConstant) {
			return ((IntConstant)l.getConstantSelf().getValue()).value;
		} else {
			return null;
		}
	}
	private Long toLong(JavaLabel l) {
		if (l.isConstant() && l.getConstantSelf().getValue() instanceof LongConstant) {
			return ((LongConstant)l.getConstantSelf().getValue()).value;
		} else {
			return null;
		}
	}
	private Float toFloat(JavaLabel l) {
		if (l.isConstant() && l.getConstantSelf().getValue() instanceof FloatConstant) {
			return ((FloatConstant)l.getConstantSelf().getValue()).value;
		} else {
			return null;
		}
	}
	private Double toDouble(JavaLabel l) {
		if (l.isConstant() && l.getConstantSelf().getValue() instanceof DoubleConstant) {
			return ((DoubleConstant)l.getConstantSelf().getValue()).value;
		} else {
			return null;
		}
	}
	
	private ConstantValueJavaLabel makeInt(int i) {
		return new ConstantValueJavaLabel(IntConstant.v(i));
	}
	private ConstantValueJavaLabel makeLong(long i) {
		return new ConstantValueJavaLabel(LongConstant.v(i));
	}
	private ConstantValueJavaLabel makeFloat(float i) {
		return new ConstantValueJavaLabel(FloatConstant.v(i));
	}
	private ConstantValueJavaLabel makeDouble(double i) {
		return new ConstantValueJavaLabel(DoubleConstant.v(i));
	}
	
	
	/**
	 * This function implements constant folding on JavaLabels.
	 * @param op the parent node's JavaLabel
	 * @param args the child nodes' JavaLabels
	 * @return a new JavaLabel that represents the folded value, or null if error (i.e. divide by 0)
	 */
	public JavaLabel get(JavaLabel op, List<? extends JavaLabel> args) {
		if (op.isAnnotation()) {
			AnnotationJavaLabel anl = op.getAnnotationSelf();
			return this.annotationFolder.fold(anl, args);
		} 
		else if (op.isBasic()) {
			switch (op.getBasicSelf().getOperator()) {
			case Negate: {
				// boolean negation
				boolean value = false;
				if (args.get(0).isTrue())
					value = true;
				else if (args.get(0).isFalse())
					value = false;
				else
					throw new IllegalArgumentException("Bad args for negate: " + args.get(0));
				
				return (value ? new BasicJavaLabel(BasicOp.False) : new BasicJavaLabel(BasicOp.True));
			}
			case And:
			case Or: {
				boolean lhs=false, rhs=false;
				if (args.get(0).isTrue())
					lhs = true;
				else if (args.get(0).isFalse())
					lhs = false;
				else
					throw new IllegalArgumentException("Bad args");
				
				if (args.get(1).isTrue())
					rhs = true;
				else if (args.get(1).isFalse())
					rhs = false;
				else
					throw new IllegalArgumentException("Bad args");

				boolean result;
				if (op.getBasicSelf().getOperator().equals(BasicOp.And))
					result = lhs & rhs;
				else
					result = lhs | rhs;
				return (result ? new BasicJavaLabel(BasicOp.True) : new BasicJavaLabel(BasicOp.False));
			}
			case Equals: {
				boolean equal = args.get(0).equalsLabel(args.get(1));
				return equal ? new BasicJavaLabel(BasicOp.True) :
							   new BasicJavaLabel(BasicOp.False);
			}
				
			default:
				throw new IllegalArgumentException("Cannot fold: " + op);
			}
		}
		else if (op.isSimple()) {
			SimpleJavaLabel operator = op.getSimpleSelf();
			switch (operator.getOperator()) {
         case PRIMITIVECAST: {
            assertArgs(args, operator.getOperator(), 2);
            JavaLabel typeLabel = args.get(0);
            JavaLabel valueLabel = args.get(1);

            if (!typeLabel.isType())
               throw new RuntimeException("First child must be a type");
            if (!valueLabel.isConstant())
               throw new RuntimeException("Second child must be a constant");

            Constant input = valueLabel.getConstantSelf().getValue();
            Number inputValue;
            if (input instanceof IntConstant)
               inputValue = ((IntConstant)input).value;
            else if (input instanceof LongConstant)
               inputValue = ((LongConstant)input).value;
            else if (input instanceof FloatConstant)
               inputValue = ((FloatConstant)input).value;
            else if (input instanceof DoubleConstant)
               inputValue = ((DoubleConstant)input).value;
            else
               throw new RuntimeException("Expecting numeric constant");
            
            Type type = typeLabel.getTypeSelf().getType();
            if (type instanceof IntType)
               return makeInt(inputValue.intValue());
            else if (type instanceof ShortType)
               return makeInt(inputValue.shortValue());
            else if (type instanceof CharType)
               return makeInt((char)inputValue.shortValue());
            else if (type instanceof ByteType)
               return makeInt(inputValue.byteValue());
            else if (type instanceof FloatType)
               return makeFloat(inputValue.floatValue());
            else if (type instanceof DoubleType)
               return makeDouble(inputValue.doubleValue());
            else if (type instanceof LongType)
               return makeLong(inputValue.longValue());
            else
               throw new RuntimeException("Invalid type given: " + type);
         }

			case PLUS:
			case DIVIDE:
			case MINUS:
			case TIMES:
			case MOD: {
				assertArgs(args, operator.getOperator(), 2);
				JavaLabel arg1 = args.get(0);
				JavaLabel arg2 = args.get(1);
				if (toInt(arg1)!=null && toInt(arg2)!=null) {
					int int1 = toInt(arg1);
					int int2 = toInt(arg2);
					
					switch(operator.getOperator()) {
					case PLUS: return makeInt(int1+int2);
					case DIVIDE: return (int2==0 ? null : makeInt(int1/int2));
					case MINUS: return makeInt(int1-int2);
					case TIMES: return makeInt(int1*int2);
					case MOD: return (int2==0 ? null : makeInt(int1%int2));
					default: throw new RuntimeException("This should never happen");
					}
				} else if (toLong(arg1)!=null && toLong(arg2)!=null) {
					long long1 = toLong(arg1);
					long long2 = toLong(arg2);
					
					switch(operator.getOperator()) {
					case PLUS: return makeLong(long1+long2);
					case DIVIDE: return (long2==0 ? null : makeLong(long1/long2));
					case MINUS: return makeLong(long1-long2);
					case TIMES: return makeLong(long1*long2);
					case MOD: return (long2==0 ? null : makeLong(long1%long2));
					default: throw new RuntimeException("This should never happen");
					}
				} else if (toDouble(arg1)!=null && toDouble(arg2)!=null) {
					double double1 = toDouble(arg1);
					double double2 = toDouble(arg2);
					
					switch(operator.getOperator()) {
					case PLUS: return makeDouble(double1+double2);
					case DIVIDE: return makeDouble(double1/double2);
					case MINUS: return makeDouble(double1-double2);
					case TIMES: return makeDouble(double1*double2);
					case MOD: return makeDouble(double1%double2);
					default: throw new RuntimeException("This should never happen");
					}
				} else if (toFloat(arg1)!=null && toFloat(arg2)!=null) {
					float float1 = toFloat(arg1);
					float float2 = toFloat(arg2);
					
					switch(operator.getOperator()) {
					case PLUS: return makeFloat(float1+float2);
					case DIVIDE: return makeFloat(float1/float2);
					case MINUS: return makeFloat(float1-float2);
					case TIMES: return makeFloat(float1*float2);
					case MOD: return makeFloat(float1%float2);
					default: throw new RuntimeException("This should never happen");
					}
				} else {
					badArgs(args, operator.getOperator());
				}
			}
				
			case CMP:{
				assertArgs(args, operator.getOperator(), 2);
				if (!(toLong(args.get(0))!=null && toLong(args.get(1))!=null))
					badArgs(args, operator.getOperator());
				long value1 = toLong(args.get(0));
				long value2 = toLong(args.get(1));
				if (value1 > value2)
					return makeInt(1);
				else if (value1 < value2)
					return makeInt(-1);
				else
					return makeInt(0);
			}
				
			case CMPL:
			case CMPG:{
				assertArgs(args, operator.getOperator(), 2);
				if (toFloat(args.get(0))!=null && toFloat(args.get(1))!=null) {
					float value1 = toFloat(args.get(0));
					float value2 = toFloat(args.get(1));
					if (Float.isNaN(value1) || Float.isNaN(value2)) {
						if (operator.getOperator().equals(JavaOperator.CMPG))
							return makeInt(1);
						else
							return makeInt(-1);
					} else if (value1 > value2) {
						return makeInt(1);
					} else if (value1 < value2) {
						return makeInt(-1);
					} else {
						return makeInt(0);
					}
				} else if (toDouble(args.get(0))!=null && toDouble(args.get(1))!=null) {
					double value1 = toDouble(args.get(0));
					double value2 = toDouble(args.get(1));
					if (Double.isNaN(value1) || Double.isNaN(value2)) {
						if (operator.getOperator().equals(JavaOperator.CMPG))
							return makeInt(1);
						else
							return makeInt(-1);
					} else if (value1 > value2) {
						return makeInt(1);
					} else if (value1 < value2) {
						return makeInt(-1);
					} else {
						return makeInt(0);
					}
				} else {
					badArgs(args, operator.getOperator());
				}
			}
				
			case EQUAL:
			case NOT_EQUAL:{
				// these can also compare references (i.e. null)
				assertArgs(args, operator.getOperator(), 2);
				boolean equal = args.get(0).equalsLabel(args.get(1));
				return (equal == operator.getOperator().equals(EQUAL)) ? 
						new BasicJavaLabel(BasicOp.True) : 
						new BasicJavaLabel(BasicOp.False); 
			}
				
			case GREATER_THAN_EQUAL:
			case GREATER_THAN:
			case LESS_THAN:
			case LESS_THAN_EQUAL:{
				assertArgs(args, operator.getOperator(), 2);
				if (toInt(args.get(0))==null || toInt(args.get(1))==null)
					badArgs(args, operator.getOperator());
				int value1 = toInt(args.get(0));
				int value2 = toInt(args.get(1));
				boolean isOne;
				switch (operator.getOperator()) {
				case EQUAL: isOne = (value1==value2); break;
				case GREATER_THAN_EQUAL: isOne = (value1>=value2); break;
				case GREATER_THAN: isOne = (value1>value2); break;
				case LESS_THAN: isOne = (value1<value2); break;
				case LESS_THAN_EQUAL: isOne = (value1<=value2) ; break;
				case NOT_EQUAL: isOne = (value1!=value2); break;
				default: throw new RuntimeException("This should never happen!");	
				}
				return (isOne ? 
						new BasicJavaLabel(BasicOp.True) :
						new BasicJavaLabel(BasicOp.False));
			}

			case SHIFT_LEFT:
			case SHIFT_RIGHT:
			case UNSIGNED_SHIFT_RIGHT:{
				assertArgs(args, operator.getOperator(), 2);
				if (!(toInt(args.get(1))!=null))
					badArgs(args, operator.getOperator());
				int rhs = toInt(args.get(1));
				if (toLong(args.get(0))!=null) {
					long lhs = toLong(args.get(0));
					switch (operator.getOperator()) {
					case SHIFT_LEFT: return makeLong(lhs<<rhs);
					case SHIFT_RIGHT: return makeLong(lhs>>rhs);
					case UNSIGNED_SHIFT_RIGHT: return makeLong(lhs>>>rhs);
					default: throw new RuntimeException("This should never happen!");
					}
				} else if (toInt(args.get(0))!=null) {
					int lhs = toInt(args.get(0));
					switch (operator.getOperator()) {
					case SHIFT_LEFT: return makeInt(lhs<<rhs);
					case SHIFT_RIGHT: return makeInt(lhs>>rhs);
					case UNSIGNED_SHIFT_RIGHT: return makeInt(lhs>>>rhs);
					default: throw new RuntimeException("This should never happen!");
					}
				} else {
					badArgs(args, operator.getOperator());
				}
			}

			case BITWISE_AND:
			case BITWISE_OR:
			case XOR:{
				assertArgs(args, operator.getOperator(), 2);
				if (toInt(args.get(0))!=null && toInt(args.get(1))!=null) {
					int lhs = toInt(args.get(0));
					int rhs = toInt(args.get(1));
					switch (operator.getOperator()) {
					case BITWISE_AND: return makeInt(lhs&rhs);
					case BITWISE_OR: return makeInt(lhs|rhs);
					case XOR: return makeInt(lhs^rhs);
					default: throw new RuntimeException("This should never happen!");
					}
				} else if (toLong(args.get(0))!=null && toLong(args.get(1))!=null) {
					long lhs = toLong(args.get(0));
					long rhs = toLong(args.get(1));
					switch (operator.getOperator()) {
					case BITWISE_AND: return makeLong(lhs&rhs);
					case BITWISE_OR: return makeLong(lhs|rhs);
					case XOR: return makeLong(lhs^rhs);
					default: throw new RuntimeException("This should never happen!");
					}
				} else {
					badArgs(args, operator.getOperator());
				}
			}
			
			case NEG:{
				assertArgs(args, operator.getOperator(), 1);
				if (toInt(args.get(0))!=null) {
					int value = toInt(args.get(0));
					return makeInt(-value);
				} else if (toLong(args.get(0))!=null) {
					long value = toLong(args.get(0));
					return makeLong(-value);
				} else if (toFloat(args.get(0))!=null) {
					float value = toFloat(args.get(0));
					return makeFloat(-value);
				} else if (toDouble(args.get(0))!=null) {
					double value = toDouble(args.get(0));
					return makeDouble(-value);
				} else {
					badArgs(args, operator.getOperator());
				}
			}
				
			default:
				throw new IllegalArgumentException("JavaLabel type cannot be folded: " + operator);
			}
		} else {
			throw new IllegalArgumentException("Bad JavaLabel type for folding: " + op);
		}
	}
	
	private static void badArgs(java.util.Collection<?> c, JavaOperator op) {
		throw new IllegalArgumentException("Bad args for " + op.getLabel() + ": " + c);
	}
	private static void assertArgs(java.util.Collection<?> c, JavaOperator op, int size) {
		if (c.size() != size)
			throw new IllegalArgumentException("Wrong number of args for " + op.getLabel() + ": " + c);
	}
}