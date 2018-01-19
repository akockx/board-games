/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.controller;

/**
 * @author A.C. Kockx
 */
public interface GameEngine {
    /**
     * (Re-)initializes the game.
     */
    void newGame() throws InterruptedException;

    /**
     * Plays the game once from beginning to end.
     */
    void playGame() throws InterruptedException;
}
