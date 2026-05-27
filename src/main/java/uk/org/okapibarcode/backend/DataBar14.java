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
import java.math.BigInteger;

/**
 * <p>Implements GS1 DataBar Omnidirectional and GS1 DataBar Truncated according to ISO/IEC 24724:2011.
 *
 * <p>Input data should be a 13-digit Global Trade Identification Number (GTIN) without check digit or
 * Application Identifier [01].
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class DataBar14 extends Symbol {

    /**
     * The different DataBar-14 barcode variants available to encode.
     */
    public enum Mode {

        /**
         * DataBar-14
         */
        LINEAR,
        /**
         * DataBar-14 Omnidirectional
         */
        OMNI,
        /**
         * DataBar-14 Omnidirectional Stacked
         */
        STACKED
    }

    private static final int[] G_SUM_TABLE = { 0, 161, 961, 2015, 2715, 0, 336, 1036, 1516 };

    private static final int[] T_TABLE = { 1, 10, 34, 70, 126, 4, 20, 48, 81 };

    private static final int[] MODULES_ODD = { 12, 10, 8, 6, 4, 5, 7, 9, 11 };

    private static final int[] MODULES_EVEN = { 4, 6, 8, 10, 12, 10, 8, 6, 4 };

    private static final int[] WIDEST_ODD = { 8, 6, 4, 3, 1, 2, 4, 6, 8 };

    private static final int[] WIDEST_EVEN = { 1, 3, 5, 6, 8, 7, 5, 3, 1 };

    private static final int[] CHECKSUM_WEIGHT = { /* Table 5 */
    1, 3, 9, 27, 2, 6, 18, 54, 4, 12, 36, 29, 8, 24, 72, 58, 16, 48, 65, 37, 32, 17, 51, 74, 64, 34, 23, 69, 49, 68, 46, 59 };

    private static final int[] FINDER_PATTERN = { 3, 8, 2, 1, 1, 3, 5, 5, 1, 1, 3, 3, 7, 1, 1, 3, 1, 9, 1, 1, 2, 7, 4, 1, 1, 2, 5, 6, 1, 1, 2, 3, 8, 1, 1, 1, 5, 7, 1, 1, 1, 3, 9, 1, 1 };

    private boolean linkageFlag;

    private int separatorHeight = 1;

    private Mode mode;

    /**
     * Creates a new instance, using mode {@link Mode#LINEAR}.
     */
    public DataBar14() {
        this(Mode.LINEAR);
    }

    /**
     * Creates a new instance, using the specified mode.
     *
     * @param mode the symbol mode
     */
    public DataBar14(Mode mode) {
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataType(DataType dummy) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Although this is a GS1 symbology, input data is expected to omit the [01] Application Identifier,
     * as well as the check digit. Thus, the input data is not considered GS1-format data.
     */
    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the symbol mode. The default is {@link Mode#LINEAR}.
     *
     * @param mode the symbol mode
     */
    public void setMode(Mode mode) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the symbol mode.
     *
     * @return the symbol mode
     */
    public Mode getMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Sets the separator height for {@link Mode#STACKED} and {@link Mode#OMNI} symbols. The default value is {@code 1}.
     *
     * @param separatorHeight the separator height for {@link Mode#STACKED} and {@link Mode#OMNI} symbols
     */
    public void setSeparatorHeight(int separatorHeight) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the separator height for {@link Mode#STACKED} and {@link Mode#OMNI} symbols.
     *
     * @return the separator height for {@link Mode#STACKED} and {@link Mode#OMNI} symbols
     */
    public int getSeparatorHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected void setLinkageFlag(boolean linkageFlag) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    protected boolean getLinkageFlag() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
