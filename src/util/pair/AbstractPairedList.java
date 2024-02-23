package util.pair;

public abstract class AbstractPairedList<F, S> implements PairedList<F, S> {
	public void add(F first, S second) {add(size(), first, second);}

	public void addAll(PairedList<? extends F, ? extends S> that) {
		for (int i = that.size(); i-- != 0; )
			add(that.getFirst(i), that.getSecond(i));
	}

	public void addAll(int index, PairedList<? extends F, ? extends S> that) {
		for (int i = 0; i != that.size(); i++)
			add(that.getFirst(i), that.getSecond(i));
	}

	public boolean equals(PairedList that) {
		if (size() != that.size())
			return false;
		for (int i = size(); i-- != 0; )
			if (!getFirst(i).equals(that.getFirst(i))
					|| !getSecond(i).equals(that.getSecond(i)))
				 return false;
		return true;
	}

	public void set(int index, F first, S second) {
		setFirst(index, first);
		setSecond(index, second);
	}
	
	public S findSecond(F first) {
		int index = indexOfFirst(first);
		return index == -1 ? null : getSecond(index);
	}
	
	public F findFirst(S second) {
		int index = indexOfSecond(second);
		return index == -1 ? null : getFirst(index);
	}
	
	public int indexOfFirst(F first) {
		for (int i = 0; i < size(); i++)
			if (first == null ? getFirst(i) == null : first.equals(getFirst(i)))
				return i;
		return -1;
	}
	
	public int indexOfSecond(S second) {
		for (int i = 0; i < size(); i++)
			if (second == null ? getSecond(i) == null
					: second.equals(getSecond(i)))
				return i;
		return -1;
	}
	
	public S removeFirst(F first) {
		int index = indexOfFirst(first);
		if (index == -1)
			return null;
		S second = getSecond(index);
		removeAt(index);
		return second;
	}
	
	public F removeSecond(S second) {
		int index = indexOfSecond(second);
		if (index == -1)
			return null;
		F first = getFirst(index);
		removeAt(index);
		return first;
	}
	
	public boolean remove(F first, S second) {
		for (int i = 0; i < size(); i++)
			if (first == null ? getFirst(i) == null : first.equals(getFirst(i)))
				if (second == null ? getSecond(i) == null
						: second.equals(getSecond(i))) {
					removeAt(i);
					return true;
				}
		return false;
	}
	
	public void removeLast() {removeAt(size() - 1);}
	
	public boolean containsFirst(F first) {return indexOfFirst(first) != -1;}
	
	public boolean containsSecond(S second) {
		return indexOfSecond(second) != -1;
	}
	
	public String toString() {
		if (isEmpty())
			return "[]";
		StringBuilder string = new StringBuilder("[(");
		string.append(getFirst(0));
		string.append(", ");
		string.append(getSecond(0));
		string.append(")");
		for (int i = 1; i < size(); i++) {
			string.append(", (");
			string.append(getFirst(i));
			string.append(", ");
			string.append(getSecond(i));
			string.append(")");
		}
		string.append(']');
		return string.toString();
	}
}