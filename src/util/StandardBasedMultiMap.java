package util;

import java.lang.ref.Reference;
import java.util.Map;

public class StandardBasedMultiMap<K extends Based<K>, V>
		extends BasedMultiMap<K,V> {
	public StandardBasedMultiMap(Basis<K> basis) {
		super(basis, new StandardBasedMap<K,Box>(basis),
				new StandardBasedMap<K,Reference<Box>>(basis));
	}
	
	protected <R> Map<K,R> makeKeyMap() {
		return new StandardBasedMap<K,R>(mBasis);
	}
}
