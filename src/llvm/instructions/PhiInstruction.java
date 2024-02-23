package llvm.instructions;

import java.util.*;

import llvm.types.Type;
import llvm.values.Value;
import util.mapped.MappedIterator;
import util.pair.Pair;

/**
 * This represents a PHI instruction.
 * It takes a type, which is the type of each operand and the return type.
 * It also takes a list of (bb,value) pairs, which denotes the value to 
 * use when coming from the particular bb.
 */
public class PhiInstruction extends Instruction {
	protected final Type type;
	protected final List<Pair<? extends Value,BasicBlock>> pairs;
	
	public PhiInstruction(Type _type, List<Pair<? extends Value,BasicBlock>> _pairs) {
		if (!_type.isFirstClass())
			throw new IllegalArgumentException("Type must be firstclass");
		if (_pairs.size() < 1)
			throw new IllegalArgumentException("Phi must have at least 1 choice");
		for (Pair<? extends Value,BasicBlock> p : _pairs) {
			if (!p.getFirst().getType().equals(_type))
				throw new IllegalArgumentException("Incoming value has wrong type");
		}
		
		this.type = _type;
		this.pairs = new ArrayList<Pair<? extends Value,BasicBlock>>(_pairs);
	}
	
	public int getNumPairs() {return this.pairs.size();}
	public Pair<? extends Value,BasicBlock> getPair(int i) {return this.pairs.get(i);}
	
	public Type getType() {return this.type;}
	public Iterator<? extends Value> getValues() {
		return new MappedIterator<Pair<? extends Value,BasicBlock>,Value>() {
			private Iterator<? extends Pair<? extends Value,BasicBlock>> wrapped = pairs.iterator();
			protected Iterator<? extends Pair<? extends Value,BasicBlock>> getWrapped() {
				return wrapped;
			}
			protected Value map(Pair<? extends Value,BasicBlock> D) {
				return D.getFirst();
			}
			public void remove() {throw new UnsupportedOperationException();}
		};
	}
	public Iterator<? extends Type> getTypes() {
		List<Type> temp = new ArrayList<Type>();
		temp.add(this.type);
		for (Pair<? extends Value,BasicBlock> pair : this.pairs) {
			temp.add(pair.getFirst().getType());
		}
		return temp.iterator();
	}
	public boolean isPhi() {return true;}
	public PhiInstruction getPhiSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("phi ").append(this.type);
		for (int i = 0; i < this.pairs.size(); i++) {
			buffer.append(", ").append(this.pairs.get(i).getFirst()).append(" ").append(this.pairs.get(i).getSecond());
		}
		return buffer.toString();
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isPhi())
			return false;
		PhiInstruction p = i.getPhiSelf();
		return this.type.equals(p.type) && this.pairs.equals(p.pairs);
	}
	public int hashCode() {
		return this.type.hashCode()*7 + this.pairs.hashCode()*19;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		List<Pair<? extends Value,BasicBlock>> newpairs = 
			new ArrayList<Pair<? extends Value,BasicBlock>>(this.pairs.size());
		boolean gotone = false;
		for (Pair<? extends Value,BasicBlock> pair : this.pairs) {
			Value newvalue = pair.getFirst().rewrite(old2new);
			if (newvalue != pair.getFirst()) {
				gotone = true;
				newpairs.add(new Pair<Value,BasicBlock>(newvalue, pair.getSecond()));
			} else {
				newpairs.add(pair);
			}
		}
		if (gotone)
			return new PhiInstruction(this.type, newpairs);
		else
			return this;
	}
}
