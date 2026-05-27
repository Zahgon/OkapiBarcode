/*
 * Copyright 2014 Robin Stuart
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import uk.org.okapibarcode.graphics.Rectangle;

/**
 * <p>Implements Code 16K symbology according to BS EN 12323:2005.
 *
 * <p>Encodes using a stacked symbology based on Code 128. Supports encoding
 * of any 8-bit ISO 8859-1 (Latin-1) data with a maximum data capacity of 77
 * alpha-numeric characters or 154 numerical digits.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Code16k extends Symbol {

    private enum Mode {

        NULL,
        SHIFTA,
        LATCHA,
        SHIFTB,
        LATCHB,
        SHIFTC,
        LATCHC,
        AORB,
        ABORC,
        CANDB,
        CANDBB
    }

    /* EN 12323 Table 1 - "Code 16K" character encodations */
    private static final String[] C16K_TABLE = { "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213", "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132", "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211", "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313", "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331", "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111", "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214", "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111", "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141", "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141", "114131", "311141", "411131", "211412", "211214", "211232", "211133" };

    /* EN 12323 Table 3 and Table 4 - Start patterns and stop patterns */
    private static final String[] C16K_START_STOP = { "3211", "2221", "2122", "1411", "1132", "1231", "1114", "3112" };

    /* EN 12323 Table 5 - Start and stop values defining row numbers */
    private static final int[] C16K_START_VALUES = { 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 5, 6, 7 };

    private static final int[] C16K_STOP_VALUES = { 0, 1, 2, 3, 4, 5, 6, 7, 4, 5, 6, 7, 0, 1, 2, 3 };

    private Mode[] block_mode = new Mode[170];

    /* RENAME block_mode */
    private int[] block_length = new int[170];

    /* RENAME block_length */
    private int block_count;

    /**
     * Creates a new instance.
     */
    public Code16k() {
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void getValueSubsetA(int source, int[] values, int bar_chars) {
        if (source > 127) {
            if (source < 160) {
                values[bar_chars] = source + 64 - 128;
            } else {
                values[bar_chars] = source - 32 - 128;
            }
        } else {
            if (source < 32) {
                values[bar_chars] = source + 64;
            } else {
                values[bar_chars] = source - 32;
            }
        }
    }

    private void getValueSubsetB(int source, int[] values, int bar_chars) {
        if (source > 127) {
            values[bar_chars] = source - 32 - 128;
        } else {
            values[bar_chars] = source - 32;
        }
    }

    private void getValueSubsetC(int source_a, int source_b, int[] values, int bar_chars) {
        int weight;
        weight = (10 * Character.getNumericValue(source_a)) + Character.getNumericValue(source_b);
        values[bar_chars] = weight;
    }

    private Mode findSubset(int letter) {
        Mode mode;
        if (letter <= 31) {
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
        return mode;
    }

    private void reduceSubsetChanges(int block_count) {
        /* Implements rules from ISO 15417 Annex E */
        int i, length;
        Mode current, last, next;
        for (i = 0; i < block_count; i++) {
            current = block_mode[i];
            length = block_length[i];
            if (i != 0) {
                last = block_mode[i - 1];
            } else {
                last = Mode.NULL;
            }
            if (i != block_count - 1) {
                next = block_mode[i + 1];
            } else {
                next = Mode.NULL;
            }
            if (i == 0) {
                /* first block */
                if ((block_count == 1) && ((length == 2) && (current == Mode.ABORC))) {
                    /* Rule 1a */
                    block_mode[i] = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    if (length >= 4) {
                        /* Rule 1b */
                        block_mode[i] = Mode.LATCHC;
                    } else {
                        block_mode[i] = Mode.AORB;
                        current = Mode.AORB;
                    }
                }
                if (current == Mode.SHIFTA) {
                    /* Rule 1c */
                    block_mode[i] = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTA)) {
                    /* Rule 1c */
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if (current == Mode.AORB) {
                    /* Rule 1d */
                    block_mode[i] = Mode.LATCHB;
                }
            } else {
                if ((current == Mode.ABORC) && (length >= 4)) {
                    /* Rule 3 */
                    block_mode[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    block_mode[i] = Mode.AORB;
                    current = Mode.AORB;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHA)) {
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHB)) {
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTA)) {
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTB)) {
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if (current == Mode.AORB) {
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (length > 1)) {
                    /* Rule 4 */
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (length > 1)) {
                    /* Rule 5 */
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHA)) {
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHB)) {
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHC)) {
                    block_mode[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHC)) {
                    block_mode[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
            }
            /* Rule 2 is implimented elsewhere, Rule 6 is implied */
        }
        combineSubsetBlocks(block_count);
    }

    private void combineSubsetBlocks(int block_count) {
        int i, j;
        /* bring together same type blocks */
        if (block_count > 1) {
            i = 1;
            while (i < block_count) {
                if (block_mode[i - 1] == block_mode[i]) {
                    /* bring together */
                    block_length[i - 1] = block_length[i - 1] + block_length[i];
                    j = i + 1;
                    /* decreace the list */
                    while (j < block_count) {
                        block_length[j - 1] = block_length[j];
                        block_mode[j - 1] = block_mode[j];
                        j++;
                    }
                    block_count = block_count - 1;
                    i--;
                }
                i++;
            }
        }
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
