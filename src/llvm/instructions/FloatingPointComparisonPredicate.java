package llvm.instructions;

import llvm.types.Type;

/**
 * This class represents the type of floating point comparison to be used in 
 * an fcmp instruction.
 */
public class FloatingPointComparisonPredicate extends ComparisonPredicate {
    // Opcode        U L G E    Intuitive operation
    public static final FloatingPointComparisonPredicate FCMP_FALSE; ///<  0 0 0 0    Always false (always folded)
    public static final FloatingPointComparisonPredicate FCMP_OEQ; ///<  0 0 0 1    True if ordered and equal
    public static final FloatingPointComparisonPredicate FCMP_OGT; ///<  0 0 1 0    True if ordered and greater than
    public static final FloatingPointComparisonPredicate FCMP_OGE; ///<  0 0 1 1    True if ordered and greater than or equal
    public static final FloatingPointComparisonPredicate FCMP_OLT; ///<  0 1 0 0    True if ordered and less than
    public static final FloatingPointComparisonPredicate FCMP_OLE; ///<  0 1 0 1    True if ordered and less than or equal
    public static final FloatingPointComparisonPredicate FCMP_ONE; ///<  0 1 1 0    True if ordered and operands are unequal
    public static final FloatingPointComparisonPredicate FCMP_ORD; ///<  0 1 1 1    True if ordered (no nans)
    public static final FloatingPointComparisonPredicate FCMP_UNO; ///<  1 0 0 0    True if unordered: isnan(X) | isnan(Y)
    public static final FloatingPointComparisonPredicate FCMP_UEQ; ///<  1 0 0 1    True if unordered or equal
    public static final FloatingPointComparisonPredicate FCMP_UGT; ///<  1 0 1 0    True if unordered or greater than
    public static final FloatingPointComparisonPredicate FCMP_UGE; ///<  1 0 1 1    True if unordered, greater than, or equal
    public static final FloatingPointComparisonPredicate FCMP_ULT; ///<  1 1 0 0    True if unordered or less than
    public static final FloatingPointComparisonPredicate FCMP_ULE; ///<  1 1 0 1    True if unordered, less than, or equal
    public static final FloatingPointComparisonPredicate FCMP_UNE; ///<  1 1 1 0    True if unordered or not equal
    public static final FloatingPointComparisonPredicate FCMP_TRUE; ///<  1 1 1 1    Always true (always folded)
    private static final FloatingPointComparisonPredicate[] ALL;
    
    static {
    	ALL = new FloatingPointComparisonPredicate[] {
    		    FCMP_FALSE = new FloatingPointComparisonPredicate(0, "false"), ///<  0 0 0 0    Always false (always folded)
    		    FCMP_OEQ   = new FloatingPointComparisonPredicate(1, "oeq"), ///<  0 0 0 1    True if ordered and equal
    		    FCMP_OGT   = new FloatingPointComparisonPredicate(2, "ogt"), ///<  0 0 1 0    True if ordered and greater than
    		    FCMP_OGE   = new FloatingPointComparisonPredicate(3, "oge"), ///<  0 0 1 1    True if ordered and greater than or equal
    		    FCMP_OLT   = new FloatingPointComparisonPredicate(4, "olt"), ///<  0 1 0 0    True if ordered and less than
    		    FCMP_OLE   = new FloatingPointComparisonPredicate(5, "ole"), ///<  0 1 0 1    True if ordered and less than or equal
    		    FCMP_ONE   = new FloatingPointComparisonPredicate(6, "one"), ///<  0 1 1 0    True if ordered and operands are unequal
    		    FCMP_ORD   = new FloatingPointComparisonPredicate(7, "ord"), ///<  0 1 1 1    True if ordered (no nans)
    		    FCMP_UNO   = new FloatingPointComparisonPredicate(8, "uno"), ///<  1 0 0 0    True if unordered: isnan(X) | isnan(Y)
    		    FCMP_UEQ   = new FloatingPointComparisonPredicate(9, "ueq"), ///<  1 0 0 1    True if unordered or equal
    		    FCMP_UGT   = new FloatingPointComparisonPredicate(10, "ugt"), ///<  1 0 1 0    True if unordered or greater than
    		    FCMP_UGE   = new FloatingPointComparisonPredicate(11, "uge"), ///<  1 0 1 1    True if unordered, greater than, or equal
    		    FCMP_ULT   = new FloatingPointComparisonPredicate(12, "ult"), ///<  1 1 0 0    True if unordered or less than
    		    FCMP_ULE   = new FloatingPointComparisonPredicate(13, "ule"), ///<  1 1 0 1    True if unordered, less than, or equal
    		    FCMP_UNE   = new FloatingPointComparisonPredicate(14, "une"), ///<  1 1 1 0    True if unordered or not equal
    		    FCMP_TRUE  = new FloatingPointComparisonPredicate(15, "true"), ///<  1 1 1 1    Always true (always folded)
    	};
    }
	
    private final int value;
    private final String label;
    private FloatingPointComparisonPredicate(int _value, String _label) {
    	this.value = _value;
    	this.label = _label;
    }
    
    public String getLabel() {return this.label;}
    public int getValue() {return this.value;}
    public boolean isFloatingPoint() {return true;}
    public FloatingPointComparisonPredicate getFloatingPointSelf() {return this;}

    public boolean equals(Object o) {
    	return this == o;
    }
    public int hashCode() {
    	return this.value*359;
    }
    
    public static FloatingPointComparisonPredicate[] values() {
    	return (FloatingPointComparisonPredicate[])ALL.clone();
    }
    
    public boolean isValid(Type lhs, Type rhs) {
    	if (!lhs.equals(rhs))
    		return false;
    	return lhs.isFloatingPoint() || 
    		(lhs.isComposite() && 
    		 lhs.getCompositeSelf().isVector() && 
    		 lhs.getCompositeSelf().getVectorSelf().getElementType().isFloatingPoint());
    }
    
    public static FloatingPointComparisonPredicate getByValue(int value) {
    	if (0 <= value && value <= 15)
    		return ALL[value];
    	else
    		throw new IllegalArgumentException("Invalid predicate value: " + value);
    }
    
    public String toString() {
    	return this.label;
    }
}
