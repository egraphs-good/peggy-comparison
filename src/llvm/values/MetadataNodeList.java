package llvm.values;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a metadata node list.
 * This is simply a list of metadata values. 
 */
public class MetadataNodeList {
	protected final List<Value> nodes = new ArrayList<Value>();
	public void addNode(Value v) {
		v.ensureMetadata();
		nodes.add(v);
	}
	public int getNumNodes() {return this.nodes.size();}
	public Value getNode(int i) {return this.nodes.get(i);}
}
