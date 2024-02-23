package llvm.instructions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import llvm.types.CompositeType;
import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents an EXTRACTVALUE instruction, which takes a struct value
 * and a list of literal indexes and returns the field value inside the struct.
 * The indexing is defined in the same way as GEP, but the initial struct value
 * is not a pointer, so the first index is different.
 */
public class ExtractValueInstruction extends Instruction {
	protected final Value aggregate;
	protected final List<Integer> indexes;
	protected final Type eltType;
	
	public ExtractValueInstruction(Value _aggregate, List<Integer> _indexes) {
		this.aggregate = _aggregate;
		this.indexes = Collections.unmodifiableList(new ArrayList<Integer>(_indexes));
		Type currentType = this.aggregate.getType();
		for (int index : indexes) {
			if (currentType.isComposite()) {
				CompositeType ctype = currentType.getCompositeSelf();
				if (!ctype.isElementIndexValid(index))
					throw new IllegalArgumentException("Index invalid for type: " + ctype);
				currentType = ctype.getElementType(index);
			} else {
				throw new IllegalArgumentException("Type is not an aggregate type");
			}
		}
		this.eltType = currentType;
	}
	
	public boolean is2_8Instruction() {return true;}

	public Value getAggregate() {return this.aggregate;}
	public List<Integer> getIndexes() {return this.indexes;}
	
	public Type getType() {return this.eltType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.aggregate);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.aggregate.getType());
	}
	public boolean isExtractValue() {return true;}
	public ExtractValueInstruction getExtractValueSelf() {return this;}
	
	public String toString() {
		return "extractvalue " + this.aggregate + " " + this.indexes;
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isExtractValue())
			return false;
		ExtractValueInstruction e = i.getExtractValueSelf();
		return this.getAggregate().equalsValue(e.getAggregate()) &&
			this.getIndexes().equals(e.getIndexes());
	}
	public int hashCode() {
		return 17*this.aggregate.hashCode() + 19*this.indexes.hashCode();
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newagg = this.aggregate.rewrite(old2new);
		if (newagg == this.aggregate)
			return this;
		else
			return new ExtractValueInstruction(newagg, this.indexes);
	}
}
