package peggy.analysis.java.inlining;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;

/**
 * This class provides some static methods to wrap
 * around the soot.Hierarchy class methods, so that 
 * I don't have to deal with its crap anymore.
 * 
 * @author steppm
 */
public class HierarchyWrapper {
	public static List<SootClass> getImplementorsOfIncluding(SootClass root) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		List<SootClass> result = new ArrayList<SootClass>();
		result.add(root);
		try {
			result.addAll(hierarchy.getImplementersOf(root));
		} catch (ConcurrentModificationException cme) {
			Scene.v().releaseActiveHierarchy();
			hierarchy = Scene.v().getActiveHierarchy();
			result.addAll(hierarchy.getImplementersOf(root));
		}
		return result;
	}
	
	public static List<SootClass> getSubinterfacesOfIncluding(SootClass root) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		List<SootClass> result;
		try {
			result = hierarchy.getSubinterfacesOfIncluding(root);
		} catch (ConcurrentModificationException cme) {
			Scene.v().releaseActiveHierarchy();
			hierarchy = Scene.v().getActiveHierarchy();
			result = hierarchy.getSubinterfacesOfIncluding(root);
		}
		return result;
	}
	
	public static List<SootClass> getSubclassesOfIncluding(SootClass root) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		List<SootClass> result;
		try {
			result = hierarchy.getSubclassesOfIncluding(root);
		} catch (ConcurrentModificationException cme) {
			Scene.v().releaseActiveHierarchy();
			hierarchy = Scene.v().getActiveHierarchy();
			result = hierarchy.getSubclassesOfIncluding(root);
		}
		return result;
	}
	
	public static boolean isClassSubclassOfIncluding(SootClass subclass, SootClass superclass) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		boolean result;
		try {
			result = hierarchy.isClassSubclassOfIncluding(subclass, superclass);
		} catch (ConcurrentModificationException cme) {
			Scene.v().releaseActiveHierarchy();
			hierarchy = Scene.v().getActiveHierarchy();
			result = hierarchy.isClassSubclassOfIncluding(subclass, superclass);
		}
		return result;
	}
	
	public static boolean isClassSubclassOf(SootClass subclass, SootClass superclass) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		boolean result;
		try {
			result = hierarchy.isClassSubclassOf(subclass, superclass);
		} catch (ConcurrentModificationException cme) {
			Scene.v().releaseActiveHierarchy();
			hierarchy = Scene.v().getActiveHierarchy();
			result = hierarchy.isClassSubclassOf(subclass, superclass);
		}
		return result;
	}
}
