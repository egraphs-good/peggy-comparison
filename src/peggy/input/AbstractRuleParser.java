package peggy.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This is a RuleParser with some default implementations of some of the
 * methods.
 */
public abstract class AbstractRuleParser<O,P,T> implements RuleParser<O,P,T> {
	/**
	 * Opens the given File with a FileInputStream, and then calls
	 * parseRule(RuleNetwork, InputStream). The FileInputStream will be closed
	 * afterwards, unless an exception occurs.
	 */
	public AxiomNode<O,? extends PEGNode<O>> parseRule(File file) throws IOException, RuleParsingException {
		FileInputStream fin = new FileInputStream(file);
		AxiomNode<O,? extends PEGNode<O>> result = this.parseRule(fin);
		fin.close();
		return result;
	}
	
	
	/**
	 * Opens the given File with a FileInputStream, and then calls
	 * parseRuleSet(RuleNetwork, InputStream). The FileInputStream
	 * will be closed afterwards, unless an exception occurs.
	 */
	public Collection<AxiomNode<O,? extends PEGNode<O>>> parseRuleSet(File file) throws IOException, RuleParsingException {
		FileInputStream fin = new FileInputStream(file);
		Collection<AxiomNode<O,? extends PEGNode<O>>> result = this.parseRuleSet(fin);
		fin.close();
		return result;
	}
}
