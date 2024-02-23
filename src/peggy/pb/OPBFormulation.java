package peggy.pb;

import java.io.File;
import java.io.IOException;

public abstract class OPBFormulation<N> extends BackedPseudoBooleanFormulation<N> {
	private int varNameCounter = 0;
	
	public OPBFormulation(File _backingFile) throws IOException {
		super(_backingFile);
	}
	public OPBFormulation(File _backingFile, int _maxSize) throws IOException {
		super(_backingFile, _maxSize);
	}
	
	public Variable<N> getFreshVariable(N node) {
		return new Variable<N>("x" + (varNameCounter++), node);
	}
	
	private static String escape(String str) {
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
	
	protected abstract void wroteConstraint(Constraint<N> cons);
	
	protected boolean writeConstraint(Constraint<N> cons, String comment) {
		if (cons.getWeightMap().getAssignedVariables().size() == 1 &&
			cons.getRelation().equals(Constraint.Relation.LESS_EQUAL) &&
			cons.getRHS() >= 1 &&
			cons.getWeightMap().getWeight(cons.getWeightMap().getAssignedVariables().iterator().next()) == 1) {
			// 1*A <= N (>= 1), worthless constraint 
			return false;
		} else if (cons.getWeightMap().getAssignedVariables().size() == 0) {
			// either invalid or trivially true
			switch (cons.getRelation()) {
			case EQUAL: 
				if (cons.getRHS() != 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;

			case LESS_EQUAL:
				if (cons.getRHS() < 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;

			case MORE_EQUAL:
				if (cons.getRHS() > 0)
					throw new IllegalArgumentException("Invalid constraint");
				return false;
			}
		}
		
		if (comment!=null) {
			comment = escape(comment);
			backingStream.println("* "+comment);
		}

		boolean invert = cons.getRelation().equals(Constraint.Relation.LESS_EQUAL);
		for (Variable<N> var : cons.getWeightMap().getAssignedVariables()) {
			int weight = cons.getWeightMap().getWeight(var);
			if (invert) weight = -weight;
			
			if (weight > 0)
				backingStream.print("+"+weight+"*"+var.name+" ");
			else
				backingStream.print(weight+"*"+var.name+" ");
		}
		backingStream.print((invert ? Constraint.Relation.MORE_EQUAL : cons.getRelation().getLabel())+" ");
		backingStream.print(invert ? -cons.getRHS() : cons.getRHS());
		backingStream.println(" ;");
		
		this.wroteConstraint(cons);
		
		return true;
	}
	protected void writeObjectiveFunction(ObjectiveFunction<N> objFunction){
		backingStream.print(objFunction.isMinimization() ? "min: " : "max: ");
		WeightMap<N> objWM = objFunction.getWeightMap();
		for (Variable<N> var : objWM.getAssignedVariables()){
			int weight = objWM.getWeight(var);
			if (weight>0)
				backingStream.print("+"+weight+"*"+var.name+" ");
			else
				backingStream.print(weight+"*"+var.name+" ");
		}
		backingStream.println(";");
	}
}
