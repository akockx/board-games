/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An extension of JPanel. Its only purpose is to draw a given image to the screen whenever it is updated.
 * Its size will be constant and is set in the constructor.
 *
 * @author A.C. Kockx
 */
public final class ImagePanel extends JPanel {
    private final int width;
    private final int height;

    private BufferedImage image = null;//can be null.

    /**
     * Creates an ImagePanel with the given width and height that shows an empty background.
     */
    public ImagePanel(int width, int height) {
        if (width <= 0) throw new IllegalArgumentException("width <= 0");
        if (height <= 0) throw new IllegalArgumentException("height <= 0");
        this.width = width;
        this.height = height;
    }

    /**
     * @param image to show. Set to null to show an empty background.
     */
    public void setImage(BufferedImage image) {
        BufferedImage imageClone = null;
        if (image != null) {
            if (image.getWidth() != width) throw new IllegalArgumentException("image.getWidth() != width");
            if (image.getHeight() != height) throw new IllegalArgumentException("image.getHeight() != height");

            //store a clone of the given image, so that the stored image cannot be changed from the outside and to avoid painting the image while it is being changed.
            imageClone = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imageClone.createGraphics();
            g.drawImage(image, 0, 0, this);
            g.dispose();
        }

        //synchronized to avoid changing image during painting.
        synchronized (this) {
            this.image = imageClone;
        }
    }

    /**
     * Paints in-memory image on screen.
     */
    @Override
    public void paintComponent(Graphics g) {
        //synchronized to avoid changing image during painting.
        Image image;
        synchronized (this) {
            image = this.image;
        }

        if (image == null) {//if nothing to draw.
            //draw empty background.
            g.setColor(getBackground());
            g.fillRect(0, 0, width, height);
        } else {
            g.drawImage(image, 0, 0, this);
        }
        g.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
