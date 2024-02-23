package llvm.values;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a mapping of parameters to ParameterAttributes,
 * with special entries for return parameters and function parameters.
 */
public class ParameterAttributeMap {
	protected final ParameterAttributes functionAttrs, returnAttrs;
	protected final Map<Integer,ParameterAttributes> map;
	protected final int maxIndex;
	
	public ParameterAttributeMap() {
		this.map = new HashMap<Integer,ParameterAttributes>();
		this.functionAttrs = null;
		this.returnAttrs = null;
		this.maxIndex = -1;
	}
	public ParameterAttributeMap(boolean is2_8, Map<Integer,ParameterAttributes> _map) {
		this.map = new HashMap<Integer,ParameterAttributes>();
		ParameterAttributes func = null, ret = null;
		int max = 0;
		for (int key : _map.keySet()) {
			if (key == 0) {
				if (is2_8) {
					// function attrs
					func = _map.get(key);
				} else {
					// return attrs;
					ret = _map.get(key);
				}
			} else if (key == -1 && is2_8) {
				func = _map.get(key);
			} else {
				this.map.put(key-1, _map.get(key));
				max = key-1;
			}
		}
		this.functionAttrs = func;
		this.returnAttrs = ret;
		this.maxIndex = max;
	}
	
	public boolean isEmpty() {
		return this.functionAttrs==null && this.returnAttrs==null && this.map.isEmpty();
	}
	/**
	 * The highest parameter index will be getMaxIndex().
	 */
	public int getMaxIndex() {return this.maxIndex;}
	public boolean hasParamAttributes(int i) {return this.map.containsKey(i);}
	public ParameterAttributes getParamAttributes(int i) {return this.map.get(i);}
	public boolean hasReturnAttributes() {return this.returnAttrs!=null;}
	public ParameterAttributes getReturnAttributes() {return this.returnAttrs;}
	public boolean hasFunctionAttributes() {return this.functionAttrs!=null;}
	public ParameterAttributes getFunctionAttributes() {return this.functionAttrs;}
	
	public boolean equals(Object o) {
		if (!(o instanceof ParameterAttributeMap))
			return false;
		ParameterAttributeMap p = (ParameterAttributeMap)o;
		if (this.functionAttrs!=null && p.functionAttrs!=null) {
			if (!this.functionAttrs.equals(p.functionAttrs))
				return false;
		}
		else if (this.functionAttrs!=null || p.functionAttrs!=null)
			return false;
		if (this.returnAttrs!=null && p.returnAttrs!=null) {
			if (!this.returnAttrs.equals(p.returnAttrs))
				return false;
		}
		else if (this.returnAttrs!=null || p.returnAttrs!=null)
			return false;
		return p.map.equals(this.map);
	}
	public int hashCode() {
		return 3*this.map.hashCode() + 
			5*(this.functionAttrs==null ? 0 : this.functionAttrs.hashCode()) +
			7*(this.returnAttrs==null ? 0 : this.returnAttrs.hashCode());
	}
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("attrs[ ");
		if (this.functionAttrs!=null)
			builder.append("func=").append(this.functionAttrs).append(' ');
		if (this.returnAttrs!=null)
			builder.append("ret=").append(this.returnAttrs).append(' ');
		for (int key : this.map.keySet()) {
			builder.append(key).append('=').append(this.map.get(key)).append(' ');
		}
		return builder.append("]").toString();
	}
}
