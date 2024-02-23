package peggy.represent.java;

/**
 * A CFGInstruction is the basic instruction type for an ExpressionCFG.
 */
public abstract class CFGInstruction {
	public boolean isIf() {return false;}
	public IfCFGInstruction getIfSelf() {throw new UnsupportedOperationException();}
	
	public boolean isEval() {return false;}
	public EvalCFGInstruction getEvalSelf() {throw new UnsupportedOperationException();}
	
	public boolean isMonitor() {return false;}
	public MonitorCFGInstruction getMonitorSelf() {throw new UnsupportedOperationException();}
}
