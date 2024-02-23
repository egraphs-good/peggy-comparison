package peggy.represent.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.jimple.IntConstant;
import soot.jimple.StringConstant;
import util.AbstractVariaticFunction;
import util.VariaticFunction;

/**
 * This is the default annotation constant folder.
 */
public class CustomAnnotationConstantFolder implements AnnotationConstantFolder {
	private static final Map<String,VariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>> FOLD_FUNCTIONS;
	
	private static Integer toInt(JavaLabel l) {
		if (l.isConstant() && l.getConstantSelf().getValue() instanceof IntConstant) {
			return ((IntConstant)l.getConstantSelf().getValue()).value;
		} else {
			return null;
		}
	}
	
	
	static {
		FOLD_FUNCTIONS = new HashMap<String, VariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>>();
		FOLD_FUNCTIONS.put(
			"String.concat",
			new AbstractVariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>() { 
				public JavaLabel get(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
					assertArgs(children, 2, "String.concat");
					String lhs = extractStringValue(children.get(0));
					String rhs = extractStringValue(children.get(1));
					return new ConstantValueJavaLabel(StringConstant.v(lhs + rhs));
				}
			}
		);
		
		FOLD_FUNCTIONS.put(
			"String.indexOf",
			new AbstractVariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>() {
				public JavaLabel get(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
					/* options:
					 * 		String,int,int
					 * 		String,int
					 * 		String,String,int
					 * 		String,String
					 */
					if (children.size() == 2) {
						// String,int
						// String,String
						String first = extractStringValue(children.get(0));
						if (toInt(children.get(1))!=null) {
							int second = toInt(children.get(1));
							try {return new ConstantValueJavaLabel(IntConstant.v(first.indexOf(second)));} 
							catch (Throwable t) {return null;}
						} else {
							String second = extractStringValue(children.get(1));
							try {return new ConstantValueJavaLabel(IntConstant.v(first.indexOf(second)));} 
							catch (Throwable t) {return null;}
						}
					} else if (children.size() == 3) {
						// String,int,int
						// String,String,int
						String first = extractStringValue(children.get(0));
						int third = toInt(children.get(2));
						
						if (toInt(children.get(1))!=null) {
							int second = toInt(children.get(1));
							try {return new ConstantValueJavaLabel(IntConstant.v(first.indexOf(second, third)));} 
							catch (Throwable t) {return null;}
						} else {
							String second = extractStringValue(children.get(1));
							try {return new ConstantValueJavaLabel(IntConstant.v(first.indexOf(second, third)));} 
							catch (Throwable t) {return null;}
						}
					} else 
						throw new IllegalArgumentException("Bad number of arguments for String.indexOf: " + children.size());
				}
			}
		);
				
		FOLD_FUNCTIONS.put(
			"String.substring",
			new AbstractVariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>() {
				public JavaLabel get(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
					// String,int
					// String,int,int
					if (children.size() == 2) {
						String first = extractStringValue(children.get(0));
						int second = toInt(children.get(1));
						try {return new ConstantValueJavaLabel(StringConstant.v(first.substring(second)));}
						catch (Throwable t) {return null;}
					} else if (children.size() == 3) {
						String first = extractStringValue(children.get(0));
						int second = toInt(children.get(1));
						int third = toInt(children.get(2));
						try {return new ConstantValueJavaLabel(StringConstant.v(first.substring(second, third)));}
						catch (Throwable t) {return null;}
					} else 
						throw new IllegalArgumentException("Bad number of arguments for String.substring: " + children.size());
				}
			}
		);
		
		FOLD_FUNCTIONS.put(
			"String.charAt",
			new AbstractVariaticFunction<AnnotationJavaLabel,JavaLabel,JavaLabel>() {
				public JavaLabel get(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
					// String,int
					assertArgs(children, 2, "String.charAt");
					String first = extractStringValue(children.get(0));
					int second = toInt(children.get(1));
					try {return new ConstantValueJavaLabel(IntConstant.v(first.charAt(second)));}
					catch (Throwable t) {return null;}
				}
			}
		);
		
	}
	
	public JavaLabel fold(AnnotationJavaLabel anl, List<? extends JavaLabel> children) {
		if (FOLD_FUNCTIONS.containsKey(anl.value)) {
			return FOLD_FUNCTIONS.get(anl.value).get(anl, children);
		} else {
			throw new IllegalArgumentException("Bad AnnotationJavaLabel for folding: " + anl.value);
		}
	}
	
	public boolean canFold(AnnotationJavaLabel anl) {
		if (anl.value.matches("StringConstant\\[.*\\]"))
			return true;
		else if (FOLD_FUNCTIONS.containsKey(anl.value))
			return true;
		else
			return false;
	}

	private static String extractStringValue(JavaLabel nl) {
		if (nl.isConstant() && nl.getConstantSelf().getValue() instanceof StringConstant) {
			return ((StringConstant)nl.getConstantSelf().getValue()).value;
		} else if (nl instanceof AnnotationJavaLabel) {
			AnnotationJavaLabel anl = (AnnotationJavaLabel)nl;
			if (anl.value.matches("StringConstant\\[.*\\]")) {
				String result = anl.value.substring("StringConstant[".length(), anl.value.length()-1);
				return result;
			}
		}
		
		throw new IllegalArgumentException("JavaLabel has no literal string value");
	}
	
	private static void assertArgs(List<?> children, int wantedSize, String label) {
		if (children.size() != wantedSize)
			throw new IllegalArgumentException("Bad number of arguments for " + label + ": " + children.size());
	}
}
