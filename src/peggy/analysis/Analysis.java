package peggy.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.WildcardPeggyAxiomizer.AxiomValue;

import util.Function;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.event.AbstractChainEvent;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.Structurizer;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * Parent class of all EPEG analyses. It has several helper methods that are 
 * generally useful when constructing an analysis, as well as storing the 
 * Network and the engine itself. 
 */
public abstract class Analysis<L,P> {
	private final Network network;
	private final CPeggyAxiomEngine<L,P> engine;
	protected boolean enableProofs = true;
	
	protected Analysis(
			Network _network,
			CPeggyAxiomEngine<L,P> _engine) {
		this.network = _network;
		this.engine = _engine;
	}
	
	public void setProofsEnabled(boolean proofs) {
		this.enableProofs = proofs;
	}
	
	protected abstract void addStringListener(
			Event<String> event, String message);
	protected abstract void addProofListener(
			Event<? extends Proof> event, String message);
	
	protected Network getNetwork() {return this.network;}
	protected CPeggyAxiomEngine<L,P> getEngine() {return this.engine;}
	protected OpAmbassador<L> getAmbassador() {
		return this.engine.getEGraph().getOpAmbassador();
	}
	protected final FlowValue<P,L> getDomain(L label) {
		return FlowValue.<P,L>createDomain(label, getAmbassador());
	}
	protected final FlowValue<P,L> getParameter(P param) {
		return FlowValue.<P,L>createParameter(param);
	}

	protected class Node extends FutureNode<L,P> {
		public Node(L _label, ChildSource<L,P>... _sources) {
			super(_label, _sources);
		}
		public Node(FlowValue<P,L> _label, ChildSource<L,P>... _sources) {
			super(_label, _sources);
		}
		protected FlowValue<P,L> getDomain(L _label) {
			return Analysis.this.getDomain(_label);
		}
		public void finish(
				CPEGTerm<L,P> lhs,
				Proof proof,
				FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
			this.buildFutureVertex(futureGraph);
			getEngine().getEGraph().addExpressions(futureGraph);
			if (enableProofs) this.buildProof(proof);
			if (lhs != null)
				getEngine().getEGraph().makeEqual(lhs, this.getTerm(), (enableProofs ? proof : null));
			getEngine().getEGraph().processEqualities();
		}
	}
	
	protected Node node(L label, ChildSource<L,P>... sources) {
		return new Node(label, sources);
	}
	protected Node nodeFlow(FlowValue<P,L> flow, ChildSource<L,P>... sources) {
		return new Node(flow, sources);
	}
	protected ConcreteSource<L,P> conc(FutureNode<L,P> node) {
		return new ConcreteSource<L,P>(node);
	}
	protected ConcreteSource<L,P> concOld(CPEGTerm<L,P> node) {
		return new ConcreteSource<L,P>(node);
	}
	protected StolenSource<L,P> steal(CPEGTerm<L,P> parent, int index) {
		return new StolenSource<L,P>(parent, index);
	}
	
	protected void addConstantProperties(
			Proof proof,
			CPEGTerm<L,P> constant) {
		proof.addProperty(
				new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(
				constant, constant.getOp()));
		proof.addProperty(
				new ArityIs<CPEGTerm<L,P>>(constant, constant.getArity()));
	}
	
	
	public class StructureFunctions {
		private final Map<String,Function<? super Structure<CPEGTerm<L,P>>, ? extends CPEGTerm<L,P>>> name2term; 
		private final Map<String,Function<? super Structure<CPEGTerm<L,P>>, ? extends TermOrTermChild<CPEGTerm<L,P>,CPEGValue<L,P>>>> name2rep; 
		public StructureFunctions() {
			this.name2term = new HashMap<String,Function<? super Structure<CPEGTerm<L,P>>, ? extends CPEGTerm<L,P>>>();
			this.name2rep = new HashMap<String,Function<? super Structure<CPEGTerm<L,P>>, ? extends TermOrTermChild<CPEGTerm<L,P>,CPEGValue<L,P>>>>();
		}
		public CPEGTerm<L,P> getTerm(
				String name, Structure<CPEGTerm<L,P>> structure) {
			return name2term.get(name).get(structure);
		}
		public TermOrTermChild<CPEGTerm<L,P>,CPEGValue<L,P>> getRep(
				String name, Structure<CPEGTerm<L,P>> structure) {
			return name2rep.get(name).get(structure);
		}
		public Set<String> getTermKeys() {return name2term.keySet();}
		public Set<String> getRepKeys() {return name2rep.keySet();}
	}
	
	protected class AxiomizerHelper {
		protected final PeggyAxiomizer<L,Integer> axiomizer;
		protected final StructureFunctions structureFunctions;
		protected final Map<String,PeggyVertex<L,Integer>> name2term;
		protected final Map<String,PeggyVertex<L,Integer>> name2rep;
		protected Structurizer<PeggyVertex<L,Integer>> structurizer;
		protected int varIndex = 1;
		protected boolean closed = false;
		
		public AxiomizerHelper(PeggyAxiomizer<L,Integer> _axiomizer) {
			this.axiomizer = _axiomizer;
			this.structureFunctions = new StructureFunctions();
			this.name2term = new HashMap<String,PeggyVertex<L,Integer>>();
			this.name2rep = new HashMap<String,PeggyVertex<L,Integer>>();
		}
		
		public void mustExist(PeggyVertex<L,Integer> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustExist(node);
		}
		public void mustBeTrue(PeggyVertex<L,Integer> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeTrue(node);
		}
		public void mustBeFalse(PeggyVertex<L,Integer> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeFalse(node);
		}
		public void mustBeDistinctLoops(int left, int right) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeDistinctLoops(left, right);
		}
		public void mustBeInvariant(int depth, PeggyVertex<L,Integer> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeInvariant(depth, node);
		}
		
		public ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L,P>>> getTrigger() {
		 	if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			PEGNode<L> trigger = this.axiomizer.getTrigger();
			ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L,P>>> triggerEvent = 
				getEngine().getEGraph().processPEGNode(trigger);
			this.structurizer = axiomizer.getStructurizer();
			closed = true;

			for (String name : name2term.keySet()) {
				structureFunctions.name2term.put(
						name, 
						getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(name2term.get(name))));
			}
			for (String name : name2rep.keySet()) {
				structureFunctions.name2rep.put(
						name, 
						getEngine().getEGraph().processValueNode(structurizer.getValue(name2rep.get(name))));
			}
			
			return triggerEvent;
		}
		
		public StructureFunctions getStructureFunctions() {
			if (!closed) throw new UnsupportedOperationException("Cannot get functions until axiomizer is closed");
			return this.structureFunctions;
		}

		private PeggyVertex<L,Integer> getTerm(String name, PeggyVertex<L,Integer> term) {
			if (name != null) {
				if (name2term.containsKey(name))
					throw new IllegalArgumentException("Duplicate node name: " + name);
				name2term.put(name, term);
			}
			return term;
		}
		

		public PeggyVertex<L,Integer> get(L label, List<? extends PeggyVertex<L,Integer>> children) {return this.get(null, label, children);}
		public PeggyVertex<L,Integer> get(String name, L label, List<? extends PeggyVertex<L,Integer>> children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(label, children));
		}
		
		
		public PeggyVertex<L,Integer> get(L label, PeggyVertex<L,Integer>... children) {return this.get(null, label, children);}
		public PeggyVertex<L,Integer> get(String name, L label, PeggyVertex<L,Integer>... children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(label, children));
		}
		
		public PeggyVertex<L,Integer> getAnyArity(L label) {return this.getAnyArity(null, label);}
		public PeggyVertex<L,Integer> getAnyArity(String name, L label) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getAnyArity(label));
		}
		
		public PeggyVertex<L,Integer> getNegate(PeggyVertex<L,Integer> child) {return this.getNegate(null, child);}
		public PeggyVertex<L,Integer> getNegate(String name, PeggyVertex<L,Integer> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getNegate(child));
		}

		public PeggyVertex<L,Integer> getEquals(PeggyVertex<L,Integer> left, PeggyVertex<L,Integer> right) {return this.getEquals(null, left, right);}
		public PeggyVertex<L,Integer> getEquals(String name, PeggyVertex<L,Integer> left, PeggyVertex<L,Integer> right) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getEquals(left, right));
		}

		public PeggyVertex<L,Integer> getPhi(
				PeggyVertex<L,Integer> condition,
				PeggyVertex<L,Integer> trueCase, 
				PeggyVertex<L,Integer> falseCase) {
			return this.getPhi(null, condition, trueCase, falseCase);
		}
		public PeggyVertex<L,Integer> getPhi(
				String name, 
				PeggyVertex<L,Integer> condition,
				PeggyVertex<L,Integer> trueCase, 
				PeggyVertex<L,Integer> falseCase) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getPhi(condition, trueCase, falseCase));
		}

		public PeggyVertex<L,Integer> getZero() {return this.getZero(null);}
		public PeggyVertex<L,Integer> getZero(String name) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getZero());
		}
		
		public PeggyVertex<L,Integer> getSuccessor(PeggyVertex<L,Integer> child) {return this.getSuccessor(null, child);}
		public PeggyVertex<L,Integer> getSuccessor(String name, PeggyVertex<L,Integer> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getSuccessor(child));
		}

		public PeggyVertex<L,Integer> getTheta(
				int depth, 
				PeggyVertex<L,Integer> base, 
				PeggyVertex<L,Integer> shift) {
			return this.getTheta(null, depth, base, shift);
		}
		public PeggyVertex<L,Integer> getTheta(
				String name, 
				int depth, 
				PeggyVertex<L,Integer> base, 
				PeggyVertex<L,Integer> shift) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getTheta(depth, base, shift));
		}
		
		public PeggyVertex<L,Integer> getPass(int depth, PeggyVertex<L,Integer> child) {
			return this.getPass(null, depth, child);
		}
		public PeggyVertex<L,Integer> getPass(String name, int depth, PeggyVertex<L,Integer> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getPass(depth, child));
		}
		
		public PeggyVertex<L,Integer> getEval(
				int depth, 
				PeggyVertex<L,Integer> child,
				PeggyVertex<L,Integer> index) {
			return this.getEval(null, depth, child, index);
		}
		public PeggyVertex<L,Integer> getEval(
				String name, 
				int depth, 
				PeggyVertex<L,Integer> child,
				PeggyVertex<L,Integer> index) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getEval(depth, child, index));
		}
		
		public PeggyVertex<L,Integer> getVariable() {return this.getVariable(null, this.varIndex++);}
		public PeggyVertex<L,Integer> getVariable(int index) {return this.getVariable(null, index);}
		public PeggyVertex<L,Integer> getVariable(String name) {return this.getVariable(name, this.varIndex++);}
		public PeggyVertex<L,Integer> getVariable(String name, int index) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			PeggyVertex<L,Integer> result = this.axiomizer.getVariable(index);
			if (name != null) {
				if (name2term.containsKey(name))
					throw new IllegalArgumentException("Duplicate node name: " + name);
				name2rep.put(name, result);
			}
			return result;
		}
	}

	
	protected class WildcardAxiomizerHelper {
		protected final WildcardPeggyAxiomizer<L,Integer> axiomizer;
		protected final StructureFunctions structureFunctions;
		protected final Map<String,Vertex<AxiomValue<L,Integer>>> name2term;
		protected final Map<String,Vertex<AxiomValue<L,Integer>>> name2rep;
		protected Structurizer<Vertex<AxiomValue<L,Integer>>> structurizer;
		protected int varIndex = 1;
		protected boolean closed = false;
		
		public WildcardAxiomizerHelper(WildcardPeggyAxiomizer<L,Integer> _axiomizer) {
			this.axiomizer = _axiomizer;
			this.structureFunctions = new StructureFunctions();
			this.name2term = new HashMap<String,Vertex<AxiomValue<L,Integer>>>();
			this.name2rep = new HashMap<String,Vertex<AxiomValue<L,Integer>>>();
		}
		
		public void mustExist(Vertex<AxiomValue<L,Integer>> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustExist(node);
		}
		public void mustBeTrue(Vertex<AxiomValue<L,Integer>> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeTrue(node);
		}
		public void mustBeFalse(Vertex<AxiomValue<L,Integer>> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeFalse(node);
		}
		public void mustBeDistinctLoops(int left, int right) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeDistinctLoops(left, right);
		}
		public void mustBeInvariant(int depth, Vertex<AxiomValue<L,Integer>> node) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			this.axiomizer.mustBeInvariant(depth, node);
		}
		
		public ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L,P>>> getTrigger() {
		 	if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			PEGNode<L> trigger = this.axiomizer.getTrigger();
			ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L,P>>> triggerEvent = 
				getEngine().getEGraph().processPEGNode(trigger);
			this.structurizer = axiomizer.getStructurizer();
			closed = true;

			for (String name : name2term.keySet()) {
				structureFunctions.name2term.put(
						name, 
						getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(name2term.get(name))));
			}
			for (String name : name2rep.keySet()) {
				structureFunctions.name2rep.put(
						name, 
						getEngine().getEGraph().processValueNode(structurizer.getValue(name2rep.get(name))));
			}
			
			return triggerEvent;
		}
		
		public StructureFunctions getStructureFunctions() {
			if (!closed) throw new UnsupportedOperationException("Cannot get functions until axiomizer is closed");
			return this.structureFunctions;
		}

		private Vertex<AxiomValue<L,Integer>> getTerm(
				String name, 
				Vertex<AxiomValue<L,Integer>> term) {
			if (name != null) {
				if (name2term.containsKey(name))
					throw new IllegalArgumentException("Duplicate node name: " + name);
				name2term.put(name, term);
			}
			return term;
		}
		

		public Vertex<AxiomValue<L,Integer>> get(L label, List<? extends Vertex<AxiomValue<L,Integer>>> children) {return this.get(null, label, children);}
		public Vertex<AxiomValue<L,Integer>> get(String name, L label, List<? extends Vertex<AxiomValue<L,Integer>>> children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(label, children));
		}
		
		
		public Vertex<AxiomValue<L,Integer>> get(L label, Vertex<AxiomValue<L,Integer>>... children) {return this.get(null, label, children);}
		public Vertex<AxiomValue<L,Integer>> get(String name, L label, Vertex<AxiomValue<L,Integer>>... children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(label, children));
		}
		
		public Vertex<AxiomValue<L,Integer>> getAnyArity(L label) {return this.getAnyArity(null, label);}
		public Vertex<AxiomValue<L,Integer>> getAnyArity(String name, L label) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getAnyArity(label));
		}

		
		public Vertex<AxiomValue<L,Integer>> get(
				Integer wildcard, 
				List<? extends Vertex<AxiomValue<L,Integer>>> children) {
			return this.get(null, wildcard, children);
		}
		public Vertex<AxiomValue<L,Integer>> get(
				String name, 
				Integer wildcard, 
				List<? extends Vertex<AxiomValue<L,Integer>>> children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(wildcard, children));
		}
		
		
		public Vertex<AxiomValue<L,Integer>> get(
				Integer wildcard, 
				Vertex<AxiomValue<L,Integer>>... children) {
			return this.get(null, wildcard, children);
		}
		public Vertex<AxiomValue<L,Integer>> get(
				String name, 
				Integer wildcard, 
				Vertex<AxiomValue<L,Integer>>... children) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.get(wildcard, children));
		}
		
		public Vertex<AxiomValue<L,Integer>> getAnyArity(Integer wildcard) {
			return this.getAnyArity(null, wildcard);
		}
		public Vertex<AxiomValue<L,Integer>> getAnyArity(
				String name, 
				Integer wildcard) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getAnyArity(wildcard));
		}
		
		public Vertex<AxiomValue<L,Integer>> getNegate(Vertex<AxiomValue<L,Integer>> child) {return this.getNegate(null, child);}
		public Vertex<AxiomValue<L,Integer>> getNegate(String name, Vertex<AxiomValue<L,Integer>> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getNegate(child));
		}

		public Vertex<AxiomValue<L,Integer>> getEquals(Vertex<AxiomValue<L,Integer>> left, Vertex<AxiomValue<L,Integer>> right) {return this.getEquals(null, left, right);}
		public Vertex<AxiomValue<L,Integer>> getEquals(String name, Vertex<AxiomValue<L,Integer>> left, Vertex<AxiomValue<L,Integer>> right) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getEquals(left, right));
		}

		public Vertex<AxiomValue<L,Integer>> getPhi(
				Vertex<AxiomValue<L,Integer>> condition,
				Vertex<AxiomValue<L,Integer>> trueCase, 
				Vertex<AxiomValue<L,Integer>> falseCase) {
			return this.getPhi(null, condition, trueCase, falseCase);
		}
		public Vertex<AxiomValue<L,Integer>> getPhi(
				String name, 
				Vertex<AxiomValue<L,Integer>> condition,
				Vertex<AxiomValue<L,Integer>> trueCase, 
				Vertex<AxiomValue<L,Integer>> falseCase) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getPhi(condition, trueCase, falseCase));
		}

		public Vertex<AxiomValue<L,Integer>> getZero() {return this.getZero(null);}
		public Vertex<AxiomValue<L,Integer>> getZero(String name) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getZero());
		}
		
		public Vertex<AxiomValue<L,Integer>> getSuccessor(Vertex<AxiomValue<L,Integer>> child) {return this.getSuccessor(null, child);}
		public Vertex<AxiomValue<L,Integer>> getSuccessor(String name, Vertex<AxiomValue<L,Integer>> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getSuccessor(child));
		}

		public Vertex<AxiomValue<L,Integer>> getTheta(
				int depth, 
				Vertex<AxiomValue<L,Integer>> base, 
				Vertex<AxiomValue<L,Integer>> shift) {
			return this.getTheta(null, depth, base, shift);
		}
		public Vertex<AxiomValue<L,Integer>> getTheta(
				String name, 
				int depth, 
				Vertex<AxiomValue<L,Integer>> base, 
				Vertex<AxiomValue<L,Integer>> shift) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getTheta(depth, base, shift));
		}
		
		public Vertex<AxiomValue<L,Integer>> getPass(int depth, Vertex<AxiomValue<L,Integer>> child) {
			return this.getPass(null, depth, child);
		}
		public Vertex<AxiomValue<L,Integer>> getPass(String name, int depth, Vertex<AxiomValue<L,Integer>> child) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getPass(depth, child));
		}
		
		public Vertex<AxiomValue<L,Integer>> getEval(
				int depth, 
				Vertex<AxiomValue<L,Integer>> child,
				Vertex<AxiomValue<L,Integer>> index) {
			return this.getEval(null, depth, child, index);
		}
		public Vertex<AxiomValue<L,Integer>> getEval(
				String name, 
				int depth, 
				Vertex<AxiomValue<L,Integer>> child,
				Vertex<AxiomValue<L,Integer>> index) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			return getTerm(name, this.axiomizer.getEval(depth, child, index));
		}
		
		public Vertex<AxiomValue<L,Integer>> getVariable() {return this.getVariable(null, this.varIndex++);}
		public Vertex<AxiomValue<L,Integer>> getVariable(int index) {return this.getVariable(null, index);}
		public Vertex<AxiomValue<L,Integer>> getVariable(String name) {return this.getVariable(name, this.varIndex++);}
		public Vertex<AxiomValue<L,Integer>> getVariable(String name, int index) {
			if (closed) throw new UnsupportedOperationException("Axiomizer is closed");
			Vertex<AxiomValue<L,Integer>> result = this.axiomizer.getVariable(index);
			if (name != null) {
				if (name2term.containsKey(name))
					throw new IllegalArgumentException("Duplicate node name: " + name);
				name2rep.put(name, result);
			}
			return result;
		}
	}
	
	
	protected abstract class Bundle {
		protected abstract Structure<CPEGTerm<L,P>> getStructure();
		protected abstract StructureFunctions getFunctions();
		public abstract Proof getTriggerProof();
		public CPEGTerm<L,P> getTerm(String name) {
			return getFunctions().getTerm(name, getStructure());
		}
		public TermOrTermChild<CPEGTerm<L,P>,CPEGValue<L,P>> getRep(String name) {
			return getFunctions().getRep(name, getStructure());
		}
	}
	
	protected abstract class ShapeListener
	extends AbstractChainEvent<Structure<CPEGTerm<L,P>>,String> {
		protected abstract String getName();
		protected abstract StructureFunctions getFunctions();
		protected abstract ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L,P>>> getProofEvent();

		private abstract class ShapeBundle extends Bundle {
			protected StructureFunctions getFunctions() {return ShapeListener.this.getFunctions();}
			public Proof getTriggerProof() {
				Proof result = new Proof(getName());
				getProofEvent().generateProof(getStructure(), result);
				return result;
			}
		}
		public boolean notify(final Structure<CPEGTerm<L,P>> structure) {
			if (!this.canUse(structure))
				return true;
			FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph = 
				new FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>>();
			String result = build(
					new ShapeBundle() {
						protected Structure<CPEGTerm<L,P>> getStructure() {return structure;}
					}, 
					futureGraph);
			trigger(result);
			return true;
		}
		protected abstract String build(
				Bundle bundle,
				FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph);
		protected abstract boolean matches(Bundle bundle);
		public boolean canUse(final Structure<CPEGTerm<L,P>> structure) {
			StructureFunctions functions = getFunctions();
			for (String termName : functions.getTermKeys()) {
				if (functions.getTerm(termName, structure) == null)
					return true;
			}
			for (String repName : functions.getRepKeys()) {
				if (functions.getRep(repName, structure) == null) 
					return true;
			}
			return matches(new ShapeBundle() {
				protected Structure<CPEGTerm<L,P>> getStructure() {return structure;}
			});
		}
	}
}
