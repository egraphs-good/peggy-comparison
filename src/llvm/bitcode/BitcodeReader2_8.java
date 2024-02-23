package llvm.bitcode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class is responsible for reading an LLVM 2.8 bitcode file from a stream.
 * The result of this reading will be a ModuleBlock2_8 instance.
 */
public class BitcodeReader2_8 {
	/*
	 * SkipBitcodeWrapperHeader - Some systems wrap bc files with a special
	 * header for padding or other reasons.  The format of this header is:
	 *	
	 * struct bc_header {
	 *    uint32_t Magic;         // 0x0B17C0DE
	 *    uint32_t Version;       // Version, currently always 0.
	 *    uint32_t BitcodeOffset; // Offset to traditional bitcode file.
	 *    uint32_t BitcodeSize;   // Size of traditional bitcode file.
	 *    ... potentially other gunk 
	 * };
	 */
	private static int tabs = 0;
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG) {
			for (int i = 0; i < tabs; i++) System.err.print("   ");
			System.err.println(message);
		}
	}

	private static final int BLOCKINFO_BLOCK_ID = 0;
	private static final int FIRST_APPLICATION_ABBREV_ID = 4;
	private static final int BLOCKINFO_CODE_SETBID = 1;
	private static final int BLOCKINFO_CODE_BLOCKNAME = 2;
	private static final int BLOCKINFO_CODE_SETRECORDNAME = 3;

	private boolean LLVM2_7MetadataDetected = false;
	private int bitIndex;
	private final int size;
	private BitSet bits;
	private final Stack<List<DefineAbbrev>> blockAbbrevs;
	private final Map<Integer,List<DefineAbbrev>> blockid2abbrevs;

	public BitcodeReader2_8(InputStream in) throws IOException {
		this.bits = new BitSet(1);

		BufferedInputStream bin = new BufferedInputStream(in);

		int index = 0;
		byte[] bytes = new byte[65536];
		int read;
		while ((read = bin.read(bytes)) > 0) {
			for (int i = 0; i < read; i++) {
				byte bi = bytes[i];
				for (int j = 0; j < 8; j++) {
					if ((bi&(1<<j)) != 0)
						this.bits.set(index);
					index++;
				}
			}
		}
		bin.close();

		this.size = index; 
		this.bitIndex = 0;
		this.blockAbbrevs = new Stack<List<DefineAbbrev>>();
		this.blockid2abbrevs = new HashMap<Integer,List<DefineAbbrev>>();
	}
	
	public boolean LLVM2_7MetadataDetected() {return LLVM2_7MetadataDetected;}

	//// General-purpose bitstream methods ///////////////////
	private boolean atEOF() {
		return this.bitIndex == this.size;
	}
	private void ensure(int n) {
		if (this.bitIndex + n > this.size)
			throw new RuntimeException("Premature EOF: " + (this.bitIndex + n));
	}

	private boolean Read1() {
		ensure(1);
		return this.bits.get(this.bitIndex++);
	}
	private int Read(int n) {
		if (n < 0 || n > 32)
			throw new IllegalArgumentException(""+n);
		ensure(n);

		int result = 0;
		for (int i = 0; i < n; i++) {
			if (this.bits.get(this.bitIndex++))
				result |= (1<<i);
		}

		return result;
	}
	private long Read64(int n) {
		if (n < 0 || n > 64)
			throw new IllegalArgumentException(""+n);
		ensure(n);

		if (n <= 32)
			return (long)Read(n);

		long bottom = (long)Read(32);
		long top = (long)Read(32);
		return bottom | (top << 32);
	}

	private void align32() {
		int byteIndex = this.bitIndex;
		byteIndex = byteIndex + ((32-(byteIndex&31))&31);
		if (byteIndex > this.size)
			throw new RuntimeException("Premature EOF");
		this.bitIndex = byteIndex;
	}

	private int ReadVBR(int width) {
		int result = 0;
		boolean gotone = false;
		int index = 0;

		do {
			ensure(width);

			for (int i = 1; i < width; i++) {
				if (this.bits.get(this.bitIndex++)) {
					if (index >= 32) throw new IndexOutOfBoundsException(""+index);
					else result |= (1<<index);
				}
				index++;
			}
			gotone = this.bits.get(this.bitIndex++);
		} while (gotone);

		return result;
	}
	private long ReadVBR64(int width) {
		long result = 0L;
		boolean gotone = false;
		int index = 0;
		do {
			ensure(width);

			for (int i = 1; i < width; i++) {
				if (this.bits.get(this.bitIndex++)) {
					if (index >= 64) throw new IndexOutOfBoundsException(""+index);
					else result |= (1L<<index);
				}
				index++;
			}
			gotone = this.bits.get(this.bitIndex++);
		} while (gotone);
		return result;
	}


	private void pushBlock(EnterSubblock enter) {
		List<DefineAbbrev> abbrevs = new ArrayList<DefineAbbrev>();
		List<DefineAbbrev> blockspecific = this.blockid2abbrevs.get(enter.getBlockID());
		if (blockspecific != null) {
			abbrevs.addAll(blockspecific);
		}
		this.blockAbbrevs.push(abbrevs);
	}
	private void popBlock() {
		this.blockAbbrevs.pop();
	}


	//// Higher-level parsing functions /////////////////////////////

	public ModuleBlock2_8 readBitcode() {
		this.blockAbbrevs.push(new ArrayList<DefineAbbrev>());

		boolean wrapperMagic = 
			(Read(8) == 0xDE) &&
			(Read(8) == 0xC0) &&
			(Read(8) == 0x17) &&
			(Read(8) == 0x0B);
		int bitcodeLastBit;
		if (wrapperMagic) {
			// skip the garbage header
			debug("Found wrapper header");
			if (Read(32) != 0)
				throw new IllegalStateException("Expecting 0 value");
			int offset = Read(32);
			debug("Wrapper offset = " + offset);
			int bitcodeSize = Read(32);
			debug("Bitcode size = " + bitcodeSize);
			bitcodeLastBit = (offset + bitcodeSize)<<3;
			while (this.bitIndex < (offset<<3)) {
				int extra = Read(8);
				debug("Extra beginning byte: " + extra);
			}
		} else {
			this.bitIndex = 0;
			bitcodeLastBit = this.size;
		}
		
		boolean magic = 
			(Read(8) == 'B') &&
			(Read(8) == 'C') &&
			(Read(4) == 0x0) &&
			(Read(4) == 0xC) &&
			(Read(4) == 0xE) &&
			(Read(4) == 0xD);
		if (!magic)
			throw new IllegalStateException("Invalid magic number");
		
		ModuleBlock2_8 module = null;
		while (this.bitIndex < bitcodeLastBit) {
			int id = Read(2);
			if (id != EnterSubblock.ENTER_SUBBLOCK_ID)
				throw new IllegalStateException("Did not read ENTER_SUBBLOCK id");

			EnterSubblock enter = readEnterSubblock();

			// each of these cases should read in the END_BLOCK record on their own
			switch (enter.getBlockID()) {
			case BLOCKINFO_BLOCK_ID:
				readBlockInfoBlock(enter); // change this from return to something else
				break;

			case ModuleBlock2_8.MODULE_BLOCK_ID:
				if (module != null)
					throw new RuntimeException("Bitcode cannot have multiple modules");
				module = readModuleBlock2_8(enter); // change this from return to something else
				break;

			default:
				throw new RuntimeException("Unknown block id: " + enter.getBlockID());
			}
		}
		
		while (!atEOF()) {
			int extra = Read(8);
			debug("Extra byte: " + extra);
		}

		if (module == null)
			throw new RuntimeException("Did not parse a ModuleBlock2_8");

		return module;
	}


	private static final String[] MODULE_CODE_NAMES = {
		"", 
		"MODULE_CODE_VERSION",
		"MODULE_CODE_TRIPLE",
		"MODULE_CODE_DATALAYOUT",
		"MODULE_CODE_ASM",
		"MODULE_CODE_SECTIONNAME",
		"MODULE_CODE_DEPLIB",
		"MODULE_CODE_GLOBALVAR",
		"MODULE_CODE_FUNCTION",
		"MODULE_CODE_ALIAS",
		"MODULE_CODE_PURGEVALS",
		"MODULE_CODE_COLLETORNAME",
	};
	private ModuleBlock2_8 readModuleBlock2_8(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering Module block:");
		tabs++;
		
		ModuleBlock2_8 module = new ModuleBlock2_8(enter);
		ParamAttrBlock paramAttrs = null;
		TypeBlock2_8 typeTable = null;

		toploop:
			while (true) {
				int id = Read(enter.getNewAbbrevLen());

				switch (id) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw EndBlock");
					readEndBlock();
					popBlock();
					break toploop;
				}

				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					EnterSubblock subenter = readEnterSubblock();

					switch (subenter.getBlockID()) {
					case BLOCKINFO_BLOCK_ID: {
						readBlockInfoBlock(subenter);
						break;
					}

					case ParamAttrBlock.PARAMATTR_BLOCK_ID: {
						if (paramAttrs != null)
							throw new RuntimeException("Multiple PARAMATTR blocks found");
						paramAttrs = readParamAttrBlock(subenter);
						module.addContents(paramAttrs);
						break;
					}

					case TypeBlock2_8.TYPE_BLOCK_ID: {
						if (typeTable != null)
							throw new RuntimeException("Multiple TYPE blocks found");
						typeTable = readTypeBlock2_8(subenter);
						module.addContents(typeTable);
						break;
					}

					case ConstantsBlock2_8.CONSTANTS_BLOCK_ID: {
						if (typeTable == null)
							throw new RuntimeException("ConstantsBlock2_8 requires a TypeBlock2_8!");
						ConstantsBlock2_8 block = readConstantsBlock2_8(subenter, typeTable);
						module.addContents(block);
						break;
					}

					case FunctionBlock2_8.FUNCTION_BLOCK_ID: {
						if (typeTable == null)
							throw new RuntimeException("FunctionBlock2_8 requires a TypeBlock2_8!");
						FunctionBlock2_8 block = readFunctionBlock2_8(subenter, typeTable);
						module.addContents(block);
						break;
					}

					case TypeSymtabBlock.TYPE_SYMTAB_BLOCK_ID: {
						TypeSymtabBlock block = readTypeSymtabBlock(subenter);
						module.addContents(block);
						break;
					}

					case ValueSymtabBlock.VALUE_SYMTAB_BLOCK_ID: {
						ValueSymtabBlock block = readValueSymtabBlock(subenter);
						module.addContents(block);
						break;
					}

					case MetadataBlock2_8.METADATA_BLOCK_ID: {
						MetadataBlock2_8 block = readMetadataBlock2_8(subenter);
						module.addContents(block);
						break;
					}
					
					default: {// unknown block, skip
					debug("Saw unknown block type, skipping...");
						skipBlock(subenter);
						break;
					}
					}

					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}

				default: {// it's some other record type
					DataRecord record = readRecord(id);
					final String recordName = MODULE_CODE_NAMES[record.getCode()];
					
					switch (record.getCode()) {
					case ModuleBlock2_8.MODULE_CODE_GLOBALVAR: {
						debug("Saw " + recordName + " record: " + record);
						module.addContents(record);
						break;
					}
					case ModuleBlock2_8.MODULE_CODE_FUNCTION: {
						debug("Saw " + recordName + " record: " + record);
						module.addContents(record);
						break;
					}
					case ModuleBlock2_8.MODULE_CODE_ALIAS: {
						debug("Saw " + recordName + " record: " + record);
						module.addContents(record);
						break;
					} 
					case ModuleBlock2_8.MODULE_CODE_VERSION:
					case ModuleBlock2_8.MODULE_CODE_TRIPLE:
					case ModuleBlock2_8.MODULE_CODE_DATALAYOUT:
					case ModuleBlock2_8.MODULE_CODE_ASM:
					case ModuleBlock2_8.MODULE_CODE_SECTIONNAME:
					case ModuleBlock2_8.MODULE_CODE_DEPLIB:
					case ModuleBlock2_8.MODULE_CODE_PURGEVALS:
					case ModuleBlock2_8.MODULE_CODE_COLLECTORNAME: {
						debug("Saw " + recordName + " record: " + record);
						module.addContents(record);
						break;
					}
					default: {
						// unknown record, ignore
						debug("Saw unknown record, ignoring: " + record);
						break;
					}
					}
					
					continue;
				}
				}
			}

		tabs--;
		debug("Exiting ModuleBlock2_8");

		return module;
	}


	private ParamAttrBlock readParamAttrBlock(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering ParamAttrBlock:");
		tabs++;
		ParamAttrBlock result = new ParamAttrBlock(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}

				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					// no subblocks, skip
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DEFINE_ABBREV: " + define);
					continue;
				}

				default: {// some data record
					DataRecord record = readRecord(abbrevid);
					if (record.getCode() == ParamAttrBlock.PARAMATTR_CODE_ENTRY) {
						result.addContents(record);
						debug("Saw PARAMATTR_CODE_ENTRY: " + record);
						continue;
					} else {
						// ignore this record
						debug("Saw unknown datarecord, ignoring...");
						continue;
					}
				}
				}
			}

		tabs--;
		debug("Ending ParamAttrBlock");
		
		return result;
	}
	
	
	private static final String[] METADATA_CODE_NAMES = {
		"",
		"METADATA_STRING",
		"METADATA_NODE",
		"METADATA_FN_NODE",
		"METADATA_NAME",
		"METADATA_NAMED_NODE",
		"METADATA_KIND",
		"",
		"METADATA_NODE2",
		"METADATA_FN_NODE2",
		"METADATA_NAMED_NODE2",
	};
	private MetadataBlock2_8 readMetadataBlock2_8(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering MetadataBlock2_8:");
		tabs++;
		MetadataBlock2_8 result = new MetadataBlock2_8(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}

				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					// no subblocks, skip
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DEFINE_ABBREV: " + define);
					continue;
				}

				default: {// some data record
					DataRecord record = readRecord(abbrevid);
					final String recordName = METADATA_CODE_NAMES[record.getCode()];
					
					switch (record.getCode()) {
					case MetadataBlock2_8.METADATA_NODE:
					case MetadataBlock2_8.METADATA_FN_NODE:
					case MetadataBlock2_8.METADATA_NAMED_NODE:
						debug("LLVM2_7MetadataDetected");
						LLVM2_7MetadataDetected = true;
						
					case MetadataBlock2_8.METADATA_KIND:
					case MetadataBlock2_8.METADATA_NODE2:
					case MetadataBlock2_8.METADATA_FN_NODE2:
					case MetadataBlock2_8.METADATA_NAMED_NODE2: 
					case MetadataBlock2_8.METADATA_STRING:
					case MetadataBlock2_8.METADATA_NAME:
					{
						result.addContents(record);
						debug("Saw " + recordName + ": " + record);
						break;
					}
					
					default: {
						// ignore this record
						debug("Saw unknown datarecord, ignoring...");
						break;
					}
					}
					continue;
				}
				}
			}

		tabs--;
		debug("Ending MetadataBlock2_8");
		
		return result;
	}
	
	private static final String[] METADATA_ATTACHMENT_NAMES = {
		"","","","","","","",
		"METADATA_ATTACHMENT","","","",
		"METADATA_ATTACHMENT2"
	};
	private MetadataAttachmentBlock2_8 readMetadataAttachmentBlock2_8(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering MetadataAttachmentBlock2_8:");
		tabs++;
		MetadataAttachmentBlock2_8 result = new MetadataAttachmentBlock2_8(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}

				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					// no subblocks, skip
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DEFINE_ABBREV: " + define);
					continue;
				}

				default: {// some data record
					DataRecord record = readRecord(abbrevid);
					final String recordName = METADATA_ATTACHMENT_NAMES[record.getCode()];
					
					switch (record.getCode()) {
					case MetadataAttachmentBlock2_8.METADATA_ATTACHMENT:
						debug("LLVM2_7MetadataDetected");
						LLVM2_7MetadataDetected = true;
						
					case MetadataAttachmentBlock2_8.METADATA_ATTACHMENT2:
						result.addContents(record);
						debug("Saw " + recordName + ": " + record);
						break;
					default:
						// ignore this record
						debug("Saw unknown datarecord, ignoring...");
						break;
					}
					continue;
				}
				}
			}

		tabs--;
		debug("Ending MetadataAttachmentBlock2_8");
		
		return result;
	}

	private static final String[] TYPE_CODE_NAMES = {
		"",
		"TYPE_CODE_NUMENTRY",
		"TYPE_CODE_VOID",
		"TYPE_CODE_FLOAT",
		"TYPE_CODE_DOUBLE",
		"TYPE_CODE_LABEL",
		"TYPE_CODE_OPAQUE",
		"TYPE_CODE_INTEGER",
		"TYPE_CODE_POINTER",
		"TYPE_CODE_FUNCTION",
		"TYPE_CODE_STRUCT",
		"TYPE_CODE_ARRAY",
		"TYPE_CODE_VECTOR",
		"TYPE_CODE_X86_FP80",
		"TYPE_CODE_FP128",
		"TYPE_CODE_PPC_FP128",
		"TYPE_CODE_METADATA",
	};
	private TypeBlock2_8 readTypeBlock2_8(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering TypeBlock2_8:");
		tabs++;
		TypeBlock2_8 result = new TypeBlock2_8(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw EndBlock");
					readEndBlock();
					popBlock();
					break toploop;
				}
					
				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					debug("Saw EnterSubblock, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}
					
				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}
					
				default: {// data record
					DataRecord record = readRecord(abbrevid);
					final String recordName = TYPE_CODE_NAMES[record.getCode()];
					
					if (TypeBlock2_8.FIRST_TYPE_CODE <= record.getCode() && record.getCode() <= TypeBlock2_8.LAST_TYPE_CODE) {
						result.addContents(record);
						debug("Saw " + recordName + ": " + record);
					} else if (record.getCode() == TypeBlock2_8.TYPE_CODE_NUMENTRY) {
						result.addContents(record);
						debug("Saw TYPE_CODE_NUMENTRY record: " + record);
					} else {
						// unknown record, ignore
						debug("Saw unknown data record, ignoring...");
					}
					continue;
				}
				}
			}

		tabs--;
		debug("Exiting TypeBlock2_8");
		
		return result;
	}

	private static final String[] CONSTANTS_CODE_NAMES = {
		"",
		"CST_CODE_SETTYPE",
		"CST_CODE_NULL",
		"CST_CODE_UNDEF",
		"CST_CODE_INTEGER",
		"CST_CODE_WIDE_INTEGER",
		"CST_CODE_FLOAT",
		"CST_CODE_AGGREGATE",
		"CST_CODE_STRING",
		"CST_CODE_CSTRING",
		"CST_CODE_CE_BINOP",
		"CST_CODE_CE_CAST",
		"CST_CODE_CE_GEP",
		"CST_CODE_CE_SELECT",
		"CST_CODE_CE_EXTRACTELT",
		"CST_CODE_CE_INSERTELT",
		"CST_CODE_CE_SHUFFLEVEC",
		"CST_CODE_CE_CMP",
		"CST_CODE_INLINEASM",
		"CST_CODE_CE_SHUFVEC_EX",
		"CST_CODE_CE_INBOUNDS_GEP",
		"CST_CODE_BLOCKADDRESS",
	};
	private ConstantsBlock2_8 readConstantsBlock2_8(EnterSubblock enter, TypeBlock2_8 typeBlock) {
		pushBlock(enter);
		
		debug("Entering ConstantsBlock2_8:");
		tabs++;
		ConstantsBlock2_8 result = new ConstantsBlock2_8(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();
		
		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}
				
				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}
					
				default: { // some data record
					DataRecord record = readRecord(abbrevid);
					final String recordName = CONSTANTS_CODE_NAMES[record.getCode()];
					
					switch (record.getCode()) {
						case ConstantsBlock2_8.CST_CODE_SETTYPE: {
							result.addContents(record);
							debug("Saw CST_CODE_SETTYPE record: " + record);
							break;
						}
							
						case ConstantsBlock2_8.CST_CODE_NULL:
						case ConstantsBlock2_8.CST_CODE_UNDEF:
						case ConstantsBlock2_8.CST_CODE_INTEGER:
						case ConstantsBlock2_8.CST_CODE_WIDE_INTEGER:	
						case ConstantsBlock2_8.CST_CODE_FLOAT:
						case ConstantsBlock2_8.CST_CODE_AGGREGATE:
						case ConstantsBlock2_8.CST_CODE_STRING:
						case ConstantsBlock2_8.CST_CODE_CSTRING:
						case ConstantsBlock2_8.CST_CODE_CE_BINOP:
						case ConstantsBlock2_8.CST_CODE_CE_CAST:
						case ConstantsBlock2_8.CST_CODE_CE_GEP:
						case ConstantsBlock2_8.CST_CODE_CE_SELECT:
						case ConstantsBlock2_8.CST_CODE_CE_EXTRACTELT:
						case ConstantsBlock2_8.CST_CODE_CE_INSERTELT:
						case ConstantsBlock2_8.CST_CODE_CE_SHUFFLEVEC:
						case ConstantsBlock2_8.CST_CODE_CE_CMP:
						case ConstantsBlock2_8.CST_CODE_INLINEASM: 
						case ConstantsBlock2_8.CST_CODE_CE_SHUFVEC_EX:
						case ConstantsBlock2_8.CST_CODE_CE_INBOUNDS_GEP:
						case ConstantsBlock2_8.CST_CODE_BLOCKADDRESS:
						{
							result.addContents(record);
							debug("Saw " + recordName + " record: " + record);
							break;
						}
							
						default: {// unknown, skip
							debug("Saw unknown record, ignoring");
							break;
						}
					}
					continue;
				}
				}
			}

		tabs--;
		debug("Exiting ConstantsBlock2_8");
		
		return result;
	}

	private static final String[] FUNCTION_CODE_NAMES = {
		"",
		"FUNC_CODE_DECLAREBLOCKS",
		"FUNC_CODE_INST_BINOP",
		"FUNC_CODE_INST_CAST",
		"FUNC_CODE_INST_GEP",
		"FUNC_CODE_INST_SELECT",
		"FUNC_CODE_INST_EXTRACTELT",
		"FUNC_CODE_INST_INSERTELT",
		"FUNC_CODE_INST_SHUFFLEVEC",
		"FUNC_CODE_INST_CMP",
		"FUNC_CODE_INST_RET",
		
		"FUNC_CODE_INST_BR",
		"FUNC_CODE_INST_SWITCH",
		"FUNC_CODE_INST_INVOKE",
		"FUNC_CODE_INST_UNWIND",
		"FUNC_CODE_INST_UNREACHABLE",
		"FUNC_CODE_INST_PHI",
		"FUNC_CODE_INST_MALLOC",
		"FUNC_CODE_INST_FREE",
		"FUNC_CODE_INST_ALLOCA",
		"FUNC_CODE_INST_LOAD",
		
		"FUNC_CODE_INST_STORE",
		"FUNC_CODE_INST_CALL",
		"FUNC_CODE_INST_VAARG",
		"FUNC_CODE_INST_STORE2",
		"FUNC_CODE_INST_GETRESULT",
		"FUNC_CODE_INST_EXTRACTVAL",
		"FUNC_CODE_INST_INSERTVAL",
		"FUNC_CODE_INST_CMP2",
		"FUNC_CODE_INST_VSELECT",
		"FUNC_CODE_INST_INBOUNDS_GEP",
		
		"FUNC_CODE_INST_INDIRECTBR",
		"FUNC_CODE_DEBUG_LOC",
		"FUNC_CODE_DEBUG_LOC_AGAIN",
		"FUNC_CODE_INST_CALL2",
		"FUNC_CODE_DEBUG_LOC2",
	};
	private FunctionBlock2_8 readFunctionBlock2_8(EnterSubblock enter, TypeBlock2_8 typeBlock) {
		pushBlock(enter);
		
		debug("Entering FunctionBlock2_8:");
		tabs++;
		FunctionBlock2_8 result = new FunctionBlock2_8(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();
		
		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}
				
				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					// allow CONSTANTS_BLOCK or VALUE_SYMTAB_BLOCK
					EnterSubblock subenter = readEnterSubblock();
					switch (subenter.getBlockID()) {
					case ValueSymtabBlock.VALUE_SYMTAB_BLOCK_ID: {
						ValueSymtabBlock block = readValueSymtabBlock(subenter);
						result.addContents(block);
						break;
					}
						
					case ConstantsBlock2_8.CONSTANTS_BLOCK_ID: {
						ConstantsBlock2_8 block = readConstantsBlock2_8(subenter, typeBlock);
						result.addContents(block);
						break;
					}

					case MetadataBlock2_8.METADATA_BLOCK_ID: {
						MetadataBlock2_8 block = readMetadataBlock2_8(subenter);
						result.addContents(block);
						break;
					}

					case MetadataAttachmentBlock2_8.METADATA_ATTACHMENT_BLOCK_ID: {
						MetadataAttachmentBlock2_8 block = readMetadataAttachmentBlock2_8(subenter);
						result.addContents(block);
						break;
					}
					
					default: {// unknown block, skip
						debug("Saw unknown block, skipping");
						skipBlock(subenter);
						continue;
					}
					}
					
					continue;
				}
				
				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}
					
				default: {// data record
					DataRecord record = readRecord(abbrevid);
					final String recordName = FUNCTION_CODE_NAMES[record.getCode()];
					
					switch (record.getCode()) {
					case FunctionBlock2_8.FUNC_CODE_DECLAREBLOCKS: {
						result.addContents(record);
						debug("Saw FUNC_CODE_DECLAREBLOCKS: " + record);
						break;
					}
					
					case FunctionBlock2_8.FUNC_CODE_INST_CALL:
					case FunctionBlock2_8.FUNC_CODE_DEBUG_LOC:
						debug("LLVM2_7MetadataDetected");
						LLVM2_7MetadataDetected = true;
						
					case FunctionBlock2_8.FUNC_CODE_INST_BINOP: 
					case FunctionBlock2_8.FUNC_CODE_INST_CAST:
					case FunctionBlock2_8.FUNC_CODE_INST_GEP:
					case FunctionBlock2_8.FUNC_CODE_INST_SELECT:
					case FunctionBlock2_8.FUNC_CODE_INST_EXTRACTELT:
					case FunctionBlock2_8.FUNC_CODE_INST_INSERTELT:
					case FunctionBlock2_8.FUNC_CODE_INST_SHUFFLEVEC:
					case FunctionBlock2_8.FUNC_CODE_INST_CMP:
					case FunctionBlock2_8.FUNC_CODE_INST_PHI:
					case FunctionBlock2_8.FUNC_CODE_INST_MALLOC:
					case FunctionBlock2_8.FUNC_CODE_INST_FREE:
					case FunctionBlock2_8.FUNC_CODE_INST_ALLOCA:
					case FunctionBlock2_8.FUNC_CODE_INST_LOAD:
					case FunctionBlock2_8.FUNC_CODE_INST_STORE:
					case FunctionBlock2_8.FUNC_CODE_INST_VAARG:
					case FunctionBlock2_8.FUNC_CODE_INST_STORE2:
					case FunctionBlock2_8.FUNC_CODE_INST_GETRESULT:
					case FunctionBlock2_8.FUNC_CODE_INST_UNWIND:
					case FunctionBlock2_8.FUNC_CODE_INST_UNREACHABLE:
					case FunctionBlock2_8.FUNC_CODE_INST_INVOKE:
					case FunctionBlock2_8.FUNC_CODE_INST_SWITCH:
					case FunctionBlock2_8.FUNC_CODE_INST_BR:
					case FunctionBlock2_8.FUNC_CODE_INST_RET: 
					case FunctionBlock2_8.FUNC_CODE_INST_EXTRACTVAL:
					case FunctionBlock2_8.FUNC_CODE_INST_INSERTVAL:
					case FunctionBlock2_8.FUNC_CODE_INST_CMP2:
					case FunctionBlock2_8.FUNC_CODE_INST_VSELECT:
					case FunctionBlock2_8.FUNC_CODE_INST_INBOUNDS_GEP:
					case FunctionBlock2_8.FUNC_CODE_INST_INDIRECTBR:
					case FunctionBlock2_8.FUNC_CODE_DEBUG_LOC_AGAIN:
					case FunctionBlock2_8.FUNC_CODE_INST_CALL2:
					case FunctionBlock2_8.FUNC_CODE_DEBUG_LOC2: 
					{
						result.addContents(record);
						debug("Saw " + recordName + ": " + record);
						break;
					}
					default: {
						// unknown record, ignore
						debug("Saw unknown record, ignoring");
						break;
					}
					}

					continue;
				}
				}
			}

		tabs--;
		debug("Exiting FunctionBlock2_8");
		
		return result;
	}

	private TypeSymtabBlock readTypeSymtabBlock(EnterSubblock enter) {
		pushBlock(enter);
		
		debug("Entering TypeSymtabBlock:");
		tabs++;
		TypeSymtabBlock result = new TypeSymtabBlock(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}
				
				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}
					
				default: { // some data record
					DataRecord record = readRecord(abbrevid);
					if (record.getCode() == TypeSymtabBlock.TST_CODE_ENTRY) {
						debug("Saw TST_CODE_ENTRY record: " +record);
						result.addContents(record);
					} else {
						// ignore
						debug("Saw unknown record, ignoring");
					}
					continue;
				}
				}
			}
		
		tabs--;
		debug("Exiting TypeSymtabBlock");
		
		return result;
	}

	private ValueSymtabBlock readValueSymtabBlock(EnterSubblock enter) {
		pushBlock(enter);
		
		debug("Entering ValueSymtabBlock:");
		tabs++;
		ValueSymtabBlock result = new ValueSymtabBlock(enter);
		int newAbbrevLen = enter.getNewAbbrevLen();

		toploop:
			while (true) {
				int abbrevid = Read(newAbbrevLen);

				switch (abbrevid) {
				case EndBlock.END_BLOCK_ID: {
					debug("Saw END_BLOCK");
					readEndBlock();
					popBlock();
					break toploop;
				}
				
				case EnterSubblock.ENTER_SUBBLOCK_ID: {
					debug("Saw ENTER_SUBBLOCK, skipping...");
					EnterSubblock subenter = readEnterSubblock();
					skipBlock(subenter);
					continue;
				}

				case DefineAbbrev.DEFINE_ABBREV_ID: {
					DefineAbbrev define = readDefineAbbrev();
					this.blockAbbrevs.peek().add(define);
					debug("Saw DefineAbbrev: " + define);
					continue;
				}
					
				default: { // some data record
					DataRecord record = readRecord(abbrevid);
					if (record.getCode() == ValueSymtabBlock.VST_CODE_ENTRY) {
						debug("Saw VST_CODE_ENTRY record: " + record);
						result.addContents(record);
					} else if (record.getCode() == ValueSymtabBlock.VST_CODE_BBENTRY) {
						debug("Saw VST_CODE_BBENTRY record: " + record);
						result.addContents(record);
					} else {
						// unknown, ignore
						debug("Saw unknown record, ignoring");
					}
					continue;
				}
				}
			}

		tabs--;
		debug("Exiting ValueSymtabBlock");
		
		return result;
	}


	/**
	 * Precondition: current bitIndex is positioned right after reading the 'blocklen' value.
	 */
	private void readBlockInfoBlock(EnterSubblock enter) {
		debug("Entering BlockInfo block:");
		tabs++;
		boolean gotbid = false;
		int bid = 0;

		toploop:
			while (true) {
				int abbrevid = Read(enter.getNewAbbrevLen());
				switch (abbrevid) {
				case DefineAbbrev.DEFINE_ABBREV_ID: {
					if (!gotbid) 
						throw new RuntimeException("Need BID before DEFINE_ABBREV");

					DefineAbbrev define = readDefineAbbrev();
					debug("Saw DefineAbbrev: " + define);

					List<DefineAbbrev> blockspecific = this.blockid2abbrevs.get(bid);
					if (blockspecific == null) {
						blockspecific = new ArrayList<DefineAbbrev>();
						this.blockid2abbrevs.put(bid, blockspecific);
					}
					blockspecific.add(define);
					break;
				}

				case EndBlock.END_BLOCK_ID: {
					readEndBlock();
					debug("Saw EndBlock");
					break toploop;
				}

				default: {// some abbrev record type (should only be abbreviated SETBID)
					DataRecord record = readRecord(abbrevid);
					switch (record.getCode()) {
					case BLOCKINFO_CODE_SETBID: {
						OperandValue value = record.getOp(0);
						if (!value.isNumeric())
							throw new RuntimeException("BID must be a basic value");
						bid = (int)value.getNumericValue();
						gotbid = true;
						break;
					}
					
					case BLOCKINFO_CODE_BLOCKNAME: {
						// Can safely ignore
						debug("Saw BLOCKINFO_CODE_BLOCKNAME record: " + record);
						break;
					}
					
					case BLOCKINFO_CODE_SETRECORDNAME: {
						// Can safely ignore
						debug("Saw BLOCKINFO_CODE_SETRECORDNAME record: " + record);
						break;
					}
					
					default:
						throw new RuntimeException("Unknown record type: " + record.getCode());
					}
					break;
				}
				}
			}

		tabs--;
		debug("Exiting BlockInfo");
	}



	/**
	 * Precondition: bit index is right after the abbrev id for this record,
	 * and that is the value passed in to this function.
	 */
	private DataRecord readRecord(int abbrevid) {
		if (abbrevid == UnabbrevRecord.UNABBREV_RECORD_ID) {
			UnabbrevRecord record = readUnabbrevRecord();
			return record;
		} else if (abbrevid >= FIRST_APPLICATION_ABBREV_ID && 
				   abbrevid-FIRST_APPLICATION_ABBREV_ID < this.blockAbbrevs.peek().size()) {
			DefineAbbrev defn = this.blockAbbrevs.peek().get(abbrevid-FIRST_APPLICATION_ABBREV_ID);
			AbbreviatedRecord record = readAbbreviatedRecord(abbrevid, defn);
			return record;
		} else { 
			throw new RuntimeException("Abbrev id is not a data record: " + abbrevid);
		}
	}


	/**
	 * Precondition: bit index is right after the abbrev id for this record
	 */
	private AbbreviatedRecord readAbbreviatedRecord(int abbrevid, DefineAbbrev definition) {
		List<OperandValue> ops = new ArrayList<OperandValue>(definition.getNumAbbrevOps());

		for (int i = 0; i < definition.getNumAbbrevOps(); i++) {
			Operand op = definition.getAbbrevOp(i);
			if (op.isBasic()) {
				ops.add(readBasicOperand(op.getBasicSelf()));
			} else if (op.isBlob()) {
				BlobOperand blobType = definition.getAbbrevOp(i).getBlobSelf();
				Operand elementType = blobType.getElementType();
				BasicOperand basicElementType = elementType.getBasicSelf();

				int numelts = ReadVBR(6);
				align32();
				List<BasicOperandValue> elements = new ArrayList<BasicOperandValue>(numelts);
				for (int j = 0; j < numelts; j++) {
					elements.add(readBasicOperand(basicElementType));
				}

				ops.add(new BlobOperandValue(blobType, elements));
			} else {
				// array operand
				if (i != definition.getNumAbbrevOps()-1)
					throw new RuntimeException("Array operand must be last");
				ArrayOperand arrayType = definition.getAbbrevOp(i).getArraySelf();
				Operand elementType = arrayType.getElementType();
				if (!elementType.isBasic())
					throw new RuntimeException("Array element type must be basic operand");
				BasicOperand basicElementType = elementType.getBasicSelf();

				int numelts = ReadVBR(6);
				List<BasicOperandValue> elements = new ArrayList<BasicOperandValue>(numelts);
				for (int j = 0; j < numelts; j++) {
					elements.add(readBasicOperand(basicElementType));
				}

				ops.add(new ArrayOperandValue(arrayType, elements));
			}
		}

		return new AbbreviatedRecord(abbrevid, definition, ops);
	}
	private BasicOperandValue readBasicOperand(BasicOperand op) {
		if (op.isLiteral()) {
			// read nothing!
			return new BasicOperandValue(op, op.getLiteralSelf().getValue());
		} else if (op.isFixed()) {
			long width = op.getFixedSelf().getWidth();
			return new BasicOperandValue(op, Read64((int)width));
		} else if (op.isVBR()) {
			long width = op.getVBRSelf().getWidth();
			return new BasicOperandValue(op, ReadVBR64((int)width));
		} else { // Char6
			return new BasicOperandValue(op, Read64(6));
		}
	}





	/** 
	 * Precondition: bit index is right after reading the ENTER_SUBBLOCK record.
	 */
	private void skipBlock(EnterSubblock enter) {
		int newindex = this.bitIndex + enter.getBlockLen()*32;
		if (newindex > this.size)
			throw new RuntimeException("Premature EOF");
		this.bitIndex = newindex;
	}

	/**
	 * Precondition: already read the ENTER_SUBBLOCK abbrev id
	 */
	private EnterSubblock readEnterSubblock() {
		int blockid = ReadVBR(8);
		int innerAbbrevLength = ReadVBR(4);
		align32();
		int blocklen = Read(32); // this length include the END_BLOCK record
		return new EnterSubblock(blockid, innerAbbrevLength, blocklen);
	}

	/**
	 * Precondition: already read the END_BLOCK abbrev id
	 */
	private EndBlock readEndBlock() {
		this.align32();
		return EndBlock.INSTANCE;
	}

	/**
	 * Precondition: already read the UNABBREV_RECORD abbrev id
	 */
	private UnabbrevRecord readUnabbrevRecord() {
		int code = ReadVBR(6);
		int numops = ReadVBR(6);
		List<Long> ops = new ArrayList<Long>(numops);

		for (int i = 0; i < numops; i++) {
			ops.add(ReadVBR64(6));
		}
		return new UnabbrevRecord(code, ops);
	}

	/**
	 * Precondition: already read the DEFINE_ABBREV abbrev id
	 */
	private DefineAbbrev readDefineAbbrev() {
		int numabbrevops = ReadVBR(5);
		LinkedList<Operand> abbrevops = 
			new LinkedList<Operand>();

		boolean sawArray = false;
		for (int i = 0; i < numabbrevops; i++) {
			if (Read1()) {
				// [1_1, litvalue_vbr8]
				long value = ReadVBR64(8);
				abbrevops.add(new LiteralOperand(value));
			} else {
				// [0_1, encoding_3]
				// [0_1, encoding_3, value_vbr5]
				int encoding = Read(3);
				switch (encoding) {
				case DefineAbbrev.ENCODING_FIXED: {// has value
					long value = ReadVBR64(5);
					abbrevops.add(new FixedOperand(value));
					break;
				}

				case DefineAbbrev.ENCODING_VBR: {// has value
					long value = ReadVBR64(5);
					abbrevops.add(new VBROperand(value));
					break;
				}

				case DefineAbbrev.ENCODING_ARRAY: {// no value
					if (i != numabbrevops-2)
						throw new RuntimeException("Array operand not second-to-last");
					sawArray = true;
					break;
				}
				
				case DefineAbbrev.ENCODING_BLOB: {// no value
					abbrevops.add(BlobOperand.INSTANCE);
					break;
				}

				case DefineAbbrev.ENCODING_CHAR6: {// no value
					abbrevops.add(Char6Operand.INSTANCE);
					break;
				}

				default:
					throw new RuntimeException("Invalid encoding value: " + encoding);
				}
			}
		}

		if (sawArray) {
			// remove the last entry and use it as the array element type
			Operand last = abbrevops.removeLast();
			if (!last.isBasic())
				throw new RuntimeException("This should never happen!");
			abbrevops.add(new ArrayOperand(last.getBasicSelf()));
		}

		return new DefineAbbrev(abbrevops);
	}

	////////////////////////////////

	public static void main(String args[]) throws Throwable {
		BitcodeReader2_8.DEBUG = true;
		BitcodeReader2_8 parser = new BitcodeReader2_8(new FileInputStream(args[0]));
		parser.readBitcode();
	}
}
