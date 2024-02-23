package peggy.represent.java;

import soot.*;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractUnopExpr;
import soot.util.Switch;

/**
 * This is a soot expression that represents an explicit exception throw, 
 * like the THROW Java PEG operator.
 */
public class ThrowExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 5555L;
	
	public ThrowExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof ThrowExpr))
			return false;
		ThrowExpr e = (ThrowExpr)o;
		return e.getOp().equals(getOp());
	}
	public int hashCode(){
		return 101+31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof ThrowExpr))
			return false;
		ThrowExpr e = (ThrowExpr)o;
		return e.getOp().equivTo(getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new ThrowExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("throw(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "throw("+getOp()+")";
	}
}
