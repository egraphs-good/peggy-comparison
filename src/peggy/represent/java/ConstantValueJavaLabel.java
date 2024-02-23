package peggy.represent.java;

import soot.jimple.Constant;

/**
 * This is a JavaLabel that hold a constant Soot value.
 * 
 * Constant value should only be one of the following:
 * 	IntConstant
 * 	LongConstant
 * 	FloatConstant
 * 	DoubleConstant
 * 	NullConstant
 * 	StringConstant
 * 
 */
public class ConstantValueJavaLabel extends JavaLabel {
	private final Constant value;
	
	public int getNumOutputs() {return 1;}
	
	public ConstantValueJavaLabel(Constant _constant) {
		this.value = _constant;
	}
	
	public boolean isConstant() {return true;}
	public ConstantValueJavaLabel getConstantSelf() {return this;}
	
	public Constant getValue() {return this.value;}
	
	public boolean equalsLabel(JavaLabel o) {
		if (!o.isConstant()) return false;
		return this.getValue().equals(o.getConstantSelf().getValue());
	}
	public int hashCode() {
		return this.getValue().hashCode()*83;
	}
	public String toString() {
		return "Constant[" + escape(this.getValue().toString()) + "]";
	}
	public boolean isRevertible() {return true;}
	
	public static String escape(String str) {
		StringBuffer buffer = new StringBuffer(str.length()*2);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < 32 || c >= 127) {
				buffer.append("&#" + Integer.toHexString(c) + ";");
			} else {
				switch (c) {
				case '\\':
				case '"':
				case '\'':
				case '\n':
				case '\r':
					buffer.append("&#" + Integer.toHexString(c) + ";");
					break;
				default:
					buffer.append(c);
					break;
				}
			}
		}
		return buffer.toString();
	}
}
