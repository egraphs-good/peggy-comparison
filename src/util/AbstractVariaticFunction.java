package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractVariaticFunction<F, V, R> implements
		VariaticFunction<F, V, R> {
	
	public R get(F first, Object... variatic) {
		return get(first, variatic == null ? null
				: (List<? extends V>)Arrays.asList(variatic));
	}
	
	public R get(F first) {return get(first, Collections.<V>emptyList());}

	public R get(F first, V second) {
		return get(first, Collections.singletonList(second));
	}

	/*public boolean usesAllArguments(F first) {return true;}
	
	public boolean usesAnyArguments(F first) {return true;}
	
	public boolean usesArgument(F first, int index) {return true;}*/
}
