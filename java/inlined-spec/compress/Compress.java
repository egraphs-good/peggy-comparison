/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Used 'Inner Classes' to minimize temptations of JVM exploiting low hanging
 * fruits. 'Inner classes' are defined in appendix D of the 'Java Programming
 * Language' by Ken Arnold. - We moved the class declaration, unchanged, of
 * Hash_Table to within the class declaration of Compressor. - We moved the
 * class declarations, unchanged, of De_Stack and Suffix_Table to within the
 * class declaration of Decompressor. - pre-computed trivial htab(i) to minimize
 * millions of trivial calls - Don McCauley (IBM), Kaivalya 4/16/98
 *
 * @(#)Compress.java 1.7 06/17/98 
 * and getcode fixed -- kaivalya & Don compress.c - File compression ala IEEE
 * Computer, June 1984.
 *
 * Authors: Spencer W. Thomas (decvax!harpo!utah-cs!utah-gr!thomas) Jim McKie
 * (decvax!mcvax!jim) Steve Davies (decvax!vax135!petsd!peora!srd) Ken Turkowski
 * (decvax!decwrl!turtlevax!ken) James A. Woods (decvax!ihnp4!ames!jaw) Joe
 * Orost (decvax!vax135!petsd!joe)
 *
 * Algorithm from "A Technique for High Performance Data Compression", Terry A.
 * Welch, IEEE Computer Vol 17, No 6 (June 1984), pp 8-19.
 *
 * Algorithm: Modified Lempel-Ziv method (LZW). Basically finds common
 * substrings and replaces them with a variable size code. This is
 * deterministic, and can be done on the fly. Thus, the decompression procedure
 * needs no input table, but tracks the way the table was built.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
import java.util.zip.CRC32;

public final class Compress {
    final static int COMPRESS = 0;
    final static int UNCOMPRESS = 1;
    final static int BITS = 16; /* always set to 16 for SPEC95 */
    final static int INIT_BITS = 9; /* initial number of bits/code */
    final static int HSIZE = 69001; /* 95% occupancy */
    final static int SUFFIX_TAB_SZ = 65536; /* 2**BITS */
    final static int STACK_SZ = 8000; /* decompression stack size */
    final static byte magic_header[] = { (byte) 037, (byte) 0235 }; /* 1F 9D */
    /* Defines for third byte of header */
    final static int BIT_MASK = 0x1f;
    final static int BLOCK_MASK = 0x80;
    /*
     * Masks 0x40 and 0x20 are free. I think 0x20 should mean that there is a
     * fourth header byte (for expansion).
     */
    /*
     * the next two codes should not be changed lightly, as they must not lie
     * within the contiguous general code space.
     */
    final static int FIRST = 257; /* first free entry */
    final static int CLEAR = 256; /* table clear output code */
    final static byte lmask[] = { (byte) 0xff, (byte) 0xfe, (byte) 0xfc,
            (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
            (byte) 0xc0, (byte) 0x80, (byte) 0x00 };
    final static byte rmask[] = { (byte) 0x00, (byte) 0x01, (byte) 0x03,
            (byte) 0x07, (byte) 0x0f, (byte) 0x1f,
            (byte) 0x3f, (byte) 0x7f, (byte) 0xff };

    public static OutputBuffer performAction(byte[] src, int srcLength,
            int action, byte[] dst) {
        InputBuffer in = new InputBuffer(srcLength, src);
        OutputBuffer out = new OutputBuffer(dst);
        if (action == COMPRESS) {
            Compressor compressor = new Compressor(in, out);
            int fcode;
            int i = 0;
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
                i = ((c << hshift) ^ ent); /* xor hashing */
                int temphtab = compressor.htab.tab[i];
                if (temphtab == fcode) {
                    ent = (int) compressor.codetab.tab[i] << 16 >>> 16;
                    continue next_byte;
                }
                if (temphtab >= 0) { /* non-empty slot dm kmd 4/15 */
                    disp = hsizeReg - i; /* secondary hash (after G. Knott) */
                    if (i == 0) {
                        disp = 1;
                    }
                    do {
                        if ((i -= disp) < 0) {
                            i += hsizeReg;
                        }
                        temphtab = compressor.htab.tab[i];
                        if (temphtab == fcode) {
                            ent = (int) compressor.codetab.tab[i] << 16 >>> 16;
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
                    compressor.buf[bp] = (byte) ((compressor.buf[bp] & rmask[rOff]) | (code << rOff)
                            & lmask[rOff]);
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
                            compressor.bitsNumber = INIT_BITS;
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
                    compressor.codetab.tab[i] = (short) v;
                    compressor.htab.tab[i] = fcode;
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
                        compressor.freeEntry = FIRST;
                        compressor.clearFlag = 1;
                        int code1 = (int) CLEAR;
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
                            compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & rmask[rOff1]) | (code1 << rOff1)
                                    & lmask[rOff1]);
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
                                    compressor.bitsNumber = INIT_BITS;
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
                compressor.buf[bp1] = (byte) ((compressor.buf[bp1] & rmask[rOff1]) | (code1 << rOff1)
                        & lmask[rOff1]);
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
                        compressor.bitsNumber = INIT_BITS;
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
                compressor.buf[bp] = (byte) ((compressor.buf[bp] & rmask[rOff]) | (code << rOff)
                        & lmask[rOff]);
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
                        compressor.bitsNumber = INIT_BITS;
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
                    decompressor.bitsNumber = INIT_BITS;
                    decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                    decompressor.clearFlag = 0;
                }
                int result;
                if (decompressor.input.cnt <= 0) {
                    result = -1;
                } else {
                    int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                    for (int i = 0; i < num; i++) {
                        decompressor.buf[i] = decompressor.input.buffer[decompressor.input.current++];
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
                code3 = ((decompressor.buf[bp2++] >> rOff2) & rmask[8 - rOff2]) & 0xff;
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
                    code3 |= (decompressor.buf[bp2] & rmask[bits2]) << rOff2;
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
                            decompressor.bitsNumber = INIT_BITS;
                            decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                            decompressor.clearFlag = 0;
                        }
                        int result;
                        if (decompressor.input.cnt <= 0) {
                            result = -1;
                        } else {
                            int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                            for (int i = 0; i < num; i++) {
                                decompressor.buf[i] = decompressor.input.buffer[decompressor.input.current++];
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
                        code2 = ((decompressor.buf[bp1++] >> rOff1) & rmask[8 - rOff1]) & 0xff;
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
                            code2 |= (decompressor.buf[bp1] & rmask[bits1]) << rOff1;
                        }
                        decompressor.offset += decompressor.bitsNumber;
                        result1 = code2;
                    }
                    if (!((code = result1) > -1))
                        break;
                    if ((code == CLEAR) && (decompressor.blockCompress != 0)) {
                        for (int code4 = 0; code4 < 256; code4++) {
                            decompressor.tabPrefix.tab[code4] = 0;
                        }
                        decompressor.clearFlag = 1;
                        decompressor.freeEntry = FIRST - 1;
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
                                decompressor.bitsNumber = INIT_BITS;
                                decompressor.maxCode = ((1 << (decompressor.bitsNumber)) - 1);
                                decompressor.clearFlag = 0;
                            }
                            int result3;
                            if (decompressor.input.cnt <= 0) {
                                result3 = -1;
                            } else {
                                int num = Math.min(decompressor.bitsNumber, decompressor.input.cnt);
                                for (int i = 0; i < num; i++) {
                                    decompressor.buf[i] = decompressor.input.buffer[decompressor.input.current++];
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
                            code1 = ((decompressor.buf[bp++] >> rOff) & rmask[8 - rOff]) & 0xff;
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
                                code1 |= (decompressor.buf[bp] & rmask[bits]) << rOff;
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
        return out;
    }
}

final class InputBuffer {
    int cnt;
    int current;
    byte[] buffer;

    public InputBuffer(int c, byte[] b) {
        cnt = c;
        buffer = b;
    }

    public int readByte() {
        return cnt-- > 0 ? (buffer[current++] & 0x00FF) : -1;
    }

    public int readBytes(byte[] buf, int n) {
        if (cnt <= 0)
            return -1;
        int num = Math.min(n, cnt);
        for (int i = 0; i < num; i++) {
            buf[i] = buffer[current++];
            cnt--;
        }
        return num;
    }
}

final class OutputBuffer {
    int cnt;
    byte[] buffer;

    public OutputBuffer(byte[] b) {
        buffer = b;
    }

    public int getLength() {
        return cnt;
    }

    public long getCRC() {
        CRC32 crc32 = new CRC32();
        crc32.update(buffer, 0, cnt);
        return crc32.getValue();
    }

    public void writeByte(byte c) {
        buffer[cnt++] = c;
    }

    public void writebytes(byte[] buf, int n) {
        for (int i = 0; i < n; i++) {
            buffer[cnt++] = buf[i];
        }
    }
}

final class CodeTable {
    short[] tab;

    public CodeTable() {
        tab = new short[Compress.HSIZE];
    }

    public int of(int i) {
        return (int) tab[i] << 16 >>> 16;
    }

    public void set(int i, int v) {
        tab[i] = (short) v;
    }

    public void clear(int size) {
        for (int code = 0; code < size; code++) {
            tab[code] = 0;
        }
    }
}

class CompBase {
    protected int bitsNumber; /* number of bits/code */
    protected int maxBits; /* user settable max # bits/code */
    protected int maxCode; /* maximum code, given n_bits */
    protected int maxMaxCode; /* should NEVER generate this code */
    protected int offset;
    protected int blockCompress;
    protected int freeEntry; /* first unused entry */
    protected int clearFlag;
    protected InputBuffer input;
    protected OutputBuffer output;
    protected byte buf[];

    public CompBase(InputBuffer in, OutputBuffer out) {
        input = in;
        output = out;
        maxBits = Compress.BITS;
        blockCompress = Compress.BLOCK_MASK;
        buf = new byte[Compress.BITS];
    }

    public int getMaxCode() {
        return ((1 << (bitsNumber)) - 1);
    }
}

/*
 * compress (Originally: stdin to stdout -- Changed by SPEC to: memory to
 * memory)
 *
 * Algorithm: use open addressing double hashing (no chaining) on the prefix
 * code / next character combination. We do a variant of Knuth's algorithm D
 * (vol. 3, sec. 6.4) along with G. Knott's relatively-prime secondary probe.
 * Here, the modular division first probe is gives way to a faster exclusive-or
 * manipulation. Also do block compression with an adaptive reset, whereby the
 * code table is cleared when the compression ratio decreases, but after the
 * table fills. The variable-length output codes are re-sized at this point, and
 * a special CLEAR code is generated for the decompressor. Late addition:
 * construct the table according to file size for noticeable speed improvement
 * on small files. Please direct questions about this implementation to
 * ames!jaw.
 */
final class Compressor extends CompBase {
    final static int CHECK_GAP = 10000; /* ratio check interval */
    int ratio;
    int checkpoint;
    int inCount; /* length of input */
    int outCount; /* # of codes output */
    int bytesOut; /* length of compressed output */
    HashTable htab;
    CodeTable codetab;

    public Compressor(InputBuffer in, OutputBuffer out) {
        super(in, out);
        if (maxBits < Compress.INIT_BITS) {
            maxBits = Compress.INIT_BITS;
        }
        if (maxBits > Compress.BITS) {
            maxBits = Compress.BITS;
        }
        maxMaxCode = 1 << maxBits;
        bitsNumber = Compress.INIT_BITS;
        maxCode = ((1 << (bitsNumber)) - 1);
        offset = 0;
        bytesOut = 3; /* includes 3-byte header mojo */
        outCount = 0;
        clearFlag = 0;
        ratio = 0;
        inCount = 1;
        checkpoint = CHECK_GAP;
        freeEntry = ((blockCompress != 0) ? Compress.FIRST : 256);
        htab = new HashTable();
        codetab = new CodeTable();
        output.buffer[output.cnt++] = Compress.magic_header[0];
        output.buffer[output.cnt++] = Compress.magic_header[1];
        output.buffer[output.cnt++] = (byte) (maxBits | blockCompress);
    }

    public void compress() {
        int fcode;
        int i = 0;
        int c;
        int disp;
        int hshift = 0;
        int ent = input.cnt-- > 0 ? (input.buffer[input.current++] & 0x00FF) : -1;
        for (fcode = htab.size; fcode < 65536; fcode *= 2) {
            hshift++;
        }
        hshift = 8 - hshift; /* set hash code range bound */
        int hsizeReg = htab.size;
        /* clear hash table */
        for (int i3 = 0; i3 < htab.size; i3++) {
            htab.tab[i3] = -1;
        }
        next_byte: while ((c = input.cnt-- > 0 ? (input.buffer[input.current++] & 0x00FF) : -1) != -1) {
            inCount++;
            fcode = (((int) c << maxBits) + ent);
            i = ((c << hshift) ^ ent); /* xor hashing */
            int temphtab = htab.tab[i];
            if (temphtab == fcode) {
                ent = (int) codetab.tab[i] << 16 >>> 16;
                continue next_byte;
            }
            if (temphtab >= 0) { /* non-empty slot dm kmd 4/15 */
                disp = hsizeReg - i; /* secondary hash (after G. Knott) */
                if (i == 0) {
                    disp = 1;
                }
                do {
                    if ((i -= disp) < 0) {
                        i += hsizeReg;
                    }
                    temphtab = htab.tab[i];
                    if (temphtab == fcode) {
                        ent = (int) codetab.tab[i] << 16 >>> 16;
                        continue next_byte;
                    }
                } while (temphtab > 0);
            }
            int code = ent;
            int rOff = offset, bits = bitsNumber;
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
                buf[bp] = (byte) ((buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                        & Compress.lmask[rOff]);
                bp++;
                bits -= 8 - rOff;
                code >>= 8 - rOff;
                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                if (bits >= 8) {
                    buf[bp++] = (byte) code;
                    code >>= 8;
                    bits -= 8;
                }
                /* Last bits. */
                if (bits != 0) {
                    buf[bp] = (byte) code;
                }
                offset += bitsNumber;
                if (offset == (bitsNumber << 3)) {
                    bp = 0;
                    bits = bitsNumber;
                    bytesOut += bits;
                    do {
                        byte c1 = buf[bp++];
                        output.buffer[output.cnt++] = c1;
                    } while (--bits != 0);
                    offset = 0;
                }
                /*
                 * If the next entry is going to be too big for the code size, then
                 * increase it, if possible.
                 */
                if (freeEntry > maxCode || clearFlag > 0) {
                    /*
                     * Write the whole buffer, because the input side won't discover
                     * the size increase until after it has read it.
                     */
                    if (offset > 0) {
                        for (int i1 = 0; i1 < bitsNumber; i1++) {
                            output.buffer[output.cnt++] = buf[i1];
                        }
                        bytesOut += bitsNumber;
                    }
                    offset = 0;
                    if (clearFlag != 0) {
                        bitsNumber = Compress.INIT_BITS;
                        maxCode = ((1 << (bitsNumber)) - 1);
                        clearFlag = 0;
                    } else {
                        bitsNumber++;
                        if (bitsNumber == maxBits) {
                            maxCode = maxMaxCode;
                        } else {
                            maxCode = ((1 << (bitsNumber)) - 1);
                        }
                    }
                }
            } else {
                /*
                 * At EOF, write the rest of the buffer.
                 */
                if (offset > 0) {
                    for (int i1 = 0; i1 < ((offset + 7) / 8); i1++) {
                        output.buffer[output.cnt++] = buf[i1];
                    }
                }
                bytesOut += (offset + 7) / 8;
                offset = 0;
            }
            outCount++;
            ent = c;
            if (freeEntry < maxMaxCode) {
                /* code -> hashtable */
                int v = freeEntry++;
                codetab.tab[i] = (short) v;
                htab.tab[i] = fcode;
            } else if (inCount >= checkpoint && blockCompress != 0) {
                int rat;
                checkpoint = inCount + CHECK_GAP;
                if (inCount > 0x007fffff) { /* shift will overflow */
                    rat = bytesOut >> 8;
                    if (rat == 0) { /* Don't divide by zero */
                        rat = 0x7fffffff;
                    } else {
                        rat = inCount / rat;
                    }
                } else {
                    rat = (inCount << 8) / bytesOut; /* 8 fractional bits */
                }
                if (rat > ratio) {
                    ratio = rat;
                } else {
                    ratio = 0;
                    for (int i2 = 0; i2 < htab.size; i2++) {
                        htab.tab[i2] = -1;
                    }
                    freeEntry = Compress.FIRST;
                    clearFlag = 1;
                    int code1 = (int) Compress.CLEAR;
                    int rOff1 = offset, bits1 = bitsNumber;
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
                        buf[bp1] = (byte) ((buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                                & Compress.lmask[rOff1]);
                        bp1++;
                        bits1 -= 8 - rOff1;
                        code1 >>= 8 - rOff1;
                        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                        if (bits1 >= 8) {
                            buf[bp1++] = (byte) code1;
                            code1 >>= 8;
                            bits1 -= 8;
                        }
                        /* Last bits. */
                        if (bits1 != 0) {
                            buf[bp1] = (byte) code1;
                        }
                        offset += bitsNumber;
                        if (offset == (bitsNumber << 3)) {
                            bp1 = 0;
                            bits1 = bitsNumber;
                            bytesOut += bits1;
                            do {
                                byte c1 = buf[bp1++];
                                output.buffer[output.cnt++] = c1;
                            } while (--bits1 != 0);
                            offset = 0;
                        }
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
                        if (freeEntry > maxCode || clearFlag > 0) {
                            /*
                             * Write the whole buffer, because the input side won't discover
                             * the size increase until after it has read it.
                             */
                            if (offset > 0) {
                                for (int i1 = 0; i1 < bitsNumber; i1++) {
                                    output.buffer[output.cnt++] = buf[i1];
                                }
                                bytesOut += bitsNumber;
                            }
                            offset = 0;
                            if (clearFlag != 0) {
                                bitsNumber = Compress.INIT_BITS;
                                maxCode = ((1 << (bitsNumber)) - 1);
                                clearFlag = 0;
                            } else {
                                bitsNumber++;
                                if (bitsNumber == maxBits) {
                                    maxCode = maxMaxCode;
                                } else {
                                    maxCode = ((1 << (bitsNumber)) - 1);
                                }
                            }
                        }
                    } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
                        if (offset > 0) {
                            for (int i1 = 0; i1 < ((offset + 7) / 8); i1++) {
                                output.buffer[output.cnt++] = buf[i1];
                            }
                        }
                        bytesOut += (offset + 7) / 8;
                        offset = 0;
                    }
                }
            }
        }
        /*
         * Put out the final code.
         */
        int code1 = ent;
        int rOff1 = offset, bits1 = bitsNumber;
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
            buf[bp1] = (byte) ((buf[bp1] & Compress.rmask[rOff1]) | (code1 << rOff1)
                    & Compress.lmask[rOff1]);
            bp1++;
            bits1 -= 8 - rOff1;
            code1 >>= 8 - rOff1;
            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
            if (bits1 >= 8) {
                buf[bp1++] = (byte) code1;
                code1 >>= 8;
                bits1 -= 8;
            }
            /* Last bits. */
            if (bits1 != 0) {
                buf[bp1] = (byte) code1;
            }
            offset += bitsNumber;
            if (offset == (bitsNumber << 3)) {
                bp1 = 0;
                bits1 = bitsNumber;
                bytesOut += bits1;
                do {
                    byte c2 = buf[bp1++];
                    output.buffer[output.cnt++] = c2;
                } while (--bits1 != 0);
                offset = 0;
            }
            /*
             * If the next entry is going to be too big for the code size, then
             * increase it, if possible.
             */
            if (freeEntry > maxCode || clearFlag > 0) {
                /*
                 * Write the whole buffer, because the input side won't discover
                 * the size increase until after it has read it.
                 */
                if (offset > 0) {
                    for (int i2 = 0; i2 < bitsNumber; i2++) {
                        output.buffer[output.cnt++] = buf[i2];
                    }
                    bytesOut += bitsNumber;
                }
                offset = 0;
                if (clearFlag != 0) {
                    bitsNumber = Compress.INIT_BITS;
                    maxCode = ((1 << (bitsNumber)) - 1);
                    clearFlag = 0;
                } else {
                    bitsNumber++;
                    if (bitsNumber == maxBits) {
                        maxCode = maxMaxCode;
                    } else {
                        maxCode = ((1 << (bitsNumber)) - 1);
                    }
                }
            }
        } else {
            /*
             * At EOF, write the rest of the buffer.
             */
            if (offset > 0) {
                for (int i2 = 0; i2 < ((offset + 7) / 8); i2++) {
                    output.buffer[output.cnt++] = buf[i2];
                }
            }
            bytesOut += (offset + 7) / 8;
            offset = 0;
        }
        outCount++;
        int code = -1;
        int rOff = offset, bits = bitsNumber;
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
            buf[bp] = (byte) ((buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                    & Compress.lmask[rOff]);
            bp++;
            bits -= 8 - rOff;
            code >>= 8 - rOff;
            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
            if (bits >= 8) {
                buf[bp++] = (byte) code;
                code >>= 8;
                bits -= 8;
            }
            /* Last bits. */
            if (bits != 0) {
                buf[bp] = (byte) code;
            }
            offset += bitsNumber;
            if (offset == (bitsNumber << 3)) {
                bp = 0;
                bits = bitsNumber;
                bytesOut += bits;
                do {
                    byte c1 = buf[bp++];
                    output.buffer[output.cnt++] = c1;
                } while (--bits != 0);
                offset = 0;
            }
            /*
             * If the next entry is going to be too big for the code size, then
             * increase it, if possible.
             */
            if (freeEntry > maxCode || clearFlag > 0) {
                /*
                 * Write the whole buffer, because the input side won't discover
                 * the size increase until after it has read it.
                 */
                if (offset > 0) {
                    for (int i1 = 0; i1 < bitsNumber; i1++) {
                        output.buffer[output.cnt++] = buf[i1];
                    }
                    bytesOut += bitsNumber;
                }
                offset = 0;
                if (clearFlag != 0) {
                    bitsNumber = Compress.INIT_BITS;
                    maxCode = ((1 << (bitsNumber)) - 1);
                    clearFlag = 0;
                } else {
                    bitsNumber++;
                    if (bitsNumber == maxBits) {
                        maxCode = maxMaxCode;
                    } else {
                        maxCode = ((1 << (bitsNumber)) - 1);
                    }
                }
            }
        } else {
            /*
             * At EOF, write the rest of the buffer.
             */
            if (offset > 0) {
                for (int i1 = 0; i1 < ((offset + 7) / 8); i1++) {
                    output.buffer[output.cnt++] = buf[i1];
                }
            }
            bytesOut += (offset + 7) / 8;
            offset = 0;
        }
        return;
    }

    /*
     * Output the given code. Inputs: code: A n_bits-bit integer. If == -1, then
     * EOF. This assumes that n_bits = < (long)wordsize - 1. Outputs: Outputs
     * code to the file. Assumptions: Chars are 8 bits long. Algorithm: Maintain
     * a BITS character long buffer (so that 8 codes will fit in it exactly).
     */
    void output(int code) {
        int rOff = offset, bits = bitsNumber;
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
            buf[bp] = (byte) ((buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                    & Compress.lmask[rOff]);
            bp++;
            bits -= 8 - rOff;
            code >>= 8 - rOff;
            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
            if (bits >= 8) {
                buf[bp++] = (byte) code;
                code >>= 8;
                bits -= 8;
            }
            /* Last bits. */
            if (bits != 0) {
                buf[bp] = (byte) code;
            }
            offset += bitsNumber;
            if (offset == (bitsNumber << 3)) {
                bp = 0;
                bits = bitsNumber;
                bytesOut += bits;
                do {
                    byte c = buf[bp++];
                    output.buffer[output.cnt++] = c;
                } while (--bits != 0);
                offset = 0;
            }
            /*
             * If the next entry is going to be too big for the code size, then
             * increase it, if possible.
             */
            if (freeEntry > maxCode || clearFlag > 0) {
                /*
                 * Write the whole buffer, because the input side won't discover
                 * the size increase until after it has read it.
                 */
                if (offset > 0) {
                    for (int i = 0; i < bitsNumber; i++) {
                        output.buffer[output.cnt++] = buf[i];
                    }
                    bytesOut += bitsNumber;
                }
                offset = 0;
                if (clearFlag != 0) {
                    bitsNumber = Compress.INIT_BITS;
                    maxCode = ((1 << (bitsNumber)) - 1);
                    clearFlag = 0;
                } else {
                    bitsNumber++;
                    if (bitsNumber == maxBits) {
                        maxCode = maxMaxCode;
                    } else {
                        maxCode = ((1 << (bitsNumber)) - 1);
                    }
                }
            }
        } else {
            /*
             * At EOF, write the rest of the buffer.
             */
            if (offset > 0) {
                for (int i = 0; i < ((offset + 7) / 8); i++) {
                    output.buffer[output.cnt++] = buf[i];
                }
            }
            bytesOut += (offset + 7) / 8;
            offset = 0;
        }
    }

    /* table clear for block compress */
    void clBlock() {
        int rat;
        checkpoint = inCount + CHECK_GAP;
        if (inCount > 0x007fffff) { /* shift will overflow */
            rat = bytesOut >> 8;
            if (rat == 0) { /* Don't divide by zero */
                rat = 0x7fffffff;
            } else {
                rat = inCount / rat;
            }
        } else {
            rat = (inCount << 8) / bytesOut; /* 8 fractional bits */
        }
        if (rat > ratio) {
            ratio = rat;
        } else {
            ratio = 0;
            for (int i1 = 0; i1 < htab.size; i1++) {
                htab.tab[i1] = -1;
            }
            freeEntry = Compress.FIRST;
            clearFlag = 1;
            int code = (int) Compress.CLEAR;
            int rOff = offset, bits = bitsNumber;
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
                buf[bp] = (byte) ((buf[bp] & Compress.rmask[rOff]) | (code << rOff)
                        & Compress.lmask[rOff]);
                bp++;
                bits -= 8 - rOff;
                code >>= 8 - rOff;
                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                if (bits >= 8) {
                    buf[bp++] = (byte) code;
                    code >>= 8;
                    bits -= 8;
                }
                /* Last bits. */
                if (bits != 0) {
                    buf[bp] = (byte) code;
                }
                offset += bitsNumber;
                if (offset == (bitsNumber << 3)) {
                    bp = 0;
                    bits = bitsNumber;
                    bytesOut += bits;
                    do {
                        byte c = buf[bp++];
                        output.buffer[output.cnt++] = c;
                    } while (--bits != 0);
                    offset = 0;
                }
                /*
                 * If the next entry is going to be too big for the code size, then
                 * increase it, if possible.
                 */
                if (freeEntry > maxCode || clearFlag > 0) {
                    /*
                     * Write the whole buffer, because the input side won't discover
                     * the size increase until after it has read it.
                     */
                    if (offset > 0) {
                        for (int i = 0; i < bitsNumber; i++) {
                            output.buffer[output.cnt++] = buf[i];
                        }
                        bytesOut += bitsNumber;
                    }
                    offset = 0;
                    if (clearFlag != 0) {
                        bitsNumber = Compress.INIT_BITS;
                        maxCode = ((1 << (bitsNumber)) - 1);
                        clearFlag = 0;
                    } else {
                        bitsNumber++;
                        if (bitsNumber == maxBits) {
                            maxCode = maxMaxCode;
                        } else {
                            maxCode = ((1 << (bitsNumber)) - 1);
                        }
                    }
                }
            } else {
                /*
                 * At EOF, write the rest of the buffer.
                 */
                if (offset > 0) {
                    for (int i = 0; i < ((offset + 7) / 8); i++) {
                        output.buffer[output.cnt++] = buf[i];
                    }
                }
                bytesOut += (offset + 7) / 8;
                offset = 0;
            }
        }
    }

    static final class HashTable {
        /*
         * Use protected instead of public to allow access by parent class of inner
         * class. wnb 4/17/98
         */
        protected int tab[];
        protected int size;

        public HashTable() {
            size = Compress.HSIZE;
            tab = new int[size];
        }

        public int of(int i) {
            return tab[i];
        }

        public void set(int i, int v) {
            tab[i] = v;
        }

        public int hsize() {
            return size;
        }

        public void clear() {
            for (int i = 0; i < size; i++) {
                tab[i] = -1;
            }
        }
    }
}

/*
 * Decompress stdin to stdout. This routine adapts to the codes in the file
 * building the "string" table on-the-fly; requiring no table to be stored in
 * the compressed file. The tables used herein are shared with those of the
 * compress() routine. See the definitions above.
 */
final class Decompressor extends CompBase {
    int size;
    CodeTable tabPrefix;
    SuffixTable tabSuffix;
    DeStack deStack;

    public Decompressor(InputBuffer in, OutputBuffer out) {
        super(in, out);
        /* Check the magic number */
        if ((((input.cnt-- > 0 ? (input.buffer[input.current++] & 0x00FF) : -1) & 0xFF) != (Compress.magic_header[0]
                & 0xFF))
                || (((input.cnt-- > 0 ? (input.buffer[input.current++] & 0x00FF) : -1)
                        & 0xFF) != (Compress.magic_header[1] & 0xFF))) {
            System.err.println("stdin: not in compressed format");
        }
        maxBits = input.cnt-- > 0 ? (input.buffer[input.current++] & 0x00FF) : -1; /* set -b from file */
        blockCompress = maxBits & Compress.BLOCK_MASK;
        maxBits &= Compress.BIT_MASK;
        maxMaxCode = 1 << maxBits;
        if (maxBits > Compress.BITS) {
            System.err.println("stdin: compressed with " + maxBits
                    + " bits, can only handle " + Compress.BITS + " bits");
        }
        bitsNumber = Compress.INIT_BITS;
        maxCode = ((1 << (bitsNumber)) - 1);
        offset = 0;
        size = 0;
        clearFlag = 0;
        freeEntry = ((blockCompress != 0) ? Compress.FIRST : 256);
        tabPrefix = new CodeTable();
        tabSuffix = new SuffixTable();
        deStack = new DeStack();
        /*
         * As above, initialize the first 256 entries in the table.
         */
        for (int code = 0; code < 256; code++) {
            tabPrefix.tab[code] = 0;
        }
        for (int code = 0; code < 256; code++) {
            tabSuffix.tab[code] = (byte) code;
        }
    }

    public void decompress() {
        int code, oldcode, incode;
        int result2 = 0;
        int code3 = 0;
        int rOff2 = 0, bits2 = 0;
        int bp2 = 0;
        if (clearFlag > 0 || offset >= size || freeEntry > maxCode) {
            /*
             * If the next entry will be too big for the current code size, then
             * we must increase the size. This implies reading a new buffer
             * full, too.
             */
            if (freeEntry > maxCode) {
                bitsNumber++;
                if (bitsNumber == maxBits) {
                    maxCode = maxMaxCode; /* won't get any bigger now */
                } else {
                    maxCode = ((1 << (bitsNumber)) - 1);
                }
            }
            if (clearFlag > 0) {
                bitsNumber = Compress.INIT_BITS;
                maxCode = ((1 << (bitsNumber)) - 1);
                clearFlag = 0;
            }
            int result;
            if (input.cnt <= 0) {
                result = -1;
            } else {
                int num = Math.min(bitsNumber, input.cnt);
                for (int i = 0; i < num; i++) {
                    buf[i] = input.buffer[input.current++];
                    input.cnt--;
                }
                result = num;
            }
            size = result;
            if (size <= 0) {
                result2 = -1;/* end of file */
            } else {
                offset = 0;/* Round size down to integral number of codes */
                size = (size << 3) - (bitsNumber - 1);
            }
        }
        if (result2 == 0) {
            rOff2 = offset;
            bits2 = bitsNumber;
            /*
             * Get to the first byte.
             */
            bp2 += rOff2 >> 3;
            rOff2 &= 7;
            /* Get first part (low order bits) */
            code3 = ((buf[bp2++] >> rOff2) & Compress.rmask[8 - rOff2]) & 0xff;
            bits2 -= 8 - rOff2;
            rOff2 = 8 - rOff2; /* now, offset into code word */
            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
            if (bits2 >= 8) {
                code3 |= (buf[bp2++] & 0xff) << rOff2;
                rOff2 += 8;
                bits2 -= 8;
            }
            /* high order bits. */
            if (bits2 > 0) {
                code3 |= (buf[bp2] & Compress.rmask[bits2]) << rOff2;
            }
            offset += bitsNumber;
            result2 = code3;
        }
        int finchar = oldcode = result2;
        if (oldcode == -1) {/* EOF already? */
            return; /* Get out of here */
        }
        /* first code must be 8 bits = byte */
        output.buffer[output.cnt++] = (byte) finchar;
        while (true) {
            int result1 = 0;
            int code2 = 0;
            int rOff1 = 0, bits1 = 0;
            int bp1 = 0;
            if (clearFlag > 0 || offset >= size || freeEntry > maxCode) {
                /*
                 * If the next entry will be too big for the current code size, then
                 * we must increase the size. This implies reading a new buffer
                 * full, too.
                 */
                if (freeEntry > maxCode) {
                    bitsNumber++;
                    if (bitsNumber == maxBits) {
                        maxCode = maxMaxCode; /* won't get any bigger now */
                    } else {
                        maxCode = ((1 << (bitsNumber)) - 1);
                    }
                }
                if (clearFlag > 0) {
                    bitsNumber = Compress.INIT_BITS;
                    maxCode = ((1 << (bitsNumber)) - 1);
                    clearFlag = 0;
                }
                int result;
                if (input.cnt <= 0) {
                    result = -1;
                } else {
                    int num = Math.min(bitsNumber, input.cnt);
                    for (int i = 0; i < num; i++) {
                        buf[i] = input.buffer[input.current++];
                        input.cnt--;
                    }
                    result = num;
                }
                size = result;
                if (size <= 0) {
                    result1 = -1;/* end of file */
                } else {
                    offset = 0;/* Round size down to integral number of codes */
                    size = (size << 3) - (bitsNumber - 1);
                }
            }
            if (result1 == 0) {
                rOff1 = offset;
                bits1 = bitsNumber;
                /*
                 * Get to the first byte.
                 */
                bp1 += rOff1 >> 3;
                rOff1 &= 7;
                /* Get first part (low order bits) */
                code2 = ((buf[bp1++] >> rOff1) & Compress.rmask[8 - rOff1]) & 0xff;
                bits1 -= 8 - rOff1;
                rOff1 = 8 - rOff1; /* now, offset into code word */
                /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                if (bits1 >= 8) {
                    code2 |= (buf[bp1++] & 0xff) << rOff1;
                    rOff1 += 8;
                    bits1 -= 8;
                }
                /* high order bits. */
                if (bits1 > 0) {
                    code2 |= (buf[bp1] & Compress.rmask[bits1]) << rOff1;
                }
                offset += bitsNumber;
                result1 = code2;
            }
            if (!((code = result1) > -1))
                break;
            if ((code == Compress.CLEAR) && (blockCompress != 0)) {
                for (int code4 = 0; code4 < 256; code4++) {
                    tabPrefix.tab[code4] = 0;
                }
                clearFlag = 1;
                freeEntry = Compress.FIRST - 1;
                int result = 0;
                int code1 = 0;
                int rOff = 0, bits = 0;
                int bp = 0;
                if (clearFlag > 0 || offset >= size || freeEntry > maxCode) {
                    /*
                     * If the next entry will be too big for the current code size, then
                     * we must increase the size. This implies reading a new buffer
                     * full, too.
                     */
                    if (freeEntry > maxCode) {
                        bitsNumber++;
                        if (bitsNumber == maxBits) {
                            maxCode = maxMaxCode; /* won't get any bigger now */
                        } else {
                            maxCode = ((1 << (bitsNumber)) - 1);
                        }
                    }
                    if (clearFlag > 0) {
                        bitsNumber = Compress.INIT_BITS;
                        maxCode = ((1 << (bitsNumber)) - 1);
                        clearFlag = 0;
                    }
                    int result3;
                    if (input.cnt <= 0) {
                        result3 = -1;
                    } else {
                        int num = Math.min(bitsNumber, input.cnt);
                        for (int i = 0; i < num; i++) {
                            buf[i] = input.buffer[input.current++];
                            input.cnt--;
                        }
                        result3 = num;
                    }
                    size = result3;
                    if (size <= 0) {
                        result = -1;/* end of file */
                    } else {
                        offset = 0;/* Round size down to integral number of codes */
                        size = (size << 3) - (bitsNumber - 1);
                    }
                }
                if (result == 0) {
                    rOff = offset;
                    bits = bitsNumber;
                    /*
                     * Get to the first byte.
                     */
                    bp += rOff >> 3;
                    rOff &= 7;
                    /* Get first part (low order bits) */
                    code1 = ((buf[bp++] >> rOff) & Compress.rmask[8 - rOff]) & 0xff;
                    bits -= 8 - rOff;
                    rOff = 8 - rOff; /* now, offset into code word */
                    /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
                    if (bits >= 8) {
                        code1 |= (buf[bp++] & 0xff) << rOff;
                        rOff += 8;
                        bits -= 8;
                    }
                    /* high order bits. */
                    if (bits > 0) {
                        code1 |= (buf[bp] & Compress.rmask[bits]) << rOff;
                    }
                    offset += bitsNumber;
                    result = code1;
                }
                if ((code = result) == -1) /* O, untimely death! */
                    break;
            }
            incode = code;
            /*
             * Special case for KwKwK string.
             */
            if (code >= freeEntry) {
                deStack.tab[deStack.index++] = (byte) finchar;
                code = oldcode;
            }
            /*
             * Generate output characters in reverse order
             */
            while (code >= 256) {
                deStack.tab[deStack.index++] = tabSuffix.tab[code];
                code = (int) tabPrefix.tab[code] << 16 >>> 16;
            }
            byte c1 = (byte) (finchar = tabSuffix.tab[code]);
            deStack.tab[deStack.index++] = c1;
            /*
             * And put them out in forward order
             */
            do {
                byte c = deStack.tab[--deStack.index];
                output.buffer[output.cnt++] = c;
            } while (!(deStack.index == 0));
            /*
             * Generate the new entry.
             */
            if ((code = freeEntry) < maxMaxCode) {
                tabPrefix.tab[code] = (short) oldcode;
                tabSuffix.tab[code] = (byte) finchar;
                freeEntry = code + 1;
            }
            /*
             * Remember previous code.
             */
            oldcode = incode;
        }
    }

    /*
     * Read one code from the standard input. If EOF, return -1. Inputs: stdin
     * Outputs: code or -1 is returned.
     */
    public int getCode() {
        int code;
        int rOff, bits;
        int bp = 0;
        if (clearFlag > 0 || offset >= size || freeEntry > maxCode) {
            /*
             * If the next entry will be too big for the current code size, then
             * we must increase the size. This implies reading a new buffer
             * full, too.
             */
            if (freeEntry > maxCode) {
                bitsNumber++;
                if (bitsNumber == maxBits) {
                    maxCode = maxMaxCode; /* won't get any bigger now */
                } else {
                    maxCode = ((1 << (bitsNumber)) - 1);
                }
            }
            if (clearFlag > 0) {
                bitsNumber = Compress.INIT_BITS;
                maxCode = ((1 << (bitsNumber)) - 1);
                clearFlag = 0;
            }
            int result;
            if (input.cnt <= 0) {
                result = -1;
            } else {
                int num = Math.min(bitsNumber, input.cnt);
                for (int i = 0; i < num; i++) {
                    buf[i] = input.buffer[input.current++];
                    input.cnt--;
                }
                result = num;
            }
            size = result;
            if (size <= 0) {
                return -1; /* end of file */
            }
            offset = 0;
            /* Round size down to integral number of codes */
            size = (size << 3) - (bitsNumber - 1);
        }
        rOff = offset;
        bits = bitsNumber;
        /*
         * Get to the first byte.
         */
        bp += rOff >> 3;
        rOff &= 7;
        /* Get first part (low order bits) */
        code = ((buf[bp++] >> rOff) & Compress.rmask[8 - rOff]) & 0xff;
        bits -= 8 - rOff;
        rOff = 8 - rOff; /* now, offset into code word */
        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
        if (bits >= 8) {
            code |= (buf[bp++] & 0xff) << rOff;
            rOff += 8;
            bits -= 8;
        }
        /* high order bits. */
        if (bits > 0) {
            code |= (buf[bp] & Compress.rmask[bits]) << rOff;
        }
        offset += bitsNumber;
        return code;
    }

    static final class DeStack {
        /*
         * Use protected instead of public to allow access by parent class of inner
         * class. wnb 4/17/98
         */
        protected byte tab[];
        protected int index;

        public DeStack() {
            tab = new byte[Compress.STACK_SZ];
        }

        public void push(byte c) {
            tab[index++] = c;
        }

        public byte pop() {
            return tab[--index];
        }

        public boolean isEmpty() {
            return index == 0;
        }
    }

    static final class SuffixTable {
        protected byte tab[];

        public SuffixTable() {
            tab = new byte[Compress.SUFFIX_TAB_SZ];
        }

        public byte of(int i) {
            return tab[i];
        }

        public void set(int i, byte v) {
            tab[i] = v;
        }

        public void init(int size) {
            for (int code = 0; code < size; code++) {
                tab[code] = (byte) code;
            }
        }
    }
}
