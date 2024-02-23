package peggy.represent.java;

import soot.Type;
import soot.UnitPrinter;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractUnopExpr;
import soot.util.Switch;

/**
 * This is a soot expression that corresponds to the RHO_SIGMA Java PEG operator.
 */
public class ProjectSigmaExpr extends AbstractUnopExpr{
	public static final long serialVersionUID = 430987342L;
	private final int projectionIndex;
	
	public ProjectSigmaExpr(int index){
		super(Jimple.v().newRValueBox(IntConstant.v(0)));
		this.projectionIndex = index;
	}
	
	public int getProjectionIndex() {return this.projectionIndex;}
	
	public boolean equals(Object o){
		if (!(o instanceof ProjectSigmaExpr))
			return false;
		ProjectSigmaExpr r = (ProjectSigmaExpr)o;
		return this.projectionIndex == r.projectionIndex;
	}
	public int hashCode(){
		return 13*this.projectionIndex;
	}
	
	public boolean equivTo(Object o){
		if (!(o instanceof ProjectSigmaExpr))
			return false;
		ProjectSigmaExpr r = (ProjectSigmaExpr)o;
		return this.projectionIndex == r.projectionIndex;
	}
	public int equivHashCode(){
		return 13*this.projectionIndex;
	}
	
	public Object clone(){
		return new ProjectSigmaExpr(this.projectionIndex);
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return getOp().getType();
	}
	
	public void toString(UnitPrinter up){
		up.literal("project[" + this.projectionIndex + "]");
	}
	
	public String toString(){
		return "project[" + this.projectionIndex + "]";
	}
}
