package peggy.input.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Element;

import peggy.input.Buffer;
import peggy.input.RuleParsingException;
import peggy.input.XMLRuleParser;
import peggy.represent.java.AnnotationJavaLabel;
import peggy.represent.java.BasicJavaLabel;
import peggy.represent.java.ConstantValueJavaLabel;
import peggy.represent.java.FieldJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelOpAmbassador;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.SimpleJavaLabel;
import peggy.represent.java.SootUtils;
import peggy.represent.java.TypeJavaLabel;
import soot.Type;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import util.DisjointUnion;
import util.Function;
import eqsat.BasicOp;
import eqsat.FlowValue;
import eqsat.engine.AxiomSelector;
import eqsat.engine.MappedAxiomSelector;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.axiom.AxiomGroup;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This is the standard XMLRuleParser for Java.
 */
public class JavaXMLRuleParser extends XMLRuleParser<JavaLabel,JavaParameter,Integer> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("JavaXMLRuleParser: " + message);
	}
	
	
	protected static final Set<String> VALUE_ATTRS;
	protected static final Set<String> ID_ATTRS;
	protected static final Set<String> FIELD_METHOD_ATTRS;
	protected static final Set<String> INDEXID_ATTRS;
	protected static final Set<String> COST_ATTRS;
	protected static final Set<String> OPERATORCOST_ATTRS;
	protected static final Set<String> INVOKETYPE_ATTRS;
	
	static {
		Set<String> set = new HashSet<String>();
		set.add("value");
		VALUE_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("index");
		set.add("id");
		INDEXID_ATTRS = Collections.unmodifiableSet(set);

		set = new HashSet<String>();
		set.add("invoketype");
		INVOKETYPE_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("id");
		ID_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("name");
		set.add("class");
		set.add("signature");
		FIELD_METHOD_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("cost");
		COST_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("cost");
		set.add("value");
		OPERATORCOST_ATTRS = Collections.unmodifiableSet(set);
	}

	protected final Set<MethodJavaLabel> sigmaInvariantMethods;
	protected final Set<MethodJavaLabel> refInvariantMethods;
	protected final Set<MethodJavaLabel> inlinedMethods;
	protected int nextVariable = 0;
	protected Network network;
	protected JavaLabelOpAmbassador ambassador;
	protected final JavaCostModel costModel;
	protected final MappedAxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> selector;
	
	public JavaXMLRuleParser(DocumentBuilder _builder, Network _network,
							 JavaLabelOpAmbassador _ambassador) {
		super(_builder);
		this.network = _network;
		this.ambassador = _ambassador;
		this.costModel = new JavaCostModel();
		this.inlinedMethods = new HashSet<MethodJavaLabel>();
		this.sigmaInvariantMethods = new HashSet<MethodJavaLabel>();
		this.refInvariantMethods = new HashSet<MethodJavaLabel>();
		this.selector = 
			new MappedAxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup>(
					true, 
					eqsat.meminfer.peggy.axiom.AxiomGroup.class);
	}

	public Set<MethodJavaLabel> getSigmaInvariantMethods() {
		return this.sigmaInvariantMethods;
	}
	public Set<MethodJavaLabel> getRefInvariantMethods() {
		return this.refInvariantMethods;
	}
	public Set<MethodJavaLabel> getInlineMethods() {
		return this.inlinedMethods;
	}
	public JavaCostModel getCostModel() {
		return this.costModel;
	}
	public AxiomSelector<eqsat.meminfer.peggy.axiom.AxiomGroup> getAxiomSelector() {
		return this.selector;
	}
	public PeggyAxiomizer<JavaLabel,Integer> createAxiomizer(String name) {
		return new PeggyAxiomizer<JavaLabel,Integer>(name, this.network, this.ambassador);
	}
	
	protected Collection<AxiomNode<JavaLabel, ? extends PEGNode<JavaLabel>>> processRuleSetElement(Element ruleset) throws RuleParsingException {
		List<Element> childElements = this.getElementChildren(ruleset);
		List<AxiomNode<JavaLabel, ? extends PEGNode<JavaLabel>>> result = 
			new ArrayList<AxiomNode<JavaLabel, ? extends PEGNode<JavaLabel>>>(childElements.size());
		for (Element rule : childElements) {
			if (rule.getTagName().equals("methodCost")) {
				this.processMethodCostElement(rule);
			} else if (rule.getTagName().equals("disableAxiom")) {
				this.processDisableAxiomElement(rule);
			} else if (rule.getTagName().equals("operatorCost")) { 
				this.processOperatorCostElement(rule);
			} else if (rule.getTagName().equals("nondomainCost")) { 
				this.processNondomainCostElement(rule);
			} else if (rule.getTagName().equals("sigmaInvariant")) {
				this.processSigmaInvariantElement(rule, result);
			} else if (rule.getTagName().equals("inline")) {
				this.processInlineElement(rule);
			} else {
				result.add(this.processRuleItem(rule));
			}
		}
		return result;
	}
	
	/**
	 * <inline invoketype="invokestatic">
	 * 		<method .../>
	 * 		...
	 * </inline>
	 */
	protected void processInlineElement(Element inline) {
		this.assertAttributes(inline, INVOKETYPE_ATTRS, EMPTY_ATTRS);
		List<Element> children = this.getElementChildren(inline);
		String invoketype = inline.getAttribute("invoketype");
		if (!invoketype.equals("invokestatic"))
			throw new RuleParsingException("Only static inlining supported!");
		for (Element child : children) {
			MethodJavaLabel method = parseSootMethod(child);
			inlinedMethods.add(method);
		}
	}
	
	protected void 
	processSigmaInvariantElement(
			Element sigmaInvariant,
			List<AxiomNode<JavaLabel, ? extends PEGNode<JavaLabel>>> result) {
		this.assertAttributes(sigmaInvariant, EMPTY_ATTRS, INVOKETYPE_ATTRS);
		List<Element> children = this.getElementChildren(sigmaInvariant);
		
		for (Element child : children) {
			MethodJavaLabel method = parseSootMethod(child);
			ambassador.addSigmaInvariantMethod(method);
			sigmaInvariantMethods.add(method);
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
		
		JavaOperator op = this.getOperatorFromString(value);
		FlowValue<JavaParameter,JavaLabel> flow = 
			FlowValue.<JavaParameter,JavaLabel>createDomain(SimpleJavaLabel.create(op), this.ambassador);
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
	
	
	protected void processMethodCostElement(Element methodCost) throws RuleParsingException {
		this.assertAttributes(methodCost, COST_ATTRS, EMPTY_ATTRS);
		List<? extends Element> children = this.getElementChildren(methodCost);

		for (Element child : children) {
			if (!child.getTagName().equals("method"))
				throw new RuleParsingException("methodCost tag expected method tag as child, found " + child.getTagName());
			MethodJavaLabel label = this.parseSootMethod(child);
			
			int cost;
			try {
				cost = Integer.parseInt(methodCost.getAttribute("cost"));
			} catch (Throwable t) {
				throw new RuleParsingException("methodCost cost value is not a valid integer");
			}
			
			if (cost <= 0)
				throw new RuleParsingException("methodCost cost must be positive");
			
			this.costModel.setMethodInvokeCost(label, cost);
		}
	}
	
	
	protected PeggyVertex<JavaLabel,Integer> getFreshVariable(
			PeggyAxiomizer<JavaLabel,Integer> axiomizer) {
		return axiomizer.getVariable(this.nextVariable++);
	}
	
	protected PeggyVertex<JavaLabel,Integer> parseSimpleDomain(
			Buffer buffer,
			String opname,
			PeggyAxiomizer<JavaLabel,Integer> axiomizer,
			Map<String,PeggyVertex<JavaLabel,Integer>> id2vertex) {
		if (opname.equals("int")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException(opname + " expects 1 string");
			int result;
			try {
				result = Integer.parseInt(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Error parsing constant: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueJavaLabel(IntConstant.v(result)));
		} else if (opname.equals("long")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException(opname + " expects 1 string");
			long result;
			try {
				result = Long.parseLong(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Error parsing constant: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueJavaLabel(LongConstant.v(result)));
		} else if (opname.equals("boolean")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("boolean expects 1 string");
			boolean result = args.get(0).getLeft().equals("true");
			return axiomizer.get(new ConstantValueJavaLabel(
					IntConstant.v(result ? 1 : 0)));
		} else if (opname.equals("char")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException(opname + " expects 1 string");
			if (args.get(0).getLeft().length() == 0)
				throw new RuleParsingException("Must have at least one char for char");
			char first = args.get(0).getLeft().charAt(0);
			return axiomizer.get(new ConstantValueJavaLabel(IntConstant.v((int)first)));
		} else if (opname.equals("float")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("float expects 1 string");
			float result;
			try {
				result = Float.parseFloat(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse float value: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueJavaLabel(FloatConstant.v(result)));
		} else if (opname.equals("double")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("double expects 1 string");
			double result;
			try {
				result = Double.parseDouble(args.get(0).getLeft());
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse double value: " + args.get(0).getLeft());
			}
			return axiomizer.get(new ConstantValueJavaLabel(DoubleConstant.v(result)));
		} else if (opname.equals("string")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("string expects 1 string");
			String result = args.get(0).getLeft();
			return axiomizer.get(new ConstantValueJavaLabel(StringConstant.v(result)));
		} else if (opname.equals("method")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 3 && 
					args.get(0).isLeft() && 
					args.get(1).isLeft() && 
					args.get(2).isLeft()))
				throw new RuleParsingException("Method expects 3 strings");
			String className = args.get(0).getLeft();
			String methodName = args.get(1).getLeft();
			String signatureString = args.get(2).getLeft();
			
			if (!(signatureString.startsWith("(") && signatureString.indexOf(")", 1) > 0)) 
				throw new RuleParsingException("Signature string has no parens: " + signatureString);
			String paramString = signatureString.substring(1, signatureString.indexOf(")", 1));
			String returnString = signatureString.substring(signatureString.indexOf(")",1)+1);
			
			Type returnType;
			try {
				returnType = SootUtils.parseType(returnString);
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse return type: " + returnString);
			}
			
			List<Type> paramTypes;
			try {
				paramTypes = SootUtils.parseParameterTypes(paramString);
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse param type string: " + paramString);
			}

			MethodJavaLabel label = 
				new MethodJavaLabel(className, methodName, returnType, paramTypes);
			return axiomizer.get(label);
		} else if (opname.equals("field")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 3 && 
					args.get(0).isLeft() && 
					args.get(1).isLeft() && 
					args.get(2).isLeft()))
				throw new RuleParsingException("Field expects 3 strings");
			String className = args.get(0).getLeft();
			String fieldName = args.get(1).getLeft();
			String typeString = args.get(2).getLeft();
			
			Type fieldType;
			try {
				fieldType = SootUtils.parseType(typeString);
			} catch (Throwable t) {
				throw new RuleParsingException("Cannot parse field type: " + typeString);
			}

			return axiomizer.get(new FieldJavaLabel(className, fieldName, fieldType));
		} else if (opname.equals("annotation")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() >= 1))
				throw new RuleParsingException("Annotation expects 1 string then nodes");
			
			List<PeggyVertex<JavaLabel,Integer>> params = 
				new ArrayList<PeggyVertex<JavaLabel,Integer>> (args.size());
			for (int i = 1; i < args.size(); i++) {
				if (!args.get(i).isRight())
					throw new RuleParsingException("Annotation expects 1 string then nodes");
				params.add(args.get(i).getRight());
			}
			String value = args.get(0).getLeft();
			
			return axiomizer.get(new AnnotationJavaLabel(value), params);
		} else if (opname.equals("type")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 1 && args.get(0).isLeft()))
				throw new RuleParsingException("Type expects 1 string");
			Type type = SootUtils.parseType(args.get(0).getLeft());
			return axiomizer.get(new TypeJavaLabel(type));
		} else if (opname.equals("null")) {
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (!(args.size() == 0))
				throw new RuleParsingException("Null expects 0 args");
			return axiomizer.get(new ConstantValueJavaLabel(NullConstant.v()));
		} else {
			// op
			JavaOperator operator = getOperatorFromString(opname);
			List<? extends DisjointUnion<String,PeggyVertex<JavaLabel,Integer>>> args = 
				this.parseSimpleOperandArguments(buffer, axiomizer, id2vertex);
			if (operator.getArgumentCount() >= 0 && 
				operator.getArgumentCount() != args.size())
				throw new RuleParsingException("Invalid number of arguments for operator " + operator.name() + ": " + args.size());

			List<PeggyVertex<JavaLabel,Integer>> params = 
				new ArrayList<PeggyVertex<JavaLabel,Integer>>(args.size());
			for (DisjointUnion<String,PeggyVertex<JavaLabel,Integer>> dj : args) {
				if (!dj.isRight())
					throw new RuleParsingException("Operator demands all node parameters");
				params.add(dj.getRight());
			}

			JavaLabel label = SimpleJavaLabel.create(operator);
			return axiomizer.get(label, params);
		}
	}
	
	
	
	
	protected PeggyVertex<JavaLabel,Integer> getAndVertex(
			PeggyAxiomizer<JavaLabel,Integer> axiomizer, 
			List<? extends PeggyVertex<JavaLabel,Integer>> args) {
		return axiomizer.get(new BasicJavaLabel(BasicOp.And), args);
	}
	protected PeggyVertex<JavaLabel,Integer> getOrVertex(
			PeggyAxiomizer<JavaLabel,Integer> axiomizer, 
			List<? extends PeggyVertex<JavaLabel,Integer>> args) {
		return axiomizer.get(new BasicJavaLabel(BasicOp.Or), args);
	}
	
	
	protected PeggyVertex<JavaLabel,Integer> parseVertex(
			final PeggyAxiomizer<JavaLabel,Integer> axiomizer, 
			final Element element, 
			final Map<String,PeggyVertex<JavaLabel,Integer>> id2vertex) {
		
		Function<JavaLabel,PeggyVertex<JavaLabel,Integer>> helper = 
			new Function<JavaLabel,PeggyVertex<JavaLabel,Integer>>() {
				public PeggyVertex<JavaLabel,Integer> get(JavaLabel label) {
					PeggyVertex<JavaLabel,Integer> node = axiomizer.get(label);
					if (element.hasAttribute("id")) {
						String id = element.getAttribute("id");
						if (id2vertex.containsKey(id))
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
			
			JavaLabel label;
			JavaOperator operator = this.getOperatorFromString(value);
			if (operator.getArgumentCount() >= 0 && 
				operator.getArgumentCount() != elementchildren.size())
				throw new RuleParsingException("Invalid number of arguments for operator " + operator.getLabel() + ": " + elementchildren.size());
			label = SimpleJavaLabel.create(operator);
			return this.parseOperator(axiomizer, label, element, id2vertex);
		} else if (elementName.equals("simple")) {
			this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
			String text = this.getAllText(element);
			
			debug("All text: " + text);
			
			Buffer buffer = new Buffer(text);
			return parseSimpleVertex(buffer, axiomizer, id2vertex);
		} else if (elementName.equals("annotation")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			String value = element.getAttribute("value");
			JavaLabel label = new AnnotationJavaLabel(value);
			return this.parseOperator(axiomizer, label, element, id2vertex);
		} else if (elementName.equals("method")) {
			MethodJavaLabel methodLabel = this.parseSootMethod(element);
			return helper.get(methodLabel);
		} else if (elementName.equals("field")) {
			this.assertAttributes(element, FIELD_METHOD_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String nameAttr = element.getAttribute("name");
			String classAttr = element.getAttribute("class");
			String signatureAttr = element.getAttribute("signature");

			Type fieldType = SootUtils.parseType(signatureAttr);
			FieldJavaLabel fieldLabel = new FieldJavaLabel(classAttr, nameAttr, fieldType);

			return helper.get(fieldLabel);
		} else if (elementName.equals("type")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			Type type = SootUtils.parseType(value);
			TypeJavaLabel typeLabel = new TypeJavaLabel(type);

			return helper.get(typeLabel);
		} else if (elementName.equals("intconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);
			
			String value = element.getAttribute("value");
			ConstantValueJavaLabel intLabel = null;
			try {
				intLabel = new ConstantValueJavaLabel(IntConstant.v(Integer.parseInt(value)));
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid integer constant string: " + value, t);
			}

			return helper.get(intLabel);
		} else if (elementName.equals("longconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			ConstantValueJavaLabel longLabel = null;
			try {
				longLabel = new ConstantValueJavaLabel(LongConstant.v(Long.parseLong(value)));
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid long constant string: " + value, t);
			}

			return helper.get(longLabel);
		} else if (elementName.equals("floatconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			ConstantValueJavaLabel floatLabel = null;
			try {
				floatLabel = new ConstantValueJavaLabel(FloatConstant.v(Float.parseFloat(value)));
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid float constant string: " + value, t);
			}

			return helper.get(floatLabel);
		} else if (elementName.equals("doubleconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = element.getAttribute("value");
			ConstantValueJavaLabel doubleLabel = null;
			try {
				doubleLabel = new ConstantValueJavaLabel(DoubleConstant.v(Double.parseDouble(value)));
			} catch (Throwable t) {
				throw new RuleParsingException("Invalid double constant string: " + value, t);
			}
			
			return helper.get(doubleLabel);
		} else if (elementName.equals("stringconstant")) {
			this.assertAttributes(element, VALUE_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);

			String value = this.unescape(element.getAttribute("value"));
			return helper.get(new ConstantValueJavaLabel(StringConstant.v(value)));
		} else if (elementName.equals("nullconstant")) {
			this.assertAttributes(element, EMPTY_ATTRS, ID_ATTRS);
			this.assertNoElementChildren(element);
			return helper.get(new ConstantValueJavaLabel(NullConstant.v()));
		} else if (elementName.equals("variable")) {
			this.assertAttributes(element, EMPTY_ATTRS, ID_ATTRS);

			PeggyVertex<JavaLabel,Integer> result = axiomizer.getVariable(this.nextVariable++);
			if (element.hasAttribute("id")) {
				String id = element.getAttribute("id");
				if (id2vertex.containsKey(id))
					throw new RuleParsingException("Duplicate id found: " + id);
				id2vertex.put(id, result);
			}
			return result;
		} else if (elementName.equals("ref")) {
			this.assertAttributes(element, ID_ATTRS, EMPTY_ATTRS);
			this.assertNoElementChildren(element);
			String id = element.getAttribute("id");
			if (id2vertex.containsKey(id)) {
				PeggyVertex<JavaLabel,Integer> result = id2vertex.get(id);
				if (result == null) {
					result = axiomizer.createPlaceHolder();
					id2vertex.put(id, result);
				}
				return result;
			} else {
				throw new RuleParsingException("Reference ID not defined: " + id);
			}
		} else {
			// assume it's a non-domain operator
			return this.parseNondomainVertex(axiomizer, element, id2vertex);
		}
	}
	
	
	private PeggyVertex<JavaLabel,Integer> parseNondomainVertex(
			final PeggyAxiomizer<JavaLabel,Integer> axiomizer, 
			Element element, 
			final Map<String,PeggyVertex<JavaLabel,Integer>> id2vertex) { 
		
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
			final List<PeggyVertex<JavaLabel,Integer>> childVertices = 
				new ArrayList<PeggyVertex<JavaLabel,Integer>>(children.size());

			Function<Element,String> idhelper = 
				new Function<Element,String>() {
					public String get(Element arg) {
						boolean hasID = arg.hasAttribute("id");
						String id = null;
						if (hasID) {
							id = arg.getAttribute("id");
							if (id2vertex.containsKey(id))
								throw new RuleParsingException("Duplicate id found: " + id);
							id2vertex.put(id, null);
						}
						
						for (Element child : children) {
							childVertices.add(parseVertex(axiomizer, child, id2vertex));
						}
						return id;
					}
				};

				
			if (value.equals("theta")) {
				int index = helper.get(element);
				if (children.size() != 2)
					throw new RuleParsingException("theta needs 2 children");
				
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getTheta(index, childVertices.get(0), childVertices.get(1));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("eval")) {
				int index = helper.get(element);
				if (children.size() != 2)
					throw new RuleParsingException("eval needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getEval(index, childVertices.get(0), childVertices.get(1));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("pass")) {
				int index = helper.get(element);
				if (children.size() != 1)
					throw new RuleParsingException("pass needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getPass(index, childVertices.get(0));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("shift")) {
				int index = helper.get(element);
				if (children.size() != 1)
					throw new RuleParsingException("shift needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getShift(index, childVertices.get(0));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			}
			
			
			else if (value.equals("phi")) {
				if (children.size() != 3)
					throw new RuleParsingException("phi needs 3 children");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getPhi(childVertices.get(0), childVertices.get(1), childVertices.get(2));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("zero")) {
				if (children.size() != 0)
					throw new RuleParsingException("zero can take no children");
				return axiomizer.getZero();
			} else if (value.equals("negate")) {
				if (children.size() != 1)
					throw new RuleParsingException("negate needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getNegate(childVertices.get(0));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("equals")) {
				if (children.size() != 2)
					throw new RuleParsingException("equals needs 2 children");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getEquals(childVertices.get(0), childVertices.get(1));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else if (value.equals("successor")) {
				if (children.size() != 1)
					throw new RuleParsingException("successor needs 1 child");
				String id = idhelper.get(element);
				PeggyVertex<JavaLabel,Integer> result = axiomizer.getSuccessor(childVertices.get(0));
				if (id!=null) {
					if (id2vertex.get(id) != null) {
						id2vertex.get(id).replaceWith(result);
					}
					id2vertex.put(id, result);
				}
				return result;
			} else {
				throw new RuleParsingException("Unknown nondomain value: " + value);
			}
		} else {
			throw new RuleParsingException("Unknown tag: " + elementName);
		}
	}
	
	private PeggyVertex<JavaLabel,Integer> parseOperator(
			PeggyAxiomizer<JavaLabel,Integer> axiomizer,
			JavaLabel label, Element element, 
			Map<String,PeggyVertex<JavaLabel,Integer>> id2vertex) { 

		List<? extends Element> childElements = this.getElementChildren(element);
		List<PeggyVertex<JavaLabel,Integer>> childVertices = 
			new ArrayList<PeggyVertex<JavaLabel,Integer>>(childElements.size());
		
		boolean hasID = element.hasAttribute("id");
		String id = null;
		if (hasID) {
			id = element.getAttribute("id");
			if (id2vertex.containsKey(id))
				throw new RuleParsingException("Duplicate id found: " + id);
			id2vertex.put(id, null);
		}
		
		for (Element child : childElements) {
			childVertices.add(this.parseVertex(axiomizer, child, id2vertex));
		}
		
		PeggyVertex<JavaLabel,Integer> result = axiomizer.get(label, childVertices);
		if (id != null) {
			if (id2vertex.get(id) != null) {
				id2vertex.get(id).replaceWith(result);
			}
			id2vertex.put(id, result);
		}

		return result;
	}
	

	////////////////////////////////////////////
	
	private MethodJavaLabel parseSootMethod(Element element) throws RuleParsingException {
		this.assertAttributes(element, FIELD_METHOD_ATTRS, ID_ATTRS);
		this.assertNoElementChildren(element);

		String nameAttr = element.getAttribute("name");
		String classAttr = element.getAttribute("class");
		String signatureAttr = element.getAttribute("signature");
		
		if (!signatureAttr.startsWith("("))
			throw new RuleParsingException("Invalid method signature: " + signatureAttr);
		int closeParen = signatureAttr.indexOf(')', 1);
		if (closeParen < 0)
			throw new RuleParsingException("Invalid method signature: " + signatureAttr);
		String paramString = signatureAttr.substring(1, closeParen);
		List<Type> paramTypes = SootUtils.parseParameterTypes(paramString);
		Type returnType = SootUtils.parseType(signatureAttr.substring(closeParen + 1));

		return new MethodJavaLabel(classAttr, nameAttr, returnType, paramTypes);
	}

	private String unescape(String str) {
		// FIX
		return str;
	}

	private JavaOperator getOperatorFromString(String op) {
		op = op.toLowerCase();
		JavaOperator[] operators = JavaOperator.values();
		for (JavaOperator operator : operators) {
			if (operator.getLabel().toLowerCase().equals(op))
				return operator;
		}
		throw new RuleParsingException("Invalid operator name: " + op);
	}
}
