package peggy.represent.java;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.Switch;

/**
 * This is a soot expression that encodes the RHO_SIGMA Java PEG operator.
 */
public class GetSigmaExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 5062392L;
	public GetSigmaExpr(Value value){
		super(Jimple.v().newRValueBox(value));
	}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof GetSigmaExpr))
			return false;
		GetSigmaExpr g = (GetSigmaExpr)o;
		return getOp().equals(g.getOp());
	}
	public int hashCode(){
		return 101+31*getOp().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof GetSigmaExpr))
			return false;
		GetSigmaExpr g = (GetSigmaExpr)o;
		return getOp().equivTo(g.getOp());
	}
	public int equivHashCode(){
		return 101+31*getOp().equivHashCode();
	}

	public Object clone(){
		return new GetSigmaExpr(getOp());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return RefType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("getSigma(");
		getOp().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "getSigma("+getOp()+")";
	}
}
