package llvm.values;

/**
 * This represents the different kinds of visibility modifiers that can
 * be placed on functions/globals/aliases.
 */
public enum Visibility {
	DefaultVisibility(0),
	HiddenVisibility(1),
	ProtectedVisibility(2);
	
	private int bits;
	private Visibility(int _bits) {
		this.bits = _bits;
	}
	public int getBits() {return this.bits;}
	
	public static Visibility decodeVisibility(int value) {
		switch (value) {
		case 0: return DefaultVisibility;
		case 1: return HiddenVisibility;
		case 2: return ProtectedVisibility;
		default:
			throw new IllegalArgumentException("Unknown visibility value: " + value);
		}
	}
}
