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

import static uk.org.okapibarcode.backend.HumanReadableLocation.NONE;
import static uk.org.okapibarcode.backend.HumanReadableLocation.TOP;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextBox;

/**
 * Implements the Code 2 of 5 family of barcode standards.
 *
 * @author <a href="mailto:jakel2006@me.com">Robert Elliott</a>
 */
public class Code2Of5 extends Symbol {

    /**
     * The different Code 2 of 5 barcode variants available to encode.
     */
    public enum ToFMode {

        /**
         * Standard Code 2 of 5 mode, also known as Code 2 of 5 Matrix. Encodes any
         * length numeric input (digits 0-9). This is the default mode.
         */
        MATRIX,
        /**
         * Industrial Code 2 of 5 which can encode any length numeric input (digits 0-9)
         * and does not include a check digit.
         */
        INDUSTRIAL,
        /**
         * International Air Transport Agency variation of Code 2 of 5. Encodes any length
         * numeric input (digits 0-9) and does not include a check digit.
         */
        IATA,
        /**
         * Code 2 of 5 Data Logic. Encodes any length numeric input (digits 0-9) and does
         * not include a check digit.
         */
        DATA_LOGIC,
        /**
         * Interleaved Code 2 of 5. Encodes pairs of numbers, and so can only encode an even
         * number of digits (0-9). If an odd number of digits is entered a leading zero is
         * added. No check digit is calculated.
         */
        INTERLEAVED,
        /**
         * Interleaved Code 2 of 5 with check digit. Encodes pairs of numbers, and so can
         * only encode an even number of digits (0-9). If adding the check digit results
         * in an odd number of digits then a leading zero is added.
         */
        INTERLEAVED_WITH_CHECK_DIGIT,
        /**
         * ITF-14, also known as UPC Shipping Container Symbol or Case Code. Requires a
         * 13-digit numeric input (digits 0-9). One modulo-10 check digit is calculated.
         */
        ITF14,
        /**
         * Deutsche Post Leitcode. Requires a 13-digit numerical input. Check digit is
         * calculated.
         */
        DP_LEITCODE,
        /**
         * Deutsche Post Identcode. Requires an 11-digit numerical input. Check digit is
         * calculated.
         */
        DP_IDENTCODE
    }

    private static final String[] C25_MATRIX_TABLE = { "113311", "311131", "131131", "331111", "113131", "313111", "133111", "111331", "311311", "131311" };

    private static final String[] C25_INDUSTRIAL_TABLE = { "1111313111", "3111111131", "1131111131", "3131111111", "1111311131", "3111311111", "1131311111", "1111113131", "3111113111", "1131113111" };

    private static final String[] C25_INTERLEAVED_TABLE = { "11331", "31113", "13113", "33111", "11313", "31311", "13311", "11133", "31131", "13131" };

    /**
     * The 2-of-5 mode.
     */
    private ToFMode mode;

    /**
     * Ratio of wide bar width to narrow bar width.
     */
    private double moduleWidthRatio = 3;

    /**
     * Creates a new instance, using mode {@link ToFMode#MATRIX}.
     */
    public Code2Of5() {
        this(ToFMode.MATRIX);
    }

    /**
     * Creates a new instance, using the specified mode.
     *
     * @param mode the 2-of-5 mode
     */
    public Code2Of5(ToFMode mode) {
        this.mode = mode;
    }

    /**
     * Sets the 2-of-5 mode. The default value is {@link ToFMode#MATRIX}.
     *
     * @param mode the 2-of-5 mode
     */
    public void setMode(ToFMode mode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the 2-of-5 mode.
     *
     * @return the 2-of-5 mode
     */
    public ToFMode getMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the ratio of wide bar width to narrow bar width. Valid values are usually
     * between {@code 2} and {@code 3}. The default value is {@code 3}.
     *
     * @param moduleWidthRatio the ratio of wide bar width to narrow bar width
     */
    public void setModuleWidthRatio(double moduleWidthRatio) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the ratio of wide bar width to narrow bar width.
     *
     * @return the ratio of wide bar width to narrow bar width
     */
    public double getModuleWidthRatio() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void dataMatrix() {
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        String dest = "311111";
        for (int i = 0; i < content.length(); i++) {
            dest += C25_MATRIX_TABLE[Character.getNumericValue(content.charAt(i))];
        }
        dest += "31111";
        readable = content;
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void industrial() {
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        String dest = "313111";
        for (int i = 0; i < content.length(); i++) {
            dest += C25_INDUSTRIAL_TABLE[Character.getNumericValue(content.charAt(i))];
        }
        dest += "31113";
        readable = content;
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void iata() {
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        String dest = "1111";
        for (int i = 0; i < content.length(); i++) {
            dest += C25_INDUSTRIAL_TABLE[Character.getNumericValue(content.charAt(i))];
        }
        dest += "311";
        readable = content;
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void dataLogic() {
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        String dest = "1111";
        for (int i = 0; i < content.length(); i++) {
            dest += C25_MATRIX_TABLE[Character.getNumericValue(content.charAt(i))];
        }
        dest += "311";
        readable = content;
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void interleaved(boolean addCheckDigit) {
        int i;
        String dest;
        readable = content;
        if (!readable.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        if (addCheckDigit) {
            char checkDigit = checkDigit(readable, 1, 3);
            readable += checkDigit;
            infoLine("Check Digit: ", checkDigit);
        }
        if ((readable.length() & 1) != 0) {
            readable = "0" + readable;
        }
        dest = "1111";
        for (i = 0; i < readable.length(); i += 2) {
            dest += interlace(i, i + 1);
        }
        dest += "311";
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private String interlace(int x, int y) {
        char a = readable.charAt(x);
        char b = readable.charAt(y);
        String one = C25_INTERLEAVED_TABLE[Character.getNumericValue(a)];
        String two = C25_INTERLEAVED_TABLE[Character.getNumericValue(b)];
        StringBuilder f = new StringBuilder(10);
        for (int i = 0; i < 5; i++) {
            f.append(one.charAt(i));
            f.append(two.charAt(i));
        }
        return f.toString();
    }

    private void itf14() {
        int i;
        int input_length = content.length();
        String dest;
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        if (input_length > 13) {
            throw OkapiInputException.inputTooLong();
        }
        readable = "";
        for (i = input_length; i < 13; i++) {
            readable += "0";
        }
        readable += content;
        char checkDigit = checkDigit(readable, 1, 3);
        readable += checkDigit;
        infoLine("Check Digit: ", checkDigit);
        dest = "1111";
        for (i = 0; i < readable.length(); i += 2) {
            dest += interlace(i, i + 1);
        }
        dest += "311";
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void deutschePostLeitcode() {
        int i;
        int input_length = content.length();
        String dest;
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        if (input_length > 13) {
            throw OkapiInputException.inputTooLong();
        }
        readable = "";
        for (i = input_length; i < 13; i++) {
            readable += "0";
        }
        readable += content;
        char checkDigit = checkDigit(readable, 9, 4);
        readable += checkDigit;
        infoLine("Check digit: ", checkDigit);
        dest = "1111";
        for (i = 0; i < readable.length(); i += 2) {
            dest += interlace(i, i + 1);
        }
        dest += "311";
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void deutschePostIdentcode() {
        int i;
        int input_length = content.length();
        String dest;
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        if (input_length > 11) {
            throw OkapiInputException.inputTooLong();
        }
        readable = "";
        for (i = input_length; i < 11; i++) {
            readable += "0";
        }
        readable += content;
        char checkDigit = checkDigit(readable, 9, 4);
        readable += checkDigit;
        infoLine("Check Digit: ", checkDigit);
        dest = "1111";
        for (i = 0; i < readable.length(); i += 2) {
            dest += interlace(i, i + 1);
        }
        dest += "311";
        pattern = new String[] { dest };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private static char checkDigit(String s, int multiplier1, int multiplier2) {
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if ((i & 1) != 0) {
                count += multiplier1 * (s.charAt(i) - '0');
            } else {
                count += multiplier2 * (s.charAt(i) - '0');
            }
        }
        return (char) (((10 - (count % 10)) % 10) + '0');
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double getModuleWidth(int originalWidth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
