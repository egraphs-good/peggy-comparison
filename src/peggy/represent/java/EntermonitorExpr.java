package peggy.represent.java;

import soot.*;
import soot.jimple.Jimple;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This represents the ENTERMONITOR instruction as a soot expression.
 */
public class EntermonitorExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 3333L;
	
	public EntermonitorExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof EntermonitorExpr))
			return false;
		EntermonitorExpr e = (EntermonitorExpr)o;
		return e.getOp().equals(getOp());
	}
	public int hashCode(){
		return 101+31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof EntermonitorExpr))
			return false;
		EntermonitorExpr e = (EntermonitorExpr)o;
		return e.getOp().equivTo(getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new EntermonitorExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return RefType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("entermonitor(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "entermonitor("+getOp()+")";
	}
}
