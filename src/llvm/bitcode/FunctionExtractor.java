package llvm.bitcode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import llvm.instructions.BasicBlock;
import llvm.instructions.FunctionBody;
import llvm.instructions.RetInstruction;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.values.Module;
import llvm.values.Value;

/**
 * This class is used to make a new minimal copy of a given bitcode module.
 * Specifically, a particular function is named, and then all other functions
 * in the module are "gutted" by removing all their contents and replacing them
 * with an immediate return. 
 */
public class FunctionExtractor {
	public static void main(String args[]) throws Throwable {
		final BitcodeReader reader = new BitcodeReader(new FileInputStream(args[0]));
		final Module module = ModuleDecoder.decode(reader.readBitcode());
		FunctionBody goodbody = null;
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			final FunctionBody body = module.getFunctionBody(i);
			String name = module.lookupValueName(body.getHeader());
			if (name.equals(args[1])) {
				goodbody = body;
				break;
			}
		}
		if (goodbody == null) {
			System.err.println("No function named " + args[1] + " found");
			System.exit(1);
		}
		gutFunctionBodies(module, goodbody);
		
		final BitcodeWriter writer = new BitcodeWriter();
		final ModuleEncoder encoder = new ModuleEncoder(writer, module);
		encoder.writeModule();
		final FileOutputStream fout = new FileOutputStream(args[0] + "." + args[1]);
		writer.dump(fout);
		fout.close();
	}
	
	private static void gutFunctionBodies(Module module, FunctionBody goodbody) {
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			FunctionBody body = module.getFunctionBody(i);
			if (body == goodbody)
				continue;
			BasicBlock start = body.getStart();
			
			// remove blocks (not start)
			for (int j = 0; j < body.getNumBlocks(); j++) {
				if (start != body.getBlock(j)) {
					body.removeBlock(j);
					j--;
				}
			}
			
			// remove handles from start
			while (start.getNumInstructions() > 0)
				start.removeInstruction(0);
			
			// clear out assignment
			body.getRegisterAssignment().clear();
			
			// clear map
			body.clearValueNameMap();
			
			// add return instruction
			Type returntype = body.getHeader().getType().getPointeeType().getFunctionSelf().getReturnType();
			RetInstruction ret;
			if (returntype.isVoid()) {
				ret = new RetInstruction();
			} else if (returntype.isComposite() && returntype.getCompositeSelf().isStructure()) {
				final StructureType type = returntype.getCompositeSelf().getStructureSelf();
				List<Value> values = new ArrayList<Value>();
				for (int k = 0; k < type.getNumFields(); k++) {
					values.add(Value.getNullValue(type.getFieldType(k)));
				}
				ret = new RetInstruction(values);
			} else {
				ret = new RetInstruction(Collections.singletonList(Value.getNullValue(returntype)));
			}
			start.addInstruction(ret);
		}
	}

}
