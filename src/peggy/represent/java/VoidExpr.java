package peggy.represent.java;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import java.util.*;

/**
 * This is a soot expression that corresponds to the VOID Java PEG operator.
 * It is mostly a placeholder.
 */
public class VoidExpr implements Value, Expr{
	public static final long serialVersionUID = 0x50973L;
	public static final VoidExpr INSTANCE = new VoidExpr();
	private VoidExpr(){}
	
	public Object clone(){return this;}
	public Type getType(){return VoidType.v();}
	public List getUseBoxes(){return new ArrayList<Value>();}
	public void toString(UnitPrinter up){
		up.literal("void");
	}
	
	public String toString(){
		return "void";
	}
	
	public void apply(Switch s){
		// do nothing!
	}
	
	public int equivHashCode(){
		return 5;
	}
	public boolean equivTo(Object o){
		return this.equals(o);
	}
}
