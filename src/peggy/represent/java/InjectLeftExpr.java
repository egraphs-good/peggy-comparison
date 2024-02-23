package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that matches the INJL Java PEG operator.
 */
public class InjectLeftExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 5062392L;
	public InjectLeftExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof InjectLeftExpr))
			return false;
		InjectLeftExpr g = (InjectLeftExpr)o;
		return getOp().equals(g.getOp());
	}
	public int hashCode(){
		return 101 + 31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof InjectLeftExpr))
			return false;
		InjectLeftExpr g = (InjectLeftExpr)o;
		return getOp().equivTo(g.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new InjectLeftExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("injl(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "injl("+getOp()+")";
	}
}
