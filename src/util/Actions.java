package util;

public final class Actions {
	private static final Action mEmpty = new Action() {
		public void execute(Object parameter) {}
	};
	
	public static <P> Action<P> empty() {return mEmpty;}
	
	public static <P> Action<P> sequence(final Action<? super P> first,
			final Action<? super P> second) {
		return new Action<P>() {
			public void execute(P parameter) {
				first.execute(parameter);
				second.execute(parameter);
			}
		};
	}
	
	public static <P> Action<P> sequence(final Action<? super P>... actions) {
		return new Action<P>() {
			public void execute(P parameter) {
				for (Action<? super P> action : actions)
					action.execute(parameter);
			}
		};
	}
}
