package peggy.represent.llvm;

import java.util.List;

import llvm.values.IntegerValue;
import llvm.values.Value;
import util.pair.Pair;

/**
 * This is a CFGInstruction that represents the LLVM switch instruction.
 */
public class SwitchCFGInstruction extends CFGInstruction {
	protected final Value value;
	protected final List<Pair<IntegerValue,LLVMBlock>> pairs;
	protected LLVMBlock defaultBlock;
	
	public SwitchCFGInstruction(Value _value, LLVMBlock _default, List<Pair<IntegerValue,LLVMBlock>> _pairs) {
		this.value = _value;
		this.defaultBlock = _default;
		this.pairs = _pairs;
	}
	
	public Value getValue() {return this.value;}
	public int getNumPairs() {return this.pairs.size();}
	public Pair<IntegerValue,LLVMBlock> getPair(int i) {return this.pairs.get(i);}
	public void setPair(int index, IntegerValue v, LLVMBlock block) {
		this.pairs.set(index, new Pair<IntegerValue,LLVMBlock>(v, block));
	}
	public LLVMBlock getDefaultBlock() {return this.defaultBlock;}
	public void setDefaultBlock(LLVMBlock block) {this.defaultBlock = block;}
	
	public boolean isSwitch() {return true;}
	public SwitchCFGInstruction getSwitchSelf() {return this;}
	public String toString() {
		StringBuffer buffer = new StringBuffer (100);
		buffer.append("switch(").append(this.value).append(") {");
		for (Pair<IntegerValue,LLVMBlock> pair : this.pairs) {
			buffer.append("case ").append(pair.getFirst()).append(": ");
			buffer.append(pair.getSecond()).append("; ");
		}
		buffer.append("default: ").append(this.defaultBlock).append("; ");
		buffer.append("}");
		return buffer.toString();
	}
}
