package peggy.pb;

import java.util.*;

import util.AbstractPattern;
import util.Pattern;

/**
 * This is an ExpressionDigraph that refers to engine values and terms explicitly.
 */
public abstract class EngineExpressionDigraph<V,N> implements ExpressionDigraph<V,N>{
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("EngineExpressionDigraph: " + message);
	}

	private final Map<V,List<N>> mClouds = new HashMap<V,List<N>>();
	private final Set<N> mRepresentations = new HashSet<N>();

	public EngineExpressionDigraph() {
		this.buildClouds();
	}

	protected abstract boolean haveSameLabel(N n1, N n2);
	public abstract Iterable<? extends N> getUnfilteredValueElements(V v);
	public abstract Pattern<? super N> getNodeInclusionPattern();
	
	public Iterable<? extends N> getValueElements(V v) {return this.mClouds.get(v);}
	public int getNodeCount() {return this.mRepresentations.size();}
	public Iterable<? extends N> getNodes() {return this.mRepresentations;}
	public boolean isSuccessor(N parent, int index, N child) {
		return contains(getChildValue(parent, index), child);
	}
	public final boolean contains(V cloud, N element) {
		return this.mClouds.containsKey(cloud) &&
			this.mClouds.get(cloud).contains(element);
	}
	
	protected void buildClouds() {
		final Pattern<? super N> oldpattern = this.getNodeInclusionPattern();
		Pattern<? super N> newpattern;
		
		if (oldpattern != null) {
			newpattern = oldpattern;
		} else {
			newpattern = new AbstractPattern<N>() {
				public boolean matches(N n) {return true;}
			};
		}

		boolean changed = false;
		for (V value : this.getValues()) {
			List<N> representations = new ArrayList<N>();

			for (N representation : util.Collections.filterIterable(this.getUnfilteredValueElements(value), newpattern))
				representations.add(representation);
			for (N representation : representations)
				if (this.getArity(representation) == 0) {
					representations = Collections.singletonList(representation);
					break;
				}
			if (representations.isEmpty()) {
				representations = Collections.emptyList();
				changed = true;
			}
			this.mClouds.put(value, representations);
		}
		
		// filter out duplicates
		for (List<N> reps : this.mClouds.values()) {
			for (int i = 0; i < reps.size(); i++) {
				jloop:
				for (int j = i+1; j < reps.size(); j++) {
					N ni = reps.get(i);
					N nj = reps.get(j);
					if (haveSameLabel(ni, nj) && 
						getArity(ni) == getArity(nj)) {
						// check for same child values
						for (int k = 0; k < getArity(ni); k++) {
							if (!getChildValue(ni, k).equals(getChildValue(nj,k)))
								continue jloop;
						}
						// same! remove j
						reps.remove(j);
						j--;
						debug("Removed a duplicate!");
						if (reps.size() == 0)
							changed = true;
					}
				}
			}
		}
		
		while (changed) {
			changed = false;
			for (Collection<N> representations : this.mClouds.values()) {
				if (representations.isEmpty())
					continue;
				for (Iterator<N> itr = representations.iterator(); itr.hasNext(); ) {
					N representation = itr.next();
					for (int child = getArity(representation); child-- != 0; )
						if (this.mClouds.get(this.getChildValue(representation,child)).isEmpty()) {
							itr.remove();
							break;
						}
				}
				if (representations.isEmpty())
					changed = true;
			}
		}
		
		for (Collection<N> representations : this.mClouds.values())
			this.mRepresentations.addAll(representations);
	}
	
	public Iterable<? extends N> getSuccessors(final N node) {
		return new Iterable<N>() {
			public Iterator<N> iterator() {
				return new Iterator<N>() {
					private int childIndex = 0;
					private Iterator<? extends N> reps;
					
					public boolean hasNext() {
						while (this.reps==null || !this.reps.hasNext()) {
							if (this.childIndex < getArity(node)) {
								this.reps = getValueElements(getChildValue(node, this.childIndex)).iterator();
								this.childIndex++;
							} else {
								return false;
							}
						}
						return true;
					}
					
					public N next() {
						if (!this.hasNext())
							throw new NoSuchElementException("Iterator empty!");
						return this.reps.next();
					}
					
					public void remove() {
						throw new UnsupportedOperationException("No remove!");
					}
				};
			}
		};
	}

	public Digraph<N> getReverseDigraph() {
		return new Digraph<N>() {
			public int getNodeCount() {return EngineExpressionDigraph.this.getNodeCount();}
			public Iterable<? extends N> getNodes() {return EngineExpressionDigraph.this.getNodes();}
			public Digraph<N> getReverseDigraph() {return EngineExpressionDigraph.this;}
			public Iterable<? extends N> getSuccessors(final N node) {
				return util.Collections.filterIterable(mRepresentations, new AbstractPattern<N>() {
					public boolean matches(N representation) {
						for (int child = getArity(representation); child-- != 0; )
							if (isSuccessor(representation, child, node))
								return true;
						return false;
					}
				});
			}
		};
	}
}
