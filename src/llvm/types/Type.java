package llvm.types;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The parent class of all Type objects.
 * Types are hashable and equallable, and may be interned for memory efficiency.
 */
public abstract class Type {
	private static Map<Type,Type> type2interntype = 
		new HashMap<Type,Type>();
	private static Map<FloatingPointType.Kind, FloatingPointType> floatkind2floatype =
		new EnumMap<FloatingPointType.Kind, FloatingPointType>(FloatingPointType.Kind.class);
	private static final Map<Integer,IntegerType> int2inttype = 
		new HashMap<Integer,IntegerType>();
	public static final IntegerType BOOLEAN_TYPE = getIntegerType(1);

	public static final Type METADATA_TYPE = new Type() {
		public boolean isPrimitive() {return true;}
		public boolean isFirstClass() {return true;}
		public boolean isMetadata() {return true;}
		protected String toString(int depth) {
			return "metadata";
		}
		public boolean equalsType(Type o) {return o.isMetadata();}
		protected int hashCode(int depth) {
			return 543875634;
		}
		protected void ensureSized() {throw new IllegalStateException();}
	};
	
	public static final Type LABEL_TYPE = new Type() {
		public boolean isPrimitive() {return true;}
		public boolean isFirstClass() {return true;}
		public boolean isLabel() {return true;}
		protected String toString(int depth) {
			return "label";
		}
		public boolean equalsType(Type o) {return o.isLabel();}
		protected int hashCode(int depth) {
			return 281;
		}
		protected void ensureSized() {throw new IllegalStateException();}
	};
	
	public static final Type VOID_TYPE = new Type() {
		public boolean isPrimitive() {return true;}
		public boolean isVoid() {return true;}
		protected String toString(int depth) {
			return "void";
		}
		public boolean equalsType(Type o) {return o.isVoid();}
		protected int hashCode(int depth) {
			return 277;
		}
		protected void ensureSized() {throw new IllegalStateException();}
	};
	
	public static IntegerType getIntegerType(int width) {
		if (width <= 0 || width >= (1<<23))
			throw new IllegalArgumentException("width is out of bounds");
		IntegerType type = int2inttype.get(width);
		if (type == null) {
			type = new IntegerType(width);
			int2inttype.put(width, type);
		}
		return type;
	}
	
	public static FloatingPointType getFloatingPointType(FloatingPointType.Kind kind) {
		if (kind == null)
			throw new NullPointerException("kind is null");
		FloatingPointType type = floatkind2floatype.get(kind);
		if (type == null) {
			type = new FloatingPointType(kind);
			floatkind2floatype.put(kind, type);
		}
		return type;
	}
	
	public <T extends Type> T intern() {
		if (type2interntype.containsKey(this)) {
			return (T)type2interntype.get(this);
		} else {
			type2interntype.put(this, this);
			return (T)this;
		}
	}
	
	public boolean isHolder() {return false;}
	public HolderType getHolderSelf() {throw new UnsupportedOperationException();}

	public Iterator<Type> getSubtypes() {
		return new TypeIterator();
	}
	
	public boolean isPrimitive() {return false;}
	public boolean isDerived() {return false;}
	public boolean isFirstClass() {return false;}
	
	public boolean isInteger() {return false;}
	public IntegerType getIntegerSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFloatingPoint() {return false;}
	public FloatingPointType getFloatingPointSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFunction() {return false;}
	public FunctionType getFunctionSelf() {throw new UnsupportedOperationException();}

	public boolean isComposite() {return false;}
	public CompositeType getCompositeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isLabel() {return false;}
	public boolean isVoid() {return false;}
	public boolean isMetadata() {return false;}

	public boolean isOpaque() {return false;}
	public OpaqueType getOpaqueSelf() {throw new UnsupportedOperationException();}

	protected abstract void ensureSized();
	public boolean hasTypeSize() {return false;}
	/** Type size is in bits! */
	public long getTypeSize() {throw new UnsupportedOperationException();}

	public boolean isVectorOfFloatingPoint() {return false;}
	public boolean isVectorOfInteger() {return false;}
	
	private static final int TOSTRING_THRESHOLD = 7;
	protected abstract String toString(int depth);
	public final String toString() {
		return this.toString(TOSTRING_THRESHOLD);
	}
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof Type))
			return false;
		return this.equalsType((Type)o);
	}
	public abstract boolean equalsType(Type t);
	
	private Integer hashCode_cache = null;
	public static final int HASHCODE_THRESHOLD = 5;
	protected abstract int hashCode(int depth);
	public final int hashCode() {
		if (this.hashCode_cache != null)
			return this.hashCode_cache;
		int result = this.hashCode(HASHCODE_THRESHOLD);
		this.hashCode_cache = result;
		return result;
	}
}
