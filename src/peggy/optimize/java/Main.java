package peggy.optimize.java;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import peggy.Logger;
import peggy.OptionParsingException;
import peggy.OptionsParser;
import peggy.analysis.BoundedEngineRunner;
import peggy.analysis.EngineRunner;
import peggy.analysis.EngineThetaMerger;
import peggy.analysis.PEGCostCalculator;
import peggy.analysis.TemporaryPhiAxioms;
import peggy.analysis.java.JavaBinopConstantAnalysis;
import peggy.analysis.java.JavaConstantAnalysis;
import peggy.analysis.java.JavaInvarianceAnalysis;
import peggy.analysis.java.JavaLIVSRAnalysis;
import peggy.analysis.java.inlining.PeggyHeuristicInliner;
import peggy.ilp.GLPKRunner;
import peggy.input.XMLRuleParser;
import peggy.input.java.JavaCostModel;
import peggy.optimize.DotOptimizerListener;
import peggy.optimize.DotPEG2PEGListener;
import peggy.optimize.Generalizer;
import peggy.optimize.GeneralizerListener;
import peggy.optimize.GeneralizerTimerListener;
import peggy.optimize.MultiStageOptimizer;
import peggy.optimize.Optimizer;
import peggy.optimize.OptimizerAdapter;
import peggy.optimize.OptimizerLastDataListener;
import peggy.optimize.OptimizerListener;
import peggy.optimize.OptimizerTimerListener;
import peggy.optimize.PEG2PEGLastDataListener;
import peggy.optimize.PEG2PEGListener;
import peggy.optimize.PEG2PEGOptimizer;
import peggy.optimize.PEG2PEGTimer;
import peggy.optimize.SingleStageOptimizer;
import peggy.optimize.SingleStageOptimizer.Level;
import peggy.pb.AverageReversionHeuristic;
import peggy.pb.ConfigurableCostModel;
import peggy.pb.CostModel;
import peggy.pb.DefaultGreedyReversionHeuristic;
import peggy.pb.LooplessReversionHeuristic;
import peggy.pb.MinisatFormulation;
import peggy.pb.MinisatRunner;
import peggy.pb.NondomainStickyPredicate;
import peggy.pb.PBRunner;
import peggy.pb.PuebloFormulation;
import peggy.pb.PuebloRunner;
import peggy.represent.CombinedStickyPredicate;
import peggy.represent.DefaultPEGExtractor;
import peggy.represent.FlowValueStickyPredicate;
import peggy.represent.FuturePEGExtractor;
import peggy.represent.PEGExtractor;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import peggy.represent.StickyPredicate;
import peggy.represent.java.CustomAnnotationConstantFolder;
import peggy.represent.java.DefaultReferenceResolver;
import peggy.represent.java.JavaCREGXML2PEG;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelOpAmbassador;
import peggy.represent.java.JavaLabelStickyPredicate;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.PEGRetyper;
import peggy.represent.java.SootMethodPEGProvider;
import peggy.represent.java.SootUtils;
import peggy.revert.PEGValidityChecker;
import peggy.revert.ReversionHeuristic;
import peggy.revert.java.DefaultGLPKReversionHeuristic;
import peggy.revert.java.DefaultPBReversionHeuristic;
import peggy.revert.java.JavaPEGCFG;
import peggy.revert.java.JavaPEGCFGEncoder;
import soot.Body;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.baf.Baf;
import soot.baf.BafBody;
import soot.jimple.JimpleBody;
import soot.util.JasminOutputStream;
import util.Action;
import util.NamedTag;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.engine.AxiomSelector;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.generalize.ExpressionTightener;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEGAxiomizer;
import eqsat.meminfer.engine.generalize.ProofPostMultiGeneralizer;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
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
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;
import eqsat.revert.RevertCFG;

/**
 * This is the command-line interface class for the Java optimizer.
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

	private static enum PB {
		AVERAGE,
		GREEDY,
		PUEBLO,
		MINISAT,
		GLPK;
	}
	
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

	private static class StageInfo {
		File dotOutputFolder = new File(".");
		Network network = new Network();
		peggy.input.java.JavaXMLRuleParser ruleParser;
		final Collection<AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>>> axioms = 
			new ArrayList<AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>>>(100);
		final Set<String> activatedAnalyses = new HashSet<String>();
		int maxPBFileSize = 0;
		PB pbOption = PB.MINISAT;
		int pbTimeout = 0; // in milliseconds
		final Set<File> axiomFiles = new HashSet<File>();
		boolean OUTPUT_EPEG = false;
		boolean OUTPUT_OPTIMAL_PEG = false;
		final MyEngineRunner engineRunner = new MyEngineRunner();
		JavaLabelOpAmbassador ambassador;
		boolean looplessReversion = false;
	}
	
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
	private static final String PUEBLO_PATH = "puebloPath";
	private static final String MINISAT_PATH = "minisatPath";
	private static final String GLPK_PATH = "glpkPath";
	private static final String USE_SOOT_OPTS = "useSootOpts";
	private static final String UNSOUND_REVERSION = "unsoundReversion";
	private static final String ENGINE_ONLY = "engineOnly";
	private static final String GENERALIZE_TIMEOUT = "generalizeTimeout";
	private static final String DUMP_PROOF = "proof";
	private static final String TMP_FOLDER = "tmpFolder";
	private static final String USE_CFG_EXCEPTIONS = "exceptions";
	private static final String DISPLAY_AXIOMS = "displayAxioms";
	private static final String INCREMENTAL_FUNCTION = "incremental";
	private static final String BC_THRESHOLD = "bcthreshold";
	private static final String DELETE_PB_FILES = "deletepb"; 
	private static final String OUTPUT_FOLDER = "o";
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
		options.registerStringPair("learn", null, null,
				"Specify the learning file and optimizing file",
				new Action<Pair<String,String>>() {
					public void execute(Pair<String,String> pair) {
						if (optimizationLevel != null)
							throw new OptionParsingException("Cannot both learn and optimize");
						
						learningMode = true;
						try {
							learningMethodFile = new File(pair.getFirst());
						} catch (Throwable t) {
							throw new OptionParsingException("Bad filename " + pair.getFirst(), t);
						}

						try {
							optimizingMethodFile = new File(pair.getSecond());
						} catch (Throwable t) {
							throw new OptionParsingException("Bad filename " + pair.getSecond(), t);
						}
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
						stages.get(stages.size()-1).OUTPUT_EPEG = true;
					}
				});
		optionsParser.registerCommand("oopt", 
				"Set to true to output a dot graph of the optimal PEG",
				new Runnable() {
					public void run() {
						stages.get(stages.size()-1).OUTPUT_OPTIMAL_PEG = true;
					}
				});
		optionsParser.registerCommand("orev", 
				"Set to true to output a dot graph of the revert graph",
				new Runnable() {
					public void run() {
						OUTPUT_REVERT_GRAPH = true;
					}
				});
		optionsParser.registerCommand("orevcfg", 
				"Set to true to output a dot graph of the revert CFG",
				new Runnable() {
					public void run() {
						OUTPUT_REVERT_CFG = true;
					}
				});
		optionsParser.registerCommand("ooutcfg", 
				"Set to true to output a dot graph of the output CFG",
				new Runnable() {
					public void run() {
						OUTPUT_OUTPUT_CFG = true;
					}
				});
		options.registerFile(OUTPUT_FOLDER, new File("optimized"),
				"Specify where optimized classfiles are placed (default 'optimized/')", null);
		options.registerString("exf", null,
				"Specify a file full of names of functions to skip",
				new Action<String>() {
					public void execute(String str) {
						try {
							BufferedReader bin = new BufferedReader(new FileReader(str));
							String line;
							while ((line = bin.readLine()) != null) {
								line = line.trim();
								if (!line.equals("")) {
									skippedFunctions.add(line);
								}
							}
							bin.close();
						} catch (Throwable t) {
							throw new OptionParsingException("Cannot read exclude list", t);
						}
					}
				});
		options.registerString("exclude", null,
				"Specify a double-colon-separated list of function signatures to skip",
				new Action<String>() {
					public void execute(String str) {
						String[] list = str.split("::");
						for (String item : list) {
							item = item.trim();
							if (!item.equals(""))
								skippedFunctions.add(item);
						}
					}
				});
		options.registerString("O0", null,
				"Specify a class to optimize at level 0",
				new Action<String>() {
					public void execute(String str) {
						if (inputClassName != null)
							throw new OptionParsingException("Duplicate input class specified: " + str);
						if (stages.size() != 1)
							throw new OptionParsingException("Cannot specify optimization level with multi-stage optimization");
						if (learningMode)
							throw new OptionParsingException("Already specified learning mode");
						inputClassName = str;
						optimizationLevel = SingleStageOptimizer.Level.PARSE_AND_REWRITE;
					}
				});
		options.registerString("O1", null,
				"Specify a class to optimize at level 1",
				new Action<String>() {
					public void execute(String str) {
						if (inputClassName != null)
							throw new OptionParsingException("Duplicate input class specified: " + str);
						if (stages.size() != 1)
							throw new OptionParsingException("Cannot specify optimization level with multi-stage optimization");
						if (learningMode)
							throw new OptionParsingException("Already specified learning mode");
						inputClassName = str;
						optimizationLevel = SingleStageOptimizer.Level.PEG_AND_BACK;
					}
				});
		options.registerString("O2", null,
				"Specify a class to optimize at level 2",
				new Action<String>() {
					public void execute(String str) {
						if (inputClassName != null)
							throw new OptionParsingException("Duplicate input class specified: " + str);
						if (stages.size() != 1)
							throw new OptionParsingException("Cannot specify optimization level with multi-stage optimization");
						if (learningMode)
							throw new OptionParsingException("Already specified learning mode");
						inputClassName = str;
						optimizationLevel = SingleStageOptimizer.Level.RUN_ENGINE_FULL;
					}
				});
		options.registerString("pb", null,
				"Specify which PB solver to use (default minisat)",
				new Action<String>() {
					public void execute(String str) {
						StageInfo currentStage = stages.get(stages.size()-1);
						if (str.toLowerCase().equals("pueblo")) {
							currentStage.pbOption = PB.PUEBLO;
						} 
						else if (str.toLowerCase().equals("minisat")) {
							currentStage.pbOption = PB.MINISAT;
						}
						else if (str.toLowerCase().equals("glpk")) {
							currentStage.pbOption = PB.GLPK;
						}
						else if (str.toLowerCase().equals("greedy")) {
							currentStage.pbOption = PB.GREEDY;
							// TODO rename this so it's more like "reversion" not "pb"
						}
						else if (str.toLowerCase().equals("average")) {
							currentStage.pbOption = PB.AVERAGE;
						}
						else {
							throw new OptionParsingException("Unknown PB solver: " + str);
						}
					}
				});
		options.registerLong("pbtime", 0L,
				"Specify the max time the PB solver may run",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).pbTimeout = value.intValue();
					}
				});	
		options.registerBoolean(DELETE_PB_FILES, false,
				"Set to true to delete all temporary files created (default false)");
		options.registerString("axioms", null,
				"Specify a colon-separated list of axiom input files",
				new Action<String>() {
					public void execute(String str) {
						String[] fileNames = str.split(":");
						for (String fileName : fileNames) {
							File file = new File(fileName);
							if (!file.exists())
								throw new OptionParsingException("Axiom input file does not exist: " + fileName);
							stages.get(stages.size()-1).axiomFiles.add(file);
						}
					}
				});
		options.registerLong("pbfilemax", 0L,
				"Specify the maximum size in bytes of a PB file",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).maxPBFileSize = value.intValue();
					}
				});
		options.registerLong("maxmemory", 0L,
				"Specify the maximum amount of memory the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setMemoryUpperBound(value);
					}
				});
		options.registerLong("eto", 1000L,
				"Specify the maximum number of iterations the engine may run (default 1000)",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setIterationUpperBound(value);
					}
				});
		options.registerLong("maxtime", 0L,
				"Specify the maximum amoutn of time the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setTimeUpperBound(value);
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
							stages.get(stages.size()-1).activatedAnalyses.add(a);
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
		options.registerBoolean(ENGINE_ONLY, false,
				"Set to true to run the engine only and not revert the EPEG (default false)");
		options.registerLong(GENERALIZE_TIMEOUT, 0L,
				"Specify a timeout (in milliseconds) on the generalizer", null);
		options.registerLong("mergeTimeUpdate", 0L,
				"Specify the time between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long l) {
						stages.get(stages.size()-1).engineRunner.setTimeUpdate(l);
					}
				});
		options.registerLong("mergeIterationUpdate", 0L,
				"Specify the number of iterations between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long l) {
						stages.get(stages.size()-1).engineRunner.setIterationUpdate(l);
					}
				});
		options.registerFile("dotOutputFolder", null,
				"Specify the folder where the dot graphs will be put (default '.')",
				new Action<File>() {
					public void execute(File f) {
						stages.get(stages.size()-1).dotOutputFolder = f;
					}
				});
		options.registerBoolean(UNSOUND_REVERSION, false, 
				"Set to true to allow potentially unsound (but faster) reversion (default false)");
		options.registerBoolean("looplessReversion", false,
				"Set to true to use loopless reversion (default false)",
				new Action<Boolean>() {
					public void execute(Boolean b) {
						stages.get(stages.size()-1).looplessReversion = b;
					}
				});
		options.registerString("pegxml", null,
				"Specify pair of 'func=file.xml' to load the PEG of a function from an XML file",
				new Action<String>() {
					public void execute(String str) {
						String pair = str;
						int equalsIndex = pair.indexOf('=');
						if (equalsIndex < 0)
							throw new OptionParsingException("pegxml pair has no '='");
						String signature = pair.substring(0,equalsIndex);
						File xmlfile = new File(pair.substring(equalsIndex+1));
						revertFromFileMap.put(signature, xmlfile);
					}
				});
		options.registerBoolean(USE_SOOT_OPTS, false,
				"Set to true to use the Soot bytecode opts as a final phase (default false)");
		optionsParser.registerCommand("newstage", 
				"Begin the options for the next stage of optimizations",
				new Runnable() {
					public void run() {
						if (!(optimizationLevel == null || optimizationLevel.equals(Level.RUN_ENGINE_FULL)))
							throw new OptionParsingException("Cannot have multiple stages when optimization level is specified");
						stages.add(new StageInfo());
					}
				});
		options.registerString(MINISAT_PATH, System.getenv("COLLIDER_ROOT") + "/scripts/minisat/Minisat",
				"Specify the path to the Minisat executable (default $COLLIDER_ROOT/scripts/minisat/Minisat)");
		options.registerString(GLPK_PATH, "/usr/bin/glpsol",
				"Specify the path to the GLPK executable (default /usr/bin/glpsol)");
		options.registerString(PUEBLO_PATH, System.getenv("COLLIDER_ROOT") + "/scripts/pueblo/Pueblo",
				"Specify the path to the Pueblo executable (default $COLLIDER_ROOT/scripts/pueblo/Pueblo)");
	}
	
	

	private static final Tag<CPEGTerm<JavaLabel,JavaParameter>> TERM_TAG = 
		new NamedTag<CPEGTerm<JavaLabel,JavaParameter>>("Tags vertices with their terms");
	// maps method signatures to XML files of PEGs
	private static final Map<String,File> revertFromFileMap = new HashMap<String,File>();
	private static boolean learningMode = false;
	private static File learningMethodFile;
	private static File optimizingMethodFile;
	private static final List<StageInfo> stages = new ArrayList<StageInfo>(5);
	private static final Set<String> skippedFunctions = new HashSet<String>();
	private static final Set<File> tempFiles = new HashSet<File>();
	private static SingleStageOptimizer.Level optimizationLevel;
	private static String inputClassName;
	private static boolean OUTPUT_ORIGINAL_PEG = false;
	private static boolean OUTPUT_REVERT_GRAPH = false;
	private static boolean OUTPUT_REVERT_CFG = false;
	private static boolean OUTPUT_OUTPUT_CFG = false;
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
	
	private static int totalBitcodeCount(Body body) {
		return body.getUnits().size();
	}
	
	private static void optimizeClass(
			Logger logger,
			SootClass clazz,
			String inputClass,
			File outputFolder,
			Set<String> skippedMethodSet) {

		logger.log("Optimizing class " + inputClass);
		Logger methodLogger = logger.getSubLogger().getSubLogger();
		
		if (stages.size() == 1) {
			SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> single = 
				getSingleStageOptimizer(stages.get(0), optimizationLevel);
			single.addListener(getDotOptimizerListener(stages.get(0), "single_"));
			single.addListener(getOutputOptimizerListener(methodLogger));
			single.getPEG2PEGOptimizer().addListener(getDotPEG2PEGListener(stages.get(0), "single_"));
			single.getPEG2PEGOptimizer().addListener(getOutputPEG2PEGListener(methodLogger));

			optimizeAll(logger, single, clazz, inputClass, outputFolder, skippedMethodSet);
		} else {
			optimizeAll(logger, getMultiStageOptimizer(methodLogger), clazz, inputClass, outputFolder, skippedMethodSet);
		}
	}
	
	
	private static void revertFromXMLFile(Logger logger, SootMethod method) {
		File xmlfile = revertFromFileMap.get(method.getSignature());
		PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo = null;
		
		logger.log("Reverting method " + method.getSignature() + " from XML file " + xmlfile);
		
		MethodJavaLabel label = new MethodJavaLabel(
				method.getDeclaringClass().getName(), 
				method.getName(), 
				method.getReturnType(), 
				method.getParameterTypes());
		
		try {
			JavaCREGXML2PEG xml2peg = new JavaCREGXML2PEG(label, method.makeRef(), stages.get(0).ambassador);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(xmlfile);
			peginfo = xml2peg.parsePEGInfo(doc.getDocumentElement());
		} catch (Throwable t) {
			abort("Error parsing XML PEG file", t, logger);
		}

		int pegcost = calculatePEGCost(peginfo, stages.get(0));
		logger.log("Optimal PEG cost: " + pegcost);
		
		if (stages.get(0).OUTPUT_OPTIMAL_PEG) {
			String filename = "OPTPEG_" + method.getSignature().replace(' ', '_') + ".dot";
			try {
				PrintStream out = new PrintStream(new FileOutputStream(filename));
				out.println(peginfo.getGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
		
		Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> revertMap = 
			new HashMap<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>>();
		for (JavaReturn arr : peginfo.getReturns())
			revertMap.put(arr, peginfo.getReturnVertex(arr));
		
		
		Map<JavaReturn,ReversionGraph<JavaParameter,JavaLabel>.Vertex> tempmap = 
			new HashMap<JavaReturn,ReversionGraph<JavaParameter,JavaLabel>.Vertex> ();
		ReversionGraph<JavaParameter,JavaLabel> result = 
			new ReversionGraph<JavaParameter,JavaLabel>(
				stages.get(0).ambassador,
				peginfo.getGraph(), 
				revertMap,
				tempmap);
		
		CFGReverter<JavaParameter,JavaLabel,JavaReturn> reverter =
			new CFGReverter<JavaParameter,JavaLabel,JavaReturn>(
					result, 
					tempmap, 
					stages.get(0).ambassador);
		RevertCFG revert = reverter.getCFG();
		
		if (OUTPUT_REVERT_CFG) {
			String filename = "REVCFG_" + method.getSignature().replace(' ', '_') + ".dot";
			try {
				PrintStream out = new PrintStream(new FileOutputStream(filename));
				out.println(revert.toString());
				out.close();
			} catch (Throwable t) {}
		}
		
		JavaPEGCFG outputCFG = new JavaPEGCFG(revert);

		if (OUTPUT_OUTPUT_CFG) {
			String filename = "OUTCFG_" + method.getSignature().replace(' ', '_') + ".dot";
			try {
				PrintStream out = new PrintStream(new FileOutputStream(filename));
				out.println(outputCFG.toString());
				out.close();
			} catch (Throwable t) {}
		}
		
		JavaPEGCFGEncoder encoder = new JavaPEGCFGEncoder(
				outputCFG, 
				method,
				new DefaultReferenceResolver());
		encoder.encode();
	}
	
	
	private static void optimizeAll(
			Logger topLogger,
			Optimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> optimizer,
			SootClass clazz,
			String inputClass,
			File outputFolder,
			Set<String> skippedMethodSet) {
		long startTotalTime = System.currentTimeMillis();
		
		Logger classLogger = topLogger.getSubLogger();
		
		int buggyMethodCount = 0;
		int skippedMethodCount = 0;
		int totalMethodCount = 0;
		
		PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> peg2pegData = 
			new PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
		PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> peg2pegTimer = 
			new PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
		OptimizerLastDataListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod> optimizerData =
			new OptimizerLastDataListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>();
		OptimizerTimerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod> timer = 
			new OptimizerTimerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>();
		optimizer.addListener(timer);
		optimizer.addListener(optimizerData);
		boolean isSingle = (optimizer instanceof SingleStageOptimizer);
		PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> peg2peg = isSingle ? 
				((SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn>)optimizer).getPEG2PEGOptimizer() :
				null;

		if (isSingle) {
			peg2peg.addListener(peg2pegData);
			peg2peg.addListener(peg2pegTimer);
		}
				
		for (Iterator<SootMethod> methoditer=clazz.getMethods().iterator(); methoditer.hasNext(); ) {
			SootMethod method = methoditer.next();
			String fullName = method.getSignature();
			totalMethodCount++;
			
			if (revertFromFileMap.containsKey(fullName)) {
				// read in an XML file of a PEG and revert that (do not run engine)
				revertFromXMLFile(classLogger, method);
				continue;
			}
			
			
			if (!(method.isConcrete())) {
				classLogger.log("Skipping function " + fullName);
				skippedMethodCount++;
				continue;
			}
				
			Body body = method.retrieveActiveBody();
				
			if (skippedMethodSet.contains(fullName)) {
				classLogger.log("Skipping function " + fullName);
				skippedMethodCount++;
				continue;
			}
			
			if (options.getLong(BC_THRESHOLD) > 0 && 
				totalBitcodeCount(body) > options.getLong(BC_THRESHOLD)) {
				classLogger.log("Method " + fullName + " exceeds bytecode threshold, skipping");
				skippedMethodCount++;
				continue;
			}
			
			if (SootUtils.hasExceptions(method)) {
				classLogger.log("Method " + fullName + " contains exceptions, skipping");
				skippedMethodCount++;
				continue;
			} 

			classLogger.log("Processing method " + fullName);
			
			Logger methodLogger = classLogger.getSubLogger();
			
			try {
				peg2peg.setLogger(methodLogger);
				optimizer.setLogger(methodLogger);
				optimizer.optimize(method);
				methodLogger.log("Optimization of method " + fullName + " SUCCESSFUL");
				methodLogger.log("Optimization took " + (timer.getEndFunctionTime() - timer.getBeginFunctionTime()));
			} catch (Throwable t) {
				buggyMethodCount++;
				methodLogger.logException("Error processing method " + fullName, t);
				methodLogger.log("Reverting to original method body");
				methodLogger.log("Optimization of method " + fullName + " FAILED");
				
				continue;
			} finally {
				if (options.getBoolean(DELETE_PB_FILES))
					deleteTempFiles();
			}
		
			if (isSingle) {
				// output some stats
				methodLogger.log("PEG2PEGTIME " + (peg2pegTimer.getEndTime() - peg2pegTimer.getBeginTime()));
				methodLogger.log("PBTIME " + (peg2pegTimer.getRevertTime() - peg2pegTimer.getEngineCompletedTime()));
				methodLogger.log("ENGINETIME " + (peg2pegTimer.getEngineCompletedTime() - peg2pegTimer.getEngineSetupTime()));
			}
			
			if (isSingle) {
				SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> single = 
					(SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn>)optimizer;
				if (single.getOptimizationLevel().equals(SingleStageOptimizer.Level.RUN_ENGINE_FULL) &&
					!peg2pegData.getLastOriginal()) { 
					int newcost = calculateCost(peg2pegData.getLastRevertPeginfo(), stages.get(0));
					int oldcost = calculateCost(optimizerData.getLastOriginalPEG(), stages.get(0));
					
					int newpegcost = calculatePEGCost(peg2pegData.getLastRevertPeginfo(), stages.get(0));
					int oldpegcost = calculatePEGCost(optimizerData.getLastOriginalPEG(), stages.get(0));
					
					methodLogger.log("Optimization ratio " + newcost + "/" + oldcost + " = " + ((double)newcost)/oldcost);
					methodLogger.log("PEG-based Optimization ratio " + newpegcost + "/" + oldpegcost + " = " + ((double)newpegcost)/oldpegcost);

					if (options.getBoolean(DUMP_PROOF))
						dumpProofs(
								methodLogger,
								peg2pegData.getLastEngine(),
								optimizerData.getLastOriginalPEG(),
								peg2pegData.getLastRevertPeginfo(),
								peg2pegData.getLastRootVertexMap(),
								method.getSignature());
				}
			}
			
			classLogger.log("Done processing method " + fullName);
		}
		
		topLogger.log("Done optimizing " + inputClass);
		
		topLogger.log("Final results:");
			classLogger.log("Skipped methods = " + skippedMethodCount);
			classLogger.log("Buggy methods = " + buggyMethodCount);
			classLogger.log("Total methods = " + totalMethodCount);

		writeClassToDisk(topLogger, clazz, outputFolder);
		
		long endTotalTime = System.currentTimeMillis();
		topLogger.log("Total optimization time = " + (endTotalTime-startTotalTime) + " milliseconds");
	}

	
	private static void optimizeEngineOnly(
			Logger topLogger,
			SootClass clazz,
			String inputClass,
			Set<String> skippedMethodSet) {
		long startTotalTime = System.currentTimeMillis();
		
		Logger classLogger = topLogger.getSubLogger();
		
		int buggyMethodCount = 0;
		int skippedMethodCount = 0;
		int totalMethodCount = 0;
		
		StageInfo stage = stages.get(0);
		
		MyPEG2PEGOptimizer peg2peg = getPEG2PEGOptimizer(stage);
		peg2peg.addListener(getDotPEG2PEGListener(stage, "engineonly_"));
		
		PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> peg2pegData = 
			new PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
		PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> timer = 
			new PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
		peg2peg.addListener(peg2pegData);
		peg2peg.addListener(timer);
		
		for (Iterator<SootMethod> methoditer=clazz.getMethods().iterator(); methoditer.hasNext(); ) {
			SootMethod method = methoditer.next();
			String fullName = method.getSignature();
			totalMethodCount++;
			
			if (!(method.isConcrete())) {
				classLogger.log("Skipping method " + fullName);
				skippedMethodCount++;
				continue;
			}
				
			Body body = method.retrieveActiveBody();
				
			if (skippedMethodSet.contains(fullName)) {
				classLogger.log("Skipping method " + fullName);
				skippedMethodCount++;
				continue;
			}
			
			if (options.getLong(BC_THRESHOLD) > 0 && 
				totalBitcodeCount(body) > options.getLong(BC_THRESHOLD)) {
				classLogger.log("Method " + fullName + " exceeds bytecode threshold, skipping");
				skippedMethodCount++;
				continue;
			}
			
			if (SootUtils.hasExceptions(method)) {
				classLogger.log("Method " + fullName + " contains exceptions, skipping");
				skippedMethodCount++;
				continue;
			} 

			classLogger.log("Processing method " + fullName);

			Logger methodLogger = classLogger.getSubLogger();
			
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo = 
				bodyPegProvider.getPEG(method);
			
			if (OUTPUT_ORIGINAL_PEG) {
				String filename = "PEG_" + method.getSignature().replaceAll(" ","_") + ".dot";
				dumpToDot(methodLogger, filename, peginfo.getGraph());
			}
			
			try {
				peg2peg.setLogger(methodLogger);
				peg2peg.optimize(method, peginfo);
				methodLogger.log("Optimization of method " + fullName + " SUCCESSFUL");
			} catch (Throwable t) {
				buggyMethodCount++;
				methodLogger.logException("Error processing method " + fullName, t);
				methodLogger.log("Optimization of method " + fullName + " FAILED");
			}
			
			int oldcost = calculateCost(peginfo, stages.get(0));
			if (!peg2pegData.getLastOriginal()) {
				int newcost = calculateCost(peg2pegData.getLastRevertPeginfo(), stage);
				methodLogger.log("Optimization ratio " + newcost + "/" + oldcost + " = " + ((double)newcost)/oldcost);
				methodLogger.log("PBTIME " + (timer.getRevertTime() - timer.getEngineCompletedTime()));
				
				if (newcost < oldcost) {
					methodLogger.log("Method " + method.getSignature() + " OPTIMIZED");
					if (options.getBoolean(DUMP_PROOF)) {
						dumpProofs(
								methodLogger,
								peg2pegData.getLastEngine(),
								peginfo,
								peg2pegData.getLastRevertPeginfo(),
								peg2pegData.getLastRootVertexMap(),
								method.getSignature());
					}
				}
			} else {
				methodLogger.log("Optimization chose original PEG");
			}
			
			if (options.getBoolean(DELETE_PB_FILES))
				deleteTempFiles();
			
			classLogger.log("Done processing method " + fullName);
			System.gc();
		}
		topLogger.log("Done optimizing " + inputClass);
		
		topLogger.log("Final results:");
			classLogger.log("Skipped methods = " + skippedMethodCount);
			classLogger.log("Buggy methods = " + buggyMethodCount);
			classLogger.log("Total methods = " + totalMethodCount);

		long endTotalTime = System.currentTimeMillis();
		topLogger.log("Total optimization time = " + (endTotalTime-startTotalTime) + " milliseconds");
	}

	
	private static void deleteTempFiles() {
		if (tempFiles.size() > 0) {
			for (Iterator<File> iter = tempFiles.iterator(); iter.hasNext(); ) {
				try {
					File next = iter.next();
					next.delete();
				} catch (Throwable t) {}
				iter.remove();
			}
		}
	}
	

	private static void learnAndOptimize(
			Logger logger,
			List<String> learnMethods,
			List<String> optimizeMethods) {
		if (!learningPhase(logger, learnMethods, stages.get(0), stages.get(1))) {
			logger.log("No new axioms generated in learning phase, " +
				"skipping optimization phase");
			return;
		}

		optimizingPhase(logger, optimizeMethods, stages.get(1));
	}

	private static GeneralizerListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>
	getOutputGeneralizerListener(final Logger logger) {
		return new GeneralizerListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>() {
			public void beginPEG(
					SootMethod method,
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> inputPEG, 
					Tag<? extends CPEGTerm<JavaLabel,JavaParameter>> termTag) {}
			public void notifyPEG2PEGBuilt(
					PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> peg2peg) {}
			public void notifyOptimalPEGBuilt(
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> optimalPEG,
					boolean lastOriginal) {}
			public void notifyExpressionsTightened(
					Tag<? extends CPEGTerm<JavaLabel,JavaParameter>> newTag) {
				logger.log("Tightened Expressions");
			}
			public void notifyReturnEPEGsBuilt(
					JavaReturn arr,
					Collection<? extends PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> epegs) {
				logger.log("Generated " + epegs.size() + " EPEGs for " + arr.toString());
			}
			public void endPEG() {
				logger.log("Done generalizing");
			}
		};
	};
	
	
	private static final Tag<String> nameTag = new NamedTag<String>("EPEG NAME");
	
	private static boolean learningPhase(
			Logger topLogger,
			List<String> learnMethods,
			StageInfo learnStage,
			StageInfo optimizeStage) {
		long startTotalTime = System.currentTimeMillis();
		
		Logger classLogger = topLogger.getSubLogger();
		
		int buggyLearnMethodCount = 0;
		int skippedLearnMethodCount = 0;
		int totalLearnMethodCount = 0;
		
		List<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> epegs = 
			new ArrayList<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> ();

		topLogger.log("Beginning learning phase");
		
		for (String learnMethodSig : learnMethods) {
			String classname;
			String subsig;
			try {
				classname = Scene.v().signatureToClass(learnMethodSig);
				subsig = Scene.v().signatureToSubsignature(learnMethodSig);
			} catch (Throwable t) {
				classLogger.log("Cannot parse signatures from string " + learnMethodSig);
				continue;
			}

			classLogger.log("Processing method " + learnMethodSig);
			
			Logger methodLogger = classLogger.getSubLogger();
			
			totalLearnMethodCount++;
			
			SootClass clazz = null;
			try {clazz = Scene.v().loadClassAndSupport(classname);}
			catch (Throwable t) {
				classLogger.log("Cannot load class " + classname);
				classLogger.log("Skipping method " + learnMethodSig);
				buggyLearnMethodCount++;
				continue;
			} 
			
			SootMethod method = null;
			try {method = clazz.getMethod(subsig);}
			catch (Throwable t) {
				classLogger.log("Cannot load method " + subsig + ", skipping");
				buggyLearnMethodCount++;
				continue;
			} 
			String mungedName = learnMethodSig.replaceAll(" ","_");

			if (!method.isConcrete()) {
				classLogger.log("Skipping non-concrete method " + learnMethodSig);
				skippedLearnMethodCount++;
				continue;
			} 

			Body body = method.retrieveActiveBody();

			if (skippedFunctions.contains(learnMethodSig)) {
				classLogger.log("Skipping method " + learnMethodSig);
				skippedLearnMethodCount++;
				continue;
			}

			if (options.getLong(BC_THRESHOLD) > 0 && 
					totalBitcodeCount(body) > options.getLong(BC_THRESHOLD)) {
				classLogger.log("Method " + learnMethodSig + " exceeds bytecode threshold, skipping");
				skippedLearnMethodCount++;
				continue;
			}
			
			if (!bodyPegProvider.canProvidePEG(method)) {
				classLogger.log("Cannot get PEG for method " + subsig + ", skipping");
				skippedLearnMethodCount++;
				continue;
			}
			
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo = 
				bodyPegProvider.getPEG(method);
			
			if (OUTPUT_ORIGINAL_PEG) {
				dumpToDot(methodLogger, learnStage, "learn_PEG_" + mungedName + ".dot", peginfo.getGraph());
			}
			
			final MyPEG2PEGOptimizer peg2peg = getPEG2PEGOptimizer(learnStage);
			Generalizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> generalizer = 
				new Generalizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> () {
				protected PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> getPEG2PEGOptimizer() {
					return peg2peg;
				}
				
				// may return null if no generalizer can be created
				protected ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>  
				getGeneralizer(
						CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
						CPEGTerm<JavaLabel,JavaParameter> left,
						CPEGTerm<JavaLabel,JavaParameter> right) {
					return Main.getGeneralizer(this.getLogger(), engine, left, right);
				}
			};
				
			generalizer.addListener(getOutputGeneralizerListener(methodLogger));
				
			GeneralizerTimerListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> generalizerTimer = 
				new GeneralizerTimerListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> ();
			generalizer.addListener(generalizerTimer);
			
			// add data listener
			PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> listener = 
				new PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
			peg2peg.addListener(listener);
			
			// add timer listener
			PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> timer = 
				new PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> ();
			peg2peg.addListener(timer);
			
			peg2peg.addListener(getDotPEG2PEGListener(learnStage, "learn_"));
			
			List<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> myepegs = 
				new ArrayList<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> ();
			
			classLogger.log("Beginning optimization");
			try {
				generalizer.setLogger(methodLogger);
				generalizer.generateEPEGs(
						method,
						TERM_TAG,
						peginfo,
						myepegs);
			} catch (Throwable t) {
				abort("Error optimizing PEG", t, methodLogger);
				buggyLearnMethodCount++;
				continue;
			} finally {
				if (options.getBoolean(DELETE_PB_FILES))
					deleteTempFiles();
			}
			
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> optimized = 
				listener.getLastRevertPeginfo();
			
			methodLogger.log("Done optimizing");
			methodLogger.log("Timing results:");
			Logger sublogger = methodLogger.getSubLogger();
				sublogger.log("Engine setup took " + (timer.getEngineSetupTime() - timer.getBeginTime()) + " milliseconds");
				sublogger.log("Engine running took " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()) + " milliseconds");
				sublogger.log("Choosing optimal PEG took " + (timer.getRevertTime() - timer.getEngineCompletedTime()) + " milliseconds");
			
			int inputCost = calculateCost(peginfo, learnStage);
			int outputCost = 
				listener.getLastOriginal() ? 
				inputCost : 
				calculateCost(optimized, learnStage);
			
			// OUTPUT SECTION ////
			methodLogger.log("LEARN " + mungedName + " ENGINEITERS " + learnStage.engineRunner.lastIterStop);
			methodLogger.log("LEARN " + mungedName + " ENGINETIME " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()));
			methodLogger.log("LEARN " + mungedName + " PBTIME " + (timer.getRevertTime() - timer.getEngineCompletedTime()));
			methodLogger.log("LEARN " + mungedName + " PEG2PEGTIME " + (timer.getEndTime() - timer.getBeginTime()));
			methodLogger.log("LEARN " + mungedName + " INPUTPEGCOST " + inputCost);
			methodLogger.log("LEARN " + mungedName + " OPTPEGCOST " + outputCost);

			methodLogger.log("Optimization ratio " + outputCost + "/" + inputCost + " = " + ((double)outputCost)/inputCost);
			
			// only generalize if there were improvements
			if (!listener.getLastOriginal() && outputCost < inputCost) {
				// MORE OUTPUT SECTION
				methodLogger.log("LEARN " + mungedName + " GENTIME " + (generalizerTimer.getEndPEGTime() - generalizerTimer.getOptimalPEGBuiltTime()));
				
				int max = Integer.MAX_VALUE;
				try {
					max = ExpressionTightener.getTimeOfExpression(
						listener.getLastEngine().getEGraph(),
						optimized.getGraph(),
						TERM_TAG);
				} catch (Throwable t) {
					methodLogger.logException("ExpressionTightener.getTimeOfExpression failed", t);
				}
				max = Math.max(max,
						listener.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
								listener.getLastRootVertexMap().get(peginfo.getReturnVertex(JavaReturn.SIGMA)),
								optimized.getReturnVertex(JavaReturn.SIGMA).getTag(TERM_TAG)));
				max = Math.max(max, 
						listener.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
								listener.getLastRootVertexMap().get(peginfo.getReturnVertex(JavaReturn.VALUE)),
								optimized.getReturnVertex(JavaReturn.VALUE).getTag(TERM_TAG)));
				// MORE OUTPUT SECTION
				methodLogger.log("LEARN " + mungedName + " TIMEOFEQUALITY " + max);
				
				// find my local unique epegs, then add to global list
				for (int i = 0; i < myepegs.size(); i++) {
					//myepegs.get(i).getTrigger().setTag(nameTag, mungedName + "_" + i);
					
					if (!myepegs.get(i).canAxiomizeSafely()) {
//						
//						System.err.println("**NOT SAFE TO AXIOMIZE   " + 
//								myepegs.get(i).getTrigger().getTag(nameTag));
						
						myepegs.remove(i);
						i--;
						continue;
					}
						
					for (int j = 0; j < i; j++) {
						if (myepegs.get(j).subsumes(myepegs.get(i))) {
							
//							System.err.println("** LOCAL SUBSUMPTION " + 
//									myepegs.get(j).getTrigger().getTag(nameTag) + 
//									" AND " + 
//									myepegs.get(i).getTrigger().getTag(nameTag));
//							
							myepegs.remove(i);
							i--;
							break;
						}
					}
				}
				
				for (int i = 0; i < myepegs.size(); i++) {
					myepegs.get(i).getTrigger().setTag(nameTag, mungedName + "_" + i);
					dumpToDot(methodLogger, learnStage, "learned_" + i + "_" + mungedName + ".dot", myepegs.get(i));
				}
				
				// MORE OUTPUT SECTION
				methodLogger.log("LEARN " + mungedName + " NEWAXIOMS " + myepegs.size());
				epegs.addAll(myepegs);
			} else {
				methodLogger.log("Output PEG is not optimized, skipping generalization");
			}
			
			classLogger.log("Done with method " + learnMethodSig);
		}
		
		topLogger.log("Learning results:");
			classLogger.log("Skipped methods = " + skippedLearnMethodCount);
			classLogger.log("Buggy methods = " + buggyLearnMethodCount);
			classLogger.log("Total methods = " + totalLearnMethodCount);

		// filter out subsumed EPEGs
		for (int i = 1; i < epegs.size(); i++) {
			for (int j = 0; j < i; j++) {
				if (epegs.get(j).subsumes(epegs.get(i))) {
					// subsumed!
					
//					System.err.println("** SUBSUMPTION  " + 
//							epegs.get(j).getTrigger().getTag(nameTag) + 
//							" AND " + 
//							epegs.get(i).getTrigger().getTag(nameTag));
					
					epegs.remove(i);
					i--;
					break;
				}
			}
		}
		
		// MORE OUTPUT SECTION 
		topLogger.log("LEARN OVERALL NEWAXIOMS "+  epegs.size());

		// make the new axioms
		for (int i = 0; i < epegs.size(); i++) {
			if (options.getBoolean(DUMP_PROOF)) {
				String filename = "learned" + i + ".dot";  
				topLogger.log("Writing generalized axiom file: " + filename);
				dumpToDot(topLogger, learnStage, filename, epegs.get(i));
			}
			
			String name = "Generalized axiom " + i;
			AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>> newaxiom = 
				PostMultiGenEPEGAxiomizer.axiomize(
					optimizeStage.network,
					epegs.get(i),
					name,
					epegs.get(i).getTrigger(),
					epegs.get(i).getResult(),
					epegs.get(i).getResult().isTrigger());
			newaxiom.setTag(XMLRuleParser.NAME_TAG, name);
			optimizeStage.axioms.add(newaxiom);
		}
		
		long endTime = System.currentTimeMillis();
		
		topLogger.log("Learned " + epegs.size() + " new axioms");
		topLogger.log("Learning phase took " + (endTime-startTotalTime) + " milliseconds");

		return (epegs.size() > 0);
	}
	

	private static OptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>
	getUnsoundReversionListener(final Logger logger) {
		return new OptimizerAdapter<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>() {
			public void notifyCFGReverterBuilt(
					CFGReverter<JavaParameter,JavaLabel,JavaReturn> reverter) {
				if (!reverter.isSound()) {
					if (!options.getBoolean(UNSOUND_REVERSION)) {
						throw new RuntimeException("Unsound revert CFG");
					} else {
						logger.log("Warning: revert CFG may be unsound!");
					}
				}
			}
		};
	}
	
	private static boolean dumpToDot(Logger logger, StageInfo stage, String filename, Object todump) {
		File file = new File(stage.dotOutputFolder, filename);
		return dumpToDot(logger, file.getPath(), todump);
	}
	private static boolean dumpToDot(Logger logger, String filename, Object todump) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(filename));
			out.println(todump);
			out.close();
			return true;
		} catch (Throwable t) {
			logger.logException("Error dumping dot file", t);
		}
		return false;
	}
	

	private static void optimizingPhase(
			Logger topLogger,
			List<String> optimizeMethods,
			StageInfo optimizeStage) {
		int buggyOptimizeMethodCount = 0;
		int skippedOptimizeMethodCount = 0;
		int totalOptimizeMethodCount = 0;
		
		Set<SootClass> alteredClasses = new HashSet<SootClass>();

		topLogger.log("Beginning optimization phase");

		Logger classLogger = topLogger.getSubLogger();
		
		for (String optimizeMethodSig : optimizeMethods) {
			String classname;
			String subsig;
			try {
				classname = Scene.v().signatureToClass(optimizeMethodSig);
				subsig = Scene.v().signatureToSubsignature(optimizeMethodSig);
			} catch (Throwable t) {
				classLogger.log("Cannot parse signatures from string " + optimizeMethodSig);
				continue;
			}
			
			classLogger.log("Processing method " + optimizeMethodSig);
			
			totalOptimizeMethodCount++;
			
			SootClass clazz = null;
			try {clazz = Scene.v().loadClassAndSupport(classname);}
			catch (Throwable t) {
				classLogger.log("Cannot load class " + classname);
				classLogger.log("Skipping method " + optimizeMethodSig);
				buggyOptimizeMethodCount++;
				continue;
			}
			SootMethod method = null;
			try {method = clazz.getMethod(subsig);}
			catch (Throwable t) {
				classLogger.log("Cannot load method " + subsig + ", skipping");
				buggyOptimizeMethodCount++;
				continue;
			}
			String mungedName = optimizeMethodSig.replaceAll(" ","_");

			if (!method.isConcrete()) {
				classLogger.log("Skipping non-concrete method " + optimizeMethodSig);
				skippedOptimizeMethodCount++;
				continue;
			}

			Body body = method.retrieveActiveBody();

			if (skippedFunctions.contains(optimizeMethodSig)) {
				classLogger.log("Skipping method " + optimizeMethodSig);
				skippedOptimizeMethodCount++;
				continue;
			}

			if (options.getLong(BC_THRESHOLD) > 0 && 
					totalBitcodeCount(body) > options.getLong(BC_THRESHOLD)) {
				classLogger.log("Method " + optimizeMethodSig + " exceeds bytecode threshold, skipping");
				skippedOptimizeMethodCount++;
				continue;
			}
			
			if (!bodyPegProvider.canProvidePEG(method)) {
				classLogger.log("Cannot get PEG for method " + subsig + ", skipping");
				skippedOptimizeMethodCount++;
				continue;
			}

			SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> single = null; 
			MyPEG2PEGOptimizer peg2peg;
			OptimizerLastDataListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod> dataListener = 
				new OptimizerLastDataListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod> ();
			
			Logger methodLogger = classLogger.getSubLogger();
			
			if (options.getBoolean(ENGINE_ONLY)) {
				peg2peg = getPEG2PEGOptimizer(optimizeStage);
			} else {
				single = getSingleStageOptimizer(
						optimizeStage, 
						SingleStageOptimizer.Level.RUN_ENGINE_FULL);
				single.addListener(dataListener);
				single.addListener(getUnsoundReversionListener(methodLogger));
				peg2peg = (MyPEG2PEGOptimizer)single.getPEG2PEGOptimizer();
			}

			// add data listener
			PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod> listener = 
				new PEG2PEGLastDataListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>();
			peg2peg.addListener(listener);
			
			// add timer
			PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> timer = 
				new PEG2PEGTimer<JavaLabel,JavaParameter,JavaReturn,SootMethod> ();
			peg2peg.addListener(timer);
			
			// add dot listener
			peg2peg.addListener(getDotPEG2PEGListener(optimizeStage, "opt_"));
			
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo = null;
			if (options.getBoolean(ENGINE_ONLY)) {
				peginfo = bodyPegProvider.getPEG(method);
			}

			PEGInfo<JavaLabel,JavaParameter,JavaReturn> optimized;
			
			classLogger.log("Begin optimization");
			try {
				if (options.getBoolean(ENGINE_ONLY)) {
					peg2peg.setLogger(methodLogger);
					optimized = peg2peg.optimize(method, peginfo);
				} else {
					single.setLogger(methodLogger);
					single.optimize(method);
					peginfo = dataListener.getLastOriginalPEG();
					optimized = listener.getLastRevertPeginfo();
				}
				alteredClasses.add(clazz);
			} catch (Throwable t) {
				methodLogger.logException("Error optimizing PEG", t);
				buggyOptimizeMethodCount++;
				continue;
			} finally {
				if (options.getBoolean(DELETE_PB_FILES))
					deleteTempFiles();
			}
			
			if (OUTPUT_ORIGINAL_PEG) {
				dumpToDot(methodLogger, optimizeStage, "opt_PEG_" + mungedName + ".dot", peginfo.getGraph());
			}
			
			int inputCost = calculateCost(peginfo, optimizeStage);
			int outputCost = 
				listener.getLastOriginal() ? 
						inputCost : 
							calculateCost(optimized, optimizeStage);

			// OUTPUT SECTION ////
			methodLogger.log("OPT " + mungedName + " ENGINEITERS " + optimizeStage.engineRunner.lastIterStop);
			methodLogger.log("OPT " + mungedName + " ENGINETIME " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()));
			methodLogger.log("OPT " + mungedName + " PBTIME " + (timer.getRevertTime() - timer.getEngineCompletedTime()));
			methodLogger.log("OPT " + mungedName + " PEG2PEGTIME " + (timer.getEndTime() - timer.getBeginTime()));
			methodLogger.log("OPT " + mungedName + " INPUTPEGCOST " + inputCost);
			methodLogger.log("OPT " + mungedName + " OPTPEGCOST " + outputCost);


			if (!listener.getLastOriginal()) {
				// get time of equality
			
				int max = Integer.MAX_VALUE;
				try {
					max = ExpressionTightener.getTimeOfExpression(
							listener.getLastEngine().getEGraph(),
							optimized.getGraph(),
							TERM_TAG);
				} catch (Throwable t) {
					methodLogger.logException("ExpressionTightener.getTimeOfExpression had an error", t);
				}
				
				max = Math.max(max,
						listener.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
								listener.getLastRootVertexMap().get(peginfo.getReturnVertex(JavaReturn.SIGMA)),
								optimized.getReturnVertex(JavaReturn.SIGMA).getTag(TERM_TAG)));
				max = Math.max(max,
						listener.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
								listener.getLastRootVertexMap().get(peginfo.getReturnVertex(JavaReturn.VALUE)),
								optimized.getReturnVertex(JavaReturn.VALUE).getTag(TERM_TAG)));
				
				// MORE OUTPUT SECTION
				methodLogger.log("OPT " + mungedName + " TIMEOFEQUALITY " + max);
			} else {
				methodLogger.log("OPT " + mungedName + " TIMEOFEQUALITY 0");
			}
			
			
			
			classLogger.log("Done optimizing " + optimizeMethodSig);
			classLogger.log("Timing results:");
				methodLogger.log("Engine setup took " + (timer.getEngineSetupTime() - timer.getBeginTime()) + " milliseconds");
				methodLogger.log("Engine running took " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()) + " milliseconds");
				methodLogger.log("Choosing optimal PEG took " + (timer.getRevertTime() - timer.getEngineCompletedTime()) + " milliseconds");
			
			classLogger.log("Optimization ratio " + outputCost + "/" + inputCost + 
					" = " + ((double)outputCost)/inputCost);
		}
		
		if (!options.getBoolean(ENGINE_ONLY)) {
			// write classes to disk
			for (SootClass todump : alteredClasses) {
				writeClassToDisk(topLogger, todump, options.getFile(OUTPUT_FOLDER));
			}
		}
		
		topLogger.log("Optimizing results:");
			classLogger.log("Skipped methods = " + skippedOptimizeMethodCount);
			classLogger.log("Buggy methods = " + buggyOptimizeMethodCount);
			classLogger.log("Total methods = " + totalOptimizeMethodCount);
		
		topLogger.log("Optimization complete");
	}
	
	private static boolean writeClassToDisk(Logger logger, SootClass clazz, File folder) {
		// load each method so that it has a body to write back
		try {
			for (SootMethod method : (List<SootMethod>)clazz.getMethods()) {
				if (method.isConcrete()) {
					logger.log("Fixing bytecode of method " + method.getSignature());
					JimpleBody jimplebody = (JimpleBody)method.retrieveActiveBody();
					
					////////////////////////////////////
					if (options.getBoolean(USE_SOOT_OPTS)) {
						logger.log("Running Soot pack jb");
						PackManager.v().getPack("jb").apply(jimplebody);
						logger.log("Running Soot pack jop");
						PackManager.v().getPack("jop").apply(jimplebody);
					}
					////////////////////////////////////
					
					BafBody bafbody = Baf.v().newBody(jimplebody);
					method.setActiveBody(bafbody);
					PhaseOptions.v().processPhaseOptions("bb.lso", "sl2:true");
					PhaseOptions.v().processPhaseOptions("bb.lso", "sll2:true");
					PackManager.v().getPack("bb").apply(bafbody);
				}
			}
		} catch (Throwable t) {
			logger.logException("Cannot fetch bodies for all concrete methods", t);
			return false;
		}
		
		try {
			String pathToClassFile = clazz.getJavaPackageName().replace('.','/');
			File myOutputFolder = new File(folder, pathToClassFile);
			File outputFile = new File(myOutputFolder, clazz.getShortName() + ".class");
			logger.log("Writing class back to " + outputFile.getPath());
			outputFile.getParentFile().mkdirs();
			soot.options.Options.v().set_output_dir(folder.getAbsolutePath());
			soot.baf.JasminClass jasmin = new soot.baf.JasminClass(clazz);
			FileOutputStream fout = new FileOutputStream(outputFile);
			PrintWriter writer = new PrintWriter(new JasminOutputStream(fout));
			jasmin.print(writer);
			writer.flush();
			fout.close();
			return true;
		} catch (Throwable t) {
			logger.logException("Error writing class back to disk", t);
		}
		return false;
	}
	
	
	private static ReversionHeuristic<JavaLabel,JavaParameter,JavaReturn,Integer> 
	getLooplessHeuristic(
			final CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> costModel,
			final ReversionHeuristic<JavaLabel,JavaParameter,JavaReturn,Integer> fallback) {
		return new LooplessReversionHeuristic<JavaLabel, JavaParameter, JavaReturn>() {
			protected ReversionHeuristic<JavaLabel, JavaParameter, JavaReturn, Integer> getFallbackHeuristic() {
				return fallback;
			}
			public CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> getCostModel() {
				return costModel;
			}
			protected boolean isRevertible(FlowValue<JavaParameter,JavaLabel> flow) {
				if (flow.isDomain()) {
					return flow.getDomain().isRevertible();
				} else 
					return flow.isRevertable();
			}
		};
	}
	
	
	private static ReversionHeuristic<JavaLabel,JavaParameter,JavaReturn,Integer> 
	getAverageHeuristic(
			final CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> costModel) {
		return new AverageReversionHeuristic<JavaLabel, JavaParameter, JavaReturn>() {
			public CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> getCostModel() {
				return costModel;
			}
			protected StickyPredicate<FlowValue<JavaParameter,JavaLabel>> getStickyPredicate() {
				return new CombinedStickyPredicate<FlowValue<JavaParameter,JavaLabel>>(
						Arrays.asList(
								new FlowValueStickyPredicate<JavaLabel,JavaParameter>(
										JavaLabelStickyPredicate.INSTANCE),
								new NondomainStickyPredicate<JavaLabel,JavaParameter>()));
			}
			protected boolean isRevertible(FlowValue<JavaParameter,JavaLabel> flow) {
				if (flow.isDomain()) {
					return flow.getDomain().isRevertible();
				} else 
					return flow.isRevertable();
			}
		};
	}
	
	
	private static PEGExtractor<JavaLabel,JavaParameter,JavaReturn> 
	getPEGExtractor(final int maxCost, final StageInfo stage, final Logger logger) {
		final CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> costModel =
			stage.ruleParser.getCostModel();
		final int maxPBFileSize = stage.maxPBFileSize;
		
		ReversionHeuristic<JavaLabel,JavaParameter,JavaReturn,Integer> heuristic;
		switch (stage.pbOption) {
		case GREEDY: {
			heuristic = new DefaultGreedyReversionHeuristic<JavaLabel,JavaParameter,JavaReturn>(
					JavaLabelStickyPredicate.INSTANCE) {
				protected boolean isRevertible(JavaLabel label) {
					return label.isRevertible();
				}
				public CostModel<CPEGTerm<JavaLabel,JavaParameter>,Integer> 
				getCostModel() {return costModel;}
			};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			
			FuturePEGExtractor<JavaLabel,JavaParameter,JavaReturn> future = 
				new FuturePEGExtractor<JavaLabel,JavaParameter,JavaReturn>(
					TERM_TAG, heuristic);
			future.setTimeout(stage.pbTimeout);
			future.setMaxCost(maxCost);
			return future;
		}

		case AVERAGE: {
			heuristic = getAverageHeuristic(costModel);
			heuristic.setLogger(logger);
			FuturePEGExtractor<JavaLabel,JavaParameter,JavaReturn> future = 
				new FuturePEGExtractor<JavaLabel,JavaParameter,JavaReturn>(
					TERM_TAG, heuristic);
			future.setTimeout(stage.pbTimeout);
			future.setMaxCost(maxCost);
			return future;
		}
		
		case PUEBLO: {
			heuristic = 
				new DefaultPBReversionHeuristic<PuebloFormulation<CPEGTerm<JavaLabel,JavaParameter>>> () {
					public CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> getCostModel() {
						return costModel;
					}
					protected int getMaxCost() {return maxCost;}
					protected PuebloFormulation<CPEGTerm<JavaLabel,JavaParameter>> getFreshFormulation() {
						try {
							File back = null;
							if (options.getFile(TMP_FOLDER) == null) {
								back = File.createTempFile("test", ".pb");
							} else {
								options.getFile(TMP_FOLDER).mkdirs();
								back = File.createTempFile("test", ".pb", options.getFile(TMP_FOLDER));
							}
							if (options.getBoolean(DELETE_PB_FILES))
								tempFiles.add(back);
							return new PuebloFormulation<CPEGTerm<JavaLabel,JavaParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected int getFormulationTimeout() {return stage.pbTimeout;}
					protected PBRunner<CPEGTerm<JavaLabel,JavaParameter>,PuebloFormulation<CPEGTerm<JavaLabel,JavaParameter>>> getRunner() {
						PuebloRunner<CPEGTerm<JavaLabel,JavaParameter>> result = 
							new PuebloRunner<CPEGTerm<JavaLabel,JavaParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(PUEBLO_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									//if (deletePBFiles)
										//tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return stage.pbTimeout;}
							};
						return result;
					}
				};
				heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
				
			return new DefaultPEGExtractor<JavaLabel,JavaParameter,JavaReturn>(
					TERM_TAG, heuristic);
		}
			
		case GLPK: {
			heuristic =  
				new DefaultGLPKReversionHeuristic () {
					public CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected int getFormulationTimeout() {return stage.pbTimeout;}
					protected GLPKRunner<JavaLabel,JavaParameter> getRunner() {
						GLPKRunner<JavaLabel,JavaParameter> result = 
							new GLPKRunner<JavaLabel,JavaParameter>() {
								protected String getCommandPath() {return "/usr/bin/glpsol";}
								protected int getTimeout() {return stage.pbTimeout;}
							};
						return result;
					}
					protected int getMaxILPFileSize() {return maxPBFileSize;}
					protected boolean isValidPEG(
							Map<CPEGValue<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>> value2term,
							Set<CPEGValue<JavaLabel,JavaParameter>> roots) {
						return PEGValidityChecker.isValid(value2term, roots);
					}
					protected File getFreshBackingFile() {
						try {
							File back;
							if (options.getFile(TMP_FOLDER) == null) {
								back = File.createTempFile("test", ".glpk");
							} else {
								options.getFile(TMP_FOLDER).mkdirs();
								back = File.createTempFile("test", ".glpk", options.getFile(TMP_FOLDER));
							}
							if (options.getBoolean(DELETE_PB_FILES))
								tempFiles.add(back);
							return back;
						} catch (Throwable t) {
							throw new RuntimeException(t);
						}
					}
					protected int getMaxCost() {return maxCost;}
				};
			heuristic.setLogger(logger);
			// doesn't need loopless wrapper, already optimized for that!
			return new DefaultPEGExtractor<JavaLabel,JavaParameter,JavaReturn>(
					TERM_TAG, heuristic);
		}
		
		case MINISAT: {
			heuristic = 
				new DefaultPBReversionHeuristic<MinisatFormulation<CPEGTerm<JavaLabel,JavaParameter>>> () {
					public CostModel<CPEGTerm<JavaLabel, JavaParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected MinisatFormulation<CPEGTerm<JavaLabel,JavaParameter>> getFreshFormulation() {
						try {
							File back = null;
							if (options.getFile(TMP_FOLDER) == null) {
								back = File.createTempFile("test", ".pb");
							} else {
								options.getFile(TMP_FOLDER).mkdirs();
								back = File.createTempFile("test", ".pb", options.getFile(TMP_FOLDER));
							}
							if (options.getBoolean(DELETE_PB_FILES))
								tempFiles.add(back);
							return new MinisatFormulation<CPEGTerm<JavaLabel,JavaParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected int getMaxCost() {return maxCost;}
					protected int getFormulationTimeout() {return stage.pbTimeout;}
					protected PBRunner<CPEGTerm<JavaLabel,JavaParameter>,MinisatFormulation<CPEGTerm<JavaLabel,JavaParameter>>> getRunner() {
						MinisatRunner<CPEGTerm<JavaLabel,JavaParameter>> result = 
							new MinisatRunner<CPEGTerm<JavaLabel,JavaParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(MINISAT_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									//if (deletePBFiles)
										//tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return stage.pbTimeout;}
							};
						return result;
					}
				};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			return new DefaultPEGExtractor<JavaLabel,JavaParameter,JavaReturn>(
					TERM_TAG, heuristic);
		}

		default:
			throw new RuntimeException("Unknown PB solver: " + stage.pbOption);
		} 
	}

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
			StageInfo stage, 
			PeggyAxiomSetup<JavaLabel,JavaParameter> setup,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			SootMethod method) {
		if (stage.activatedAnalyses.contains("livsr")) {
			JavaLIVSRAnalysis analysis = 
				new JavaLIVSRAnalysis(stage.network, engine) {
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

		if (stage.activatedAnalyses.contains("inline")) {
			PeggyHeuristicInliner inliner = new PeggyHeuristicInliner(
					new DefaultReferenceResolver(), 
					bodyPegProvider, 
					stage.network, 
					engine,
					stage.ruleParser.getInlineMethods(),
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
		} else if (stage.activatedAnalyses.contains("inlineall")) {
			PeggyHeuristicInliner inliner = new PeggyHeuristicInliner(
					new DefaultReferenceResolver(), 
					bodyPegProvider, 
					stage.network, 
					engine,
					stage.ruleParser.getInlineMethods(),
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
		
		if (stage.activatedAnalyses.contains("binop")) {
			JavaBinopConstantAnalysis analysis = 
				new JavaBinopConstantAnalysis(
						stage.network,
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

		if (stage.activatedAnalyses.contains("constant")) {
			JavaConstantAnalysis analysis = 
				new JavaConstantAnalysis(
						stage.network,
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
						stage.network,
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
					stage.ruleParser.getSigmaInvariantMethods());
		}
		
		for (AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>> node : stage.axioms) {
			Event<? extends Proof> event = setup.getEngine().addPEGAxiom(node);
			if (node.hasTag(XMLRuleParser.NAME_TAG))
				event.addListener(new PrintListener(logger, node.getTag(XMLRuleParser.NAME_TAG)));
			debug("adding parsed axiom to engine");
		}

		AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector =  
			stage.ruleParser.getAxiomSelector();
		
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

	
	private static void setupEngine(
			Logger logger, 
			SootMethod method,
			StageInfo stage,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo,
			Map<Vertex<FlowValue<JavaParameter,JavaLabel>>, CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap) {
		PeggyAxiomSetup<JavaLabel,JavaParameter> setup = 
			new PeggyAxiomSetup<JavaLabel,JavaParameter>(stage.network, stage.ambassador, engine);
		addAxioms(logger.getSubLogger(), stage, setup, engine, method);

		// the list we're going to get representations for
		List<Vertex<FlowValue<JavaParameter,JavaLabel>>> vertices = 
			new ArrayList<Vertex<FlowValue<JavaParameter,JavaLabel>>>();
		Set<Vertex<FlowValue<JavaParameter,JavaLabel>>> returns = 
			new HashSet<Vertex<FlowValue<JavaParameter,JavaLabel>>>();
		int countReturns = 0;
		for (JavaReturn arr : peginfo.getReturns()) {
			countReturns++;
			returns.add(peginfo.getReturnVertex(arr));
			vertices.add(peginfo.getReturnVertex(arr));
		}
		for (Vertex<FlowValue<JavaParameter,JavaLabel>> vertex : peginfo.getGraph().getVertices()) {
			if (!returns.contains(vertex))
				vertices.add(vertex);
		}
		
		List<? extends CPEGTerm<JavaLabel,JavaParameter>> reps = 
			engine.addExpressions(vertices);
		for (int i = 0; i < vertices.size(); i++)
			vertices.get(i).setTag(TERM_TAG, reps.get(i));

		// map for the reverter (maps significant vertexes to their reps)
		for (int i = 0; i < countReturns; i++)
			rootVertexMap.put(vertices.get(i), reps.get(i));
	}

	private static JavaPEGCFG getOutputCFG(
			SootMethod body, 
			RevertCFG revert) {
		JavaPEGCFG outcfg = new JavaPEGCFG(revert);
		return outcfg;
	}
	
	private static void encodeCFG(JavaPEGCFG cfg, SootMethod body) {
		JavaPEGCFGEncoder encoder = new JavaPEGCFGEncoder(
				cfg, 
				body,
				new DefaultReferenceResolver());
		encoder.encode();
	}
	

		
	private static void dumpProofs(
			Logger logger,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> lastEngine,
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> originalPEG,
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> revertPEG,
			Map<Vertex<FlowValue<JavaParameter,JavaLabel>>,CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap,
			String signature) {
		logger.log("Tightening expressions");
		Tag<? extends CPEGTerm<JavaLabel,JavaParameter>> newTag = null;
		try {
			newTag = ExpressionTightener.tighten(
					lastEngine.getEGraph(),
					revertPEG.getGraph(),
					TERM_TAG);
		} catch (Throwable t) {
			abort("Exception tightening expressions", t, logger);
		}

		Map<JavaReturn,Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>> return2pair = 
			new HashMap<JavaReturn,Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>>();
		for (JavaReturn arr : originalPEG.getReturns()) {
			Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>> pair = 
				new Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>(
						rootVertexMap.get(originalPEG.getReturnVertex(arr)),
						revertPEG.getReturnVertex(arr).getTag(newTag));
			return2pair.put(arr, pair);
		}
		
		String mungedName = signature.replaceAll(" ", "_");
		printGeneralizedProof(logger, lastEngine, return2pair, mungedName); 
	}

	
	
	private static int calculateCost(
			Collection<? extends CPEGTerm<JavaLabel,JavaParameter>> terms,
			StageInfo stage) {
		int cost = 0;
		ConfigurableCostModel<FlowValue<JavaParameter,JavaLabel>,MethodJavaLabel,CPEGTerm<JavaLabel,JavaParameter>,Integer>
		costModel = stage.ruleParser.getCostModel();
		for (CPEGTerm<JavaLabel,JavaParameter> term : terms) {
			cost += costModel.cost(term);
		}
		return cost;
	}
	private static int calculateCost(
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> taggedPEG,
			StageInfo stage) {
		Set<CPEGTerm<JavaLabel,JavaParameter>> terms = 
			new HashSet<CPEGTerm<JavaLabel,JavaParameter>>();
		for (Vertex<FlowValue<JavaParameter,JavaLabel>> vertex : taggedPEG.getGraph().getVertices()) {
			terms.add(vertex.getTag(TERM_TAG));
		}
		
		return calculateCost(terms, stage);
	}
	private static int calculatePEGCost(
			PEGInfo<JavaLabel,JavaParameter,JavaReturn> peg,
			StageInfo stage) {
		PEGCostCalculator<JavaLabel,JavaParameter,JavaReturn> costCalculator = 
			((JavaCostModel)stages.get(0).ruleParser.getCostModel()).getPEGCostCalculator();
		return costCalculator.cost(peg);
	}
	
	static class MyPEG2PEGOptimizer 
	extends PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> {
		StageInfo stage;
		
		MyPEG2PEGOptimizer(StageInfo _stage) {
			this.stage = _stage;
		}

		protected EngineRunner<JavaLabel,JavaParameter> getEngineRunner() {return stage.engineRunner;}
		protected OpAmbassador<JavaLabel> getOpAmbassador() {return stage.ambassador;}
		protected PEGExtractor<JavaLabel,JavaParameter,JavaReturn> getExtractor() {
			return Main.getPEGExtractor(-1, stage, this.getLogger());
		}
		protected void setupEngine(
				SootMethod method,
				CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
				PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo,
				Map<Vertex<FlowValue<JavaParameter,JavaLabel>>, CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap) {
			Main.setupEngine(this.getLogger(), method, stage, engine, peginfo, rootVertexMap);
		}
	}
		
		
	private static PEG2PEGListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>
	getOutputPEG2PEGListener(final Logger logger) {
		return new PEG2PEGListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>() {
			public void beginFunction(SootMethod method) {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
					Map<Vertex<FlowValue<JavaParameter,JavaLabel>>,CPEGTerm<JavaLabel,JavaParameter>> rootVertexMap) {
				logger.log("Running engine");
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<JavaLabel,JavaParameter> engine) {
				logger.log("Building optimal PEG");
			}
			public void notifyRevertPEGBuilt(
					boolean original,
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo) {
				if (original)
					logger.log("Original PEG chosen as output");
				logger.log("Building reversion graph");
			}
			public void endFunction() {}
		};
	}
	
	
	private static DotPEG2PEGListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>
	getDotPEG2PEGListener(final StageInfo stage, final String prefix) {
		return new DotPEG2PEGListener<JavaLabel,JavaParameter,JavaReturn,SootMethod>(
				stage.OUTPUT_EPEG, stage.OUTPUT_OPTIMAL_PEG) {
			private String getFilenameString(String type, SootMethod method) {
				File outputFile = new File(stage.dotOutputFolder, 
						prefix + type + method.getSignature().replaceAll(" ","_") + ".dot"); 
				return outputFile.getPath();
			}
			protected String getEPEGFilename(SootMethod method) {
				return getFilenameString("EPEG_", method);
			}
			protected String getOPTPEGFilename(SootMethod method) {
				return getFilenameString("OPTPEG_", method);
			}
		};
	}
	
	private static MyPEG2PEGOptimizer getPEG2PEGOptimizer(StageInfo stage) {
		MyPEG2PEGOptimizer result = new MyPEG2PEGOptimizer(stage);
		return result;
	}
	
	private static MultiStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> 
	getMultiStageOptimizer(Logger logger) {
		List<PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod>> optimizers = 
			new ArrayList<PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod>>(stages.size());
		for (int i = 0; i < stages.size(); i++) {
			StageInfo stage = stages.get(i);
			PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> p2p = 
				getPEG2PEGOptimizer(stage);
			optimizers.add(p2p);
			p2p.addListener(
					getDotPEG2PEGListener(stage, "multi_stage" + i + "_"));
		}
		
		MultiStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> optimizer = 
			new MultiStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn>(optimizers) {
			protected OpAmbassador<JavaLabel> getOpAmbassador() {return stages.get(0).ambassador;}
			protected PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> getPEGProvider() {return bodyPegProvider;}
			protected JavaPEGCFG getOutputCFG(
					SootMethod body, 
					RevertCFG<JavaLabel,JavaParameter,JavaReturn> revert) {
				return Main.getOutputCFG(body, revert);
			}
			protected boolean canOptimize(SootMethod body) {
				return !SootUtils.hasExceptions(body);
			}
			protected void encodeCFG(JavaPEGCFG cfg, SootMethod body) {
				Main.encodeCFG(cfg, body);
			}
		};
		
		optimizer.addListener(getOutputOptimizerListener(logger));
		
		// TODO fix this to dump dot in different folders
		optimizer.addListener(
				new DotOptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>(
						OUTPUT_ORIGINAL_PEG, false, OUTPUT_REVERT_GRAPH, OUTPUT_REVERT_CFG, OUTPUT_OUTPUT_CFG) {
					private String mungeName(SootMethod method) {
						return method.getSignature().replaceAll(" ", "_");
					}
					protected String getPEGFilename(SootMethod method) {
						return "PEG_" + mungeName(method) + ".dot";
					}
					protected String getOPTPEGFilename(SootMethod method) {return null;}
					protected String getRevertFilename(SootMethod method) {
						return "REVERT_" + mungeName(method) + ".dot";
					}
					protected String getRevertCFGFilename(SootMethod method) {
						return "REVCFG_" + mungeName(method) + ".dot";
					}
					protected String getOutputCFGFilename(SootMethod method) {
						return "OUTCFG_" + mungeName(method) + ".dot";
					}
				});
		
		return optimizer;
	}

	private static OptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>
	getOutputOptimizerListener(final Logger logger) {
		return new OptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>() {
			public void beginFunction(SootMethod method) {
				logger.log("Building original PEG");
			}
			public void notifyOriginalPEGBuilt(
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo) {
				logger.log("Setting up engine");
			}
			public void notifyOptimalPEGBuilt(
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> peginfo) {
			}
			public void notifyReversionGraphBuilt(
					ReversionGraph<JavaParameter,JavaLabel> result) {
				logger.log("Building revert CFG");
			}
			public void notifyCFGReverterBuilt(
					CFGReverter<JavaParameter,JavaLabel,JavaReturn> reverter) {
				logger.log("Building output CFG");
			}
			public void notifyOutputCFGBuilt(JavaPEGCFG cfg) {
				logger.log("Encoding output CFG");
			}
			public void endFunction() {
				logger.log("Optimization completed");
			}
		};
	}
	
	private static DotOptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>
	getDotOptimizerListener(final StageInfo stage, final String prefix) {
		return new DotOptimizerListener<JavaLabel,JavaParameter,JavaReturn,JavaPEGCFG,SootMethod>(
				OUTPUT_ORIGINAL_PEG, false, OUTPUT_REVERT_GRAPH, OUTPUT_REVERT_CFG, OUTPUT_OUTPUT_CFG) {
			private String getFilenameString(String type, SootMethod method) {
				File file = new File(stage.dotOutputFolder, prefix + type 
						+ method.getSignature().replaceAll(" ", "_") + ".dot");
				return file.getPath();
			}
			protected String getPEGFilename(SootMethod method) {
				return getFilenameString("PEG_", method);
			}
			protected String getOPTPEGFilename(SootMethod method) {
				return getFilenameString("OPTPEG_", method);
			}
			protected String getRevertFilename(SootMethod method) {
				return getFilenameString("REVERT_", method);
			}
			protected String getRevertCFGFilename(SootMethod method) {
				return getFilenameString("REVCFG_", method);
			}
			protected String getOutputCFGFilename(SootMethod method) {
				return getFilenameString("OUTCFG_", method);
			}
		};
	}
	
	
	private static SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> 
	getSingleStageOptimizer(
			final StageInfo stage, 
			SingleStageOptimizer.Level optLevel) {
		PEG2PEGOptimizer<JavaLabel,JavaParameter,JavaReturn,SootMethod> p2p = 
			getPEG2PEGOptimizer(stage);
		SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn> optimizer = 
			new SingleStageOptimizer<JavaPEGCFG,SootMethod,JavaLabel,JavaParameter,JavaReturn>(p2p) {
			protected OpAmbassador<JavaLabel> getOpAmbassador() {return stage.ambassador;}
			protected PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> getPEGProvider() {return bodyPegProvider;}
			protected JavaPEGCFG getOutputCFG(
					SootMethod body, 
					RevertCFG<JavaLabel,JavaParameter,JavaReturn> revert) {
				return Main.getOutputCFG(body, revert);
			}
			protected boolean canOptimize(SootMethod body) {
				return !SootUtils.hasExceptions(body);
			}
			protected void encodeCFG(JavaPEGCFG cfg, SootMethod body) {
				Main.encodeCFG(cfg, body);
			}
			protected PEGInfo<JavaLabel,JavaParameter,JavaReturn> sanitizePEG(
					PEGInfo<JavaLabel,JavaParameter,JavaReturn> input) {
				return input;
			}
		};
		optimizer.setOptimizationLevel(optLevel);
		return optimizer;
	}
	
	private static SootClass setup(Logger logger) {
		abortIf(inputClassName == null, "No input file specified", logger);
		
		globalAmbassador = new JavaLabelOpAmbassador(
				new CustomAnnotationConstantFolder());
		
		// build rule parsers
		for (StageInfo stage : stages) {
			// build OpAmbassador
			stage.ambassador = new JavaLabelOpAmbassador(
					new CustomAnnotationConstantFolder());
			
			stage.ruleParser = new peggy.input.java.JavaXMLRuleParser(
					null, stage.network, stage.ambassador);
		
			// read axiom files
			for (File axiomFile : stage.axiomFiles) {
				try {
					stage.axioms.addAll(stage.ruleParser.parseRuleSet(axiomFile));
					logger.log("Successfully added axiom file: " + axiomFile.getPath());
				} catch (Throwable t) {
					abort("Error parsing axiom file: " + axiomFile.getPath(), t, logger);
				}
			}
		}
			
		logger.log("Loading class file " + inputClassName);
		
		// read the module file
		SootClass clazz = null;
		try {
			clazz = Scene.v().loadClassAndSupport(inputClassName);
			Scene.v().loadBasicClasses();
		} catch(Throwable t) {
			abort("Error loading class " + inputClassName, t, logger);
		}

		return clazz;
	}
	
	
	private static void setupLearn(
			Logger logger,
			List<String> learnMethods,
			List<String> optimizeMethods) {
		if (stages.size() != 2)
			abort("Learning mode must have 2 stages", logger);

		stages.get(0).dotOutputFolder.mkdirs();
		stages.get(1).dotOutputFolder.mkdirs();
		
		globalAmbassador = new JavaLabelOpAmbassador(
				new CustomAnnotationConstantFolder());
		
		// build rule parsers
		for (StageInfo stage : stages) {
			stage.ambassador = new JavaLabelOpAmbassador(
					new CustomAnnotationConstantFolder());
			
			
			stage.ruleParser = new peggy.input.java.JavaXMLRuleParser(
					null, stage.network, stage.ambassador); 
			// read axiom files
			for (File axiomFile : stage.axiomFiles) {
				try {
					stage.axioms.addAll(stage.ruleParser.parseRuleSet(axiomFile));
					logger.log("Successfully added axiom file: " + axiomFile.getPath());
				} catch (Throwable t) {
					abort("Error parsing axiom file: " + axiomFile.getPath(), t, logger);
				}
			}
		}
			
		// load the method lists
		logger.log("Parsing learn method file");
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(learningMethodFile));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0)
					learnMethods.add(line);
			}
			reader.close();
		} catch (Throwable t) {
			abort("Error reading learn method file", t, logger);
		}

		logger.log("Parsing optimize method file");
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(optimizingMethodFile));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0)
					optimizeMethods.add(line);
			}
			reader.close();
		} catch (Throwable t) {
			abort("Error reading optimize method file", t, logger);
		}
		
		try {
			Scene.v().loadBasicClasses();
		} catch (Throwable t) {
			abort("Soot cannot load basic Java classes", t, logger);
		}
	}
	
	
	
	static class Holder<T> {
		private T thing;
		public Holder() {}
		public void set(T t) {thing = t;}
		public T get() {return thing;}
	}

	
	
	private static ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>
	getGeneralizer(
			Logger logger,
			final CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			final CPEGTerm<JavaLabel,JavaParameter> left,
			final CPEGTerm<JavaLabel,JavaParameter> right) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> future = 
			service.submit(new Callable<ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>>() {
				public ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> call() {
					ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> generalizer = 
						new ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>(
								globalAmbassador,
								engine.getEGraph().getProofManager(),
								left, right, true);
					return generalizer;
				}
			});
		
		ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> generalizer = null; 

		if (options.getLong(GENERALIZE_TIMEOUT) > 0) {
			try {
				generalizer = future.get(options.getLong(GENERALIZE_TIMEOUT), TimeUnit.MILLISECONDS);
			} catch (TimeoutException te) {
				logger.log("Generalization timed out");
			} catch (Throwable t) {
				logger.logException("Error in generalization", t);
			}
		} else {
			try {
				generalizer = future.get();
			} catch (Throwable t) {
				abort("Error in generalization", t, logger);
			}
		}

		service.shutdownNow();
		
		return generalizer;
	}
	
	
	private static void printGeneralizedProof(
			Logger logger,
			final CPeggyAxiomEngine<JavaLabel,JavaParameter> engine,
			Map<JavaReturn,Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>>> return2nodes,
			String filenamePrefix) {
		long startGenTime = System.currentTimeMillis();
		logger.log("Computing generalization");

		Logger genLogger = logger.getSubLogger();
		
		List<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> epegs = 
			new ArrayList<PostMultiGenEPEG<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> ();
		for (JavaReturn arr : return2nodes.keySet()) {
			Pair<CPEGTerm<JavaLabel,JavaParameter>,CPEGTerm<JavaLabel,JavaParameter>> pair = 
				return2nodes.get(arr);

			genLogger.log("Generalizing " + arr);
			ProofPostMultiGeneralizer<JavaLabel,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> generalizer = 
				getGeneralizer(genLogger, engine, pair.getFirst(), pair.getSecond());

			if (generalizer != null) {
				genLogger.log("Generated " + generalizer.getEPEGs().size() + " from " + arr);
				epegs.addAll(generalizer.getEPEGs());
				// print generalized PEG
				//String filename = filenamePrefix + ".genpeg." + arr + ".dot";
				//dumpToDot(filename, generalizer.getPEG());
			} else {
				genLogger.log("Cannot generalize " + arr);
			}
		}
		long endGenTime = System.currentTimeMillis();
			
		genLogger.log("GENTIME " + (endGenTime-startGenTime));
		
		if (epegs.size() > 0) {
			genLogger.log("Writing generalized EPEGs to file");
			
			Logger subLogger = genLogger.getSubLogger();
			for (int i = 0; i < epegs.size(); i++) {
				int subsumed = -1;
				for (int j = 0; j < i; j++)
					if (epegs.get(j).subsumes(epegs.get(i))) {
						subsumed = j;
						break;
					}

				if (subsumed != -1)
					subLogger.log("EPEG " + i + " subsumed by " + subsumed);
//				String fullName = filenamePrefix + ".generalized" + i + (subsumed > 0 ? ".subsumedby" + subsumed : "") + ".dot";
//				output("Writing " + fullName);
//				dumpToDot(fullName, epegs.get(i).toString(true));
			}
		} else
			genLogger.log("No generalized EPEGs to write");
		
		logger.log("Done generalizing");
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
			stages.add(new StageInfo());
			optionsParser.parse(args);
		} catch (OptionParsingException ex) {
			abort("Error parsing command line: " + ex.getMessage(), TOP_LOGGER);
		}
		
		if (optimizationLevel != null) {
			SootClass clazz = setup(TOP_LOGGER);
			if (options.getBoolean(ENGINE_ONLY)) {
				if (stages.size() != 1) {
					abort("Engine only optimization can only have 1 stage", TOP_LOGGER);
				}
				optimizeEngineOnly(
						TOP_LOGGER,
						clazz,
						inputClassName,
						skippedFunctions);
			} else {
				optimizeClass(
						TOP_LOGGER,
						clazz, 
						inputClassName, 
						options.getFile(OUTPUT_FOLDER), 
						skippedFunctions);
			}
		} else if (learningMode) {
			List<String> learnMethods = new ArrayList<String>();
			List<String> optimizeMethods = new ArrayList<String>();
			setupLearn(TOP_LOGGER, learnMethods, optimizeMethods);
			learnAndOptimize(TOP_LOGGER, learnMethods, optimizeMethods);
		} else {
			abort("No optimization or validation specified", TOP_LOGGER);
		}
		
		System.exit(0);
	}
}
