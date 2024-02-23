package llvm.instructions;

import java.util.List;

import llvm.values.Value;

/**
 * This represents the INBOUNDS GETELEMENTPTR instruction.
 * It contains the same data as the GETELEMENTPTR instruction, but behaves slightly
 * differently.
 */
public class InboundsGEPInstruction extends GEPInstruction {
	public InboundsGEPInstruction(Value _baseValue, List<? extends Value> _indexes) {
		super(_baseValue, _indexes);
	}
	public boolean isInbounds() {return false;}
	public boolean is2_8Instruction() {return true;}
}
