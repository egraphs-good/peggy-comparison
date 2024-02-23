package peggy.represent.java;

import soot.*;
import soot.util.Switch;
import soot.jimple.internal.*;
import soot.jimple.*;
import java.util.*;

/**
 * This is a soot expression that corresponds to the SETARRAY Java PEG operator.
 */
public class SetarrayExpr implements Value, Expr {
	public static final long serialVersionUID = 43087543876L;
	private ValueBox arrayBox, indexBox, valueBox;
	
	public SetarrayExpr(Value _array, Value _index, Value _value){
		arrayBox = new RValueBox(_array);
		indexBox = new RValueBox(_index);
		valueBox = new RValueBox(_value);
	}
	
	public Value getArray(){return arrayBox.getValue();}
	public Value getIndex(){return indexBox.getValue();}
	public Value getValue(){return valueBox.getValue();}
	
	public void setArray(Value _array){
		arrayBox.setValue(_array);
	}
	public void setIndex(Value _index){
		indexBox.setValue(_index);
	}
	public void setValue(Value _value){
		valueBox.setValue(_value);
	}

	
	public boolean equals(Object o){
		if (o==null || !(o instanceof SetarrayExpr))
			return false;
		SetarrayExpr s = (SetarrayExpr)o;
		return arrayBox.getValue().equals(s.arrayBox.getValue()) && 
			indexBox.getValue().equals(s.indexBox.getValue()) && 
			valueBox.getValue().equals(s.valueBox.getValue());
	}
	public int hashCode(){
		return 7*arrayBox.getValue().hashCode() + 
			11*indexBox.getValue().hashCode() + 
			13*valueBox.getValue().hashCode();
	}
	
	
	public boolean equivTo(Object o){
		if (o==null || !(o instanceof SetarrayExpr))
			return false;
		SetarrayExpr s = (SetarrayExpr)o;
		return arrayBox.getValue().equivTo(s.arrayBox.getValue()) && 
			indexBox.getValue().equivTo(s.indexBox.getValue()) && 
			valueBox.getValue().equivTo(s.valueBox.getValue());
	}
	public int equivHashCode(){
		return 7*arrayBox.getValue().equivHashCode() + 
			11*indexBox.getValue().equivHashCode() + 
			13*valueBox.getValue().equivHashCode(); 
	}
	

	public Object clone(){
		return new SetarrayExpr(arrayBox.getValue(), indexBox.getValue(), valueBox.getValue());
	}
	
	public void apply(Switch sw){
		// do nothing
	}
	
	public Type getType(){
		return VoidType.v();
	}
	
	public void toString(UnitPrinter up){
		up.literal("setarray(");
		arrayBox.getValue().toString(up);
		up.literal(",");
		indexBox.getValue().toString(up);
		up.literal(",");
		valueBox.getValue().toString(up);
		up.literal(")");
	}
	
	public String toString(){
		return "setarray("+arrayBox.getValue()+","+indexBox.getValue()+","+valueBox.getValue()+")";
	}
	
	public List getUseBoxes(){
		List<ValueBox> result = new ArrayList<ValueBox>(3);
		result.add(arrayBox);
		result.add(indexBox);
		result.add(valueBox);
		return result;
	}
}
