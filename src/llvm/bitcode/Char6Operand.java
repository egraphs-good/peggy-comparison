package llvm.bitcode;

/**
 * This is a bitcode data record operand type whose contents are 6 characters.
 */
public final class Char6Operand extends BasicOperand {
	public static final Char6Operand INSTANCE = new Char6Operand();

	public static char indexToChar6(int index) {
		if (index < 0 || index >= 64)
			throw new IllegalArgumentException("Index must be in [0,64)");
		if (index < 26)
			return (char)('a' + index);
		else if (index < 52)
			return (char)('A' + (index-26));
		else if (index < 62)
			return (char)('0' + (index-52));
		else if (index == 62)
			return '.';
		else
			return '_';
	}
	public static int char6ToIndex(char c) {
		if ('a' <= c && c <= 'z')
			return c - 'a';
		else if ('A' <= c && c <= 'Z')
			return 26 + c - 'A';
		else if ('0' <= c && c <= '9')
			return 52 + c - '0';
		else if (c == '.')
			return 62;
		else if (c == '_')
			return 63;
		else
			throw new IllegalArgumentException("Character " + c + " is not a valid six bit char");
	}
	
	private Char6Operand() {}

	public final boolean isChar6() {return true;}
	public final Char6Operand getChar6Self() {return this;}
	
	public String toString() {
		return "Char6";
	}
	public boolean equals(Object o) {
		return (o != null) && (o instanceof Char6Operand);
	}
	public int hashCode() {
		return 67;
	}
}
