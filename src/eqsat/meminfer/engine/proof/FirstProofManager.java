package eqsat.meminfer.engine.proof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eqsat.meminfer.engine.basic.Ambassador;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.Value;
import util.AbstractPattern;
import util.Collections;
import util.Function;
import util.PriorityHeap;
import util.UnhandledCaseException;
import util.integer.PairInt;
import util.pair.Couple;
import util.pair.DoublePairedList;
import util.pair.Pair;
import util.pair.PairedList;

public class FirstProofManager<T extends Term<T,V>, V extends Value<T,V>>
		implements ProofManager<T,V> {
	protected final static class ProofLink<T,V> {
		private static final ProofLink mNil = new ProofLink();
		private static final Comparator<ProofLink> mPrioritizer
				= new Comparator<ProofLink>() {
			public int compare(ProofLink first, ProofLink second) {
				return second.mLength - first.mLength;
			}
		};
		
		protected final TermOrTermChild<T,V> mRepresentative;
		protected final Proof mProof;
		protected final int mTime;
		protected final ProofLink<T,V> mTail;
		private final int mLength;
		
		private ProofLink() {
			mRepresentative = null;
			mProof = null;
			mTime = Integer.MIN_VALUE;
			mTail = null;
			mLength = 0;
		}
		protected ProofLink(TermOrTermChild<T,V> representative, Proof proof,
				int time, ProofLink<T,V> tail) {
			mRepresentative = representative;
			mProof = proof;
			mTime = time;
			mTail = tail;
			mLength = mTail.mLength + 1;
		}
		
		protected static <T,V> ProofLink<T,V> nil() {return mNil;}
		protected static <T,V> Comparator<? super ProofLink<T,V>> prioritizer(){
			return mPrioritizer;
		}
		
		protected int size() {return mLength;}
		
		protected void addToVertices(
				Collection<? super TermOrTermChild<T,V>> vertices) {
			if (mTail == null)
				return;
			mTail.addToVertices(vertices);
			vertices.add(mRepresentative);
		}
		
		protected void addToProofs(Collection<? super Proof> proofs) {
			if (mTail == null)
				return;
			mTail.addToProofs(proofs);
			proofs.add(mProof);
		}
		
		protected int getTime() {
			if (mTail == null)
				return Integer.MIN_VALUE;
			else if (mProof == null)
				return mTail.getTime();
			else
				return Math.max(mTime, mTail.getTime());
		}
		
		public String toString() {
			if (mTail == null)
				return "[]";
			else
				return mTail + ":(" + mRepresentative + "," + mProof + ")";
		}
	}
	
	protected final Map<Couple<TermOrTermChild<T,V>>,PairInt<Proof>> mProofs
			= new HashMap<Couple<TermOrTermChild<T,V>>,PairInt<Proof>>();

	public void addEqualityProof(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right, Proof proof, int time) {
		if (left.getValue().equals(right.getValue()))
			return;
		Couple<TermOrTermChild<T,V>> equality
				= new Couple<TermOrTermChild<T,V>>(left, right);
		if (!mProofs.containsKey(equality))
			mProofs.put(equality, new PairInt<Proof>(proof, time));
	}
	
	public PairedList<TermOrTermChild<T,V>,Proof> getProofPath(
			TermOrTermChild<T,V> from, TermOrTermChild<T,V> to) {
		ProofLink<T,V> path = getShortestProofPath(from, to);
		List<TermOrTermChild<T,V>> vertices = new ArrayList(path.size() + 1);
		vertices.add(from);
		path.addToVertices(vertices);
		List<Proof> proofs = new ArrayList(path.size() + 1);
		path.addToProofs(proofs);
		proofs.add(null);
		return new DoublePairedList<TermOrTermChild<T,V>,Proof>(vertices,
				proofs);
	}
	
	private ProofLink<T,V> getShortestProofPath(
			TermOrTermChild<T,V> from, TermOrTermChild<T,V> to) {
		if (!from.getValue().equals(to.getValue()))
			throw new IllegalArgumentException();
		Set<TermOrTermChild<T,V>> visited = new HashSet();
		PriorityHeap<TermOrTermChild<T,V>,ProofLink<T,V>> fringe
				= new PriorityHeap(ProofLink.<T,V>prioritizer());
		fringe.improvePriority(from, ProofLink.<T,V>nil());
		do {
			ProofLink<T,V> path = fringe.peekPriority();
			TermOrTermChild<T,V> rep = fringe.pop();
			if (rep.equals(to))
				return path;
			visited.add(rep);
			for (Iterator<Pair<TermOrTermChild<T,V>,PairInt<Proof>>> neighbors
					= getNeighbors(rep, to); neighbors.hasNext(); ) {
				Pair<TermOrTermChild<T,V>,PairInt<Proof>> link
						= neighbors.next();
				TermOrTermChild<T,V> neighbor = link.getFirst();
				if (!visited.contains(neighbor))
					fringe.improvePriority(neighbor, new ProofLink<T,V>(
							neighbor, link.getSecond().getFirst(),
							link.getSecond().getSecond(), path));
			}
		} while (!fringe.isEmpty());
		throw new RuntimeException();
	}

	public int getTimeOfEquality(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right) {
		return getShortestProofPath(left, right).getTime();
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
	
	private Iterator<TermOrTermChild<T,V>> getGivenNeighbors(
			final TermOrTermChild<T,V> vertex, TermOrTermChild<T,V> end) {
		HashSet<TermOrTermChild<T,V>> given = new HashSet();
		for (Couple<TermOrTermChild<T,V>> couple : mProofs.keySet()) {
			if (givenEquality(vertex, couple.getLeft()))
				given.add(couple.getLeft());
			if (givenEquality(vertex, couple.getRight()))
				given.add(couple.getRight());
		}
		if (givenEquality(vertex, end))
			given.add(end);
		given.remove(vertex);
		return given.iterator();
	}
	
	private Iterator<Pair<TermOrTermChild<T,V>,PairInt<Proof>>>
			getNeighbors(final TermOrTermChild<T,V> vertex,
					TermOrTermChild<T,V> end) {
		return Collections.concatonateIterators(
				Collections.mapIterator(getGivenNeighbors(vertex, end),
				new Function<TermOrTermChild<T,V>,
						Pair<TermOrTermChild<T,V>,PairInt<Proof>>>() {
			public Pair<TermOrTermChild<T,V>,PairInt<Proof>> get(
					TermOrTermChild<T,V> parameter) {
				return new Pair<TermOrTermChild<T,V>,PairInt<Proof>>(
						parameter, new PairInt<Proof>(null, Integer.MIN_VALUE));
			}
		}), Collections.mapIterator(
				Collections.filterIterator(mProofs.entrySet().iterator(),
				new AbstractPattern
				<Entry<Couple<TermOrTermChild<T,V>>,PairInt<Proof>>>() {
			public boolean matches(
					Entry<Couple<TermOrTermChild<T,V>>,PairInt<Proof>> entry) {
				return entry.getKey().contains(vertex);
			}
		}), new Function<Entry<Couple<TermOrTermChild<T,V>>,PairInt<Proof>>,
				Pair<TermOrTermChild<T,V>,PairInt<Proof>>>() {
			public Pair<TermOrTermChild<T,V>,PairInt<Proof>> get(
					Entry<Couple<TermOrTermChild<T,V>>,PairInt<Proof>> entry) {
				return new Pair<TermOrTermChild<T,V>,PairInt<Proof>>(
						entry.getKey().getLeft().equals(vertex)
								? entry.getKey().getRight()
								: entry.getKey().getLeft(),
						entry.getValue());
			}
		}));
	}
}
