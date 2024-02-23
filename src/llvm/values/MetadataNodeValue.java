package llvm.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeEqualityCheck.IdentityPairSet;
import util.pair.Pair;

/**
 * Represents a metadata node value.
 * Metadata nodes contain other values.
 * MetadataNodeValues can be cyclic, so we have to be careful how we write
 * some of the methods here.
 */
public class MetadataNodeValue extends Value {
	protected final boolean isFunctionLocal;
	protected final List<Value> values;

	public MetadataNodeValue(boolean _isFunctionLocal, List<? extends Value> _values) {
		this.isFunctionLocal = _isFunctionLocal;
		this.values = new ArrayList<Value>(_values);
	}
	
	public boolean is2_8Value() {return true;}
	
	public boolean isFunctionLocal() {return this.isFunctionLocal;}
	public int getNumValues() {return this.values.size();}
	public Value getValue(int i) {return this.values.get(i);}

	public void ensureMetadata() {}
	public Type getType() {return Type.METADATA_TYPE;}
	public Iterator<? extends Value> getSubvalues() {
		List<Value> subs = new ArrayList<Value>();
		for (Value v : values) {
			if (v != null)
				subs.add(v);
		}
		return subs.iterator();
	}
	public boolean isMetadataNode() {return true;}
	public MetadataNodeValue getMetadataNodeSelf() {return this;}

	public String toString() {
		StringBuilder builder = new StringBuilder(100);
		builder.append("!{");
		for (int i = 0; i < this.values.size(); i++) {
			if (i>0) builder.append(", ");
			Value v = this.values.get(i);
			if (v==null)
				builder.append("null");
			else
				builder.append(v.getType());
		}
		return builder.append("}").toString();
	}
	public boolean equalsValue(Value o) {
		if (!o.isMetadataNode())
			return false;
		return equalsMetadata(this, o);
	}
	
	private static boolean equalsMetadata(Value v1, Value v2) {
		IdentityPairSet<Value> seen = new IdentityPairSet<Value>();
		LinkedList<Pair<Value,Value>> worklist = new LinkedList<Pair<Value,Value>>();
		worklist.add(new Pair<Value,Value>(v1,v2));
		
		while (worklist.size() > 0) {
			final Pair<Value,Value> next = worklist.removeFirst();
			final Value left = next.getFirst();
			final Value right = next.getSecond();
			if (seen.hasPair(left, right))
				continue;
			seen.addPair(left, right);
			
			if (left.isMetadataNode() && right.isMetadataNode()) {
				MetadataNodeValue lm = left.getMetadataNodeSelf();
				MetadataNodeValue rm = right.getMetadataNodeSelf();
				if (lm.getNumValues() != rm.getNumValues() ||
					lm.isFunctionLocal() != rm.isFunctionLocal())
					return false;
				for (int i = 0; i < lm.getNumValues(); i++) {
					Value lc = lm.getValue(i);
					Value rc = rm.getValue(i);
					if (lc!=null && rc!=null) {
						worklist.add(new Pair<Value,Value>(lc,rc));
					} else if (lc!=null || rc!=null) {
						return false;
					}
				}
			} else if (left.isMetadataNode() || right.isMetadataNode()) {
				return false;
			} else if (!left.equalsValue(right)) {
				return false;
			}
		}
		
		return true;
	}

	private Integer hashCode_cache = null;
	public int hashCode() {
		if (hashCode_cache == null) {
			hashCode_cache = hashMetadata(this, 0);
		}
		return hashCode_cache;
	}
	
	private static final int THRESHOLD = 5;
	private static int hashMetadata(Value value, int depth) {
		if (depth >= THRESHOLD)
			return 0;
		if (value.isMetadataNode()) {
			MetadataNodeValue meta = value.getMetadataNodeSelf();
			int result = meta.isFunctionLocal() ? 0 : 3;
			for (Value sub : meta.values) {
				if (sub!=null)
					result += 5*hashMetadata(sub, depth+1);
			}
			return result;
		} else {
			return value.hashCode();
		}
	}

	protected MetadataNodeValue rewriteChildren(Map<Value,Value> old2new) {
		List<Value> newvalues = new ArrayList<Value>();
		boolean anynew = false;
		for (int i = 0; i < this.values.size(); i++) {
			Value oldv = this.values.get(i);
			if (oldv != null) {
				Value newv = oldv.rewrite(old2new);
				newvalues.add(newv);
				if (newv != this.values.get(i))
					anynew = true;
			} else {
				newvalues.add(null);
			}
		}
		if (anynew)
			return new MetadataNodeValue(this.isFunctionLocal, newvalues);
		else
			return this;
	}
}
