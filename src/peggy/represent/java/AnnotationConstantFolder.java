package peggy.represent.java;

import java.util.List;

/**
 * This is a constant folder that specifically applies to annotation nodes.
 * It will be invoked by the standard constant folder when it is seen that
 * the operator in question is an annotation node.
 */
public interface AnnotationConstantFolder {
	public boolean canFold(AnnotationJavaLabel anl);
	public JavaLabel fold(AnnotationJavaLabel parent, List<? extends JavaLabel> children);
}
