package peggy.tv.llvm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.DataLayout;
import llvm.bitcode.FabricatingReferenceResolver;
import llvm.bitcode.FunctionComparator;
import llvm.bitcode.ReferenceResolver;
import llvm.instructions.Cast;
import llvm.instructions.FunctionBody;
import llvm.types.Type;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantNullPointerValue;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FloatingPointValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.IntegerValue;
import llvm.values.Module;
import llvm.values.Value;
import peggy.Logger;
import peggy.OptionParsingException;
import peggy.OptionsParser;
import peggy.analysis.BoundedEngineRunner;
import peggy.analysis.CREGVertexIterable;
import peggy.analysis.DynamicPhiCollapser;
import peggy.analysis.EPEGTypeAnalysis;
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
import peggy.analysis.llvm.LLVMEPEGTypeAnalysis;
import peggy.analysis.llvm.LLVMInliner;
import peggy.analysis.llvm.LLVMIntrinsicAnalysis;
import peggy.analysis.llvm.LLVMOperatorAnalysis;
import peggy.analysis.llvm.LLVMPhiCollapserAnalysis;
import peggy.analysis.llvm.LibCAnalysis;
import peggy.analysis.llvm.LoadStoreAnalysis;
import peggy.analysis.llvm.NonstackFunctionAnalysis;
import peggy.analysis.llvm.SelectAnalysis;
import peggy.analysis.llvm.TypeBasedAnalysis;
import peggy.analysis.llvm.types.LLVMType;
import peggy.input.EPEGLayout;
import peggy.input.GraphPrinter;
import peggy.input.XMLRuleParser;
import peggy.pb.ConfigurableCostModel;
import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import peggy.represent.PEGMerger;
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
import peggy.represent.llvm.ModuleProvider2_8;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import peggy.tv.DotTVListener;
import peggy.tv.TVLastDataListener;
import peggy.tv.TVListener;
import peggy.tv.TVTimerListener;
import peggy.tv.TranslationValidator;
import util.Action;
import util.Function;
import util.NamedTag;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import util.pair.PairedList;
import eqsat.FlowValue;
import eqsat.engine.AxiomSelector;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.AreEquivalent;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.engine.proof.ProofManager;
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
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This class is the command-line interface for the LLVM TV engine.
 * A description of the command-line parameters is displayed when you give
 * the "-help" option.
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
			
			// run type analysis
			if (activatedAnalyses.contains("typebased")) {
				typeAnalysis.run(); // TODO uncomment!
			}
			
			if (options.getBoolean(DYNAMIC_PHI_COLLAPSER)) {
				if (dynamicPhiCollapser != null) {
					dynamicPhiCollapser.run();
				}
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
		Set<String> stringPairKeys = options.getStringPairKeys();
		
		keys.addAll(boolKeys);
		keys.addAll(longKeys);
		keys.addAll(stringKeys);
		keys.addAll(stringPairKeys);
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
			else if (stringPairKeys.contains(opt)) {
				System.err.printf(format, "-" + opt + " <string1> <string2>", options.getDescription(opt)); 
			}
			else {
				System.err.printf(format, "-" + opt, optionsParser.getCommandDescription(opt)); 
			}
		}
		System.exit(0);
	}
	
	
	
	private static final Options options = new Options();
	private static final OptionsParser optionsParser = new OptionsParser(options);
	private static final String NON_LINEAR_LOADS = "tvNonlinearLoads";
	private static final String PRINT_PROOFS = "printProofs";
	private static final String REMOVE_ALLOCA_POINT = "removeAllocaPoint";
	private static final String THETA_MERGER_TIMEOUT = "thetaMergerTimeout"; 
	private static final String SKIP_EQUIVALENT = "skipEquivalent";
	private static final String DEBUG_EPEG = "debugepeg";
	private static final String DEBUG_PEG = "debugpeg";
	private static final String DEBUG_MERGED = "debugmerged";
	private static final String MERGE_THETAS = "mergeThetas";
	private static final String STATIC_ALLOCA_REMOVER = "sar";
	private static final String USE_CFG_EXCEPTIONS = "exceptions";
	private static final String DISPLAY_AXIOMS = "displayAxioms";
	private static final String OUTPUT_ORIGINAL_PEG = "oop";
	private static final String OUTPUT_EPEG = "oep";
	private static final String PARAMS_DNA_NULL = "paramsDNANull";
	private static final String USE_2_8_BITCODE = "v2.8";
	private static final String COLLAPSE_PHIS = "collapsePhis";
	private static final String PEG_NODE_THRESHOLD = "pegMax";
	private static final String DYNAMIC_PHI_COLLAPSER = "dynamicPhiCollapser";
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
		options.registerLong(PEG_NODE_THRESHOLD, -1L,
				"Specify a maximum number of PEG nodes before a function will be skipped (default no max)", null);
		options.registerBoolean(DYNAMIC_PHI_COLLAPSER, false,
				"Set to true to periodically collapse phi nodes in the EPEG");
		options.registerBoolean(COLLAPSE_PHIS, false,
				"Set to true to statically collapse phi nodes on the PEG");
		options.registerBoolean(USE_2_8_BITCODE, false, 
				"Set to true to parse LLVM v2.8 bitcode files, else v2.3 (default 2.3)",
				new Action<Boolean>() {
					public void execute(Boolean value) {
						if (value) {
							moduleProvider = new ModuleProvider2_8();
						} else {
							moduleProvider = new ModuleProvider();
						}
					}
				});
		
		options.registerBoolean(PARAMS_DNA_NULL, false, 
				"Set to true to add info that says pointer parameters do not alias NULL");
		options.registerStringPair("tv", null, null,
				"Specify the pair of functions to validate, as 'func1:module1' 'func2:module2'",
				new Action<Pair<String,String>>() {
					public void execute(Pair<String,String> pair) {
						int firstIndex = pair.getFirst().indexOf(":");
						int secondIndex = pair.getSecond().indexOf(":");
						if (!(firstIndex >= 0 && secondIndex >= 0))
							throw new OptionParsingException("No ':' found in TV pairs");
						tvBefore = new Pair<String,String>(
								pair.getFirst().substring(0, firstIndex),
								pair.getFirst().substring(firstIndex+1));
						tvAfter = new Pair<String,String>(
								pair.getSecond().substring(0, secondIndex),
								pair.getSecond().substring(secondIndex+1));
					}
				});
		options.registerString("datalayout:explicit", null,
				"Specify an explicit LLVM data layout",
				new Action<String>() {
					public void execute(String str) {
						dataLayoutSource = DataLayoutSource.EXPLICIT;
						explicitDataLayout = str;
					}
				});
		options.registerString("modulePath", null,
				"Specify a colon-separated list of module files to load functions from",
				new Action<String>() {
					public void execute(String str) {
						String[] modules = str.split(":");
						for (String module : modules) {
							module = module.trim();
							if (!module.equals(""))
								moduleProviderFiles.add(new File(module));
						}
					}
				});
		options.registerBoolean(OUTPUT_ORIGINAL_PEG, false,
				"Set to true to output a dot graph of the original PEGs (default false)");
		options.registerBoolean(OUTPUT_EPEG, false,
				"Set to true to output a dot graph of the final EPEG (default false)");
		optionsParser.registerCommand("datalayout:default",
				"Specify that LLVM should use the default data layout",
				new Runnable() {
					public void run() {
						dataLayoutSource = DataLayoutSource.DEFAULT;
					}
				});
		optionsParser.registerCommand("datalayout:module",
				"Specify that LLVM should use the module's data layout (default)",
				new Runnable() {
					public void run() {
						dataLayoutSource = DataLayoutSource.MODULE;
					}
				});
		options.registerString("exf", null,
				"Specify the name of a file with names of functions to exclude",
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
							throw new OptionParsingException("Error reading file", t);
						}
					}
				});
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
		optionsParser.registerCommand("gep64", 
				"Specify that all GEP offsets should be forced to 64 bits",
				new Runnable() {
					public void run() {
						forcingPolicy = GEPForcingPolicy.FORCE_64;
					}
				});
		
		optionsParser.registerCommand("gep32", 
				"Specify that all GEP offsets should be forced to 32 bits",
				new Runnable() {
					public void run() {
						forcingPolicy = GEPForcingPolicy.FORCE_32;
					}
				});
		options.registerLong("maxmemory", 0L,
				"Specify the maximum amount of memory that the engine may use",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setMemoryUpperBound(value);
					}
				});
		options.registerLong("eto", 1000L,
				"Specify the maximum number of iterations that the engine may run (default 1000)",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setIterationUpperBound(value);
					}
				});
		options.registerLong("maxtime", 0L,
				"Specify the maximum number of milliseconds that the engine may run",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setTimeUpperBound(value);
					}
				});
		options.registerBoolean(DISPLAY_AXIOMS, false,
				"Set to true to display the axioms used by the engine (default false)");
		options.registerBoolean(STATIC_ALLOCA_REMOVER, false,
				"Set to true to use the static alloca remover (default false)");
		options.registerBoolean(USE_CFG_EXCEPTIONS, false,
				"Set to true to represent exceptions in the PEG/EPEG (default false)");
		options.registerLong("mergeTimeUpdate", 0L,
				"Specify how much time in between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setTimeUpdate(value);
					}
				});
		options.registerLong("mergeIterationUpdate", 0L,
				"Specify how many iterations in between runs of the theta merger",
				new Action<Long>() {
					public void execute(Long value) {
						engineRunner.setIterationUpdate(value);
					}
				});
		options.registerBoolean(MERGE_THETAS, true,
				"Set to true to enable the theta merger during validation (default true)");
		options.registerBoolean(DEBUG_EPEG, false,
				"Set to true to display a debug dialog for the EPEG (default false)");
		options.registerBoolean(DEBUG_MERGED, false,
				"Set to true to print dot graph of merged PEG for debugging (default false)");
		options.registerBoolean(DEBUG_PEG, false,
				"Set to true to print a dot graph of the original EPG for debugging (default false)");
		options.registerBoolean(SKIP_EQUIVALENT, true,
				"Set to true to skip function pairs that are syntactically equivalent (default true)");
		options.registerLong(THETA_MERGER_TIMEOUT, 0L,
				"Specify the maximum time (in milliseconds) that the theta merger can run", null);
		options.registerBoolean(REMOVE_ALLOCA_POINT, false,
				"Set to true to remove the %\"alloca point\" variable when testing for syntactic equality (default false)");
		options.registerBoolean(PRINT_PROOFS, false,
				"Set to true to print out the proof of equivalence to a file (default false)");
		options.registerBoolean(NON_LINEAR_LOADS, false, 
				"Set to true to ignore linearity for read-only ops (loads) (default false)");
	}
	
	private static LLVMEPEGTypeAnalysis typeAnalysis;
	private static DynamicPhiCollapser<LLVMLabel,LLVMParameter> dynamicPhiCollapser;
	private static final Tag<CPEGTerm<LLVMLabel,LLVMParameter>> TERM_TAG = 
		new NamedTag<CPEGTerm<LLVMLabel,LLVMParameter>>("Tags vertices with their terms");
	
	private static peggy.input.llvm.LLVMXMLRuleParser ruleParser;
	private static final Collection<AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>>> axioms = 
		new ArrayList<AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>>>(100);
	private static final Set<String> activatedAnalyses = new HashSet<String>();
	private static final Set<File> axiomFiles = new HashSet<File>();
	private static final MyEngineRunner engineRunner = new MyEngineRunner();
	private static final Logger TOP_LOGGER = new MyLogger();
	private static Pair<String,String> tvBefore = null, tvAfter = null; // <funcname, modulepath>
	private static final Set<String> skippedFunctions = new HashSet<String>();
	private static Network network = new PeggyAxiomNetwork<LLVMLabel>(new Network());
	private static LLVMOpAmbassador ambassador;
	private static DataLayoutSource dataLayoutSource = DataLayoutSource.MODULE;
	private static String explicitDataLayout = null;
	private static GEPForcingPolicy forcingPolicy = GEPForcingPolicy.NONE;
	private static final DefaultLLVMConstantFolder constantFolder = 
		new DefaultLLVMConstantFolder(new DataLayout());
	private static ModuleProvider moduleProvider = new ModuleProvider();
	private static final Set<File> moduleProviderFiles = new HashSet<File>();
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
				PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result = pegprovider.getPEG(function);
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

				PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> result = 
					pegprovider.getPEG(lookup(module, function));
				return result;
			}
		};
	
	
	private static void checkOption(boolean condition, String errorMessage) {
		if (!condition) {
			System.err.println(errorMessage);
			System.exit(1);
		}
	}

	///////////////////////////////////////////////

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

	private static int calculateCost(
			Collection<? extends CPEGTerm<LLVMLabel,LLVMParameter>> terms) {
		int cost = 0;
		ConfigurableCostModel<FlowValue<LLVMParameter,LLVMLabel>,FunctionLLVMLabel,CPEGTerm<LLVMLabel,LLVMParameter>,Integer>
		costModel = ruleParser.getCostModel();
		for (CPEGTerm<LLVMLabel,LLVMParameter> term : terms) {
			cost += costModel.cost(term);
		}
		return cost;
	}
	private static int calculateCost(
			PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> taggedPEG) {
		Set<CPEGTerm<LLVMLabel,LLVMParameter>> terms = 
			new HashSet<CPEGTerm<LLVMLabel,LLVMParameter>>();
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : taggedPEG.getGraph().getVertices()) {
			terms.add(vertex.getTag(TERM_TAG));
		}
		return calculateCost(terms);
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
			PeggyAxiomSetup<LLVMLabel,LLVMParameter> setup,
			final LLVMAliasAnalysis aliasAnalysis,
			Logger logger) {
		if (activatedAnalyses.contains("inline")) {
			LLVMInliner inliner = new LLVMInliner(
					network, 
					setup.getEngine(),
					labelPegProvider,
					ruleParser.getInlineHeuristic()) {
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
			inliner.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			
			inliner.addInliningAxioms();

			logger.log("Activating inlining analysis");
		}
		
		// add the GEP removal analysis
		if (module != null &&
			!forcingPolicy.equals(GEPForcingPolicy.NONE) && 
			activatedAnalyses.contains("gep")) {
			logger.log("Activating GEP removal analysis");
			
			DataLayout layout = getDataLayout(module);
			constantFolder.setDataLayout(layout);
			
			GEPRemovalAnalysis analysis = new GEPRemovalAnalysis(
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll(layout);
		}

		if (activatedAnalyses.contains("folding")) {
			logger.log("Activating folding analysis");
			LLVMConstantFoldingAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}

		if (activatedAnalyses.contains("typebased")) {
			logger.log("Activating typebased analysis");
			TypeBasedAnalysis analysis = 
				new TypeBasedAnalysis(network, setup.getEngine()) {
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
				protected EPEGTypeAnalysis<LLVMLabel, LLVMParameter, LLVMType> getTypeAnalysis() {
					return typeAnalysis;
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}
		
		if (activatedAnalyses.contains("ops")) {// add the LLVM operator analyses
			logger.log("Activating op analysis");
			LLVMOperatorAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}
		
		if (activatedAnalyses.contains("select")) {// add the select analysis
			logger.log("Activating select analysis");
			SelectAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}
		
		if (activatedAnalyses.contains("constants")) {// add the constant analysis
			logger.log("Activating constant analysis");
			LLVMConstantAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();

			LLVMBinopConstantAnalysis analysis2 = 
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
			};
			analysis2.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis2.addAll();
		}
		
		if (activatedAnalyses.contains("livsr")) {// add the LIVSR helper analysis
			logger.log("Activating livsr analysis");
			LIVSRHelperAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}

		if (activatedAnalyses.contains("intrinsic")) {
			logger.log("Activating intrinsic analysis");
			LLVMIntrinsicAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}

		if (activatedAnalyses.contains("loadstore")) {
			logger.log("Activating loadstore analysis");
			LoadStoreAnalysis analysis = 
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
					return ruleParser.getFunctionModifies();
				}
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}
		
		if (activatedAnalyses.contains("nonstack")) {// add nonstack function analysis
			logger.log("Activating nonstack analysis");

			NonstackFunctionAnalysis analysis = new NonstackFunctionAnalysis(
					network,
					ruleParser.getSigmaInvariantFunctions(),
					setup.getEngine(),
					new FabricatingReferenceResolver(module, moduleProvider),
					activatedAnalyses.contains("loadstore")) {
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
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll(
					ruleParser.getNonstackFunctions(),
					ruleParser.getFunctionModifies());
		}
		
		if (module != null &&
			activatedAnalyses.contains("global")) {// add global analysis
			logger.log("Activating global analysis");
			GlobalAnalysis analysis = new GlobalAnalysis(
					activatedAnalyses.contains("loadstore"),
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
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll(ruleParser.getAliasExpansions());
		}
		
		if (activatedAnalyses.contains("libc")) {
			logger.log("Activating libc analysis");
			LibCAnalysis analysis = 
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
			};
			analysis.setProofsEnabled(options.getBoolean(ENABLE_PROOFS));
			analysis.addAll();
		}
		
		for (AxiomNode<LLVMLabel,? extends PEGNode<LLVMLabel>> node : axioms) {
			Event<? extends Proof> event = setup.getEngine().addPEGAxiom(node);
			if (node.hasTag(XMLRuleParser.NAME_TAG))
				event.addListener(new PrintListener(node.getTag(XMLRuleParser.NAME_TAG)));
		}

		AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector = 
			ruleParser.getAxiomSelector();
		
		if (selector.isEnabled(eqsat.meminfer.peggy.axiom.AxiomGroup.BOOLEAN_AXIOMS)) {
			BooleanAxioms<LLVMLabel,LLVMParameter> axioms = 
				new BooleanAxioms<LLVMLabel,LLVMParameter>(setup);
			axioms.addNegateTrueIsFalse().addListener(new PrintListener("!T = F"));
			axioms.addNegateFalseIsTrue().addListener(new PrintListener("!F = T"));
			axioms.addNegateNegate().addListener(new PrintListener("!!B = B"));
//			axioms.addOrSymmetric().addListener(new PrintListener("A|B = B|A"));
//			axioms.addAndSymmetric().addListener(new PrintListener("A&B = B&A"));
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
	
	private static void addGlobalConstantHasBits(
			Module module,
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		// find sigma
		CPEGTerm<LLVMLabel,LLVMParameter> sigma = null;
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isParameter() && 
					term.getOp().getParameter().isSigma()) {
					sigma = term;
					break;
				}
			}
		}
		if (sigma==null)
			return;

		// find constant globals
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isDomain() &&
					term.getOp().getDomain().isGlobal()) {
					GlobalLLVMLabel global = term.getOp().getDomain().getGlobalSelf();
					ReferenceResolver resolver = new FabricatingReferenceResolver(
							module, moduleProvider);
					GlobalVariable gv = resolver.resolveGlobal(global.getName(), global.getType());
					if (!(gv.isConstant() && gv.getInitialValue()!=null))
						continue;
					Value initialValue = gv.getInitialValue();
					List<Boolean> bits = getGlobalConstantBits(initialValue);
					if (bits==null)
						continue;
					// got a good global! add hasBits

					FlowValue<LLVMParameter,LLVMLabel> hasBitsFlow = 
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								new StringAnnotationLLVMLabel("hasBits"),
								ambassador);
					FlowValue<LLVMParameter,LLVMLabel> bitlabel = 
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								new TypeBasedAnalysis.ConcreteBitLabel(
										bits.toArray(new Boolean[0])),
								ambassador);
					
					FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
						new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
					FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> hasBits = 
						futureGraph.getExpression(
								hasBitsFlow,
								futureGraph.getVertex(sigma),
								futureGraph.getVertex(term),
								futureGraph.getExpression(bitlabel));
					engine.getEGraph().addExpressions(futureGraph);
					
					Proof proof = null;
					if (options.getBoolean(ENABLE_PROOFS)) {
						proof = new Proof("Constant global has bits on sigma");
						proof.addProperties(
								new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(term, term.getOp()),
								new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(term, term.getArity()),
								new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(sigma, sigma.getOp()),
								new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(sigma, sigma.getArity()));
					}
					engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), hasBits.getTerm(), proof);
				}
			}
		}
	}
	// returns null if a nonliteral is included
	private static List<Boolean> getGlobalConstantBits(Value value) {
		if (value.isConstantArray()) {
			ConstantArrayValue cv = value.getConstantArraySelf();
			List<Boolean> bits = new ArrayList<Boolean>();
			int numElements = (int)cv.getNumElements().signedValue();
			for (int i = 0; i < numElements; i++) {
				List<Boolean> subbits = getGlobalConstantBits(cv.getElement(i));
				if (subbits == null) return null;
				bits.addAll(subbits);
			}
			return bits;
		} else if (value.isConstantNullPointer()) {
			ConstantNullPointerValue cp = value.getConstantNullPointerSelf();
			int numBits = (int)cp.getType().getTypeSize();
			List<Boolean> bits = new ArrayList<Boolean>(numBits);
			for (int i = 0; i < numBits; i++)
				bits.add(false);
			return bits;
		} else if (value.isConstantStructure()) {
			ConstantStructureValue cs = value.getConstantStructureSelf();
			List<Boolean> bits = new ArrayList<Boolean>();
			for (int i = 0; i < cs.getNumFields(); i++) {
				List<Boolean> subbits = getGlobalConstantBits(cs.getFieldValue(i));
				if (subbits == null) return null;
				bits.addAll(subbits);
			}
			return bits;
		} else if (value.isConstantVector()) {
			ConstantVectorValue cv = value.getConstantVectorSelf();
			List<Boolean> bits = new ArrayList<Boolean>();
			int numElements = (int)cv.getNumElements().signedValue();
			for (int i = 0; i < numElements; i++) {
				List<Boolean> subbits = getGlobalConstantBits(cv.getElement(i));
				if (subbits == null) return null;
				bits.addAll(subbits);
			}
			return bits;
		} else if (value.isFloatingPoint()) {
			FloatingPointValue f = value.getFloatingPointSelf();
			List<Boolean> bits = new ArrayList<Boolean>();
			int numBits = f.getType().getKind().getTypeSize();
			for (int i = 0; i < numBits; i++)
				bits.add(f.getBit(i));
			return bits;
		} else if (value.isInteger()) {
			IntegerValue iv = value.getIntegerSelf();
			List<Boolean> bits = new ArrayList<Boolean>();
			for (int i = 0; i < iv.getWidth(); i++) {
				bits.add(iv.getBit(i));
			}
			return bits;
		} else if (value.isUndef()) {
			int numBits = (int)value.getType().getTypeSize();
			List<Boolean> bits = new ArrayList<Boolean>(numBits);
			for (int i = 0; i < numBits; i++)
				bits.add(false);
			return bits;
		} else {
			return null;
		}
	}

	private static void addGEPBitcastParamIsDerived(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		Function<CPEGValue<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> findPP = 
			new Function<CPEGValue<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> () {
				public CPEGTerm<LLVMLabel, LLVMParameter> get(
						CPEGValue<LLVMLabel, LLVMParameter> value) {
					for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
						if (term.getOp().isParameter() &&
							term.getOp().getParameter().isArgument()) {
							Type type = term.getOp().getParameter().getArgumentSelf().getType();
							if (type.isComposite() && type.getCompositeSelf().isPointer()) {
								return term;
							}
						}
					}
					return null;
				}
			};
		Function<CPEGValue<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> findPT = 
			new Function<CPEGValue<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> () {
				public CPEGTerm<LLVMLabel, LLVMParameter> get(
						CPEGValue<LLVMLabel, LLVMParameter> value) {
					for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
						if (term.getOp().isDomain() && 
							term.getOp().getDomain().isType()) {
							Type type = term.getOp().getDomain().getTypeSelf().getType();
							if (type.isComposite() && type.getCompositeSelf().isPointer())
								return term;
						}
					}
					return null;
				}
			};
			
		Map<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> gep2param = 
			new HashMap<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>();
		Map<CPEGTerm<LLVMLabel,LLVMParameter>,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>> bitcast2info =  
			new HashMap<CPEGTerm<LLVMLabel,LLVMParameter>,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>>();

		// find geps and bitcasts on top of the pointer params
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (!term.getOp().isDomain())
					continue;
				final LLVMLabel label = term.getOp().getDomain();
				if (label.isCast() && 
					label.getCastSelf().getOperator().equals(Cast.Bitcast)) {
					CPEGTerm<LLVMLabel,LLVMParameter> paramTerm = 
						findPP.get(term.getChild(1).getValue());
					CPEGTerm<LLVMLabel,LLVMParameter> typeTerm = 
						findPT.get(term.getChild(0).getValue());
					if (paramTerm != null && typeTerm != null) {
						bitcast2info.put(
								term,
								new Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>(typeTerm, paramTerm));
						continue;
					}
				} 
				else if (label.isSimple() && 
						 label.getSimpleSelf().getOperator().equals(LLVMOperator.GETELEMENTPTR)) {
					CPEGTerm<LLVMLabel,LLVMParameter> paramTerm = 
						findPP.get(term.getChild(0).getValue());
					if (paramTerm != null) {
						gep2param.put(term, paramTerm);
						continue;
					}
				}
			}
		}
		
		// now add to the EPEG
		for (CPEGTerm<LLVMLabel,LLVMParameter> gep : gep2param.keySet()) {
			/*
			 * G:gep(P:param,*,*)
			 * ==>
			 * derivedPointer(G,P)
			 */
			final CPEGTerm<LLVMLabel,LLVMParameter> param = gep2param.get(gep);
			
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
				new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> derivedPointer = 
				futureGraph.getExpression(
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								new StringAnnotationLLVMLabel("derivedPointer"),
								ambassador),
						futureGraph.getVertex(gep),
						futureGraph.getVertex(param));
			engine.getEGraph().addExpressions(futureGraph);
			
			Proof proof = null;
			if (options.getBoolean(ENABLE_PROOFS)) {
				proof = new Proof("GEP of param is derived");
				proof.addProperties(
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(gep, gep.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(gep, gep.getArity()),
						new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(gep, 0, param),
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(param, param.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(param, param.getArity()));
			}
			
			engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), derivedPointer.getTerm(), proof);
		}
		
		// now add to the EPEG
		for (CPEGTerm<LLVMLabel,LLVMParameter> bitcast : bitcast2info.keySet()) {
			/*
			 * B:bitcast(T,P:param)
			 * ==>
			 * derivedPointer(B,P)
			 */
			CPEGTerm<LLVMLabel,LLVMParameter> typeTerm, paramTerm;
			{
				Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>> pair = 
					bitcast2info.get(bitcast);
				typeTerm = pair.getFirst();
				paramTerm = pair.getSecond();
			}
			
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
				new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
			FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> derivedPointer = 
				futureGraph.getExpression(
						FlowValue.<LLVMParameter,LLVMLabel>createDomain(
								new StringAnnotationLLVMLabel("derivedPointer"),
								ambassador),
						futureGraph.getVertex(bitcast),
						futureGraph.getVertex(paramTerm));
			engine.getEGraph().addExpressions(futureGraph);
			
			Proof proof = null;
			if (options.getBoolean(ENABLE_PROOFS)) {
				proof = new Proof("Bitcast of param is derived");
				proof.addProperties(
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(bitcast, bitcast.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(bitcast, bitcast.getArity()),
						new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(bitcast, 0, typeTerm),
						new ChildIsEquivalentTo<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(bitcast, 1, paramTerm),
						
						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(typeTerm, typeTerm.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(typeTerm, typeTerm.getArity()),

						new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(paramTerm, paramTerm.getOp()),
						new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(paramTerm, paramTerm.getArity()));
			}
			
			engine.getEGraph().makeEqual(engine.getEGraph().getTrue(), derivedPointer.getTerm(), proof);
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
	
	private static void addParamsDNANullInfo(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		List<CPEGTerm<LLVMLabel,LLVMParameter>> params = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		List<CPEGTerm<LLVMLabel,LLVMParameter>> nulls = 
			new ArrayList<CPEGTerm<LLVMLabel,LLVMParameter>>();
		
		for (CPEGValue<LLVMLabel,LLVMParameter> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
				if (term.getOp().isParameter() && 
					term.getOp().getParameter().isArgument()) {
					Type type = term.getOp().getParameter().getArgumentSelf().getType();
					if (type.isComposite() && type.getCompositeSelf().isPointer())
						params.add(term);
				} else if (term.getOp().isDomain() && 
						 term.getOp().getDomain().isConstantValue() &&
						 term.getOp().getDomain().getConstantValueSelf().getValue().isConstantNullPointer())
					nulls.add(term);
			}
		}
		
		for (CPEGTerm<LLVMLabel,LLVMParameter> param : params) {
			for (CPEGTerm<LLVMLabel,LLVMParameter> nul : nulls) {
				FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph = 
					new FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>>();
				FutureExpression<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> doesNotAlias = 
					futureGraph.getExpression(
							FlowValue.<LLVMParameter,LLVMLabel>createDomain(
									new StringAnnotationLLVMLabel("doesNotAlias"),
									ambassador),
							futureGraph.getVertex(param),
							futureGraph.getVertex(nul));
				engine.getEGraph().addExpressions(futureGraph);
				
				Proof proof = null;
				if (options.getBoolean(ENABLE_PROOFS)) {
					proof = new Proof("pointer param does not alias null");
					// properties of global
					proof.addProperty(
							new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
									param,
									param.getOp()));
					proof.addProperty(
							new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
									param, param.getArity()));
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
									doesNotAlias.getTerm(), 0, param));
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
				return new DataLayout(explicitDataLayout);
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
	
	
	private static Pair<Module,Module> setupTV(Logger logger) {
		// build op ambassador
		ambassador = 
			new LLVMOpAmbassador(
					constantFolder, 
					forcingPolicy, 
					options.getBoolean(USE_CFG_EXCEPTIONS),
					!options.getBoolean(NON_LINEAR_LOADS));
		
		// build rule parsers
		ruleParser = new peggy.input.llvm.LLVMXMLRuleParser(
				null, network, ambassador); 
		
		// read axiom files
		for (File axiomFile : axiomFiles) {
			try {
				axioms.addAll(ruleParser.parseRuleSet(axiomFile));
				logger.log("Successfully added axiom file: " + axiomFile.getPath());
			} catch (Throwable t) {
				abort("Error parsing axiom file: " + axiomFile.getPath(), t);
			}
		}
		
		for (File file : moduleProviderFiles) {
			moduleProvider.addModuleFile(file);
		}

		debug("loading module files");
		
		File beforeModulePath = new File(tvBefore.getSecond());
		logger.log("Loading before module file " + beforeModulePath.getPath());
		// read the module file
		Module beforeModule = null;
		try {
			beforeModule = moduleProvider.addAndLoadModuleFile(beforeModulePath);
			debug("Loaded before module");
		} catch(Throwable t) {
			abort("Error loading module " + beforeModulePath.getPath(), t);
		}

		File afterModulePath = new File(tvAfter.getSecond());
		logger.log("Loading after module file " + afterModulePath.getPath());
		Module afterModule = null;
		try {
			afterModule = moduleProvider.addAndLoadModuleFile(afterModulePath);
			debug("Loaded after module");
		} catch(Throwable t) {
			abort("Error loading module " + afterModulePath.getPath(), t);
		}

		return new Pair<Module,Module>(beforeModule, afterModule);
	}
	
	

	private static void setupTVEngine(
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine,
			MergedPEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo,
			Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>> rootVertexMap,
			Module beforeModule, Module afterModule,
			Logger logger) {
		PeggyAxiomSetup<LLVMLabel,LLVMParameter> setup = 
			new PeggyAxiomSetup<LLVMLabel,LLVMParameter>(network, ambassador, engine);
		LLVMAliasAnalysis aliasAnalysis = new LLVMAliasAnalysis(
					engine.getEGraph().getValueManager(), 
					options.getBoolean(PARAMS_DNA_NULL));
		addAxioms(beforeModule, setup, aliasAnalysis, logger);
		
		// the list we're going to get representations for
		List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> vertices = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		for (LLVMReturn arr : peginfo.getReturns()) {
			roots.add(peginfo.getReturnVertex1(arr));
			roots.add(peginfo.getReturnVertex2(arr));
		}
		
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> v :
			 new CREGVertexIterable<LLVMLabel,LLVMParameter>(roots)) { 
			vertices.add(v);
		}
		
		List<? extends CPEGTerm<LLVMLabel,LLVMParameter>> reps = 
			engine.addExpressions(vertices);

		// map for the reverter (maps significant vertexes to their reps)
		for (int i = 0; i < vertices.size(); i++)
			rootVertexMap.put(vertices.get(i), reps.get(i));
		
		if (activatedAnalyses.contains("loadstore")) {
			aliasAnalysis.addAll(reps, vertices);
		} else {
			if (options.getBoolean(PARAMS_DNA_NULL))
				addParamsDNANullInfo(engine);
			addGlobalDoesNotAliasInfo(engine);
			addAllocasDoNotAliasInfo(engine);
			addParamsNotStackPointerInfo(engine);
			addGlobalDoesNotAliasNullInfo(engine);
			addGEPBitcastParamIsDerived(engine);
			addGlobalConstantHasBits(beforeModule, engine);
		}
	}
	
	
	private static LLVMTranslationValidator
	getTranslationValidator(final Module before, final Module after, final Logger extlogger) {
		LLVMTranslationValidator tv = 
			new LLVMTranslationValidator() {
				public EngineRunner<LLVMLabel,LLVMParameter> getEngineRunner() {return engineRunner;}
				protected CPeggyAxiomEngine<LLVMLabel,LLVMParameter> createEngine(
						MergedPEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> mergedpeginfo,
						Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>> rootVertexMap) {
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine = 
						options.getBoolean(ENABLE_PROOFS) ?
						new CPeggyAxiomEngine<LLVMLabel,LLVMParameter>(ambassador) :
						new CPeggyAxiomEngine<LLVMLabel,LLVMParameter>(ambassador, null);
					
					typeAnalysis = new LLVMEPEGTypeAnalysis(engine.getEGraph());
					typeAnalysis.setCurrentMethod(this.getCurrentMethod());
						
					dynamicPhiCollapser = 
						new DynamicPhiCollapser<LLVMLabel,LLVMParameter>(network, engine) {
						protected void addStringListener(
								Event<String> event, String message) {
						}
						protected void addProofListener(
								Event<? extends Proof> event, String message) {
						}
					};
					Main.setupTVEngine(engine, mergedpeginfo, rootVertexMap, before, after, extlogger);
					
					if (activatedAnalyses.contains("typebased")) {
						// do an initial run of the type analysis
						typeAnalysis.run();
					}
					
					return engine;
				}
				protected MergedPEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> mergePEGs(
						PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo1,
						PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo2) {
					if (options.getBoolean(COLLAPSE_PHIS)) {
						LLVMPhiCollapserAnalysis analysis = 
							new LLVMPhiCollapserAnalysis(8);
						peginfo1 = analysis.collapsePhis(peginfo1);
						peginfo2 = analysis.collapsePhis(peginfo2);
						
						if (options.getBoolean(OUTPUT_ORIGINAL_PEG)) {
							try {
								PrintStream out = new PrintStream(new FileOutputStream("collapsed1.dot"));
								out.println(peginfo1.getGraph());
								out.close();
							} catch (Throwable t) {}
							try {
								PrintStream out = new PrintStream(new FileOutputStream("collapsed2.dot"));
								out.println(peginfo2.getGraph());
								out.close();
							} catch (Throwable t) {}
						}
					}
					
					PEGMerger<LLVMLabel,LLVMParameter,LLVMReturn> merger = 
						new PEGMerger<LLVMLabel, LLVMParameter, LLVMReturn>(peginfo1, peginfo2) {
							protected boolean equalConstants(
									FlowValue<LLVMParameter, LLVMLabel> left,
									FlowValue<LLVMParameter, LLVMLabel> right) {
								return left.equals(right);
							}
						};
					return merger.mergePEGs();
				}
				protected void enginePostPass(CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
					if (options.getBoolean(MERGE_THETAS)) {
						EngineThetaMerger<LLVMLabel,LLVMParameter> merger = 
							new EngineThetaMerger<LLVMLabel, LLVMParameter>(engine);
						// output engine stats
						extlogger.log("ENGINEVALUES " + getEngineValueCount(engine));			
						extlogger.log("ENGINETERMS " + getEngineTermCount(engine));
						extlogger.log("THETASTATS " + Arrays.toString(getMatchingThetaStats(engine)));

						merger.setTimeout(options.getLong(THETA_MERGER_TIMEOUT));
						merger.setLogger(extlogger);
						merger.mergeThetas();
					}
					
					if (options.getBoolean(DYNAMIC_PHI_COLLAPSER)) {
						dynamicPhiCollapser.run();
					}
				}
			};
		tv.setLogger(extlogger);
		return tv;
	}
	
	
	private static TVListener<LLVMLabel,LLVMParameter,LLVMReturn>
	getOutputTVListener(final Logger logger) {
		return new TVListener<LLVMLabel,LLVMParameter,LLVMReturn>() {
			public void beginValidation(
					String method1, 
					String method2,
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo1,
					PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo2) {
				logger.log("Beginning validation of " + method1 + " and " + method2);
			}
			public void notifyMergedPEGBuilt(MergedPEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> merged) {
				logger.log("Built merged PEG");
			}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine,
					Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,CPEGTerm<LLVMLabel,LLVMParameter>> _rootVertexMap) {							
				logger.log("Engine setup complete");
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
				logger.log("Engine finished running");
			}
			public void notifyReturnsEqual(
					LLVMReturn arr,
					CPEGTerm<LLVMLabel,LLVMParameter> root1,
					CPEGTerm<LLVMLabel,LLVMParameter> root2) {
				if (arr.equals(LLVMReturn.SIGMA)) {
					logger.log("Sigma roots validated");
				} else if (arr.equals(LLVMReturn.VALUE)) {
					logger.log("Value roots validated");
				} else {
					throw new IllegalArgumentException("Unknown return: " + arr);
				}
			}
			public void endValidation() {
				logger.log("Validation completed");
			}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {
				logger.log("Merged PEG roots already equal, skipping engine");
			}
		};
	}
	

	private static DotTVListener<LLVMLabel,LLVMParameter,LLVMReturn> 
	getDotTVListener(
			final String prefix,
			final String suffix) {
		return new DotTVListener<LLVMLabel,LLVMParameter,LLVMReturn>(
				options.getBoolean(OUTPUT_ORIGINAL_PEG), options.getBoolean(OUTPUT_ORIGINAL_PEG), options.getBoolean(OUTPUT_EPEG)) {
			protected String getOriginalPEG1Filename() {return prefix + "peg1." + suffix + ".dot";}
			protected String getOriginalPEG2Filename() {return prefix + "peg2." + suffix + ".dot";}
			protected String getEPEGFilename() {return prefix + "epeg." + suffix + ".dot";}
			protected String getMergedPEGFilename() {return prefix + "merged." + suffix + ".dot";}
		};
	}
	
	private static int getMergedPEGSize(
			MergedPEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> merged) {
		int count = 0;
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : 
			new CREGVertexIterable<LLVMLabel, LLVMParameter>(merged.getAllReturnVertices())) {
			count++;
		}
		return count;
	}
	private static int getPEGSize(
			PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo) {
		int count = 0;
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : 
			new CREGVertexIterable<LLVMLabel, LLVMParameter>(peginfo.getReturnVertices())) {
			count++;
		}
		return count;
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
	
	private static TVListener<LLVMLabel,LLVMParameter,LLVMReturn> getStatPrintListener(
			final Logger logger) {
		return new TVListener<LLVMLabel,LLVMParameter,LLVMReturn>() {
			public void beginValidation(String functionName1,
					String functionName2,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo1,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo2) {
				logger.log("PEGNODES1 " + getPEGSize(peginfo1));
				logger.log("PEGNODES2 " + getPEGSize(peginfo2));
			}
			public void endValidation() {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine,
					Map<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, CPEGTerm<LLVMLabel, LLVMParameter>> rootVertexMap) {}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {}
			public void notifyMergedPEGBuilt(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {
				logger.log("MERGEDPEGNODES " + getMergedPEGSize(merged));
			}
			public void notifyReturnsEqual(LLVMReturn arr,
					CPEGTerm<LLVMLabel, LLVMParameter> root1,
					CPEGTerm<LLVMLabel, LLVMParameter> root2) {}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
		};
	}
	
	private static TVListener<LLVMLabel,LLVMParameter,LLVMReturn> getHaltListener(
			final TranslationValidator<LLVMLabel,LLVMParameter,LLVMReturn> tv) {
		return new TVListener<LLVMLabel, LLVMParameter, LLVMReturn>() {
			Set<LLVMReturn> returns = null;
			public void beginValidation(String functionName1,
					String functionName2,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo1,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo2) {
				returns = new HashSet<LLVMReturn>(peginfo1.getReturns());
			}
			public void endValidation() {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine,
					Map<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, CPEGTerm<LLVMLabel, LLVMParameter>> rootVertexMap) {}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {}
			public void notifyMergedPEGBuilt(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
			public void notifyReturnsEqual(LLVMReturn arr,
					CPEGTerm<LLVMLabel, LLVMParameter> root1,
					CPEGTerm<LLVMLabel, LLVMParameter> root2) {
				returns.remove(arr);
				if (returns.size() == 0)
					tv.getEngineRunner().halt();
			}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
		};
	}


	public static <T extends Term<T,V>,V extends eqsat.meminfer.engine.basic.Value<T,V>> 
	void printProof(
			ProofManager<T,V> manager,
			PrintStream out,
			Set<Proof> seen,
			Proof proof) {

		if (proof == null)
			return;
		if (seen.contains(proof))
			return;
		seen.add(proof);
		
		// print axiom name
		out.println(proof.getAxiomName());
		
		// recurse on certain properties
		for (Property p : proof.getProperties()) {
			if (p instanceof AreEquivalent) {
				AreEquivalent<T,V> ae = 
					(AreEquivalent)p;
				PairedList<TermOrTermChild<T,V>,Proof> pairs =
					manager.getProofPath(ae.getLeft(), ae.getRight());
				for (int i = 0; i < pairs.size(); i++) {
					printProof(manager, out, seen, pairs.getSecond(i));
				}
			} else if (p instanceof ChildIsEquivalentTo) {
				ChildIsEquivalentTo<T,V> ciet = 
					(ChildIsEquivalentTo)p;
				TermChild<T,V> tc = 
					 new TermChild<T,V>(ciet.getParentTerm(), ciet.getChild());
				PairedList<TermOrTermChild<T,V>,Proof> pairs =
					manager.getProofPath(tc, ciet.getTerm());
				for (int i = 0; i < pairs.size(); i++) {
					printProof(manager, out, seen, pairs.getSecond(i));
				}
			} else if (p instanceof EquivalentChildren) {
				EquivalentChildren<T,V> ec = 
					(EquivalentChildren)p;
				PairedList<TermOrTermChild<T,V>,Proof> pairs =
					manager.getProofPath(ec.getLeft(), ec.getRight());
				for (int i = 0; i < pairs.size(); i++) {
					printProof(manager, out, seen, pairs.getSecond(i));
				}
			}
		}
	}
	

	private static TVListener<LLVMLabel,LLVMParameter,LLVMReturn> getProofListener(
			final String suffix) {
		return new TVListener<LLVMLabel, LLVMParameter, LLVMReturn>() {
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine;
			public void beginValidation(String functionName1,
					String functionName2,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo1,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo2) {}
			public void endValidation() {}
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine,
					Map<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, CPEGTerm<LLVMLabel, LLVMParameter>> rootVertexMap) {
				this.engine = _engine;
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {}
			public void notifyMergedPEGBuilt(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
			public void notifyReturnsEqual(LLVMReturn arr,
					CPEGTerm<LLVMLabel, LLVMParameter> root1,
					CPEGTerm<LLVMLabel, LLVMParameter> root2) {
				PairedList<TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>,Proof> pairs = 
					engine.getEGraph().getProofManager().getProofPath(root1, root2);
				try {
					PrintStream out = new PrintStream(new FileOutputStream("proof." + arr.toString().toLowerCase() + "." + suffix));
					Set<Proof> seen = new HashSet<Proof>();
					for (int i = 0; i < pairs.size(); i++) {
						printProof(engine.getEGraph().getProofManager(), out, seen, pairs.getSecond(i));
					}
					out.close();
				} catch (Throwable t) {}
			}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
		};
	}
	
	
	
	private static TVListener<LLVMLabel,LLVMParameter,LLVMReturn> getDebugListener(
			final String suffix,
			final TranslationValidator<LLVMLabel,LLVMParameter,LLVMReturn> tv) {
		return new TVListener<LLVMLabel,LLVMParameter,LLVMReturn>() {
			Set<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots = 
				new HashSet<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
			
			public void beginValidation(String functionName1,
					String functionName2,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo1,
					PEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> peginfo2) {
				if (options.getBoolean(DEBUG_PEG)) {
					for (LLVMReturn arr : peginfo1.getReturns()) {
						try {
							PrintStream out = new PrintStream(
									new FileOutputStream("peg1_" + arr.toString().toLowerCase() + "." + suffix + ".dot"));
							GraphPrinter.<LLVMLabel,LLVMParameter>printRootPairDot(
									out,
									peginfo1.getReturnVertex(arr),
									peginfo1.getReturnVertex(arr));
						} catch (Throwable t) {}
					}
					for (LLVMReturn arr : peginfo2.getReturns()) {
						try {
							PrintStream out = new PrintStream(
									new FileOutputStream("peg2_" + arr.toString().toLowerCase() + "." + suffix + ".dot"));
							GraphPrinter.<LLVMLabel,LLVMParameter>printRootPairDot(
									out,
									peginfo2.getReturnVertex(arr),
									peginfo2.getReturnVertex(arr));
						} catch (Throwable t) {}
					}
//					Set<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots1 = 
//						new HashSet<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
//					for (LLVMReturn arr : peginfo1.getReturns()) {
//						roots1.add(peginfo1.getReturnVertex(arr));
//					}
//
//					Set<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots2 = 
//						new HashSet<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
//					for (LLVMReturn arr : peginfo2.getReturns()) {
//						roots2.add(peginfo2.getReturnVertex(arr));
//					}
//
//					Layout.run(new PEGLayout<LLVMLabel,LLVMParameter,LLVMReturn>(roots1, false));
//					Layout.run(new PEGLayout<LLVMLabel,LLVMParameter,LLVMReturn>(roots2, false));
				}
			}
			public void endValidation() {}
			private Map<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, CPEGTerm<LLVMLabel, LLVMParameter>> myMap;
			public void notifyEngineSetup(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine,
					Map<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, CPEGTerm<LLVMLabel, LLVMParameter>> rootVertexMap) {
				myMap = rootVertexMap;
			}
			public void notifyEngineCompleted(
					CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {
				Set<CPEGValue<LLVMLabel,LLVMParameter>> rootValues = 
					new HashSet<CPEGValue<LLVMLabel,LLVMParameter>>();
				if (options.getBoolean(DEBUG_EPEG)) {
					for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> r : roots) {
						rootValues.add(myMap.get(r).getValue());
					}
 					EPEGLayout.<LLVMLabel,LLVMParameter>showEPEG(engine, rootValues, false);
				}
			}
			public void notifyMergedPEGBuilt(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {
				roots.clear();
				for (LLVMReturn arr : merged.getReturns()) {
					roots.add(merged.getReturnVertex1(arr));
					roots.add(merged.getReturnVertex2(arr));
				}
				
				if (options.getBoolean(DEBUG_MERGED)) {
					for (LLVMReturn arr : merged.getReturns()) {
						try {
							PrintStream out = new PrintStream(
									new FileOutputStream("merged_" + arr.toString().toLowerCase() + "." + suffix + ".dot"));
							GraphPrinter.<LLVMLabel,LLVMParameter>printRootPairDot(
									out,
									merged.getReturnVertex1(arr),
									merged.getReturnVertex2(arr));
						} catch (Throwable t) {}
					}
				}
			}
			public void notifyReturnsEqual(LLVMReturn arr,
					CPEGTerm<LLVMLabel, LLVMParameter> root1,
					CPEGTerm<LLVMLabel, LLVMParameter> root2) {}
			public void notifyMergedPEGEqual(
					MergedPEGInfo<LLVMLabel, LLVMParameter, LLVMReturn> merged) {}
		};
	}
	
	private static void performTranslationValidation(
			Logger logger,
			final LLVMTranslationValidator tv, 
			String name1, FunctionBody method1, 
			String name2, FunctionBody method2) {
		if (!bodyPegProvider.canProvidePEG(method1))
			abort("Cannot build PEG from method: " + name1);
		if (!bodyPegProvider.canProvidePEG(method2))
			abort("Cannot build PEG from method: " + name2);

		final Logger tvLogger = logger.getSubLogger();
		
		TVTimerListener<LLVMLabel,LLVMParameter,LLVMReturn> timer = 
			new TVTimerListener<LLVMLabel,LLVMParameter,LLVMReturn> ();
		TVLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn> lastData = 
			new TVLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn> ();
		
		tv.addListener(lastData);
		tv.addListener(getDotTVListener("tv_", name1));
		tv.addListener(getOutputTVListener(tvLogger));
		tv.addListener(getStatPrintListener(tvLogger));
		tv.addListener(timer);
		tv.addListener(getDebugListener(name1, tv));
		if (options.getBoolean(ENABLE_PROOFS) && 
			options.getBoolean(PRINT_PROOFS))
			tv.addListener(getProofListener(name1));
		tv.addListener(getHaltListener(tv));
		
		long beforeMakePEGs = System.currentTimeMillis();
		PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo1 = null, peginfo2 = null;
		
		try {
			peginfo1 = bodyPegProvider.getPEG(method1);
			peginfo2 = bodyPegProvider.getPEG(method2);
		} catch (Throwable t) {
			abort("Cannot build PEGs", t);
		}
		long afterMakePEGs = System.currentTimeMillis();
		
		int numNodes1 = getPEGSize(peginfo1);
		int numNodes2 = getPEGSize(peginfo2);
		
		tvLogger.log("CFG2PEGTIME " + (afterMakePEGs-beforeMakePEGs));
		
		if (options.getLong(PEG_NODE_THRESHOLD) > 0 && 
			(numNodes1 > options.getLong(PEG_NODE_THRESHOLD) ||
			 numNodes2 > options.getLong(PEG_NODE_THRESHOLD))) {
			tvLogger.log("PEG node count exceeds threshold, skipping");
			return;
		}
		
		tv.setCurrentMethod(method1);
		
		tv.validate(
				name1, name2,
				peginfo1, peginfo2);
		
		if (lastData.getLastMergedEqual()) {
			// engine did not run
			tvLogger.log("Validate entire optimization!");
			tvLogger.log("Validation took " + (timer.getEndValidationTime() - timer.getBeginValidationTime()) + " milliseconds");
		} else {
			// engine ran
			tvLogger.log("ENGINEITERS " + engineRunner.lastIterStop);
			tvLogger.log("ENGINETIME " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()));
			tvLogger.log("ENGINEVALUES " + getEngineValueCount(lastData.getLastEngine()));			
			tvLogger.log("ENGINETERMS " + getEngineTermCount(lastData.getLastEngine()));			
			
			tvLogger.log("Validation took " + (timer.getEndValidationTime() - timer.getBeginValidationTime()) + " milliseconds");
	
			Map<LLVMReturn,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>> validatedReturns = 
				new HashMap<LLVMReturn,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>>();
			for (LLVMReturn arr : peginfo1.getReturns()) {
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
	//			int max = Integer.MIN_VALUE;
	//			for (LLVMReturn arr : peginfo1.getReturns()) {
	//				max = Math.max(max,
	//						lastData.getLastEngine().getEGraph().getProofManager().getTimeOfEquality(
	//								lastData.getValidatedPair(arr).getFirst(),
	//								lastData.getValidatedPair(arr).getSecond()));
	//			}
	//			tvLogger.log("TIMEOFEQUALITY " + max);
			} else if (validatedReturns.keySet().size() == 0) {
				tvLogger.log("Could not validate any of the optimization");
			}
		}
		
		logger.log("Done validating");
	}


	private static void validateAll(
			final Logger logger,
			final LLVMTranslationValidator tv, 
			final Module module1, 
			final Module module2) {
		
		final TVTimerListener<LLVMLabel,LLVMParameter,LLVMReturn> timer = 
			new TVTimerListener<LLVMLabel,LLVMParameter,LLVMReturn> ();
		final TVLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn> lastData = 
			new TVLastDataListener<LLVMLabel,LLVMParameter,LLVMReturn> ();
		
		final Logger tvLogger = logger.getSubLogger();
		
		tv.addListener(getOutputTVListener(tvLogger));
		tv.addListener(timer);
		tv.addListener(lastData);
		tv.addListener(getStatPrintListener(tvLogger));
		tv.addListener(getHaltListener(tv));

		Function<Integer,Void> body = new Function<Integer,Void>() {
			public Void get(Integer i1) {
				FunctionBody b1 = module1.getFunctionBody(i1);
				String name1 = module1.lookupValueName(b1.getHeader());
				if (name1==null) {
					logger.log("Cannot find name for function, skipping");
					return null;
				}
				if (!bodyPegProvider.canProvidePEG(b1)) {
					logger.log("Cannot build PEG from function: " + name1);
					return null;
				}
				// found a name for b1

				FunctionBody b2 = null;
				for (int i2 = 0; i2 < module2.getNumFunctionBodies(); i2++) {
					FunctionBody b = module2.getFunctionBody(i2);
					String name2 = module2.lookupValueName(b.getHeader());
					if (name2!=null && name2.equals(name1)) {
						b2 = b;
						break;
					}
				}
				if (b2 == null) {
					logger.log("Cannot find match for function " + name1 + ", skipping");
					return null;
				}
				if (!bodyPegProvider.canProvidePEG(b2)) {
					logger.log("Cannot build PEG from method: " + name1);
					return null;
				}
				// found a match!
				
				if (options.getBoolean(SKIP_EQUIVALENT)) {
					FunctionComparator fc = new FunctionComparator(
							module1, module2, b1, b2);
					if (options.getBoolean(REMOVE_ALLOCA_POINT))
						fc.removeAllocaPoint();
					if (fc.areEquivalent()) {
						logger.log("Function bodies are equivalent, skipping");
						return null;
					}
				}

				logger.log("Function name: " + name1);

				long beforeMakePEGs = System.currentTimeMillis();
				PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peginfo1 = null, peginfo2 = null;
				try {
					peginfo1 = bodyPegProvider.getPEG(b1);
					peginfo2 = bodyPegProvider.getPEG(b2);
				} catch (Throwable t) {
					tvLogger.logException("Cannot build PEGs", t);
					return null;
				}
				int numNodes1 = getPEGSize(peginfo1);
				int numNodes2 = getPEGSize(peginfo2);
				
				long afterMakePEGs = System.currentTimeMillis();
				tvLogger.log("CFG2PEGTIME " + (afterMakePEGs-beforeMakePEGs));
				
				if (options.getLong(PEG_NODE_THRESHOLD) > 0 && 
					(numNodes1 > options.getLong(PEG_NODE_THRESHOLD) ||
					 numNodes2 > options.getLong(PEG_NODE_THRESHOLD))) {
					tvLogger.log("PEG node count exceeds threshold, skipping");
					return null;
				}
				
				tv.setCurrentMethod(b1);
				tv.validate(
						name1, name1,
						peginfo1, peginfo2);
				
				if (lastData.getLastMergedEqual()) {
					// did not run engine
					tvLogger.log("Validate entire optimization!");
					tvLogger.log("Validation took " + (timer.getEndValidationTime() - timer.getBeginValidationTime()) + " milliseconds");
				} else {
					// ran engine
					tvLogger.log("ENGINEITERS " + engineRunner.lastIterStop);
					tvLogger.log("ENGINETIME " + (timer.getEngineCompletedTime() - timer.getEngineSetupTime()));
					tvLogger.log("ENGINEVALUES " + getEngineValueCount(lastData.getLastEngine()));			
					tvLogger.log("ENGINETERMS " + getEngineTermCount(lastData.getLastEngine()));			
					tvLogger.log("Validation took " + (timer.getEndValidationTime() - timer.getBeginValidationTime()) + " milliseconds");
	
					Map<LLVMReturn,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>> validatedReturns = 
						new HashMap<LLVMReturn,Pair<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGTerm<LLVMLabel,LLVMParameter>>>();
					for (LLVMReturn arr : peginfo1.getReturns()) {
						if (lastData.hasValidatedReturn(arr)) {
							validatedReturns.put(arr, 
									lastData.getValidatedPair(arr));
						} else {
							tvLogger.log("Could not validate " + arr);
						}
					}
	
					if (validatedReturns.keySet().containsAll(peginfo1.getReturns())) {
						tvLogger.log("Validate entire optimization!");
					} else if (validatedReturns.keySet().size() == 0) {
						tvLogger.log("Could not validate any of the optimization");
					}
				}

				logger.log("Done validating " + name1);
				
				return null;
			}
		};
		
		// do them all
		for (int i1 = 0; i1 < module1.getNumFunctionBodies(); i1++) {
			logger.log("Begin validating function");
			try {
				body.get(i1);
			} catch (Throwable t) {
				logger.logException("Error during validation", t);
			}
			logger.log("End validating function");
		}
		
		logger.log("Finished with module");
	}
	
	
	public static void main(String args[]) {
		// process the arguments
		if (args.length==0) 
			displayHelp();
		
		try {
			optionsParser.parse(args);
		} catch (OptionParsingException ex) {
			abort("Error parsing command line", ex);
		}
		
		checkOption(
				tvBefore != null,
				"Validator must specify before and after functions");
		
		// do TV
		final String beforeName = tvBefore.getFirst();
		final String afterName = tvAfter.getFirst();
		final Pair<Module,Module> beforeAfterModules = setupTV(TOP_LOGGER);
		final Module beforeModule = beforeAfterModules.getFirst();
		final Module afterModule = beforeAfterModules.getSecond();
		
		if (beforeName.equals("*")) {
			if (!afterName.equals("*"))
				abort("Both names must be '*'");
			LLVMTranslationValidator tv = 
				getTranslationValidator(beforeModule, afterModule, TOP_LOGGER.getSubLogger());
			validateAll(TOP_LOGGER, tv, beforeModule, afterModule);
		} else {
			Value beforeValue = beforeModule.getValueByName(beforeName);
			Value afterValue = afterModule.getValueByName(afterName);
			if (!(beforeValue.isFunction() && afterValue.isFunction()))
				abort("Named values must be functions");
			
			FunctionBody beforeBody = null, afterBody = null;
			for (int i = 0; i < beforeModule.getNumFunctionBodies(); i++) {
				FunctionBody body = beforeModule.getFunctionBody(i);
				if (body.getHeader().equalsValue(beforeValue)) {
					beforeBody = body;
					break;
				}
			}
			if (beforeBody == null)
				abort("Cannot find body for function: " + beforeName);
			
			for (int i = 0; i < afterModule.getNumFunctionBodies(); i++) {
				FunctionBody body = afterModule.getFunctionBody(i);
				if (body.getHeader().equalsValue(afterValue)) {
					afterBody = body;
					break;
				}
			}
			if (afterBody == null)
				abort("Cannot find body for function: " + afterName);
			
			boolean doTV = true;
			if (options.getBoolean(SKIP_EQUIVALENT)) {
				FunctionComparator fc = new FunctionComparator(
						beforeModule, afterModule, beforeBody, afterBody);
				if (options.getBoolean(REMOVE_ALLOCA_POINT))
					fc.removeAllocaPoint();
				if (fc.areEquivalent()) {
					TOP_LOGGER.log("Function bodies are equivalent, skipping TV");
					doTV = false;
				}
			}

			if (doTV) {
				LLVMTranslationValidator tv = 
					getTranslationValidator(beforeModule, afterModule, TOP_LOGGER.getSubLogger());
				performTranslationValidation(TOP_LOGGER, tv, beforeName, beforeBody, afterName, afterBody);
			}
		}
		
//		System.exit(0);
	}
}
