/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.controller.player;

import util.model.GameModel;
import util.model.Move;

import java.util.Random;

/**
 * Plays a turn-based game by making a random legal move each time. All legal moves are equally probable.
 *
 * @author A.C. Kockx
 */
public final class RandomLegalMovePlayer implements Player {
    private final String name;
    private final Random random;

    private Move nextMove = null;

    public RandomLegalMovePlayer(String name) {
        if (name == null) throw new IllegalArgumentException("name == null");

        this.name = name;

        //use current time as seed.
        random = new Random(System.nanoTime());
    }

    /**
     * Picks a random move that is legal.
     */
    @Override
    public void calculateMove(GameModel model) {
        if (model == null) throw new IllegalArgumentException("model == null");

        Move[] legalMoves = model.getLegalMoves();
        if (legalMoves.length <= 0) throw new IllegalStateException("No legal moves available.");

        nextMove = legalMoves[random.nextInt(legalMoves.length)];
    }

    @Override
    public Move getCalculatedMove() {
        if (nextMove == null) throw new IllegalStateException("nextMove not initialized. First call calculateMove().");
        return nextMove;
    }

    @Override
    public float[][] getCalculatedMoveProbabilities() {
        return null;
    }

    @Override
    public void notifyNewGame() {
        //reset.
        nextMove = null;
    }

    @Override
    public void notifyWon() {
    }

    @Override
    public void notifyLost() {
    }

    @Override
    public void notifyDraw() {
    }

    @Override
    public void notifyTriedIllegalMove() {
        throw new IllegalStateException(getClass().getSimpleName() + " tried an illegal move.");
    }

    @Override
    public void notifyMadeIllegalMove() {
        throw new IllegalStateException(getClass().getSimpleName() + " made an illegal move.");
    }

    @Override
    public void notifyOtherPlayerMadeIllegalMove() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isHumanPlayer() {
        return false;
    }
}
