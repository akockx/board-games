/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author A.C. Kockx
 */
public final class GraphicsUtils {
    private GraphicsUtils() {
    }

    /**
     * Draws the given string centered on the given coordinates.
     */
    public static void drawCenteredString(Graphics2D g, String string, int x, int y) {
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(string, g);
        double stringWidth = stringBounds.getWidth();
        double stringHeight = stringBounds.getHeight();
        g.drawString(string, x - (int) Math.round(stringWidth/2), y + (int) Math.round(stringHeight/3));
    }

    /**
     * Makes the given image completely transparent.
     */
    public static void clearImage(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        //here cannot use default compositing rule with a transparent color, since that would blend the transparent color with the existing colors and would have no effect at all.
        //for color compositing in Java see https://docstore.mik.ua/orelly/java-ent/jfc/ch04_07.htm
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
    }

    /**
     * Returns a Graphics2D object for the given image with antialiasing switched on.
     */
    public static Graphics2D createAntiAliasedGraphics(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }
}
