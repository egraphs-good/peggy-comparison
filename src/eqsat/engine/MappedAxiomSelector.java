package eqsat.engine;

import java.util.EnumMap;

public class MappedAxiomSelector<G extends Enum<G>> implements AxiomSelector<G> {
	protected EnumMap<G, Boolean> groupMap;
	protected boolean defaultValue;
	
	public MappedAxiomSelector(boolean _defaultValue, Class<G> clazz) {
		this.groupMap = new EnumMap<G, Boolean>(clazz);
		this.defaultValue = _defaultValue;
	}
	public MappedAxiomSelector(Class<G> clazz) {
		this(false, clazz);
	}
	
	public void setEnabled(G group, boolean enabled) {
		if (enabled == this.defaultValue) {
			this.groupMap.remove(group);
		} else {
			this.groupMap.put(group, enabled);
		}
	}
	
	public boolean isEnabled(G group) {
		Boolean result = this.groupMap.get(group);
		if (result == null)
			return this.defaultValue;
		else
			return result.booleanValue();
	}
}
