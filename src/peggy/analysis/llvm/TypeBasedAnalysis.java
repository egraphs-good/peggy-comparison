package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.types.CompositeType;
import llvm.types.FloatingPointType;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantExplicitArrayValue;
import llvm.values.ConstantExplicitVectorValue;
import llvm.values.ConstantNullPointerValue;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FloatingPointValue;
import llvm.values.IntegerValue;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.analysis.EPEGTypeAnalysis;
import peggy.analysis.WildcardPeggyAxiomizer;
import peggy.analysis.WildcardPeggyAxiomizer.AxiomValue;
import peggy.analysis.llvm.types.LLVMType;
import peggy.analysis.llvm.types.PEGType;
import peggy.represent.llvm.AnnotationLLVMLabel;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis has axioms that rely on the types from the LLVMEPEGTypeAnalysis.
 */
public abstract class TypeBasedAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("TypeBasedAnalysis: " + message);
	}
	
	protected TypeBasedAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	protected abstract EPEGTypeAnalysis<LLVMLabel,LLVMParameter,LLVMType> getTypeAnalysis();
	
	public static abstract class BitLabel extends AnnotationLLVMLabel {
		public abstract int getNumBits();
		public abstract Boolean getBit(int i);
		public abstract boolean isValidIndex(int i);

		public final boolean equalsAnnotation(AnnotationLLVMLabel label) {
			if (!(label instanceof BitLabel))
				return false;
			BitLabel b = (BitLabel)label;
			if (b.getNumBits() != this.getNumBits())
				return false;
			for (int i = 0; i < this.getNumBits(); i++) {
				if (this.getBit(i) != null && b.getBit(i) != null) {
					if (!this.getBit(i).equals(b.getBit(i)))
						return false;
				} else if (this.getBit(i) != null || b.getBit(i) != null) {
					return false;
				}
			}
			return true;
		}
		private Integer hashCache = null;
		public final int hashCode() {
			if (hashCache == null) {
				int numBits = this.getNumBits(); 
				int result = numBits*11;
				for (int i = 0; i < numBits; i++) {
					Boolean b = this.getBit(i);
					if (b==null)
						result = 3-result;
					else if (b)
						result = 5-result;
					else 
						result = 7-result;
				}
				hashCache = result;
			}
			return hashCache;
		}
		private String toStringCache = null;
		public final String toString() {
			if (toStringCache == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("Bits[");
				int numBits = this.getNumBits();
				for (int i = 0; i < numBits; i++) {
					Boolean b  = this.getBit(i);
					if (b==null)
						builder.append('U');
					else if (b)
						builder.append('1');
					else
						builder.append('0');
				}
				builder.append("]");
				toStringCache = builder.toString();
			}
			return toStringCache;
		}
	}
	
	public static class OverlayBitLabel extends BitLabel {
		private final BitLabel inner;
		private final Boolean[] newbits;
		
		public OverlayBitLabel(BitLabel _inner, List<Boolean> _bits) {
			this.inner = _inner;
			this.newbits = _bits.toArray(new Boolean[0]);
			for (int i = 0; i < this.newbits.length; i++) {
				if (!this.inner.isValidIndex(i))
					throw new RuntimeException("Too many new bits");
			}
		}
		public int getNumBits() {return this.inner.getNumBits();}
		public Boolean getBit(int i) {
			if (i >= 0 && i < this.newbits.length)
				return this.newbits[i];
			else
				return this.inner.getBit(i);
		}
		public boolean isValidIndex(int i) {
			return this.inner.isValidIndex(i);
		}
	}

	public static class OffsetBitLabel extends BitLabel {
		protected final BitLabel inner;
		protected final int offset;
		public OffsetBitLabel(BitLabel _inner, int _offset) {
			this.inner = _inner;
			this.offset = _offset;
			if (!this.inner.isValidIndex(this.offset))
				throw new IllegalArgumentException();
		}
		public boolean isValidIndex(int i) {
			return this.inner.isValidIndex(i + this.offset);
		}
		public int getNumBits() {
			return this.inner.getNumBits() - this.offset;
		}
		public Boolean getBit(int i) {
			return this.inner.getBit(i + this.offset);
		}
	}
	
	public static class ConcreteBitLabel extends BitLabel {
		private final Boolean[] bits;
		public ConcreteBitLabel(Boolean[] _bits) {
			this.bits = _bits;
		}
		public ConcreteBitLabel(int size, Boolean init) {
			this.bits = new Boolean[size];
			for (int i = 0; i < size; i++) {
				this.bits[i] = init;
			}
		}
		public boolean isValidIndex(int i) {
			return i>=0 && i<this.bits.length;
		}
		public int getNumBits() {return this.bits.length;}
		public Boolean getBit(int i) {return this.bits[i];}
	}
	
	private static final LLVMLabel hasBits = 
		new StringAnnotationLLVMLabel("hasBits");

	public void addAll() {
		addAMinusAEq0();
		addFreeCastPtrPtr();
		addXplusXeqXlshr1();
		addXorXXeq0();
		
		addAllocaInit();
		addGlobalPropagateBits();
		addStoreLiteral();
		addLoadBits();
		addGEPWithLiterals(1);
		addGEPWithLiterals(2);
		addGEPWithLiterals(3);
		addGEPWithLiterals(4);
		addBitcastHasBits();
	}

	private boolean flowIsIntConstant(FlowValue<LLVMParameter,LLVMLabel> flow) {
		return flow.isDomain() &&
			flow.getDomain().isConstantValue() &&
			flow.getDomain().getConstantValueSelf().getValue().isInteger();
	}
	
	
	/**
	 * hasBits(S1,G,bits)
	 * load(S2,*,*)
	 * [G global]
	 * ==>
	 * hasBits(S2,G,bits)
	 */
	public void addGlobalPropagateBits() {
		final String name = "Global with bits propagates under loads";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> hasBits1 =  
			helper.get("hasBits", hasBits,
					helper.getVariable("S1"),
					helper.get("G", 1),
					helper.get("bits", 2));
		Vertex<AxiomValue<LLVMLabel,Integer>> load = 
			helper.get("load", SimpleLLVMLabel.get(LLVMOperator.LOAD),
					helper.getVariable("S2"),
					helper.getVariable(),
					helper.getVariable());
		helper.mustBeTrue(hasBits1);
		helper.mustExist(load);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("bits"));
					addConstantProperties(proof, bundle.getTerm("G"));
				}

				Node result = node(hasBits,
								steal(bundle.getTerm("load"), 0),
								steal(bundle.getTerm("hasBits"), 1),
								steal(bundle.getTerm("hasBits"), 2));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return bundle.getTerm("G").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> G = bundle.getTerm("G").getOp();
				FlowValue<LLVMParameter,LLVMLabel> bits = bundle.getTerm("bits").getOp();
				return bits.isDomain() &&
					bits.getDomain().isAnnotation() &&
					bits.getDomain().getAnnotationSelf() instanceof BitLabel &&
					G.isDomain() &&
					G.getDomain().isGlobal();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * B:bitcast(T,P)
	 * hasBits(S,P,bits)
	 * [T is pointer type]
	 * ==>
	 * hasBits(S,B,bits)
	 */
	public void addBitcastHasBits() {
		final String name = "Bitcast of pointer with bits has same bits";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> P = helper.getVariable("P");
		Vertex<AxiomValue<LLVMLabel,Integer>> bitcast = 
			helper.get("bitcast", new CastLLVMLabel(Cast.Bitcast),
					helper.get("T", 1),
					P);
		Vertex<AxiomValue<LLVMLabel,Integer>> hasBits1 =  
			helper.get("hasBits", hasBits,
					helper.getVariable("SIGMA"),
					P,
					helper.get("bits", 2));
		helper.mustBeTrue(hasBits1);
		helper.mustExist(bitcast);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("bits"));
					addConstantProperties(proof, bundle.getTerm("T"));
				}

				Node result = node(hasBits,
								steal(bundle.getTerm("hasBits"), 0),
								concOld(bundle.getTerm("bitcast")),
								steal(bundle.getTerm("hasBits"), 2));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return bundle.getTerm("T").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> T = bundle.getTerm("T").getOp();
				FlowValue<LLVMParameter,LLVMLabel> bits = bundle.getTerm("bits").getOp();
				if (bits.isDomain() &&
					bits.getDomain().isAnnotation() &&
					bits.getDomain().getAnnotationSelf() instanceof BitLabel &&
					T.isDomain() &&
					T.getDomain().isType()) {
					Type type = T.getDomain().getTypeSelf().getType();
					return type.isComposite() && type.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * V:rho_value(load(S,P,A))
	 * hasBits(S,P,bits)
	 * [either P or V has a type]
	 * ==>
	 * V = appropriate constant value derived from bits
	 */
	public void addLoadBits() {
		final String name = "Load of pointer with bits";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> P = helper.getVariable("P");
		Vertex<AxiomValue<LLVMLabel,Integer>> SIGMA = helper.getVariable("SIGMA");
		Vertex<AxiomValue<LLVMLabel,Integer>> rho_value = 
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					helper.get("load", SimpleLLVMLabel.get(LLVMOperator.LOAD),
							SIGMA,
							P,
							helper.getVariable()));
		Vertex<AxiomValue<LLVMLabel,Integer>> hasBits1 =  
			helper.get("hasBits", hasBits,
					SIGMA,
					P,
					helper.get("bits", 1));
		helper.mustBeTrue(hasBits1);
		helper.mustExist(rho_value);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("bits"));
				}

				Type loadType;
				{
					PEGType<LLVMType> Ptype = getTypeAnalysis().getOrComputeType(bundle.getRep("P").getValue());
					PEGType<LLVMType> Vtype = getTypeAnalysis().getOrComputeType(bundle.getTerm("rho_value").getValue());
					if (Vtype!=null) {
						loadType = Vtype.getDomain().getSimpleType();
					} else if (Ptype != null) {
						loadType = Ptype.getDomain().getSimpleType().getCompositeSelf().getPointerSelf().getPointeeType();
					} else {
						throw new RuntimeException("No type");
					}
				}
				
				final BitLabel bits = (BitLabel)bundle.getTerm("bits").getOp().getDomain().getAnnotationSelf();
				Iterator<Boolean> biterator = new Iterator<Boolean>() {
					int index = 0;
					public boolean hasNext() {return true;}
					public Boolean next() {
						if (bits.isValidIndex(index))
							return bits.getBit(index++);
						else {
							index++;
							return false;
						}
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
				
				Value value = makeValue(loadType, biterator);

				Node result = node(new ConstantValueLLVMLabel(value));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);
				return value.toString();
			}
			private Value makeValue(Type type, Iterator<Boolean> bits) {
				if (type.isComposite()) {
					CompositeType ctype = type.getCompositeSelf();
					if (ctype.isArray()) {
						List<Value> elements = new ArrayList<Value>();
						int numElements = (int)ctype.getArraySelf().getNumElements().signedValue();
						Type elementType = ctype.getArraySelf().getElementType();
						for (int i = 0; i < numElements; i++) {
							elements.add(makeValue(elementType, bits));
						}
						return new ConstantExplicitArrayValue(ctype.getArraySelf(), elements);
					} else if (ctype.isVector()) {
						List<Value> elements = new ArrayList<Value>();
						int numElements = (int)ctype.getVectorSelf().getNumElements().signedValue();
						Type elementType = ctype.getVectorSelf().getElementType();
						for (int i = 0; i < numElements; i++) {
							elements.add(makeValue(elementType, bits));
						}
						return new ConstantExplicitVectorValue(ctype.getVectorSelf(), elements);
					} else if (ctype.isPointer()) {
						return new ConstantNullPointerValue(ctype.getPointerSelf());
					} else if (ctype.isStructure()) {
						StructureType stype = ctype.getStructureSelf();
						List<Value> fields = new ArrayList<Value>();
						int numFields = ctype.getStructureSelf().getNumFields();
						for (int i = 0; i < numFields; i++) {
							Type fieldType = stype.getFieldType(i);
							fields.add(makeValue(fieldType, bits));
						}
						return new ConstantStructureValue(stype, fields);
					} else {
						throw new RuntimeException("Did not implement: " + ctype);
					}
				} else if (type.isFloatingPoint()) {
					FloatingPointType fp = type.getFloatingPointSelf();
					int numBits = fp.getKind().getTypeSize();
					BitSet newbits = new BitSet();
					for (int i = 0; i < numBits; i++) {
						Boolean b = bits.next();
						newbits.set(i, b!=null && b);
					}
					return FloatingPointValue.get(fp, newbits);
				} else if (type.isInteger()) {
					int width = type.getIntegerSelf().getWidth();
					BitSet newbits = new BitSet();
					for (int i = 0; i < width; i++) {
						Boolean b = bits.next();
						newbits.set(i, b!=null && b);
					}
					return new IntegerValue(width, newbits);
				} else {
					throw new RuntimeException("Cannot create value of type: " + type);
				}
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> bits = bundle.getTerm("bits").getOp();
				if (bits.isDomain() &&
					bits.getDomain().isAnnotation() &&
					bits.getDomain().getAnnotationSelf() instanceof BitLabel) {
					PEGType<LLVMType> Ptype = getTypeAnalysis().getOrComputeType(bundle.getRep("P").getValue());
					PEGType<LLVMType> Vtype = getTypeAnalysis().getOrComputeType(bundle.getTerm("rho_value").getValue());
					return Ptype!=null || Vtype!=null;
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	/**
	 * hasBits(S,P,bits)
	 * G:GEP(P,T,indexes(L1,L2,...,Ln))  // where N is given
	 * [all Li are constants]
	 * ==>
	 * hasBits(S,G,newbits)
	 * [where newbits is a suffix of bits]
	 */
	public void addGEPWithLiterals(final int numLits) {
		final String name = "GEP with literal indexes on pointer with bits has bits";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> P = helper.getVariable("P");
		Vertex<AxiomValue<LLVMLabel,Integer>> hasBits1 =  
			helper.get("hasBits", hasBits,
					helper.getVariable("S"),
					P,
					helper.get("bits", 1));
		
		List<Vertex<AxiomValue<LLVMLabel,Integer>>> lits = 
			new ArrayList<Vertex<AxiomValue<LLVMLabel,Integer>>>();
		for (int i = 0; i < numLits; i++) {
			lits.add(helper.get("L" + i, i+3));
		}
		
		Vertex<AxiomValue<LLVMLabel,Integer>> gep = 
			helper.get("gep", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
				P,
				helper.get("T", 2),
				helper.get("indexes", SimpleLLVMLabel.get(LLVMOperator.INDEXES),
						lits));
					
		helper.mustBeTrue(hasBits1);
		helper.mustExist(gep);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("T"));
					addConstantProperties(proof, bundle.getTerm("bits"));
					for (int i = 0; i < numLits; i++)
						addConstantProperties(proof, bundle.getTerm("L" + i));
				}

				// get literal offsets
				List<Long> offsets = new ArrayList<Long>();
				for (int i = 0; i < numLits; i++) {
					FlowValue<LLVMParameter,LLVMLabel> Lvalue = bundle.getTerm("L" + i).getOp();
					offsets.add(Lvalue.getDomain().getConstantValueSelf().getValue().getIntegerSelf().getLongBits());
				}

				Type pointee = bundle.getTerm("T").getOp().getDomain().getTypeSelf().getType().getCompositeSelf().getPointerSelf().getPointeeType();
				int bitOffset = (int)getBitOffset(pointee, offsets);
				BitLabel newbits = new OffsetBitLabel(
						(BitLabel)bundle.getTerm("bits").getOp().getDomain().getAnnotationSelf(),
						bitOffset);

				Node result = node(hasBits,
									steal(bundle.getTerm("hasBits"), 0),
									concOld(bundle.getTerm("gep")),
									conc(node(newbits)));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return newbits.toString();
			}
			private long getBitOffset(Type type, List<Long> offsets) {
				long result = 0L;
				for (Long offset : offsets) {
					if (type == null)
						throw new RuntimeException("Type cannot be indexed");
					result += offset*type.getTypeSize();
					if (type.isComposite()) {
						CompositeType ctype = type.getCompositeSelf();
						type = ctype.getElementType(offset.intValue());
					} else
						type = null;
				}
				return result;
			}
			protected boolean matches(Bundle bundle) {
				List<Long> offsets = new ArrayList<Long>();
				for (int i = 0; i < numLits; i++) {
					FlowValue<LLVMParameter,LLVMLabel> Lvalue = bundle.getTerm("L" + i).getOp();
					if (!flowIsIntConstant(Lvalue)) {
						return false;
					}
					offsets.add(Lvalue.getDomain().getConstantValueSelf().getValue().getIntegerSelf().getLongBits());
				}
				
				FlowValue<LLVMParameter,LLVMLabel> Tvalue = bundle.getTerm("T").getOp();
				FlowValue<LLVMParameter,LLVMLabel> bits = bundle.getTerm("bits").getOp();
				if (Tvalue.isDomain() && 
					Tvalue.getDomain().isType()) {
					if (bits.isDomain() &&
						bits.getDomain().isAnnotation() &&
						bits.getDomain().getAnnotationSelf() instanceof BitLabel) {
						Type pointee = Tvalue.getDomain().getTypeSelf().getType().getCompositeSelf().getPointerSelf().getPointeeType();
						long bitOffset = getBitOffset(
								pointee,
								offsets);
						BitLabel bitLabel = (BitLabel)bits.getDomain().getAnnotationSelf();
						if (bitLabel.isValidIndex((int)bitOffset)) {
							return true;
						}
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	/**
	 * S2:store(S,P,V,A)
	 * hasBits(S,P,bits)
	 * [V is constant value]
	 * ==>
	 * hasBits(S2,P,newbits)
	 * [where newbits is derived from bits and V]
	 */
	public void addStoreLiteral() {
		final String name = "Store of literal updates hasBits";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> SIGMA = 
			helper.getVariable("SIGMA");
		Vertex<AxiomValue<LLVMLabel,Integer>> P = 
			helper.getVariable("P");
		Vertex<AxiomValue<LLVMLabel,Integer>> store = 
			helper.get("store", SimpleLLVMLabel.get(LLVMOperator.STORE),
					SIGMA,
					P,
					helper.get("V", 1),
					helper.getVariable());
		Vertex<AxiomValue<LLVMLabel,Integer>> hasBits1 =  
			helper.get("hasBits", hasBits,
					SIGMA,
					P,
					helper.get("bits", 2));
		helper.mustExist(store);
		helper.mustBeTrue(hasBits1);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("V"));
					addConstantProperties(proof, bundle.getTerm("bits"));
				}
				
				List<Boolean> Vbits = getBits(bundle.getTerm("V").getOp().getDomain().getConstantValueSelf().getValue());
				BitLabel bits = (BitLabel)bundle.getTerm("bits").getOp().getDomain().getAnnotationSelf();
				BitLabel newbits = new OverlayBitLabel(bits, Vbits);
				
				Node result = node(hasBits,
									concOld(bundle.getTerm("store")),
									steal(bundle.getTerm("store"), 1),
									conc(node(newbits)));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return newbits.toString();
			}
			private List<Boolean> getBits(Value value) {
				if (value.isConstantArray()) {
					ConstantArrayValue cv = value.getConstantArraySelf();
					List<Boolean> bits = new ArrayList<Boolean>();
					int numElements = (int)cv.getNumElements().signedValue();
					for (int i = 0; i < numElements; i++) {
						bits.addAll(getBits(cv.getElement(i)));
					}
					return bits;
				} else if (value.isConstantNullPointer()) {
					ConstantNullPointerValue cp = value.getConstantNullPointerSelf();
					int numBits = (int)cp.getType().getTypeSize();
					List<Boolean> bits = new ArrayList<Boolean>(numBits);
					for (int i = 0; i < numBits; i++)
						bits.add(false);
					return bits;
				} else if (value.isConstantStructure()) {
					ConstantStructureValue cs = value.getConstantStructureSelf();
					List<Boolean> bits = new ArrayList<Boolean>();
					for (int i = 0; i < cs.getNumFields(); i++) {
						bits.addAll(getBits(cs.getFieldValue(i)));
					}
					return bits;
				} else if (value.isConstantVector()) {
					ConstantVectorValue cv = value.getConstantVectorSelf();
					List<Boolean> bits = new ArrayList<Boolean>();
					int numElements = (int)cv.getNumElements().signedValue();
					for (int i = 0; i < numElements; i++) {
						bits.addAll(getBits(cv.getElement(i)));
					}
					return bits;
				} else if (value.isFloatingPoint()) {
					FloatingPointValue f = value.getFloatingPointSelf();
					List<Boolean> bits = new ArrayList<Boolean>();
					int numBits = f.getType().getKind().getTypeSize();
					for (int i = 0; i < numBits; i++)
						bits.add(f.getBit(i));
					return bits;
				} else if (value.isInteger()) {
					IntegerValue iv = value.getIntegerSelf();
					List<Boolean> bits = new ArrayList<Boolean>();
					for (int i = 0; i < iv.getWidth(); i++) {
						bits.add(iv.getBit(i));
					}
					return bits;
				} else if (value.isUndef()) {
					int numBits = (int)value.getType().getTypeSize();
					List<Boolean> bits = new ArrayList<Boolean>(numBits);
					for (int i = 0; i < numBits; i++)
						bits.add(false);
					return bits;
				} else {
					throw new RuntimeException("Forgot to implement: " + value);
				}
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> V = bundle.getTerm("V").getOp();
				FlowValue<LLVMParameter,LLVMLabel> bits = bundle.getTerm("bits").getOp();
				return bits.isDomain() &&
					bits.getDomain().isAnnotation() &&
					(bits.getDomain().getAnnotationSelf() instanceof BitLabel) &&
					V.isDomain() &&
					V.getDomain().isConstantValue();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	/**
	 * V:rho_value(A:alloca(*,T,N,A))
	 * R:rho_sigma(A)
	 * [N is a literal]
	 * =>
	 * hasBits(R,V, bit pattern of sizeof(T)*N  0's)
	 */
	public void addAllocaInit() {
		final String name = "Initial bit pattern of ALLOCA";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		Vertex<AxiomValue<LLVMLabel,Integer>> alloca = 
			helper.get("alloca", SimpleLLVMLabel.get(LLVMOperator.ALLOCA),
					helper.getVariable(),
					helper.get("T", 1),
					helper.get("N", 2),
					helper.getVariable());
		Vertex<AxiomValue<LLVMLabel,Integer>> rho_value = 
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					alloca);
		Vertex<AxiomValue<LLVMLabel,Integer>> rho_sigma = 
			helper.get("rho_sigma", SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA),
					alloca);
		helper.mustExist(rho_value);
		helper.mustExist(rho_sigma);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("T"));
					addConstantProperties(proof, bundle.getTerm("N"));
				}
				
				long typeBits = bundle.getTerm("T").getOp().getDomain().getTypeSelf().getType().getTypeSize();
				int numElements = bundle.getTerm("N").getOp().getDomain().getConstantValueSelf().getValue().getIntegerSelf().getIntBits();
				BitLabel bits = new ConcreteBitLabel((int)(typeBits*numElements), new Boolean(false));

				Node result = node(hasBits,
									concOld(bundle.getTerm("rho_sigma")),
									concOld(bundle.getTerm("rho_value")),
									conc(node(bits)));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return bits.toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> Tvalue = bundle.getTerm("T").getOp();
				FlowValue<LLVMParameter,LLVMLabel> Nvalue = bundle.getTerm("N").getOp();
				return Tvalue.isDomain() && 
					 Tvalue.getDomain().isType() &&
					 flowIsIntConstant(Nvalue);
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	public void addXorXXeq0() {
		final String name = "X xor X = 0";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> X = helper.getVariable("X");
		PeggyVertex<LLVMLabel,Integer> xor =
			helper.get("xor", new BinopLLVMLabel(Binop.Xor), X, X);
		helper.mustExist(xor);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				int width = getTypeAnalysis().getOrComputeType(bundle.getRep("X").getValue()).getDomain().getSimpleType().getIntegerSelf().getWidth();
				Value zero = IntegerValue.getZero(width);
				Node result = node(new ConstantValueLLVMLabel(zero));
				result.finish(bundle.getTerm("xor"), proof, futureGraph);
				return zero.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGValue<LLVMLabel, LLVMParameter> Xvalue = bundle.getRep("X").getValue();
				PEGType<LLVMType> Xtype = getTypeAnalysis().getOrComputeType(Xvalue);
				return Xtype!=null && 
					Xtype.isDomain() && 
					Xtype.getDomain().isSimple() &&
					Xtype.getDomain().getSimpleType().isInteger();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	public void addXplusXeqXlshr1() {
		final String name = "X+X = X<<1";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> X = helper.getVariable("X");
		PeggyVertex<LLVMLabel,Integer> add =
			helper.get("add", new BinopLLVMLabel(Binop.Add), X, X);
		helper.mustExist(add);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				int width = getTypeAnalysis().getOrComputeType(bundle.getRep("X").getValue()).getDomain().getSimpleType().getIntegerSelf().getWidth();
				Value one = IntegerValue.getOne(width);
				Node result = node(new BinopLLVMLabel(Binop.LShr),
								   steal(bundle.getTerm("add"), 0),
								   conc(node(new ConstantValueLLVMLabel(one))));
				result.finish(bundle.getTerm("add"), proof, futureGraph);
				return one.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGValue<LLVMLabel, LLVMParameter> Xvalue = bundle.getRep("X").getValue();
				PEGType<LLVMType> Xtype = getTypeAnalysis().getOrComputeType(Xvalue);
				return Xtype!=null && 
					Xtype.isDomain() && 
					Xtype.getDomain().isSimple() &&
					Xtype.getDomain().getSimpleType().isInteger();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	public void addFreeCastPtrPtr() {
		final String name = "free(cast[ptr->ptr] X) = free(X)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> free =
			helper.get("free", SimpleLLVMLabel.get(LLVMOperator.FREE),
					helper.getVariable("SIGMA"),
					helper.get("cast", null,
							helper.getVariable("TYPE"),
							helper.getVariable("X")));
		helper.mustExist(free);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				Node result = node(SimpleLLVMLabel.get(LLVMOperator.FREE),
								   steal(bundle.getTerm("free"), 0),
								   steal(bundle.getTerm("cast"), 1));
				result.finish(bundle.getTerm("free"), proof, futureGraph);
				return bundle.getTerm("cast").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> cast = bundle.getTerm("cast");
				if (cast.getOp().isDomain() && cast.getOp().getDomain().isCast()) {
					CPEGValue<LLVMLabel, LLVMParameter> Xvalue = bundle.getRep("X").getValue();
					PEGType<LLVMType> Xtype = getTypeAnalysis().getOrComputeType(Xvalue);
					if (Xtype!=null && Xtype.isDomain() && Xtype.getDomain().isSimple()) {
						Type type = Xtype.getDomain().getSimpleType();
						return type.isComposite() && type.getCompositeSelf().isPointer();
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	public void addAMinusAEq0() {
		final String name = "A - A = 0 (of correct type)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> A = helper.getVariable("A");
		PeggyVertex<LLVMLabel,Integer> minus =
			helper.get("minus", new BinopLLVMLabel(Binop.Sub), A, A);
		helper.mustExist(minus);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				CPEGValue<LLVMLabel,LLVMParameter> Avalue = bundle.getRep("A").getValue();
				PEGType<LLVMType> pegtype = getTypeAnalysis().getOrComputeType(Avalue);
				if (!(pegtype.isDomain() && pegtype.getDomain().isSimple()))
					throw new RuntimeException("Must be simple type: " + pegtype);
				Type type = pegtype.getDomain().getSimpleType();
				Value zero;
				if (type.isInteger() || type.isFloatingPoint()) {
					zero = Value.getNullValue(type);
				} else {
					throw new RuntimeException("Expecting numeric type: " + type);
				}

				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				Node result = node(new ConstantValueLLVMLabel(zero));
				result.finish(bundle.getTerm("minus"), proof, futureGraph);
				
				return type.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGValue<LLVMLabel, LLVMParameter> Avalue = bundle.getRep("A").getValue();
				return getTypeAnalysis().getOrComputeType(Avalue) != null;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
