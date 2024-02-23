package peggy.represent.llvm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import llvm.bitcode.BitcodeReader;
import llvm.bitcode.ModuleBlock;
import llvm.bitcode.ModuleDecoder;
import llvm.values.Module;

/**
 * A ModuleProvider loads Module from Files by using the standard Module
 * parsing code in the llvm.bitcode package.
 */
public class ModuleProvider {
	private final List<Module> loadedModules;
	private final LinkedList<File> unloadedModules;
	private final Map<File,Module> loadedFileMap;
	private int modificationCount = 0;
	
	public ModuleProvider() {
		this.loadedModules = new ArrayList<Module>();
		this.unloadedModules = new LinkedList<File>();
		this.loadedFileMap = new HashMap<File,Module>();
	}
	
	public Iterable<Module> getLoadedModules() {
		return Collections.unmodifiableList(this.loadedModules);
	}
	
	public Iterable<Module> getAllModulesLazily() {
		return new Iterable<Module>() {
			public Iterator<Module> iterator() {
				return new Iterator<Module>() {
					int snapshot = modificationCount;
					int loadedIndex = 0;
					public boolean hasNext() {
						if (snapshot != modificationCount)
							throw new ConcurrentModificationException();
						if (loadedIndex < loadedModules.size()) {
							return true;
						} else {
							Module newmodule = loadOneModule();
							return (newmodule != null);
						} 
					}
					public Module next() {
						if (!hasNext())
							throw new NoSuchElementException();
						return loadedModules.get(loadedIndex++);
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
	public void addModuleFile(File file) {
		if (this.loadedFileMap.containsKey(file))
			return;
		this.unloadedModules.add(file);
	}
	
	/**
	 * Adds the module file to the front of the unloaded list and immediately 
	 * attempts to load it. If the module cannot be loaded, then some sort of 
	 * RuntimeException will be thrown. If there is a problem reading the file, 
	 * then an IOException will be thrown.
	 */
	public Module addAndLoadModuleFile(File file) throws IOException {
		if (this.loadedFileMap.containsKey(file))
			return this.loadedFileMap.get(file);
		Module module = readModule(file);
		this.loadedFileMap.put(file, module);
		this.loadedModules.add(module);
		return module;
	}
	
	protected Module readModule(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		BitcodeReader reader = new BitcodeReader(fin);
		ModuleBlock moduleBlock = reader.readBitcode();
		Module module = ModuleDecoder.decode(moduleBlock);
		return module;
	}
	
	private Module loadOneModule() {
		while (!this.unloadedModules.isEmpty()) {
			File file = this.unloadedModules.removeFirst();
			if (this.loadedFileMap.containsKey(file))
				continue;
			
			try {
				Module module = readModule(file);
				this.loadedModules.add(module);
				this.loadedFileMap.put(file, module);
				return module;
			} catch (Throwable t) {}
		}
		return null;
	}
}
