package peggy.revert.llvm;

import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.revert.PEGCFGBlock;

/**
 * This class is the block class for LLVMPEGCFGs.
 */
public class LLVMPEGCFGBlock extends PEGCFGBlock<LLVMLabel,LLVMParameter,LLVMReturn,Object,LLVMPEGCFG,LLVMPEGCFGBlock> { 
	protected final LLVMPEGCFG cfg;
	public LLVMPEGCFGBlock(LLVMPEGCFG _cfg) {
		this.cfg = _cfg;
	}
	public LLVMPEGCFG getCFG() {return this.cfg;}
	public LLVMPEGCFGBlock getSelf() {return this;}
}
