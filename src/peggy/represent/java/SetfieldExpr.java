package peggy.represent.java;

import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;

/**
 * This is a soot expression that corresponds to the SETFIELD Java PEG operator.
 */
public class SetfieldExpr implements Value, Expr{
	public static final long serialVersionUID = 1111L;
	private ValueBox baseBox, valueBox;
	private SootFieldRef fieldref;
	
	public SetfieldExpr(Value base, SootFieldRef _fieldref, Value value){
		fieldref = _fieldref;
		baseBox = new RValueBox(base);
		valueBox = new RValueBox(value);
	}
	
	public SootFieldRef getFieldRef(){return fieldref;}
	public Value getBase(){return baseBox.getValue();}
	public Value getValue(){return valueBox.getValue();}
	
	public void setBase(Value base){baseBox.setValue(base);}
	public void setValue(Value value){valueBox.setValue(value);}
	public void setFieldRef(SootFieldRef _fieldref){fieldref = _fieldref;}
	
	
	public boolean equals(Object o){
		if (o==null || !(o instanceof SetfieldExpr))
			return false;
		SetfieldExpr s = (SetfieldExpr)o;
		return baseBox.getValue().equals(s.baseBox.getValue()) &&
			valueBox.getValue().equals(s.valueBox.getValue()) &&
			fieldref.equals(s.fieldref);
	}
	public int hashCode(){
		return 17*baseBox.getValue().hashCode() + 19*valueBox.getValue().hashCode() + 23*fieldref.hashCode();
	}

	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof SetfieldExpr))
			return false;
		SetfieldExpr s = (SetfieldExpr)o;
		return baseBox.getValue().equivTo(s.baseBox.getValue()) &&
			valueBox.getValue().equivTo(s.valueBox.getValue()) &&
			fieldref.equals(s.fieldref);
	}
	public int equivHashCode(){
		return 17*baseBox.getValue().equivHashCode() + 19*valueBox.getValue().equivHashCode() + 23*fieldref.hashCode();
	}
	
	
	public Object clone(){
		return new SetfieldExpr(baseBox.getValue(), fieldref, valueBox.getValue());
	}
	
	public void apply(soot.util.Switch s){
		// do nothing, nothing!!
	}
	
	public Type getType(){
		return VoidType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("setfield(");
		baseBox.getValue().toString(up);
		up.literal(",");
		up.literal(fieldref.toString());
		up.literal(",");
		valueBox.getValue().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "setfield("+baseBox.getValue()+","+fieldref+","+valueBox.getValue()+")";
	}
	
	public List getUseBoxes(){
		List<ValueBox> result = new ArrayList<ValueBox>(3);
		result.add(baseBox);
		result.add(valueBox);
		return result;
	}
}
