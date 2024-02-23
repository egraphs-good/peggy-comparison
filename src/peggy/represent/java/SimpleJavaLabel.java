package peggy.represent.java;

import java.util.EnumMap;
import java.util.Map;

/**
 * This is a very common type of JavaLabel, that is just a wrapper around
 * a JavaOperator.
 */
public class SimpleJavaLabel extends JavaLabel {
	private static final Map<JavaOperator, SimpleJavaLabel> CACHE;
	static {
		CACHE = new EnumMap<JavaOperator, SimpleJavaLabel>(JavaOperator.class);
	}
	public static SimpleJavaLabel create(JavaOperator _operator) {
		if (_operator == null)
			throw new NullPointerException();
		if (CACHE.containsKey(_operator))
			return CACHE.get(_operator);
		SimpleJavaLabel result = new SimpleJavaLabel(_operator);
		CACHE.put(_operator, result);
		return result;
	}
	
	private final JavaOperator operator;
	private SimpleJavaLabel(JavaOperator _operator){
		this.operator = _operator;
	}
	
	public int getNumOutputs() {return operator.getNumOutputs();}

	public boolean isSimple() {return true;}
	public SimpleJavaLabel getSimpleSelf() {return this;}

	public int hashCode(){
		return this.operator.hashCode();
	}

	public boolean equalsLabel(JavaLabel o){
		if (!o.isSimple()) return false;
		SimpleJavaLabel onl = o.getSimpleSelf();
		return onl.getOperator().equals(this.getOperator());
	}

	public String getName() {return this.operator.getLabel();}
	public JavaOperator getOperator(){return this.operator;}
	public String toString(){return this.operator.getLabel();}
	public boolean isRevertible() {return true;}
}
