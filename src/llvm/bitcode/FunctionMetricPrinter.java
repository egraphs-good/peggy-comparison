package llvm.bitcode;

import java.io.FileInputStream;
import java.io.PrintStream;

import llvm.instructions.BasicBlock;
import llvm.instructions.FunctionBody;
import llvm.values.Module;

/**
 * This class is used to print particular statistics about bitcode modules.
 */
public class FunctionMetricPrinter {
	public static void printMetrics(Module module, PrintStream out) {
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			final FunctionBody body = module.getFunctionBody(i);
			final String name = module.lookupValueName(body.getHeader());
			if (name != null)
				out.println("Function " + name);
			else
				out.println("Function #" + i);

			out.println(" - Instruction count = " + getInstructionCount(body));
			// TODO add more!
		}
	}
	
	private static int getInstructionCount(FunctionBody body) {
		int total = 0;
		for (int i = 0; i < body.getNumBlocks(); i++) {
			BasicBlock bb = body.getBlock(i);
			total += bb.getNumInstructions();
		}
		return total;
	}
	
	public static void main(String args[]) throws Throwable {
		if (args.length < 1) {
			System.err.println("USAGE: FunctionMetricPrinter <module name> [use2.8]");
			System.exit(1);
		}
		
		if (args.length >= 2) {
			BitcodeReader2_8 reader = new BitcodeReader2_8(
					new FileInputStream(args[0]));
			Module module = ModuleDecoder2_8.decode(reader.readBitcode(), reader.LLVM2_7MetadataDetected());
			printMetrics(module, System.out);
		} else {
			BitcodeReader reader = new BitcodeReader(
					new FileInputStream(args[0]));
			Module module = ModuleDecoder.decode(reader.readBitcode());
			printMetrics(module, System.out);
		}
	}
}
