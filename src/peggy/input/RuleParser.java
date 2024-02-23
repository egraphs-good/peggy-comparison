package peggy.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;

/**
 * This interface is used by classes that wish to read axioms into the engine
 * from a file or stream.
 */
public interface RuleParser<O,P,T> {
	public AxiomNode<O,? extends PEGNode<O>> parseRule(File file) throws IOException, RuleParsingException;
	public AxiomNode<O,? extends PEGNode<O>> parseRule(InputStream in) throws IOException, RuleParsingException;
	public Collection<AxiomNode<O,? extends PEGNode<O>>> parseRuleSet(File file) throws IOException, RuleParsingException;
	public Collection<AxiomNode<O,? extends PEGNode<O>>> parseRuleSet(InputStream in) throws IOException, RuleParsingException;
	public PeggyAxiomizer<O,T> createAxiomizer(String name);
}
