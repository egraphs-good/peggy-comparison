package llvm.values;

/**
 * This represents the debug location data for an instruction.
 * It names a source line number, column number, scope, and IA(?).
 */
public class DebugLocation {
	/*
	 * scope and IA can be null
	 */
	private final int line;
	private final int column;
	private final Value scope;
	private final Value IA;
	
	public DebugLocation(int _line, int _column, Value _scope, Value _IA) {
		this.line = _line;
		this.column = _column;
		this.scope = _scope;
		this.IA = _IA;
		if (this.scope != null)
			this.scope.ensureMetadata();
		if (this.IA != null)
			this.IA.ensureMetadata();
	}
	
	public int getLine() {return this.line;}
	public int getColumn() {return this.column;}
	public Value getScope() {return this.scope;}
	public Value getIA() {return this.IA;}
	
	public boolean equals(Object o) {
		if (!(o instanceof DebugLocation))
			return false;
		DebugLocation d = (DebugLocation)o;
		
		if (this.scope != null && d.scope != null) {
			if (!this.scope.equalsValue(d.scope))
				return false;
		} else if (this.scope != null || d.scope != null) { 
			return false;
		}

		if (this.IA != null && d.IA != null) {
			if (!this.IA .equalsValue(d.IA))
				return false;
		} else if (this.IA != null || d.IA != null) { 
			return false;
		}
		
		return this.line == d.line &&
			this.column == d.column;
	}
	public int hashCode() {
		int result = this.line*3 + this.column*5;
		if (this.scope!=null)
			result += this.scope.hashCode()*7;
		if (this.IA!=null)
			result += this.IA.hashCode()*11;
		return result;
	}
}
