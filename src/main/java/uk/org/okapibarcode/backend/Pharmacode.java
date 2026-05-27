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
 * <p>Implements the <a href="http://en.wikipedia.org/wiki/Pharmacode">Pharmacode</a>
 * bar code symbology.
 *
 * <p>Pharmacode is used for the identification of pharmaceuticals. The symbology is
 * able to encode whole numbers between 3 and 131070.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Pharmacode extends Symbol {

    /**
     * Creates a new instance.
     */
    public Pharmacode() {
        this.humanReadableLocation = HumanReadableLocation.NONE;
    }

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
