package llvm.bitcode;

import java.util.List;
import java.util.Map;

import llvm.bitcode.FunctionDecoder2_8.Int;
import llvm.instructions.Instruction;
import llvm.types.Type;
import llvm.values.Module;
import llvm.values.Value;

/**
 * This class reads and decodes the metadata attachment block in an LLVM 2.8
 * bitcode file.
 */
public class MetadataAttachmentDecoder {
	public static void decodeMetadataAttachment(
			MetadataAttachmentBlock2_8 block,
			Module module,
			Map<Integer,Integer> mdkindmap,
			List<Value> metadataValueTable,
			List<Instruction> allinsts) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			final BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock())
				throw new RuntimeException("No subblocks for MetadataBlock");

			final DataRecord record = bc.getDataRecordSelf();
			switch (record.getCode()) {
			case MetadataAttachmentBlock2_8.METADATA_ATTACHMENT:
			case MetadataAttachmentBlock2_8.METADATA_ATTACHMENT2: {
				Int index = new Int(0);
				Instruction inst = allinsts.get((int)record.getOp(index.value++).getNumericValue());
				while (index.value < record.getNumOps()) {
					int kind = (int)record.getOp(index.value++).getNumericValue();
					int newkind = mdkindmap.get(kind);
					Value data = FunctionDecoder2_8.getValue(record, index, Type.METADATA_TYPE, metadataValueTable);
					inst.setMetadata(newkind, data);
				}
				break;
			}
			default:
				throw new IllegalStateException("Invalid code: " + record.getCode());
			}
		}
	}
}
