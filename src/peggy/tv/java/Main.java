package peggy.tv.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.Logger;
import peggy.OptionParsingException;
import peggy.OptionsParser;
import peggy.analysis.BoundedEngineRunner;
import peggy.analysis.EngineRunner;
import peggy.analysis.EngineThetaMerger;
import peggy.analysis.TemporaryPhiAxioms;
import peggy.analysis.java.JavaBinopConstantAnalysis;
import peggy.analysis.java.JavaConstantAnalysis;
import peggy.analysis.java.JavaInvarianceAnalysis;
import peggy.analysis.java.JavaLIVSRAnalysis;
import peggy.analysis.java.inlining.PeggyHeuristicInliner;
import peggy.input.XMLRuleParser;
import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import peggy.represent.java.CustomAnnotationConstantFolder;
import peggy.represent.java.DefaultReferenceResolver;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelOpAmbassador;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.represent.java.PEGRetyper;
import peggy.represent.java.SootMethodPEGProvider;
import peggy.tv.DotTVListener;
import peggy.tv.TVLastDataListener;
import peggy.tv.TVListener;
import peggy.tv.TVTimerListener;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import util.Action;
import util.NamedTag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;
import eqsat.engine.AxiomSelector;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.engine.proof.Property;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.axiom.BooleanAxioms;
import eqsat.meminfer.peggy.axiom.EqualityAxioms;
import eqsat.meminfer.peggy.axiom.LoopAxioms;
import eqsat.meminfer.peggy.axiom.LoopInteractionAxioms;
import eqsat.meminfer.peggy.axiom.PeggyAxiomSetup;
import eqsat.meminfer.peggy.axiom.PhiAxioms;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This is the command-line interface class for the Java translation validator.
 * Details on the usage and command-line parameters are given through the 
 * "-help" option.
 */
public class Main {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("Main: " + message);
	}
	
	///////////////////////////////////
	// Command line option variables //
	///////////////////////////////////

	private static class MyEngineRunner extends 
	BoundedEngineRunner<JavaLabel,JavaParameter> {
		long lastTimeStop;
		long lastIterStop;
		
		MyEngineRunner() {super(-1L, 1000L, -1L);}
		protected void updateEngine(CPeggyAxiomEngine<JavaLabel,JavaParameter> engine) {
			getLogger().log("Performing theta merging");
			EngineThetaMerger<JavaLabel,JavaParameter> merger = 
				new EngineThetaMerger<JavaLabel,JavaParameter>(engine);
			merger.mergeThetas();
		}
		protected void notifySaturated(long iters, long time) {
			lastTimeStop = time;
			lastIterStop = iters;
			getLogger().log("Engine saturated in " + iters + " iterations");
		}
		protected void notifyTimeBoundReached(long iters, long time) {
			lastTimeStop = time;
			lastIterStop = iters;
			getLogger().log("Engine reached time bound of " + time + " after " + iters + " iterations");
		}
		protected void notifyIterationBoundReached(long iters, long time) {
			lastTimeStop = time;
			lastIterStop = iters;
			getLogger().log("Engine reached iteration bound of " + iters + " after " + time + " milliseconds");
		}
		protected void notifyMemoryBoundReached(long iters, long time, long mem) {
			lastTimeStop = time;
			lastIterStop = iters;
			getLogger().log("Engine reached memory bound of " + mem + " after " + time + " milliseconds");
		}
		protected void notifyHalted(long iters, long time, long mem) {
			lastTimeStop = time;
			lastIterStop = iters;
			getLogger().log("Engine halted after " + iters + " iterations");
		}
	};

	private static void displayHelp() {
		List<String> keys = new ArrayList<String>(optionsParser.getCommandKeys());
		Set<String> boolKeys = options.getBooleanKeys();
		Set<String> longKeys = options.getLongKeys();
		Set<String> stringKeys = options.getStringKeys();
		Set<String> fileKeys = options.getFileKeys();
		Set<String> pairKeys = options.getStringPairKeys();
		
		keys.addAll(boolKeys);
		keys.addAll(longKeys);
		keys.addAll(stringKeys);
		keys.addAll(fileKeys);
		keys.addAll(pairKeys);
		Collections.sort(keys);
		
		System.err.println("USAGE: Main <options>\n");
		
		final String format = "%-35s %s\n";
		
		for (String opt : keys) {
			if (boolKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <true|false>", options.getDescription(opt)); 
			} 
			else if (longKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <num>", options.getDescription(opt)); 
			}
			else if (stringKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <string>", options.getDescription(opt)); 
			}
			else if (fileKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <file>", options.getDescription(opt)); 
			}
			else if (pairKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <str1> <str2>", options.getDescription(opt));
			}
			else {
				System.err.printf(format, "-" + opt, optionsParser.getCommandDescription(opt)); 
			}
		}
		System.exit(0);
	}
	

	private static final Options options = new Options();
	private static final OptionsParser optionsParser = new OptionsParser(options);
	private static final String DUMP_PROOF = "proof";
	private static final String TMP_FOLDER = "tmpFolder";
	private static final String USE_CFG_EXCEPTIONS = "exceptions";
	private static final String DISPLAY_AXIOMS = "displayAxioms";
	private static final String INCREMENTAL_FUNCTION = "incremental";
	private static final String BC_THRESHOLD = "bcthreshold";
	private static final String ENABLE_PROOFS = "enableProofs";
	
	static {
		optionsParser.registerCommand("help", 
				"Display help commands",
				new Runnable() {
					public void run() {
						displayHelp();
					}
				});
		
		options.registerBoolean(ENABLE_PROOFS, true,
				"Set to true to generate EPEG proofs");
		options.registerStringPair("tv", null, null,
				"Specify a pair of function signatures to validate",
				new Action<Pair<String,String>>() {
					public void execute(Pair<String,String> pair) {
						if (translationValidationMethodSignatures != null)
							throw new OptionParsingException("Duplicate pairs of method signatures given"); 
						String firstMethod = pair.getFirst();
						String secondMethod = pair.getSecond();
						translationValidationMethodSignatures = 
							new Pair<String,String>(firstMethod, secondMethod);
					}
				});
		optionsParser.registerCommand("oop", 
				"Set to true to output a dot graph of the original PEG",
				new Runnable() {
					public void run() {
						OUTPUT_ORIGINAL_PEG = true;
					}
				});
		optionsParser.registerCommand("oep", 
				"Set to true to output a dot graph of the final EPEG",
				new Runnable() {
					public void run() {
						OUTPUT_EPEG = true;
					}
				});
		options.registerString("axioms", null,
				"Specify a colon-separated list of axiom input files",
				new Action<String>() {
					public void execute(String str) {
						String[] fileNames = str.split(":");
						for (String fileName : fileNames) {
							File file = new File(fileName);
							if (!file.exists())
								throw new OptionParsingException("Axiom input file does not exist: " + fileName);
							axiomFiles.add(file);
						}
					}
				});
		options.registerLong("maxmemory", 0L,
				"Specify the maximum amount of memory the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setMemoryUpperBound(value);
					}
				});
		options.registerLong("eto", 1000L,
				"Specify the maximum number of iterations the engine may run (default 1000)",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setIterationUpperBound(value);
					}
				});
		options.registerLong("maxtime", 0L,
				"Specify the maximum amoutn of time the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setTimeUpperBound(value);
					}
				});
		options.registerString(INCREMENTAL_FUNCTION, null,
				"Specify the name of a single function to optimize in an incremental run");
		options.registerLong(BC_THRESHOLD, 0L,
				"Specify the maximum number of instructions a function may have before being skipped", null);
		options.registerString("activate", null,
				"Specify a colon-separated list of equality analyses by name",
				new Action<String>() {
					public void execute(String str) {
						String[] analyses = str.split(":");
						for (String a : analyses) {
							activatedAnalyses.add(a);
						}
					}
				});
		options.registerBoolean(DISPLAY_AXIOMS, false,
				"Set to true to display the axioms used by the engine (default false)");
		options.registerBoolean(DUMP_PROOF, false,
				"Set to true to dump the proofs produced by the optimization (default false)");
		options.registerFile(TMP_FOLDER, null,
				"Specify the folder where temporary files are made", null);
		options.registerBoolean(USE_CFG_EXCEPTIONS, false,
				"Set to true to represent exceptions in the PEG/EPEG (default false)");
		options.registerLong("mergeTimeUpdate", 0L,
				"Specify the time between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long l) {
						engineRunner.setTimeUpdate(l);
					}
				});
		options.registerLong("mergeIterationUpdate", 0L,
				"Specify the number of iterations between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long l) {
						engineRunner.setIterationUpdate(l);
					}
				});
		options.registerFile("dotOutputFolder", null,
				"Specify the folder where the dot graphs will be put (default '.')",
				new Action<File>() {
					public void execute(File f) {
						dotOutputFolder = f;
					}
				});
	}
	
	
	private static File dotOutputFolder = new File(".");
	private static Network network = new Network();
	private static peggy.input.java.JavaXMLRuleParser ruleParser;
	private static final Collection<AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>>> axioms = 
		new ArrayList<AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>>>(100);
	private static final Set<String> activatedAnalyses = new HashSet<String>();
	private static final Set<File> axiomFiles = new HashSet<File>();
	private static boolean OUTPUT_EPEG = false;
	private static final MyEngineRunner engineRunner = new MyEngineRunner();
	private static JavaLabelOpAmbassador ambassador;
	private static Pair<String,String> translationValidationMethodSignatures = null;
	private static boolean OUTPUT_ORIGINAL_PEG = false;
	private static final Map<String,String> tvClassRenamingMap = 
		new HashMap<String,String>();
	private static PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> bodyPegProvider =
		new SootMethodPEGProvider() {
			public JavaLabelOpAmbassador getAmbassador() {return globalAmbassador;}
		};
	private static JavaLabelOpAmbassador globalAmbassador;
	
	private static void checkOption(boolean condition, String errorMessage) {
		if (!condition) {
			System.err.println(errorMessage);
			System.exit(1);
		}
	}

	///////////////////////////////////////////////

	private static final Logger TOP_LOGGER = new MyLogger();
	private static class MyLogger implements Logger {
		private static final char[] SYMBOLS = {'+', '-', '*', '@', '%', '&', '<', '~'}; 
		private final String tabs;
		private final int tabindex;
		MyLogger() {this("", 0);}
		private MyLogger(String _tabs, int _tabindex) {
			this.tabs = _tabs;
			this.tabindex = _tabindex;
		}
		public Logger getSubLogger() {
			return new MyLogger(tabs+"   ", (tabindex+1)%SYMBOLS.length);
		}
		public void log(String message) {
			System.out.println(tabs + SYMBOLS[tabindex] + " " + message);
			System.out.flush();
		}
		public void logException(String message, Throwable t) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(baos);
			t.printStackTrace(out);
			out.flush();
			String error = baos.toString();
			error = error.replaceAll("\\n", "\n   " + tabs);
			StringBuilder builder = new StringBuilder();
			builder.append(tabs).append("! ").append(message).append(" [\n   ").append(tabs).append(error).append("\n").append(tabs).append(']');
			System.out.println(builder);
			System.out.flush();
		}
	}
	
	
	private static void abortIf(boolean b, String message, Logger logger) {
		if (b) abort(message, logger);
	}
	private static void abort(String message, Logger logger) {
		logger.log("!!! CRITICAL ERROR: " + message + " !!!");
		System.exit(1);
	}
	private static void abort(String message, Throwable thrown, Logger log) {
		log.logException("!!! CRITICAL ERROR: " + message + " !!!", thrown);
		System.exit(1);
	}
	
	///////////////////////////////////////////////
	
	public static class PrintStructureListener 
	implements EventListener<Structure<CPEGTerm<JavaLabel,JavaParameter>>> {
		private final String message;
		private final Logger logger;
		
		public PrintStructureListener(Logger _logger, String _message) {
			this.message = _message;
			this.logger = _logger;
		}
		public boolean notify(Structure<CPEGTerm<JavaLabel,JavaParameter>> v) {
			if (options.getBoolean(DISPLAY_AXIOMS)) {
				String msg = message.trim().replaceAll("\\n[ \\t]*", "  ");
				logger.log("Applied axiom: " + msg);
			}
			return true;
		}
		public boolean canUse(Structure<CPEGTerm<JavaLabel,JavaParameter>> v) {
			return true;
		}
	}
		
	private static class PrintListener implements EventListener<Proof> {
		private final String message;
		private final Logger logger;
		
		public PrintListener(Logger _logger, String _message) {
			this.message = _message;
			this.logger = _logger;
		}
		public boolean notify(Proof v) {
			if (options.getBoolean(DISPLAY_AXIOMS)) {
				String msg = message.trim().replaceAll("\\n[ \\t]*", "  ");
				logger.log("Applied axiom: " + msg);
				
				if (DEBUG) {
					Logger sublogger = logger.getSubLogger();
					for (Property p : v.getProperties()) {
						sublogger.log("Property: " + p);
					}
				}
			}
			return true;
		}
		public boolean canUse(Proof v) {return true;}
	}
	
	private static class PrintStringListener implements EventListener<String> {
		private final String message;
		private final Logger logger;
		public PrintStringListener(Logger _logger, String _message) {
			this.message = _message;
			this.logger = _logger;
		}
		public boolean notify(String s) {
			if (options.getBoolean(DISPLAY_AXIOMS)) {
				logger.log("Applied axiom: " + message + (s==null ? "" : ": " + s));
			}
			return true;
		}
		public boolean canUse(String s) {return true;}
	}
	
	private static void addAxioms(
			final Logger logger, 
			PeggyAxiomSetup<JavaLabel,JavaParameter> setup,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			SootMethod method) {
		if (activatedAnalyses.contains("livsr")) {
			JavaLIVSRAnalysis analysis = 
				new JavaLIVSRAnalysis(network, engine) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll(logger);
			logger.log("Activating analysis: livsr");
		}

		if (activatedAnalyses.contains("inline")) {
			PeggyHeuristicInliner inliner = new PeggyHeuristicInliner(
					new DefaultReferenceResolver(), 
					bodyPegProvider, 
					network, 
					engine,
					ruleParser.getInlineMethods(),
					false) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			inliner.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			inliner.addStaticInliningAxioms(method);
			logger.log("Activating analysis: inline");
		} else if (activatedAnalyses.contains("inlineall")) {
			PeggyHeuristicInliner inliner = new PeggyHeuristicInliner(
					new DefaultReferenceResolver(), 
					bodyPegProvider, 
					network, 
					engine,
					ruleParser.getInlineMethods(),
					true) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			inliner.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			inliner.addStaticInliningAxioms(method);
			logger.log("Activating analysis: inlineall");
		}
		
		if (activatedAnalyses.contains("binop")) {
			JavaBinopConstantAnalysis analysis = 
				new JavaBinopConstantAnalysis(
						network,
						engine) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
			logger.log("Activating analysis: binop");
		}

		if (activatedAnalyses.contains("constant")) {
			JavaConstantAnalysis analysis = 
				new JavaConstantAnalysis(
						network,
						engine) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
			logger.log("Activating analysis: constant");
		}
		
		{
			JavaInvarianceAnalysis analysis = 
				new JavaInvarianceAnalysis(
						network,
						engine) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(logger, message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(logger, message));
					}
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
			analysis.addSigmaInvariantMethods(
					ruleParser.getSigmaInvariantMethods());
		}
		
		for (AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>> node : axioms) {
			Event<? extends Proof> event = setup.getEngine().addPEGAxiom(node);
			if (node.hasTag(XMLRuleParser.NAME_TAG))
				event.addListener(new PrintListener(logger, node.getTag(XMLRuleParser.NAME_TAG)));
			debug("adding parsed axiom to engine");
		}

		AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector =  
			ruleParser.getAxiomSelector();
		
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.BOOLEAN_AXIOMS)) {
			BooleanAxioms<JavaLabel,JavaParameter> axioms = 
				new BooleanAxioms<JavaLabel,JavaParameter>(setup);
			axioms.addNegateTrueIsFalse().addListener(new PrintListener(logger, "!T = F"));
			axioms.addNegateFalseIsTrue().addListener(new PrintListener(logger, "!F = T"));
			axioms.addNegateNegate().addListener(new PrintListener(logger, "!!B = B"));
			
			debug("adding boolean axioms");
		}
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EQUALITY_AXIOMS)) {
			EqualityAxioms<JavaLabel,JavaParameter> axioms = 
				new EqualityAxioms<JavaLabel,JavaParameter>(setup);
			axioms.addReflexiveEquals().addListener(new PrintListener(logger, "(X==X) = T"));
			axioms.addTrueEquals().addListener(new PrintListener(logger, "((X==Y)=T) => X=Y"));

			debug("adding equality axioms");
		}
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.PHI_AXIOMS)) {
			PhiAxioms<JavaLabel,JavaParameter> axioms = 
				new PhiAxioms<JavaLabel,JavaParameter>(setup);
			axioms.addPhiTrueCondition().addListener(new PrintListener(logger, "phi(T,b,c) = b"));
			axioms.addPhiFalseCondition().addListener(new PrintListener(logger, "phi(F,b,c) = c"));
			axioms.addPhiNegateCondition().addListener(new PrintListener(logger, "phi(!a,b,c) = phi(a,c,b)"));
			axioms.addJoinPhi().addListener(new PrintListener(logger, "phi(a,b,b) = b"));
			axioms.addPhiTrueFalse().addListener(new PrintListener(logger, "phi(c,t,f) = c"));
			axioms.addPhiFalseTrue().addListener(new PrintListener(logger, "phi(c,f,t) = !c"));
			
			debug("adding phi axioms");
			
			TemporaryPhiAxioms<JavaLabel,JavaParameter> tempaxioms = 
				new TemporaryPhiAxioms<JavaLabel,JavaParameter>(setup, 1);
			tempaxioms.addPhiOverPhiLeftAxiom().addListener(new PrintListener(logger, "phi(c,phi(c,t1,f1),f2) = phi(c,t1,f2)"));
			tempaxioms.addPhiOverPhiRightAxiom().addListener(new PrintListener(logger, "phi(c,t2,phi(c,t1,f1)) = phi(c,t2,f1)"));

			debug("adding temp phi axioms");
		}
		
		{
			LoopAxioms<JavaLabel,JavaParameter> loopaxioms = new LoopAxioms<JavaLabel,JavaParameter>(setup);
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL0_THETA_AXIOMS))
				loopaxioms.addEval0Theta().addListener(new PrintListener(logger, "invariant(b) => eval(theta(b, u),0) = b"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL_INVARIANT_AXIOMS))
				loopaxioms.addEvalInvariant().addListener(new PrintListener(logger, "invariant(x) => eval(x,i) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL_SUCC_SHIFT_AXIOMS))
				loopaxioms.addEvalSuccShift().addListener(new PrintListener(logger, "eval(x, succ(i)) = eval(shift(x), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.SHIFT_THETA_AXIOMS))
				loopaxioms.addShiftTheta().addListener(new PrintListener(logger, "shift(theta(b,u)) = u"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.SHIFT_INVARIANT_AXIOMS))
				loopaxioms.addShiftInvariant().addListener(new PrintListener(logger, "invariant(x) => shift(x) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.JOIN_THETA_AXIOMS))
				loopaxioms.addJoinTheta().addListener(new PrintListener(logger, "invariant(x) => theta(x,x) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_SHIFT_AXIOMS))
				loopaxioms.addDistributeShift().addListener(new PrintStructureListener(logger, "distribute shift"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_EVAL_AXIOMS))
				loopaxioms.addDistributeEval().addListener(new PrintStructureListener(logger, "distribute eval"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THROUGH_EVAL_AXIOMS))
				loopaxioms.addDistributeThroughEval().addListener(new PrintStructureListener(logger, "distribute through eval"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THROUGH_THETA_AXIOMS))
				loopaxioms.addDistributeThroughTheta().addListener(new PrintStructureListener(logger, "distribute through theta"));

			debug("adding loop axioms");		
		}

		{
			LoopInteractionAxioms<JavaLabel,JavaParameter> interaxioms = 
				new LoopInteractionAxioms<JavaLabel,JavaParameter>(setup);
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THETA_THROUGH_EVAL1_AXIOMS))
				interaxioms.addDistributeThetaThroughEval1().addListener(new PrintListener(logger, "invariant_1(i) ^ invariant_2(u) => "
						+ "theta_1(eval_2(b, i), u) = eval_2(theta_1(b, u), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THETA_THROUGH_EVAL2_AXIOMS))
				interaxioms.addDistributeThetaThroughEval2().addListener(new PrintListener(logger, "invariant_1(i) ^ invariant_2(b) => "
						+ "theta_1(b, eval_2(u, i)) = eval_2(theta_1(b, u), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_SHIFT_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributeShiftThroughEval().addListener(new PrintListener(logger, "shift_1(eval_2(x, i)) = eval_2(shift_1(x), shift_1(i))"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_PASS_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributePassThroughEval().addListener(new PrintListener(logger, "invariant_1(i) => "
						+ "pass_1(eval_2(c, i)) = eval_2(pass_1(c), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_EVAL_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributeEvalThroughEval().addListener(new PrintListener(logger, "invariant_1(i2) ^ invariant_2(i1) => "
						+ "eval_1(eval_2(x, i2), i1) = eval_2(eval_1(x, i1), i2)"));

			debug("adding loop interaction axioms");
		}
	}

	
	private static void setupTVEngine(
			Logger logger,
			JavaTranslationValidator tv,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			MergedPEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo,
			Map<Vertex<FlowValue<JavaParameter,JavaLabel>>, CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap) {
		PeggyAxiomSetup<JavaLabel,JavaParameter> setup = 
			new PeggyAxiomSetup<JavaLabel,JavaParameter>(network, ambassador, engine);
		addAxioms(logger.getSubLogger(), setup, engine, tv.getCurrentMethod());
		
		// the list we're going to get representations for
		List<Vertex<FlowValue<JavaParameter,JavaLabel>>> vertices = 
			new ArrayList<Vertex<FlowValue<JavaParameter,JavaLabel>>>();
		for (JavaReturn arr : peginfo.getReturns()) {
			vertices.add(peginfo.getReturnVertex1(arr));
			vertices.add(peginfo.getReturnVertex2(arr));
		}
		
		List<? extends CPEGTerm<JavaLabel,JavaParameter>> reps = 
			engine.addExpressions(vertices);

		// map for the reverter (maps significant vertexes to their reps)
		for (int i = 0; i < vertices.size(); i++)
			rootVertexMap.put(vertices.get(i), reps.get(i));
	}
	
	
	private static Pair<SootMethod,SootMethod> setupTV(Logger logger) {
		globalAmbassador = new JavaLabelOpAmbassador(
				new CustomAnnotationConstantFolder());
		
		// build OpAmbassador
		ambassador = new JavaLabelOpAmbassador(
				new CustomAnnotationConstantFolder());

		// build rule parsers
		ruleParser = new peggy.input.java.JavaXMLRuleParser(
				null, network, ambassador); 
	
		// read axiom files
		for (File axiomFile : axiomFiles) {
			try {
				axioms.addAll(ruleParser.parseRuleSet(axiomFile));
				logger.log("Successfully added axiom file: " + axiomFile.getPath());
			} catch (Throwable t) {
				abort("Error parsing axiom file: " + axiomFile.getPath(), t, logger);
			}
		}
		
		logger.log("Loading methods " + 
				translationValidationMethodSignatures.getFirst() + 
				" and " +
				translationValidationMethodSignatures.getSecond());
		
		// read the module file
		SootClass clazz1 = null, clazz2 = null;
		try {
			clazz1 = Scene.v().loadClassAndSupport(
					Scene.v().signatureToClass(translationValidationMethodSignatures.getFirst()));
			clazz2 = Scene.v().loadClassAndSupport(
					Scene.v().signatureToClass(translationValidationMethodSignatures.getSecond()));
			Scene.v().loadBasicClasses();
		} catch(Throwable t) {
			abort("Error loading classes", t, logger);
		}
		
		SootMethod method1 = null, method2 = null;
		try {
			method1 = clazz1.getMethod(
					Scene.v().signatureToSubsignature(
							translationValidationMethodSignatures.getFirst()));
			method2 = clazz2.getMethod(
					Scene.v().signatureToSubsignature(
							translationValidationMethodSignatures.getSecond()));
		} catch(Throwable t) {
			abort("Cannot find methods", t, logger);
		}
		if (method1 == null || !method1.isConcrete())
			abort("Method has no body " + translationValidationMethodSignatures.getFirst(), logger);
		if (method2 == null || !method2.isConcrete())
			abort("Method has no body " + translationValidationMethodSignatures.getSecond(), logger);
		
		return new Pair<SootMethod,SootMethod>(method1, method2);
	}
	
	
	private static JavaTranslationValidator
	getTranslationValidator() {
		JavaTranslationValidator tv = 
			new JavaTranslationValidator() {
				public EngineRunner<JavaLabel,JavaParameter> getEngineRunner() {return engineRunner;}
				protected CPeggyAxiomEngine<JavaLabel,JavaParameter> createEngine(
						MergedPEGInfo<JavaLabel,JavaParameter,JavaReturn> mergedpeginfo,
						Map<Vertex<FlowValue<JavaParameter,JavaLabel>>, CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap) {
					CPeggyAxiomEngine<JavaLabel,JavaParameter> engine = 
						options.getBoolean(ENABLE_PROOFS) ?
						new CPeggyAxiomEngine<JavaLabel,JavaParameter>(ambassador) :
						new CPeggyAxiomEngine<JavaLabel,JavaParameter>(ambassador, null);

					Main.setupTVEngine(this.getLogger(), this, engine, mergedpeginfo, rootVertexMap);
					return engine;
				}
				protected MergedPEGInfo<JavaLabel,JavaParameter,JavaReturn> mergePEGs(
						PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo1,
						PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo2) {
					final Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> map1 = 
						new HashMap<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>>();
					final Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> map2 = 
						new HashMap<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>>();
					
					for (JavaReturn arr : peginfo1.getReturns()) {
						Vertex<FlowValue<JavaParameter,JavaLabel>> vertex = 
							rebuildAndRetype(peginfo1.getGraph(), peginfo2.getReturnVertex(arr), tvClassRenamingMap);
						vertex.makeSignificant();
						map1.put(arr, peginfo1.getReturnVertex(arr));
						map2.put(arr, vertex);
					}
					
					return new MergedPEGInfo<JavaLabel,JavaParameter,JavaReturn>(
							peginfo1.getGraph(), map1, map2) {
						public Collection<? extends JavaReturn> getReturns() {
							return map1.keySet();
						}
					};
				}
				protected void enginePostPass(CPeggyAxiomEngine<JavaLabel,JavaParameter> engine) {
					EngineThetaMerger<JavaLabel,JavaParameter> merger = 
						new EngineThetaMerger<JavaLabel, JavaParameter>(engine);
//					merger.setTimeout(options.getLong(THETA_MERGER_TIMEOUT));
					merger.mergeThetas();
				}
			};
		return tv;
	}
	
	static class Holder<T> {
		private T thing;
		public Holder() {}
		public void set(T t) {thing = t;}
		public T get() {return thing;}
	}

	
	private static TVListener<JavaLabel,JavaParameter,JavaReturn>
	getOutputTVListener(final Logger logger) {
		return new TVListener<JavaLabel,JavaParameter,JavaReturn>() {
			public void beginValidation(
					String method1, 
					String method2,
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo1,
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo2) {
				logger.log("Beginning validation of " + method1 + " and " + method2);
			}
			public void notifyMergedPEGBuilt(MergedPEGInfo<JavaLabel,JavaParameter,JavaReturn> merged) {
				logger.log("Built merged PEG");
			}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
					Map<Vertex<FlowValue<JavaParameter,JavaLabel>>,CPEGTerm<JavaLabel,JavaParameter>> _rootVertexMap) {							
				logger.log("Engine setup complete");
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<JavaLabel,JavaParameter> engine) {
				logger.log("Engine finished running");
			}
			public void notifyReturnsEqual(
					JavaReturn arr,
					CPEGTerm<JavaLabel,JavaParameter> root1,
					CPEGTerm<JavaLabel,JavaParameter> root2) {
				if (arr.equals(JavaReturn.SIGMA)) {
					logger.log("Sigma roots validated");
				} else if (arr.equals(JavaReturn.VALUE)) {
					logger.log("Value roots validated");
				} else {
					throw new IllegalArgumentException("Unknown return: " + arr);
				}
			}
			public void endValidation() {
				logger.log("Validation completed");
			}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<JavaLabel, JavaParameter, JavaReturn> merged) {
				logger.log("Merged PEG roots already equal, skipping engine");
			}
		};
		
	}
	
	
	private static DotTVListener<JavaLabel,JavaParameter,JavaReturn> 
	getDotTVListener(final String prefix) {
		return new DotTVListener<JavaLabel,JavaParameter,JavaReturn>(
				OUTPUT_ORIGINAL_PEG, OUTPUT_ORIGINAL_PEG, OUTPUT_EPEG) {
			protected String getOriginalPEG1Filename() {return prefix + "peg1.dot";}
			protected String getOriginalPEG2Filename() {return prefix + "peg2.dot";}
			protected String getEPEGFilename() {return prefix + "epeg.dot";}
			protected String getMergedPEGFilename() {return prefix + "merged.dot";}
		};
	}
	
	
	private static void performTranslationValidation(
			Logger logger,
			final JavaTranslationValidator tv, 
			SootMethod method1, SootMethod method2) {

		if (!bodyPegProvider.canProvidePEG(method1))
			abort("Cannot build PEG from method: " + method1.getSignature(), logger);
		if (!bodyPegProvider.canProvidePEG(method2))
			abort("Cannot build PEG from method: " + method2.getSignature(), logger);

		TVTimerListener<JavaLabel,JavaParameter,JavaReturn> timer = 
			new TVTimerListener<JavaLabel,JavaParameter,JavaReturn> ();
		TVLastDataListener<JavaLabel,JavaParameter,JavaReturn> lastData = 
			new TVLastDataListener<JavaLabel,JavaParameter,JavaReturn> ();
		
		Logger tvLogger = logger.getSubLogger();
		
		tv.addListener(getDotTVListener("tv_"));
		tv.addListener(getOutputTVListener(tvLogger));
		tv.addListener(timer);
		tv.addListener(lastData);
		
		// add halt listener
		tv.addListener(new TVListener<JavaLabel, JavaParameter, JavaReturn>() {
			Set<JavaReturn> returns = null;
			public void beginValidation(String functionName1,
					String functionName2,
					PEGInfo<JavaLabel, JavaParameter, JavaReturn> peginfo1,
					PEGInfo<JavaLabel, JavaParameter, JavaReturn> peginfo2) {
				returns = new HashSet<JavaReturn>(peginfo1.getReturns());
			}
			public void endValidation() {}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<JavaLabel, JavaParameter> engine) {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<JavaLabel, JavaParameter> engine,
					Map<Vertex<FlowValue<JavaParameter, JavaLabel>>, CPEGTerm<JavaLabel, JavaParameter>> rootVertexMap) {}
			public void notifyMergedPEGBuilt(
					MergedPEGInfo<JavaLabel, JavaParameter, JavaReturn> merged) {}
			public void notifyReturnsEqual(JavaReturn arr,
					CPEGTerm<JavaLabel, JavaParameter> root1,
					CPEGTerm<JavaLabel, JavaParameter> root2) {
				returns.remove(arr);
				if (returns.size() == 0)
					tv.getEngineRunner().halt();
			}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<JavaLabel, JavaParameter, JavaReturn> merged) {
			}
		});
		
		
		PEGInfo<JavaLabel,JavaParameter,JavaReturn> 
			peginfo1 = bodyPegProvider.getPEG(method1),
			peginfo2 = bodyPegProvider.getPEG(method2);
		
		tv.setCurrentMethod(method1);
		
		tvClassRenamingMap.clear();
		tvClassRenamingMap.put(
				method2.getDeclaringClass().getName(),
				method1.getDeclaringClass().getName());
		
		tv.validate(
				method1.getSignature(),
				method2.getSignature(),
				peginfo1, peginfo2);

		tvLogger.log("ENGINEITERS " + engineRunner.lastIterStop);
		tvLogger.log("ENGINETIME " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()));
		tvLogger.log("Validation took " + (timer.getEndValidationTime() - timer.getBeginValidationTime()) + " milliseconds");

		Map<JavaReturn,Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>> validatedReturns = 
			new HashMap<JavaReturn,Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>>();
		for (JavaReturn arr : peginfo1.getReturns()) {
			if (lastData.hasValidatedReturn(arr)) {
				validatedReturns.put(arr, 
						lastData.getValidatedPair(arr));
			} else {
				tvLogger.log("Could not validate " + arr);
			}
		}
		
		if (validatedReturns.keySet().containsAll(peginfo1.getReturns())) {
			tvLogger.log("Validate entire optimization!");
			// compute time of equality
			int max = Integer.MIN_VALUE;
			for (JavaReturn arr : peginfo1.getReturns()) {
				max = Math.max(max,
						lastData.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
								lastData.getValidatedPair(arr).getFirst(),
								lastData.getValidatedPair(arr).getSecond()));
								
								
								//lastData.getLastRootVertexMap().get(peginfo1.getReturnVertex(arr)),
								//lastData.getLastRootVertexMap().get(peginfo2.getReturnVertex(arr))));
			}
			tvLogger.log("TIMEOFEQUALITY " + max);
		} else if (validatedReturns.keySet().size() == 0) {
			tvLogger.log("Could not validate any of the optimization");
		}
		
		logger.log("Done validating");
	}
	
	private static final NamedTag<Vertex<FlowValue<JavaParameter,JavaLabel>>> REBUILD_TAG = 
		new NamedTag<Vertex<FlowValue<JavaParameter,JavaLabel>>>("REBUILD_CACHE");
	/**
	 * Rebuilds the subgraph rooted at 'oldvertex' in the CREG 'newgraph'.
	 * This will also perform a retyping, so that the inputs of newgraph should equal the inputs
	 * of the oldvertex's graph.
	 */
	private static Vertex<FlowValue<JavaParameter,JavaLabel>> rebuildAndRetype(
			CRecursiveExpressionGraph<FlowValue<JavaParameter,JavaLabel>> newgraph,
			Vertex<FlowValue<JavaParameter,JavaLabel>> oldvertex,
			Map<String,String> before2after) {
		if (oldvertex.hasTag(REBUILD_TAG)) {
			if (oldvertex.getTag(REBUILD_TAG) == null) {
				Vertex<FlowValue<JavaParameter,JavaLabel>> result = 
					newgraph.createPlaceHolder();
				oldvertex.setTag(REBUILD_TAG, result);
				return result;
			} else {
				return oldvertex.getTag(REBUILD_TAG);
			}
		}
		
		if (oldvertex.getLabel().isParameter()) {
			JavaParameter newparam = 
				PEGRetyper.retypeParameter(oldvertex.getLabel().getParameter(), before2after);
			Vertex<FlowValue<JavaParameter,JavaLabel>> result =
				newgraph.getVertex(FlowValue.<JavaParameter,JavaLabel>createParameter(newparam));
			oldvertex.setTag(REBUILD_TAG, result);
			return result;
		} else if (oldvertex.getLabel().isDomain()) {
			JavaLabel newlabel = PEGRetyper.retypeLabel(oldvertex.getLabel().getDomain(), before2after);
			List<Vertex<FlowValue<JavaParameter,JavaLabel>>> children = 
				new ArrayList<Vertex<FlowValue<JavaParameter,JavaLabel>>>(oldvertex.getChildCount());
			oldvertex.setTag(REBUILD_TAG, null);
			for (int i = 0; i < oldvertex.getChildCount(); i++) {
				children.add(rebuildAndRetype(newgraph, oldvertex.getChild(i), before2after));
			}
			Vertex<FlowValue<JavaParameter,JavaLabel>> result =
				newgraph.getVertex(
						FlowValue.<JavaParameter,JavaLabel>createDomain(newlabel, globalAmbassador),
						children);
			if (oldvertex.getTag(REBUILD_TAG) != null) {
				oldvertex.getTag(REBUILD_TAG).replaceWith(result);
			} else {
				oldvertex.setTag(REBUILD_TAG, result);
			}
			return result;
		} else {
			List<Vertex<FlowValue<JavaParameter,JavaLabel>>> children = 
				new ArrayList<Vertex<FlowValue<JavaParameter,JavaLabel>>>(oldvertex.getChildCount());
			oldvertex.setTag(REBUILD_TAG, null);
			for (int i = 0; i < oldvertex.getChildCount(); i++) {
				children.add(rebuildAndRetype(newgraph, oldvertex.getChild(i), before2after));
			}
			Vertex<FlowValue<JavaParameter,JavaLabel>> result =
				newgraph.getVertex(oldvertex.getLabel(), children);
			
			if (oldvertex.getTag(REBUILD_TAG) != null) {
				oldvertex.getTag(REBUILD_TAG).replaceWith(result);
			} else {
				oldvertex.setTag(REBUILD_TAG, result);
			}
			return result;
		}
	}
	
	public static void main(String args[]) {
		// process the arguments
		if (args.length == 0)
			displayHelp();
		
		try {
			optionsParser.parse(args);
		} catch (OptionParsingException ex) {
			abort("Error parsing command line: " + ex.getMessage(), TOP_LOGGER);
		}
		
		if (translationValidationMethodSignatures != null) {
			Pair<SootMethod,SootMethod> methods = setupTV(TOP_LOGGER);
			JavaTranslationValidator tv = 
				getTranslationValidator();
			performTranslationValidation(TOP_LOGGER,
					tv, methods.getFirst(), methods.getSecond());
		} else {
			abort("No validation specified", TOP_LOGGER);
		}
		
		System.exit(0);
	}
}
