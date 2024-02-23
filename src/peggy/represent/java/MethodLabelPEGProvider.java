package peggy.represent.java;

import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import soot.SootMethod;
import soot.SootMethodRef;

/**
 * This is a PEGProvider that provides PEGs based on MethodJavaLabels.
 */
public class MethodLabelPEGProvider
implements PEGProvider<MethodJavaLabel,JavaLabel,JavaParameter,JavaReturn> {
	protected final PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> methodProvider;
	protected final ReferenceResolver resolver;
	
	public MethodLabelPEGProvider(
			ReferenceResolver _resolver,
			PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> _methodProvider) {
		this.methodProvider = _methodProvider;
		this.resolver = _resolver;
	}

	private SootMethod getMethod(MethodJavaLabel label) {
		SootMethodRef ref = resolver.resolveMethod(
				label.getClassName(),
				label.getMethodName(),
				label.getReturnType(),
				label.getParameterTypes());
		return ref.resolve();
	}
	
	public boolean canProvidePEG(MethodJavaLabel label) {
		SootMethod body = getMethod(label);
		return (body != null) && this.methodProvider.canProvidePEG(body);
	}

	public PEGInfo<JavaLabel,JavaParameter,JavaReturn> getPEG(MethodJavaLabel function) {
		SootMethod body = getMethod(function);
		if (body == null)
			throw new IllegalArgumentException("Can't find function body for: " + function.getMethodName());
		return this.methodProvider.getPEG(body);
	}
}
