package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that corresponds to the RHO_VALUE Java PEG operator.
 */
public class Rho1Expr extends AbstractUnopExpr{
	public static final long serialVersionUID = 66667L;
	public Rho1Expr(Value value){
		super(Jimple.v().newRValueBox(value));
	}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof Rho1Expr))
			return false;
		Rho1Expr r = (Rho1Expr)o;
		return r.getOp().equals(getOp());
	}
	public int hashCode(){
		return 101+31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof Rho1Expr))
			return false;
		Rho1Expr r = (Rho1Expr)o;
		return r.getOp().equivTo(getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new Rho1Expr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("rho1(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "rho1("+getOp()+")";
	}
}
