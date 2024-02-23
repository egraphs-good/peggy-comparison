package peggy.analysis.java.inlining;

import peggy.analysis.java.ClassSet;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JimpleLocal;

/**
 * This class is a type analysis over jimple code. Every local variable,
 * field, and method return type is given a ClassSet of possible classes
 * it could be.
 */
public abstract class JimpleTypeAnalysis {
	public abstract SootMethod getMethod();
	public abstract ClassSet getLocalClassSet(JimpleLocal local, Unit where);
	public abstract ClassSet getFieldClassSet(SootField field);
	public abstract ClassSet getMethodReturnClassSet(SootMethod method);
	public abstract DispatchMap getPotentialTargets(Unit where);
}
