package eqsat.meminfer.engine.basic;

import java.util.Collection;
import java.util.Set;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import util.MultiMap;
import util.Triple;

public interface ValueManager<V> {
	Collection<? extends V> getValues();
	V createValue();
	Set<V> createValueSet();
	<K> MultiMap<V,K> createValueMultiMap();
	MultiMap<V,V> createValueMultiValueMap();
	Event<Triple<V,V,Event<Void>>> getPreMergeEvent();
	Event<Void> getMergedEvent(V left, V right);
	Event<V> getMergedEvent();
	Collection<? extends EventListener<? super Void>> merge(V left, V right);
	void makeUnequal(V left, V right);
	boolean canEqual(V left, V right);
}
