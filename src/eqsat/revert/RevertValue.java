package eqsat.revert;

import java.util.List;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import util.VariaticFunction;

public abstract class RevertValue<L, P> {
	public static <L,P> RevertValue<L,P> getDomain(final L domain) {
		return new RevertValue<L,P>() {
			public boolean isDomain() {return true;}
			public L getDomain() {return domain;}
			public boolean equals(Object that) {
				return that instanceof RevertValue
						&& equals((RevertValue)that);
			}
			public boolean equals(RevertValue that) {
				return that.isDomain()
						&& getDomain().equals(that.getDomain());
			}
			public int hashCode() {return getDomain().hashCode();}
			public String toString() {return getDomain().toString();}
		};
	}
	
	public static <L,P> RevertValue<L,P> getParameter(final P parameter) {
		return new RevertValue<L,P>() {
			public boolean isParameter() {return true;}
			public P getParameter() {return parameter;}
			public boolean equals(Object that) {
				return that instanceof RevertValue
						&& equals((RevertValue)that);
			}
			public boolean equals(RevertValue that) {
				return that.isParameter()
						&& getParameter().equals(that.getParameter());
			}
			public int hashCode() {return getParameter().hashCode();}
			public String toString() {return getParameter().toString();}
		};
	}
	
	public static <L,P,E> E get(
			VariaticFunction<RevertValue<L,P>,ReversionGraph<P,L>.Vertex,E>
			converter,
			ReversionGraph<P,L>.Vertex vertex,
			OpAmbassador<? extends L> ambassador) {
		if (vertex.isVariable())
			return converter.get(vertex.getVariable().<L,P>getRevert());
		Value<P,L> value = vertex.getLabel();
		if (value.getHead() == null)
			throw new IllegalArgumentException();
		FlowValue<P,L> flow = value.getHead();
		RevertValue<L,P> head;
		if (flow.isDomain() || flow.isBasicOp())
			head = getDomain(flow.isDomain() ? flow.getDomain()
					: ambassador.getBasicOp(flow.getBasicOp()));
		else if (flow.isParameter())
			head = getParameter(flow.getParameter());
		else
			throw new IllegalArgumentException();
		List<? extends ReversionGraph<P,L>.Vertex> tails = vertex.getTails();
		if (tails.isEmpty())
			return converter.get(head);
		else if (tails.size() == 1)
			return converter.get(head, tails.get(0));
		else
			return converter.get(head, tails);
	}
	
	public boolean isVariable() {return false;}
	public Variable getVariable() {throw new UnsupportedOperationException();}
	public boolean isParameter() {return false;}
	public P getParameter() {throw new UnsupportedOperationException();}
	public boolean isDomain() {return false;}
	public L getDomain() {throw new UnsupportedOperationException();}
}
