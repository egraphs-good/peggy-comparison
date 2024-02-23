package peggy.revert.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Unit;

/**
 * This is a CFG block for Soot CFGs for use when reverting a PEG to a CFG.
 */
public class SootBlock {
	private final List<Unit> instructions;
	private final List<SootBlock> successors;
	
	public SootBlock() {
		this.instructions = new ArrayList<Unit>();
		this.successors = new ArrayList<SootBlock>();
	}
	
	public int getNumInstructions() {return this.instructions.size();}
	public Unit removeInstruction(int index) {return this.instructions.remove(index);}
	public void addInstruction(Unit unit) {this.instructions.add(unit);}
	public void addInstruction(int index, Unit unit) {this.instructions.add(index, unit);}
	public Unit getInstruction(int index) {return this.instructions.get(index);}
	public Iterable<? extends Unit> instructions() {
		return Collections.unmodifiableList(this.instructions);
	}
	
	public int getNumSuccessors() {return this.successors.size();}
	public SootBlock removeSuccessor(int index) {return this.successors.remove(index);}
	public void addSuccessor(SootBlock block) {this.successors.add(block);}
	public void addSuccessor(int index, SootBlock block) {this.successors.add(index, block);}
	public SootBlock replaceSuccessor(int index, SootBlock block) {
		SootBlock old = this.successors.get(index);
		this.successors.set(index, block);
		return old;
	}
	public SootBlock getSuccessor(int index) {return this.successors.get(index);}
	public Iterable<? extends SootBlock> successors() {
		return Collections.unmodifiableList(this.successors);
	}
}
