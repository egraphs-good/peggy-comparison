package peggy.represent.java;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.represent.java.CFG2Soot.BB;
import soot.Body;
import soot.IntType;
import soot.PatchingChain;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.ConditionExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractJimpleIntBinopExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JXorExpr;
import soot.jimple.internal.JimpleLocal;

/**
 * This class simplifies the CFG.
 */
public class CFGSimplifier{
	private CFG2Soot cfg;
	private SootMethod method;
	
	public CFGSimplifier(CFG2Soot _cfg, SootMethod _method){
		cfg = _cfg;
		method = _method;
		createNewBody();
		serialize();

		
		//printLocals(System.out, method.retrieveActiveBody());
		//System.out.println("//////////////");
		//printUnits(System.out, method.retrieveActiveBody());
		//System.out.println("//////////////");
	}

	
	/** This is a simple test to determine whether or not the given method
	 *  contains any exceptions.
	 */ 
	private boolean containsExceptions(){
		for (BB block : (Set<BB>)cfg.getBlocks()){
			for (Unit unit : block.statements){
				if (unit instanceof JAssignStmt){
					JAssignStmt assign = (JAssignStmt)unit; 
					if (assign.getRightOp() instanceof InjectLeftExpr)
						return true;
				}
				else if (unit instanceof JIfStmt){
					JIfStmt ifstmt = (JIfStmt)unit;
					if (ifstmt.getCondition() instanceof IsExceptionExpr)
						return true;
				}
			}
		}
		return false;
	}
	
	
	/** This method is similar to redirectBranches, except it acts on the BBs
	 *  in the CFG2Soot instance, and not on the sequential code in the Soot Body.
	 */
	private void redirectBlockBranches(Unit oldTarget, Unit newTarget){
		for (BB block : (Set<BB>)cfg.getBlocks()){
			for (Unit unit : block.statements){
				SootUtils.redirectBranch(unit, oldTarget, newTarget);
			}
		}
	}
	
	
	
	
	private void createNewBody(){
		if (containsExceptions())
			throw new RuntimeException("This method contains exceptions");
		
		for (BB block : (Set<BB>)cfg.getBlocks()){
			for (int i=0;i<block.statements.size();i++){
				Unit unit = block.statements.get(i);
				if (unit instanceof ResolveStmt){
					ResolveStmt resolve = (ResolveStmt)unit;
					
					if (resolve.getRHS() instanceof ThrowExpr)
						throw new RuntimeException("This method contains exceptions");
					else if (resolve.getRHS() instanceof EntermonitorExpr){
						EntermonitorExpr enter = (EntermonitorExpr)resolve.getRHS();
						
						JEnterMonitorStmt replace = new JEnterMonitorStmt(enter.getOp());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
					else if (resolve.getRHS() instanceof ExitmonitorExpr){
						ExitmonitorExpr exit = (ExitmonitorExpr)resolve.getRHS();
						
						JExitMonitorStmt replace = new JExitMonitorStmt(exit.getOp());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
					else if (resolve.getRHS() instanceof SetarrayExpr){
						SetarrayExpr set = (SetarrayExpr)resolve.getRHS();
						
						JAssignStmt replace = new JAssignStmt(new JArrayRef(set.getArray(), set.getIndex()), set.getValue());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
					else if (resolve.getRHS() instanceof SetfieldExpr){
						SetfieldExpr set = (SetfieldExpr)resolve.getRHS();
						
						JAssignStmt replace = new JAssignStmt(new JInstanceFieldRef(set.getBase(), set.getFieldRef()), set.getValue());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
					else if (resolve.getRHS() instanceof SetstaticfieldExpr){
						SetstaticfieldExpr set = (SetstaticfieldExpr)resolve.getRHS();
						
						JAssignStmt replace = new JAssignStmt(Jimple.v().newStaticFieldRef(set.getFieldRef()), set.getValue());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
					else if (resolve.getRHS() instanceof InvokeExpr){
						InvokeExpr invoke = (InvokeExpr)resolve.getRHS();
						
						if (invoke.getMethod().getReturnType().equals(VoidType.v())){
							JInvokeStmt replace = new JInvokeStmt(invoke);
							block.statements.set(i, replace);
							redirectBlockBranches(unit, replace);
						}else{
							JAssignStmt replace = new JAssignStmt(resolve.getException(), invoke);
							block.statements.set(i, replace);
							redirectBlockBranches(unit, replace);
						}
					}
					else{
						JAssignStmt replace = new JAssignStmt(resolve.getException(), resolve.getRHS());
						block.statements.set(i, replace);
						redirectBlockBranches(unit, replace);
					}
				}
				
				else if (unit instanceof JAssignStmt){
					JAssignStmt assign = (JAssignStmt)unit;
					
					boolean progress = true;
					while (progress){
						progress = false;
						
						if (assign.getRightOp() instanceof Rho1Expr){
							Rho1Expr rho1 = (Rho1Expr)assign.getRightOp();
							assign.setRightOp(rho1.getOp());
							progress = true;
						}
						else if (assign.getRightOp() instanceof GetExceptionExpr ||
								 assign.getRightOp() instanceof VoidExpr){
							((JimpleLocal)assign.getLeftOp()).setType(IntType.v());
							assign.setRightOp(IntConstant.v(0));
							break;
						}
						else if (assign.getRightOp() instanceof Rho2Expr || 
								 assign.getRightOp() instanceof GetSigmaExpr){
							((JimpleLocal)assign.getLeftOp()).setType(IntType.v());
							assign.setRightOp(IntConstant.v(0));
							break;
						}
						else if (assign.getRightOp() instanceof GetValueExpr){
							GetValueExpr getvalue = (GetValueExpr)assign.getRightOp();
							assign.setRightOp(getvalue.getOp());
							progress = true;
						}
						else if (assign.getRightOp() instanceof InjectRightExpr){
							InjectRightExpr inject = (InjectRightExpr)assign.getRightOp();
							assign.setRightOp(inject.getOp());
							progress = true;
						}
					}
					
					
					if (assign==null){
						if (i==block.statements.size()-1){
							Unit next = new JNopStmt();
							block.statements.add(next);
							block.statements.remove(i);
							redirectBlockBranches(unit, next);
						}else{
							Unit next = block.statements.get(i+1);
							block.statements.remove(i);
							redirectBlockBranches(unit, next);
							i--;
						}
					}
				}
				
				else if (unit instanceof JReturnStmt && method.getReturnType().equals(VoidType.v())){
					JReturnVoidStmt replace = new JReturnVoidStmt();
					block.statements.set(i, replace);
					redirectBlockBranches(unit, replace);
				}
			}
			
			
			// now add gotos after every if
			if (block.statements.get(block.statements.size()-1) instanceof JIfStmt){
				BB fallthroughblock = (BB)cfg.getChildren(block).get(1);
				
				JGotoStmt gotoafterif = new JGotoStmt(fallthroughblock.statements.get(0));
				block.statements.add(gotoafterif);
			}
		}
	}
	
	
	
	private void reverseDFS(BB node, LinkedList<BB> stack, Set<BB> visited){
		if (visited.contains(node))
			return;
		
		visited.add(node);
		for (BB child : (List<BB>)cfg.getChildren(node)){
			reverseDFS(child, stack, visited);
		}
		
		stack.addLast(node);
	}
	
	
	
	
	/** This method will serialize the BBs into a sequential body
	 *  of code. Since every block ends in a branch (or a return),
	 *  the serialization process does not need to worry about fallthrough blocks.
	 *  
	 *  After serialization completes, several cleanup methods will sanitize the code.
	 */
	private void serialize(){
		Body body = method.retrieveActiveBody();
		body.getUnits().clear();
		
		Set<BB> visited = new HashSet<BB>();
		LinkedList<BB> stack = new LinkedList<BB>();
		BB source = cfg.getStart();
		
		reverseDFS(source, stack, visited);
		
		
		while (stack.size()>0){
			BB next = stack.removeLast();
			
			for (Unit unit : next.statements){
				body.getUnits().addLast(unit);
			}
		}
		// now all the statements are in the body!
		// clean them up a bit

		SootUtils.removeTags(body);
		addIdentityRefs(body);
        removeReturnVoid(body);
        
		verifyLocalTypes(body, false);
		
		
		{// do dead assignment elimination
			soot.jimple.toolkits.scalar.DeadAssignmentEliminator dae = 
				soot.jimple.toolkits.scalar.DeadAssignmentEliminator.v();
			dae.transform(body);
		}
        
        replaceConditionAssignments(body);

        
        {// do copy propagation
        	soot.jimple.toolkits.scalar.CopyPropagator cp = 
        		soot.jimple.toolkits.scalar.CopyPropagator.v();
        	cp.transform(body);
        }
        
        {// do dead assignment elimination
        	soot.jimple.toolkits.scalar.DeadAssignmentEliminator dae = 
        		soot.jimple.toolkits.scalar.DeadAssignmentEliminator.v();
        	dae.transform(body);
        }
		  
        
        {// do some unconditional branch folding
        	soot.jimple.toolkits.scalar.UnconditionalBranchFolder ubf =  
        		soot.jimple.toolkits.scalar.UnconditionalBranchFolder.v();
        	ubf.transform(body);
        }

        updateLocalChain(body);		
		verifyLocalTypes(body, true);
		
		//removeDumbNegations(body);   // this method is not quite accurate, rethink it!
	}
	

	
	
	
	/**
	 * This method is not sound. It needs to be re-thought-out.
	 * DO NOT USE!
	 */
	private void removeDumbNegations(Body body) {
		Map<Unit,Set<Unit>> targetterMap = SootUtils.buildTargetterMap(body);

		progressloop:
		for (boolean progress=true; progress; ) {
			progress = false;

			PatchingChain units = body.getUnits();
			Unit current = (Unit)units.getFirst();
			
			LinkedList<Unit> lastgroup = new LinkedList<Unit>();
			for (int i = 0; i < 5 && current!=null; i++) {
				lastgroup.addLast(current);
				current = (Unit)units.getSuccOf(current);
			}
			if (lastgroup.size() < 5)
				break;
		
			JAssignStmt replace;
			if ((replace = foundNegation(lastgroup, targetterMap)) != null) {
				// remove the next 4 instructions
				Unit oldstmt = lastgroup.get(0);
				units.insertBefore(replace, oldstmt);
				if (targetterMap.containsKey(oldstmt)) {
					for (Unit targetter : targetterMap.get(oldstmt)) {
						SootUtils.redirectBranch(targetter, oldstmt, replace);
					}
				}
				for (int i = 0; i < lastgroup.size() - 1; i++) {
					units.remove(lastgroup.get(i));
				}
				progress = true;
				continue progressloop;
			}

			while (current != null) {
				lastgroup.removeFirst();
				lastgroup.addLast(current);

				if ((replace = foundNegation(lastgroup, targetterMap)) != null) {
					// remove the next 4 instructions
					Unit oldstmt = lastgroup.get(0);
					units.insertBefore(replace, oldstmt);
					if (targetterMap.containsKey(oldstmt)) {
						for (Unit targetter : targetterMap.get(oldstmt)) {
							SootUtils.redirectBranch(targetter, oldstmt, replace);
						}
					}
					for (int i = 0; i < lastgroup.size() - 1; i++) {
						units.remove(lastgroup.get(i));
					}
					progress = true;
					continue progressloop;
				}

				current = (Unit)units.getSuccOf(current);
			}
		}
	}
	private JAssignStmt foundNegation(LinkedList<Unit> lastgroup, Map<Unit,Set<Unit>> targetterMap) {
		// instr 0
		if (!(lastgroup.get(0) instanceof JIfStmt))
			return null;
		JIfStmt ifstmt = (JIfStmt)lastgroup.get(0);
			
		Value condition = ifstmt.getCondition();
		boolean eq;
		if (condition instanceof JEqExpr) {
			eq = true;
		} else if (condition instanceof JNeExpr) {
			eq = false;
		} else {
			return null;
		}
		AbstractJimpleIntBinopExpr expr = (AbstractJimpleIntBinopExpr)condition;
		
		Value lhs = expr.getOp1();
		Value rhs = expr.getOp2();
		JimpleLocal readlocal;
		if (lhs.equals(IntConstant.v(0)) && rhs instanceof JimpleLocal) {
			readlocal = (JimpleLocal)rhs;
		} else if (rhs.equals(IntConstant.v(0)) && lhs instanceof JimpleLocal) {
			readlocal = (JimpleLocal)lhs;
		} else {
			return null;
		}
		
		// instr 1
		if (!(lastgroup.get(1) instanceof JAssignStmt))
			return null;
		JAssignStmt assign0 = (JAssignStmt)lastgroup.get(1);
		
		Value target0 = assign0.getLeftOp();
		Value value0 = assign0.getRightOp();
		if (!(target0 instanceof JimpleLocal))
			return null;
		if (!value0.equals(IntConstant.v(0)))
			return null;
		JimpleLocal writelocal = (JimpleLocal)target0;
		
		// instr 2
		if (!(lastgroup.get(2) instanceof JGotoStmt))
			return null;
		JGotoStmt gotostmt = (JGotoStmt)lastgroup.get(2);
		
		// instr 3
		if (!(lastgroup.get(3) instanceof JAssignStmt))
			return null;
		JAssignStmt assign1 = (JAssignStmt)lastgroup.get(3);
		
		Value target1 = assign1.getLeftOp();
		Value value1 = assign1.getRightOp();
		if (!(target1 instanceof JimpleLocal && SootUtils.jimpleLocalsEqual(writelocal, (JimpleLocal)target1)))
			return null;
		if (!value1.equals(IntConstant.v(1)))
			return null;
		
		// compare targets:
		// ifstmt should point to assign1,
		// gotostmt should point to stmt4
		
		Unit stmt4 = lastgroup.get(4);
		
		if (!(targetterMap.containsKey(assign1) &&
			  targetterMap.get(assign1).contains(ifstmt)))
			return null;
		if (!(targetterMap.containsKey(stmt4) &&
			  targetterMap.get(stmt4).contains(gotostmt)))
			return null;
		
		// assign0 and gotostmt should have no targetters
		if (targetterMap.containsKey(gotostmt) || targetterMap.containsKey(assign0))
			return null;
		
		// verified the pattern!
		if (eq) {
			return new JAssignStmt(writelocal, new JXorExpr(readlocal, IntConstant.v(1)));
		} else {
			return new JAssignStmt(writelocal, readlocal);
		}
	}
	

	
	private void replaceConditionAssignments(Body body){
		// implement hack!!
		// check for a block that did NOT end in a branch, but still has a "V = op1 REL op2"
		// if found, replace with 
		//		if (op1 REL op2)
		//			V = 1;
		//		else
		//			V = 0;

		PatchingChain units = body.getUnits();
		for (Unit current=(Unit)units.getFirst(); current!=null; current=(Unit)units.getSuccOf(current)){ 
			if (current instanceof JAssignStmt){
				JAssignStmt assign = (JAssignStmt)current;
				if (assign.getRightOp() instanceof ConditionExpr){
					// factor out into an if!
					
					// if (op1 REL op2) goto L1;
					// LHS = 0;
					// goto L2;
					//L1:
					// LHS=1;
					//L2:
					// nop;
					
					
					
					JimpleLocal LHS = (JimpleLocal)assign.getLeftOp();
					JAssignStmt LHS_0 = new JAssignStmt(LHS, IntConstant.v(0));
					JAssignStmt LHS_1 = new JAssignStmt(LHS, IntConstant.v(1));
					JNopStmt afternop = new JNopStmt();
					JGotoStmt gotonop = new JGotoStmt(afternop);
					JIfStmt ifrel = new JIfStmt(assign.getRightOp(), LHS_1);
					
					units.insertAfter(afternop, assign);
					units.insertAfter(LHS_1, assign);
					units.insertAfter(gotonop, assign);
					units.insertAfter(LHS_0, assign);
					units.insertAfter(ifrel, assign);
					
					units.remove(assign);
					redirectBranches(body, assign, ifrel);
					
					current = afternop;
				}
			}
		}
	}
	
	
	
	
	/** This method will add the initial JIdentityRefs that
	 *  will assign the ThisRef and ParameterRefs to local variables.
	 *  The local variables will be named "__this" and "__param"+i (0<=i<n)
	 *  for a method of n parameters. These instructions will be the first
	 *  ones in the method body.
	 */
	private void addIdentityRefs(Body body){
		// insert/reorder the initial refs (this/parameter)
		// insert the parameter locals
		
		for (int i=method.getParameterCount()-1;i>=0;i--){
        	Type type = method.getParameterType(i);
        	JimpleLocal paramlocal = null;
        	String name = "__param"+i;

			for (Iterator iter=body.getUseBoxes().iterator();iter.hasNext();){
				ValueBox box = (ValueBox)iter.next();
				if (box.getValue() instanceof JimpleLocal && ((JimpleLocal)box.getValue()).getName().equals(name)){
					paramlocal = (JimpleLocal)box.getValue();
					break;
				}
			}
			if (paramlocal==null){
				for (Iterator iter=body.getDefBoxes().iterator();iter.hasNext();){
					ValueBox box = (ValueBox)iter.next();
					if (!(box.getValue() instanceof JimpleLocal))
						continue;
					JimpleLocal local = (JimpleLocal)box.getValue();
					if (local.getName().equals(name)){
						paramlocal = local;
						break;
					}
				}
			}
			if (paramlocal==null)
				paramlocal = new JimpleLocal(name, type);
        	
        	ParameterRef ref = new ParameterRef(type, i);
        	Unit found = new JIdentityStmt(paramlocal, ref);
        	body.getUnits().addFirst(found);
        }

		
		
		if (!method.isStatic()){
			JimpleLocal thislocal = null;
			RefType type = method.getDeclaringClass().getType();

			for (Iterator iter=body.getUseBoxes().iterator();iter.hasNext();){
				ValueBox box = (ValueBox)iter.next();
				
				if (box.getValue() instanceof JimpleLocal && ((JimpleLocal)box.getValue()).getName().equals("__this")){
					thislocal = (JimpleLocal)box.getValue();
					break;
				}
			}
			if (thislocal==null){
				for (Iterator iter=body.getDefBoxes().iterator();iter.hasNext();){
					ValueBox box = (ValueBox)iter.next();
					if (!(box.getValue() instanceof JimpleLocal))
						continue;
					
					JimpleLocal local = (JimpleLocal)box.getValue();
					if (local.getName().equals("__this")){
						thislocal = local;
						break;
					}
				}
			}
			if (thislocal==null)
				thislocal = new JimpleLocal("__this", type);
		
			Unit found = new JIdentityStmt(thislocal, new ThisRef(type));
			body.getUnits().addFirst(found);
		}
	}

	
	/** In a modified CFG, all methods have non-void return statements.
	 *  This method will remove all assignments to the $$returnvalue variable
	 *  if we are optimizing a void method.
	 */
	private void removeReturnVoid(Body body){
        // if void, remove all assignments to $$returnvalue
        if (method.getReturnType().equals(VoidType.v())){
        	boolean progress = true;
        	while(progress){
        		progress = false;
        	
        		for (Iterator iter=body.getUnits().iterator();iter.hasNext();){
        			Unit unit = (Unit)iter.next();
        			if (!(unit instanceof JAssignStmt))
        				continue;
        			JAssignStmt assign = (JAssignStmt)unit;
        			if (!(assign.getLeftOp() instanceof JimpleLocal))
        				continue;
        			JimpleLocal LHS = (JimpleLocal)assign.getLeftOp();
        			if (LHS.getName().equals("$$returnvalue")){
        				Unit next = (Unit)body.getUnits().getSuccOf(unit);
        				redirectBranches(body, unit, next);
        				
        				body.getUnits().remove(unit);
        				progress = true;
        				break;
        			}
        		}
        	}
        }
	}
	
	
	/** This attempts to do some rudimentary dead assignment elimination.
	 *  Soot itself has this feature, so this method may be removed. 
	 */
	/*
	private void pruneUnusedDefs(Body body){
        // remove defs with no uses
        Set<JimpleLocal> usedlocals = new HashSet<JimpleLocal>();
        for (Iterator iter=body.getUseBoxes().iterator();iter.hasNext();){
        	ValueBox box = (ValueBox)iter.next();
        	if (box.getValue() instanceof JimpleLocal)
        		usedlocals.add((JimpleLocal)box.getValue());
        }
        
        for (Unit current = (Unit)body.getUnits().getFirst(); current!=null; ){
        	if (current instanceof JAssignStmt){
        		JAssignStmt assign = (JAssignStmt)current;
        		if (assign.getLeftOp() instanceof JimpleLocal){
        			if (!usedlocals.contains(assign.getLeftOp())){
        	        	current = (Unit)body.getUnits().getSuccOf(current);
        	        	
        	        	Unit next = (Unit)body.getUnits().getSuccOf(assign);
        	        	redirectBranches(body, assign, next);

        				body.getUnits().remove(assign);

        				
        				// recompute the usedlocals set
        		        usedlocals = new HashSet<JimpleLocal>();
        		        for (Iterator iter=body.getUseBoxes().iterator();iter.hasNext();){
        		        	ValueBox box = (ValueBox)iter.next();
        		        	if (box.getValue() instanceof JimpleLocal)
        		        		usedlocals.add((JimpleLocal)box.getValue());
        		        }
        				
        				continue;
        			}
        		}
        	}
        	current = (Unit)body.getUnits().getSuccOf(current);
        }
	}
	*/
	

	/** The soot Body requires an accurate list of the local variables
	 *  used in the method. This will update that list to contain 
	 *  exactly those vars that are referenced in the units of the body.
	 */
	private void updateLocalChain(Body body){
		// adjust the locals
		body.getLocals().clear();
		for (Iterator iter=body.getUseBoxes().iterator();iter.hasNext();){
			ValueBox box = (ValueBox)iter.next();
			if (box.getValue() instanceof JimpleLocal){
				if (!body.getLocals().contains(box.getValue())){
					body.getLocals().add(box.getValue());
				}
			}
		}
		for (Iterator iter=body.getDefBoxes().iterator();iter.hasNext();){
			ValueBox box = (ValueBox)iter.next();
			if (box.getValue() instanceof JimpleLocal){
				if (!body.getLocals().contains(box.getValue())){
					body.getLocals().add(box.getValue());
				}
			}
		}
	}
	
	
	/** This method examines the type of every local variable
	 *  that was assigned in the program. If the variable has no type,
	 *  the type is derived from the RHS of the assignment. This 
	 *  process iterates until no more progress can be made.
	 *  If there are untyped variables at the end of this process, an
	 *  exception is thrown.
	 */
	private void verifyLocalTypes(Body body, boolean enforce){
		// now make sure that every local has a type
		boolean anywithouttypes = true;
		boolean progress = true;
		while (anywithouttypes && progress){
			anywithouttypes = false;
			progress = false;
			for (Iterator iter=body.getUnits().iterator();iter.hasNext();){
				Unit unit = (Unit)iter.next();
				if (unit instanceof JAssignStmt){
					JAssignStmt assign = (JAssignStmt)unit;
					if (!(assign.getLeftOp() instanceof JimpleLocal))
						continue;
					JimpleLocal local = (JimpleLocal)assign.getLeftOp();
					if (local.getType()!=null)
						continue;
					
					Type righttype = assign.getRightOp().getType();
					if (righttype==null){
						anywithouttypes = true;
						continue;
					}
					local.setType(righttype);
					progress = true;
				}
				else if (unit instanceof JIdentityStmt){
					JIdentityStmt identity = (JIdentityStmt)unit;
					JimpleLocal local = (JimpleLocal)identity.getLeftOp();
					if (local.getType()==null){
						local.setType(identity.getRightOp().getType());
						progress = true;
					}
				}
			}
		}
		
		if (enforce && anywithouttypes)
			throw new RuntimeException("Cannot assign types to all locals!");
	}

	
	// for debugging
	private static void printUnits(java.io.PrintStream out, Body body){
		for (Iterator iter=body.getUnits().iterator();iter.hasNext();){
			Unit unit = (Unit)iter.next();
			out.println(unit);
		}
	}
	
	// for debugging
	private static void printLocals(java.io.PrintStream out, Body body){
		for (Iterator iter=body.getLocals().iterator();iter.hasNext();){
			JimpleLocal local = (JimpleLocal)iter.next();
			out.println("local "+local.getName() +" : "+local.getType());
		}
	}
	
	

	
	/** Within the given Body, redirect all branches from oldTarget to newTarget.
	 *  (This only affects gotos and ifs because there will be no switches.)
	 */
	private void redirectBranches(Body body, Unit oldTarget, Unit newTarget){
		for (Iterator iter=body.getUnits().iterator();iter.hasNext();){
			Unit unit = (Unit)iter.next();
			SootUtils.redirectBranch(unit, oldTarget, newTarget);
		}
	}
}
