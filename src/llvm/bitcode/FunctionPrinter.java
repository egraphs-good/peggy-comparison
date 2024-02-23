package llvm.bitcode;

import java.io.FileInputStream;

import llvm.values.FunctionValue;
import llvm.values.Module;

/**
 * This class simply writes out the names of all the functions with bodies
 * that are contained in a given LLVM bitcode file (2.3 or 2.8).
 */
public class FunctionPrinter {
	public static void main(String args[]) throws Throwable {
		if (args.length < 1) {
			System.err.println("USAGE: FunctionPrinter <bitcode> [true|false]    // for v2.8");
			System.exit(1);
		}

		Module module = null;
		if (args.length >= 2 && args[1].toLowerCase().equals("true")) {
			BitcodeReader2_8 reader = new BitcodeReader2_8(new FileInputStream(args[0]));
			ModuleBlock2_8 moduleBlock = reader.readBitcode();
			module = ModuleDecoder2_8.decode(moduleBlock, reader.LLVM2_7MetadataDetected());
		} else {
			BitcodeReader reader = new BitcodeReader(new FileInputStream(args[0]));
			ModuleBlock moduleBlock = reader.readBitcode();
			module = ModuleDecoder.decode(moduleBlock);
		}
		
		ReferenceResolver resolver = new DefaultReferenceResolver(module);
		
		for (int i = 0; i < module.getNumFunctionHeaders(); i++) {
			FunctionValue func =  module.getFunctionHeader(i);
			if (!func.isPrototype())
				System.out.println(resolver.getFunctionName(func));
		}
	}
}
