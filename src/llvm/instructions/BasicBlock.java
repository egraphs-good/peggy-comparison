package llvm.instructions;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents an LLVM basic block. This lives inside of a FunctionBody
 * and contains Instructions.
 */
public class BasicBlock implements Iterable<BasicBlock.Handle> {
	public class Handle {
		protected final Instruction instruction;
		protected Handle(Instruction _inst) {
			this.instruction = _inst;
		}

		public Instruction getInstruction() {return this.instruction;}
		public BasicBlock getParent() {return BasicBlock.this;}
		public Handle getNext() {
			int index = BasicBlock.this.handles.indexOf(this);
			if (index < 0)
				throw new IllegalArgumentException("Handle has been removed from its basic block");
			if (index+1 >= BasicBlock.this.handles.size())
				return null;
			else
				return BasicBlock.this.handles.get(index+1);
		}
		public Handle getPrev() {
			int index = BasicBlock.this.handles.indexOf(this);
			if (index < 0)
				throw new IllegalArgumentException("Handle has been removed from its basic block");
			if (index == 0)
				return null;
			else
				return BasicBlock.this.handles.get(index-1);
		}
	}
	
	protected final LinkedList<Handle> handles;
	protected String name;
	
	public BasicBlock() {
		this.handles = new LinkedList<Handle>();
	}
	
	public String getName() {return this.name;}
	public void setName(String _name) {this.name = _name;}
	
	public void verify() {
		if (this.handles.size() == 0)
			throw new IllegalStateException("No instructions in block");
		for (int i = 0; i < this.handles.size()-1; i++) {
			if (this.handles.get(i).getInstruction().isTerminator())
				throw new IllegalStateException("Interior instruction cannot be terminator");
		}
		if (!this.handles.getLast().getInstruction().isTerminator())
			throw new IllegalStateException("End instruction is not a terminator");
	}
	
	/**
	 * Returns an unmodifiable iterator
	 */
	public Iterator<Instruction> instructionIterator() {
		final Iterator<Handle> handleIter = this.handles.iterator();
		return new Iterator<Instruction>() {
			public boolean hasNext() {return handleIter.hasNext();}
			public Instruction next() {return handleIter.next().getInstruction();}
			public void remove() {throw new UnsupportedOperationException();}
		};
	}
	public Iterator<Handle> handleIterator() {
		return Collections.unmodifiableList(this.handles).iterator();
	}
	
	/**
	 * Alias for handleIterator.
	 */
	public Iterator<Handle> iterator() {return this.handleIterator();}
	
	public Handle addInstruction(Instruction inst) {
		Handle handle = new Handle(inst);
		this.handles.add(handle);
		return handle;
	}
	public int getNumInstructions() {return this.handles.size();}
	public Handle getHandle(int i) {return this.handles.get(i);}
	public Instruction getInstruction(int i) {return this.handles.get(i).getInstruction();}
	public Handle insertInstruction(int index, Instruction inst) {
		Handle handle = new Handle(inst);
		this.handles.add(index, handle);
		return handle;
	}
	public Handle removeInstruction(int i) {
		return this.handles.remove(i);
	}
	public Handle getLastHandle() {return this.handles.getLast();}
	public Handle getFirstHandle() {return this.handles.getFirst();}

	public int getNumSuccs() {
		return this.handles.getLast().getInstruction().getTerminatorSelf().getNumTargets();
	}
	public BasicBlock getSucc(int i) {return this.handles.getLast().getInstruction().getTerminatorSelf().getTarget(i);}
}
