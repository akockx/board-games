/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.model;

/**
 * Interface to be implemented by classes that store the current state of a turn-based game that is played by one or more players.
 *
 * @author A.C. Kockx
 */
public interface TurnBasedGameModel extends GameModel {
    /**
     * Returns the names of the players in a fixed order.
     */
    String[] getPlayerNames();

    /**
     * Returns the index (in the array returned by method getPlayerNames) of the player that has to make the next move.
     */
    int getIndexOfCurrentPlayer();

    /**
     * Returns the index (in the array returned by method getPlayerNames) of the winning player.
     * Can be -1, which means no winner (yet).
     */
    int getIndexOfWinner();

    /**
     * Returns the index (in the array returned by method getPlayerNames) of the player that made an illegal move causing the game to end.
     * Can be -1, which means no player made an illegal move.
     */
    int getIndexOfPlayerThatMadeAnIllegalMove();

    /**
     * Changes the state of the model by making the given move for the current player.
     */
    void tryMove(Move move, float[][] moveProbabilities);

    /**
     * Used for computer players. For each square, stores the probability that the
     * player would have chosen that move as the previous move.
     *
     * This is only used for some types of computer players. Can be null.
     */
    float[][] getPreviousMoveProbabilities();
}
