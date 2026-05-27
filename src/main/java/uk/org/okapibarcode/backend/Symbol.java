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

import static uk.org.okapibarcode.backend.HumanReadableLocation.BOTTOM;
import static uk.org.okapibarcode.backend.HumanReadableLocation.NONE;
import static uk.org.okapibarcode.backend.HumanReadableLocation.TOP;
import static uk.org.okapibarcode.graphics.TextAlignment.CENTER;
import static uk.org.okapibarcode.util.Arrays.containsAt;
import static uk.org.okapibarcode.util.Arrays.positionOf;
import static uk.org.okapibarcode.util.Doubles.roughlyEqual;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import uk.org.okapibarcode.graphics.Circle;
import uk.org.okapibarcode.graphics.Hexagon;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextAlignment;
import uk.org.okapibarcode.graphics.TextBox;
import uk.org.okapibarcode.output.Java2DRenderer;
import uk.org.okapibarcode.util.EciMode;
import uk.org.okapibarcode.util.Gs1;

/**
 * Generic barcode symbology class.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public abstract class Symbol {

    // TODO: Setting attributes like module width, font size, etc should probably throw
    // an exception if set *after* encoding has already been completed.
    // TODO: GS1 data is encoded slightly differently depending on whether [AI]data content
    // is used, or if FNC1 escape sequences are used. We may want to make sure that they
    // encode to the same output.
    /**
     * The type of input data to expect in {@link #setContent(String)}.
     */
    public enum DataType {

        /**
         * Extended Channel Interpretations (the default).
         */
        ECI,
        /**
         * GS1 Application Identifier and data pairs in "[AI]DATA" format.
         */
        GS1,
        /**
         * Health Industry Bar Code number (without check digit).
         */
        HIBC
    }

    protected static final int FNC1 = -1;

    protected static final int FNC2 = -2;

    protected static final int FNC3 = -3;

    protected static final int FNC4 = -4;

    protected static final String FNC1_STRING = "\\<FNC1>";

    protected static final String FNC2_STRING = "\\<FNC2>";

    protected static final String FNC3_STRING = "\\<FNC3>";

    protected static final String FNC4_STRING = "\\<FNC4>";

    private static char[] HIBC_CHAR_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '.', ' ', '$', '/', '+', '%' };

    // user-specified values and settings
    protected DataType inputDataType = DataType.ECI;

    protected boolean readerInit;

    protected int defaultHeight = 40;

    protected int quietZoneHorizontal = 0;

    protected int quietZoneVertical = 0;

    protected int moduleWidth = 1;

    protected Font font;

    protected String fontName = "Helvetica";

    protected int fontSize = 8;

    protected HumanReadableLocation humanReadableLocation = BOTTOM;

    protected TextAlignment humanReadableAlignment = CENTER;

    protected boolean emptyContentAllowed = false;

    // internal state calculated when setContent() is called
    protected String content;

    protected int eciMode = -1;

    // usually bytes (values 0-255), but may also contain FNC flags
    protected int[] inputData;

    protected String readable = "";

    protected String[] pattern;

    protected int[] rowHeight;

    protected int rowCount = 0;

    protected int symbolHeight = 0;

    protected int symbolWidth = 0;

    protected StringBuilder encodeInfo = new StringBuilder();

    // note positions do not account for quiet zones (handled in renderers)
    protected List<TextBox> texts = new ArrayList<>();

    // note positions do not account for quiet zones (handled in renderers)
    protected List<Hexagon> hexagons = new ArrayList<>();

    // note positions do not account for quiet zones (handled in renderers)
    protected List<Circle> target = new ArrayList<>();

    // note positions do not account for quiet zones (handled in renderers)
    protected List<Rectangle> rectangles = new ArrayList<>();

    // x-position -> last seen rectangle at that position (optimization)
    protected Map<Double, Rectangle> prevRectangles = new HashMap<>();

    /**
     * <p>Sets the type of input data. This setting influences what pre-processing is done on
     * data before encoding in the symbol. For example: for <code>GS1</code> mode the AI
     * data will be used to calculate the position of 'FNC1' characters.
     *
     * <p>Valid values are:
     *
     * <ul>
     * <li><code>ECI</code> Extended Channel Interpretations (default)
     * <li><code>GS1</code> Application Identifier and data pairs in "[AI]DATA" format
     * <li><code>HIBC</code> Health Industry Bar Code number (without check digit)
     * </ul>
     *
     * @param dataType the type of input data
     */
    public void setDataType(DataType dataType) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the type of input data in this symbol.
     *
     * @return the type of input data in this symbol
     */
    public DataType getDataType() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this type of symbology supports GS1 data.
     *
     * @return <code>true</code> if this type of symbology supports GS1 data
     */
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * If set to <code>true</code>, the symbol is prefixed with a "Reader Initialization"
     * or "Reader Programming" instruction.
     *
     * @param readerInit whether or not to enable reader initialization
     */
    public void setReaderInit(boolean readerInit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns whether or not reader initialization is enabled.
     *
     * @return whether or not reader initialization is enabled
     */
    public boolean getReaderInit() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the default bar height for this symbol.
     * The default value is {@code 40}.
     *
     * @param barHeight the default bar height for this symbol
     */
    public void setBarHeight(int barHeight) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the default bar height for this symbol.
     * The default value is {@code 40}.
     *
     * @return the default bar height for this symbol
     */
    public int getBarHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the module width for this symbol.
     * The default value is {@code 1}.
     *
     * @param moduleWidth the module width for this symbol
     */
    public void setModuleWidth(int moduleWidth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the module width for this symbol.
     * The default value is {@code 1}.
     *
     * @return the module width for this symbol
     */
    public int getModuleWidth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the horizontal quiet zone (white space) added to the left and to the right of this symbol.
     * The default value is {@code 0}.
     *
     * @param quietZoneHorizontal the horizontal quiet zone (white space) added to the left and to the right of this symbol
     */
    public void setQuietZoneHorizontal(int quietZoneHorizontal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the horizontal quiet zone (white space) added to the left and to the right of this symbol.
     * The default value is {@code 0}.
     *
     * @return the horizontal quiet zone (white space) added to the left and to the right of this symbol
     */
    public int getQuietZoneHorizontal() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the vertical quiet zone (white space) added above and below this symbol.
     * The default value is {@code 0}.
     *
     * @param quietZoneVertical the vertical quiet zone (white space) added above and below this symbol
     */
    public void setQuietZoneVertical(int quietZoneVertical) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the vertical quiet zone (white space) added above and below this symbol.
     * The default value is {@code 0}.
     *
     * @return the vertical quiet zone (white space) added above and below this symbol
     */
    public int getQuietZoneVertical() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>Sets the font to use to render the human-readable text. This is an alternative to setting the
     * {@link #setFontName(String) font name} and {@link #setFontSize(int) font size} separately. May
     * allow some applications to avoid the use of {@link GraphicsEnvironment#registerFont(Font)}
     * when using the {@link Java2DRenderer}.
     *
     * <p>Do not use this method in combination with {@link #setFontName(String)} or {@link #setFontSize(int)}.
     *
     * @param font the font to use to render the human-readable text
     */
    public void setFont(Font font) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the font to use to render the human-readable text.
     *
     * @return the font to use to render the human-readable text
     */
    public Font getFont() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>Sets the name of the font to use to render the human-readable text (default value is <code>Helvetica</code>).
     * The specified font name needs to be registered via {@link GraphicsEnvironment#registerFont(Font)} if you are
     * using the {@link Java2DRenderer}. In order to set the font without registering the font with the graphics
     * environment when using the {@link Java2DRenderer}, you may need to use {@link #setFont(Font)} instead.
     *
     * <p>Use this method in combination with {@link #setFontSize(int)}.
     *
     * <p>Do not use this method in combination with {@link #setFont(Font)}.
     *
     * @param fontName the name of the font to use to render the human-readable text
     */
    public void setFontName(String fontName) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the name of the font to use to render the human-readable text.
     *
     * @return the name of the font to use to render the human-readable text
     */
    public String getFontName() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>Sets the size of the font to use to render the human-readable text (default value is <code>8</code>).
     *
     * <p>Use this method in combination with {@link #setFontName(String)}.
     *
     * <p>Do not use this method in combination with {@link #setFont(Font)}.
     *
     * @param fontSize the size of the font to use to render the human-readable text
     */
    public void setFontSize(int fontSize) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the size of the font to use to render the human-readable text.
     *
     * @return the size of the font to use to render the human-readable text
     */
    public int getFontSize() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the width of the encoded symbol, including the horizontal quiet zone.
     *
     * @return the width of the encoded symbol
     */
    public int getWidth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the height of the symbol, including the human-readable text, if any, as well as the vertical
     * quiet zone. This height is an approximation, since it is calculated without access to a font engine.
     *
     * @return the height of the symbol, including the human-readable text, if any, as well as the vertical
     *         quiet zone
     */
    public int getHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the height of the human-readable text, including the space between the text and other symbols.
     * This height is an approximation, since it is calculated without access to a font engine.
     *
     * @return the height of the human-readable text
     */
    public int getHumanReadableHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the height of the human-readable text, assuming this symbol had human-readable text.
     *
     * @return the height of the human-readable text, assuming this symbol had human-readable text
     */
    protected int getTheoreticalHumanReadableHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns a human readable summary of the decisions made by the encoder when creating a symbol.
     *
     * @return a human readable summary of the decisions made by the encoder when creating a symbol
     */
    public String getEncodeInfo() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Forces this symbol to use a specific ECI mode, rather than allowing the ECI mode to be chosen
     * automatically. It is usually recommended that you allow the system to choose the ECI mode
     * automatically, instead of setting it explicitly. If you do need to set the ECI mode explicitly
     * for some reason, you may specify any of the following values:
     *
     * <ul>
     * <li>0 (IBM437)</li>
     * <li>1 (ISO 8859-1)</li>
     * <li>2 (IBM437)</li>
     * <li>3 (ISO 8859-1, the default and most commonly used value)</li>
     * <li>4 (ISO 8859-2)</li>
     * <li>5 (ISO 8859-3)</li>
     * <li>6 (ISO 8859-4)</li>
     * <li>7 (ISO 8859-5)</li>
     * <li>8 (ISO 8859-6)</li>
     * <li>9 (ISO 8859-7)</li>
     * <li>10 (ISO 8859-8)</li>
     * <li>11 (ISO 8859-9)</li>
     * <li>12 (ISO 8859-10)</li>
     * <li>13 (ISO 8859-11)</li>
     * <li>15 (ISO 8859-13)</li>
     * <li>16 (ISO 8859-14)</li>
     * <li>17 (ISO 8859-15)</li>
     * <li>18 (ISO 8859-16)</li>
     * <li>20 (Shift JIS)</li>
     * <li>21 (Windows-1250)</li>
     * <li>22 (Windows-1251)</li>
     * <li>23 (Windows-1252)</li>
     * <li>24 (Windows-1256)</li>
     * <li>25 (UTF-16BE)</li>
     * <li>26 (UTF-8, another commonly used value)</li>
     * <li>27 (US-ASCII)</li>
     * <li>28 (Big5)</li>
     * <li>29 (GB2312)</li>
     * <li>30 (EUC-KR)</li>
     * <li>31 (GBK)</li>
     * <li>32 (GB18030)</li>
     * <li>33 (UTF-16LE)</li>
     * <li>34 (UTF-32BE)</li>
     * <li>35 (UTF-32LE)</li>
     * </ul>
     *
     * @param eciMode the ECI mode to force the symbol to use
     * @throws IllegalArgumentException if this symbology does not support ECI or an unsupported ECI mode is requested
     */
    public void setEciMode(int eciMode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the ECI mode used by this symbol. The ECI mode is chosen automatically during encoding
     * if the symbol data type has been set to {@link DataType#ECI}. If this symbol does not use ECI,
     * this method will return <code>-1</code>.
     *
     * @return the ECI mode used by this symbol
     * @see #eciProcess()
     */
    public int getEciMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this type of symbology supports ECI (Extended Channel Interpretation).
     *
     * @return <code>true</code> if this type of symbology supports ECI (Extended Channel Interpretation)
     */
    public boolean supportsEci() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the location of the human-readable text (default value is {@link HumanReadableLocation#BOTTOM}).
     *
     * @param humanReadableLocation the location of the human-readable text
     */
    public void setHumanReadableLocation(HumanReadableLocation humanReadableLocation) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the location of the human-readable text.
     *
     * @return the location of the human-readable text
     */
    public HumanReadableLocation getHumanReadableLocation() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the text alignment of the human-readable text (default value is {@link TextAlignment#CENTER}).
     *
     * @param humanReadableAlignment the text alignment of the human-readable text
     */
    public void setHumanReadableAlignment(TextAlignment humanReadableAlignment) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the text alignment of the human-readable text.
     *
     * @return the text alignment of the human-readable text
     */
    public TextAlignment getHumanReadableAlignment() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>Adds the specified rectangle to this symbol. If the specified rectangle cleanly
     * extends another rectangle immediately above it, the rectangles are instead merged,
     * so as to minimize the number of rectangles which must be drawn.
     *
     * <p>IMPORTANT: Rectangle merging requires that rectangles be added in order, from
     * top to bottom.
     *
     * @param rect the rectangle to add
     */
    protected void addRectangle(Rectangle rect) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * <p>Removes the existing rectangles from this symbol and replaces them with the
     * specified rectangles. If any rectangles can be merged, they are merged during
     * this process.
     *
     * <p>IMPORTANT: Rectangle merging requires that rectangles are ordered from top
     * to bottom.
     *
     * @param rects the new rectangles
     */
    protected void setRectangles(List<Rectangle> rects) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns render information about the rectangles in this symbol.
     *
     * @return render information about the rectangles in this symbol
     */
    public List<Rectangle> getRectangles() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns render information about the text elements in this symbol.
     *
     * @return render information about the text elements in this symbol
     */
    public List<TextBox> getTexts() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns render information about the hexagons in this symbol.
     *
     * @return render information about the hexagons in this symbol
     */
    public List<Hexagon> getHexagons() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns render information about the target circles in this symbol.
     *
     * @return render information about the target circles in this symbol
     */
    public List<Circle> getTarget() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static String bin2pat(CharSequence bin) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static String bin2pat(int[] bin, int index, int size, StringBuilder pat) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static String bin2pat(boolean[] bin, int index, int size, StringBuilder pat) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets whether or not empty content is allowed. Some symbologies may be able to generate empty symbols when no data is
     * present, though this is not usually desired behavior. The default value is <code>false</code> (no empty content allowed).
     *
     * @param emptyContentAllowed whether or not empty content is allowed
     */
    public void setEmptyContentAllowed(boolean emptyContentAllowed) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns whether or not empty content is allowed.
     *
     * @return whether or not empty content is allowed
     */
    public boolean getEmptyContentAllowed() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the data to be encoded and triggers encoding. Input data will be assumed
     * to be of the type set by {@link #setDataType(DataType)}.
     *
     * @param data the data to encode
     * @throws OkapiException if no data or data is invalid
     */
    public void setContent(String data) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this symbology allows the user to embed {@link #FNC1_STRING} directly in the content.
     *
     * @return <code>true</code> if this symbology allows the user to embed {@link #FNC1_STRING} directly in the content
     */
    protected boolean supportsFnc1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this symbology allows the user to embed {@link #FNC2_STRING} directly in the content.
     *
     * @return <code>true</code> if this symbology allows the user to embed {@link #FNC2_STRING} directly in the content
     */
    protected boolean supportsFnc2() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this symbology allows the user to embed {@link #FNC3_STRING} directly in the content.
     *
     * @return <code>true</code> if this symbology allows the user to embed {@link #FNC3_STRING} directly in the content
     */
    protected boolean supportsFnc3() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns <code>true</code> if this symbology allows the user to embed {@link #FNC4_STRING} directly in the content.
     *
     * @return <code>true</code> if this symbology allows the user to embed {@link #FNC4_STRING} directly in the content
     */
    protected boolean supportsFnc4() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the content encoded by this symbol.
     *
     * @return the content encoded by this symbol
     */
    public String getContent() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the human-readable text for this symbol.
     *
     * @return the human-readable text for this symbol
     */
    public String getHumanReadableText() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Chooses the ECI mode most suitable for the content of this symbol and uses it to encode the input data.
     */
    protected void eciProcess() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static EciMode determineEci(String content, int eciMode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected static int[] toBytes(String s, Charset charset, int... suffix) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected abstract void encode();

    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void resetPlotElements() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the module width to use for the specified original module width, taking into account any module width ratio
     * customizations. Intended to be overridden by subclasses that support such module width ratio customization.
     *
     * @param originalWidth the original module width
     * @return the module width to use for the specified original module width
     */
    protected double getModuleWidth(int originalWidth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Adds the HIBC prefix and check digit to the specified data, returning the resultant data string.
     *
     * @see <a href="https://sourceforge.net/p/zint/code/ci/master/tree/backend/library.c">Corresponding Zint code</a>
     */
    private String hibcProcess(String source) {
        // HIBC 2.6 allows up to 110 characters, not including the "+" prefix or the check digit
        if (source.length() > 110) {
            throw new OkapiInputException("Data too long for HIBC LIC");
        }
        source = source.toUpperCase();
        if (!source.matches("[A-Z0-9-\\. \\$/+\\%]+?")) {
            throw OkapiInputException.invalidCharactersInInput();
        }
        int counter = 41;
        for (int i = 0; i < source.length(); i++) {
            counter += positionOf(source.charAt(i), HIBC_CHAR_TABLE);
        }
        counter = counter % 43;
        char checkDigit = HIBC_CHAR_TABLE[counter];
        infoLine("HIBC Check Digit Counter: ", counter);
        infoLine("HIBC Check Digit: ", checkDigit);
        return "+" + source + checkDigit;
    }

    /**
     * Returns the intermediate coding of this bar code. Symbol types that use the test
     * infrastructure should override this method.
     *
     * @return the intermediate coding of this bar code
     */
    protected int[] getCodewords() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns this bar code's pattern, converted into a set of corresponding codewords.
     * Useful for bar codes that encode their content as a pattern.
     *
     * @param size the number of digits in each codeword
     * @return this bar code's pattern, converted into a set of corresponding codewords
     */
    protected int[] getPatternAsCodewords(int size) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void info(char c) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void info(CharSequence s) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoSpace(int i) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoSpace(char c) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine(CharSequence s) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine(CharSequence s1, CharSequence s2) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine(CharSequence s, char c) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine(CharSequence s, int i) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine(CharSequence s, boolean b) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void infoLine() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Splits the specified data so that it will fit across N symbols configured like the test symbol.
     * Uses binary search instead of linear search, for performance reasons.
     *
     * @param <T> the type of symbol
     * @param data the data to split
     * @param testSymbol the test symbol
     * @param check custom logic to check whether a symbol fits or not
     * @param max the maximum number of symbols to allow
     * @return the split data
     */
    protected static <T extends Symbol> List<String> split(String data, T testSymbol, FitsCheck<T> check, int max) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @FunctionalInterface
    protected interface FitsCheck<T extends Symbol> {

        boolean fits(String data, T testSymbol, boolean last);
    }
}
