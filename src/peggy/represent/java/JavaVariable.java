package peggy.represent.java;

/**
 * This class is used to implement the V parameter of a CFG for Java programs.
 * A JavaVariable is one of:
 * 1) sigma return variable
 * 2) value return variable
 * 3) counter return variable
 * 4) dummy variable
 * 5) this reference variable
 * 6) method parameter variable
 */
public abstract class JavaVariable {
	public boolean isReturn() {return false;}
	public boolean isSigma() {return false;}
	public boolean isDummy() {return false;}
	
	public boolean isThis() {return false;}
	public ThisJavaVariable getThisSelf() {throw new UnsupportedOperationException();}

	public boolean isArgument() {return false;}
	public ArgumentJavaVariable getArgumentSelf() {throw new UnsupportedOperationException();}
	
	public abstract int hashCode();
	public abstract boolean equals(Object o);
	public abstract String toString();
	
	public static final JavaVariable RETURN = 
		new JavaVariable() {
			public boolean isReturn() {return true;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof JavaVariable))
					return false;
				return ((JavaVariable)o).isReturn();
			}
			public int hashCode() {return 543879203;}
			public String toString() {return "ReturnVariable";}
		};

	public static final JavaVariable SIGMA = 
		new JavaVariable() {
			public boolean isSigma() {return true;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof JavaVariable))
					return false;
				return ((JavaVariable)o).isSigma();
			}
			public int hashCode() {return 439081754;}
			public String toString() {return "SigmaVariable";}
		};

	private static int NEXT = 1;
	public static JavaVariable getDummyVariable() {
		final int id = (NEXT++);
		return new JavaVariable() {
			private int myid = id;
			public boolean isDummy() {return true;}
			public boolean equals(Object o) {return o == this;}
			public int hashCode() {return this.myid*101*17;}
			public String toString() {return "DummyVariable[" + this.myid + "]";}
		};
	}
}
