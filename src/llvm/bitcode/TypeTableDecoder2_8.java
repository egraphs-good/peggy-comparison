package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

import llvm.types.*;

/**
 * This class decodes the module types from the type block of an
 * LLVM 2.8 bitode file.
 */
public class TypeTableDecoder2_8 {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("TypeTableDecoder2_8: " + message);
	}
	
	private static Type parseType(HolderType[] typeTable, List<DataRecord> typeRecords, int index) {
		DataRecord record = typeRecords.get(index);
		
		switch (record.getCode()) {
		case TypeBlock2_8.TYPE_CODE_NUMENTRY:
			throw new RuntimeException("How did this happen?");
		case TypeBlock2_8.TYPE_CODE_VOID:
			return Type.VOID_TYPE;
		case TypeBlock2_8.TYPE_CODE_FLOAT:
			return Type.getFloatingPointType(FloatingPointType.Kind.FLOAT);
		case TypeBlock2_8.TYPE_CODE_DOUBLE:
			return Type.getFloatingPointType(FloatingPointType.Kind.DOUBLE);
		case TypeBlock2_8.TYPE_CODE_LABEL:
			return Type.LABEL_TYPE;
		case TypeBlock2_8.TYPE_CODE_OPAQUE:
			return new OpaqueType();
		case TypeBlock2_8.TYPE_CODE_X86_FP80:
			return Type.getFloatingPointType(FloatingPointType.Kind.X86_FP80);
		case TypeBlock2_8.TYPE_CODE_FP128:
			return Type.getFloatingPointType(FloatingPointType.Kind.FP128);
		case TypeBlock2_8.TYPE_CODE_PPC_FP128:
			return Type.getFloatingPointType(FloatingPointType.Kind.PPC_FP128);
		case TypeBlock2_8.TYPE_CODE_METADATA:
			return Type.METADATA_TYPE;
		case TypeBlock2_8.TYPE_CODE_INTEGER:
			return Type.getIntegerType((int)record.getOp(0).getNumericValue());
			
		case TypeBlock2_8.TYPE_CODE_POINTER: {
			int pointeeTypeIndex = (int)record.getOp(0).getNumericValue();
			Type pointee = typeTable[pointeeTypeIndex];
			
			if (record.getNumOps() > 1)
				return new PointerType(pointee, (int)record.getOp(1).getNumericValue());
			else
				return new PointerType(pointee);
		}
		
		case TypeBlock2_8.TYPE_CODE_FUNCTION: {
			boolean isVararg = (record.getOp(0).getNumericValue() != 0L);
			Type returnType = typeTable[(int)record.getOp(2).getNumericValue()];
			if (returnType.isHolder()) {
				returnType.getHolderSelf().setInnerType(parseType(typeTable, typeRecords, (int)record.getOp(2).getNumericValue())); 
			}
			
			List<Type> paramTypes = new ArrayList<Type>(record.getNumOps()-2);
			for (int i = 3; i < record.getNumOps(); i++) {
				Type param = typeTable[(int)record.getOp(i).getNumericValue()];
				if (param.isHolder()) {
					param.getHolderSelf().setInnerType(parseType(typeTable, typeRecords, (int)record.getOp(i).getNumericValue()));
				}
				paramTypes.add(param);
			}
			return new FunctionType(returnType, paramTypes, isVararg);
		}
			
		case TypeBlock2_8.TYPE_CODE_STRUCT: {
			boolean isPacked = (record.getOp(0).getNumericValue() != 0L);
			List<Type> fieldTypes = new ArrayList<Type>(record.getNumOps());
			for (int i = 1; i < record.getNumOps(); i++) {
				int fieldTypeIndex = (int)record.getOp(i).getNumericValue();
				Type field = typeTable[fieldTypeIndex];
				fieldTypes.add(field);
			}
			return new StructureType(isPacked, fieldTypes);
		}
			
		case TypeBlock2_8.TYPE_CODE_ARRAY: {
			long numelts = record.getOp(0).getNumericValue();
			
			debug("Array size operand: " + record.getOp(0));
			
			Type elttype = typeTable[(int)record.getOp(1).getNumericValue()];
			return new ArrayType(elttype, numelts);
		}
		
		case TypeBlock2_8.TYPE_CODE_VECTOR: {
			long numelts = record.getOp(0).getNumericValue();
			Type elttype = typeTable[(int)record.getOp(1).getNumericValue()];
			if (elttype.isHolder()) {
				elttype.getHolderSelf().setInnerType(parseType(typeTable, typeRecords, (int)record.getOp(1).getNumericValue()));
			}
			return new VectorType(elttype, numelts);
		}
		default: // invalid!
			throw new IllegalArgumentException("Invalid record code for typecode");
		}
	}

	
	/**
	 * Returns an array of types that corresponds to the type table
	 * encoded in the given TypeBlock2_8. This method is smart enough to deal
	 * with forward references.
	 */
	public static Type[] decodeTypeTable(TypeBlock2_8 block) {
		debug("Begin decoding type table");
		
		List<DataRecord> typeRecords = new ArrayList<DataRecord>(block.getNumBlockContents());
		
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			DataRecord record = block.getBlockContents(i).getDataRecordSelf();
			switch (record.getCode()) {
			case TypeBlock2_8.TYPE_CODE_NUMENTRY:
				break;
			case TypeBlock2_8.TYPE_CODE_VOID:
			case TypeBlock2_8.TYPE_CODE_FLOAT:
			case TypeBlock2_8.TYPE_CODE_DOUBLE:
			case TypeBlock2_8.TYPE_CODE_LABEL:
			case TypeBlock2_8.TYPE_CODE_OPAQUE:
			case TypeBlock2_8.TYPE_CODE_X86_FP80:
			case TypeBlock2_8.TYPE_CODE_FP128:
			case TypeBlock2_8.TYPE_CODE_PPC_FP128:
			case TypeBlock2_8.TYPE_CODE_INTEGER:
			case TypeBlock2_8.TYPE_CODE_POINTER:
			case TypeBlock2_8.TYPE_CODE_FUNCTION:
			case TypeBlock2_8.TYPE_CODE_STRUCT:
			case TypeBlock2_8.TYPE_CODE_ARRAY:
			case TypeBlock2_8.TYPE_CODE_VECTOR:
			case TypeBlock2_8.TYPE_CODE_METADATA:
				typeRecords.add(record);
				break;
			default: // invalid!
				throw new RuntimeException("Invalid record code for typecode");
			}
		}
		
		HolderType[] typeTable = new HolderType[typeRecords.size()];
		// initialize with all holders
		for (int i = 0; i < typeRecords.size(); i++) {
			typeTable[i] = new HolderType();
		}
		for (int i = 0; i < typeRecords.size(); i++) {
			if (typeTable[i].isHolder()) {
				Type result = parseType(typeTable, typeRecords, i);
				typeTable[i].setInnerType(result);
			}
		}
		
		debug("Done decoding type table");
		
		return typeTable;
	}
}
