package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that corresponds to the RHO_SIGMA Java PEG operator.
 */
public class Rho2Expr extends AbstractUnopExpr{
	public static final long serialVersionUID = 7777L;
	public Rho2Expr(Value value){
		super(Jimple.v().newRValueBox(value));
	}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof Rho2Expr))
			return false;
		Rho2Expr r = (Rho2Expr)o;
		return getOp().equals(r.getOp());
	}
	public int hashCode(){
		return 101+31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof Rho2Expr))
			return false;
		Rho2Expr r = (Rho2Expr)o;
		return getOp().equivTo(r.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new Rho2Expr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return RefType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("rho2(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "rho2("+getOp()+")";
	}
}
