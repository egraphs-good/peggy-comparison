package eqsat.engine;

public class AllEnabledAxiomSelector<G extends Enum<G>> implements AxiomSelector<G> {
	public boolean isEnabled(G group) {
		return true;
	}
}
