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

import static uk.org.okapibarcode.backend.HumanReadableLocation.BOTTOM;
import static uk.org.okapibarcode.backend.HumanReadableLocation.NONE;
import static uk.org.okapibarcode.backend.HumanReadableLocation.TOP;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextAlignment;
import uk.org.okapibarcode.graphics.TextBox;
import uk.org.okapibarcode.output.PostScriptRenderer;
import uk.org.okapibarcode.output.SvgRenderer;

/**
 * <p>Implements EAN bar code symbology according to BS EN 797:1996.
 *
 * <p>European Article Number data can be encoded in EAN-8 or EAN-13 format requiring a 7-digit
 * or 12-digit input respectively. EAN-13 numbers map to Global Trade Identification Numbers (GTIN)
 * whereas EAN-8 symbols are generally for internal use only. Check digit is calculated and should not
 * be in input data. Leading zeroes are added as required.
 *
 * <p>Add-on content can be appended to the main symbol content by adding a {@code '+'} character,
 * followed by the add-on content (up to 5 digits).
 *
 * <p><b>NOTE:</b> When generating standalone documents like {@link SvgRenderer SVG}
 * or {@link PostScriptRenderer EPS}, it may be necessary to configure additional
 * {@link #setQuietZoneHorizontal(int) horizontal quiet zone} so that the
 * EAN-13 prefix digit (to the left of the symbol) is fully visible.
 *
 * @author <a href="mailto:jakel2006@me.com">Robert Elliott</a>
 */
public class Ean extends Symbol {

    /**
     * The different EAN barcode variants available to encode.
     */
    public enum Mode {

        /**
         * EAN-8
         */
        EAN8,
        /**
         * EAN-13
         */
        EAN13
    }

    private static final String[] EAN13_PARITY = { "AAAAAA", "AABABB", "AABBAB", "AABBBA", "ABAABB", "ABBAAB", "ABBBAA", "ABABAB", "ABABBA", "ABBABA" };

    private static final String[] EAN_SET_A = { "3211", "2221", "2122", "1411", "1132", "1231", "1114", "1312", "1213", "3112" };

    private static final String[] EAN_SET_B = { "1123", "1222", "2212", "1141", "2311", "1321", "4111", "2131", "3121", "2113" };

    private Mode mode;

    private int guardPatternExtraHeight = 5;

    private boolean linkageFlag;

    private EanUpcAddOn addOn;

    /**
     * Creates a new instance, using mode {@link Mode#EAN13}.
     */
    public Ean() {
        this(Mode.EAN13);
    }

    /**
     * Creates a new instance, using the specified mode.
     *
     * @param mode the EAN mode (EAN-8 or EAN-13)
     */
    public Ean(Mode mode) {
        this.mode = mode;
        this.humanReadableAlignment = TextAlignment.JUSTIFY;
    }

    /**
     * Sets the EAN mode (EAN-8 or EAN-13). The default is EAN-13.
     *
     * @param mode the EAN mode (EAN-8 or EAN-13)
     */
    public void setMode(Mode mode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the EAN mode (EAN-8 or EAN-13).
     *
     * @return the EAN mode (EAN-8 or EAN-13)
     */
    public Mode getMode() {
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
     * Returns the EAN add-on content which was encoded, if any. Only available after {@link #setContent(String)} is called.
     *
     * @return the EAN add-on content which was encoded, if any
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

    private void ean13() {
        content = validateAndPad(content, 12);
        char check = calcDigit(content);
        infoLine("Check Digit: ", check);
        String hrt = content + check;
        char parityChar = hrt.charAt(0);
        String parity = EAN13_PARITY[parityChar - '0'];
        infoLine("Parity Digit: ", parityChar);
        StringBuilder dest = new StringBuilder("111");
        for (int i = 1; i < 13; i++) {
            if (i == 7) {
                dest.append("11111");
            }
            if (i <= 6) {
                if (parity.charAt(i - 1) == 'B') {
                    dest.append(EAN_SET_B[hrt.charAt(i) - '0']);
                } else {
                    dest.append(EAN_SET_A[hrt.charAt(i) - '0']);
                }
            } else {
                dest.append(EAN_SET_A[hrt.charAt(i) - '0']);
            }
        }
        dest.append("111");
        readable = hrt;
        pattern = new String[] { dest.toString() };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    private void ean8() {
        content = validateAndPad(content, 7);
        char check = calcDigit(content);
        infoLine("Check Digit: ", check);
        String hrt = content + check;
        StringBuilder dest = new StringBuilder("111");
        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                dest.append("11111");
            }
            dest.append(EAN_SET_A[hrt.charAt(i) - '0']);
        }
        dest.append("111");
        readable = hrt;
        pattern = new String[] { dest.toString() };
        rowHeight = new int[] { defaultHeight };
        rowCount = 1;
    }

    protected static String validateAndPad(String s, int targetLength) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static char calcDigit(String s) {
        throw new UnsupportedOperationException("STUB: not implemented");
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
