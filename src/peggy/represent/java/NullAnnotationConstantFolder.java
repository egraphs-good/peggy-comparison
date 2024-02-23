package peggy.represent.java;

import java.util.List;

/**
 * This is an annotation constant folder that cannot do any folding.
 */
public class NullAnnotationConstantFolder implements AnnotationConstantFolder {
	public JavaLabel fold(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
		return null;
	}
	public boolean canFold(AnnotationJavaLabel anl) {
		return false;
	}
}
