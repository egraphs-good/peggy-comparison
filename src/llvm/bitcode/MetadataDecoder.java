package llvm.bitcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import llvm.bitcode.FunctionDecoder2_8.Int;
import llvm.types.Type;
import llvm.values.MetadataNodeList;
import llvm.values.MetadataNodeValue;
import llvm.values.MetadataStringValue;
import llvm.values.Module;
import llvm.values.Value;

/**
 * This class is responsible for reading the metadata blocks from an LLVM 2.8
 * bitcode file. 
 */
public class MetadataDecoder {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("MetadataDecoder: " + message);
	}
	
	public static List<Value> decodeMetadata(
			boolean LLVM2_7MetadataDetected,
			ModuleBlock2_8 block,
			Module module,
			Type[] typeTable,
			Map<Integer,Integer> mdkindmap,
			List<Value> valueTable) {
		int metadataValueIndex = 0;
		LinkedList<Value> metadataValueTable = new LinkedList<Value>();
		for (int b = 0; b < block.getNumBlockContents(); b++) {
			BlockContents bc = block.getBlockContents(b);
			if (!bc.isBlock()) continue;
			Block subblock = bc.getBlockSelf();
			if (subblock.isMetadata2_8()) {
				metadataValueIndex = decodeMetadataBlock(
						LLVM2_7MetadataDetected,
						metadataValueIndex,
						subblock.getMetadata2_8Self(),
						module,
						typeTable,
						mdkindmap,
						valueTable,
						metadataValueTable);
			}
		}
		
		return metadataValueTable;
	}
		

	public static int decodeMetadataBlock(
			boolean LLVM2_7MetadataDetected,
			int metadataValueIndex,
			MetadataBlock2_8 block,
			Module module,
			Type[] typeTable,
			Map<Integer,Integer> mdkindmap,
			List<Value> valueTable,
			List<Value> metadataValueTable) {
		String lastname = null;
		boolean isFunctionLocal = false;
		
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			final BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock())
				throw new RuntimeException("No subblocks for MetadataBlock");

			final DataRecord record = bc.getDataRecordSelf();
			switch (record.getCode()) {
			case MetadataBlock2_8.METADATA_NAME: {
				debug("Decoding METADATA_NAME");
				lastname = "";
				for (int j = 0; j < record.getNumOps(); j++)
					lastname += (char)record.getOp(j).getNumericValue();
				break;
			}

			// follows NAME
			case MetadataBlock2_8.METADATA_NAMED_NODE:
			case MetadataBlock2_8.METADATA_NAMED_NODE2: {
				debug("Decoding METADATA_NAMED_NODE{2}");
				MetadataNodeList nodes = module.getNamedMetadata(lastname);
				Int index = new Int(0);
				for (int j = 0; j < record.getNumOps(); j++) {
					nodes.addNode(FunctionDecoder2_8.getValue(record, index, Type.METADATA_TYPE, metadataValueTable));
				}
				if (LLVM2_7MetadataDetected)
					addToList(metadataValueTable, null, metadataValueIndex++); // named nodes have value numbers 
				break;
			}
				
			case MetadataBlock2_8.METADATA_STRING: {
				debug("Decoding METADATA_STRING");
				StringBuilder value = new StringBuilder();
				for (int j = 0; j < record.getNumOps(); j++) {
					value.append((char)record.getOp(j).getNumericValue());
				}
				
				addToList(metadataValueTable, new MetadataStringValue(value.toString()), metadataValueIndex++);
				break;
			}
			
			case MetadataBlock2_8.METADATA_FN_NODE:
			case MetadataBlock2_8.METADATA_FN_NODE2:
				isFunctionLocal = true;
				debug("isFunctionLocal");
				
			case MetadataBlock2_8.METADATA_NODE:
			case MetadataBlock2_8.METADATA_NODE2: {
				debug("Decoding METADATA_{FN}_NODE{2}");

				List<Value> values = new ArrayList<Value>();
				Int index = new Int(0);
				while (index.value < record.getNumOps()) {
					Type type = typeTable[(int)record.getOp(index.value++).getNumericValue()];
					Value value = null;
					if (type.isMetadata()) {
						value = FunctionDecoder2_8.getValue(record, index, type, metadataValueTable);
					} else if (!type.isVoid()) {
						value = FunctionDecoder2_8.getValue(record, index, type, valueTable);
					} 
					else {
						index.value++;
					}
					values.add(value);
				}
				
				addToList(metadataValueTable, new MetadataNodeValue(isFunctionLocal, values), metadataValueIndex++);
				isFunctionLocal = false;
				break;
			}
				
			case MetadataBlock2_8.METADATA_KIND: {
				debug("Decoding METADATA_KIND");

				// registers a new (local) metadata kind
				int kind = (int)record.getOp(0).getNumericValue();
				StringBuilder builder = new StringBuilder();
				for (int j = 1; j < record.getNumOps(); j++) {
					builder.append((char)record.getOp(j).getNumericValue());
				}
				String name = builder.toString();
				int newkind = module.getMetadataKind(name);
				mdkindmap.put(kind, newkind);
				break;
			}
			
			default:
				throw new IllegalStateException("Invalid code for metadata block: " + record.getCode());
			}
		}
		
		return metadataValueIndex;
	}
	
	private static void addToList(List<Value> list, Value entry, int index) {
		if (index < list.size()) {
			// has a holder already
			Value holder = list.get(index);
			if (!holder.isHolder()) 
				throw new RuntimeException("Value should be a holder");
			holder.getHolderSelf().setInnerValue(entry);
		} else {
			list.add(entry);
		}
	}
}
