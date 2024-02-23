package util;

public interface Operation<P,R> {
	R execute(P parameter);
}
