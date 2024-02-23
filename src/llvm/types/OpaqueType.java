package llvm.types;

/**
 * This is the opaque type in LLVM.
 */
public class OpaqueType extends Type {
	public OpaqueType() {}
	
	public boolean isDerived() {return true;}
	public boolean isOpaque() {return true;}
	public OpaqueType getOpaqueSelf() {return this;}
	
	protected String toString(int depth) {
		return "opaque";
	}
	public boolean equalsType(Type o) {
		return o.isOpaque();
		// TODO ???   && o.getOpaqueSelf() == this;
	}
	protected int hashCode(int depth) {
		return 271;
	}
	protected void ensureSized() {throw new IllegalStateException();}
}
