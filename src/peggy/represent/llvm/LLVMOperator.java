package peggy.represent.llvm;

/**
 * This class enumerates many of the LLVM operators.
 * These are used inside of SimpleLLVMLabel instances.
 */
public enum LLVMOperator {
	INJR(1,false,false),					// v -> e|v
	INJL(1,false,false),					// e -> e|v
	CALL(4,true,true),						// (sigma,function,n:CC,params) -> (sigma,e|v)
	TAILCALL(4,true,true),					// (sigma,function,n:CC,params) -> (sigma,e|v)
	INVOKE(4,true,true),					// (sigma,function,n:CC,params) -> (sigma,e|v)
	RHO_VALUE(1,false,false),				// forall A, (sigma,e|A) -> A
	RHO_SIGMA(1,false,false),				// forall A, (sigma,A) -> sigma
	RHO_EXCEPTION(1,false,false),			// forall A, (sigma,e|A) -> e
	SHUFFLEVECTOR(3,false,false),			// (v:vec1,v:vec2,v:mask) -> v:vec
	INSERTELEMENT(3,false,false),			// (v:vec,v:elt,v:idx) -> v:vec
	GETELEMENTPTR(3,false,false),			// (v:base,type:basetype,indexes) -> v:ptr
	INBOUNDSGETELEMENTPTR(3,false,false),	// (v:base,type:basetype,indexes) -> v:ptr
	INDEXES(-1,false,false), 				// (v:off1,...,v:offN) -> v
	SELECT(3,false,false),					// (v:cond,v:trueVal,v:falseVal) -> v
	EXTRACTELEMENT(2,false,false),			// (v:vec,v:idx) -> v
	GETRESULT(2,false,false),				// (returnstructure,n:idx) -> v
	MALLOC(4,true,true),					// (sigma,type,v:numElts,n:align) -> (sigma,()|v:ptr)
	FREE(2,true,true),						// (sigma,v:ptr) -> sigma
	ALLOCA(4,true,true),					// (sigma,type,v:numElts,n:align) -> (sigma,()|v:ptr)
	VOLATILE_LOAD(3,true,false),			// (sigma,v:ptr,n:align) -> v  OR  (sigma,v:ptr,n:align) -> (sigma,()|v)
	LOAD(3,true,false),						// (sigma,v:ptr,n:align) -> v  OR  (sigma,v:ptr,n:align) -> (sigma,()|v)
	VOLATILE_STORE(4,true,true),			// (sigma,v:ptr,v:val,n:align) -> sigma
	STORE(4,true,true),						// (sigma,v:ptr,v:val,n:align) -> sigma
	PARAMS(-1,false,false),					// (v1,...,vN)
	UNWIND(1,true,true),					// sigma -> (sigma,e|())
	VOID(0,false,false),					// void
	RETURNSTRUCTURE(-1,false,false),		// (v0,...,vN)
	VAARG(3,true,true),						// (sigma,v:valist,type) -> (sigma,()|v)
	IS_EXCEPTION(1,false,false),			// (_,e|_) -> bool
	NONSTACK(1,false,false),				// sigma -> sigma
	
	INSERTVALUE(3,false,false),				// (v:agg, v:elt, offsets) -> v
	EXTRACTVALUE(2,false,false),			// (v:agg, offsets) -> v
	OFFSETS(-1,false,false),				// (num0,...,numN)  (used by insertvalue and extractvalue)
	VSELECT(3,false,false),					// (v:cond,v:true,v:false) -> v
	;
	
	private final int arity;
	private final boolean altersSigma;
	private final boolean readsSigma;
	private LLVMOperator(int _arity, boolean _reads, boolean _alters) {
		this.arity = _arity;
		this.readsSigma = _reads;
		this.altersSigma = _alters;
	}
	public int getArity() {return this.arity;}
	public boolean altersSigma() {return this.altersSigma;}
	public boolean readsSigma() {return this.readsSigma;}
}
