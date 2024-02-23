package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

import llvm.bitcode.ModuleEncoder2_8.TypeTable;
import llvm.types.Type;
import llvm.values.MetadataNodeList;
import llvm.values.MetadataNodeValue;
import llvm.values.Module;
import llvm.values.Value;

/**
 * This class is responsible for encoding the metadata blocks in an LLVM 2.8
 * bitcode file, based on a Module instance. This class will also encode the
 * metadata kind blocks.
 */
public class MetadataEncoder {
	protected final BitcodeWriter writer;
	protected final int abbrevLength;
	protected final TypeTable typeTable;
	protected final List<Long> ops;

	public MetadataEncoder(BitcodeWriter _writer, int _abbrevLength, TypeTable _typeTable) {
		this.writer = _writer;
		this.abbrevLength = _abbrevLength;
		this.typeTable = _typeTable;
		this.ops = new ArrayList<Long>();
	}
	
	public void writeMetadataBlock(
			Module module,
			HashList<Value> metadataTable,
			HashList<Value> valueTable) {
		writeMetadataBlock(module, 0, metadataTable, valueTable);
	}
	public void writeMetadataBlock(
			Module module,
			int startIndex,
			HashList<Value> metadataTable,
			HashList<Value> valueTable) {
		final int innerAbbrevLength = 5;
		int patch = this.writer.writeEnterSubblock(this.abbrevLength, new EnterSubblock(MetadataBlock2_8.METADATA_BLOCK_ID, innerAbbrevLength, 0));
		
		// do named nodes
		for (String name : module.getNamedMetadataNames()) {
			// output the METADATA_NAME record
			ops.clear();
			for (int i = 0; i < name.length(); i++) {
				ops.add((long)name.charAt(i));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(MetadataBlock2_8.METADATA_NAME, ops));

			MetadataNodeList named = module.getNamedMetadata(name);
			// output the METADATA_NAMED_NODE2 record
			ops.clear();
			for (int i = 0; i < named.getNumNodes(); i++) {
				ops.add((long)metadataTable.getIndex(named.getNode(i)));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(MetadataBlock2_8.METADATA_NAMED_NODE2, ops));
		}
		
		// write out the metadata values
		for (int i = startIndex; i < metadataTable.size(); i++) {
			Value metadata = metadataTable.getValue(i);
			if (metadata.isMetadataNode()) {
				ops.clear();
				MetadataNodeValue node = metadata.getMetadataNodeSelf(); 
				for (int j = 0; j < node.getNumValues(); j++) {
					Value item = node.getValue(j);
					if (item == null) {
						ops.add((long)typeTable.getTypeIndex(Type.VOID_TYPE));
						ops.add(0L);
					} else {
						ops.add((long)typeTable.getTypeIndex(item.getType()));
						if (item.getType().isMetadata()) {
							ops.add((long)metadataTable.getIndex(item));
						} else if (!item.getType().isVoid()) {
							ops.add((long)valueTable.getIndex(item));
						}
					}
				}
				int code = node.isFunctionLocal() ? MetadataBlock2_8.METADATA_FN_NODE2 : MetadataBlock2_8.METADATA_NODE2;
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(code, ops));
			} 
			else if (metadata.isMetadataString()) {
				String value = metadata.getMetadataStringSelf().getValue();
				ops.clear();
				for (int j = 0; j < value.length(); j++) {
					ops.add((long)value.charAt(j));
				}
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(MetadataBlock2_8.METADATA_STRING, ops));
			}
			else 
				throw new RuntimeException("Not a metadata node: " + metadata);
		}

		this.writer.writeEndBlock(innerAbbrevLength);
		this.writer.patchEnterSubblockSize(patch);
	}
	
	
	public void writeMetadataKindsBlock(
			Module module) {
		final int innerAbbrevLength = 5;
		int patch = this.writer.writeEnterSubblock(this.abbrevLength, new EnterSubblock(MetadataBlock2_8.METADATA_BLOCK_ID, innerAbbrevLength, 0));
		
		// do the metadata kinds
		for (String kindname : module.getMetadataKindNames()) {
			int kind = module.getMetadataKind(kindname);
			ops.clear();
			ops.add((long)kind);
			for (int j = 0; j < kindname.length(); j++) {
				ops.add((long)kindname.charAt(j));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(MetadataBlock2_8.METADATA_KIND, ops));
		}
		
		this.writer.writeEndBlock(innerAbbrevLength);
		this.writer.patchEnterSubblockSize(patch);
	}
}
