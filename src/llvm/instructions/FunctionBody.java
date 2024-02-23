package llvm.instructions;

import java.util.*;

import llvm.values.FunctionValue;
import llvm.values.NamedValueMap;
import llvm.values.Value;

/**
 * This class represents a function body in an LLVM Module.
 * A function body contains basic blocks, a reference to the function header,
 * the virtual register assignment map, and a local value symbol table map.
 */
public class FunctionBody implements NamedValueMap {
	protected final List<BasicBlock> blocks;
	protected final FunctionValue header;
	protected BasicBlock start;
	protected final RegisterAssignment assignment;
	protected final Map<String,Value> valueNameMap;

	public FunctionBody(FunctionValue _header) {
		this.header = _header;
		this.blocks = new ArrayList<BasicBlock>();
		this.assignment = new RegisterAssignment();
		this.valueNameMap = new HashMap<String,Value>();
	}
	
	public FunctionValue getHeader() {return this.header;}
	public RegisterAssignment getRegisterAssignment() {return this.assignment;}
	
	public int getNumBlocks() {return this.blocks.size();}
	public BasicBlock getBlock(int i) {return this.blocks.get(i);}
	public void addBlock(BasicBlock bb) {this.blocks.add(bb);}
	public BasicBlock removeBlock(int i) {return this.blocks.remove(i);}
	public Iterator<BasicBlock> blockIterator() {
		return Collections.unmodifiableList(this.blocks).iterator();
	}
	
	public BasicBlock getStart() {return this.start;}
	public void setStart(BasicBlock bb) {this.start = bb;}
	
	public Set<String> getValueNames() {return Collections.unmodifiableSet(this.valueNameMap.keySet());}
	public Value getValueByName(String name) {return this.valueNameMap.get(name);}
	public void addValueName(String name, Value value) {this.valueNameMap.put(name, value);}
	public Value removeValueName(String name) {return this.valueNameMap.remove(name);}
	public void clearValueNameMap() {this.valueNameMap.clear();}
}
