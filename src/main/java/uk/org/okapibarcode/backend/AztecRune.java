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
 * <p>Implements the Aztec Runes bar code symbology according to ISO/IEC 24778:2008 Annex A.
 *
 * <p>Aztec Runes is a fixed-size matrix symbology which can encode whole integer values between 0 and 255.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class AztecRune extends Symbol {

    private static final int[] BIT_PLACEMENT_MAP = { 1, 1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 29, 1, 0, 0, 0, 0, 0, 0, 0, 1, 9, 28, 1, 0, 1, 1, 1, 1, 1, 0, 1, 10, 27, 1, 0, 1, 0, 0, 0, 1, 0, 1, 11, 26, 1, 0, 1, 0, 1, 0, 1, 0, 1, 12, 25, 1, 0, 1, 0, 0, 0, 1, 0, 1, 13, 24, 1, 0, 1, 1, 1, 1, 1, 0, 1, 14, 23, 1, 0, 0, 0, 0, 0, 0, 0, 1, 15, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 22, 21, 20, 19, 18, 17, 16, 0, 0 };

    /**
     * Creates a new instance.
     */
    public AztecRune() {
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
