package util.integer;

/** Implementing this interface allows an object to be the target of
 *  the "foreach" statement.
 */
public interface IntIterable extends Iterable<Integer> {

   /**
    * Returns an iterator over a set of elements of type T.
    *
    * @return an Iterator.
    */
   IntIterator iterator();
}