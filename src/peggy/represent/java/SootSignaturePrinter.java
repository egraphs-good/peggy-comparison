package peggy.represent.java;

import java.util.List;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

/**
 * This is a convenience class for printing out all the Soot signatures for
 * the methods in a given Java class.
 */
public class SootSignaturePrinter {
	public static void main(String args[]) throws Throwable {
		SootClass clazz = Scene.v().loadClassAndSupport(args[0]);
		for (SootMethod method : (List<SootMethod>)clazz.getMethods()) {
			System.out.println(method.getSignature());
		}
	}
}
