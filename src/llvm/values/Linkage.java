package llvm.values;

/**
 * Represents the various linkage types defined in LLVM.
 * There are separate constants for LLVM 2.3 and 2.8. They are mostly disjoint.
 */
public enum Linkage {
	ExternalLinkage(0),
	LinkOnceLinkage(1),
	WeakLinkage(2),
	AppendingLinkage(3),
	InternalLinkage(4),
	DLLImportLinkage(5),
	DLLExportLinkage(6),
	ExternalWeakLinkage(7),
	GhostLinkage(8),
	//// new 2.8 linkages /////
	ExternalLinkage2_8(0,true),
	AvailableExternallyLinkage2_8(1, true),
	LinkOnceAnyLinkage2_8(2,true),
	LinkOnceODRLinkage2_8(3,true),
	WeakAnyLinkage2_8(4,true),
	WeakODRLinkage2_8(5,true),
	AppendingLinkage2_8(6,true),
	InternalLinkage2_8(7,true),
	PrivateLinkage2_8(8,true),
	LinkerPrivateLinkage2_8(9,true),
	LinkerPrivateWeakLinkage2_8(10,true),
	LinkerPrivateWeakDefAutoLinkage2_8(11,true),
	DLLImportLinkage2_8(12,true),
	DLLExportLinkage2_8(13,true),
	ExternalWeakLinkage2_8(14,true),
	CommonLinkage2_8(15,true);
	
	private final int value;
	private final boolean is2_8;
	private Linkage(int _value) {
		this(_value, false);
	}
	private Linkage(int _value, boolean _is2_8) {
		this.value = _value;
		this.is2_8 = _is2_8;
	}

	public boolean is2_8() {return this.is2_8;}
	public int getValue() {return this.value;}
	
	public static Linkage decodeLinkage(int value) {
		switch (value) {
		case 0: return ExternalLinkage;
		case 1: return LinkOnceLinkage;
		case 2: return WeakLinkage;
		case 3: return AppendingLinkage;
		case 4: return InternalLinkage;
		case 5: return DLLImportLinkage;
		case 6: return DLLExportLinkage;
		case 7: return ExternalWeakLinkage;
		case 8: return GhostLinkage;
		default:
			throw new IllegalArgumentException("Unknown linkage value: " + value);
		}
	}
	
	public static Linkage decodeLinkage2_8(int value) {
		switch (value) {
		case 0: return ExternalLinkage2_8;
		case 1: return AvailableExternallyLinkage2_8;
		case 2: return LinkOnceAnyLinkage2_8;
		case 3: return LinkOnceODRLinkage2_8;
		case 4: return WeakAnyLinkage2_8;
		case 5: return WeakODRLinkage2_8;
		case 6: return AppendingLinkage2_8;
		case 7: return InternalLinkage2_8;
		case 8: return PrivateLinkage2_8;
		case 9: return LinkerPrivateLinkage2_8;
		case 10: return LinkerPrivateWeakLinkage2_8;
		case 11: return LinkerPrivateWeakDefAutoLinkage2_8;
		case 12: return DLLImportLinkage2_8;
		case 13: return DLLExportLinkage2_8;
		case 14: return ExternalWeakLinkage2_8;
		case 15: return CommonLinkage2_8;
		default:
			throw new IllegalArgumentException("Unknown linkage value: " + value);
		}
	}
}
