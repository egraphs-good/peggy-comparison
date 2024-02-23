package peggy.analysis.java.inlining;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peggy.represent.java.SootUtils;
import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.JasminClass;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.options.Options;
import soot.util.JasminOutputStream;

/**
 * This class is a java inliner based around a particular inlining heuristic.
 */
public class HeuristicInliner {
	public static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.out.println("HeuristicInliner: " + message);
	}

	protected int totalInvokesExamined;
	protected int totalInvokesInlined;
	protected final JimpleInliner inliner;
	protected final InlinerHeuristic heuristic;

	public HeuristicInliner(InlinerHeuristic _heuristic, JimpleInliner _inliner) {
		this.inliner = _inliner;
		this.heuristic = _heuristic;
		this.totalInvokesExamined = 0;
		this.totalInvokesInlined = 0;
	}
	
	
	public boolean inlineMulti(SootMethod inliner, Unit where) {
		this.totalInvokesExamined++;
		
		DispatchMap map = this.inliner.getTypeAnalysis(inliner).getPotentialTargets(where);
		if (map == null) {
			debug("inliner gave null target");
			return false;
		}
		
		InlinerSafetyAnalysis safetyAnalysis = new InlinerSafetyAnalysis();
		
		if (map.isIndependent()) {
			SootMethod inlinee = map.getIndependentSelf().getDispatchedMethod();

			if (!safetyAnalysis.isSafeToInline(inliner, inlinee))
				return false;
			
			// ask the heuristic if we should inline
			if (!this.heuristic.shouldInline(inliner, where, inlinee)) {
				debug("heuristic says not to inline");
				return false;
			}
			
			// do the inlining
			this.inliner.justDoIt(inliner, where, inlinee, null);
			
			// update the heuristic data
			this.heuristic.update(inliner);

			this.totalInvokesInlined++;
			
			return true;
		} else {
			TargetDependentDispatchMap tddm = map.getDependentSelf();
			
			Map<SootClass,SootMethod> safeMap = new HashMap<SootClass,SootMethod>();
			for (SootClass clazz : tddm.getClassKeys()) {
				SootMethod method = tddm.getDispatchedMethod(clazz);
				if (safetyAnalysis.isSafeToInline(inliner, method)) {
					safeMap.put(clazz, method);
				}
			}
			
			if (safeMap.size() == 0) {
				debug("no safe methods");
				return false;
			}
			
			TargetDependentDispatchMap safeDM = new TargetDependentDispatchMap(tddm.getNamedSignature(), safeMap);
			
			if (!this.heuristic.shouldInlineAll(inliner, where, safeMap.values())) {
				debug("heuristic says not to inline");
				return false;
			}

			// do the inlining
			this.inliner.justDoItMulti(inliner, where, safeDM, true);
			
			// update the heuristic data
			this.heuristic.update(inliner);
			
			this.totalInvokesInlined++;

			return true;
		}
	}
	
	
	
	public boolean inline(SootMethod inliner, Unit where) {
		this.totalInvokesExamined++;
		
		DispatchMap map = this.inliner.getSingleInlineTarget(inliner, where);
		// determine if it's safe to inline
		if (map == null) {
			debug("inliner gave null target");
			return false;
		}

		SootClass dynamicClass;
		SootMethod inlinee;
		if (map.isDependent()) {
			dynamicClass = map.getDependentSelf().getClassKeys().iterator().next();
			inlinee = map.getDependentSelf().getDispatchedMethod(dynamicClass);
		} else {
			dynamicClass = null;
			inlinee = map.getIndependentSelf().getDispatchedMethod();
		}
		
		// ask the heuristic if we should inline
		if (!this.heuristic.shouldInline(inliner, where, inlinee)) {
			debug("heuristic says not to inline");
			return false;
		}
		
		// do the inlining
		this.inliner.justDoIt(inliner, where, inlinee, dynamicClass);
		
		// update the heuristic data
		this.heuristic.update(inliner);

		this.totalInvokesInlined++;
		
		return true;
	}

	

	/**
	 * Attempts to inline every invoke instruction in the given method.
	 * This will not do recursive inlining.
	 * Returns true iff any inlining was done.
	 */
	public boolean inlineAll(SootMethod inliner, boolean multi) {
		if (!inliner.isConcrete() || SootUtils.hasExceptions(inliner))
			return false;
		
		debug("- inlining inside method " + inliner.getSignature());
		
		Body body = inliner.retrieveActiveBody();
		List<Unit> originalUnits = new ArrayList<Unit>(body.getUnits());
		boolean progress = false;
		
		for (Unit unit : originalUnits) {
			if (unit instanceof JInvokeStmt) {
				debug("  - Attempting to inline instruction " + unit);
				boolean inlined = (multi ? this.inlineMulti(inliner, unit) : this.inline(inliner, unit));
				progress = progress || inlined;
				debug(inlined ? "  - Successfully inlined method call!" : "  - FAILED inlining method call");
			} else if (unit instanceof JAssignStmt) {
				JAssignStmt assign = (JAssignStmt)unit;
				if (!(assign.getRightOp() instanceof InvokeExpr))
					continue;
				
				debug("  - Attempting to inline instruction " + unit);
				boolean inlined = (multi ? this.inlineMulti(inliner, unit) : this.inline(inliner, unit));
				progress = progress || inlined;
				debug(inlined ? "  - Successfully inlined method call!" : "  - FAILED inlining method call");
			} else {
				continue;
			}
		}
		
		return progress;
	}
	
	
	
	/**
	 * args[0] = classname
	 * args[1] = output folder
	 * args[2] = "multi" -> multi, else single
	 * args[3] = "recursive" -> recursive, else nonrecursive
	 */
	public static void main(String args[]) throws Throwable {
		String classname = args[0];
		String outputFolder = args[1];
		boolean multi = args[2].equals("multi");
		boolean recursive = args[3].equals("recursive");
		
		System.out.println("Running HeuristicInliner [" +
				 		   "classname=" + classname + 
						   ", outputFolder=" + outputFolder + 
						   ", multi-target=" + multi + 
						   ", recursive=" + recursive + 
						   "]");
		
		SootClass clazz = Scene.v().loadClassAndSupport(classname);
		Scene.v().loadBasicClasses();

		HeuristicInliner inliner = new HeuristicInliner(new SimpleInlinerHeuristic(), new SimpleJimpleInliner());

		System.out.println("Inlining inside class " + classname);
		System.out.println(multi ? "Doing multi inlining" : "Doing single inlining");
		for (SootMethod method : (List<SootMethod>)clazz.getMethods()) {
			if (!method.isConcrete()) continue;
			
			int initialInstructions = method.retrieveActiveBody().getUnits().size();

			if (recursive) {
				while (inliner.inlineAll(method, multi)) {}
			} else {
				inliner.inlineAll(method, multi);
			}
			
			int finalInstructions = method.retrieveActiveBody().getUnits().size();
			
			System.out.println("Method size: original=" + initialInstructions + ", final=" + finalInstructions);
			
			// apply all the normal soot optimizations
			PackManager.v().getPack("jb").apply(method.retrieveActiveBody());
			PackManager.v().getPack("jop").apply(method.retrieveActiveBody());
		}
		
		System.out.println("Inlined " + inliner.totalInvokesInlined + "/" + inliner.totalInvokesExamined);
		System.out.println("Ending class " + classname);
		
		try {
			Options.v().set_output_dir(outputFolder);
			String outputFilename = SourceLocator.v().getFileNameFor(clazz, Options.output_format_class);
			System.out.println("* Writing class back to " + outputFilename);

			JasminClass jasmin = new JasminClass(clazz);
			File outputFile = new File(outputFilename);
			outputFile.getParentFile().mkdirs();
			FileOutputStream fout = new FileOutputStream(outputFile);
			PrintWriter writer = new PrintWriter(new JasminOutputStream(fout));
			jasmin.print(writer);
			writer.flush();
		} catch (Throwable t) {
			System.out.println("* Error writing class back to disk:");
			t.printStackTrace();
		}
	}
}
