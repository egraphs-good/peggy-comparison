package eqsat.revert;

import eqsat.OpAmbassador;

public final class Variable {
	private Value mValue = new Value() {
		public boolean isVariable() {return true;}
		public Variable getVariable() {return Variable.this;}
		public String toString() {return Variable.this.toString();}
		public boolean equals(Value that) {return this == that;}
		public boolean isAnyVolatile(OpAmbassador ambassador) {
			return false;
		}
		public boolean isVolatile(OpAmbassador ambassador, int child) {
			return false;
		}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return false;}
		public boolean containsEval() {return false;}
		public boolean containsPhi() {return false;}
		public ReversionGraph.Vertex rewrite(ReversionGraph.Vertex vertex) {
			return vertex;
		}
	};
	private RevertValue mRevert = new RevertValue() {
		public boolean isVariable() {return true;}
		public Variable getVariable() {return Variable.this;}
		public String toString() {return Variable.this.toString();}
	};
	private Value mProject = new Value() {
		public boolean isProject() {return true;}
		public Variable getVariable() {return Variable.this;}
		public String toString() {return "Get " + Variable.this.toString();}
		public boolean equals(Value that) {return this == that;}
		public boolean isFree(OpAmbassador ambassador) {return true;}
		public boolean isAnyVolatile(OpAmbassador ambassador) {
			return false;
		}
		public boolean isVolatile(OpAmbassador ambassador, int child) {
			return false;
		}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return false;}
		public boolean containsEval() {return false;}
		public boolean containsPhi() {return false;}
		public ReversionGraph.Vertex rewrite(ReversionGraph.Vertex vertex) {
			return rewriteVertex(vertex);
		}
		public <P,L> ReversionGraph<P,L>.Vertex rewriteVertex(
				ReversionGraph<P,L>.Vertex vertex) {
			/*BlockValue block = vertex.getChild(0).getLabel().getBlockSelf();
			if (block.modifies(Variable.this))*/ //TODO modifies broken in Block
				return vertex;
			/*else
				return vertex.getChild(0).getChild(
						block.getInput(Variable.this));*/
		}
		public boolean needsChild(OpAmbassador ambassador, int child) {
			return true;
		}
	};
	
	public <P,L> Value<P,L> getValue() {return mValue;}
	public <L,P> RevertValue<L,P> getRevert() {return mRevert;}
	public <P,L> Value<P,L> getProject() {return mProject;}
	public String toString() {return "V" + hashCode();}
}
