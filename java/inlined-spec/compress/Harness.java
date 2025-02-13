/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 * All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * Modified by Kaivalya M. Dixit & Don McCauley (IBM) to read input files This
 * source code is provided as is, without any express or implied warranty.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public final class Harness {
    public static final String[] FILES_NAMES = new String[] {
            "resources/compress/input/202.tar",
            "resources/compress/input/205.tar",
            "resources/compress/input/208.tar",
            "resources/compress/input/209.tar",
            "resources/compress/input/210.tar",
            "resources/compress/input/211.tar",
            "resources/compress/input/213x.tar",
            "resources/compress/input/228.tar",
            "resources/compress/input/239.tar",
            "resources/compress/input/misc.tar" };
    public static final int FILES_NUMBER = FILES_NAMES.length;
    public static final int LOOP_COUNT = 2;
    public static Source[] SOURCES;
    public static byte[][] COMPRESS_BUFFERS;
    public static byte[][] DECOMPRESS_BUFFERS;
    public static Compress CB;

    public void runCompress(int btid) {
        for (int i = 0; i < LOOP_COUNT; i++) {
            for (int j = 0; j < FILES_NUMBER; j++) {
                Source source = SOURCES[j];
                OutputBuffer comprBuffer, decomprBufer;
                InputBuffer in1 = new InputBuffer(source.length, source.buffer);
                OutputBuffer out1 = new OutputBuffer(COMPRESS_BUFFERS[btid - 1]);
                if (CB.COMPRESS == Compress.COMPRESS) {
                    Compressor compressor1 = new Compressor(in1, out1);
                    int fcode1;
                    int i5 = 0;
                    int c3;
                    int disp1;
                    int hshift1 = 0;
                    int ent1 = compressor1.input.cnt-- > 0 ? (compressor1.input.buffer[compressor1.input.current++] & 0x00FF) : -1;
                    for (fcode1 = compressor1.htab.size; fcode1 < 65536; fcode1 *= 2) {
                        hshift1++;
                    }
                    hshift1 = 8 - hshift1; /* set hash code range bound */
                    int hsizeReg1 = compressor1.htab.size;
                    /* clear hash table */
                    for (int i6 = 0; i6 < compressor1.htab.size; i6++) {
                        compressor1.htab.tab[i6] = -1;
                    }
                    next_byte: while ((c3 = compressor1.input.cnt-- > 0
                            ? (compressor1.input.buffer[compressor1.input.current++] & 0x00FF)
                            : -1) != -1) {
                        compressor1.inCount++;
                        fcode1 = (((int) c3 << compressor1.maxBits) + ent1);
                        i5 = ((c3 << hshift1) ^ ent1); /* xor hashing */
                        int temphtab1 = compressor1.htab.tab[i5];
                        if (temphtab1 == fcode1) {
                            ent1 = (int) compressor1.codetab.tab[i5] << 16 >>> 16;
                            continue next_byte;
                        }
                        if (temphtab1 >= 0) { /* non-empty slot dm kmd 4/15 */
                            disp1 = hsizeReg1 - i5; /* secondary hash (after G. Knott) */
                            if (i5 == 0) {
                                disp1 = 1;
                            }
                            do {
                                if ((i5 -= disp1) < 0) {
                                    i5 += hsizeReg1;
                                }
                                temphtab1 = compressor1.htab.tab[i5];
                                if (temphtab1 == fcode1) {
                                    ent1 = (int) compressor1.codetab.tab[i5] << 16 >>> 16;
                                    continue next_byte;
                                }
                            } while (temphtab1 > 0);
                        }
                        int code5 = ent1;
                        int rOff3 = compressor1.offset, bits3 = compressor1.bitsNumber;
                        int bp3 = 0;
                        if (code5 >= 0) {
                            /*
                             * Get to the first byte.
                             */
                            bp3 += rOff3 >> 3;
                            rOff3 &= 7;
                            /*
                             * Since code is always >= 8 bits, only need to mask the first hunk
                             * on the left.
                             */
                            compressor1.buf[bp3] = (byte) ((compressor1.buf[bp3] & Compress.rmask[rOff3]) | (code5 << rOff3)
                                    & Compress.lmask[rOff3]);
                            bp3++;
                            bits3 -= 8 - rOff3;
                            code5 >>= 8 - rOff3;
                            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                            if (bits3 >= 8) {
                                compressor1.buf[bp3++] = (byte) code5;
                                code5 >>= 8;
                                bits3 -= 8;
                            }
                            /* Last bits. */
                            if (bits3 != 0) {
                                compressor1.buf[bp3] = (byte) code5;
                            }
                            compressor1.offset += compressor1.bitsNumber;
                            if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                                bp3 = 0;
                                bits3 = compressor1.bitsNumber;
                                compressor1.bytesOut += bits3;
                                do {
                                    byte c4 = compressor1.buf[bp3++];
                                    compressor1.output.buffer[compressor1.output.cnt++] = c4;
                                } while (--bits3 != 0);
                                compressor1.offset = 0;
                            }
                            /*
                             * If the next entry is going to be too big for the code size, then
                             * increase it, if possible.
                             */
                            if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                                /*
                                 * Write the whole buffer, because the input side won't discover
                                 * the size increase until after it has read it.
                                 */
                                if (compressor1.offset > 0) {
                                    for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                        compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                    }
                                    compressor1.bytesOut += compressor1.bitsNumber;
                                }
                                compressor1.offset = 0;
                                if (compressor1.clearFlag != 0) {
                                    compressor1.bitsNumber = Compress.INIT_BITS;
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                    compressor1.clearFlag = 0;
                                } else {
                                    compressor1.bitsNumber++;
                                    if (compressor1.bitsNumber == compressor1.maxBits) {
                                        compressor1.maxCode = compressor1.maxMaxCode;
                                    } else {
                                        compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                    }
                                }
                            }
                        } else {
                            /*
                             * At EOF, write the rest of the buffer.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                            }
                            compressor1.bytesOut += (compressor1.offset + 7) / 8;
                            compressor1.offset = 0;
                        }
                        compressor1.outCount++;
                        ent1 = c3;
                        if (compressor1.freeEntry < compressor1.maxMaxCode) {
                            /* code -> hashtable */
                            int v1 = compressor1.freeEntry++;
                            compressor1.codetab.tab[i5] = (short) v1;
                            compressor1.htab.tab[i5] = fcode1;
                        } else if (compressor1.inCount >= compressor1.checkpoint && compressor1.blockCompress != 0) {
                            int rat1;
                            compressor1.checkpoint = compressor1.inCount + Compressor.CHECK_GAP;
                            if (compressor1.inCount > 0x007fffff) { /* shift will overflow */
                                rat1 = compressor1.bytesOut >> 8;
                                if (rat1 == 0) { /* Don't divide by zero */
                                    rat1 = 0x7fffffff;
                                } else {
                                    rat1 = compressor1.inCount / rat1;
                                }
                            } else {
                                rat1 = (compressor1.inCount << 8) / compressor1.bytesOut; /* 8 fractional bits */
                            }
                            if (rat1 > compressor1.ratio) {
                                compressor1.ratio = rat1;
                            } else {
                                compressor1.ratio = 0;
                                for (int i6 = 0; i6 < compressor1.htab.size; i6++) {
                                    compressor1.htab.tab[i6] = -1;
                                }
                                compressor1.freeEntry = Compress.FIRST;
                                compressor1.clearFlag = 1;
                                int code6 = (int) Compress.CLEAR;
                                int rOff4 = compressor1.offset, bits4 = compressor1.bitsNumber;
                                int bp4 = 0;
                                if (code6 >= 0) {
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp4 += rOff4 >> 3;
                                    rOff4 &= 7;
                                    /*
                                     * Since code is always >= 8 bits, only need to mask the first hunk
                                     * on the left.
                                     */
                                    compressor1.buf[bp4] = (byte) ((compressor1.buf[bp4] & Compress.rmask[rOff4]) | (code6 << rOff4)
                                            & Compress.lmask[rOff4]);
                                    bp4++;
                                    bits4 -= 8 - rOff4;
                                    code6 >>= 8 - rOff4;
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits4 >= 8) {
                                        compressor1.buf[bp4++] = (byte) code6;
                                        code6 >>= 8;
                                        bits4 -= 8;
                                    }
                                    /* Last bits. */
                                    if (bits4 != 0) {
                                        compressor1.buf[bp4] = (byte) code6;
                                    }
                                    compressor1.offset += compressor1.bitsNumber;
                                    if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                                        bp4 = 0;
                                        bits4 = compressor1.bitsNumber;
                                        compressor1.bytesOut += bits4;
                                        do {
                                            byte c4 = compressor1.buf[bp4++];
                                            compressor1.output.buffer[compressor1.output.cnt++] = c4;
                                        } while (--bits4 != 0);
                                        compressor1.offset = 0;
                                    }
                                    /*
                                     * If the next entry is going to be too big for the code size, then
                                     * increase it, if possible.
                                     */
                                    if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                                        /*
                                         * Write the whole buffer, because the input side won't discover
                                         * the size increase until after it has read it.
                                         */
                                        if (compressor1.offset > 0) {
                                            for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                            }
                                            compressor1.bytesOut += compressor1.bitsNumber;
                                        }
                                        compressor1.offset = 0;
                                        if (compressor1.clearFlag != 0) {
                                            compressor1.bitsNumber = Compress.INIT_BITS;
                                            compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                            compressor1.clearFlag = 0;
                                        } else {
                                            compressor1.bitsNumber++;
                                            if (compressor1.bitsNumber == compressor1.maxBits) {
                                                compressor1.maxCode = compressor1.maxMaxCode;
                                            } else {
                                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                            }
                                        }
                                    }
                                } else {
                                    /*
                                     * At EOF, write the rest of the buffer.
                                     */
                                    if (compressor1.offset > 0) {
                                        for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                            compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                        }
                                    }
                                    compressor1.bytesOut += (compressor1.offset + 7) / 8;
                                    compressor1.offset = 0;
                                }
                            }
                        }
                    }
                    /*
                     * Put out the final code.
                     */
                    int code5 = ent1;
                    int rOff3 = compressor1.offset, bits3 = compressor1.bitsNumber;
                    int bp3 = 0;
                    if (code5 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp3 += rOff3 >> 3;
                        rOff3 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor1.buf[bp3] = (byte) ((compressor1.buf[bp3] & Compress.rmask[rOff3]) | (code5 << rOff3)
                                & Compress.lmask[rOff3]);
                        bp3++;
                        bits3 -= 8 - rOff3;
                        code5 >>= 8 - rOff3;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits3 >= 8) {
                            compressor1.buf[bp3++] = (byte) code5;
                            code5 >>= 8;
                            bits3 -= 8;
                        }
                        /* Last bits. */
                        if (bits3 != 0) {
                            compressor1.buf[bp3] = (byte) code5;
                        }
                        compressor1.offset += compressor1.bitsNumber;
                        if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                            bp3 = 0;
                            bits3 = compressor1.bitsNumber;
                            compressor1.bytesOut += bits3;
                            do {
                                byte c4 = compressor1.buf[bp3++];
                                compressor1.output.buffer[compressor1.output.cnt++] = c4;
                            } while (--bits3 != 0);
                            compressor1.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                                compressor1.bytesOut += compressor1.bitsNumber;
                            }
                            compressor1.offset = 0;
                            if (compressor1.clearFlag != 0) {
                                compressor1.bitsNumber = Compress.INIT_BITS;
                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                compressor1.clearFlag = 0;
                            } else {
                                compressor1.bitsNumber++;
                                if (compressor1.bitsNumber == compressor1.maxBits) {
                                    compressor1.maxCode = compressor1.maxMaxCode;
                                } else {
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor1.offset > 0) {
                            for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                            }
                        }
                        compressor1.bytesOut += (compressor1.offset + 7) / 8;
                        compressor1.offset = 0;
                    }
                    compressor1.outCount++;
                    int code6 = -1;
                    int rOff4 = compressor1.offset, bits4 = compressor1.bitsNumber;
                    int bp4 = 0;
                    if (code6 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp4 += rOff4 >> 3;
                        rOff4 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor1.buf[bp4] = (byte) ((compressor1.buf[bp4] & Compress.rmask[rOff4]) | (code6 << rOff4)
                                & Compress.lmask[rOff4]);
                        bp4++;
                        bits4 -= 8 - rOff4;
                        code6 >>= 8 - rOff4;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits4 >= 8) {
                            compressor1.buf[bp4++] = (byte) code6;
                            code6 >>= 8;
                            bits4 -= 8;
                        }
                        /* Last bits. */
                        if (bits4 != 0) {
                            compressor1.buf[bp4] = (byte) code6;
                        }
                        compressor1.offset += compressor1.bitsNumber;
                        if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                            bp4 = 0;
                            bits4 = compressor1.bitsNumber;
                            compressor1.bytesOut += bits4;
                            do {
                                byte c4 = compressor1.buf[bp4++];
                                compressor1.output.buffer[compressor1.output.cnt++] = c4;
                            } while (--bits4 != 0);
                            compressor1.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                                compressor1.bytesOut += compressor1.bitsNumber;
                            }
                            compressor1.offset = 0;
                            if (compressor1.clearFlag != 0) {
                                compressor1.bitsNumber = Compress.INIT_BITS;
                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                compressor1.clearFlag = 0;
                            } else {
                                compressor1.bitsNumber++;
                                if (compressor1.bitsNumber == compressor1.maxBits) {
                                    compressor1.maxCode = compressor1.maxMaxCode;
                                } else {
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor1.offset > 0) {
                            for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                            }
                        }
                        compressor1.bytesOut += (compressor1.offset + 7) / 8;
                        compressor1.offset = 0;
                    }
                } else {
                    Decompressor decompressor1 = new Decompressor(in1, out1);
                    int code5, oldcode1, incode1;
                    int result4 = 0;
                    int code6 = 0;
                    int rOff3 = 0, bits3 = 0;
                    int bp3 = 0;
                    if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                            || decompressor1.freeEntry > decompressor1.maxCode) {
                        /*
                         * If the next entry will be too big for the current code size, then
                         * we must increase the size. This implies reading a new buffer
                         * full, too.
                         */
                        if (decompressor1.freeEntry > decompressor1.maxCode) {
                            decompressor1.bitsNumber++;
                            if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                            } else {
                                decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                            }
                        }
                        if (decompressor1.clearFlag > 0) {
                            decompressor1.bitsNumber = Compress.INIT_BITS;
                            decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                            decompressor1.clearFlag = 0;
                        }
                        int result5;
                        if (decompressor1.input.cnt <= 0) {
                            result5 = -1;
                        } else {
                            int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                            for (int i5 = 0; i5 < num1; i5++) {
                                decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                decompressor1.input.cnt--;
                            }
                            result5 = num1;
                        }
                        decompressor1.size = result5;
                        if (decompressor1.size <= 0) {
                            result4 = -1;/* end of file */
                        } else {
                            decompressor1.offset = 0;/* Round size down to integral number of codes */
                            decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                        }
                    }
                    if (result4 == 0) {
                        rOff3 = decompressor1.offset;
                        bits3 = decompressor1.bitsNumber;
                        /*
                         * Get to the first byte.
                         */
                        bp3 += rOff3 >> 3;
                        rOff3 &= 7;
                        /* Get first part (low order bits) */
                        code6 = ((decompressor1.buf[bp3++] >> rOff3) & Compress.rmask[8 - rOff3]) & 0xff;
                        bits3 -= 8 - rOff3;
                        rOff3 = 8 - rOff3; /* now, offset into code word */
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits3 >= 8) {
                            code6 |= (decompressor1.buf[bp3++] & 0xff) << rOff3;
                            rOff3 += 8;
                            bits3 -= 8;
                        }
                        /* high order bits. */
                        if (bits3 > 0) {
                            code6 |= (decompressor1.buf[bp3] & Compress.rmask[bits3]) << rOff3;
                        }
                        decompressor1.offset += decompressor1.bitsNumber;
                        result4 = code6;
                    }
                    int finchar1 = oldcode1 = result4;
                    /* EOF already? */
                    /* Get out of here */
                    if (oldcode1 != -1) {/* first code must be 8 bits = byte */
                        decompressor1.output.buffer[decompressor1.output.cnt++] = (byte) finchar1;
                        while (true) {
                            int result5 = 0;
                            int code7 = 0;
                            int rOff4 = 0, bits4 = 0;
                            int bp4 = 0;
                            if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                                    || decompressor1.freeEntry > decompressor1.maxCode) {
                                /*
                                 * If the next entry will be too big for the current code size, then
                                 * we must increase the size. This implies reading a new buffer
                                 * full, too.
                                 */
                                if (decompressor1.freeEntry > decompressor1.maxCode) {
                                    decompressor1.bitsNumber++;
                                    if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                        decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                                    } else {
                                        decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                    }
                                }
                                if (decompressor1.clearFlag > 0) {
                                    decompressor1.bitsNumber = Compress.INIT_BITS;
                                    decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                    decompressor1.clearFlag = 0;
                                }
                                int result6;
                                if (decompressor1.input.cnt <= 0) {
                                    result6 = -1;
                                } else {
                                    int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                                    for (int i5 = 0; i5 < num1; i5++) {
                                        decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                        decompressor1.input.cnt--;
                                    }
                                    result6 = num1;
                                }
                                decompressor1.size = result6;
                                if (decompressor1.size <= 0) {
                                    result5 = -1;/* end of file */
                                } else {
                                    decompressor1.offset = 0;/* Round size down to integral number of codes */
                                    decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                                }
                            }
                            if (result5 == 0) {
                                rOff4 = decompressor1.offset;
                                bits4 = decompressor1.bitsNumber;
                                /*
                                 * Get to the first byte.
                                 */
                                bp4 += rOff4 >> 3;
                                rOff4 &= 7;
                                /* Get first part (low order bits) */
                                code7 = ((decompressor1.buf[bp4++] >> rOff4) & Compress.rmask[8 - rOff4]) & 0xff;
                                bits4 -= 8 - rOff4;
                                rOff4 = 8 - rOff4; /* now, offset into code word */
                                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                if (bits4 >= 8) {
                                    code7 |= (decompressor1.buf[bp4++] & 0xff) << rOff4;
                                    rOff4 += 8;
                                    bits4 -= 8;
                                }
                                /* high order bits. */
                                if (bits4 > 0) {
                                    code7 |= (decompressor1.buf[bp4] & Compress.rmask[bits4]) << rOff4;
                                }
                                decompressor1.offset += decompressor1.bitsNumber;
                                result5 = code7;
                            }
                            if (!((code5 = result5) > -1))
                                break;
                            if ((code5 == Compress.CLEAR) && (decompressor1.blockCompress != 0)) {
                                for (int code8 = 0; code8 < 256; code8++) {
                                    decompressor1.tabPrefix.tab[code8] = 0;
                                }
                                decompressor1.clearFlag = 1;
                                decompressor1.freeEntry = Compress.FIRST - 1;
                                int result6 = 0;
                                int code8 = 0;
                                int rOff5 = 0, bits5 = 0;
                                int bp5 = 0;
                                if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                                        || decompressor1.freeEntry > decompressor1.maxCode) {
                                    /*
                                     * If the next entry will be too big for the current code size, then
                                     * we must increase the size. This implies reading a new buffer
                                     * full, too.
                                     */
                                    if (decompressor1.freeEntry > decompressor1.maxCode) {
                                        decompressor1.bitsNumber++;
                                        if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                            decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                                        } else {
                                            decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                        }
                                    }
                                    if (decompressor1.clearFlag > 0) {
                                        decompressor1.bitsNumber = Compress.INIT_BITS;
                                        decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                        decompressor1.clearFlag = 0;
                                    }
                                    int result7;
                                    if (decompressor1.input.cnt <= 0) {
                                        result7 = -1;
                                    } else {
                                        int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                                        for (int i5 = 0; i5 < num1; i5++) {
                                            decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                            decompressor1.input.cnt--;
                                        }
                                        result7 = num1;
                                    }
                                    decompressor1.size = result7;
                                    if (decompressor1.size <= 0) {
                                        result6 = -1;/* end of file */
                                    } else {
                                        decompressor1.offset = 0;/* Round size down to integral number of codes */
                                        decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                                    }
                                }
                                if (result6 == 0) {
                                    rOff5 = decompressor1.offset;
                                    bits5 = decompressor1.bitsNumber;
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp5 += rOff5 >> 3;
                                    rOff5 &= 7;
                                    /* Get first part (low order bits) */
                                    code8 = ((decompressor1.buf[bp5++] >> rOff5) & Compress.rmask[8 - rOff5]) & 0xff;
                                    bits5 -= 8 - rOff5;
                                    rOff5 = 8 - rOff5; /* now, offset into code word */
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits5 >= 8) {
                                        code8 |= (decompressor1.buf[bp5++] & 0xff) << rOff5;
                                        rOff5 += 8;
                                        bits5 -= 8;
                                    }
                                    /* high order bits. */
                                    if (bits5 > 0) {
                                        code8 |= (decompressor1.buf[bp5] & Compress.rmask[bits5]) << rOff5;
                                    }
                                    decompressor1.offset += decompressor1.bitsNumber;
                                    result6 = code8;
                                }
                                if ((code5 = result6) == -1) /* O, untimely death! */
                                    break;
                            }
                            incode1 = code5;
                            /*
                             * Special case for KwKwK string.
                             */
                            if (code5 >= decompressor1.freeEntry) {
                                decompressor1.deStack.tab[decompressor1.deStack.index++] = (byte) finchar1;
                                code5 = oldcode1;
                            }
                            /*
                             * Generate output characters in reverse order
                             */
                            while (code5 >= 256) {
                                decompressor1.deStack.tab[decompressor1.deStack.index++] = decompressor1.tabSuffix.tab[code5];
                                code5 = (int) decompressor1.tabPrefix.tab[code5] << 16 >>> 16;
                            }
                            byte c3 = (byte) (finchar1 = decompressor1.tabSuffix.tab[code5]);
                            decompressor1.deStack.tab[decompressor1.deStack.index++] = c3;
                            /*
                             * And put them out in forward order
                             */
                            do {
                                byte c4 = decompressor1.deStack.tab[--decompressor1.deStack.index];
                                decompressor1.output.buffer[decompressor1.output.cnt++] = c4;
                            } while (!(decompressor1.deStack.index == 0));
                            /*
                             * Generate the new entry.
                             */
                            if ((code5 = decompressor1.freeEntry) < decompressor1.maxMaxCode) {
                                decompressor1.tabPrefix.tab[code5] = (short) oldcode1;
                                decompressor1.tabSuffix.tab[code5] = (byte) finchar1;
                                decompressor1.freeEntry = code5 + 1;
                            }
                            /*
                             * Remember previous code.
                             */
                            oldcode1 = incode1;
                        }
                    }
                }
                comprBuffer = out1;
                InputBuffer in = new InputBuffer(comprBuffer.cnt, COMPRESS_BUFFERS[btid - 1]);
                OutputBuffer out = new OutputBuffer(DECOMPRESS_BUFFERS[btid - 1]);
                if (CB.UNCOMPRESS == Compress.COMPRESS) {
                    Compressor compressor = new Compressor(in, out);
                    int fcode;
                    int i4 = 0;
                    int c;
                    int disp;
                    int hshift = 0;
                    int ent = compressor.input.cnt-- > 0 ? (compressor.input.buffer[compressor.input.current++] & 0x00FF) : -1;
                    for (fcode = compressor.htab.size; fcode < 65536; fcode *= 2) {
                        hshift++;
                    }
                    hshift = 8 - hshift; /* set hash code range bound */
                    int hsizeReg = compressor.htab.size;
                    /* clear hash table */
                    for (int i3 = 0; i3 < compressor.htab.size; i3++) {
                        compressor.htab.tab[i3] = -1;
                    }
                    next_byte: while ((c = compressor.input.cnt-- > 0
                            ? (compressor.input.buffer[compressor.input.current++] & 0x00FF)
                            : -1) != -1) {
                        compressor.inCount++;
                        fcode = (((int) c << compressor.maxBits) + ent);
                        i4 = ((c << hshift) ^ ent); /* xor hashing */
                        int temphtab = compressor.htab.tab[i4];
                        if (temphtab == fcode) {
                            ent = (int) compressor.codetab.tab[i4] << 16 >>> 16;
                            continue next_byte;
                        }
                        if (temphtab >= 0) { /* non-empty slot dm kmd 4/15 */
                            disp = hsizeReg - i4; /* secondary hash (after G. Knott) */
                            if (i4 == 0) {
                                disp = 1;
                            }
                            do {
                                if ((i4 -= disp) < 0) {
                                    i4 += hsizeReg;
                                }
                                temphtab = compressor.htab.tab[i4];
                                if (temphtab == fcode) {
                                    ent = (int) compressor.codetab.tab[i4] << 16 >>> 16;
                                    continue next_byte;
                                }
                            } while (temphtab > 0);
                        }
                        int code = ent;
                        int rOff = compressor.offset, bits = compressor.bitsNumber;
                        int bp = 0;
                        if (code >= 0) {
                            /*
                             * Get to the first byte.
                             */
                            bp += rOff >> 3;
                            rOff &= 7;
                            /*
                             * Since code is always >= 8 bits, only need to mask the first hunk
                             * on the left.
                             */
                            compressor.buf[bp] = (byte) ((compressor.buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                                    & Compress.lmask[rOff]);
                            bp++;
                            bits -= 8 - rOff;
                            code >>= 8 - rOff;
                            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                            if (bits >= 8) {
                                compressor.buf[bp++] = (byte) code;
                                code >>= 8;
                                bits -= 8;
                            }
                            /* Last bits. */
                            if (bits != 0) {
                                compressor.buf[bp] = (byte) code;
                            }
                            compressor.offset += compressor.bitsNumber;
                            if (compressor.offset == (compressor.bitsNumber << 3)) {
                                bp = 0;
                                bits = compressor.bitsNumber;
                                compressor.bytesOut += bits;
                                do {
                                    byte c1 = compressor.buf[bp++];
                                    compressor.output.buffer[compressor.output.cnt++] = c1;
                                } while (--bits != 0);
                                compressor.offset = 0;
                            }
                            /*
                             * If the next entry is going to be too big for the code size, then
                             * increase it, if possible.
                             */
                            if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                                /*
                                 * Write the whole buffer, because the input side won't discover
                                 * the size increase until after it has read it.
                                 */
                                if (compressor.offset > 0) {
                                    for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                        compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                    }
                                    compressor.bytesOut += compressor.bitsNumber;
                                }
                                compressor.offset = 0;
                                if (compressor.clearFlag != 0) {
                                    compressor.bitsNumber = Compress.INIT_BITS;
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                    compressor.clearFlag = 0;
                                } else {
                                    compressor.bitsNumber++;
                                    if (compressor.bitsNumber == compressor.maxBits) {
                                        compressor.maxCode = compressor.maxMaxCode;
                                    } else {
                                        compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                    }
                                }
                            }
                        } else {
                            /*
                             * At EOF, write the rest of the buffer.
                             */
                            if (compressor.offset > 0) {
                                for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                }
                            }
                            compressor.bytesOut += (compressor.offset + 7) / 8;
                            compressor.offset = 0;
                        }
                        compressor.outCount++;
                        ent = c;
                        if (compressor.freeEntry < compressor.maxMaxCode) {
                            /* code -> hashtable */
                            int v = compressor.freeEntry++;
                            compressor.codetab.tab[i4] = (short) v;
                            compressor.htab.tab[i4] = fcode;
                        } else if (compressor.inCount >= compressor.checkpoint && compressor.blockCompress != 0) {
                            int rat;
                            compressor.checkpoint = compressor.inCount + Compressor.CHECK_GAP;
                            if (compressor.inCount > 0x007fffff) { /* shift will overflow */
                                rat = compressor.bytesOut >> 8;
                                if (rat == 0) { /* Don't divide by zero */
                                    rat = 0x7fffffff;
                                } else {
                                    rat = compressor.inCount / rat;
                                }
                            } else {
                                rat = (compressor.inCount << 8) / compressor.bytesOut; /* 8 fractional bits */
                            }
                            if (rat > compressor.ratio) {
                                compressor.ratio = rat;
                            } else {
                                compressor.ratio = 0;
                                for (int i2 = 0; i2 < compressor.htab.size; i2++) {
                                    compressor.htab.tab[i2] = -1;
                                }
                                compressor.freeEntry = Compress.FIRST;
                                compressor.clearFlag = 1;
                                int code1 = (int) Compress.CLEAR;
                                int rOff1 = compressor.offset, bits1 = compressor.bitsNumber;
                                int bp1 = 0;
                                if (code1 >= 0) {
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp1 += rOff1 >> 3;
                                    rOff1 &= 7;
                                    /*
                                     * Since code is always >= 8 bits, only need to mask the first hunk
                                     * on the left.
                                     */
                                    compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                                            & Compress.lmask[rOff1]);
                                    bp1++;
                                    bits1 -= 8 - rOff1;
                                    code1 >>= 8 - rOff1;
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits1 >= 8) {
                                        compressor.buf[bp1++] = (byte) code1;
                                        code1 >>= 8;
                                        bits1 -= 8;
                                    }
                                    /* Last bits. */
                                    if (bits1 != 0) {
                                        compressor.buf[bp1] = (byte) code1;
                                    }
                                    compressor.offset += compressor.bitsNumber;
                                    if (compressor.offset == (compressor.bitsNumber << 3)) {
                                        bp1 = 0;
                                        bits1 = compressor.bitsNumber;
                                        compressor.bytesOut += bits1;
                                        do {
                                            byte c1 = compressor.buf[bp1++];
                                            compressor.output.buffer[compressor.output.cnt++] = c1;
                                        } while (--bits1 != 0);
                                        compressor.offset = 0;
                                    }
                                    /*
                                     * If the next entry is going to be too big for the code size, then
                                     * increase it, if possible.
                                     */
                                    if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                                        /*
                                         * Write the whole buffer, because the input side won't discover
                                         * the size increase until after it has read it.
                                         */
                                        if (compressor.offset > 0) {
                                            for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                            }
                                            compressor.bytesOut += compressor.bitsNumber;
                                        }
                                        compressor.offset = 0;
                                        if (compressor.clearFlag != 0) {
                                            compressor.bitsNumber = Compress.INIT_BITS;
                                            compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                            compressor.clearFlag = 0;
                                        } else {
                                            compressor.bitsNumber++;
                                            if (compressor.bitsNumber == compressor.maxBits) {
                                                compressor.maxCode = compressor.maxMaxCode;
                                            } else {
                                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                            }
                                        }
                                    }
                                } else {
                                    /*
                                     * At EOF, write the rest of the buffer.
                                     */
                                    if (compressor.offset > 0) {
                                        for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                            compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                        }
                                    }
                                    compressor.bytesOut += (compressor.offset + 7) / 8;
                                    compressor.offset = 0;
                                }
                            }
                        }
                    }
                    /*
                     * Put out the final code.
                     */
                    int code1 = ent;
                    int rOff1 = compressor.offset, bits1 = compressor.bitsNumber;
                    int bp1 = 0;
                    if (code1 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp1 += rOff1 >> 3;
                        rOff1 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                                & Compress.lmask[rOff1]);
                        bp1++;
                        bits1 -= 8 - rOff1;
                        code1 >>= 8 - rOff1;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits1 >= 8) {
                            compressor.buf[bp1++] = (byte) code1;
                            code1 >>= 8;
                            bits1 -= 8;
                        }
                        /* Last bits. */
                        if (bits1 != 0) {
                            compressor.buf[bp1] = (byte) code1;
                        }
                        compressor.offset += compressor.bitsNumber;
                        if (compressor.offset == (compressor.bitsNumber << 3)) {
                            bp1 = 0;
                            bits1 = compressor.bitsNumber;
                            compressor.bytesOut += bits1;
                            do {
                                byte c2 = compressor.buf[bp1++];
                                compressor.output.buffer[compressor.output.cnt++] = c2;
                            } while (--bits1 != 0);
                            compressor.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor.offset > 0) {
                                for (int i2 = 0; i2 < compressor.bitsNumber; i2++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i2];
                                }
                                compressor.bytesOut += compressor.bitsNumber;
                            }
                            compressor.offset = 0;
                            if (compressor.clearFlag != 0) {
                                compressor.bitsNumber = Compress.INIT_BITS;
                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                compressor.clearFlag = 0;
                            } else {
                                compressor.bitsNumber++;
                                if (compressor.bitsNumber == compressor.maxBits) {
                                    compressor.maxCode = compressor.maxMaxCode;
                                } else {
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor.offset > 0) {
                            for (int i2 = 0; i2 < ((compressor.offset + 7) / 8); i2++) {
                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i2];
                            }
                        }
                        compressor.bytesOut += (compressor.offset + 7) / 8;
                        compressor.offset = 0;
                    }
                    compressor.outCount++;
                    int code = -1;
                    int rOff = compressor.offset, bits = compressor.bitsNumber;
                    int bp = 0;
                    if (code >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp += rOff >> 3;
                        rOff &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor.buf[bp] = (byte) ((compressor.buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                                & Compress.lmask[rOff]);
                        bp++;
                        bits -= 8 - rOff;
                        code >>= 8 - rOff;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits >= 8) {
                            compressor.buf[bp++] = (byte) code;
                            code >>= 8;
                            bits -= 8;
                        }
                        /* Last bits. */
                        if (bits != 0) {
                            compressor.buf[bp] = (byte) code;
                        }
                        compressor.offset += compressor.bitsNumber;
                        if (compressor.offset == (compressor.bitsNumber << 3)) {
                            bp = 0;
                            bits = compressor.bitsNumber;
                            compressor.bytesOut += bits;
                            do {
                                byte c1 = compressor.buf[bp++];
                                compressor.output.buffer[compressor.output.cnt++] = c1;
                            } while (--bits != 0);
                            compressor.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor.offset > 0) {
                                for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                }
                                compressor.bytesOut += compressor.bitsNumber;
                            }
                            compressor.offset = 0;
                            if (compressor.clearFlag != 0) {
                                compressor.bitsNumber = Compress.INIT_BITS;
                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                compressor.clearFlag = 0;
                            } else {
                                compressor.bitsNumber++;
                                if (compressor.bitsNumber == compressor.maxBits) {
                                    compressor.maxCode = compressor.maxMaxCode;
                                } else {
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor.offset > 0) {
                            for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                            }
                        }
                        compressor.bytesOut += (compressor.offset + 7) / 8;
                        compressor.offset = 0;
                    }
                } else {
                    Decompressor decompressor = new Decompressor(in, out);
                    int code, oldcode, incode;
                    int result2 = 0;
                    int code3 = 0;
                    int rOff2 = 0, bits2 = 0;
                    int bp2 = 0;
                    if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                            || decompressor.freeEntry > decompressor.maxCode) {
                        /*
                         * If the next entry will be too big for the current code size, then
                         * we must increase the size. This implies reading a new buffer
                         * full, too.
                         */
                        if (decompressor.freeEntry > decompressor.maxCode) {
                            decompressor.bitsNumber++;
                            if (decompressor.bitsNumber == decompressor.maxBits) {
                                decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                            } else {
                                decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                            }
                        }
                        if (decompressor.clearFlag > 0) {
                            decompressor.bitsNumber = Compress.INIT_BITS;
                            decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                            decompressor.clearFlag = 0;
                        }
                        int result;
                        if (decompressor.input.cnt <= 0) {
                            result = -1;
                        } else {
                            int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                            for (int i1 = 0; i1 < num; i1++) {
                                decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                decompressor.input.cnt--;
                            }
                            result = num;
                        }
                        decompressor.size = result;
                        if (decompressor.size <= 0) {
                            result2 = -1;/* end of file */
                        } else {
                            decompressor.offset = 0;/* Round size down to integral number of codes */
                            decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                        }
                    }
                    if (result2 == 0) {
                        rOff2 = decompressor.offset;
                        bits2 = decompressor.bitsNumber;
                        /*
                         * Get to the first byte.
                         */
                        bp2 += rOff2 >> 3;
                        rOff2 &= 7;
                        /* Get first part (low order bits) */
                        code3 = ((decompressor.buf[bp2++] >> rOff2) & Compress.rmask[8 - rOff2]) & 0xff;
                        bits2 -= 8 - rOff2;
                        rOff2 = 8 - rOff2; /* now, offset into code word */
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits2 >= 8) {
                            code3 |= (decompressor.buf[bp2++] & 0xff) << rOff2;
                            rOff2 += 8;
                            bits2 -= 8;
                        }
                        /* high order bits. */
                        if (bits2 > 0) {
                            code3 |= (decompressor.buf[bp2] & Compress.rmask[bits2]) << rOff2;
                        }
                        decompressor.offset += decompressor.bitsNumber;
                        result2 = code3;
                    }
                    int finchar = oldcode = result2;
                    /* EOF already? */
                    /* Get out of here */
                    if (oldcode != -1) {/* first code must be 8 bits = byte */
                        decompressor.output.buffer[decompressor.output.cnt++] = (byte) finchar;
                        while (true) {
                            int result1 = 0;
                            int code2 = 0;
                            int rOff1 = 0, bits1 = 0;
                            int bp1 = 0;
                            if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                                    || decompressor.freeEntry > decompressor.maxCode) {
                                /*
                                 * If the next entry will be too big for the current code size, then
                                 * we must increase the size. This implies reading a new buffer
                                 * full, too.
                                 */
                                if (decompressor.freeEntry > decompressor.maxCode) {
                                    decompressor.bitsNumber++;
                                    if (decompressor.bitsNumber == decompressor.maxBits) {
                                        decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                                    } else {
                                        decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                    }
                                }
                                if (decompressor.clearFlag > 0) {
                                    decompressor.bitsNumber = Compress.INIT_BITS;
                                    decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                    decompressor.clearFlag = 0;
                                }
                                int result;
                                if (decompressor.input.cnt <= 0) {
                                    result = -1;
                                } else {
                                    int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                                    for (int i1 = 0; i1 < num; i1++) {
                                        decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                        decompressor.input.cnt--;
                                    }
                                    result = num;
                                }
                                decompressor.size = result;
                                if (decompressor.size <= 0) {
                                    result1 = -1;/* end of file */
                                } else {
                                    decompressor.offset = 0;/* Round size down to integral number of codes */
                                    decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                                }
                            }
                            if (result1 == 0) {
                                rOff1 = decompressor.offset;
                                bits1 = decompressor.bitsNumber;
                                /*
                                 * Get to the first byte.
                                 */
                                bp1 += rOff1 >> 3;
                                rOff1 &= 7;
                                /* Get first part (low order bits) */
                                code2 = ((decompressor.buf[bp1++] >> rOff1) & Compress.rmask[8 - rOff1]) & 0xff;
                                bits1 -= 8 - rOff1;
                                rOff1 = 8 - rOff1; /* now, offset into code word */
                                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                if (bits1 >= 8) {
                                    code2 |= (decompressor.buf[bp1++] & 0xff) << rOff1;
                                    rOff1 += 8;
                                    bits1 -= 8;
                                }
                                /* high order bits. */
                                if (bits1 > 0) {
                                    code2 |= (decompressor.buf[bp1] & Compress.rmask[bits1]) << rOff1;
                                }
                                decompressor.offset += decompressor.bitsNumber;
                                result1 = code2;
                            }
                            if (!((code = result1) > -1))
                                break;
                            if ((code == Compress.CLEAR) && (decompressor.blockCompress != 0)) {
                                for (int code4 = 0; code4 < 256; code4++) {
                                    decompressor.tabPrefix.tab[code4] = 0;
                                }
                                decompressor.clearFlag = 1;
                                decompressor.freeEntry = Compress.FIRST - 1;
                                int result = 0;
                                int code1 = 0;
                                int rOff = 0, bits = 0;
                                int bp = 0;
                                if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                                        || decompressor.freeEntry > decompressor.maxCode) {
                                    /*
                                     * If the next entry will be too big for the current code size, then
                                     * we must increase the size. This implies reading a new buffer
                                     * full, too.
                                     */
                                    if (decompressor.freeEntry > decompressor.maxCode) {
                                        decompressor.bitsNumber++;
                                        if (decompressor.bitsNumber == decompressor.maxBits) {
                                            decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                                        } else {
                                            decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                        }
                                    }
                                    if (decompressor.clearFlag > 0) {
                                        decompressor.bitsNumber = Compress.INIT_BITS;
                                        decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                        decompressor.clearFlag = 0;
                                    }
                                    int result3;
                                    if (decompressor.input.cnt <= 0) {
                                        result3 = -1;
                                    } else {
                                        int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                                        for (int i1 = 0; i1 < num; i1++) {
                                            decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                            decompressor.input.cnt--;
                                        }
                                        result3 = num;
                                    }
                                    decompressor.size = result3;
                                    if (decompressor.size <= 0) {
                                        result = -1;/* end of file */
                                    } else {
                                        decompressor.offset = 0;/* Round size down to integral number of codes */
                                        decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                                    }
                                }
                                if (result == 0) {
                                    rOff = decompressor.offset;
                                    bits = decompressor.bitsNumber;
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp += rOff >> 3;
                                    rOff &= 7;
                                    /* Get first part (low order bits) */
                                    code1 = ((decompressor.buf[bp++] >> rOff) & Compress.rmask[8 - rOff]) & 0xff;
                                    bits -= 8 - rOff;
                                    rOff = 8 - rOff; /* now, offset into code word */
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits >= 8) {
                                        code1 |= (decompressor.buf[bp++] & 0xff) << rOff;
                                        rOff += 8;
                                        bits -= 8;
                                    }
                                    /* high order bits. */
                                    if (bits > 0) {
                                        code1 |= (decompressor.buf[bp] & Compress.rmask[bits]) << rOff;
                                    }
                                    decompressor.offset += decompressor.bitsNumber;
                                    result = code1;
                                }
                                if ((code = result) == -1) /* O, untimely death! */
                                    break;
                            }
                            incode = code;
                            /*
                             * Special case for KwKwK string.
                             */
                            if (code >= decompressor.freeEntry) {
                                decompressor.deStack.tab[decompressor.deStack.index++] = (byte) finchar;
                                code = oldcode;
                            }
                            /*
                             * Generate output characters in reverse order
                             */
                            while (code >= 256) {
                                decompressor.deStack.tab[decompressor.deStack.index++] = decompressor.tabSuffix.tab[code];
                                code = (int) decompressor.tabPrefix.tab[code] << 16 >>> 16;
                            }
                            byte c1 = (byte) (finchar = decompressor.tabSuffix.tab[code]);
                            decompressor.deStack.tab[decompressor.deStack.index++] = c1;
                            /*
                             * And put them out in forward order
                             */
                            do {
                                byte c = decompressor.deStack.tab[--decompressor.deStack.index];
                                decompressor.output.buffer[decompressor.output.cnt++] = c;
                            } while (!(decompressor.deStack.index == 0));
                            /*
                             * Generate the new entry.
                             */
                            if ((code = decompressor.freeEntry) < decompressor.maxMaxCode) {
                                decompressor.tabPrefix.tab[code] = (short) oldcode;
                                decompressor.tabSuffix.tab[code] = (byte) finchar;
                                decompressor.freeEntry = code + 1;
                            }
                            /*
                             * Remember previous code.
                             */
                            oldcode = incode;
                        }
                    }
                }
                decomprBufer = out;
                System.out.print(source.length + " " + source.crc + " ");
                CRC32 crc33 = new CRC32();
                crc33.update(comprBuffer.buffer, 0, comprBuffer.cnt);
                System.out.print(comprBuffer.cnt + crc33.getValue() + " ");
                CRC32 crc32 = new CRC32();
                crc32.update(decomprBufer.buffer, 0, decomprBufer.cnt);
                System.out.println(decomprBufer.cnt + " " + crc32.getValue());
            }
        }
    }

    public long inst_main(int btid) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < LOOP_COUNT; i++) {
            for (int j = 0; j < FILES_NUMBER; j++) {
                Source source = SOURCES[j];
                OutputBuffer comprBuffer, decomprBufer;
                InputBuffer in1 = new InputBuffer(source.length, source.buffer);
                OutputBuffer out1 = new OutputBuffer(COMPRESS_BUFFERS[btid - 1]);
                if (CB.COMPRESS == Compress.COMPRESS) {
                    Compressor compressor1 = new Compressor(in1, out1);
                    int fcode1;
                    int i5 = 0;
                    int c3;
                    int disp1;
                    int hshift1 = 0;
                    int ent1 = compressor1.input.cnt-- > 0 ? (compressor1.input.buffer[compressor1.input.current++] & 0x00FF) : -1;
                    for (fcode1 = compressor1.htab.size; fcode1 < 65536; fcode1 *= 2) {
                        hshift1++;
                    }
                    hshift1 = 8 - hshift1; /* set hash code range bound */
                    int hsizeReg1 = compressor1.htab.size;
                    /* clear hash table */
                    for (int i6 = 0; i6 < compressor1.htab.size; i6++) {
                        compressor1.htab.tab[i6] = -1;
                    }
                    next_byte: while ((c3 = compressor1.input.cnt-- > 0
                            ? (compressor1.input.buffer[compressor1.input.current++] & 0x00FF)
                            : -1) != -1) {
                        compressor1.inCount++;
                        fcode1 = (((int) c3 << compressor1.maxBits) + ent1);
                        i5 = ((c3 << hshift1) ^ ent1); /* xor hashing */
                        int temphtab1 = compressor1.htab.tab[i5];
                        if (temphtab1 == fcode1) {
                            ent1 = (int) compressor1.codetab.tab[i5] << 16 >>> 16;
                            continue next_byte;
                        }
                        if (temphtab1 >= 0) { /* non-empty slot dm kmd 4/15 */
                            disp1 = hsizeReg1 - i5; /* secondary hash (after G. Knott) */
                            if (i5 == 0) {
                                disp1 = 1;
                            }
                            do {
                                if ((i5 -= disp1) < 0) {
                                    i5 += hsizeReg1;
                                }
                                temphtab1 = compressor1.htab.tab[i5];
                                if (temphtab1 == fcode1) {
                                    ent1 = (int) compressor1.codetab.tab[i5] << 16 >>> 16;
                                    continue next_byte;
                                }
                            } while (temphtab1 > 0);
                        }
                        int code5 = ent1;
                        int rOff3 = compressor1.offset, bits3 = compressor1.bitsNumber;
                        int bp3 = 0;
                        if (code5 >= 0) {
                            /*
                             * Get to the first byte.
                             */
                            bp3 += rOff3 >> 3;
                            rOff3 &= 7;
                            /*
                             * Since code is always >= 8 bits, only need to mask the first hunk
                             * on the left.
                             */
                            compressor1.buf[bp3] = (byte) ((compressor1.buf[bp3] & Compress.rmask[rOff3]) | (code5 << rOff3)
                                    & Compress.lmask[rOff3]);
                            bp3++;
                            bits3 -= 8 - rOff3;
                            code5 >>= 8 - rOff3;
                            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                            if (bits3 >= 8) {
                                compressor1.buf[bp3++] = (byte) code5;
                                code5 >>= 8;
                                bits3 -= 8;
                            }
                            /* Last bits. */
                            if (bits3 != 0) {
                                compressor1.buf[bp3] = (byte) code5;
                            }
                            compressor1.offset += compressor1.bitsNumber;
                            if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                                bp3 = 0;
                                bits3 = compressor1.bitsNumber;
                                compressor1.bytesOut += bits3;
                                do {
                                    byte c4 = compressor1.buf[bp3++];
                                    compressor1.output.buffer[compressor1.output.cnt++] = c4;
                                } while (--bits3 != 0);
                                compressor1.offset = 0;
                            }
                            /*
                             * If the next entry is going to be too big for the code size, then
                             * increase it, if possible.
                             */
                            if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                                /*
                                 * Write the whole buffer, because the input side won't discover
                                 * the size increase until after it has read it.
                                 */
                                if (compressor1.offset > 0) {
                                    for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                        compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                    }
                                    compressor1.bytesOut += compressor1.bitsNumber;
                                }
                                compressor1.offset = 0;
                                if (compressor1.clearFlag != 0) {
                                    compressor1.bitsNumber = Compress.INIT_BITS;
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                    compressor1.clearFlag = 0;
                                } else {
                                    compressor1.bitsNumber++;
                                    if (compressor1.bitsNumber == compressor1.maxBits) {
                                        compressor1.maxCode = compressor1.maxMaxCode;
                                    } else {
                                        compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                    }
                                }
                            }
                        } else {
                            /*
                             * At EOF, write the rest of the buffer.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                            }
                            compressor1.bytesOut += (compressor1.offset + 7) / 8;
                            compressor1.offset = 0;
                        }
                        compressor1.outCount++;
                        ent1 = c3;
                        if (compressor1.freeEntry < compressor1.maxMaxCode) {
                            /* code -> hashtable */
                            int v1 = compressor1.freeEntry++;
                            compressor1.codetab.tab[i5] = (short) v1;
                            compressor1.htab.tab[i5] = fcode1;
                        } else if (compressor1.inCount >= compressor1.checkpoint && compressor1.blockCompress != 0) {
                            int rat1;
                            compressor1.checkpoint = compressor1.inCount + Compressor.CHECK_GAP;
                            if (compressor1.inCount > 0x007fffff) { /* shift will overflow */
                                rat1 = compressor1.bytesOut >> 8;
                                if (rat1 == 0) { /* Don't divide by zero */
                                    rat1 = 0x7fffffff;
                                } else {
                                    rat1 = compressor1.inCount / rat1;
                                }
                            } else {
                                rat1 = (compressor1.inCount << 8) / compressor1.bytesOut; /* 8 fractional bits */
                            }
                            if (rat1 > compressor1.ratio) {
                                compressor1.ratio = rat1;
                            } else {
                                compressor1.ratio = 0;
                                for (int i6 = 0; i6 < compressor1.htab.size; i6++) {
                                    compressor1.htab.tab[i6] = -1;
                                }
                                compressor1.freeEntry = Compress.FIRST;
                                compressor1.clearFlag = 1;
                                int code6 = (int) Compress.CLEAR;
                                int rOff4 = compressor1.offset, bits4 = compressor1.bitsNumber;
                                int bp4 = 0;
                                if (code6 >= 0) {
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp4 += rOff4 >> 3;
                                    rOff4 &= 7;
                                    /*
                                     * Since code is always >= 8 bits, only need to mask the first hunk
                                     * on the left.
                                     */
                                    compressor1.buf[bp4] = (byte) ((compressor1.buf[bp4] & Compress.rmask[rOff4]) | (code6 << rOff4)
                                            & Compress.lmask[rOff4]);
                                    bp4++;
                                    bits4 -= 8 - rOff4;
                                    code6 >>= 8 - rOff4;
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits4 >= 8) {
                                        compressor1.buf[bp4++] = (byte) code6;
                                        code6 >>= 8;
                                        bits4 -= 8;
                                    }
                                    /* Last bits. */
                                    if (bits4 != 0) {
                                        compressor1.buf[bp4] = (byte) code6;
                                    }
                                    compressor1.offset += compressor1.bitsNumber;
                                    if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                                        bp4 = 0;
                                        bits4 = compressor1.bitsNumber;
                                        compressor1.bytesOut += bits4;
                                        do {
                                            byte c4 = compressor1.buf[bp4++];
                                            compressor1.output.buffer[compressor1.output.cnt++] = c4;
                                        } while (--bits4 != 0);
                                        compressor1.offset = 0;
                                    }
                                    /*
                                     * If the next entry is going to be too big for the code size, then
                                     * increase it, if possible.
                                     */
                                    if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                                        /*
                                         * Write the whole buffer, because the input side won't discover
                                         * the size increase until after it has read it.
                                         */
                                        if (compressor1.offset > 0) {
                                            for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                            }
                                            compressor1.bytesOut += compressor1.bitsNumber;
                                        }
                                        compressor1.offset = 0;
                                        if (compressor1.clearFlag != 0) {
                                            compressor1.bitsNumber = Compress.INIT_BITS;
                                            compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                            compressor1.clearFlag = 0;
                                        } else {
                                            compressor1.bitsNumber++;
                                            if (compressor1.bitsNumber == compressor1.maxBits) {
                                                compressor1.maxCode = compressor1.maxMaxCode;
                                            } else {
                                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                            }
                                        }
                                    }
                                } else {
                                    /*
                                     * At EOF, write the rest of the buffer.
                                     */
                                    if (compressor1.offset > 0) {
                                        for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                            compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                        }
                                    }
                                    compressor1.bytesOut += (compressor1.offset + 7) / 8;
                                    compressor1.offset = 0;
                                }
                            }
                        }
                    }
                    /*
                     * Put out the final code.
                     */
                    int code5 = ent1;
                    int rOff3 = compressor1.offset, bits3 = compressor1.bitsNumber;
                    int bp3 = 0;
                    if (code5 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp3 += rOff3 >> 3;
                        rOff3 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor1.buf[bp3] = (byte) ((compressor1.buf[bp3] & Compress.rmask[rOff3]) | (code5 << rOff3)
                                & Compress.lmask[rOff3]);
                        bp3++;
                        bits3 -= 8 - rOff3;
                        code5 >>= 8 - rOff3;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits3 >= 8) {
                            compressor1.buf[bp3++] = (byte) code5;
                            code5 >>= 8;
                            bits3 -= 8;
                        }
                        /* Last bits. */
                        if (bits3 != 0) {
                            compressor1.buf[bp3] = (byte) code5;
                        }
                        compressor1.offset += compressor1.bitsNumber;
                        if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                            bp3 = 0;
                            bits3 = compressor1.bitsNumber;
                            compressor1.bytesOut += bits3;
                            do {
                                byte c4 = compressor1.buf[bp3++];
                                compressor1.output.buffer[compressor1.output.cnt++] = c4;
                            } while (--bits3 != 0);
                            compressor1.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                                compressor1.bytesOut += compressor1.bitsNumber;
                            }
                            compressor1.offset = 0;
                            if (compressor1.clearFlag != 0) {
                                compressor1.bitsNumber = Compress.INIT_BITS;
                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                compressor1.clearFlag = 0;
                            } else {
                                compressor1.bitsNumber++;
                                if (compressor1.bitsNumber == compressor1.maxBits) {
                                    compressor1.maxCode = compressor1.maxMaxCode;
                                } else {
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor1.offset > 0) {
                            for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                            }
                        }
                        compressor1.bytesOut += (compressor1.offset + 7) / 8;
                        compressor1.offset = 0;
                    }
                    compressor1.outCount++;
                    int code6 = -1;
                    int rOff4 = compressor1.offset, bits4 = compressor1.bitsNumber;
                    int bp4 = 0;
                    if (code6 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp4 += rOff4 >> 3;
                        rOff4 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor1.buf[bp4] = (byte) ((compressor1.buf[bp4] & Compress.rmask[rOff4]) | (code6 << rOff4)
                                & Compress.lmask[rOff4]);
                        bp4++;
                        bits4 -= 8 - rOff4;
                        code6 >>= 8 - rOff4;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits4 >= 8) {
                            compressor1.buf[bp4++] = (byte) code6;
                            code6 >>= 8;
                            bits4 -= 8;
                        }
                        /* Last bits. */
                        if (bits4 != 0) {
                            compressor1.buf[bp4] = (byte) code6;
                        }
                        compressor1.offset += compressor1.bitsNumber;
                        if (compressor1.offset == (compressor1.bitsNumber << 3)) {
                            bp4 = 0;
                            bits4 = compressor1.bitsNumber;
                            compressor1.bytesOut += bits4;
                            do {
                                byte c4 = compressor1.buf[bp4++];
                                compressor1.output.buffer[compressor1.output.cnt++] = c4;
                            } while (--bits4 != 0);
                            compressor1.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor1.freeEntry > compressor1.maxCode || compressor1.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor1.offset > 0) {
                                for (int i6 = 0; i6 < compressor1.bitsNumber; i6++) {
                                    compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                                }
                                compressor1.bytesOut += compressor1.bitsNumber;
                            }
                            compressor1.offset = 0;
                            if (compressor1.clearFlag != 0) {
                                compressor1.bitsNumber = Compress.INIT_BITS;
                                compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                compressor1.clearFlag = 0;
                            } else {
                                compressor1.bitsNumber++;
                                if (compressor1.bitsNumber == compressor1.maxBits) {
                                    compressor1.maxCode = compressor1.maxMaxCode;
                                } else {
                                    compressor1.maxCode = ((1 << (compressor1.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor1.offset > 0) {
                            for (int i6 = 0; i6 < ((compressor1.offset + 7) / 8); i6++) {
                                compressor1.output.buffer[compressor1.output.cnt++] = compressor1.buf[i6];
                            }
                        }
                        compressor1.bytesOut += (compressor1.offset + 7) / 8;
                        compressor1.offset = 0;
                    }
                } else {
                    Decompressor decompressor1 = new Decompressor(in1, out1);
                    int code5, oldcode1, incode1;
                    int result4 = 0;
                    int code6 = 0;
                    int rOff3 = 0, bits3 = 0;
                    int bp3 = 0;
                    if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                            || decompressor1.freeEntry > decompressor1.maxCode) {
                        /*
                         * If the next entry will be too big for the current code size, then
                         * we must increase the size. This implies reading a new buffer
                         * full, too.
                         */
                        if (decompressor1.freeEntry > decompressor1.maxCode) {
                            decompressor1.bitsNumber++;
                            if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                            } else {
                                decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                            }
                        }
                        if (decompressor1.clearFlag > 0) {
                            decompressor1.bitsNumber = Compress.INIT_BITS;
                            decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                            decompressor1.clearFlag = 0;
                        }
                        int result5;
                        if (decompressor1.input.cnt <= 0) {
                            result5 = -1;
                        } else {
                            int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                            for (int i5 = 0; i5 < num1; i5++) {
                                decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                decompressor1.input.cnt--;
                            }
                            result5 = num1;
                        }
                        decompressor1.size = result5;
                        if (decompressor1.size <= 0) {
                            result4 = -1;/* end of file */
                        } else {
                            decompressor1.offset = 0;/* Round size down to integral number of codes */
                            decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                        }
                    }
                    if (result4 == 0) {
                        rOff3 = decompressor1.offset;
                        bits3 = decompressor1.bitsNumber;
                        /*
                         * Get to the first byte.
                         */
                        bp3 += rOff3 >> 3;
                        rOff3 &= 7;
                        /* Get first part (low order bits) */
                        code6 = ((decompressor1.buf[bp3++] >> rOff3) & Compress.rmask[8 - rOff3]) & 0xff;
                        bits3 -= 8 - rOff3;
                        rOff3 = 8 - rOff3; /* now, offset into code word */
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits3 >= 8) {
                            code6 |= (decompressor1.buf[bp3++] & 0xff) << rOff3;
                            rOff3 += 8;
                            bits3 -= 8;
                        }
                        /* high order bits. */
                        if (bits3 > 0) {
                            code6 |= (decompressor1.buf[bp3] & Compress.rmask[bits3]) << rOff3;
                        }
                        decompressor1.offset += decompressor1.bitsNumber;
                        result4 = code6;
                    }
                    int finchar1 = oldcode1 = result4;
                    /* EOF already? */
                    /* Get out of here */
                    if (oldcode1 != -1) {/* first code must be 8 bits = byte */
                        decompressor1.output.buffer[decompressor1.output.cnt++] = (byte) finchar1;
                        while (true) {
                            int result5 = 0;
                            int code7 = 0;
                            int rOff4 = 0, bits4 = 0;
                            int bp4 = 0;
                            if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                                    || decompressor1.freeEntry > decompressor1.maxCode) {
                                /*
                                 * If the next entry will be too big for the current code size, then
                                 * we must increase the size. This implies reading a new buffer
                                 * full, too.
                                 */
                                if (decompressor1.freeEntry > decompressor1.maxCode) {
                                    decompressor1.bitsNumber++;
                                    if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                        decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                                    } else {
                                        decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                    }
                                }
                                if (decompressor1.clearFlag > 0) {
                                    decompressor1.bitsNumber = Compress.INIT_BITS;
                                    decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                    decompressor1.clearFlag = 0;
                                }
                                int result6;
                                if (decompressor1.input.cnt <= 0) {
                                    result6 = -1;
                                } else {
                                    int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                                    for (int i5 = 0; i5 < num1; i5++) {
                                        decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                        decompressor1.input.cnt--;
                                    }
                                    result6 = num1;
                                }
                                decompressor1.size = result6;
                                if (decompressor1.size <= 0) {
                                    result5 = -1;/* end of file */
                                } else {
                                    decompressor1.offset = 0;/* Round size down to integral number of codes */
                                    decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                                }
                            }
                            if (result5 == 0) {
                                rOff4 = decompressor1.offset;
                                bits4 = decompressor1.bitsNumber;
                                /*
                                 * Get to the first byte.
                                 */
                                bp4 += rOff4 >> 3;
                                rOff4 &= 7;
                                /* Get first part (low order bits) */
                                code7 = ((decompressor1.buf[bp4++] >> rOff4) & Compress.rmask[8 - rOff4]) & 0xff;
                                bits4 -= 8 - rOff4;
                                rOff4 = 8 - rOff4; /* now, offset into code word */
                                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                if (bits4 >= 8) {
                                    code7 |= (decompressor1.buf[bp4++] & 0xff) << rOff4;
                                    rOff4 += 8;
                                    bits4 -= 8;
                                }
                                /* high order bits. */
                                if (bits4 > 0) {
                                    code7 |= (decompressor1.buf[bp4] & Compress.rmask[bits4]) << rOff4;
                                }
                                decompressor1.offset += decompressor1.bitsNumber;
                                result5 = code7;
                            }
                            if (!((code5 = result5) > -1))
                                break;
                            if ((code5 == Compress.CLEAR) && (decompressor1.blockCompress != 0)) {
                                for (int code8 = 0; code8 < 256; code8++) {
                                    decompressor1.tabPrefix.tab[code8] = 0;
                                }
                                decompressor1.clearFlag = 1;
                                decompressor1.freeEntry = Compress.FIRST - 1;
                                int result6 = 0;
                                int code8 = 0;
                                int rOff5 = 0, bits5 = 0;
                                int bp5 = 0;
                                if (decompressor1.clearFlag > 0 || decompressor1.offset >= decompressor1.size
                                        || decompressor1.freeEntry > decompressor1.maxCode) {
                                    /*
                                     * If the next entry will be too big for the current code size, then
                                     * we must increase the size. This implies reading a new buffer
                                     * full, too.
                                     */
                                    if (decompressor1.freeEntry > decompressor1.maxCode) {
                                        decompressor1.bitsNumber++;
                                        if (decompressor1.bitsNumber == decompressor1.maxBits) {
                                            decompressor1.maxCode = decompressor1.maxMaxCode; /* won't get any bigger now */
                                        } else {
                                            decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                        }
                                    }
                                    if (decompressor1.clearFlag > 0) {
                                        decompressor1.bitsNumber = Compress.INIT_BITS;
                                        decompressor1.maxCode = ((1 << (decompressor1.bitsNumber)) - 1);
                                        decompressor1.clearFlag = 0;
                                    }
                                    int result7;
                                    if (decompressor1.input.cnt <= 0) {
                                        result7 = -1;
                                    } else {
                                        int num1 = Math.min(decompressor1.bitsNumber, decompressor1.input.cnt);
                                        for (int i5 = 0; i5 < num1; i5++) {
                                            decompressor1.buf[i5] = decompressor1.input.buffer[decompressor1.input.current++];
                                            decompressor1.input.cnt--;
                                        }
                                        result7 = num1;
                                    }
                                    decompressor1.size = result7;
                                    if (decompressor1.size <= 0) {
                                        result6 = -1;/* end of file */
                                    } else {
                                        decompressor1.offset = 0;/* Round size down to integral number of codes */
                                        decompressor1.size = (decompressor1.size << 3) - (decompressor1.bitsNumber - 1);
                                    }
                                }
                                if (result6 == 0) {
                                    rOff5 = decompressor1.offset;
                                    bits5 = decompressor1.bitsNumber;
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp5 += rOff5 >> 3;
                                    rOff5 &= 7;
                                    /* Get first part (low order bits) */
                                    code8 = ((decompressor1.buf[bp5++] >> rOff5) & Compress.rmask[8 - rOff5]) & 0xff;
                                    bits5 -= 8 - rOff5;
                                    rOff5 = 8 - rOff5; /* now, offset into code word */
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits5 >= 8) {
                                        code8 |= (decompressor1.buf[bp5++] & 0xff) << rOff5;
                                        rOff5 += 8;
                                        bits5 -= 8;
                                    }
                                    /* high order bits. */
                                    if (bits5 > 0) {
                                        code8 |= (decompressor1.buf[bp5] & Compress.rmask[bits5]) << rOff5;
                                    }
                                    decompressor1.offset += decompressor1.bitsNumber;
                                    result6 = code8;
                                }
                                if ((code5 = result6) == -1) /* O, untimely death! */
                                    break;
                            }
                            incode1 = code5;
                            /*
                             * Special case for KwKwK string.
                             */
                            if (code5 >= decompressor1.freeEntry) {
                                decompressor1.deStack.tab[decompressor1.deStack.index++] = (byte) finchar1;
                                code5 = oldcode1;
                            }
                            /*
                             * Generate output characters in reverse order
                             */
                            while (code5 >= 256) {
                                decompressor1.deStack.tab[decompressor1.deStack.index++] = decompressor1.tabSuffix.tab[code5];
                                code5 = (int) decompressor1.tabPrefix.tab[code5] << 16 >>> 16;
                            }
                            byte c3 = (byte) (finchar1 = decompressor1.tabSuffix.tab[code5]);
                            decompressor1.deStack.tab[decompressor1.deStack.index++] = c3;
                            /*
                             * And put them out in forward order
                             */
                            do {
                                byte c4 = decompressor1.deStack.tab[--decompressor1.deStack.index];
                                decompressor1.output.buffer[decompressor1.output.cnt++] = c4;
                            } while (!(decompressor1.deStack.index == 0));
                            /*
                             * Generate the new entry.
                             */
                            if ((code5 = decompressor1.freeEntry) < decompressor1.maxMaxCode) {
                                decompressor1.tabPrefix.tab[code5] = (short) oldcode1;
                                decompressor1.tabSuffix.tab[code5] = (byte) finchar1;
                                decompressor1.freeEntry = code5 + 1;
                            }
                            /*
                             * Remember previous code.
                             */
                            oldcode1 = incode1;
                        }
                    }
                }
                comprBuffer = out1;
                InputBuffer in = new InputBuffer(comprBuffer.cnt, COMPRESS_BUFFERS[btid - 1]);
                OutputBuffer out = new OutputBuffer(DECOMPRESS_BUFFERS[btid - 1]);
                if (CB.UNCOMPRESS == Compress.COMPRESS) {
                    Compressor compressor = new Compressor(in, out);
                    int fcode;
                    int i4 = 0;
                    int c;
                    int disp;
                    int hshift = 0;
                    int ent = compressor.input.cnt-- > 0 ? (compressor.input.buffer[compressor.input.current++] & 0x00FF) : -1;
                    for (fcode = compressor.htab.size; fcode < 65536; fcode *= 2) {
                        hshift++;
                    }
                    hshift = 8 - hshift; /* set hash code range bound */
                    int hsizeReg = compressor.htab.size;
                    /* clear hash table */
                    for (int i3 = 0; i3 < compressor.htab.size; i3++) {
                        compressor.htab.tab[i3] = -1;
                    }
                    next_byte: while ((c = compressor.input.cnt-- > 0
                            ? (compressor.input.buffer[compressor.input.current++] & 0x00FF)
                            : -1) != -1) {
                        compressor.inCount++;
                        fcode = (((int) c << compressor.maxBits) + ent);
                        i4 = ((c << hshift) ^ ent); /* xor hashing */
                        int temphtab = compressor.htab.tab[i4];
                        if (temphtab == fcode) {
                            ent = (int) compressor.codetab.tab[i4] << 16 >>> 16;
                            continue next_byte;
                        }
                        if (temphtab >= 0) { /* non-empty slot dm kmd 4/15 */
                            disp = hsizeReg - i4; /* secondary hash (after G. Knott) */
                            if (i4 == 0) {
                                disp = 1;
                            }
                            do {
                                if ((i4 -= disp) < 0) {
                                    i4 += hsizeReg;
                                }
                                temphtab = compressor.htab.tab[i4];
                                if (temphtab == fcode) {
                                    ent = (int) compressor.codetab.tab[i4] << 16 >>> 16;
                                    continue next_byte;
                                }
                            } while (temphtab > 0);
                        }
                        int code = ent;
                        int rOff = compressor.offset, bits = compressor.bitsNumber;
                        int bp = 0;
                        if (code >= 0) {
                            /*
                             * Get to the first byte.
                             */
                            bp += rOff >> 3;
                            rOff &= 7;
                            /*
                             * Since code is always >= 8 bits, only need to mask the first hunk
                             * on the left.
                             */
                            compressor.buf[bp] = (byte) ((compressor.buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                                    & Compress.lmask[rOff]);
                            bp++;
                            bits -= 8 - rOff;
                            code >>= 8 - rOff;
                            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                            if (bits >= 8) {
                                compressor.buf[bp++] = (byte) code;
                                code >>= 8;
                                bits -= 8;
                            }
                            /* Last bits. */
                            if (bits != 0) {
                                compressor.buf[bp] = (byte) code;
                            }
                            compressor.offset += compressor.bitsNumber;
                            if (compressor.offset == (compressor.bitsNumber << 3)) {
                                bp = 0;
                                bits = compressor.bitsNumber;
                                compressor.bytesOut += bits;
                                do {
                                    byte c1 = compressor.buf[bp++];
                                    compressor.output.buffer[compressor.output.cnt++] = c1;
                                } while (--bits != 0);
                                compressor.offset = 0;
                            }
                            /*
                             * If the next entry is going to be too big for the code size, then
                             * increase it, if possible.
                             */
                            if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                                /*
                                 * Write the whole buffer, because the input side won't discover
                                 * the size increase until after it has read it.
                                 */
                                if (compressor.offset > 0) {
                                    for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                        compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                    }
                                    compressor.bytesOut += compressor.bitsNumber;
                                }
                                compressor.offset = 0;
                                if (compressor.clearFlag != 0) {
                                    compressor.bitsNumber = Compress.INIT_BITS;
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                    compressor.clearFlag = 0;
                                } else {
                                    compressor.bitsNumber++;
                                    if (compressor.bitsNumber == compressor.maxBits) {
                                        compressor.maxCode = compressor.maxMaxCode;
                                    } else {
                                        compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                    }
                                }
                            }
                        } else {
                            /*
                             * At EOF, write the rest of the buffer.
                             */
                            if (compressor.offset > 0) {
                                for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                }
                            }
                            compressor.bytesOut += (compressor.offset + 7) / 8;
                            compressor.offset = 0;
                        }
                        compressor.outCount++;
                        ent = c;
                        if (compressor.freeEntry < compressor.maxMaxCode) {
                            /* code -> hashtable */
                            int v = compressor.freeEntry++;
                            compressor.codetab.tab[i4] = (short) v;
                            compressor.htab.tab[i4] = fcode;
                        } else if (compressor.inCount >= compressor.checkpoint && compressor.blockCompress != 0) {
                            int rat;
                            compressor.checkpoint = compressor.inCount + Compressor.CHECK_GAP;
                            if (compressor.inCount > 0x007fffff) { /* shift will overflow */
                                rat = compressor.bytesOut >> 8;
                                if (rat == 0) { /* Don't divide by zero */
                                    rat = 0x7fffffff;
                                } else {
                                    rat = compressor.inCount / rat;
                                }
                            } else {
                                rat = (compressor.inCount << 8) / compressor.bytesOut; /* 8 fractional bits */
                            }
                            if (rat > compressor.ratio) {
                                compressor.ratio = rat;
                            } else {
                                compressor.ratio = 0;
                                for (int i2 = 0; i2 < compressor.htab.size; i2++) {
                                    compressor.htab.tab[i2] = -1;
                                }
                                compressor.freeEntry = Compress.FIRST;
                                compressor.clearFlag = 1;
                                int code1 = (int) Compress.CLEAR;
                                int rOff1 = compressor.offset, bits1 = compressor.bitsNumber;
                                int bp1 = 0;
                                if (code1 >= 0) {
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp1 += rOff1 >> 3;
                                    rOff1 &= 7;
                                    /*
                                     * Since code is always >= 8 bits, only need to mask the first hunk
                                     * on the left.
                                     */
                                    compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                                            & Compress.lmask[rOff1]);
                                    bp1++;
                                    bits1 -= 8 - rOff1;
                                    code1 >>= 8 - rOff1;
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits1 >= 8) {
                                        compressor.buf[bp1++] = (byte) code1;
                                        code1 >>= 8;
                                        bits1 -= 8;
                                    }
                                    /* Last bits. */
                                    if (bits1 != 0) {
                                        compressor.buf[bp1] = (byte) code1;
                                    }
                                    compressor.offset += compressor.bitsNumber;
                                    if (compressor.offset == (compressor.bitsNumber << 3)) {
                                        bp1 = 0;
                                        bits1 = compressor.bitsNumber;
                                        compressor.bytesOut += bits1;
                                        do {
                                            byte c1 = compressor.buf[bp1++];
                                            compressor.output.buffer[compressor.output.cnt++] = c1;
                                        } while (--bits1 != 0);
                                        compressor.offset = 0;
                                    }
                                    /*
                                     * If the next entry is going to be too big for the code size, then
                                     * increase it, if possible.
                                     */
                                    if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                                        /*
                                         * Write the whole buffer, because the input side won't discover
                                         * the size increase until after it has read it.
                                         */
                                        if (compressor.offset > 0) {
                                            for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                            }
                                            compressor.bytesOut += compressor.bitsNumber;
                                        }
                                        compressor.offset = 0;
                                        if (compressor.clearFlag != 0) {
                                            compressor.bitsNumber = Compress.INIT_BITS;
                                            compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                            compressor.clearFlag = 0;
                                        } else {
                                            compressor.bitsNumber++;
                                            if (compressor.bitsNumber == compressor.maxBits) {
                                                compressor.maxCode = compressor.maxMaxCode;
                                            } else {
                                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                            }
                                        }
                                    }
                                } else {
                                    /*
                                     * At EOF, write the rest of the buffer.
                                     */
                                    if (compressor.offset > 0) {
                                        for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                            compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                        }
                                    }
                                    compressor.bytesOut += (compressor.offset + 7) / 8;
                                    compressor.offset = 0;
                                }
                            }
                        }
                    }
                    /*
                     * Put out the final code.
                     */
                    int code1 = ent;
                    int rOff1 = compressor.offset, bits1 = compressor.bitsNumber;
                    int bp1 = 0;
                    if (code1 >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp1 += rOff1 >> 3;
                        rOff1 &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                                & Compress.lmask[rOff1]);
                        bp1++;
                        bits1 -= 8 - rOff1;
                        code1 >>= 8 - rOff1;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits1 >= 8) {
                            compressor.buf[bp1++] = (byte) code1;
                            code1 >>= 8;
                            bits1 -= 8;
                        }
                        /* Last bits. */
                        if (bits1 != 0) {
                            compressor.buf[bp1] = (byte) code1;
                        }
                        compressor.offset += compressor.bitsNumber;
                        if (compressor.offset == (compressor.bitsNumber << 3)) {
                            bp1 = 0;
                            bits1 = compressor.bitsNumber;
                            compressor.bytesOut += bits1;
                            do {
                                byte c2 = compressor.buf[bp1++];
                                compressor.output.buffer[compressor.output.cnt++] = c2;
                            } while (--bits1 != 0);
                            compressor.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor.offset > 0) {
                                for (int i2 = 0; i2 < compressor.bitsNumber; i2++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i2];
                                }
                                compressor.bytesOut += compressor.bitsNumber;
                            }
                            compressor.offset = 0;
                            if (compressor.clearFlag != 0) {
                                compressor.bitsNumber = Compress.INIT_BITS;
                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                compressor.clearFlag = 0;
                            } else {
                                compressor.bitsNumber++;
                                if (compressor.bitsNumber == compressor.maxBits) {
                                    compressor.maxCode = compressor.maxMaxCode;
                                } else {
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor.offset > 0) {
                            for (int i2 = 0; i2 < ((compressor.offset + 7) / 8); i2++) {
                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i2];
                            }
                        }
                        compressor.bytesOut += (compressor.offset + 7) / 8;
                        compressor.offset = 0;
                    }
                    compressor.outCount++;
                    int code = -1;
                    int rOff = compressor.offset, bits = compressor.bitsNumber;
                    int bp = 0;
                    if (code >= 0) {
                        /*
                         * Get to the first byte.
                         */
                        bp += rOff >> 3;
                        rOff &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
                        compressor.buf[bp] = (byte) ((compressor.buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                                & Compress.lmask[rOff]);
                        bp++;
                        bits -= 8 - rOff;
                        code >>= 8 - rOff;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits >= 8) {
                            compressor.buf[bp++] = (byte) code;
                            code >>= 8;
                            bits -= 8;
                        }
                        /* Last bits. */
                        if (bits != 0) {
                            compressor.buf[bp] = (byte) code;
                        }
                        compressor.offset += compressor.bitsNumber;
                        if (compressor.offset == (compressor.bitsNumber << 3)) {
                            bp = 0;
                            bits = compressor.bitsNumber;
                            compressor.bytesOut += bits;
                            do {
                                byte c1 = compressor.buf[bp++];
                                compressor.output.buffer[compressor.output.cnt++] = c1;
                            } while (--bits != 0);
                            compressor.offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (compressor.freeEntry > compressor.maxCode || compressor.clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (compressor.offset > 0) {
                                for (int i1 = 0; i1 < compressor.bitsNumber; i1++) {
                                    compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                                }
                                compressor.bytesOut += compressor.bitsNumber;
                            }
                            compressor.offset = 0;
                            if (compressor.clearFlag != 0) {
                                compressor.bitsNumber = Compress.INIT_BITS;
                                compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                compressor.clearFlag = 0;
                            } else {
                                compressor.bitsNumber++;
                                if (compressor.bitsNumber == compressor.maxBits) {
                                    compressor.maxCode = compressor.maxMaxCode;
                                } else {
                                    compressor.maxCode = ((1 << (compressor.bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (compressor.offset > 0) {
                            for (int i1 = 0; i1 < ((compressor.offset + 7) / 8); i1++) {
                                compressor.output.buffer[compressor.output.cnt++] = compressor.buf[i1];
                            }
                        }
                        compressor.bytesOut += (compressor.offset + 7) / 8;
                        compressor.offset = 0;
                    }
                } else {
                    Decompressor decompressor = new Decompressor(in, out);
                    int code, oldcode, incode;
                    int result2 = 0;
                    int code3 = 0;
                    int rOff2 = 0, bits2 = 0;
                    int bp2 = 0;
                    if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                            || decompressor.freeEntry > decompressor.maxCode) {
                        /*
                         * If the next entry will be too big for the current code size, then
                         * we must increase the size. This implies reading a new buffer
                         * full, too.
                         */
                        if (decompressor.freeEntry > decompressor.maxCode) {
                            decompressor.bitsNumber++;
                            if (decompressor.bitsNumber == decompressor.maxBits) {
                                decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                            } else {
                                decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                            }
                        }
                        if (decompressor.clearFlag > 0) {
                            decompressor.bitsNumber = Compress.INIT_BITS;
                            decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                            decompressor.clearFlag = 0;
                        }
                        int result;
                        if (decompressor.input.cnt <= 0) {
                            result = -1;
                        } else {
                            int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                            for (int i1 = 0; i1 < num; i1++) {
                                decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                decompressor.input.cnt--;
                            }
                            result = num;
                        }
                        decompressor.size = result;
                        if (decompressor.size <= 0) {
                            result2 = -1;/* end of file */
                        } else {
                            decompressor.offset = 0;/* Round size down to integral number of codes */
                            decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                        }
                    }
                    if (result2 == 0) {
                        rOff2 = decompressor.offset;
                        bits2 = decompressor.bitsNumber;
                        /*
                         * Get to the first byte.
                         */
                        bp2 += rOff2 >> 3;
                        rOff2 &= 7;
                        /* Get first part (low order bits) */
                        code3 = ((decompressor.buf[bp2++] >> rOff2) & Compress.rmask[8 - rOff2]) & 0xff;
                        bits2 -= 8 - rOff2;
                        rOff2 = 8 - rOff2; /* now, offset into code word */
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits2 >= 8) {
                            code3 |= (decompressor.buf[bp2++] & 0xff) << rOff2;
                            rOff2 += 8;
                            bits2 -= 8;
                        }
                        /* high order bits. */
                        if (bits2 > 0) {
                            code3 |= (decompressor.buf[bp2] & Compress.rmask[bits2]) << rOff2;
                        }
                        decompressor.offset += decompressor.bitsNumber;
                        result2 = code3;
                    }
                    int finchar = oldcode = result2;
                    /* EOF already? */
                    /* Get out of here */
                    if (oldcode != -1) {/* first code must be 8 bits = byte */
                        decompressor.output.buffer[decompressor.output.cnt++] = (byte) finchar;
                        while (true) {
                            int result1 = 0;
                            int code2 = 0;
                            int rOff1 = 0, bits1 = 0;
                            int bp1 = 0;
                            if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                                    || decompressor.freeEntry > decompressor.maxCode) {
                                /*
                                 * If the next entry will be too big for the current code size, then
                                 * we must increase the size. This implies reading a new buffer
                                 * full, too.
                                 */
                                if (decompressor.freeEntry > decompressor.maxCode) {
                                    decompressor.bitsNumber++;
                                    if (decompressor.bitsNumber == decompressor.maxBits) {
                                        decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                                    } else {
                                        decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                    }
                                }
                                if (decompressor.clearFlag > 0) {
                                    decompressor.bitsNumber = Compress.INIT_BITS;
                                    decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                    decompressor.clearFlag = 0;
                                }
                                int result;
                                if (decompressor.input.cnt <= 0) {
                                    result = -1;
                                } else {
                                    int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                                    for (int i1 = 0; i1 < num; i1++) {
                                        decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                        decompressor.input.cnt--;
                                    }
                                    result = num;
                                }
                                decompressor.size = result;
                                if (decompressor.size <= 0) {
                                    result1 = -1;/* end of file */
                                } else {
                                    decompressor.offset = 0;/* Round size down to integral number of codes */
                                    decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                                }
                            }
                            if (result1 == 0) {
                                rOff1 = decompressor.offset;
                                bits1 = decompressor.bitsNumber;
                                /*
                                 * Get to the first byte.
                                 */
                                bp1 += rOff1 >> 3;
                                rOff1 &= 7;
                                /* Get first part (low order bits) */
                                code2 = ((decompressor.buf[bp1++] >> rOff1) & Compress.rmask[8 - rOff1]) & 0xff;
                                bits1 -= 8 - rOff1;
                                rOff1 = 8 - rOff1; /* now, offset into code word */
                                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                if (bits1 >= 8) {
                                    code2 |= (decompressor.buf[bp1++] & 0xff) << rOff1;
                                    rOff1 += 8;
                                    bits1 -= 8;
                                }
                                /* high order bits. */
                                if (bits1 > 0) {
                                    code2 |= (decompressor.buf[bp1] & Compress.rmask[bits1]) << rOff1;
                                }
                                decompressor.offset += decompressor.bitsNumber;
                                result1 = code2;
                            }
                            if (!((code = result1) > -1))
                                break;
                            if ((code == Compress.CLEAR) && (decompressor.blockCompress != 0)) {
                                for (int code4 = 0; code4 < 256; code4++) {
                                    decompressor.tabPrefix.tab[code4] = 0;
                                }
                                decompressor.clearFlag = 1;
                                decompressor.freeEntry = Compress.FIRST - 1;
                                int result = 0;
                                int code1 = 0;
                                int rOff = 0, bits = 0;
                                int bp = 0;
                                if (decompressor.clearFlag > 0 || decompressor.offset >= decompressor.size
                                        || decompressor.freeEntry > decompressor.maxCode) {
                                    /*
                                     * If the next entry will be too big for the current code size, then
                                     * we must increase the size. This implies reading a new buffer
                                     * full, too.
                                     */
                                    if (decompressor.freeEntry > decompressor.maxCode) {
                                        decompressor.bitsNumber++;
                                        if (decompressor.bitsNumber == decompressor.maxBits) {
                                            decompressor.maxCode = decompressor.maxMaxCode; /* won't get any bigger now */
                                        } else {
                                            decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                        }
                                    }
                                    if (decompressor.clearFlag > 0) {
                                        decompressor.bitsNumber = Compress.INIT_BITS;
                                        decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                        decompressor.clearFlag = 0;
                                    }
                                    int result3;
                                    if (decompressor.input.cnt <= 0) {
                                        result3 = -1;
                                    } else {
                                        int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                                        for (int i1 = 0; i1 < num; i1++) {
                                            decompressor.buf[i1] = decompressor.input.buffer[decompressor.input.current++];
                                            decompressor.input.cnt--;
                                        }
                                        result3 = num;
                                    }
                                    decompressor.size = result3;
                                    if (decompressor.size <= 0) {
                                        result = -1;/* end of file */
                                    } else {
                                        decompressor.offset = 0;/* Round size down to integral number of codes */
                                        decompressor.size = (decompressor.size << 3) - (decompressor.bitsNumber - 1);
                                    }
                                }
                                if (result == 0) {
                                    rOff = decompressor.offset;
                                    bits = decompressor.bitsNumber;
                                    /*
                                     * Get to the first byte.
                                     */
                                    bp += rOff >> 3;
                                    rOff &= 7;
                                    /* Get first part (low order bits) */
                                    code1 = ((decompressor.buf[bp++] >> rOff) & Compress.rmask[8 - rOff]) & 0xff;
                                    bits -= 8 - rOff;
                                    rOff = 8 - rOff; /* now, offset into code word */
                                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                                    if (bits >= 8) {
                                        code1 |= (decompressor.buf[bp++] & 0xff) << rOff;
                                        rOff += 8;
                                        bits -= 8;
                                    }
                                    /* high order bits. */
                                    if (bits > 0) {
                                        code1 |= (decompressor.buf[bp] & Compress.rmask[bits]) << rOff;
                                    }
                                    decompressor.offset += decompressor.bitsNumber;
                                    result = code1;
                                }
                                if ((code = result) == -1) /* O, untimely death! */
                                    break;
                            }
                            incode = code;
                            /*
                             * Special case for KwKwK string.
                             */
                            if (code >= decompressor.freeEntry) {
                                decompressor.deStack.tab[decompressor.deStack.index++] = (byte) finchar;
                                code = oldcode;
                            }
                            /*
                             * Generate output characters in reverse order
                             */
                            while (code >= 256) {
                                decompressor.deStack.tab[decompressor.deStack.index++] = decompressor.tabSuffix.tab[code];
                                code = (int) decompressor.tabPrefix.tab[code] << 16 >>> 16;
                            }
                            byte c1 = (byte) (finchar = decompressor.tabSuffix.tab[code]);
                            decompressor.deStack.tab[decompressor.deStack.index++] = c1;
                            /*
                             * And put them out in forward order
                             */
                            do {
                                byte c = decompressor.deStack.tab[--decompressor.deStack.index];
                                decompressor.output.buffer[decompressor.output.cnt++] = c;
                            } while (!(decompressor.deStack.index == 0));
                            /*
                             * Generate the new entry.
                             */
                            if ((code = decompressor.freeEntry) < decompressor.maxMaxCode) {
                                decompressor.tabPrefix.tab[code] = (short) oldcode;
                                decompressor.tabSuffix.tab[code] = (byte) finchar;
                                decompressor.freeEntry = code + 1;
                            }
                            /*
                             * Remember previous code.
                             */
                            oldcode = incode;
                        }
                    }
                }
                decomprBufer = out;
                System.out.print(source.length + " " + source.crc + " ");
                CRC32 crc33 = new CRC32();
                crc33.update(comprBuffer.buffer, 0, comprBuffer.cnt);
                System.out.print(comprBuffer.cnt + crc33.getValue() + " ");
                CRC32 crc32 = new CRC32();
                crc32.update(decomprBufer.buffer, 0, decomprBufer.cnt);
                System.out.println(decomprBufer.cnt + " " + crc32.getValue());
            }
        }
        return System.currentTimeMillis() - startTime;
    }

    static void prepareBuffers() {
        CB = new Compress();
        SOURCES = new Source[FILES_NUMBER];
        for (int i = 0; i < FILES_NUMBER; i++) {
            SOURCES[i] = new Source("/" + FILES_NAMES[i]);
        }
        DECOMPRESS_BUFFERS = new byte[10][Source.MAX_LENGTH];
        COMPRESS_BUFFERS = new byte[10][Source.MAX_LENGTH];
    }

    static class Source {
        public byte[] buffer;
        public long crc;
        public int length;
        static int MAX_LENGTH;

        public Source(String fileName) {
            byte[] res = null;
            try {
                FileInputStream sif = new FileInputStream(fileName);
                int length1 = (int) new File(fileName).length();
                int counter = 0;
                byte[] result = new byte[length1];
                int bytes_read;
                while ((bytes_read = sif.read(result, counter,
                        (length1 - counter))) > 0) {
                    counter += bytes_read;
                }
                sif.close();
                if (counter != length1) {
                    System.out.println(
                            "ERROR reading test input file");
                }
                res = result;
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            buffer = res;
            length = buffer.length;
            MAX_LENGTH = Math.max(length, MAX_LENGTH);
            CRC32 crc32 = new CRC32();
            crc32.update(buffer, 0, length);
            crc = crc32.getValue();
        }

        long getCRC() {
            return crc;
        }

        int getLength() {
            return length;
        }

        byte[] getBuffer() {
            return buffer;
        }

        public static byte[] fillBuffer(String fileName) {
            try {
                FileInputStream sif = new FileInputStream(fileName);
                int length = (int) new File(fileName).length();
                int counter = 0;
                byte[] result = new byte[length];
                int bytes_read;
                while ((bytes_read = sif.read(result, counter,
                        (length - counter))) > 0) {
                    counter += bytes_read;
                }
                sif.close();
                if (counter != length) {
                    System.out.println(
                            "ERROR reading test input file");
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            return null;
        }
    }
}
