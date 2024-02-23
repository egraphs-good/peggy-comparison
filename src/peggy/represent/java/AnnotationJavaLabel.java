package peggy.represent.java;

/**
 * This is a JavaLabel for annotation nodes. They are not revertable.
 */
public class AnnotationJavaLabel extends JavaLabel {
	public final String value;
	
	public AnnotationJavaLabel(String _value) {
		if (_value == null)
			throw new IllegalArgumentException("annotation name is null");
		this.value = _value;
	}
	
	public int getNumOutputs() {return 1;}

	public boolean isAnnotation() {return true;}
	public AnnotationJavaLabel getAnnotationSelf() {return this;}
	
	public String toString() {
		return "Annotation[" + this.value + "]";
	}
	
	public boolean equalsLabel(JavaLabel o) {
		if (!o.isAnnotation()) return false;
		return this.value.equals(o.getAnnotationSelf().value);
	}
	
	public int hashCode() {
		return this.value.hashCode();
	}
	
	public final boolean isRevertible() {return false;}
}
