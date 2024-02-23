package peggy.represent.llvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import llvm.instructions.Instruction;
import llvm.instructions.TerminatorInstruction;
import util.graph.AbstractVertex;

/**
 * This is the block class for LLVMCFG's.
 */
public class LLVMBlock extends AbstractVertex<LLVMCFG, LLVMBlock> 
implements eqsat.Block<LLVMCFG, LLVMBlock, LLVMVariable, LLVMLabel> {
	protected final List<CFGInstruction> instructions;
	protected final LLVMCFG graph;
	protected final Map<CFGInstruction, LLVMVariable> assignmentMap;

	public LLVMBlock(LLVMCFG _graph) {
		this.graph = _graph;
		this.instructions = new ArrayList<CFGInstruction>();
		this.assignmentMap = new HashMap<CFGInstruction,LLVMVariable>();
	}

	public boolean isStart() {return this.graph.getStart().equals(this);}
	public boolean isEnd() {return this.graph.getEnd().equals(this);}
	public List<? extends LLVMBlock> getChildren() {
		return this.graph.successorMap.get(this);
	}
	public LLVMBlock getChild(int childIndex) {
		return this.graph.successorMap.get(this).get(childIndex);
	}
	
	public boolean modifies(LLVMVariable var) {
		boolean modifies = false;
		
		for (CFGInstruction inst : this.instructions) {
			if (inst.isSimple()) {
				Instruction llvm = inst.getSimpleSelf().getInstruction();
				if (llvm.isTerminator()) {
					TerminatorInstruction term = llvm.getTerminatorSelf();
					if (term.isRet()) {
						modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
					} else if (term.isIndirectBR()) {
						throw new RuntimeException("This should never happen");
					} else if (term.isBr()) {
						throw new RuntimeException("This should never happen");
					} else if (term.isSwitch()) {
						throw new RuntimeException("This should never happen");
					} else if (term.isInvoke()) {
						modifies |= var.equals(LLVMVariable.SIGMA) ||
							(this.hasVariable(inst) && var.equals(this.getAssignment(inst)));
					} else if (term.isUnwind()) {
						modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
					} else if (term.isUnreachable()) {
						throw new IllegalArgumentException("Unreachable statements are not allowed");
					} else {
						throw new RuntimeException("Mike forgot to handle: " + term.getClass());
					}
				} else if (llvm.isBinop() ||
						   llvm.isCast() ||
						   llvm.isShuffleVec() ||
						   llvm.isInsertElt() ||
						   llvm.isGEP() ||
						   llvm.isSelect() ||
						   llvm.isExtractElt() ||
						   llvm.isGetResult() ||
						   llvm.isVSelect() || 
						   llvm.isExtractValue() ||
						   llvm.isInsertValue() ||
						   llvm.isShuffleVec2_8() ||
						   llvm.isCmp()) {
					modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
				} else if (llvm.isLoad()) {
					// special rules for load
					modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
					if (getGraph().getOpAmbassador().hasLinearLoads()) {
						modifies |= var.equals(LLVMVariable.SIGMA);
					}
				} else if (llvm.isPhi()) {
					throw new RuntimeException("This shouldn't happen");
				} else if (llvm.isMalloc() ||
						   llvm.isAlloca() ||
						   llvm.isCall() ||
						   llvm.isVaarg()) {
					// modifies sigma and has result
					modifies |= var.equals(LLVMVariable.SIGMA) ||
						(this.hasVariable(inst) && var.equals(this.getAssignment(inst)));
				} else if (llvm.isFree() ||
						   llvm.isStore()) {
					// just modifies sigma
					modifies |= var.equals(LLVMVariable.SIGMA);
				} else {
					throw new RuntimeException("Mike forgot to handle: " + llvm.getClass());
				}
			} else if (inst.isIf()) {
				// skip
			} else if (inst.isIfException()) {
				// skip
			} else if (inst.isExtractException()) {
				modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
			} else if (inst.isExtractValue()) {
				modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
			} else if (inst.isCopy()) {
				modifies |= this.hasVariable(inst) && var.equals(this.getAssignment(inst));
			} else {
				throw new RuntimeException("Mike forgot to handle: " + inst.getClass());
			}
		}
		return modifies;
	}
	
	public int getNumInstructions() {return this.instructions.size();}
	public CFGInstruction getInstruction(int i) {return this.instructions.get(i);}
	public CFGInstruction getLastInstruction() {return this.instructions.get(this.instructions.size()-1);}
	public CFGInstruction getFirstInstruction() {return this.instructions.get(0);}
	public boolean addInstruction(CFGInstruction cfg) {return this.instructions.add(cfg);}
	public void insertInstruction(int index, CFGInstruction cfg) {this.instructions.add(index, cfg);}
	public CFGInstruction removeInstruction(int index) {return this.instructions.remove(index);}
	
	public boolean hasVariable(CFGInstruction cfg) {return this.assignmentMap.containsKey(cfg);}
	public LLVMVariable getAssignment(CFGInstruction cfg) {return this.assignmentMap.get(cfg);}
	public void putAssignment(CFGInstruction inst, LLVMVariable var) {
		if (!this.instructions.contains(inst))
			throw new IllegalArgumentException("Instruction not contained in this block");
		this.assignmentMap.put(inst, var);
	}
	public void removeAssignment(CFGInstruction cfg) {this.assignmentMap.remove(cfg);}
	public Collection<? extends LLVMVariable> getAssignedVariables() {
		return Collections.unmodifiableCollection(this.assignmentMap.values());
	}

	public LLVMCFG getGraph() {return this.graph;}
	public LLVMBlock getSelf() {return this;}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		for (int i = 0; i < this.instructions.size(); i++) {
			buffer.append(this.instructions.get(i));
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
