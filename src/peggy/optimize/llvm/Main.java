package peggy.optimize.llvm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.BitcodeWriter;
import llvm.bitcode.DataLayout;
import llvm.bitcode.DefaultReferenceResolver;
import llvm.bitcode.FabricatingReferenceResolver;
import llvm.bitcode.ModuleEncoder;
import llvm.bitcode.ReferenceResolver;
import llvm.instructions.BasicBlock;
import llvm.instructions.BlockMerger;
import llvm.instructions.DefaultFunctionValueNamer;
import llvm.instructions.FunctionBody;
import llvm.instructions.FunctionValueNamer;
import llvm.instructions.RetInstruction;
import llvm.types.Type;
import llvm.values.FunctionValue;
import llvm.values.Module;
import llvm.values.Value;
import peggy.Logger;
import peggy.OptionParsingException;
import peggy.OptionsParser;
import peggy.analysis.BoundedEngineRunner;
import peggy.analysis.CREGVertexIterable;
import peggy.analysis.EngineRunner;
import peggy.analysis.EngineThetaMerger;
import peggy.analysis.TemporaryPhiAxioms;
import peggy.analysis.llvm.DefaultLLVMConstantFolder;
import peggy.analysis.llvm.FunctionModifies;
import peggy.analysis.llvm.GEPRemovalAnalysis;
import peggy.analysis.llvm.GlobalAnalysis;
import peggy.analysis.llvm.LIVSRHelperAnalysis;
import peggy.analysis.llvm.LLVMAliasAnalysis;
import peggy.analysis.llvm.LLVMBinopConstantAnalysis;
import peggy.analysis.llvm.LLVMConstantAnalysis;
import peggy.analysis.llvm.LLVMConstantFoldingAnalysis;
import peggy.analysis.llvm.LLVMInliner;
import peggy.analysis.llvm.LLVMIntrinsicAnalysis;
import peggy.analysis.llvm.LLVMOperatorAnalysis;
import peggy.analysis.llvm.LibCAnalysis;
import peggy.analysis.llvm.LoadStoreAnalysis;
import peggy.analysis.llvm.NonstackFunctionAnalysis;
import peggy.analysis.llvm.SelectAnalysis;
import peggy.ilp.GLPKRunner;
import peggy.input.XMLRuleParser;
import peggy.optimize.DotOptimizerListener;
import peggy.optimize.DotPEG2PEGListener;
import peggy.optimize.MultiStageOptimizer;
import peggy.optimize.Optimizer;
import peggy.optimize.OptimizerLastDataListener;
import peggy.optimize.OptimizerListener;
import peggy.optimize.OptimizerTimerListener;
import peggy.optimize.PEG2PEGLastDataListener;
import peggy.optimize.PEG2PEGListener;
import peggy.optimize.PEG2PEGOptimizer;
import peggy.optimize.SingleStageOptimizer;
import peggy.optimize.SingleStageOptimizer.Level;
import peggy.pb.ConfigurableCostModel;
import peggy.pb.CostModel;
import peggy.pb.LooplessReversionHeuristic2;
import peggy.pb.MinisatFormulation;
import peggy.pb.MinisatRunner;
import peggy.pb.NonPessimizingHeuristic;
import peggy.pb.PBRunner;
import peggy.pb.PuebloFormulation;
import peggy.pb.PuebloRunner;
import peggy.represent.DefaultPEGExtractor;
import peggy.represent.PEGExtractor;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GEPForcingPolicy;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.LLVMBodyPEGProvider;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOpAmbassador;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.represent.llvm.LazyMultiModulePEGProvider;
import peggy.represent.llvm.ModuleProvider;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import peggy.revert.ReversionHeuristic;
import peggy.revert.llvm.LLVMPEGCFG;
import peggy.revert.llvm.LLVMPEGCFGEncoder;
import util.AbstractPattern;
import util.Action;
import util.NamedTag;
import util.Pattern;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.engine.AxiomSelector;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.axiom.BooleanAxioms;
import eqsat.meminfer.peggy.axiom.EqualityAxioms;
import eqsat.meminfer.peggy.axiom.LoopAxioms;
import eqsat.meminfer.peggy.axiom.LoopInteractionAxioms;
import eqsat.meminfer.peggy.axiom.PeggyAxiomSetup;
import eqsat.meminfer.peggy.axiom.PhiAxioms;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;
import eqsat.revert.RevertCFG;

/**
 * This is the command-line interface class for the LLVM optimizer.
 * For the usage and command-line arguments, run with the "-help" option.
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
		PUEBLO,
		MINISAT,
		GLPK,
	}

	private static enum DataLayoutSource {
		MODULE,
		EXPLICIT,
		DEFAULT;
	}
	
	private static class MyEngineRunner extends 
	BoundedEngineRunner<LLVMLabel,LLVMParameter> {
		long lastIterStop;
		MyEngineRunner() {super(-1L, 1000L, -1L);}
		protected void updateEngine(CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
			if (getLogger() != null)
				getLogger().log("Performing theta merging");
			if (options.getBoolean(MERGE_THETAS)) {
				EngineThetaMerger<LLVMLabel,LLVMParameter> merger = 
					new EngineThetaMerger<LLVMLabel,LLVMParameter>(engine);
				
				if (getLogger() != null) {
					getLogger().log("ENGINEVALUES " + getEngineValueCount(engine));			
					getLogger().log("ENGINETERMS " + getEngineTermCount(engine));
					getLogger().log("THETASTATS " + Arrays.toString(getMatchingThetaStats(engine)));
				}
				
				merger.setLogger(getLogger());
				merger.setTimeout(options.getLong(THETA_MERGER_TIMEOUT));
				merger.mergeThetas();
			}
		}
		protected void notifySaturated(long iters, long time) {
			lastIterStop = iters;
			if (getLogger() != null)
				getLogger().log("Engine saturated in " + iters + " iterations");
		}
		protected void notifyTimeBoundReached(long iters, long time) {
			lastIterStop = iters;
			if (getLogger() != null)
				getLogger().log("Engine reached time bound of " + time + " after " + iters + " iterations");
		}
		protected void notifyIterationBoundReached(long iters, long time) {
			lastIterStop = iters;
			if (getLogger() != null)
				getLogger().log("Engine reached iteration bound of " + iters + " after " + time + " milliseconds");
		}
		protected void notifyMemoryBoundReached(long iters, long time, long mem) {
			lastIterStop = iters;
			if (getLogger() != null)
				getLogger().log("Engine reached memory bound of " + mem + " after " + time + " milliseconds");
		}
		protected void notifyHalted(long iters, long time, long mem) {
			lastIterStop = iters;
			if (getLogger() != null)
				getLogger().log("Engine halted after " + iters + " iterations");
		}
	}
	
	private static class StageInfo {
		boolean allocaStage = false;
		peggy.input.llvm.LLVMXMLRuleParser ruleParser;
		final Collection<AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>>> axioms = 
			new ArrayList<AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>>>(100);
		final Set<String> activatedAnalyses = new HashSet<String>();
		int maxPBFileSize = 0;
		PB pbOption = PB.MINISAT;
		final Set<File> axiomFiles = new HashSet<File>();
		boolean OUTPUT_EPEG = false;
		boolean OUTPUT_OPTIMAL_PEG = false;
		boolean looplessReversion = false;
		final MyEngineRunner engineRunner = new MyEngineRunner();
	}
	
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
	
	private static void displayHelp() {
		List<String> keys = new ArrayList<String>(optionsParser.getCommandKeys());
		Set<String> boolKeys = options.getBooleanKeys();
		Set<String> longKeys = options.getLongKeys();
		Set<String> stringKeys = options.getStringKeys();
		Set<String> fileKeys = options.getFileKeys();
		
		keys.addAll(boolKeys);
		keys.addAll(longKeys);
		keys.addAll(stringKeys);
		keys.addAll(fileKeys);
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
			else {
				System.err.printf(format, "-" + opt, optionsParser.getCommandDescription(opt)); 
			}
		}
		System.exit(0);
	}
	

	private static final Options options = new Options();
	private static final OptionsParser optionsParser = new OptionsParser(options);
	private static final String THETA_MERGER_TIMEOUT = "thetaMergerTimeout";
	private static final String USE_STATIC_ALLOCA_REMOVER = "staticAllocaRemover";
	private static final String MERGE_THETAS = "mergeThetas";
	private static final String BITCODE_THRESHOLD = "bcthreshold";
	private static final String USE_CFG_EXCEPTIONS = "exceptions";
	private static final String DISPLAY_AXIOMS = "displayAxioms";
	private static final String OUTPUT_ORIGINAL_PEG = "oop";
	private static final String OUTPUT_REVERT_GRAPH = "orev";
	private static final String OUTPUT_REVERT_CFG = "orevcfg";
	private static final String OUTPUT_OUTPUT_CFG = "ooutcfg";
	private static final String DELETE_PB_FILES = "deletepb";
	private static final String PB_TIMEOUT = "pbtime";
	private static final String TMP_FOLDER = "tmpFolder";
	private static final String INCREMENTAL_FUNCTION_NAME = "incremental";
	private static final String OUTPUT_FOLDER = "o";
	private static final String DATALAYOUT_EXPLICIT = "datalayout:explicit";
	private static final String PUEBLO_PATH = "puebloPath";
	private static final String MINISAT_PATH = "minisatPath";
	private static final String GLPK_PATH = "glpkPath";
	private static final String PARAMS_DNA_NULL = "paramsDNANull";
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
				"Set to true to enable EPEG proof generation");
		options.registerBoolean(PARAMS_DNA_NULL, false,
				"Set to true to add info saying that pointer parameters do not alias null");
		options.registerString(MINISAT_PATH, System.getenv("COLLIDER_ROOT") + "/scripts/minisat/Minisat",
				"Specify the path to the Minisat executable (default $COLLIDER_ROOT/scripts/minisat/Minisat)");
		options.registerString(GLPK_PATH, "/usr/bin/glpsol",
				"Specify the path to the GLPK executable (default /usr/bin/glpsol)");
		options.registerString(PUEBLO_PATH, System.getenv("COLLIDER_ROOT") + "/scripts/pueblo/Pueblo",
				"Specify the path to the Pueblo executable (default $COLLIDER_ROOT/scripts/pueblo/Pueblo)");
		options.registerLong(THETA_MERGER_TIMEOUT, -1L, 
				"The maximal amount of time that the theta merger shall be allowed to run, in milliseconds",
				null);
		// TODO this is a huge hack and should be removed
		options.registerBoolean(USE_STATIC_ALLOCA_REMOVER, false,
				"Set to true if you wish to run the static ALLOCA remover stage (default false)");
		options.registerBoolean(MERGE_THETAS, true,
				"Set to true if you wish to run the theta merger (default true)");
		optionsParser.registerCommand("newstage",
				"Signals the start of a new optimizer stage",
				new Runnable() {
					public void run() {
						if (!(optimizationLevel  == null || optimizationLevel.equals(Level.RUN_ENGINE_FULL)))
							throw new OptionParsingException("Cannot have multiple stages when optimization level is specified"); 
						stages.add(new StageInfo());
					}
				});
		options.registerString("excludelist", "", 
				"Specify the name of a file containing " +
				"functions to skip when optimizing",
				new Action<String>() {
					public void execute(String list) {
						try {
							BufferedReader bin = new BufferedReader(new FileReader(list));
							String line;
							while ((line = bin.readLine()) != null) {
								line = line.trim();
								if (!line.equals("")) {
									skippedFunctions.add(line);
									debug("adding skipped function: " + line);
								}
							}
							bin.close();
						} catch (Throwable t) {
							throw new OptionParsingException("Cannot read exclude list", t);
						}
					}
				});
		options.registerLong(BITCODE_THRESHOLD, 0L,
				"Sets a maximum threshold on the size of functions we will optimize", null);
		options.registerBoolean(USE_CFG_EXCEPTIONS, false,
				"Set to true if you want the PEG/EPEG to represent exceptions (default false)");
		options.registerBoolean(DISPLAY_AXIOMS, false,
				"Set to true if you wish to see which axioms are applied during EQSAT (default false)");
		options.registerBoolean(OUTPUT_ORIGINAL_PEG, false, 
				"Set to true to output a dot graph of the original PEG (default false)");
		options.registerBoolean(OUTPUT_REVERT_GRAPH, false,
				"Set to true to output a dot graph of the revert graph (default false)");
		options.registerBoolean(OUTPUT_REVERT_CFG, false,
				"Set to true to output a dot graph of the revert CFG (default false)");
		options.registerBoolean(OUTPUT_OUTPUT_CFG, false,
				"Set to true to output a dot graph of the output CFG (default false)");
		options.registerBoolean(DELETE_PB_FILES, true,
				"Set to true to delete temporary files use by the PB solver (default true)");
		options.registerLong(PB_TIMEOUT, 0L, 
				"Sets a timeout on how long the PB solver may run (milliseconds)", null);
		options.registerFile(TMP_FOLDER, null,
				"Set the folder where temporary files are made", null);

		options.registerLong("mergeTimeUpdate", 0L, 
				"Set the time after which the theta merger will run (default 0)",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setTimeUpdate(value);
					}
				});
		options.registerLong("mergeIterationUpdate", 0L,
				"Set the number of iterations after which the theta merger will run (default 0)",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setIterationUpdate(value);
					}
				});
		options.registerBoolean("looplessReversion", false,
				"Set to true to activate loopless reversion (default false)",
				new Action<Boolean>() {
					public void execute(Boolean value) {
						stages.get(stages.size()-1).looplessReversion = value;
						
					}
				});
		optionsParser.registerCommand("allocaStage",
				"Make the current stage into the alloca removal stage (default false)",
				new Runnable() {
					public void run() {
						stages.get(stages.size()-1).allocaStage = true;
					}
				});
		options.registerString("activate", null,
				"Activate equality analyses by name, in a colon-separated string",
				new Action<String>() {
					public void execute(String str) {
						String[] analyses = str.split(":");
						for (String a : analyses) {
							stages.get(stages.size()-1).activatedAnalyses.add(a);
						}
					}
				});
		options.registerString(INCREMENTAL_FUNCTION_NAME, null,
				"Set to the name of a single function to optimize",
				null);
		options.registerLong("maxmemory", 0L,
				"Set the maximum memory that the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setMemoryUpperBound(value);
					}
				});
		options.registerLong("eto", 1000L,
				"Set the maximum number of iterations the engine will run (default 1000)",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setIterationUpperBound(value);
					}
				});
		options.registerLong("maxtime", 0L,
				"Set the maximum time the engine may run (in milliseconds)",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).engineRunner.setTimeUpperBound(value);
					}
				});
		optionsParser.registerCommand("gep64", 
				"Force all GEPs to use 64-bit operands",
				new Runnable() {
					public void run() {
						forcingPolicy = GEPForcingPolicy.FORCE_64;
					}
				});
		optionsParser.registerCommand("gep32", 
				"Force all GEPs to use 32-bit operands",
				new Runnable() {
					public void run() {
						forcingPolicy = GEPForcingPolicy.FORCE_32;
					}
				});
		options.registerString("axioms", null,
				"Add axiom files to the engine, in a colon-separated list",
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
				"Set the maximum allowable file size of a PB input file",
				new Action<Long>() {
					public void execute(Long value) {
						stages.get(stages.size()-1).maxPBFileSize = (int)value.longValue();		
					}
				});
		options.registerFile(OUTPUT_FOLDER, new File("optimized"),
				"Set the output folder for optimized modules (default 'optimized/')", null);
		options.registerString("exclude", null,
				"Specify a colon-separated list of function names to exclude",
				new Action<String>() {
					public void execute(String str) {
						String[] list = str.split(":");
						for (String item : list) {
							item = item.trim();
							if (!item.equals(""))
								skippedFunctions.add(item);
						}
					}
				});
		optionsParser.registerCommand("oep", 
				"Set to true to output a dot graph of the EPEG after saturation (default false)",
				new Runnable() {
					public void run() {
						stages.get(stages.size()-1).OUTPUT_EPEG = true;			
					}
				});
		optionsParser.registerCommand("oopt", 
				"Set to true to output a dot graph of the optimal PEG after saturation (default false)",
				new Runnable() {
					public void run() {
						stages.get(stages.size()-1).OUTPUT_OPTIMAL_PEG = true;			
					}
				});
		optionsParser.registerCommand("datalayout:default", 
				"Specify this option if you want the default LLVM datalayout",
				new Runnable() {
					public void run() {
						dataLayoutSource = DataLayoutSource.DEFAULT;
					}
				});
		optionsParser.registerCommand("datalayout:module", 
				"Specify this option if you want to use the module's LLVM datalayout (default)",
				new Runnable() {
					public void run() {
						dataLayoutSource = DataLayoutSource.MODULE;
					}
				});
		options.registerString("datalayout:explicit", null,
				"Explicitly set the LLVM data layout string",
				new Action<String>() {
					public void execute(String layout) {
						dataLayoutSource = DataLayoutSource.EXPLICIT;
					}
				});
		options.registerString("modulePath", null,
				"Specify a colon-separated list of module files where functions may be loaded from",
				new Action<String>() {
					public void execute(String str) {
						String[] modules = str.split(":");
						for (String module : modules) {
							module = module.trim();
							if (!module.equals(""))
								moduleProvider.addModuleFile(new File(module));
						}
					}
				});
		options.registerString("pb", null, 
				"Set the PB solver to use",
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
						else {
							throw new OptionParsingException("Unknown PB solver: " + str);
						}
					}
				});
		options.registerString("O0", null,
				"Optimize the given file at level 0",
				new Action<String>() {
					public void execute(String str) {
						if (moduleInputFile != null)
							throw new OptionParsingException("Duplicate input module specified");
						if (stages.size() != 1)
							throw new OptionParsingException(
								"Cannot specify optimization level with multi-stage optimization");
						File moduleFile = new File(str);
						moduleInputFile = moduleFile;
						optimizationLevel = SingleStageOptimizer.Level.PARSE_AND_REWRITE;
					}
				});
		options.registerString("O1", null,
				"Optimize the given file at level 1",
				new Action<String>() {
					public void execute(String str) {
						if (moduleInputFile != null)
							throw new OptionParsingException("Duplicate input module specified");
						if (stages.size() != 1)
							throw new OptionParsingException(
								"Cannot specify optimization level with multi-stage optimization");
						File moduleFile = new File(str);
						moduleInputFile = moduleFile;
						optimizationLevel = SingleStageOptimizer.Level.PEG_AND_BACK;
					}
				});
		options.registerString("O2", null,
				"Optimize the given file at level 2",
				new Action<String>() {
					public void execute(String str) {
						if (moduleInputFile != null)
							throw new OptionParsingException("Duplicate input module specified");
						if (stages.size() != 1)
							throw new OptionParsingException(
								"Cannot specify optimization level with multi-stage optimization");
						File moduleFile = new File(str);
						moduleInputFile = moduleFile;
						optimizationLevel = SingleStageOptimizer.Level.RUN_ENGINE_FULL;
					}
				});
	}

	private static final Tag<CPEGTerm<LLVMLabel,LLVMParameter>> TERM_TAG = 
		new NamedTag<CPEGTerm<LLVMLabel,LLVMParameter>>("Tags vertices with their terms");
	private static final Logger TOP_LOGGER = new MyLogger();
	private static final List<StageInfo> stages = 
		new ArrayList<StageInfo>(Arrays.asList(new StageInfo()));
	private static final Set<String> skippedFunctions = new HashSet<String>();
	private static Network network = new PeggyAxiomNetwork<LLVMLabel>(new Network());
	private static LLVMOpAmbassador ambassador;
	private static final Set<File> tempFiles = new HashSet<File>();
	private static DataLayoutSource dataLayoutSource = DataLayoutSource.MODULE;
	private static final DefaultLLVMConstantFolder constantFolder = 
		new DefaultLLVMConstantFolder(new DataLayout());
	private static ModuleProvider moduleProvider = new ModuleProvider();
	private static SingleStageOptimizer.Level optimizationLevel; 
	private static GEPForcingPolicy forcingPolicy = GEPForcingPolicy.NONE;
	private static File moduleInputFile;
	private static final LazyMultiModulePEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> bodyPegProvider =
		new LazyMultiModulePEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn>() {
			protected boolean hasFunction(Module module, FunctionBody function) {
				for (int i = 0; i < module.getNumFunctionBodies(); i++) {
					if (module.getFunctionBody(i) == function)
						return true;
				}
				return false;
			}
			protected ModuleProvider getModuleProvider() {return moduleProvider;}
			protected PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> getPEG(
				Module module, 
				FunctionBody function) {
				LLVMBodyPEGProvider pegprovider = 
					new LLVMBodyPEGProvider(
							new FabricatingReferenceResolver(module, moduleProvider),
							ambassador);
				// TODO THIS IS A HACK
				PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result = pegprovider.getPEG(function);
//				if (options.getBoolean(USE_STATIC_ALLOCA_REMOVER)) {
//					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result2;
//					try {
//						result2 = AllocaRemover2.removeAllocas(result);
//					} catch (Throwable t) {
//						result2 = result;
//					}
//					return result2;
//				} else {
//					return result;
//				}
				
				return result;
			}
		};
	
	private static final LazyMultiModulePEGProvider<FunctionLLVMLabel,LLVMLabel,LLVMParameter,LLVMReturn> labelPegProvider =
		new LazyMultiModulePEGProvider<FunctionLLVMLabel,LLVMLabel,LLVMParameter,LLVMReturn>() {
			protected boolean hasFunction(Module module, FunctionLLVMLabel function) {
				return lookup(module, function) != null;
			}
			protected ModuleProvider getModuleProvider() {return moduleProvider;}
			private FunctionBody lookup(Module module, FunctionLLVMLabel label) {
				Value namedValue = module.getValueByName(label.getFunctionName());
				if (namedValue == null || !namedValue.isFunction())
					return null;
				FunctionValue header = namedValue.getFunctionSelf();
				if (!header.getType().getPointeeType().equalsType(label.getType()))
					return null;
				for (int i = 0; i < module.getNumFunctionBodies(); i++) {
					if (module.getFunctionBody(i).getHeader().equalsValue(header)) {
						return module.getFunctionBody(i);
					}
				}
				return null;
			}
			protected PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> getPEG(
				Module module, 
				FunctionLLVMLabel function) {
				LLVMBodyPEGProvider pegprovider = 
					new LLVMBodyPEGProvider(
							new FabricatingReferenceResolver(module, moduleProvider),
							ambassador);

				// TODO THIS IS A HACK
				PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result = 
					pegprovider.getPEG(lookup(module, function));
//				if (options.getBoolean(USE_STATIC_ALLOCA_REMOVER)) {
//					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result2; 
//					try {
//						result2 = AllocaRemover2.removeAllocas(result);
//					} catch (Throwable t) {
//						result2 = result;
//					}
//					return result2;
//				} else {
//					return result;
//				}
				
				return result;
			}
		};
	
	///////////////////////////////////////////////

	private static void abortIf(boolean b, String message) {
		if (b) abort(message);
	}
	private static void abort(String message) {
		TOP_LOGGER.log("!!! CRITICAL ERROR: " + message);
		System.exit(1);
	}
	private static void abort(String message, Throwable thrown) {
		TOP_LOGGER.logException("!!! CRITICAL ERROR: " + message, thrown);
		System.exit(1);
	}
	
	///////////////////////////////////////////////
	
	private static int totalBitcodeCount(FunctionBody body) {
		int total = 0;
		for (int i = 0; i < body.getNumBlocks(); i++)
			total += body.getBlock(i).getNumInstructions();
		return total;
	}
	
	private static void optimizeModule(
			Module module,
			File inputFile,
			File outputFolder,
			Set<String> skippedFunctionSet, 
			Logger logger) {
			
		Optimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> optimizer = 
			getOptimizer(module, logger);

		logger.log("Optimizing module " + inputFile.getPath());
		
		boolean isSingle = (stages.size()==1);
		
		if (options.getString(INCREMENTAL_FUNCTION_NAME) != null) {
			optimizeIncremental(optimizer, module, options.getString(INCREMENTAL_FUNCTION_NAME), inputFile, outputFolder, isSingle, logger);
		} else {
			optimizeAll(optimizer, module, inputFile, outputFolder, skippedFunctionSet, isSingle, logger);
		}
	}
	
	private static void optimizeIncremental(
			Optimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> optimizer,
			Module module,
			String functionName,
			File inputFile,
			File outputFolder,
			boolean isSingle,
			Logger logger) {
		long startTotalTime = System.currentTimeMillis();

		Logger funcLogger = logger.getSubLogger();
		
		OptimizerTimerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody> timer = 
			new OptimizerTimerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody> ();
		optimizer.addListener(timer);
		
		Value value = module.getValueByName(functionName);
		abortIf(!value.isFunction(), "Value named " + functionName + " is not a function");
		
		FunctionBody body = null;
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			if (module.getFunctionBody(i).getHeader().equalsValue(value)) {
				body = module.getFunctionBody(i);
				break;
			}
		}
		abortIf(body == null, "Cannot find function named " + functionName);
		abortIf(!bodyPegProvider.canProvidePEG(body), "Function " + functionName + " cannot be PEGified, skipping");

		logger.log("Processing function " + functionName);
		
		try {
			optimizer.optimize(body);
			
			{// perform some block merging
				BlockMerger merger = new BlockMerger(body);
				merger.mergeBlocks();
				merger = null;
			}
			
			{// name the values in the function
				FunctionValueNamer namer = new DefaultFunctionValueNamer(body);
				namer.assignNames();
				namer = null;
			}
			funcLogger.log("Optimization of function " + functionName + " SUCCESSFUL");
			funcLogger.log("Optimization took " + (timer.getEndFunctionTime() - timer.getBeginFunctionTime()) + " milliseconds");
		} catch (Throwable t) {
			funcLogger.logException("Error processing function " + functionName, t);
			funcLogger.log("Reverting to original function body");
			funcLogger.log("Optimization of function " + functionName + " FAILED");
		} finally {
			deleteTempFiles();
		}

		funcLogger.log("Done processing function " + functionName);
		System.gc();

		logger.log("Done optimizing " + inputFile.getPath());
	
		// gut the functions
		gutFunctionBodies(module, body);
		
		try {
			File outputFile = new File(outputFolder, inputFile.getName() + "." + functionName);
			logger.log("Writing module back to " + outputFile.getPath());
			
			outputFile.getParentFile().mkdirs();
			
			FileOutputStream fout = new FileOutputStream(outputFile);
			BitcodeWriter writer = new BitcodeWriter();
			new ModuleEncoder(writer, module).writeModule();
			writer.dump(fout);
			fout.close();
		} catch (Throwable t) {
			logger.logException("Error writing module back to disk", t);
		}
		
		long endTotalTime = System.currentTimeMillis();
		logger.log("Total optimization time = " + (endTotalTime-startTotalTime) + " milliseconds");
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
	
	private static void gutFunctionBodies(Module module, FunctionBody goodbody) {
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			FunctionBody body = module.getFunctionBody(i);
			if (body == goodbody)
				continue;
			BasicBlock start = body.getStart();
			
			// remove blocks (not start)
			for (int j = 0; j < body.getNumBlocks(); j++) {
				if (start != body.getBlock(j)) {
					body.removeBlock(j);
					j--;
				}
			}
			
			// remove handles from start
			while (start.getNumInstructions() > 0)
				start.removeInstruction(0);
			
			// clear out assignment
			body.getRegisterAssignment().clear();
			
			// clear map
			body.clearValueNameMap();
			
			// add return instruction
			Type returntype = body.getHeader().getType().getPointeeType().getFunctionSelf().getReturnType();
			RetInstruction ret;
			if (returntype.isVoid()) {
				ret = new RetInstruction();
			} else {
				ret = new RetInstruction(Collections.singletonList(Value.getNullValue(returntype)));
			}
			start.addInstruction(ret);
		}
	}


	private static int calculateCost(
			Collection<? extends CPEGTerm<LLVMLabel,LLVMParameter>> terms,
			StageInfo stage) {
		int cost = 0;
		ConfigurableCostModel<FlowValue<LLVMParameter,LLVMLabel>,FunctionLLVMLabel,CPEGTerm<LLVMLabel,LLVMParameter>,Integer>
		costModel = stage.ruleParser.getCostModel();
		for (CPEGTerm<LLVMLabel,LLVMParameter> term : terms) {
			cost += costModel.cost(term);
		}
		return cost;
	}
	private static int calculateCost(
			PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> taggedPEG,
			StageInfo stage) {
		Set<CPEGTerm<LLVMLabel,LLVMParameter>> terms = 
			new HashSet<CPEGTerm<LLVMLabel,LLVMParameter>>();
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : taggedPEG.getGraph().getVertices()) {
			terms.add(vertex.getTag(TERM_TAG));
		}
		return calculateCost(terms, stage);
	}
	
	
	
	private static void optimizeAll(
			Optimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> optimizer,
			Module module,
			File inputFile,
			File outputFolder,
			Set<String> skippedFunctionSet,
			boolean isSingle,
			Logger logger) {
		long startTotalTime = System.currentTimeMillis();
		
		int buggyFunctionCount = 0;
		int skippedFunctionCount = 0;
		int totalFunctionCount = 0;
		
		PEG2PEGLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> peg2pegData = 
			new PEG2PEGLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>();
		OptimizerLastDataListener<LLVMLabel, LLVMParameter, LLVMReturn, LLVMPEGCFG, FunctionBody> data = 
			new OptimizerLastDataListener<LLVMLabel, LLVMParameter, LLVMReturn, LLVMPEGCFG, FunctionBody> ();
		optimizer.addListener(data);

		Logger funcLogger = logger.getSubLogger();
		
		PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> peg2peg = null;
		if (isSingle) {
			peg2peg = ((SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn>)optimizer).getPEG2PEGOptimizer();
			peg2peg.addListener(peg2pegData);
		}
		
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			FunctionBody body = module.getFunctionBody(i);
			totalFunctionCount++;
				
			String fullName = module.lookupValueName(body.getHeader());
				
			if (skippedFunctionSet.contains(fullName)) {
				logger.log("Skipping function " + fullName);
				skippedFunctionCount++;
				continue;
			}
			
			if (options.getLong(BITCODE_THRESHOLD) > 0 && 
				totalBitcodeCount(body) > options.getLong(BITCODE_THRESHOLD)) {
				logger.log("Function " + fullName + " exceeds bitcode threshold, skipping");
				skippedFunctionCount++;
				continue;
			}

			if (!bodyPegProvider.canProvidePEG(body)) {
				logger.log("Function " + fullName + " contains label parameters, skipping");
				skippedFunctionCount++;
				continue;
			} 

			logger.log("Processing function " + fullName);
			
			try {
				optimizer.optimize(body);
				
				{// perform some block merging
					BlockMerger merger = new BlockMerger(body);
					merger.mergeBlocks();
					merger = null;
				}
				
				{// name the values in the function
					FunctionValueNamer namer = new DefaultFunctionValueNamer(body);
					namer.assignNames();
					namer = null;
				}
				funcLogger.log("Optimization of function " + fullName + " SUCCESSFUL");
			} catch (Throwable t) {
				buggyFunctionCount++;
				funcLogger.logException("Error processing function " + fullName, t);
				funcLogger.log("Reverting to original function body");
				funcLogger.log("Optimization of function " + fullName + " FAILED");
			} finally {
				deleteTempFiles();
			}

			if (isSingle) {
				// output the cost ratio
				SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> single = 
					(SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn>)optimizer;
				if (single.getOptimizationLevel().equals(SingleStageOptimizer.Level.RUN_ENGINE_FULL) &&
						!peg2pegData.getLastOriginal()) { 
					int newcost = calculateCost(peg2pegData.getLastRevertPeginfo(), stages.get(0));
					int oldcost = calculateCost(data.getLastOriginalPEG(), stages.get(0));
					funcLogger.log("Optimization ratio " + newcost + "/" + oldcost + " = " + ((double)newcost)/oldcost);
				}
			}
			//////////////
			
			logger.log("Done processing function " + fullName);
			System.gc();
		}
		logger.log("Done optimizing " + inputFile.getPath());
		
		logger.log("Final results:");
			funcLogger.log("Skipped methods = " + skippedFunctionCount);
			funcLogger.log("Buggy methods = " + buggyFunctionCount);
			funcLogger.log("Total methods = " + totalFunctionCount);

		try {
			File outputFile = new File(outputFolder, inputFile.getName());
			logger.log("Writing module back to " + outputFile.getPath());
			
			outputFile.getParentFile().mkdirs();
			
			FileOutputStream fout = new FileOutputStream(outputFile);
			BitcodeWriter writer = new BitcodeWriter();
			new ModuleEncoder(writer, module).writeModule();
			writer.dump(fout);
			fout.close();
		} catch (Throwable t) {
			logger.logException("Error writing module back to disk", t);
		}
		
		long endTotalTime = System.currentTimeMillis();
		logger.log("Total optimization time = " + (endTotalTime-startTotalTime) + " milliseconds");
	}
	

	private static ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> 
	getLooplessHeuristic(
			final CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> costModel,
			final ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> fallback) {
		return new LooplessReversionHeuristic2<LLVMLabel, LLVMParameter, LLVMReturn>() {
			protected ReversionHeuristic<LLVMLabel, LLVMParameter, LLVMReturn, Integer> getFallbackHeuristic() {
				return fallback;
			}
			public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
				return costModel;
			}
			public boolean isRevertible(FlowValue<LLVMParameter,LLVMLabel> flow) {
				if (flow.isDomain())
					return flow.getDomain().isRevertible();
				else
					return flow.isRevertable();
			}
		};
	}
	
	private static ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> 
	getNonPessimizingHeuristic(
			final CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> costModel,
			final ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> fallback) {
		return new NonPessimizingHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer>(
				fallback,
				TERM_TAG) {
			public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
				return costModel;
			}
		};
	}

	private static ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> 
	getReversionHeuristic(final int maxCost, StageInfo stage, final Logger logger) {
		final CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> costModel =
			stage.ruleParser.getCostModel();
		final int maxPBFileSize = stage.maxPBFileSize;
		
		ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> heuristic;
		switch (stage.pbOption) {
		case PUEBLO: {
			heuristic =  
				new DefaultPBReversionHeuristic<PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;
					}
					protected int getMaxCost() {return maxCost;}
					protected PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>> getFreshFormulation() {
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
							return new PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected PBRunner<CPEGTerm<LLVMLabel,LLVMParameter>,PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> getRunner() {
						PuebloRunner<CPEGTerm<LLVMLabel,LLVMParameter>> result = 
							new PuebloRunner<CPEGTerm<LLVMLabel,LLVMParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(PUEBLO_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									if (options.getBoolean(DELETE_PB_FILES))
										tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return (int)options.getLong(PB_TIMEOUT);}
							};
						return result;
					}
				};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			return getNonPessimizingHeuristic(costModel, heuristic);
		}
			
		case GLPK: {
			heuristic =  
				new DefaultGLPKReversionHeuristic () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected GLPKRunner<LLVMLabel,LLVMParameter> getRunner() {
						GLPKRunner<LLVMLabel,LLVMParameter> result = 
							new GLPKRunner<LLVMLabel,LLVMParameter>() {
								protected String getCommandPath() {
									return options.getString(GLPK_PATH);
								}
								protected int getTimeout() {return -1;}
							};
						return result;
					}
					protected int getMaxILPFileSize() {return maxPBFileSize;}
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
			return getNonPessimizingHeuristic(costModel, heuristic);
		}
		
		case MINISAT: {
			heuristic =  
				new DefaultPBReversionHeuristic<MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>> getFreshFormulation() {
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
							return new MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected int getMaxCost() {return maxCost;}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected PBRunner<CPEGTerm<LLVMLabel,LLVMParameter>,MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> getRunner() {
						MinisatRunner<CPEGTerm<LLVMLabel,LLVMParameter>> result = 
							new MinisatRunner<CPEGTerm<LLVMLabel,LLVMParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(MINISAT_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									if (options.getBoolean(DELETE_PB_FILES))
										tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return (int)options.getLong(PB_TIMEOUT);}
							};
						return result;
					}
				};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			return getNonPessimizingHeuristic(costModel, heuristic);
		}

		default:
			abort("Unknown PB solver: " + stage.pbOption);
			return null; // this won't happen, but is needed for control flow
		} 
	}

	
	
	private static ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> 
	getAllocaReversionHeuristic(final int maxCost, StageInfo stage, final Logger logger) {
		final CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> costModel =
			stage.ruleParser.getCostModel();
		final int maxPBFileSize = stage.maxPBFileSize;
		
		ReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn,Integer> heuristic;
		switch (stage.pbOption) {
		case PUEBLO: {
			heuristic =  
				new DefaultPBReversionHeuristic<PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;
					}
					protected int getMaxCost() {return maxCost;}
					protected PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>> getFreshFormulation() {
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
							return new PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> getNodeInclusionPattern() {
						final Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> oldpattern = super.getNodeInclusionPattern();
						return new AbstractPattern<CPEGTerm<LLVMLabel,LLVMParameter>>() {
							public boolean matches(CPEGTerm<LLVMLabel,LLVMParameter> label) {
								if (label.getOp().isDomain() &&
									label.getOp().getDomain().isSimple() &&
									label.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.ALLOCA)) {
									return false;
								}
								return oldpattern.matches(label);
							}
						};
					}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected PBRunner<CPEGTerm<LLVMLabel,LLVMParameter>,PuebloFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> getRunner() {
						PuebloRunner<CPEGTerm<LLVMLabel,LLVMParameter>> result = 
							new PuebloRunner<CPEGTerm<LLVMLabel,LLVMParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(PUEBLO_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									if (options.getBoolean(DELETE_PB_FILES))
										tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return (int)options.getLong(PB_TIMEOUT);}
							};
						return result;
					}
				};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			return heuristic;
		}
			
		case GLPK: {
			heuristic =  
				new DefaultGLPKReversionHeuristic () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected GLPKRunner<LLVMLabel,LLVMParameter> getRunner() {
						GLPKRunner<LLVMLabel,LLVMParameter> result = 
							new GLPKRunner<LLVMLabel,LLVMParameter>() {
								protected String getCommandPath() {
									return options.getString(GLPK_PATH);
								}
								protected int getTimeout() {return -1;}
							};
						return result;
					}
					protected Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> getNodeInclusionPattern() {
						final Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> oldpattern = super.getNodeInclusionPattern();
						return new AbstractPattern<CPEGTerm<LLVMLabel,LLVMParameter>>() {
							public boolean matches(CPEGTerm<LLVMLabel,LLVMParameter> label) {
								if (label.getOp().isDomain() &&
									label.getOp().getDomain().isSimple() &&
									label.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.ALLOCA)) {
									return false;
								}
								return oldpattern.matches(label);
							}
						};
					}
					protected int getMaxILPFileSize() {return maxPBFileSize;}
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
			return heuristic;
		}
				
		case MINISAT: {
			heuristic =  
				new DefaultPBReversionHeuristic<MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> () {
					public CostModel<CPEGTerm<LLVMLabel, LLVMParameter>, Integer> getCostModel() {
						return costModel;						
					}
					protected MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>> getFreshFormulation() {
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
							return new MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>(back, maxPBFileSize);
						} catch (IOException io) {
							throw new RuntimeException("Cannot create temp file", io);
						}
					}
					protected Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> getNodeInclusionPattern() {
						final Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> oldpattern = super.getNodeInclusionPattern();
						return new AbstractPattern<CPEGTerm<LLVMLabel,LLVMParameter>>() {
							public boolean matches(CPEGTerm<LLVMLabel,LLVMParameter> label) {
								if (label.getOp().isDomain() &&
									label.getOp().getDomain().isSimple() &&
									label.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.ALLOCA)) {
									return false;
								}
								return oldpattern.matches(label);
							}
						};
					}
					protected int getMaxCost() {return maxCost;}
					protected int getFormulationTimeout() {return (int)options.getLong(PB_TIMEOUT);}
					protected PBRunner<CPEGTerm<LLVMLabel,LLVMParameter>,MinisatFormulation<CPEGTerm<LLVMLabel,LLVMParameter>>> getRunner() {
						MinisatRunner<CPEGTerm<LLVMLabel,LLVMParameter>> result = 
							new MinisatRunner<CPEGTerm<LLVMLabel,LLVMParameter>>() {
								protected String getPBCommandPath() {
									return options.getString(MINISAT_PATH);
								}
								public File getPBOutputFile(File inputFile) {
									File output = new File(inputFile.getAbsolutePath() + ".output");
									if (options.getBoolean(DELETE_PB_FILES))
										tempFiles.add(output);
									return output;
								}
								public int getTimeout() {return (int)options.getLong(PB_TIMEOUT);}
							};
						return result;
					}
				};
			heuristic.setLogger(logger);
			if (stage.looplessReversion)
				heuristic = getLooplessHeuristic(costModel, heuristic);
			return heuristic;
		}

		default:
			abort("Unknown PB solver: " + stage.pbOption);
			return null; // this won't happen, but is needed for control flow
		} 
	}
	
	
	
	private static class PrintListener implements EventListener<Proof> {
		private String message;
		Logger logger = TOP_LOGGER.getSubLogger().getSubLogger();
		public PrintListener(String _message) {
			this.message = _message;
		}
		public boolean notify(Proof v) {
			if (options.getBoolean(DISPLAY_AXIOMS)) {
				String msg = message.trim().replaceAll("\\n[ \\t]*", "  ");
				logger.log("Applied axiom: " + msg);
			}
			return true;
		}
		public boolean canUse(Proof v) {return true;}
	}
	
	private static class PrintStructureListener 
	implements EventListener<Structure<CPEGTerm<LLVMLabel,LLVMParameter>>> {
		private String message;
		Logger logger = TOP_LOGGER.getSubLogger().getSubLogger();
		public PrintStructureListener(String _message) {
			this.message = _message;
		}
		public boolean notify(Structure<CPEGTerm<LLVMLabel,LLVMParameter>> v) {
			if (options.getBoolean(DISPLAY_AXIOMS)) {
				String msg = message.trim().replaceAll("\\n[ \\t]*", "  ");
				logger.log("Applied axiom: " + msg);
			}
			return true;
		}
		public boolean canUse(Structure<CPEGTerm<LLVMLabel,LLVMParameter>> v) {
			return true;
		}
	}
	
	private static class PrintStringListener implements EventListener<String> {
		private String message;
		Logger logger = TOP_LOGGER.getSubLogger().getSubLogger();
		public PrintStringListener(String _message) {
			this.message = _message;
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
			Module module,
			final StageInfo stage, 
			PeggyAxiomSetup<LLVMLabel,LLVMParameter> setup,
			final LLVMAliasAnalysis aliasAnalysis,
			Logger logger) {
		if (stage.activatedAnalyses.contains("inline")) {
			LLVMInliner inliner = new LLVMInliner(
					network, 
					setup.getEngine(),
					labelPegProvider,
					stage.ruleParser.getInlineHeuristic()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			};
			
			inliner.addInliningAxioms();

			logger.log("Activating inlining analysis");
		}
		
		// add the GEP removal analysis
		if (module != null &&
			!forcingPolicy.equals(GEPForcingPolicy.NONE) && 
			stage.activatedAnalyses.contains("gep")) {
			logger.log("Activating GEP removal analysis");
			
			DataLayout layout = getDataLayout(module);
			constantFolder.setDataLayout(layout);
			
			new GEPRemovalAnalysis(
					forcingPolicy, network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll(layout);
		}

		if (stage.activatedAnalyses.contains("folding")) {
			logger.log("Activating folding analysis");
			new LLVMConstantFoldingAnalysis(constantFolder, network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}
		
		if (stage.activatedAnalyses.contains("ops")) {// add the LLVM operator analyses
			logger.log("Activating op analysis");
			new LLVMOperatorAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}
		
		if (stage.activatedAnalyses.contains("select")) {// add the select analysis
			logger.log("Activating select analysis");
			new SelectAnalysis(network, setup.getEngine()) {
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
				protected void addStringListener(Event<String> event,
						String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
			}.addAll();
		}
		
		if (stage.activatedAnalyses.contains("constants")) {// add the constant analysis
			logger.log("Activating constant analysis");
			new LLVMConstantAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();

			new LLVMBinopConstantAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}
		
		if (stage.activatedAnalyses.contains("livsr")) {// add the LIVSR helper analysis
			logger.log("Activating livsr analysis");
			new LIVSRHelperAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}

		if (stage.activatedAnalyses.contains("intrinsic")) {
			logger.log("Activating intrinsic analysis");
			new LLVMIntrinsicAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}

		if (stage.activatedAnalyses.contains("loadstore")) {
			logger.log("Activating loadstore analysis");
			new LoadStoreAnalysis(network, setup.getEngine()) {
				protected boolean isStackPointer(CPEGValue<LLVMLabel, LLVMParameter> value) {
					return aliasAnalysis.isStackPointer(value);
				}
				protected boolean isNonStackPointer(
						CPEGValue<LLVMLabel, LLVMParameter> value) {
					return aliasAnalysis.isNonStackPointer(value);
				}
				protected boolean doesNotAlias(CPEGValue<LLVMLabel, LLVMParameter> left,
						CPEGValue<LLVMLabel, LLVMParameter> right) {
					return aliasAnalysis.doesNotAlias(left, right);
				}
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
				protected Collection<FunctionModifies> getFunctionModifies() {
					return stage.ruleParser.getFunctionModifies();
				}
			}.addAll();
		}
		
		if (stage.activatedAnalyses.contains("nonstack")) {// add nonstack function analysis
			logger.log("Activating nonstack analysis");

			NonstackFunctionAnalysis analysis = new NonstackFunctionAnalysis(
					network,
					stage.ruleParser.getSigmaInvariantFunctions(),
					setup.getEngine(),
					new FabricatingReferenceResolver(module, moduleProvider),					
					stage.activatedAnalyses.contains("loadstore")) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			};
			analysis.addAll(
					stage.ruleParser.getNonstackFunctions(),
					stage.ruleParser.getFunctionModifies());
		}
		
		if (module != null &&
			stage.activatedAnalyses.contains("global")) {// add global analysis
			logger.log("Activating global analysis");
			GlobalAnalysis analysis = new GlobalAnalysis(
					stage.activatedAnalyses.contains("loadstore"),
					new FabricatingReferenceResolver(module, moduleProvider), 
					network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			};
			analysis.addAll(stage.ruleParser.getAliasExpansions());
		}

		if (stage.activatedAnalyses.contains("libc")) {
			logger.log("Activating libc analysis");
			new LibCAnalysis(network, setup.getEngine()) {
				protected void addStringListener(
						Event<String> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintStringListener(message));
					}
				}
				protected void addProofListener(
						Event<? extends Proof> event, String message) {
					if (options.getBoolean(DISPLAY_AXIOMS)) {
						event.addListener(new PrintListener(message));
					}
				}
			}.addAll();
		}
		
		for (AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>> node : stage.axioms) {
			Event<? extends Proof> event = setup.getEngine().addPEGAxiom(node);
			if (node.hasTag(XMLRuleParser.NAME_TAG))
				event.addListener(new PrintListener(node.getTag(XMLRuleParser.NAME_TAG)));
		}

		AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector = 
			stage.ruleParser.getAxiomSelector();
		
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.BOOLEAN_AXIOMS)) {
			BooleanAxioms<LLVMLabel,LLVMParameter> axioms = 
				new BooleanAxioms<LLVMLabel,LLVMParameter>(setup);
			axioms.addNegateTrueIsFalse().addListener(new PrintListener("!T = F"));
			axioms.addNegateFalseIsTrue().addListener(new PrintListener("!F = T"));
			axioms.addNegateNegate().addListener(new PrintListener("!!B = B"));
			axioms.addOrSymmetric().addListener(new PrintListener("A|B = B|A"));
			axioms.addAndSymmetric().addListener(new PrintListener("A&B = B&A"));
			axioms.addOrTrueIsTrue().addListener(new PrintListener("A|T = T"));
			axioms.addOrFalseIsOther().addListener(new PrintListener("A|F = A"));
			axioms.addAndTrueIsOther().addListener(new PrintListener("A&T = A"));
			axioms.addAndFalseIsFalse().addListener(new PrintListener("A&F = F"));
		}
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EQUALITY_AXIOMS)) {
			EqualityAxioms<LLVMLabel,LLVMParameter> axioms = 
				new EqualityAxioms<LLVMLabel,LLVMParameter>(setup);
			axioms.addReflexiveEquals().addListener(new PrintListener("(X==X) = T"));
			axioms.addTrueEquals().addListener(new PrintListener("((X==Y)=T) => X=Y"));
		}
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.PHI_AXIOMS)) {
			PhiAxioms<LLVMLabel,LLVMParameter> axioms = 
				new PhiAxioms<LLVMLabel,LLVMParameter>(setup);
			axioms.addPhiTrueCondition().addListener(new PrintListener("phi(T,b,c) = b"));
			axioms.addPhiFalseCondition().addListener(new PrintListener("phi(F,b,c) = c"));
			axioms.addPhiNegateCondition().addListener(new PrintListener("phi(!a,b,c) = phi(a,c,b)"));
			axioms.addJoinPhi().addListener(new PrintListener("phi(a,b,b) = b"));
			axioms.addPhiTrueFalse().addListener(new PrintListener("phi(c,t,f) = c"));
			axioms.addPhiFalseTrue().addListener(new PrintListener("phi(c,f,t) = !c"));
			
			TemporaryPhiAxioms<LLVMLabel,LLVMParameter> tempaxioms = 
				new TemporaryPhiAxioms<LLVMLabel,LLVMParameter>(setup, 1);
			tempaxioms.addPhiOverPhiLeftAxiom().addListener(new PrintListener("phi(c,phi(c,t1,f1),f2) = phi(c,t1,f2)"));
			tempaxioms.addPhiOverPhiRightAxiom().addListener(new PrintListener("phi(c,t2,phi(c,t1,f1)) = phi(c,t2,f1)"));
			boolean[] bs = {true,false};
			for (boolean b1 : bs) {
				for (boolean b2 : bs) {
					tempaxioms.addPhi2Deep(b1,b2).addListener(
							new PrintListener("phi 2 deep (" + b1 + "," + b2 + ")"));
				}
			}
		}
		
		{
			LoopAxioms<LLVMLabel,LLVMParameter> loopaxioms = new LoopAxioms<LLVMLabel,LLVMParameter>(setup);
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL0_THETA_AXIOMS))
				loopaxioms.addEval0Theta().addListener(new PrintListener("invariant(b) => eval(theta(b, u),0) = b"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL_INVARIANT_AXIOMS))
				loopaxioms.addEvalInvariant().addListener(new PrintListener("invariant(x) => eval(x,i) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.EVAL_SUCC_SHIFT_AXIOMS))
				loopaxioms.addEvalSuccShift().addListener(new PrintListener("eval(x, succ(i)) = eval(shift(x), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.SHIFT_THETA_AXIOMS))
				loopaxioms.addShiftTheta().addListener(new PrintListener("shift(theta(b,u)) = u"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.SHIFT_INVARIANT_AXIOMS))
				loopaxioms.addShiftInvariant().addListener(new PrintListener("invariant(x) => shift(x) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.JOIN_THETA_AXIOMS))
				loopaxioms.addJoinTheta().addListener(new PrintListener("invariant(x) => theta(x,x) = x"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_SHIFT_AXIOMS))
				loopaxioms.addDistributeShift().addListener(new PrintStructureListener("distribute shift"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_EVAL_AXIOMS))
				loopaxioms.addDistributeEval().addListener(new PrintStructureListener("distribute eval"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THROUGH_EVAL_AXIOMS))
				loopaxioms.addDistributeThroughEval().addListener(new PrintStructureListener("distribute through eval"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THROUGH_THETA_AXIOMS))
				loopaxioms.addDistributeThroughTheta().addListener(new PrintStructureListener("distribute through theta"));
		}

		{
			LoopInteractionAxioms<LLVMLabel,LLVMParameter> interaxioms = 
				new LoopInteractionAxioms<LLVMLabel,LLVMParameter>(setup);
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THETA_THROUGH_EVAL1_AXIOMS))
				interaxioms.addDistributeThetaThroughEval1().addListener(new PrintListener("invariant_1(i) ^ invariant_2(u) => "
						+ "theta_1(eval_2(b, i), u) = eval_2(theta_1(b, u), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_THETA_THROUGH_EVAL2_AXIOMS))
				interaxioms.addDistributeThetaThroughEval2().addListener(new PrintListener("invariant_1(i) ^ invariant_2(b) => "
						+ "theta_1(b, eval_2(u, i)) = eval_2(theta_1(b, u), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_SHIFT_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributeShiftThroughEval().addListener(new PrintListener("shift_1(eval_2(x, i)) = eval_2(shift_1(x), shift_1(i))"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_PASS_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributePassThroughEval().addListener(new PrintListener("invariant_1(i) => "
						+ "pass_1(eval_2(c, i)) = eval_2(pass_1(c), i)"));
			if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.DISTRIBUTE_EVAL_THROUGH_EVAL_AXIOMS))
				interaxioms.addDistributeEvalThroughEval().addListener(new PrintListener("invariant_1(i2) ^ invariant_2(i1) => "
						+ "eval_1(eval_2(x, i2), i1) = eval_2(eval_1(x, i1), i2)"));
		}
	}

	
	
	
	private static void setupEngine(
			StageInfo stage,
			Module module,
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine,
			PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo,
			Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>> rootVertexMap,
			Logger logger) {
		PeggyAxiomSetup<LLVMLabel,LLVMParameter> setup = 
			new PeggyAxiomSetup<LLVMLabel,LLVMParameter>(network, ambassador, engine);
		LLVMAliasAnalysis aliasAnalysis = new LLVMAliasAnalysis(
				engine.getEGraph().getValueManager(), 
				options.getBoolean(PARAMS_DNA_NULL));
		addAxioms(module, stage, setup, aliasAnalysis, logger);
		
		// the list we're going to get representations for
		List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> vertices = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		for (LLVMReturn arr : peginfo.getReturns())
			roots.add(peginfo.getReturnVertex(arr));
		
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> v :
			 new CREGVertexIterable<LLVMLabel,LLVMParameter>(roots)) { 
			vertices.add(v);
		}
		
		List<? extends CPEGTerm<LLVMLabel,LLVMParameter>> reps = 
			engine.addExpressions(vertices);
		
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).setTag(TERM_TAG, reps.get(i));
		}

		if (stage.activatedAnalyses.contains("loadstore")) {
			aliasAnalysis.addAll(reps, vertices);
		} else {
			addGlobalDoesNotAliasInfo(engine);
			addAllocasDoNotAliasInfo(engine);
			addParamsNotStackPointerInfo(engine);
			addGlobalDoesNotAliasNullInfo(engine);
		}
		
		// map for the reverter (maps significant vertexes to their reps)
		for (int i = 0; i < vertices.size(); i++) {
			rootVertexMap.put(vertices.get(i), reps.get(i));
		}
	}
	
	
	/**
	 * For each param P of pointer type, adds 
	 * 	  True = !(stackPointer(P)) 
	 */
	private static void addParamsNotStackPointerInfo(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {

		List<CPEGTerm<LLVMLabel,LLVMParameter>> paramPointers = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isParameter() &&
					term.getOp().getParameter().isArgument()) {
					final Type paramType = term.getOp().getParameter().getArgumentSelf().getType();
					if (paramType.isComposite() && paramType.getCompositeSelf().isPointer())
						paramPointers.add(term);
				}
			}
		}

		
		for (CPEGTerm<LLVMLabel,LLVMParameter> param : paramPointers) {
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
				new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> stackPointer =
				futureGraph.getExpression(
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								new StringAnnotationLLVMLabel("stackPointer"),ambassador),
						futureGraph.getVertex(param));
			engine.getEGraph().addExpressions(futureGraph);
			
			Proof proof = null;
			if (options.getBoolean(ENABLE_PROOFS)) {
				proof = new Proof("Params are not stack pointers");
			
				// properties of pointer
				proof.addProperty(
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
								param, 
								param.getOp()));
				proof.addProperty(
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
								param, 0));
				// properties of stackPointer
				proof.addProperty(
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
								stackPointer.getTerm(),
								stackPointer.getTerm().getOp()));
				proof.addProperty(
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
								stackPointer.getTerm(), 1));
				proof.addProperty(
						new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
								stackPointer.getTerm(), 0, param));
			}

			engine.getEGraph().makeEqual(engine.getEGraph().getFalse(), stackPointer.getTerm(), proof);
		}
	}
	
	
	
	private static void addGlobalDoesNotAliasNullInfo(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		List<CPEGTerm<LLVMLabel,LLVMParameter>> globals = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		List<CPEGTerm<LLVMLabel,LLVMParameter>> nulls = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isDomain() && 
					term.getOp().getDomain().isGlobal())
					globals.add(term);
				else if (term.getOp().isDomain() && 
						 term.getOp().getDomain().isConstantValue() &&
						 term.getOp().getDomain().getConstantValueSelf().getValue().isConstantNullPointer())
					nulls.add(term);
			}
		}
		
		for (CPEGTerm<LLVMLabel,LLVMParameter> global : globals) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> nul : nulls) {
				FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
					new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
				FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> doesNotAlias = 
					futureGraph.getExpression(
							FlowValue.<LLVMParameter,LLVMLabel>createDomain(
									new StringAnnotationLLVMLabel("doesNotAlias"),
									ambassador),
							futureGraph.getVertex(global),
							futureGraph.getVertex(nul));
				engine.getEGraph().addExpressions(futureGraph);
				
				Proof proof = null;
				if (options.getBoolean(ENABLE_PROOFS)) {
					proof = new Proof("global does not alias null");
					
					// properties of global
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									global,
									global.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									global, global.getArity()));
					// properties of null
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									nul,
									nul.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									nul, nul.getArity()));
					// properties of doesNotAlias
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									doesNotAlias.getTerm(),
									doesNotAlias.getTerm().getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									doesNotAlias.getTerm(), 2));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									doesNotAlias.getTerm(), 0, global));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									doesNotAlias.getTerm(), 1, nul));
				}

				engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), doesNotAlias.getTerm(), proof);
			}
		}
	}
	
	
	/**
	 * For each distinct pair of globals (G1,G2), add 
	 *   doesNotAlias(G1,G2) = True
	 */
	private static void addGlobalDoesNotAliasInfo(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		List<CPEGTerm<LLVMLabel,LLVMParameter>> globals = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isDomain() && term.getOp().getDomain().isGlobal())
					globals.add(term);
			}
		}

		
		for (int i = 0; i < globals.size(); i++) {
			GlobalLLVMLabel gi = globals.get(i).getOp().getDomain().getGlobalSelf();
			for (int j = i+1; j < globals.size(); j++) {
				GlobalLLVMLabel gj = globals.get(j).getOp().getDomain().getGlobalSelf();
				if (!gi.equals(gj)) {// necessary?
					FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
						new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
					FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> doesNotAlias = 
						futureGraph.getExpression(
								FlowValue.<LLVMParameter,LLVMLabel>createDomain(
										new StringAnnotationLLVMLabel("doesNotAlias"),
										ambassador),
								futureGraph.getVertex(globals.get(i)),
								futureGraph.getVertex(globals.get(j)));
					engine.getEGraph().addExpressions(futureGraph);
					
					Proof proof = null;
					if (options.getBoolean(ENABLE_PROOFS)) {
						proof = new Proof("globals do not alias");
						// properties of global 1
						proof.addProperty(
								new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
										globals.get(i),
										globals.get(i).getOp()));
						proof.addProperty(
								new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
										globals.get(i), 0));
						// properties of global 2
						proof.addProperty(
								new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
										globals.get(j),
										globals.get(j).getOp()));
						proof.addProperty(
								new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
										globals.get(j), 0));
						// properties of doesNotAlias
						proof.addProperty(
								new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
										doesNotAlias.getTerm(),
										doesNotAlias.getTerm().getOp()));
						proof.addProperty(
								new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
										doesNotAlias.getTerm(), 2));
						proof.addProperty(
								new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
										doesNotAlias.getTerm(), 0, globals.get(i)));
						proof.addProperty(
								new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
										doesNotAlias.getTerm(), 1, globals.get(j)));
					}

					engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), doesNotAlias.getTerm(), proof);
				}
			}
		}
	}

	
	
	/**
	 * For each distinct pair of allocas (A1,A2), add 
	 *   doesNotAlias(rho_value(G1),rho_value(G2)) = True
	 */
	private static void addAllocasDoNotAliasInfo(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		List<CPEGTerm<LLVMLabel,LLVMParameter>> allocas = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isDomain() && 
					term.getOp().getDomain().isSimple() &&
					term.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.ALLOCA))
					allocas.add(term);
			}
		}

		final FlowValue<LLVMParameter,LLVMLabel> doesNotAlias = 
			FlowValue.<LLVMParameter,LLVMLabel>createDomain(
					new StringAnnotationLLVMLabel("doesNotAlias"), ambassador);
		final FlowValue<LLVMParameter,LLVMLabel> rho_value = 
			FlowValue.<LLVMParameter,LLVMLabel>createDomain(
					SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE), ambassador);
		
		for (int i = 0; i < allocas.size(); i++) {
			final CPEGTerm<LLVMLabel,LLVMParameter> ai = allocas.get(i);
			for (int j = i+1; j < allocas.size(); j++) {
				final CPEGTerm<LLVMLabel,LLVMParameter> aj = allocas.get(j);
				FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
					new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
				FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> rvI =  
					futureGraph.getExpression(rho_value, futureGraph.getVertex(ai));
				FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> rvJ =  
					futureGraph.getExpression(rho_value, futureGraph.getVertex(aj));
				FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> newExpression = 
					futureGraph.getExpression(
							doesNotAlias,
							rvI,
							rvJ);
				engine.getEGraph().addExpressions(futureGraph);

				Proof proof = null;
				if (options.getBoolean(ENABLE_PROOFS)) {
					proof = new Proof("allocas do not alias");
					
					// properties of alloca 1
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									ai, ai.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									ai, ai.getArity()));
					// properties of alloca 2
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									aj, aj.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									aj, aj.getArity()));
					// properties of rvI
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									rvI.getTerm(), rvI.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									rvI.getTerm(), rvI.getTerm().getArity()));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									rvI.getTerm(), 0, ai));
					// properties of rvJ
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									rvJ.getTerm(), rvJ.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									rvJ.getTerm(), rvJ.getTerm().getArity()));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									rvJ.getTerm(), 0, aj));
					// properties of doesNotAlias
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									newExpression.getTerm(),
									newExpression.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									newExpression.getTerm(), 
									newExpression.getTerm().getArity()));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									newExpression.getTerm(), 0, rvI.getTerm()));
					proof.addProperty(
							new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
									newExpression.getTerm(), 1, rvJ.getTerm()));
				}

				engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), newExpression.getTerm(), proof);
			}
		}
	}
	
	
	private static DataLayout getDataLayout(Module module) {
		switch (dataLayoutSource) {
		case EXPLICIT: {
			try {
				return new DataLayout(options.getString(DATALAYOUT_EXPLICIT));
			} catch (Throwable t) {
				abort("Cannot parse explicit data layout string", t);
				throw null;
			}
		}
			
		case DEFAULT:
			return new DataLayout();

		case MODULE: {
			if (module.getDataLayout() == null) {
				return new DataLayout();
			} else {
				try {
					return new DataLayout(module.getDataLayout());
				} catch (Throwable t) {
					abort("Cannot parse module data layout string", t);
					throw null;
				}
			}
		}
		default:
			throw new RuntimeException("Didn't handle: " + dataLayoutSource);
		}
	}
	
	private static LLVMPEGCFG getOutputCFG(
			Module module,
			FunctionBody body, 
			RevertCFG revert) {
		LLVMPEGCFG outcfg = new LLVMPEGCFG(revert);
		return outcfg;
	}
	
	private static void encodeCFG(Module module, LLVMPEGCFG cfg, FunctionBody body) {
		LLVMPEGCFGEncoder encoder = new LLVMPEGCFGEncoder(
				cfg, 
				body,
				ambassador,
				new FabricatingReferenceResolver(module, moduleProvider));
		encoder.encode();
	}
	
	
	private static DotPEG2PEGListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>
	getDotPEG2PEGListener(StageInfo stage, final String prefix, final ReferenceResolver resolver) {
		return new DotPEG2PEGListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>(
				stage.OUTPUT_EPEG, stage.OUTPUT_OPTIMAL_PEG) {
			private String getFileName(String type, FunctionBody body) {
				return prefix + type + resolver.getFunctionName(body.getHeader()) + ".dot";
			}
			protected String getEPEGFilename(FunctionBody body) {
				return getFileName("EPEG_", body);
			}
			protected String getOPTPEGFilename(FunctionBody body) {
				return getFileName("OPTPEG_", body);
			}
		};
	}
	
	private static PEG2PEGListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>
	getOutputPEG2PEGListener(final Logger logger) {
		return new PEG2PEGListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>() {
			public void beginFunction(FunctionBody body) {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine,
					Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,CPEGTerm<LLVMLabel,LLVMParameter>> rootVertexMap) {
				logger.log("Running engine");
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
				logger.log("Building optimal PEG");
			}
			public void notifyRevertPEGBuilt(
					boolean original,
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo) {
				if (original)
					logger.log("Original PEG chosen as output");
				logger.log("Building reversion graph");
			}
			public void endFunction() {}
		};
	}

	
	private static PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> 
	getPEG2PEGOptimizer(final StageInfo stage, final Module module, final Logger logger) {
		PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> result = 
			new PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>() { 
			PEG2PEGLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> data =
				new PEG2PEGLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> ();
			
			// constructor
			{this.addListener(data);}
			
			protected EngineRunner<LLVMLabel,LLVMParameter> getEngineRunner() {return stage.engineRunner;}
			protected OpAmbassador<LLVMLabel> getOpAmbassador() {return ambassador;}
			protected PEGExtractor<LLVMLabel,LLVMParameter,LLVMReturn> getExtractor() {
				return new DefaultPEGExtractor<LLVMLabel,LLVMParameter,LLVMReturn>(
						TERM_TAG,
						(stage.allocaStage ?
						 getAllocaReversionHeuristic(-1, stage, getLogger()) :
						 getReversionHeuristic(-1, stage, getLogger())));
			}
			protected void setupEngine(
					FunctionBody body,					
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine,
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo,
					Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>> rootVertexMap) {
				debug("PEG2PEGOptimizer.setupEngine");
				Main.setupEngine(stage, module, engine, peginfo, rootVertexMap, logger);
			}
		};
		// TODO add prefix
		result.addListener(getDotPEG2PEGListener(stage, "", 
				new DefaultReferenceResolver(module)));
		result.addListener(getOutputPEG2PEGListener(logger.getSubLogger()));
		result.setLogger(logger);
		return result;
	}
	

	
	private static Optimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> 
	getOptimizer(Module module, Logger logger) {
		debug("getOptimizer");
		if (stages.size() == 1) {
			return getSingleStageOptimizer(module, logger);
		} else {
			return getMultiStageOptimizer(module, logger);
		}
	}
	private static MultiStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> 
	getMultiStageOptimizer(final Module module, final Logger logger) {
		List<PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>> optimizers = 
			new ArrayList<PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody>>(stages.size());
		for (int i = 0; i < stages.size(); i++) {
			StageInfo stage = stages.get(i);
			PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> p2p = 
				getPEG2PEGOptimizer(stage, module, logger); 
			optimizers.add(p2p);
			
			p2p.addListener(getOutputPEG2PEGListener(logger.getSubLogger()));
			p2p.addListener(getDotPEG2PEGListener(stage, "multi_" + i + "_",
					new DefaultReferenceResolver(module)));
		}
		
		MultiStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> optimizer = 
			new MultiStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn>(optimizers) {
			protected OpAmbassador<LLVMLabel> getOpAmbassador() {return ambassador;}
			protected PEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> getPEGProvider() {return bodyPegProvider;}
			protected LLVMPEGCFG getOutputCFG(
					FunctionBody body, 
					RevertCFG<LLVMLabel,LLVMParameter,LLVMReturn> revert) {
				return Main.getOutputCFG(module, body, revert);
			}
			protected boolean canOptimize(FunctionBody body) {
				return getPEGProvider().canProvidePEG(body);
			}
			protected void encodeCFG(LLVMPEGCFG cfg, FunctionBody body) {
				Main.encodeCFG(module, cfg, body);
			}
		};
		
		// TODO
		optimizer.addListener(getDotListener("multi_", new DefaultReferenceResolver(module)));
		optimizer.addListener(getOutputListener(logger.getSubLogger()));
		
		return optimizer;
	}
	
	
	private static DotOptimizerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody>
	getDotListener(final String prefix, final ReferenceResolver resolver) {
		return new DotOptimizerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody>(
				options.getBoolean(OUTPUT_ORIGINAL_PEG), false, options.getBoolean(OUTPUT_REVERT_GRAPH), options.getBoolean(OUTPUT_REVERT_CFG), options.getBoolean(OUTPUT_OUTPUT_CFG)) {
			private String getFileName(String type, FunctionBody body) {
				return prefix + type + resolver.getFunctionName(body.getHeader()) + ".dot";
			}
			protected String getPEGFilename(FunctionBody body) {
				return getFileName("PEG_", body);
			}
			protected String getOPTPEGFilename(FunctionBody body) {return null;}
			protected String getRevertFilename(FunctionBody body) {
				return getFileName("REVERT_", body);
			}
			protected String getRevertCFGFilename(FunctionBody body) {
				return getFileName("REVCFG_", body);
			}
			protected String getOutputCFGFilename(FunctionBody body) {
				return getFileName("OUTCFG_", body);
			}
		};
	}

	private static OptimizerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody>
	getOutputListener(final Logger logger) {
		return new OptimizerListener<LLVMLabel,LLVMParameter,LLVMReturn,LLVMPEGCFG,FunctionBody>() {
			public void beginFunction(FunctionBody body) {
				logger.log("Building original PEG");
			}
			public void notifyOriginalPEGBuilt(
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo) {
				logger.log("Setting up engine");
			}
			public void notifyOptimalPEGBuilt(
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo) {
			}
			public void notifyReversionGraphBuilt(
					ReversionGraph<LLVMParameter,LLVMLabel> result) {
				logger.log("Building revert CFG");
			}
			public void notifyCFGReverterBuilt(
					CFGReverter<LLVMParameter,LLVMLabel,LLVMReturn> reverter) {
				logger.log("Building output CFG");
			}
			public void notifyOutputCFGBuilt(LLVMPEGCFG cfg) {
				logger.log("Encoding output CFG");
			}
			public void endFunction() {
				logger.log("Optimization completed");
			}
		};
	}
	
	
	private static SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> 
	getSingleStageOptimizer(final Module module, final Logger logger) {
		PEG2PEGOptimizer<LLVMLabel,LLVMParameter,LLVMReturn,FunctionBody> p2p = 
			getPEG2PEGOptimizer(stages.get(0), module, logger);
		
		SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> optimizer = 
			new SingleStageOptimizer<LLVMPEGCFG,FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn>(p2p) {
			protected OpAmbassador<LLVMLabel> getOpAmbassador() {return ambassador;}
			protected PEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> getPEGProvider() {return bodyPegProvider;}
			protected LLVMPEGCFG getOutputCFG(
					FunctionBody body, 
					RevertCFG<LLVMLabel,LLVMParameter,LLVMReturn> revert) {
				return Main.getOutputCFG(module, body, revert);
			}
			protected boolean canOptimize(FunctionBody body) {
				return getPEGProvider().canProvidePEG(body);
			}
			protected void encodeCFG(LLVMPEGCFG cfg, FunctionBody body) {
				Main.encodeCFG(module, cfg, body);
			}
			protected PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> sanitizePEG(
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> input) {
				return input;
			}
		};
		optimizer.setOptimizationLevel(optimizationLevel);
		
		optimizer.addListener(getDotListener("single", new DefaultReferenceResolver(module)));
		optimizer.addListener(getOutputListener(logger.getSubLogger()));
		
		return optimizer;
	}
	
	private static Module setup(Logger logger) {
		abortIf(moduleInputFile == null, "No input file specified");
		
		// build OpAmbassador
		ambassador = new LLVMOpAmbassador(
				constantFolder, 
				forcingPolicy, 
				options.getBoolean(USE_CFG_EXCEPTIONS), 
				true);
		
		// build rule parsers
		for (StageInfo stage : stages) {
			stage.ruleParser = new peggy.input.llvm.LLVMXMLRuleParser(
					null, network, ambassador); 
		
			// read axiom files
			for (File axiomFile : stage.axiomFiles) {
				try {
					stage.axioms.addAll(stage.ruleParser.parseRuleSet(axiomFile));
					logger.log("Successfully added axiom file: " + axiomFile.getPath());
				} catch (Throwable t) {
					abort("Error parsing axiom file: " + axiomFile.getPath(), t);
				}
			}
		}

		debug("loading module file");
		
		logger.log("Loading module file " + moduleInputFile.getPath());
		
		// read the module file
		Module module = null;
		try {
			module = moduleProvider.addAndLoadModuleFile(moduleInputFile);
			debug("Loaded module");
		} catch(Throwable t) {
			abort("Error loading module " + moduleInputFile.getPath(), t);
		}

		return module;
	}
	
	private static int getEngineValueCount(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		return engine.getEGraph().getValueManager().getValues().size();
	}
	private static int getEngineTermCount(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		int terms = 0;
		for (CPEGValue<LLVMLabel,LLVMParameter> value : 
			engine.getEGraph().getValueManager().getValues()) {
			terms += value.getTerms().size();
		}
		return terms;
	}
	private static int[] getMatchingThetaStats(
		CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		List<List<CPEGTerm<LLVMLabel,LLVMParameter>>> thetas = 
			new ArrayList<List<CPEGTerm<LLVMLabel,LLVMParameter>>>();
		for (CPEGValue<LLVMLabel,LLVMParameter> value : 
			engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isTheta()) {
					int depth = term.getOp().getLoopDepth();
					while (depth >= thetas.size()) {
						thetas.add(new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>());
					}
					thetas.get(depth).add(term);
				}
			}
		}
		
		int[] counts = new int[thetas.size()];
		for (int d = 0; d < thetas.size(); d++) {
			List<CPEGTerm<LLVMLabel,LLVMParameter>> thetaD = thetas.get(d);
			for (int i = 0; i < thetaD.size(); i++) {
				CPEGValue<LLVMLabel,LLVMParameter> ichild = 
					thetaD.get(i).getChild(0).getValue();
				for (int j = i+1; j < thetaD.size(); j++) {
					if (ichild.equals(thetaD.get(j).getChild(0).getValue())) {
						counts[d]++;
					}
				}
			}
		}
		
		return counts;
	}
	
	
	public static void main(String args[]) {
		// process the arguments
		if (args.length == 0)
			displayHelp();
		
		try {
			optionsParser.parse(args);
		} catch (OptionParsingException ex) {
			abort("Error parsing: " + ex.getMessage());
		}

		Module module = setup(TOP_LOGGER);
		optimizeModule(
				module, 
				moduleInputFile, 
				options.getFile(OUTPUT_FOLDER), 
				skippedFunctions,
				TOP_LOGGER);
		
		System.exit(0);
	}
}
