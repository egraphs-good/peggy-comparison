package peggy.pb;

public class ObjectiveFunction<N> {
	private boolean minimize;
	private WeightMap<N> weightMap;
	
	public ObjectiveFunction(boolean _minimize){
		weightMap = new WeightMap<N>();
		minimize = _minimize;
	}
	
	/** This will set whether this objective function
	 *  is a minimization or a maximization.
	 *  Passing in true will be a min, false will be a max.
	 */
	public void setMinMax(boolean _minimize){
		minimize = _minimize;
	}
	
	/** Return value of false implies it's a maximization.
	 */
	public boolean isMinimization(){
		return minimize;
	}
	
	public WeightMap<N> getWeightMap(){
		return weightMap;
	}
}
