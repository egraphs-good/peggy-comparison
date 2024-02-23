package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that encodes the GetException Java PEG operator.
 */
public class GetExceptionExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 45078543L;
	public final RefType exceptionType;
	
	public GetExceptionExpr(Value value, RefType _exceptionType){
		super(Jimple.v().newRValueBox(value));
		exceptionType = _exceptionType;
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof GetExceptionExpr))
			return false;
		GetExceptionExpr g = (GetExceptionExpr)o;
		return g.getOp().equals(getOp());
	}
	public int hashCode(){
		return 101 + 31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof GetExceptionExpr))
			return false;
		GetExceptionExpr g = (GetExceptionExpr)o;
		return g.getOp().equivTo(getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new GetExceptionExpr(getOp(), exceptionType);
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return RefType.v("java.lang.Throwable");
	}
	
	public void toString(UnitPrinter up){
		up.literal("getException[");
		up.literal(exceptionType.toString());
		up.literal("](");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "getException["+exceptionType+"]("+getOp()+")";
	}
}
