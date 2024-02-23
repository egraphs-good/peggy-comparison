package util;

import java.util.ArrayList;
import java.util.Collection;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class ClassFinder {
	/**
	 * Returns a list of all the Class objects for all of the classfiles located in the given folder (not including subfolders). 
	 * The packagePrefix is a way to denote a difference between the filename and the
	 * class name. For instance, if you have two packages named foo1.foo2.foo3 and foo1.foo2.foo4 
	 * but you only want the classes that are in foo3, then you could call
	 * 	getClassesRecursive("foo1.foo2", new File("whatever/foo1/foo2/foo3"))
	 * 
	 * @param packagePrefix the prefix of the package name of the classes in the given folder.
	 * 		this should NOT have trailing "."s.   If the package you want is the default or empty package,
	 * 		you may pass either null or the empty string.
	 * @throws SecurityException if there is insufficient file access to get all the classfiles off the disk 
	 * @return a collection of the found classes
	 */
	public static Collection<Class<?>> getClasses(String packagePrefix, File folder) {
		Collection<Class<?>> result = new ArrayList<Class<?>>();
		getClassesRecursive(packagePrefix == null ? "" : packagePrefix, folder, result, false);
		return result;
	}

	/**
	 * Returns a list of all the Class objects for all of the .class files located in any subfolder
	 * of the given folder. The packagePrefix is a way to denote a difference between the filename and the
	 * class name. For instance, if you have two packages named foo1.foo2.foo3 and foo1.foo2.foo4 
	 * but you only want the classes that are in foo3, then you could call
	 * 	getClassesRecursive("foo1.foo2", new File("whatever/foo1/foo2/foo3"))
	 * 
	 * @param packagePrefix the prefix of the package name of the classes in the given folder.
	 * 		this should NOT have trailing "."s.   If the package you want is the default or empty package,
	 * 		you may pass either null or the empty string.
	 * @throws SecurityException if there is insufficient file access to get all the classfiles off the disk 
	 * @return a collection of the found classes
	 */
	public static Collection<Class<?>> getClassesRecursive(String packagePrefix, File root) {
		Collection<Class<?>> result = new ArrayList<Class<?>>();
		getClassesRecursive(packagePrefix == null ? "" : packagePrefix, root, result, true);
		return result;
	}
	
	// packagePrefix will not be null
	private static void getClassesRecursive(String packagePrefix, File file, Collection<Class<?>> classes, boolean recursive) {
		if (file == null)
			throw new NullPointerException("folder is null");
		if (file.isDirectory()) {
			File[] contents = file.listFiles();
			if (contents == null)
				return;
			String newPrefix = (packagePrefix.equals("")
								? file.getName()
								: packagePrefix + "." + file.getName());
			for (File child : contents) {
				if (child.isDirectory() && !recursive)
					continue;
				getClassesRecursive(newPrefix, child, classes, recursive);
			}
		} else {
			String name = file.getName();
			if (name.endsWith(".class")) {
				String className = (packagePrefix.equals("") 
									? name.substring(0, name.length()-6)
									: packagePrefix + "." + name.substring(0, name.length()-6));
				try {
					Class<?> clazz = Class.forName(className);
					classes.add(clazz);
				} catch (ClassNotFoundException cnfe) {}
			}
		}
	}


	
	
	public static Collection<File> getClassFiles(File root, boolean recursive) {
		if (root == null)
			throw new NullPointerException("root is null");
		Collection<File> result = new ArrayList<File>();
		getClassFiles(root, recursive, result);
		return result;
	}
	private static void getClassFiles(File file, boolean recursive, Collection<File> result) {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			File[] contents = file.listFiles();
			if (contents == null)
				return;
			for (File child : contents) {
				if (child.isDirectory() && !recursive)
					continue;
				getClassFiles(child, recursive, result);
			}
		} else {
			if (file.getName().endsWith(".class")) {
				result.add(file);
			}
		}
	}
	
	
	
	/**
	 * Returns a File object that represents the folder in which the given Class' classfile lives 
	 * on the disk. This method is useful in combination with getClasses or getClassesRecursive.
	 * 
	 * @param clazz the Class whose classfile has the parent folder you want
	 * @return the parent folder of the given Class' classfile
	 */
	public static java.io.File getClassfileParentFolder(Class<?> clazz) {
		java.net.URL url = clazz.getResource(clazz.getSimpleName() + ".class");
		File pathFile = new File(url.getPath());
		return pathFile.getParentFile();
	}
	
	
	/**
	 * Returns a collection of all the Methods defined in the given class (excluding superclasses/interfaces) that have the given
	 * annotation. This will NOT include constructors! It will include <clinit> though.
	 * 
	 * @param clazz the class to search for annotated methods
	 * @param annotation the target annotation to search for
	 * @return
	 */
	public static Collection<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		Collection<Method> result = new ArrayList<Method>();
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotation))
				result.add(method);
		}
		return result;
	}
}
