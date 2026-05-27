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

/**
 * <p>Calculate NVE-18 (Nummer der Versandeinheit), also known as SSCC-18 (Serial Shipping Container Code).
 *
 * <p>Encodes a 17-digit number, adding a modulo-10 check digit.
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Nve18 extends Symbol {

    @Override
    protected void encode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
