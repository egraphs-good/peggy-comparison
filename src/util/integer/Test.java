package util.integer;

public class Test {
	private static Bit64IntSet getEmptyBits64() {
		return new Bit64IntSet();
	}
	private static Bit64IntSet getFullBits64() {
		Bit64IntSet result = new Bit64IntSet();
		result.addRange(0, 63);
		return result;
	}
	private static Bit64IntSet getAlternatingBits64() {
		Bit64IntSet result = new Bit64IntSet();
		for (int i = 0; i < 64; i+=2)
			result.add(i);
		return result;
	}
	
	
	
	public static void testBit64IntSet_iterator() {
		Bit64IntSet set = getEmptyBits64();
		IntIterator iter = set.iterator();
		assertTrue(!iter.hasNext(), "Empty bitset should not have a next");
		
		set = getFullBits64();
		iter = set.iterator();
		for (int i = 0; i < 64; i++)
			assertTrue(iter.hasNext() && iter.next() == i, "Full set missing " + i);
		assertTrue(!iter.hasNext(), "Too many elements in full bitset");
		
		set = getAlternatingBits64();
		iter = set.iterator();
		for (int i = 0; i < 64; i+=2)
			assertTrue(iter.hasNext() && iter.next() == i, "Alternating set missing " + i);
		assertTrue(!iter.hasNext(), "Too many elements in alternating bitset");
	}
	
	private static void assertTrue(boolean condition, String error) {
		if (!condition) throw new RuntimeException(error);
	}
	
	public static void main(String args[]) {
		testBit64IntSet_iterator();
	}
}
