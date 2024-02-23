package peggy.represent;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.instructions.ComparisonPredicate;
import llvm.types.Type;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantInlineASM;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FloatingPointValue;
import llvm.values.IntegerValue;
import llvm.values.Value;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.ArgumentLLVMVariable;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.represent.llvm.ParamAttrLLVMLabel;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * Prints out the peg as XML, which can be reparsed into a new CREG.
 */
public class PEG2XML {
	private static final String TAB = "   ";
	
	private static class Helper {
		private int tab;
		private String tabstr;
		private PrintStream out;
		
		public Helper(PrintStream _out, int _tab) {
			this.out = _out;
			this.tab = _tab;
			this.tabstr = makeTabString();
		}
		private String makeTabString() {
			StringBuilder builder = new StringBuilder(this.tab*3);
			for (int i = 0; i < this.tab; i++)
				builder.append(TAB);
			return builder.toString();
		}
		public void tab() {
			this.tab++;
			this.tabstr = makeTabString();
		}
		public void untab() {
			this.tab--;
			this.tabstr = makeTabString();
		}
		public void open(
				String tag,
				String... attrs) {
			out.print(tabstr + "<" + tag);
			if (attrs != null) {
				if (attrs.length%2 != 0)
					throw new IllegalArgumentException("Attrs should be in pairs");
				for (int i = 0; i < attrs.length; i+=2) {
					out.print(" " + attrs[i] + "=\"" + xml_escape(attrs[i+1]) + "\"");
				}
			}
			out.println(">");
		}
		public void close(String tag) {
			out.println(tabstr + "</" + tag + ">");
		}
		public void openclose(
				String tag,
				String... attrs) {
			out.print(tabstr + "<" + tag);
			if (attrs != null) {
				if (attrs.length%2 != 0)
					throw new IllegalArgumentException("Attrs should be in pairs");
				for (int i = 0; i < attrs.length; i+=2) {
					out.print(" " + attrs[i] + "=\"" + xml_escape(attrs[i+1]) + "\"");
				}
			}
			out.println("/>");
		}
	}
	
	public static void peg2xml(
			PrintStream out, 
			Map<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> rootmap) {
		Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,String> node2id = 
			new HashMap<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,String>();

		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.println("<peg>");
		
		Helper helper = new Helper(out, 2);
		out.println(TAB + "<rootValue>");
		peg2xml_helper(rootmap.get(LLVMReturn.VALUE), node2id, helper);
		out.println(TAB + "</rootValue>");
		
		out.println(TAB + "<rootSigma>");
		peg2xml_helper(rootmap.get(LLVMReturn.SIGMA), node2id, helper);
		out.println(TAB + "</rootSigma>");
		
		out.println("</peg>");
	}
	
	
	private static void peg2xml_helper(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> node,
			Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,String> node2id,
			Helper helper) {
		if (node2id.containsKey(node)) {
			// ref tag
			helper.openclose(
					"ref", 
					"id", node2id.get(node).toString());
			return;
		}
		String idstr = "" + node2id.size();
		node2id.put(node, idstr);
		
		FlowValue<LLVMParameter,LLVMLabel> flow = node.getLabel();
		if (flow.isDomain()) {
			LLVMLabel label = flow.getDomain();
			if (label.isSimple()) {
				LLVMOperator op = label.getSimpleSelf().getOperator();
				helper.open("op",
						"value", op.name().toLowerCase(),
						"id", idstr);
				helper.tab();
				for (int i = 0; i < node.getChildCount(); i++) {
					peg2xml_helper(node.getChild(i), node2id, helper);
				}
				helper.untab();
				helper.close("op");
			} else if (label.isAlias()) {
				AliasLLVMLabel alias = label.getAliasSelf();
				helper.openclose("alias",
						"name", alias.getName(),
						"type", alias.getType().toString(),
						"id", idstr);
			} else if (label.isFunction()) {
				FunctionLLVMLabel func = label.getFunctionSelf();
				helper.openclose("function",
						"name", func.getFunctionName(),
						"type", func.getType().toString(),
						"id", idstr);
			} else if (label.isGlobal()) {
				GlobalLLVMLabel global = label.getGlobalSelf();
				helper.openclose("global",
						"name", global.getName(),
						"type", global.getType().toString(),
						"id", idstr);
			} else if (label.isAnnotation()) {
				if (label.getAnnotationSelf().isString()) {
					String value = label.getAnnotationSelf().getStringSelf().getValue();
					helper.open("annotation",
							"value", value,
							"id", idstr);
					helper.tab();
					for (int i = 0; i < node.getChildCount(); i++) {
						peg2xml_helper(node.getChild(i), node2id, helper);
					}
					helper.untab();
					helper.close("annotation");
				} else {
					throw new IllegalArgumentException("Unrecognized annotation: " + label);
				}
			} else if (label.isBinop()) {
				Binop binop = label.getBinopSelf().getOperator();
				helper.open("binop",
						"type", binop.name().toLowerCase(),
						"id", idstr);
				helper.tab();
				peg2xml_helper(node.getChild(0), node2id, helper);
				peg2xml_helper(node.getChild(1), node2id, helper);
				helper.untab();
				helper.close("binop");
			} else if (label.isCast()) {
				Cast cast = label.getCastSelf().getOperator();
				helper.open("cast",
						"type", cast.name().toLowerCase(),
						"id", idstr);
				helper.tab();
				peg2xml_helper(node.getChild(0), node2id, helper);
				peg2xml_helper(node.getChild(1), node2id, helper);
				helper.untab();
				helper.close("cast");
			} else if (label.isCmp()) {
				ComparisonPredicate pred = label.getCmpSelf().getPredicate();
				String name = (pred.isInteger() ? "icmp" : "fcmp");
				helper.open(name,
						"type", pred.getLabel(),
						"id", idstr);
				helper.tab();
				peg2xml_helper(node.getChild(0), node2id, helper);
				peg2xml_helper(node.getChild(1), node2id, helper);
				helper.untab();
				helper.close(name);
			} else if (label.isConstantValue()) {
				peg2xml_constant_value(idstr, label.getConstantValueSelf().getValue(), helper);
			} else if (label.isNumeral()) {
				helper.openclose("numeral",
						"value", label.getNumeralSelf().getValue()+"",
						"id", idstr);
			} else if (label.isParamAttr()) {
				ParamAttrLLVMLabel pal = label.getParamAttrSelf();
				helper.openclose("paramAttr",
						"bits", pal.getAttributes().getBits()+"",
						"id", idstr);
			} else if (label.isType()) {
				Type type = label.getTypeSelf().getType();
				helper.openclose("type",
						"value", type.toString(),
						"id", idstr);
			} else {
				throw new IllegalArgumentException("Invalid in PEG: " + label);
			}
//		} else if (flow.isSplit()) {
//			helper.open("split");
//			helper.tab();
//			peg2xml_helper(node.getChild(0), node2id, helper);
//			helper.untab();
//			helper.close("split");
		} else if (flow.isShortCircuitAnd()) {	
			helper.open("nondomain",
					"value", "scand",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isShortCircuitOr()) {	
			helper.open("nondomain",
					"value", "scor",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isAnd()) {
			helper.open("nondomain",
					"value", "and",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isOr()) {
			helper.open("nondomain",
					"value", "or",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isEquals()) {
			helper.open("nondomain",
					"value", "equals",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isEval()) {
			helper.open("nondomain",
					"index", flow.getLoopDepth()+"",
					"value", "eval",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isNegate()) {
			helper.open("nondomain",
					"value", "negate",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isPass()) {
			helper.open("nondomain",
					"index", flow.getLoopDepth()+"",
					"value", "pass",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isPhi()) {
			helper.open("nondomain",
					"value", "phi",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			peg2xml_helper(node.getChild(2), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isSuccessor()) {
			helper.open("nondomain",
					"value", "successor",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isTheta()) {
			helper.open("nondomain",
					"index", flow.getLoopDepth()+"",
					"value", "theta",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			peg2xml_helper(node.getChild(1), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isShift()) {
			helper.open("nondomain",
					"index", flow.getLoopDepth()+"",
					"value", "shift",
					"id", idstr);
			helper.tab();
			peg2xml_helper(node.getChild(0), node2id, helper);
			helper.untab();
			helper.close("nondomain");
		} else if (flow.isZero()) {
			helper.openclose("nondomain",
					"value", "zero",
					"id", idstr);
		} else if (flow.isParameter()) {
			// func arguments
			LLVMParameter param = flow.getParameter();
			if (param.isSigma()) {
				helper.openclose("sigma",
						"id", idstr);
			} else if (param.isArgument()) {
				ArgumentLLVMVariable arg = param.getArgumentSelf().getVariableVersion();
				helper.openclose("param",
						"index", arg.getIndex()+"",
						"type", arg.getType().toString(),
						"id", idstr);
			} else
				throw new IllegalArgumentException("Unexpected parameter: " + param);
		} else {
			throw new IllegalArgumentException("Invalid in a PEG: " + flow);	
		}
	}
	private static void peg2xml_constant_value(
			String idstr,
			Value value,
			Helper helper) {
		// integer
		// float
		// undef
		// nulls
		// const structs
		// const arrays
		// const vectors
		// inline asm
		
		if (value.isInteger()) {
			IntegerValue iv = value.getIntegerSelf();
			StringBuilder bits = new StringBuilder(iv.getWidth());
			for (int i = iv.getWidth()-1; i >= 0; i--) {
				bits.append(iv.getBit(i) ? '1' : '0');
			}
			if (idstr==null)
				helper.openclose("integer",
						"width", iv.getWidth()+"",
						"value", bits.toString());
			else
				helper.openclose("integer",
						"width", iv.getWidth()+"",
						"value", bits.toString(),
						"id", idstr);
		} else if (value.isFloatingPoint()) {
			FloatingPointValue fv = value.getFloatingPointSelf();
			int width = fv.getType().getKind().getTypeSize();
			StringBuilder bits = new StringBuilder(width);
			for (int i = width-1; i >= 0; i--) {
				bits.append(fv.getBit(i) ? '1' : '0');
			}
			if (idstr==null)
				helper.openclose("fp",
						"kind", fv.getType().getKind().name().toLowerCase(),
						"value", bits.toString());
			else
				helper.openclose("fp",
						"kind", fv.getType().getKind().name().toLowerCase(),
						"value", bits.toString(),
						"id", idstr);
		} else if (value.isUndef()) {
			Type type = value.getUndefSelf().getType();
			if (idstr==null)
				helper.openclose("undef",
						"type", type.toString());
			else
				helper.openclose("undef",
						"type", type.toString(),
						"id", idstr);
		} else if (value.isConstantStructure()) {
			// in terms of other constants
			if (Value.isNullConstant(value)) {
				if (idstr==null)
					helper.openclose("null",
							"type", value.getType().toString());
				else
					helper.openclose("null",
							"type", value.getType().toString(),
							"id", idstr);
			} else {
				ConstantStructureValue csv = value.getConstantStructureSelf();
				if (idstr==null)
					helper.open("structure",
							"type", csv.getType().toString());
				else
					helper.open("structure",
							"type", csv.getType().toString(),
							"id", idstr);
					
				helper.tab();
				for (int i = 0; i <csv.getNumFields(); i++) {
					peg2xml_constant_value(null, csv.getFieldValue(i), helper);
				}
				helper.untab();
				helper.close("structure");
			}
		} else if (value.isConstantArray()) {
			// in terms of other constants
			if (Value.isNullConstant(value)) {
				if (idstr==null)
					helper.openclose("null",
							"type", value.getType().toString());
				else
					helper.openclose("null",
							"type", value.getType().toString(),
							"id", idstr);
			} else {
				ConstantArrayValue cav = value.getConstantArraySelf();
				if (idstr==null)
					helper.open("array",
							"type", value.getType().toString());
				else
					helper.open("array",
							"type", value.getType().toString(),
							"id", idstr);
					
				helper.tab();
				for (int i = 0; i < cav.getNumElements().signedValue(); i++) {
					peg2xml_constant_value(null, cav.getElement(i), helper);
				}
				helper.untab();
				helper.close("array");
			}
		} else if (value.isConstantNullPointer()) {
			if (idstr==null)
				helper.openclose("null",
						"type", value.getType().toString());
			else
				helper.openclose("null",
						"type", value.getType().toString(),
						"id", idstr);
		} else if (value.isConstantVector()) {
			// in terms of other constants
			if (Value.isNullConstant(value)) {
				if (idstr==null)
					helper.openclose("null",
							"type", value.getType().toString());
				else
					helper.openclose("null",
							"type", value.getType().toString(),
							"id", idstr);
			} else {
				ConstantVectorValue cvv = value.getConstantVectorSelf();
				if (idstr==null)
					helper.open("vector",
							"type", value.getType().toString());
				else
					helper.open("vector",
							"type", value.getType().toString(),
							"id", idstr);
					
				helper.tab();
				for (int i = 0; i < cvv.getNumElements().signedValue(); i++) {
					peg2xml_constant_value(null, cvv.getElement(i), helper);
				}
				helper.untab();
				helper.close("vector");
			}
		} else if (value.isInlineASM()) {
			ConstantInlineASM asm = value.getInlineASMSelf();
			if (idstr==null)
				helper.openclose("inlineasm",
						"asm", asm.getASMString(),
						"constraint", asm.getConstraintString(),
						"sideEffects", asm.hasSideEffects()+"",
						"type", asm.getType().toString());
			else
				helper.openclose("inlineasm",
						"asm", asm.getASMString(),
						"constraint", asm.getConstraintString(),
						"sideEffects", asm.hasSideEffects()+"",
						"type", asm.getType().toString(),
						"id", idstr);
		} else {
			throw new IllegalArgumentException("Unexpected value: " + value);
		}
	}
	
	
	private static String xml_escape(String str) {
		StringBuilder result = new StringBuilder(str.length()*2);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '"':
				result.append("&quot;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '\'':
				result.append("&apos;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			default:
				result.append(c);
				break;
			}
		}
		return result.toString();
	}
	
	
	
	private static boolean equals(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> node1,
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> node2,
			Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> map) {
		if (map.containsKey(node1)) {
			return map.get(node1).equals(node2);
		}
		map.put(node1, node2);
		
		if (!node1.getLabel().equals(node2.getLabel()))
			return false;
		if (node1.getChildCount() != node2.getChildCount())
			return false;
		
		for (int i = 0; i < node1.getChildCount(); i++) {
			if (!equals(node1.getChild(i), node2.getChild(i), map))
				return false;
		}
		return true;
	}
	
	/*
	public static void main(String args[]) throws Throwable {
		Module module = TestBuildCFG.buildModule(args[0]);
		FunctionBody body = TestBuildCFG.getBodyByName(module, args[1]);
		boolean exceptions = (args.length > 2 ? args[2].equals("true") : false);
		boolean linearReads = (args.length > 3 ? args[3].equals("true") : false);
		
		DataLayout layout = (module.getDataLayout() == null ?
				new DataLayout() :
				new DataLayout(module.getDataLayout())); 
		
		LLVMOpAmbassador ambassador = 
			new LLVMOpAmbassador(
					new DefaultLLVMConstantFolder(layout),
					GEPForcingPolicy.NONE,
					exceptions, 
					linearReads);
		
		LLVMCFG incfg = TestBuildCFG.buildCFG(module, body, ambassador);

		Map<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> outputs = 
			new HashMap<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		
		CRecursiveExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>> graph = 
			TestBuildPEG.buildPEG(incfg, outputs);
		
		FileOutputStream fout = new FileOutputStream("peg.xml");
		peg2xml(new PrintStream(fout), outputs);
		fout.close();

		CREGXML2PEG xml2peg = new CREGXML2PEG(ambassador);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new FileInputStream("peg.xml"));
		
		FunctionLLVMLabel label = new FunctionLLVMLabel(
				body.getHeader().getType().getPointeeType().getFunctionSelf(),
				args[1]);
		
		Map<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> returnmap = 
			new HashMap<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		CRecursiveExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>> graph2 =
			xml2peg.parsePEG(body.getHeader(), label, document.getDocumentElement(), returnmap); 

		Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> cache = 
			new HashMap<Vertex<FlowValue<LLVMParameter,LLVMLabel>>,Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		System.out.println(equals(
				outputs.get(LLVMReturn.VALUE),
				returnmap.get(LLVMReturn.VALUE),
				cache));
		System.out.println(equals(
				outputs.get(LLVMReturn.SIGMA),
				returnmap.get(LLVMReturn.SIGMA),
				cache));
		
		FileOutputStream fout2 = new FileOutputStream("peg2.xml");
		peg2xml(new PrintStream(fout2), returnmap);
		fout2.close();
	}
	*/
}
