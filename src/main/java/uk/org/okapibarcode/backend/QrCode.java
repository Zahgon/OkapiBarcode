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

import uk.org.okapibarcode.util.EciMode;
import static uk.org.okapibarcode.util.Arrays.positionOf;
import static uk.org.okapibarcode.util.Strings.binaryAppend;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Implements QR Code bar code symbology According to ISO/IEC 18004:2015.
 *
 * <p>The maximum capacity of a (version 40) QR Code symbol is 7089 numeric digits,
 * 4296 alphanumeric characters or 2953 bytes of data. QR Code symbols can also
 * be used to encode GS1 data. QR Code symbols can encode characters in the
 * Latin-1 set and Kanji characters which are members of the Shift-JIS encoding
 * scheme.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class QrCode extends Symbol {

    /**
     * The different QR Code error correction levels.
     */
    public enum EccLevel {

        /**
         * Low error correction level. Appropriate for symbols that are high-quality or require smallest possible size.
         */
        L,
        /**
         * Medium or "standard" error correction level. Offers a good compromise between symbol size and reliability.
         */
        M,
        /**
         * High error correction level. Suitable for critically-important symbols or applications with low print quality.
         */
        Q,
        /**
         * Highest error correction level. Provides the maximum achievable reliability at the cost of much larger symbols.
         */
        H
    }

    private enum QrMode {

        NULL, KANJI, BINARY, ALPHANUM, NUMERIC
    }

    /* Table 5 - Encoding/Decoding table for Alphanumeric mode */
    private static final char[] RHODIUM = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '$', '%', '*', '+', '-', '.', '/', ':' };

    private static final int[] QR_DATA_CODEWORDS_L = { 19, 34, 55, 80, 108, 136, 156, 194, 232, 274, 324, 370, 428, 461, 523, 589, 647, 721, 795, 861, 932, 1006, 1094, 1174, 1276, 1370, 1468, 1531, 1631, 1735, 1843, 1955, 2071, 2191, 2306, 2434, 2566, 2702, 2812, 2956 };

    private static final int[] QR_DATA_CODEWORDS_M = { 16, 28, 44, 64, 86, 108, 124, 154, 182, 216, 254, 290, 334, 365, 415, 453, 507, 563, 627, 669, 714, 782, 860, 914, 1000, 1062, 1128, 1193, 1267, 1373, 1455, 1541, 1631, 1725, 1812, 1914, 1992, 2102, 2216, 2334 };

    private static final int[] QR_DATA_CODEWORDS_Q = { 13, 22, 34, 48, 62, 76, 88, 110, 132, 154, 180, 206, 244, 261, 295, 325, 367, 397, 445, 485, 512, 568, 614, 664, 718, 754, 808, 871, 911, 985, 1033, 1115, 1171, 1231, 1286, 1354, 1426, 1502, 1582, 1666 };

    private static final int[] QR_DATA_CODEWORDS_H = { 9, 16, 26, 36, 46, 60, 66, 86, 100, 122, 140, 158, 180, 197, 223, 253, 283, 313, 341, 385, 406, 442, 464, 514, 538, 596, 628, 661, 701, 745, 793, 845, 901, 961, 986, 1054, 1096, 1142, 1222, 1276 };

    private static final int[] QR_BLOCKS_L = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 4, 6, 6, 6, 6, 7, 8, 8, 9, 9, 10, 12, 12, 12, 13, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 24, 25 };

    private static final int[] QR_BLOCKS_M = { 1, 1, 1, 2, 2, 4, 4, 4, 5, 5, 5, 8, 9, 9, 10, 10, 11, 13, 14, 16, 17, 17, 18, 20, 21, 23, 25, 26, 28, 29, 31, 33, 35, 37, 38, 40, 43, 45, 47, 49 };

    private static final int[] QR_BLOCKS_Q = { 1, 1, 2, 2, 4, 4, 6, 6, 8, 8, 8, 10, 12, 16, 12, 17, 16, 18, 21, 20, 23, 23, 25, 27, 29, 34, 34, 35, 38, 40, 43, 45, 48, 51, 53, 56, 59, 62, 65, 68 };

    private static final int[] QR_BLOCKS_H = { 1, 1, 2, 4, 4, 4, 5, 6, 8, 8, 11, 11, 16, 16, 18, 16, 19, 21, 25, 25, 25, 34, 30, 32, 35, 37, 40, 42, 45, 48, 51, 54, 57, 60, 63, 66, 70, 74, 77, 81 };

    private static final int[] QR_TOTAL_CODEWORDS = { 26, 44, 70, 100, 134, 172, 196, 242, 292, 346, 404, 466, 532, 581, 655, 733, 815, 901, 991, 1085, 1156, 1258, 1364, 1474, 1588, 1706, 1828, 1921, 2051, 2185, 2323, 2465, 2611, 2761, 2876, 3034, 3196, 3362, 3532, 3706 };

    private static final int[] QR_SIZES = { 21, 25, 29, 33, 37, 41, 45, 49, 53, 57, 61, 65, 69, 73, 77, 81, 85, 89, 93, 97, 101, 105, 109, 113, 117, 121, 125, 129, 133, 137, 141, 145, 149, 153, 157, 161, 165, 169, 173, 177 };

    private static final int[] QR_ALIGN_LOOPSIZE = { 0, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7 };

    private static final int[] QR_TABLE_E1 = { 6, 18, 0, 0, 0, 0, 0, 6, 22, 0, 0, 0, 0, 0, 6, 26, 0, 0, 0, 0, 0, 6, 30, 0, 0, 0, 0, 0, 6, 34, 0, 0, 0, 0, 0, 6, 22, 38, 0, 0, 0, 0, 6, 24, 42, 0, 0, 0, 0, 6, 26, 46, 0, 0, 0, 0, 6, 28, 50, 0, 0, 0, 0, 6, 30, 54, 0, 0, 0, 0, 6, 32, 58, 0, 0, 0, 0, 6, 34, 62, 0, 0, 0, 0, 6, 26, 46, 66, 0, 0, 0, 6, 26, 48, 70, 0, 0, 0, 6, 26, 50, 74, 0, 0, 0, 6, 30, 54, 78, 0, 0, 0, 6, 30, 56, 82, 0, 0, 0, 6, 30, 58, 86, 0, 0, 0, 6, 34, 62, 90, 0, 0, 0, 6, 28, 50, 72, 94, 0, 0, 6, 26, 50, 74, 98, 0, 0, 6, 30, 54, 78, 102, 0, 0, 6, 28, 54, 80, 106, 0, 0, 6, 32, 58, 84, 110, 0, 0, 6, 30, 58, 86, 114, 0, 0, 6, 34, 62, 90, 118, 0, 0, 6, 26, 50, 74, 98, 122, 0, 6, 30, 54, 78, 102, 126, 0, 6, 26, 52, 78, 104, 130, 0, 6, 30, 56, 82, 108, 134, 0, 6, 34, 60, 86, 112, 138, 0, 6, 30, 58, 86, 114, 142, 0, 6, 34, 62, 90, 118, 146, 0, 6, 30, 54, 78, 102, 126, 150, 6, 24, 50, 76, 102, 128, 154, 6, 28, 54, 80, 106, 132, 158, 6, 32, 58, 84, 110, 136, 162, 6, 26, 54, 82, 110, 138, 166, 6, 30, 58, 86, 114, 142, 170 };

    private static final int[] QR_ANNEX_C = { /* Format information bit sequences */
    0x5412, 0x5125, 0x5e7c, 0x5b4b, 0x45f9, 0x40ce, 0x4f97, 0x4aa0, 0x77c4, 0x72f3, 0x7daa, 0x789d, 0x662f, 0x6318, 0x6c41, 0x6976, 0x1689, 0x13be, 0x1ce7, 0x19d0, 0x0762, 0x0255, 0x0d0c, 0x083b, 0x355f, 0x3068, 0x3f31, 0x3a06, 0x24b4, 0x2183, 0x2eda, 0x2bed };

    private static final int[] QR_ANNEX_D = { /* Version information bit sequences */
    0x07c94, 0x085bc, 0x09a99, 0x0a4d3, 0x0bbf6, 0x0c762, 0x0d847, 0x0e60d, 0x0f928, 0x10b78, 0x1145d, 0x12a17, 0x13532, 0x149a6, 0x15683, 0x168c9, 0x177ec, 0x18ec4, 0x191e1, 0x1afab, 0x1b08e, 0x1cc1a, 0x1d33f, 0x1ed75, 0x1f250, 0x209d5, 0x216f0, 0x228ba, 0x2379f, 0x24b0b, 0x2542e, 0x26a64, 0x27541, 0x28c69 };

    /* The max number of symbols in a structured append sequence. */
    private static final int MAX_STRUCTURED_APPEND_SYMBOLS = 16;

    protected int minVersion = 1;

    protected int preferredVersion;

    protected EccLevel preferredEccLevel = EccLevel.L;

    protected boolean improveEccLevelIfPossible = true;

    protected boolean forceByteCompaction;

    protected int structuredAppendPosition = 1;

    protected int structuredAppendTotal = 1;

    protected int structuredAppendParity = 0;

    /**
     * Creates a new instance.
     */
    public QrCode() {
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    /**
     * Sets the preferred symbol size / version. This value may be ignored if the data
     * string is too large to fit into the specified symbol. Input values correspond
     * to symbol sizes as shown in the following table:
     *
     * <table>
     * <tbody>
     * <tr><th>Input</th><th>Symbol Size</th><th>Input</th><th>Symbol Size</th></tr>
     * <tr><td>1    </td><td>21 x 21    </td><td>21   </td><td>101 x 101  </td></tr>
     * <tr><td>2    </td><td>25 x 25    </td><td>22   </td><td>105 x 105  </td></tr>
     * <tr><td>3    </td><td>29 x 29    </td><td>23   </td><td>109 x 109  </td></tr>
     * <tr><td>4    </td><td>33 x 33    </td><td>24   </td><td>113 x 113  </td></tr>
     * <tr><td>5    </td><td>37 x 37    </td><td>25   </td><td>117 x 117  </td></tr>
     * <tr><td>6    </td><td>41 x 41    </td><td>26   </td><td>121 x 121  </td></tr>
     * <tr><td>7    </td><td>45 x 45    </td><td>27   </td><td>125 x 125  </td></tr>
     * <tr><td>8    </td><td>49 x 49    </td><td>28   </td><td>129 x 129  </td></tr>
     * <tr><td>9    </td><td>53 x 53    </td><td>29   </td><td>133 x 133  </td></tr>
     * <tr><td>10   </td><td>57 x 57    </td><td>30   </td><td>137 x 137  </td></tr>
     * <tr><td>11   </td><td>61 x 61    </td><td>31   </td><td>141 x 141  </td></tr>
     * <tr><td>12   </td><td>65 x 65    </td><td>32   </td><td>145 x 145  </td></tr>
     * <tr><td>13   </td><td>69 x 69    </td><td>33   </td><td>149 x 149  </td></tr>
     * <tr><td>14   </td><td>73 x 73    </td><td>34   </td><td>153 x 153  </td></tr>
     * <tr><td>15   </td><td>77 x 77    </td><td>35   </td><td>157 x 157  </td></tr>
     * <tr><td>16   </td><td>81 x 81    </td><td>36   </td><td>161 x 161  </td></tr>
     * <tr><td>17   </td><td>85 x 85    </td><td>37   </td><td>165 x 165  </td></tr>
     * <tr><td>18   </td><td>89 x 89    </td><td>38   </td><td>169 x 169  </td></tr>
     * <tr><td>19   </td><td>93 x 93    </td><td>39   </td><td>173 x 173  </td></tr>
     * <tr><td>20   </td><td>97 x 97    </td><td>40   </td><td>177 x 177  </td></tr>
     * </tbody>
     * </table>
     *
     * @param version the preferred symbol version
     */
    public void setPreferredVersion(int version) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the preferred symbol version.
     *
     * @return the preferred symbol version
     */
    public int getPreferredVersion() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the preferred amount of symbol space allocated to error correction. This value may
     * be ignored if there is room for a higher error correction level. Levels are predefined
     * according to the following table:
     *
     * <table>
     * <tbody>
     * <tr><th>ECC Level  </th><th>Error Correction Capacity</th><th>Recovery Capacity</th></tr>
     * <tr><td>L (default)</td><td>About 20% of symbol      </td><td>About 7%         </td></tr>
     * <tr><td>M          </td><td>About 37% of symbol      </td><td>About 15%        </td></tr>
     * <tr><td>Q          </td><td>About 55% of symbol      </td><td>About 25%        </td></tr>
     * <tr><td>H          </td><td>About 65% of symbol      </td><td>About 30%        </td></tr>
     * </tbody>
     * </table>
     *
     * @param preferredEccLevel the preferred error correction level
     */
    public void setPreferredEccLevel(EccLevel preferredEccLevel) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the preferred amount of symbol space allocated to error correction.
     *
     * @return the preferred amount of symbol space allocated to error correction
     */
    public EccLevel getPreferredEccLevel() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>If set to <code>false</code> (which is the default value), this symbol is
     * allowed to optimize compaction modes automatically, based on the data to be
     * encoded. If set to <code>true</code>, this symbol is forced to use byte
     * compaction mode instead of optimizing the compaction modes automatically.
     *
     * <p><b>NOTE:</b> Forcing the use of byte compaction mode is usually sub-optimal,
     * and will result in larger symbols than would otherwise be possible. This method
     * should only be used if your downstream systems <b>require</b> the use of byte
     * compaction mode, which is <b>not</b> usually the case.
     *
     * @param forceByteCompaction whether or not to force the use of byte compaction mode
     */
    public void setForceByteCompaction(boolean forceByteCompaction) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns whether or not this symbol has been forced to use byte compaction mode.
     * By default, this method returns <code>false</code>, and the symbol is allowed
     * to optimize compaction modes in whatever way best fits the provided data.
     *
     * @return whether or not this symbol has been forced to use byte compaction mode
     */
    public boolean getForceByteCompaction() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the position of this QR Code symbol in a structured append sequence (1–16).
     *
     * @param position the position of this QR Code symbol in a structured append sequence (1–16)
     */
    public void setStructuredAppendPosition(int position) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the position of this QR Code symbol in a structured append sequence (1-16).
     *
     * @return the position of this QR Code symbol in a structured append sequence (1–16)
     */
    public int getStructuredAppendPosition() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the total number of QR Code symbols in the structured append sequence (1–16).
     *
     * @param total the total number of QR Code symbols in the structured append sequence (1–16)
     */
    public void setStructuredAppendTotal(int total) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the total number of QR Code symbols in the structured append sequence (1-16).
     *
     * @return the total number of QR Code symbols in the structured append sequence (1–16)
     */
    public int getStructuredAppendTotal() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the structured append parity (XOR of all bytes in the original message, 0–255).
     *
     * @param parity the parity value (0–255)
     */
    public void setStructuredAppendParity(int parity) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the structured append parity (XOR of all bytes in the original message, 0–255).
     *
     * @return the parity value (0–255)
     */
    public int getStructuredAppendParity() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public boolean supportsEci() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Place Kanji / Binary / Alphanumeric / Numeric values in inputMode.
     */
    private static void defineMode(QrMode[] inputMode, int[] inputData, boolean forceByteCompaction) {
        if (forceByteCompaction) {
            Arrays.fill(inputMode, 0, inputData.length, QrMode.BINARY);
            return;
        }
        for (int i = 0; i < inputData.length; i++) {
            if (inputData[i] > 0xff) {
                inputMode[i] = QrMode.KANJI;
            } else {
                inputMode[i] = QrMode.BINARY;
                if (isAlpha(inputData[i])) {
                    inputMode[i] = QrMode.ALPHANUM;
                }
                if (inputData[i] == FNC1) {
                    inputMode[i] = QrMode.ALPHANUM;
                }
                if (isNumeric(inputData[i])) {
                    inputMode[i] = QrMode.NUMERIC;
                }
            }
        }
        // TODO: uncomment
        //        /* If less than 6 numeric digits together then don't use numeric mode */
        //        for (int i = 0; i < inputMode.length; i++) {
        //            if (inputMode[i] == QrMode.NUMERIC) {
        //                if (((i != 0) && (inputMode[i - 1] != QrMode.NUMERIC)) || (i == 0)) {
        //                    mlen = 0;
        //                    while (((mlen + i) < inputMode.length) && (inputMode[mlen + i] == QrMode.NUMERIC)) {
        //                        mlen++;
        //                    };
        //                    if (mlen < 6) {
        //                        for (int j = 0; j < mlen; j++) {
        //                            inputMode[i + j] = QrMode.ALPHANUM;
        //                        }
        //                    }
        //                }
        //            }
        //        }
        //
        //        /* If less than 4 alphanumeric characters together then don't use alphanumeric mode */
        //        for (int i = 0; i < inputMode.length; i++) {
        //            if (inputMode[i] == QrMode.ALPHANUM) {
        //                if (((i != 0) && (inputMode[i - 1] != QrMode.ALPHANUM)) || (i == 0)) {
        //                    mlen = 0;
        //                    while (((mlen + i) < inputMode.length) && (inputMode[mlen + i] == QrMode.ALPHANUM)) {
        //                        mlen++;
        //                    };
        //                    if (mlen < 4) {
        //                        for (int j = 0; j < mlen; j++) {
        //                            inputMode[i + j] = QrMode.BINARY;
        //                        }
        //                    }
        //                }
        //            }
        //        }
    }

    /**
     * Calculate the actual bit length of the proposed binary string.
     */
    private static int getBinaryLength(int version, QrMode[] inputModeUnoptimized, int[] inputData, boolean gs1, int eciMode, boolean structuredAppendMode) {
        int i, j;
        QrMode currentMode;
        int inputLength = inputModeUnoptimized.length;
        int count = 0;
        int alphaLength;
        int percent = 0;
        // ZINT NOTE: in Zint, this call modifies the input mode array directly; here, we leave
        // the original array alone so that subsequent binary length checks don't irrevocably
        // optimize the mode array for the wrong QR Code version
        QrMode[] inputMode = applyOptimisation(version, inputModeUnoptimized);
        currentMode = QrMode.NULL;
        if (structuredAppendMode) {
            count += 20;
        }
        if (gs1) {
            count += 4;
        }
        if (eciMode != 3) {
            count += 12;
        }
        for (i = 0; i < inputLength; i++) {
            if (inputMode[i] != currentMode) {
                count += 4;
                switch(inputMode[i]) {
                    case KANJI:
                        count += tribus(version, 8, 10, 12);
                        // 2-byte SJIS character -> 13 bits
                        count += (blockLength(i, inputMode) * 13);
                        break;
                    case BINARY:
                        count += tribus(version, 8, 16, 16);
                        int max = i + blockLength(i, inputMode);
                        for (j = i; j < max; j++) {
                            if (inputData[j] > 0xff) {
                                // actually a 2-byte SJIS character
                                count += 16;
                            } else {
                                // just a normal byte
                                count += 8;
                            }
                        }
                        break;
                    case ALPHANUM:
                        count += tribus(version, 9, 11, 13);
                        alphaLength = blockLength(i, inputMode);
                        if (gs1) {
                            for (j = i; j < (i + alphaLength); j++) {
                                if (inputData[j] == '%') {
                                    // in GS1 and alphanumeric mode % becomes %%
                                    percent++;
                                }
                            }
                        }
                        alphaLength += percent;
                        switch(alphaLength % 2) {
                            case 0:
                                // 2 characters -> 11 bits
                                count += (alphaLength / 2) * 11;
                                break;
                            case 1:
                                // 2 characters -> 11 bits
                                count += ((alphaLength - 1) / 2) * 11;
                                // trailing character -> 6 bits
                                count += 6;
                                break;
                        }
                        break;
                    case NUMERIC:
                        count += tribus(version, 10, 12, 14);
                        int length = blockLength(i, inputMode);
                        switch(length % 3) {
                            case 0:
                                // 3 digits -> 10 bits
                                count += (length / 3) * 10;
                                break;
                            case 1:
                                // 3 digits -> 10 bits
                                count += ((length - 1) / 3) * 10;
                                // trailing digit -> 4 bits
                                count += 4;
                                break;
                            case 2:
                                // 3 digits -> 10 bits
                                count += ((length - 2) / 3) * 10;
                                // trailing 2 digits -> 7 bits
                                count += 7;
                                break;
                        }
                        break;
                }
                currentMode = inputMode[i];
            }
        }
        return count;
    }

    /**
     * Implements a custom optimization algorithm, because implementation of the algorithm
     * shown in Annex J.2 created LONGER binary sequences.
     */
    private static QrMode[] applyOptimisation(int version, QrMode[] inputMode) {
        int inputLength = inputMode.length;
        int blockCount = 0;
        int i, j, min;
        boolean returns;
        QrMode currentMode = QrMode.NULL;
        for (i = 0; i < inputLength; i++) {
            if (inputMode[i] != currentMode) {
                currentMode = inputMode[i];
                blockCount++;
            }
        }
        int[] blockLength = new int[blockCount];
        QrMode[] blockMode = new QrMode[blockCount];
        j = -1;
        currentMode = QrMode.NULL;
        for (i = 0; i < inputLength; i++) {
            if (inputMode[i] != currentMode) {
                j++;
                blockLength[j] = 1;
                blockMode[j] = inputMode[i];
                currentMode = inputMode[i];
            } else {
                blockLength[j]++;
            }
        }
        // Encoding costs in bits (switching cost depends on symbol version):
        // Byte mode: (8,16,16) + (N * 8)              = (8,16,16)  + (N * 8)
        // Kanji mode: (8,10,12) + (N * 13 / 2)        = (8,10,12)  + (N * 6.5) -> saves 1.5 bits / byte encoded
        // Alphanumeric mode: (9,11,13) + (N * 11 / 2) = (9,11,13)  + (N * 5.5) -> saves 2.5 bits / byte encoded
        // Numeric mode: (10,12,14) + (N * 10 / 3)     = (10,12,14) + (N * 3.3) -> saves 4.6 bits / byte encoded
        // NOTE: our kanji values pack 2 bytes per value, so block lengths are actually twice the recorded size
        // NOTE: a X -> Y -> X mode change needs to recoup not just the X -> Y overhead, but also the Y -> X overhead
        // ZINT NOTE: the thresholds below are different from the original code, and also take mode returns into account
        if (blockCount > 1) {
            // Search forward
            for (i = 0; i < blockCount - 1; i++) {
                returns = (i + 2 < blockMode.length && blockMode[i + 2] == blockMode[i]);
                if (blockMode[i] == QrMode.BINARY) {
                    switch(blockMode[i + 1]) {
                        case KANJI:
                            min = (returns ? tribus(version, 12, 18, 20) : tribus(version, 6, 8, 8));
                            if (blockLength[i + 1] * 2 < min) {
                                // not worth switching
                                blockMode[i + 1] = QrMode.BINARY;
                            }
                            break;
                        case ALPHANUM:
                            min = (returns ? tribus(version, 7, 11, 12) : tribus(version, 4, 5, 6));
                            if (blockLength[i + 1] < min) {
                                // not worth switching
                                blockMode[i + 1] = QrMode.BINARY;
                            }
                            break;
                        case NUMERIC:
                            min = (returns ? tribus(version, 4, 7, 7) : tribus(version, 3, 3, 4));
                            if (blockLength[i + 1] < min) {
                                // not worth switching
                                blockMode[i + 1] = QrMode.BINARY;
                            }
                            break;
                    }
                }
                if (blockMode[i] == QrMode.ALPHANUM && blockMode[i + 1] == QrMode.NUMERIC) {
                    min = (returns ? tribus(version, 9, 12, 15) : tribus(version, 6, 9, 9));
                    if (blockLength[i + 1] < min) {
                        // not worth switching
                        blockMode[i + 1] = QrMode.ALPHANUM;
                    }
                }
            }
            // Search backward
            for (i = blockCount - 1; i > 0; i--) {
                returns = (i - 2 >= 0 && blockMode[i - 2] == blockMode[i]);
                if (blockMode[i] == QrMode.BINARY) {
                    switch(blockMode[i - 1]) {
                        case KANJI:
                            min = (returns ? tribus(version, 12, 18, 20) : tribus(version, 6, 8, 8));
                            if (blockLength[i - 1] * 2 < min) {
                                // not worth switching
                                blockMode[i - 1] = QrMode.BINARY;
                            }
                            break;
                        case ALPHANUM:
                            min = (returns ? tribus(version, 7, 11, 12) : tribus(version, 4, 5, 6));
                            if (blockLength[i - 1] < min) {
                                // not worth switching
                                blockMode[i - 1] = QrMode.BINARY;
                            }
                            break;
                        case NUMERIC:
                            min = (returns ? tribus(version, 4, 7, 7) : tribus(version, 3, 3, 4));
                            if (blockLength[i - 1] < min) {
                                // not worth switching
                                blockMode[i - 1] = QrMode.BINARY;
                            }
                            break;
                    }
                }
                if (blockMode[i] == QrMode.ALPHANUM && blockMode[i - 1] == QrMode.NUMERIC) {
                    min = (returns ? tribus(version, 9, 12, 15) : tribus(version, 6, 9, 9));
                    if (blockLength[i - 1] < min) {
                        // not worth switching
                        blockMode[i - 1] = QrMode.ALPHANUM;
                    }
                }
            }
        }
        // ZINT NOTE: this method is different from the original Zint code in that it creates a
        // new array to hold the optimized values and returns it, rather than modifying the
        // original array; this allows this method to be called as many times as we want without
        // worrying about side effects
        QrMode[] optimized = new QrMode[inputMode.length];
        j = 0;
        for (int block = 0; block < blockCount; block++) {
            currentMode = blockMode[block];
            for (i = 0; i < blockLength[block]; i++) {
                optimized[j] = currentMode;
                j++;
            }
        }
        return optimized;
    }

    /**
     * Find the length of the block starting from 'start'.
     */
    private static int blockLength(int start, QrMode[] inputMode) {
        QrMode mode = inputMode[start];
        int i = start + 1;
        while (i < inputMode.length && inputMode[i] == mode) {
            i++;
        }
        return i - start;
    }

    /**
     * Choose from three numbers based on version.
     */
    private static int tribus(int version, int a, int b, int c) {
        if (version < 10) {
            // 1-9
            return a;
        } else if (version < 27) {
            // 10-26
            return b;
        } else {
            // 27-40
            return c;
        }
    }

    /**
     * Returns true if input is in the Alphanumeric set (see Table J.1)
     */
    private static boolean isAlpha(int c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c == ' ') || (c == '$') || (c == '%') || (c == '*') || (c == '+') || (c == '-') || (c == '.') || (c == '/') || (c == ':');
    }

    /**
     * Returns true if input is in the Numeric set (see Table J.1)
     */
    private static boolean isNumeric(int c) {
        return (c >= '0' && c <= '9');
    }

    private static String maskToString(int mask) {
        switch(mask) {
            case 0:
                return "000";
            case 1:
                return "001";
            case 2:
                return "010";
            case 3:
                return "011";
            case 4:
                return "100";
            case 5:
                return "101";
            case 6:
                return "110";
            case 7:
                return "111";
            default:
                return "000";
        }
    }

    /**
     * Converts input data to a binary stream and adds padding.
     */
    private void qrBinary(int[] datastream, int version, int target_binlen, QrMode[] inputMode, int[] inputData, boolean gs1, int eciMode, int est_binlen) {
        int position = 0;
        int short_data_block_length, i;
        int padbits;
        int current_binlen, current_bytes;
        int toggle;
        QrMode data_block;
        int reserved = est_binlen + 12;
        StringBuilder binary = new StringBuilder(reserved);
        if (getStructuredAppendMode()) {
            binary.append("0011");
            /* Structured append mode */
            binaryAppend(binary, structuredAppendPosition - 1, 4);
            binaryAppend(binary, structuredAppendTotal - 1, 4);
            binaryAppend(binary, structuredAppendParity, 8);
        }
        if (gs1) {
            binary.append("0101");
            /* FNC1 */
        }
        if (eciMode != 3) {
            binary.append("0111");
            /* ECI (Table 4) */
            if (eciMode <= 127) {
                binaryAppend(binary, eciMode, 8);
                /* 000000 to 000127 */
            } else if (eciMode <= 16383) {
                binaryAppend(binary, 0x8000 + eciMode, 16);
                /* 000000 to 016383 */
            } else {
                binaryAppend(binary, 0xC00000 + eciMode, 24);
                /* 000000 to 999999 */
            }
        }
        info("Encoding: ");
        while (position < inputMode.length) {
            data_block = inputMode[position];
            short_data_block_length = 0;
            do {
                short_data_block_length++;
            } while (((short_data_block_length + position) < inputMode.length) && (inputMode[position + short_data_block_length] == data_block));
            switch(data_block) {
                case KANJI:
                    /* Kanji mode */
                    /* Mode indicator */
                    binary.append("1000");
                    /* Character count indicator */
                    binaryAppend(binary, short_data_block_length, tribus(version, 8, 10, 12));
                    info("KNJI ");
                    /* Character representation */
                    for (i = 0; i < short_data_block_length; i++) {
                        int jis = inputData[position + i];
                        if (jis >= 0x8140 && jis <= 0x9ffc) {
                            jis -= 0x8140;
                        } else if (jis >= 0xe040 && jis <= 0xebbf) {
                            jis -= 0xc140;
                        }
                        int prod = ((jis >> 8) * 0xc0) + (jis & 0xff);
                        binaryAppend(binary, prod, 13);
                        infoSpace(prod);
                    }
                    break;
                case BINARY:
                    /* Byte mode */
                    /* Mode indicator */
                    binary.append("0100");
                    /* Character count indicator (watch for packed 2-byte values, Kanji prior to optimization) */
                    int bytes = 0;
                    for (i = 0; i < short_data_block_length; i++) {
                        int b = inputData[position + i];
                        bytes += (b > 0xff ? 2 : 1);
                    }
                    binaryAppend(binary, bytes, tribus(version, 8, 16, 16));
                    info("BYTE ");
                    /* Character representation */
                    for (i = 0; i < short_data_block_length; i++) {
                        int b = inputData[position + i];
                        if (b > 0xff) {
                            // actually 2 packed Kanji bytes
                            int b1 = b >> 8;
                            int b2 = b & 0xff;
                            binaryAppend(binary, b1, 8);
                            infoSpace(b1);
                            binaryAppend(binary, b2, 8);
                            infoSpace(b2);
                        } else {
                            // a single byte
                            if (b == FNC1) {
                                b = 0x1d;
                                /* FNC1 */
                            }
                            binaryAppend(binary, b, 8);
                            infoSpace(b);
                        }
                    }
                    break;
                case ALPHANUM:
                    /* Alphanumeric mode */
                    /* Mode indicator */
                    binary.append("0010");
                    /* If in GS1 mode, expand FNC1 -> '%' and expand '%' -> '%%' in a new array */
                    int percentCount = 0;
                    if (gs1) {
                        for (i = 0; i < short_data_block_length; i++) {
                            if (inputData[position + i] == '%') {
                                percentCount++;
                            }
                        }
                    }
                    int[] inputExpanded = new int[short_data_block_length + percentCount];
                    percentCount = 0;
                    for (i = 0; i < short_data_block_length; i++) {
                        int c = inputData[position + i];
                        if (c == FNC1) {
                            inputExpanded[i + percentCount] = '%';
                            /* FNC1 */
                        } else {
                            inputExpanded[i + percentCount] = c;
                            if (gs1 && c == '%') {
                                percentCount++;
                                inputExpanded[i + percentCount] = c;
                            }
                        }
                    }
                    /* Character count indicator */
                    binaryAppend(binary, inputExpanded.length, tribus(version, 9, 11, 13));
                    info("ALPH ");
                    /* Character representation */
                    for (i = 0; i + 1 < inputExpanded.length; i += 2) {
                        int first = positionOf((char) inputExpanded[i], RHODIUM);
                        int second = positionOf((char) inputExpanded[i + 1], RHODIUM);
                        int prod = (first * 45) + second;
                        int count = 2;
                        binaryAppend(binary, prod, 1 + (5 * count));
                        infoSpace(prod);
                    }
                    if (inputExpanded.length % 2 != 0) {
                        int first = positionOf((char) inputExpanded[inputExpanded.length - 1], RHODIUM);
                        int prod = first;
                        int count = 1;
                        binaryAppend(binary, prod, 1 + (5 * count));
                        infoSpace(prod);
                    }
                    break;
                case NUMERIC:
                    /* Numeric mode */
                    /* Mode indicator */
                    binary.append("0001");
                    /* Character count indicator */
                    binaryAppend(binary, short_data_block_length, tribus(version, 10, 12, 14));
                    info("NUMB ");
                    /* Character representation */
                    i = 0;
                    while (i < short_data_block_length) {
                        int first = Character.getNumericValue(inputData[position + i]);
                        int count = 1;
                        int prod = first;
                        if (i + 1 < short_data_block_length) {
                            int second = Character.getNumericValue(inputData[position + i + 1]);
                            count = 2;
                            prod = (prod * 10) + second;
                            if (i + 2 < short_data_block_length) {
                                int third = Character.getNumericValue(inputData[position + i + 2]);
                                count = 3;
                                prod = (prod * 10) + third;
                            }
                        }
                        binaryAppend(binary, prod, 1 + (3 * count));
                        infoSpace(prod);
                        i += count;
                    }
                    break;
            }
            position += short_data_block_length;
        }
        infoLine();
        /* Terminator */
        binary.append("0000");
        current_binlen = binary.length();
        padbits = 8 - (current_binlen % 8);
        if (padbits == 8) {
            padbits = 0;
        }
        current_bytes = (current_binlen + padbits) / 8;
        /* Padding bits */
        for (i = 0; i < padbits; i++) {
            binary.append('0');
        }
        /* Put data into 8-bit codewords */
        for (i = 0; i < current_bytes; i++) {
            datastream[i] = 0x00;
            for (int p = 0; p < 8; p++) {
                if (binary.charAt((i * 8) + p) == '1') {
                    datastream[i] += (0x80 >> p);
                }
            }
        }
        /* Add pad codewords */
        toggle = 0;
        for (i = current_bytes; i < target_binlen; i++) {
            if (toggle == 0) {
                datastream[i] = 0xec;
                toggle = 1;
            } else {
                datastream[i] = 0x11;
                toggle = 0;
            }
        }
        info("Codewords: ");
        for (i = 0; i < target_binlen; i++) {
            infoSpace(datastream[i]);
        }
        infoLine();
        assert binary.length() <= reserved;
    }

    private boolean getStructuredAppendMode() {
        return structuredAppendTotal > 1;
    }

    /**
     * Splits data into blocks, adds error correction and then interleaves the blocks and error correction data.
     */
    private static void addEcc(int[] fullstream, int[] datastream, int version, int data_cw, int blocks) {
        int ecc_cw = QR_TOTAL_CODEWORDS[version - 1] - data_cw;
        int short_data_block_length = data_cw / blocks;
        int qty_long_blocks = data_cw % blocks;
        int qty_short_blocks = blocks - qty_long_blocks;
        int ecc_block_length = ecc_cw / blocks;
        int i, j, length_this_block, posn;
        int[] data_block = new int[short_data_block_length + 2];
        int[] ecc_block = new int[ecc_block_length + 2];
        int[] interleaved_data = new int[data_cw + 2];
        int[] interleaved_ecc = new int[ecc_cw + 2];
        posn = 0;
        for (i = 0; i < blocks; i++) {
            if (i < qty_short_blocks) {
                length_this_block = short_data_block_length;
            } else {
                length_this_block = short_data_block_length + 1;
            }
            for (j = 0; j < length_this_block; j++) {
                data_block[j] = datastream[posn + j];
            }
            ReedSolomon rs = ReedSolomon.get(0x11d, ecc_block_length, 0, true);
            int[] result = rs.encode(length_this_block, data_block);
            System.arraycopy(result, 0, ecc_block, 0, ecc_block_length);
            for (j = 0; j < short_data_block_length; j++) {
                interleaved_data[(j * blocks) + i] = data_block[j];
            }
            if (i >= qty_short_blocks) {
                interleaved_data[(short_data_block_length * blocks) + (i - qty_short_blocks)] = data_block[short_data_block_length];
            }
            for (j = 0; j < ecc_block_length; j++) {
                interleaved_ecc[(j * blocks) + i] = ecc_block[ecc_block_length - j - 1];
            }
            posn += length_this_block;
        }
        for (j = 0; j < data_cw; j++) {
            fullstream[j] = interleaved_data[j];
        }
        for (j = 0; j < ecc_cw; j++) {
            fullstream[j + data_cw] = interleaved_ecc[j];
        }
    }

    private static void setupGrid(int[] grid, int size, int version) {
        int i;
        boolean toggle = true;
        /* Add timing patterns */
        for (i = 0; i < size; i++) {
            if (toggle) {
                grid[(6 * size) + i] = 0x21;
                grid[(i * size) + 6] = 0x21;
                toggle = false;
            } else {
                grid[(6 * size) + i] = 0x20;
                grid[(i * size) + 6] = 0x20;
                toggle = true;
            }
        }
        /* Add finder patterns */
        placeFinder(grid, size, 0, 0);
        placeFinder(grid, size, 0, size - 7);
        placeFinder(grid, size, size - 7, 0);
        /* Add separators */
        for (i = 0; i < 7; i++) {
            grid[(7 * size) + i] = 0x10;
            grid[(i * size) + 7] = 0x10;
            grid[(7 * size) + (size - 1 - i)] = 0x10;
            grid[(i * size) + (size - 8)] = 0x10;
            grid[((size - 8) * size) + i] = 0x10;
            grid[((size - 1 - i) * size) + 7] = 0x10;
        }
        grid[(7 * size) + 7] = 0x10;
        grid[(7 * size) + (size - 8)] = 0x10;
        grid[((size - 8) * size) + 7] = 0x10;
        /* Add alignment patterns */
        int loopsize = QR_ALIGN_LOOPSIZE[version - 1];
        for (int x = 0; x < loopsize; x++) {
            for (int y = 0; y < loopsize; y++) {
                int xcoord = QR_TABLE_E1[((version - 2) * 7) + x];
                int ycoord = QR_TABLE_E1[((version - 2) * 7) + y];
                if ((grid[(ycoord * size) + xcoord] & 0x10) == 0) {
                    placeAlign(grid, size, xcoord, ycoord);
                }
            }
        }
        /* Reserve space for format information */
        for (i = 0; i < 8; i++) {
            grid[(8 * size) + i] += 0x20;
            grid[(i * size) + 8] += 0x20;
            grid[(8 * size) + (size - 1 - i)] = 0x20;
            grid[((size - 1 - i) * size) + 8] = 0x20;
        }
        grid[(8 * size) + 8] += 0x20;
        grid[((size - 1 - 7) * size) + 8] = 0x21;
        /* Dark Module from Figure 25 */
        /* Reserve space for version information */
        if (version >= 7) {
            for (i = 0; i < 6; i++) {
                grid[((size - 9) * size) + i] = 0x20;
                grid[((size - 10) * size) + i] = 0x20;
                grid[((size - 11) * size) + i] = 0x20;
                grid[(i * size) + (size - 9)] = 0x20;
                grid[(i * size) + (size - 10)] = 0x20;
                grid[(i * size) + (size - 11)] = 0x20;
            }
        }
    }

    private static void placeFinder(int[] grid, int size, int x, int y) {
        int[] finder = { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 };
        for (int xp = 0; xp < 7; xp++) {
            for (int yp = 0; yp < 7; yp++) {
                if (finder[xp + (7 * yp)] == 1) {
                    grid[((yp + y) * size) + (xp + x)] = 0x11;
                } else {
                    grid[((yp + y) * size) + (xp + x)] = 0x10;
                }
            }
        }
    }

    private static void placeAlign(int[] grid, int size, int x, int y) {
        int[] alignment = { 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1 };
        x -= 2;
        y -= 2;
        /* Input values represent centre of pattern */
        for (int xp = 0; xp < 5; xp++) {
            for (int yp = 0; yp < 5; yp++) {
                if (alignment[xp + (5 * yp)] == 1) {
                    grid[((yp + y) * size) + (xp + x)] = 0x11;
                } else {
                    grid[((yp + y) * size) + (xp + x)] = 0x10;
                }
            }
        }
    }

    private static void populateGrid(int[] grid, int size, int[] fullstream, int cw) {
        boolean goingUp = true;
        int row = 0;
        /* right hand side */
        int i, n, y;
        n = cw * 8;
        y = size - 1;
        i = 0;
        do {
            int x = (size - 2) - (row * 2);
            if (x < 6) {
                x--;
                /* skip over vertical timing pattern */
            }
            if ((grid[(y * size) + (x + 1)] & 0xf0) == 0) {
                if (cwbit(fullstream, i)) {
                    grid[(y * size) + (x + 1)] = 0x01;
                } else {
                    grid[(y * size) + (x + 1)] = 0x00;
                }
                i++;
            }
            if (i < n) {
                if ((grid[(y * size) + x] & 0xf0) == 0) {
                    if (cwbit(fullstream, i)) {
                        grid[(y * size) + x] = 0x01;
                    } else {
                        grid[(y * size) + x] = 0x00;
                    }
                    i++;
                }
            }
            if (goingUp) {
                y--;
            } else {
                y++;
            }
            if (y == -1) {
                /* reached the top */
                row++;
                y = 0;
                goingUp = false;
            }
            if (y == size) {
                /* reached the bottom */
                row++;
                y = size - 1;
                goingUp = true;
            }
        } while (i < n);
    }

    private static boolean cwbit(int[] fullstream, int i) {
        return ((fullstream[i / 8] & (0x80 >> (i % 8))) != 0);
    }

    private static int applyBitmask(int[] grid, int size, EccLevel ecc_level, StringBuilder encodeInfo) {
        int x, y;
        int p;
        int pattern;
        int penalty, best_val, best_pattern;
        byte[] mask = new byte[size * size];
        byte[] eval = new byte[size * size];
        /* Perform data masking */
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                // all eight bit mask variants are encoded in the 8 bits of the bytes that make up the mask array
                if ((grid[(y * size) + x] & 0xf0) == 0) {
                    // exclude areas not to be masked
                    if (((y + x) & 1) == 0) {
                        mask[(y * size) + x] += (byte) 0x01;
                    }
                    if ((y & 1) == 0) {
                        mask[(y * size) + x] += (byte) 0x02;
                    }
                    if ((x % 3) == 0) {
                        mask[(y * size) + x] += (byte) 0x04;
                    }
                    if (((y + x) % 3) == 0) {
                        mask[(y * size) + x] += (byte) 0x08;
                    }
                    if ((((y / 2) + (x / 3)) & 1) == 0) {
                        mask[(y * size) + x] += (byte) 0x10;
                    }
                    if ((((y * x) & 1) + ((y * x) % 3)) == 0) {
                        mask[(y * size) + x] += (byte) 0x20;
                    }
                    if (((((y * x) & 1) + ((y * x) % 3)) & 1) == 0) {
                        mask[(y * size) + x] += (byte) 0x40;
                    }
                    if (((((y + x) & 1) + ((y * x) % 3)) & 1) == 0) {
                        mask[(y * size) + x] += (byte) 0x80;
                    }
                }
            }
        }
        /* Apply data masks to grid, result in eval (each byte in eval contains 8 masked bit values, one per mask) */
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if ((grid[(y * size) + x] & 0x01) != 0) {
                    p = 0xff;
                } else {
                    p = 0x00;
                }
                eval[(y * size) + x] = (byte) (mask[(y * size) + x] ^ p);
            }
        }
        /* Evaluate result */
        best_pattern = 0;
        best_val = Integer.MAX_VALUE;
        for (pattern = 0; pattern < 8; pattern++) {
            addFormatInfoEval(eval, size, ecc_level, pattern);
            penalty = evaluate(eval, size, pattern, best_val, encodeInfo);
            if (penalty < best_val) {
                best_pattern = pattern;
                best_val = penalty;
            }
        }
        /* Apply mask */
        for (x = 0; x < size; x++) {
            for (y = 0; y < size; y++) {
                if ((mask[(y * size) + x] & (0x01 << best_pattern)) != 0) {
                    if ((grid[(y * size) + x] & 0x01) != 0) {
                        grid[(y * size) + x] = 0x00;
                    } else {
                        grid[(y * size) + x] = 0x01;
                    }
                }
            }
        }
        return best_pattern;
    }

    /**
     * Adds format information to eval.
     */
    private static void addFormatInfoEval(byte[] eval, int size, EccLevel ecc_level, int pattern) {
        int format;
        int seq;
        int i;
        switch(ecc_level) {
            case L:
                format = pattern | 0x08;
                break;
            case Q:
                format = pattern | 0x18;
                break;
            case H:
                format = pattern | 0x10;
                break;
            case M:
                format = pattern;
                break;
            default:
                throw new OkapiInternalException("Unknown ECC level: " + ecc_level);
        }
        seq = QR_ANNEX_C[format];
        for (i = 0; i < 6; i++) {
            eval[(i * size) + 8] = (byte) (((seq >>> i) & 0x01) << pattern);
        }
        for (i = 0; i < 8; i++) {
            eval[(8 * size) + (size - i - 1)] = (byte) (((seq >>> i) & 0x01) << pattern);
        }
        for (i = 0; i < 6; i++) {
            eval[(8 * size) + (5 - i)] = (byte) (((seq >>> (i + 9)) & 0x01) << pattern);
        }
        for (i = 0; i < 7; i++) {
            eval[(((size - 7) + i) * size) + 8] = (byte) (((seq >>> (i + 8)) & 0x01) << pattern);
        }
        eval[(7 * size) + 8] = (byte) (((seq >>> 6) & 0x01) << pattern);
        eval[(8 * size) + 8] = (byte) (((seq >>> 7) & 0x01) << pattern);
        eval[(8 * size) + 7] = (byte) (((seq >>> 8) & 0x01) << pattern);
    }

    private static int evaluate(byte[] eval, int size, int pattern, int best, StringBuilder encodeInfo) {
        int x, y, i, block;
        int result = 0;
        byte state;
        int dark_mods;
        int percentage, k;
        boolean light;
        byte[] local = new byte[size * size];
        // all eight bit mask variants have been encoded in the 8 bits of the bytes
        // that make up the grid array; select them for evaluation according to the
        // desired pattern
        dark_mods = 0;
        int mask = 0x01 << pattern;
        for (i = 0; i < local.length; i++) {
            // 0 or 1
            byte val = (byte) ((eval[i] & mask) >> pattern);
            local[i] = val;
            // optimization: early prep for test 4 below
            dark_mods += val;
        }
        encodeInfo.append("Mask ").append(maskToString(pattern)).append(" Penalties: ");
        /* Test 1: Adjacent modules in row/column in same colour */
        /* Vertical */
        for (x = 0; x < size; x++) {
            state = local[x];
            block = 1;
            for (y = 1; y < size; y++) {
                i = (y * size) + x;
                if (local[i] == state) {
                    block++;
                } else {
                    if (block >= 5) {
                        result += (3 + (block - 5));
                    }
                    block = 1;
                    state = local[i];
                }
            }
            if (block >= 5) {
                result += (3 + (block - 5));
            }
        }
        /* Horizontal */
        for (y = 0; y < size; y++) {
            state = local[y * size];
            block = 1;
            for (x = 1; x < size; x++) {
                i = (y * size) + x;
                if (local[i] == state) {
                    block++;
                } else {
                    if (block >= 5) {
                        result += (3 + (block - 5));
                    }
                    block = 1;
                    state = local[i];
                }
            }
            if (block >= 5) {
                result += (3 + (block - 5));
            }
        }
        encodeInfo.append(result).append(' ');
        if (result > best) {
            encodeInfo.append("EXIT\n");
            return result;
        }
        /* Test 2: Block of modules in same color */
        for (x = 0; x < size - 1; x++) {
            for (y = 0; y < size - 1; y++) {
                i = (y * size) + x;
                state = local[i];
                if (state == local[i + 1] && state == local[i + size] && state == local[i + size + 1]) {
                    result += 3;
                }
            }
        }
        encodeInfo.append(result).append(' ');
        if (result > best) {
            encodeInfo.append("EXIT\n");
            return result;
        }
        /* Test 3: 1:1:3:1:1 ratio pattern in row/column */
        /* Vertical */
        for (x = 0; x < size; x++) {
            for (y = 0; y <= (size - 7); y++) {
                if (local[((y + 0) * size) + x] == 1 && local[((y + 1) * size) + x] == 0 && local[((y + 2) * size) + x] == 1 && local[((y + 3) * size) + x] == 1 && local[((y + 4) * size) + x] == 1 && local[((y + 5) * size) + x] == 0 && local[((y + 6) * size) + x] == 1) {
                    // Pattern found, check before and after
                    light = true;
                    for (i = y - 4; i < y; i++) {
                        if (i >= 0 && local[(i * size) + x] == 1) {
                            light = false;
                            break;
                        }
                    }
                    if (light) {
                        // Pattern is preceded by light area 4 modules wide
                        result += 40;
                    } else {
                        light = true;
                        for (i = y + 7; i <= y + 10; i++) {
                            if (i < size && local[(i * size) + x] == 1) {
                                light = false;
                                break;
                            }
                        }
                        if (light) {
                            // Pattern is followed by light area 4 modules wide
                            result += 40;
                        }
                    }
                }
            }
        }
        /* Horizontal */
        for (y = 0; y < size; y++) {
            for (x = 0; x <= (size - 7); x++) {
                if (local[(y * size) + x + 0] == 1 && local[(y * size) + x + 1] == 0 && local[(y * size) + x + 2] == 1 && local[(y * size) + x + 3] == 1 && local[(y * size) + x + 4] == 1 && local[(y * size) + x + 5] == 0 && local[(y * size) + x + 6] == 1) {
                    // Pattern found, check before and after
                    light = true;
                    for (i = x - 4; i < x; i++) {
                        if (i >= 0 && local[(y * size) + i] == 1) {
                            light = false;
                            break;
                        }
                    }
                    if (light) {
                        // Pattern is preceded by light area 4 modules wide
                        result += 40;
                    } else {
                        light = true;
                        for (i = x + 7; i <= x + 10; i++) {
                            if (i < size && local[(y * size) + i] == 1) {
                                light = false;
                                break;
                            }
                        }
                        if (light) {
                            // Pattern is followed by light area 4 modules wide
                            result += 40;
                        }
                    }
                }
            }
        }
        encodeInfo.append(result).append(' ');
        if (result > best) {
            encodeInfo.append("EXIT\n");
            return result;
        }
        /* Test 4: Proportion of dark modules in entire symbol */
        percentage = (int) (100d * dark_mods / (size * size));
        k = Math.abs(percentage - 50) / 5;
        result += 10 * k;
        encodeInfo.append(result).append('\n');
        return result;
    }

    /* Adds format information to grid. */
    private static void addFormatInfo(int[] grid, int size, EccLevel ecc_level, int pattern) {
        int format;
        int seq;
        int i;
        switch(ecc_level) {
            case L:
                format = pattern | 0x08;
                break;
            case Q:
                format = pattern | 0x18;
                break;
            case H:
                format = pattern | 0x10;
                break;
            case M:
                format = pattern;
                break;
            default:
                throw new OkapiInternalException("Unknown ECC level: " + ecc_level);
        }
        seq = QR_ANNEX_C[format];
        for (i = 0; i < 6; i++) {
            grid[(i * size) + 8] += (seq >> i) & 0x01;
        }
        for (i = 0; i < 8; i++) {
            grid[(8 * size) + (size - i - 1)] += (seq >> i) & 0x01;
        }
        for (i = 0; i < 6; i++) {
            grid[(8 * size) + (5 - i)] += (seq >> (i + 9)) & 0x01;
        }
        for (i = 0; i < 7; i++) {
            grid[(((size - 7) + i) * size) + 8] += (seq >> (i + 8)) & 0x01;
        }
        grid[(7 * size) + 8] += (seq >> 6) & 0x01;
        grid[(8 * size) + 8] += (seq >> 7) & 0x01;
        grid[(8 * size) + 7] += (seq >> 8) & 0x01;
    }

    /**
     * Adds version information.
     */
    private static void addVersionInfo(int[] grid, int size, int version) {
        int version_data = QR_ANNEX_D[version - 7];
        for (int i = 0; i < 6; i++) {
            grid[((size - 11) * size) + i] += (version_data >> (i * 3)) & 0x01;
            grid[((size - 10) * size) + i] += (version_data >> ((i * 3) + 1)) & 0x01;
            grid[((size - 9) * size) + i] += (version_data >> ((i * 3) + 2)) & 0x01;
            grid[(i * size) + (size - 11)] += (version_data >> (i * 3)) & 0x01;
            grid[(i * size) + (size - 10)] += (version_data >> ((i * 3) + 1)) & 0x01;
            grid[(i * size) + (size - 9)] += (version_data >> ((i * 3) + 2)) & 0x01;
        }
    }

    /**
     * Adds any final customization to the grid (none by default, but subclasses can override).
     */
    protected void customize(int[] grid, int size) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Creates a list of QR Code symbols for structured append from a string, using a template symbol.
     * The template's settings are cloned for each symbol created.
     *
     * @param data the input data
     * @param template the template QrCode symbol
     * @return a list of QrCode symbols with structured append set
     * @throws OkapiException if no data or data is invalid
     */
    public static List<QrCode> createStructuredAppendSymbols(String data, QrCode template) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static List<String> splitData(String data, QrCode template) {
        QrCode testSymbol = new QrCode() {

            // expensive plotting is not required
            @Override
            protected void plotSymbol() {
                throw new UnsupportedOperationException("STUB: not implemented");
            }
        };
        clone(template, testSymbol);
        testSymbol.setStructuredAppendTotal(2);
        return split(data, testSymbol, QrCode::fits, MAX_STRUCTURED_APPEND_SYMBOLS);
    }

    private static boolean fits(String data, QrCode testSymbol, boolean last) {
        if (!data.isEmpty()) {
            try {
                testSymbol.setContent(data);
            } catch (OkapiInputException e) {
                return false;
            }
        }
        return true;
    }

    private static List<QrCode> createStructuredAppendSymbols(List<String> split, int parity, QrCode template) {
        int count = split.size();
        List<QrCode> symbols = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String data = split.get(i);
            QrCode symbol = new QrCode();
            symbols.add(symbol);
            clone(template, symbol);
            symbol.setStructuredAppendPosition(i + 1);
            symbol.setStructuredAppendTotal(count);
            symbol.setStructuredAppendParity(parity);
            symbol.setContent(data);
        }
        return symbols;
    }

    private static int calculateStructuredAppendParity(String content, QrCode template) {
        EciMode eci = determineEci(content, template.eciMode);
        int[] bytes = toBytes(content, eci.charset);
        int parity = 0;
        for (int b : bytes) {
            parity ^= b & 0xFF;
        }
        return parity;
    }

    private static void clone(QrCode template, QrCode target) {
        target.setFontName(template.getFontName());
        target.setFontSize(template.getFontSize());
        target.setDataType(template.getDataType());
        target.setEmptyContentAllowed(template.getEmptyContentAllowed());
        target.setHumanReadableAlignment(template.getHumanReadableAlignment());
        target.setHumanReadableLocation(template.getHumanReadableLocation());
        target.setModuleWidth(template.getModuleWidth());
        target.setQuietZoneHorizontal(template.getQuietZoneHorizontal());
        target.setQuietZoneVertical(template.getQuietZoneVertical());
        target.setReaderInit(template.getReaderInit());
        target.setBarHeight(template.getBarHeight());
        target.setForceByteCompaction(template.getForceByteCompaction());
        target.setPreferredEccLevel(template.getPreferredEccLevel());
        if (template.getPreferredVersion() != 0) {
            target.setPreferredVersion(template.getPreferredVersion());
        }
    }
}
