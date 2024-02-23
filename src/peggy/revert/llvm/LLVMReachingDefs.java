package peggy.revert.llvm;

import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.revert.ReachingDefs;
import static peggy.revert.llvm.LLVMReachingDefs.LLVMUse;
import static peggy.revert.llvm.LLVMReachingDefs.LLVMDef;

/**
 * This is an implementation of reachingdefs for LLVM.
 */
public class LLVMReachingDefs extends ReachingDefs<LLVMLabel,LLVMParameter,LLVMReturn,Object,LLVMPEGCFG,LLVMPEGCFGBlock,LLVMReachingDefs,LLVMUse,LLVMDef> {
	public static class LLVMUse extends ReachingDefs.Use<Object,LLVMPEGCFGBlock,LLVMUse> {
		private final LLVMPEGCFGBlock block;
		private final Object variable;
		protected LLVMUse(LLVMPEGCFGBlock _block, Object _variable) {
			this.block = _block;
			this.variable = _variable;
		}
		public LLVMPEGCFGBlock getBlock() {return this.block;}
		public Object getVariable() {return this.variable;}
		public LLVMUse getSelf() {return this;}
		public boolean equals(Object o) {
			if (!(o instanceof LLVMUse))
				return false;
			LLVMUse use = (LLVMUse)o;
			return this.getBlock().equals(use.getBlock()) &&
				this.getVariable().equals(use.getVariable());
		}
		public int hashCode() {
			return this.getBlock().hashCode()*3 + 
				this.getVariable().hashCode()*7;
		}
		public String toString() {
			return "Use[block " + this.getBlock() + ", var " + this.getVariable().hashCode() + "]";
		}
	}
	public static class LLVMDef extends ReachingDefs.Def<Object,LLVMPEGCFGBlock,LLVMDef> {
		private final LLVMPEGCFGBlock block;
		private final Object variable;
		protected LLVMDef(LLVMPEGCFGBlock _block, Object _variable) {
			this.block = _block;
			this.variable = _variable;
		}
		public LLVMPEGCFGBlock getBlock() {return this.block;}
		public Object getVariable() {return this.variable;}
		public LLVMDef getSelf() {return this;}
		public boolean equals(Object o) {
			if (!(o instanceof LLVMDef))
				return false;
			LLVMDef def = (LLVMDef)o;
			return this.getBlock().equals(def.getBlock()) &&
				this.getVariable().equals(def.getVariable());
		}
		public int hashCode() {
			return this.getBlock().hashCode()*3 + 
				this.getVariable().hashCode()*7;
		}
		public String toString() {
			return "Def[block " + this.getBlock() + ", var " + this.getVariable().hashCode() + "]";
		}
	}
	
	public LLVMReachingDefs(LLVMPEGCFG _cfg) {
		super(_cfg);
	}
	
	public LLVMUse getUse(LLVMPEGCFGBlock block, Object var) {
		return new LLVMUse(block, var);
	}
	public LLVMDef getDef(LLVMPEGCFGBlock block, Object var) {
		return new LLVMDef(block, var);
	}
}
