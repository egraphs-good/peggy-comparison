package peggy.revert.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.represent.StickyPredicate;
import peggy.represent.java.ArgumentJavaVariable;
import peggy.represent.java.BasicJavaLabel;
import peggy.represent.java.FieldJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelStickyPredicate;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.ReferenceResolver;
import peggy.represent.java.SimpleJavaLabel;
import peggy.represent.java.SootUtils;
import peggy.represent.java.ThisJavaVariable;
import peggy.represent.java.TypeJavaLabel;
import peggy.revert.BlockVerticesIterator;
import peggy.revert.Item;
import peggy.revert.MiniPEG.Vertex;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.IntType;
import soot.LongType;
import soot.PatchingChain;
import soot.RefType;
import soot.ShortType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.ClassConstant;
import soot.jimple.ConditionExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JAndExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JCmpExpr;
import soot.jimple.internal.JCmpgExpr;
import soot.jimple.internal.JCmplExpr;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNegExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JOrExpr;
import soot.jimple.internal.JRemExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JShlExpr;
import soot.jimple.internal.JShrExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JUshrExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JXorExpr;
import soot.jimple.internal.JimpleLocal;
import util.AbstractPattern;
import util.Function;
import util.Pattern;
import util.pair.Pair;
import eqsat.BasicOp;

/**
 * This class converts a JavaPEGCFG back to the instructions inside
 * a SootMethod.
 */
public class JavaPEGCFGEncoder {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("JavaPEGCFGEncoder: " + message);
	}
	
	private static class LabelOperatorPattern extends AbstractPattern<Vertex<Item<JavaLabel,JavaParameter,Object>>> {
		private final JavaLabel label;
		public LabelOperatorPattern(JavaLabel _label) {
			this.label = _label;
		}
		public boolean matches(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
			Item<JavaLabel,JavaParameter,Object> item = vertex.getLabel();
			return item.isLabel() && item.getLabel().equalsLabel(this.label);
		}
	}
	
	//////////////////////////////////////////////////

	private int localCounter = 0;
	private final JavaPEGCFG cfg;
	private final SootMethod body;
	private final ReferenceResolver resolver;
	
	public JavaPEGCFGEncoder(
			JavaPEGCFG _cfg, 
			SootMethod _body, 
			ReferenceResolver _resolver) {
		this.cfg = _cfg;
		this.body = _body;
		this.resolver = _resolver;
	}

	/**
	 * This function encodes the input CFG as an Java FunctionBody.
	 * This method will modify the given CFG!
	 * The function body is returned.
	 */
	public SootMethod encode() {
		debug("fixSticky");
		this.fixSticky();

		debug("removeNegates");
		this.removeNegates();

		// do this before normalizeBranches!
		debug("removeDeadAssignments");
		this.removeDeadAssignments();
		
		debug("normalizeBranches");
		this.normalizeBranches();

		debug("propagateTF");
		this.propagateTF();
		
		for (boolean progress=true; progress; ) {
			progress = false;
			debug("removeDeadAssignments");
			progress |= this.removeDeadAssignments();
			
			debug("removeEmptyBlocks");
			progress |= this.removeEmptyBlocks();
			
			debug("simplifyBranches");
			this.simplifyBranches();
		}
		
		debug("removeINJR");
		this.removeINJR();
		
		debug("makeReturnLast");
		this.makeReturnLast();

//		debug("removeDuplicateBlocks");
//		this.removeDuplicateBlocks();
		
//		System.err.println("*** Begin middle cfg");
//		System.err.println(this.cfg);
//		System.err.println("*** End middle cfg");
		
		debug("buildBlocks");
		final List<SootBlock> newblocks = new ArrayList<SootBlock>();
		final SootBlock startBlock = this.buildBlocks(newblocks);
		linearizeBlocks(startBlock, newblocks);
		bubbleIdentities();
		
		final Body methodBody = this.body.retrieveActiveBody();
		
		debug("soot passes");
		////// now do some soot optimizations
        // dead assignment elimination
		soot.jimple.toolkits.scalar.DeadAssignmentEliminator.v().transform(methodBody);
		// copy propagation
		soot.jimple.toolkits.scalar.CopyPropagator.v().transform(methodBody);
        // dead assignment elimination
		soot.jimple.toolkits.scalar.DeadAssignmentEliminator.v().transform(methodBody);
        // some unconditional branch folding
		soot.jimple.toolkits.scalar.UnconditionalBranchFolder.v().transform(methodBody);
		
		debug("checkAssignments");
		this.checkAssignments();
		
		return this.body;
	}
	
	
	// moves T/F forward, specializing as it goes
	private void propagateTF() {
		for (boolean progress=true; progress; ) {
			progress = false;
			
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				for (Object assigned : block.getAssignedVars()) {
					Vertex<Item<JavaLabel,JavaParameter,Object>> vertex = 
						block.getAssignment(assigned);
					if (vertex.getLabel().isLabel() && vertex.getLabel().getLabel().isTrue()) {
						// true
						boolean[] changed = {false};
						Map<JavaPEGCFGBlock,JavaPEGCFGBlock> cache = 
							new HashMap<JavaPEGCFGBlock,JavaPEGCFGBlock>();
						for (int i = 0; i < block.getNumSuccs(); i++) {
							JavaPEGCFGBlock newchild = specializeBlocks(
									block.getSucc(i), 
									assigned, 
									true, 
									cache,
									changed);
							block.removeSucc(i);
							block.insertSucc(i, newchild);
						}
						progress |= changed[0];
					} else if (vertex.getLabel().isLabel() && vertex.getLabel().getLabel().isFalse()) {
						// false
						boolean[] changed = {false};
						Map<JavaPEGCFGBlock,JavaPEGCFGBlock> cache = 
							new HashMap<JavaPEGCFGBlock,JavaPEGCFGBlock>();
						for (int i = 0; i < block.getNumSuccs(); i++) {
							JavaPEGCFGBlock newchild = specializeBlocks(
									block.getSucc(i), 
									assigned, 
									false, 
									cache,
									changed);
							block.removeSucc(i);
							block.insertSucc(i, newchild);
						}
						progress |= changed[0];
					}
				}
			}
		}
	}
	
	
	private JavaPEGCFGBlock specializeBlocks(
			JavaPEGCFGBlock block,
			Object assigned,
			boolean TF,
			Map<JavaPEGCFGBlock,JavaPEGCFGBlock> cache,
			boolean changed[]) {
		if (cache.containsKey(block))
			return cache.get(block);

		if (block.getAssignedVars().contains(assigned)) {
			// can still propagate T/F in nodes
			changed[0] |= specializeVariable(block, assigned, TF);
			return block;
		}

		JavaPEGCFGBlock result;
		if (findLastAssignments(assigned, block).size() > 1) {
			// specialize
			result = duplicateBlock(block);
			cache.put(block, result);
			cache.put(result, result);
			changed[0] = true;
		} else {
			// keep this block
			result = block;
			cache.put(block, result);
		}

		changed[0] |= specializeVariable(result, assigned, TF);
		
		if (findUses(result, assigned).size() == 0)
			return result;
		
		// now do child blocks
		for (int i = 0; i < result.getNumSuccs(); i++) {
			JavaPEGCFGBlock newchild = specializeBlocks(
					result.getSucc(i),
					assigned,
					TF, 
					cache,
					changed);
			result.removeSucc(i);
			result.insertSucc(i, newchild);
		}

		return result;
	}
	
	private boolean specializeVariable(
			JavaPEGCFGBlock block, Object assigned, boolean TF) {
		boolean any = false;
		
		// propagate T/F value
		Item<JavaLabel,JavaParameter,Object> item = 
			Item.<JavaLabel,JavaParameter,Object>getLabel(
					new BasicJavaLabel((TF ? BasicOp.True : BasicOp.False)));
		Vertex<Item<JavaLabel,JavaParameter,Object>> replacement = null;
		for (Vertex<Item<JavaLabel,JavaParameter,Object>> vertex : block.getMiniPEG().getVertices()) {
			for (int i = 0; i < vertex.getChildCount(); i++) {
				Vertex<Item<JavaLabel,JavaParameter,Object>> child = vertex.getChild(i);
				if (child.getLabel().isVariable() && 
					child.getLabel().getVariable().equals(assigned)) {
					// reset
					if (replacement == null)
						replacement = block.getMiniPEG().getVertex(item);
					vertex.setChild(i, replacement);
					any = true;
				}
			}
		}
		for (Object v : block.getAssignedVars()) {
			Vertex<Item<JavaLabel,JavaParameter,Object>> vertex = block.getAssignment(v);
			if (vertex.getLabel().isVariable() &&
				vertex.getLabel().getVariable().equals(assigned)) {
				// reset output
				if (replacement == null)
					replacement = block.getMiniPEG().getVertex(item);
				block.setAssignment(v, replacement);
				any = true;
			}
		}
		if (block.getBranchCondition() != null &&
			block.getBranchCondition().getLabel().isVariable() &&
			block.getBranchCondition().getLabel().getVariable().equals(assigned)) {
			// reset bc
			block.setBranchCondition(null);
			block.removeSucc(TF ? 1 : 0);
			any = true;
		}
	
		return any;
	}
	
	
	class State {
		final JavaPEGCFGBlock block;
		final State prev;
		final Set<Object> assigned = new HashSet<Object>();
		// these do not include block's assignments
		State(JavaPEGCFGBlock _block, State _prev) {
			this.block = _block;
			this.prev = _prev;
		}
		public boolean hasBlock(JavaPEGCFGBlock b) {
			for (State s = this; s != null; s = s.prev) {
				if (s.block.equals(b))
					return true;
			}
			return false;
		}
	}
	private void checkAssignments() {
		LinkedList<State> worklist = new LinkedList<State>();
		worklist.add(new State(this.cfg.getStartBlock(), null));
		
		while (worklist.size() > 0) {
			State next = worklist.removeFirst();
			Set<Vertex<Item<JavaLabel,JavaParameter,Object>>> vertices = 
				new HashSet<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
			for (Object assigned : next.block.getAssignedVars()) {
				vertices.addAll(next.block.getAssignment(assigned).getDescendents());
			}
			for (Vertex<Item<JavaLabel,JavaParameter,Object>> v : vertices) {
				if (v.getLabel().isVariable() &&
					!next.assigned.contains(v.getLabel().getVariable()))
					throw new RuntimeException("Undefined var: " + v.getLabel().getVariable().hashCode());
			}
			for (int i = 0; i < next.block.getNumSuccs(); i++) {
				if (next.hasBlock(next.block.getSucc(i)))
					continue;
				State newstate = new State(next.block.getSucc(i), next);
				newstate.assigned.addAll(next.assigned);
				newstate.assigned.addAll(next.block.getAssignedVars());
				worklist.addLast(newstate);
			}
		}
	}
	
	
	
	/**
	 * Removes blocks with no assignments and no branch condition.
	 */
	private boolean removeEmptyBlocks() {
		boolean any = false;
		for (boolean progress=true; progress; ) {
			progress = false;
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				if (block.getBranchCondition() != null)
					continue;
				if (block.getAssignedVars().size() > 0)
					continue;
				if (block.getNumSuccs() == 0)
					continue;
				
				// block is empty, remove
				JavaPEGCFGBlock succ = block.getSucc(0);
				replaceBlock(block, succ);
				progress = true;
				any = true;
			}
		}
		return any;
	}
	
	
	
	private boolean isRelationalOperator(JavaLabel label) {
		if (label.isBasic() && label.getBasicSelf().getOperator().equals(BasicOp.Equals))
			return true;
		else if (label.isSimple()) {
			switch (label.getSimpleSelf().getOperator()) {
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case LESS_THAN:
			case LESS_THAN_EQUAL:
			case EQUAL:
			case NOT_EQUAL:
				return true;
			default:
				return false;
			}
		}
		else return false;
	}
	
	
	/**
	 * For any Set of a boolean op (other than T/F),
	 * replace it with a branch on that op and new blocks with assignments to T/F.
	 */
	private void normalizeBranches() {
		for (boolean progress = true; progress; ) {
			progress = false;
		
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				for (Object assigned : block.getAssignedVars()) {
					Vertex<Item<JavaLabel,JavaParameter,Object>> vertex = 
						block.getAssignment(assigned);
					if (!vertex.getLabel().isLabel()) continue;
					if (!isRelationalOperator(vertex.getLabel().getLabel())) 
						continue;
					// remove assignment, make a branch
					
					if (block.getBranchCondition() != null &&
						!block.getBranchCondition().equals(vertex)) {
						// move branch to later block
						// remove BC and set a fresh var
						Object newvar = this.cfg.makeNewTemporary();
						block.setAssignment(newvar, block.getBranchCondition());
						block.setBranchCondition(null);
						
						JavaPEGCFGBlock newblock = this.cfg.makeNewBlock();
						newblock.setBranchCondition(
								newblock.getMiniPEG().getVertex(
										Item.<JavaLabel,JavaParameter,Object>getVariable(newvar)));
						newblock.addSucc(block.getSucc(0));
						newblock.addSucc(block.getSucc(1));
						
						block.removeSucc(1);
						block.removeSucc(0);
						block.addSucc(newblock);
					}
					
					// no branch, one successor
					block.setBranchCondition(vertex);
					block.removeAssignment(assigned);
					
					JavaPEGCFGBlock trueBlock = this.cfg.makeNewBlock();
					JavaPEGCFGBlock falseBlock = this.cfg.makeNewBlock();
					
					trueBlock.setAssignment(assigned, 
							trueBlock.getMiniPEG().getVertex(
									Item.<JavaLabel,JavaParameter,Object>getLabel(
											new BasicJavaLabel(BasicOp.True))));
					falseBlock.setAssignment(assigned, 
							falseBlock.getMiniPEG().getVertex(
									Item.<JavaLabel,JavaParameter,Object>getLabel(
											new BasicJavaLabel(BasicOp.False))));
					trueBlock.addSucc(block.getSucc(0));
					falseBlock.addSucc(block.getSucc(0));
					
					block.removeSucc(0);
					block.addSucc(trueBlock);
					block.addSucc(falseBlock);
					progress = true;
					break;
				}
			}
		}		
	}
	
	
	/**
	 * Copies assignments, expressions, and children.
	 */
	private JavaPEGCFGBlock duplicateBlock(JavaPEGCFGBlock block) {
		JavaPEGCFGBlock newblock = this.cfg.makeNewBlock();
		Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>> cache = 
			new HashMap<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>>();
		for (Object assigned : block.getAssignedVars()) {
			newblock.setAssignment(assigned, 
					copyVertex(newblock, block.getAssignment(assigned), cache)); 
		}
		if (block.getBranchCondition() != null) {
			newblock.setBranchCondition(
					copyVertex(newblock, block.getBranchCondition(), cache));
		}
		for (int i = 0; i < block.getNumSuccs(); i++) {
			newblock.addSucc(block.getSucc(i));
		}
		return newblock;
	}
	private Vertex<Item<JavaLabel,JavaParameter,Object>> copyVertex(
			JavaPEGCFGBlock newblock,
			Vertex<Item<JavaLabel,JavaParameter,Object>> vertex,
			Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>> cache) {
		if (cache.containsKey(vertex))
			return cache.get(vertex);
		
		if (vertex.getLabel().isLabel()) {
			List<Vertex<Item<JavaLabel,JavaParameter,Object>>> children = 
				new ArrayList<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
			for (int i = 0; i < vertex.getChildCount(); i++) {
				children.add(copyVertex(newblock, vertex.getChild(i), cache));
			}
			Vertex<Item<JavaLabel,JavaParameter,Object>> result = 
				newblock.getMiniPEG().getVertex(vertex.getLabel(), children);
			cache.put(vertex, result);
			return result;
		} else {
			Vertex<Item<JavaLabel,JavaParameter,Object>> result = 
				newblock.getMiniPEG().getVertex(vertex.getLabel());
			cache.put(vertex, result);
			return result;
		}
	}
	
	
	
	// Works over set of reachable blocks.
	private void replaceBlock(JavaPEGCFGBlock oldB, JavaPEGCFGBlock newB) {
		Set<JavaPEGCFGBlock> seen = new HashSet<JavaPEGCFGBlock>();
		LinkedList<JavaPEGCFGBlock> worklist = new LinkedList<JavaPEGCFGBlock>();
		
		if (this.cfg.getStartBlock().equals(oldB)) {
			worklist.add(newB);
			this.cfg.setStartBlock(newB);
		} else {
			worklist.add(this.cfg.getStartBlock());
		}
		
		while (worklist.size() > 0) {
			JavaPEGCFGBlock next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			// remove all children then re-add them
			final LinkedList<JavaPEGCFGBlock> children = 
				new LinkedList<JavaPEGCFGBlock>();
			for (int i = next.getNumSuccs()-1; i>=0; i--) {
				children.addFirst(next.getSucc(i));
				next.removeSucc(i);
			}
			
			for (JavaPEGCFGBlock child : children) {
				if (child.equals(oldB)) {
					next.addSucc(newB);
					worklist.addLast(newB);
				} else {
					next.addSucc(child);
					worklist.addLast(child);
				}
			}
		}
	}
	
	
	
	/**
	 * Returns the set of blocks reachable from the given block where the given
	 * var is read before it is reassigned. 
	 * Does not generally include the given block in the result list.
	 */
	private Set<JavaPEGCFGBlock> findUses(
			JavaPEGCFGBlock block,
			Object var) {
		final Set<JavaPEGCFGBlock> seen = new HashSet<JavaPEGCFGBlock>();
		final LinkedList<JavaPEGCFGBlock> worklist = new LinkedList<JavaPEGCFGBlock>();
		for (JavaPEGCFGBlock succ : block.getSuccs())
			worklist.addLast(succ);

		final Set<JavaPEGCFGBlock> result = new HashSet<JavaPEGCFGBlock>();

		while (worklist.size() > 0) {
			JavaPEGCFGBlock next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			for (JavaIterator iter = new JavaIterator(next); iter.hasNext(); ) {
				Vertex<Item<JavaLabel,JavaParameter,Object>> vertex = iter.next();
				if (vertex.getLabel().isVariable() &&
					vertex.getLabel().getVariable().equals(var)) {
					// found a use, var is live!
					result.add(next);
				}
			}

			// add next blocks if no assignment
			if (!next.getAssignedVars().contains(var)) {
				for (JavaPEGCFGBlock child : next.getSuccs())
					worklist.addLast(child);
			}
		}
		
		return result;
	}
	

	/**
	 * Checks each (non-return) assigned var to see if it has any uses before 
	 * it is reassigned. If not, the assignment is removed.
	 */
	private boolean removeDeadAssignments() {
		boolean any = false;
		for (boolean progress = true; progress; ) {
			progress = false;
		
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				Set<Object> deadvars = new HashSet<Object>();
				for (Object var : block.getAssignedVars()) {
					if (this.cfg.getReturns().contains(var))
						continue;
					if (findUses(block, var).size() == 0)
						deadvars.add(var);
				}

				// remove dead vars
				for (Object var : deadvars) {
					progress = true;
					any = true;
					block.removeAssignment(var);
				}
			}
		}
		return any;
	}
	
	
	private final static Pattern<Vertex<Item<JavaLabel,JavaParameter,Object>>> isNegate = 
		new AbstractPattern<Vertex<Item<JavaLabel,JavaParameter,Object>>>() {
			final BasicJavaLabel negate = new BasicJavaLabel(BasicOp.Negate);
			public boolean matches(
					Vertex<Item<JavaLabel, JavaParameter, Object>> object) {
				Item<JavaLabel, JavaParameter, Object> item = object.getLabel();
				return item.isLabel() && item.getLabel().equals(negate);
			}
		};
		
		
	/**
	 * Replaces all NEGATE ops with short-circuit branches. 
	 */
	private void removeNegates() {
		for (boolean progress=true; progress; ) {
			progress = false;
			for (JavaPEGCFGBlock block : new HashSet<JavaPEGCFGBlock>(this.cfg.getBlocks())) {
				if (removeNegate(block))
					progress = true;
			}
		}
	}
	private boolean removeNegate(JavaPEGCFGBlock block) {
		Set<Vertex<Item<JavaLabel,JavaParameter,Object>>> bops = 
			new HashSet<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
		Set<Vertex<Item<JavaLabel,JavaParameter,Object>>> bopsWithParents = 
			new HashSet<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
		for (JavaIterator iter = new JavaIterator(block); iter.hasNext(); ) {
			Vertex<Item<JavaLabel,JavaParameter,Object>> next = iter.next();
			if (isNegate.matches(next))
				bops.add(next);

			for (int i = 0; i < next.getChildCount(); i++) {
				if (isNegate.matches(next.getChild(i)))
					bopsWithParents.add(next.getChild(i));
			}
		}

		if (bops.size() == 0)
			return false;
		bops.removeAll(bopsWithParents);
		if (bops.size() == 0)
			throw new RuntimeException("No bops without parents!");

		// handle one arbitrary bop
		final Vertex<Item<JavaLabel,JavaParameter,Object>> mybop = 
			bops.iterator().next();

		// remove branch condition, if any
		if (block.getBranchCondition() != null) {
			final Object freshvar = this.cfg.makeNewTemporary();
			block.setAssignment(freshvar, block.getBranchCondition());
			block.setBranchCondition(null);

			JavaPEGCFGBlock newblock = this.cfg.makeNewBlock();
			newblock.setBranchCondition(newblock.getMiniPEG().getVertex(
					Item.<JavaLabel,JavaParameter,Object>getVariable(freshvar)));
			newblock.addSucc(block.getSucc(0));
			newblock.addSucc(block.getSucc(1));

			block.removeSucc(1);
			block.removeSucc(0);
			block.addSucc(newblock);
		}

		// find all vars assigned to mybop
		final Set<Object> assignedVars = new HashSet<Object>();
		for (Object var : block.getAssignedVars()) {
			if (block.getAssignment(var).equals(mybop))
				assignedVars.add(var);
		}

		final Item<JavaLabel,JavaParameter,Object> trueItem = 
			Item.<JavaLabel,JavaParameter,Object>getLabel(new BasicJavaLabel(BasicOp.True));
		final Item<JavaLabel,JavaParameter,Object> falseItem = 
			Item.<JavaLabel,JavaParameter,Object>getLabel(new BasicJavaLabel(BasicOp.False));
		
		{// will be a negate
			// make two new blocks, one for true and one for false,
			// branch on the negate child, setting the original variables
			// in the new blocks
			final JavaPEGCFGBlock trueBlock = this.cfg.makeNewBlock();
			final JavaPEGCFGBlock falseBlock = this.cfg.makeNewBlock();

			block.setBranchCondition(mybop.getChild(0));
			trueBlock.addSucc(block.getSucc(0));
			falseBlock.addSucc(block.getSucc(0));
			block.removeSucc(0);
			block.addSucc(falseBlock);
			block.addSucc(trueBlock);
			// false should be first? since we're negating?

			for (Object var : assignedVars) {
				block.removeAssignment(var);
				trueBlock.setAssignment(var, 
						trueBlock.getMiniPEG().getVertex(trueItem));
				falseBlock.setAssignment(var,
						falseBlock.getMiniPEG().getVertex(falseItem));
			}

		}

		return true;
	}
	

	
	/**
	 * Fixes the branch condition for the given block.
	 * If the branch is T/F then the branch is simplified. 
	 * If the branch block has both children equal,
	 * then the branch is simplified. Otherwise, no change is made. 
	 */
	private boolean simplifyBranches() {
		boolean any = false;
		for (boolean progress = true; progress; ) {
			progress = false;
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				if (block.getBranchCondition() == null)
					continue;
				progress |= simplifyBranch(block);
			}
			any |= progress;
		}
		return any;
	}
	private boolean simplifyBranch(JavaPEGCFGBlock block) {
		final Vertex<Item<JavaLabel,JavaParameter,Object>> bc = 
			block.getBranchCondition();
		if (block.getSucc(0).equals(block.getSucc(1))) {
			// not a real branch!
			block.removeSucc(1);
			block.setBranchCondition(null);
			return true;
		}
		
		if (bc.getLabel().isLabel()) {
			JavaLabel label = bc.getLabel().getLabel();
			if (label.isTrue()) {
				block.removeSucc(1);
				block.setBranchCondition(null);
				return true;
			} else if (label.isFalse()) {
				block.removeSucc(0);
				block.setBranchCondition(null);
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Any JIdentityStmts must come first in the method. 
	 */
	private void bubbleIdentities() {
		final Body methodBody = this.body.retrieveActiveBody();

		Unit thisStmt = null;
		JimpleLocal thisLocal = null;
		if (!this.body.isStatic()) {
			ThisRef thisref = new ThisRef(this.body.getDeclaringClass().getType());
			thisLocal = newLocal(thisref.getType());
			thisStmt = new JIdentityStmt(thisLocal, thisref);
			methodBody.getLocals().add(thisLocal);
		}

		Unit[] paramStmts = new Unit[this.body.getParameterCount()];
		final JimpleLocal[] paramLocals = new JimpleLocal[this.body.getParameterCount()];
		for (int i = 0; i < paramLocals.length; i++) {
			paramLocals[i] = newLocal(this.body.getParameterType(i));
			paramStmts[i] = new JIdentityStmt(paramLocals[i], new ParameterRef(this.body.getParameterType(i), i));
			methodBody.getLocals().add(paramLocals[i]);
		}
		
		for (Unit curr=(Unit)methodBody.getUnits().getFirst(); curr!=null; curr=(Unit)methodBody.getUnits().getSuccOf(curr)) {
			if (curr instanceof JIdentityStmt) {
				JIdentityStmt ident = (JIdentityStmt)curr;
				JAssignStmt stmt;
				if (ident.getRightOp() instanceof ThisRef) {
					stmt = new JAssignStmt(ident.getLeftOp(), thisLocal);
				} else if (ident.getRightOp() instanceof ParameterRef) {
					int i = ((ParameterRef)ident.getRightOp()).getIndex();
					stmt = new JAssignStmt(ident.getLeftOp(), paramLocals[i]);
				} else
					throw new RuntimeException("Invalid identity LHS: " + ident.getRightOp());
				
				methodBody.getUnits().insertBefore(stmt, curr);
				methodBody.getUnits().remove(curr);
				curr = stmt;
			}
		}
		
		// now shove them into the front (in reverse order)
		for (int i = paramStmts.length-1; i>=0; i--)
			methodBody.getUnits().insertBefore(paramStmts[i], methodBody.getUnits().getFirst());
		
		// add the 'this' identity
		if (!body.isStatic())
			methodBody.getUnits().insertBefore(thisStmt, methodBody.getUnits().getFirst());
	}

	
	/**
	 * Finds blocks that are equivalent and points all parents of one at the other.
	 */
	public void removeDuplicateBlocks() {
		for (boolean progress=true; progress; ) {
			progress = false;
			
			final List<JavaPEGCFGBlock> allblocks = 
				new ArrayList<JavaPEGCFGBlock>(this.cfg.getBlocks());
			for (int i = 0; i < allblocks.size(); i++) {
				final JavaPEGCFGBlock blockI = allblocks.get(i);
				for (int j = i+1; j < allblocks.size(); j++) {
					final JavaPEGCFGBlock blockJ = allblocks.get(j);
					if (blocksEqual(blockI, blockJ)) {
						// reroute preds of blockJ to be preds of blockI
						for (JavaPEGCFGBlock predJ : this.cfg.getPreds(blockJ))
							predJ.replaceChild(blockJ, blockI);
						if (this.cfg.getStartBlock().equals(blockJ))
							this.cfg.setStartBlock(blockI);

						// destroy blockJ
						for (int k = blockJ.getNumSuccs()-1; k>=0; k--)
							blockJ.removeSucc(k);
						
						progress = true;
					}
				}
			}
		}
	}
	
	
	
	private boolean blocksEqual(JavaPEGCFGBlock block1, JavaPEGCFGBlock block2) {
		Set<Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>> seen = 
			new HashSet<Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>>();
		LinkedList<Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>> worklist = 
			new LinkedList<Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>>();
		worklist.addLast(new Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>(block1, block2));
		
		while (worklist.size() > 0) {
			Pair<JavaPEGCFGBlock,JavaPEGCFGBlock> next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			final JavaPEGCFGBlock left = next.getFirst();
			final JavaPEGCFGBlock right = next.getSecond();
			if (left.equals(right))
				continue;
			if (left.getNumSuccs() != right.getNumSuccs())
				return false;
			if (!left.haveSameMiniPEG(right))
				return false;
			for (int i = 0; i < left.getNumSuccs(); i++)
				worklist.addLast(new Pair<JavaPEGCFGBlock,JavaPEGCFGBlock>(left, right));
		}
		return true;
	}
	
	
	private void linearizeBlocks(SootBlock start, List<SootBlock> blocks) {
		// linearize blocks
		Body methodBody = this.body.retrieveActiveBody();
		PatchingChain units = methodBody.getUnits();
		units.clear();
		Unit last = new JNopStmt();
		units.add(last);
		
		Map<JIfStmt,SootBlock> patches = new HashMap<JIfStmt,SootBlock>(); 
		Map<SootBlock,Unit> block2first = new HashMap<SootBlock,Unit>();
		LinkedList<SootBlock> worklist = new LinkedList<SootBlock>();
		worklist.addFirst(start);
		
		while (!worklist.isEmpty()) {
			SootBlock next = worklist.removeFirst();
			if (block2first.containsKey(next))
				continue;

			if (next.getNumInstructions() == 0) {
				next.addInstruction(new JNopStmt());
			}
			Unit first = next.getInstruction(0);
			block2first.put(next, first);

			if (next.getNumSuccessors() == 0) {
				// end block
				for (int i = 0; i < next.getNumInstructions(); i++) {
					units.insertAfter(next.getInstruction(i), last);
					last = next.getInstruction(i);
				}
			} else if (next.getNumSuccessors() == 1) {
				// fallthrough block
				for (int i = 0; i < next.getNumInstructions(); i++) {
					units.insertAfter(next.getInstruction(i), last);
					last = next.getInstruction(i);
				}
				SootBlock succ = next.getSuccessor(0);
				if (block2first.containsKey(succ)) {
					// add a goto
					Unit gotoUnit = new JGotoStmt(block2first.get(succ));
					units.insertAfter(gotoUnit, last);
					last = gotoUnit;
				} else {
					// make him next
					worklist.addFirst(succ);
				}
			} else if (next.getNumSuccessors() == 2) {
				// branch block
				SootBlock jump = next.getSuccessor(0);
				SootBlock fall = next.getSuccessor(1);
				worklist.addFirst(jump);
				
				// do all but last (should be an if)
				for (int i = 0; i < next.getNumInstructions()-1; i++) {
					units.insertAfter(next.getInstruction(i), last);
					last = next.getInstruction(i);
				}
				Unit lastInst = next.getInstruction(next.getNumInstructions()-1);
				if (!(lastInst instanceof JIfStmt))
					throw new RuntimeException("Last stmt in branch block should be if: " + lastInst);
				JIfStmt ifstmt = (JIfStmt)lastInst;
				units.insertAfter(ifstmt, last);
				last = ifstmt;
				
				if (block2first.containsKey(jump)) {
					// can patch now
					ifstmt.setTarget(block2first.get(jump));
				} else {
					// must patch later
					patches.put(ifstmt, jump);
				}
				
				// now handle fallthrough
				if (block2first.containsKey(fall)) {
					// add a goto at the end
					JGotoStmt gotostmt = new JGotoStmt(block2first.get(fall));
					units.insertAfter(gotostmt, last);
					last = gotostmt;
				} else {
					// add to front of worklist
					worklist.addFirst(fall);
				}
			} else 
				throw new RuntimeException("Invalid number of successors: " + next.getNumSuccessors());
		}
		
		// now apply patches
		for (JIfStmt ifstmt : patches.keySet()) {
			SootBlock jumpBlock = patches.get(ifstmt);
			if (!block2first.containsKey(jumpBlock))
				throw new RuntimeException("Jump block never linearized!!");
			ifstmt.setTarget(block2first.get(jumpBlock));
		}
		
		// now reassign the local chain (barf)
		Set<JimpleLocal> locals = new HashSet<JimpleLocal>();
		methodBody.getLocals().clear();
		for (Iterator<Unit> unititer = methodBody.getUnits().iterator(); unititer.hasNext(); ) {
			Unit unit = unititer.next();
			for (ValueBox box : (List<ValueBox>)unit.getUseAndDefBoxes()) {
				Value v = box.getValue();
				if (v instanceof JimpleLocal)
					locals.add((JimpleLocal)v);
			}
		}
		methodBody.getLocals().addAll(locals);
	}
	
	private SootBlock buildBlocks(List<SootBlock> newblocks) {
		// build type map
		Map<Object,JimpleLocal> varmap = 
			new HashMap<Object,JimpleLocal>();
		
		this.buildTypeMap(varmap);

		// build basic blocks
		Map<JavaPEGCFGBlock,SootBlock> blockmap = 
			new HashMap<JavaPEGCFGBlock,SootBlock>();
		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			SootBlock newblock = new SootBlock();
			newblocks.add(newblock);
			blockmap.put(block, newblock);
		}
		
		// build successor map
		for (JavaPEGCFGBlock block : blockmap.keySet()) {
			SootBlock newblock = blockmap.get(block);
			for (JavaPEGCFGBlock succ : block.getSuccs()) {
				newblock.addSuccessor(blockmap.get(succ));
			}
		}
		
		// build instructions
		for (JavaPEGCFGBlock block : blockmap.keySet()) {
			SootBlock newblock = blockmap.get(block);
			this.buildInstructions(block, newblock, varmap);
		}

		return blockmap.get(this.cfg.getStartBlock());
	}
	
	
	
	/**
	 * Converts the minipeg in the Java block to instructions
	 * in the basicblock.
	 * All the maps that are passed in are already full.
	 */
	private void buildInstructions(
			JavaPEGCFGBlock block,
			SootBlock newblock,
			Map<Object,JimpleLocal> varmap) {
		// build the substitution map
		// submap is a set of vars that are temporarily assigned to
		//  before the BB is done, then at the end the appropriate loop vars 
		//  are assigned the temp vars
		Map<Object,Object> submap = new HashMap<Object,Object>();
		for (JavaIterator iter=new JavaIterator(block); iter.hasNext(); ) {
			Vertex<Item<JavaLabel,JavaParameter,Object>> next = iter.next();
			if (next.getLabel().isVariable() && 
				block.getAssignedVars().contains(next.getLabel().getVariable())) {
				// both get and set, add to submap
				Object newvar = this.cfg.makeNewTemporary();
				Object oldvar = next.getLabel().getVariable();
				submap.put(oldvar, newvar);
				
				// update the varmap
				if (varmap.get(oldvar) == null) {
					varmap.put(newvar, null);
				} else {
					varmap.put(newvar, newLocal(varmap.get(oldvar).getType()));
				}
			}
		}
		
		// copy the values from the submap
		// CONVENTION: read from newV, write to oldV
		for (Object oldvar : submap.keySet()) {
			if (varmap.get(oldvar) == null)
				continue;
			// newV = oldV
			newblock.addInstruction(new JAssignStmt(
					varmap.get(submap.get(oldvar)),
					varmap.get(oldvar)));
		}

		
		Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,JimpleLocal> tempmap =
			new HashMap<Vertex<Item<JavaLabel,JavaParameter,Object>>,JimpleLocal>();
		
		Emitter emitter = new Emitter(newblock, tempmap, varmap, submap); 

		final Object returnVar = this.cfg.getReturnVariable(JavaReturn.VALUE);
		
		// assign vars to their values
		for (Object assigned : block.getAssignedVars()) {
			if (assigned.equals(returnVar)) {
				if (newblock.getNumInstructions() != 0)
					throw new RuntimeException("Return vars should only be assigned in end block");
				continue;
			}

			// preemptively set the type of domain bottom
			if (block.getAssignment(assigned).getLabel().isLabel() &&
				block.getAssignment(assigned).getLabel().getLabel().isBottom() &&
				varmap.containsKey(assigned) &&
				varmap.get(assigned) != null) {
				newblock.addInstruction(new JAssignStmt(varmap.get(assigned),
														SootUtils.getNullByType(varmap.get(assigned).getType())));
			} else {
				// evaluate expressions and produce instructions
				Value value = emitter.emitInstructions(block.getAssignment(assigned));
				if (value != null) {
					if (!varmap.containsKey(assigned))
						throw new RuntimeException("Var has no local");
					if (varmap.get(assigned) != null) {
						newblock.addInstruction(new JAssignStmt(varmap.get(assigned), value));
					}
				}
			}
		}
		
		if (newblock.getNumSuccessors() == 0) {
			// return block
			Type returnType = this.body.getReturnType();
			if (returnType instanceof VoidType) {
				// emit return void
				Unit inst = new JReturnVoidStmt();
				newblock.addInstruction(inst);
			} else if (block.getAssignedVars().contains(returnVar)) {
				// non void and actually assigned in this block
				// normal return value
				if (block.getAssignment(returnVar).getLabel().isLabel() &&
					block.getAssignment(returnVar).getLabel().getLabel().isBottom() &&
					varmap.containsKey(returnVar) &&
					varmap.get(returnVar) != null) {
					newblock.addInstruction(new JReturnStmt(SootUtils.getNullByType(varmap.get(returnVar).getType())));
				} else {
					Value result = emitter.emitInstructions(block.getAssignment(returnVar));
					if (result == null)
						throw new RuntimeException("Result cannot be null");
					newblock.addInstruction(new JReturnStmt(result));
				}
			}
		}
		
		if (block.getBranchCondition() != null) {
			// non-invoke branching
			final Vertex<Item<JavaLabel,JavaParameter,Object>> bc = 
				block.getBranchCondition();
			if (bc.getLabel().isLabel()) {
				if (bc.getLabel().getLabel().isBottom())
					throw new RuntimeException("Bottom not allowed here");
				
				Value value = emitter.emitInstructions(bc, true);
				if (!(value instanceof ConditionExpr)) 
					throw new RuntimeException("Expecting condition expression: " + value);
				JIfStmt br = new JIfStmt(value, (Unit)null);
				newblock.addInstruction(br);
			} else if (bc.getLabel().isVariable()) {
				// make it V!=0
				Value value = emitter.emitInstructions(bc); // will be a JimpleLocal
				if (value == null)
					throw new RuntimeException("Should not be null");
				JIfStmt br = new JIfStmt(
						new JNeExpr(value, IntConstant.v(0)), 
						(Unit)null);
				newblock.addInstruction(br);
			} else {
				throw new RuntimeException("Expecting label or variable: " + bc.getLabel());
			}
		}
	}
		

	private class Emitter {
		private final SootBlock newblock;
		private final Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,JimpleLocal> tempmap;
		private final Map<? extends Object,? extends JimpleLocal> varmap;
		private final Map<Object,Object> submap;

		Emitter(
				SootBlock _newblock,
				Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,JimpleLocal> _tempmap,
				Map<? extends Object,? extends JimpleLocal> _varmap,
				Map<Object,Object> _submap) {
			this.newblock = _newblock;
			this.tempmap = _tempmap;
			this.varmap = _varmap;
			this.submap = _submap;
		}

		private Value helper(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex, Value rhs) {
			JimpleLocal lhs = newLocal(rhs.getType());
			JAssignStmt inst = new JAssignStmt(lhs, rhs);
			newblock.addInstruction(inst);
			tempmap.put(vertex, lhs);
			return lhs;
		}
		
		public Value emitInstructions(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
			return emitInstructions(vertex, false);
		}
		public Value emitInstructions(
				Vertex<Item<JavaLabel,JavaParameter,Object>> vertex, 
				boolean isBranchTop) {
			if (!isBranchTop) {
				if (tempmap.containsKey(vertex))
					return tempmap.get(vertex);
			}

			/* heuristic: whenever you add instructions, you must add to tempmap
			 */

			Item<JavaLabel,JavaParameter,Object> item = vertex.getLabel();

			if (item.isParameter()) {
				JavaParameter param = item.getParameter();
				if (param.isSigma())
					return null;
				else if (param.isArgument()) {
					ArgumentJavaVariable arg = param.getArgumentSelf().getVariableVersion();
					ParameterRef ref = new ParameterRef(arg.getArgumentType(), arg.getArgumentIndex());
					JimpleLocal local = newLocal(ref.getType());
					JIdentityStmt stmt = new JIdentityStmt(local, ref);
					newblock.addInstruction(stmt);
					tempmap.put(vertex, local);
					return local;
				}
				else if (param.isThis()) {
					ThisJavaVariable arg = param.getThisSelf().getVariableVersion();
					ThisRef ref = new ThisRef(arg.getThisType());
					JimpleLocal local = newLocal(ref.getType());
					JIdentityStmt stmt = new JIdentityStmt(local, ref);
					newblock.addInstruction(stmt);
					tempmap.put(vertex, local);
					return local;
				}
				else
					throw new IllegalArgumentException("Mike didn't handle: " + item);
			} else if (item.isVariable()) {
				Object var = item.getVariable();
				if (this.submap.containsKey(var))
					return varmap.get(this.submap.get(var));
				else
					return varmap.get(var);
			} else {
				JavaLabel label = item.getLabel();
				if (label.isSimple()) {
					switch (label.getSimpleSelf().getOperator()) {
					case INJR:
					case INJL:
						// should not exist
						throw new IllegalArgumentException("Should not have " + label.getSimpleSelf().getOperator());

					case INVOKEVIRTUAL:
					case INVOKESPECIAL:
					case INVOKEINTERFACE:
					case INVOKESTATIC: {
						int methodIndex = 
							(label.getSimpleSelf().getOperator().equals(JavaOperator.INVOKESTATIC) ?
									1 : 2);
						
						emitInstructions(vertex.getChild(0)); // sigma
						Value target = (methodIndex==2 ?  
							emitInstructions(vertex.getChild(1)) :
							null);
						MethodJavaLabel methodLabel = 
							vertex.getChild(methodIndex).getLabel().getLabel().getMethodSelf();
						List<Value> actuals = new ArrayList<Value>();
						Vertex<Item<JavaLabel,JavaParameter,Object>> params = 
							vertex.getChild(methodIndex+1);
						for (int i = 0; i < params.getChildCount(); i++) {
							actuals.add(emitInstructions(params.getChild(i)));
						}

						if (!label.getSimpleSelf().getOperator().equals(JavaOperator.INVOKESTATIC) &&
							!(target instanceof JimpleLocal)) {
							// put target into a local (if and target is not a local)
							JimpleLocal newtarget = newLocal(target.getType());
							JAssignStmt assign = new JAssignStmt(newtarget, target);
							newblock.addInstruction(assign);
							target = newtarget;
						}
						
						
						InvokeExpr invoke;
						switch (label.getSimpleSelf().getOperator()) {
						case INVOKEVIRTUAL: {
							invoke = new JVirtualInvokeExpr(
									target, 
									resolver.resolveMethod(
											methodLabel.getClassName(),
											methodLabel.getMethodName(),
											methodLabel.getReturnType(),
											methodLabel.getParameterTypes()),
									actuals);
							break;
						}
							
						case INVOKESPECIAL:
							invoke = new JSpecialInvokeExpr(
									(JimpleLocal)target, 
									resolver.resolveMethod(
											methodLabel.getClassName(),
											methodLabel.getMethodName(),
											methodLabel.getReturnType(),
											methodLabel.getParameterTypes()),
									actuals);
							break;

						case INVOKEINTERFACE:
							invoke = new JInterfaceInvokeExpr(
									target, 
									resolver.resolveMethod(
											methodLabel.getClassName(),
											methodLabel.getMethodName(),
											methodLabel.getReturnType(),
											methodLabel.getParameterTypes()),
									actuals);
							break;

						default:
							invoke = new JStaticInvokeExpr(
									resolver.resolveMethod(
											methodLabel.getClassName(),
											methodLabel.getMethodName(),
											methodLabel.getReturnType(),
											methodLabel.getParameterTypes()),
									actuals);
							break;
						}
						
						if (methodLabel.getReturnType() instanceof VoidType) {
							JInvokeStmt stmt = new JInvokeStmt(invoke);
							newblock.addInstruction(stmt);
							tempmap.put(vertex, null);
							return null;
						} else {
							return helper(vertex, invoke);
						}
					}
						
					case DIMS:
					case PARAMS:
					case VOID:
						return null;

					case PRIMITIVECAST: {
						TypeJavaLabel typeLabel = vertex.getChild(0).getLabel().getLabel().getTypeSelf();
						Value castee = emitInstructions(vertex.getChild(1));
						JCastExpr cast = new JCastExpr(castee, typeLabel.getType());
						return helper(vertex, cast);
					}

					case CAST: {
						emitInstructions(vertex.getChild(0));
						TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
						Value castee = emitInstructions(vertex.getChild(2));
						JCastExpr cast = new JCastExpr(castee, typeLabel.getType());
						return helper(vertex, cast);
					}
					
					case ARRAYLENGTH: {
						emitInstructions(vertex.getChild(0));
						Value array = emitInstructions(vertex.getChild(1));
						JLengthExpr length = new JLengthExpr(array);
						return helper(vertex, length);
					}
					
					case GETFIELD: {
						emitInstructions(vertex.getChild(0));
						Value target = emitInstructions(vertex.getChild(1));
						FieldJavaLabel fieldLabel = vertex.getChild(2).getLabel().getLabel().getFieldSelf();
						JInstanceFieldRef ref = new JInstanceFieldRef(
								target,
								resolver.resolveField(
										fieldLabel.getClassName(),
										fieldLabel.getFieldName(),
										fieldLabel.getType()));
						return helper(vertex, ref);
					}
					
					case GETSTATICFIELD: {
						emitInstructions(vertex.getChild(0));
						FieldJavaLabel fieldLabel = vertex.getChild(1).getLabel().getLabel().getFieldSelf();
						StaticFieldRef ref = Jimple.v().newStaticFieldRef(
								resolver.resolveField(
										fieldLabel.getClassName(),
										fieldLabel.getFieldName(),
										fieldLabel.getType()));
						return helper(vertex, ref);
					}
					
					case GETARRAY: {
						emitInstructions(vertex.getChild(0));
						Value array = emitInstructions(vertex.getChild(1));
						Value index = emitInstructions(vertex.getChild(2));
						JArrayRef ref = new JArrayRef(array, index);
						return helper(vertex, ref);
					}
					
					case INSTANCEOF: {
						emitInstructions(vertex.getChild(0));
						Value target = emitInstructions(vertex.getChild(1));
						TypeJavaLabel typeLabel = vertex.getChild(2).getLabel().getLabel().getTypeSelf();
						JInstanceOfExpr expr = new JInstanceOfExpr(target, typeLabel.getType());
						return helper(vertex, expr);
					}
					
					case PLUS: 
					case MINUS:
					case TIMES:
					case DIVIDE:
					case BITWISE_AND:
					case BITWISE_OR:
					case SHIFT_LEFT:
					case SHIFT_RIGHT:
					case UNSIGNED_SHIFT_RIGHT:
					case XOR:
					case MOD:
					case CMP:
					case CMPL:
					case CMPG: {
						Value lhs = emitInstructions(vertex.getChild(0));
						Value rhs = emitInstructions(vertex.getChild(1));
						Value binop;
						switch (label.getSimpleSelf().getOperator()) {
						case PLUS: binop = new JAddExpr(lhs, rhs); break;
						case MINUS: binop = new JSubExpr(lhs, rhs); break;
						case TIMES: binop = new JMulExpr(lhs, rhs); break;
						case DIVIDE: binop = new JDivExpr(lhs, rhs); break;
						case BITWISE_AND: binop = new JAndExpr(lhs, rhs); break;
						case BITWISE_OR: binop = new JOrExpr(lhs, rhs); break;
						case SHIFT_LEFT: binop = new JShlExpr(lhs, rhs); break;
						case SHIFT_RIGHT: binop = new JShrExpr(lhs, rhs); break;
						case UNSIGNED_SHIFT_RIGHT: binop = new JUshrExpr(lhs, rhs); break;
						case XOR: binop = new JXorExpr(lhs, rhs); break;
						case MOD: binop = new JRemExpr(lhs, rhs); break;
						case CMP: binop = new JCmpExpr(lhs, rhs); break;
						case CMPL: binop = new JCmplExpr(lhs, rhs); break;
						default: binop = new JCmpgExpr(lhs, rhs); break;
						}
						return helper(vertex, binop);
					}
					
					case GREATER_THAN:
					case GREATER_THAN_EQUAL:
					case LESS_THAN:
					case LESS_THAN_EQUAL:
					case EQUAL:
					case NOT_EQUAL: {
						Value lhs = emitInstructions(vertex.getChild(0));
						Value rhs = emitInstructions(vertex.getChild(1));
						ConditionExpr expr;
						switch (label.getSimpleSelf().getOperator()) {
						case GREATER_THAN: expr = new JGtExpr(lhs, rhs); break;
						case GREATER_THAN_EQUAL: expr = new JGeExpr(lhs, rhs); break;
						case LESS_THAN: expr = new JLtExpr(lhs, rhs); break;
						case LESS_THAN_EQUAL: expr = new JLeExpr(lhs, rhs); break;
						case EQUAL: expr = new JEqExpr(lhs, rhs); break;
						default: expr = new JNeExpr(lhs, rhs); break;
						}
						
						if (!isBranchTop) {
							throw new RuntimeException("Should be a branch top!");
							
//							// being stored for later branch
//							JimpleLocal V = newLocal(BooleanType.v());
//
//							//    if (condition) goto L1
//							// 	  V = 0
//							//	  goto L2
//							// L1:V = 1
//							// L2:nop
//							Unit L2nop = new JNopStmt();
//							Unit L1assignV1 = new JAssignStmt(V, IntConstant.v(1));
//							Unit gotoL2  = new JGotoStmt(L2nop);
//							Unit assignV0 = new JAssignStmt(V, IntConstant.v(0));
//							Unit ifstmt = new JIfStmt(expr, L1assignV1);
//							
//							newblock.addInstruction(ifstmt);
//							newblock.addInstruction(assignV0);
//							newblock.addInstruction(gotoL2);
//							newblock.addInstruction(L1assignV1);
//							newblock.addInstruction(L2nop);
//
//							tempmap.put(vertex, V);
//							return V;
						} else {
							return expr;
						}
					}
					
					case NEG: {
						Value child = emitInstructions(vertex.getChild(0));
						JNegExpr neg = new JNegExpr(child);
						return helper(vertex, neg);
					}
					
					case CLASS: {
						TypeJavaLabel typeLabel = vertex.getChild(0).getLabel().getLabel().getTypeSelf();
						Value cons = ClassConstant.v(typeLabel.getType().toString());
						return helper(vertex, cons);
					}
					
					case NEWARRAY: {
						emitInstructions(vertex.getChild(0));
						Value size = emitInstructions(vertex.getChild(2));
						TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
						JNewArrayExpr expr = new JNewArrayExpr(typeLabel.getType(), size);
						return helper(vertex, expr);
					}
					
					case NEWMULTIARRAY: {
						emitInstructions(vertex.getChild(0));
						TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
						Vertex<Item<JavaLabel,JavaParameter,Object>> dims =
							vertex.getChild(2);
						List<Value> values = new ArrayList<Value>();
						for (int i = 0; i < dims.getChildCount(); i++) {
							values.add(emitInstructions(dims.getChild(i)));
						}
						JNewMultiArrayExpr multi = new JNewMultiArrayExpr(
								(ArrayType)typeLabel.getType(),
								values);
						return helper(vertex, multi);
					}
					
					case NEWINSTANCE: {
						emitInstructions(vertex.getChild(0));
						TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
						JNewExpr expr = new JNewExpr((RefType)typeLabel.getType());
						return helper(vertex, expr);
					}
					
					case ENTERMONITOR: {
						emitInstructions(vertex.getChild(0));
						Value target = emitInstructions(vertex.getChild(1));
						JEnterMonitorStmt enter = new JEnterMonitorStmt(target);
						newblock.addInstruction(enter);
						tempmap.put(vertex, null);
						return null;
					}
					
					case EXITMONITOR: {
						emitInstructions(vertex.getChild(0));
						Value target = emitInstructions(vertex.getChild(1));
						JExitMonitorStmt exit = new JExitMonitorStmt(target);
						newblock.addInstruction(exit);
						tempmap.put(vertex, null);
						return null;
					}
					
					case SETARRAY: {
						emitInstructions(vertex.getChild(0));
						Value array = emitInstructions(vertex.getChild(1));
						Value index = emitInstructions(vertex.getChild(2));
						Value value = emitInstructions(vertex.getChild(3));
						JAssignStmt stmt = new JAssignStmt(
								new JArrayRef(array, index),
								value);
						newblock.addInstruction(stmt);
						tempmap.put(vertex, null);
						return null;
					}
					
					case SETFIELD: {
						emitInstructions(vertex.getChild(0));
						Value target = emitInstructions(vertex.getChild(1));
						Value value = emitInstructions(vertex.getChild(3));
						FieldJavaLabel fieldLabel = vertex.getChild(2).getLabel().getLabel().getFieldSelf();
						JInstanceFieldRef ref = new JInstanceFieldRef(
								target, 
								resolver.resolveField(
										fieldLabel.getClassName(),
										fieldLabel.getFieldName(),
										fieldLabel.getType()));
						JAssignStmt stmt = new JAssignStmt(ref, value);
						newblock.addInstruction(stmt);
						tempmap.put(vertex, null);
						return null;
					}
					
					case SETSTATICFIELD: {
						emitInstructions(vertex.getChild(0));
						Value value = emitInstructions(vertex.getChild(2));
						FieldJavaLabel fieldLabel = vertex.getChild(1).getLabel().getLabel().getFieldSelf();
						StaticFieldRef ref = Jimple.v().newStaticFieldRef(
								resolver.resolveField(
										fieldLabel.getClassName(),
										fieldLabel.getFieldName(),
										fieldLabel.getType()));
						JAssignStmt stmt = new JAssignStmt(ref, value);
						newblock.addInstruction(stmt);
						tempmap.put(vertex, null);
						return null;
					}
					
//					case TEMPSPLIT: {
//						emitInstructions(vertex.getChild(0));
//						return null;
//					}

					case RHO_VALUE: {
						return emitInstructions(vertex.getChild(0));
					}
					
					case RHO_SIGMA: {
						emitInstructions(vertex.getChild(0));
						return null;
					}
					
					default:
						throw new IllegalArgumentException("Unhandled case: " + label);
					}
				} else if (label.isBottom()) {
					return null;
				} else if (label.isType()) {
					return null;
				} else if (label.isMethod()) {
					return null;
				} else if (label.isField()) {
					return null;
				} else if (label.isConstant()) {
					return label.getConstantSelf().getValue();
				} else if (label.isBasic()) {
					switch (label.getBasicSelf().getOperator()) {
					case True: {
						return IntConstant.v(1);
					}
					
					case False: {
						return IntConstant.v(0);
					}
					
					case Negate: {
						throw new RuntimeException("Should not appear! " + label);
					}

					case And:
					case Or: {
						throw new RuntimeException("Should not appear! " + label);
					}

					case Equals: {
						Value lhs = emitInstructions(vertex.getChild(0));
						Value rhs = emitInstructions(vertex.getChild(1));
						JEqExpr expr = new JEqExpr(lhs, rhs);
						
						if (!isBranchTop)
//							return helper(vertex, expr);
							throw new RuntimeException("Should be a branch top!");
						else
							return expr;
					}

					default:
						throw new IllegalArgumentException("Invalid basicop: " + label.getBasicSelf().getOperator());
					}
				} else {
					throw new IllegalArgumentException("Invalid label: " + label);
				}
			}
		}
	}
	
	/**
	 * Convenience class to make using BlockVerticesIterator easier. 
	 */
	private static class JavaIterator 
	extends BlockVerticesIterator<JavaLabel,JavaParameter,JavaReturn,Object,JavaPEGCFG,JavaPEGCFGBlock> {
		public JavaIterator(JavaPEGCFGBlock _block) {super(_block);}
	}

	private JimpleLocal newLocal(Type type) {
		return new JimpleLocal("_T" + (localCounter++) + "_", type);
	}
	
	/**
	 * Assigns types to all the minipeg nodes in the entire CFG, 
	 * but only retain enough info for the variables.
	 */
	private void buildTypeMap(Map<Object,JimpleLocal> varmap) {
		// gather all vars first
		Set<Object> allvars = new HashSet<Object>();
		Map<JavaPEGCFGBlock,Collection<?>> block2vars = 
			new HashMap<JavaPEGCFGBlock,Collection<?>>();
		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			Collection<?> vars = block.getReferencedVars();
			block2vars.put(block, vars);
			allvars.addAll(vars);
		}
		
		// set the type of the return var preemptively
		Object returnVar = this.cfg.getReturnVariable(JavaReturn.VALUE);
		Type returnType = this.body.getReturnType();
		if (returnType instanceof VoidType) {
			varmap.put(returnVar, null);
		} else {
			JimpleLocal var = newLocal(returnType);
			varmap.put(returnVar, var);
		}
		
		// iteratively assign types to the vars
		// type of null means it's not a Java type, but a PEG type

		Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Type> typemap = 
			new HashMap<Vertex<Item<JavaLabel,JavaParameter,Object>>,Type>();
		
		while (varmap.size() < allvars.size()) {
			final int varmapSize = varmap.size();
			final int typemapSize = typemap.size();

			// iterate over all blocks
			for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
				Collection<?> vars = block2vars.get(block);
				
				if (block.getBranchCondition() != null &&
					!typemap.containsKey(block.getBranchCondition())) {
					typemap.put(block.getBranchCondition(), BooleanType.v());
					if (block.getBranchCondition().getLabel().isVariable()) {
						Object bcvar = block.getBranchCondition().getLabel().getVariable();
						if (!varmap.containsKey(bcvar))
							varmap.put(bcvar, newLocal(BooleanType.v()));
					}
				}
				
				if (varmap.keySet().containsAll(vars))
					continue;

				// loop over all vertices in the block
				Set<Vertex<Item<JavaLabel,JavaParameter,Object>>> getvars = 
					new HashSet<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
				for (JavaIterator iter = new JavaIterator(block); iter.hasNext(); ) {
					Vertex<Item<JavaLabel,JavaParameter,Object>> vertex = iter.next();

					Item<JavaLabel,JavaParameter,Object> item = vertex.getLabel();
					if (item.isVariable()) {
						getvars.add(vertex);
						if (varmap.containsKey(item.getVariable())) {
							JimpleLocal reg = varmap.get(item.getVariable());
							if (reg == null)
								setType(typemap, vertex, null);
							else
								setType(typemap, vertex, reg.getType());
						}
						continue;
					} else if (item.isParameter()) {
						// assign type (sigma has type null)
						JavaParameter param = item.getParameter();
						if (param.isArgument()) {
							Type type = param.getArgumentSelf().getType();
							setType(typemap, vertex, type);
						} else if (param.isThis()) {
							Type type = param.getThisSelf().getVariableVersion().getThisType();
							setType(typemap, vertex, type);
						} else {
							setType(typemap, vertex, null);
						}
					} else {
						// label! infer type
						this.inferLabelType(vertex, typemap);
					}
				}
				
				// check if any setvars have new types
				for (Object assigned : block.getAssignedVars()) {
					if (varmap.containsKey(assigned)) {
						if (block.getAssignment(assigned).getLabel().isLabel() &&
							block.getAssignment(assigned).getLabel().getLabel().isBottom() &&
							varmap.get(assigned) != null) {
							typemap.put(block.getAssignment(assigned), varmap.get(assigned).getType());
						}
						continue;
					}
					if (typemap.containsKey(block.getAssignment(assigned))) {
						Type type = typemap.get(block.getAssignment(assigned));
						if (type == null) {
							varmap.put(assigned, null);
						} else {
							varmap.put(assigned, newLocal(type));
						}
					}
				}
				
				// check if any getvars have new types
				for (Vertex<Item<JavaLabel,JavaParameter,Object>> get : getvars) {
					Object var = get.getLabel().getVariable();
					if (varmap.containsKey(var)) {
						if (!typemap.containsKey(get)) {
							if (varmap.get(var) == null)
								setType(typemap, get, null);
							else
								setType(typemap, get, varmap.get(var).getType());
						}
						continue;
					}
					if (typemap.containsKey(get)) {
						Type type = typemap.get(get);
						if (type == null) {
							varmap.put(var, null);
						} else {
							varmap.put(var, newLocal(typemap.get(get)));
						}
					}
				}
				
				getvars = null;
			}
			
			if (varmap.size() <= varmapSize && typemap.size() <= typemapSize) {
				throw new RuntimeException("Loop made no progress, cannot derive all types");
			}
		}
	}
	
	
	private Type getCompatibleType(Type newtype, Type oldtype) {
		if (oldtype instanceof IntType &&
			(newtype instanceof ShortType || 
			 newtype instanceof ByteType || 
			 newtype instanceof BooleanType ||
			 newtype instanceof CharType)) {
			return newtype;
		}
		else if (newtype instanceof IntType && 
				 (oldtype instanceof ShortType ||
				  oldtype instanceof ByteType ||
			      oldtype instanceof BooleanType ||
			      oldtype instanceof CharType)) {
			return oldtype;
		}
		else if (newtype instanceof ArrayType &&
				 oldtype instanceof ArrayType) {
			// might be compatible array types
			Type newbase = newtype;
			Type oldbase = oldtype;
			
			while (newbase instanceof ArrayType) {
				newbase = ((ArrayType)newbase).getArrayElementType();
				if (!(oldbase instanceof ArrayType))
					throw new RuntimeException("Not both array types: " + oldbase);
				oldbase = ((ArrayType)oldbase).getArrayElementType();
			}

			Type compatible = getCompatibleType(newbase, oldbase);
			if (compatible == null)
				return null;
			else if (compatible.equals(newbase))
				return newtype;
			else
				return oldtype;
		}
		else
			return null;
	}
	
	
	private void setType(
			Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Type> typemap,
			Vertex<Item<JavaLabel,JavaParameter,Object>> vertex,
			Type type) {
		if (typemap.containsKey(vertex)) {
			Type old = typemap.get(vertex);
			if (old == null && type == null) {
				return;
			}
			else if (old == null || type == null) {
				throw new IllegalArgumentException(
						"Mismatched types for vertex " + 
						vertex.getLabel() + ": " +
						old + " vs " + type);
			}
			else if (!old.equals(type)) {
				// a few acceptable cases
				Type compatible = getCompatibleType(type, old);
				if (compatible != null) {
					typemap.put(vertex, compatible);
				} else {
					throw new IllegalArgumentException(
							"Mismatched types for vertex " +
							vertex.getLabel() + ": " + 
							old + " vs " + type);
				}
			}
		} else {
			typemap.put(vertex, type);
		}
	}
	
	
	private void inferLabelType(
			Vertex<Item<JavaLabel,JavaParameter,Object>> vertex,
			Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Type> typemap) {

		JavaLabel label = vertex.getLabel().getLabel();
		if (label.isSimple()) {
			switch (label.getSimpleSelf().getOperator()) {
			case INJR:
			case INJL:
			case VOID:
			case PARAMS:
			case DIMS:
				setType(typemap, vertex, null);
				break;
				
			case INVOKESTATIC:
			case INVOKEVIRTUAL:
			case INVOKESPECIAL:
			case INVOKEINTERFACE:{
				int methodIndex = 
					(label.getSimpleSelf().getOperator().equals(JavaOperator.INVOKESTATIC) ?
							1 : 2);
				
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(methodIndex), null); // method
				setType(typemap, vertex.getChild(methodIndex+1), null); // params
				
				// set return type
				MethodJavaLabel methodLabel = 
					vertex.getChild(methodIndex).getLabel().getLabel().getMethodSelf();
				Type returnType = methodLabel.getReturnType();
				if (returnType instanceof VoidType)
					setType(typemap, vertex, null);
				else
					setType(typemap, vertex, returnType);
				break;
			}

			case PRIMITIVECAST: {
				setType(typemap, vertex.getChild(0), null); // type
				TypeJavaLabel typeLabel = vertex.getChild(0).getLabel().getLabel().getTypeSelf();
				setType(typemap, vertex, typeLabel.getType());
				break;
			}

			case CAST: {
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(1), null); // type
				TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
				setType(typemap, vertex, typeLabel.getType());
				break;
			}
			
			case ARRAYLENGTH: {
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex, IntType.v());
				break;
			}
			
			case GETFIELD: {
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(2), null); // field
				FieldJavaLabel field = 
					vertex.getChild(2).getLabel().getLabel().getFieldSelf();
				setType(typemap, vertex, field.getType());
				break;
			}

			case GETSTATICFIELD: {
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(1), null); // field
				FieldJavaLabel field = 
					vertex.getChild(1).getLabel().getLabel().getFieldSelf();
				setType(typemap, vertex, field.getType());
				break;
			}
			
			case GETARRAY: {
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(2), IntType.v()); // index
				
				if (typemap.containsKey(vertex.getChild(1))) {
					// set self from array child
					ArrayType arrayType = (ArrayType)typemap.get(vertex.getChild(1));
					setType(typemap, vertex, arrayType.getElementType());
				} else if (typemap.containsKey(vertex)) {
					// set array child from self
					Type eltType = typemap.get(vertex);
					ArrayType arrayType = ArrayType.v(eltType, 1);
					setType(typemap, vertex.getChild(1), arrayType);
				}
				break;
			}

			case INSTANCEOF: {
				setType(typemap, vertex, IntType.v());
				setType(typemap, vertex.getChild(0), null); // sigma
				setType(typemap, vertex.getChild(2), null); // type
 				break;
			}
			
			case PLUS:
			case MINUS:
			case TIMES:
			case DIVIDE:
			case MOD: 
			case XOR:
			case BITWISE_AND:
			case BITWISE_OR: {
				if (typemap.containsKey(vertex)) {
					Type type = typemap.get(vertex);
					setType(typemap, vertex.getChild(0), type);
					setType(typemap, vertex.getChild(1), type);
				} else if (typemap.containsKey(vertex.getChild(0))) {
					Type type = typemap.get(vertex.getChild(0));
					setType(typemap, vertex, type);
					setType(typemap, vertex.getChild(1), type);
				} else if (typemap.containsKey(vertex.getChild(1))) {
					Type type = typemap.get(vertex.getChild(1));
					setType(typemap, vertex, type);
					setType(typemap, vertex.getChild(0), type);
				}
				break;
			}
			
			case CMP: {
				setType(typemap, vertex, IntType.v());
				setType(typemap, vertex.getChild(0), LongType.v());
				setType(typemap, vertex.getChild(1), LongType.v());
				break;
			}
			
			case CMPL:
			case CMPG: {
				setType(typemap, vertex, IntType.v());
				if (typemap.containsKey(vertex.getChild(0))) {
					setType(typemap, vertex.getChild(1), typemap.get(vertex.getChild(0)));
				} else if (typemap.containsKey(vertex.getChild(1))) {
					setType(typemap, vertex.getChild(0), typemap.get(vertex.getChild(1)));
				}
				break;
			}
			
			case GREATER_THAN:
			case GREATER_THAN_EQUAL:
			case LESS_THAN:
			case LESS_THAN_EQUAL: {
				setType(typemap, vertex, BooleanType.v());
				setType(typemap, vertex.getChild(0), IntType.v());
				setType(typemap, vertex.getChild(1), IntType.v());
				break;
			}
			
			case EQUAL: 
			case NOT_EQUAL: {
				setType(typemap, vertex, BooleanType.v());
				break;
			}
			
			case SHIFT_LEFT:
			case SHIFT_RIGHT: 
			case UNSIGNED_SHIFT_RIGHT: {
				// lhs=long or int, rhs=int
				setType(typemap, vertex.getChild(1), IntType.v());
				if (typemap.containsKey(vertex)) {
					setType(typemap, vertex.getChild(0), typemap.get(vertex));
				} else if (typemap.containsKey(vertex.getChild(0))) {
					setType(typemap, vertex, typemap.get(vertex.getChild(0)));
				}
				break;
			}
			
			case NEG: {
				// numeric
				if (typemap.containsKey(vertex)) {
					setType(typemap, vertex.getChild(0), typemap.get(vertex));
				} else if (typemap.containsKey(vertex.getChild(0))) {
					setType(typemap, vertex, typemap.get(vertex.getChild(0)));
				}
				break;
			}
			
			case CLASS: {
				setType(typemap, vertex, RefType.v("java.lang.Class"));
				setType(typemap, vertex.getChild(0), null);
				break;
			}

			case NEWARRAY: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(1), null);
				setType(typemap, vertex.getChild(2), IntType.v());
				TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
				setType(typemap, vertex, ArrayType.v(typeLabel.getType(), 1));
				break;
			}
			
			case NEWMULTIARRAY: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(1), null);
				setType(typemap, vertex.getChild(2), null);
				TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
				setType(typemap, vertex, typeLabel.getType());
				break;
			}
			
			case NEWINSTANCE: {
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(1), null);
				TypeJavaLabel typeLabel = vertex.getChild(1).getLabel().getLabel().getTypeSelf();
				setType(typemap, vertex, typeLabel.getType());
				break;
			}
			
			case ENTERMONITOR:
			case EXITMONITOR: {
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				break;
			}
			
			case SETARRAY: {
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(2), IntType.v());
				break;
			}
			
			case SETFIELD: {
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(2), null);
				break;
			}
			
			case SETSTATICFIELD: {
				setType(typemap, vertex, null);
				setType(typemap, vertex.getChild(0), null);
				setType(typemap, vertex.getChild(1), null);
				break;
			}
			
//			case TEMPSPLIT: {
//				setType(typemap, vertex, null);
//				setType(typemap, vertex.getChild(0), null);
//				break;
//			}
			
			case THROW:
				throw new RuntimeException("Should not be any IS_EXCEPTIONs by now");

			case RHO_VALUE: {
				if (typemap.containsKey(vertex)) {
					setType(typemap, vertex.getChild(0), typemap.get(vertex));
				} else if (typemap.containsKey(vertex.getChild(0))) {
					setType(typemap, vertex, typemap.get(vertex.getChild(0)));
				}
				break;
			}
			
			case RHO_SIGMA: {
				setType(typemap, vertex, null);
				break;
			}
				
			default:
				throw new RuntimeException("Mike forgot to handle: " + label.getSimpleSelf().getOperator());
			}
		} else if (label.isType() || label.isField() || label.isMethod()) {
			setType(typemap, vertex, null);
		} else if (label.isConstant()) {
			Type type = label.getConstantSelf().getValue().getType();
			setType(typemap, vertex, type);
		} else if (label.isBasic()) {
			switch (label.getBasicSelf().getOperator()) {
			case Negate:
				setType(typemap, vertex, BooleanType.v());
				setType(typemap, vertex.getChild(0), BooleanType.v());
				break;
				
			case And:
			case Or:
				setType(typemap, vertex, BooleanType.v());
				setType(typemap, vertex.getChild(0), BooleanType.v());
				setType(typemap, vertex.getChild(1), BooleanType.v());
				break;
				
			case Equals:
				setType(typemap, vertex, BooleanType.v());
				// can say anything about child types??
				break;

			case True:
			case False:
				setType(typemap, vertex, BooleanType.v());
				break;
				
			default:
				throw new RuntimeException("Should not happen");
			}
		} else if (label.isBottom()) {
			return;
		} else {
			// error
			throw new RuntimeException("Invalid label type: " + label);
		}
	}
	
	
	private void removeINJR() {
		LabelOperatorPattern pattern = 
			new LabelOperatorPattern(SimpleJavaLabel.create(JavaOperator.INJR));
		
		Function<Vertex<Item<JavaLabel,JavaParameter,Object>>,
				 Vertex<Item<JavaLabel,JavaParameter,Object>>> builder = 
			new Function<Vertex<Item<JavaLabel,JavaParameter,Object>>,
						 Vertex<Item<JavaLabel,JavaParameter,Object>>>() {
				public Vertex<Item<JavaLabel,JavaParameter,Object>> get(
						Vertex<Item<JavaLabel,JavaParameter,Object>> param) {
					return param.getChild(0);
				}
			};

		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			replaceInBlock(pattern, block, builder);
		}
	}
	
	
	
	/**
	 * This method will attempt to replace all the nodes in 
	 * the given block with ones created by
	 * builder.get(block), but only if they match pattern.
	 * This will proceed by recursive descent on the assigned 
	 * variable vertices, and recursively rebuild them if necessary.
	 * Will also do the branch condition.
	 */
	private void replaceInBlock(
			Pattern<Vertex<Item<JavaLabel,JavaParameter,Object>>> pattern,
			JavaPEGCFGBlock block,
			Function<Vertex<Item<JavaLabel,JavaParameter,Object>>,
					 Vertex<Item<JavaLabel,JavaParameter,Object>>> builder) {
		
		Map<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>> changed = 
			new HashMap<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>>();
		
		for (JavaIterator iter = new JavaIterator(block); iter.hasNext(); ) {
			Vertex<Item<JavaLabel,JavaParameter,Object>> next = iter.next();
			if (changed.containsKey(next))
				continue;
			
			if (pattern.matches(next)) {
				Vertex<Item<JavaLabel,JavaParameter,Object>> newnext = 
					builder.get(next);
				changed.put(next, newnext);
				
				for (Vertex<Item<JavaLabel,JavaParameter,Object>> parent : next.getParents()) {
					parent.replaceChild(next, newnext);
				}
			}
			
		}
		
		for (Object assigned : block.getAssignedVars()) {
			if (changed.containsKey(block.getAssignment(assigned))) {
				block.setAssignment(
						assigned, 
						changed.get(block.getAssignment(assigned)));
			}
		}
		if (block.getBranchCondition() != null) {
			if (changed.containsKey(block.getBranchCondition())) {
				block.setBranchCondition(changed.get(block.getBranchCondition()));
			}
		}
	}
	

	
	/**
	 * Assumes that 'bottom' is some descendant of 'root', 
	 * will rebuild 'root' in block, but replacing 'bottom' 
	 * with a get of 'replaceVar'.
	 * Returns the resulting new root.
	 * IMPORTANT: Copies nodes into a different block than they came from 
	 */
	/*
	private Vertex<Item<JavaLabel,JavaParameter,Object>> copyUpTo(
			Vertex<Item<JavaLabel,JavaParameter,Object>> root,
			Vertex<Item<JavaLabel,JavaParameter,Object>> bottom,
			Object replaceVar,
			JavaPEGCFGBlock block) {
		if (root.equals(bottom)) {
			return block.getMiniPEG().getVertex(
					Item.<JavaLabel,JavaParameter,Object>getVariable(replaceVar));
		} else {
			// not bottom, build new
			List<Vertex<Item<JavaLabel,JavaParameter,Object>>> children = 
				new ArrayList<Vertex<Item<JavaLabel,JavaParameter,Object>>> ();
			for (int i = 0; i < root.getChildCount(); i++) {
				children.add(copyUpTo(root.getChild(i), bottom, replaceVar, block));
			}
			return block.getMiniPEG().getVertex(root.getLabel(), children);
		}
	}
	*/
	
	
	/**
	 * Fixes sticky problems in the CFG.
	 * Should only be necessary for non-constant sticky children
	 * (i.e. params and dims)
	 */
	private void fixSticky() {
		final StickyPredicate<JavaLabel> sticky = JavaLabelStickyPredicate.INSTANCE;
		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			// find the root vertexes
			final Set<Vertex<Item<JavaLabel,JavaParameter,Object>>> roots = 
				new HashSet<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
			for (Object outputVar : block.getAssignedVars())
				roots.add(block.getAssignment(outputVar));
			if (block.getBranchCondition() != null)
				roots.add(block.getBranchCondition());
			
			// iterate over all vertexes
			for (Vertex<Item<JavaLabel,JavaParameter,Object>> vertex : 
				new VertexIterable(roots)) {
				if (!vertex.getLabel().isLabel())
					continue;

				final JavaLabel parentLabel = vertex.getLabel().getLabel();
				for (int i = 0; i < vertex.getChildCount(); i++) {
					if (!sticky.isSticky(parentLabel, i)) 
						continue;
					if (!vertex.getChild(i).getLabel().isLabel() ||
						!sticky.allowsChild(parentLabel, i, vertex.getChild(i).getLabel().getLabel())) {
						// sticky not satisfied!
						Object childVar = vertex.getChild(i).getLabel().getVariable();
						
						Set<Pair<JavaPEGCFGBlock,Object>> pairs = 
							findLastAssignments(childVar, block);
						if (pairs.size() == 0)
							throw new RuntimeException("No assignments found");
						
						int childCount = -1;
						Item<JavaLabel,JavaParameter,Object> item = null;
						for (Pair<JavaPEGCFGBlock,Object> pair : pairs) {
							Vertex<Item<JavaLabel,JavaParameter,Object>> pairvertex = 
								pair.getFirst().getAssignment(pair.getSecond());
							if (item == null) {
								item = pairvertex.getLabel();
								childCount = pairvertex.getChildCount();
							}
							else if (!item.equals(pairvertex.getLabel()) ||
									 childCount != pairvertex.getChildCount())
								throw new RuntimeException("Mismatched items: " + item + " vs " + pairvertex.getLabel());
						}
						
						if (childCount == 0) {
							// constant, just copy it
							vertex.setChild(i, block.getMiniPEG().getVertex(item));
						} else {
							/* Non-constant (i.e. params or dims).
							 * Assign its children to fresh vars in the 
							 * assignment block, then read from them in the 
							 * current block.
							 */
							List<Vertex<Item<JavaLabel,JavaParameter,Object>>> children = 
								new ArrayList<Vertex<Item<JavaLabel,JavaParameter,Object>>>();
							for (int j = 0; j < childCount; j++) {
								Object freshVar = this.cfg.makeNewTemporary();
								
								for (Pair<JavaPEGCFGBlock,Object> pair : pairs) {
									Vertex<Item<JavaLabel,JavaParameter,Object>> pairvertex = 
										pair.getFirst().getAssignment(pair.getSecond());
									pair.getFirst().setAssignment(
											freshVar, 
											pairvertex.getChild(j));
								}
								
								children.add(block.getMiniPEG().getVertex(
										Item.<JavaLabel,JavaParameter,Object>getVariable(freshVar)));
							}
							vertex.setChild(
									i, 
									block.getMiniPEG().getVertex(item, children));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Finds the block with the most recent assignment of 'assignVar', 
	 * starting backwards from 'startBlock'. Assumes that startBlock has a get of
	 * assignVar.
	 * Returns a pair of (block,var) because the final block might be an 
	 * assignment of a different var (for sets that point to gets)
	 */
	private Set<Pair<JavaPEGCFGBlock,Object>> findLastAssignments(
			Object assignVar, JavaPEGCFGBlock startBlock) {
		Set<Pair<JavaPEGCFGBlock,Object>> seen = 
			new HashSet<Pair<JavaPEGCFGBlock,Object>>();
		LinkedList<Pair<JavaPEGCFGBlock,Object>> worklist = 
			new LinkedList<Pair<JavaPEGCFGBlock,Object>>();
		for (JavaPEGCFGBlock pred : this.cfg.getPreds(startBlock))
			worklist.addLast(new Pair<JavaPEGCFGBlock, Object>(pred, assignVar));
		
		final Set<Pair<JavaPEGCFGBlock,Object>> result = 
			new HashSet<Pair<JavaPEGCFGBlock,Object>> ();
		
		while (worklist.size() > 0) {
			Pair<JavaPEGCFGBlock,Object> next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			final JavaPEGCFGBlock hisblock = next.getFirst();
			final Object hisvar = next.getSecond();
			
			Object newvar;
			if (hisblock.getAssignedVars().contains(hisvar)) {
				// found an assignment
				if (hisblock.getAssignment(hisvar).getLabel().isVariable()) {
					// assigned value is another var, recurse
					newvar = hisblock.getAssignment(hisvar).getLabel().getVariable();
				} else {
					// assigned value is not a var, add to results list
					result.add(next);
					continue;
				}
			} else {
				// no assignment, continue moving backwards
				newvar = hisvar;
			}
			
			for (JavaPEGCFGBlock pred : this.cfg.getPreds(hisblock))
				worklist.addLast(new Pair<JavaPEGCFGBlock, Object>(pred, newvar));
		}
		
		return result;
	}
	
	
	private class VertexIterable extends 
	peggy.analysis.VertexIterable<Vertex<Item<JavaLabel,JavaParameter,Object>>> {
		public VertexIterable(
				Collection<? extends Vertex<Item<JavaLabel, JavaParameter, Object>>> roots) {
			super(roots);
		}
		protected Collection<? extends Vertex<Item<JavaLabel, JavaParameter, Object>>> getChildren(
				Vertex<Item<JavaLabel, JavaParameter, Object>> v) {
			return v.getChildren();
		}
	}
	
	
	/**
	 * Creates a new variable and reroutes all assignments to 
	 * the return variable to be assignments to the new variable.
	 * Then makes a new end block that sets the return var to the new var.
	 */
	private boolean makeReturnLast() {
		final Object newvar = this.cfg.makeNewTemporary();
		final Object returnVar = this.cfg.getReturnVariable(JavaReturn.VALUE);
		
		Pattern<Vertex<Item<JavaLabel,JavaParameter,Object>>> returnVarPattern = 
			new AbstractPattern<Vertex<Item<JavaLabel,JavaParameter,Object>>>() {
				public boolean matches(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
					return vertex.getLabel().isVariable() &&
					vertex.getLabel().getVariable().equals(returnVar);
				}
			};
			
		Function<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>> builder = 
			new Function<Vertex<Item<JavaLabel,JavaParameter,Object>>,Vertex<Item<JavaLabel,JavaParameter,Object>>>() {
				public Vertex<Item<JavaLabel,JavaParameter,Object>> get(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
					return vertex.getGraph().getVertex(
							Item.<JavaLabel,JavaParameter,Object>getVariable(newvar));
				}
			};
			
		boolean hasAssigns = false;
		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			if (block.getAssignedVars().contains(returnVar)) {
				hasAssigns = true;
				break;
			}
		}
		if (!hasAssigns) {
			return false;
		}
			
		for (JavaPEGCFGBlock block : this.cfg.getBlocks()) {
			if (block.getAssignedVars().contains(returnVar)) {
				block.setAssignment(newvar, block.getAssignment(returnVar));
				block.removeAssignment(returnVar);
			}
			replaceInBlock(returnVarPattern, block, builder);
		}
		
		JavaPEGCFGBlock newend = this.cfg.makeNewBlock();
		this.cfg.getEndBlock().addSucc(newend);
		newend.setAssignment(
				returnVar,
				newend.getMiniPEG().getVertex(
						Item.<JavaLabel,JavaParameter,Object>getVariable(newvar)));
		this.cfg.setEndBlock(newend);
		return true;
	}
}
