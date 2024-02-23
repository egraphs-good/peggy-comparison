package llvm.bitcode;

public abstract class BasicOperand extends Operand {
	public final boolean isBasic() {return true;}
	public final BasicOperand getBasicSelf() {return this;}

	public boolean isLiteral() {return false;}
	public LiteralOperand getLiteralSelf() {throw new UnsupportedOperationException();}

	public boolean isFixed() {return false;}
	public FixedOperand getFixedSelf() {throw new UnsupportedOperationException();}

	public boolean isVBR() {return false;}
	public VBROperand getVBRSelf() {throw new UnsupportedOperationException();}

	public boolean isChar6() {return false;}
	public Char6Operand getChar6Self() {throw new UnsupportedOperationException();}
}
