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

import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Experiment:
 * Creates three TicTacToe games that are played in parallel on three separate threads.
 * The third game is observed by two views at the same time (only the first of these two views accepts mouse input).
 *
 * For more information see class TicTacToe.
 *
 * KNOWN ISSUE: in full screen mode the playerType selection dialog is not visible.
 *
 * @author A.C. Kockx
 */
public final class MultiTicTacToe {
    private static final int ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 3;
    private static final int WINNING_NUMBER_OF_PIECES_IN_A_ROW = 3;
    private static final int PLAYER_COUNT = 2;

    private static final int SQUARE_SIZE_IN_PIXELS = 60;
    private static final int MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS = 100;
    private static final int TIME_BETWEEN_GAMES_IN_MILLISECONDS = 400;

    private MultiTicTacToe() {
    }

    public static void main(String[] args) throws Exception {
        //create models.
        String[] playerNames1 = BoardGameUtils.createPlayerNames(PLAYER_COUNT);
        String[] playerNames2 = BoardGameUtils.createPlayerNames(PLAYER_COUNT);
        String[] playerNames3 = BoardGameUtils.createPlayerNames(PLAYER_COUNT);
        MnkBoardGameModel model1 = new MnkBoardGameModel(ROW_COUNT, COLUMN_COUNT, WINNING_NUMBER_OF_PIECES_IN_A_ROW, playerNames1);
        MnkBoardGameModel model2 = new MnkBoardGameModel(ROW_COUNT, COLUMN_COUNT, WINNING_NUMBER_OF_PIECES_IN_A_ROW, playerNames2);
        MnkBoardGameModel model3 = new MnkBoardGameModel(ROW_COUNT, COLUMN_COUNT, WINNING_NUMBER_OF_PIECES_IN_A_ROW, playerNames3);

        //create views.
        final SquaresPanelView view1 = new SquaresPanelView(new TicTacToeBoardDrawer(model1, SQUARE_SIZE_IN_PIXELS, SQUARE_SIZE_IN_PIXELS));
        final SquaresPanelView view2 = new SquaresPanelView(new TicTacToeBoardDrawer(model2, SQUARE_SIZE_IN_PIXELS, SQUARE_SIZE_IN_PIXELS));
        final SquaresPanelView view3 = new SquaresPanelView(new TicTacToeBoardDrawer(model3, SQUARE_SIZE_IN_PIXELS, SQUARE_SIZE_IN_PIXELS));
        final SquaresPanelView view4 = new SquaresPanelView(new TicTacToeBoardDrawer(model3, SQUARE_SIZE_IN_PIXELS, SQUARE_SIZE_IN_PIXELS));
        model1.addObserver(view1);
        model2.addObserver(view2);
        model3.addObserver(view3);
        model3.addObserver(view4);
        //init GUI on event-dispatching thread.
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                JPanel panel = new JPanel(new GridBagLayout());
                panel.setBackground(TicTacToeBoardDrawer.BACKGROUND_COLOR);
                panel.setForeground(TicTacToeBoardDrawer.LINE_COLOR);

                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                panel.add(view1.getPanel(), c);
                c.gridx = 1;
                c.gridy = 0;
                panel.add(view2.getPanel(), c);
                c.gridx = 0;
                c.gridy = 1;
                panel.add(view3.getPanel(), c);
                c.gridx = 1;
                c.gridy = 1;
                panel.add(view4.getPanel(), c);

                //show view in a window.
                //KNOWN ISSUE: in full screen mode the playerType selection dialog is not visible.
                GuiUtils.createAndShowFrame(panel, MultiTicTacToe.class.getSimpleName(), TicTacToeBoardDrawer.BACKGROUND_COLOR, TicTacToeBoardDrawer.LINE_COLOR, true, false);
            }
        });

        //ask user for game settings.
        //choose a playerType for each player.
        PlayerType[] valuesToChooseFrom = new PlayerType[]{PlayerType.HUMAN, PlayerType.COMPUTER_RANDOM_MOVE};
        PlayerType defaultValue = PlayerType.HUMAN;
        PlayerType[] playerTypes = BoardGameUtils.choosePlayerTypes(valuesToChooseFrom, defaultValue, PLAYER_COUNT, view1.getPanel().getParent());
        if (playerTypes == null) System.exit(0);//if user input cancelled, exit game.

        //create controllers.
        Player[] players1 = BoardGameUtils.createPlayers(playerTypes, playerNames1, view1);
        Player[] players2 = BoardGameUtils.createPlayers(playerTypes, playerNames2, view2);
        Player[] players3 = BoardGameUtils.createPlayers(playerTypes, playerNames3, view3);
        GameEngine engine1 = new TurnBasedGameEngine(model1, players1, MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS);
        GameEngine engine2 = new TurnBasedGameEngine(model2, players2, MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS);
        GameEngine engine3 = new TurnBasedGameEngine(model3, players3, MINIMUM_TIME_BEFORE_COMPUTER_MOVE_IN_MILLISECONDS);

        //start games.
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        executor.scheduleWithFixedDelay(new PlaySingleGame(engine1), 0, TIME_BETWEEN_GAMES_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new PlaySingleGame(engine2), 0, TIME_BETWEEN_GAMES_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(new PlaySingleGame(engine3), 0, TIME_BETWEEN_GAMES_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }
}
