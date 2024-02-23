package util;

public final class Functions {
	private static Function mIdentity = new Function() {
		public Object get(Object parameter) {return parameter;}
		
		public String toString() {return "<identity>";}
	};
	
	public static <R,D extends R> Function<D,R> getIdentity() {
		return mIdentity;
	}
	
	public static <D,R> Function<D,R> getConstant(final R value) {
		return new Function<D,R>() {
			public R get(D parameter) {return value;}
			
			public String toString() {
				return value == null ? "<null>" : value.toString();
			}
			
			public boolean equals(Object that) {return value.equals(that);}
		};
	}
	
	public static <D,M,R> Function<D,R> compose(
			final Function<? super D,? extends M> first,
			final Function<? super M,? extends R> second) {
		return new Function<D,R>() {
			public R get(D domain) {return second.get(first.get(domain));}
			public String toString() {return second + " of " + first;}
		};
	}
}
