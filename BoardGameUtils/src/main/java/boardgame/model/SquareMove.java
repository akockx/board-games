/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.model;

import util.model.Move;

/**
 * Stores one move for a board game.
 *
 * @author A.C. Kockx
 */
public final class SquareMove implements Move {
    public final int row;
    public final int column;

    public SquareMove(int row, int column) {
        this.row = row;
        this.column = column;
    }
}
