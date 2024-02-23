package eqsat.meminfer.engine.proof;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import eqsat.meminfer.engine.basic.Ambassador;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.Value;
import util.AbstractIterable;
import util.AbstractPattern;
import util.Collections;
import util.Function;
import util.HashMultiMap;
import util.MultiMap;
import util.UnhandledCaseException;
import util.pair.ArrayPairedList;
import util.pair.Couple;
import util.pair.Pair;
import util.pair.PairedList;

@Deprecated
public class SimpleProofManager<T extends Term<T,V>, V extends Value<T,V>>
		implements ProofManager<T,V> {
	private final class ProofTracer
			implements Iterator<PairedList<TermOrTermChild<T,V>,Proof>> {
		private final TermOrTermChild<T,V> mEnd;
		private final Stack<TermOrTermChild<T,V>> mVisited = new Stack();
		private final Stack<Proof> mRequirements = new Stack<Proof>();
		private final Stack<Iterator<Pair<TermOrTermChild<T,V>,Proof>>>
				mIterators = new Stack();
		private PairedList<TermOrTermChild<T,V>,Proof> mNext = null;
		
		public ProofTracer(TermOrTermChild<T,V> start,
				TermOrTermChild<T,V> end) {
			if (start.equals(end))
				throw new IllegalArgumentException();
			mEnd = end;
			mVisited.push(start);
			mIterators.push(getNeighbors(start));
		}
		
		public Iterator<TermOrTermChild<T,V>> getGivenNeighbors(
				final TermOrTermChild<T,V> vertex) {
			HashSet<TermOrTermChild<T,V>> given = new HashSet();
			for (Couple<TermOrTermChild<T,V>> couple : mProofs.keySet()) {
				if (givenEquality(vertex, couple.getLeft()))
					given.add(couple.getLeft());
				if (givenEquality(vertex, couple.getRight()))
					given.add(couple.getRight());
			}
			if (givenEquality(vertex, mEnd))
				given.add(mEnd);
			given.remove(vertex);
			return given.iterator();
		}
		
		public Iterator<Pair<TermOrTermChild<T,V>,Proof>>
				getNeighbors(final TermOrTermChild<T,V> vertex) {
			return Collections.concatonateIterators(
					Collections.mapIterator(getGivenNeighbors(vertex),
					new Function<TermOrTermChild<T,V>,
							Pair<TermOrTermChild<T,V>,Proof>>() {
				public Pair<TermOrTermChild<T,V>,Proof> get(
						TermOrTermChild<T,V> parameter) {
					return new Pair<TermOrTermChild<T,V>,Proof>(
							parameter, null);
				}
			}), Collections.mapIterator(
					Collections.filterIterator(mProofs.entries().iterator(),
					new AbstractPattern
					<Entry<Couple<TermOrTermChild<T,V>>,Proof>>() {
				public boolean matches(
						Entry<Couple<TermOrTermChild<T,V>>,Proof> entry) {
					return entry.getKey().contains(vertex);
				}
			}), new Function<Entry<Couple<TermOrTermChild<T,V>>,Proof>,
					Pair<TermOrTermChild<T,V>,Proof>>() {
				public Pair<TermOrTermChild<T,V>,Proof> get(
						Entry<Couple<TermOrTermChild<T,V>>,Proof> entry) {
					return new Pair<TermOrTermChild<T,V>,Proof>(
							entry.getKey().getLeft().equals(vertex)
									? entry.getKey().getRight()
									: entry.getKey().getLeft(),
							entry.getValue());
				}
			}));
		}

		public boolean hasNext() {
			if (mNext != null)
				return true;
			processstack: while (!mIterators.isEmpty()) {
				Iterator<Pair<TermOrTermChild<T,V>,Proof>> iterator
						= mIterators.peek();
				while (iterator.hasNext()) {
					Pair<TermOrTermChild<T,V>,Proof> neighbor = iterator.next();
					if (mVisited.contains(neighbor.getFirst()))
						continue;
					else if (mEnd.equals(neighbor.getFirst())) {
						mNext = new ArrayPairedList(mRequirements.size() + 2);
						Iterator<TermOrTermChild<T,V>> steps
								= mVisited.iterator();
						Iterator<Proof> requirements = mRequirements.iterator();
						while (requirements.hasNext())
							mNext.add(steps.next(), requirements.next());
						mNext.add(steps.next(), neighbor.getSecond());
						mNext.add(neighbor.getFirst(), null);
						return true;
					}
					mVisited.push(neighbor.getFirst());
					mRequirements.push(neighbor.getSecond());
					mIterators.push(getNeighbors(neighbor.getFirst()));
					continue processstack;
				}
				mIterators.pop();
			}
			return false;
		}

		public PairedList<TermOrTermChild<T,V>,Proof> next() {
			if (!hasNext())
				throw new IllegalStateException();
			PairedList<TermOrTermChild<T,V>,Proof> next = mNext;
			mNext = null;
			return next;
		}

		public void remove() {throw new UnsupportedOperationException();}
	}
	
	private final MultiMap<Couple<TermOrTermChild<T,V>>,Proof> mProofs
			= new HashMultiMap<Couple<TermOrTermChild<T,V>>,Proof>();

	public void addEqualityProof(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right, Proof proof, int time) {
		if (!left.getValue().equals(right.getValue()))
			throw new IllegalArgumentException();
		Set<Proof> proofs = mProofs.get(
				new Couple<TermOrTermChild<T,V>>(left, right));
		for (Iterator<Proof> itr = proofs.iterator(); itr.hasNext(); ) {
			Proof that = itr.next();
			if (proof.getProperties().containsAll(that.getProperties()))
				return;
			else if (that.getProperties().containsAll(proof.getProperties()))
				itr.remove();
		}
		proofs.add(proof);
	}
	
	private Iterable<? extends PairedList<TermOrTermChild<T,V>,Proof>>
			getProofPaths(final TermOrTermChild<T,V> from,
					final TermOrTermChild<T,V> to) {
		if (from.equals(to)) {
			PairedList<TermOrTermChild<T,V>,Proof> path
					= new ArrayPairedList<TermOrTermChild<T,V>,Proof>(1);
			path.add(from, null);
			return java.util.Collections.singleton(path);
		} else
			return new AbstractIterable
					<PairedList<TermOrTermChild<T,V>,Proof>>() {
			public Iterator<PairedList<TermOrTermChild<T,V>,Proof>> iterator() {
				return new ProofTracer(from, to);
			}
		};
	}
	
	public PairedList<TermOrTermChild<T,V>,Proof> getProofPath(
			TermOrTermChild<T,V> from, TermOrTermChild<T,V> to) {
		return getShortestProofPath(from, to);
	}
	
	private PairedList<TermOrTermChild<T,V>,Proof> getShortestProofPath(
			TermOrTermChild<T,V> from, TermOrTermChild<T,V> to) {
		if (!from.getValue().equals(to.getValue()))
			throw new IllegalArgumentException();
		PairedList<TermOrTermChild<T,V>,Proof> shortest = null;
		for (PairedList<TermOrTermChild<T,V>,Proof> path
				: getProofPaths(from, to))
			if (shortest == null || path.size() < shortest.size())
				shortest = path;
		return shortest;
	}
	
	private boolean givenEquality(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right) {
		T leftTerm;
		if (left.getRepresentative().isTerm())
			leftTerm = (T)left.getRepresentative();
		else if (left.getRepresentative().isAmbassador())
			leftTerm = ((Ambassador<T,V>)left.getRepresentative()).getTerm();
		else
			throw new UnhandledCaseException();
		if (right.getRepresentative().isTerm())
			return leftTerm.equals((T)right.getRepresentative());
		else if (right.getRepresentative().isAmbassador())
			return leftTerm.equals(
					((Ambassador<T,V>)right.getRepresentative()).getTerm());
		else
			throw new UnhandledCaseException();
	}

	public int getTimeOfEquality(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right) {
		throw new UnsupportedOperationException();
	}
}
