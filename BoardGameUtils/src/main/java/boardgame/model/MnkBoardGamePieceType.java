/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.model;

/**
 * A piece type for an m,n,k-game.
 *
 * @author A.C. Kockx
 */
public final class MnkBoardGamePieceType {
    public final int player;

    /**
     * @param player the player who owns this piece.
     */
    MnkBoardGamePieceType(int player) {
        this.player = player;
    }
}
