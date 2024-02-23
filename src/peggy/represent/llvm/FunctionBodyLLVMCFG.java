package peggy.represent.llvm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.LLVMUtils;
import llvm.bitcode.ReferenceResolver;
import llvm.instructions.BasicBlock;
import llvm.instructions.BrInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.Instruction;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.PhiInstruction;
import llvm.instructions.RegisterAssignment;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.SwitchInstruction;
import llvm.instructions.TerminatorInstruction;
import llvm.instructions.UnwindInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.values.FunctionValue;
import llvm.values.IntegerValue;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import util.pair.Pair;

/**
 * This is an LLVMCFG that builds itself from a FunctionBody instance.
 */
public class FunctionBodyLLVMCFG extends LLVMCFG {
	public FunctionBodyLLVMCFG(
			LLVMOpAmbassador _ambassador,
			FunctionBody body,
			ReferenceResolver _resolver) {
		super(_ambassador, _resolver);
		
		if (LLVMUtils.containsLabelParameters(body) || 
			LLVMUtils.containsIndirectBranches(body))
			throw new IllegalArgumentException("Body contains label parameters to call");

		this.buildBlocks(body);

		FunctionValue header = body.getHeader();
		String functionName = getResolver().getFunctionName(header);
		for (int i = 0; i < header.getNumArguments(); i++) {
			ArgumentLLVMVariable arg = new ArgumentLLVMVariable(
					new FunctionLLVMLabel(
							header.getType().getPointeeType().getFunctionSelf(),
							functionName),
							header.getArgument(i).getIndex(),
							header.getArgument(i).getType());
			this.variables.add(arg);
		}
		
		pruneUnreachable();
	}
	
	/**
	 * This must be done because a PEG panics when it sees
	 * unreachable blocks.
	 */
	private void pruneUnreachable() {
		Set<LLVMBlock> reachable = new HashSet<LLVMBlock>();
		LinkedList<LLVMBlock> queue = new LinkedList<LLVMBlock>();
		queue.add(this.start);
		
		while (!queue.isEmpty()) {
			LLVMBlock next = queue.removeFirst();
			if (reachable.contains(next))
				continue;
			reachable.add(next);
			
			if (this.successorMap.containsKey(next)) {
				queue.addAll(this.successorMap.get(next));
			}
		}
		
		List<LLVMBlock> unreachable = new ArrayList<LLVMBlock>(this.vertices);
		unreachable.removeAll(reachable);
		
		this.vertices.removeAll(unreachable);
		for (LLVMBlock block : unreachable) {
			this.successorMap.remove(block);
		}
	}
	
	
	private void buildBlocks(FunctionBody body) {
		// first pass: make the new blocks, blockmap, and register2variable
		Map<BasicBlock,LLVMBlock> blockmap = new HashMap<BasicBlock,LLVMBlock>();
		for (int i = 0; i < body.getNumBlocks(); i++) {
			LLVMBlock block = newBlock();
			blockmap.put(body.getBlock(i), block);
		}
		RegisterAssignment assignment = body.getRegisterAssignment();
		for (VirtualRegister reg : assignment.getRegisters()) {
			newRegisterVariable(reg);
		}
		
		// assign the start and end blocks
		this.start = blockmap.get(body.getStart());
		this.end = newBlock();
		
		// second pass: fill blocks with CFGInstructions
		for (int i = 0; i < body.getNumBlocks(); i++) {
			BasicBlock oldbb = body.getBlock(i);
			LLVMBlock newbb = blockmap.get(oldbb);
			
			for (int j = 0; j < oldbb.getNumInstructions(); j++) {
				Handle handle = oldbb.getHandle(j);
				Instruction inst = handle.getInstruction();

				if (inst.isTerminator()) {
					TerminatorInstruction term = inst.getTerminatorSelf();
					if (term.isBr()) {
						BrInstruction br = term.getBrSelf();
						if (br.getCondition() == null) {
							// goto, add nothing
							this.successorMap.put(
									newbb, 
									Arrays.asList(blockmap.get(br.getTrueTarget())));
						} else {
							// if
							newbb.addInstruction(new IfCFGInstruction(br.getCondition())); 
							this.successorMap.put(
									newbb, 
									Arrays.asList(
											blockmap.get(br.getTrueTarget()),
											blockmap.get(br.getFalseTarget())));
						}
					} else if (term.isSwitch()) {
						// replace with cascading if's
						// temporarily just leave these in
						// we have to remove the phis before we can remove switches
						List<Pair<IntegerValue,LLVMBlock>> pairs = 
							new ArrayList<Pair<IntegerValue,LLVMBlock>>();
						SwitchInstruction switchinst = term.getSwitchSelf();
						List<LLVMBlock> succs = new ArrayList<LLVMBlock>();
						for (int m = 0; m < switchinst.getNumCaseLabels(); m++) {
							IntegerValue iv = switchinst.getCaseLabel(m);
							BasicBlock target = switchinst.getCaseTarget(m);
							pairs.add(new Pair<IntegerValue,LLVMBlock>(iv, blockmap.get(target)));
							succs.add(blockmap.get(target));
						}

						newbb.addInstruction(new SwitchCFGInstruction(
								switchinst.getInputValue(),
								blockmap.get(switchinst.getDefaultTarget()),
								pairs));
						this.successorMap.put(newbb, succs);
						
						// removeswitch will add to the successorMap and the switch block and the register map
					} else if (term.isInvoke()) {
						// check for assigned reg, if not create one
						InvokeInstruction invoke = term.getInvokeSelf();
						
						LLVMVariable var;
						if (assignment.isAssigned(handle)) {
							var = this.register2variable.get(assignment.getRegister(handle));
						} else if (!inst.getType().isVoid()) {
							VirtualRegister reg = VirtualRegister.getVirtualRegister(inst.getType());
							RegisterLLVMVariable regvar = newRegisterVariable(reg);
							var = regvar;
						} else {
							// void!
							var = LLVMVariable.getDummyVariable();
							this.variables.add(var);
						}

						CFGInstruction cfginvoke = new SimpleCFGInstruction(inst);
						newbb.addInstruction(cfginvoke);
						newbb.putAssignment(cfginvoke, var);
						
						// this will get overridden later (maybe!)
						this.successorMap.put(
								newbb, 
								Arrays.asList(
										blockmap.get(invoke.getUnwindBlock()),  // true
										blockmap.get(invoke.getReturnBlock())));// false
					} else if (term.isRet()) {
						RetInstruction ret = term.getRetSelf();
						SimpleCFGInstruction cfgret = new SimpleCFGInstruction(ret);
						newbb.addInstruction(cfgret);
						newbb.putAssignment(cfgret, LLVMVariable.RETURN);
						
						this.successorMap.put(newbb, Arrays.asList(this.end));
					} else if (term.isUnwind() || term.isUnreachable()) {
						SimpleCFGInstruction unwindcfg = new SimpleCFGInstruction(UnwindInstruction.INSTANCE);
						newbb.addInstruction(unwindcfg);
						newbb.putAssignment(unwindcfg, LLVMVariable.RETURN);

						ExtractExceptionCFGInstruction extract = 
							new ExtractExceptionCFGInstruction(LLVMVariable.RETURN);
						newbb.addInstruction(extract);
						newbb.putAssignment(extract, LLVMVariable.RETURN);
						
						this.successorMap.put(newbb, Arrays.asList(this.end));
					} else {
						throw new RuntimeException("Mike forgot to handle: " + inst);
					}
				} else if (inst.isCall()) {
					// call instruction 
					// at this point, just make sure that this call has a variable associated with it
					SimpleCFGInstruction callcfg = new SimpleCFGInstruction(inst);
					
					LLVMVariable var;
					if (!assignment.isAssigned(handle)) {
						var = LLVMVariable.getDummyVariable();
						this.variables.add(var);
					} else {
						var = this.register2variable.get(assignment.getRegister(handle));
					}

					newbb.addInstruction(callcfg);
					newbb.putAssignment(callcfg, var);
					// later pass will break up this block right here and put in new stuff
				} else if (inst.isVaarg() || inst.isMalloc() || inst.isAlloca() || inst.isLoad()) {
					// these guys all need ExtractValueCFGInstructions after them
					SimpleCFGInstruction instcfg = new SimpleCFGInstruction(inst);
					newbb.addInstruction(instcfg);
					
					if (assignment.isAssigned(handle)) {
						LLVMVariable var = this.register2variable.get(assignment.getRegister(handle));
						newbb.putAssignment(instcfg, var);
						
						ExtractValueCFGInstruction extract = new ExtractValueCFGInstruction(var);
						newbb.addInstruction(extract);
						newbb.putAssignment(extract, var);
					}
				} else {
					// not a terminator nor call
					SimpleCFGInstruction simple = new SimpleCFGInstruction(inst);
					newbb.addInstruction(simple);
					
					// check for register assignments
					if (assignment.isAssigned(handle)) {
						VirtualRegister reg = assignment.getRegister(handle);
						newbb.putAssignment(simple, this.register2variable.get(reg));
					}
				}
			}
		}
		
		cleanUpPhis(blockmap);
		cleanUpSwitches();
		cleanUpInvokes(blockmap);
		if (this.ambassador.hasExceptions())
			cleanUpCalls(blockmap);
		else
			simpleCleanUpCalls(blockmap);
		
		// ensure that every block has a successor list
		for (LLVMBlock block : this.vertices) {
			if (!this.successorMap.containsKey(block))
				this.successorMap.put(block, new ArrayList<LLVMBlock>());
		}
	}


	
	private void updateBlockSuccessors(LLVMBlock pred, LLVMBlock oldsucc, LLVMBlock newsucc) {
		if (pred.getNumInstructions() > 0 && pred.getLastInstruction().isSwitch()) {
			SwitchCFGInstruction inst = pred.getLastInstruction().getSwitchSelf();
			for (int i = 0; i < inst.getNumPairs(); i++) {
				if (inst.getPair(i).getSecond().equals(oldsucc))
					inst.setPair(i, inst.getPair(i).getFirst(), newsucc);
			}
			if (inst.getDefaultBlock().equals(oldsucc))
				inst.setDefaultBlock(newsucc);
		}
		
		
		List<LLVMBlock> succs = this.successorMap.get(pred);
		for (int i = 0; i < succs.size(); i++) {
			if (succs.get(i).equals(oldsucc))
				succs.set(i, newsucc);
		}
	}
	
	
	private void cleanUpPhis(Map<BasicBlock,LLVMBlock> blockmap) {
		// clean up the phis (this must come before cleanUpInvokes and cleanUpCalls!)
		int size = this.vertices.size();
		for (int i = 0; i < size; i++) {
			LLVMBlock next = this.vertices.get(i);
			if (next.getNumInstructions() == 0)
				continue;
			
			// gather and remove phis and their assignments
			List<Pair<LLVMVariable,PhiInstruction>> phis = 
				new ArrayList<Pair<LLVMVariable,PhiInstruction>>();
			for (int p = 0; p < next.getNumInstructions(); p++) {
				if (next.getInstruction(p).isSimple()) {
					SimpleCFGInstruction simple = next.getInstruction(p).getSimpleSelf();
					if (simple.getInstruction().isPhi()) {
						LLVMVariable assign = next.getAssignment(simple);
						phis.add(new Pair<LLVMVariable,PhiInstruction>(assign, simple.getInstruction().getPhiSelf()));
						next.removeAssignment(simple);
						next.removeInstruction(p);
						p--;
					}
				}
			}
			if (phis.size() == 0)
				continue;
			
			// have phis, gather all the preds and make select blocks
			Map<LLVMBlock,LLVMBlock> pred2selectblock = 
				new HashMap<LLVMBlock,LLVMBlock>();

			for (Pair<LLVMVariable,PhiInstruction> phi : phis) {
				for (int p = 0; p < phi.getSecond().getNumPairs(); p++) {
					Pair<? extends Value,BasicBlock> pair = phi.getSecond().getPair(p);
					
					LLVMBlock pred = blockmap.get(pair.getSecond());
					LLVMBlock selectblock = pred2selectblock.get(pred);
					LLVMBlock setblock;
					if (selectblock == null) {
						selectblock = newBlock();
						setblock = newBlock();
						
						this.successorMap.put(selectblock, Arrays.asList(setblock));
						this.successorMap.put(setblock, Arrays.asList(next));
						
						pred2selectblock.put(pred, selectblock);
						updateBlockSuccessors(pred, next, selectblock);
					} else {
						setblock = selectblock.getChild(0);
					}

					LLVMVariable tempvar = LLVMVariable.getDummyVariable();
					this.variables.add(tempvar);
					
					SimpleCFGInstruction select = 
						new SimpleCFGInstruction(new SelectInstruction(
							IntegerValue.TRUE,
							pair.getFirst(),
							pair.getFirst()));
					selectblock.addInstruction(select);
					selectblock.putAssignment(select, tempvar);
					
					CopyCFGInstruction cfg = new CopyCFGInstruction(tempvar);
					setblock.addInstruction(cfg);
					if (phi.getFirst() != null)
						setblock.putAssignment(cfg, phi.getFirst());
				}
			}
		}
	}

	
	private void cleanUpSwitches() {
		List<LLVMBlock> switches = new ArrayList<LLVMBlock>();
		for (LLVMBlock block : this.vertices) {
			if (block.getNumInstructions() > 0 && block.getLastInstruction().isSwitch()) {
				switches.add(block);
			}
		}
		
		// split up to avoid ConcurrentModificationException
		for (LLVMBlock switchblock : switches) {
			SwitchCFGInstruction inst = switchblock.getLastInstruction().getSwitchSelf();
			switchblock.removeInstruction(switchblock.getNumInstructions()-1);
			removeSwitch(inst, switchblock);
		}
	}
	
	private void cleanUpInvokes(Map<BasicBlock,LLVMBlock> blockmap) {
		// clean up the invokes
		int size = this.vertices.size();

		for (int i = 0; i < size; i++) {
			LLVMBlock block = this.vertices.get(i);
			if (block.getNumInstructions() == 0)
				continue;
			if (!block.getLastInstruction().isSimple())
				continue;
			SimpleCFGInstruction simple = block.getLastInstruction().getSimpleSelf();
			if (!(simple.getInstruction().isTerminator() && simple.getInstruction().getTerminatorSelf().isInvoke()))
				continue;
			InvokeInstruction invoke = simple.getInstruction().getTerminatorSelf().getInvokeSelf();

			LLVMVariable var = block.getAssignment(simple);
			block.addInstruction(new IfExceptionCFGInstruction(var));
			
			List<LLVMBlock> succs = this.successorMap.get(block); 
			// succs[0] = unwind
			// succs[1] = return
			
			if (!invoke.getType().isVoid()) {
				// make the extractvalue block
				LLVMBlock extractValueBlock = newBlock();
				ExtractValueCFGInstruction extract = new ExtractValueCFGInstruction(var);
				extractValueBlock.addInstruction(extract);
				extractValueBlock.putAssignment(extract, var);
				
				this.successorMap.put(
						extractValueBlock,
						Arrays.asList(succs.get(1)));// return block
				
				// set the new block's successors
				this.successorMap.put(
						block, 
						Arrays.asList(
								succs.get(0),// unwind block
								extractValueBlock));
			}
		}
	}
	

	/**
	 * This is a method to use to clean up the call instructions
	 * when exceptional control flow is not taken into account.
	 */
	private void simpleCleanUpCalls(Map<BasicBlock,LLVMBlock> blockmap) {
		// clean up the calls
		LinkedList<LLVMBlock> unprocessed = new LinkedList<LLVMBlock>(this.vertices);
		while (unprocessed.size() > 0) {
			LLVMBlock block = unprocessed.removeFirst();
			for (int i = 0; i < block.getNumInstructions(); i++) {
				CFGInstruction cfg = block.getInstruction(i);
				if (!cfg.isSimple()) continue;
				SimpleCFGInstruction simple = cfg.getSimpleSelf();
				if (!simple.getInstruction().isCall()) continue;
				// found a call, split the block

				LLVMVariable callvar = block.getAssignment(cfg);
				ExtractValueCFGInstruction value = new ExtractValueCFGInstruction(callvar);
				block.insertInstruction(i+1, value);
				block.putAssignment(value, callvar);
				continue;
			}
		}
	}

	
	private void cleanUpCalls(Map<BasicBlock,LLVMBlock> blockmap) {
		// clean up the calls
		LinkedList<LLVMBlock> unprocessed = new LinkedList<LLVMBlock>(this.vertices);
		while (unprocessed.size() > 0) {
			LLVMBlock block = unprocessed.removeFirst();
			for (int i = 0; i < block.getNumInstructions(); i++) {
				CFGInstruction cfg = block.getInstruction(i);
				if (!cfg.isSimple()) continue;
				SimpleCFGInstruction simple = cfg.getSimpleSelf();
				if (!simple.getInstruction().isCall()) continue;
				// found a call, split the block
				
				LLVMBlock bottom = newBlock();

				// move instructions from i+1-->n to bottom block
				// move assignments too
				while (block.getNumInstructions() > i+1) {
					CFGInstruction movecfg = block.removeInstruction(i+1);
					if (block.hasVariable(movecfg)) {
						LLVMVariable movevar = block.getAssignment(movecfg);
						block.removeAssignment(movecfg);
						bottom.addInstruction(movecfg);
						bottom.putAssignment(movecfg, movevar);
					} else {
						bottom.addInstruction(movecfg);
					}
				}
				
				// update successor map
				{
					List<LLVMBlock> succs = this.successorMap.remove(block);
					if (succs == null)
						succs = new ArrayList<LLVMBlock>();
					this.successorMap.put(bottom, succs);
				}
				
				// add the IfException instruction
				LLVMVariable callvar = block.getAssignment(cfg);
				block.addInstruction(new IfExceptionCFGInstruction(callvar));
				
				// make the extract value block
				LLVMBlock extractValueBlock = newBlock();
				{
					ExtractValueCFGInstruction value = new ExtractValueCFGInstruction(callvar);
					extractValueBlock.addInstruction(value);
					extractValueBlock.putAssignment(value, callvar);
					this.successorMap.put(extractValueBlock, 
							Arrays.asList(bottom));
				}
				
				// make the extract exception block
				LLVMBlock extractExceptionBlock = newBlock();
				{
					ExtractExceptionCFGInstruction exception = new ExtractExceptionCFGInstruction(callvar);
					extractExceptionBlock.addInstruction(exception);
					extractExceptionBlock.putAssignment(exception, LLVMVariable.RETURN);
					this.successorMap.put(extractExceptionBlock, 
							Arrays.asList(this.end));
				}
				
				// set the successors of the call block
				this.successorMap.put(block, 
						Arrays.asList(
								extractExceptionBlock,	// ifexception=true
								extractValueBlock));  	// ifexception=false

				// continue processing bottom
				unprocessed.addLast(bottom);
				break;
			}
		}
	}
	


	/**
	 * Assumes the given block ends with a SwitchCFGInstruction,
	 * which has just been removed and passed in.
	 * Replaces the switch with a chain of branches.
	 */
	private void removeSwitch(SwitchCFGInstruction SWITCH, LLVMBlock switchBlock) {
		if (SWITCH.getNumPairs() == 0) {
			// replace with an unconditional branch
			LLVMBlock defaultTarget = SWITCH.getDefaultBlock();
			this.successorMap.put(switchBlock, Arrays.asList(defaultTarget));
			return;
		}

		// at least one case label
		// list = [switchBlock, case2test, ..., caseNtest, defaultBlock]
		
		List<LLVMBlock> list = new ArrayList<LLVMBlock>(SWITCH.getNumPairs()+3);
		list.add(switchBlock);
		for (int i = 1; i < SWITCH.getNumPairs(); i++) {
			LLVMBlock newblock = newBlock();
			list.add(newblock);
		}
		list.add(SWITCH.getDefaultBlock());

		// put test code into list[i], branches to either SWITCH.target[i] or list[i+1]
		for (int i = 0; i < SWITCH.getNumPairs(); i++) {
			LLVMBlock branchBlock = list.get(i);
			LLVMBlock nextBlock = list.get(i+1);

			CmpInstruction cmp = new CmpInstruction(
					IntegerComparisonPredicate.ICMP_EQ, 
					SWITCH.getValue(), 
					SWITCH.getPair(i).getFirst());
			SimpleCFGInstruction simplecmp = new SimpleCFGInstruction(cmp);
			VirtualRegister reg = VirtualRegister.getVirtualRegister(cmp.getType());
			RegisterLLVMVariable var = newRegisterVariable(reg);
			IfCFGInstruction ifinst = new IfCFGInstruction(reg);

			branchBlock.addInstruction(simplecmp);
			branchBlock.addInstruction(ifinst);
			branchBlock.putAssignment(simplecmp, var);

			this.successorMap.put(
					branchBlock, 
					Arrays.asList(
							SWITCH.getPair(i).getSecond(),	// true block 
							nextBlock));					// false block
		}
	}
}
