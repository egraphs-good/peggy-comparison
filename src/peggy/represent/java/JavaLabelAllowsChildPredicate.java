package peggy.represent.java;

/**
 * This class encodes the 'allowsChild' data for the sticky predicate for Java. 
 */
public class JavaLabelAllowsChildPredicate {
	private JavaLabelAllowsChildPredicate() {}
	
	/** Assume that the childindex will always be in range for the parent.
	 */
	public static boolean allowsChild(JavaLabel parent, int childindex, JavaLabel child) {
		if (parent.isSimple()) {
			JavaOperator operator = parent.getSimpleSelf().getOperator();
			switch (operator) {
			case PRIMITIVECAST: 
				return (childindex!=0) || child.isType(); 
			case CAST: 
				return (childindex!=1) || child.isType(); 
			case INSTANCEOF: 
				return (childindex!=2) || child.isType();
			case CLASS:
				return (childindex!=0) || child.isType();
			case GETFIELD: 
				return (childindex!=2) || child.isField();
			case GETSTATICFIELD:
				return (childindex!=1) || child.isField();
			case INVOKEVIRTUAL:
			case INVOKEINTERFACE:
			case INVOKESPECIAL:
				if (childindex==3) {
					// params
					return child.isSimple() && child.getSimpleSelf().getOperator().equals(JavaOperator.PARAMS);
				} else if (childindex==2) {
					// method
					return child.isMethod();
				}
				
			case INVOKESTATIC:
				if (childindex==2) {
					// params
					return child.isSimple() && child.getSimpleSelf().getOperator().equals(JavaOperator.PARAMS);
				} else if (childindex==1) {
					// method
					return child.isMethod();
				}
				
			case NEWARRAY:
				return (childindex!=1) || child.isType();
			case NEWMULTIARRAY:
				if (childindex==1) {
					// type
					return child.isType();
				} else if (childindex==2) {
					// dims
					return child.isSimple() && child.getSimpleSelf().getOperator().equals(JavaOperator.DIMS);
				}
			
			case NEWINSTANCE: 
				return (childindex!=1) || child.isType();
			case SETFIELD:
				return (childindex!=2) || child.isField();
			case SETSTATICFIELD:
				return (childindex!=1) || child.isField();
			default: return true;
			}
		}
		return true;
	}
}
