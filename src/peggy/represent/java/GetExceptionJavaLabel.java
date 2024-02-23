package peggy.represent.java;

import soot.RefType;

/**
 * This JavaLabel extracts the exception value from operators that
 * can optionally return an exception (i.e. INVOKEVIRTUAL).
 */
public class GetExceptionJavaLabel extends JavaLabel {
	private final RefType exceptionType;

	public int getNumOutputs() {return 1;}
	
	public GetExceptionJavaLabel(RefType _extype){
		this.exceptionType = _extype;
	}
	
	public boolean isGetException() {return true;}
	public GetExceptionJavaLabel getGetExceptionSelf() {return this;}
	
	public RefType getExceptionType() {return this.exceptionType;}
	
	public boolean equalsLabel(JavaLabel o){
		if (!o.isGetException()) return false;
		GetExceptionJavaLabel g = o.getGetExceptionSelf();
		return g.getExceptionType().equals(this.getExceptionType());
	}
	
	public int hashCode(){return this.exceptionType.hashCode();}
	
	public String toString(){
		return "GetException[" + this.exceptionType + "]";
	}
	
	public boolean isRevertible() {return true;}
}
