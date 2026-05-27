/*
 * Copyright 2014-2015 Robin Stuart, Robert Elliott, Daniel Gredler
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
import static uk.org.okapibarcode.util.Integers.normalizeRotation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
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
 * Renders symbologies using the Java 2D API.
 */
public final class Java2DRenderer implements SymbolRenderer {

    /**
     * The graphics to render to.
     */
    private final Graphics2D g2d;

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
     * Creates a new Java 2D renderer which draws the symbol using the current {@link Graphics2D} color.
     *
     * @param g2d the graphics to render to
     */
    public Java2DRenderer(Graphics2D g2d) {
        this(g2d, 1, null, null, 0);
    }

    /**
     * Creates a new Java 2D renderer. If the specified paper color is {@code null}, the symbol is drawn without clearing the
     * existing {@code g2d} background.
     *
     * @param g2d the graphics to render to
     * @param magnification the magnification factor to apply
     * @param paper the paper (background) color (may be {@code null}, in which case no background is drawn)
     * @param ink the ink (foreground) color (may be {@code null}, in which case the current {@link Graphics2D} color is used)
     */
    public Java2DRenderer(Graphics2D g2d, double magnification, Color paper, Color ink) {
        this(g2d, magnification, paper, ink, 0);
    }

    /**
     * Creates a new Java 2D renderer. If the specified paper color is {@code null}, the symbol is drawn without clearing the
     * existing {@code g2d} background.
     *
     * @param g2d the graphics to render to
     * @param magnification the magnification factor to apply
     * @param paper the paper (background) color (may be {@code null}, in which case no background is drawn)
     * @param ink the ink (foreground) color (may be {@code null}, in which case the current {@link Graphics2D} color is used)
     * @param rotation the clockwise rotation of the symbol in degrees (must be a multiple of 90)
     */
    public Java2DRenderer(Graphics2D g2d, double magnification, Color paper, Color ink, int rotation) {
        this.g2d = Objects.requireNonNull(g2d);
        this.magnification = magnification;
        this.paper = paper;
        this.ink = ink;
        this.rotation = normalizeRotation(rotation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Symbol symbol) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static Rectangle2D getBounds(TextBox text, Graphics2D g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        return fm.getStringBounds(text.text, g2d);
    }

    private static Ellipse2D.Double adjust(Circle circle, double magnification, int marginX, int marginY) {
        double x = marginX + ((circle.centreX - circle.radius) * magnification);
        double y = marginY + ((circle.centreY - circle.radius) * magnification);
        double w = 2d * circle.radius * magnification;
        double h = 2d * circle.radius * magnification;
        return new Ellipse2D.Double(x, y, w, h);
    }

    private static Font addTracking(Font baseFont, double maxTextWidth, String text, Graphics2D g2d) {
        FontRenderContext frc = g2d.getFontRenderContext();
        double originalWidth = baseFont.getStringBounds(text, frc).getWidth();
        double extraSpace = maxTextWidth - originalWidth;
        double extraSpacePerGap = extraSpace / (text.length() - 1);
        double scaleX = (baseFont.isTransformed() ? baseFont.getTransform().getScaleX() : 1);
        double tracking = extraSpacePerGap / (baseFont.getSize2D() * scaleX);
        return baseFont.deriveFont(Collections.singletonMap(TextAttribute.TRACKING, tracking));
    }
}
