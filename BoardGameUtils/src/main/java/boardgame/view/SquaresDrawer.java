/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.view;

import util.view.Drawer;

/**
 * Interface to be implemented by classes that draw a rectangular board made of equally sized squares in a PanelView.
 *
 * @author A.C. Kockx
 */
public interface SquaresDrawer extends Drawer {

    int getRowCount();

    int getColumnCount();

    /**
     * @return squareSize in pixels.
     */
    int getSquareSize();

    /**
     * @return borderSize in pixels.
     */
    int getBorderSize();
}
