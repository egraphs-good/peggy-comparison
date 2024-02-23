package eqsat;

public interface OpExpression<L> {
	L getOperation();
	int getOperandCount();
	OpExpression<L> getOperand(int index);
}
