package peggy.represent.java;

import soot.*;

/**
 * This is a JavaLabel that represents a particular Java field (static or nonstatic).
 */
public class FieldJavaLabel extends JavaLabel {
	private final String className;
	private final String fieldName;
	private final Type fieldType;

	public FieldJavaLabel(String _class, String _field, Type _type) {
		this.className = _class;
		this.fieldName = _field;
		this.fieldType = _type;
	}

	public int getNumOutputs() {return 1;}

	public boolean isField() {return true;}
	public FieldJavaLabel getFieldSelf() {return this;}
	
	public String getClassName() {return this.className;}
	public String getFieldName() {return this.fieldName;}
	public Type getType() {return this.fieldType;}

	public int hashCode(){
		return this.className.hashCode()*3 + 
			this.fieldName.hashCode()*5 +
			this.fieldType.hashCode()*7;
	}

	public boolean equalsLabel(JavaLabel o){
		if (!o.isField())
			return false;

		FieldJavaLabel f = o.getFieldSelf();
		return f.getClassName().equals(this.getClassName()) &&
			f.getFieldName().equals(this.getFieldName()) &&
			f.getType().equals(this.getType());
	}

	public String toString(){
		return "Field[" + this.getFieldName() + "]";
	}

	public boolean isRevertible() {return true;}
}
