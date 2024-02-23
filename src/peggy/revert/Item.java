package peggy.revert;

/**
 * An Item is a disjoint union between 3 types: label, parameter, or variable.
 */
public abstract class Item<L,P,V> {
	public boolean isVariable() {return false;}
	public V getVariable() {throw new UnsupportedOperationException();}

	public boolean isLabel() {return false;}
	public L getLabel() {throw new UnsupportedOperationException();}

	public boolean isParameter() {return false;}
	public P getParameter() {throw new UnsupportedOperationException();}

	public final boolean equals(Object o) {
		if (!(o instanceof Item))
			return false;
		return equalsItem((Item<L,P,V>)o);
	}
	protected abstract boolean equalsItem(Item<L,P,V> item);
	public abstract int hashCode();
	public abstract String toString();

	public static <L,P,V> Item<L,P,V> getVariable(final V var) {
		return new Item<L,P,V>() {
			public boolean isVariable() {return true;}
			public V getVariable() {return var;}
			protected boolean equalsItem(Item<L,P,V> item) {
				if (!item.isVariable())
					return false;
				return this.getVariable().equals(item.getVariable());
			}
			public int hashCode() {return this.getVariable().hashCode()*7;}
			public String toString() {return "V" + this.getVariable().hashCode();}
		};
	}
	public static <L,P,V> Item<L,P,V> getParameter(final P param) {
		return new Item<L,P,V>() {
			public boolean isParameter() {return true;}
			public P getParameter() {return param;}
			protected boolean equalsItem(Item<L,P,V> item) {
				if (!item.isParameter())
					return false;
				return this.getParameter().equals(item.getParameter());
			}
			public int hashCode() {return this.getParameter().hashCode()*11;}
			public String toString() {return this.getParameter().toString();}
		};
	}
	public static <L,P,V> Item<L,P,V> getLabel(final L label) {
		return new Item<L,P,V>() {
			public boolean isLabel() {return true;}
			public L getLabel() {return label;}
			protected boolean equalsItem(Item<L,P,V> item) {
				if (!item.isLabel())
					return false;
				return this.getLabel().equals(item.getLabel());
			}
			public int hashCode() {return this.getLabel().hashCode()*13;}
			public String toString() {return this.getLabel().toString();}
		};
	}
}
