package peggy.represent.java;


/**
 * This class implements the L parameter of CFGs for Java.
 * There are many types of JavaLabels, and a significant subset of them 
 * are SimpleJavaLabels.
 */
public abstract class JavaLabel {
	public boolean isTrue() {return false;}
	public boolean isFalse() {return false;}
	
	public abstract int getNumOutputs();
	
	public boolean isBottom() {return false;}
	private static final JavaLabel BOTTOM = new JavaLabel() {
		public boolean isBottom() {return true;}
		public boolean equalsLabel(JavaLabel label) {return label.isBottom();}
		public int getNumOutputs() {return 0;}
		public int hashCode() {return 432087;}
		public boolean isRevertible() {return false;}
		public String toString() {return "BOTTOM";}
	};
	public static JavaLabel getBottom() {return BOTTOM;}
	
	public boolean isSimple() {return false;}
	public SimpleJavaLabel getSimpleSelf() {throw new UnsupportedOperationException();}
	
	public boolean isBasic() {return false;}
	public BasicJavaLabel getBasicSelf() {throw new UnsupportedOperationException();}
	
	public boolean isConstant() {return false;}
	public ConstantValueJavaLabel getConstantSelf() {throw new UnsupportedOperationException();}
	
	public boolean isAnnotation() {return false;}
	public AnnotationJavaLabel getAnnotationSelf() {throw new UnsupportedOperationException();}
	
	public boolean isField() {return false;}
	public FieldJavaLabel getFieldSelf() {throw new UnsupportedOperationException();}
	
	public boolean isGetException() {return false;}
	public GetExceptionJavaLabel getGetExceptionSelf() {throw new UnsupportedOperationException();}
	
	public boolean isIsException() {return false;}
	public IsExceptionJavaLabel getIsExceptionSelf() {throw new UnsupportedOperationException();}
	
	public boolean isType() {return false;}
	public TypeJavaLabel getTypeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isMethod() {return false;}
	public MethodJavaLabel getMethodSelf() {throw new UnsupportedOperationException();}
	
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof JavaLabel))
			return false;
		JavaLabel label = (JavaLabel)o;
		return this.equalsLabel(label);
	}
	public abstract boolean equalsLabel(JavaLabel label);
	public abstract int hashCode();
	public abstract String toString();
	public abstract boolean isRevertible();
}
