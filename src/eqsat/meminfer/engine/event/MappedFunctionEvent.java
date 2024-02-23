package eqsat.meminfer.engine.event;

import java.util.Collection;
import java.util.Set;

import util.MultiMap;
import util.pair.Pair;

public class MappedFunctionEvent<D, R> extends AbstractEvent<Pair<D,R>>
		implements FunctionEvent<D,R> {
	protected final MultiMap<R,D> mMatches;
	
	public MappedFunctionEvent(MultiMap<R,D> map) {mMatches = map;}

	public Collection<? extends D> getPreimage(R range) {
		return mMatches.get(range);
	}

	public boolean hasPreimage(R range) {return mMatches.containsKey(range);}

	protected void addMatch(D domain, R range) {
		Set<D> matches = mMatches.get(range);
		if (matches.add(domain))
			trigger(new Pair<D,R>(domain, range));
	}
}
