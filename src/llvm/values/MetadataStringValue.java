package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;

/**
 * Represents a metadata string.
 * This is just a metadata value whose contents are a string.
 */
public class MetadataStringValue extends Value {
	protected final String value;
	
	public MetadataStringValue(String _value) {
		this.value = _value;
	}	
	
	public boolean is2_8Value() {return true;}
	
	public String getValue() {return this.value;}
	
	public void ensureConstant() {}
	public void ensureMetadata() {}
	public Type getType() {return Type.METADATA_TYPE;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isMetadataString() {return true;}
	public MetadataStringValue getMetadataStringSelf() {return this;}
	
	public String toString() {
		return "!\"" + this.value + "\"";
	}
	public boolean equalsValue(Value o) {
		return o.isMetadataString() && this.value.equals(o.getMetadataStringSelf().getValue());
	}
	public int hashCode() {
		return this.value.hashCode()*79;
	}

	protected MetadataStringValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
