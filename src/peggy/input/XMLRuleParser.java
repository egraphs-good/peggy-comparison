package peggy.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.DisjointUnion;
import util.NamedTag;
import util.pair.Pair;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This class is the abstract parent of all XML-based axiom parsers.
 * It uses xml files to encode simple axioms to be used in equality saturation.
 * All such xml files follow an abstract grammar, which is described below.
 * 
 * Grammar:
 *
 * 	<rule name="...">
 * 		<trigger>
 * 			<exists>
 *				... 
 * 			</exists>
 * 			<trues>
 *				... 
 * 			</trues>
 * 			<falses>
 * 				...
 * 			</falses>
 * 			<invariant index="...">
 * 				...
 * 			</invariant>
 * 			<distinct index1="..." index2="..."/>
 *		</trigger>
 * 
 * 		<response>
 * 			<creates>
 * 				...
 * 			</creates>
 * 			<trues>
 * 				...
 * 			</trues>
 * 			<falses>
 * 				...
 * 			</falses>
 * 			<equalities>
 * 				<><>
 * 				<><>
 * 				...
 * 			</equalities>
 *		</response>		 
 * 	</rule>
 * 
 *  <transform name="...">
 *    <before expression>
 *    <after expression>
 *  </transform>
 * 
 *  
 *  <simpleRule name="...">
 *    grammar:
 *    	 rule := [exists] [trues] [falses] '==>' [trues] [falses] [equalities]
 *    
 *    	 exists := nodelist
 *    
 *       nodelist := node | node nodelist
 * 		 nodecommalist := node | node ',' nodecommalist
 * 		 operandcommalist := operand | operand ',' operandcommalist
 *    
 *       trues := '{' nodelist '}'
 *       
 *       falses := '!{' nodelist '}!' 
 *       
 *       equalities := node '=' node
 *       
 *       node :=  '@' ident 
 *       	   | ['@' ident ':'] op 
 *       	   | ['@' ident ':'] nondomain 
 *             | ['@' ident ':'] '*'
 *             
 *       operand := node | String
 *       
 *       op := ident '(' ')'
 *       	 | ident '(' nodelist ')'
 *       
 *       ident := identifier made of letter,number,underscore
 *       
 *       nondomain := nondomain_ident '(' ')'
 *       			| nondomain_ident '(' nodelist ')'
 *       
 *       nondomain_ident := identifier starting with '%', using nondomain name.
 *       			  indexed ones end with '-N' for some N (i.e. "%theta-1")
 *        
 *       String := in quotes, allows escape chars
 *  </simpleRule>
 *  
 *  <simpleTransform name="...">
 *     transform := node '=' node  
 *  </simpleTransform>
 * 
 * @author steppm
 */
public abstract class XMLRuleParser<O,P,T> extends AbstractRuleParser<O,P,T> {
	protected static final Set<String> EMPTY_ATTRS = Collections.unmodifiableSet(new HashSet<String>());
	protected static final Set<String> INDEX_ATTRS;
	protected static final Set<String> INDEX12_ATTRS;
	protected static final Set<String> NAME_ATTRS;
	public static final NamedTag<String> NAME_TAG = new NamedTag<String>("name");

	static {
		Set<String> set = new HashSet<String>();
		set.add("index");
		INDEX_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("index1");
		set.add("index2");
		INDEX12_ATTRS = Collections.unmodifiableSet(set);
		
		set = new HashSet<String>();
		set.add("name");
		NAME_ATTRS = Collections.unmodifiableSet(set);
	}
	
	
	protected final DocumentBuilder builder;
	protected int axiomNameCounter = 0;

	/**
	 * Creates a new XMLRuleParser based on the given XML DocumentBuilder.
	 * This builder will be used to parse the XML from the input source.
	 * 
	 * @param _builder the DocumentBuilder to use. if null, the system default builder will be used
	 */
	public XMLRuleParser(DocumentBuilder _builder) {
		if (_builder == null) {
			try {
				this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException pce) {
				throw new RuntimeException("Default DocumentBuilder cannot be created");
			}
		} else {
			this.builder = _builder;
		}
	}
	
	public Collection<AxiomNode<O, ? extends PEGNode<O>>> parseRuleSet(InputStream in) throws IOException, RuleParsingException {
		Document document = null;
		try {
			document = this.builder.parse(in);
		} catch (SAXException saxe) {
			throw new RuleParsingException("Error parsing XML", saxe);
		}
		
		return this.processRuleSetElement(document.getDocumentElement());
	}
	
	public AxiomNode<O, ? extends PEGNode<O>> parseRule(InputStream in) throws IOException {
		Document document = null;
		try {
			document = this.builder.parse(in);
		} catch (SAXException saxe) {
			throw new RuleParsingException("Error parsing XML", saxe);
		}
		return this.processRuleItem(document.getDocumentElement());
	}
	
	protected Collection<AxiomNode<O, ? extends PEGNode<O>>> processRuleSetElement(Element ruleset) throws RuleParsingException {
		List<? extends Element> childElements = this.getElementChildren(ruleset);
		List<AxiomNode<O, ? extends PEGNode<O>>> result = 
			new ArrayList<AxiomNode<O, ? extends PEGNode<O>>>(childElements.size());
		for (Element rule : childElements) {
			result.add(this.processRuleItem(rule)); 
		}
		return result;
	}
	
	
	protected AxiomNode<O, ? extends PEGNode<O>> processRuleItem(Element item) throws RuleParsingException {
		String elementName = item.getTagName();
		if (elementName.equals("rule")) {
			return this.processRuleElement(item);
		} else if (elementName.equals("transform")) {
			return this.processTransformElement(item);
		} else if (elementName.equals("simpleRule")) {
			return this.processSimpleRuleElement(item);
		} else if (elementName.equals("simpleTransform")) {
			return this.processSimpleTransformElement(item);
		} else {
			throw new RuleParsingException("Invalid rule item tag: " + elementName);
		}
	}

	protected class ParsingInfo {
		Map<String,PeggyVertex<O,T>> id2vertex = new HashMap<String,PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> exists = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> trues = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> falses = new ArrayList<PeggyVertex<O,T>>();
		Map<Integer,List<PeggyVertex<O,T>>> invariants = new HashMap<Integer,List<PeggyVertex<O,T>>>();
		Set<Pair<Integer,Integer>> distincts = new HashSet<Pair<Integer,Integer>>(); 

		List<PeggyVertex<O,T>> creates = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> learntrues = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> learnfalses = new ArrayList<PeggyVertex<O,T>>();
		List<Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>> equalities = new ArrayList<Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>>();
	}
	
	protected AxiomNode<O, ? extends PEGNode<O>> processSimpleTransformElement(Element simpleTransform) throws RuleParsingException {
		this.assertAttributes(simpleTransform, EMPTY_ATTRS, NAME_ATTRS);

		StringBuffer buffer = new StringBuffer(100);
		NodeList list = simpleTransform.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				buffer.append(node.getTextContent());
			}
		}
		
		String name;
		if (simpleTransform.hasAttribute("name")) {
			name = simpleTransform.getAttribute("name");
		} else {
			name = buffer.toString();
		}
		
		Map<String,PeggyVertex<O,T>> id2vertex = new HashMap<String,PeggyVertex<O,T>>();		
		PeggyAxiomizer<O,T> axiomizer = this.createAxiomizer(name);
		Buffer buf = new Buffer(buffer);
		
		PeggyVertex<O,T> lhs = this.parseSimpleVertex(buf, axiomizer, id2vertex);
		buf.skipWS();
		if (!buf.nextN("="))
			throw new RuleParsingException("Expecting '=' for simple transform");
		buf.inc();
		
		PeggyVertex<O,T> rhs = this.parseSimpleVertex(buf, axiomizer, id2vertex);
		buf.skipWS();
		if (!buf.atEnd())
			throw new RuleParsingException("Extra input at end of simpleTransform");
		
		axiomizer.mustExist(lhs);
		axiomizer.create(rhs);
		axiomizer.makeEqual(lhs, rhs);

		/*
		try {
			PrintStream pout = new PrintStream(new FileOutputStream("axiom.dot"));
			pout.println(axiomizer.mGraph);
			pout.close();
		} catch (Throwable t) {}
		*/

		
		AxiomNode<O, ? extends PEGNode<O>> anode = axiomizer.getAxiom();
		anode.<String>setTag(NAME_TAG, name);
		return anode;
	}
	
	protected AxiomNode<O, ? extends PEGNode<O>> processSimpleRuleElement(Element simpleRule) throws RuleParsingException {
		this.assertAttributes(simpleRule, EMPTY_ATTRS, NAME_ATTRS);

		StringBuffer buffer = new StringBuffer(100);
		NodeList list = simpleRule.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				buffer.append(node.getTextContent());
			}
		}
		
		String name;
		if (simpleRule.hasAttribute("name")) {
			name = simpleRule.getAttribute("name");
		} else {
			name = buffer.toString();
		}
		
		PeggyAxiomizer<O,T> axiomizer = this.createAxiomizer(name);

		ParsingInfo info = new ParsingInfo();
		this.parseSimpleRule(new Buffer(buffer), axiomizer, info);
		
		// make the axiom!
		for (PeggyVertex<O,T> vertex : info.exists) {
			axiomizer.mustExist(vertex);
		}
		for (PeggyVertex<O,T> vertex : info.trues) {
			axiomizer.mustBeTrue(vertex);
		}
		for (PeggyVertex<O,T> vertex : info.falses) {
			axiomizer.mustBeFalse(vertex);
		}
		for (int index : info.invariants.keySet()) {
			for (PeggyVertex<O,T> vertex : info.invariants.get(index)) {
				axiomizer.mustBeInvariant(index, vertex);
			}
		}
		for (Pair<Integer,Integer> pair : info.distincts) {
			axiomizer.mustBeDistinctLoops(pair.getFirst(), pair.getSecond());
		}
		
		// made the trigger, now do the response
		
		for (PeggyVertex<O,T> vertex : info.creates) {
			axiomizer.create(vertex);
		}
		for (PeggyVertex<O,T> vertex : info.learntrues) {
			axiomizer.makeTrue(vertex);
		}
		for (PeggyVertex<O,T> vertex : info.learnfalses) {
			axiomizer.makeFalse(vertex);
		}
		for (Pair<PeggyVertex<O,T>,PeggyVertex<O,T>> pair : info.equalities) {
			axiomizer.makeEqual(pair.getFirst(), pair.getSecond()); 
		}
		
		AxiomNode<O, ? extends PEGNode<O>> anode = axiomizer.getAxiom();
		anode.<String>setTag(NAME_TAG, name);
		return anode;
	}
	

	protected void parseSimpleRule(
			Buffer buffer,
			PeggyAxiomizer<O,T> axiomizer, 
			ParsingInfo info) {
		// rule := [exists] [trues] [falses] '==>' [trues] [falses] [equalities]
		boolean hasExists = false;
		boolean hasTrues = false;
		boolean hasFalses = false;
		
		while (!buffer.atEnd()) {
			buffer.skipWS();
			if (buffer.atEnd())
				throw new RuleParsingException("Premature EOF");
			if (buffer.peek() == '{') {
				// trues
				buffer.inc();
				if (hasTrues)
					throw new RuleParsingException("Multiple trues blocks found");
			
				buffer.skipWS();
				while (!buffer.nextN("}")) {
					PeggyVertex<O,T> trueVertex = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
					info.trues.add(trueVertex);
					buffer.skipWS();
				}
				if (!buffer.nextN("}"))
					throw new RuleParsingException("} expected at end of true block");
				buffer.inc();
			
				hasTrues = true;
			} else if (buffer.nextN("!{")) {
				// falses
				buffer.inc(2);
				if (hasFalses)
					throw new RuleParsingException("Multiple falses blocks found");
			
				buffer.skipWS();
				while (!buffer.nextN("}!")) {
					PeggyVertex<O,T> falseVertex = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
					info.falses.add(falseVertex);
					buffer.skipWS();
				}
				if (!buffer.nextN("}!"))
					throw new RuleParsingException("} expected at end of false block");
				buffer.inc(2);
				
				hasFalses = true;
			} else if (buffer.nextN("==>")) {
				// move to response
				buffer.inc(3);
				break;
			} else {
				// exists
				PeggyVertex<O,T> vertex = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
				info.exists.add(vertex);
				hasExists = true;
			}
		}
		
		if (!(hasExists | hasTrues | hasFalses))
			throw new RuleParsingException("No trigger specified");

		boolean hasLearntrues = false;
		boolean hasLearnfalses = false;
		boolean hasEqualities = false;
		
		buffer.skipWS();
		while (!buffer.atEnd()) {
			if (buffer.nextN("{")) {
				// trues
				buffer.inc();
				if (hasLearntrues)
					throw new RuleParsingException("Multiple learned true blocks found");
				
				buffer.skipWS();
				while (!buffer.nextN("}")) {
					PeggyVertex<O,T> trueVertex = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
					info.learntrues.add(trueVertex);
					buffer.skipWS();
				}
				if (!buffer.nextN("}"))
					throw new RuleParsingException("} expected at end of learned true block");
				buffer.inc();
			
				hasLearntrues = true;
			} else if (buffer.nextN("!{")) {
				// falses
				buffer.inc(2);
				if (hasLearnfalses)
					throw new RuleParsingException("Multiple learned false blocks found");
				
				buffer.skipWS();
				while (!buffer.nextN("}!")) {
					PeggyVertex<O,T> falseVertex = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
					info.learnfalses.add(falseVertex);
					buffer.skipWS();
				}
				if (!buffer.nextN("}!"))
					throw new RuleParsingException("} expected at end of learned false block");
				buffer.inc(2);
				
				hasLearnfalses = true;
			} else {
				// equalities
				PeggyVertex<O,T> lhs = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
				buffer.skipWS();
				if (!buffer.nextN("="))
					throw new RuleParsingException("Expecting '=' in equality");
				buffer.inc();
				PeggyVertex<O,T> rhs = this.parseSimpleVertex(buffer, axiomizer, info.id2vertex);
				
				info.equalities.add(
						new Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>(lhs,rhs));
				hasEqualities = true;
			}
					
			buffer.skipWS();
		}

		if (!(hasLearntrues | hasLearnfalses | hasEqualities))
			throw new RuleParsingException("No response in rule");
	}

	
	/**
	 * 		 operandcommalist := operand | operand ',' operandcommalist
	 * 		 nodecommalist := node | node ',' nodecommalist
	 *       node := op | nondomain
	 *       operand := node | String
	 *       
	 *       op := opstart '(' ')'
	 *       	 | opstart '(' operandcommalist ')'
	 *       
	 *       opstart := ['@' ident ':'] ident
	 *       
	 *       ident := identifier made of letter,number,underscore
	 *       
	 *       nondomain := nondomain_start '(' ')'
	 *       			| nondomain_start '(' nodecommalist ')'
	 *       
	 *       nondomain_start := ['@' ident ':'] nondomain_ident
	 *       
	 *       nondomain_ident := identifier starting with '%', using nondomain name.
	 *       			  indexed ones end with '-N' for some N (i.e. "%theta-1")
	 *        
	 *       String := in quotes, allows escape chars
	 */
	
	protected boolean isIdentChar(char c) {
		return (('a' <= c) & (c <= 'z')) |
			(('A' <= c) & (c <= 'Z')) |
			(('0' <= c) & (c <= '9')) |
			(c=='_');
	}
	
	protected abstract PeggyVertex<O,T> getFreshVariable(
			PeggyAxiomizer<O,T> axiomizer);

	protected abstract PeggyVertex<O,T> parseSimpleDomain(
			Buffer buffer,
			String opname,
			PeggyAxiomizer<O,T> axiomizer, 
			Map<String,PeggyVertex<O,T>> id2vertex);
	
	protected PeggyVertex<O,T> parseSimpleVertex(
			Buffer buffer,
			PeggyAxiomizer<O,T> axiomizer, 
			Map<String,PeggyVertex<O,T>> id2vertex) {
		buffer.skipWS();
		if (buffer.nextN("@")) {
			// op or nondomain with label
			buffer.inc();
			StringBuffer label = new StringBuffer(20);
			while (buffer.hasN(1) && isIdentChar(buffer.peek())) {
				label.append(buffer.read());
			}
			if (label.length() == 0)
				throw new RuleParsingException("Empty label name");
			buffer.skipWS();
			String myLabel = label.toString();
			
			if (!buffer.nextN(":")) {
				// using a label
				if (!id2vertex.containsKey(myLabel))
					throw new RuleParsingException("Undefined label: " + myLabel);
				return id2vertex.get(myLabel);
			} else if (id2vertex.containsKey(myLabel)) {
				// redefining an existing label
				throw new RuleParsingException("Label " + myLabel + " is already defined");
			}
			
			// assigning a label
			PeggyVertex<O,T> placeholder = 
				axiomizer.createPlaceHolder();
			id2vertex.put(myLabel, placeholder);
			
			buffer.inc();
			buffer.skipWS();
		
			// now ident or % nondomain
			if (buffer.nextN("%")) {
				// nondomain start
				PeggyVertex<O,T> vertex = 
					this.parseSimpleNondomain(buffer, axiomizer, id2vertex);
				placeholder.replaceWith(vertex);
				return vertex;
			} else if (buffer.nextN("*")) {
				// variable
				buffer.inc();
				PeggyVertex<O,T> vertex = this.getFreshVariable(axiomizer);
				placeholder.replaceWith(vertex);
				return vertex;
			} else {
				// domain!
				StringBuffer opname = new StringBuffer(20);
				while (buffer.hasN(1) && isIdentChar(buffer.peek())) {
					opname.append(buffer.read());
				}
				if (opname.length() == 0)
					throw new RuleParsingException("Empty domain op name");

				PeggyVertex<O,T> vertex =
					this.parseSimpleDomain(buffer, opname.toString(), axiomizer, id2vertex);
				placeholder.replaceWith(vertex);
				return vertex;
			}
		} else if (buffer.nextN("%")) {
			// nondomain
			return this.parseSimpleNondomain(buffer, axiomizer, id2vertex);
		} else if (buffer.nextN("*")) {
			// unlabelled variable
			buffer.inc();
			return this.getFreshVariable(axiomizer);
		} else if (buffer.hasN(1) && isIdentChar(buffer.peek())) {
			// domain
			StringBuffer opname = new StringBuffer(20);
			while (buffer.hasN(1) && isIdentChar(buffer.peek())) {
				opname.append(buffer.read());
			}
			if (opname.length() == 0)
				throw new RuleParsingException("Empty domain op name");

			PeggyVertex<O,T> vertex =
				this.parseSimpleDomain(buffer, opname.toString(), axiomizer, id2vertex);
			return vertex;
		} else {
			// garbage
			throw new RuleParsingException("Cannot parse node");
		}
	}
	
	protected List<? extends PeggyVertex<O,T>> parseSimpleNodeArguments(
			Buffer buffer,
			PeggyAxiomizer<O,T> axiomizer, 
			Map<String,PeggyVertex<O,T>> id2vertex) {
		buffer.skipWS();
		if (!buffer.nextN("("))
			throw new RuleParsingException("Expecting '(' in argument list");
		buffer.inc();
		List<PeggyVertex<O,T>> result = 
			new ArrayList<PeggyVertex<O,T>>();
		while (true) {
			PeggyVertex<O,T> vertex = 
				parseSimpleVertex(buffer, axiomizer, id2vertex);
			result.add(vertex);
			
			buffer.skipWS();
			if (buffer.nextN(",")) {
				buffer.inc();
				continue;
			} else if (buffer.nextN(")")) {
				buffer.inc();
				break;
			} else {
				throw new RuleParsingException("Expecting ',' or ')' in argument list");
			}
		}
		
		return result;
	}
	

	protected int parseInt(Buffer buffer) {
		int result = 0;
		while (buffer.hasN(1) && '0' <= buffer.peek() && buffer.peek() <= '9') {
			result = (result*10) + (buffer.read()-'0');
		}
		return result;
	}
	
	
	protected PeggyVertex<O,T> parseSimpleNondomain(
			Buffer buffer,
			PeggyAxiomizer<O,T> axiomizer,
			Map<String,PeggyVertex<O,T>> id2vertex) {
		// assume starting at '%' (with maybe WS)
		buffer.skipWS();
		
		if (buffer.nextN("%theta-")) {
			buffer.inc(7);
			int pos = buffer.getIndex();
			int index = parseInt(buffer);
			if (pos == buffer.getIndex())
				throw new RuleParsingException("No index given for theta");
			
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 2)
				throw new RuleParsingException("Theta must have 2 arguments");
			return axiomizer.getTheta(index, args.get(0), args.get(1));
		} else if (buffer.nextN("%eval-")) {
			buffer.inc(6);
			int pos = buffer.getIndex();
			int index = parseInt(buffer);
			if (pos == buffer.getIndex())
				throw new RuleParsingException("No index given for eval");
			
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 2)
				throw new RuleParsingException("Eval must have 2 arguments: " + args.size());
			return axiomizer.getEval(index, args.get(0), args.get(1));
		} else if (buffer.nextN("%shift-")) {
			buffer.inc(7);
			int pos = buffer.getIndex();
			int index = parseInt(buffer);
			if (pos == buffer.getIndex())
				throw new RuleParsingException("No index given for shift");
			
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 1)
				throw new RuleParsingException("Shift must have 1 argument");
			return axiomizer.getShift(index, args.get(0));
		} else if (buffer.nextN("%pass-")) {
			buffer.inc(6);
			int pos = buffer.getIndex();
			int index = parseInt(buffer);
			if (pos == buffer.getIndex())
				throw new RuleParsingException("No index given for pass");
			
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 1)
				throw new RuleParsingException("Pass must have 1 argument");
			return axiomizer.getPass(index, args.get(0));
		} else if (buffer.nextN("%phi")) {
			buffer.inc(4);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 3)
				throw new RuleParsingException("Phi must have 3 arguments");
			return axiomizer.getPhi(args.get(0), args.get(1), args.get(2));
		} else if (buffer.nextN("%zero")) {
			buffer.inc(5);
			return axiomizer.getZero();
		} else if (buffer.nextN("%negate")) {
			buffer.inc(7);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 1)
				throw new RuleParsingException("Negate must have 1 argument");
			return axiomizer.getNegate(args.get(0));
		} else if (buffer.nextN("%equals")) {
			buffer.inc(7);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 2)
				throw new RuleParsingException("Equals must have 2 arguments");
			return axiomizer.getEquals(args.get(0), args.get(1));
		} else if (buffer.nextN("%successor")) {
			buffer.inc(10);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 1)
				throw new RuleParsingException("Successor must have 1 argument");
			return axiomizer.getSuccessor(args.get(0));
		} else if (buffer.nextN("%and")) {
			buffer.inc(4);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 2)
				throw new RuleParsingException("And must have 2 arguments");
			return this.getAndVertex(axiomizer, args);
		} else if (buffer.nextN("%or")) {
			buffer.inc(3);
			List<? extends PeggyVertex<O,T>> args = 
				this.parseSimpleNodeArguments(buffer, axiomizer, id2vertex);
			if (args.size() != 2)
				throw new RuleParsingException("Or must have 2 arguments");
			return this.getOrVertex(axiomizer, args);
		} else {
			throw new RuleParsingException("Unrecognized nondomain operator");
		}
	}
	
	protected List<? extends DisjointUnion<String,PeggyVertex<O,T>>> 
	parseSimpleOperandArguments(
			Buffer buffer,
			PeggyAxiomizer<O,T> axiomizer, 
			Map<String,PeggyVertex<O,T>> id2vertex) {
		buffer.skipWS();
		if (!buffer.nextN("("))
			throw new RuleParsingException("Expecting '(' in argument list");
		buffer.inc();
		List<DisjointUnion<String,PeggyVertex<O,T>>> result = 
			new ArrayList<DisjointUnion<String,PeggyVertex<O,T>>>();
		if (buffer.nextN(")")) {
			buffer.inc();
		} else {
			while (true) {
				buffer.skipWS();
				if (buffer.nextN("\"")) {
					String stringValue = this.parseSimpleString(buffer);
					result.add(DisjointUnion.<String,PeggyVertex<O,T>>injectLeft(stringValue));
				} else {
					PeggyVertex<O,T> vertex = 
						parseSimpleVertex(buffer, axiomizer, id2vertex);
					result.add(DisjointUnion.<String,PeggyVertex<O,T>>injectRight(vertex));
				}

				buffer.skipWS();
				if (buffer.nextN(",")) {
					buffer.inc();
					continue;
				} else if (buffer.nextN(")")) {
					buffer.inc();
					break;
				} else {
					throw new RuleParsingException("Expecting ',' or ')' in argument list");
				}
			}
		}
		
		return result;
	}

	protected String parseSimpleString(Buffer buffer) {
		buffer.skipWS();
		if (!buffer.nextN("\""))
			throw new RuleParsingException("Expecting '\"' in string");
		buffer.inc();
		StringBuffer result = new StringBuffer(20);
		while (buffer.hasN(1) && buffer.peek() != '\"') {
			char next = buffer.peek();
			if ((next < 32) | (next > 127) | (next=='\n') |
				(next=='\r') | (next=='\t')) {
				// invalid in string
				throw new RuleParsingException("Invalid chat in string: " + (int)next);
			}
			else if (next == '\\') {
				// escape!
				buffer.inc();
				if (!buffer.hasN(1))
					throw new RuleParsingException("Escape sequence ended prematurely");
				switch (buffer.read()) {
				case 'n': result.append('\n'); break;
				case 'r': result.append('\r'); break;
				case 't': result.append('\t'); break;
				case '\\': result.append('\\'); break;
				default: throw new RuleParsingException("Unknown escape sequence: \\" + buffer.peek());
				}
			} 
			else {
				result.append(buffer.read());
			}
		}
		if (!buffer.nextN("\""))
			throw new RuleParsingException("Unterminated string");
		buffer.inc();
		return result.toString();
	}
	
	
	protected abstract PeggyVertex<O,T> getAndVertex(
			PeggyAxiomizer<O,T> axiomizer, 
			List<? extends PeggyVertex<O,T>> args);
	protected abstract PeggyVertex<O,T> getOrVertex(
			PeggyAxiomizer<O,T> axiomizer, 
			List<? extends PeggyVertex<O,T>> args);
	
	protected AxiomNode<O, ? extends PEGNode<O>> processTransformElement(Element transform) throws RuleParsingException {
		this.assertAttributes(transform, EMPTY_ATTRS, NAME_ATTRS);
	
		Map<String,PeggyVertex<O,T>> id2vertex = new HashMap<String,PeggyVertex<O,T>>();
		
		List<Element> children = this.getElementChildren(transform);
		if (children.size() != 2)
			throw new RuleParsingException("transform tag must have 2 children");
		Element original = children.get(0);
		Element transformed = children.get(1);

		String name;
		if (transform.hasAttribute("name")) {
			name = transform.getAttribute("name");
		} else {
			name = "axiom " + (axiomNameCounter++);
		}
		
		PeggyAxiomizer<O,T> axiomizer = this.createAxiomizer(name);
		PeggyVertex<O,T> originalVertex = this.parseVertex(axiomizer, original, id2vertex);
		PeggyVertex<O,T> transformedVertex = this.parseVertex(axiomizer, transformed, id2vertex);
		
		// make the axiom!
		axiomizer.mustExist(originalVertex);
		axiomizer.create(transformedVertex);
		axiomizer.makeEqual(originalVertex, transformedVertex); 
		
		AxiomNode<O, ? extends PEGNode<O>> anode = axiomizer.getAxiom();
		if (transform.hasAttribute("name")) {
			anode.<String>setTag(NAME_TAG, transform.getAttribute("name"));
		}
		return anode;
	}
	
	protected AxiomNode<O, ? extends PEGNode<O>> processRuleElement(Element rule) throws RuleParsingException {
		this.assertAttributes(rule, EMPTY_ATTRS, NAME_ATTRS);
	
		Map<String,PeggyVertex<O,T>> id2vertex = new HashMap<String,PeggyVertex<O,T>>();
		
		List<Element> children = this.getElementChildren(rule);
		if (children.size() != 2)
			throw new RuleParsingException("rule tag must have 2 children");
		Element trigger = children.get(0);
		Element response = children.get(1);
		if (!trigger.getTagName().equals("trigger"))
			throw new RuleParsingException("rule tag must contain trigger node as first child");
		if (!response.getTagName().equals("response"))
			throw new RuleParsingException("rule tag must contain response node as second child");
		this.assertAttributes(trigger, EMPTY_ATTRS, EMPTY_ATTRS);
		this.assertAttributes(response, EMPTY_ATTRS,  EMPTY_ATTRS);

		String name;
		if (rule.hasAttribute("name")) {
			name = rule.getAttribute("name");
		} else {
			name = "axiom " + (axiomNameCounter++);
		}
		
		PeggyAxiomizer<O,T> axiomizer = this.createAxiomizer(name);
		
		List<PeggyVertex<O,T>> exists = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> trues = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> falses = new ArrayList<PeggyVertex<O,T>>();
		Map<Integer,List<PeggyVertex<O,T>>> invariants = new HashMap<Integer,List<PeggyVertex<O,T>>>();
		Set<Pair<Integer,Integer>> distincts = new HashSet<Pair<Integer,Integer>>(); 

		this.processTriggerElement(axiomizer, trigger, id2vertex, exists, trues, falses, invariants, distincts);
		
		List<PeggyVertex<O,T>> creates = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> learntrues = new ArrayList<PeggyVertex<O,T>>();
		List<PeggyVertex<O,T>> learnfalses = new ArrayList<PeggyVertex<O,T>>();
		List<Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>> equalities = new ArrayList<Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>>();
		
		this.processResponseElement(axiomizer, response, id2vertex, creates, learntrues, learnfalses, equalities);

		
		// make the axiom!
		for (PeggyVertex<O,T> vertex : exists) {
			axiomizer.mustExist(vertex);
		}
		for (PeggyVertex<O,T> vertex : trues) {
			axiomizer.mustBeTrue(vertex);
		}
		for (PeggyVertex<O,T> vertex : falses) {
			axiomizer.mustBeFalse(vertex);
		}
		for (int index : invariants.keySet()) {
			for (PeggyVertex<O,T> vertex : invariants.get(index)) {
				axiomizer.mustBeInvariant(index, vertex);
			}
		}
		for (Pair<Integer,Integer> pair : distincts) {
			axiomizer.mustBeDistinctLoops(pair.getFirst(), pair.getSecond());
		}
		
		// made the trigger, now do the response
		
		for (PeggyVertex<O,T> vertex : creates) {
			axiomizer.create(vertex);
		}
		for (PeggyVertex<O,T> vertex : learntrues) {
			axiomizer.makeTrue(vertex);
		}
		for (PeggyVertex<O,T> vertex : learnfalses) {
			axiomizer.makeFalse(vertex);
		}
		for (Pair<PeggyVertex<O,T>,PeggyVertex<O,T>> pair : equalities) {
			axiomizer.makeEqual(pair.getFirst(), pair.getSecond()); 
		}
		
		AxiomNode<O, ? extends PEGNode<O>> anode = axiomizer.getAxiom();
		if (rule.hasAttribute("name")) {
			anode.<String>setTag(NAME_TAG, rule.getAttribute("name"));
		}
		return anode;
	}

	
	protected void processTriggerElement(PeggyAxiomizer<O,T> axiomizer, Element trigger, Map<String,PeggyVertex<O,T>> id2vertex, 
										 List<PeggyVertex<O,T>> exists, List<PeggyVertex<O,T>> trues, List<PeggyVertex<O,T>> falses,
										 Map<Integer,List<PeggyVertex<O,T>>> invariants, Set<Pair<Integer,Integer>> distincts) {
		boolean hasExists = false;
		boolean hasTrue = false;
		boolean hasFalse = false;
		
		List<Element> triggerChildren = this.getElementChildren(trigger);
		
		for (Element element : triggerChildren) {
			String elementName = element.getTagName();
			if (elementName.equals("exists")) {
				if (hasExists)
					throw new RuleParsingException("Duplicate exists block");
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
				
				List<Element> existsChildren = this.getElementChildren(element);
				for (Element exist : existsChildren) {
					exists.add(this.parseVertex(axiomizer, exist, id2vertex));
				}
				hasExists = true;
			} else if (elementName.equals("trues")) {
				if (hasTrue)
					throw new RuleParsingException("Duplicate true block");
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);

				List<Element> trueChildren = this.getElementChildren(element);
				for (Element child : trueChildren) {
					PeggyVertex<O,T> vertex = this.parseVertex(axiomizer, child, id2vertex);
					exists.add(vertex);
					trues.add(vertex);
				}
				hasTrue = true;
			} else if (elementName.equals("falses")) {
				if (hasFalse)
					throw new RuleParsingException("Duplicate false block");
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);

				List<Element> falseChildren = this.getElementChildren(element);
				for (Element child : falseChildren) {
					PeggyVertex<O,T> vertex = this.parseVertex(axiomizer, child, id2vertex);
					exists.add(vertex);
					falses.add(vertex);
				}
				hasFalse = true;
			} else if (elementName.equals("invariant")) {
				this.assertAttributes(element, INDEX_ATTRS, EMPTY_ATTRS);
				int index;
				try {
					index = Integer.parseInt(element.getAttribute("index"));
				} catch (NumberFormatException nfe) {
					throw new RuleParsingException("invalid invariant index value: " + element.getAttribute("index"));
				}
				if (index <= 0)
					throw new RuleParsingException("Nonpositive invariant index given: " + index);
				if (invariants.containsKey(index))
					throw new RuleParsingException("Duplicate invariant index block: " + index);
				
				List<Element> invariantChildren = this.getElementChildren(element);
				List<PeggyVertex<O,T>> invariantNodes = new ArrayList<PeggyVertex<O,T>>(invariantChildren.size());
				for (Element child : invariantChildren) {
					PeggyVertex<O,T> vertex = this.parseVertex(axiomizer, child, id2vertex);
					exists.add(vertex);
					invariantNodes.add(vertex);
				}
				invariants.put(index, invariantNodes);
			} else if (elementName.equals("distinct")) {
				this.assertAttributes(element, INDEX12_ATTRS, EMPTY_ATTRS);
				int index1;
				int index2;
				try {
					index1 = Integer.parseInt(element.getAttribute("index1"));
					index2 = Integer.parseInt(element.getAttribute("index2"));
				} catch (NumberFormatException nfe) {
					throw new RuleParsingException("distinct index values are not valid");
				}
				if (index1 <= 0 || index2 <= 0)
					throw new RuleParsingException("Nonpositive distinct index value given");
				
				if (index1 > index2) {
					int temp = index1;
					index1 = index2;
					index2 = temp;
				}
				Pair<Integer,Integer> pair = new Pair<Integer,Integer>(index1,index2);
				if (distincts.contains(pair))
					throw new RuleParsingException("Duplicate distinct index pair");
				distincts.add(pair);
			} else {
				throw new RuleParsingException("Unknown tag: " + elementName);
			}
		}

	}
	
	
	protected void processResponseElement(PeggyAxiomizer<O,T> axiomizer, Element response, Map<String,PeggyVertex<O,T>> id2vertex, 
										  List<PeggyVertex<O,T>> creates, List<PeggyVertex<O,T>> learntrues, List<PeggyVertex<O,T>> learnfalses, 
										  List<Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>> equalities) {
		List<Element> responseChildren = this.getElementChildren(response);
		
		for (Element element : responseChildren) {
			String elementName = element.getTagName();
			if (elementName.equals("creates")) {
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
				List<Element> createsChildren = this.getElementChildren(element);
				for (Element child : createsChildren) {
					creates.add(this.parseVertex(axiomizer, child, id2vertex));
				}
			} else if (elementName.equals("trues")) {
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
				List<Element> truesChildren = this.getElementChildren(element);
				for (Element child : truesChildren) {
					PeggyVertex<O,T> vertex = this.parseVertex(axiomizer, child, id2vertex);
					creates.add(vertex);
					learntrues.add(vertex);
				}
			} else if (elementName.equals("falses")) {
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
				List<Element> falsesChildren = this.getElementChildren(element);
				for (Element child : falsesChildren) {
					PeggyVertex<O,T> vertex = this.parseVertex(axiomizer, child, id2vertex);
					creates.add(vertex);
					learnfalses.add(vertex);
				}
			} else if (elementName.equals("equalities")) {
				this.assertAttributes(element, EMPTY_ATTRS, EMPTY_ATTRS);
				List<Element> equalitiesChildren = this.getElementChildren(element);
				if ((equalitiesChildren.size()&1) != 0)
					throw new RuleParsingException("equalities tag must have an even number of children");

				for (int i = 0; i < equalitiesChildren.size(); i+=2) {
					PeggyVertex<O,T> vertex1 = this.parseVertex(axiomizer, equalitiesChildren.get(i), id2vertex);
					PeggyVertex<O,T> vertex2 = this.parseVertex(axiomizer, equalitiesChildren.get(i+1), id2vertex);
					//creates.add(vertex1);
					//creates.add(vertex2);
					Pair<PeggyVertex<O,T>,PeggyVertex<O,T>> pair = new Pair<PeggyVertex<O,T>,PeggyVertex<O,T>>(vertex1, vertex2);
					equalities.add(pair);
				}
			} else {
				throw new RuleParsingException("Unknown tag: " + elementName);
			}
		}
	}
	
	protected abstract PeggyVertex<O,T> parseVertex(PeggyAxiomizer<O,T> axiomizer, Element element, Map<String,PeggyVertex<O,T>> id2vertex);
	
	/////////////////////////////////////////////////////////////
	
	/**
	 * Helper method to assert that a given element has no child nodes that are Elements.
	 * If the assertion fails, a RuleParsingException is thrown.
	 */
	protected void assertNoElementChildren(Element element) throws RuleParsingException {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element)
				throw new RuleParsingException("Invalid child element found for element " + element.getTagName() + ": " + children.item(i).getNodeName());
		}
	}
	
	/**
	 * Helper method to assert that the given element have the correct XML attributes.
	 * The element must have all of the attributes in 'requiredAttrs', and may optionally have 
	 * attributes from 'optionalAttrs'. Any attribute that the element has that is not contained in
	 * (requiredAttrs UNION optionalAttrs) is invalid.
	 * If any of these assertions fails, a RuleParsingException is thrown.
	 */
	protected void assertAttributes(Element element, Set<? extends String> requiredAttrs, Set<? extends String> optionalAttrs) throws RuleParsingException {
		NamedNodeMap attrs = element.getAttributes();
		Set<String> foundAttrs = new HashSet<String>();
		for (int i = 0; i < attrs.getLength(); i++) {
			String name = ((Attr)attrs.item(i)).getName();
			if (!(requiredAttrs.contains(name) || optionalAttrs.contains(name)))
				throw new RuleParsingException("Found extraneous attribute for " + element.getTagName() + " element: " + name);
			foundAttrs.add(name);
		}
		for (String s : requiredAttrs) {
			if (!foundAttrs.contains(s))
				throw new RuleParsingException("Missing required attribute '" + s + "' for element " + element.getTagName());
		}
	}

	
	/**
	 * Returns the concatenation of all the text nodes inside the given element.
	 * Only uses immediate children, no separator chars.
	 */
	protected String getAllText(Element element) {
		StringBuilder result = new StringBuilder(100); 
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				result.append(child.getNodeValue());
			}
		}
		return result.toString();
	}
	
	/**
	 * Returns a List of the child nodes of the given Element that are also Elements themselves.
	 * The List will preserve the input ordering of the child elements. 
	 * This is not a flattening operation (i.e. only immediate children wil be present in the returned list).
	 */
	protected List<Element> getElementChildren(Node element) {
		List<Element> result = new ArrayList<Element>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				result.add((Element)children.item(i));
			}
		}
		return result;
	}
}
