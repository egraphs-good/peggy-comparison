package peggy.represent.java;

import soot.jimple.internal.*;
import soot.jimple.*;
import soot.util.*;
import soot.*;

/**
 * This is a soot expression that encodes the IsException operator for Java PEGs.
 */
public class IsExceptionExpr extends AbstractUnopExpr implements ConditionExpr{
	public static final long serialVersionUID = 5062392L;
	public final RefType exceptionType;
	
	public IsExceptionExpr(JimpleLocal l, RefType t){
		super(Jimple.v().newLocalBox(l));
		exceptionType = t;
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof IsExceptionExpr))
			return false;
		IsExceptionExpr i = (IsExceptionExpr)o;
		return exceptionType.equals(i.exceptionType) && getOp().equals(i.getOp());
	}
	public int hashCode(){
		return 101 + 31*getOp().hashCode() + 97*exceptionType.hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof IsExceptionExpr))
			return false;
		IsExceptionExpr i = (IsExceptionExpr)o;
		return exceptionType.equals(i.exceptionType) && getOp().equivTo(i.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode() + 97*exceptionType.hashCode();
	}

	public Object clone(){
		return new IsExceptionExpr((JimpleLocal)getOp(), exceptionType);
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return BooleanType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("isException[");
		up.literal(exceptionType.toString());
		up.literal("](");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "isException["+exceptionType.toString()+"]("+getOp()+")";
	}
	
	//////////////
	
	public String getSymbol(){return "isException["+exceptionType.toString()+"]";}
	public Value getOp1(){return getOp();}
	public Value getOp2(){return getOp();}
	public void setOp1(Value v){setOp(v);}
	public void setOp2(Value v){setOp(v);}
	public ValueBox getOp1Box(){return getOpBox();}
	public ValueBox getOp2Box(){return getOpBox();}
}
