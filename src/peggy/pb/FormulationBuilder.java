package peggy.pb;

/**
 * A formulation builder is responsible for filling in the variables 
 * and constraints for a given formulation type. 
 */
public interface FormulationBuilder<V, N, NUMBER extends Number, D extends ExpressionDigraph<V,N>> {
	public D getGraph();
	public CostModel<N, NUMBER> getCostModel();
	
	/** 
	 * Returns true iff the building was successful
	 */ 
	public boolean buildFormulation(PseudoBooleanFormulation<N> pbf);
}
