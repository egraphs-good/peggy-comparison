package peggy.represent.java;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.represent.BijectionMapping;
import soot.Body;
import soot.PrimType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefBlockGraph;
import util.Function;
import util.VariaticFunction;
import util.graph.AbstractGraph;
import eqsat.CFG;
import eqsat.CFGTranslator;

/**
 * This is the CFG type for converting Java CFGs into PEGs.
 * Does not deal with exceptions!
 */
public class ExpressionCFG2 
extends AbstractGraph<ExpressionCFG2, ExpressionCFGBlock2> 
implements CFG<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable,JavaLabel,JavaParameter,JavaReturn> {
	private final Body methodBody;
	private ExpressionCFGBlock2 start, end;
	private final List<ExpressionCFGBlock2> blocks;
	private final Map<ExpressionCFGBlock2,List<ExpressionCFGBlock2>> block2children;
	private final Set<JavaVariable> variables;
	protected final BijectionMapping<JimpleLocal,JavaVariable> jimple2var;
	private final MethodJavaLabel label;
	private final JavaLabelOpAmbassador ambassador;

	public ExpressionCFG2(
			SootMethod method,
			JavaLabelOpAmbassador _ambassador) {
		this.ambassador = _ambassador;
		this.methodBody = (Body)method.retrieveActiveBody().clone();
		this.jimple2var = new BijectionMapping<JimpleLocal,JavaVariable>();
		this.variables = new HashSet<JavaVariable>();
		
		this.normalizeInstructions(this.methodBody, method);
		
		this.label = new MethodJavaLabel(
				method.getDeclaringClass().getName(),
				method.getName(),
				method.getReturnType(),
				(List<Type>)method.getParameterTypes());

		BriefBlockGraph bbg = new BriefBlockGraph(this.methodBody);
		
		this.blocks = new ArrayList<ExpressionCFGBlock2>(bbg.getBlocks().size()+10);
		this.block2children = new HashMap<ExpressionCFGBlock2,List<ExpressionCFGBlock2>>();

		this.buildBlocks(bbg);

		this.variables.clear();
		this.variables.add(JavaVariable.SIGMA);
		this.variables.add(JavaVariable.RETURN);
		for (ExpressionCFGBlock2 block : this.blocks) {
			this.variables.addAll(block.variables());
		}
	}
	
	public JavaLabelOpAmbassador getOpAmbassador() {return this.ambassador;}
	
	public JavaVariable getReturnVariable(JavaReturn arr) {
		return arr.getVariableVersion();
	}
	
	public <E> CFGTranslator<ExpressionCFGBlock2,JavaVariable,E> getTranslator(
			Function<JavaParameter,E> paramConverter,
			VariaticFunction<JavaLabel,E,E> converter,
			Collection<? super E> known){
		return new ExpressionCFGTranslator2<E>(this, paramConverter, converter, known); 
	}
	
	public Collection<? extends JavaVariable> getVariables() {return this.variables;}
	
	public JavaParameter getParameter(JavaVariable var) {
		if (var.isArgument())
			return new ArgumentJavaParameter(var.getArgumentSelf());
		else if (var.isSigma())
			return JavaParameter.SIGMA;
		else if (var.isThis())
			return new ThisJavaParameter(var.getThisSelf());
		else if (var.isReturn() || var.isDummy())
			return null;
		else
			throw new IllegalArgumentException("Mike forgot " + var);
	}
	
	public MethodJavaLabel getMethodLabel() {return this.label;}
	
	public Collection<? extends JavaReturn> getReturns() {
		return Arrays.asList(JavaReturn.VALUE, JavaReturn.SIGMA);
	}
	public Collection<? extends ExpressionCFGBlock2> getVertices() {return this.blocks;}
	public ExpressionCFG2 getSelf() {return this;}
	public ExpressionCFGBlock2 getStart() {return this.start;}
	public ExpressionCFGBlock2 getEnd() {return this.end;}
	
	
	private void buildBlocks(BriefBlockGraph bbg) {
		Map<soot.toolkits.graph.Block,ExpressionCFGBlock2> blockMap = 
			new HashMap<soot.toolkits.graph.Block,ExpressionCFGBlock2>();
		for (Iterator<soot.toolkits.graph.Block> blockiter=bbg.getBlocks().iterator(); blockiter.hasNext(); ) {
			soot.toolkits.graph.Block block = blockiter.next();
			ExpressionCFGBlock2 newblock = new ExpressionCFGBlock2(this);
			this.blocks.add(newblock);
			this.block2children.put(newblock, new ArrayList<ExpressionCFGBlock2>());
			blockMap.put(block, newblock);
		}
		
		this.end = new ExpressionCFGBlock2(this);
		this.blocks.add(this.end);
		this.block2children.put(this.end, new ArrayList<ExpressionCFGBlock2>());
		
		this.start = blockMap.get((soot.toolkits.graph.Block)bbg.getHeads().get(0));
		if (this.start == null)
			throw new RuntimeException("No start block!");
		
		for (soot.toolkits.graph.Block block : blockMap.keySet()) {
			ExpressionCFGBlock2 newblock = blockMap.get(block);
			for (soot.toolkits.graph.Block succ : (List<soot.toolkits.graph.Block>)bbg.getSuccsOf(block)) {
				this.block2children.get(newblock).add(blockMap.get(succ));
			}
			
			// check for wacky if stmts
			if (block.getTail() instanceof JIfStmt) {
				if (this.block2children.get(newblock).size() == 2) {
					ExpressionCFGBlock2 second = this.block2children.get(newblock).remove(1);
					ExpressionCFGBlock2 first = this.block2children.get(newblock).remove(0);
					this.block2children.get(newblock).add(second);
					this.block2children.get(newblock).add(first);
				} else if (this.block2children.get(newblock).size() == 1) {
					// soot actually thinks removing duplicates is useful
					this.block2children.get(newblock).add(
							this.block2children.get(newblock).get(0));
				} else 
					throw new IllegalArgumentException("Branch block has weird number of successors: " + 
							this.block2children.get(newblock).size());
			}
			
			translate(block, blockMap);
		}

		// add branches from tails to end
		for (int i = 0; i < blocks.size(); i++) {
			final ExpressionCFGBlock2 block = blocks.get(i);
			if (block.equals(end)) continue;
			if (block2children.get(block).size() == 0) {
				// add edge to end
				block2children.get(block).add(this.end);
			}
		}
		
//		for (soot.toolkits.graph.Block tail : (List<soot.toolkits.graph.Block>)bbg.getTails()) {
//			this.block2children.get(blockMap.get(tail)).add(this.end);
//		}
	}
	
	protected JavaVariable findOrMakeVar(JimpleLocal local) {
		if (jimple2var.hasA(local)) {
			return jimple2var.getByA(local);
		} else {
			JavaVariable var = JavaVariable.getDummyVariable();
			this.variables.add(var);
			jimple2var.put(local, var);
			return var;
		}		
	}
	
	private boolean needsRhoValue(Value v) {
		return v instanceof InvokeExpr ||
         v instanceof JInstanceFieldRef ||
         v instanceof StaticFieldRef ||
         ((v instanceof JCastExpr) && !(((JCastExpr)v).getCastType() instanceof PrimType)) ||
         v instanceof JLengthExpr ||
         v instanceof JInstanceOfExpr ||
         v instanceof JArrayRef ||
//         v instanceof JDivExpr ||
//         v instanceof JRemExpr ||
         v instanceof ClassConstant ||
         v instanceof JNewArrayExpr ||
         v instanceof JNewMultiArrayExpr ||
         v instanceof JNewExpr;
	}
	
	private void translate(
			soot.toolkits.graph.Block block,
			Map<soot.toolkits.graph.Block,ExpressionCFGBlock2> blockMap) {
		ExpressionCFGBlock2 newblock = blockMap.get(block);
		boolean seenLast = false;
		
		for (Iterator<Unit> unititer=block.iterator(); unititer.hasNext(); ) {
			if (seenLast)
				throw new RuntimeException("More instructions after terminator!");
			
			Unit unit = unititer.next();
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt)unit;
				if (stmt.getLeftOp() instanceof JimpleLocal) {
					// make eval
					JimpleLocal local = (JimpleLocal)stmt.getLeftOp();
					JavaVariable var = findOrMakeVar(local);
					EvalCFGInstruction eval = new EvalCFGInstruction(stmt.getRightOp());
					newblock.addInstruction(eval);
					newblock.putAssignment(eval, var);

					/* if needs rho_value, make it
					 * V = expr
					 * V = getValue(V)
					 */
					Value rhs = stmt.getRightOp();
					if (needsRhoValue(rhs)) {
						GetValueExpr gv = new GetValueExpr(local);
						EvalCFGInstruction evalrho = new EvalCFGInstruction(gv);
						newblock.addInstruction(evalrho);
						newblock.putAssignment(evalrho, var);
					}
				} else if (stmt.getLeftOp() instanceof StaticFieldRef) {
					StaticFieldRef ref = (StaticFieldRef)stmt.getLeftOp();
					SetstaticfieldExpr expr = new SetstaticfieldExpr(ref.getFieldRef(), stmt.getRightOp());
					EvalCFGInstruction eval = new EvalCFGInstruction(expr);
					newblock.addInstruction(eval);
				} else if (stmt.getLeftOp() instanceof JInstanceFieldRef) {
					JInstanceFieldRef ref = (JInstanceFieldRef)stmt.getLeftOp();
					SetfieldExpr expr = new SetfieldExpr(ref.getBase(), ref.getFieldRef(), stmt.getRightOp());
					EvalCFGInstruction eval = new EvalCFGInstruction(expr);
					newblock.addInstruction(eval);
				} else if (stmt.getLeftOp() instanceof JArrayRef) {
					JArrayRef ref = (JArrayRef)stmt.getLeftOp();
					SetarrayExpr expr = new SetarrayExpr(ref.getBase(), ref.getIndex(), stmt.getRightOp());
					EvalCFGInstruction eval = new EvalCFGInstruction(expr);
					newblock.addInstruction(eval);
				} else
					throw new IllegalArgumentException("Mike forgot " + stmt);
			} 
			else if (unit instanceof JEnterMonitorStmt) {
				Value target = ((JEnterMonitorStmt)unit).getOp();
				MonitorCFGInstruction monitor = new MonitorCFGInstruction(target, true);
				newblock.addInstruction(monitor);
			} 
			else if (unit instanceof JExitMonitorStmt) {
				Value target = ((JExitMonitorStmt)unit).getOp();
				MonitorCFGInstruction monitor = new MonitorCFGInstruction(target, false);
				newblock.addInstruction(monitor);
			} 
			else if (unit instanceof JGotoStmt) {
				// skip
			}
			else if (unit instanceof JIdentityStmt) {
				JIdentityStmt stmt = (JIdentityStmt)unit;
				JimpleLocal lhs = (JimpleLocal)stmt.getLeftOp();
				if (stmt.getRightOp() instanceof ParameterRef) {
					//ParameterRef ref = (ParameterRef)stmt.getRightOp();
					EvalCFGInstruction eval = new EvalCFGInstruction(stmt.getRightOp());
					newblock.addInstruction(eval);
					newblock.putAssignment(eval, findOrMakeVar(lhs));
				} else if (stmt.getRightOp() instanceof ThisRef) {
					//ThisRef ref = (ThisRef)stmt.getRightOp();
					EvalCFGInstruction eval = new EvalCFGInstruction(stmt.getRightOp());
					newblock.addInstruction(eval);
					newblock.putAssignment(eval, findOrMakeVar(lhs));
				} else {
					throw new IllegalArgumentException("Mike forgot " + stmt.getRightOp());
				}
			}
			else if (unit instanceof JIfStmt) {
				Value condition = ((JIfStmt)unit).getCondition();
				IfCFGInstruction ifcfg = new IfCFGInstruction(condition);
				newblock.addInstruction(ifcfg);
				seenLast = true;
			} 
			else if (unit instanceof JInvokeStmt) {
				InvokeExpr invoke = ((JInvokeStmt)unit).getInvokeExpr();
				EvalCFGInstruction eval = new EvalCFGInstruction(invoke);
				newblock.addInstruction(eval);
			} 
			else if (unit instanceof JNopStmt) {
				// skip
			}
			else if (unit instanceof JReturnStmt) {
				Value value = ((JReturnStmt)unit).getOp();
				EvalCFGInstruction eval = new EvalCFGInstruction(value);
				newblock.addInstruction(eval);
				newblock.putAssignment(eval, JavaVariable.RETURN);
				if (block2children.get(newblock).size() > 0)
					throw new IllegalArgumentException("Return block has successors!");
				block2children.get(newblock).add(this.end);
				seenLast = true;
			}
			else if (unit instanceof JReturnVoidStmt) {
				EvalCFGInstruction eval = new EvalCFGInstruction(VoidExpr.INSTANCE);
				newblock.addInstruction(eval);
				newblock.putAssignment(eval, JavaVariable.RETURN);
				if (block2children.get(newblock).size() > 0)
					throw new IllegalArgumentException("Return block has successors!");
				block2children.get(newblock).add(this.end);
				seenLast = true;
			}
			else
				throw new IllegalArgumentException("Mike forgot " + unit);
		}
	}
	
	public List<ExpressionCFGBlock2> getChildren(ExpressionCFGBlock2 block) {
		return block2children.get(block);
	}
	
	/** This method cleans up the code a bit before it is divided into 
	 *  blocks. 
	 *  It replaces all switch statements with a series of if's, and 
	 *  makes it so that there is one final return statement 
	 *  (all other returns assign to a $$returnvalue variable and goto the single return).
	 * 
	 *  For void methods, we still have a return value but it is the special value 'void'.
	 *  We make the first instruction of the method be "$$returnvalue = void".
	 */
	private void normalizeInstructions(Body body, SootMethod method) {
		ArrayList<Unit> switches = new ArrayList<Unit>(10);
		for (Iterator<Unit> unititer=body.getUnits().iterator(); unititer.hasNext(); ) {
			Unit unit = unititer.next();
			if (unit instanceof JLookupSwitchStmt || unit instanceof JTableSwitchStmt)
				switches.add(unit);
		}

		// convert the switch statements to if's
		for (Unit unit : switches) {
			List<Unit> targets = null;
			List<Value> values = new ArrayList<Value>(10); 
			Value key = null;
			Unit defaulttarget = null;

			if (unit instanceof JLookupSwitchStmt) {
				JLookupSwitchStmt lookup = (JLookupSwitchStmt)unit;
				targets = lookup.getTargets();
				for (Iterator<Value> iter=lookup.getLookupValues().iterator();iter.hasNext();)
					values.add(iter.next());
				key = lookup.getKey();
				defaulttarget = lookup.getDefaultTarget();
			} else if (unit instanceof JTableSwitchStmt) {
				JTableSwitchStmt table = (JTableSwitchStmt)unit;
				targets = table.getTargets();
				for (int i=table.getLowIndex();i<=table.getHighIndex();i++)
					values.add(IntConstant.v(i));
				key = table.getKey();
				defaulttarget = table.getDefaultTarget();
			}

			Unit laststmt = unit;
			Unit firstif = null;

			// go through each case label and replace it with an IfStmt
			for (int i=0;i<targets.size();i++) {
				Unit target = (Unit)targets.get(i);
				Value value = (Value)values.get(i);
				JIfStmt ifstmt = new JIfStmt(new JEqExpr(key, value), target);
				if (firstif==null)
					firstif = ifstmt;

				body.getUnits().insertAfter(ifstmt, laststmt);
				laststmt = ifstmt;
			}

			// add the final goto stmt
			JGotoStmt gotostmt = new JGotoStmt(defaulttarget);
			body.getUnits().insertAfter(gotostmt, laststmt);

			// replace the switch with the first ifstmt
			redirectBranches(body, unit, firstif);
			body.getUnits().remove(unit);
		}
	}



	private void redirectBranches(Body body, Unit oldtarget, Unit newstart){
		for (Iterator<Unit> iter=body.getUnits().iterator();iter.hasNext();){
			Unit unit = iter.next();
			SootUtils.redirectBranch(unit, oldtarget, newstart);
		}
	}
	
	public void toDot(PrintStream out) {
		out.println("digraph {");
		out.println("   ordering=out;");
		for (ExpressionCFGBlock2 block : blocks) {
			int myhash = block.hashCode();
			StringBuilder label = new StringBuilder(100);
			for (int i = 0; i < block.getNumInstructions(); i++) {
				CFGInstruction inst = block.getInstruction(i);
				if (block.hasAssignment(inst)) {
					label.append(block.getAssignment(inst).toString() + " = ");
				}
				label.append(inst.toString());
				label.append("\\n");
			}
			out.println("   " + myhash + " [shape=rect,label=\"" + label + "\"];");
			for (ExpressionCFGBlock2 child : block2children.get(block)) {
				out.println("   " + myhash + " -> " + child.hashCode() + " ;");
			}
		}
		out.println("}");
	}
}
