package peggy.revert.java;

import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.revert.ReachingDefs;
import static peggy.revert.java.JavaReachingDefs.JavaDef;
import static peggy.revert.java.JavaReachingDefs.JavaUse;

/**
 * This is a reachingdefs implementation for Java.
 */
public class JavaReachingDefs extends ReachingDefs<JavaLabel,JavaParameter,JavaReturn,Object,JavaPEGCFG,JavaPEGCFGBlock,JavaReachingDefs,JavaUse,JavaDef> {
	public static class JavaUse extends ReachingDefs.Use<Object,JavaPEGCFGBlock,JavaUse> {
		private final JavaPEGCFGBlock block;
		private final Object variable;
		protected JavaUse(JavaPEGCFGBlock _block, Object _variable) {
			this.block = _block;
			this.variable = _variable;
		}
		public JavaPEGCFGBlock getBlock() {return this.block;}
		public Object getVariable() {return this.variable;}
		public JavaUse getSelf() {return this;}
		public boolean equals(Object o) {
			if (!(o instanceof JavaUse))
				return false;
			JavaUse use = (JavaUse)o;
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
	public static class JavaDef extends ReachingDefs.Def<Object,JavaPEGCFGBlock,JavaDef> {
		private final JavaPEGCFGBlock block;
		private final Object variable;
		protected JavaDef(JavaPEGCFGBlock _block, Object _variable) {
			this.block = _block;
			this.variable = _variable;
		}
		public JavaPEGCFGBlock getBlock() {return this.block;}
		public Object getVariable() {return this.variable;}
		public JavaDef getSelf() {return this;}
		public boolean equals(Object o) {
			if (!(o instanceof JavaDef))
				return false;
			JavaDef def = (JavaDef)o;
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
	
	public JavaReachingDefs(JavaPEGCFG _cfg) {
		super(_cfg);
	}
	
	public JavaUse getUse(JavaPEGCFGBlock block, Object var) {
		return new JavaUse(block, var);
	}
	public JavaDef getDef(JavaPEGCFGBlock block, Object var) {
		return new JavaDef(block, var);
	}
}
