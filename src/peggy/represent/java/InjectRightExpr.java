package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that matches the INJR Java PEG operator.
 */
public class InjectRightExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 5062392L;
	public InjectRightExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof InjectRightExpr))
			return false;
		InjectRightExpr g = (InjectRightExpr)o;
		return getOp().equals(g.getOp());
	}
	public int hashCode(){
		return 101 + 31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof InjectRightExpr))
			return false;
		InjectRightExpr g = (InjectRightExpr)o;
		return getOp().equivTo(g.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new InjectRightExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("injr(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "injr("+getOp()+")";
	}
}
