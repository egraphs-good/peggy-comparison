package peggy.represent.llvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import llvm.bitcode.BitcodeReader2_8;
import llvm.bitcode.ModuleBlock2_8;
import llvm.bitcode.ModuleDecoder2_8;
import llvm.values.Module;

/**
 * This is an extension of the ModuleProvider class that is used for LLVM 2.8
 * modules.
 */
public class ModuleProvider2_8 extends ModuleProvider {
	protected Module readModule(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		BitcodeReader2_8 reader = new BitcodeReader2_8(fin);
		ModuleBlock2_8 moduleBlock = reader.readBitcode();
		Module module = ModuleDecoder2_8.decode(moduleBlock, reader.LLVM2_7MetadataDetected());
		return module;
	}
}
