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

/**
 * <p>Implements Code 3 of 9 Extended, also known as Code 39e and Code39+.
 *
 * <p>Supports encoding of all characters in the 7-bit ASCII table. A modulo-43
 * check digit can be added if required.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Code3Of9Extended extends Symbol {

    /**
     * The types of Code 39 Extended check digits available.
     */
    public enum CheckDigit {

        /**
         * No check digit.
         */
        NONE,
        /**
         * One mod 43 check digit.
         */
        MOD43
    }

    private static final String[] E_CODE_39 = { "%U", "$A", "$B", "$C", "$D", "$E", "$F", "$G", "$H", "$I", "$J", "$K", "$L", "$M", "$N", "$O", "$P", "$Q", "$R", "$S", "$T", "$U", "$V", "$W", "$X", "$Y", "$Z", "%A", "%B", "%C", "%D", "%E", " ", "/A", "/B", "/C", "/D", "/E", "/F", "/G", "/H", "/I", "/J", "/K", "/L", "-", ".", "/O", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/Z", "%F", "%G", "%H", "%I", "%J", "%V", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "%K", "%L", "%M", "%N", "%O", "%W", "+A", "+B", "+C", "+D", "+E", "+F", "+G", "+H", "+I", "+J", "+K", "+L", "+M", "+N", "+O", "+P", "+Q", "+R", "+S", "+T", "+U", "+V", "+W", "+X", "+Y", "+Z", "%P", "%Q", "%R", "%S", "%T" };

    private CheckDigit checkDigit = CheckDigit.NONE;

    private double moduleWidthRatio = 2;

    /**
     * Sets the ratio of wide bar width to narrow bar width. Valid values are usually between
     * {@code 2} and {@code 3}. The default value is {@code 2}.
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

    /**
     * Sets the check digit scheme (no check digit, or a modulo-43 check digit). By default, no check digit is added.
     *
     * @param checkDigit the check digit scheme
     */
    public void setCheckDigit(CheckDigit checkDigit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the check digit scheme (no check digit, or a modulo-43 check digit). By default, no check digit is added.
     *
     * @return the check digit scheme
     */
    public CheckDigit getCheckDigit() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void encode() {
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
