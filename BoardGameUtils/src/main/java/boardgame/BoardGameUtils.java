/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame;

import boardgame.controller.player.HumanBoardGamePlayer;
import boardgame.view.SquaresPanelView;
import util.controller.player.Player;
import util.controller.player.RandomLegalMovePlayer;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * @author A.C. Kockx
 */
public final class BoardGameUtils {
    public enum PlayerType {
        HUMAN("Human"),
        COMPUTER_RANDOM_MOVE("Computer Random Move"),
        COMPUTER_BRUTE_FORCE("Computer Brute Force"),
        COMPUTER_NEURAL_NETWORK("Computer Neural Network");

        private final String displayName;

        PlayerType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * For each player shows a popup dialog where the user can choose the type of that player (e.g. human or computer).
     */
    public static PlayerType[] choosePlayerTypes(PlayerType[] valuesToChooseFrom, PlayerType defaultValue, int playerCount, Component parentComponent) {
        PlayerType[] playerTypes = new PlayerType[playerCount];
        for (int player = 0; player < playerTypes.length; player++) {
            PlayerType choice = (PlayerType) JOptionPane.showInputDialog(parentComponent, "Please choose a controller for player " + (player + 1),
                    "New Game - Choose Player " + (player + 1), JOptionPane.QUESTION_MESSAGE, null, valuesToChooseFrom, defaultValue);
            if (choice == null) return null;//if user input cancelled, return null.

            playerTypes[player] = choice;
        }

        return playerTypes;
    }

    /**
     * Returns an array with names of players, i.e. "Player 1", "Player 2", "Player 3", etc.
     */
    public static String[] createPlayerNames(int playerCount) {
        String[] playerNames = new String[playerCount];
        for (int n = 0; n < playerCount; n++) {
            playerNames[n] = "Player " + (n + 1);
        }
        return playerNames;
    }

    /**
     * Creates Player objects of the given types, with the given names.
     * The given view will be used to get mouse input for human players.
     */
    public static Player[] createPlayers(PlayerType[] playerTypes, String[] playerNames, SquaresPanelView view) {
        Player[] players = new Player[playerTypes.length];

        for (int n = 0; n < players.length; n++) {
            PlayerType playerType = playerTypes[n];
            String playerName = playerNames[n];
            Player player;
            switch (playerType) {
                case HUMAN:
                    HumanBoardGamePlayer humanMousePlayer = new HumanBoardGamePlayer(playerName, view);
                    view.getPanel().addMouseListener(humanMousePlayer);
                    player = humanMousePlayer;
                    break;
                case COMPUTER_RANDOM_MOVE:
                    player = new RandomLegalMovePlayer(playerName);
                    break;
                case COMPUTER_BRUTE_FORCE:
                    throw new UnsupportedOperationException(playerType.toString() + " not supported yet.");
                case COMPUTER_NEURAL_NETWORK:
                    throw new UnsupportedOperationException(playerType.toString() + " not supported yet.");
                default:
                    throw new IllegalStateException("Unknown " + PlayerType.class.getSimpleName() + " " + playerType);
            }

            players[n] = player;
        }

        return players;
    }
}
