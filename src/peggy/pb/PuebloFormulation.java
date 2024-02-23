package peggy.pb;

import java.io.File;
import java.io.IOException;

public class PuebloFormulation<N> extends BackedPseudoBooleanFormulation<N> {
	private int varNameCounter = 0;
	
	public PuebloFormulation(File _backingFile) throws IOException {
		super(_backingFile);
	}
	public PuebloFormulation(File _backingFile, int _maxSize) throws IOException {
		super(_backingFile, _maxSize);
	}
	
	public Variable<N> getFreshVariable(N node) {
		return new Variable<N>("N" + (varNameCounter++), node);
	}
	
	public static String escape(String str) {
		StringBuffer result = new StringBuffer(str.length()*2);
		char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n') {
				result.append("\\n");
			} else if (c == '\r') {
				result.append("\\r");
			} else if (c < 32 || c > 127) {
				result.append("\\u" + hex[(c>>12)&0xF] + hex[(c>>8)&0xF] + hex[(c>>4)&0xF] + hex[c&0xF]);
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}
	
	protected boolean writeConstraint(Constraint<N> cons, String comment) {
		Constraint.Relation relation = cons.getRelation();
		int RHS = cons.getRHS();
		WeightMap<N> map = cons.getWeightMap();
		
		if (map.getAssignedVariables().size() == 1 &&
			relation.equals(Constraint.Relation.LESS_EQUAL) &&
			RHS >= 1 &&
			map.getWeight(map.getAssignedVariables().iterator().next()) == 1) {
			// 1*A <= N (>= 1), worthless constraint 
			return false;
		} else if (map.getAssignedVariables().size() == 0) {
			// either invalid or trivially true
			switch (relation) {
			case EQUAL: 
				if (RHS != 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;
				
			case LESS_EQUAL:
				if (RHS < 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;
				
			case MORE_EQUAL:
				if (RHS > 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;
			}
		}

		if (comment!=null) {
			comment = escape(comment);
			this.backingStream.println("* "+comment);
		}
		
		for (Variable<N> var : map.getAssignedVariables()) {
			int weight = map.getWeight(var);
			if (weight > 0)
				this.backingStream.print("+"+weight+" "+var.name+" ");
			else
				this.backingStream.print(weight+" "+var.name+" ");
		}
		this.backingStream.print(relation.getLabel()+" ");
		this.backingStream.print(RHS);
		this.backingStream.println(" ;");
		
		return true;
	}
	protected void writeObjectiveFunction(ObjectiveFunction<N> objFunction){
		this.backingStream.print(objFunction.isMinimization() ? "min: " : "max: ");
		WeightMap<N> objWM = objFunction.getWeightMap();
		for (Variable<N> var : objWM.getAssignedVariables()){
			int weight = objWM.getWeight(var);
			if (weight>0)
				this.backingStream.print("+"+weight+" "+var.name+" ");
			else
				this.backingStream.print(weight+" "+var.name+" ");
		}
		this.backingStream.println(";");
	}
}
