/*
 * Copyright 2015 Robin Stuart
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

import static uk.org.okapibarcode.util.Strings.deleteLastLine;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextBox;

/**
 * <p>Implements USPS Intelligent Mail Package Barcode (IMpb), a linear barcode based on GS1-128.
 * Includes additional data checks.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 * @see <a href="https://ribbs.usps.gov/intelligentmail_package/documents/tech_guides/BarcodePackageIMSpec.pdf">IMpb Specification</a>
 */
public class UspsPackage extends Symbol {

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void plotSymbol() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getCodewords() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
