/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.view;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Basic implementation of PanelView.
 * Draws an image to a panel using a given Drawer, whenever update is called by an observed object.
 *
 * @author A.C. Kockx
 */
@SuppressWarnings("serial")
public final class BasicPanelView implements PanelView {
    private final int width;
    private final int height;
    private final Drawer drawer;//to draw and update image.
    private final ImagePanel imagePanel;//to store and show image.

    /**
     * @param width of view in pixels.
     * @param height of view in pixels.
     * @param drawer to draw the content of this view.
     */
    public BasicPanelView(int width, int height, Drawer drawer) {
        if (width <= 0) throw new IllegalArgumentException("width <= 0");
        if (height <= 0) throw new IllegalArgumentException("height <= 0");
        if (drawer == null) throw new IllegalArgumentException("drawer == null");

        this.width = width;
        this.height = height;
        this.drawer = drawer;
        imagePanel = new ImagePanel(width, height);
    }

    /**
     * Updates this view.
     */
    @Override
    public void update(Observable o, Object arg) {
        //drawing may take some time. To avoid multiple calls to this method drawing to the same image in parallel,
        //create a new image to draw to each time. After drawing is finished, set the finished image in imagePanel (this happens atomically)
        //and do not store the image here.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        drawer.draw(o, arg, image);
        //imagePanel.setImage() creates a clone of image, so any further changes to image made here will not influence painting in imagePanel.
        imagePanel.setImage(image);
        imagePanel.repaint();
    }

    @Override
    public JPanel getPanel() {
        return imagePanel;
    }
}
