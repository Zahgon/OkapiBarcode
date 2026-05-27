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

import static uk.org.okapibarcode.util.Arrays.positionOf;
import java.util.Locale;
import uk.org.okapibarcode.graphics.Rectangle;

/**
 * <p>Implements the Japanese Postal Code symbology as used to encode address
 * data for mail items in Japan. Valid input characters are digits 0-9,
 * characters A-Z and the dash (-) character. A modulo-19 check digit is
 * added and should not be included in the input data.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class JapanPost extends Symbol {

    private static final String[] JAPAN_TABLE = { "FFT", "FDA", "DFA", "FAD", "FTF", "DAF", "AFD", "ADF", "TFF", "FTT", "TFT", "DAT", "DTA", "ADT", "TDA", "ATD", "TAD", "TTF", "FFF" };

    private static final char[] KASUT_SET = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

    private static final char[] CH_KASUT_SET = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

    /**
     * Creates a new instance.
     */
    public JapanPost() {
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
