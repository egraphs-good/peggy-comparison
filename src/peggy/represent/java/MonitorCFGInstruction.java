package peggy.represent.java;

import soot.Value;

/**
 * This is a CFGInstruction for ENTERMONITOR and EXITMONITOR Java instructions.
 */
public class MonitorCFGInstruction extends CFGInstruction {
	private final boolean isEnter;
	private final Value target;
	
	public MonitorCFGInstruction(Value _target, boolean _enter) {
		this.target = _target;
		this.isEnter = _enter;
	}
	
	public boolean isMonitor() {return true;}
	public MonitorCFGInstruction getMonitorSelf() {return this;}
	
	public boolean isEnter() {return this.isEnter;}
	public Value getTarget() {return this.target;}
	
	public String toString() {
		return "monitor" + (isEnter ? "enter(" : "exit(") + target.toString() + ")";
	}
}
