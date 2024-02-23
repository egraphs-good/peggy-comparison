package llvm.bitcode;

import java.util.*;

import llvm.types.*;
import llvm.values.Module;

/**
 * This class is responsible for emitting the TypeBlock and the TypeSymtabBlock,
 * as well as flattening the type table and constructing type name map. 
 * It uses a BitcodeWriter to do the emitting.
 * 
 * @author steppm
 */
class TypeEncoder {
	protected final BitcodeWriter writer;
	protected final int abbrevLength;
	protected final HashList<Type> typeTable;
	protected boolean wroteTypeBlock = false;
	
	public TypeEncoder(BitcodeWriter _writer, int _abbrevLength) {
		this.writer = _writer;
		this.abbrevLength = _abbrevLength;
		this.typeTable = new HashList<Type>();
	}
	
	public HashList<Type> getTypeTable() {
		if (!this.wroteTypeBlock)
			throw new IllegalStateException("Have not emitted yet");
		return this.typeTable;
	}
	
	/**
	 * Flattens out the type collection given and build the type table,
	 * then emits the TypeBlock. The 'index2name' map is an output parameter that
	 * gets filled up with the mapping from indexes-of-types-with-names to the names.
	 * This is used for building the TypeSymtabBlock. 
	 * Returns the type table HashList.
	 */
	public void writeTypeBlock(Collection<? extends Type> types) {
		if (this.wroteTypeBlock)
			throw new RuntimeException("Should not emit twice");
		
		// write EnterSubblock record
		final int innerAbbrevLength = 4;
		int blockSizeIndex = writer.writeEnterSubblock(
				abbrevLength,
				new EnterSubblock(TypeBlock.TYPE_BLOCK_ID, innerAbbrevLength, 0));
		
		// flatten and order the types
		LinkedList<Type> queue = new LinkedList<Type>(types);
		HashList<Type> initialTypeTable = new HashList<Type>();
		while (!queue.isEmpty()) {
			Type next = queue.removeFirst();
			if (initialTypeTable.hasValue(next))
				continue;
			
			initialTypeTable.add(next);
			
			for (Iterator<Type> iter = next.getSubtypes(); iter.hasNext(); ) {
				queue.addLast(iter.next());
			}
		}
		// flattened types, now topo-order them
		
		orderTypes(initialTypeTable, this.typeTable);
		initialTypeTable = null;
		
		// start emitting!
		List<Long> ops = new ArrayList<Long>(10);

		// emit numentries record
		ops.clear();
		ops.add((long)this.typeTable.size());
		writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_NUMENTRY, ops)); 

		// TODO: make some abbreviations??
		
		for (int i = 0; i < this.typeTable.size(); i++) {
			ops.clear();
			
			Type type = this.typeTable.getValue(i);
			
			if (type.isVoid()) {
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_VOID, ops));
			} else if (type.isFloatingPoint()) {
				FloatingPointType.Kind kind = type.getFloatingPointSelf().getKind();
				
				switch (kind) {
				case FLOAT:
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_FLOAT, ops));
					break;
				case DOUBLE:
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_DOUBLE, ops));
					break;
				case X86_FP80:
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_X86_FP80, ops));
					break;
				case FP128:
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_FP128, ops));
					break;
				case PPC_FP128:
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_PPC_FP128, ops));
					break;
				default:
					throw new RuntimeException("This shouldn't happen");
				}
			} else if (type.isLabel()) {
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_LABEL, ops));
			} else if (type.isOpaque()) {
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_OPAQUE, ops));
			} else if (type.isInteger()) {
				ops.add((long)type.getIntegerSelf().getWidth());
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_INTEGER, ops));
			} else if (type.isComposite()) {
				CompositeType ctype = type.getCompositeSelf();
				if (ctype.isArray()) {
					int subIndex = this.typeTable.getIndex(ctype.getArraySelf().getElementType());
					ops.add(ctype.getArraySelf().getNumElements().signedValue());
					ops.add((long)subIndex);
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_ARRAY, ops));
				} else if (ctype.isPointer()) {
					int subIndex = this.typeTable.getIndex(ctype.getPointerSelf().getPointeeType());
					ops.add((long)subIndex);
					if (ctype.getPointerSelf().getAddressSpace() != 0)
						ops.add((long)ctype.getPointerSelf().getAddressSpace());
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_POINTER, ops));
				} else if (ctype.isStructure()) {
					StructureType stype = ctype.getStructureSelf();
					ops.add(stype.isPacked() ? 1L : 0L);
					for (int j = 0; j < stype.getNumFields(); j++) {
						int subIndex = this.typeTable.getIndex(stype.getFieldType(j));
						ops.add((long)subIndex);
					}
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_STRUCT, ops));
				} else if (ctype.isVector()) {
					int subIndex = this.typeTable.getIndex(ctype.getVectorSelf().getElementType());
					ops.add(ctype.getVectorSelf().getNumElements().signedValue());
					ops.add((long)subIndex);
					writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_VECTOR, ops));
				} else {
					throw new RuntimeException("This shouldn't happen");
				}
			} else if (type.isFunction()) {
				FunctionType ftype = type.getFunctionSelf();
				ops.add(ftype.isVararg() ? 1L : 0L);
				ops.add(0L);
				int returnIndex = this.typeTable.getIndex(ftype.getReturnType());
				ops.add((long)returnIndex);
				for (int j = 0; j < ftype.getNumParams(); j++) {
					int subIndex = this.typeTable.getIndex(ftype.getParamType(j));
					ops.add((long)subIndex);
				}
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(TypeBlock.TYPE_CODE_FUNCTION, ops));
			} else {
				throw new RuntimeException("This shouldn't happen");
			}
		}
		
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(blockSizeIndex);
		
		this.wroteTypeBlock = true;
	}
	
	
	/**
	 * Emits the type symbol table. This method must be run after writeTypeBlock.
	 */
	public void writeTypeSymtabBlock(Module module) {
		if (!this.wroteTypeBlock)
			throw new IllegalStateException("Must write TypeBlock first");
		
		Set<String> typeNames = module.getTypeNames(); 
		if (typeNames.size() == 0)
			return;
		
		// write EnterSubblock record
		final int innerAbbrevLength = 2;
		int blockSizeIndex = this.writer.writeEnterSubblock(
				this.abbrevLength, 
				new EnterSubblock(TypeSymtabBlock.TYPE_SYMTAB_BLOCK_ID, innerAbbrevLength, 0));

		List<Long> ops = new ArrayList<Long>(20);
		for (String name : typeNames) {
			Type t = module.getTypeByName(name);
			int index = this.typeTable.getIndex(t);
			
			ops.clear();
			ops.add((long)index);
			for (int i = 0; i < name.length(); i++) {
				ops.add((long)name.charAt(i));
			}
			
			UnabbrevRecord record = new UnabbrevRecord(TypeSymtabBlock.TST_CODE_ENTRY, ops);
			this.writer.writeUnabbrevRecord(innerAbbrevLength, record);
		}
		
		this.writer.writeEndBlock(innerAbbrevLength);
		this.writer.patchEnterSubblockSize(blockSizeIndex);
	}
	
	
	/**
	 * Makes a topological sorting of the type table.
	 */
	private void orderTypes(HashList<Type> input, HashList<Type> result) {
		boolean got[] = new boolean[input.size()];
		int leftover = got.length;
		
		for (boolean progress = true; progress && (leftover > 0); ) {
			progress = false;
			
			for (int index = 0; index < got.length; index++) {
				if (!got[index]) {
					Type t = input.getValue(index);
					boolean missing = false;
					for (Iterator<? extends Type> iter=t.getSubtypes(); iter.hasNext(); ) {
						Type sub = iter.next();
						if (!result.hasValue(sub)) {
							missing = true;
							break;
						}
					}
					if (missing) continue;
					
					result.add(t);
					progress = true;
					leftover--;
					got[index] = true;
				}
			}
		}
		
		for (int index = 0; index < got.length; index++) {
			if (!got[index]) {
				got[index] = true;
				leftover--;
				result.add(input.getValue(index));
			}
		}
	}
}
