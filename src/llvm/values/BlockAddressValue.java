package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.instructions.BasicBlock;
import llvm.types.PointerType;
import llvm.types.Type;

/**
 * This represents a block address value, which names a basic block in
 * a particular function.
 */
public class BlockAddressValue extends Value {
	protected final Type type = new PointerType(Type.getIntegerType(8));
	protected final FunctionValue function;
	protected BasicBlock block;
	protected final int blockNumber;
	
	public BlockAddressValue(FunctionValue _function, int _blockNumber) {
		this.function = _function;
		this.blockNumber = _blockNumber;
	}

	public boolean is2_8Value() {return true;}
	
	public boolean isBlockAddress() {return true;}
	public BlockAddressValue getBlockAddressSelf() {return this;}
	
	public void assertResolved() {
		if (this.block == null)
			throw new IllegalStateException("Block address not resolved");
	}
	
	public void resolve(BasicBlock _block) {
		if (this.block != null)
			throw new IllegalStateException("Block has already been resolved");
		this.block = _block;
	}

	public FunctionValue getFunction() {return this.function;}
	public int getBlockNumber() {return this.blockNumber;}
	public BasicBlock getBlock() {
		this.assertResolved();
		return this.block;
	}
	
	public Type getType() {return this.type;}
	public void ensureConstant() {}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	
	public String toString() {
		return "blockaddress " + this.function + ", " + this.block;
	}
	public boolean equalsValue(Value v) {
		if (!v.isBlockAddress())
			return false;
		BlockAddressValue l = v.getBlockAddressSelf();
		boolean result = this.function.equalsValue(l.getFunction()) &&
			this.blockNumber == l.blockNumber;
		if (this.block != null && l.block != null) {
			return result && this.block.equals(l.block);
		} else if (this.block != null || l.block != null) {
			return false;
		} else {
			return result;
		}
	}
	public int hashCode() {
		if (this.block == null)
			return this.blockNumber*7 + 13*this.function.hashCode();
		else
			return this.blockNumber*7 + 13*this.function.hashCode() + this.block.hashCode()*17;
	}
	
	protected BlockAddressValue rewriteChildren(Map<Value,Value> old2new) {
		// TODO? replace function??
		return this;
	}
}
