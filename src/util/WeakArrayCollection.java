package util;

import java.lang.ref.Reference;

public class WeakArrayCollection<E> extends WeakCollection<E> {
	public WeakArrayCollection() {super(new ArrayCollection<Reference<E>>());}
}
