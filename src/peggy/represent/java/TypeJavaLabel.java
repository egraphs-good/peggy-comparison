package peggy.represent.java;

import soot.Type;

/**
 * This is a JavaLabel that encodes a particular java type name.
 * These are used, for example, as children to CAST operators.
 */
public class TypeJavaLabel extends JavaLabel {
	private final Type type;

	public TypeJavaLabel(Type _type){
		this.type = _type;
	}

	public int getNumOutputs() {return 1;}

	public boolean isType() {return true;}
	public TypeJavaLabel getTypeSelf() {return this;}

	public Type getType() {return this.type;}

	public String toString(){
		return "Type[" + this.type.toString() + "]";
	}

	public int hashCode(){
		return this.type.hashCode()*151;
	}

	public boolean equalsLabel(JavaLabel o){
		if (!o.isType()) return false;

		TypeJavaLabel jtnl = o.getTypeSelf();
		return jtnl.getType().equals(this.getType());
	}

	public boolean isRevertible() {return true;}
}
