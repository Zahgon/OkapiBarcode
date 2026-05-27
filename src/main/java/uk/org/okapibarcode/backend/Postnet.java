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
 * <p>Implements <a href="http://en.wikipedia.org/wiki/POSTNET">POSTNET</a> and
 * <a href="http://en.wikipedia.org/wiki/Postal_Alpha_Numeric_Encoding_Technique">PLANET</a>
 * bar code symbologies.
 *
 * <p>POSTNET and PLANET both use numerical input data and include a modulo-10 check digit.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Postnet extends Symbol {

    /**
     * Whether to encode a POSTNET or PLANET barcode.
     */
    public enum Mode {

        /**
         * Encode a PLANET barcode.
         */
        PLANET,
        /**
         * Encode a POSTNET barcode.
         */
        POSTNET
    }

    private static final String[] PN_TABLE = { "LLSSS", "SSSLL", "SSLSL", "SSLLS", "SLSSL", "SLSLS", "SLLSS", "LSSSL", "LSSLS", "LSLSS" };

    private static final String[] PL_TABLE = { "SSLLL", "LLLSS", "LLSLS", "LLSSL", "LSLLS", "LSLSL", "LSSLL", "SLLLS", "SLLSL", "SLSLL" };

    private Mode mode;

    private double moduleWidthRatio;

    /**
     * Creates a new instance, using mode {@link Mode#POSTNET}
     */
    public Postnet() {
        this(Mode.POSTNET);
    }

    /**
     * Creates a new instance, using the specified mode.
     *
     * @param mode the barcode mode (PLANET or POSTNET)
     */
    public Postnet(Mode mode) {
        this.mode = mode;
        this.moduleWidthRatio = 1.5;
        this.defaultHeight = 12;
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    /**
     * Sets the barcode mode (PLANET or POSTNET). The default mode is POSTNET.
     *
     * @param mode the barcode mode (PLANET or POSTNET)
     */
    public void setMode(Mode mode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the barcode mode (PLANET or POSTNET). The default mode is POSTNET.
     *
     * @return the barcode mode (PLANET or POSTNET)
     */
    public Mode getMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the ratio of space width to bar width. The default value is {@code 1.5} (spaces are 50% wider than bars).
     *
     * @param moduleWidthRatio the ratio of space width to bar width
     */
    public void setModuleWidthRatio(double moduleWidthRatio) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the ratio of space width to bar width.
     *
     * @return the ratio of space width to bar width
     */
    public double getModuleWidthRatio() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void encode(String[] table) {
        if (content.length() > 38) {
            throw OkapiInputException.inputTooLong();
        }
        if (!content.matches("[0-9]*")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        int sum = 0;
        int destLen = 7 + (content.length() * 5);
        StringBuilder dest = new StringBuilder(destLen);
        dest.append('L');
        for (int i = 0; i < content.length(); i++) {
            dest.append(table[content.charAt(i) - '0']);
            sum += content.charAt(i) - '0';
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        infoLine("Check Digit: ", checkDigit);
        dest.append(table[checkDigit]);
        dest.append('L');
        assert dest.length() == destLen;
        infoLine("Encoding: ", dest);
        readable = content;
        pattern = new String[] { dest.toString() };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
