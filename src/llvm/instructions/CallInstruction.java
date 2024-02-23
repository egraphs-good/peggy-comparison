package llvm.instructions;

import java.util.*;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;

/**
 * This represents a CALL/TAILCALL instruction. This takes a function pointer value,
 * a list of actual parameter values, a set of paramattrs, and a calling convention
 * number and performs a function call.
 */
public class CallInstruction extends Instruction {
	protected final ParameterAttributeMap paramAttrs;
	protected final Value functionPointer;
	protected final int cc;
	protected final boolean tailCall;
	protected final List<Value> actuals;
	protected final FunctionType type;
	
	public CallInstruction(
			boolean _tail,
			int _cc, 
			Value _functionPointer, 
			ParameterAttributeMap _paramAttrs,
			List<? extends Value> _actuals) {
		if (!(_functionPointer.getType().isComposite() && 
			  _functionPointer.getType().getCompositeSelf().isPointer() &&
			  _functionPointer.getType().getCompositeSelf().getPointerSelf().getPointeeType().isFunction()))
			throw new IllegalArgumentException("Function pointer has bad type");
		FunctionType funcType = _functionPointer.getType().getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
		
		if (_actuals.size() < funcType.getNumParams())
			throw new IllegalArgumentException("Not enough actual params");
		
		for (int i = 0; i < funcType.getNumParams(); i++) {
			if (!_actuals.get(i).getType().equals(funcType.getParamType(i)))
				throw new IllegalArgumentException("Wrong type for actual parameter");
		}
		
		if (_actuals.size() > funcType.getNumParams() && !funcType.isVararg())
			throw new IllegalArgumentException("Too many actuals for non-vararg function");
		
		this.tailCall = _tail;
		this.type = funcType;
		this.cc = _cc;
		this.functionPointer = _functionPointer;
		this.paramAttrs = _paramAttrs;
		this.actuals = new ArrayList<Value>(_actuals);
	}
	
	public boolean isTailCall() {return this.tailCall;}
	public ParameterAttributeMap getParameterAttributeMap() {return this.paramAttrs;}
	public Value getFunctionPointer() {return this.functionPointer;}
	public int getCallingConvention() {return this.cc;}
	public int getNumActuals() {return this.actuals.size();}
	public Value getActual(int i) {return this.actuals.get(i);}

	public Type getType() {return this.type.getReturnType();}
	public Iterator<? extends Value> getValues() {
		List<Value> temp = new ArrayList<Value>(this.actuals);
		temp.add(this.functionPointer);
		return temp.iterator();
	}
	public Iterator<? extends Type> getTypes() {
		List<Type> temp = new ArrayList<Type>();
		temp.add(this.functionPointer.getType());
		temp.add(this.type);
		for (Value v : this.actuals) {
			temp.add(v.getType());
		}
		return temp.iterator();
	}
	public boolean isCall() {return true;}
	public CallInstruction getCallSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		if (this.tailCall)
			buffer.append("tail ");
		buffer.append("call");
		if (this.cc != 0)
			buffer.append(" cc=").append(cc);
		buffer.append(" [").append(this.functionPointer).append("] (");
		for (int i = 0; i < this.actuals.size(); i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(this.actuals.get(i));
		}
		buffer.append(" )");
		return buffer.toString();
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isCall())
			return false;
		CallInstruction i = o.getCallSelf();
		return 
			this.tailCall == i.isTailCall() &&
			this.functionPointer.equalsValue(i.functionPointer) &&
			this.paramAttrs == i.paramAttrs &&
			this.cc == i.cc &&
			this.actuals.equals(i.actuals);
	}
	public int hashCode() {
		int result =
			(this.tailCall ? 3779 : 3847) +
			this.functionPointer.hashCode()*2 +
			this.cc*3 +
			this.paramAttrs.hashCode()*11;
		for (Value v : this.actuals) {
			result += v.hashCode()*31;
		}
		return result;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newptr = this.functionPointer.rewrite(old2new);
		List<Value> newactuals = new ArrayList<Value>(this.actuals.size());
		boolean gotone = (newptr != this.functionPointer);
		for (Value a : this.actuals) {
			Value newa = a.rewrite(old2new);
			newactuals.add(newa);
			if (newa != a)
				gotone = true;
		}
		if (gotone)
			return new CallInstruction(this.tailCall, this.cc, newptr, this.paramAttrs, newactuals);
		else
			return this;
	}
}
