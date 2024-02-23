package llvm.instructions;

import java.util.*;

import llvm.types.Type;
import llvm.values.IntegerValue;
import llvm.values.Value;

/**
 * This represents a SWITCH instruction.
 * It takes a list of (value,bb) pairs, a condition value, and a default
 * basic block.
 */
public class SwitchInstruction extends TerminatorInstruction {
	protected final Map<IntegerValue,BasicBlock> caseMap;
	protected final Value value;
	protected final BasicBlock defaultBlock;
	protected final List<Map.Entry<IntegerValue,BasicBlock>> linearMap;
	
	public SwitchInstruction(Value _value, BasicBlock _defaultBlock, Map<IntegerValue,BasicBlock> _map) {
		if (!_value.getType().isInteger())
			throw new IllegalArgumentException("Value must have integer type");
		for (IntegerValue v : _map.keySet()) {
			if (!v.getType().equals(_value.getType()))
				throw new IllegalArgumentException("Case labels must have same type as input value");
			v.ensureConstant();
		}
		
		this.value = _value;
		this.defaultBlock = _defaultBlock;
		this.caseMap = new LinkedHashMap<IntegerValue,BasicBlock>(_map);
		this.linearMap = new ArrayList<Map.Entry<IntegerValue,BasicBlock>>(_map.entrySet());
	}
	
	public int getNumCaseLabels() {return this.linearMap.size();}
	public IntegerValue getCaseLabel(int i) {return this.linearMap.get(i).getKey();}
	/**
	 * Warning: this method does not alias getTarget(int).
	 * This method only returns the blocks for the case labels, IT DOES NOT RETURN
	 * THE DEFAULT BLOCK!
	 */
	public BasicBlock getCaseTarget(int i) {return this.linearMap.get(i).getValue();}
	public Value getInputValue() {return this.value;}
	public BasicBlock getDefaultTarget() {return this.defaultBlock;}
	
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		List<Value> temp = new ArrayList<Value>(this.caseMap.keySet());
		temp.add(this.value);
		return temp.iterator();
	}
	public Iterator<? extends Type> getTypes() {
		List<Type> temp = new ArrayList<Type>();
		temp.add(this.value.getType());
		for (IntegerValue iv : this.caseMap.keySet()) {
			temp.add(iv.getType());
		}
		return temp.iterator();
	}
	public boolean isSwitch() {return true;}
	public SwitchInstruction getSwitchSelf() {return this;}
	
	public int getNumTargets() {return 1+this.caseMap.size();}
	public BasicBlock getTarget(int i) {
		if (i == 0)
			return this.defaultBlock;
		else
			return this.linearMap.get(i-1).getValue();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("switch ").append(this.value).append(", ").append(this.defaultBlock);
		for (IntegerValue iv : this.caseMap.keySet()) {
			buffer.append(", ").append(iv).append(", ").append(this.caseMap.get(iv));
		}
		return buffer.toString();		
	}
	public boolean equalsTerminator(TerminatorInstruction o) {
		if (!o.isSwitch())
			return false;
		SwitchInstruction s = o.getSwitchSelf();
		if (!(this.value.equalsValue(s.value) && this.defaultBlock.equals(s.defaultBlock)))
			return false;
		return this.caseMap.equals(s.caseMap);
	}
	public int hashCode() {
		int result = this.value.hashCode()*5 + this.defaultBlock.hashCode()*67;
		for (IntegerValue iv : this.caseMap.keySet()) {
			result += iv.hashCode()*this.caseMap.get(iv).hashCode()*3;
		}
		return result;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvalue = this.value.rewrite(old2new);
		Map<IntegerValue,BasicBlock> newmap = new HashMap<IntegerValue,BasicBlock>();
		boolean gotone = (newvalue != this.value);
		for (IntegerValue v : this.caseMap.keySet()) {
			Value newv = v.rewrite(old2new);
			if (!newv.isInteger())
				throw new IllegalArgumentException("Case label must be an IntegerValue");
			if (newv == v)
				gotone = true;
			newmap.put(newv.getIntegerSelf(), this.caseMap.get(v));
		}
		if (gotone)
			return new SwitchInstruction(newvalue, this.defaultBlock, newmap);
		else
			return this;
	}
}
