package peggy.analysis.llvm.types;

import peggy.represent.PEGInfo;
import util.Tag;

/**
 * Annotates all the Vertexes of a PEG with PEGTypes.
 * Will annotate using the Tag that is passed in.
 * 
 * @param D is the domain type of the PEGTypes 
 */
public interface PEGTypeAnnotater<D,L,P,R> {
	public void computeTypes(
			D returnType,
			PEGInfo<L,P,R> peg, 
			Tag<PEGType<D>> typetag);
}
