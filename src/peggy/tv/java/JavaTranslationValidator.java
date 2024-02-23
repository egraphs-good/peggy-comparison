package peggy.tv.java;

import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.tv.TranslationValidator;
import soot.SootMethod;

/**
 * This is the default abstract Java TV engine.
 */
public abstract class JavaTranslationValidator 
extends TranslationValidator<JavaLabel,JavaParameter,JavaReturn> {
	private SootMethod currentMethod;
	public void setCurrentMethod(SootMethod method) {
		this.currentMethod = method;
	}
	public SootMethod getCurrentMethod() {return this.currentMethod;}
}
