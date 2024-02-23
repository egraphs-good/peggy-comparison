package peggy.analysis.java;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import peggy.analysis.java.inlining.HierarchyWrapper;
import soot.SootClass;

/**
 * A ClassSet represents an immutable set of Java classes, as
 * represented by SootClass instances.
 * A ClassSet can be either one of the leaf sets or a combination
 * of ClassSets using set constructors. The leaf sets are the empty set,
 * singleton(C) for some class C, the set of all classes, and cone(C) for 
 * some class C.
 * The set constructors are union and difference.
 * 
 * @author steppm
 */
public abstract class ClassSet implements Iterable<SootClass> {
	public static ClassSet EMPTY_CLASSES = new EmptyClassSet();
	public static ClassSet ALL_CLASSES = new AllClassSet();

	public boolean isAll() {return false;}
	
	private Boolean isEmpty_cache;
	public boolean isEmpty() {
		if (this.isEmpty_cache == null) {
			this.isEmpty_cache = new Boolean(!this.iterator().hasNext());
		}
		return this.isEmpty_cache.booleanValue();
	}
	public abstract boolean containsClass(SootClass clazz);
	public abstract Iterator<SootClass> iterator();
	public abstract String toString();
	protected abstract boolean isEquivalent(ClassSet cs);
	
	/**
	 * Returns true iff this ClassSet is a 
	 * subset of 'set.
	 */
	public final boolean isSubSet(ClassSet set) {
		if (set.isAll())
			return true;
		else if (this.isAll())
			return false;
		else if (this.isEmpty())
			return true;
		for (SootClass mine : this) {
			if (!set.containsClass(mine))
				return false;
		}
		return true;
	}
	
	
	/**
	 * Returns a new ClassSet that represents the union of
	 * this ClassSet with the given one.
	 */
	public final ClassSet union(ClassSet c) {
		if (c.isEmpty())
			return this;
		else if (this.isEmpty())
			return c;
		else if (c.isAll())
			return c;
		else if (this.isAll())
			return this;
		else if (c.isEquivalent(this))
			return c;
		else
			return new UnionClassSet(this, c);
	}
	
	public final ClassSet intersection(ClassSet c) {
		if (c.isEmpty())
			return EMPTY_CLASSES;
		else if (this.isEmpty())
			return EMPTY_CLASSES;
		else if (c.isAll())
			return this;
		else if (this.isAll())
			return c;
		else if (this.isEquivalent(c))
			return this;
		else
			return new IntersectionClassSet(this, c);
	}
	
	public final ClassSet difference(ClassSet c) {
		if (c.isAll())
			return EMPTY_CLASSES;
		else if (c.isEmpty())
			return this;
		else if (c.isEquivalent(this))
			return EMPTY_CLASSES;
		else
			return new DifferenceClassSet(this, c);
	}
	
	public static ClassSet cone(SootClass clazz) {
		if (clazz.getName().equals("java.lang.Object"))
			return ALL_CLASSES;
		else
			return new ConeClassSet(clazz);
	}
	
	public static ClassSet singleton(SootClass clazz) {
		return new SingletonClassSet(clazz);
	}

	private static class UnionClassSet extends ClassSet {
		protected ClassSet left, right;
		public UnionClassSet(ClassSet _left, ClassSet _right) {
			this.left = _left;
			this.right = _right;
		}
		public boolean containsClass(SootClass clazz) {
			return this.left.containsClass(clazz) || this.right.containsClass(clazz);
		}
		public Iterator<SootClass> iterator() {
			final Set<SootClass> seen = new HashSet<SootClass>();
			final Iterator<SootClass> leftIter = this.left.iterator();
			final Iterator<SootClass> rightIter = this.right.iterator();
			return new Iterator<SootClass>() {
				private SootClass saved;
				public boolean hasNext() {
					if (this.saved != null)
						return true;
					if (leftIter.hasNext()) {
						this.saved = leftIter.next();
						return true;
					} else if (rightIter.hasNext()) {
						while (rightIter.hasNext()) {
							SootClass temp = rightIter.next();
							if (seen.contains(temp))
								continue;
							this.saved = temp;
							return true;
						}
						return false;
					} else {
						return false;
					}
				}
				public SootClass next() {
					if (!this.hasNext())
						throw new NoSuchElementException();
					SootClass temp = this.saved;
					this.saved = null;
					seen.add(temp);
					return temp;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		public String toString() {
			return "Union[" + this.left.toString() + "," + this.right.toString() + "]";
		}
		protected boolean isEquivalent(ClassSet cs) {
			if (cs instanceof UnionClassSet) {
				UnionClassSet ucs = (UnionClassSet)cs;
				return this.left.isEquivalent(ucs.left) && this.right.isEquivalent(ucs.right);
			}
			return false;
		}
	}
	
	private static class IntersectionClassSet extends ClassSet {
		protected ClassSet left, right;
		public IntersectionClassSet(ClassSet _left, ClassSet _right) {
			this.left = _left;
			this.right = _right;
		}
		public boolean containsClass(SootClass clazz) {
			return this.left.containsClass(clazz) && this.right.containsClass(clazz);
		}
		public Iterator<SootClass> iterator() {
			final Iterator<SootClass> leftIter = this.left.iterator();
			return new Iterator<SootClass>() {
				private SootClass saved;
				public boolean hasNext() {
					if (this.saved != null)
						return true;
					while (leftIter.hasNext()) {
						SootClass temp = leftIter.next();
						if (!IntersectionClassSet.this.right.containsClass(temp))
							continue;
						this.saved = temp;
						return true;
					}
					return false;
				}
				public SootClass next() {
					if (!this.hasNext())
						throw new NoSuchElementException();
					SootClass result = this.saved;
					this.saved = null;
					return result;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		public String toString() {
			return "Intersection[" + this.left.toString() + "," + this.right.toString() + "]";
		}
		protected boolean isEquivalent(ClassSet cs) {
			if (cs instanceof IntersectionClassSet) {
				IntersectionClassSet ucs = (IntersectionClassSet)cs;
				return this.left.isEquivalent(ucs.left) && this.right.isEquivalent(ucs.right);
			}
			return false;
		}
	}
	
	private static class DifferenceClassSet extends ClassSet {
		protected ClassSet left, right;
		public DifferenceClassSet(ClassSet _left, ClassSet _right) {
			this.left = _left;
			this.right = _right;
		}
		public boolean containsClass(SootClass clazz) {
			return this.left.containsClass(clazz) && !this.right.containsClass(clazz);
		}
		public Iterator<SootClass> iterator() {
			final Iterator<SootClass> leftIter = this.left.iterator();
			return new Iterator<SootClass>() {
				private SootClass saved;
				public boolean hasNext() {
					if (this.saved != null)
						return true;
					while (leftIter.hasNext()) {
						SootClass temp = leftIter.next();
						if (!DifferenceClassSet.this.right.containsClass(temp)) {
							this.saved = temp;
							break;
						}
					}
					return this.saved != null;
				}
				public SootClass next() {
					if (!this.hasNext())
						throw new NoSuchElementException();
					SootClass result = this.saved;
					this.saved = null;
					return result;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		public String toString() {
			return "Difference[" + this.left.toString() + "," + this.right.toString() + "]";
		}
		protected boolean isEquivalent(ClassSet cs) {
			if (cs instanceof DifferenceClassSet) {
				DifferenceClassSet ucs = (DifferenceClassSet)cs;
				return this.left.isEquivalent(ucs.left) && this.right.isEquivalent(ucs.right);
			}
			return false;
		}
	}
	
	private static class ConeClassSet extends ClassSet {
		protected SootClass root;
		public ConeClassSet(SootClass _root) {
			this.root = _root;
		}
		public boolean containsClass(SootClass clazz) {
			if (this.root.isInterface())
				return this.containsInterface(clazz);
			while (true) {
				if (clazz.equals(this.root))
					return true;
				if (clazz.hasSuperclass())
					clazz = clazz.getSuperclass();
				else
					break;
			}
			return false;
		}
		private boolean containsInterface(SootClass clazz) {
			if (clazz.equals(this.root))
				return true;
			for (Iterator it = clazz.getInterfaces().iterator(); it.hasNext(); ) {
				if (this.containsInterface((SootClass)it.next()))
					return true;
			}
			return false;
		}
		public boolean isAll() {return this.root.getName().equals("java.lang.Object");}
		public Iterator<SootClass> iterator() {
			List<SootClass> subs = (this.root.isInterface() ? 
									HierarchyWrapper.getImplementorsOfIncluding(this.root) :
									HierarchyWrapper.getSubclassesOfIncluding(this.root));
			return subs.iterator();
		}
		public String toString() {
			return "Cone[" + this.root.getName() + "]";
		}
		protected boolean isEquivalent(ClassSet cs) {
			if (cs instanceof ConeClassSet) {
				ConeClassSet ucs = (ConeClassSet)cs;
				return this.root.equals(ucs.root);
			}
			return false;
		}
	}
	
	private static class AllClassSet extends ClassSet {
		public boolean containsClass(SootClass clazz) {
			return true;
		}
		public boolean isAll() {return true;}
		public boolean isEmpty() {return false;}
		public Iterator<SootClass> iterator() {
			throw new UnsupportedOperationException();
		}
		public String toString() {
			return "All";
		}
		protected boolean isEquivalent(ClassSet cs) {
			return (cs instanceof AllClassSet);
		}
	}

	private static class EmptyClassSet extends ClassSet {
		public boolean containsClass(SootClass clazz) {
			return false;
		}
		public boolean isEmpty() {return true;}
		public Iterator<SootClass> iterator() {
			return new Iterator<SootClass>() {
				public boolean hasNext() {return false;}
				public SootClass next() {
					throw new NoSuchElementException();
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		public String toString() {
			return "Empty";
		}
		protected boolean isEquivalent(ClassSet cs) {
			return (cs instanceof EmptyClassSet);
		}
	}
	
	private static class SingletonClassSet extends ClassSet {
		protected SootClass singleton;
		public SingletonClassSet(SootClass _clazz) {
			this.singleton = _clazz;
		}
		public boolean containsClass(SootClass clazz) {
			return this.singleton.equals(clazz);
		}
		public Iterator<SootClass> iterator() {
			return new Iterator<SootClass>() {
				private SootClass saved = singleton;
				public boolean hasNext() {
					return this.saved != null;
				}
				public SootClass next() {
					if (!this.hasNext())
						throw new NoSuchElementException();
					SootClass temp = this.saved;
					this.saved = null;
					return temp;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		public String toString() {
			return "Singleton[" + this.singleton.getName() + "]";
		}
		protected boolean isEquivalent(ClassSet cs) {
			if (cs instanceof SingletonClassSet) {
				SingletonClassSet scs = (SingletonClassSet)cs;
				return this.singleton.equals(scs.singleton);
			}
			return false;
		}
	}
}
