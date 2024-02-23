package llvm.types;

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.LinkedList;

import util.pair.Pair;

/**
 * This class implements an efficient equality test between LLVM Type
 * objects. This test is only complicated in the presence of recursive types.
 * 
 * This class also implements a method to output a DOT representation of an
 * LLVM Type object. This representation will be based on the reference
 * structure of the Type, not on the smallest equivalent Type structure.
 */
public class TypeEqualityCheck {
	/**
	 * This class represents a set of pairs of a given type T.
	 * Membership in a set/pair is tested by reference equality 
	 * (hence 'Identity' pair set).
	 * The only operations on the set are adding pairs and testing
	 * for the membership of a given pair.
	 */
	public static class IdentityPairSet<T> {
		private final IdentityHashMap<T,IdentityHashMap<T,Void>> pairs =
			new IdentityHashMap<T,IdentityHashMap<T,Void>> ();
		public IdentityPairSet() {}
		public boolean addPair(T t1, T t2) {
			boolean result;
			IdentityHashMap<T,Void> matches = pairs.get(t1);
			if (matches == null) {
				matches = new IdentityHashMap<T,Void>();
				pairs.put(t1, matches);
			}
			
			if (matches.containsKey(t2)) {
				result = false;
			} else {
				result = true;
				matches.put(t2, null);
			}
			return result;
		}
		public boolean hasPair(T t1, T t2) {
			return pairs.containsKey(t1) && 
				pairs.get(t1).containsKey(t2);
		}
	}
	
//	/**
//	 * Performs efficient type equality check. This is correct in the presence
//	 * of recursive types.
//	 */
//	public static boolean equals2(Type t1, Type t2) {
//		return equals_helper(t1, t2, new IdentityPairSet<Type>());
//	}
//	private static boolean equals_helper(
//			Type t1, Type t2, IdentityPairSet<Type> pairs) {
//		if (t1.isComposite()) {
//			if (t2.isComposite()) {
//				// both are composite
//				CompositeType c1 = t1.getCompositeSelf();
//				CompositeType c2 = t2.getCompositeSelf();
//				if (c1.isArray() && c2.isArray()) {
//					// both are arrays
//					ArrayType a1 = c1.getArraySelf();
//					ArrayType a2 = c2.getArraySelf();
//					if (a1 == a2) return true;
//					if (pairs.hasPair(a1, a2)) return true;
//					
//					if (a1.getNumElements() != a2.getNumElements())
//						return false;
//					pairs.addPair(a1, a2);
//					return equals_helper(a1.getElementType(), a2.getElementType(), pairs);
//				}
//				else if (c1.isVector() && c2.isVector()) {
//					// both are vectors
//					VectorType v1 = c1.getVectorSelf();
//					VectorType v2 = c2.getVectorSelf();
//					if (v1 == v2) return true;
//
//					if (pairs.hasPair(v1, v2)) return true;
//
//					if (!v1.getNumElements().equals(v2.getNumElements()))
//						return false;
//					pairs.addPair(v1, v2);
//					return equals_helper(v1.getElementType(), v2.getElementType(), pairs);
//				}
//				else if (c1.isPointer() && c2.isPointer()) {
//					// both are pointers
//					PointerType p1 = c1.getPointerSelf();
//					PointerType p2 = c2.getPointerSelf();
//					if (p1 == p2) return true;
//					if (pairs.hasPair(p1, p2)) return true;
//					
//					if (p1.getAddressSpace() != p2.getAddressSpace())
//						return false;
//					pairs.addPair(p1, p2);
//					return equals_helper(p1.getPointeeType(), p2.getPointeeType(), pairs);
//				} 
//				else if (c1.isStructure() && c2.isStructure()) {
//					// both are structures
//					StructureType s1 = c1.getStructureSelf();
//					StructureType s2 = c2.getStructureSelf();
//					if (s1 == s2) return true;
//					if (pairs.hasPair(s1, s2)) return true;
//					
//					if (s1.isPacked() != s2.isPacked() || 
//						s1.getNumFields() != s2.getNumFields()) 
//						return false;
//					pairs.addPair(s1, s2);
//					
//					for (int i = 0; i < s1.getNumFields(); i++) {
//						if (!equals_helper(
//								s1.getFieldType(i),
//								s2.getFieldType(i),
//								pairs))
//							return false;
//					}
//					return true;
//				} 
//				else return false;
//			} else return false;
//		} else if (t1.isFunction()) {
//			if (t2.isFunction()) {
//				// both are functions
//				FunctionType f1 = t1.getFunctionSelf();
//				FunctionType f2 = t2.getFunctionSelf();
//				if (f1 == f2) return true;
//				if (pairs.hasPair(f1, f2)) return true;
//				
//				if (f1.isVararg() != f2.isVararg() ||
//					f1.getNumParams() != f2.getNumParams())
//					return false;
//				pairs.addPair(f1, f2);
//				
//				if (!equals_helper(
//						f1.getReturnType(),
//						f2.getReturnType(),
//						pairs))
//					return false;
//				
//				for (int i = 0; i < f1.getNumParams(); i++) {
//					if (!equals_helper(
//							f1.getParamType(i),
//							f2.getParamType(i),
//							pairs))
//						return false;
//				}
//				return true;
//			} else return false;
//		}
//		else {
//			// t1 is not a composite or function type,
//			// simple type equality test should complete quickly
//			return t1.equalsType(t2);
//		}
//	}
	
	
	/**
	 * Implements the rooted-DAG-based equality testing for types.
	 * This is a worklist implementation, so it uses no recursion.
	 */
	public static boolean equals(Type left, Type right) {
		IdentityPairSet<Type> seen = new IdentityPairSet<Type>();
		LinkedList<Pair<Type,Type>> worklist = new LinkedList<Pair<Type,Type>>();
		worklist.add(new Pair<Type,Type>(left, right));
		
		while (worklist.size() > 0) {
			final Pair<Type,Type> next = worklist.removeFirst();
			final Type lhs = next.getFirst();
			final Type rhs = next.getSecond();
			if (seen.hasPair(lhs, rhs))
				continue;
			seen.addPair(lhs, rhs);
			
			if (lhs.isComposite() && rhs.isComposite()) {
				CompositeType clhs = lhs.getCompositeSelf();
				CompositeType crhs = rhs.getCompositeSelf();
				if (clhs.isArray() && crhs.isArray()) {
					ArrayType alhs = clhs.getArraySelf();
					ArrayType arhs = crhs.getArraySelf();
					if (!alhs.getNumElements().equals(arhs.getNumElements()))
						return false;
					worklist.addLast(new Pair<Type,Type>(alhs.getElementType(), arhs.getElementType()));
				} else if (clhs.isVector() && crhs.isVector()) {
					VectorType vlhs = clhs.getVectorSelf();
					VectorType vrhs = crhs.getVectorSelf();
					if (!vlhs.getNumElements().equals(vrhs.getNumElements()))
						return false;
					worklist.addLast(new Pair<Type,Type>(vlhs.getElementType(), vrhs.getElementType()));
				} else if (clhs.isPointer() && crhs.isPointer()) {
					PointerType plhs = clhs.getPointerSelf();
					PointerType prhs = crhs.getPointerSelf();
					if (plhs.getAddressSpace() != prhs.getAddressSpace())
						return false;
					worklist.addLast(new Pair<Type,Type>(plhs.getPointeeType(), prhs.getPointeeType()));
				} else if (clhs.isStructure() && crhs.isStructure()) {
					StructureType slhs = clhs.getStructureSelf();
					StructureType srhs = crhs.getStructureSelf();
					if (slhs.isPacked() != srhs.isPacked() || 
						slhs.getNumFields() != srhs.getNumFields())
						return false;
					for (int i = 0; i < slhs.getNumFields(); i++) {
						worklist.addLast(new Pair<Type,Type>(slhs.getFieldType(i), srhs.getFieldType(i)));
					}
				} else {
					return false;
				}
			} else if (lhs.isFunction() && rhs.isFunction()) {
				FunctionType flhs = lhs.getFunctionSelf();
				FunctionType frhs = rhs.getFunctionSelf();
				if (flhs.isVararg() != frhs.isVararg() ||
					flhs.getNumParams() != frhs.getNumParams())
					return false;
				for (int i = 0; i < flhs.getNumParams(); i++) {
					worklist.addLast(new Pair<Type,Type>(flhs.getParamType(i), frhs.getParamType(i)));
				}
				worklist.addLast(new Pair<Type,Type>(flhs.getReturnType(), frhs.getReturnType()));
			} else {
				// has no subtypes, do direct comparison
				if (!lhs.equalsType(rhs))
					return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * If this type is a holder type, will return the inner type.
	 * Else returns the given type. 
	 */
	private static Type unfold(Type t) {
		if (t.isInteger()) return t.getIntegerSelf();
		else if (t.isFloatingPoint()) return t.getFloatingPointSelf();
		else if (t.isFunction()) return t.getFunctionSelf();
		else if (t.isOpaque()) return t.getOpaqueSelf();
		else if (t.isComposite()) {
			CompositeType c = t.getCompositeSelf();
			if (c.isArray()) return c.getArraySelf();
			else if (c.isVector()) return c.getVectorSelf();
			else if (c.isPointer()) return c.getPointerSelf();
			else if (c.isStructure()) return c.getStructureSelf();
			else return c;
		}
		else return t;
	}
	
	
	/**
	 * Prints out a Dot representation of the given type to the 
	 * given PrintStream.
	 * Will not close the stream.
	 */
	public static void type2dot(Type root, PrintStream out) {
		out.println("digraph {");
		
		IdentityHashMap<Type,String> node2name = 
			new IdentityHashMap<Type,String>();
		LinkedList<Type> worklist = new LinkedList<Type>();
		root = unfold(root);
		worklist.addLast(root);
		
		while (!worklist.isEmpty()) {
			Type next = worklist.removeFirst();
			if (node2name.containsKey(next))
				continue;
			String name = "node" + System.identityHashCode(next);
			String color = (root==next ? "red" : "black");
			node2name.put(next, name);
			
			if (next.isInteger()) {
				out.println("   " + name + " [color=" + color + ", label=\"int[" + next.getIntegerSelf().getWidth() + "]\"];");
			}
			else if (next.isFloatingPoint()) {
				out.println("   " + name + " [color=" + color + ", label=\"float[" + next.getFloatingPointSelf().getKind() + "]\"];");
			}
			else if (next.isLabel()) {
				out.println("   " + name + " [color=" + color + ", label=\"label\"];");
			}
			else if (next.isVoid()) {
				out.println("   " + name + " [color=" + color + ", label=\"void\"];");
			}
			else if (next.isMetadata()) {
				out.println("   " + name + " [color=" + color + ", label=\"metadata\"];");
			}
			else if (next.isOpaque()) {
				out.println("   " + name + " [color=" + color + ", label=\"opaque\"];");
			}
			else if (next.isFunction()) {
				FunctionType f = next.getFunctionSelf();
				out.println("   " + name + " [color=" + color + ", label=\"func[" + f.isVararg() + "]\"];");
				Type r = unfold(f.getReturnType());
				worklist.addLast(r);
				out.println("   " + name + " -> node" + System.identityHashCode(r) + " [label=\"0\"]; ");
				for (int i = 0; i < f.getNumParams(); i++) {
					Type p = unfold(f.getParamType(i));
					worklist.addLast(p);
					out.println("   " + name + " -> node" + System.identityHashCode(p) + " [label=\"" + (i+1) + "\"];");
				}
			}
			else if (next.isComposite()) {
				CompositeType c = next.getCompositeSelf();
				if (c.isArray()) {
					ArrayType a = c.getArraySelf();
					out.println("   " + name + " [color=" + color + ", label=\"array[" + a.getNumElements() + "]\"];");
					Type e = unfold(a.getElementType());
					worklist.addLast(e);
					out.println("   " + name + " -> node" + System.identityHashCode(e) + " [label=\"0\"];");
				}
				else if (c.isVector()) {
					VectorType v = c.getVectorSelf();
					out.println("   " + name + " [color=" + color + ", label=\"vector[" + v.getNumElements() + "]\"];");
					Type e = unfold(v.getElementType());
					worklist.addLast(e);
					out.println("   " + name + " -> node" + System.identityHashCode(e) + " [label=\"0\"];");
				}
				else if (c.isPointer()) {
					PointerType p = c.getPointerSelf();
					out.println("   " + name + " [color=" + color + ", label=\"pointer[" + p.getAddressSpace() + "]\"];");
					Type e = unfold(p.getPointeeType());
					worklist.addLast(e);
					out.println("   " + name + " -> node" + System.identityHashCode(e) + " [label=\"0\"];");
				}
				else if (c.isStructure()) {
					StructureType s = c.getStructureSelf();
					out.println("   " + name + " [color=" + color + ", label=\"struct[" + s.isPacked() + "]\"];");
					for (int i = 0; i < s.getNumFields(); i++) {
						Type f = unfold(s.getFieldType(i));
						worklist.addLast(f);
						out.println("   " + name + " -> node" + System.identityHashCode(f) + " [label=\"" + i + "\"];");
					}
				}
				else
					throw new RuntimeException("Forgot to implement: " + c.getClass());
			} 
			else 
				throw new RuntimeException("Forgot to implement: " + next.getClass());
		}
		
		out.println("}");
	}
}
