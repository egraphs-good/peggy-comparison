package peggy.represent.java;

import eqsat.BasicOp;

/**
 * This is a JavaLabel that corresponds to a BasicOp in the engine.
 */
public class BasicJavaLabel extends JavaLabel {
	private final BasicOp operator;
	
	public BasicJavaLabel(BasicOp op) {
		this.operator = op;
	}
	
	public int getNumOutputs() {return 1;}

	public boolean isTrue() {return this.operator.equals(BasicOp.True);}
	public boolean isFalse() {return this.operator.equals(BasicOp.False);}
	
	public boolean isBasic() {return true;}
	public BasicJavaLabel getBasicSelf() {return this;}
	
	public BasicOp getOperator() {return this.operator;}
	
	public boolean equalsLabel(JavaLabel l) {
		if (!l.isBasic()) return false;
		return this.getOperator().equals(l.getBasicSelf().getOperator());
	}
	public int hashCode() {
		return this.getOperator().hashCode();
	}
	public String toString() {
		return "BasicOp[" + this.getOperator() + "]";
	}
	public boolean isRevertible() {return true;}
}
