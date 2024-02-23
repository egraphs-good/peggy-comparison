package peggy.analysis.java.inlining;

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import util.pair.Pair;

/**
 * This class describes a heuristic for when a java method should be inlined.
 */
public class SimpleInlinerHeuristic implements InlinerHeuristic {
	private static final boolean DEBUG = false;
	private static final void debug(String message) {
		if (DEBUG)
			System.err.println("SimpleInlinerHeuristic: " + message);
	}
	
	
	public static final int MAX_INLINES_PER_CALL = 3;
	public static final double METHOD_THRESHOLD = 3.00;
	public static final double CLASS_THRESHOLD = 3.00;
	public static final double MAX_COST = 1000.0;
	public static final int MAX_NEW_INSTRUCTIONS = 20;
	public static final int MAXIMAL_METHOD_SIZE = 5000;
	
	protected final Map<String,Pair<Integer,Integer>> methodIncreaseMap;
	protected final Map<String,Pair<Integer,Integer>> classIncreaseMap;
	
	public SimpleInlinerHeuristic() {
		this.methodIncreaseMap = new HashMap<String,Pair<Integer,Integer>>();
		this.classIncreaseMap = new HashMap<String,Pair<Integer,Integer>>();
	}

	private Pair<Integer,Integer> getMethodPair(SootMethod method) {
		String signature = method.getSignature();
		Pair<Integer,Integer> methodPair;
		if (!this.methodIncreaseMap.containsKey(signature)) {
			SootResolver.v().resolveClass(method.getDeclaringClass().getName(), SootClass.BODIES);
			Body body = method.retrieveActiveBody();
			methodPair = new Pair<Integer,Integer>(body.getUnits().size(), 0);
			this.methodIncreaseMap.put(signature, methodPair);
		} else {
			methodPair = this.methodIncreaseMap.get(signature);
		}
		return methodPair;
	}

	private Pair<Integer,Integer> getClassPair(SootClass clazz) {
		String classname = clazz.getName();
		Pair<Integer,Integer> classPair;
		if (!this.classIncreaseMap.containsKey(classname)) {
			int size = 0;
			for (SootMethod method : (List<SootMethod>)clazz.getMethods()) {
				if (method.isConcrete()) {
					Body body = method.retrieveActiveBody();
					size += body.getUnits().size();
				}
			}
			classPair = new Pair<Integer,Integer>(size, 0);
			this.classIncreaseMap.put(classname, classPair);
		} else {
			classPair = this.classIncreaseMap.get(classname);
		}
		return classPair;
	}
	
	public boolean shouldInline(SootMethod inliner, Unit where, SootMethod inlinee) {
		int inlineeSize = inlinee.retrieveActiveBody().getUnits().size();
		
		if (inlineeSize > MAX_NEW_INSTRUCTIONS) {
			debug("Exceeded max new instructions");
			return false;
		}
		
		// see if this inlining would exceed the method threshold
		Pair<Integer,Integer> methodPair = this.getMethodPair(inliner);
		if ((methodPair.getFirst() + methodPair.getSecond() + inlineeSize) > (METHOD_THRESHOLD * methodPair.getFirst())) {
			debug("Method threshold broken");
			return false;
		}
		
		if ((methodPair.getFirst() + methodPair.getSecond() + inlineeSize) > MAXIMAL_METHOD_SIZE) {
			debug("Exceeded maximal method size");
			return false;
		}
		
		
		/*
		// see if this inlining would exceed the class threshold
		Pair<Integer,Integer> classPair = this.getClassPair(inliner.getDeclaringClass());
		if ((classPair.getFirst() + classPair.getSecond() + inlineeSize) > (CLASS_THRESHOLD * classPair.getFirst())) {
			debug("Class threshold broken");
			return false;
		}
		*/
		
		
		/*
		boolean result = (this.computeMethodCost(inlinee.retrieveActiveBody()) < MAX_COST);
		if (!result) {
			debug("Cost threshold broken");
		}
		*/
		
		
		
		return true;
	}
	
	
	public boolean shouldInlineAll(SootMethod inliner, Unit where, Collection<SootMethod> inlinees) {
		if (inlinees.size() > MAX_INLINES_PER_CALL) {
			debug("Exceeded max inlines per call");
			return false;
		}
		
		
		int additionalSize = 0;
		for (SootMethod inlinee : inlinees) {
			Body body = inlinee.retrieveActiveBody();
			int mysize = body.getUnits().size();
			if (mysize > MAX_NEW_INSTRUCTIONS) {
				debug("Exceeded max new instructions");
				return false;
			}
			
			additionalSize += mysize;
		}
		
		// see if this inlining would exceed the method threshold
		Pair<Integer,Integer> methodPair = this.getMethodPair(inliner);
		if ((methodPair.getFirst() + methodPair.getSecond() + additionalSize) > (METHOD_THRESHOLD * methodPair.getFirst())) {
			debug("Method threshold broken");
			return false;
		}
		
		
		if ((methodPair.getFirst() + methodPair.getSecond() + additionalSize) > MAXIMAL_METHOD_SIZE) {
			debug("Exceeded maximal method size");
			return false;
		}
		
		
		
		/*
		// see if this inlining would exceed the class threshold
		Pair<Integer,Integer> classPair = this.getClassPair(inliner.getDeclaringClass());
		if ((classPair.getFirst() + classPair.getSecond() + additionalSize) > (CLASS_THRESHOLD * classPair.getFirst())) {
			debug("Class threshold broken");
			return false;
		}
		*/


		/*
		for (SootMethod inlinee : inlinees) {
			Body body = inlinee.retrieveActiveBody();
			double cost = this.computeMethodCost(body);
			if (cost > MAX_COST) {
				debug("Cost threshold broken");
				return false;
			}
		}
		*/
		
		return true;
	}
	
	
	
	private double computeMethodCost(Body body) {
		double cost = 0.0;
		
		for (Iterator<Unit> it = body.getUnits().iterator(); it.hasNext(); ) {
			Unit unit = it.next();
			cost += this.getStmtCost(unit);
		}
		
		return cost;
	}
	
	private double getStmtCost(Unit unit) {
		if (unit instanceof JAssignStmt) {
			JAssignStmt assign = (JAssignStmt)unit;
			return this.getExprCost(assign.getLeftOp()) +
				this.getExprCost(assign.getRightOp());
		} else if (unit instanceof JEnterMonitorStmt ||
				   unit instanceof JExitMonitorStmt) {
			return 30.0;
		} else if (unit instanceof JGotoStmt) {
			return 10.0;
		} else if (unit instanceof JIdentityStmt) {
			JIdentityStmt id = (JIdentityStmt)unit;
			return this.getExprCost(id.getLeftOp()) +
				this.getExprCost(id.getRightOp());
		} else if (unit instanceof JIfStmt) {
			JIfStmt ifstmt = (JIfStmt)unit;
			return 10.0 + this.getExprCost(ifstmt.getCondition());
		} else if (unit instanceof JInvokeStmt) {
			JInvokeStmt invoke = (JInvokeStmt)unit;
			return this.getExprCost(invoke.getInvokeExpr());
		} else if (unit instanceof JLookupSwitchStmt) {
			JLookupSwitchStmt lookup = (JLookupSwitchStmt)unit;
			return 10.0 + lookup.getTargetCount()*2.0;
		} else if (unit instanceof JTableSwitchStmt) {
			//JTableSwitchStmt table = (JTableSwitchStmt)unit;
			return 10.0 + 1.0;
		} else if (unit instanceof JNopStmt) {
			return 1.0;
		} else if (unit instanceof JReturnStmt) {
			JReturnStmt stmt = (JReturnStmt)unit;
			return 10.0 + this.getExprCost(stmt.getOp());
		} else if (unit instanceof JReturnVoidStmt) {
			return 10.0;
		} else {
			throw new IllegalArgumentException("We don't support units of type: " + unit.getClass());
		}
	}
	
	private double getExprCost(Value value) {
		if (value instanceof AbstractJimpleFloatBinopExpr) { // arith
			AbstractJimpleFloatBinopExpr binop = (AbstractJimpleFloatBinopExpr)value;
			double operands = this.getExprCost(binop.getOp1()) + this.getExprCost(binop.getOp2());
			if (binop instanceof JAddExpr || binop instanceof JSubExpr)
				return operands + 5.0;
			else if (binop instanceof JMulExpr)
				return operands + 10.0;
			else
				return operands + 20.0;
		} else if (value instanceof AbstractJimpleIntLongBinopExpr) { // bitwise
			AbstractJimpleIntLongBinopExpr binop = (AbstractJimpleIntLongBinopExpr)value;
			return this.getExprCost(binop.getOp1()) + this.getExprCost(binop.getOp2()) + 5.0;
		} else if (value instanceof JArrayRef) {
			JArrayRef ref = (JArrayRef)value;
			return this.getExprCost(ref.getBase()) + this.getExprCost(ref.getIndex()) + 50.0;
		} else if (value instanceof JCastExpr) {
			JCastExpr cast = (JCastExpr)value;
			return this.getExprCost(cast.getOp()) + 50.0;
		} else if (value instanceof AbstractJimpleIntBinopExpr) { // cmp
			AbstractJimpleIntBinopExpr binop = (AbstractJimpleIntBinopExpr)value;
			return this.getExprCost(binop.getOp1()) + this.getExprCost(binop.getOp2()) + 10.0;
		} else if (value instanceof JimpleLocal) {
			return 3.0;
		} else if (value instanceof JInstanceFieldRef) {
			JInstanceFieldRef ref = (JInstanceFieldRef)value;
			return this.getExprCost(ref.getBase()) + 50.0;
		} else if (value instanceof JInstanceOfExpr) {
			JInstanceOfExpr instance = (JInstanceOfExpr)value;
			return 30.0 + this.getExprCost(instance.getOp());
		} else if (value instanceof InvokeExpr) {
			InvokeExpr invoke = (InvokeExpr)value;
			double cost = 0.0;
			for (Value param : (List<Value>)invoke.getArgs()) {
				cost += this.getExprCost(param);
			}
			if (invoke instanceof InstanceInvokeExpr)
				cost += this.getExprCost(((InstanceInvokeExpr)invoke).getBase());
			cost += 100.0;
			return cost;
		} else if (value instanceof JLengthExpr) {
			JLengthExpr length = (JLengthExpr)value;
			return this.getExprCost(length.getOp()) + 20.0;
		} else if (value instanceof JNewArrayExpr) {
			JNewArrayExpr newarray = (JNewArrayExpr)value;
			return this.getExprCost(newarray.getSize()) + 100.0;
		} else if (value instanceof JNewExpr) {
			//JNewExpr newexpr = (JNewExpr)value;
			return 100.0;
		} else if (value instanceof JNewMultiArrayExpr) {
			JNewMultiArrayExpr multi = (JNewMultiArrayExpr)value;
			double cost = 0.0;
			for (Value size : (List<Value>)multi.getSizes()) {
				cost += this.getExprCost(size);
			}
			return cost + 100.0;
		} else if (value instanceof Constant) {
			return 1.0;
		} else if (value instanceof IdentityRef) {
			return 3.0;
		} else if (value instanceof StaticFieldRef) {
			return 50.0;
		} else if (value instanceof JNegExpr) {
			JNegExpr neg = (JNegExpr)value;
			return this.getExprCost(neg.getOp()) + 5.0;
		} else {
			throw new RuntimeException("Mike didn't support: " + value.getClass());
		}
	}

	
	public void update(SootMethod inliner) {
		Pair<Integer,Integer> methodPair = this.getMethodPair(inliner);
		Pair<Integer,Integer> classPair = this.getClassPair(inliner.getDeclaringClass());
		
		int increment = inliner.retrieveActiveBody().getUnits().size() - methodPair.getFirst();
		methodPair = new Pair<Integer,Integer>(methodPair.getFirst(), methodPair.getSecond() + increment);
		this.methodIncreaseMap.put(inliner.getSignature(), methodPair);
		
		classPair = new Pair<Integer,Integer>(classPair.getFirst(), classPair.getSecond() + increment);
		this.classIncreaseMap.put(inliner.getDeclaringClass().getName(), classPair);
	}
	
}
