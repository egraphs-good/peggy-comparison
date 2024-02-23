package peggy.represent.java;

/**
 * This class enumerates all the simple java PEG operators.
 * These are used inside SimpleJavaLabel instances.
 */
public enum JavaOperator {
	PRIMITIVECAST("primcast",2,false,1),  		// (type,v) -> v
	CAST("cast",3,true,2),						// (sigma,type,v) -> (sigma,E|v) 
	ARRAYLENGTH("arraylength",2,true,2),		// (sigma,v) -> (sigma,E|v)
	GETFIELD("getfield",3,true,2),				// (sigma,target,field) -> (sigma,E|v)
	GETSTATICFIELD("getstaticfield",2,true,2),	// (sigma,field) -> (sigma,E|v)
	GETARRAY("getarray",3,true,2),				// (sigma,array:v,index:v) -> (sigma,E|V)
	INSTANCEOF("instanceof",3,true,2),			// (sigma,v,type) -> (sigma,()|v)
	INJL("injl",1,false,1),						// E -> E|v
	INJR("injr",1,false,1),						// v -> E|v
	VOID("void",0,false,1),						// v
	PLUS("add",2,false,1),						// (v,v) -> v
	MINUS("sub",2,false,1),						// (v,v) -> v
	TIMES("mul",2,false,1),						// (v,v) -> v
	DIVIDE("div",2,true,1),						// (sigma,v,v) -> (sigma,E|v)   ??
	MOD("mod",2,true,1),						// (sigma,v,v) -> (sigma,E|v)   ??
	CMP("cmp",2,false,1),						// (v,v) -> v
	CMPL("cmpl",2,false,1),						// (v,v) -> v
	CMPG("cmpg",2,false,1),						// (v,v) -> v
	GREATER_THAN_EQUAL("gte",2,false,1),		// (v,v) -> v
	GREATER_THAN("gt",2,false,1),				// (v,v) -> v
	LESS_THAN_EQUAL("lte",2,false,1),			// (v,v) -> v
	LESS_THAN("lt",2,false,1),					// (v,v) -> v
	EQUAL("eq",2,false,1),						// (v,v) -> v
	NOT_EQUAL("ne",2,false,1),					// (v,v) -> v
	BITWISE_AND("and",2,false,1),				// (v,v) -> v
	BITWISE_OR("or",2,false,1),					// (v,v) -> v
	SHIFT_LEFT("shl",2,false,1),				// (v,v) -> v
	SHIFT_RIGHT("shr",2,false,1),				// (v,v) -> v
	UNSIGNED_SHIFT_RIGHT("ushr",2,false,1),		// (v,v) -> v
	XOR("xor",2,false,1),						// (v,v) -> v
	NEG("neg",1,false,1),						// v -> v
	CLASS("class",1,true,2),					// (sigma,type) -> (sigma,E|v)
	PARAMS("params",-1,false,1),				// (v1,...,vN) -> v
	INVOKESTATIC("invokestatic",3,true,2),		// (sigma,method,params) -> (sigma,E|v)
	INVOKEVIRTUAL("invokevirtual",4,true,2),	// (sigma,target:v,method,params) -> (sigma,E|v)
	INVOKEINTERFACE("invokeinterface",4,true,2),// (sigma,target:v,method,params) -> (sigma,E|v)
	INVOKESPECIAL("invokespecial",4,true,2),	// (sigma,target:v,method,params) -> (sigma,E|v)
	NEWARRAY("newarray",3,true,2),				// (sigma,type,size:v) -> (sigma,E|v)
	DIMS("dims",-1,false,1),					// (v1,...,vN) -> v
	NEWMULTIARRAY("newmultiarray",3,true,2),    // (sigma,type,dims) -> (sigma,E|v) 
	NEWINSTANCE("newinstance",2,true,2),		// (sigma,type) -> (sigma,E|v)
	ENTERMONITOR("entermonitor",2,true,2),		// (sigma,v) -> (sigma,E|())
	EXITMONITOR("exitmonitor",2,true,2),		// (sigma,v) -> (sigma,E|())
	SETARRAY("setarray",4,true,2),				// (sigma,array:v,index:v,value:v) -> (sigma,E|())
	SETFIELD("setfield",4,true,2),				// (sigma,target:v,field,value:v) -> (sigma,E|())
	SETSTATICFIELD("setstaticfield",3,true,2),	// (sigma,field,v) -> (sigma,E|())
	THROW("throw",3,true,2),					// (sigma,v) -> (sigma,E|())
	RHO_VALUE("rho_value",1,false,1),			// (.,.|v) -> v
	RHO_SIGMA("rho_sigma",1,true,1),			// (sigma,.) -> sigma
	;

	private final String label;
	protected final int numArguments, numOutputs;
	private final boolean usesSigma;
	
	private JavaOperator(
			String _label, 
			int _numArguments, 
			boolean _sigma, 
			int _numOutputs) {
		this.numOutputs = _numOutputs;
		this.label = _label;
		this.numArguments = _numArguments;
		this.usesSigma = _sigma;
	}
	public String getLabel() {return this.label;}
	public int getArgumentCount() {return this.numArguments;}
	public int getNumOutputs() {return this.numOutputs;}
	public boolean usesSigma() {return this.usesSigma;}
}
