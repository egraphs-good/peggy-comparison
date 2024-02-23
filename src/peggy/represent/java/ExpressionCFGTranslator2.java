package peggy.represent.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.PrimType;
import soot.RefType;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractJimpleFloatBinopExpr;
import soot.jimple.internal.AbstractJimpleIntBinopExpr;
import soot.jimple.internal.AbstractJimpleIntLongBinopExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JAndExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JCmpExpr;
import soot.jimple.internal.JCmpgExpr;
import soot.jimple.internal.JCmplExpr;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNegExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JOrExpr;
import soot.jimple.internal.JRemExpr;
import soot.jimple.internal.JShlExpr;
import soot.jimple.internal.JShrExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JUshrExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JXorExpr;
import soot.jimple.internal.JimpleLocal;
import util.Function;
import util.VariaticFunction;
import eqsat.CFGTranslator;
import static peggy.represent.java.JavaOperator.*;

/**
 * This class is a CFGTranslator for JAVA ExpressionCFG2's.
 */
public class ExpressionCFGTranslator2<E> implements CFGTranslator<ExpressionCFGBlock2,JavaVariable,E> {
	private final Function<JavaParameter,E> parameterConverter;
	private final VariaticFunction<JavaLabel,E,E> converter;
	private final ExpressionCFG2 cfg;
	////////////////////////////////////
	
	protected ExpressionCFGTranslator2(
			ExpressionCFG2 _cfg,
			final Function<JavaParameter,E> _paramConverter,
			final VariaticFunction<JavaLabel,E,E> _converter,
			Collection<? super E> _known) {
		this.cfg = _cfg;
		this.parameterConverter = _paramConverter;
		this.converter = _converter;
	}
	
	public Function<JavaVariable,E> getOutputs(
			final ExpressionCFGBlock2 block, 
			final Function<JavaVariable,E> inputs) {
		return new Function<JavaVariable,E>() {
			private List<Map<JavaVariable,E>> variableCache;
			private List<Map<Value,E>> valueCache;
			private int numStatements = block.getNumInstructions();
			
			{// initializer
				variableCache = new ArrayList<Map<JavaVariable,E>>(numStatements);
				valueCache = new ArrayList<Map<Value,E>>(numStatements);
				for (int i=0;i<numStatements;i++){
					variableCache.add(new HashMap<JavaVariable,E>());
					valueCache.add(new HashMap<Value,E>());
				}
			}
			
			public E get(JavaVariable variable) {
				if (variable==null)
					return getBranchCondition();
				else {
					E result = getOutputAt(block.getNumInstructions()-1, variable);
					if (variable.isReturn() && block.isEnd()) {
						// add INJR
						return converter.get(
								SimpleJavaLabel.create(JavaOperator.INJR),
								result);
					} else {
						return result;
					}
				}
			}

			private E getBranchCondition() {
				if (block.getNumInstructions() == 0)
					throw new UnsupportedOperationException("Block does not end in a branch");
				CFGInstruction tail = block.getInstruction(block.getNumInstructions()-1);
				if (!(tail instanceof IfCFGInstruction))
					throw new UnsupportedOperationException("Block does not end in a branch");

				IfCFGInstruction ifcfg = tail.getIfSelf();
				E result = valueOf(ifcfg.getCondition(), block.getNumInstructions()-1);
				return result;
			}
			
			private E valueOf(Value value, int unitindex) {
				Map<Value,E> cached = valueCache.get(unitindex);
				if (cached.containsKey(value)) {
					return cached.get(value);
				}

				if (value instanceof SetarrayExpr) {
					SetarrayExpr set = (SetarrayExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					E array = valueOf(set.getArray(), unitindex);
					E index = valueOf(set.getIndex(), unitindex);
					E rhs = valueOf(set.getValue(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(SETARRAY), sigma, array, index, rhs);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof ProjectSigmaExpr) {
					ProjectSigmaExpr project = (ProjectSigmaExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					E rhoI = converter.get(SimpleJavaLabel.create(RHO_SIGMA), sigma);
					cached.put(value, rhoI);
					return rhoI;
				}
				else if (value instanceof SetfieldExpr) {
					SetfieldExpr set = (SetfieldExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					E base = valueOf(set.getBase(), unitindex);
					FieldJavaLabel field = new FieldJavaLabel(
							set.getFieldRef().declaringClass().getName(),
							set.getFieldRef().name(),
							set.getFieldRef().type());
					E fieldnode = converter.get(field);
					E rhs = valueOf(set.getValue(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(SETFIELD), sigma, base, fieldnode, rhs);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof SetstaticfieldExpr) {
					SetstaticfieldExpr set = (SetstaticfieldExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					FieldJavaLabel field = new FieldJavaLabel(
							set.getFieldRef().declaringClass().getName(),
							set.getFieldRef().name(),
							set.getFieldRef().type());
					E fieldnode = converter.get(field);
					E rhs = valueOf(set.getValue(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(SETSTATICFIELD), sigma, fieldnode, rhs);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof GetValueExpr) {
					GetValueExpr gee = (GetValueExpr)value;
					E op = valueOf(gee.getOp(), unitindex);
					E result = converter.get(SimpleJavaLabel.create(RHO_VALUE), op);
					cached.put(value, result);
					return result;
				}

				else if (value instanceof AbstractJimpleFloatBinopExpr) {
					JavaOperator operator = null;
					if (value instanceof JAddExpr) operator = PLUS;
					else if (value instanceof JDivExpr) operator = DIVIDE; 
					else if (value instanceof JMulExpr) operator = TIMES;
					else if (value instanceof JRemExpr) operator = MOD; 
					else if (value instanceof JSubExpr) operator = MINUS;
					else throw new RuntimeException("Mike forgot to support AbstractJimpleFloatBinopExpr "+value.getClass());

					AbstractJimpleFloatBinopExpr binop = (AbstractJimpleFloatBinopExpr)value;
					E op1E = valueOf(binop.getOp1(), unitindex);
					E op2E = valueOf(binop.getOp2(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(operator), op1E, op2E);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof AbstractJimpleIntBinopExpr) {
					AbstractJimpleIntBinopExpr binop = (AbstractJimpleIntBinopExpr)value;
					JavaOperator operator = null;
					if (value instanceof JCmpExpr) operator = CMP;
					else if (value instanceof JCmpgExpr) operator = CMPG;
					else if (value instanceof JCmplExpr) operator = CMPL;
					else if (value instanceof JEqExpr) operator = EQUAL;
					else if (value instanceof JGeExpr) operator = GREATER_THAN_EQUAL;
					else if (value instanceof JGtExpr) operator = GREATER_THAN;
					else if (value instanceof JLeExpr) operator = LESS_THAN_EQUAL;
					else if (value instanceof JLtExpr) operator = LESS_THAN;
					else if (value instanceof JNeExpr) operator = NOT_EQUAL;
					else throw new RuntimeException("Mike forgot to support AbstractJimpleIntBinopExpr "+value.getClass());

					E op1E = valueOf(binop.getOp1(), unitindex);
					E op2E = valueOf(binop.getOp2(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(operator), op1E, op2E);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof AbstractJimpleIntLongBinopExpr) {
					AbstractJimpleIntLongBinopExpr binop = (AbstractJimpleIntLongBinopExpr)value;
					JavaOperator operator = null;
					if (value instanceof JAndExpr) operator = BITWISE_AND;
					else if (value instanceof JOrExpr) operator = BITWISE_OR;
					else if (value instanceof JShlExpr) operator = SHIFT_LEFT;
					else if (value instanceof JShrExpr) operator = SHIFT_RIGHT;
					else if (value instanceof JUshrExpr) operator = UNSIGNED_SHIFT_RIGHT;
					else if (value instanceof JXorExpr) operator = XOR;
					else throw new RuntimeException("Mike forgot to support AbstractJimpleIntLongBinopExpr "+value.getClass());

					E op1E = valueOf(binop.getOp1(), unitindex);
					E op2E = valueOf(binop.getOp2(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(operator), op1E, op2E);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JCastExpr) {
					// either a cast or a primitivecast
					JCastExpr cast = (JCastExpr)value;
					Type casttype = cast.getCastType();
					if (casttype instanceof PrimType) {
						E opE = valueOf(cast.getOp(), unitindex);
						E typenode = converter.get(new TypeJavaLabel(casttype));

						E result = converter.get(SimpleJavaLabel.create(PRIMITIVECAST), typenode, opE);
						cached.put(value, result);
						return result;
					} else {
						E opE = valueOf(cast.getOp(), unitindex);
						E typenode = converter.get(new TypeJavaLabel(casttype));
						E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);

						E result = converter.get(SimpleJavaLabel.create(CAST), sigma, typenode, opE);
						cached.put(value, result);
						return result;
					}
				}
				else if (value instanceof JInstanceFieldRef) {
					JInstanceFieldRef field = (JInstanceFieldRef)value;
					E target = valueOf(field.getBase(), unitindex);
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					FieldJavaLabel fieldlabel = new FieldJavaLabel(
							field.getFieldRef().declaringClass().getName(),
							field.getFieldRef().name(),
							field.getType());
					E fieldnode = converter.get(fieldlabel);
					
					E result = converter.get(SimpleJavaLabel.create(GETFIELD), sigma, target, fieldnode);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof InvokeExpr) {
					// JInterfaceInvokeExpr, JVirtualInvokeExpr, JStaticInvokeExpr, JSpecialInvokeExpr
					E tuple = valueOfInvoke((InvokeExpr)value, unitindex);
					cached.put(value, tuple);
					return tuple;
				}
				else if (value instanceof JInstanceOfExpr) {
					JInstanceOfExpr inst = (JInstanceOfExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					E opE = valueOf(inst.getOp(), unitindex);
					E typenode = converter.get(new TypeJavaLabel(inst.getCheckType()));
					
					E result = converter.get(SimpleJavaLabel.create(INSTANCEOF), sigma, opE, typenode);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JLengthExpr) {
					JLengthExpr length = (JLengthExpr)value;
					E opE = valueOf(length.getOp(), unitindex);
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					
					E result = converter.get(SimpleJavaLabel.create(ARRAYLENGTH), sigma, opE);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JNegExpr) {
					JNegExpr neg = (JNegExpr)value;
					E opE = valueOf(neg.getOp(), unitindex);

					E result = converter.get(SimpleJavaLabel.create(NEG), opE);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JNewArrayExpr) {
					JNewArrayExpr array = (JNewArrayExpr)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					E typenode = converter.get(new TypeJavaLabel(array.getBaseType()));
					E size = valueOf(array.getSize(), unitindex);
					
					E result = converter.get(SimpleJavaLabel.create(NEWARRAY), sigma, typenode, size);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JNewExpr) {
					JNewExpr newexpr = (JNewExpr)value;
					E typenode = converter.get(new TypeJavaLabel(newexpr.getBaseType()));
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					
					E result = converter.get(SimpleJavaLabel.create(NEWINSTANCE), sigma, typenode);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JNewMultiArrayExpr) {
					E result = valueOfNewMultiArray((JNewMultiArrayExpr)value, unitindex);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof IntConstant) {
					IntConstant ic = (IntConstant)value;
					E result = converter.get(new ConstantValueJavaLabel(ic));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof LongConstant) {
					LongConstant lc = (LongConstant)value;
					E result = converter.get(new ConstantValueJavaLabel(lc));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof ClassConstant) {
					ClassConstant cc = (ClassConstant)value;
					E typenode = converter.get(new TypeJavaLabel(SootUtils.typeNameToType(cc.getValue())));
					E result = converter.get(SimpleJavaLabel.create(CLASS), typenode);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof FloatConstant) {
					FloatConstant fc = (FloatConstant)value;
					E result = converter.get(new ConstantValueJavaLabel(fc));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JArrayRef) {
					JArrayRef ref = (JArrayRef)value;
					E baseE = valueOf(ref.getBase(), unitindex);
					E indexE = valueOf(ref.getIndex(), unitindex);
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);

					E result = converter.get(SimpleJavaLabel.create(GETARRAY), sigma, baseE, indexE);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof NullConstant) {
					E result = converter.get(new ConstantValueJavaLabel((NullConstant)value));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof DoubleConstant) {
					DoubleConstant dc = (DoubleConstant)value;
					E result = converter.get(new ConstantValueJavaLabel(dc));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof ParameterRef) {
					ParameterRef ref = (ParameterRef)value;
					E result = parameterConverter.get(new ArgumentJavaParameter(
							new ArgumentJavaVariable(
									cfg.getMethodLabel(), 
									ref.getIndex())));
					cached.put(value, result);
					return result;					
				}
				else if (value instanceof StringConstant) {
					StringConstant sc = (StringConstant)value;
					E result = converter.get(new ConstantValueJavaLabel(sc));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof ThisRef) {
					ThisRef ref = (ThisRef)value;
					E result = parameterConverter.get(new ThisJavaParameter(
							new ThisJavaVariable((RefType)ref.getType())));
					cached.put(value, result);
					return result;
				}
				else if (value instanceof JimpleLocal) {
					JavaVariable localvar = cfg.findOrMakeVar((JimpleLocal)value);
					E result = getOutputAt(unitindex-1, localvar);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof StaticFieldRef) {
					StaticFieldRef field = (StaticFieldRef)value;
					E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
					FieldJavaLabel label = new FieldJavaLabel(
							field.getFieldRef().declaringClass().getName(),
							field.getFieldRef().name(),
							field.getType());
					E fieldnode = converter.get(label);
					
					E result = converter.get(SimpleJavaLabel.create(GETSTATICFIELD), sigma, fieldnode);
					cached.put(value, result);
					return result;
				}
				else if (value instanceof VoidExpr) {
					E result = converter.get(SimpleJavaLabel.create(VOID));
					cached.put(value, result);
					return result;
				}
				
				throw new RuntimeException("Mike forgot to support value type "+value.getClass());
			}
			
			
			@SuppressWarnings("unchecked")
			private E valueOfInvoke(InvokeExpr value, int unitindex){
				Map<Value,E> cached = valueCache.get(unitindex);
				if (cached.containsKey(value)) {
					return cached.get(value);
				}
				
				//	all args must be 'immediates': locals or constants  (check class ImmediateBox)
				List actuals = value.getArgs();
				Object[] paramlist = new Object[actuals.size()];
				int index=0;
				for (Iterator<Value> iter=actuals.iterator();iter.hasNext();){
					Value arg = iter.next();
					paramlist[index++] = valueOf(arg, unitindex);
				}
				E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
				E params = converter.get(SimpleJavaLabel.create(PARAMS), paramlist);
				SootMethodRef ref = value.getMethodRef();
				MethodJavaLabel label = new MethodJavaLabel(
						ref.declaringClass().getName(),
						ref.name(),
						ref.returnType(),
						(List<Type>)ref.parameterTypes());
				E methodnode = converter.get(label);

				if (value instanceof JStaticInvokeExpr) {
					// no base!
					E result = converter.get(SimpleJavaLabel.create(INVOKESTATIC), sigma, methodnode, params);
					cached.put(value, result);
					return result;
				} else if (value instanceof JVirtualInvokeExpr) {
					E baseE = valueOf(((JVirtualInvokeExpr)value).getBase(), unitindex);
					E result = converter.get(SimpleJavaLabel.create(INVOKEVIRTUAL), sigma, baseE, methodnode, params);
					cached.put(value, result);
					return result;
				} else if (value instanceof JInterfaceInvokeExpr) {
					E baseE = valueOf(((JInterfaceInvokeExpr)value).getBase(), unitindex);
					E result = converter.get(SimpleJavaLabel.create(INVOKEINTERFACE), sigma, baseE, methodnode, params);
					cached.put(value, result);
					return result;
				} else if (value instanceof JSpecialInvokeExpr) {
					E baseE = valueOf(((JSpecialInvokeExpr)value).getBase(), unitindex);
					E result = converter.get(SimpleJavaLabel.create(INVOKESPECIAL), sigma, baseE, methodnode, params);
					cached.put(value, result);
					return result;
				} else
					throw new RuntimeException("Mike forgot an InvokeExpr type "+value.getClass());
			}

			// this function logically returns a tuple
			@SuppressWarnings("unchecked")
			private E valueOfNewMultiArray(JNewMultiArrayExpr array, int unitindex) {
				Map<Value,E> cached = valueCache.get(unitindex);
				if (cached.containsKey(array))
					return cached.get(array);

				List sizes = array.getSizes();
				Object[] dimlist = new Object[sizes.size()];
				int index=0;
				for (Iterator iter=sizes.iterator();iter.hasNext();){
					Value dim = (Value)iter.next();
					dimlist[index++] = valueOf(dim, unitindex);
				}

				E dims = converter.get(SimpleJavaLabel.create(DIMS), dimlist);
				E typenode = converter.get(new TypeJavaLabel(array.getType()));
				E sigma = getOutputAt(unitindex-1, JavaVariable.SIGMA);
				
				E result = converter.get(SimpleJavaLabel.create(NEWMULTIARRAY), sigma, typenode, dims);
				cached.put(array, result);
				return result;
			}
			
			private E getOutputAt(int index, JavaVariable variable){
				if (index < 0)
					return inputs.get(variable);
				CFGInstruction current = block.getInstruction(index);
				return getOutputAt(current, index, variable);
			}

			private E getOutputAt(CFGInstruction current, int index, JavaVariable variable){
				if (index < 0)
					return inputs.get(variable);
				Map<JavaVariable,E> cached = variableCache.get(index);
				if (cached.containsKey(variable))
					return cached.get(variable);

				if (current.isEval()) {
					if (block.hasAssignment(current) && block.getAssignment(current).equals(variable)) {
						E value = valueOf(current.getEvalSelf().getValue(), index);
						cached.put(variable, value);
						return value;
					} else if (variable.isSigma()) {
						Value value = current.getEvalSelf().getValue();
						if ((value instanceof InvokeExpr) ||
							(value instanceof JNewArrayExpr) ||
							(value instanceof JNewMultiArrayExpr) ||
							(value instanceof JNewExpr) ||
							((value instanceof JCastExpr) && !(((JCastExpr)value).getCastType() instanceof PrimType)) ||
							(value instanceof JInstanceFieldRef) ||
							(value instanceof JInstanceOfExpr) ||
							(value instanceof JLengthExpr) ||
							(value instanceof JArrayRef) ||
							(value instanceof StaticFieldRef) ||
							(value instanceof SetfieldExpr) ||
							(value instanceof SetarrayExpr) ||
							(value instanceof SetstaticfieldExpr)) {
							E base = valueOf(value, index);
							E result = converter.get(SimpleJavaLabel.create(RHO_SIGMA), base);
							cached.put(variable, result);
							return result;
						}
						// fallthrough
					}
//					else if (variable.isCounter()) {
//						Value value = current.getEvalSelf().getValue();
//						if ((value instanceof InvokeExpr) ||
//							(value instanceof JNewArrayExpr) ||
//							(value instanceof JNewMultiArrayExpr) ||
//							(value instanceof JNewExpr)) {
//							E base = valueOf(value, index);
//							E result = converter.get(JavaLabel.makeProjection(2), base);
//							cached.put(variable, result);
//							return result;
//						}
//					}
					// fallthrough
				} 
				else if (current.isIf()) {
					// skip, fallthrough
				} 
				else if (current.isMonitor()) {
					if (variable.isSigma()) {
						E sigma = getOutputAt(index-1, JavaVariable.SIGMA);
						E target = valueOf(current.getMonitorSelf().getTarget(), index-1);
						E result = converter.get(
								(current.getMonitorSelf().isEnter() ?
								 SimpleJavaLabel.create(ENTERMONITOR) :
								 SimpleJavaLabel.create(EXITMONITOR)),
								 sigma, target);
						cached.put(variable, result);
						return result;
					}
					// fallthrough
				}
				else 
					throw new IllegalArgumentException("Mike forgot " + current);

				// this needs to be the fallthrough!
				// this instruction didn't modify 'variable', get the previous value
				E result = getOutputAt(index-1, variable);
				cached.put(variable, result);
				return result;
			}
		};
	}
}
