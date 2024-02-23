package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.instructions.BasicBlock;
import llvm.types.Type;

/**
 * Represents a basic block label value.
 */
public class LabelValue extends Value {
	protected final BasicBlock block;
	
	public LabelValue(BasicBlock _block) {
		this.block = _block;
	}
	
	public BasicBlock getBlock() {return this.block;}

	public boolean isFunctionLocal() {return true;}
	
	public void ensureConstant() {}
	public Type getType() {return Type.LABEL_TYPE;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isLabel() {return true;}
	public LabelValue getLabelSelf() {return this;}
	
	public String toString() {
		return "label " + this.block;
	}
	public boolean equalsValue(Value v) {
		if (!v.isLabel())
			return false;
		LabelValue l = v.getLabelSelf();
		return this.block.equals(l.block);
	}
	public int hashCode() {
		return this.block.hashCode()*7;
	}
	
	protected LabelValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
