package peggy.pb;

import java.util.*;

public class WeightMap<N>{
	private Map<Variable<N>,Integer> weightMap;
	
	public WeightMap(){
		weightMap = new HashMap<Variable<N>,Integer>();
	}
	
	public WeightMap(WeightMap<N> toCopy){
		weightMap = new HashMap<Variable<N>,Integer>(toCopy.weightMap);
	}
	
	/** This will be an unmodifiable Collection.
	 */
	public Collection<Variable<N>> getAssignedVariables(){
		return Collections.unmodifiableSet(weightMap.keySet());
	}

	
	/** If the given weight is 0, the variable is unassigned.
	 */
	public void assignWeight(Variable<N> v, int weight){
		if (v==null)
			throw new RuntimeException("Null variable");
		if (weight==0)
			weightMap.remove(v);
		else
			weightMap.put(v, weight);
	}

	
	/** If the given variable is not assigned, 
	 *  then a weight of 0 is returned.
	 */
	public int getWeight(Variable<N> v){
		if (weightMap.containsKey(v))
			return weightMap.get(v);
		return 0;
	}
	
	
	public boolean isAssigned(Variable<N> v){
		return weightMap.containsKey(v);
	}
}
