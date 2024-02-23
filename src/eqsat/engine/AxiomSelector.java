package eqsat.engine;

public interface AxiomSelector<G extends Enum<G>> {
	public boolean isEnabled(G group);
}
