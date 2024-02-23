package peggy.analysis.llvm.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A PEGType describes the type of a node in a PEG.
 * It can be a tuple, a disjoint union, a boolean, an iteration value,
 * or a domain-specific type as defined by the D parameter.
 */
public abstract class PEGType<D> {
	public final boolean equals(Object o) {
		if (!(o instanceof PEGType))
			return false;
		return equalsPEGType((PEGType<D>)o);
	}
	public abstract boolean equalsPEGType(PEGType<D> type);
	public abstract int hashCode();
	public abstract String toString();
	
	public boolean isBoolean() {return false;}
	public static <D> PEGType<D> makeBoolean() {return BOOLEAN;}
	private static PEGType BOOLEAN = new PEGType() {
		public boolean isBoolean() {return true;}
		public boolean equalsPEGType(PEGType type) {return type.isBoolean();}
		public int hashCode() {return 54397543;}
		public String toString() {return "BOOL";}
	};
	
	public boolean isDomain() {return false;}
	public D getDomain() {throw new UnsupportedOperationException();}
	public static <D> PEGType<D> makeDomain(final D domain) {
		return new PEGType<D>() {
			public boolean isDomain() {return true;}
			public D getDomain() {return domain;}
			public boolean equalsPEGType(PEGType<D> type) {
				return type.isDomain() && type.getDomain().equals(domain);
			}
			public int hashCode() {return domain.hashCode()*3;}
			public String toString() {return domain.toString();}
		};
	}
	
	public boolean isTuple() {return false;}
	public int getNumTupleElements() {throw new UnsupportedOperationException();}
	public PEGType<D> getTupleElement(int i) {throw new UnsupportedOperationException();}
	public static <D> PEGType<D> makeTupleType(
			PEGType<D>... elements) {
		return makeTupleType(Arrays.asList(elements));
	}
	public static <D> PEGType<D> makeTupleType(
			List<? extends PEGType<D>> elements) {
		final List<PEGType<D>> mylist = new ArrayList<PEGType<D>>(elements);
		return new PEGType<D>() {
			public boolean isTuple() {return true;}
			public int getNumTupleElements() {return mylist.size();}
			public PEGType<D> getTupleElement(int i) {return mylist.get(i);}
			public boolean equalsPEGType(PEGType<D> type) {
				if (!(type.isTuple() && type.getNumTupleElements() == mylist.size()))
					return false;
				for (int i = 0; i < mylist.size(); i++) {
					if (!type.getTupleElement(i).equalsPEGType(mylist.get(i)))
						return false;
				}
				return true;
			}
			public int hashCode() {
				int result = 13;
				for (int i = 0; i < mylist.size(); i++) {
					result = mylist.get(i).hashCode()*5 - result;
				}
				return result;
			}
			public String toString() {
				StringBuilder builder = new StringBuilder(100);
				builder.append('(');
				for (int i = 0; i < mylist.size(); i++) {
					if (i>0) builder.append(',');
					builder.append(mylist.get(i));
				}
				return builder.append(')').toString();
			}
		};
	}
	
	public boolean isDisjointUnion() {return false;}
	public PEGType<D> getDisjointUnionLeft() {throw new UnsupportedOperationException();}
	public PEGType<D> getDisjointUnionRight() {throw new UnsupportedOperationException();}
	public static <D> PEGType<D> makeDisjointUnion(
			final PEGType<D> left, final PEGType<D> right) {
		return new PEGType<D>() {
			public boolean isDisjointUnion() {return true;}
			public PEGType<D> getDisjointUnionLeft() {return left;}
			public PEGType<D> getDisjointUnionRight() {return right;}
			public boolean equalsPEGType(PEGType<D> type) {	
				return type.isDisjointUnion() &&
					type.getDisjointUnionLeft().equalsPEGType(left) &&
					type.getDisjointUnionRight().equalsPEGType(right);
			}
			public int hashCode() {
				return left.hashCode()*7 + right.hashCode()*11;
			}
			public String toString() {
				return left.toString() + "|" + right.toString();
			}
		};
	}
	
	public boolean isIterationValue() {return false;}
	private static final PEGType ITERATION_VALUE = new PEGType() {
		public boolean isIterationValue() {return true;}
		public boolean equalsPEGType(PEGType type) {
			return type.isIterationValue();
		}
		public int hashCode() {return 17;}
		public String toString() {return "ITERATION_VALUE";}
	};
	public static <D> PEGType<D> makeIterationValue() {
		return ITERATION_VALUE;
	}
}
