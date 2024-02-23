package llvm.instructions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import llvm.types.Type;
import llvm.values.DebugLocation;
import llvm.values.Value;

/**
 * This is the parent class of all LLVM instruction instances.
 * Every instruction may have metadata and metadata kinds, as well
 * as debug location data.
 */
public abstract class Instruction {
	public boolean isTerminator() {return false;}
	public TerminatorInstruction getTerminatorSelf() {throw new UnsupportedOperationException();}
	
	public boolean isBinop() {return false;}
	public BinopInstruction getBinopSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCast() {return false;}
	public CastInstruction getCastSelf() {throw new UnsupportedOperationException();}
	
	public boolean isShuffleVec() {return false;}
	public ShuffleVecInstruction getShuffleVecSelf() {throw new UnsupportedOperationException();}
	
	public boolean isInsertElt() {return false;}
	public InsertEltInstruction getInsertEltSelf() {throw new UnsupportedOperationException();}

	public boolean isGEP() {return false;}
	public GEPInstruction getGEPSelf() {throw new UnsupportedOperationException();}

	public boolean isSelect() {return false;}
	public SelectInstruction getSelectSelf() {throw new UnsupportedOperationException();}
	
	public boolean isExtractElt() {return false;}
	public ExtractEltInstruction getExtractEltSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCmp() {return false;}
	public CmpInstruction getCmpSelf() {throw new UnsupportedOperationException();}
	
	public boolean isPhi() {return false;}
	public PhiInstruction getPhiSelf() {throw new UnsupportedOperationException();}
	
	public boolean isGetResult() {return false;}
	public GetResultInstruction getGetResultSelf() {throw new UnsupportedOperationException();}

	public boolean isMalloc() {return false;}
	public MallocInstruction getMallocSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFree() {return false;}
	public FreeInstruction getFreeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isAlloca() {return false;}
	public AllocaInstruction getAllocaSelf() {throw new UnsupportedOperationException();}
	
	public boolean isLoad() {return false;}
	public LoadInstruction getLoadSelf() {throw new UnsupportedOperationException();}
	
	public boolean isStore() {return false;}
	public StoreInstruction getStoreSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCall() {return false;}
	public CallInstruction getCallSelf() {throw new UnsupportedOperationException();}
	
	public boolean isVaarg() {return false;}
	public VaargInstruction getVaargSelf() {throw new UnsupportedOperationException();}

	// new 2.8 instructions //////////////

	public boolean is2_8Instruction() {return false;}
	
	public boolean isExtractValue() {return false;}
	public ExtractValueInstruction getExtractValueSelf() {throw new UnsupportedOperationException();}
	
	public boolean isInsertValue() {return false;}
	public InsertValueInstruction getInsertValueSelf() {throw new UnsupportedOperationException();}

	public boolean isVSelect() {return false;}
	public VSelectInstruction getVSelectSelf() {throw new UnsupportedOperationException();}
	
	public boolean isShuffleVec2_8() {return false;}
	public ShuffleVec2_8Instruction getShuffleVec2_8Self() {throw new UnsupportedOperationException();}
	
	/////////////////////////////////////
	
	public abstract boolean equalsInstruction(Instruction i);
	public abstract Type getType();
	public abstract Iterator<? extends Value> getValues();
	public abstract Iterator<? extends Type> getTypes();
	
	public final Instruction rewrite(Value oldValue, Value newValue) {
		if (!oldValue.getType().equalsType(newValue.getType()))
			throw new IllegalArgumentException("Values do not share types: " + oldValue.getType() + " != " + newValue.getType());
		return this.rewriteChildren(Collections.<Value,Value>singletonMap(oldValue, newValue));
	}
	
	public final Instruction rewrite(Map<Value,Value> old2new) {
		return this.rewriteChildren(old2new);
	}
	protected abstract Instruction rewriteChildren(Map<Value,Value> old2new);
	
	/////// new 2.8 metadata stuff //////////
	
	protected DebugLocation debugLocation = null;
	public final DebugLocation getDebugLocation() {
		return this.debugLocation;
	}
	public final void setDebugLocation(DebugLocation dl) {
		if (this.debugLocation != null)
			throw new IllegalStateException("Already has debug location");
		this.debugLocation = dl;
	}
	
	protected final Map<Integer,Value> kind2node = 
		new HashMap<Integer,Value>();
	public final Value getMetadata(int kind) {
		return this.kind2node.get(kind);
	}
	public final void setMetadata(int kind, Value data) {
		data.ensureMetadata();
		this.kind2node.put(kind, data);
	}
	public Set<Integer> getMetadataKinds() {
		return this.kind2node.keySet();
	}
	public void removeMetadata(int kind) {
		this.kind2node.remove(kind);
	}
	public boolean hasMetadataKind(int kind) {
		return this.kind2node.containsKey(kind);
	}
}
