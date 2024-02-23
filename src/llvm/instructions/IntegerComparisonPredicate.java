package llvm.instructions;

import llvm.types.Type;

/**
 * This class represents one of the integer comparison types that
 * can be used in an ICMP instruction.
 */
public class IntegerComparisonPredicate extends ComparisonPredicate {
    public static final IntegerComparisonPredicate ICMP_EQ;
    public static final IntegerComparisonPredicate ICMP_NE;
    public static final IntegerComparisonPredicate ICMP_UGT;
    public static final IntegerComparisonPredicate ICMP_UGE;
    public static final IntegerComparisonPredicate ICMP_ULT;
    public static final IntegerComparisonPredicate ICMP_ULE;
    public static final IntegerComparisonPredicate ICMP_SGT;
    public static final IntegerComparisonPredicate ICMP_SGE;
    public static final IntegerComparisonPredicate ICMP_SLT;
    public static final IntegerComparisonPredicate ICMP_SLE;
    private static final IntegerComparisonPredicate[] ALL;
    
    static {
    	ALL = new IntegerComparisonPredicate[] {
    			ICMP_EQ = new IntegerComparisonPredicate(32, "eq"),     ///< equal
    			ICMP_NE = new IntegerComparisonPredicate(33, "ne"),     ///< not equal
    			ICMP_UGT = new IntegerComparisonPredicate(34, "ugt"),    ///< unsigned greater than
    			ICMP_UGE = new IntegerComparisonPredicate(35, "uge"),    ///< unsigned greater or equal
    			ICMP_ULT = new IntegerComparisonPredicate(36, "ult"),    ///< unsigned less than
    			ICMP_ULE = new IntegerComparisonPredicate(37, "ule"),    ///< unsigned less or equal
    			ICMP_SGT = new IntegerComparisonPredicate(38, "sgt"),    ///< signed greater than
    			ICMP_SGE = new IntegerComparisonPredicate(39, "sge"),    ///< signed greater or equal
    			ICMP_SLT = new IntegerComparisonPredicate(40, "slt"),    ///< signed less than
    			ICMP_SLE = new IntegerComparisonPredicate(41, "sle")     ///< signed less or equal
    	};
    }
    
    private final int value;
    private final String label;
    private IntegerComparisonPredicate(int _value, String _label) {
    	this.value = _value;
    	this.label = _label;
    }
    
    public String getLabel() {return this.label;}
    public int getValue() {return this.value;}
    public boolean isInteger() {return true;}
    public IntegerComparisonPredicate getIntegerSelf() {return this;}
    
    public boolean equals(Object o) {
    	return this == o;
    }
    public int hashCode() {
    	return this.value*293;
    }
    
    public static IntegerComparisonPredicate[] values() {
    	return (IntegerComparisonPredicate[])ALL.clone();
    }
    
    public boolean isValid(Type lhs, Type rhs) {
    	if (lhs.isComposite() && lhs.getCompositeSelf().isPointer() &&
    		rhs.isComposite() && rhs.getCompositeSelf().isPointer())
    		return true;
    	if (!lhs.equals(rhs))
    		return false;
    	return lhs.isInteger() ||  
    		(lhs.isComposite() && 
    		 lhs.getCompositeSelf().isVector() && 
    		 lhs.getCompositeSelf().getVectorSelf().getElementType().isInteger());
    }
    
    public static IntegerComparisonPredicate getByValue(int value) {
    	if (32 <= value && value <= 41)
    		return ALL[value-32];
    	else
    		throw new IllegalArgumentException("Invalid predicate value: " + value);
    }
    
    public String toString() {
    	return this.label;
    }
}
