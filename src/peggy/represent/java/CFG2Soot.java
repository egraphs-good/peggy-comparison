package peggy.represent.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.represent.NodeValue;
import soot.RefType;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.ConditionExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
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
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
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
import soot.jimple.internal.JShlExpr;
import soot.jimple.internal.JShrExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JUshrExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JXorExpr;
import soot.jimple.internal.JimpleLocal;
import util.AbstractVariaticFunction;
import util.Function;
import util.graph.CExpressionGraph;
import util.graph.CExpressionGraph.Vertex;
import eqsat.Block;
import eqsat.CFG;
import eqsat.CFGTranslator;

/**
 * This class converts a CFG instance to a soot cfg.
 */
public class CFG2Soot<G extends CFG<G,B,V,JavaLabel,JavaParameter,JavaReturn>, B extends Block<G,B,V,JavaLabel>, V>
implements peggy.pb.Digraph<CFG2Soot.BB> {
	public static class BB {
		public List<Unit> statements;
		BB(List<Unit> stmts) {
			statements = new ArrayList<Unit>(stmts);
		}
	}
	
	private static int NEXTVARID = 0;
	private static JimpleLocal nextVar(Type type){
		return new JimpleLocal("_T"+(NEXTVARID++)+"_", type);
	}
	
	///////////////////////////////////
	private BB source, sink;
	private final Map<BB,List<BB>> block2succs;
	private final CFG<G,B,V,JavaLabel,JavaParameter,JavaReturn> cfg;
	private final ReferenceResolver resolver;

	// this maps V's to their corresponding local variable names
	private final Map<V,String> var2name;
	
	// this maps the V's local variable names to their corresponding JimpleLocals.
	// this represents a lazy association of names to JimpleLocals, because I don't know
	// what types to give the JimpleLocals immediately
	private final Map<String,JimpleLocal> varname2local;
	
	public CFG2Soot(
			CFG<G,B,V,JavaLabel,JavaParameter,JavaReturn> _cfg,
			ReferenceResolver _resolver) {
		this.resolver = _resolver;
		this.cfg = _cfg;
		this.block2succs = new HashMap<BB,List<BB>>();

		// this is only needed to get the right varnames
		Map<V,JavaReturn> returnVmap = new HashMap<V,JavaReturn>();
		for (JavaReturn s : cfg.getReturns())
			returnVmap.put(cfg.getReturnVariable(s), s);
		
		// associate a local var name with each V
		var2name = new HashMap<V,String>();
		varname2local = new HashMap<String,JimpleLocal>();
//		int varid=0;
//		
//		for (V var : cfg.getVariables()) {
//			JavaParameter p = null;
//			try {
//				p = cfg.getParameter(var);
//			} catch (IllegalArgumentException iae) {}
//
//			if (p==null) {
//				if (returnVmap.containsKey(var)) {
//					JimpleLocal l = returnVmap.get(var).getJimpleLocal();
//					var2name.put(var, l.getName());
//					varname2local.put(l.getName(), l);
//				} else {
//					var2name.put(var, "variable"+(varid++));
//				}
//			} else {
//				var2name.put(var, p.getJimpleLocal().getName());
//				varname2local.put(p.getJimpleLocal().getName(), p.getJimpleLocal());
//			}
//		}
		
		buildBlocks();

//		sanitize();
	}
	
	
	////// Digraph methods ///////////
	public int getNodeCount() {return block2succs.size();}
	public Iterable<BB> getNodes() {return block2succs.keySet();}
	public Iterable<BB> getSuccessors(BB b) {return block2succs.get(b);}
	public peggy.pb.Digraph<BB> getReverseDigraph() {
		return new peggy.pb.Digraph<BB>() {
			public int getNodeCount() {return block2succs.size();}
			public Iterable<BB> getNodes() {return block2succs.keySet();}
			public Iterable<BB> getSuccessors(BB b) {
				List<BB> parents = new ArrayList<BB>();
				for (BB block : block2succs.keySet()) {
					if (block2succs.get(block).contains(b))
						parents.add(block);
				}
				return parents;
			}
			public peggy.pb.Digraph<BB> getReverseDigraph() {return CFG2Soot.this;}
		};
	}
	//////////////////////////////
	
	// Accessors
	public Set<BB> getBlocks() {return block2succs.keySet();}
	public BB getStart() {return source;}
	public BB getEnd() {return sink;}
	public java.util.List<BB> getChildren(BB b) {return block2succs.get(b);}

	
//	/** This method just cleans up the instructions a bit
//	 */
//	private void sanitize(){
//		Map<BB,Integer> block2preds = new HashMap<BB,Integer>();
//		for (BB bb : block2succs.keySet()){
//			block2preds.put(bb, 0);
//		}
//		
//		// establish the source and sink
//		for (BB block : block2succs.keySet()){
//			List<BB> succs = block2succs.get(block);
//			if (succs.size()==0){
//				sink = block;
//			}else{
//				for (BB succ : succs){
//					block2preds.put(succ, block2preds.get(succ).intValue()+1);
//				}
//			}
//		}
//		for (BB block : block2preds.keySet()){
//			if (block2preds.get(block)==0){
//				source = block;
//				break;
//			}
//		}
//		if (source==null || sink==null)
//			throw new RuntimeException("Cannot find source/sink");
//		
//		
//		
//		
//		// remove all assignments "SIGMA = rho2(T0)"   with "T0 == invoke" earlier in the block
//		for (BB block : block2succs.keySet()){
//			List<Unit> stmts = block.statements;
//			for (int i=0;i<stmts.size();i++){
//				Unit current = stmts.get(i);
//				if (!(current instanceof JAssignStmt))
//					continue;
//				JAssignStmt assign = (JAssignStmt)current;
//				if (!(assign.getLeftOp() instanceof JimpleLocal && assign.getRightOp() instanceof Rho2Expr))
//					continue;
//				JimpleLocal LHS = (JimpleLocal)assign.getLeftOp();
//				Rho2Expr RHS = (Rho2Expr)assign.getRightOp();
//				if (!(RHS.getOp() instanceof JimpleLocal && LHS.equals(SootVariable.SIGMA.getJimpleLocal())))
//					continue;
//				JimpleLocal OP = (JimpleLocal)RHS.getOp();
//				// found a "$$SIGMA$$ = rho2(OP)"
//				
//				// look for a "OP == invoke"
//				for (int j=i-1;j>=0;j--){
//					Unit above = stmts.get(j);
//					if (!(above instanceof ResolveStmt))
//						continue;
//					ResolveStmt resolve = (ResolveStmt)above;
//					if (!(resolve.getRHS() instanceof InvokeExpr))
//						continue;
//					if (!resolve.getException().equals(OP))
//						continue;
//					// found "OP == invoke"
//					
//					// replace assign with "SIGMA = null"
//					//LHS.setType(soot.IntType.v());
//					assign.setRightOp(NullConstant.v());
//					break;
//				}
//			}
//		}
//		
//
//		/*
//		// now remove all the "V = rho1(T0)" where "T0 == invoke" is earlier in the block
//		for (BB block : block2succs.keySet()){
//			List<Unit> stmts = block.statements;
//			for (int i=0;i<stmts.size();i++){
//				Unit current = stmts.get(i);
//				if (!(current instanceof JAssignStmt))
//					continue;
//				JAssignStmt assign = (JAssignStmt)current;
//				if (!(assign.getLeftOp() instanceof JimpleLocal && assign.getRightOp() instanceof Rho1Expr))
//					continue;
//				Rho1Expr rho1 = (Rho1Expr)assign.getRightOp();
//				if (!(rho1.getOp() instanceof JimpleLocal))
//					continue;
//				JimpleLocal T0 = (JimpleLocal)rho1.getOp();
//				// found a "V = rho1(T0)"
//				
//				// look for a "T0 == invoke"
//				for (int j=i-1;j>=0;j--){
//					Unit above = stmts.get(j);
//					if (!(above instanceof ResolveStmt))
//						continue;
//					ResolveStmt resolve = (ResolveStmt)above;
//					if (!(resolve.getRHS() instanceof InvokeExpr))
//						continue;
//					if (!resolve.getException().equals(T0))
//						continue;
//					// found "T0 == invoke"
//					
//					assign.setRightOp(T0);
//					break;
//				}
//			}
//		}
//		*/
//		
//		
//		
//		
//		/*
//		// move caught ResolveStmts to the end of their blocks
//		for (BB block : block2succs.keySet()){
//			List<BB> succs = block2succs.get(block);
//			if (succs.size()!=1)
//				continue;
//			BB succ = succs.get(0);
//			if (!(succ.statements.get(0) instanceof JIfStmt))
//				continue;
//			JIfStmt ifstmt = (JIfStmt)succ.statements.get(0);
//			if (!(ifstmt.getCondition() instanceof IsExceptionExpr))
//				continue;
//			// know it's an exception branch!
//			
//			IsExceptionExpr condition = (IsExceptionExpr)ifstmt.getCondition();
//			JimpleLocal var = (JimpleLocal)condition.getOp();
//			
//
//			// find the ResolveStmt in 'block' that assigns to 'var'
//			int index = -1;
//			List<Unit> stmts = block.statements;
//			for (int i=stmts.size()-1;i>=0;i--){
//				Unit current = stmts.get(i);
//				if (!(current instanceof ResolveStmt))
//					continue;
//				ResolveStmt unit = (ResolveStmt)current;
//				if (unit.getException().equals(var)){
//					// found it!
//					index = i;
//					break;
//				}
//			}
//
//			if (index==-1){
//				// this is not the block with the resolve (the if is a later if)
//				continue;
//			}
//
//
//			Unit last = stmts.get(stmts.size()-1);
//			if (last instanceof JGotoStmt){
//				Unit unit = stmts.remove(index);
//				stmts.add(stmts.size()-1, unit);
//			}else{
//				// move it to the end of the stmt list
//				Unit unit = stmts.remove(index);
//				stmts.add(unit);
//			}
//		}
//		*/
//	}
	
	
	public void dump(java.io.PrintStream out){
		out.println("digraph {");
		for (BB block : block2succs.keySet()){
			out.print("   "+System.identityHashCode(block)+" [shape=box, label=\"Block:");
			for (Unit stmt : block.statements){
				out.print("\\n"+stmt);
			}
			out.println("\"];");
			
			int i=0;
			for (BB succ : block2succs.get(block)){
				out.println("   "+System.identityHashCode(block)+" -> "+System.identityHashCode(succ)+" [label=\""+i+"\"];");
				i++;
			}
		}
		out.println("}");
	}
	
	private void buildBlocks(){
		Map<B,BB> old2new = new HashMap<B,BB>();
		
		for (B block : cfg.getVertices()){
			BB newblock = translateBack(block);
			old2new.put(block, newblock);
			block2succs.put(newblock, new ArrayList<BB>(block.getChildCount()));
		}
		
		for (B block : cfg.getVertices()){
			BB newblock = old2new.get(block);
			List<BB> children = block2succs.get(newblock);
			for (B child : block.getChildren()){
				children.add(old2new.get(child));
			}
		}
		
		
		// find the exception vars
		Set<JimpleLocal> exceptionvars = new HashSet<JimpleLocal>();
		for (BB block : block2succs.keySet()){
			for (Unit unit : block.statements){
				if (unit instanceof JAssignStmt){
					JAssignStmt assign = (JAssignStmt)unit;
					if (assign.getRightOp() instanceof GetExceptionExpr){
						exceptionvars.add((JimpleLocal)((GetExceptionExpr)assign.getRightOp()).getOp());
					}
				}
				else if (unit instanceof JIfStmt){
					JIfStmt ifstmt = (JIfStmt)unit;
					if (ifstmt.getCondition() instanceof IsExceptionExpr){
						exceptionvars.add((JimpleLocal)((IsExceptionExpr)ifstmt.getCondition()).getOp());
					}
				}
			}
		}
		
		
		// find the local var to use in the return statement 
		JimpleLocal returnlocal = null;
		for (JavaReturn r : cfg.getReturns()){
			if (r.isSigma())
				continue;
			V returnvar = cfg.getReturnVariable(r);
			String varname = var2name.get(returnvar);
			returnlocal = varname2local.get(varname);
			break;
		}
		if (returnlocal==null)
			throw new RuntimeException("Can't find the return variable");
		
		
		
		
		// now add gotos to empty blocks
		for (BB block : block2succs.keySet()){
			List<Unit> stmts = block.statements;
			if (stmts.size()==0 || !(stmts.get(stmts.size()-1) instanceof JIfStmt)){
				if (block2succs.get(block).size()==0){
					// this is the return block!
					Unit returnstmt = new JReturnStmt(returnlocal);
					stmts.add(returnstmt);
				}else{
					Unit endgoto = new JGotoStmt(new JNopStmt());
					stmts.add(endgoto);
				}
			}
		}
		
		// now redirect the branch targets
		for (BB block : block2succs.keySet()){
			List<BB> succs = block2succs.get(block);
			List<Unit> stmts = block.statements;
			
			switch(succs.size()){
			case 0:{
				// do nothing! it's the return block!
				break;
			}
				
			case 1:{ // goto
				JGotoStmt last = (JGotoStmt)stmts.get(stmts.size()-1);
				BB mysucc = succs.get(0);
				last.setTarget(mysucc.statements.get(0));
				break;
			}
				
			case 2:{ // if
				JIfStmt last = (JIfStmt)stmts.get(stmts.size()-1);
				last.setTarget(succs.get(0).statements.get(0));
				break;
			}
				
			default:
				throw new RuntimeException("Block cannot have >2 successors");
			}
		}
	}

	
	
	private class ParamConverter implements Function<JavaParameter,Vertex<NodeValue<V,JavaLabel,JavaParameter>>> {
		private CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> model;
		public ParamConverter(CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> _model) {
			model = _model;
		}
		public Vertex<NodeValue<V,JavaLabel,JavaParameter>> get(JavaParameter v){
			if (v.isArgument() ||
				v.isThis() ||
				v.isSigma()) {
				return model.getVertex(NodeValue.<V,JavaLabel,JavaParameter>makeP(v));
			}
				
			throw new RuntimeException("Not a parameter!");
		}
	}
	
	private class Converter extends AbstractVariaticFunction<JavaLabel,
															 Vertex<NodeValue<V,JavaLabel,JavaParameter>>,
															 Vertex<NodeValue<V,JavaLabel,JavaParameter>>>{
		private CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> model;
		public Converter(CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> _model){
			model = _model;
		}
		public Vertex<NodeValue<V,JavaLabel,JavaParameter>> get(JavaLabel label, 
								 				  List<? extends Vertex<NodeValue<V,JavaLabel,JavaParameter>>> children){
			return model.getVertex(NodeValue.<V,JavaLabel,JavaParameter>makeL(label), children);
		}
	}
	
	
	private class Inputs implements Function<V,Vertex<NodeValue<V,JavaLabel,JavaParameter>>>{
		private CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> model;
		public Inputs(CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> _model){
			model = _model;
		}
		public Vertex<NodeValue<V,JavaLabel,JavaParameter>> get(V v){
			return model.getVertex(NodeValue.<V,JavaLabel,JavaParameter>makeV(v));
		}
	}
	

	/** This method will convert a single basic block 
	 *  into a list of Soot units.
	 */
	private BB translateBack(B block){
		CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>> model
		= new CExpressionGraph<NodeValue<V,JavaLabel,JavaParameter>>();
		CFGTranslator<B,V,Vertex<NodeValue<V,JavaLabel,JavaParameter>>> translator = 
			cfg.getTranslator(new ParamConverter(model), new Converter(model), 
					new ArrayList<Vertex<NodeValue<V,JavaLabel,JavaParameter>>>());
		
		Function<V,Vertex<NodeValue<V,JavaLabel,JavaParameter>>> outputs = 
			translator.getOutputs(block, new Inputs(model));

		// build a map from vertices to the list of variables whose expression value
		// is rooted at that vertex (more that one variable can use the same vertex)
		Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,List<V>> rootmap = 
			new HashMap<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,List<V>>();
		for (V var : cfg.getVariables()){
			if (block.modifies(var)){
				Vertex<NodeValue<V,JavaLabel,JavaParameter>> key = outputs.get(var);

				List<V> varlist = rootmap.get(key);
				if (varlist==null){
					varlist = new ArrayList<V>();
					rootmap.put(key, varlist);
				}
				varlist.add(var);
			}
		}

		// this is to cache already-computed values
		Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>, Value> valuecache = 
			new HashMap<Vertex<NodeValue<V,JavaLabel,JavaParameter>>, Value>();
		
		// this is the list of Units that are the Jimple equivalent of the expressions
		List<Unit> stmts = new ArrayList<Unit>(50);

		// build a map from read/written parameters to new temporary vars
		Map<V,JimpleLocal> parammap = new HashMap<V,JimpleLocal>();
		for (Vertex<NodeValue<V,JavaLabel,JavaParameter>> vertex : model.getVertices()){
			if (vertex.getLabel().isV()){
				NodeValue<V,JavaLabel,JavaParameter> nv = vertex.getLabel();
				JavaParameter p = null;
				try {
					p = cfg.getParameter(nv.getV());
				} catch (IllegalArgumentException iae) {}
				
				if (p==null) {
					// only non-parameters get mapped
					// now check to see if this var is written as well
					if (block.modifies(nv.getV())) {
						// create new temp var and map it
						String varname = var2name.get(nv.getV());
						JimpleLocal varlocal = varname2local.get(varname);
						if (varlocal==null) {
							varlocal = new JimpleLocal(varname, null);
							varname2local.put(varname, varlocal);
						}
						
						JimpleLocal local = nextVar(varlocal.getType());
						parammap.put(nv.getV(), local);
						
						stmts.add(new JAssignStmt(local, varlocal));
					}
				}
			}
		}

		// get the branch condition (if any)
		Vertex<NodeValue<V,JavaLabel,JavaParameter>> branchoutput = null;
		try {
			branchoutput = outputs.get(null);
		} catch (UnsupportedOperationException uoe) {}
		
		// find the list of vertices that are used as args to other vertices (i.e. not as a branch condition)
		Set<Vertex<NodeValue<V,JavaLabel,JavaParameter>>> asValue = 
			new HashSet<Vertex<NodeValue<V,JavaLabel,JavaParameter>>>();
		{
			LinkedList<Vertex<NodeValue<V,JavaLabel,JavaParameter>>> queue = 
				new LinkedList<Vertex<NodeValue<V,JavaLabel,JavaParameter>>>();
			for (Vertex<NodeValue<V,JavaLabel,JavaParameter>> root : rootmap.keySet()) {
				queue.addLast(root);
				//asValue.add(root);
			}
			
			while (queue.size() > 0) {
				Vertex<NodeValue<V,JavaLabel,JavaParameter>> next = queue.removeFirst();
				if (asValue.contains(next))
					continue;
				for (int i = 0; i < next.getChildCount(); i++) {
					Vertex<NodeValue<V,JavaLabel,JavaParameter>> child = next.getChild(i);
					asValue.add(child);
					queue.addLast(child);
				}
			}
		}

		
		BlockData data = new BlockData(stmts, rootmap, valuecache, parammap, branchoutput, asValue);
		
		// output the stmts for each V modified by this block
		for (Vertex<NodeValue<V,JavaLabel,JavaParameter>> root : rootmap.keySet()){
			computeValue(root, data);
		}
		

		// write out the instructions for the branch condition (if any)
		if (branchoutput!=null){
			Value condition = computeValue(branchoutput, data);
			
			if (!(condition instanceof ConditionExpr)){
				condition = new JNeExpr(condition, IntConstant.v(0));
			}
			
			Unit ifstmt = new JIfStmt(condition, new JNopStmt()); // the branch target will be corrected later
			stmts.add(ifstmt);
		}

		
		// update the types of the parammap variables
		for (V var : parammap.keySet()){
			JimpleLocal varlocal = varname2local.get(var2name.get(var));
			if (varlocal.getType()!=null){
				parammap.get(var).setType(varlocal.getType());
			}
		}
		
		
		return new BB(stmts);
	}
	
	
	
	
	/** This method will take a value computed in computeValue and find the best local
	 *  variable to assign it to. If there is a V that wants this value, then we will
	 *  use its local variable. Also, if there are multiple V's that want this value then 
	 *  we will make additional assignments to copy the first such local variable into the rest.
	 *  This method will also update the valuecache appropriately.
	 */
	private Value assignLocals(Vertex<NodeValue<V,JavaLabel,JavaParameter>> root, 
							   Type resulttype, Value RHS, BlockData data){
		JimpleLocal resultlocal = null;
		if (data.rootmap.containsKey(root)){
			V var = data.rootmap.get(root).get(0);
			String varname = var2name.get(var);
			JimpleLocal varlocal = varname2local.get(varname);
			if (varlocal==null){
				varlocal = new JimpleLocal(varname, resulttype);
				varname2local.put(varname, varlocal);
			}
			if (varlocal.getType()==null)
				varlocal.setType(resulttype);
			resultlocal = varlocal;
		}
		else
			resultlocal = nextVar(resulttype);
		
		
		Unit unit = new JAssignStmt(resultlocal, RHS);
		data.stmts.add(unit);
		data.valuecache.put(root, resultlocal);
		
		if (data.rootmap.containsKey(root)){
			for (V var : data.rootmap.get(root)){
				String varname = var2name.get(var);
				if (varname.equals(resultlocal.getName()))
					continue;
				
				
				JimpleLocal varlocal = varname2local.get(varname);
				if (varlocal==null){
					varlocal = new JimpleLocal(varname, resultlocal.getType());
					varname2local.put(varname, varlocal);
				}
				if (varlocal.getType()==null)
					varlocal.setType(resultlocal.getType());
				
				// this value must be assigned to the variable associated with vertex
				data.stmts.add(new JAssignStmt(varlocal, resultlocal));
			}
		}

		return resultlocal;
	}
							

	/** This method is like assignLocals but it is reserved for values that must be wrapped
	 *  in ResolveStmts. We take the given 'RHS' value and wrap it in a ResolveStmt. For the
	 *  exception variables, we add any V's that want the resolved value of RHS. If no V's
	 *  want this value, we make a new local var and stick it in the exceptions list.
	 */
	private Value assignResolveLocals(Vertex<NodeValue<V,JavaLabel,JavaParameter>> root, 
			   						  Type resulttype, Value RHS, BlockData data){
		JimpleLocal resultlocal = null;
		if (data.rootmap.containsKey(root)){
			V var = data.rootmap.get(root).get(0);
			String varname = var2name.get(var);
			JimpleLocal varlocal = varname2local.get(varname);
			if (varlocal==null){
				varlocal = new JimpleLocal(varname, resulttype);
				varname2local.put(varname, varlocal);
			}
			if (varlocal.getType()==null)
				varlocal.setType(resulttype);
			resultlocal = varlocal;
		}
		else
			resultlocal = nextVar(resulttype);
		
		ResolveStmt unit = new ResolveStmt(resultlocal, RHS);
		data.stmts.add(unit);
		data.valuecache.put(root, resultlocal);
		
		if (data.rootmap.containsKey(root)){
			for (V var : data.rootmap.get(root)){
				String varname = var2name.get(var);
				if (varname.equals(resultlocal.getName()))
					continue;
				
				
				JimpleLocal varlocal = varname2local.get(varname);
				if (varlocal==null){
					varlocal = new JimpleLocal(varname, resultlocal.getType());
					varname2local.put(varname, varlocal);
				}
				if (varlocal.getType()==null)
					varlocal.setType(resultlocal.getType());
				
				// this value must be assigned to the variable associated with vertex
				data.stmts.add(new JAssignStmt(varlocal, resultlocal));
			}
		}

		return resultlocal;
	}
	
	
	private class BlockData {
		public final List<Unit> stmts;
		public final Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,List<V>> rootmap;
		public final Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,Value> valuecache;
//		public final Map<V,JimpleLocal> parammap;
		public final Vertex<NodeValue<V,JavaLabel,JavaParameter>> branchcondition;
		public final Set<Vertex<NodeValue<V,JavaLabel,JavaParameter>>> asValue;
		
		public BlockData(List<Unit> _stmts, Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,List<V>> _rootmap,
				Map<Vertex<NodeValue<V,JavaLabel,JavaParameter>>,Value> _valuecache,
				Map<V,JimpleLocal> _parammap, Vertex<NodeValue<V,JavaLabel,JavaParameter>> _branchcondition,
				Set<Vertex<NodeValue<V,JavaLabel,JavaParameter>>> _asValue) {
			this.stmts = _stmts;
			this.rootmap = _rootmap;
			this.valuecache = _valuecache;
//			this.parammap = _parammap;
			this.branchcondition = _branchcondition;
			this.asValue = _asValue;
		}
	}
	
	
	/** This method recursively outputs the stmts needed to construct the given
	 *  Vertex expression. It is essentially a postorder traversal of the DAG, where 
	 *  nodes are marked once visited so that they won't be output twice. The return
	 *  value will be the Soot value of the given expression, which can be used in 
	 *  later statements. In some cases (i.e. when computing the value of SIGMA) the return
	 *  value will be ignored. 
	 */
	private Value computeValue(Vertex<NodeValue<V,JavaLabel,JavaParameter>> root, BlockData data) {
		if (data.valuecache.containsKey(root))
			return data.valuecache.get(root);

		NodeValue<V,JavaLabel,JavaParameter> nv = root.getLabel();

		Value result = null;
		
		if (nv.isL()){
			JavaLabel label = nv.getL();

			if (label.isSimple()) {
				// giant switch statement!
				JavaOperator operator = label.getSimpleSelf().getOperator();
				switch (operator) {
				case RHO_VALUE: {
					Value child = computeValue(root.getChild(0), data);
					Value answer = new GetValueExpr(child);
					return assignLocals(root, child.getType(), answer, data);
				}
				
				case RHO_SIGMA: {
					Value child = computeValue(root.getChild(0), data);
					Value answer = new Rho2Expr(child);
					return assignLocals(root, answer.getType(), answer, data);
				}
				
				case INJL: {
					Value child = computeValue(root.getChild(0), data);
					Value answer = new InjectLeftExpr(child);
					return assignLocals(root, child.getType(), answer, data);
				}
				case INJR: {
					Value child = computeValue(root.getChild(0), data);
					Value answer = new InjectRightExpr(child);
					return assignLocals(root, child.getType(), answer, data);
				}
//				case NEGATE: {
//					Value child = computeValue(root.getChild(0), data);
//					if (!(child instanceof JimpleLocal)){
//						JimpleLocal var = nextVar(child.getType());
//						data.stmts.add(new JAssignStmt(var, child));
//						child = var;
//					}
//
//					/*
//					Value condition = new JEqExpr(child, IntConstant.v(0));
//					Unit trueStmt = new JAssignStmt(child, IntConstant.v(1));
//					Unit falseStmt = new JAssignStmt(child, IntConstant.v(0));
//					Unit ifStmt = new JIfStmt(condition, trueStmt);
//					Unit afterNop = new JNopStmt();
//					Unit gotoStmt = new JGotoStmt(afterNop);
//					
//					data.stmts.add(ifStmt);
//					data.stmts.add(falseStmt);
//					data.stmts.add(gotoStmt);
//					data.stmts.add(trueStmt);
//					data.stmts.add(afterNop);
//					*/
//					
//					data.stmts.add(new JAssignStmt(child, new JXorExpr(child, IntConstant.v(1))));
//					
//					data.valuecache.put(root, child);
//					return assignLocals(root, child.getType(), child, data);
//				}
//				case BOOLEAN_AND: {
//					Value LHS = computeValue(root.getChild(0), data);
//					Value RHS = computeValue(root.getChild(1), data);
//
//					JimpleLocal and = nextVar(soot.IntType.v());
//					
//					Unit and_0 = new JAssignStmt(and, IntConstant.v(0));
//					Unit and_RHS = new JAssignStmt(and, RHS);
//					Unit ifLHS = new JIfStmt(new JNeExpr(LHS, IntConstant.v(0)), and_RHS);
//					Unit afternop = new JNopStmt();
//					Unit gotoNop = new JGotoStmt(afternop);
//					
//					data.stmts.add(ifLHS);
//					data.stmts.add(and_0);
//					data.stmts.add(gotoNop);
//					data.stmts.add(and_RHS);
//					data.stmts.add(afternop);
//					
//					data.valuecache.put(root, and);
//					return and;
//				}
//				case BOOLEAN_OR: {
//					Value LHS = computeValue(root.getChild(0), data);
//					Value RHS = computeValue(root.getChild(1), data);
//					
//					JimpleLocal or = nextVar(soot.IntType.v());
//					
//					Unit or_1 = new JAssignStmt(or, IntConstant.v(1));
//					Unit or_RHS = new JAssignStmt(or, RHS);
//					Unit ifLHS = new JIfStmt(new JEqExpr(LHS, IntConstant.v(0)), or_RHS);
//					Unit afternop = new JNopStmt();
//					Unit gotoNop = new JGotoStmt(afternop);
//					
//					data.stmts.add(ifLHS);
//					data.stmts.add(or_1);
//					data.stmts.add(gotoNop);
//					data.stmts.add(or_RHS);
//					data.stmts.add(afternop);
//					
//					data.valuecache.put(root, or);
//					return or;
//				}
				case VOID: {
					return assignLocals(root, soot.VoidType.v(), VoidExpr.INSTANCE, data);
				}
				case PLUS:
				case DIVIDE:
				case MINUS:
				case TIMES:
				case MOD: {
					Value LHS = computeValue(root.getChild(0), data);
					Value RHS = computeValue(root.getChild(1), data);
					Type LT = LHS.getType();
					Type RT = RHS.getType();
					Type resulttype = null;
					if (LT==null && RT==null)
						resulttype = null;
					else if ((LT!=null && LT.equals(soot.DoubleType.v())) || 
							 (RT!=null && RT.equals(soot.DoubleType.v())))
						resulttype = soot.DoubleType.v();
					else if ((LT!=null && LT.equals(soot.FloatType.v())) || 
							 (RT!=null && RT.equals(soot.FloatType.v())))
						resulttype = soot.FloatType.v();
					else if ((LT!=null && LT.equals(soot.LongType.v())) || 
							 (RT!=null && RT.equals(soot.LongType.v())))
						resulttype = soot.LongType.v();
					else 
						resulttype = soot.IntType.v();

					Value binop=null;
					if (operator == JavaOperator.PLUS) binop=new JAddExpr(LHS, RHS);
					else if (operator == JavaOperator.DIVIDE) binop=new JDivExpr(LHS,RHS);
					else if (operator == JavaOperator.MINUS) binop=new JSubExpr(LHS,RHS);
					else if (operator == JavaOperator.TIMES) binop=new JMulExpr(LHS,RHS);
					else if (operator == JavaOperator.MOD) binop=new JRemExpr(LHS,RHS);

//					if (operator == JavaOperator.DIVIDE){
//						return assignResolveLocals(root, resulttype, binop, data);
//					}else{
						return assignLocals(root, resulttype, binop, data);
//					}
				}
				case CMP:
				case CMPG:
				case CMPL:
				case GREATER_THAN_EQUAL:
				case GREATER_THAN:
				case LESS_THAN_EQUAL:
				case LESS_THAN:
				case EQUAL:
				case NOT_EQUAL: {
					Value LHS = computeValue(root.getChild(0), data);
					Value RHS = computeValue(root.getChild(1), data);
					
					Value binop=null;
					switch (operator) {
					case CMP: binop=new JCmpExpr(LHS, RHS); break;
					case CMPG: binop=new JCmpgExpr(LHS,RHS); break;
					case CMPL: binop=new JCmplExpr(LHS,RHS); break;
					case EQUAL: binop=new JEqExpr(LHS,RHS); break;
					case GREATER_THAN_EQUAL: binop=new JGeExpr(LHS,RHS); break;
					case GREATER_THAN: binop=new JGtExpr(LHS,RHS); break;
					case LESS_THAN_EQUAL: binop=new JLeExpr(LHS,RHS); break; 
					case LESS_THAN: binop=new JLtExpr(LHS,RHS); break;
					case NOT_EQUAL: binop=new JNeExpr(LHS,RHS); break;
					default: throw new RuntimeException("This should never happen: " + operator);
					}

					if (binop instanceof ConditionExpr) {
						if (root == data.branchcondition && !data.asValue.contains(root) && !data.rootmap.containsKey(root)) {
							// this is used as a branch condition and nothing else
							// can leave this as a condition expr
							return binop;
						} else {
							// turn this into a simpler value
							JimpleLocal resultvar = nextVar(soot.IntType.v());

							//   if (condition) goto tlabel
							//   var = 0
							//   goto afterlabel
							// tlabel:
							//   var = 1
							// afterlabel:
							//   nop
							
							Unit nop = new JNopStmt();
							Unit assign1 = new JAssignStmt(resultvar, IntConstant.v(1));
							Unit gotonop = new JGotoStmt(nop);
							Unit assign0 = new JAssignStmt(resultvar, IntConstant.v(0));
							Unit ifstmt = new JIfStmt(binop, assign1);
							
							data.stmts.add(ifstmt);
							data.stmts.add(assign0);
							data.stmts.add(gotonop);
							data.stmts.add(assign1);
							data.stmts.add(nop);
							
							return assignLocals(root, soot.IntType.v(), resultvar, data);
						}
					} else {
						return assignLocals(root, soot.IntType.v(), binop, data);
					}
				}
				case BITWISE_AND:
				case BITWISE_OR:
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
				case UNSIGNED_SHIFT_RIGHT:
				case XOR: {
					Value LHS = computeValue(root.getChild(0), data);
					Value RHS = computeValue(root.getChild(1), data);
					
					Value binop=null;
					if (operator.equals(JavaOperator.BITWISE_AND)) binop=new JAndExpr(LHS, RHS);
					else if (operator.equals(JavaOperator.BITWISE_OR)) binop=new JOrExpr(LHS,RHS);
					else if (operator.equals(JavaOperator.SHIFT_LEFT)) binop=new JShlExpr(LHS,RHS);
					else if (operator.equals(JavaOperator.SHIFT_RIGHT)) binop=new JShrExpr(LHS,RHS);
					else if (operator.equals(JavaOperator.UNSIGNED_SHIFT_RIGHT)) binop=new JUshrExpr(LHS,RHS);
					else if (operator.equals(JavaOperator.XOR)) binop=new JXorExpr(LHS,RHS);
					else throw new RuntimeException("Mike forgot " + operator);

					Type resulttype = null;
					if (LHS.getType()==null && RHS.getType()==null)
						resulttype = null;
					else if ((LHS.getType()!=null && LHS.getType().equals(soot.LongType.v())) ||
							 (RHS.getType()!=null && RHS.getType().equals(soot.LongType.v()))){
						resulttype = soot.LongType.v();
					}
					else
						resulttype = soot.IntType.v();
					
					return assignLocals(root, resulttype, binop, data); 
				}
				case PRIMITIVECAST: { 
					TypeJavaLabel typelabel = root.getChild(0).getLabel().getL().getTypeSelf();
					Value value = computeValue(root.getChild(1), data);
					Value cast = new JCastExpr(value, typelabel.getType());
					return assignResolveLocals(root, typelabel.getType(), cast, data);
				}
				case CAST: { 
					TypeJavaLabel typelabel = root.getChild(1).getLabel().getL().getTypeSelf();
					computeValue(root.getChild(0), data);
					Value value = computeValue(root.getChild(2), data);
					Value cast = new JCastExpr(value, typelabel.getType());
					return assignResolveLocals(root, typelabel.getType(), cast, data);
				}
				case GETFIELD: {
					FieldJavaLabel field = root.getChild(2).getLabel().getL().getFieldSelf(); 
					computeValue(root.getChild(0), data);
					Value base = computeValue(root.getChild(1), data);
					SootFieldRef ref = resolver.resolveField(field.getClassName(), field.getFieldName(), field.getType());
					return assignResolveLocals(root, field.getType(), new JInstanceFieldRef(base, ref), data); 
				}
				case INSTANCEOF: {
					TypeJavaLabel typelabel = root.getChild(2).getLabel().getL().getTypeSelf();
					computeValue(root.getChild(0), data);
					Value base = computeValue(root.getChild(1), data);
					Value answer = new JInstanceOfExpr(base, typelabel.getType());
					return assignLocals(root, soot.BooleanType.v(), answer, data); 
				}
				case ARRAYLENGTH: {
					computeValue(root.getChild(0), data);
					Value array = computeValue(root.getChild(1), data);
					Value answer = new JLengthExpr(array);
					return assignResolveLocals(root, soot.IntType.v(), answer, data);
				}
				case NEG: {
					Value op = computeValue(root.getChild(0), data);

					Type resulttype=null;
					if (op.getType()==null)
						resulttype = null;
					else if (op.getType().equals(soot.IntType.v()) || 
							 op.getType().equals(soot.ByteType.v()) ||
							 op.getType().equals(soot.ShortType.v()) || 
							 op.getType().equals(soot.BooleanType.v()) ||
							 op.getType().equals(soot.CharType.v()))
							resulttype = soot.IntType.v();
					else if(op.getType().equals(soot.LongType.v()))
						resulttype = soot.LongType.v();
					else if(op.getType().equals(soot.DoubleType.v()))
						resulttype = soot.DoubleType.v();
					else if(op.getType().equals(soot.FloatType.v()))
						resulttype = soot.FloatType.v();
					
					Value answer = new JNegExpr(op);
					return assignLocals(root, resulttype, answer, data);
				}
				case CLASS: {
					TypeJavaLabel typelabel = root.getChild(0).getLabel().getL().getTypeSelf();
					Value answer = ClassConstant.v(SootUtils.typeToTypeName(typelabel.getType()));
					return assignResolveLocals(root, RefType.v("java.lang.Class"), answer, data);
				}
				case GETARRAY: {
					computeValue(root.getChild(0), data);
					Value base = computeValue(root.getChild(1), data);
					Value index = computeValue(root.getChild(2), data);
					
					soot.ArrayType arraytype = (soot.ArrayType)base.getType();
					Value answer = new JArrayRef(base, index);
					return assignResolveLocals(root, (arraytype==null ? null : arraytype.getElementType()), answer, data);
				}
				case GETSTATICFIELD: {
					computeValue(root.getChild(0), data);
					FieldJavaLabel field = root.getChild(1).getLabel().getL().getFieldSelf();
					SootFieldRef ref = resolver.resolveField(field.getClassName(), field.getFieldName(), field.getType());
					Value answer = Jimple.v().newStaticFieldRef(ref);
					return assignResolveLocals(root, field.getType(), answer, data);
				}
				case PARAMS: {
					throw new RuntimeException("Not a value: params");
				}
				
				
				case INVOKESTATIC:
				case INVOKEVIRTUAL:
				case INVOKEINTERFACE:
				case INVOKESPECIAL: {
					boolean isStatic = (operator==JavaOperator.INVOKESTATIC);
					int methodindex = isStatic ? 1 : 2;
					
					MethodJavaLabel method = root.getChild(methodindex).getLabel().getL().getMethodSelf();
					Vertex<NodeValue<V,JavaLabel,JavaParameter>> params = root.getChild(methodindex+1);
					
					// sigma
					computeValue(root.getChild(0), data);

					SootMethodRef ref = resolver.resolveMethod(method.getClassName(), method.getMethodName(), method.getReturnType(), method.getParameterTypes());
					List<Value> args = new ArrayList<Value>(method.getParameterTypes().size());
					for (int i=0;i<params.getChildCount();i++){
						args.add(computeValue(params.getChild(i), data));
					}
					
					Value invoke = null;
					if (isStatic) invoke = new JStaticInvokeExpr(ref, args);
					else{
						Value base = computeValue(root.getChild(1), data);
						if (!(base instanceof JimpleLocal)){
							JimpleLocal targetLocal = nextVar(base.getType());
							data.stmts.add(new JAssignStmt(targetLocal, base));
							base = targetLocal;
						}
						
						if (operator==JavaOperator.INVOKEVIRTUAL) invoke = new JVirtualInvokeExpr(base, ref, args);
						else if (operator==JavaOperator.INVOKEINTERFACE) invoke = new JInterfaceInvokeExpr(base, ref, args);
						else invoke = new JSpecialInvokeExpr((JimpleLocal)base, ref, args);
					}
					
					return assignResolveLocals(root, method.getReturnType(), invoke, data);
				}
				case NEWARRAY: {
					TypeJavaLabel type = root.getChild(1).getLabel().getL().getTypeSelf();
					Value size = computeValue(root.getChild(2), data);
					
					computeValue(root.getChild(0), data);
					
					Value arrayexpr = new JNewArrayExpr(type.getType(), size);

					return assignResolveLocals(root, arrayexpr.getType(), arrayexpr, data);
				}
				case DIMS: {
					throw new RuntimeException("Invalid value: dims");
				}
				case NEWMULTIARRAY: {
					TypeJavaLabel typelabel = root.getChild(1).getLabel().getL().getTypeSelf();
					Vertex<NodeValue<V,JavaLabel,JavaParameter>> dims = root.getChild(2);
					
					computeValue(root.getChild(0), data);
					
					List<Value> sizes = new ArrayList<Value>(dims.getChildCount());
					for (int i=0;i<dims.getChildCount();i++)
						sizes.add(computeValue(dims.getChild(i), data));
					
					Value arrayexpr = new JNewMultiArrayExpr((soot.ArrayType)typelabel.getType(), sizes);
					
					return assignResolveLocals(root, arrayexpr.getType(), arrayexpr, data);
				}
				case NEWINSTANCE: {
					TypeJavaLabel typelabel = root.getChild(1).getLabel().getL().getTypeSelf();
					computeValue(root.getChild(0), data);
					Value instance = new JNewExpr((RefType)typelabel.getType());
					
					return assignResolveLocals(root, typelabel.getType(), instance, data);
				}
				/*
				case THROW: {
					computeValue(root.getChild(0), stmts, rootmap, valuecache, parammap);
					Value child = computeValue(root.getChild(1), stmts, rootmap, valuecache, parammap);
					Value answer = new ThrowExpr(child);
					return assignResolveLocals(root, stmts, rootmap, valuecache, answer.getType(), answer);
				}
				*/
				case ENTERMONITOR: {
					computeValue(root.getChild(0), data);
					Value child = computeValue(root.getChild(1), data);
					Value answer = new EntermonitorExpr(child);
					return assignResolveLocals(root, answer.getType(), answer, data);
				}
				case EXITMONITOR: {
					computeValue(root.getChild(0), data);
					Value child = computeValue(root.getChild(1), data);
					Value answer = new ExitmonitorExpr(child);
					return assignResolveLocals(root, answer.getType(), answer, data);
				}
				case SETARRAY: {
					computeValue(root.getChild(0), data);
					Value array = computeValue(root.getChild(1), data);
					Value index = computeValue(root.getChild(2), data);
					Value rhs = computeValue(root.getChild(3), data);
					Value answer = new SetarrayExpr(array, index, rhs);
					
					return assignResolveLocals(root, answer.getType(), answer, data);
				}
				case SETFIELD: {
					FieldJavaLabel field = root.getChild(2).getLabel().getL().getFieldSelf();
					
					computeValue(root.getChild(0), data);
					Value base = computeValue(root.getChild(1), data);
					Value rhs = computeValue(root.getChild(3), data);
				
					SootFieldRef ref = resolver.resolveField(field.getClassName(), field.getFieldName(), field.getType());
					Value answer = new SetfieldExpr(base, ref, rhs);
					
					return assignResolveLocals(root, answer.getType(), answer, data);
				}
				case SETSTATICFIELD: {
					FieldJavaLabel field = root.getChild(1).getLabel().getL().getFieldSelf();
					
					computeValue(root.getChild(0), data);
					Value rhs = computeValue(root.getChild(2), data);

					SootFieldRef ref = resolver.resolveField(field.getClassName(), field.getFieldName(), field.getType());
					Value answer = new SetstaticfieldExpr(ref, rhs);
					return assignResolveLocals(root, answer.getType(), answer, data);
				}
				default:
					throw new RuntimeException("Mike forgot to support: " + operator.getLabel());
				}
			}
			else if (label.isGetException()){
				GetExceptionJavaLabel genl = label.getGetExceptionSelf();
				Value child = computeValue(root.getChild(0), data);
				Value answer = new GetExceptionExpr(child, genl.getExceptionType());
				return assignLocals(root, genl.getExceptionType(), answer, data);
			}
			else if (label.isConstant()) {
				result = label.getConstantSelf().getValue();
			}
			else if (label.isIsException()){
				// this one is tricky because it won't be put into a local variable
				IsExceptionJavaLabel ienl = label.getIsExceptionSelf();
				Value child = computeValue(root.getChild(0), data);
				result = new IsExceptionExpr((JimpleLocal)child, ienl.getExceptionType());
			}
			else if (label.isType()) {
				throw new RuntimeException("JavaTypeJavaLabel as value: "+label);
			}
			else if (label.isField()) {
				throw new RuntimeException("FieldJavaLabel as value: "+label); 
			}
			else if (label.isMethod()) {
				throw new RuntimeException("MethodJavaLabel as value: "+label);
			}
			else 
				throw new RuntimeException("Mike forgot node label type: "+label.getClass());
		}
//		else if (nv.isV()) {
//			// this is a variable (input)
//			if (data.parammap.containsKey(nv.getV())) {
//				result = data.parammap.get(nv.getV());
//			} else {
//				JavaParameter p = null;
//				try {
//					p = cfg.getParameter(nv.getV());
//				} catch (IllegalArgumentException iae) {}
//				
//				if (p != null) {
//					JimpleLocal param = p.getJimpleLocal();
//					if (param.getName().startsWith("__param")){
//						result = param;
//					}else if (param.getName().equals("__this")){
//						result = param;
//					}else if (param.equals(SootVariable.SIGMA.getJimpleLocal())){
//						return null; // SIGMA has no value
//					}else if (param.equals(SootVariable.M)) {
//						return null; // M has no value
//					}else
//						throw new RuntimeException("Unknown parameter "+param);
//				}else{
//					V var = nv.getV();
//					String varname = var2name.get(var);
//					JimpleLocal varlocal = varname2local.get(varname);
//					if (varlocal==null){
//						varlocal = new JimpleLocal(varname, null);
//						varname2local.put(varname, varlocal);
//					}
//	
//					result = varlocal;
//				}
//			}
//		}
//		else if (nv.isP()){
//			JimpleLocal param = nv.getP().getJimpleLocal();
//			if (param.getName().startsWith("__param")){
//				result = param;
//			}else if (param.getName().equals("__this")){
//				result = param;
//			}else if (param.equals(SootVariable.SIGMA.getJimpleLocal())){
//				return null; // SIGMA has no value
//			}else if (param.equals(SootVariable.M.getJimpleLocal())) {
//				return null; // M has no value
//				//throw new RuntimeException("shouldn't call this on M!");
//			}else
//				throw new RuntimeException("Unknown parameter "+param);
//		}
		else
			throw new RuntimeException("Invalid NodeValue type: "+nv);
		
		
		// deal with 'result' if it didn't NEED to be put in a local
		if (result!=null){
			data.valuecache.put(root, result);

			if (data.rootmap.containsKey(root)){
				for (V var : data.rootmap.get(root)){
					// this value must be assigned to the variable associated with vertex
					String varname = var2name.get(var);
					JimpleLocal varlocal = varname2local.get(varname);
					if (varlocal==null){
						varlocal = new JimpleLocal(varname, result.getType());
						varname2local.put(varname, varlocal);
					}
					if (varlocal.getType()==null)
						varlocal.setType(result.getType());
					
					if (result instanceof ParameterRef || result instanceof ThisRef){
						Unit assign = new JIdentityStmt(varlocal, result);
						data.stmts.add(assign);
					}else{
						Unit assign = new JAssignStmt(varlocal, result);
						data.stmts.add(assign);
					}
				}
			}
			return result;
		}
		
		throw new RuntimeException("Bad fallthrough!!!");
	}
}
