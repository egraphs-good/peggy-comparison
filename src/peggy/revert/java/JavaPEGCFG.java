package peggy.revert.java;

import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.revert.PEGCFG;
import eqsat.CFG;

/**
 * This is the implementation of PEGCFGs for Java.
 */
public class JavaPEGCFG extends 
PEGCFG<JavaLabel,JavaParameter,JavaReturn,Object,JavaPEGCFG,JavaPEGCFGBlock> {
	public <G extends CFG<G,B,V,JavaLabel,JavaParameter,JavaReturn>,B extends eqsat.Block<G,B,V,JavaLabel>,V> 
	JavaPEGCFG(CFG<G,B,V,JavaLabel,JavaParameter,JavaReturn> graph) {
		super(graph);
	}
	public JavaPEGCFG getSelf() {return this;}
	protected JavaPEGCFGBlock makeNewBlock() {return new JavaPEGCFGBlock(this);}
	protected Object makeNewTemporary() {return new Object();}
	public HolderBlock makeHolderBlock() {return new HolderBlock(this);}
}
