package peggy.represent.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import peggy.input.RuleParsingException;
import peggy.represent.CREGXML2PEG;
import peggy.represent.PEGInfo;
import soot.RefType;
import soot.SootMethodRef;
import soot.Type;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.BasicOp;
import eqsat.FlowValue;
import eqsat.OpAmbassador;

/**
 * This is an implementation of CREGXML2PEG for Java PEGs.
 */
public class JavaCREGXML2PEG extends CREGXML2PEG<JavaLabel,JavaParameter,JavaReturn> {
	private final MethodJavaLabel methodLabel;
	private final SootMethodRef methodRef;
	private ThisJavaParameter thisParam;
	private ArgumentJavaParameter[] parameters;
	private final OpAmbassador<JavaLabel> ambassador;
	
	public JavaCREGXML2PEG(
			MethodJavaLabel _label, 
			SootMethodRef _method, 
			OpAmbassador<JavaLabel> _ambassador) {
		this.ambassador = _ambassador;
		this.methodLabel = _label;
		this.methodRef = _method;
		this.parameters = 
			new ArgumentJavaParameter[this.methodLabel.getParameterTypes().size()];
	}
	
	protected PEGInfo<JavaLabel,JavaParameter,JavaReturn> getPEGInfo(
			CRecursiveExpressionGraph<FlowValue<JavaParameter,JavaLabel>> graph,
			Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> outputs) {
		return new JavaPEGInfo(graph, outputs);
	}
	
	protected OpAmbassador<JavaLabel> getAmbassador() {
		return this.ambassador;
	}
	
	protected JavaParameter getParam(String paramname) {
		if (paramname.equals("sigma")) {
			return JavaParameter.SIGMA;
		} else if (paramname.equals("this")) {
			if (this.thisParam == null) {
				this.thisParam = new ThisJavaParameter(
						new ThisJavaVariable(RefType.v(this.methodRef.declaringClass())));
			}
			return this.thisParam;
		} else if (paramname.startsWith("param")) {
			int index;
			try {
				index = Integer.parseInt(paramname.substring(5));
			} catch (Throwable t) {
				throw new RuleParsingException("Expecting name 'param###'");
			}
			if (index<0 || index>=this.parameters.length)
				throw new RuleParsingException("Index out of bounds");
			
			if (this.parameters[index] == null) {
				this.parameters[index] = 
					new ArgumentJavaParameter(
							new ArgumentJavaVariable(
									this.methodLabel,
									index));
			}
			return this.parameters[index];
		} else {
			throw new RuleParsingException("Unrecognized param name: " + paramname);
		}
	}

	protected JavaReturn getReturn(String rootname) {
		if (rootname.equals("sigma")) {
			return JavaReturn.SIGMA;
		} else if (rootname.equals("value")) {
			return JavaReturn.VALUE;
		} else {
			throw new RuleParsingException("Unrecognized return name: " + rootname);
		}
	}

	private static final Object[] TYPE_INFO = {"type", 0, new String[]{"value"}, new String[]{"id"}};
	private static final Object[] GETEXCEPTION_INFO = {"getexception", 1, new String[]{"type"}, new String[]{"id"}};
	private static final Object[] ISEXCEPTION_INFO = {"isexception", 1, new String[]{"type"}, new String[]{"id"}};
	private static final Object[] METHOD_INFO = {"method", 0, new String[]{"name", "class", "signature"}, new String[]{"id"}};
	private static final Object[] FIELD_INFO = {"field", 0, new String[]{"name", "class", "signature"}, new String[]{"id"}};
	private static final Object[] BASIC_INFO = {"basic", 0, new String[]{"name"}, new String[]{"id"}};
	private static final Object[] OP_INFO = {"op", -1, new String[]{"name"}, new String[]{"id"}};
	private static final Object[] CONSTANT_INFO = {"constant", 0, new String[]{"type", "value"}, new String[]{"id"}};
	
	protected Vertex<FlowValue<JavaParameter, JavaLabel>> parseDomainNode(
			Element element,
			CRecursiveExpressionGraph<FlowValue<JavaParameter, JavaLabel>> graph,
			Map<String, Vertex<FlowValue<JavaParameter, JavaLabel>>> id2vertex) {
		final String tag = element.getTagName();
		List<Element> children = new ArrayList<Element>();

		if (tag.equals("op")) {
			assertElement(element, children, OP_INFO);
			String value = element.getAttribute("name");
			
			JavaOperator op = null;
			for (JavaOperator bop : JavaOperator.values()) {
				if (bop.getLabel().equals(value)) {
					op = bop;
					break;
				}
			}
			if (op == null)
				throw new RuleParsingException("Unrecognized simple op: " + value);
			
			this.registerID(element, null, id2vertex);
			List<Vertex<FlowValue<JavaParameter, JavaLabel>>> childNodes = 
				new ArrayList<Vertex<FlowValue<JavaParameter, JavaLabel>>>(children.size());
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			Vertex<FlowValue<JavaParameter, JavaLabel>> result =
				this.getLabelNode(graph, SimpleJavaLabel.create(op), childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (tag.equals("basic")) {
			assertElement(element, children, BASIC_INFO);
			String value = element.getAttribute("name");
			
			JavaLabel label;
			if (value.equals("true")) label = new BasicJavaLabel(BasicOp.True);
			else if (value.equals("false")) label = new BasicJavaLabel(BasicOp.False);
			else if (value.equals("negate")) label = new BasicJavaLabel(BasicOp.Negate);
			else if (value.equals("and")) label = new BasicJavaLabel(BasicOp.And);
			else if (value.equals("or")) label = new BasicJavaLabel(BasicOp.Or);
			else if (value.equals("equals")) label = new BasicJavaLabel(BasicOp.Equals);
			else throw new RuleParsingException("Unrecognized basic op: " + value);

			this.registerID(element, null, id2vertex);
			List<Vertex<FlowValue<JavaParameter, JavaLabel>>> childNodes = 
				new ArrayList<Vertex<FlowValue<JavaParameter, JavaLabel>>>(children.size());
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			Vertex<FlowValue<JavaParameter, JavaLabel>> result =
				this.getLabelNode(graph, label, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (tag.equals("constant")) {
			assertElement(element, null, CONSTANT_INFO);
			String type = element.getAttribute("type");
			String value = element.getAttribute("value");
			
			JavaLabel label;
			if (type.equals("string")) {
				label = new ConstantValueJavaLabel(StringConstant.v(value));
			} else if (type.equals("null")) {
				// no value
				label = new ConstantValueJavaLabel(NullConstant.v());
			} else if (type.equals("int")) {
				try {
					label = new ConstantValueJavaLabel(IntConstant.v(Integer.parseInt(value)));
				} catch (Throwable t) {
					throw new RuleParsingException("Bad int value: " + value);
				}
			} else if (type.equals("long")) {
				try {
					label = new ConstantValueJavaLabel(LongConstant.v(Long.parseLong(value)));
				} catch (Throwable t) {
					throw new RuleParsingException("Bad long value: " + value);
				}
			} else if (type.equals("float")) {
				try {
					label = new ConstantValueJavaLabel(FloatConstant.v(Float.parseFloat(value)));
				} catch (Throwable t) {
					throw new RuleParsingException("Bad float value: " + value);
				}
			} else if (type.equals("double")) {
				try {
					label = new ConstantValueJavaLabel(DoubleConstant.v(Double.parseDouble(value)));
				} catch (Throwable t) {
					throw new RuleParsingException("Bad double value: " + value);
				}
			} else {
				throw new RuleParsingException("Unrecognized constant type: " + type); 
			}
			
			Vertex<FlowValue<JavaParameter, JavaLabel>> result = 
				this.getLabelNode(graph, label);
			return this.registerID(element, result, id2vertex);
		} else if (tag.equals("field")) {
			assertElement(element, null, FIELD_INFO);
			String nameString = element.getAttribute("name");
			String classString = element.getAttribute("class");
			String signatureString = element.getAttribute("signature");
			
			Type fieldType = SootUtils.parseType(signatureString);
			
			FieldJavaLabel label = 
				new FieldJavaLabel(classString, nameString, fieldType);
			Vertex<FlowValue<JavaParameter, JavaLabel>> result = 
				this.getLabelNode(graph, label);
			return this.registerID(element, result, id2vertex);
		} else if (tag.equals("method")) {
			assertElement(element, null, METHOD_INFO);
			String nameString = element.getAttribute("name");
			String classString = element.getAttribute("class");
			String signatureString = element.getAttribute("signature");
			
			int lparenIndex = signatureString.indexOf('(');
			int rparenIndex = signatureString.indexOf(')', lparenIndex);
			String paramString = signatureString.substring(lparenIndex+1, rparenIndex);
			String returnString = signatureString.substring(rparenIndex+1);
			
			List<Type> paramTypes = SootUtils.parseParameterTypes(paramString);
			Type returnType = SootUtils.parseType(returnString);
			
			MethodJavaLabel label = 
				new MethodJavaLabel(classString, nameString, returnType, paramTypes);
			Vertex<FlowValue<JavaParameter, JavaLabel>> result = 
				this.getLabelNode(graph, label);
			return this.registerID(element, result, id2vertex);
		} else if (tag.equals("isexception")) {
			assertElement(element, children, ISEXCEPTION_INFO);
			Type type = SootUtils.parseType(element.getAttribute("type"));
			if (!(type instanceof RefType))
				throw new RuleParsingException("Exception type must be RefType");
			
			this.registerID(element, null, id2vertex);
			Vertex<FlowValue<JavaParameter, JavaLabel>> expr = 
				this.parseNode(children.get(0), graph, id2vertex);
			Vertex<FlowValue<JavaParameter, JavaLabel>> result = 
				this.getLabelNode(graph, new IsExceptionJavaLabel((RefType)type), Collections.singletonList(expr));
			return this.patchID(element, result, id2vertex);
		} else if (tag.equals("getexception")) {
			assertElement(element, children, GETEXCEPTION_INFO);
			Type type = SootUtils.parseType(element.getAttribute("type"));
			if (!(type instanceof RefType))
				throw new RuleParsingException("Exception type must be RefType");
			
			this.registerID(element, null, id2vertex);
			Vertex<FlowValue<JavaParameter, JavaLabel>> expr = 
				this.parseNode(children.get(0), graph, id2vertex);
			Vertex<FlowValue<JavaParameter, JavaLabel>> result = 
				this.getLabelNode(graph, new GetExceptionJavaLabel((RefType)type), Collections.singletonList(expr));
			return this.patchID(element, result, id2vertex);
		} else if (tag.equals("type")) {
			assertElement(element, null, TYPE_INFO);
			Type type = SootUtils.parseType(element.getAttribute("value"));
			return this.registerID(
					element, 
					this.getLabelNode(graph, new TypeJavaLabel(type)), 
					id2vertex);
		} else {
			throw new RuleParsingException("Unrecognized tag: " + tag);
		}
	}
}
