package peggy.represent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import peggy.input.RuleParsingException;

/**
 * This class allows one to read in a PEG from an XML specification.
 * The XML grammar is as follows:
 * 
 * <peg>
 * 	  <define>
 *	     expressions... 
 * 	  </define>
 *    <returns>
 *       <return name="value">
 *          one expression
 *       </return>
 *       <return name="sigma">
 *  		one expression       
 *       </return>
 *    </returns>
 * </peg>
 * 
 * expression ::= <ref id="string"/>
 *              | <nondomain name="theta" index="int"> children </nondomain>
 *              | <nondomain name="eval" index="int"> children </nondomain>
 *              | ... more nondomain ...
 *              | <param name="string"/>   name="sigma"/"counter"/"param0"/...
 *              | ... domain ops ...
 *              ;
 * 
 */
public abstract class XML2PEG<G,E,R> {
	protected void getElementChildren(Element e, List<Element> children) {
		NodeList childNodes = e.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				children.add((Element)childNodes.item(i));
			}
		}
	}
	
	/**
	 * profile = {String tagname, Integer numchildren, String[] required, String[] optional}
	 */
	protected void assertElement(Element e, List<Element> children, Object[] profile) {
		String tagname = (String)profile[0];
		if (!e.getTagName().equals(tagname))
			throw new RuleParsingException("Expecting tagname \"" + tagname + "\", got \"" + e.getTagName() + "\"");
		
		int numchildren = ((Integer)profile[1]).intValue();
		List<Element> childs = ((children == null) ? new ArrayList<Element>() : children);
		this.getElementChildren(e, childs);
		if (numchildren >= 0 && childs.size() != numchildren)
			throw new RuleParsingException("Expecting " + numchildren + " children");
		
		String[] required = (String[])profile[2];
		String[] optional = (String[])profile[3];
		Set<String> allowed = new HashSet<String>();
		if (required != null)
			for (String s : required)
				allowed.add(s);
		if (optional != null)
			for (String s : optional)
				allowed.add(s);
		NamedNodeMap attrs = e.getAttributes();
		Set<String> foundAttrs = new HashSet<String>();
		for (int i = 0; i < attrs.getLength(); i++) {
			String name = ((Attr)attrs.item(i)).getName();
			if (!allowed.contains(name))
				throw new RuleParsingException("Found extraneous attribute for " + e.getTagName() + " element: " + name);
			foundAttrs.add(name);
		}
		if (required!=null) {
			for (String s : required) {
				if (!foundAttrs.contains(s))
					throw new RuleParsingException("Missing required attribute '" + s + "' for element " + e.getTagName());
			}
		}
	}
	
	/////////////////////////////////
	
	protected abstract E getParamNode(G graph, String paramname);
	protected abstract G getFreshGraph();
	protected abstract E getPlaceHolder(G graph);
	protected abstract void replaceWith(E placeholder, E replacement);
	protected abstract R getReturn(String rootname);
	protected abstract E parseDomainNode(
			Element element, G graph, Map<String,E> id2vertex);
	
	private static final Object[] PEG_INFO = {"peg", -1, null, null};
	private static final Object[] DEFINE_INFO = {"define", -1, null, null};
	private static final Object[] RETURNS_INFO = {"returns", -1, null, null};
	private static final Object[] RETURN_INFO = {"return", 1, new String[]{"name"}, null};
	private static final Object[] REF_INFO = {"ref", 0, new String[]{"id"}, null};
	private static final Object[] PARAMETER_INFO = {"parameter", 0, new String[]{"name"}, new String[]{"id"}};
	private static final Object[] NONDOMAIN_INFO = {"nondomain", -1, new String[]{"name"}, new String[]{"id", "index"}};
	
	/**
	 * Returns a new graph that has been parsed from the given XML document root element.
	 * The 'outputs' map will map returns to their expressions within the graph.
	 */
	public final G parsePEG(Element docroot, Map<R,E> outputs) {
		List<Element> children = new ArrayList<Element>();
		assertElement(docroot, children, PEG_INFO);
			
		G result = getFreshGraph();
		Map<String,E> id2vertex = new HashMap<String,E>();

		for (Element child : children) {
			if (child.getTagName().equals("define")) {
				List<Element> defineChildren = new ArrayList<Element>();
				assertElement(child, defineChildren, DEFINE_INFO);
				for (Element dc : defineChildren) {
					this.parseNode(dc, result, id2vertex);
				}
			} else if (child.getTagName().equals("returns")) {
				List<Element> returnsChildren = new ArrayList<Element>();
				assertElement(child, returnsChildren, RETURNS_INFO);
				for (Element rc : returnsChildren) {
					this.parseReturn(rc, result, id2vertex, outputs);
				}
			} else {
				throw new RuleParsingException("Unexpected element: " + child.getTagName());
			}
		}
		
		return result;
	}
	
	private E parseReturn(Element returnElement, G graph, Map<String,E> id2vertex, Map<R,E> outputs) {
		List<Element> children = new ArrayList<Element>();
		assertElement(returnElement, children, RETURN_INFO);
		R arr = this.getReturn(returnElement.getAttribute("name"));
		E expr = this.parseNode(children.get(0), graph, id2vertex);
		outputs.put(arr, expr);
		return expr;
	}
	
	// assume element's id maps to either null or a new placeholder
	protected E patchID(Element element, E result, Map<String,E> id2vertex) {
		if (element.hasAttribute("id")) {
			String id = element.getAttribute("id");
			if (id2vertex.containsKey(id)) {
				E placeholder = id2vertex.get(id);
				if (placeholder != null)
					this.replaceWith(placeholder, result);
				id2vertex.put(id, result);
			}
		}
		return result;
	}
	
	protected E registerID(Element element, E result, Map<String,E> id2vertex) {
		if (element.hasAttribute("id")) {
			String id = element.getAttribute("id");
			if (id2vertex.containsKey(id))
				throw new RuleParsingException("Duplicate id found: " + id);
			id2vertex.put(id, result);
		}
		return result;
	}
	
	private int getNondomainIndex(Element e) {
		if (!e.hasAttribute("index"))
			throw new RuleParsingException(
					"Nondomain node " + 
					e.getAttribute("name") + 
					" expects attribute 'index'");
		int result;
		try {
			result = Integer.parseInt(e.getAttribute("index"));
		} catch (Throwable t) {
			throw new RuleParsingException("Cannot parse string as int: " + e.getAttribute("index"));
		}
		if (result<1)
			throw new RuleParsingException("Index must be positive");
		return result;
	}
	
//	protected abstract E getSplitNode(G graph, List<E> children);
	protected abstract E getThetaNode(G graph, int index, List<E> children);
	protected abstract E getEvalNode(G graph, int index, List<E> children);
	protected abstract E getPassNode(G graph, int index, List<E> children);
	protected abstract E getShiftNode(G graph, int index, List<E> children);
	protected abstract E getPhiNode(G graph, List<E> children);
	protected abstract E getAndNode(G graph, List<E> children);
	protected abstract E getOrNode(G graph, List<E> children);
	protected abstract E getNegateNode(G graph, List<E> children);
	protected abstract E getEqualsNode(G graph, List<E> children);
	protected abstract E getSuccessorNode(G graph, List<E> children);
	protected abstract E getZeroNode(G graph);
	
	protected final E parseNondomainNode(
			Element element, G graph, Map<String,E> id2vertex) {
		List<Element> children = new ArrayList<Element>();
		assertElement(element, children, NONDOMAIN_INFO);
		String name = element.getAttribute("name");
		
		if (name.equals("theta")) {
			if (children.size() != 2)
				throw new RuleParsingException("Wrong number of children");
			int index = this.getNondomainIndex(element);
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(2);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getThetaNode(graph, index, childNodes);
			return this.patchID(element, result, id2vertex);
//		} else if (name.equals("split")) {
//			if (children.size() != 1)
//				throw new RuleParsingException("Wrong number of children");
//			this.registerID(element, null, id2vertex);
//			
//			List<E> childNodes = new ArrayList<E>(2);
//			for (Element child : children) {
//				childNodes.add(this.parseNode(child, graph, id2vertex));
//			}
//			E result = this.getSplitNode(graph, childNodes);
//			return this.patchID(element, result, id2vertex);
		} else if (name.equals("eval")) {
			if (children.size() != 2)
				throw new RuleParsingException("Wrong number of children");
			int index = this.getNondomainIndex(element);
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(2);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getEvalNode(graph, index, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("pass")) {
			if (children.size() != 1)
				throw new RuleParsingException("Wrong number of children");
			int index = this.getNondomainIndex(element);
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(2);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getPassNode(graph, index, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("shift")) {
			if (children.size() != 1)
				throw new RuleParsingException("Wrong number of children");
			int index = this.getNondomainIndex(element);
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(2);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getShiftNode(graph, index, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("phi")) {
			if (children.size() != 3)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getPhiNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("and")) {
			if (children.size() != 2)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getAndNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("or")) {
			if (children.size() != 2)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getOrNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("negate")) {
			if (children.size() != 1)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getNegateNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("equals")) {
			if (children.size() != 2)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getEqualsNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("successor")) {
			if (children.size() != 1)
				throw new RuleParsingException("Wrong number of children");
			this.registerID(element, null, id2vertex);
			
			List<E> childNodes = new ArrayList<E>(3);
			for (Element child : children) {
				childNodes.add(this.parseNode(child, graph, id2vertex));
			}
			E result = this.getSuccessorNode(graph, childNodes);
			return this.patchID(element, result, id2vertex);
		} else if (name.equals("zero")) {
			if (children.size() != 0)
				throw new RuleParsingException("Wrong number of children");
			E result = this.getZeroNode(graph);
			return this.registerID(element, result, id2vertex);
		} else {
			throw new RuleParsingException("Unexpected name: " + name);
		}
	}
	
	protected E parseNode(Element element, G graph, Map<String,E> id2vertex) {
		String tagname = element.getTagName();
		
		if (tagname.equals("ref")) {
			assertElement(element, null, REF_INFO);
			String id = element.getAttribute("id");
			if (!id2vertex.containsKey(id))
				throw new RuleParsingException("Undefined id: " + id);
			
			E result = id2vertex.get(id);
			if (result == null) {
				result = this.getPlaceHolder(graph);
				id2vertex.put(id, result);
			}
			return result;
		} else if (tagname.equals("parameter")) {
			assertElement(element, null, PARAMETER_INFO);
			E node = this.getParamNode(graph, element.getAttribute("name"));
			return this.registerID(element, node, id2vertex);
		} else if (tagname.equals("nondomain")) {
			return this.parseNondomainNode(element, graph, id2vertex);
		} else {
			return this.parseDomainNode(element, graph, id2vertex);
		}
	}

}