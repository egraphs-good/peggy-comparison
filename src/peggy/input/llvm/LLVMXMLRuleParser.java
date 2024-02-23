package peggy.input.llvm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import llvm.bitcode.LLVMUtils;
import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.instructions.ComparisonPredicate;
import llvm.instructions.FloatingPointComparisonPredicate;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.types.ArrayType;
import llvm.types.FloatingPointType;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.ConstantExplicitArrayValue;
import llvm.values.FloatingPointValue;
import llvm.values.IntegerValue;
import llvm.values.ParameterAttributes;
import llvm.values.UndefValue;
import llvm.values.Value;

import org.w3c.dom.Element;

import peggy.analysis.java.inlining.EngineInlineHeuristic;
import peggy.analysis.java.inlining.NameEngineInlineHeuristic;
import peggy.analysis.llvm.FunctionModifies;
import peggy.input.Buffer;
import peggy.input.RuleParsingException;
import peggy.input.XMLRuleParser;
import peggy.pb.ConfigurableCostModel;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.BasicOpLLVMLabel;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.CmpLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.NumeralLLVMLabel;
import peggy.represent.llvm.ParamAttrLLVMLabel;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import peggy.represent.llvm.TypeLLVMLabel;
import util.DisjointUnion;
import util.Function;
import eqsat.BasicOp;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.engine.AxiomSelector;
import eqsat.engine.MappedAxiomSelector;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.axiom.AxiomGroup;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This is the standard XMLRuleParser for LLVM.
 */
public class LLVMXMLRuleParser extends XMLRuleParser<LLVMLabel,LLVMParameter,Integer> {
	protected static final Set<String> TYPE_ATTRS;
	protected static final Set<String> INTCONST_ATTRS;
	protected static final Set<String> VALUE_ATTRS;
	protected static final Set<String> ID_ATTRS;
	protected static final Set<String> FUNCTION_ATTRS;
	protected static final Set<String> GLOBAL_ATTRS;
	protected static final Set<String> INDEXID_ATTRS;
	protected static final Set<String> COST_ATTRS;
	protected static final Set<String> OPERATORCOST_ATTRS;
	protected static final Set<String> INLINE_ATTRS;
	protected static final Set<String> MODIFIES_ATTRS;
	
	static {
		Set<String> set = new HashSet<String>();
		set.add("value");
		VALUE_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("cost");
		INLINE_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("modifies");
		set.add("nargs");
		MODIFIES_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("type");
		TYPE_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("value");
		set.add("width");
		INTCONST_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("index");
		set.add("id");
		INDEXID_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("id");
		ID_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("name");
		set.add("signature");
		FUNCTION_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("name");
		set.add("type");
		GLOBAL_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("cost");
		COST_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("cost");
		set.add("value");
		OPERATORCOST_ATTRS = Collections.unmodifiableSet(set);
	}

	protected final Set<FunctionLLVMLabel> sigmaInvariantFunctions;
	protected int nextVariable = 0;
	protected Network network;
	protected OpAmbassador<? super LLVMLabel> ambassador;
	protected final LLVMCostModel costModel;
	protected final NameEngineInlineHeuristic<FunctionLLVMLabel,Integer> inlineHeuristic;
	protected final Set<FunctionLLVMLabel> nonstackFunctions;
	protected final Map<AliasLLVMLabel,LLVMLabel> aliasExpansionMap;
	protected final MappedAxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector;
	protected final List<FunctionModifies> functionModifies;
	
	public LLVMXMLRuleParser(DocumentBuilder _builder, Network _network,
			OpAmbassador<? super LLVMLabel> _ambassador) {
		super(_builder);
		this.functionModifies = new ArrayList<FunctionModifies>();
		this.sigmaInvariantFunctions = new HashSet<FunctionLLVMLabel>();
		this.inlineHeuristic = 
			new NameEngineInlineHeuristic<FunctionLLVMLabel,Integer>() {
				protected String getName(FunctionLLVMLabel method) {
					return method.getFunctionName();
				}
			};	
		this.selector = 
			new MappedAxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup>(
					true, 
					eqsat.meminfer.peggy.axiom.AxiomGroup.class);
	
		this.aliasExpansionMap = new HashMap<AliasLLVMLabel,LLVMLabel>();
		this.nonstackFunctions = new HashSet<FunctionLLVMLabel>();
		this.network = _network;
		this.ambassador = _ambassador;
		this.costModel = new LLVMCostModel();
	}

	public Set<FunctionLLVMLabel> getSigmaInvariantFunctions() {
		return this.sigmaInvariantFunctions;
	}
	public Collection<FunctionModifies> getFunctionModifies() {
		return this.functionModifies;
	}
	public AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> getAxiomSelector() {
		return this.selector;
	}
	public Set<FunctionLLVMLabel> getNonstackFunctions() {
		return this.nonstackFunctions;
	}
	public Map<? extends AliasLLVMLabel,? extends LLVMLabel> getAliasExpansions() {
		return this.aliasExpansionMap;
	}
	public EngineInlineHeuristic<FunctionLLVMLabel,Integer> getInlineHeuristic() {
		return this.inlineHeuristic;
	}
	public ConfigurableCostModel<FlowValue<LLVMParameter,LLVMLabel>,FunctionLLVMLabel,CPEGTerm<LLVMLabel,LLVMParameter>,Integer> getCostModel() {
		return this.costModel;
	}

	public PeggyAxiomizer<LLVMLabel,Integer> createAxiomizer(String name) {
		return new PeggyAxiomizer<LLVMLabel,Integer>(name, this.network, this.ambassador);
	}

	protected Collection<AxiomNode<LLVMLabel, ? extends PEGNode<LLVMLabel>>> processRuleSetElement(Element ruleset) throws RuleParsingException {
		List<Element> childElements = this.getElementChildren(ruleset);
		List<AxiomNode<LLVMLabel, ? extends PEGNode<LLVMLabel>>> result = 
			new ArrayList<AxiomNode<LLVMLabel, ? extends PEGNode<LLVMLabel>>>(childElements.size());
		for (Element rule : childElements) {
			if (rule.getTagName().equals("functionCost")) {
				this.processFunctionCostElement(rule);
			} else if (rule.getTagName().equals("operatorCost")) { 
				this.processOperatorCostElement(rule);
			} else if (rule.getTagName().equals("nondomainCost")) { 
				this.processNondomainCostElement(rule);
			} else if (rule.getTagName().equals("sigmaInvariant")) {
				this.processSigmaInvariantElement(rule, result);
			} else if (rule.getTagName().equals("inline")) {
				this.processInlineElement(rule);
			} else if (rule.getTagName().equals("nonstackFunction")) {
				this.processNonstackFunctionElement(rule);
			} else if (rule.getTagName().equals("aliasExpansion")) {
				this.processAliasExpansionElement(rule);
			} else if (rule.getTagName().equals("disableAxiom")) {
				this.processDisableAxiomElement(rule);
			} else if (rule.getTagName().equals("functionModifies")) {
				this.processFunctionModifiesElement(rule);
			} else {
				result.add(this.processRuleItem(rule));
			}
		}
		return result;
	}

	protected void processSigmaInvariantElement(
			Element sigmaInvariant,
			List<AxiomNode<LLVMLabel, ? extends PEGNode<LLVMLabel>>> result) {
		this.assertAttributes(sigmaInvariant, EMPTY_ATTRS, EMPTY_ATTRS);
		List<Element> children = this.getElementChildren(sigmaInvariant);
		for (Element child : children) {
			FunctionLLVMLabel method = parseFunction(child);
			sigmaInvariantFunctions.add(method);
		}
	}
	
	
	/**
	 * Asserts that the given function only modifies the named
	 * pointer parameters.
	 * 'modifies' will be a list of parameter indexes, 0-based.
	 * 'nargs' is only necessary for varargs functions.
	 *   
	 * <functionModifies nargs="#" modifies="#,#,#,...,#">
	 *    <function name="..." signature="..."/>
	 * </functionModifies>
	 */
	protected void processFunctionModifiesElement(Element functionModifies) {
		this.assertAttributes(functionModifies, MODIFIES_ATTRS, EMPTY_ATTRS);
		List<Element> children = this.getElementChildren(functionModifies);
		
		int nargs;
		String nargsString = functionModifies.getAttribute("nargs");
		try {
			nargs = Integer.parseInt(nargsString);
			if (nargs < 0)
				throw new RuleParsingException("nargs must not be negative");
		}
		catch (Throwable t) {throw new RuleParsingException("Cannot parse nargs string: " + nargsString);}
		
		
		Set<Integer> mods = new HashSet<Integer>();
		String[] tokens = functionModifies.getAttribute("modifies").split(",");
		for (String token : tokens) {
			try {
				int index = Integer.parseInt(token.trim());
				if (index < 0)
					throw new RuleParsingException("Indexes cannot be negative");
				mods.add(index);
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse index string: " + token);
			}
		}
		
		for (Element child : children) {
			FunctionLLVMLabel func = parseFunction(child);
			FunctionType type = func.getType();
			if (type.isVararg()) {
				if (type.getNumParams() > nargs)
					throw new RuleParsingException("Too few args for function: " + func);
			} else if (type.getNumParams() != nargs) {
				throw new RuleParsingException("Wrong nargs for function: " + func);
			}
			FunctionModifies modifies = new FunctionModifies(func, nargs, mods);
			this.functionModifies.add(modifies);
		}
	}
	
	
	
	protected void processDisableAxiomElement(Element disableAxiom) {
		this.assertAttributes(disableAxiom, EMPTY_ATTRS, VALUE_ATTRS);
		this.assertNoElementChildren(disableAxiom);

		String value = disableAxiom.getAttribute("value");
		AxiomGroup group = null;
		for (AxiomGroup g : AxiomGroup.values()) {
			if (g.name().toLowerCase().equals(value.toLowerCase())) {
				group = g;
				break;
			}
		}
		if (group == null)
			throw new RuleParsingException("Unknown axiom group: " + value);
		this.selector.setEnabled(group, false);
	}
	

	/**
	 * This processes the "aliasExpansion" element, which
	 * contains a list of <alias>/[<function>,<global>] pairs
	 * that describe what the alias is aliasing. Then peggy
	 * can replace the alias with the actual value in an axiom.
	 */
	protected void processAliasExpansionElement(Element aliasExpansion) {
		this.assertAttributes(aliasExpansion, EMPTY_ATTRS, EMPTY_ATTRS);
		List<Element> children = getElementChildren(aliasExpansion);
		if (children.size()%2 != 0)
			throw new RuleParsingException("aliasExpansion element must contain a list of pairs");
		
		for (int i = 0; i < children.size(); i+=2) {
			AliasLLVMLabel alias = parseAlias(children.get(i));
			Element aliaseeElement = children.get(i+1);
			LLVMLabel aliaseeLabel;
			if (aliaseeElement.getTagName().equals("function")) {
				aliaseeLabel = parseFunction(aliaseeElement);
			} else if (aliaseeElement.getTagName().equals("global")) {
				aliaseeLabel = parseGlobal(aliaseeElement);
			} else {
				throw new RuleParsingException("Invalid aliasee tag: " + aliaseeElement.getTagName());
			}
			this.aliasExpansionMap.put(alias, aliaseeLabel);
		}
	}
	
	
	/**
	 * This processes the "nonstackFunction" element, which
	 * tells peggy that a given LLVM function will not alter the
	 * callee's stack state in any way.
	 */
	protected void processNonstackFunctionElement(Element nonstackFunction) {
		this.assertAttributes(nonstackFunction, EMPTY_ATTRS, EMPTY_ATTRS);
		List<Element> children = getElementChildren(nonstackFunction);
		
		for (Element child : children) {
			FunctionLLVMLabel function = parseFunction(child);
			this.nonstackFunctions.add(function);
		}
	}
	
	
	/**
	 * This processes the "inline" element, which directs the
	 * inliner that the named method is a good inline target.
	 */
	protected void processInlineElement(Element inline) {
		this.assertAttributes(inline, INLINE_ATTRS, EMPTY_ATTRS);
		List<Element> children = getElementChildren(inline);

		String costString = inline.getAttribute("cost");
		int cost = -1;
		try {
			cost = Integer.parseInt(costString);
		} catch (Throwable t) {
			throw new RuleParsingException("Cannot parse cost string as integer: " + costString);
		}
		if (cost < 0)
			throw new RuleParsingException("Inline function cost cannot be negative: " + cost);
		
		for (Element child : children) {
			FunctionLLVMLabel function = this.parseFunction(child);
			this.inlineHeuristic.addFunction(function.getFunctionName(), cost);
		}
	}


	/**
	 * This processes the "operatorCost" element. This will add an entry in the 
	 * cost model's node cost map.
	 * @param operatorCost the operatorCost element
	 */
	protected void processOperatorCostElement(Element operatorCost) {
		this.assertAttributes(operatorCost, OPERATORCOST_ATTRS, EMPTY_ATTRS);
		this.assertNoElementChildren(operatorCost);
		String value = operatorCost.getAttribute("value");
		String costString = operatorCost.getAttribute("cost");
		int cost;
		try {
			cost = Integer.parseInt(costString);
		} catch (Throwable t) {
			throw new RuleParsingException("operator cost string is not a valid integer");
		}

		LLVMLabel label = this.getLabelFromString(value);
		FlowValue<LLVMParameter,LLVMLabel> flow = 
			FlowValue.<LLVMParameter,LLVMLabel>createDomain(label, this.ambassador);
		this.costModel.setConfiguredCost(flow, cost);
	}
	
	
	/**
	 * This processes the "nondomainCost" element. This will assign a cost to 
	 * the various nondomain operators in the PEG.
	 * @param nondomainCost the nondomainCost element
	 */
	protected void processNondomainCostElement(Element nondomainCost) {
		this.assertAttributes(nondomainCost, OPERATORCOST_ATTRS, EMPTY_ATTRS);
		this.assertNoElementChildren(nondomainCost);
		String value = nondomainCost.getAttribute("value");
		String costString = nondomainCost.getAttribute("cost");
		int cost;
		try {
			cost = Integer.parseInt(costString);
		} catch (Throwable t) {
			throw new RuleParsingException("operator cost string is not a valid integer");
		}
		
		if (value.equals("theta")) {
			this.costModel.setThetaCost(cost);
		} else if (value.equals("eval")) {
			this.costModel.setEvalCost(cost);
		} else if (value.equals("pass")) {
			this.costModel.setPassCost(cost);
		} else if (value.equals("shift")) {
			this.costModel.setShiftCost(cost);
		} else if (value.equals("phi")) {
			this.costModel.setPhiCost(cost);
		} else if (value.equals("and")) {
			this.costModel.setAndCost(cost);
		} else if (value.equals("or")) {
			this.costModel.setOrCost(cost);
		} else if (value.equals("negate")) {
			this.costModel.setNegateCost(cost);
		} else if (value.equals("equals")) {
			this.costModel.setEqualsCost(cost);
		} else if (value.equals("varianceMultiplier")) {
			this.costModel.setVarianceMultiplier(cost);
		} else {
			throw new RuleParsingException("Invalid nondomain operator: " + value);
		}
	}
	


	protected void processFunctionCostElement(Element functionCost) throws RuleParsingException {
		this.assertAttributes(functionCost, COST_ATTRS, EMPTY_ATTRS);
		List<? extends Element> children = this.getElementChildren(functionCost);
		if (children.size() != 1)
			throw new RuleParsingException("functionCost tag must have exactly one child");
		Element child = children.get(0);
		if (!child.getTagName().equals("function"))
			throw new RuleParsingException("functionCost tag expected function tag as child, found " + child.getTagName());

		int cost;
		try {
			cost = Integer.parseInt(functionCost.getAttribute("cost"));
		} catch (Throwable t) {
			throw new RuleParsingException("methodCost cost value is not a valid integer");
		}

		FunctionLLVMLabel function = this.parseFunction(child);
		this.costModel.setMethodInvokeCost(function, cost);
	}

	protected PeggyVertex<LLVMLabel,Integer> getFreshVariable(
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer) {
		return axiomizer.getVariable(this.nextVariable++);
	}
	
	protected PeggyVertex<LLVMLabel,Integer> parseSimpleDomain(
			Buffer buffer,
			String opname,
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer,
			Map<String,PeggyVertex<LLVMLabel,Integer>> id2vertex) {
		// TODO constants (string)
		if (opname.equals("int")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 2 && args.get(0).isLeft() && args.get(1).isLeft()))
				throw new RuleParsingException("int expects 2 strings");
			IntegerValue result;
			try {
				int width = Integer.parseInt(args.get(0).getLeft());
				BigInteger value = new BigInteger(args.get(1).getLeft());
				result = IntegerValue.get(width, value);
			} catch (Throwable t) {
				throw new RuleParsingException("Error parsing int constant");
			}
			return axiomizer.get(new ConstantValueLLVMLabel(result));
		} else if (opname.equals("fp")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 2 && args.get(0).isLeft() && args.get(1).isLeft()))
				throw new RuleParsingException("fp expects 2 strings");
			FloatingPointValue result;
			String kind = args.get(0).getLeft();
			if (kind.equals("float")) {
				try {
					float value = Float.parseFloat(args.get(1).getLeft());
					result = FloatingPointValue.fromFloat(value);
				} catch (Throwable t) {
					throw new RuleParsingException("Cannot parse float value: " + args.get(1).getLeft());
				}
			} else if (kind.equals("double")) {
				try {
					double value = Double.parseDouble(args.get(1).getLeft());
					result = FloatingPointValue.fromDouble(value);
				} catch (Throwable t) {
					throw new RuleParsingException("Cannot parse double value: " + args.get(1).getLeft());
				}
			} else {
				try {
					result = FloatingPointValue.get(
							FloatingPointType.fromString(args.get(0).getLeft()),
							args.get(1).getLeft());
				} catch (Throwable t) {
					throw new RuleParsingException("Cannot parse floating point type: " + args.get(0).getLeft());
				}
			}
			return axiomizer.get(new ConstantValueLLVMLabel(result));
		} else if (opname.equals("undef")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("undef expects 1 string");
			Type inner;
			try {
				inner = LLVMUtils.parseType(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse undef type: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueLLVMLabel(
					new UndefValue(inner)));

		} else if (opname.equals("function")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 2 && args.get(0).isLeft() && args.get(1).isLeft()))
				throw new RuleParsingException("Function expects 2 strings");
			FunctionType type;
			try {
				type = LLVMUtils.parseType(args.get(1).getLeft()).getFunctionSelf();
			} catch (Throwable t) {
				throw new RuleParsingException("Unable to parse function type: " + args.get(1).getLeft());
			}
			return axiomizer.get(new FunctionLLVMLabel(type, args.get(0).getLeft()));
		} else if (opname.equals("global")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 2 && args.get(0).isLeft() && args.get(1).isLeft()))
				throw new RuleParsingException("Global expects 2 strings");
			Type type;
			try {
				type = LLVMUtils.parseType(args.get(1).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Unable to parse global type: " + args.get(1).getLeft());
			}
			return axiomizer.get(new GlobalLLVMLabel(type, args.get(0).getLeft()));
		} else if (opname.equals("alias")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 2 && args.get(0).isLeft() && args.get(1).isLeft()))
				throw new RuleParsingException("Alias expects 2 strings");
			Type type;
			try {
				type = LLVMUtils.parseType(args.get(1).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Unable to parse alias type: " + args.get(1).getLeft());
			}
			return axiomizer.get(new AliasLLVMLabel(type, args.get(0).getLeft()));
		} else if (opname.equals("annotation")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() >= 1))
				throw new RuleParsingException("Annotation expects 1 string then nodes");
			
			List<PeggyVertex<LLVMLabel,Integer>> params = 
				new ArrayList<PeggyVertex<LLVMLabel,Integer>> (args.size());
			for (int i = 1; i < args.size(); i++) {
				if (!args.get(i).isRight())
					throw new RuleParsingException("Annotation expects 1 string then nodes");
				params.add(args.get(i).getRight());
			}
			String value = args.get(0).getLeft();
			
			return axiomizer.get(new StringAnnotationLLVMLabel(value), params);
		} else if (opname.equals("type")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("Type expects 1 string");
			Type type;
			try {
				type = LLVMUtils.parseType(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse type: " + args.get(0).getLeft());
			}
			return axiomizer.get(new TypeLLVMLabel(type));
		} else if (opname.equals("numeral")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("Numeral expects 1 string");
			int value;
			try {
				value = Integer.parseInt(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse integer from string: " + args.get(0).getLeft());
			}
			return axiomizer.get(new NumeralLLVMLabel(value));
		} else if (opname.equals("null")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("Null expects 1 string");
			Type type;
			try {
				type = LLVMUtils.parseType(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse type: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueLLVMLabel(Value.getNullValue(type)));
		} else if (opname.equals("paramAttrs")) {
			List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("Numeral expects 1 string");
			String value = args.get(0).getLeft();
			int bits;
			try {
				if (value.startsWith("0X") || value.startsWith("0x")) {
					bits = Integer.parseInt(value.substring(2), 16);
				} else {
					bits = Integer.parseInt(value, 16);
				}
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse paramAttrs value as hex integer: " + value);
			}
			return axiomizer.get(new ParamAttrLLVMLabel(new ParameterAttributes(bits)));
		} else {
			Binop binop = null;
			Cast cast = null;
			ComparisonPredicate pred = null;
			
			if ((binop = binopFromString(opname)) != null) {
				List<? extends PeggyVertex<LLVMLabel,Integer>> args = 
					this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
				if (args.size() != 2)
					throw new RuleParsingException("Binop " + opname + " expects 2 arguments: " + args.size());
				return axiomizer.get(new BinopLLVMLabel(binop), args);
			} else if ((cast = castFromString(opname)) != null) {
				List<? extends PeggyVertex<LLVMLabel,Integer>> args = 
					this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
				if (args.size() != 2)
					throw new RuleParsingException("Cast " + opname + " expects 2 arguments: " + args.size());
				return axiomizer.get(new CastLLVMLabel(cast), args);
			} else if ((pred = predFromString(opname)) != null) {
				List<? extends PeggyVertex<LLVMLabel,Integer>> args = 
					this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
				if (args.size() != 2)
					throw new RuleParsingException("Comparison predicate " + opname + " expects 2 arguments: " + args.size());
				return axiomizer.get(new CmpLLVMLabel(pred), args);
			} else {
				// op
				LLVMOperator operator = getOperatorFromString(opname);
				List<? extends DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>>> args = 
					this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
				if (operator.getArity() >= 0 && operator.getArity() != args.size())
					throw new RuleParsingException("Invalid number of arguments for operator " + operator.name() + ": " + args.size());

				List<PeggyVertex<LLVMLabel,Integer>> params = 
					new ArrayList<PeggyVertex<LLVMLabel,Integer>>(args.size());
				for (DisjointUnion<String,PeggyVertex<LLVMLabel,Integer>> dj : args) {
					if (!dj.isRight())
						throw new RuleParsingException("Operator demands all node parameters");
					params.add(dj.getRight());
				}

				LLVMLabel label = SimpleLLVMLabel.get(operator);
				return axiomizer.get(label, params);
			}
		}
	}
	
	
	protected Binop binopFromString(String str) {
		for (Binop b : Binop.values()) {
			if (b.name().toLowerCase().equals(str)) {
				return b;
			}
		}
		return null;
	}
	protected Cast castFromString(String str) {
		for (Cast c : Cast.values()) {
			if (c.name().toLowerCase().equals(str)) {
				return c;
			}
		}
		return null;
	}
	protected ComparisonPredicate predFromString(String str) {
		for (IntegerComparisonPredicate icmp : IntegerComparisonPredicate.values()) {
			if (str.equals("icmp_" + icmp.getLabel().toLowerCase()))
				return icmp;
		}
		for (FloatingPointComparisonPredicate fcmp : FloatingPointComparisonPredicate.values()) {
			if (str.equals("fcmp_" + fcmp.getLabel().toLowerCase()))
				return fcmp;
		}
		return null;
	}
	
	protected PeggyVertex<LLVMLabel,Integer> getAndVertex(
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer, 
			List<? extends PeggyVertex<LLVMLabel,Integer>> args) {
		return axiomizer.get(new BasicOpLLVMLabel(BasicOp.And), args);
	}
	protected PeggyVertex<LLVMLabel,Integer> getOrVertex(
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer, 
			List<? extends PeggyVertex<LLVMLabel,Integer>> args) {
		return axiomizer.get(new BasicOpLLVMLabel(BasicOp.Or), args);
	}
	
	
	
	protected PeggyVertex<LLVMLabel,Integer> parseVertex(PeggyAxiomizer<LLVMLabel,Integer> axiomizer, 
			Element element, Map<String,PeggyVertex<LLVMLabel,Integer>> id2vertex) {

		LinkedList<String> parentIDs = new LinkedList<String>();
		Map<String, List<PeggyVertex<LLVMLabel,Integer>>> placeholderMap =
			new HashMap<String, List<PeggyVertex<LLVMLabel,Integer>>>();

		PeggyVertex<LLVMLabel,Integer> result = 
			this.parseVertex(axiomizer, element, id2vertex, parentIDs, placeholderMap);
		for (String id : placeholderMap.keySet()) {
			PeggyVertex<LLVMLabel,Integer> replacement = id2vertex.get(id);
			if (replacement == null)
				throw new RuntimeException("This is a bug");

			List<PeggyVertex<LLVMLabel,Integer>> holders = placeholderMap.get(id);
			for (PeggyVertex<LLVMLabel,Integer> vertex : holders) {
				vertex.replaceWith(replacement);
			}
		}

		return result;
	}


	private PeggyVertex<LLVMLabel,Integer> parseVertex(
			final PeggyAxiomizer<LLVMLabel,Integer> axiomizer, 
			final Element element, 
			final Map<String,PeggyVertex<LLVMLabel,Integer>> id2vertex,
			final LinkedList<String> parentIDs, 
			Map<String, List<PeggyVertex<LLVMLabel,Integer>>> placeholderMap) {

		Function<LLVMLabel,PeggyVertex<LLVMLabel,Integer>> helper = 
			new Function<LLVMLabel,PeggyVertex<LLVMLabel,Integer>>() {
			public PeggyVertex<LLVMLabel,Integer> get(LLVMLabel label) {
				PeggyVertex<LLVMLabel,Integer> node = axiomizer.get(label);
				if (element.hasAttribute("id")) {
					String id = element.getAttribute("id");
					if (id2vertex.containsKey(id) || parentIDs.contains(id))
						throw new RuleParsingException("Duplicate id found: " + id);
					id2vertex.put(id, node);
				}
				return node;
			}
		};

		
		String elementName = element.getTagName();
		if (elementName.equals("op")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			List<? extends Element> elementchildren = this.getElementChildren(element);
			String value = element.getAttribute("value");

			LLVMOperator operator = getOperatorFromString(value);
			if (operator.getArity() >= 0 && operator.getArity() != elementchildren.size())
				throw new RuleParsingException("Invalid number of arguments for operator " + operator.name() + ": " + elementchildren.size());
			LLVMLabel label = SimpleLLVMLabel.get(operator);
			return this.parseOperator(axiomizer, label, element, id2vertex, parentIDs, placeholderMap);
		} else if (elementName.equals("simple")) {
			this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
			String text = this.getAllText(element);
			Buffer buffer = new Buffer(text);
			return parseSimpleVertex(buffer, axiomizer, id2vertex);
		} else if (elementName.equals("function")) {
			FunctionLLVMLabel label = this.parseFunction(element);
			return helper.get(label);
		} else if (elementName.equals("global")) {
			GlobalLLVMLabel label = this.parseGlobal(element);
			return helper.get(label);
		} else if (elementName.equals("alias")) {
			AliasLLVMLabel label = this.parseAlias(element);
			return helper.get(label);
		} else if (elementName.equals("annotation")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			String value = element.getAttribute("value");
			StringAnnotationLLVMLabel label = new StringAnnotationLLVMLabel(value);
			return this.parseOperator(axiomizer, label, element, id2vertex, parentIDs, placeholderMap);
		} else if (elementName.equals("binop")) {
			this.assertAttributes(element, TYPE_ATTRS, ID_ATTRS);
			List<? extends Element> elementchildren = this.getElementChildren(element);
			if (elementchildren.size() != 2)
				throw new RuleParsingException("Binop expects 2 children");
			
			String type = element.getAttribute("type");
			Binop binop = null;
			for (Binop b : Binop.values()) {
				if (b.name().toLowerCase().equals(type)) {
					binop = b;
					break;
				}
			}
			if (binop == null)
				throw new RuleParsingException("Invalid binop type: " + type);
			LLVMLabel label = new BinopLLVMLabel(binop);

			return this.parseOperator(axiomizer, label, element, id2vertex, parentIDs, placeholderMap);
		} else if (elementName.equals("icmp") ||
				   elementName.equals("fcmp")) {
			this.assertAttributes(element, TYPE_ATTRS, ID_ATTRS);
			List<? extends Element> elementchildren = this.getElementChildren(element);
			if (elementchildren.size() != 2)
				throw new RuleParsingException("cmp expects 2 children");
			
			String type = element.getAttribute("type");
			LLVMLabel label;
			
			if (elementName.equals("icmp")) {
				IntegerComparisonPredicate pred = null;
				for (IntegerComparisonPredicate ip : IntegerComparisonPredicate.values()) {
					if (ip.getLabel().toLowerCase().equals(type)) {
						pred = ip;
						break;
					}
				}
				if (pred == null)
					throw new RuleParsingException("Invalid icmp type: " + type);
				label = new CmpLLVMLabel(pred);
			} else {
				FloatingPointComparisonPredicate pred = null;
				for (FloatingPointComparisonPredicate fp : FloatingPointComparisonPredicate.values()) {
					if (fp.getLabel().toLowerCase().equals(type)) {
						pred = fp;
						break;
					}
				}
				if (pred == null)
					throw new RuleParsingException("Invalid fcmp type: " + type);
				label = new CmpLLVMLabel(pred);
			}

			return this.parseOperator(axiomizer, label, element, id2vertex, parentIDs, placeholderMap);
		} else if (elementName.equals("cast")) {
			this.assertAttributes(element, TYPE_ATTRS, ID_ATTRS);
			List<? extends Element> elementchildren = this.getElementChildren(element);
			if (elementchildren.size() != 2)
				throw new RuleParsingException("Cast expects 2 children");
			
			String type = element.getAttribute("type");
			Cast cast = null;
			for (Cast c : Cast.values()) {
				if (c.name().toLowerCase().equals(type)) {
					cast = c;
					break;
				}
			}
			if (cast == null)
				throw new RuleParsingException("Invalid cast type: " + type);
			LLVMLabel label = new CastLLVMLabel(cast);

			return this.parseOperator(axiomizer, label, element, id2vertex, parentIDs, placeholderMap);
		} else if (elementName.equals("type")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			Type type = LLVMUtils.parseType(value);
			TypeLLVMLabel typeLabel = new TypeLLVMLabel(type);

			return helper.get(typeLabel);
		} else if (elementName.equals("numeral")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			NumeralLLVMLabel label;
			try {
				label = new NumeralLLVMLabel(Integer.parseInt(value));
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid value for numeral: " + value);
			}
			return helper.get(label);
		} else if (elementName.equals("paramAttrs")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			int hex;
			if (value.startsWith("0x") || value.startsWith("0X")) {
				try {
					hex = Integer.parseInt(value.substring(2), 16);
				} catch (Throwable t) {
					throw new RuleParsingException("Invalid hex value: " + value);
				}
			} else {
				try {
					hex = Integer.parseInt(value, 16);
				} catch (Throwable t) {
					throw new RuleParsingException("Invalid hex value: " + value);
				}
			}
			ParameterAttributes attrs = new ParameterAttributes(hex);
			ParamAttrLLVMLabel label = new ParamAttrLLVMLabel(attrs);
			return helper.get(label);
		} else if (elementName.equals("undefconstant")) {
			this.assertAttributes(element, TYPE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);
			
			String typeString = element.getAttribute("type");
			Type type;
			try {
				type = LLVMUtils.parseType(typeString);
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid type string: " + typeString);
			}
			
			return helper.get(new ConstantValueLLVMLabel(new UndefValue(type)));
		} else if (elementName.equals("intconstant")) {
			this.assertAttributes(element, INTCONST_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			String width = element.getAttribute("width"); 
			ConstantValueLLVMLabel intLabel = null;
			try {
				long[] words = new long[1];
				words[0] = Integer.parseInt(value);
				IntegerValue iv = IntegerValue.get(Integer.parseInt(width), words); 
				intLabel = new ConstantValueLLVMLabel(iv);
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid integer constant string: " + value, t);
			}

			return helper.get(intLabel);
		} else if (elementName.equals("floatconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			ConstantValueLLVMLabel floatLabel = null;
			try {
				long[] words = new long[] {Float.floatToRawIntBits(Float.parseFloat(value))};
				FloatingPointType type = Type.getFloatingPointType(FloatingPointType.Kind.FLOAT);
				FloatingPointValue fv = FloatingPointValue.get(type, words);
				floatLabel = new ConstantValueLLVMLabel(fv);
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid float constant string: " + value, t);
			}

			return helper.get(floatLabel);
		} else if (elementName.equals("doubleconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			ConstantValueLLVMLabel floatLabel = null;
			try {
				long[] words = new long[] {Double.doubleToRawLongBits(Double.parseDouble(value))};
				FloatingPointType type = Type.getFloatingPointType(FloatingPointType.Kind.DOUBLE);
				FloatingPointValue fv = FloatingPointValue.get(type, words);
				floatLabel = new ConstantValueLLVMLabel(fv);
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid double constant string: " + value, t);
			}

			return helper.get(floatLabel);
		} else if (elementName.equals("stringconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = this.unescape(element.getAttribute("value"));
			
			ArrayType type = new ArrayType(Type.getIntegerType(8), value.length()).intern();
			List<IntegerValue> values = new ArrayList<IntegerValue>(value.length());
			
			long[] words = new long[1];
			for (int i = 0; i < value.length(); i++) {
				words[0] = value.charAt(i)&0xFFFFL;
				values.add(IntegerValue.get(8, words));
			}
			ConstantExplicitArrayValue av = 
				new ConstantExplicitArrayValue(type, values);
			
			return helper.get(new ConstantValueLLVMLabel(av));
		} else if (elementName.equals("nullconstant")) {
			this.assertAttributes(element, EMPTY_ATTRS, ID_ATTRS);
			List<? extends Element> elementchildren = this.getElementChildren(element);
			if (elementchildren.size() != 1)
				throw new RuleParsingException("nullconstant expects 1 child");
			
			Type type = this.parseType(elementchildren.get(0));
			Value result = Value.getNullValue(type);

			return helper.get(new ConstantValueLLVMLabel(result));
		} else if (elementName.equals("variable")) {
			this.assertAttributes(element, EMPTY_ATTRS, ID_ATTRS);

			PeggyVertex<LLVMLabel,Integer> result = axiomizer.getVariable(this.nextVariable++);
			if (element.hasAttribute("id")) {
				String id = element.getAttribute("id");
				if (id2vertex.containsKey(id) || parentIDs.contains(id))
					throw new RuleParsingException("Duplicate id found: " + id);
				id2vertex.put(id, result);
			}
			return result;
		} else if (elementName.equals("ref")) {
			this.assertAttributes(element, ID_ATTRS, EMPTY_ATTRS);
			this.assertNoElementChildren(element);

			String id = element.getAttribute("id");
			if (id2vertex.containsKey(id)) {
				return id2vertex.get(id);
			} else if (parentIDs.contains(id)) {
				List<PeggyVertex<LLVMLabel,Integer>> holders = placeholderMap.get(id);
				if (holders == null) {
					holders = new ArrayList<PeggyVertex<LLVMLabel,Integer>>();
					placeholderMap.put(id, holders);
				}
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.createPlaceHolder();
				holders.add(result);
				return result;
			} else {
				throw new RuleParsingException("Reference ID not defined: " + id);
			}
		} else {
			// assume it's a non-domain operator
			return this.parseNondomainVertex(axiomizer, element, id2vertex, parentIDs, placeholderMap);
		}
	}


	private PeggyVertex<LLVMLabel,Integer> parseNondomainVertex(
			final PeggyAxiomizer<LLVMLabel,Integer> axiomizer, 
			Element element, 
			final Map<String,PeggyVertex<LLVMLabel,Integer>> id2vertex, 
			final LinkedList<String> parentIDs, 
			final Map<String,List<PeggyVertex<LLVMLabel,Integer>>> placeholderMap) {

		String elementName = element.getTagName();

		if (elementName.equals("nondomain")) {
			this.assertAttributes(element, VALUE_ATTRS, INDEXID_ATTRS);
			String value = element.getAttribute("value");

			Function<Element,Integer> helper = 
				new Function<Element,Integer>() {
				public Integer get(Element arg) {
					if (!arg.hasAttribute("index"))
						throw new RuleParsingException("'index' attribute required");
					int index;
					try {
						index = Integer.parseInt(arg.getAttribute("index"));
					} catch (NumberFormatException nfe) {
						throw new RuleParsingException("Invalid index value: " + arg.getAttribute("index"));
					}
					if (index <= 0)
						throw new RuleParsingException("Invalid index value: " + index);
					return index;
				}
			};

			final List<Element> children = this.getElementChildren(element);
			final List<PeggyVertex<LLVMLabel,Integer>> childVertices = 
				new ArrayList<PeggyVertex<LLVMLabel,Integer>>(children.size());

			Function<Element,String> idhelper = 
				new Function<Element,String>() {
				public String get(Element arg) {
					boolean hasID = arg.hasAttribute("id");
					String id = null;
					if (hasID) {
						id = arg.getAttribute("id");
						if (id2vertex.containsKey(id) || parentIDs.contains(id))
							throw new RuleParsingException("Duplicate id found: " + id);
					}

					if (hasID) parentIDs.addLast(arg.getAttribute("id"));
					for (Element child : children) {
						childVertices.add(parseVertex(axiomizer, child, id2vertex, parentIDs, placeholderMap));
					}
					if (hasID) parentIDs.removeLast();
					return id;
				}
			};


			if (value.equals("theta")) {
				int index = helper.get(element);
				if (children.size() != 2)
					throw new RuleParsingException("theta needs 2 children");

				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getTheta(index, childVertices.get(0), childVertices.get(1));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("eval")) {
				int index = helper.get(element);
				if (children.size() != 2)
					throw new RuleParsingException("eval needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getEval(index, childVertices.get(0), childVertices.get(1));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("pass")) {
				int index = helper.get(element);
				if (children.size() != 1)
					throw new RuleParsingException("pass needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getPass(index, childVertices.get(0));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("shift")) {
				int index = helper.get(element);
				if (children.size() != 1)
					throw new RuleParsingException("shift needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getShift(index, childVertices.get(0));
				if (id!=null) id2vertex.put(id, result);
				return result;
			}


			else if (value.equals("phi")) {
				if (children.size() != 3)
					throw new RuleParsingException("phi needs 3 children");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getPhi(childVertices.get(0), childVertices.get(1), childVertices.get(2));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("zero")) {
				if (children.size() != 0)
					throw new RuleParsingException("zero can take no children");
				return axiomizer.getZero();
			} else if (value.equals("negate")) {
				if (children.size() != 1)
					throw new RuleParsingException("negate needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getNegate(childVertices.get(0));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("equals")) {
				if (children.size() != 2)
					throw new RuleParsingException("equals needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getEquals(childVertices.get(0), childVertices.get(1));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("successor")) {
				if (children.size() != 1)
					throw new RuleParsingException("successor needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = axiomizer.getSuccessor(childVertices.get(0));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("and")) {
				if (children.size() != 2)
					throw new RuleParsingException("and needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = 
					axiomizer.get(new BasicOpLLVMLabel(BasicOp.And), childVertices.get(0), childVertices.get(1));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else if (value.equals("or")) {
				if (children.size() != 2)
					throw new RuleParsingException("or needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<LLVMLabel,Integer> result = 
					axiomizer.get(new BasicOpLLVMLabel(BasicOp.Or), childVertices.get(0), childVertices.get(1));
				if (id!=null) id2vertex.put(id, result);
				return result;
			} else {
				throw new RuleParsingException("Unknown nondomain value: " + value);
			}
		} else {
			throw new RuleParsingException("Unknown tag: " + elementName);
		}
	}

	
	private Type parseType(Element element) {
		this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
		this.assertNoElementChildren(element);
		
		String value = element.getAttribute("value");
		try {
			Type type = LLVMUtils.parseType(value);
			return type;
		} catch (Throwable t) {
			throw new RuleParsingException("Cannot parse type: " + value);
		}
	}
	
	
	private PeggyVertex<LLVMLabel,Integer> parseOperator(
			PeggyAxiomizer<LLVMLabel,Integer> axiomizer,
			LLVMLabel label, Element element, 
			Map<String,PeggyVertex<LLVMLabel,Integer>> id2vertex, 
			LinkedList<String> parentIDs, 
			Map<String, List<PeggyVertex<LLVMLabel,Integer>>> placeholderMap) {

		List<? extends Element> childElements = this.getElementChildren(element);

		List<PeggyVertex<LLVMLabel,Integer>> childVertices = 
			new ArrayList<PeggyVertex<LLVMLabel,Integer>>(childElements.size());

		boolean hasID = element.hasAttribute("id");
		String id = null;
		if (hasID) {
			id = element.getAttribute("id");
			if (id2vertex.containsKey(id) || parentIDs.contains(id))
				throw new RuleParsingException("Duplicate id found: " + id);
		}

		if (hasID) parentIDs.addLast(element.getAttribute("id"));
		for (Element child : childElements) {
			childVertices.add(this.parseVertex(axiomizer, child, id2vertex, parentIDs, placeholderMap));
		}
		if (hasID) parentIDs.removeLast();

		PeggyVertex<LLVMLabel,Integer> result = axiomizer.get(label, childVertices);

		if (hasID) id2vertex.put(id, result);

		return result;
	}

	private GlobalLLVMLabel parseGlobal(Element element) throws RuleParsingException {
		this.assertAttributes(element, GLOBAL_ATTRS, ID_ATTRS);
		this.assertNoElementChildren(element);
		
		String nameAttr = element.getAttribute("name");
		String typeAttr = element.getAttribute("type");
		
		Type globalType;
		try {
			globalType = LLVMUtils.parseType(typeAttr);
		} catch (Throwable t) {
			throw new RuleParsingException("Error parsing global type: " + t.getMessage());
		}
		return new GlobalLLVMLabel(globalType, nameAttr);
	}

	private AliasLLVMLabel parseAlias(Element element) throws RuleParsingException {
		this.assertAttributes(element, GLOBAL_ATTRS, ID_ATTRS);
		this.assertNoElementChildren(element);
		
		String nameAttr = element.getAttribute("name");
		String typeAttr = element.getAttribute("type");
		
		Type aliasType;
		try {
			aliasType = LLVMUtils.parseType(typeAttr);
		} catch (Throwable t) {
			throw new RuleParsingException("Error parsing alias type: " + t.getMessage());
		}
		return new AliasLLVMLabel(aliasType, nameAttr);
	}
	
	
	private FunctionLLVMLabel parseFunction(Element element) throws RuleParsingException {
		this.assertAttributes(element, FUNCTION_ATTRS, ID_ATTRS);
		this.assertNoElementChildren(element);

		String nameAttr = element.getAttribute("name");
		String signatureAttr = element.getAttribute("signature");

		Type functionType;
		try {
			functionType = LLVMUtils.parseType(signatureAttr);
		} catch (Throwable t) {
			throw new RuleParsingException("Error parsing function type: " + t.getMessage());
		}
		if (!functionType.isFunction())
			throw new RuleParsingException("Type is not a function type: " + functionType);
		
		return new FunctionLLVMLabel(functionType.getFunctionSelf(), nameAttr);
	}


	
	private String unescape(String str) {
		// TODO: finish
		return str;
	}
	
	
	
	private LLVMLabel getLabelFromString(String op) {
		if (op.matches("binop\\(.*\\)")) {
			String inner = op.substring(6, op.length()-1);
			for (Binop b : Binop.values()) {
				if (b.name().toLowerCase().equals(inner)) {
					return new BinopLLVMLabel(b);
				}
			}
			throw new IllegalArgumentException("Unrecognized binop: " + inner);
		} else if (op.matches("icmp\\(.*\\)")) {
			String inner = op.substring(5, op.length()-1);
			for (IntegerComparisonPredicate i : IntegerComparisonPredicate.values()) {
				if (i.getLabel().toLowerCase().equals(inner)) {
					return new CmpLLVMLabel(i);
				}
			}
			throw new IllegalArgumentException("Unrecognized icmp: " + inner);
		} else if (op.matches("fcmp\\(.*\\)")) {
			String inner = op.substring(5, op.length()-1);
			for (FloatingPointComparisonPredicate f : FloatingPointComparisonPredicate.values()) {
				if (f.getLabel().toLowerCase().equals(inner)) {
					return new CmpLLVMLabel(f);
				}
			}
			throw new IllegalArgumentException("Unrecognized fcmp: " + inner);
		} else if (op.matches("cast\\(.*\\)")) {
			String inner = op.substring(5, op.length()-1);
			for (Cast c : Cast.values()) {
				if (c.name().toLowerCase().equals(inner)) {
					return new CastLLVMLabel(c);
				}
			}
			throw new IllegalArgumentException("Unrecognized cast: " + inner);
		} else {
			LLVMOperator operator = getOperatorFromString(op);
			return SimpleLLVMLabel.get(operator);
		}
	}

	public static LLVMOperator getOperatorFromString(String op) {
		op = op.toLowerCase();
		LLVMOperator[] operators = LLVMOperator.values();
		for (LLVMOperator operator : operators) {
			if (operator.name().toLowerCase().equals(op))
				return operator;
		}
		throw new RuleParsingException("Invalid operator name: " + op);
	}
}
