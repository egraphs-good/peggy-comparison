package llvm.bitcode;

import java.io.FileInputStream;
import java.util.Set;

import llvm.instructions.FunctionBody;
import llvm.values.*;

/**
 * This class will print out a simple description of the contents of 
 * an LLVM bitcode module.
 */
public class LLVMMetrics {
	public static void main(String args[]) throws Throwable {
		if (args.length < 2) {
			System.err.println("USAGE: java LLVMMetrics <module file> [bhgascltdm]");
			System.exit(1);
		}
		
		BitcodeReader reader = new BitcodeReader(new FileInputStream(args[0]));
		ModuleBlock moduleBlock = reader.readBitcode();
		Module module = ModuleDecoder.decode(moduleBlock);
		args[1] = args[1].toLowerCase();
		
		System.out.println("Module " + args[0]);
		Set<String> valueNames = module.getValueNames();
		if (args[1].contains("h")) {
			System.out.println("- function (h)eader count = " + module.getNumFunctionHeaders());
			for (int i = 0; i < module.getNumFunctionHeaders(); i++) {
				String name = "<unnamed>";
				FunctionValue header = module.getFunctionHeader(i);
				for (String vname : valueNames) {
					if (module.getValueByName(vname).equalsValue(header)) {
						name = vname;
						break;
					}
				}
				System.out.println("  + function " + name + " " + header.getType().getPointeeType());
			}
		}

		if (args[1].contains("b")) {
			System.out.println("- function (b)ody count = " + module.getNumFunctionBodies());
			for (int i = 0; i < module.getNumFunctionBodies(); i++) {
				displayFunctionBody(module, module.getFunctionBody(i));
			}
		}
		
		if (args[1].contains("g")) {
			System.out.println("- (g)lobal var count = " + module.getNumGlobalVariables());
			for (int i = 0; i < module.getNumGlobalVariables(); i++) {
				String name = "<unnamed>";
				GlobalVariable global = module.getGlobalVariable(i);
				for (String vname : valueNames) {
					if (module.getValueByName(vname).equalsValue(global)) {
						name = vname;
						break;
					}
				}
				System.out.println("  + global " + name + " " + global.getType().getPointeeType());
			}
		}
		if (args[1].contains("a")) {
			System.out.println("- (a)lias count = " + module.getNumAliases());
			for (int i = 0; i < module.getNumAliases(); i++) {
				String name = "<unnamed>";
				AliasValue alias = module.getAlias(i);
				for (String vname : valueNames) {
					if (module.getValueByName(vname).equalsValue(alias)) {
						name = vname;
						break;
					}
				}
				System.out.println("  + alias " + name + " " + alias.getType().getPointeeType());
			}
		}
		if (args[1].contains("s")) {
			System.out.println("- (s)ection count = " + module.getNumSections());
			for (int i = 0; i < module.getNumSections(); i++) {
				System.out.println("  + section " + (i+1) + " = " + module.getSectionName(i));
			}
		}
		if (args[1].contains("c")) {
			System.out.println("- (c)ollector count = " + module.getNumCollectors());
			for (int i = 0; i < module.getNumCollectors(); i++) {
				System.out.println("  + collector " + (i+1) + " = " + module.getCollectorName(i));
			}
		}
		if (args[1].contains("l")) {
			System.out.println("- (l)ibrary count = " + module.getNumLibraries());
			for (int i = 0; i < module.getNumLibraries(); i++) {
				System.out.println("  + library " + (i+1) + " = " + module.getLibraryName(i));
			}
		}
		if (args[1].contains("t")) {
			String tt = module.getTargetTriple();
			if (tt != null)
				System.out.println("- (t)arget triple = " + tt);
		}
		if (args[1].contains("d")) {
			String dl = module.getDataLayout();
			if (dl != null)
				System.out.println("- (d)ata layout = " + dl);
		}
		if (args[1].contains("m")) {
			String asm = module.getModuleInlineASM();
			if (asm != null)
				System.out.println("- inline as(m) = " + asm);
		}
	}
	
	private static void displayFunctionBody(Module module, FunctionBody body) {
		String name = "<unnamed>";
		for (String vname : module.getValueNames()) {
			if (module.getValueByName(vname).equalsValue(body.getHeader())) {
				name = vname;
				break;
			}
		}
		
		int icount = 0;
		for (int i = 0; i < body.getNumBlocks(); i++) {
			icount += body.getBlock(i).getNumInstructions();
		}
		
		System.out.println("  + function " + name + " " + body.getHeader().getType().getPointeeType());
		System.out.println("    - parameter count = " + body.getHeader().getNumArguments());
		System.out.println("    - bb count = " + body.getNumBlocks());
		System.out.println("    - instruction count = " + icount);
	}
}
