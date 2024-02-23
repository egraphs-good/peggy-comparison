package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.Value;
import util.pair.PairedList;

public interface ProofManager<T extends Term<T,V>, V extends Value<T,V>> {
	void addEqualityProof(TermOrTermChild<T,V> left, TermOrTermChild<T,V> right,
			Proof proof, int time);
	PairedList<TermOrTermChild<T,V>,Proof> getProofPath(
			TermOrTermChild<T,V> left, TermOrTermChild<T,V> right);
	int getTimeOfEquality(TermOrTermChild<T,V> left,
			TermOrTermChild<T,V> right);
}
