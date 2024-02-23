package peggy.represent.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.PrimType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JimpleLocal;
import util.graph.AbstractVertex;

/**
 * This is the block type for ExpressionCFG2's.
 */
public class ExpressionCFGBlock2 
extends AbstractVertex<ExpressionCFG2,ExpressionCFGBlock2> 
implements eqsat.Block<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable,JavaLabel> {
	private final ExpressionCFG2 graph;
	private final List<CFGInstruction> statements;
	protected final Map<CFGInstruction,JavaVariable> assignment;
	
	public ExpressionCFGBlock2(ExpressionCFG2 _graph) {
		this.graph = _graph;
		this.statements = new ArrayList<CFGInstruction>();
		this.assignment = new HashMap<CFGInstruction,JavaVariable>();
	}
	
	public List<? extends ExpressionCFGBlock2> getChildren() {
		return this.graph.getChildren(this);
	}
	
	public boolean hasAssignment(CFGInstruction cfg) {
		return this.assignment.containsKey(cfg);
	}
	public JavaVariable getAssignment(CFGInstruction cfg) {
		return this.assignment.get(cfg);
	}
	public JavaVariable removeAssignment(CFGInstruction cfg) {
		return this.assignment.remove(cfg);
	}
	public void putAssignment(CFGInstruction cfg, JavaVariable var) {
		this.assignment.put(cfg, var);
	}

	public CFGInstruction getInstruction(int index) {
		return this.statements.get(index);
	}
	public void addInstruction(CFGInstruction cfg) {
		this.statements.add(cfg);
	}
	public void addInstruction(int index, CFGInstruction cfg) {
		this.statements.add(index, cfg);
	}
	public CFGInstruction removeInstruction(int index) {
		return this.statements.remove(index);
	}
	public int getNumInstructions() {return this.statements.size();}
	
	public ExpressionCFGBlock2 getChild(int child) {
		return getChildren().get(child);
	}
	public boolean isStart() {return graph.getStart()==this;}
	public boolean isEnd() {return graph.getEnd()==this;}
	public int size() {return statements.size();}

	public boolean modifies(JavaVariable variable) {
		for (CFGInstruction cfg : statements) {
			if (modifiesVar(variable, cfg))
				return true;
		}
		return false;
	}
	private boolean modifiesVar(JavaVariable variable, CFGInstruction cfg) {
		if (cfg.isIf()) {
			return false;
		} else if (cfg.isEval()) {
			if (this.assignment.containsKey(cfg) &&
				this.assignment.get(cfg).equals(variable)) {
				return true;
			} else if (variable.isSigma()) {
				// check for sigma-altering ops
				Value value = cfg.getEvalSelf().getValue();
				if (value instanceof JCastExpr) {
					JCastExpr cast = (JCastExpr)value;
					return !(cast.getCastType() instanceof PrimType);
				} else {
					return (value instanceof InvokeExpr) ||
						(value instanceof JNewArrayExpr) ||
						(value instanceof JNewMultiArrayExpr) ||
						(value instanceof JNewExpr) ||
						(value instanceof JInstanceFieldRef) ||
						(value instanceof JInstanceOfExpr) ||
						(value instanceof JLengthExpr) ||
						(value instanceof JArrayRef) ||
						(value instanceof StaticFieldRef) ||
						(value instanceof SetfieldExpr) ||
						(value instanceof SetarrayExpr) ||
						(value instanceof SetstaticfieldExpr);
				}
			}
		} else if (cfg.isMonitor()) {
			return variable.isSigma();
		} else {
			throw new IllegalArgumentException("Mike forgot to implement: " + cfg);
		}
		
		return false;
	}

	

	
	/** Returns the set of variables referenced by this block.
	 */
	public Set<JavaVariable> variables() {
		Set<JavaVariable> result = new HashSet<JavaVariable>();
		for (CFGInstruction cfg : statements){
			variables(cfg, result);
		}
		return result;
	}
	private void variables(CFGInstruction cfg, Set<JavaVariable> variables) {
		if (cfg.isIf()) {
			for (Iterator<ValueBox> boxiter=cfg.getIfSelf().getCondition().getUseBoxes().iterator(); boxiter.hasNext(); ) {
				ValueBox box = boxiter.next();
				if (box.getValue() instanceof JimpleLocal) {
					variables.add(graph.findOrMakeVar((JimpleLocal)box.getValue()));
				}
			}
		} 
		else if (cfg.isEval()) {
			Value value = cfg.getEvalSelf().getValue();
			if ((value instanceof InvokeExpr) ||
				(value instanceof JNewArrayExpr) ||
				(value instanceof JNewMultiArrayExpr) ||
				(value instanceof JNewExpr)) {
				variables.add(JavaVariable.SIGMA);
			}
			if ((value instanceof SetfieldExpr) ||
				(value instanceof SetarrayExpr) ||
				(value instanceof SetstaticfieldExpr))
				variables.add(JavaVariable.SIGMA);
			if (this.assignment.containsKey(cfg))
				variables.add(this.assignment.get(cfg));
			for (Iterator<ValueBox> boxiter=value.getUseBoxes().iterator(); boxiter.hasNext(); ) {
				ValueBox box = boxiter.next();
				if (box.getValue() instanceof JimpleLocal) {
					variables.add(graph.findOrMakeVar((JimpleLocal)box.getValue()));
				}
			}
		} 
		else if (cfg.isMonitor()) {
			variables.add(JavaVariable.SIGMA);
		}
		else 
			throw new IllegalArgumentException("Mike forgot " + cfg);
	}
	
	/////// from Vertex ///////

	public ExpressionCFGBlock2 getSelf() {return this;}
	public ExpressionCFG2 getGraph() {return graph;}
}
