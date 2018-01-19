/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.controller;

/**
 * @author A.C. Kockx
 */
public final class PlaySingleGame implements Runnable {
    private final GameEngine engine;

    public PlaySingleGame(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Plays the game once.
     */
    @Override
    public void run() {
        try {
            engine.newGame();
            engine.playGame();
        } catch (InterruptedException e) {
            //do nothing.
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            String message = t.getMessage();
            System.exit(message == null ? -1 : message.hashCode());
        }
    }
}
