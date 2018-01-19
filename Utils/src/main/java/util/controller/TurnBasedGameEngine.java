/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.controller;

import util.controller.player.Player;
import util.model.Move;
import util.model.TurnBasedGameModel;

/**
 * Controller for turn-based games.
 * Starts a new turn-based game, then asks each player in turn to make a move, until the game is finished.
 *
 * The players can be of any type that implements Player.
 * The number of players depends on the given TurnBasedGameModel.
 * The rules of the game are entirely encapsulated within the given TurnBasedGameModel and are not known to the engine.
 *
 * Can be used as a controller in a Model-View-Controller pattern.
 * Note: this class is not thread-safe, always use from the same thread only.
 *
 * @author A.C. Kockx
 */
public final class TurnBasedGameEngine implements GameEngine {
    private final TurnBasedGameModel model;
    private final Player[] players;

    private final long minimumTimeBeforeComputerMoveInMilliseconds;

    public TurnBasedGameEngine(TurnBasedGameModel model, Player[] players, long minimumTimeBeforeComputerMoveInMilliseconds) {
        if (model == null) throw new IllegalArgumentException("model == null");
        if (players == null || players.length <= 0) throw new IllegalArgumentException("players is empty");
        if (minimumTimeBeforeComputerMoveInMilliseconds < 0) throw new IllegalArgumentException("minimumTimeBeforeComputerMoveInMilliseconds < 0");

        this.model = model;
        this.players = players;
        this.minimumTimeBeforeComputerMoveInMilliseconds = minimumTimeBeforeComputerMoveInMilliseconds;
    }

    @Override
    public void newGame() throws InterruptedException {
        model.reset();
        for (Player player : players) {
            player.notifyNewGame();
        }
        if (Thread.interrupted()) throw new InterruptedException();
    }

    @Override
    public void playGame() throws InterruptedException {
        //players make moves, until end condition reached.
        boolean gameOver = model.isGameOver();
        while (!gameOver) {
            //calculate legal move.
            Player currentPlayer = players[model.getIndexOfCurrentPlayer()];
            Move move = null;
            long thinkingTime = 0;
            boolean legalMove = false;
            int maxTries = currentPlayer.isHumanPlayer() ? Integer.MAX_VALUE : 1000;
            int tries = 0;
            while (!legalMove && tries < maxTries) {
                thinkingTime += calculateMove(currentPlayer);
                move = currentPlayer.getCalculatedMove();
                legalMove = model.isLegalMove(move);
                if (!legalMove) currentPlayer.notifyTriedIllegalMove();
                tries++;
            }

            if (!currentPlayer.isHumanPlayer()) {//if computer player.
                //wait some time before making the move, otherwise for fast thinking computer players
                //the computer player's move would be visible almost immediately after the previous player's move,
                //which would be confusing for any humans that are watching the game.
                long waitingTime = Math.max(minimumTimeBeforeComputerMoveInMilliseconds - thinkingTime, 0);
                synchronizedWait(waitingTime);
            }

            //make move and (re)set move probabilities.
            model.tryMove(move, currentPlayer.getCalculatedMoveProbabilities());
            gameOver = model.isGameOver();
            if (gameOver) notifyPlayersOfGameEndStatus();
            if (Thread.interrupted()) throw new InterruptedException();
        }
    }

    /**
     * Notifies players of game end status, to allow neural network computer players to learn.
     */
    private void notifyPlayersOfGameEndStatus() {
        int indexOfPlayerThatMadeAnIllegalMove = model.getIndexOfPlayerThatMadeAnIllegalMove();
        int indexOfWinner = model.getIndexOfWinner();

        if (indexOfPlayerThatMadeAnIllegalMove != -1) {//if a player made an illegal move and caused the game to end.
            Player playerThatMadeAnIllegalMove = players[indexOfPlayerThatMadeAnIllegalMove];
            playerThatMadeAnIllegalMove.notifyMadeIllegalMove();
            for (Player player : players) {
                if (player != playerThatMadeAnIllegalMove) player.notifyOtherPlayerMadeIllegalMove();
            }

        } else if (indexOfWinner != -1) {//if there is a winner.
            Player winner = players[indexOfWinner];
            winner.notifyWon();
            for (Player player : players) {
                if (player != winner) player.notifyLost();
            }

        } else {//if game ended in a draw.
            for (Player player : players) {
                player.notifyDraw();
            }
        }
    }

    /**
     * @return thinkingTime.
     */
    private long calculateMove(Player player) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        player.calculateMove(model);
        long endTime = System.currentTimeMillis();
        if (Thread.interrupted()) throw new InterruptedException();
        return endTime - startTime;
    }

    /**
     * Also checks if thread has been interrupted before this method was called.
     */
    private synchronized void synchronizedWait(long waitingTime) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (waitingTime > 0) wait(waitingTime);
    }
}
