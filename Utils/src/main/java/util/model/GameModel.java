/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.model;

/**
 * Interface to be implemented by classes that store the current state of a game
 * and can be used as a model in a Model-View-Controller pattern.
 *
 * A model in this context is a state machine. It has a state that can be changed only by the model itself.
 * The model should protect its state to keep it consistent at all times.
 * The model should only store its own state and nothing else.
 *
 * @author A.C. Kockx
 */
public interface GameModel extends Observable {
    /**
     * Prepares the model for a new game.
     */
    void reset();

    /**
     * Returns all legal moves for the current state of the model. Can be empty array.
     */
    Move[] getLegalMoves();

    /**
     * Returns whether the given move is legal for the current state of the model.
     */
    boolean isLegalMove(Move move);

    /**
     * Changes the state of the model by undoing the previous move.
     */
    void undoMove();

    /**
     * Returns whether the model has reached an end condition of the game.
     */
    boolean isGameOver();
}
