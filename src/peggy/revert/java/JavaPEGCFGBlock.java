package peggy.revert.java;

import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.revert.PEGCFGBlock;

/**
 * This is the block class for JavaPEGCFGs.
 */
public class JavaPEGCFGBlock extends 
PEGCFGBlock<JavaLabel,JavaParameter,JavaReturn,Object,JavaPEGCFG,JavaPEGCFGBlock> {
	protected final JavaPEGCFG cfg;
	public JavaPEGCFGBlock(JavaPEGCFG _cfg) {
		this.cfg = _cfg;
	}
	public JavaPEGCFG getCFG() {return this.cfg;}
	public JavaPEGCFGBlock getSelf() {return this;}
	public boolean isHolder() {return false;}
}
