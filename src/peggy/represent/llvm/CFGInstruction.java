package peggy.represent.llvm;

/**
 * A CFGinstruction represents an LLVM instruction in an LLVMCFG.
 */
public abstract class CFGInstruction {
	public boolean isSimple() {return false;}
	public SimpleCFGInstruction getSimpleSelf() {throw new UnsupportedOperationException();}
	
	public boolean isIf() {return false;}
	public IfCFGInstruction getIfSelf() {throw new UnsupportedOperationException();}
	
	public boolean isIfException() {return false;}
	public IfExceptionCFGInstruction getIfExceptionSelf() {throw new UnsupportedOperationException();}
	
	public boolean isExtractException() {return false;}
	public ExtractExceptionCFGInstruction getExtractExceptionSelf() {throw new UnsupportedOperationException();}
	
	public boolean isExtractValue() {return false;}
	public ExtractValueCFGInstruction getExtractValueSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCopy() {return false;}
	public CopyCFGInstruction getCopySelf() {throw new UnsupportedOperationException();}
	
	public boolean isSwitch() {return false;}
	public SwitchCFGInstruction getSwitchSelf() {throw new UnsupportedOperationException();}
	
	public final boolean equals(Object o) {return super.equals(o);}
	public final int hashCode() {return super.hashCode();}
	public abstract String toString();
}
