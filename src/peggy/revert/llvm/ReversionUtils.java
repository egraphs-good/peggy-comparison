package peggy.revert.llvm;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.instructions.BasicBlock;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.BrInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.TerminatorInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.values.IntegerValue;
import llvm.values.Value;
import llvm.values.VirtualRegister;

/**
 * This class works on a representation that is very close to the final version
 * that will be output to an LLVM file. It will work with the following
 * data structures:
 * 	- a list of BasicBlocks
 * 	- a map from BasicBlocks to List<BasicBlock> that represents the successors
 *  - a map from Handles to VirtualRegisters that will be the assignment
 * @author steppm
 */
public class ReversionUtils {
	public static void addTrailingGotos(
			List<BasicBlock> newblocks, 
			Map<BasicBlock,List<BasicBlock>> successorMap) {
		
		for (BasicBlock bb : newblocks) {
			List<BasicBlock> successors = successorMap.get(bb);
			
			if (bb.getNumInstructions() > 0) {
				Handle last = bb.getLastHandle();
				if (!last.getInstruction().isTerminator()) {
					if (successors.size() != 1)
						throw new IllegalArgumentException("Block does not end with terminator and has !=1 children: " + last.getInstruction());
					
					BasicBlock target = successors.get(0);
					bb.addInstruction(new BrInstruction(target));
				}
			} else {
				if (successors.size() == 0)
					continue;
				if (successors.size() > 1)
					throw new IllegalArgumentException("Block does not end with terminator and has >1 children");
				BasicBlock target = successors.get(0);
				bb.addInstruction(new BrInstruction(target));
			}
		}
	}
	
	
	/**
	 * Removes all the blocks that are not reachable from the start block.
	 */
	public static void pruneUnreachable(BasicBlock start, List<BasicBlock> newblocks) {
		Set<BasicBlock> seen = new HashSet<BasicBlock>();
		LinkedList<BasicBlock> queue = new LinkedList<BasicBlock>();
		queue.add(start);
		
		while (!queue.isEmpty()) {
			BasicBlock next = queue.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			if (next.getNumInstructions() == 0)
				continue;
			Handle last = next.getLastHandle();
			TerminatorInstruction term = last.getInstruction().getTerminatorSelf();
			for (int i = 0; i < term.getNumTargets(); i++) 
				queue.addLast(term.getTarget(i));
		}
		
		newblocks.retainAll(seen);
	}
	

	/**
	 * Inserts a dummy assignment to EVERY variable in the start block.
	 * The instruction will be
	 * 		%V = select i1 true, %ty NULL, %ty NULL
	 * where %ty is the type of %V and NULL is the result of Value.getNullValue(%ty)
	 */
	public static void insertDummyAssignments(BasicBlock start, Map<Handle,VirtualRegister> regmap) {
		Set<VirtualRegister> regs = 
			new HashSet<VirtualRegister>(regmap.values());
		Set<VirtualRegister> already = new HashSet<VirtualRegister>();
		for (int i = 0; i < start.getNumInstructions(); i++) {
			Handle handle = start.getHandle(i);
			if (regmap.containsKey(handle))
				already.add(regmap.get(handle));
		}
		regs.removeAll(already);
		
		for (VirtualRegister v : regs) {
			Value nullvalue = Value.getNullValue(v.getType());
			SelectInstruction select = new SelectInstruction(IntegerValue.TRUE, nullvalue, nullvalue);
			Handle handle = start.insertInstruction(0, select);
			regmap.put(handle, v);
		}
	}

	
	
	
	/**
	 * Removes the extra copy instructions that are added as
	 * an artifact of reversion:
	 * 
	 * 		I: %a = select i1 true, %reg, %reg
	 * 		remove I, replace all uses of %a with uses of %reg
	 * 
	 *		I: %a = or i1 true, true
	 * 		remove I, replace uses of %a with uses of i1(1)=true
	 * 
	 * This function assumes SSA form.		
	 */	
	public static void removeSpuriousCopies(List<BasicBlock> newblocks, Map<Handle,VirtualRegister> regmap) {
		Value TRUE = IntegerValue.TRUE;
		Value FALSE = IntegerValue.FALSE;
		
		Map<Value,Value> old2new = new HashMap<Value,Value>();

		// find redundant instructions and remove them
		for (BasicBlock block : newblocks) {
			for (int i = 0; i < block.getNumInstructions(); i++) {
				Handle handle = block.getHandle(i);
				if (handle.getInstruction().isSelect()) {
					SelectInstruction select = handle.getInstruction().getSelectSelf();
					if (select.getCondition().equals(TRUE)) {
						VirtualRegister reg = regmap.remove(handle);
						block.removeInstruction(i);
						i--;
						if (reg != null)
							old2new.put(reg, select.getTrueValue());
					} else if (select.getCondition().equals(FALSE)) {
						VirtualRegister reg = regmap.remove(handle);
						block.removeInstruction(i);
						i--;
						if (reg != null)
							old2new.put(reg, select.getFalseValue());
					}
				} else if (handle.getInstruction().isBinop()) {
					BinopInstruction binop = handle.getInstruction().getBinopSelf();
					if (binop.getBinop().equals(Binop.Or) &&
							binop.getLHS().equals(TRUE) &&
							binop.getRHS().equals(TRUE)) {
						VirtualRegister reg = regmap.remove(handle);
						block.removeInstruction(i);
						i--;
						if (reg != null)
							old2new.put(reg, TRUE);
					}
				}
			}
		}
		
		if (old2new.size() == 0)
			return;

		// patch instructions
		for (BasicBlock block : newblocks) {
			for (int i = 0; i < block.getNumInstructions(); i++) {
				Handle handle = block.getHandle(i);
				Instruction inst = handle.getInstruction().rewrite(old2new);
				if (inst != handle.getInstruction()) {
					VirtualRegister reg = regmap.remove(handle);
					block.removeInstruction(i);
					Handle newhandle = block.insertInstruction(i, inst);
					if (reg != null)
						regmap.put(newhandle, reg);
				}
			}
		}
	}
	
	
	/**
	 * Removes instructions that have assignments which are never used,
	 * assuming the instruction is not sigma-altering.
	 */
	public static void removeDeadAssignments(List<BasicBlock> newblocks, Map<Handle,VirtualRegister> regmap) {
		for (boolean progress = true; progress; ) {
			progress = false;

			Set<VirtualRegister> used = getUsedRegisters(newblocks);
			Set<VirtualRegister> canremove = new HashSet<VirtualRegister>();
			
			varloop:
			for (VirtualRegister v : regmap.values()) {
				if (!used.contains(v)) {
					// check that all of its assignments are non-sigma-altering
					for (Handle handle : regmap.keySet()) {
						if (regmap.get(handle).equals(v)) {
							if (altersSigma(handle.getInstruction()))
								continue varloop;
						}
					}
					
					// know that assignments are not used and do not change sigma, can remove v
					canremove.add(v);
				}
			}
			

			if (canremove.size() > 0) {
				// remove them
				for (BasicBlock bb : newblocks) {
					for (int i = 0; i < bb.getNumInstructions(); i++) {
						Handle handle = bb.getHandle(i);
						if (regmap.containsKey(handle) && canremove.contains(regmap.get(handle))) { 
							// remove!
							regmap.remove(handle);
							bb.removeInstruction(i);
							i--;
							progress = true;
						}
					}
				}
			}
		}
	}
	
	private static boolean altersSigma(Instruction inst) {
		if (inst.isTerminator()) { 
			TerminatorInstruction term = inst.getTerminatorSelf();
			if (term.isRet() ||
					term.isBr() ||
					term.isSwitch() ||
					term.isUnreachable()) {
				return false;
			} 
			else if (term.isUnwind() ||
					term.isInvoke()) {
				return true;
			}
			else
				throw new IllegalArgumentException("Mike forgot to handle: " + term.getClass());
		} else if (inst.isBinop() || 
				inst.isCast() || 
				inst.isShuffleVec() ||
				inst.isInsertElt() ||
				inst.isGEP() ||
				inst.isSelect() ||
				inst.isExtractElt() ||
				inst.isCmp() ||
				inst.isPhi() ||
				inst.isGetResult() ||
				inst.isLoad()) {
			return false;
		} 
		else if (inst.isMalloc() ||
				inst.isFree() ||
				inst.isAlloca() ||
				inst.isStore() ||
				inst.isCall() ||
				inst.isVaarg()) {
			return true;
		}
		else 
			throw new IllegalArgumentException("Mike forgot to handle: " + inst.getClass()); 
	}

	private static Set<VirtualRegister> getUsedRegisters(List<BasicBlock> newblocks) {
		Set<VirtualRegister> result = new HashSet<VirtualRegister>();
		
		for (BasicBlock bb : newblocks) {
			for (int i = 0; i < bb.getNumInstructions(); i++) {
				Instruction inst = bb.getInstruction(i);
				Set<Value> allvalues = new HashSet<Value>();
				LinkedList<Value> queue = new LinkedList<Value>();
				for (Iterator<? extends Value> iter = inst.getValues(); iter.hasNext(); ) {
					queue.add(iter.next());
				}
				
				// flatten
				while (!queue.isEmpty()) {
					Value next = queue.removeFirst();
					if (allvalues.contains(next))
						continue;
					allvalues.add(next);
					if (next.isRegister())
						result.add(next.getRegisterSelf());
					for (Iterator<? extends Value> iter = next.getSubvalues(); iter.hasNext(); ) {
						queue.addLast(iter.next());
					}
				}
			}
		}
		
		return result;
	}
	
	
	public static void toDot(
			PrintStream out, 
			List<BasicBlock> newblocks, 
			Map<Handle,VirtualRegister> regmap,
			Map<BasicBlock,List<BasicBlock>> successorMap) {
		out.println("digraph {");
		for (BasicBlock bb : newblocks) {
			out.print(bb.hashCode());
			out.print(" [shape=rect, label=\"Block " + bb + "\\n");
			
			for (int i = 0; i < bb.getNumInstructions(); i++) {
				Handle handle = bb.getHandle(i);
				if (regmap.containsKey(handle)) {
					out.print(regmap.get(handle) + " = ");
				}
				out.print(handle.getInstruction());
				out.print("\\n");
			}
			out.println("\"]; ");

			int numSuccs = successorMap.get(bb).size();
			for (int i = 0; i < numSuccs; i++) {
				out.println(bb.hashCode() + " -> " + successorMap.get(bb).get(i).hashCode() + "; ");
			}
		}
		out.println("}");
	}
	
}

