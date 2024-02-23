package eqsat.meminfer.engine.peg;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.event.AbstractEvent;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import util.ArrayCollection;
import util.BackedMultiMap;
import util.HashMultiMap;
import util.MultiMap;
import util.Triple;
import util.WeakArrayCollection;

public class CPEGValueManager<O, P>
		implements ValueManager<CPEGValue<O,P>>, EventListener<Void> {
	protected final Collection<CPEGValue<O,P>> mValues = new ArrayCollection();
	protected final Event<Triple<CPEGValue<O,P>,CPEGValue<O,P>,Event<Void>>>
			mPreMergeEvent = new AbstractEvent();
	protected final Event<CPEGValue<O,P>> mMergedEvent = new AbstractEvent();
	protected final Collection<MultiMap<CPEGValue<O,P>,?>> mMultiMaps
			= new WeakArrayCollection<MultiMap<CPEGValue<O,P>,?>>();
	protected final Collection<Set<CPEGValue<O,P>>> mSets
			= new WeakArrayCollection<Set<CPEGValue<O,P>>>();
	protected final Map<CPEGValue<O,P>,
			MultiMap<CPEGValue<O,P>,EventListener<? super Void>>>
			mMergedListeners = new HashMap();
	protected final MultiMap<CPEGValue<O,P>,CPEGValue<O,P>> mDisequalities
			= createValueMultiValueMap();

	public Collection<? extends CPEGValue<O,P>> getValues() {return mValues;}

	public CPEGValue<O,P> createValue() {
		CPEGValue<O,P> value = new CPEGValue<O,P>();
		mValues.add(value);
		return value;
	}

	public Set<CPEGValue<O,P>> createValueSet() {
		Set<CPEGValue<O,P>> set = new HashSet<CPEGValue<O,P>>();
		mSets.add(set);
		return set;
	}

	public <K> MultiMap<CPEGValue<O,P>,K> createValueMultiMap() {
		MultiMap<CPEGValue<O,P>,K> map = new HashMultiMap<CPEGValue<O,P>,K>();
		mMultiMaps.add(map);
		return map;
	}

	public MultiMap<CPEGValue<O,P>,CPEGValue<O,P>> createValueMultiValueMap() {
		MultiMap<CPEGValue<O,P>,CPEGValue<O,P>> map
				= new BackedMultiMap<CPEGValue<O,P>,CPEGValue<O,P>>() {
			protected <R> Map<CPEGValue<O,P>,R> makeKeyMap() {
				return new HashMap<CPEGValue<O,P>,R>();
			}
			protected Set<CPEGValue<O,P>> makeValueSet() {
				return createValueSet();
			}
		};
		mMultiMaps.add(map);
		return map;
	}

	public Event<Void> getMergedEvent(final CPEGValue<O,P> left,
			final CPEGValue<O,P> right) {
		if (left.equals(right))
			return new Event<Void>() {
				public void addListener(EventListener<? super Void> listener) {}
				public void addListeners(
						Collection<? extends EventListener<? super Void>>
						listeners) {
				}
				public void trigger(Void parameter) {
					throw new UnsupportedOperationException(
							"Not allowed to trigger");
				}
				public String toString() {
					return left + " already equals " + right;
				}
			};
		else if (canEqual(left, right))
			return new Event<Void>() {
				public void addListener(EventListener<? super Void> listener) {
					if (mMergedListeners.containsKey(left))
						mMergedListeners.get(left).addValue(right, listener);
					else if (mMergedListeners.containsKey(right))
						mMergedListeners.get(right).addValue(left, listener);
					else {
						MultiMap<CPEGValue<O,P>,EventListener<? super Void>>
								map = createValueMultiMap();
						map.addValue(right, listener);
						mMergedListeners.put(left, map);
					}
				}
	
				public void addListeners(
						Collection<? extends EventListener<? super Void>>
						listeners) {
					if (mMergedListeners.containsKey(left))
						mMergedListeners.get(left).addValues(right, listeners);
					else if (mMergedListeners.containsKey(right))
						mMergedListeners.get(right).addValues(left, listeners);
					else {
						MultiMap<CPEGValue<O,P>,EventListener<? super Void>>
								map = createValueMultiMap();
						map.addValues(right, listeners);
						mMergedListeners.put(left, map);
					}
				}
	
				public void trigger(Void parameter) {
					throw new UnsupportedOperationException(
							"Not allowed to trigger");
				}
				
				public String toString() {
					return left + " merged with " + right;
				}
			};
		else
			return new Event<Void>() {
				public void addListener(EventListener<? super Void> listener) {}
				public void addListeners(
						Collection<? extends EventListener<? super Void>>
						listeners) {
				}
				public void trigger(Void parameter) {
					throw new UnsupportedOperationException(
							"Not allowed to trigger");
				}
				public String toString() {
					return left + " cannot equal " + right;
				}
			};
	}

	public Event<Triple<CPEGValue<O,P>,CPEGValue<O,P>,Event<Void>>>
			getPreMergeEvent() {
		return mPreMergeEvent;
	}
	public Event<CPEGValue<O,P>> getMergedEvent() {return mMergedEvent;}

	public Collection<? extends EventListener<? super Void>>
			merge(CPEGValue<O,P> left, CPEGValue<O,P> right) {
		if (left.equals(right))
			return null;
		else if (!canEqual(left, right))
			throw new IllegalStateException("Merging unequal values: " + 
					left.getTerms() + ", " + right.getTerms());
		CPEGValue<O,P> keep = left.getValue(), lose = right.getValue();
		mPreMergeEvent.trigger(
				new Triple(keep, lose, getMergedEvent(keep, lose)));
		mValues.remove(lose);
		mergeTerms(keep, lose);
		mergeSets(keep, lose);
		mergeMultiMaps(keep, lose);
		mergeListeners(keep, lose);
		lose.mValue = keep;
		mMergedEvent.trigger(keep);
		return mMergedListeners.get(keep).removeKey(keep);
	}
	
	protected void mergeTerms(CPEGValue<O,P> keep, CPEGValue<O,P> lose) {
		List<CPEGTerm<O,P>> kept = keep.mTerms;
		List<CPEGTerm<O,P>> lost = lose.mTerms;
		if (kept.size() < lost.size()) {
			lost.addAll(kept);
			keep.mTerms = lost;
		} else
			kept.addAll(lost);
		lose.mTerms = null;
	}
	
	protected void mergeMultiMaps(CPEGValue<O,P> keep, CPEGValue<O,P> lose) {
		for (MultiMap<CPEGValue<O,P>,?> map : mMultiMaps)
			if (map.containsKey(lose))
				map.addValues(keep, (Collection)map.removeKey(lose));
	}
	
	protected void mergeSets(CPEGValue<O,P> keep, CPEGValue<O,P> lose) {
		for (Set<CPEGValue<O,P>> map : mSets)
			if (map.remove(lose))
				map.add(keep);
	}
	
	protected void mergeListeners(CPEGValue<O,P> keep, CPEGValue<O,P> lose) {
		MultiMap<CPEGValue<O,P>,EventListener<? super Void>> kept
				= mMergedListeners.get(keep);
		MultiMap<CPEGValue<O,P>,EventListener<? super Void>> lost
				= mMergedListeners.remove(lose);
		if (kept == null) {
			if (lost == null)
				;
			else
				mMergedListeners.put(keep, lost);
		} else if (lost == null)
			;
		else if (kept.numKeys() < lost.numKeys()) {
			lost.addAll(kept);
			mMergedListeners.put(keep, lost);
		} else
			kept.addAll(lost);
	}

	public boolean canEqual(CPEGValue<O,P> left, CPEGValue<O,P> right) {
		return !mDisequalities.containsEntry(left, right);
	}

	public void makeUnequal(CPEGValue<O,P> left, CPEGValue<O,P> right) {
		if (left.equals(right))
			throw new IllegalStateException("Cannot make same value unequal");
		mDisequalities.addValue(left, right);
		mDisequalities.addValue(right, left);
		if (mMergedListeners.containsKey(left))
			mMergedListeners.get(left).removeKey(right);
		if (mMergedListeners.containsKey(right))
			mMergedListeners.get(right).removeKey(left);
	}

	public boolean canUse(Void parameter) {return true;}
	public boolean notify(Void parameter) {
		for (MultiMap<CPEGValue<O,P>,EventListener<? super Void>> map
				: mMergedListeners.values())
			for (Iterator<EventListener<? super Void>> listeners
					= map.values().iterator(); listeners.hasNext(); )
				if (!listeners.next().canUse(null))
					listeners.remove();
		return false;
	}
	
	public String toString() {return "CPEGValueManager";}
}
