package eqsat.meminfer.engine.op;

import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermConstructor;
import eqsat.meminfer.engine.basic.Value;

public abstract class OpTerm
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>> extends Term<T,V> {
	protected final O mOp;
	
	public OpTerm(V value, O op, Representative<V>... children) {
		super(value, children);
		mOp = op;
	}
	public OpTerm(TermConstructor<V> constructor, O op) {
		super(constructor);
		mOp = op;
	}
	public OpTerm(OpTermConstructor<O,V> constructor) {
		this(constructor.getSuper(), constructor.getOp());
	}
	
	public final O getOp() {return mOp;}
	
	public String toString() {
		if (getArity() == 0)
			return hashCode() + ":" + mOp.toString();
		else {
			String string = hashCode() + ":" + mOp
					+ "(" + getChild(0).hashCode();
			for (int i = 1; i < getArity(); i++)
				string += "," + getChild(i).hashCode();
			return string + ")";
		}
	}
}
