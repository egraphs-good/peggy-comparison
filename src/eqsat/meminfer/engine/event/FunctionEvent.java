package eqsat.meminfer.engine.event;

import java.util.Collection;

import util.pair.Pair;

public interface FunctionEvent<D, R> extends Event<Pair<D,R>> {
	public boolean hasPreimage(R range);
	public Collection<? extends D> getPreimage(R range);
}