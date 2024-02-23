package peggy.revert.llvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import llvm.instructions.BasicBlock;
import llvm.instructions.Instruction;
import llvm.instructions.PhiInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import peggy.revert.SSAConverter;
import peggy.revert.llvm.LLVMDominatorGraph.Vertex;
import util.Function;
import util.pair.Pair;

/**
 * Version of SSAConverter that applies to LLVM BasicBlocks.
 * Takes a LLVMDominatorGraph and transforms it into SSA form.
 * 
 * @author steppm
 */
public class LLVMSSAConverter extends SSAConverter<LLVMDominatorGraph,Vertex,VirtualRegister> {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMSSAConverter: " + message);
	}
	
	public class LLVMStatement extends Statement {
		protected final int instructionIndex;
		protected final BasicBlock block;

		protected LLVMStatement(int _index, BasicBlock _block) {
			this.instructionIndex = _index;
			this.block = _block;
		}
		private Handle getHandle() {return this.block.getHandle(this.instructionIndex);}
		private Instruction getInstruction() {return this.block.getInstruction(this.instructionIndex);}
		public boolean isPhi() {return this.getInstruction().isPhi();}
		public Iterable<? extends VirtualRegister> getUsedVariables() {
			List<VirtualRegister> used = new ArrayList<VirtualRegister>();
			for (Iterator<? extends Value> iter = this.getInstruction().getValues(); iter.hasNext(); ) {
				Value next = iter.next();
				if (next.isRegister())
					used.add(next.getRegisterSelf());
			}
			return used;
		}
		public Iterable<? extends VirtualRegister> getDefinedVariables() {
			List<VirtualRegister> defs = new ArrayList<VirtualRegister>();
			if (registerMap.containsKey(this.getHandle()))
				defs.add(registerMap.get(this.getHandle()));
			return defs;
		}
		public void replaceOperand(VirtualRegister oldvar, int index) {
			if (index == 0)
				throw new IllegalArgumentException("index must be > 0");
			
			Value replace = (index == 0 ? Value.getNullValue(oldvar.getType()) : renameVar(index, oldvar));
			Instruction newinst = this.getInstruction().rewrite(oldvar, replace);
			replaceMyInstruction(newinst);
		}
		private void replaceMyInstruction(Instruction newinst) {
			if (newinst == this.getInstruction()) return;
			
			Handle handle = this.getHandle();
			VirtualRegister lhs = registerMap.remove(handle);
			
			this.block.removeInstruction(this.instructionIndex);
			handle = this.block.insertInstruction(this.instructionIndex, newinst);
			if (lhs != null)
				registerMap.put(handle, lhs);
		}
		public void replaceDefinition(VirtualRegister oldvar, int index) {
			Handle handle = this.getHandle();
			VirtualRegister lhs = registerMap.get(handle);
			
			if (lhs != null && lhs.equals(oldvar)) {
				registerMap.remove(handle);
				registerMap.put(handle, renameVar(index, oldvar));
			}
		}
		public void replacePhiOperand(
				Vertex node, 
				Function<VirtualRegister,Integer> var2index) {
			if (!this.getInstruction().isPhi())
				throw new UnsupportedOperationException();
			PhiInstruction phi = this.getInstruction().getPhiSelf();
			int index = -1;
			List<Pair<? extends Value,BasicBlock>> newpairs = 
				new ArrayList<Pair<? extends Value,BasicBlock>>();
			for (int i = 0; i < phi.getNumPairs(); i++) {
				Pair<? extends Value,BasicBlock> pair = phi.getPair(i);
				newpairs.add(pair);
				if (pair.getSecond().equals(node.getBlock())) {
					index = i;
				}
			}
			if (index < 0)
				return;
			
			// make sure the right entry is a register
			if (!newpairs.get(index).getFirst().isRegister())
				return;
			
			VirtualRegister oldreg = newpairs.get(index).getFirst().getRegisterSelf();
			int renameIndex = var2index.get(oldreg);
			if (renameIndex < 0)
				return;
			
			if (renameIndex == 0)
				throw new IllegalArgumentException("index cannot be 0");
			
			Value replace = (renameIndex == 0 ? Value.getNullValue(oldreg.getType()) : renameVar(renameIndex, oldreg));
			newpairs.set(index, new Pair<Value,BasicBlock>(replace, node.getBlock()));
			replaceMyInstruction(new PhiInstruction(phi.getType(), newpairs));
		}
	}
	
	protected final Set<VirtualRegister> variables;
	protected final Map<Handle,VirtualRegister> registerMap;
	protected final Map<Pair<VirtualRegister,Integer>,VirtualRegister> renameCache;
	protected final Set<VirtualRegister> toReplace;
	
	public LLVMSSAConverter(
			LLVMDominatorGraph _graph,
			Map<Handle,VirtualRegister> _registerMap) {
		super(_graph);
		this.renameCache = new HashMap<Pair<VirtualRegister,Integer>,VirtualRegister>();
		this.registerMap = _registerMap;
		
		// compute the set of registers
		this.variables = new HashSet<VirtualRegister>();
		for (Vertex v : this.graph.getVertices()) {
			BasicBlock bb = v.getBlock();
			for (int i = 0; i < bb.getNumInstructions(); i++) {
				for (Iterator<? extends Value> iter = bb.getInstruction(i).getValues(); iter.hasNext(); ) {
					Value next = iter.next();
					if (next.isRegister())
						this.variables.add(next.getRegisterSelf());
				}
			}
		}
		this.variables.addAll(this.registerMap.values());
		
		// compute the set of variables that should be SSA-ified
		this.toReplace = new HashSet<VirtualRegister>(this.variables);
	}
	
	protected boolean canReplace(VirtualRegister reg) {
		return this.toReplace.contains(reg);
	}
	
	protected Set<? extends VirtualRegister> getVariables() {
		return Collections.unmodifiableSet(this.variables);
	}
	
	protected Iterable<? extends Statement> getStatements(final Vertex node) {
		return new Iterable<LLVMStatement>() {
			public Iterator<LLVMStatement> iterator() {
				return new Iterator<LLVMStatement>() {
					int index = 0;
					
					public boolean hasNext() {
						return index < node.getBlock().getNumInstructions();
					}
					public LLVMStatement next() {
						if (!hasNext())
							throw new NoSuchElementException();
						return new LLVMStatement(index++, node.getBlock());
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	private VirtualRegister renameVar(int index, VirtualRegister original) {
		Pair<VirtualRegister,Integer> pair = 
			new Pair<VirtualRegister,Integer>(original, index);
		VirtualRegister result = this.renameCache.get(pair);
		if (result == null) {
			result = VirtualRegister.getVirtualRegister(original.getType());
			this.renameCache.put(pair, result);
			this.variables.add(result);
			
			debug("var(" + original + "," + index + ") renamed to " + result);
		}
		return result;
	}
	
	protected void insertPhi(VirtualRegister a, Vertex y) {
		List<Pair<? extends Value,BasicBlock>> pairs = 
			new ArrayList<Pair<? extends Value,BasicBlock>>();
		for (Vertex pred : y.getParents()) {
			pairs.add(new Pair<Value,BasicBlock>(a, pred.getBlock()));
		}
		PhiInstruction phi = new PhiInstruction(a.getType(), pairs);
		Handle handle = y.getBlock().insertInstruction(0, phi);
		registerMap.put(handle, a);
	}
}
