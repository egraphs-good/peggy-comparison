package eqsat.meminfer.engine.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import eqsat.meminfer.engine.event.AbstractEvent;
import eqsat.meminfer.engine.event.AbstractProofChainEvent;
import eqsat.meminfer.engine.event.AbstractProofEvent;
import eqsat.meminfer.engine.event.AbstractTermProofEvent;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.event.EventListenerClosure;
import eqsat.meminfer.engine.event.ProofConvertEvent;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.event.ProofPatternEvent;
import eqsat.meminfer.engine.event.TermProofPatternEvent;
import eqsat.meminfer.engine.proof.AreEquivalent;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.engine.proof.ProofManager;
import eqsat.meminfer.engine.proof.Property;
import eqsat.meminfer.network.basic.StructureNetwork.AnyChildNode;
import eqsat.meminfer.network.basic.StructureNetwork.CheckArityNode;
import eqsat.meminfer.network.basic.StructureNetwork.CheckChildEqualityNode;
import eqsat.meminfer.network.basic.StructureNetwork.CheckChildIsFalseNode;
import eqsat.meminfer.network.basic.StructureNetwork.CheckChildIsKnownNode;
import eqsat.meminfer.network.basic.StructureNetwork.CheckEqualityNode;
import eqsat.meminfer.network.basic.StructureNetwork.FalseNode;
import eqsat.meminfer.network.basic.StructureNetwork.GeneralNode;
import eqsat.meminfer.network.basic.StructureNetwork.JoinNode;
import eqsat.meminfer.network.basic.StructureNetwork.KnownNode;
import eqsat.meminfer.network.basic.StructureNetwork.ProductJoinNode;
import eqsat.meminfer.network.basic.StructureNetwork.RepresentNode;
import eqsat.meminfer.network.basic.StructureNetwork.RepresentativeNode;
import eqsat.meminfer.network.basic.StructureNetwork.StructureNode;
import eqsat.meminfer.network.basic.StructureNetwork.TermNode;
import eqsat.meminfer.network.basic.TermValueNetwork.ComponentValueNode;
import eqsat.meminfer.network.basic.TermValueNetwork.TermValueNode;
import eqsat.meminfer.network.basic.ValueNetwork.ChildValueNode;
import eqsat.meminfer.network.basic.ValueNetwork.ValueNode;
import util.Function;
import util.LinkList;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.Triple;
import util.UnhandledCaseException;
import util.WrappingArrayList;

public abstract class EGraphManager<T extends Term<T,V>, V extends Value<T,V>> {
	private static final class RepresentedStructure
			<T extends Term<T,V>, V extends Value<T,V>> {
		private final Structure<T> mStructure;
		private final TermOrTermChild<T,V> mRepresentative;
		
		public RepresentedStructure(Structure<T> structure,
				TermOrTermChild<T,V> representative) {
			mStructure = structure;
			mRepresentative = representative;
		}
		
		public Structure<T> getStructure() {return mStructure;}
		public TermOrTermChild<T,V> getRepresentative() {
			return mRepresentative;
		}
		
		public boolean equals(Object that) {
			return that instanceof RepresentedStructure
					&& equals((RepresentedStructure)that);
		}
		public boolean equals(RepresentedStructure that) {
			return (mStructure == null ? that.mStructure == null
							: mStructure.equals(that.mStructure))
					&& (mRepresentative == null ? that.mRepresentative == null
							: mRepresentative.equals(that.mRepresentative));
		}
		
		public int hashCode() {
			return (mStructure == null ? 0 : mStructure.hashCode())
					+ 37 * (mRepresentative == null
							? 0 : mRepresentative.hashCode());
		}
		
		public String toString() {
			return "[" + (mStructure == null ? "<null>"
							: mStructure.toString()) + ","
					+ (mRepresentative == null ? "<null>"
							: mRepresentative.toString()) + "]";
		}
	}
	
	private static class RepresentativeEvent
			<T extends Term<T,V>, V extends Value<T,V>>
			extends AbstractProofChainEvent
			<T,RepresentedStructure<T,V>,RepresentedStructure<T,V>> {
		private final MultiMap<V,RepresentedStructure<T,V>> mMatches;
		
		public RepresentativeEvent(EGraphManager<T,V> manager,
				ProofEvent<T,RepresentedStructure<T,V>> input) {
			super(input);
			mMatches = manager.getValueManager().
					<RepresentedStructure<T,V>>createValueMultiMap();
			manager.getRemoveTermEvent().addListener(new EventListener<T>() {
				public boolean canUse(T parameter) {return true;}
				public boolean notify(T parameter) {
					for (Iterator<RepresentedStructure<T,V>> matches
							= mMatches.values().iterator();
							matches.hasNext(); ) {
						RepresentedStructure<T,V> match = matches.next();
						if (match.getStructure().isRemoved())
							matches.remove();
					}
					return true;
				}
			});
		}

		public Collection<? extends RepresentedStructure<T,V>> getPreimage(
				V range) {
			return mMatches.get(range);
		}

		public boolean hasPreimage(V range) {
			return mMatches.containsKey(range);
		}

		protected void addMatch(RepresentedStructure<T,V> domain, V range) {
			Set<RepresentedStructure<T,V>> matches = mMatches.get(range);
			if (matches.add(domain))
				trigger(domain);
		}
		
		public boolean notify(RepresentedStructure<T,V> parameter) {
			if (canUse(parameter))
				addMatch(parameter, parameter.getRepresentative().getValue());
			return true;
		}
		
		public boolean canUse(RepresentedStructure<T,V> parameter) {
			if (parameter.getStructure() != null
					&& parameter.getStructure().isRemoved())
				return false;
			return listenersCanUse(parameter);
		}

		protected void addConstraints(Structure<T> result, Proof proof) {}
		
		public String toString() {return "Representative";}
	}
	
	private static final class JoinEvent
			<T extends Term<T,V>, V extends Value<T,V>>
			extends AbstractEvent<Structure<T>>
			implements EventListener<Triple<V,V,Event<Void>>>,
			ProofEvent<T,Structure<T>> {
		private static final class ProofComposeStructure
				<T extends Term<T,V>, V extends Value<T,V>>
				implements Structure<T> {
			protected final RepresentedStructure<T,V> mFirst, mSecond;
			
			public ProofComposeStructure(RepresentedStructure<T,V> first,
					RepresentedStructure<T,V> second) {
				if (first == null || second == null)
					throw new NullPointerException();
				mFirst = first;
				mSecond = second;
			}
			
			public int getTermCount() {
				return mFirst.getStructure().getTermCount()
						+ mSecond.getStructure().getTermCount();
			}
			
			public T getTerm(int index) {
				if (index < mFirst.getStructure().getTermCount())
					return mFirst.getStructure().getTerm(index);
				else
					return mSecond.getStructure().getTerm(
							index - mFirst.getStructure().getTermCount());
			}
			
			public boolean isComplete() {
				return mFirst.getStructure().isComplete()
						&& mSecond.getStructure().isComplete();
			}
			
			public boolean isRemoved() {
				return mFirst.getStructure().isRemoved()
						|| mSecond.getStructure().isRemoved();
			}
			
			public String toString() {
				return "[" + mFirst.getStructure() + ","
						+ mSecond.getStructure() + "]";
			}
		}
		
		protected final RepresentativeEvent<T,V> mLeft, mRight;
		protected final int mTermCount;
		
		public JoinEvent(ValueManager<V> manager, int termCount,
				RepresentativeEvent<T,V> left, RepresentativeEvent<T,V> right) {
			mTermCount = termCount;
			manager.getPreMergeEvent().addListener(this);
			mLeft = left;
			mRight = right;
			mLeft.addListener(new EventListener<RepresentedStructure<T,V>>() {
				public boolean notify(RepresentedStructure<T,V> parameter) {
					V value = parameter.getRepresentative().getValue();
					if (mRight.hasPreimage(value))
						for (RepresentedStructure<T,V> match
								: mRight.getPreimage(value))
							trigger(combine(parameter, match));
					return true;
				}
				public boolean canUse(RepresentedStructure<T,V> parameter) {
					return listenersCanUse(
							combineLeft(parameter.getStructure()));
				}
				public String toString() {
					return "Left " + JoinEvent.this.toString();
				}
			});
			mRight.addListener(new EventListener<RepresentedStructure<T,V>>() {
				public boolean notify(RepresentedStructure<T,V> parameter) {
					V value = parameter.getRepresentative().getValue();
					if (mLeft.hasPreimage(value))
						for (RepresentedStructure<T,V> match
								: mLeft.getPreimage(value))
							trigger(combine(match, parameter));
					return true;
				}
				public boolean canUse(RepresentedStructure<T,V> parameter) {
					return listenersCanUse(
							combineRight(parameter.getStructure()));
				}
				public String toString() {
					return "Right " + JoinEvent.this.toString();
				}
			});
		}
		
		protected Structure<T> combine(RepresentedStructure<T,V> left,
				RepresentedStructure<T,V> right) {
			return new ProofComposeStructure<T,V>(left, right);
		}
		
		protected Structure<T> combineLeft(Structure<T> left) {
			return new HeadStructure<T>(left, mTermCount - left.getTermCount());
		}
		protected Structure<T> combineRight(Structure<T> right) {
			return new TailStructure<T>(mTermCount - right.getTermCount(),
					right);
		}

		public boolean canUse(Triple<V,V,Event<Void>> parameter) {return true;}

		public boolean notify(Triple<V,V,Event<Void>> parameter) {
			final Collection<Structure<T>> triggers = new ArrayList();
			Collection<? extends RepresentedStructure<T,V>>
					left = mLeft.getPreimage(parameter.getFirst());
			if (!left.isEmpty()) {
				Collection<? extends RepresentedStructure<T,V>>
						right = mRight.getPreimage(parameter.getSecond());
				if (!right.isEmpty())
					for (RepresentedStructure<T,V> l : left)
						for (RepresentedStructure<T,V> r : right)
							triggers.add(combine(l, r));
			}
			left = mLeft.getPreimage(parameter.getSecond());
			if (!left.isEmpty()) {
				Collection<? extends RepresentedStructure<T,V>>
						right = mRight.getPreimage(parameter.getFirst());
				if (!right.isEmpty())
					for (RepresentedStructure<T,V> l : left)
						for (RepresentedStructure<T,V> r : right)
							triggers.add(combine(l, r));
			}
			parameter.getThird().addListener(new EventListener<Void>() {
				public boolean canUse(Void parameter) {return true;}
				public boolean notify(Void parameter) {
					for (Structure<T> trigger : triggers)
						trigger(trigger);
					return false;
				}
			});
			return true;
		}

		public void generateProof(Structure<T> result, Proof proof) {
			ProofComposeStructure<T,V> structure
					= (ProofComposeStructure<T,V>)result;
			mLeft.generateProof(structure.mFirst.getStructure(), proof);
			mRight.generateProof(structure.mSecond.getStructure(), proof);
			proof.addProperty(
					areEquivalent(structure.mFirst.getRepresentative(),
							structure.mSecond.getRepresentative()));
		}
		
		public String toString() {return "Join (size " + mTermCount + ")";}
	}
	
	private static class RememberEvent
			<T extends Term<T,V>, V extends Value<T,V>>
			extends AbstractProofChainEvent<T,Structure<T>,Structure<T>> {
		private final Set<Structure<T>> mMatches = new HashSet();
		
		public RememberEvent(EGraphManager<T,V> manager,
				ProofEvent<T,? extends Structure<T>> input) {
			super(input);
			manager.getRemoveTermEvent().addListener(new EventListener<T>() {
				public boolean canUse(T parameter) {return true;}
				public boolean notify(T parameter) {
					for (Iterator<Structure<T>> matches = mMatches.iterator();
							matches.hasNext(); ) {
						Structure<T> match = matches.next();
						if (match.isRemoved())
							matches.remove();
					}
					return true;
				}
			});
		}

		public Collection<? extends Structure<T>> getMatches() {
			return mMatches;
		}
		
		public boolean notify(Structure<T> parameter) {
			if (canUse(parameter) && mMatches.add(parameter))
				trigger(parameter);
			return true;
		}
		
		public boolean canUse(Structure<T> parameter) {
			if (parameter != null && parameter.isRemoved())
				return false;
			return listenersCanUse(parameter);
		}

		protected void addConstraints(Structure<T> result, Proof proof) {}
		
		public String toString() {return "Remember";}
	}
	
	private static final class ProductJoinEvent
			<T extends Term<T,V>, V extends Value<T,V>>
			extends AbstractProofEvent<T,Structure<T>> {
		protected final RememberEvent<T,V> mLeft, mRight;
		protected final int mTermCount;
		
		public ProductJoinEvent(int termCount,
				RememberEvent<T,V> left, RememberEvent<T,V> right) {
			mTermCount = termCount;
			mLeft = left;
			mRight = right;
			mLeft.addListener(new EventListener<Structure<T>>() {
				public boolean notify(Structure<T> parameter) {
					for (Structure<T> match : mRight.getMatches())
						trigger(combine(parameter, match));
					return true;
				}
				public boolean canUse(Structure<T> parameter) {
					return listenersCanUse(combineLeft(parameter));
				}
				public String toString() {
					return "Left " + ProductJoinEvent.this.toString();
				}
			});
			mRight.addListener(new EventListener<Structure<T>>() {
				public boolean notify(Structure<T> parameter) {
					for (Structure<T> match : mLeft.getMatches())
						trigger(combine(match, parameter));
					return true;
				}
				public boolean canUse(Structure<T> parameter) {
					return listenersCanUse(combineRight(parameter));
				}
				public String toString() {
					return "Right " + ProductJoinEvent.this.toString();
				}
			});
		}
		
		protected Structure<T> combine(Structure<T> left, Structure<T> right) {
			return new ComposeStructure<T>(left, right);
		}
		
		protected Structure<T> combineLeft(Structure<T> left) {
			return new HeadStructure<T>(left, mTermCount - left.getTermCount());
		}
		protected Structure<T> combineRight(Structure<T> right) {
			return new TailStructure<T>(mTermCount - right.getTermCount(),
					right);
		}

		public void generateProof(Structure<T> result, Proof proof) {
			ComposeStructure<T> structure = (ComposeStructure<T>)result;
			mLeft.generateProof(structure.getFirst(), proof);
			mRight.generateProof(structure.getSecond(), proof);
		}
		
		public String toString() {
			return "Product Join (size " + mTermCount + ")";
		}
	}
	
	private final Tag<ProofEvent<T,? extends T>> mTermEventTag
			= new NamedTag<ProofEvent<T,? extends T>>("Term Event");
	protected final Tag<ProofEvent<T,? extends Structure<T>>> mStructureEventTag
			= new NamedTag("Structure Event");
	private final Tag<RepresentativeEvent<T,V>> mRepresentativeEventTag
			= new NamedTag<RepresentativeEvent<T,V>>("Representative Event");
	private final Tag<RememberEvent<T,V>> mRememberEventTag
			= new NamedTag<RememberEvent<T,V>>("Remember Event");
	private final
			Tag<Function<? super Structure<T>,? extends TermOrTermChild<T,V>>>
			mValueFunctionTag = new NamedTag("Value Function");
	private final Tag< Function<? super Structure<T>,? extends T>>
			mTermValueFunctionTag = new NamedTag("Term Value Function");
	
	protected final ProofEvent<T,T> mGeneralEvent = createGeneralEvent();
	protected final ProofEvent<T,T> mKnownEvent = createKnownEvent();
	protected final ProofEvent<T,T> mFalseEvent = createFalseEvent();
	
	protected final Event<T> mTermCreatedEvent = new AbstractEvent<T>();
	protected final Event<T> mRemoveTermEvent = new AbstractEvent<T>();

	private int mTime = 0;
	
	private final List<T> mUnprocessed = new WrappingArrayList<T>();
	private final MultiMap<V,T> mUses;
	private final List<EventListener<? super Void>> mPostProcesses
			= new WrappingArrayList<EventListener<? super Void>>();
	
	protected EGraphManager(ValueManager<V> manager) {
		mUses = manager.<T>createValueMultiMap();
		manager.getMergedEvent().addListener(new EventListener<V>() {
			public boolean canUse(V parameter) {return true;}
			public boolean notify(V parameter) {
				LinkList<T> selfLoops = LinkList.<T>empty();
				for (T term : parameter.getTerms())
					for (Representative<V> child : term.getChildren())
						if (child.getValue().equals(parameter)) {
							selfLoops = selfLoops.prepend(term);
							break;
						}
				for (T term : selfLoops)
					if (!allowSelfLoop(term))
						removeTerm(term);
				return true;
			}
			public String toString() {return "Remove Self-Looped Terms";}
		});
	}
	
	public abstract T getTrue();
	public abstract T getFalse();
	
	public abstract void constrainTrue(Proof proof);
	public abstract void constrainFalse(Proof proof);
	
	public abstract ValueManager<V> getValueManager();
	
	public abstract ProofManager<T,V> getProofManager();
	public final boolean hasProofManager() {return getProofManager() != null;}
	
	protected Iterable<? extends T> getUses(V value) {return mUses.get(value);}
	
	public <P> boolean watchEquality(
			Representative<V> left, Representative<V> right,
			EventListener<? super P> listener, P parameter) {
		V l = left.getValue(), r = right.getValue();
		if (l.equals(r))
			return true;
		getValueManager().getMergedEvent(l, r).addListener(
				new EventListenerClosure<P>(listener, parameter));
		return false;
	}
	public boolean canEqual(Representative<V> left,
			Representative<V> right) {
		return getValueManager().canEqual(left.getValue(), right.getValue());
	}

	protected ProofEvent<T,T> createGeneralEvent() {
		return new AbstractTermProofEvent<T,T>() {
			public void generateProof(T result, Proof proof) {}
		};
	}
	protected ProofEvent<T,T> createKnownEvent() {
		return new TermProofPatternEvent<T,T>(mGeneralEvent) {
			protected boolean matches(T pattern) {
				return !pattern.isRemoved()
						&& watchEquality(getTrue(), pattern, this, pattern);
			}
			protected boolean canMatch(T pattern) {
				return !pattern.isRemoved() && canEqual(getTrue(), pattern);
			}
			protected void addConstraints(T term, Proof proof) {
				constrainTrue(proof);
				proof.addProperty(new AreEquivalent<T,V>(term, getTrue()));
			}
			public String toString() {return "Known";}
		};
	}
	protected ProofEvent<T,T> createFalseEvent() {
		return new TermProofPatternEvent<T,T>(mGeneralEvent) {
			protected boolean matches(T pattern) {
				return !pattern.isRemoved()
						&& watchEquality(getFalse(), pattern, this, pattern);
			}
			protected boolean canMatch(T pattern) {
				return !pattern.isRemoved() && canEqual(getFalse(), pattern);
			}
			protected void addConstraints(T term, Proof proof) {
				constrainFalse(proof);
				proof.addProperty(new AreEquivalent<T,V>(term, getFalse()));
			}
			public String toString() {return "False";}
		};
	}
	
	protected ProofEvent<T,? extends T> setupGeneralEvent(GeneralNode node) {
		return mGeneralEvent;
	}
	protected ProofEvent<T,? extends T> setupKnownEvent(KnownNode node) {
		return mKnownEvent;
	}
	protected ProofEvent<T,? extends T> setupFalseEvent(FalseNode node) {
		return mFalseEvent;
	}
	
	protected ProofEvent<T,? extends T> setupCheckArityEvent(
			CheckArityNode node) {
		final int arity = node.getArity();
		return new TermProofPatternEvent<T,T>(processTermNode(node.getInput())){
			protected boolean matches(T pattern) {
				return !pattern.isRemoved()
						&& pattern.getArity() == arity;
			}
			protected boolean canMatch(T pattern) {return matches(pattern);}
			protected void addConstraints(T term, Proof proof) {
				proof.addProperty(new ArityIs<T>(term, arity));
			}
			public String toString() {return "Arity = " + arity;}
		};
	}
	
	protected ProofEvent<T,? extends T> setupCheckChildEqualityEvent(
			CheckChildEqualityNode node) {
		final int left = node.getLeft(), right = node.getRight();
		return new TermProofPatternEvent<T,T>(processTermNode(node.getInput())){
			protected boolean matches(T pattern) {
				return !pattern.isRemoved()
						&& watchEquality(pattern.getChild(left),
						pattern.getChild(right), this, pattern);
			}
			protected boolean canMatch(T pattern) {
				return !pattern.isRemoved()
						&& canEqual(pattern.getChild(left),
						pattern.getChild(right));
			}
			protected void addConstraints(T term, Proof proof) {
				proof.addProperty(
						new EquivalentChildren<T,V>(term, left, term, right));
			}
			public String toString() {
				return "Child " + left + " = Child " + right;
			}
		};
	}
	
	protected ProofEvent<T,? extends T> setupTermEvent(TermNode node) {
		if (node.isGeneral())
			return setupGeneralEvent(node.getGeneral());
		else if (node.isKnown())
			return setupKnownEvent(node.getKnown());
		else if (node.isFalse())
			return setupFalseEvent(node.getFalse());
		else if (node.isCheckArity())
			return setupCheckArityEvent(node.getCheckArity());
		else if (node.isCheckChildEquality())
			return setupCheckChildEqualityEvent(node.getCheckChildEquality());
		else
			return null;
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupTermToStructureEvent(
			TermNode node) {
		return processTermNode(node);
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupJoinEvent(
			JoinNode node) {
		return new JoinEvent<T,V>(getValueManager(), node.getTermCount(),
				processRepresentativeNode(node.getLeft()),
				processRepresentativeNode(node.getRight()));
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupProductJoinEvent(
			ProductJoinNode node) {
		return new ProductJoinEvent<T,V>(node.getTermCount(),
				processStructureNodeRemember(node.getLeft()),
				processStructureNodeRemember(node.getRight()));
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupCheckEqualityEvent(
			CheckEqualityNode node) {
		final Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
				left = processValueNode(node.getLeft()),
				right = processValueNode(node.getRight());
		return new ProofPatternEvent<T,Structure<T>>(
				processStructureNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& watchEquality(left.get(pattern).getRepresentative(),
						right.get(pattern).getRepresentative(), this, pattern);
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern == null)
					return true;
				else if (pattern.isRemoved())
					return false;
				TermOrTermChild<T,V> leftRep = left.get(pattern);
				if (leftRep == null)
					return true;
				TermOrTermChild<T,V> rightRep = right.get(pattern);
				if (rightRep == null)
					return true;
				return canEqual(leftRep.getRepresentative(),
						rightRep.getRepresentative());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(
						areEquivalent(left.get(result), right.get(result)));
			}
			public String toString() {return "Check " + left + " = " + right;}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupCheckChildIsKnownEvent(
			CheckChildIsKnownNode node) {
		final Function<? super Structure<T>,? extends T>
				term = processTermValueNode(node.getParentTerm());
		final int child = node.getChild();
		return new ProofPatternEvent<T,Structure<T>>(
				processStructureNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& watchEquality(term.get(pattern).getChild(child),
								getTrue(), this, pattern);
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern == null)
					return true;
				else if (pattern.isRemoved())
					return false;
				T parent = term.get(pattern);
				if (parent == null)
					return true;
				return canEqual(parent.getChild(child), getTrue());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				constrainTrue(proof);
				proof.addProperty(new ChildIsEquivalentTo<T,V>(
						term.get(result), child, getTrue()));
			}
			public String toString() {
				return "Check Child " + child + " of " + term + " is known";}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupCheckChildIsFalseEvent(
			CheckChildIsFalseNode node) {
		final Function<? super Structure<T>,? extends T>
				term = processTermValueNode(node.getParentTerm());
		final int child = node.getChild();
		return new ProofPatternEvent<T,Structure<T>>(
				processStructureNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& watchEquality(term.get(pattern).getChild(child),
								getFalse(), this, pattern);
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern == null)
					return true;
				else if (pattern.isRemoved())
					return false;
				T parent = term.get(pattern);
				if (parent == null)
					return true;
				return canEqual(parent.getChild(child), getFalse());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				constrainFalse(proof);
				proof.addProperty(new ChildIsEquivalentTo<T,V>(
						term.get(result), child, getFalse()));
			}
			public String toString() {
				return "Check Child " + child + " of " + term + " is false";}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupStructureEvent(
			StructureNode node) {
		if (node.isTerm())
			return setupTermToStructureEvent(node.getTerm());
		else if (node.isJoin())
			return setupJoinEvent(node.getJoin());
		else if (node.isProductJoin())
			return setupProductJoinEvent(node.getProductJoin());
		else if (node.isCheckEquality())
			return setupCheckEqualityEvent(node.getCheckEquality());
		else if (node.isCheckChildIsKnown())
			return setupCheckChildIsKnownEvent(node.getCheckChildIsKnown());
		else if (node.isCheckChildIsFalse())
			return setupCheckChildIsFalseEvent(node.getCheckChildIsFalse());
		else
			return null;
	}
	
	protected RepresentativeEvent<T,V> setupTermToRepresentativeEvent(
			TermNode node) {
		return new RepresentativeEvent<T,V>(this,
				new ProofConvertEvent<T,T,RepresentedStructure<T,V>>(
						processTermNode(node)) {
			protected RepresentedStructure<T,V> convert(T source) {
				return new RepresentedStructure<T,V>(source, source);
			}
			protected void addConstraints(Structure<T> result, Proof proof) {}
			public String toString() {return "Convert term to representative";}
		});
	}

	protected RepresentativeEvent<T,V> setupRepresentEvent(
			RepresentNode node) {
		final Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
				value = processValueNode(node.getValue());
		return new RepresentativeEvent<T,V>(this,
				new ProofConvertEvent<T,Structure<T>,RepresentedStructure<T,V>>(
						processStructureNode(node.getStructure())) {
			protected RepresentedStructure<T,V> convert(Structure<T> source) {
				return new RepresentedStructure<T,V>(source, value.get(source));
			}
			protected boolean canConvert(Structure<T> source) {
				return !source.isRemoved();
			}
			protected void addConstraints(Structure<T> result, Proof proof) {}
			public String toString() {return "Represent with " + value;}
		});
	}

	protected RepresentativeEvent<T,V> setupAnyChildEvent(
			AnyChildNode node) {
		final Function<? super Structure<T>,? extends T>
				termValue = processTermValueNode(node.getTermValue());
		return new RepresentativeEvent<T,V>(this, new AbstractProofChainEvent
				<T,Structure<T>,RepresentedStructure<T,V>>(
						processStructureNode(node.getStructure())) {
			public boolean canUse(Structure<T> parameter) {
				if (parameter.isRemoved())
					return false;
				T term = termValue.get(parameter);
				if (term == null)
					return true;
				for (int i = 0; i < term.getArity(); i++)
					if (listenersCanUse(new RepresentedStructure<T,V>(term,
							new TermChild<T,V>(term, i))))
						return true;
				return false;
			}

			public boolean notify(Structure<T> parameter) {
				if (parameter.isRemoved())
					return true;
				T term = termValue.get(parameter);
				for (int i = 0; i < term.getArity(); i++)
					trigger(new RepresentedStructure<T,V>(term,
							new TermChild<T,V>(term, i)));
				return true;
			}
			
			public void addConstraints(Structure<T> result, Proof proof) {}
			
			public String toString() {
				return "Represent " + termValue + " with any child";
			}
		});
	}
	
	protected RepresentativeEvent<T,V> setupRepresentativeEvent(
			RepresentativeNode node) {
		if (node.isTerm())
			return setupTermToRepresentativeEvent(node.getTerm());
		else if (node.isRepresent())
			return setupRepresentEvent(node.getRepresent());
		else if (node.isAnyChild())
			return setupAnyChildEvent(node.getAnyChild());
		else
			return null;
	}
	
	private RememberEvent<T,V> setupRememberEvent(StructureNode node) {
		return new RememberEvent<T,V>(this, processStructureNode(node));
	}
	
	protected Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
			setupChildValueFunction(ChildValueNode node) {
		final int child = node.getChild();
		final Function<? super Structure<T>,? extends T> input
				= processTermValueNode(node.getInput());
		return new Function<Structure<T>,TermOrTermChild<T,V>>() {
			public TermOrTermChild<T,V> get(Structure<T> parameter) {
				T term = input.get(parameter);
				return term == null ? null : new TermChild<T,V>(term, child);
			}
			public String toString() {return "Child " + child + " of " + input;}
		};
	}
	
	protected Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
			setupValueFunction(ValueNode node) {
		if (node.isTermValue())
			return processTermValueNode(node.getTermValue());
		else if (node.isChildValue())
			return setupChildValueFunction(node.getChildValue());
		else
			return null;
	}
	
	protected Function<? super Structure<T>,? extends T>
			setupComponentValueFunction(ComponentValueNode node) {
		final int component = node.getComponent();
		return new Function<Structure<T>,T>() {
			public T get(Structure<T> parameter) {
				return parameter.getTerm(component);
			}
			public String toString() {return "Component " + component;}
		};
	}
	
	protected Function<? super Structure<T>,? extends T>
			setupTermValueFunction(TermValueNode node) {
		if (node.isComponentValue())
			return setupComponentValueFunction(node.getComponentValue());
		else
			return null;
	}
	
	protected final ProofEvent<T,? extends T> processTermNode(TermNode node) {
		if (node.hasTag(mTermEventTag))
			return node.getTag(mTermEventTag);
		ProofEvent<T,? extends T> event = setupTermEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mTermEventTag, event);
		return event;
	}
	
	public final ProofEvent<T,? extends Structure<T>> processStructureNode(
			StructureNode node) {
		if (node.hasTag(mStructureEventTag))
			return node.getTag(mStructureEventTag);
		ProofEvent<T,? extends Structure<T>> event = setupStructureEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mStructureEventTag, event);
		return event;
	}
	
	protected final RepresentativeEvent<T,V> processRepresentativeNode(
			RepresentativeNode node) {
		if (node.hasTag(mRepresentativeEventTag))
			return node.getTag(mRepresentativeEventTag);
		RepresentativeEvent<T,V> event = setupRepresentativeEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mRepresentativeEventTag, event);
		return event;
	}
	
	protected final RememberEvent<T,V> processStructureNodeRemember(
			StructureNode node) {
		if (node.hasTag(mRememberEventTag))
			return node.getTag(mRememberEventTag);
		RememberEvent<T,V> event = setupRememberEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mRememberEventTag, event);
		return event;
	}

	public final Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
			processValueNode(ValueNode node) {
		if (node.hasTag(mValueFunctionTag))
			return node.getTag(mValueFunctionTag);
		Function<? super Structure<T>,? extends TermOrTermChild<T,V>> function
				= setupValueFunction(node);
		if (function == null)
			throw new UnhandledCaseException(node);
		node.setTag(mValueFunctionTag, function);
		return function;
	}
	
	public final Function<? super Structure<T>,? extends T>
			processTermValueNode(TermValueNode node) {
		if (node.hasTag(mTermValueFunctionTag))
			return node.getTag(mTermValueFunctionTag);
		Function<? super Structure<T>,? extends T> function
				= setupTermValueFunction(node);
		if (function == null)
			throw new UnhandledCaseException(node);
		node.setTag(mTermValueFunctionTag, function);
		return function;
	}
	
	public final Event<? extends T> getGeneralEvent() {return mGeneralEvent;}
	
	protected Ambassador<T,V> createAmbassador(
			FutureAmbassador<?,?,V> ambassador) {
		return new Ambassador<T,V>(getValueManager().createValue());
	}
	
	protected RepresentativeConstructor<V> getRepresentativeConstructor(
			FutureExpression<?,?,V> expression) {
		return new RepresentativeConstructor<V>(getValueManager());
	}
	
	protected TermConstructor<V> getTermConstructor(
			FutureExpression<?,?,V> expression) {
		Representative<V>[] children
				= new Representative[expression.getChildCount()];
		for (int i = 0; i < children.length; i++)
			children[i] = expression.getChild(i).getValue();
		return new TermConstructor<V>(getRepresentativeConstructor(expression),
				children);
	}
	
	protected void processNewTerm(T term) {
		for (int i = term.getArity(); i-- != 0; )
			mUses.addValue(term.getChild(i).getValue(), term);
		mUnprocessed.add(term);
		mTermCreatedEvent.trigger(term);
	}

	public Event<? extends T> getTermCreatedEvent() {return mTermCreatedEvent;}
	
	protected void processTerm(T term) {
		mGeneralEvent.trigger(term);
	}
	
	public void processEqualities() {postProcess();}
	
	protected void postProcess() {
		while (!mPostProcesses.isEmpty())
			mPostProcesses.remove(0).notify(null);
	}
	
	protected void addPostProcess(EventListener<? super Void> postProcess) {
		mPostProcesses.add(postProcess);
	}
	protected void addPostProcesses(
			Collection<? extends EventListener<? super Void>> postProcesses) {
		if (postProcesses != null)
			mPostProcesses.addAll(postProcesses);
	}
	
	public boolean process() {
		postProcess();
		if (mUnprocessed.isEmpty())
			return false;
		mTime++;
		processTerm(mUnprocessed.remove(0));
		postProcess();
		return true;
	}
	
	protected void makeEqual(final T left, final Ambassador<T,V> right) {
		if (right.hasTerm())
			throw new IllegalArgumentException();
		addPostProcesses(getValueManager().merge(
				left.getValue(), right.getValue()));
		right.setTerm(left);
	}
	
	public void makeEqual(final TermOrTermChild<T,V> left,
			final TermOrTermChild<T,V> right, final Proof proof) {
		addPostProcess(new EventListener<Void>() {
			public boolean canUse(Void parameter) {return true;}
			public boolean notify(Void parameter) {
				if (hasProofManager())
					getProofManager().addEqualityProof(
							left, right, proof, mTime);
				addPostProcesses(getValueManager().merge(
						left.getValue(), right.getValue()));
				return false;
			}
			public String toString() {
				return "Make " + left.getValue() + " equal " + right.getValue();
			}
		});
	}
	
	protected static <T extends Term<T,V>, V extends Value<T,V>>
			Property areEquivalent(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right) {
		if (left.isTerm()) {
			if (right.isTerm())
				return new AreEquivalent<T,V>(left.getTerm(), right.getTerm());
			else if (right.isTermChild())
				return new ChildIsEquivalentTo<T,V>(right.getParentTerm(),
						right.getChildIndex(), left.getTerm());
			else
				throw new UnhandledCaseException();
		} else if (left.isTermChild()) {
			if (right.isTerm())
				return new ChildIsEquivalentTo<T,V>(left.getParentTerm(),
						left.getChildIndex(), right.getTerm());
			else if (right.isTermChild())
				return new EquivalentChildren<T,V>(
						left.getParentTerm(), left.getChildIndex(),
						right.getParentTerm(), right.getChildIndex());
			else
				throw new UnhandledCaseException();
		} else
			throw new UnhandledCaseException();
	}
	
	protected Event<? extends T> getRemoveTermEvent() {return mRemoveTermEvent;}
	protected void removeTerm(final T term) {
		term.remove();
		term.getValue().removeTerm(term);
		mUnprocessed.remove(term);
		addPostProcess(new EventListener<Void>() {
			public boolean canUse(Void parameter) {return true;}
			public boolean notify(Void parameter) {
				mRemoveTermEvent.trigger(term);
				return false;
			}
			public String toString() {return "Remove term " + term;}
		});
	}
	
	protected boolean allowSelfLoop(T term) {return false;}
	
	protected String getValueInfoString(V value) {return "";}
	protected String getTermInfoString(T term) {return "";}
	
	public String toString() {
		StringBuilder string = new StringBuilder(
				"digraph {\nordering=out;\ncompound=true;\n");
		for (V value : getValueManager().getValues()) {
//			if (mUnprocessed.containsAll(value.getTerms()))
//				continue;
			string.append("subgraph cluster");
			string.append(value.hashCode());
			string.append(" {\n\tvalue");
			string.append(value.hashCode());
			string.append(" [label=\"");
			string.append(getValueInfoString(value));
			string.append("\", shape=box, rank=source");
			//if (mUnprocessed.containsAll(value.getTerms()))
			//	string.append(",peripheries=2");
			string.append("];\n");
			for (T term : value.getTerms()) {
//				if (mUnprocessed.contains(term))
//					continue;
				string.append("\tterm");
				string.append(term.hashCode());
				string.append(" [label=\"");
				string.append(getTermInfoString(term));
				string.append('"');
				//if (mUnprocessed.contains(term))
				//	string.append(",peripheries=2");
				string.append("];\n");
			}
			string.append("}\n");
		}
		for (V value : getValueManager().getValues())
			for (T term : value.getTerms())
				for (int i = 0; i < term.getArity(); i++) {
//					if (mUnprocessed.contains(term))
//						continue;
					string.append("term");
					string.append(term.hashCode());
					if (term.getChild(i).isTerm()
							&& !term.getChild(i).isRemoved()) {
						string.append(" -> term");
						string.append(term.getChild(i).hashCode());
					} else {
						string.append(" -> value");
						string.append(term.getChild(i).getValue().hashCode());
					}
					string.append(" [taillabel=\"");
					string.append(i);
					string.append('"');
					if (!term.getValue().equals(term.getChild(i).getValue())) {
						string.append(",lhead=cluster");
						string.append(term.getChild(i).getValue().hashCode());
					}
					string.append("];\n");
				}
		string.append("}");
		return string.toString();
	}
}
