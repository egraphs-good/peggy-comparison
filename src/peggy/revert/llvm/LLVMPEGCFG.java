package peggy.revert.llvm;

import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.revert.PEGCFG;
import eqsat.CFG;

/**
 * This is an implementation of PEGCFGs for LLVM.
 */
public class LLVMPEGCFG 
extends PEGCFG<LLVMLabel,LLVMParameter,LLVMReturn,Object,LLVMPEGCFG,LLVMPEGCFGBlock> {
	public <G extends CFG<G,B,V,LLVMLabel,LLVMParameter,LLVMReturn>,B extends eqsat.Block<G,B,V,LLVMLabel>,V> 
	LLVMPEGCFG(CFG<G,B,V,LLVMLabel,LLVMParameter,LLVMReturn> graph) {
		super(graph);
	}
	public LLVMPEGCFG getSelf() {return this;}
	protected LLVMPEGCFGBlock makeNewBlock() {return new LLVMPEGCFGBlock(this);}
	protected Object makeNewTemporary() {return new Object();}
}
