package eqsat;

public enum BasicOp {
	True {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createTrue();
		}
	}, False {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createFalse();
		}
	}, Negate {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createNegate();
		}
	}, And {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createAnd();
		}
	}, Or {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createOr();
		}
	}, Equals {
		public <P,L> FlowValue<P,L> getFlowValue() {
			return FlowValue.<P,L>createEquals();
		}
	};
	
	public abstract <P,L> FlowValue<P,L> getFlowValue();
}
