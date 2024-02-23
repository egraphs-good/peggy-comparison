package peggy.analysis.llvm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.instructions.Cast;
import llvm.types.Type;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import util.HashMultiMap;
import util.MultiMap;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This class acts as a prepass on the engine, to identify the stackPointers,
 * and to identify pairs of pointers that do not alias.
 * This information will be static, so that the engine does not need to compute
 * it dynamically.
 */
public class LLVMAliasAnalysis {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMAliasAnalysis: " + message);
	}
	
	private final Map<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>> vertex2term = 
		new HashMap<Vertex<FlowValue<LLVMParameter,LLVMLabel>>, CPEGTerm<LLVMLabel,LLVMParameter>>();
	private final Set<CPEGValue<LLVMLabel,LLVMParameter>> stackPointers; 
	private final Set<CPEGValue<LLVMLabel,LLVMParameter>> nonStackPointers; 
	private final MultiMap<CPEGValue<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> doesNotAlias;
	private final ValueManager<CPEGValue<LLVMLabel,LLVMParameter>> manager;
	private final boolean paramsDNANull;
	
	public LLVMAliasAnalysis(
			ValueManager<CPEGValue<LLVMLabel,LLVMParameter>> _manager,
			boolean _paramsDNANull) {
		this.paramsDNANull = _paramsDNANull;
		this.manager = _manager;
		this.stackPointers = this.manager.createValueSet();
		this.nonStackPointers = this.manager.createValueSet();
		this.doesNotAlias = this.manager.createValueMultiValueMap();
	}
	
	public boolean doesNotAlias(
			CPEGValue<LLVMLabel,LLVMParameter> left,
			CPEGValue<LLVMLabel,LLVMParameter> right) {
		return doesNotAlias.containsEntry(left, right) ||
			doesNotAlias.containsEntry(right, left);
	}
	public boolean isStackPointer(CPEGValue<LLVMLabel,LLVMParameter> left) {
		return stackPointers.contains(left);
	}
	public boolean isNonStackPointer(CPEGValue<LLVMLabel,LLVMParameter> left) {
		return nonStackPointers.contains(left);
	}
	
	public void addAll(
			List<? extends CPEGTerm<LLVMLabel,LLVMParameter>> _terms,
			List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> _vertices) {
		if (_vertices.size() != _terms.size())
			throw new IllegalArgumentException("Mismatched terms and vertices");
		for (int i = 0; i < _vertices.size(); i++) {
			this.vertex2term.put(_vertices.get(i), _terms.get(i));
		}
		
		debug("About to do analyses");
		
		this.addStackPointerAnnotations();
		this.addDoesNotAliasAnnotations();
	}
	
	public void addStackPointerAnnotations() {
		// find initial stackPointers
		for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : vertex2term.keySet()) {
			if (isSimple(vertex, LLVMOperator.RHO_VALUE)) {
				Vertex<FlowValue<LLVMParameter,LLVMLabel>> child = 
					vertex.getChild(0);
				if (isSimple(child, LLVMOperator.ALLOCA)) {
					// rho_value(alloca(*)) is a stackPointer
					debug("alloca is SP");
					stackPointers.add(vertex2term.get(vertex).getValue());
				} 
				else if (isSimple(child, LLVMOperator.MALLOC)) {
					// rho_value(malloc(*)) is not a stackPointer
					debug("malloc is NSP");
					nonStackPointers.add(vertex2term.get(vertex).getValue());
				}
			}
			else if (vertex.getLabel().isParameter() &&
					 vertex.getLabel().getParameter().isArgument()) {
				Type argtype = vertex.getLabel().getParameter().getArgumentSelf().getType();
				if (argtype.isComposite() && 
					argtype.getCompositeSelf().isPointer()) {
					// pointer parameter is not a stackPointer
					debug("param is NSP");
					nonStackPointers.add(vertex2term.get(vertex).getValue());
				}
			}
			else if (vertex.getLabel().isDomain() &&
					 vertex.getLabel().getDomain().isGlobal()) {
				// global is not a stackPointer
				debug("global is NSP");
				nonStackPointers.add(vertex2term.get(vertex).getValue());
			}
			else if (vertex.getLabel().isDomain() &&
					 vertex.getLabel().getDomain().isConstantValue() &&
					 vertex.getLabel().getDomain().getConstantValueSelf().getValue().isConstantNullPointer()) {
				// NULL is not a stackPointer
				debug("null is NSP");
				nonStackPointers.add(vertex2term.get(vertex).getValue());
			}
		}
		
		// propagate stackPointer-ness
		int NSPSize = nonStackPointers.size();
		int SPSize = stackPointers.size();
		int oldSPSize, oldNSPSize;
		while (true) {
			oldSPSize = SPSize; 
			oldNSPSize = NSPSize;
			
			for (Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex : vertex2term.keySet()) {
				if (vertex.getLabel().isDomain() &&
					vertex.getLabel().getDomain().isCast() &&
					vertex.getLabel().getDomain().getCastSelf().getOperator().equals(Cast.Bitcast)) {
					final Vertex<FlowValue<LLVMParameter,LLVMLabel>> typeVertex = 
						vertex.getChild(0);
					if (!(typeVertex.getLabel().isDomain() &&
						  typeVertex.getLabel().getDomain().isType()))
						throw new IllegalArgumentException("First child of bitcast must be type node");
					final Type type = 
						typeVertex.getLabel().getDomain().getTypeSelf().getType();
					if (type.isComposite() &&
						type.getCompositeSelf().isPointer()) {
						// bitcast of pointer to pointer preserves stackPointer-ness
						final CPEGValue<LLVMLabel,LLVMParameter> pointerValue = 
							vertex2term.get(vertex.getChild(1)).getValue();
						if (stackPointers.contains(pointerValue)) {
							debug("bitcast preserves SP");
							stackPointers.add(vertex2term.get(vertex).getValue());
						}
						else if (nonStackPointers.contains(pointerValue)) {
							debug("bitcast preserves NSP");
							nonStackPointers.add(vertex2term.get(vertex).getValue());
						}
					}
				}
				else if (isSimple(vertex, LLVMOperator.GETELEMENTPTR)) {
					final CPEGValue<LLVMLabel,LLVMParameter> baseValue = 
						vertex2term.get(vertex.getChild(0)).getValue();
					// GEP preserves stackPointer-ness
					if (stackPointers.contains(baseValue)) {
						debug("GEP preserves SP");
						stackPointers.add(vertex2term.get(vertex).getValue());
					} 
					else if (nonStackPointers.contains(baseValue)) {
						debug("GEP preserves NSP");
						nonStackPointers.add(vertex2term.get(vertex).getValue());
					}
				}
				else if (vertex.getLabel().isPhi()) {
					final CPEGValue<LLVMLabel,LLVMParameter> leftValue = 
						vertex2term.get(vertex.getChild(1)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> rightValue = 
						vertex2term.get(vertex.getChild(2)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> topValue = 
						vertex2term.get(vertex).getValue();
					
					// phi of two stackPointers is a stackPointer,
					// phi of two nonStackPointers is a nonStackPointer,
					if (stackPointers.contains(leftValue) && stackPointers.contains(rightValue)) {
						debug("SP factor phi");
						stackPointers.add(topValue);
					}
					else if (nonStackPointers.contains(leftValue) && nonStackPointers.contains(rightValue)) {
						debug("NSP factor phi");
						nonStackPointers.add(topValue);
					}
					
					
					// phi is [N]SP => children are [N]SP
					if (stackPointers.contains(topValue)) {
						debug("SP through phi");
						stackPointers.add(leftValue);
						stackPointers.add(rightValue);
					} else if (nonStackPointers.contains(topValue)) {
						debug("NSP through phi");
						nonStackPointers.add(leftValue);
						nonStackPointers.add(rightValue);
					}
				}
				else if (vertex.getLabel().isTheta()) {
					final CPEGValue<LLVMLabel,LLVMParameter> leftValue = 
						vertex2term.get(vertex.getChild(0)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> rightValue = 
						vertex2term.get(vertex.getChild(1)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> topValue = 
						vertex2term.get(vertex).getValue();
					
					// theta of two stackPointers is a stackPointer,
					// theta of two nonStackPointers is a nonStackPointer,
					if (stackPointers.contains(leftValue) && stackPointers.contains(rightValue)) {
						debug("SP factor theta");
						stackPointers.add(topValue);
					}
					else if (nonStackPointers.contains(leftValue) && nonStackPointers.contains(rightValue)) {
						debug("NSP factor theta");
						nonStackPointers.add(topValue);
					}
					
					
					// theta is [N]SP => children are [N]SP
					if (stackPointers.contains(topValue)) {
						debug("SP through theta");
						stackPointers.add(leftValue);
						stackPointers.add(rightValue);
					} else if (nonStackPointers.contains(topValue)) {
						debug("NSP through theta");
						nonStackPointers.add(leftValue);
						nonStackPointers.add(rightValue);
					}
				}
				else if (vertex.getLabel().isEval()) {
					final CPEGValue<LLVMLabel,LLVMParameter> childValue = 
						vertex2term.get(vertex.getChild(0)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> topValue = 
						vertex2term.get(vertex).getValue();
					// eval preserves stackPointer-ness
					if (stackPointers.contains(childValue)) {
						debug("SP factor eval");
						stackPointers.add(topValue);
					}
					else if (nonStackPointers.contains(childValue)) {
						debug("NSP factor eval");
						nonStackPointers.add(topValue);
					}
					
					
					// eval is [N]SP => child is [N]SP
					if (stackPointers.contains(topValue)) {
						debug("SP through eval");
						stackPointers.add(childValue);
					} else if (nonStackPointers.contains(topValue)) {
						debug("NSP through eval");
						nonStackPointers.add(childValue);
					}
				}
				else if (vertex.getLabel().isShift()) {
					final CPEGValue<LLVMLabel,LLVMParameter> childValue = 
						vertex2term.get(vertex.getChild(0)).getValue();
					final CPEGValue<LLVMLabel,LLVMParameter> topValue = 
						vertex2term.get(vertex).getValue();
					// shift preserves stackPointer-ness
					if (stackPointers.contains(childValue)) {
						debug("SP factor shift");
						stackPointers.add(topValue);
					}
					else if (nonStackPointers.contains(childValue)) {
						debug("NSP factor shift");
						nonStackPointers.add(topValue);
					}
					
					
					// shift is [N]SP => child is [N]SP
					if (stackPointers.contains(topValue)) {
						debug("SP through shift");
						stackPointers.add(childValue);
					} else if (nonStackPointers.contains(topValue)) {
						debug("NSP through shift");
						nonStackPointers.add(childValue);
					}
				}
			}
			
			SPSize = stackPointers.size();
			NSPSize = nonStackPointers.size();
			if (oldSPSize == SPSize && oldNSPSize == NSPSize)
				break;
		}
	}
	
	private static boolean isSimple(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex,
			LLVMOperator op) {
		return vertex.getLabel().isDomain() &&
			vertex.getLabel().getDomain().isSimple() &&
			vertex.getLabel().getDomain().getSimpleSelf().getOperator().equals(op);
	}
	
	private void addDNA(
			CPEGValue<LLVMLabel,LLVMParameter> left,
			CPEGValue<LLVMLabel,LLVMParameter> right) {
		doesNotAlias.addValue(left, right);
		doesNotAlias.addValue(right, left);
	}
	
	public void addDoesNotAliasAnnotations() {
		// stackPointer and nonStackPointer do not alias
		for (CPEGValue<LLVMLabel,LLVMParameter> stack : stackPointers) {
			for (CPEGValue<LLVMLabel,LLVMParameter> nonstack : nonStackPointers) {
				debug("SP and NSP DNA");
				addDNA(stack, nonstack);
			}
		}

		final List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> vlist = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>(vertex2term.keySet());
		
		// load of alloca holding pointer does not alias alloca
		for (int i = 0; i < vlist.size(); i++) {
			final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);
			if (isSimple(vi, LLVMOperator.RHO_VALUE) &&
				isSimple(vi.getChild(0), LLVMOperator.LOAD) &&
				isSimple(vi.getChild(0).getChild(1), LLVMOperator.RHO_VALUE) &&
				isSimple(vi.getChild(0).getChild(1).getChild(0), LLVMOperator.ALLOCA)) {
				final Vertex<FlowValue<LLVMParameter,LLVMLabel>> typenode = 
					vi.getChild(0).getChild(1).getChild(0).getChild(1);
				if (!(typenode.getLabel().isDomain() && typenode.getLabel().getDomain().isType()))
					throw new RuntimeException("Should be a type node");
				Type type = typenode.getLabel().getDomain().getTypeSelf().getType();
				if (type.isComposite() && type.getCompositeSelf().isPointer()) {
					debug("load of alloca DNA the alloca");
					addDNA(vertex2term.get(vi).getValue(),
						   vertex2term.get(vi.getChild(0).getChild(1)).getValue());
				}
			}
		}
		
		
		// different allocas do not alias
		for (int i = 0; i < vlist.size(); i++) {
			final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);
			if (!(isSimple(vi, LLVMOperator.RHO_VALUE) &&
				  isSimple(vi.getChild(0), LLVMOperator.ALLOCA)))
				continue;
			
			for (int j = i+1; j < vlist.size(); j++) {
				final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vj = vlist.get(j);
				if (!(isSimple(vj, LLVMOperator.RHO_VALUE) &&
					  isSimple(vj.getChild(0), LLVMOperator.ALLOCA)))
						continue;
				// different ALLOCAs do not alias
				debug("different allocas DNA");
				addDNA(vertex2term.get(vi).getValue(),
					   vertex2term.get(vj).getValue());
			}
		}
		
		
		// different globals do not alias
		for (int i = 0; i < vlist.size(); i++) {
			final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);
			if (!(vi.getLabel().isDomain() && 
				  vi.getLabel().getDomain().isGlobal()))
				continue;	
			
			for (int j = i+1; j < vlist.size(); j++) {
				final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vj = vlist.get(j);
				if (!(vj.getLabel().isDomain() && 
					  vj.getLabel().getDomain().isGlobal()))
					continue;
				
				// pairs of different globals do not alias
				debug("different globals DNA");
				addDNA(vertex2term.get(vi).getValue(),
					   vertex2term.get(vj).getValue());
			}
		}
		
		
		// geps of different indexes with same base do not alias
		// gep with nonzero indexes does not alias base ptr
		for (int i = 0; i < vlist.size(); i++) {
			final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);
			if (isSimple(vi, LLVMOperator.GETELEMENTPTR)) {
				// gep of with nonzero indexes does not alias base ptr
				final Vertex<FlowValue<LLVMParameter,LLVMLabel>> indexes = vi.getChild(2);
				boolean foundnonzero = false;
				for (int j = 0; j < indexes.getChildCount(); j++) {
					BigInteger index = isConstInt(indexes.getChild(j));
					if (index != null && !index.equals(BigInteger.ZERO)) {
						foundnonzero = true;
						break;
					}
				}
				if (foundnonzero) {
					debug("GEP of nonzero DNA base");
					addDNA(vertex2term.get(vi).getValue(),
						   vertex2term.get(vi.getChild(0)).getValue());
				}
				
				
				// different geps
				for (int j = i+1; j < vlist.size(); j++) {
					final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vj = vlist.get(j);
					if (isSimple(vj, LLVMOperator.GETELEMENTPTR)) {
						if (vi.getChild(0).equals(vj.getChild(0)) &&
							differentIndexes(vi.getChild(2), vj.getChild(2))) {
							if (DEBUG) {
								String indexes1 = "";
								String indexes2 = "";
								for (int k = 0; k < vi.getChild(2).getChildCount(); k++) {
									indexes1 += vi.getChild(2).getChild(k).getLabel() + " ";
								}
								for (int k = 0; k < vj.getChild(2).getChildCount(); k++) {
									indexes2 += vj.getChild(2).getChild(k).getLabel() + " ";
								}
								debug("GEPs of same base with different indexes DNA: " + indexes1 + ", " + indexes2);
							}
							addDNA(vertex2term.get(vi).getValue(),
								   vertex2term.get(vj).getValue());
						}
					}
				}
			}
		}
		
		
		// null does not alias malloc/alloca/global
		for (int i = 0; i < vlist.size(); i++) {
			final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);
			if (vi.getLabel().isDomain() &&
				vi.getLabel().getDomain().isConstantValue() &&
				vi.getLabel().getDomain().getConstantValueSelf().getValue().isConstantNullPointer()) {
				for (int j = 0; j < vlist.size(); j++) {
					final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vj = vlist.get(j);
					
					if (isSimple(vj, LLVMOperator.RHO_VALUE)) {
						Vertex<FlowValue<LLVMParameter,LLVMLabel>> child = vj.getChild(0);
						if (isSimple(child, LLVMOperator.ALLOCA) ||
							isSimple(child, LLVMOperator.MALLOC)) {
							// null does not alias alloca/malloc
							debug("null DNA " + child.getLabel().getDomain());
							addDNA(vertex2term.get(vi).getValue(),
								   vertex2term.get(vj).getValue());
						}
					} 
					else if (vj.getLabel().isDomain() && vj.getLabel().getDomain().isGlobal()) {
						// null does not alias global
						debug("null DNA global");
						addDNA(vertex2term.get(vi).getValue(),
							   vertex2term.get(vj).getValue());
					}
					else if (paramsDNANull && 
							 vj.getLabel().isParameter() &&
							 vj.getLabel().getParameter().isArgument()) {
						Type type = vj.getLabel().getParameter().getArgumentSelf().getType();
						if (type.isComposite() && type.getCompositeSelf().isPointer()) {
							addDNA(vertex2term.get(vi).getValue(),
								   vertex2term.get(vj).getValue());
						}
					}
				}
			}
		}

		
		// propagate doesNotAlias
		while (true) {
			MultiMap<CPEGValue<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> copy = 
				new HashMultiMap<CPEGValue<LLVMLabel,LLVMParameter>, CPEGValue<LLVMLabel,LLVMParameter>>();
			copy.addAll(doesNotAlias);
			
			for (int i = 0; i < vlist.size(); i++) {
				final Vertex<FlowValue<LLVMParameter,LLVMLabel>> vi = vlist.get(i);

				if (isSimple(vi, LLVMOperator.GETELEMENTPTR)) {
					// doesNotAlias(P1,P2) => doesNotAlias(GEP(P1),P2)
					final CPEGValue<LLVMLabel,LLVMParameter> baseValue = 
						vertex2term.get(vi.getChild(0)).getValue();
					for (Map.Entry<CPEGValue<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> pair : copy.entries()) {
						if (pair.getKey().equals(baseValue)) {
							debug("DNA(P1,P2) => DNA(GEP(P1),P2)");
							addDNA(vertex2term.get(vi).getValue(),
								   pair.getValue());
						} else if (pair.getValue().equals(baseValue)) {
							debug("DNA(P1,P2) => DNA(GEP(P1),P2)");
							addDNA(vertex2term.get(vi).getValue(),
								   pair.getKey());
						}
					}
				}

				else if (vi.getLabel().isDomain() &&
						vi.getLabel().getDomain().isCast() &&
						vi.getLabel().getDomain().getCastSelf().getOperator().equals(Cast.Bitcast)) {
					final Vertex<FlowValue<LLVMParameter,LLVMLabel>> child = 
						vi.getChild(0);
					if (!(child.getLabel().isDomain() &&
							child.getLabel().getDomain().isType()))
						throw new RuntimeException("Must be type label");
					final Type type = 
						child.getLabel().getDomain().getTypeSelf().getType();
					if (type.isComposite() &&
							type.getCompositeSelf().isPointer()) {
						// doesNotAlias(P1,P2) => doesNotAlias(bitcast(P1,ptr),P2)
						final CPEGValue<LLVMLabel,LLVMParameter> baseValue = 
							vertex2term.get(vi.getChild(0)).getValue();
						for (Map.Entry<CPEGValue<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> pair : copy.entries()) {
							if (pair.getKey().equals(baseValue)) {
								debug("DNA(P1,P2) => DNA(bitcast(P1),P2)");
								addDNA(vertex2term.get(vi).getValue(),
									   pair.getValue());
							} else if (pair.getKey().equals(baseValue)) {
								debug("DNA(P1,P2) => DNA(bitcast(P1),P2)");
								addDNA(vertex2term.get(vi).getValue(),
									   pair.getKey());
							}
						}
					}
				}
			}
			
			if (copy.numEntries() == doesNotAlias.numEntries())
				break;
		}
	}
	
	private BigInteger isConstInt(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> v) {
		if (v.getLabel().isDomain() &&
			v.getLabel().getDomain().isConstantValue() &&
			v.getLabel().getDomain().getConstantValueSelf().getValue().isInteger()) {
			return v.getLabel().getDomain().getConstantValueSelf().getValue().getIntegerSelf().getAsBigInteger();
		}
		return null;
	}
	
	/**
	 * Returns true only if the two indexes definitely do not alias.
	 */
	private boolean differentIndexes(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> indexes1,
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> indexes2) {
		int index = 0;
		while (index < indexes1.getChildCount() &&
			   index < indexes2.getChildCount()) {
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> child1 = indexes1.getChild(index);
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> child2 = indexes2.getChild(index);
			BigInteger b1 = isConstInt(child1);
			if (b1 != null) {
				BigInteger b2 = isConstInt(child2);
				if (b2 != null) {
					if (b1.equals(b2)) {
						index++;
						continue;
					}
					else
						return true;
				}
				else
					return false;
			} 
			else if (child1.equals(child2)) 
				index++;
			else
				return false;
		}
		
		// only one will have more values
		while (index < indexes1.getChildCount()) {
			BigInteger b1 = isConstInt(indexes1.getChild(index));
			if (b1!=null && !b1.equals(BigInteger.ZERO))
				return true;
			index++;
		}
		
		while (index < indexes2.getChildCount()) {
			BigInteger b2 = isConstInt(indexes2.getChild(index));
			if (b2!=null && !b2.equals(BigInteger.ZERO))
				return true;
			index++;
		}
		
		return false;
	}
}
