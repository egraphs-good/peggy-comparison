package peggy.represent.java;

import soot.*;
import soot.jimple.internal.*;

/**
 * This is a Soot statement that wraps around any value that may throw an
 * exception.
 */
public class ResolveStmt extends soot.jimple.internal.AbstractStmt{
	public static final long serialVersionUID = 859834986398L;
	private JimpleLocal exception;
	private Value RHS;
	
	public ResolveStmt(JimpleLocal _first, Value _RHS){
		exception = _first;
		RHS = _RHS;
	}
	
	public Value getRHS(){return RHS;}
	public void setRHS(Value _RHS){RHS = _RHS;}
	public JimpleLocal getException(){return exception;}

	public Object clone(){
		ResolveStmt result = new ResolveStmt(exception, RHS);
		return result;
	}
	
	public final boolean fallsThrough(){return true;}
	public final boolean branches(){return false;}
	
	
	public void toString(UnitPrinter up){
		exception.toString(up);
		up.literal(" == ");
		RHS.toString(up);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer(100);
		buffer.append(exception.toString());
		buffer.append(" == ");
		buffer.append(RHS.toString());
		return buffer.toString();
	}
}
