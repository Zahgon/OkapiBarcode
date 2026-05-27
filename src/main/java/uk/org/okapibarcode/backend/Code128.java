/*
 * Copyright 2014-2018 Robin Stuart, Daniel Gredler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.okapibarcode.backend;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * <p>Implements Code 128 bar code symbology according to ISO/IEC 15417:2007.
 *
 * <p>Code 128 supports encoding of 8-bit ISO 8859-1 (Latin-1) characters.
 *
 * <p>Setting GS1 mode allows encoding in GS1-128 (also known as UCC/EAN-128).
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 * @author Daniel Gredler
 */
public class Code128 extends Symbol {

    /**
     * Code sets and code set combinations which the user may require the symbol to use.
     */
    public enum CodeSet {

        /**
         * Code set A, which can encode ASCII values 0-95, as well as FNC1, FNC2, FNC3 and FNC4.
         */
        A,
        /**
         * Code set B, which can encode ASCII values 32-127, as well as FNC1, FNC2, FNC3 and FNC4.
         */
        B,
        /**
         * Code set C, which can encode pairs of numbers, as well as FNC1.
         */
        C,
        /**
         * Code sets A and B only (suppress code set C).
         */
        AB,
        /**
         * No code set restrictions (code sets A, B and C are all allowed).
         */
        ABC
    }

    private enum Mode {

        NULL,
        SHIFTA,
        LATCHA,
        SHIFTB,
        LATCHB,
        SHIFTC,
        LATCHC,
        AORB,
        ABORC
    }

    private enum FMode {

        SHIFTN, LATCHN, SHIFTF, LATCHF
    }

    private enum Composite {

        OFF, CCA, CCB, CCC
    }

    protected static final String[] CODE128_TABLE = { "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213", "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132", "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211", "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313", "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331", "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111", "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214", "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111", "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141", "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141", "114131", "311141", "411131", "211412", "211214", "211232", "2331112" };

    private CodeSet codeSet;

    private Composite compositeMode = Composite.OFF;

    /**
     * Creates a new instance.
     */
    public Code128() {
        this(CodeSet.ABC);
    }

    /**
     * <p>Creates a new instance, using the specified code set restrictions.
     *
     * <p><b>NOTE:</b> Unless your application has very specific encoding requirements, it is recommended that no
     * custom code set restrictions are used, allowing the system to fully optimize the encoded data.
     *
     * @param codeSet the code set restrictions to use
     */
    public Code128(CodeSet codeSet) {
        this.codeSet = codeSet;
    }

    /**
     * <p>Sets the code set restrictions. The default value is {@link CodeSet#ABC}, which allows the use of any code set.
     *
     * <p><b>NOTE:</b> Unless your application has very specific encoding requirements, it is recommended that no
     * custom code set restrictions are used, allowing the system to fully optimize the encoded data.
     *
     * @param codeSet the code set restrictions to use
     */
    public void setCodeSet(CodeSet codeSet) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the code set restrictions. The default value is {@link CodeSet#ABC}, which allows the use of any code set.
     *
     * @return the code set restrictions used
     */
    public CodeSet getCodeSet() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void setCca() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void setCcb() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void setCcc() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    public void unsetCc() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected boolean supportsFnc2() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected boolean supportsFnc3() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected boolean supportsFnc4() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static String removeFncEscapeSequences(String s) {
        return s.replace(FNC1_STRING, "").replace(FNC2_STRING, "").replace(FNC3_STRING, "").replace(FNC4_STRING, "");
    }

    private void resolveOddCs(Mode[] set, int i, int cs, int nums, int fncs) {
        if ((nums & 1) != 0) {
            int index;
            Mode m;
            if (codeSet == CodeSet.C) {
                // User wants to force the use of code set C only, but it's not possible
                throw new OkapiInputException("Unable to encode the specified data using only code set C");
            }
            if (i - cs == 0 || fncs > 0) {
                // Rule 2: first block -> swap last digit to A or B
                index = i - 1;
                if (index + 1 < set.length && set[index + 1] != null && set[index + 1] != Mode.LATCHC) {
                    // next block is either A or B -- match it
                    m = set[index + 1];
                } else {
                    // next block is C, or there is no next block -- just latch to B
                    m = Mode.LATCHB;
                }
            } else {
                // Rule 3b: subsequent block -> swap first digit to A or B
                // Note that we make an exception for C blocks which contain one (or more) FNC1 characters,
                // since swapping the first digit would place the FNC1 in an invalid position in the block
                index = i - nums;
                if (index - 1 >= 0 && set[index - 1] != null && set[index - 1] != Mode.LATCHC) {
                    // previous block is either A or B -- match it
                    m = set[index - 1];
                } else {
                    // previous block is C, or there is no previous block -- just latch to B
                    m = Mode.LATCHB;
                }
            }
            set[index] = m;
        }
    }

    private Mode findSubset(int letter, int numbers) {
        Mode mode;
        if (letter == FNC1) {
            if (numbers % 2 == 0) {
                /* ISO 15417 Annex E Note 2 */
                /* FNC1 may use subset C, so long as it doesn't break data into an odd number of digits */
                mode = Mode.ABORC;
            } else {
                mode = Mode.AORB;
            }
        } else if (letter == FNC2 || letter == FNC3 || letter == FNC4) {
            mode = Mode.AORB;
        } else if (letter <= 31) {
            mode = Mode.SHIFTA;
        } else if ((letter >= 48) && (letter <= 57)) {
            mode = Mode.ABORC;
        } else if (letter <= 95) {
            mode = Mode.AORB;
        } else if (letter <= 127) {
            mode = Mode.SHIFTB;
        } else if (letter <= 159) {
            mode = Mode.SHIFTA;
        } else if (letter <= 223) {
            mode = Mode.AORB;
        } else {
            mode = Mode.SHIFTB;
        }
        // if the user wishes to force the use of certain code sets, take that into account
        if (codeSet == CodeSet.A) {
            if (mode == Mode.ABORC || mode == Mode.AORB || mode == Mode.SHIFTA) {
                mode = Mode.SHIFTA;
            } else {
                throw new OkapiInputException("Unable to encode the specified data using only code set A");
            }
        } else if (codeSet == CodeSet.B) {
            if (mode == Mode.ABORC || mode == Mode.AORB || mode == Mode.SHIFTB) {
                mode = Mode.SHIFTB;
            } else {
                throw new OkapiInputException("Unable to encode the specified data using only code set B");
            }
        } else if (codeSet == CodeSet.C) {
            if (mode == Mode.ABORC) {
                mode = Mode.SHIFTC;
            } else {
                throw new OkapiInputException("Unable to encode the specified data using only code set C");
            }
        } else if (codeSet == CodeSet.AB) {
            if (mode == Mode.ABORC) {
                mode = Mode.AORB;
            }
        }
        return mode;
    }

    private int length(int letter, Mode mode) {
        if (letter == FNC1 && mode == Mode.ABORC) {
            /* ISO 15417 Annex E Note 2 */
            /* Logical length used for making subset switching decisions, not actual length */
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Implements rules from ISO 15417 Annex E. Returns the updated index point.
     */
    private int reduceSubsetChanges(Mode[] mode_type, int[] mode_length, int index_point) {
        int totalLength = 0;
        int length;
        Mode current, last, next, nextShift;
        for (int i = 0; i < index_point; i++) {
            current = mode_type[i];
            length = mode_length[i];
            if (i != 0) {
                last = mode_type[i - 1];
            } else {
                last = Mode.NULL;
            }
            if (i != index_point - 1) {
                next = mode_type[i + 1];
            } else {
                next = Mode.NULL;
            }
            /* The next shift mode is the location of the next fully-known code set. */
            /* Everything between here and there will be either A/B/C or A/B. */
            nextShift = Mode.NULL;
            for (int j = i + 1; j < index_point; j++) {
                if (mode_type[j] == Mode.SHIFTA || mode_type[j] == Mode.SHIFTB || mode_type[j] == Mode.SHIFTC) {
                    nextShift = mode_type[j];
                    break;
                }
            }
            /* ISO 15417 Annex E Note 2 */
            /* Calculate difference between logical length and actual length in this block */
            int extraLength = 0;
            for (int j = 0; j < length - extraLength; j++) {
                if (length(inputData[totalLength + j], current) == 2) {
                    extraLength++;
                }
            }
            if (i == 0) {
                /* first block */
                if ((index_point == 1) && ((length == 2) && (current == Mode.ABORC))) {
                    /* Rule 1a */
                    mode_type[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    if (length >= 4) {
                        /* Rule 1b */
                        mode_type[i] = Mode.LATCHC;
                        current = Mode.LATCHC;
                    } else {
                        mode_type[i] = Mode.AORB;
                        current = Mode.AORB;
                    }
                }
                if (current == Mode.SHIFTA) {
                    /* Rule 1c */
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (nextShift == Mode.SHIFTA)) {
                    /* Rule 1c */
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if (current == Mode.AORB) {
                    /* Rule 1d */
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if (current == Mode.SHIFTC) {
                    /* user forced use of code set C */
                    mode_type[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
            } else {
                if ((current == Mode.ABORC) && (length >= 4)) {
                    /* Rule 3 */
                    mode_type[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    mode_type[i] = Mode.AORB;
                    current = Mode.AORB;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHA)) {
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHB)) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.AORB) && (nextShift == Mode.SHIFTA)) {
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (nextShift == Mode.SHIFTB)) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if (current == Mode.AORB) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (length > 1)) {
                    /* Rule 4 */
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (length > 1)) {
                    /* Rule 5 */
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHA)) {
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHB)) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (next == Mode.AORB)) {
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (next == Mode.AORB)) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHC)) {
                    mode_type[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHC)) {
                    mode_type[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
            }
            /* Rule 2 is implemented elsewhere, Rule 6 is implied */
            /* ISO 15417 Annex E Note 2 */
            /* Convert logical length back to actual length for this block, now that we've decided on a subset */
            mode_length[i] -= extraLength;
            totalLength += mode_length[i];
        }
        return combineSubsetBlocks(mode_type, mode_length, index_point);
    }

    /**
     * Modifies the specified mode and length arrays to combine adjacent modes of the same type, returning the updated index point.
     */
    private int combineSubsetBlocks(Mode[] mode_type, int[] mode_length, int index_point) {
        /* bring together same type blocks */
        if (index_point > 1) {
            for (int i = 1; i < index_point; i++) {
                if (mode_type[i - 1] == mode_type[i]) {
                    /* bring together */
                    mode_length[i - 1] = mode_length[i - 1] + mode_length[i];
                    /* decrease the list */
                    for (int j = i + 1; j < index_point; j++) {
                        mode_length[j - 1] = mode_length[j];
                        mode_type[j - 1] = mode_type[j];
                    }
                    index_point--;
                    i--;
                }
            }
        }
        return index_point;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getCodewords() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
