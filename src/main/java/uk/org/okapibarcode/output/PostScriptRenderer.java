/*
 * Copyright 2015 Robin Stuart, Daniel Gredler
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
package uk.org.okapibarcode.output;

import static uk.org.okapibarcode.graphics.TextAlignment.CENTER;
import static uk.org.okapibarcode.graphics.TextAlignment.JUSTIFY;
import static uk.org.okapibarcode.util.Doubles.roughlyEqual;
import static uk.org.okapibarcode.util.Integers.normalizeRotation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import uk.org.okapibarcode.backend.OkapiInternalException;
import uk.org.okapibarcode.backend.Symbol;
import uk.org.okapibarcode.graphics.Circle;
import uk.org.okapibarcode.graphics.Color;
import uk.org.okapibarcode.graphics.Hexagon;
import uk.org.okapibarcode.graphics.Rectangle;
import uk.org.okapibarcode.graphics.TextAlignment;
import uk.org.okapibarcode.graphics.TextBox;

/**
 * Renders symbologies to EPS (Encapsulated PostScript).
 *
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 * @author Daniel Gredler
 */
public final class PostScriptRenderer implements SymbolRenderer {

    /**
     * The output stream to render to.
     */
    private final OutputStream out;

    /**
     * The magnification factor to apply.
     */
    private final double magnification;

    /**
     * The paper (background) color.
     */
    private final Color paper;

    /**
     * The ink (foreground) color.
     */
    private final Color ink;

    /**
     * The clockwise rotation of the symbol in degrees.
     */
    private final int rotation;

    /**
     * Creates a new PostScript renderer.
     *
     * @param out the output stream to render to
     * @param magnification the magnification factor to apply
     * @param paper the paper (background) color
     * @param ink the ink (foreground) color
     */
    public PostScriptRenderer(OutputStream out, double magnification, Color paper, Color ink) {
        this(out, magnification, paper, ink, 0);
    }

    /**
     * Creates a new PostScript renderer.
     *
     * @param out the output stream to render to
     * @param magnification the magnification factor to apply
     * @param paper the paper (background) color
     * @param ink the ink (foreground) color
     * @param rotation the clockwise rotation of the symbol in degrees (must be a multiple of 90)
     */
    public PostScriptRenderer(OutputStream out, double magnification, Color paper, Color ink, int rotation) {
        this.out = Objects.requireNonNull(out);
        this.magnification = magnification;
        this.paper = Objects.requireNonNull(paper);
        this.ink = Objects.requireNonNull(ink);
        this.rotation = normalizeRotation(rotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Symbol symbol) throws IOException {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
