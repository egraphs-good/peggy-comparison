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
 * This represents the INSERTVALUE instruction.
 * It takes a struct value, an element value, and a list of literal indexes
 * into the struct. 
 */
public class InsertValueInstruction extends Instruction {
	protected final Value aggregate;
	protected final Value elt;
	protected final List<Integer> indexes;
	
	public InsertValueInstruction(Value _aggregate, Value _elt, List<Integer> _indexes) {
		this.aggregate = _aggregate;
		this.elt = _elt;
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

		if (!currentType.equalsType(this.elt.getType()))
			throw new IllegalArgumentException("Element value has wrong type: " + this.elt.getType());
	}
	
	public boolean is2_8Instruction() {return true;}

	public Value getAggregate() {return this.aggregate;}
	public Value getElement() {return this.elt;}
	public List<Integer> getIndexes() {return this.indexes;}
	
	public Type getType() {return this.aggregate.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.aggregate, this.elt);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.aggregate.getType());
	}
	public boolean isInsertValue() {return true;}
	public InsertValueInstruction getInsertValueSelf() {return this;}
	
	public String toString() {
		return "insertvalue " + this.aggregate + " " + this.elt + " " + this.indexes;
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isInsertValue())
			return false;
		InsertValueInstruction e = i.getInsertValueSelf();
		return this.getAggregate().equalsValue(e.getAggregate()) &&
			this.getElement().equalsValue(e.getElement()) &&
			this.getIndexes().equals(e.getIndexes());
	}
	public int hashCode() {
		return 17*this.aggregate.hashCode() + 19*this.indexes.hashCode() + 23*this.elt.hashCode();
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newagg = this.aggregate.rewrite(old2new);
		Value newelt = this.elt.rewrite(old2new);
		if (newagg == this.aggregate && newelt == this.elt)
			return this;
		else
			return new InsertValueInstruction(newagg, newelt, this.indexes);
	}
}
