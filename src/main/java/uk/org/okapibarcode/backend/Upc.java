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

import static uk.org.okapibarcode.backend.Ean.calcDigit;
import static uk.org.okapibarcode.backend.Ean.validateAndPad;
import static uk.org.okapibarcode.backend.HumanReadableLocation.BOTTOM;
import static uk.org.okapibarcode.backend.HumanReadableLocation.NONE;
import static uk.org.okapibarcode.backend.HumanReadableLocation.TOP;
import java.util.Arrays;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextAlignment;
import uk.org.okapibarcode.graphics.TextBox;
import uk.org.okapibarcode.output.PostScriptRenderer;
import uk.org.okapibarcode.output.SvgRenderer;

/**
 * <p>Implements UPC bar code symbology according to BS EN 797:1996.
 *
 * <p>UPC-A requires an 11 digit article number. The check digit is calculated.
 * UPC-E is a zero-compressed version of UPC-A developed for smaller packages.
 * The code requires a 6 digit article number (digits 0-9). The check digit
 * is calculated. Also supports Number System 1 encoding by entering a 7-digit
 * article number stating with the digit 1.
 *
 * <p>EAN-2 and EAN-5 add-on symbols can be added using the '+' character followed
 * by the add-on data.
 *
 * <p><b>NOTE:</b> When generating standalone documents like {@link SvgRenderer SVG}
 * or {@link PostScriptRenderer EPS}, it may be necessary to configure additional
 * {@link #setQuietZoneHorizontal(int) horizontal quiet zone} so that the
 * prefix digit (to the left of the symbol) and check digit (to the right of
 * the symbol) are fully visible.
 *
 * @author <a href="mailto:jakel2006@me.com">Robert Elliott</a>
 */
public class Upc extends Symbol {

    /**
     * The different UPC barcode variants available to encode.
     */
    public enum Mode {

        /**
         * UPC-A
         */
        UPCA,
        /**
         * UPC-E
         */
        UPCE
    }

    private static final String[] SET_AC = { "3211", "2221", "2122", "1411", "1132", "1231", "1114", "1312", "1213", "3112" };

    private static final String[] SET_B = { "1123", "1222", "2212", "1141", "2311", "1321", "4111", "2131", "3121", "2113" };

    /* Number set for UPC-E symbol (EN Table 4) */
    private static final String[] UPC_PARITY_0 = { "BBBAAA", "BBABAA", "BBAABA", "BBAAAB", "BABBAA", "BAABBA", "BAAABB", "BABABA", "BABAAB", "BAABAB" };

    /* Not covered by BS EN 797 */
    private static final String[] UPC_PARITY_1 = { "AAABBB", "AABABB", "AABBAB", "AABBBA", "ABAABB", "ABBAAB", "ABBBAA", "ABABAB", "ABABBA", "ABBABA" };

    private Mode mode;

    private boolean showCheckDigit = true;

    private int guardPatternExtraHeight = 5;

    private boolean linkageFlag;

    private EanUpcAddOn addOn;

    /**
     * Creates a new instance, using mode {@link Mode#UPCA}.
     */
    public Upc() {
        this(Mode.UPCA);
    }

    /**
     * Creates a new instance, using the specified mode.
     *
     * @param mode the UPC mode (UPC-A or UPC-E)
     */
    public Upc(Mode mode) {
        this.mode = mode;
        this.humanReadableAlignment = TextAlignment.JUSTIFY;
    }

    /**
     * Sets the UPC mode (UPC-A or UPC-E). The default is UPC-A.
     *
     * @param mode the UPC mode (UPC-A or UPC-E)
     */
    public void setMode(Mode mode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the UPC mode (UPC-A or UPC-E).
     *
     * @return the UPC mode (UPC-A or UPC-E)
     */
    public Mode getMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets whether or not to show the check digit in the human-readable text.
     *
     * @param showCheckDigit whether or not to show the check digit in the human-readable text
     */
    public void setShowCheckDigit(boolean showCheckDigit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns whether or not to show the check digit in the human-readable text.
     *
     * @return whether or not to show the check digit in the human-readable text
     */
    public boolean getShowCheckDigit() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the extra height used for the guard patterns. The default value is <code>5</code>.
     *
     * @param guardPatternExtraHeight the extra height used for the guard patterns
     */
    public void setGuardPatternExtraHeight(int guardPatternExtraHeight) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the extra height used for the guard patterns.
     *
     * @return the extra height used for the guard patterns
     */
    public int getGuardPatternExtraHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the UPC add-on content which was encoded, if any. Only available after {@link #setContent(String)} is called.
     *
     * @return the UPC add-on content which was encoded, if any
     */
    public String getAddOnContent() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the linkage flag. If set to <code>true</code>, this symbol is part of a composite symbol.
     *
     * @param linkageFlag the linkage flag
     */
    protected void setLinkageFlag(boolean linkageFlag) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private void separateContent() {
        int splitPoint = content.indexOf('+');
        if (splitPoint == -1) {
            // there is no add-on data
            addOn = null;
        } else if (splitPoint == content.length() - 1) {
            // we found the add-on separator, but no add-on data
            throw new OkapiInputException("Invalid add-on data");
        } else {
            // there is a '+' in the input data, use an add-on EAN2 or EAN5
            addOn = new EanUpcAddOn();
            addOn.font = this.font;
            addOn.fontName = this.fontName;
            addOn.fontSize = this.fontSize;
            addOn.humanReadableLocation = (this.humanReadableLocation == NONE ? NONE : TOP);
            addOn.moduleWidth = this.moduleWidth;
            addOn.defaultHeight = this.defaultHeight + this.guardPatternExtraHeight - 8;
            addOn.setContent(content.substring(splitPoint + 1));
            content = content.substring(0, splitPoint);
        }
    }

    private void upca() {
        content = validateAndPad(content, 11);
        char check = calcDigit(content);
        infoLine("Check Digit: ", check);
        String hrt = content + check;
        int length = 3 + (12 * 4) + 5 + 3;
        StringBuilder dest = new StringBuilder(length);
        dest.append("111");
        for (int i = 0; i < 12; i++) {
            if (i == 6) {
                dest.append("11111");
            }
            dest.append(SET_AC[hrt.charAt(i) - '0']);
        }
        dest.append("111");
        readable = hrt;
        pattern = new String[] { dest.toString() };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void upce() {
        content = validateAndPad(content, 7);
        String expanded = expandToEquivalentUpcA(content, true);
        infoLine("UPC-A Equivalent: ", expanded);
        char check = calcDigit(expanded);
        infoLine("Check Digit: ", check);
        String hrt = content + check;
        int numberSystem = getNumberSystem(content);
        String[] parityArray = (numberSystem == 1 ? UPC_PARITY_1 : UPC_PARITY_0);
        String parity = parityArray[check - '0'];
        int length = 3 + (6 * 4) + 6;
        StringBuilder dest = new StringBuilder(length);
        dest.append("111");
        for (int i = 0; i < 6; i++) {
            if (parity.charAt(i) == 'A') {
                dest.append(SET_AC[content.charAt(i + 1) - '0']);
            } else {
                // B
                dest.append(SET_B[content.charAt(i + 1) - '0']);
            }
        }
        dest.append("111111");
        readable = hrt;
        pattern = new String[] { dest.toString() };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    /**
     * Expands the zero-compressed UPC-E code to make a UPC-A equivalent (EN Table 5).
     *
     * @param content the UPC-E code to expand
     * @param validate whether or not to validate the input
     * @return the UPC-A equivalent of the specified UPC-E code
     */
    protected String expandToEquivalentUpcA(String content, boolean validate) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Two number systems can be used: system 0 and system 1.
     */
    private static int getNumberSystem(String content) {
        switch(content.charAt(0)) {
            case '0':
                return 0;
            case '1':
                return 1;
            default:
                throw new OkapiInputException("Invalid input data");
        }
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Scales the specified width or x-dimension according to the current module width.
     */
    private int scale(int w) {
        return moduleWidth * w;
    }
}
