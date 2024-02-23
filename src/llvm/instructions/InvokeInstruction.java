package llvm.instructions;

import java.util.*;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;

/**
 * This represents an INVOKE instruction.
 * It takes a function pointer value, a bb it branch to upon returning, a bb to
 * branch to upon unwinding, a list of actual values, a calling convention index,
 * and a set of paramattrs.
 */
public class InvokeInstruction extends TerminatorInstruction {
	protected final ParameterAttributeMap paramAttrs;
	protected final Value functionPointer;
	protected final BasicBlock returnBlock, unwindBlock;
	protected final int cc;
	protected final List<Value> actuals;
	protected final FunctionType type;
	
	public InvokeInstruction(
			int _cc, 
			Value _functionPointer, 
			ParameterAttributeMap _paramAttrs,
			BasicBlock _returnBlock,
			BasicBlock _unwindBlock,
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
		
		this.type = funcType;
		this.cc = _cc;
		this.functionPointer = _functionPointer;
		this.paramAttrs = _paramAttrs;
		this.returnBlock = _returnBlock;
		this.unwindBlock = _unwindBlock;
		this.actuals = new ArrayList<Value>(_actuals);
	}
	
	public ParameterAttributeMap getParameterAttributeMap() {return this.paramAttrs;}
	public Value getFunctionPointer() {return this.functionPointer;}
	public BasicBlock getReturnBlock() {return this.returnBlock;}
	public BasicBlock getUnwindBlock() {return this.unwindBlock;}
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
		temp.add(this.type);
		temp.add(this.functionPointer.getType());
		for (Value v : this.actuals) {
			temp.add(v.getType());
		}
		return temp.iterator();
	}
	public boolean isInvoke() {return true;}
	public InvokeInstruction getInvokeSelf() {return this;}
	
	public int getNumTargets() {return 2;}
	public BasicBlock getTarget(int i) {
		if (i == 0) return this.returnBlock;
		else if (i == 1) return this.unwindBlock;
		else throw new IndexOutOfBoundsException("" + i);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("invoke");
		if (this.cc != 0)
			buffer.append(" cc=").append(cc);
		buffer.append(" [").append(this.functionPointer).append("] (");
		for (int i = 0; i < this.actuals.size(); i++) {
			if (i > 0) buffer.append(", ");
			buffer.append(this.actuals.get(i));
		}
		buffer.append(")");
		return buffer.toString();
	}
	public boolean equalsTerminator(TerminatorInstruction o) {
		if (!o.isInvoke())
			return false;
		InvokeInstruction i = o.getInvokeSelf();
		return 
			this.functionPointer.equalsValue(i.functionPointer) &&
			this.returnBlock.equals(i.returnBlock) &&
			this.unwindBlock.equals(i.unwindBlock) &&
			this.paramAttrs == i.paramAttrs &&
			this.cc == i.cc &&
			this.actuals.equals(i.actuals);
	}
	public int hashCode() {
		int result = 
			this.functionPointer.hashCode()*2 +
			this.returnBlock.hashCode()*7 +
			this.unwindBlock.hashCode()*17 +
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
			return new InvokeInstruction(this.cc, newptr, this.paramAttrs, this.returnBlock, this.unwindBlock, newactuals);
		else
			return this;
	}
}
