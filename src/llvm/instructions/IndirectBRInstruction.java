package llvm.instructions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the INDIRECTBR instruction.
 * It takes a blockaddress value and a list of bb's that it can potentially jump to.
 */
public class IndirectBRInstruction extends TerminatorInstruction {
	protected final Value address;
	protected final List<BasicBlock> dests;
	
	public IndirectBRInstruction(Value _address, List<? extends BasicBlock> _dests) {
		this.address = _address;
		this.dests = Collections.unmodifiableList(new ArrayList<BasicBlock>(_dests));
	}

	public boolean is2_8Instruction() {return true;}
	
	public Value getAddress() {return this.address;}
	public List<BasicBlock> getDestinations() {return this.dests;}

	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.address);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.address.getType(), Type.LABEL_TYPE);
	}
	public boolean isIndirectBR() {return true;}
	public IndirectBRInstruction getIndirectBRSelf() {return this;}
	
	public int getNumTargets() {
		return this.dests.size();
	}
	public BasicBlock getTarget(int i) {
		return this.dests.get(i);
	}
	
	public String toString() {
		return "indirectbr " + this.address + " " + this.dests;
	}
	public boolean equalsTerminator(TerminatorInstruction t) {
		if (!t.isIndirectBR())
			return false;
		IndirectBRInstruction b = t.getIndirectBRSelf();
		return this.address.equalsValue(b.getAddress()) &&
			this.getDestinations().equals(b.getDestinations());
	}
	public int hashCode() {
		return 11*this.address.hashCode() + 31*this.dests.hashCode();
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newaddr = this.address.rewrite(old2new);
		if (newaddr == this.address)
			return this;
		else
			return new IndirectBRInstruction(newaddr, this.dests);
	}
}
