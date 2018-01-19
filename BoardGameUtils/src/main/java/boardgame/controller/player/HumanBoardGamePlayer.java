/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.controller.player;

import boardgame.model.SquareMove;
import boardgame.view.SquaresPanelView;
import util.controller.player.Player;
import util.model.GameModel;
import util.model.Move;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Controller that converts mouse input from the user to a SquareMove.
 * In other words, when the user clicks on a square of a board on the screen,
 * then this class creates a SquareMove object that contains the row and column
 * of the clicked square.
 *
 * @author A.C. Kockx
 */
public final class HumanBoardGamePlayer implements Player, MouseListener {
    private final String name;
    private final SquaresPanelView view;

    private Move nextMove = null;
    private boolean waitingForInput = false;

    public HumanBoardGamePlayer(String name, SquaresPanelView view) {
        if (name == null) throw new IllegalArgumentException("name == null");
        if (view == null) throw new IllegalArgumentException("view == null");
        this.name = name;
        this.view = view;
    }

    /**
     * Allows the user to make a move by clicking in the GUI.
     */
    @Override
    public void calculateMove(GameModel model) throws InterruptedException {
        if (model == null) throw new IllegalArgumentException("model == null");
        if (model.getLegalMoves().length <= 0) throw new IllegalStateException("No legal moves available.");

        nextMove = null;
        //wait until the user inputs a move, before continuing with the game.
        synchronized (this) {
            waitingForInput = true;//enable processing of mouse input.
            //wait until the user inputs a move, see method mousePressed.
            while (waitingForInput) {
                if (Thread.interrupted()) throw new InterruptedException();
                wait();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (!waitingForInput) return;
        if (event.getSource() != view.getPanel()) return;

        //get the square on the board that the player has clicked on.
        int row = view.getRow(event.getY());
        int column = view.getColumn(event.getX());
        if (row == -1 || column == -1) return;//if outside board.

        nextMove = new SquareMove(row, column);
        synchronized (this) {
            waitingForInput = false;//disable processing of mouse input.
            notifyAll();//continue execution of method calculateMove.
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
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
        waitingForInput = false;//disable processing of mouse input.
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
        //this could show a tutorial message on the screen to explain why the chosen move is illegal.
    }

    @Override
    public void notifyMadeIllegalMove() {
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
        return true;
    }
}
