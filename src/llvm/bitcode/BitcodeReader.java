package llvm.bitcode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class is responsible for reading an LLVM 2.3 module from a stream.
 * The result of this reading will be a ModuleBlock instance.
 */
public class BitcodeReader {
	protected static boolean DEBUG = false;
	private static int tabs = 0;
	private static void debug(String message) {
		if (DEBUG) {
			for (int i = 0; i < tabs; i++) System.err.print("   ");
			System.err.println(message);
		}
	}
	
	private static final int BLOCKINFO_BLOCK_ID = 0;
	private static final int FIRST_APPLICATION_ABBREV_ID = 4;
	private static final int SETBID_CODE = 1;

	private int bitIndex;
	private final int size;
	private BitSet bits;
	private final Stack<List<DefineAbbrev>> blockAbbrevs;
	private final Map<Integer,List<DefineAbbrev>> blockid2abbrevs;

	public BitcodeReader(InputStream in) throws IOException {
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


	public ModuleBlock readBitcode() {
		this.blockAbbrevs.push(new ArrayList<DefineAbbrev>());

		boolean magic = 
			(Read(8) == 'B') &&
			(Read(8) == 'C') &&
			(Read(4) == 0x0) &&
			(Read(4) == 0xC) &&
			(Read(4) == 0xE) &&
			(Read(4) == 0xD);
		if (!magic)
			throw new RuntimeException("Error parsing magic number");

		ModuleBlock module = null;
		while (!atEOF()) {
			int id = Read(2);
			if (id != EnterSubblock.ENTER_SUBBLOCK_ID)
				throw new IllegalStateException("Did not read ENTER_SUBBLOCK id");

			EnterSubblock enter = readEnterSubblock();

			// each of these cases should read in the END_BLOCK record on their own
			switch (enter.getBlockID()) {
			case BLOCKINFO_BLOCK_ID:
				readBlockInfoBlock(enter); // change this from return to something else
				break;

			case ModuleBlock.MODULE_BLOCK_ID:
				if (module != null)
					throw new RuntimeException("Bitcode cannot have multiple modules");
				module = readModuleBlock(enter); // change this from return to something else
				break;

			default:
				throw new RuntimeException("Unknown block id: " + enter.getBlockID());
			}
		}
		
		if (module == null)
			throw new RuntimeException("Did not parse a ModuleBlock");

		return module;
	}



	private ModuleBlock readModuleBlock(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering Module block:");
		tabs++;
		
		ModuleBlock module = new ModuleBlock(enter);
		ParamAttrBlock paramAttrs = null;
		TypeBlock typeTable = null;

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

					case TypeBlock.TYPE_BLOCK_ID: {
						if (typeTable != null)
							throw new RuntimeException("Multiple TYPE blocks found");
						typeTable = readTypeBlock(subenter);
						module.addContents(typeTable);
						break;
					}

					case ConstantsBlock.CONSTANTS_BLOCK_ID: {
						if (typeTable == null)
							throw new RuntimeException("ConstantsBlock requires a TypeBlock!");
						ConstantsBlock block = readConstantsBlock(subenter, typeTable);
						module.addContents(block);
						break;
					}

					case FunctionBlock.FUNCTION_BLOCK_ID: {
						if (typeTable == null)
							throw new RuntimeException("FunctionBlock requires a TypeBlock!");
						FunctionBlock block = readFunctionBlock(subenter, typeTable);
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
					
					switch (record.getCode()) {
					case ModuleBlock.MODULE_CODE_GLOBALVAR: {
						debug("Saw MODULE_CODE_GLOBALVAR record: " + record);
						module.addContents(record);
						break;
					}
					case ModuleBlock.MODULE_CODE_FUNCTION: {
						debug("Saw MODULE_CODE_FUNCTION record: " + record);
						module.addContents(record);
						break;
					}
					case ModuleBlock.MODULE_CODE_ALIAS: {
						debug("Saw MODULE_CODE_ALIAS record: " + record);
						module.addContents(record);
						break;
					} 
					case ModuleBlock.MODULE_CODE_VERSION:
					case ModuleBlock.MODULE_CODE_TRIPLE:
					case ModuleBlock.MODULE_CODE_DATALAYOUT:
					case ModuleBlock.MODULE_CODE_ASM:
					case ModuleBlock.MODULE_CODE_SECTIONNAME:
					case ModuleBlock.MODULE_CODE_DEPLIB:
					case ModuleBlock.MODULE_CODE_PURGEVALS:
					case ModuleBlock.MODULE_CODE_COLLECTORNAME: {
						debug("Saw MODULE_CODE_??? record: " + record);
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
		debug("Exiting ModuleBlock");

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

	private TypeBlock readTypeBlock(EnterSubblock enter) {
		pushBlock(enter);

		debug("Entering TypeBlock:");
		tabs++;
		TypeBlock result = new TypeBlock(enter);
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
					if (TypeBlock.FIRST_TYPE_CODE <= record.getCode() && record.getCode() <= TypeBlock.LAST_TYPE_CODE) {
						result.addContents(record);
						debug("Saw typecode datarecord: " + record);
					} else if (record.getCode() == TypeBlock.TYPE_CODE_NUMENTRY) {
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
		debug("Exiting TypeBlock");
		
		return result;
	}

	private ConstantsBlock readConstantsBlock(EnterSubblock enter, TypeBlock typeBlock) {
		pushBlock(enter);
		
		debug("Entering ConstantsBlock:");
		tabs++;
		ConstantsBlock result = new ConstantsBlock(enter);
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
					
					switch (record.getCode()) {
						case ConstantsBlock.CST_CODE_SETTYPE: {
							result.addContents(record);
							debug("Saw CST_CODE_SETTYPE record: " + record);
							break;
						}
							
						case ConstantsBlock.CST_CODE_NULL:
						case ConstantsBlock.CST_CODE_UNDEF:
						case ConstantsBlock.CST_CODE_INTEGER:
						case ConstantsBlock.CST_CODE_WIDE_INTEGER:	
						case ConstantsBlock.CST_CODE_FLOAT:
						case ConstantsBlock.CST_CODE_AGGREGATE:
						case ConstantsBlock.CST_CODE_STRING:
						case ConstantsBlock.CST_CODE_CSTRING:
						case ConstantsBlock.CST_CODE_CE_BINOP:
						case ConstantsBlock.CST_CODE_CE_CAST:
						case ConstantsBlock.CST_CODE_CE_GEP:
						case ConstantsBlock.CST_CODE_CE_SELECT:
						case ConstantsBlock.CST_CODE_CE_EXTRACTELT:
						case ConstantsBlock.CST_CODE_CE_INSERTELT:
						case ConstantsBlock.CST_CODE_CE_SHUFFLEVEC:
						case ConstantsBlock.CST_CODE_CE_CMP:
						case ConstantsBlock.CST_CODE_INLINEASM: {
							result.addContents(record);
							debug("Saw CST_CODE_??? record: " + record);
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
		debug("Exiting ConstantsBlock");
		
		return result;
	}

	private FunctionBlock readFunctionBlock(EnterSubblock enter, TypeBlock typeBlock) {
		pushBlock(enter);
		
		debug("Entering FunctionBlock:");
		tabs++;
		FunctionBlock result = new FunctionBlock(enter);
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
						
					case ConstantsBlock.CONSTANTS_BLOCK_ID: {
						ConstantsBlock block = readConstantsBlock(subenter, typeBlock);
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
					switch (record.getCode()) {
					case FunctionBlock.FUNC_CODE_DECLAREBLOCKS: {
						result.addContents(record);
						debug("Saw FUNC_CODE_DECLAREBLOCKS: " + record);
						break;
					}
					case FunctionBlock.FUNC_CODE_INST_BINOP: 
					case FunctionBlock.FUNC_CODE_INST_CAST:
					case FunctionBlock.FUNC_CODE_INST_GEP:
					case FunctionBlock.FUNC_CODE_INST_SELECT:
					case FunctionBlock.FUNC_CODE_INST_EXTRACTELT:
					case FunctionBlock.FUNC_CODE_INST_INSERTELT:
					case FunctionBlock.FUNC_CODE_INST_SHUFFLEVEC:
					case FunctionBlock.FUNC_CODE_INST_CMP:
					case FunctionBlock.FUNC_CODE_INST_PHI:
					case FunctionBlock.FUNC_CODE_INST_MALLOC:
					case FunctionBlock.FUNC_CODE_INST_FREE:
					case FunctionBlock.FUNC_CODE_INST_ALLOCA:
					case FunctionBlock.FUNC_CODE_INST_LOAD:
					case FunctionBlock.FUNC_CODE_INST_STORE:
					case FunctionBlock.FUNC_CODE_INST_CALL:
					case FunctionBlock.FUNC_CODE_INST_VAARG:
					case FunctionBlock.FUNC_CODE_INST_STORE2:
					case FunctionBlock.FUNC_CODE_INST_GETRESULT:
					case FunctionBlock.FUNC_CODE_INST_UNWIND:
					case FunctionBlock.FUNC_CODE_INST_UNREACHABLE:
					case FunctionBlock.FUNC_CODE_INST_INVOKE:
					case FunctionBlock.FUNC_CODE_INST_SWITCH:
					case FunctionBlock.FUNC_CODE_INST_BR:
					case FunctionBlock.FUNC_CODE_INST_RET: {
						result.addContents(record);
						debug("Saw instruction: " + record);
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
		debug("Exiting FunctionBlock");
		
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
						debug("Saw typename record: " +record);
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
						debug("Saw value symbol record: " + record);
						result.addContents(record);
					} else if (record.getCode() == ValueSymtabBlock.VST_CODE_BBENTRY) {
						debug("Saw bb symbol record: " + record);
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
					if (record.getCode() != SETBID_CODE) {
						debug("Saw unknown data record, ignoring: " + record);
						continue;
					} else {
						debug("Saw SETBID record: " + record);
					}
					OperandValue value = record.getOp(0);
					if (!value.isNumeric())
						throw new RuntimeException("BID must be a basic value");
					bid = (int)value.getNumericValue();
					gotbid = true;
					break;
				}
				}
			}

		tabs--;
		debug("Exiting BlockInfo");
	}



	/**
	 * Precondition: bit index is right aftre the abbrev id for this record,
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
		BitcodeReader.DEBUG = true;
		BitcodeReader parser = new BitcodeReader(new FileInputStream(args[0]));
		parser.readBitcode();
	}
}
