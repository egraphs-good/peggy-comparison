package llvm.bitcode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * This class is responsible for writing out an LLVM 2.3 Module to a stream.
 */
public class BitcodeWriter {
	protected final BitSet bits;
	protected int bitIndex;
	protected int size;
	
	public BitcodeWriter() {
		this.bits = new BitSet(1000000);
		this.size = 0;
		this.bitIndex = 0;
	}
	
	public void dump(OutputStream out) throws IOException {
		int numBytes = (this.size+7)>>3;
		for (int i = 0; i < numBytes; i++) {
			int value = 0;
			for (int j = 0; j < 8; j++) {
				if (this.bits.get((i<<3)+j))
					value |= (1<<j);
			}
			out.write(value);
		}
	}
	
	//////// General-purpose bit writing methods ////////////
	
	public void WriteWriter(BitcodeWriter subwriter) {
		BitSet hisbits = subwriter.bits;
		for (int i = 0; i < subwriter.bitIndex; i++) {
			this.bits.set(this.bitIndex++, hisbits.get(i));
		}
	}
	
	public void Write(int value, int n) {
		if (n < 0 || n > 32)
			throw new IllegalArgumentException("n is invalid: " + n);
		for (int i = 0; i < n; i++) {
			this.bits.set(this.bitIndex++, (value&(1<<i)) != 0);
		}
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}
	
	public void Write64(long value, int n) {
		if (n < 0 || n > 64)
			throw new IllegalArgumentException("n is invalid: " + n);
		for (int i = 0; i < n; i++) {
			this.bits.set(this.bitIndex++, (value&(1L<<i)) != 0L);
		}
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}
	
	public void WriteVBR(int value, int width) {
		if (width <= 1 || width > 31)
			throw new IllegalArgumentException("width is invalid: " + width);
		while ((value>>>(width-1)) != 0) {
			Write(value, width-1);
			this.bits.set(this.bitIndex++);
			value >>>= (width-1);
		}
		
		Write(value, width-1);
		this.bits.set(this.bitIndex++, false);
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}

	public void WriteVBR64(long value, int width) {
		if (width <= 1 || width > 63)
			throw new IllegalArgumentException("width is invalid: " + width);
		while ((value>>>(width-1)) != 0L) {
			Write64(value, width-1);
			this.bits.set(this.bitIndex++);
			value >>>= (width-1);
		}
		
		Write64(value, width-1);
		this.bits.set(this.bitIndex++, false);
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}
	
	public void align32() {
		int byteIndex = this.bitIndex;
		byteIndex = byteIndex + ((32-(byteIndex&31))&31);
		this.bitIndex = byteIndex;
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}

	/////////////////////////////////////////////////

	public void writeEndBlock(int abbrevLength) {
		Write(EndBlock.END_BLOCK_ID, abbrevLength);
		align32();
	}
	
	public void writeUnabbrevRecord(int abbrevLength, UnabbrevRecord record) {
		Write(UnabbrevRecord.UNABBREV_RECORD_ID, abbrevLength);
		WriteVBR(record.getCode(), 6);
		WriteVBR(record.getNumUnflattenedOps(), 6);
		for (int i = 0; i < record.getNumUnflattenedOps(); i++) {
			OperandValue value = record.getOp(i);
			WriteVBR64(value.getNumericValue(), 6);
		}
	}
	
	/**
	 * Writes out an EnterSubblock record and returns the bit index
	 * where the 'blocklength' field was written. This assumes that
	 * the blocklength field of the given EnterSubblock record is
	 * bupkus and will be patched later with patchEnterSubblockSize.
	 */
	public int writeEnterSubblock(int abbrevLength, EnterSubblock block) {
		Write(EnterSubblock.ENTER_SUBBLOCK_ID, abbrevLength);
		WriteVBR(block.getBlockID(), 8);
		WriteVBR(block.getNewAbbrevLen(), 4);
		align32();
		int blockSizeIndex = this.bitIndex;
		// save this location and patch it up later
		Write(block.getBlockLen(), 32);

		return blockSizeIndex;
	}
	
	/**
	 * Precondition: The bitindex should be at the very end of the entire block
	 * (i.e. after the EndBlock (so it'll be word-aligned for sure))
	 * 
	 * @param blockSizeIndex the bitIndex of the blocklength field to patch
	 */
	public void patchEnterSubblockSize(int blockSizeIndex) {
		// patch up the size entry
		int endIndex = this.bitIndex;
		int length = endIndex - blockSizeIndex - 32;
		length = (length+31)>>5;
		this.bitIndex = blockSizeIndex;
		Write(length, 32);
		
		this.bitIndex = endIndex;
		if (this.size < this.bitIndex)
			this.size = this.bitIndex;
	}
}
