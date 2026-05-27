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

import static uk.org.okapibarcode.backend.DataBarLimited.getWidths;
import static uk.org.okapibarcode.util.Strings.binaryAppend;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Implements GS1 DataBar Expanded Omnidirectional and GS1 DataBar Expanded Stacked
 * Omnidirectional according to ISO/IEC 24724:2011.
 *
 * <p>DataBar expanded encodes GS1 data in either a linear or stacked format.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class DataBarExpanded extends Symbol {

    private static final int[] G_SUM_EXP = { 0, 348, 1388, 2948, 3988 };

    private static final int[] T_EVEN_EXP = { 4, 20, 52, 104, 204 };

    private static final int[] MODULES_ODD_EXP = { 12, 10, 8, 6, 4 };

    private static final int[] MODULES_EVEN_EXP = { 5, 7, 9, 11, 13 };

    private static final int[] WIDEST_ODD_EXP = { 7, 5, 4, 3, 1 };

    private static final int[] WIDEST_EVEN_EXP = { 2, 4, 5, 6, 8 };

    /**
     * Table 14
     */
    private static final int[] CHECKSUM_WEIGHT_EXP = { 1, 3, 9, 27, 81, 32, 96, 77, 20, 60, 180, 118, 143, 7, 21, 63, 189, 145, 13, 39, 117, 140, 209, 205, 193, 157, 49, 147, 19, 57, 171, 91, 62, 186, 136, 197, 169, 85, 44, 132, 185, 133, 188, 142, 4, 12, 36, 108, 113, 128, 173, 97, 80, 29, 87, 50, 150, 28, 84, 41, 123, 158, 52, 156, 46, 138, 203, 187, 139, 206, 196, 166, 76, 17, 51, 153, 37, 111, 122, 155, 43, 129, 176, 106, 107, 110, 119, 146, 16, 48, 144, 10, 30, 90, 59, 177, 109, 116, 137, 200, 178, 112, 125, 164, 70, 210, 208, 202, 184, 130, 179, 115, 134, 191, 151, 31, 93, 68, 204, 190, 148, 22, 66, 198, 172, 94, 71, 2, 6, 18, 54, 162, 64, 192, 154, 40, 120, 149, 25, 75, 14, 42, 126, 167, 79, 26, 78, 23, 69, 207, 199, 175, 103, 98, 83, 38, 114, 131, 182, 124, 161, 61, 183, 127, 170, 88, 53, 159, 55, 165, 73, 8, 24, 72, 5, 15, 45, 135, 194, 160, 58, 174, 100, 89 };

    /**
     * Table 15
     */
    private static final int[] FINDER_PATTERN_EXP = { 1, 8, 4, 1, 1, 1, 1, 4, 8, 1, 3, 6, 4, 1, 1, 1, 1, 4, 6, 3, 3, 4, 6, 1, 1, 1, 1, 6, 4, 3, 3, 2, 8, 1, 1, 1, 1, 8, 2, 3, 2, 6, 5, 1, 1, 1, 1, 5, 6, 2, 2, 2, 9, 1, 1, 1, 1, 9, 2, 2 };

    /**
     * Table 16
     */
    private static final int[] FINDER_SEQUENCE = { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 6, 3, 8, 0, 0, 0, 0, 0, 0, 0, 1, 10, 3, 8, 5, 0, 0, 0, 0, 0, 0, 1, 10, 3, 8, 7, 12, 0, 0, 0, 0, 0, 1, 10, 3, 8, 9, 12, 11, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 10, 9, 0, 0, 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 0, 1, 2, 3, 4, 5, 8, 7, 10, 9, 12, 11 };

    private static final int[] WEIGHT_ROWS = { 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 10, 3, 4, 13, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 18, 3, 4, 13, 14, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 18, 3, 4, 13, 14, 11, 12, 21, 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17, 18, 3, 4, 13, 14, 15, 16, 21, 22, 19, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 17, 18, 15, 16, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 17, 18, 19, 20, 21, 22, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 14, 11, 12, 17, 18, 15, 16, 21, 22, 19, 20 };

    protected enum EncodeMode {

        NUMERIC, ALPHA, ISOIEC, ANY_ENC, ALPHA_OR_ISO
    }

    private boolean linkageFlag;

    private int preferredColumns = 2;

    private boolean stacked = true;

    public DataBarExpanded() {
        inputDataType = DataType.GS1;
    }

    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the preferred width of a stacked symbol by selecting the number of "columns" or symbol segments in each row of data.
     *
     * @param columns the number of segments in each row
     */
    public void setPreferredColumns(int columns) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the preferred width of a stacked symbol by selecting the number of "columns" or symbol segments in each row of data.
     *
     * @return the number of segments in each row
     */
    public int getPreferredColumns() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets whether or not this symbology is stacked.
     *
     * @param stacked {@code true} for GS1 DataBar Expanded Stacked Omnidirectional, {@code false} for GS1 DataBar Expanded Omnidirectional
     */
    public void setStacked(boolean stacked) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns whether or not this symbology is stacked.
     *
     * @return {@code true} for GS1 DataBar Expanded Stacked Omnidirectional, {@code false} for GS1 DataBar Expanded Omnidirectional
     */
    public boolean isStacked() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void setLinkageFlag(boolean linkageFlag) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static String separator(CharSequence pattern, int cols, boolean below, boolean specialCaseRow, boolean leftToRight, boolean oddLastRow, AtomicBoolean v2mutable) {
        // start with the complement of the linear symbol
        StringBuilder linearBin = new StringBuilder();
        StringBuilder separator = new StringBuilder();
        boolean black = true;
        for (int i = 0; i < pattern.length(); i++) {
            int c = pattern.charAt(i) - '0';
            for (int j = 0; j < c; j++) {
                linearBin.append(black ? '1' : '0');
                separator.append(black ? '0' : '1');
            }
            black = !black;
        }
        // clear first 4 and last 4 modules
        for (int i = 0; i < 4; i++) {
            separator.setCharAt(i, '0');
            separator.setCharAt(separator.length() - 1 - i, '0');
        }
        // finder adjustments
        boolean space = false;
        boolean v2 = v2mutable.get();
        for (int j = 0; j < cols; j++) {
            // 49 == data (17) + finder (15) + data(17) triplet
            // 19 == 2 (guard) + 17 (initial check/data character)
            int k = (49 * j) + 19 + (specialCaseRow ? 1 : 0);
            if (leftToRight) {
                // version 1 finder: first 13 modules
                // version 2 finder: last 13 modules
                int start = v2 ? 2 : 0;
                int end = v2 ? 15 : 13;
                for (int i = start; i < end; i++) {
                    if (i + k < linearBin.length()) {
                        if (linearBin.charAt(i + k) == '1') {
                            separator.setCharAt(i + k, '0');
                            space = false;
                        } else {
                            separator.setCharAt(i + k, space ? '0' : '1');
                            space = !space;
                        }
                    }
                }
            } else {
                if (oddLastRow) {
                    // no data char at beginning of row (ends with finder)
                    k -= 17;
                }
                // version 1 finder: first 13 modules
                // version 2 finder: last 13 modules
                int start = v2 ? 14 : 12;
                int end = v2 ? 2 : 0;
                for (int i = start; i >= end; i--) {
                    if (i + k < linearBin.length()) {
                        if (linearBin.charAt(i + k) == '1') {
                            separator.setCharAt(i + k, '0');
                            space = false;
                        } else {
                            separator.setCharAt(i + k, space ? '0' : '1');
                            space = !space;
                        }
                    }
                }
            }
            v2 = !v2;
        }
        if (below) {
            v2mutable.set(v2);
        }
        return bin2pat(separator);
    }

    /**
     * Handles all data encodation from section 7.2.5 of ISO/IEC 24724.
     */
    private static int calculateBinaryString(boolean stacked, int blocksPerRow, int[] inputData, StringBuilder binaryString) {
        EncodeMode lastMode = EncodeMode.NUMERIC;
        int remainder, d1, d2, value;
        String padstring;
        /* Decide whether a compressed data field is required and if so what method to use: method 2 = no compressed data field */
        int encodingMethod;
        if (inputData.length >= 16 && inputData[0] == '0' && inputData[1] == '1') {
            /* (01) and other AIs */
            encodingMethod = 1;
        } else {
            /* any AIs */
            encodingMethod = 2;
        }
        if (inputData.length >= 20 && encodingMethod == 1 && inputData[2] == '9' && inputData[16] == '3') {
            /* Possibly encoding method > 2 */
            if (inputData.length >= 26 && inputData[17] == '1') {
                /* Methods 3, 7, 9, 11 and 13 */
                if (inputData[18] == '0') {
                    /* (01) and (310x), weight in kilos */
                    double weight = 0;
                    for (int i = 0; i < 6; i++) {
                        weight *= 10;
                        weight += (inputData[20 + i] - '0');
                    }
                    if (weight < 99_999) {
                        /* Maximum weight = 99999 */
                        if (inputData[19] == '3' && inputData.length == 26) {
                            /* (01) and (3103) */
                            weight /= 1000.0;
                            if (weight <= 32.767) {
                                encodingMethod = 3;
                            }
                        }
                        if (inputData.length == 34) {
                            if (inputData[26] == '1' && inputData[27] == '1') {
                                /* (01), (310x) and (11) - metric weight and production date */
                                encodingMethod = 7;
                            }
                            if (inputData[26] == '1' && inputData[27] == '3') {
                                /* (01), (310x) and (13) - metric weight and packaging date */
                                encodingMethod = 9;
                            }
                            if (inputData[26] == '1' && inputData[27] == '5') {
                                /* (01), (310x) and (15) - metric weight and "best before" date */
                                encodingMethod = 11;
                            }
                            if (inputData[26] == '1' && inputData[27] == '7') {
                                /* (01), (310x) and (17) - metric weight and expiration date */
                                encodingMethod = 13;
                            }
                        }
                    }
                }
            }
            if (inputData.length >= 26 && inputData[17] == '2') {
                /* Methods 4, 8, 10, 12 and 14 */
                if (inputData[18] == '0') {
                    /* (01) and (320x), weight in pounds */
                    double weight = 0;
                    for (int i = 0; i < 6; i++) {
                        weight *= 10;
                        weight += (inputData[20 + i] - '0');
                    }
                    if (weight < 99_999) {
                        /* Maximum weight = 99999 */
                        if ((inputData[19] == '2' || inputData[19] == '3') && (inputData.length == 26)) {
                            /* (01) and (3202)/(3203) */
                            if (inputData[19] == '3') {
                                weight /= 1000.0;
                                if (weight <= 22.767) {
                                    encodingMethod = 4;
                                }
                            } else {
                                weight /= 100.0;
                                if (weight <= 99.99) {
                                    encodingMethod = 4;
                                }
                            }
                        }
                        if (inputData.length == 34) {
                            if (inputData[26] == '1' && inputData[27] == '1') {
                                /* (01), (320x) and (11) - English weight and production date */
                                encodingMethod = 8;
                            }
                            if (inputData[26] == '1' && inputData[27] == '3') {
                                /* (01), (320x) and (13) - English weight and packaging date */
                                encodingMethod = 10;
                            }
                            if (inputData[26] == '1' && inputData[27] == '5') {
                                /* (01), (320x) and (15) - English weight and "best before" date */
                                encodingMethod = 12;
                            }
                            if (inputData[26] == '1' && inputData[27] == '7') {
                                /* (01), (320x) and (17) - English weight and expiration date */
                                encodingMethod = 14;
                            }
                        }
                    }
                }
            }
            if (inputData[17] == '9') {
                /* Methods 5 and 6 */
                if (inputData[18] == '2' && inputData[19] >= '0' && inputData[19] <= '3') {
                    /* (01) and (392x) */
                    encodingMethod = 5;
                }
                if (inputData[18] == '3' && inputData[19] >= '0' && inputData[19] <= '3') {
                    /* (01) and (393x) */
                    encodingMethod = 6;
                }
            }
        }
        /* Encoding method - Table 10 */
        /* Variable length symbol bit field is just given a place holder (XX) for the time being */
        int read_posn;
        switch(encodingMethod) {
            case 1:
                binaryString.append("1XX");
                read_posn = 16;
                break;
            case 2:
                binaryString.append("00XX");
                read_posn = 0;
                break;
            case 3:
                binaryString.append("0100");
                read_posn = inputData.length;
                break;
            case 4:
                binaryString.append("0101");
                read_posn = inputData.length;
                break;
            case 5:
                binaryString.append("01100XX");
                read_posn = 20;
                break;
            case 6:
                binaryString.append("01101XX");
                read_posn = 23;
                break;
            default:
                /* modes 7 (0111000) to 14 (0111111) */
                binaryString.append("0" + Integer.toBinaryString(56 + encodingMethod - 7));
                read_posn = inputData.length;
                break;
        }
        /* Verify that the data to be placed in the compressed data field is all numeric data before carrying out compression */
        for (int i = 0; i < read_posn; i++) {
            if (inputData[i] < '0' || inputData[i] > '9') {
                /* Something is wrong */
                throw OkapiInputException.invalidCharactersInInput();
            }
        }
        /* Now encode the compressed data field */
        if (encodingMethod == 1) {
            /* Encoding method field "1" - general item identification data */
            binaryAppend(binaryString, inputData[2] - '0', 4);
            for (int i = 1; i < 5; i++) {
                int group = parseInt(inputData, i * 3, 3);
                binaryAppend(binaryString, group, 10);
            }
        }
        if (encodingMethod == 3 || encodingMethod == 4) {
            /* Encoding method field "0100" - variable weight item (0,001 kilogram increments) */
            /* Encoding method field "0101" - variable weight item (0,01 or 0,001 pound increment) */
            for (int i = 1; i < 5; i++) {
                int group = parseInt(inputData, i * 3, 3);
                binaryAppend(binaryString, group, 10);
            }
            int group = parseInt(inputData, 20, 6);
            if (encodingMethod == 4 && inputData[19] == '3') {
                group += 10_000;
            }
            binaryAppend(binaryString, group, 15);
        }
        if (encodingMethod == 5 || encodingMethod == 6) {
            /* Encoding method field "01100" - variable measure item and price */
            /* Encoding method "01101" - variable measure item and price with ISO 4217 currency code */
            for (int i = 1; i < 5; i++) {
                int group = parseInt(inputData, i * 3, 3);
                binaryAppend(binaryString, group, 10);
            }
            binaryAppend(binaryString, inputData[19] - '0', 2);
            if (encodingMethod == 6) {
                int currency = parseInt(inputData, 20, 3);
                binaryAppend(binaryString, currency, 10);
            }
        }
        if (encodingMethod >= 7 && encodingMethod <= 14) {
            /* Encoding method fields "0111000" through "0111111" - variable weight item plus date */
            for (int i = 1; i < 5; i++) {
                int group = parseInt(inputData, i * 3, 3);
                binaryAppend(binaryString, group, 10);
            }
            int weight = inputData[19] - '0';
            for (int i = 0; i < 5; i++) {
                weight *= 10;
                weight += inputData[21 + i] - '0';
            }
            binaryAppend(binaryString, weight, 20);
            int date;
            if (inputData.length == 34) {
                /* Date information is included */
                date = parseInt(inputData, 28, 2) * 384;
                date += (parseInt(inputData, 30, 2) - 1) * 32;
                date += parseInt(inputData, 32, 2);
            } else {
                date = 38_400;
            }
            binaryAppend(binaryString, date, 16);
        }
        /* The compressed data field has been processed if appropriate - the rest of the data (if any) goes into a general-purpose data compaction field */
        int[] generalField = Arrays.copyOfRange(inputData, read_posn, inputData.length);
        if (generalField.length != 0) {
            EncodeMode[] generalFieldType = getInitialEncodeModes(generalField);
            // modifies generalFieldType
            boolean trailingDigit = applyGeneralFieldRules(generalFieldType);
            // modifies binaryString
            lastMode = appendToBinaryString(generalField, generalFieldType, trailingDigit, false, binaryString);
            remainder = calculateRemainder(binaryString.length(), stacked, blocksPerRow);
            if (trailingDigit) {
                /* There is still one more numeric digit to encode */
                int i = generalField.length - 1;
                if (lastMode == EncodeMode.NUMERIC) {
                    if (remainder >= 4 && remainder <= 6) {
                        value = generalField[i] - '0';
                        value++;
                        binaryAppend(binaryString, value, 4);
                    } else {
                        d1 = generalField[i] - '0';
                        d2 = 10;
                        value = (11 * d1) + d2 + 8;
                        binaryAppend(binaryString, value, 7);
                    }
                } else {
                    value = generalField[i] - 43;
                    binaryAppend(binaryString, value, 5);
                }
            }
        }
        if (binaryString.length() > 252) {
            throw OkapiInputException.inputTooLong();
        }
        remainder = calculateRemainder(binaryString.length(), stacked, blocksPerRow);
        /* Now add padding to binary string (7.2.5.5.4) */
        int i = remainder;
        if (lastMode == EncodeMode.NUMERIC) {
            padstring = "0000";
            i -= 4;
        } else {
            padstring = "";
        }
        for (; i > 0; i -= 5) {
            padstring += "00100";
        }
        binaryString.append(padstring.substring(0, remainder));
        /* Patch variable length symbol bit field */
        char patchEvenOdd, patchSize;
        if ((((binaryString.length() / 12) + 1) & 1) == 0) {
            patchEvenOdd = '0';
        } else {
            patchEvenOdd = '1';
        }
        if (binaryString.length() <= 156) {
            patchSize = '0';
        } else {
            patchSize = '1';
        }
        if (encodingMethod == 1) {
            binaryString.setCharAt(2, patchEvenOdd);
            binaryString.setCharAt(3, patchSize);
        }
        if (encodingMethod == 2) {
            binaryString.setCharAt(3, patchEvenOdd);
            binaryString.setCharAt(4, patchSize);
        }
        if (encodingMethod == 5 || encodingMethod == 6) {
            binaryString.setCharAt(6, patchEvenOdd);
            binaryString.setCharAt(7, patchSize);
        }
        return encodingMethod;
    }

    private static int calculateRemainder(int binaryStringLength, boolean stacked, int blocksPerRow) {
        int remainder = 12 - (binaryStringLength % 12);
        if (remainder == 12) {
            remainder = 0;
        }
        if (binaryStringLength < 36) {
            remainder = 36 - binaryStringLength;
        }
        if (stacked) {
            // +1 for check digit
            int symbolChars = ((binaryStringLength + remainder) / 12) + 1;
            int symbolCharsInLastRow = symbolChars % (blocksPerRow * 2);
            if (symbolCharsInLastRow == 1) {
                // 7.2.8: The last row shall contain a minimum of two symbol characters with extra padding, if needed.
                remainder += 12;
            }
        }
        return remainder;
    }

    /**
     * Logs binary string as hexadecimal
     */
    private void logBinaryStringInfo(StringBuilder binaryString) {
        infoLine("Binary Length: ", binaryString.length());
        info("Binary String: ");
        int nibble = 0;
        for (int i = 0; i < binaryString.length(); i++) {
            switch(i % 4) {
                case 0:
                    if (binaryString.charAt(i) == '1') {
                        nibble += 8;
                    }
                    break;
                case 1:
                    if (binaryString.charAt(i) == '1') {
                        nibble += 4;
                    }
                    break;
                case 2:
                    if (binaryString.charAt(i) == '1') {
                        nibble += 2;
                    }
                    break;
                case 3:
                    if (binaryString.charAt(i) == '1') {
                        nibble += 1;
                    }
                    info(Integer.toHexString(nibble));
                    nibble = 0;
                    break;
            }
        }
        if ((binaryString.length() % 4) != 0) {
            info(Integer.toHexString(nibble));
        }
        infoLine();
    }

    protected static EncodeMode[] getInitialEncodeModes(int[] generalField) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Attempts to apply encoding rules from sections 7.2.5.5.1 to 7.2.5.5.3 of ISO/IEC 24724:2006
     */
    protected static boolean applyGeneralFieldRules(EncodeMode[] generalFieldType) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static EncodeMode appendToBinaryString(int[] generalField, EncodeMode[] generalFieldType, boolean trailingDigit, boolean treatFnc1AsNumericLatch, StringBuilder binaryString) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static int parseInt(int[] chars, int index, int length) {
        int val = 0;
        int pow = (int) Math.pow(10, length - 1);
        for (int i = 0; i < length; i++) {
            int c = chars[index + i];
            val += (c - '0') * pow;
            pow /= 10;
        }
        return val;
    }
}
