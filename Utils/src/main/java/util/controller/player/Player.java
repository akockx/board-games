/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.controller.player;

import util.model.GameModel;
import util.model.Move;

/**
 * Interface to be implemented by classes that can calculate moves to play a turn-based game.
 *
 * @author A.C. Kockx
 */
public interface Player {

    String getName();

    boolean isHumanPlayer();

    /**
     * Determines a legal move for the current state of the model.
     */
    void calculateMove(GameModel model) throws InterruptedException;

    /**
     * Returns the move determined in method calculateMove.
     */
    Move getCalculatedMove();

    /**
     * Returns the move probabilities determined in method calculateMove. Can be null.
     */
    float[][] getCalculatedMoveProbabilities();

    /**
     * This is called when a new game starts.
     */
    void notifyNewGame();

    /**
     * This is called when this player wins the game.
     */
    void notifyWon();

    /**
     * This is called when this player loses the game.
     */
    void notifyLost();

    /**
     * This is called when the game ends in a draw.
     */
    void notifyDraw();

    /**
     * This is called when this player tries to make an illegal move.
     * This can happen for human players or for neural network computer players that are still learning how to play the game.
     */
    void notifyTriedIllegalMove();

    /**
     * This is called when this player still tries to make an illegal move after a maximum number of tries.
     * This can happen for neural network computer players that are still learning how to play the game.
     */
    void notifyMadeIllegalMove();

    /**
     * This is called when another player made an illegal move and lost the game.
     */
    void notifyOtherPlayerMadeIllegalMove();
}
