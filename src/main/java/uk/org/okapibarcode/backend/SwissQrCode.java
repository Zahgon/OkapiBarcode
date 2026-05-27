/*
 * Copyright 2024 Daniel Gredler
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

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Swiss QR Code is a specialized type of QR Code symbol used for QR-bill in Switzerland. It is mostly a
 * spec-compliant QR Code, but it must use error correction level M, it cannot hold more than 997 characters,
 * it must always measure 46x46 mm when printed, data must be encoded as UTF-8 without the use of ECI, and it
 * features a Swiss cross logo in the center of the symbol.
 *
 * @author Daniel Gredler
 * @see <a href="https://www.six-group.com/dam/download/banking-services/standardization/qr-bill/ig-qr-bill-v2.2-en.pdf">Swiss QR Bill Specification, Section 5</a>
 */
public class SwissQrCode extends QrCode {

    public SwissQrCode() {
        // min size to fit logo
        minVersion = 6;
        // mandated by spec
        preferredEccLevel = EccLevel.M;
    }

    @Override
    public void setPreferredEccLevel(EccLevel preferredEccLevel) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void setPreferredVersion(int version) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public boolean supportsGs1() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public void setContent(String data) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    protected void customize(int[] grid, int size) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
