package peggy.pb;

/** A constraint is an instance of a PB solver constraint.
 *  It has the following general form:
 *  
 *    w1*x1 + w2*x2 + ... + wN*xN    OP   RHS
 *  
 *  The xI are variables, the wI are their weights
 *  (the (wI,xI) pairs are given by a WeightMap), the 
 *  OP is given by an instance of Constraint.Relation 
 *  (one of =, <=, >=), and the RHS is an integer.
 */
public class Constraint<N>{
	public static enum Relation{
		EQUAL("="),
		LESS_EQUAL("<="),
		MORE_EQUAL(">=");
		
		private String label;
		private Relation(String _label){
			label = _label;
		}
		
		public String getLabel(){
			return label;
		}
		
		public String toString(){
			return label;
		}
	}
	///////////////////////////
	private int RHS;
	private Relation relation;
	private WeightMap<N> LHS;
	private String comment;
	
	public Constraint(WeightMap<N> wm, Relation r, int _RHS){
		this(wm, r, _RHS, null);
	}
	
	/** The WeightMap is copied out.
	 */
	public Constraint(WeightMap<N> wm, Relation r, int _RHS, String _comment){
		comment = _comment; 
		LHS = new WeightMap<N>(wm);
		relation = r;
		RHS = _RHS;
	}
	
	public Constraint(){
		RHS = 0;
		relation = Relation.EQUAL;
		LHS = new WeightMap<N>();
	}
	
	public WeightMap<N> getWeightMap(){
		return LHS;
	}
	
	public void setRelation(Relation r){
		relation = r;
	}
	public Relation getRelation(){
		return relation;
	}
	
	public String getCommentString(){return comment;}
	public void setCommentString(String _comment){comment = _comment;}
	
	public void setRHS(int _RHS){
		RHS = _RHS;
	}
	public int getRHS(){
		return RHS;
	}
}

	