/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.view;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Interface to be implemented by classes that can draw something in a PanelView.
 *
 * @author A.C. Kockx
 */
public interface Drawer {
    /**
     * Draws content to the given image.
     */
    void draw(Observable o, Object arg, BufferedImage image);
}
