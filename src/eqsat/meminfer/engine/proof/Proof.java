package eqsat.meminfer.engine.proof;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Proof {
	private final String mAxiomName;
	private final Set<Property> mProperties;
	
	public Proof(String axiomName) {
		mAxiomName = axiomName;
		mProperties = new HashSet<Property>();
	}
	public Proof(String axiomName, Collection<? extends Property> properties) {
		mAxiomName = axiomName;
		mProperties = new HashSet<Property>(properties);
	}
	public Proof(String axiomName, Property... properties) {
		this(axiomName, Arrays.asList(properties));
	}
	
	public String getAxiomName() {return mAxiomName;}
	public Set<? extends Property> getProperties() {return mProperties;}
	
	public void addProperty(Property property) {mProperties.add(property);}
	public void addProperties(Collection<? extends Property> properties) {
		mProperties.addAll(properties);
	}
	public void addProperties() {}
	public void addProperties(Property property) {mProperties.add(property);}
	public void addProperties(Property... properties) {
		addProperties(Arrays.asList(properties));
	}
	
	public String toString() {return mAxiomName + " " + mProperties;}
}
