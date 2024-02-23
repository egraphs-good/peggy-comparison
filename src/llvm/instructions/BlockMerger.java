package llvm.instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import llvm.instructions.BasicBlock.Handle;
import llvm.values.IntegerValue;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import util.pair.Pair;

/**
 * This class takes in a FunctionBody and 
 * attempts to merge adjacent basic blocks into 
 * larger basic blocks. This simplifies the CFG
 * and reduces memory overhead.
 * 
 * @author steppm
 */
public class BlockMerger {
	protected final FunctionBody body;
	
	public BlockMerger(FunctionBody _body) {
		this.body = _body;
	}
	
	/**
	 * Call this method to perform the block merging.
	 */
	public void mergeBlocks() {
		toploop:
		while (true) {
			for (int i = 0; i < this.body.getNumBlocks(); i++) {
				BasicBlock block = this.body.getBlock(i);
				if (block.getNumSuccs() == 1) {
					Instruction inst = block.getLastHandle().getInstruction();
					if (inst.isTerminator() && inst.getTerminatorSelf().isBr()) {
						BrInstruction br = inst.getTerminatorSelf().getBrSelf();
						if (tryToMerge(block))
							continue toploop;
						else if (block.getNumInstructions() == 1 &&
								 br.getCondition() == null &&
								 !this.body.getStart().equals(block) &&
								 tryToRemove(i, block)) {
							continue toploop;
						}
						
					}
				}
			}
			break;
		}
	}
	
	
	/**
	 * Precondtions: block = {br label %succBlock}
	 * 
	 * This method will try to remove block if it has exactly 1
	 * predecessor. In this case, it will update all branch targets
	 * that named block to now name succBlock. All phi instructions
	 * that named block will name block's sole predecessor. 
	 *
	 * @return true if any changes were made
	 */
	private boolean tryToRemove(int blockIndex, BasicBlock block) {
		int predIndex = -1;
		int predCount = 0;
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock pred = this.body.getBlock(i);
			for (int j = 0; j < pred.getNumSuccs(); j++) {
				if (pred.getSucc(j).equals(block)) {
					predCount++;
					predIndex = i;
					break;
				}
			}
		}
		if (predCount != 1)
			return false;
		
		// block has one predecessor, can remove it
		BasicBlock pred = this.body.getBlock(predIndex);
		BasicBlock succ = block.getLastHandle().getInstruction().getTerminatorSelf().getBrSelf().getTrueTarget();
		
		// ensure that no phi block names both 'block' and 'pred'
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock bb = this.body.getBlock(i);
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				if (!inst.isPhi()) continue;

				PhiInstruction phi = inst.getPhiSelf();
				boolean hasBlock = false;
				boolean hasSucc = false;
				for (int k = 0; k < phi.getNumPairs(); k++) {
					Pair<? extends Value,BasicBlock> pair = phi.getPair(k);
					if (pair.getSecond().equals(block)) 
						hasBlock = true;
					if (pair.getSecond().equals(pred))
						hasSucc = true;
				}

				if (hasBlock && hasSucc)
					return false;
			}
		}
		
		// replace all branch targets with succ,
		// replace all phi targets with pred
		
		this.body.removeBlock(blockIndex);
		this.globalReplace(block, succ, pred);
		
		return true;
	}
	
	
	/**
	 * Precondition: block must have exactly 1 successor
	 *
	 * @return true iff a merging was performed
	 */
	private boolean tryToMerge(BasicBlock block) {
		BasicBlock succ = block.getSucc(0);
		
		// check to make sure succ has no other predecessors
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock succpred = this.body.getBlock(i);
			for (int j = 0; j < succpred.getNumSuccs(); j++) {
				if (succpred.getSucc(j).equals(succ) && 
					!succpred.equals(block)) {
					return false;
				}
			}
			
			// check that no phi instruction names both 'block' and 'succ'
			for (int j = 0; j < succpred.getNumInstructions(); j++) {
				Instruction inst = succpred.getInstruction(j);
				if (!inst.isPhi()) continue;

				PhiInstruction phi = inst.getPhiSelf();
				boolean hasBlock = false;
				boolean hasSucc = false;
				for (int k = 0; k < phi.getNumPairs(); k++) {
					Pair<? extends Value,BasicBlock> pair = phi.getPair(k);
					if (pair.getSecond().equals(block)) 
						hasBlock = true;
					if (pair.getSecond().equals(succ))
						hasSucc = true;
				}
				
				if (hasBlock && hasSucc)
					return false;
			}
		}

		// succ has no other preds, can safely merge!
		
		// remove block's last instruction (br)
		block.removeInstruction(block.getNumInstructions()-1);
		
		// copy succ instructions into block
		RegisterAssignment assignment = this.body.getRegisterAssignment();
		while (succ.getNumInstructions() > 0) {
			Handle handle = succ.getHandle(0);
			VirtualRegister reg = assignment.remove(handle);
			Instruction inst = handle.getInstruction();
			succ.removeInstruction(0);
			
			if (inst.isPhi()) {
				// phi of one block! silly
				PhiInstruction phi = inst.getPhiSelf();
				if (phi.getNumPairs() != 1)
					throw new IllegalArgumentException("Invalid phi: should only have 1 pair");
				if (!phi.getPair(0).getSecond().equals(block))
					throw new IllegalArgumentException("Invalid phi: does not point to correct predecessor");
				Value value = phi.getPair(0).getFirst();
				SelectInstruction select = 
					new SelectInstruction(IntegerValue.TRUE, value, value);
				inst = select;
			}
			
			handle = block.addInstruction(inst);
			if (reg != null)
				assignment.set(reg, handle);
		}
		
		// remove succ (find its index)
		{
			for (int i = 0; i < this.body.getNumBlocks(); i++) {
				if (this.body.getBlock(i).equals(succ)) {
					this.body.removeBlock(i);
					break;
				}
			}
		}
		
		// update instructions with new blocks
		this.globalReplace(succ, block, block);
		
		return true;
	}
	

	/**
	 * If the given instruction references oldBlock, then a new version of it will be
	 * created and returned that references newBlock instead. This only applies to
	 * 		br, switch, invoke
	 * If the instruction does not reference any basic blocks, or simply does not reference
	 * oldBlock, then the original instruction is returned.
	 */
	public static TerminatorInstruction replaceBB(
			TerminatorInstruction inst, 
			BasicBlock oldBlock, 
			BasicBlock newBlock) {
		TerminatorInstruction term = inst.getTerminatorSelf();
		if (term.isBr()) {
			BrInstruction br = term.getBrSelf();
			boolean changed = false;
			BasicBlock newtrue;
			BasicBlock newfalse = (br.getFalseTarget()!=null && br.getFalseTarget().equals(oldBlock)) ? newBlock : br.getFalseTarget();
			
			if (br.getTrueTarget().equals(oldBlock)) {
				changed = true;
				newtrue = newBlock;
			} else {
				newtrue = br.getTrueTarget();
			}
			if (br.getFalseTarget() != null && br.getFalseTarget().equals(oldBlock)) {
				changed = true;
				newfalse = newBlock;
			} else {
				newfalse = br.getFalseTarget();
			}
			if (!changed)
				return inst;
			else
				return (newfalse==null ? new BrInstruction(newtrue) : new BrInstruction(br.getCondition(), newtrue, newfalse));
		} else if (term.isSwitch()) {
			SwitchInstruction swit = term.getSwitchSelf();
			Map<IntegerValue,BasicBlock> casemap =
				new HashMap<IntegerValue,BasicBlock>();
			boolean changed = false;
			for (int i = 0; i < swit.getNumCaseLabels(); i++) {
				BasicBlock target = swit.getCaseTarget(i);
				if (target.equals(oldBlock)) {
					changed = true;
					casemap.put(swit.getCaseLabel(i), newBlock);
				} else {
					casemap.put(swit.getCaseLabel(i), target);
				}
			}
			BasicBlock newdefault;
			if (swit.getDefaultTarget().equals(oldBlock)) {
				changed = true;
				newdefault = newBlock;
			} else {
				newdefault = swit.getDefaultTarget();
			}
			if (!changed)
				return swit;
			else
				return new SwitchInstruction(swit.getInputValue(), newdefault, casemap);
		} else if (term.isInvoke()) {
			InvokeInstruction invoke = term.getInvokeSelf();
			boolean changed = false;
			BasicBlock newreturn, newunwind;
			if (invoke.getReturnBlock().equals(oldBlock)) {
				changed = true;
				newreturn = newBlock;
			} else {
				newreturn = invoke.getReturnBlock();
			}
			if (invoke.getUnwindBlock().equals(oldBlock)) {
				changed = true;
				newunwind = newBlock;
			} else {
				newunwind = invoke.getUnwindBlock();
			}
			if (!changed)
				return inst;
			else {
				List<Value> actuals = new ArrayList<Value>();
				for (int i = 0; i < invoke.getNumActuals(); i++) 
					actuals.add(invoke.getActual(i));
				return new InvokeInstruction(
						invoke.getCallingConvention(), 
						invoke.getFunctionPointer(),
						invoke.getParameterAttributeMap(),
						newreturn,
						newunwind,
						actuals);
			}
		} else {
			return inst;
		}
	}
	
	
	/**
	 * If the given phi instruction references oldBlock, then a new version of it will be
	 * created and returned that references newBlock instead. 
	 * If the instruction does not reference oldBlock
	 * then the original instruction is returned.
	 */
	public static PhiInstruction replaceBB(
			PhiInstruction inst,
			BasicBlock oldBlock, 
			BasicBlock newBlock) {
		PhiInstruction phi = inst.getPhiSelf();
		List<Pair<? extends Value,BasicBlock>> newpairs = 
			new ArrayList<Pair<? extends Value,BasicBlock>>();
		boolean changed = false;
		for (int i = 0; i < phi.getNumPairs(); i++) {
			Pair<? extends Value,BasicBlock> pair = phi.getPair(i);
			if (pair.getSecond().equals(oldBlock)) {
				newpairs.add(new Pair<Value,BasicBlock>(pair.getFirst(), newBlock));
				changed = true;
			} else {
				newpairs.add(pair);
			}
		}
		if (!changed)
			return inst;
		else
			return new PhiInstruction(phi.getType(), newpairs);
	}
	

	/**
	 * Replaces references to oldBlock in all the instructions in the function body.
	 * In terminator blocks, any reference to oldBlock will be replaced by a
	 * reference to newTermBlock.
	 * In phi blocks, any reference to oldBlock will be replaced by a reference
	 * to newPhiBlock.
	 */
	private void globalReplace(BasicBlock oldBlock, BasicBlock newTermBlock, BasicBlock newPhiBlock) {
		RegisterAssignment assignment = this.body.getRegisterAssignment();
		
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock updateBlock = this.body.getBlock(i);
			for (int j = 0; j < updateBlock.getNumInstructions(); j++) {
				Handle oldhandle = updateBlock.getHandle(j);
				Instruction newinst;
				if (oldhandle.getInstruction().isTerminator())
					newinst = replaceBB(oldhandle.getInstruction().getTerminatorSelf(), oldBlock, newTermBlock);
				else if (oldhandle.getInstruction().isPhi())
					newinst = replaceBB(oldhandle.getInstruction().getPhiSelf(), oldBlock, newPhiBlock);
				else
					newinst = oldhandle.getInstruction();
				
				if (oldhandle.getInstruction() != newinst) {
					VirtualRegister reg = assignment.remove(oldhandle);
					updateBlock.removeInstruction(j);
					Handle newhandle = updateBlock.insertInstruction(j, newinst);
					if (reg != null)
						assignment.set(reg, newhandle);
				}
			}
		}
	}
}
