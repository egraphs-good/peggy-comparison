package llvm.instructions;

import llvm.types.Type;

/** 
 * This is a parent class of integer comparison predicates and floating point
 * comparison predicates.
 */
public abstract class ComparisonPredicate {
	public boolean isInteger() {return false;}
	public IntegerComparisonPredicate getIntegerSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFloatingPoint() {return false;}
	public FloatingPointComparisonPredicate getFloatingPointSelf() {throw new UnsupportedOperationException();}

	public abstract boolean isValid(Type lhs, Type rhs);
	public abstract int getValue();
	public abstract String getLabel();
	public abstract boolean equals(Object o);
	public abstract int hashCode();
}
