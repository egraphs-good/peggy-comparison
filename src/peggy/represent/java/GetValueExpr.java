package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that encodes the RHO_VALUE Java PEG operator.
 */
public class GetValueExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 5062392L;
	public GetValueExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof GetValueExpr))
			return false;
		GetValueExpr g = (GetValueExpr)o;
		return getOp().equals(g.getOp());
	}
	public int hashCode(){
		return 101 + 31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof GetValueExpr))
			return false;
		GetValueExpr g = (GetValueExpr)o;
		return getOp().equivTo(g.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}
	

	public Object clone(){
		return new GetValueExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("getValue(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "getValue("+getOp()+")";
	}
}
