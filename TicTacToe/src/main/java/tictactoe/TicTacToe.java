/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package tictactoe;

import boardgame.BoardGameUtils;
import boardgame.BoardGameUtils.PlayerType;
import boardgame.model.MnkBoardGameModel;
import util.controller.GameEngine;
import util.controller.PlaySingleGame;
import util.controller.TurnBasedGameEngine;
import util.controller.player.Player;
import util.gui.GuiUtils;
import boardgame.view.SquaresPanelView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Starts a TicTacToe game.
 *
 * The user can choose the following combinations of types of players:
 * human vs human
 * human vs computer
 * computer vs computer
 *
 * After the game is finished, a new game is started with the same types of players.
 *
 * KNOWN ISSUE: in full screen mode the playerType selection dialog is not visible.
 *
 * @author A.C. Kockx
 */
public final class TicTacToe {
    private static final int ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 3;
    private static final int WINNING_NUMBER_OF_PIECES_IN_A_ROW = 3;
    private static final int PLAYER_COUNT = 2;

    private static final int SQUARE_SIZE_IN_PIXELS = 100;
    private static final long MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS = 1000;
    private static final long TIME_BETWEEN_GAMES_IN_MILLISECONDS = 4000;

    private TicTacToe() {
    }

    /**
     * Initializes model, view and controller for a TicTacToe game.
     */
    public static void main(String[] args) throws Exception {
        //create model.
        String[] playerNames = BoardGameUtils.createPlayerNames(PLAYER_COUNT);
        MnkBoardGameModel model = new MnkBoardGameModel(ROW_COUNT, COLUMN_COUNT, WINNING_NUMBER_OF_PIECES_IN_A_ROW, playerNames);

        //create view.
        final SquaresPanelView view = new SquaresPanelView(new TicTacToeBoardDrawer(model, SQUARE_SIZE_IN_PIXELS, SQUARE_SIZE_IN_PIXELS));
        model.addObserver(view);
        //init GUI on event-dispatching thread.
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                //show view in a window.
                //KNOWN ISSUE: in full screen mode the playerType selection dialog is not visible.
                GuiUtils.createAndShowFrame(view.getPanel(), TicTacToe.class.getSimpleName(), TicTacToeBoardDrawer.BACKGROUND_COLOR, TicTacToeBoardDrawer.LINE_COLOR, true, false);
            }
        });

        //ask user for game settings.
        //choose a playerType for each player.
        PlayerType[] valuesToChooseFrom = new PlayerType[]{PlayerType.HUMAN, PlayerType.COMPUTER_RANDOM_MOVE};
        PlayerType defaultValue = PlayerType.HUMAN;
        PlayerType[] playerTypes = BoardGameUtils.choosePlayerTypes(valuesToChooseFrom, defaultValue, PLAYER_COUNT, view.getPanel());
        if (playerTypes == null) System.exit(0);//if user input cancelled, exit game.

        //create controller.
        Player[] players = BoardGameUtils.createPlayers(playerTypes, playerNames, view);
        GameEngine engine = new TurnBasedGameEngine(model, players, MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS);

        //start game.
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new PlaySingleGame(engine), 0, TIME_BETWEEN_GAMES_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }
}
