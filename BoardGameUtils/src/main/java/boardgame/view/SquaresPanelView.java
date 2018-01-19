/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.view;

import util.view.BasicPanelView;
import util.view.PanelView;

import javax.swing.JPanel;
import java.util.Observable;

/**
 * An implementation of PanelView that can translate pixel coordinates to the corresponding row and column on the displayed board.
 *
 * @author A.C. Kockx
 */
@SuppressWarnings("serial")
public final class SquaresPanelView implements PanelView {
    //wrapped view to delegate to.
    private final PanelView view;

    private final int squareSize;
    private final int borderSize;
    private final int rowCount;
    private final int columnCount;

    public SquaresPanelView(SquaresDrawer squaresDrawer) {
        if (squaresDrawer == null) throw new IllegalArgumentException("squaresDrawer == null");

        squareSize = squaresDrawer.getSquareSize();
        borderSize = squaresDrawer.getBorderSize();
        rowCount = squaresDrawer.getRowCount();
        columnCount = squaresDrawer.getColumnCount();
        view = new BasicPanelView(columnCount*squareSize + 2*borderSize, rowCount*squareSize + 2*borderSize, squaresDrawer);
    }

    /**
     * Translates the given pixel y coordinate in this Panel to a row on the displayed board.
     */
    public int getRow(int y) {
        if (y < borderSize || y >= borderSize + rowCount*squareSize) return -1;

        return (y - borderSize)/squareSize;
    }

    /**
     * Translates the given pixel x coordinate in this Panel to a column on the displayed board.
     */
    public int getColumn(int x) {
        if (x < borderSize || x >= borderSize + columnCount*squareSize) return -1;

        return (x - borderSize)/squareSize;
    }

    @Override
    public void update(Observable o, Object arg) {
        //delegate to wrapped view.
        view.update(o, arg);
    }

    @Override
    public JPanel getPanel() {
        //delegate to wrapped view.
        return view.getPanel();
    }
}
