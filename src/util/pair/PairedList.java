package util.pair;

public interface PairedList<F, S> {
    int size();
    boolean isEmpty();
    void add(F first, S second);
    void addAll(PairedList<? extends F, ? extends S> that);
    void addAll(int index, PairedList<? extends F, ? extends S> that);
    void clear();
    boolean equals(Object that);
    boolean equals(PairedList that);
    int hashCode();
    F getFirst(int index);
    S getSecond(int index);
    F setFirst(int index, F first);
    S setSecond(int index, S second);
    void set(int index, F first, S second);
    void add(int index, F first, S second);
    void removeAt(int index);
    void removeLast();
    PairedList<F,S> subList(int fromIndex, int toIndex);
    S findSecond(F first);
    F findFirst(S second);
    int indexOfFirst(F first);
    int indexOfSecond(S second);
    S removeFirst(F first);
    F removeSecond(S second);
    boolean remove(F first, S second);
    boolean containsFirst(F first);
    boolean containsSecond(S second);
}
