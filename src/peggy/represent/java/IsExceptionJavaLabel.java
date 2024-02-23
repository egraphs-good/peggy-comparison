package peggy.represent.java;

import soot.*;

/**
 * This is a JavaLabel that checks whether a given computation produced
 * a specific exception. This operator is usually the child of a Phi node.
 */
public class IsExceptionJavaLabel extends JavaLabel {
	private final RefType exceptionType;

	public int getNumOutputs() {return 1;}
	
	public IsExceptionJavaLabel(RefType _exceptionType){
		this.exceptionType = _exceptionType;
	}
	
	public boolean isIsException() {return true;}
	public IsExceptionJavaLabel getIsExceptionSelf() {return this;}
	
	public RefType getExceptionType() {return this.exceptionType;}
	
	public int hashCode(){
		return this.exceptionType.hashCode()*89;
	}
	
	public boolean equalsLabel(JavaLabel o){
		if (!o.isIsException()) return false;
		IsExceptionJavaLabel n = o.getIsExceptionSelf();
		return n.getExceptionType().equals(this.getExceptionType());
	}
	public String toString(){
		return "IsException[" + this.getExceptionType() + "]";
	}
	
	public boolean isRevertible() {return true;}
}
