package llvm.instructions;

import java.util.*;

import llvm.types.StructureType;
import llvm.types.Type;
import llvm.values.Value;

/**
 * This represents a RET instruction. A ret can have one of three types:
 * 1) void ret, no values
 * 2) simple ret, 1 value
 * 3) aggregate ret, multiple values
 */
public class RetInstruction extends TerminatorInstruction {
	protected final List<Value> returnValues;
	protected final Type returnType;

	public RetInstruction() {
		this.returnValues = new ArrayList<Value>();
		this.returnType = Type.VOID_TYPE;
	}
	public RetInstruction(List<? extends Value> _returnValues) {
		this.returnValues = new ArrayList<Value>(_returnValues.size());
		List<Type> fieldTypes = new ArrayList<Type>(_returnValues.size());
		for (Value v : _returnValues) {
			if (!v.getType().isFirstClass())
				throw new IllegalArgumentException("Invalid type for ret: " + v.getType());
			this.returnValues.add(v);
			fieldTypes.add(v.getType());
		}
		
		if (fieldTypes.size() == 0) {
			// void
			this.returnType = Type.VOID_TYPE;
		} else if (fieldTypes.size() == 1) {
			this.returnType = fieldTypes.get(0);
		} else {
			this.returnType = new StructureType(false, fieldTypes);
		}
	}

	public boolean isVoid() {return this.returnValues.size() == 0;}
	public int getNumReturnValues() {return this.returnValues.size();}
	public Value getReturnValue(int i) {return this.returnValues.get(i);}
	public Type getReturnValueType() {return this.returnType;}
	
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return Collections.unmodifiableList(this.returnValues).iterator();
	}
	public Iterator<? extends Type> getTypes() {
		List<Type> temp = new ArrayList<Type>();
		temp.add(this.returnType);
		for (Value v : this.returnValues) {
			temp.add(v.getType());
		}
		return temp.iterator();
	}
	public boolean isRet() {return true;}
	public RetInstruction getRetSelf() {return this;}
	
	public int getNumTargets() {return 0;}
	public BasicBlock getTarget(int i) {throw new IndexOutOfBoundsException(""+i);}
	
	public String toString() {
		if (this.returnValues.size() == 0) {
			return "ret void";
		} else {
			StringBuffer buffer = new StringBuffer(100);
			buffer.append("ret ");
			for (int i = 0; i < this.returnValues.size(); i++) {
				if (i > 0) buffer.append(", ");
				buffer.append(this.returnValues.get(i));
			}
			return buffer.toString();
		}
	}
	public boolean equalsTerminator(TerminatorInstruction o) {
		if (!o.isRet())
			return false;
		RetInstruction r = o.getRetSelf();
		if (this.returnValues.size() != r.returnValues.size())
			return false;
		for (int i = 0; i < this.returnValues.size(); i++) {
			if (!this.returnValues.get(i).equalsValue(r.returnValues.get(i)))
				return false;
		}
		return true;
	}
	public int hashCode() {
		int result = 49308730;
		for (Value v : this.returnValues)
			result += v.hashCode()*37;
		return result;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		List<Value> newvalues = new ArrayList<Value>(this.returnValues.size());
		boolean gotone = false;
		for (Value r : this.returnValues) {
			Value newr = r.rewrite(old2new);
			if (newr != r)
				gotone = true;
			newvalues.add(newr);
		}
		if (gotone)
			return new RetInstruction(newvalues);
		else
			return this;
	}
}
