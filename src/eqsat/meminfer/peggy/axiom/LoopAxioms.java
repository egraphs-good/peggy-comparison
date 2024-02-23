package eqsat.meminfer.peggy.axiom;

import static eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp.Eval;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp.Shift;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.ChildIsInvariant;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpIsSameLoop;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public final class LoopAxioms<O, P> extends PeggyAxioms<O,P> {
	public LoopAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addAll() {
		//addThetaEval0();
		addEval0Theta();
		addEvalInvariant();
		addEvalSuccShift();
		addShiftTheta();
		addShiftInvariant();
		addJoinTheta();
		//addTightTheta();
		//addPassTrue();
		//addPassThetaTrue();
		//addPassThetaFalse();
		//addPeelPass();
		//addShiftEval();
		//addInduction();
		//addJoinThetaBase(true);
		//addJoinThetas(true);
		//addThetaEquals();

		addDistributeShift();
		addDistributeEval();
		//addUndistributeEval();
		addDistributeThroughEval();
		addDistributeThroughTheta();
		//addUndistributeThroughTheta();
	}
	
	public Event<? extends Proof> addEval0Theta() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant(b) => eval(theta(b, u),0) = b");
		PeggyVertex<O,Integer> b = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> u = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> theta = axiomizer.getTheta(1, b, u);
		PeggyVertex<O,Integer> eval
				= axiomizer.getEval(1, theta, axiomizer.getZero());
		axiomizer.mustBeInvariant(1, b);
		axiomizer.mustExist(eval);
		axiomizer.makeEqual(eval, b);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addEvalInvariant() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant(x) => eval(x,i) = x");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> eval = axiomizer.getEval(1, x, i);
		axiomizer.mustBeInvariant(1, x);
		axiomizer.mustExist(eval);
		axiomizer.makeEqual(eval, x);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addEvalSuccShift() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"eval(x, succ(i)) = eval(shift(x), i)");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> eval
				= axiomizer.getEval(1, x, axiomizer.getSuccessor(i));
		axiomizer.mustExist(eval);
		axiomizer.makeEqual(eval,
				axiomizer.getEval(1, axiomizer.getShift(1, x), i));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addShiftTheta() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"shift(theta(b,u)) = u");
		PeggyVertex<O,Integer> b = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> u = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> shift
				= axiomizer.getShift(1, axiomizer.getTheta(1, b, u));
		axiomizer.mustExist(shift);
		axiomizer.makeEqual(shift, u);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addShiftInvariant() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant(x) => shift(x) = x");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> shift = axiomizer.getShift(1, x);
		axiomizer.mustBeInvariant(1, x);
		axiomizer.mustExist(shift);
		axiomizer.makeEqual(shift, x);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addJoinTheta() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant(x) => theta(x,x) = x");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> theta = axiomizer.getTheta(1, x, x);
		axiomizer.mustBeInvariant(1, x);
		axiomizer.mustExist(theta);
		axiomizer.makeEqual(theta, x);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	/*public void addTightTheta() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer();
		PeggyVertex<O,Integer> b = axiomizer.getParameter(0);
		PeggyVertex<O,Integer> theta = axiomizer.createPlaceHolder();
		theta.replaceWith(axiomizer.getTheta(1, b, theta));
		axiomizer.mustBeInvariant(1, b);
		axiomizer.mustExist(theta);
		axiomizer.makeEqual(theta, b);
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}*/
	
	public Event<? extends Structure<CPEGTerm<O,P>>> addDistributeShift() {
		return addDistributeShift(getPEGNetwork(), getEngine().getEGraph());
	}
	protected static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		Event<? extends Structure<T>> addDistributeShift(
			PEGNetwork<O> network, final EPEGManager<O,P,T,V> manager) {
		final ProofEvent<T,? extends Structure<T>> trigger
				= manager.processPEGNode(
				network.opIsLoopLifted(network.componentValue(1),
				network.opLoop(network.componentValue(0)),
				network.opIsLoopOp(network.componentValue(0),
						PEGLoopOp.Shift,
						network.adaptExpression(network.adaptStructure(
							network.join(network.represent(network.childValue(0,
										network.componentValue(0)),
									network.checkArity(1, network.general())),
								network.adaptTerm(network.general())))))));
		trigger.addListener(new EventListener<Structure<T>>() {
			public boolean canUse(Structure<T> parameter) {
				return parameter.getTerm(1) == null
						|| (parameter.getTerm(1).getArity() > 0
						&& (parameter.getTerm(0) == null
						|| !parameter.getTerm(1).getValue().isInvariant(
								parameter.getTerm(0).getOp().getLoopDepth())));
			}
			public boolean notify(Structure<T> parameter) {
				if (!canUse(parameter))
					return true;
				Proof proof;
				if (manager.hasProofManager())
					proof = new Proof("Distribute Shift");
				else
					proof = null;
				if (proof!=null) 
					trigger.generateProof(parameter, proof);
				T shift = parameter.getTerm(0);
				FlowValue<P,O> shiftOp = shift.getOp();
				int loop = shiftOp.getLoopDepth();
				T target = parameter.getTerm(1);
				FutureExpressionGraph<FlowValue<P,O>,T,V> graph
						= new FutureExpressionGraph<FlowValue<P,O>,T,V>();
				Vertex<FlowValue<P,O>,T,V>[] children
						= new Vertex[target.getArity()];
				for (int i = 0; i < children.length; i++)
					if (target.getChild(i).getValue().isInvariant(loop))
						children[i] = graph.getVertex(target.getChild(i));
					else
						children[i] = graph.getExpression(shiftOp,
								graph.getVertex(target.getChild(i)));
				FutureExpression<FlowValue<P,O>,T,V> expression
						= graph.getExpression(target.getOp(), children);
				manager.addExpressions(graph);
				T term = expression.getTerm();
				if (proof != null) {
					proof.addProperties(new ArityIs<T>(target, children.length),
							new ArityIs<T>(term, children.length),
							new OpsEqual<FlowValue<P,O>,T>(target, term));
					for (int i = 0; i < children.length; i++)
						if (target.getChild(i).getValue().isInvariant(loop))
							proof.addProperties(new EquivalentChildren<T,V>(
									target, i, term, i),
									new ChildIsInvariant<T,V>(
											target, i, shift));
						else {
							T child = expression.getChild(i)
									.getFutureExpression().getTerm();
							proof.addProperties(
									new ChildIsEquivalentTo<T,V>(
											term, i, child),
									new ArityIs<T>(child, 1),
									new OpsEqual<FlowValue<P,O>,T>(
											shift, child),
									new EquivalentChildren<T,V>(
											target, i, child, 0));
						}
				}
				manager.makeEqual(shift, term, proof);
				return true;
			}
			public String toString() {return "Distribute Shift";}
		});
		return trigger;
	}

	public Event<? extends Structure<CPEGTerm<O,P>>> addDistributeEval() {
		return addDistributeEval(getPEGNetwork(), getEngine().getEGraph());
	}
	protected static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		Event<? extends Structure<T>> addDistributeEval(
			PEGNetwork<O> network, final EPEGManager<O,P,T,V> manager) {
		final ProofEvent<T,? extends Structure<T>> trigger
				= manager.processPEGNode(
				network.opIsLoopLifted(network.componentValue(1),
				network.opLoop(network.componentValue(0)),
				network.opIsLoopOp(network.componentValue(0),
						PEGLoopOp.Eval,
						network.adaptExpression(network.adaptStructure(
							network.join(network.represent(network.childValue(0,
										network.componentValue(0)),
									network.checkArity(2, network.general())),
								network.adaptTerm(network.general())))))));
		trigger.addListener(new EventListener<Structure<T>>() {
			public boolean canUse(Structure<T> parameter) {
				return parameter.getTerm(1) == null
						|| (parameter.getTerm(1).getArity() > 0
						&& (parameter.getTerm(0) == null
						|| !parameter.getTerm(1).getValue().isInvariant(
								parameter.getTerm(0).getOp().getLoopDepth())));
			}
			
			public boolean notify(Structure<T> parameter) {
				if (!canUse(parameter))
					return true;
				Proof proof;
				T eval = parameter.getTerm(0);
				T target = parameter.getTerm(1);
				if (!target.getOp().isLoopLiftedAll()) {
					if (!manager.watchInvariance(target.getOp().getLoopDepth(),
							eval.getChild(1), this, parameter))
						return true;
					else if (manager.hasProofManager())
						proof = new Proof("Distribute Eval",
								new ChildIsInvariant<T,V>(eval, 1, target));
					else
						proof = null;
				} else if (manager.hasProofManager())
					proof = new Proof("Distribute Eval");
				else
					proof = null;
				if (proof != null)
					trigger.generateProof(parameter, proof);
				FlowValue<P,O> evalOp = eval.getOp();
				int loop = evalOp.getLoopDepth();
				FutureExpressionGraph<FlowValue<P,O>,T,V> graph
						= new FutureExpressionGraph<FlowValue<P,O>,T,V>();
				Vertex<FlowValue<P,O>,T,V> index
						= graph.getVertex(eval.getChild(1));
				Vertex<FlowValue<P,O>,T,V>[] children
						= new Vertex[target.getArity()];
				for (int i = 0; i < children.length; i++)
					if (target.getChild(i).getValue().isInvariant(loop))
						children[i] = graph.getVertex(target.getChild(i));
					else
						children[i] = graph.getExpression(evalOp,
								graph.getVertex(target.getChild(i)), index);
				FutureExpression<FlowValue<P,O>,T,V> expression
						= graph.getExpression(target.getOp(), children);
				manager.addExpressions(graph);
				T term = expression.getTerm();
				if (proof != null) {
					proof.addProperties(new ArityIs<T>(target, children.length),
							new ArityIs<T>(term, children.length),
							new OpsEqual<FlowValue<P,O>,T>(target, term));
					for (int i = 0; i < children.length; i++)
						if (target.getChild(i).getValue().isInvariant(loop))
							proof.addProperties(new EquivalentChildren<T,V>(
									target, i, term, i),
									new ChildIsInvariant<T,V>(target, i, eval));
						else {
							T child = children[i].getFutureExpression()
									.getTerm();
							proof.addProperties(
									new ChildIsEquivalentTo<T,V>(
											term, i, child),
									new ArityIs<T>(child, 2),
									new OpsEqual<FlowValue<P,O>,T>(eval, child),
									new EquivalentChildren<T,V>(
											target, i, child, 0),
									new EquivalentChildren<T,V>(
											eval, 1, child, 1));
						}
				}
				manager.makeEqual(term, eval, proof);
				return true;
			}
			public String toString() {return "Distribute Eval";}
		});
		return trigger;
	}
	
	public Event<? extends Structure<CPEGTerm<O,P>>> addDistributeThroughEval() {
		return addDistributeThroughEval(getPEGNetwork(), getEngine().getEGraph());
	}
	protected static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
			Event<? extends Structure<T>> addDistributeThroughEval(
			PEGNetwork<O> network, final EPEGManager<O,P,T,V> manager) {
		final ProofEvent<T,? extends Structure<T>> trigger
				= manager.processPEGNode(network.isInvariant(
					network.childValue(1, network.componentValue(1)),
				network.opLoop(network.componentValue(1)),
				network.opIsLoopOp(network.componentValue(1),
						PEGLoopOp.Eval,
						network.opIsAllLoopLifted(
								network.componentValue(0),
								network.adaptExpression(network.adaptStructure(
									network.join(network.anyChild(
												network.componentValue(0),
												network.general()),
										network.adaptTerm(network.checkArity(2,
												network.general())))))))));
		trigger.addListener(new EventListener<Structure<T>>() {
			public boolean canUse(Structure<T> parameter) {
				//TODO add canBeInvariant checks
				return parameter.getTerm(1) == null
						|| !parameter.getTerm(1).getChild(0).getValue()
								.isInvariant(
								parameter.getTerm(1).getOp().getLoopDepth());
			}
			
			public boolean notify(Structure<T> parameter) {
				if (!canUse(parameter))
					return true;
				T source = parameter.getTerm(0);
				T eval = parameter.getTerm(1);
				int loop = eval.getOp().getLoopDepth();
				for (Representative<V> child : source.getChildren())
					if (!manager.watchInvariance(loop, child, this, parameter))
						return true;
				for (int i = 0; i < source.getArity(); i++)
					if (source.getChild(i).getValue().equals(eval.getValue())) {
						Proof proof;
						if (manager.hasProofManager()) {
							proof = new Proof("Distribute Through Eval " + i);
							trigger.generateProof(parameter, proof);
							proof.addProperty(new ChildIsEquivalentTo<T,V>(
									source, i, eval));
							for (int j = 0; j < source.getArity(); j++)
								if (j != i)
									proof.addProperty(new ChildIsInvariant<T,V>(
											source, j, eval));
						} else
							proof = null;
						distribute(source, i, eval, proof);
					}
				return true;
			}
			
			private void distribute(T source, int child, T eval, Proof proof) {
				FlowValue<P,O> evalOp = eval.getOp();
				FutureExpressionGraph<FlowValue<P,O>,T,V> graph
						= new FutureExpressionGraph<FlowValue<P,O>,T,V>();
				Vertex<FlowValue<P,O>,T,V>[] children
						= new Vertex[source.getArity()];
				for (int i = 0; i < children.length; i++)
					if (i == child)
						children[i] = graph.getVertex(eval.getChild(0));
					else 
						children[i] = graph.getVertex(source.getChild(i));
				FutureExpression<FlowValue<P,O>,T,V> targetExpression
						= graph.getExpression(source.getOp(), children);
				FutureExpression<FlowValue<P,O>,T,V> expression
						= graph.getExpression(evalOp, targetExpression,
						graph.getVertex(eval.getChild(1)));
				manager.addExpressions(graph);
				T term = expression.getTerm();
				T target = targetExpression.getTerm();
				if (proof != null) {
					proof.addProperties(new ArityIs<T>(term, 2),
							new OpsEqual<FlowValue<P,O>,T>(eval, term),
							new ChildIsEquivalentTo<T,V>(term, 0, target),
							new EquivalentChildren<T,V>(eval, 1, term, 1),
							new ArityIs<T>(source, children.length),
							new ArityIs<T>(target, children.length),
							new OpsEqual<FlowValue<P,O>,T>(source, target));
					for (int i = 0; i < children.length; i++)
						if (i == child)
							proof.addProperty(new EquivalentChildren<T,V>(
									eval, 0, target, i));
						else 
							proof.addProperty(new EquivalentChildren<T,V>(
									source, i, target, i));
				}
				manager.makeEqual(source, term, proof);
			}
			public String toString() {return "Distribute Through Eval";}
		});
		return trigger;
	}
	
	public Event<? extends Structure<CPEGTerm<O,P>>> addDistributeThroughTheta() {
		return addDistributeThroughTheta(getPEGNetwork(), getEngine().getEGraph());
	}
	protected static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		Event<? extends Structure<T>> addDistributeThroughTheta(
			PEGNetwork<O> network, final EPEGManager<O,P,T,V> manager) {
		final ProofEvent<T,? extends Structure<T>> trigger
				= manager.processPEGNode(
				network.opIsLoopOp(network.componentValue(1), PEGLoopOp.Theta,
				network.opIsAllLoopLifted(
						network.componentValue(0),
						network.adaptExpression(network.adaptStructure(
								network.join(network.anyChild(
												network.componentValue(0),
												network.general()),
										network.adaptTerm(network.checkArity(2,
												network.general()))))))));
		trigger.addListener(new EventListener<Structure<T>>() {
			public boolean canUse(Structure<T> parameter) {
				return parameter.getTerm(1) == null
						|| !parameter.getTerm(1).getValue().isInvariant(
								parameter.getTerm(1).getOp().getLoopDepth());
			}
			
			public boolean notify(Structure<T> parameter) {
				if (!canUse(parameter))
					return true;
				T source = parameter.getTerm(0);
				T theta = parameter.getTerm(1);
				for (int i = 0; i < source.getArity(); i++)
					if (source.getChild(i).getValue().equals(theta.getValue())){
						Proof proof;
						if (manager.hasProofManager()) {
							proof = new Proof("Distribute Through Theta " +i);
							trigger.generateProof(parameter, proof);
						} else
							proof = null;
						distribute(source, i, theta, proof);
					}
				return true;
			}
			
			private void distribute(T source, int child, T theta, Proof proof) {
				FlowValue<P,O> thetaOp = theta.getOp();
				int loop = thetaOp.getLoopDepth();
				FlowValue<P,O> shiftOp = FlowValue.<P,O>createShift(loop);
				FlowValue<P,O> evalOp = FlowValue.<P,O>createEval(loop);
				FlowValue<P,O> zeroOp = FlowValue.<P,O>createZero();
				FutureExpressionGraph<FlowValue<P,O>,T,V> graph
						= new FutureExpressionGraph<FlowValue<P,O>,T,V>();
				Vertex<FlowValue<P,O>,T,V>[] baseChildren
						= new Vertex[source.getArity()];
				Vertex<FlowValue<P,O>,T,V>[] nextChildren
						= new Vertex[baseChildren.length];
				for (int i = 0; i < baseChildren.length; i++)
					if (i == child) {
						baseChildren[i] = graph.getVertex(theta.getChild(0));
						nextChildren[i] = graph.getVertex(theta.getChild(1));
					} else if (source.getChild(i).getValue().isInvariant(loop))
						baseChildren[i] = nextChildren[i]
								= graph.getVertex(source.getChild(i));
					else {
						baseChildren[i] = graph.getExpression(evalOp,
								graph.getVertex(source.getChild(i)),
								graph.getExpression(zeroOp));
						nextChildren[i] = graph.getExpression(shiftOp,
								graph.getVertex(source.getChild(i)));
					}
				FutureExpression<FlowValue<P,O>,T,V> baseExpression
						= graph.getExpression(source.getOp(), baseChildren);
				FutureExpression<FlowValue<P,O>,T,V> nextExpression
						= graph.getExpression(source.getOp(), nextChildren);
				FutureExpression<FlowValue<P,O>,T,V> expression
						= graph.getExpression(thetaOp,
								baseExpression, nextExpression);
				manager.addExpressions(graph);
				T term = expression.getTerm();
				T base = baseExpression.getTerm();
				T next = nextExpression.getTerm();
				if (proof != null) {
					proof.addProperties(new ArityIs<T>(term, 2),
							new OpsEqual<FlowValue<P,O>,T>(theta, term),
							new ChildIsEquivalentTo<T,V>(term, 0, base),
							new ChildIsEquivalentTo<T,V>(term, 1, next),
							new ArityIs<T>(source, baseChildren.length),
							new ArityIs<T>(base, baseChildren.length),
							new OpsEqual<FlowValue<P,O>,T>(source, base),
							new ArityIs<T>(next, nextChildren.length),
							new OpsEqual<FlowValue<P,O>,T>(source, next));
					for (int i = 0; i < baseChildren.length; i++)
						if (i == child)
							proof.addProperties(new EquivalentChildren<T,V>(
											theta, 0, base, i),
									new EquivalentChildren<T,V>(
											theta, 1, next, i));
						else if (source.getChild(i).getValue()
								.isInvariant(loop))
							proof.addProperties(
									new ChildIsInvariant<T,V>(source, i, theta),
									new EquivalentChildren<T,V>(
											source, i, base, i),
									new EquivalentChildren<T,V>(
											source, i, next, i));
						else {
							T zero = graph.getExpression(zeroOp).getTerm();
							T baseChild = baseChildren[i].getFutureExpression()
									.getTerm();
							T nextChild = nextChildren[i].getFutureExpression()
									.getTerm();
							proof.addProperties(
									new ChildIsEquivalentTo<T,V>(
											base, i, baseChild),
									new ChildIsEquivalentTo<T,V>(
											next, i, nextChild),
									new OpIsLoopOp<T>(baseChild, Eval),
									new OpIsLoopOp<T>(nextChild, Shift),
									new OpIsSameLoop<T>(theta, baseChild),
									new OpIsSameLoop<T>(theta, nextChild),
									new ArityIs<T>(baseChild, 2),
									new EquivalentChildren<T,V>(
											source, i, baseChild, 0),
									new ChildIsEquivalentTo<T,V>(
											baseChild, 1, zero),
									new ArityIs<T>(zero, 0),
									new OpIs<FlowValue<P,O>,T>(zero, zeroOp),
									new ArityIs<T>(nextChild, 1),
									new EquivalentChildren<T,V>(
											source, i, nextChild, 0));
						}
				}
				manager.makeEqual(source, term, proof);
			}
			public String toString() {return "Distribute Through Theta";}
		});
		return trigger;
	}
}
