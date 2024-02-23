package util;

public final class Patterns {
	private static Pattern mTrue = new AbstractPattern() {
		public boolean matches(Object that) {return true;}
	};
	private static Pattern mFalse = new AbstractPattern() {
		public boolean matches(Object that) {return false;}
	};
	
	public static <P> Pattern<P> getTrue() {return mTrue;}
	
	public static <P> Pattern<P> getFalse() {return mFalse;}
	
	public static <P> Pattern<P> getPattern(final Function<P,Boolean> pattern) {
		return new Pattern<P>() {
			public Boolean get(P object) {return pattern.get(object);}
			public boolean matches(P object) {return pattern.get(object);}
			public boolean equals(Object that) {
				return this == that || pattern == that;
			}
			public int hashCode() {return pattern.hashCode();}
		};
	}
}
