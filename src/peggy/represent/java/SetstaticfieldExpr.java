package peggy.represent.java;

import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;

/**
 * This is a soot expression that corresponds to the SETSTATICFIELD Java PEG
 * operator.
 */
public class SetstaticfieldExpr implements Value, Expr{
	public static final long serialVersionUID = 11112222L;
	private SootFieldRef fieldref;
	private ValueBox valueBox;
	
	public SetstaticfieldExpr(SootFieldRef _fieldref, Value value){
		fieldref = _fieldref;
		valueBox = new RValueBox(value);
	}
	
	public SootFieldRef getFieldRef(){return fieldref;}
	public Value getValue(){return valueBox.getValue();}
	
	public void setFieldRef(SootFieldRef _fieldref){fieldref = _fieldref;}
	public void setValue(Value value){valueBox.setValue(value);}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof SetfieldExpr))
			return false;
		SetstaticfieldExpr s = (SetstaticfieldExpr)o;
		return fieldref.equals(s.fieldref) &&
			valueBox.getValue().equals(s.valueBox.getValue());
	}
	public int hashCode(){
		return 17*fieldref.hashCode() + 19*valueBox.getValue().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof SetfieldExpr))
			return false;
		SetstaticfieldExpr s = (SetstaticfieldExpr)o;
		return fieldref.equals(s.fieldref) &&
			valueBox.getValue().equivTo(s.valueBox.getValue());
	}
	public int equivHashCode(){
		return 17*fieldref.hashCode() + 19*valueBox.getValue().equivHashCode();
	}
	
	
	public Object clone(){
		return new SetstaticfieldExpr(fieldref, valueBox.getValue());
	}
	
	public void apply(soot.util.Switch s){
		// do nothing, nothing!!
	}
	
	public Type getType(){
		return VoidType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("setstaticfield(");
		up.literal(fieldref.toString());
		up.literal(",");
		valueBox.getValue().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "setstaticfield("+fieldref+","+valueBox.getValue()+")";
	}
	
	public List getUseBoxes(){
		List<ValueBox> result = new ArrayList<ValueBox>(3);
		result.add(valueBox);
		return result;
	}
}
