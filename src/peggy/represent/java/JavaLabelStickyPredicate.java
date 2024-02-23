package peggy.represent.java;

import peggy.represent.StickyPredicate;

/**
 * This is a sticky predicate for JavaLabels.
 */
public class JavaLabelStickyPredicate implements StickyPredicate<JavaLabel> {
	public static final JavaLabelStickyPredicate INSTANCE = 
		new JavaLabelStickyPredicate();
	
	private JavaLabelStickyPredicate() {}
	
	public boolean isSticky(JavaLabel label, int childIndex) {
		return isSticky(childIndex, label);
	}
	public boolean allowsChild(
			JavaLabel parent, 
			int childIndex, 
			JavaLabel child) {
		return JavaLabelAllowsChildPredicate.allowsChild(
				parent, childIndex, child);
	}
	
	/** Passing a childindex of <0 will tell you whether the label has any sticky children.
	 */
	public static boolean isSticky(int childindex, JavaLabel label){
		if (label.isSimple()) {
			switch (label.getSimpleSelf().getOperator()) {
			case PRIMITIVECAST: return (childindex==0 || childindex<0);
			case CAST: return (childindex==1 || childindex<0);
			case INSTANCEOF: return (childindex==2 || childindex<0);
			case CLASS: return (childindex==0 || childindex<0);
			case GETFIELD: return (childindex==2 || childindex<0);
			case GETSTATICFIELD: return (childindex==1 || childindex<0);
			case INVOKEVIRTUAL: 
			case INVOKEINTERFACE:
			case INVOKESPECIAL:
				return (childindex==2 || childindex==3 || childindex<0);
			case INVOKESTATIC: 
				return (childindex==1 || childindex==2 || childindex<0);
			case NEWARRAY: return (childindex==1 || childindex<0);
			case NEWMULTIARRAY: 
				return (childindex==1 || childindex==2 || childindex<0);
			case NEWINSTANCE: return (childindex==1 || childindex<0);
			case SETFIELD: return (childindex==2 || childindex<0);
			case SETSTATICFIELD: return (childindex==1 || childindex<0);
			default: return false;
			}
		}
		return false;
	}
}
