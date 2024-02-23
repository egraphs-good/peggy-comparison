package llvm.bitcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import llvm.instructions.BasicBlock;
import llvm.instructions.FunctionBody;
import llvm.instructions.Instruction;
import llvm.values.FunctionValue;
import llvm.values.Module;
import llvm.values.ParameterAttributeMap;

/**
 * This class encodes and writes the param attr block for an LLVM (2.3 or 2.8)
 * bitcode file.
 */
class ParamAttrEncoder {
	protected final BitcodeWriter writer;
	protected final int abbrevLength;
	protected final Module module;
	
	public ParamAttrEncoder(BitcodeWriter _writer, int _abbrevLength, Module _module) {
		this.writer = _writer;
		this.abbrevLength = _abbrevLength;
		this.module = _module;
	}
	
	/**
	 * Collects all the parameter attributes from all the function protos
	 * and bodies and call/invoke instructions, and returns the list of paramattrs. 
	 * Also emits the ParamAttrBlock.
	 */
	public List<ParameterAttributeMap> writeParamAttrs() {
		Set<ParameterAttributeMap> paramAttrs = new HashSet<ParameterAttributeMap>();
		
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue header = this.module.getFunctionHeader(i);
			if (!header.isPrototype())
				continue;
			ParameterAttributeMap map = header.getParameterAttributeMap();
			if (!map.isEmpty())
				paramAttrs.add(map);
		}
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionBody body = this.module.getFunctionBody(i);
			{
				ParameterAttributeMap map = body.getHeader().getParameterAttributeMap();
				if (!map.isEmpty())
					paramAttrs.add(map);
			}
			for (int b = 0; b < body.getNumBlocks(); b++) {
				BasicBlock bb = body.getBlock(b);
				for (int j = 0; j < bb.getNumInstructions(); j++) {
					Instruction inst = bb.getInstruction(j);
					if (inst.isCall()) {
						ParameterAttributeMap map = inst.getCallSelf().getParameterAttributeMap();
						if (!map.isEmpty())
							paramAttrs.add(map);
					} else if (inst.isTerminator() && inst.getTerminatorSelf().isInvoke()) {
						ParameterAttributeMap map = inst.getTerminatorSelf().getInvokeSelf().getParameterAttributeMap();
						if (!map.isEmpty())
							paramAttrs.add(map);
					}
				}
			}
		}
		
		// gathered all paramattrs, now sequence them
		List<ParameterAttributeMap> result = new ArrayList<ParameterAttributeMap>(paramAttrs);
		paramAttrs = null;

		if (result.size() == 0)
			return result;
		
		// emit ParamAttrsBlock
		
		final int innerAbbrevLength = 2;
		int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(ParamAttrBlock.PARAMATTR_BLOCK_ID, innerAbbrevLength, 0));
		
		List<Long> ops = new ArrayList<Long>(10);
		for (int i = 0; i < result.size(); i++) {
			ParameterAttributeMap map = result.get(i);
			ops.clear();
			if (module.is2_8()) {
				if (map.hasFunctionAttributes()) {
					ops.add(-1L);
					ops.add((long)map.getFunctionAttributes().getBits());
				}
				if (map.hasReturnAttributes()) {
					ops.add(0L);
					ops.add((long)map.getReturnAttributes().getBits());
				}
			} else {
				if (map.hasFunctionAttributes()) {
					ops.add(0L);
					ops.add((long)map.getFunctionAttributes().getBits());
				}
			}
			
			for (int j = 0; j <= map.getMaxIndex(); j++) {
				if (!map.hasParamAttributes(j))
					continue;
				ops.add((long)(j+1));
				ops.add((long)map.getParamAttributes(j).getBits());
			}
			
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ParamAttrBlock.PARAMATTR_CODE_ENTRY, ops));
		}
		
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
		
		return result;
	}
}
