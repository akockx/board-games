/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package tictactoe;

import boardgame.model.MnkBoardGamePieceType;
import boardgame.model.MnkBoardGameModel;
import boardgame.model.SquareMove;
import boardgame.view.SquaresDrawer;
import util.graphics.GraphicsUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Draws a TicTacToe board.
 *
 * @author A.C. Kockx
 */
public final class TicTacToeBoardDrawer implements SquaresDrawer {
    private static final int CROSSES_PLAYER = 1;
    private static final int NOUGHTS_PLAYER = 2;
    static final Color BACKGROUND_COLOR = Color.WHITE;
    static final Color LINE_COLOR = Color.BLACK;
    private static final Color PREVIOUS_MOVE_COLOR = Color.GREEN;
    private static final Color CROSS_COLOR = Color.BLUE;
    private static final Color NOUGHT_COLOR = Color.RED;

    private final MnkBoardGameModel model;
    private final int squareSize;
    private final int borderSize;

    TicTacToeBoardDrawer(MnkBoardGameModel model, int squareSizeInPixels, int borderSizeInPixels) {
        if (model == null) throw new IllegalArgumentException("model == null");
        if (squareSizeInPixels <= 0) throw new IllegalArgumentException("squareSizeInPixels <= 0");
        if (borderSizeInPixels <= 0) throw new IllegalArgumentException("borderSizeInPixels <= 0");

        this.model = model;
        squareSize = squareSizeInPixels;
        borderSize = borderSizeInPixels;
    }

    /**
     * Draws the current board from this.model to the given in-memory image.
     */
    @Override
    public void draw(Observable o, Object arg, BufferedImage image) {
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        int requiredK = model.getWinningNumberOfPiecesInARow();
        MnkBoardGamePieceType[][] board = model.getBoard();
        SquareMove previousMove = model.getPreviousMove();
        float[][] previousMoveProbabilities = model.getPreviousMoveProbabilities();
        boolean gameOver = model.isGameOver();
        String[] playerNames = model.getPlayerNames();
        int indexOfWinner = model.getIndexOfWinner();
        int indexOfPlayerThatMadeAnIllegalMove = model.getIndexOfPlayerThatMadeAnIllegalMove();

        //draw background.
        Graphics2D g = GraphicsUtils.createAntiAliasedGraphics(image);
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        //draw move probabilities.
        drawPreviousMoveProbabilities(g, previousMoveProbabilities, rowCount, columnCount, squareSize, borderSize);

        //draw board.
        g.setColor(LINE_COLOR);
        int lineWidth = squareSize / 20;
        g.setStroke(new BasicStroke(lineWidth));
        for (int row = 1; row < rowCount; row++) {
            g.drawLine(borderSize, borderSize + row*squareSize, borderSize + columnCount*squareSize, borderSize + row*squareSize);
        }
        for (int column = 1; column < columnCount; column++) {
            g.drawLine(borderSize + column*squareSize, borderSize, borderSize + column*squareSize, borderSize + rowCount*squareSize);
        }

        //draw previous move.
        if (previousMove != null) {
            int row = previousMove.row;
            int column = previousMove.column;

            g.setColor(PREVIOUS_MOVE_COLOR);
            g.setStroke(new BasicStroke(lineWidth));
            g.drawRect(borderSize + column*squareSize, borderSize + row*squareSize, squareSize, squareSize);
        }

        //draw pieces.
        g.setStroke(new BasicStroke(lineWidth));
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                MnkBoardGamePieceType piece = board[row][column];
                if (piece != null) {
                    drawPiece(g, row, column, piece, squareSize, borderSize, lineWidth);
                }
            }
        }

        //draw winning line if k in a row.
        g.setStroke(new BasicStroke(3 * lineWidth));
        OUTER:
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                MnkBoardGamePieceType firstPiece = board[row][column];
                if (firstPiece == null) continue;

                //check for horizontal k in a row.
                if (column <= columnCount - requiredK) {//if square is far enough from right edge.
                    boolean kInARow = true;
                    for (int k = 1; k < requiredK; k++) {
                        MnkBoardGamePieceType currentPiece = board[row][column + k];
                        if (currentPiece == null || currentPiece.player != firstPiece.player) {
                            kInARow = false;
                            break;
                        }
                    }

                    if (kInARow) {
                        //draw line.
                        g.setColor(getColor(firstPiece));
                        g.drawLine(borderSize + column*squareSize, borderSize + (int) ((row + 0.5)*squareSize),
                                borderSize + (column + requiredK)*squareSize, borderSize + (int) ((row + 0.5)*squareSize));
                        break OUTER;
                    }
                }

                //check for vertical k in a row.
                if (row <= rowCount - requiredK) {//if square is far enough from bottom edge.
                    boolean kInARow = true;
                    for (int k = 1; k < requiredK; k++) {
                        MnkBoardGamePieceType currentPiece = board[row + k][column];
                        if (currentPiece == null || currentPiece.player != firstPiece.player) {
                            kInARow = false;
                            break;
                        }
                    }

                    if (kInARow) {
                        g.setColor(getColor(firstPiece));
                        g.drawLine(borderSize + (int) ((column + 0.5)*squareSize), borderSize + row*squareSize,
                                borderSize + (int) ((column + 0.5)*squareSize), borderSize + (row + requiredK)*squareSize);
                        break OUTER;
                    }
                }

                //check for NW-SE diagonal k in a row.
                if (column <= columnCount - requiredK && row <= rowCount - requiredK) {
                    //if square is far enough from right edge and far enough from bottom edge.
                    boolean kInARow = true;
                    for (int k = 1; k < requiredK; k++) {
                        MnkBoardGamePieceType currentPiece = board[row + k][column + k];
                        if (currentPiece == null || currentPiece.player != firstPiece.player) {
                            kInARow = false;
                            break;
                        }
                    }

                    if (kInARow) {
                        g.setColor(getColor(firstPiece));
                        g.drawLine(borderSize + column*squareSize, borderSize + row*squareSize,
                                   borderSize + (column + requiredK)*squareSize, borderSize + (row + requiredK)*squareSize);
                        break OUTER;
                    }
                }

                //check for NE-SW diagonal k in a row.
                if (column >= requiredK - 1 && row <= rowCount - requiredK) {
                    //if square is far enough from left edge and far enough from bottom edge.
                    boolean kInARow = true;
                    for (int k = 1; k < requiredK; k++) {
                        MnkBoardGamePieceType currentPiece = board[row + k][column - k];
                        if (currentPiece == null || currentPiece.player != firstPiece.player) {
                            kInARow = false;
                            break;
                        }
                    }

                    if (kInARow) {
                        g.setColor(getColor(firstPiece));
                        g.drawLine(borderSize + (column + 1)*squareSize, borderSize + row*squareSize,
                                   borderSize + (column - requiredK + 1)*squareSize, borderSize + (row + requiredK)*squareSize);
                        break OUTER;
                    }
                }
            }
        }

        //draw text.
        if (gameOver) {
            String gameOverText;
            if (indexOfPlayerThatMadeAnIllegalMove != -1) {
                gameOverText = playerNames[indexOfPlayerThatMadeAnIllegalMove] + " made an illegal move and ended the game.";
            } else if (indexOfWinner != -1) {
                gameOverText = playerNames[indexOfWinner] + " wins!";
            } else {//if there is a draw.
                gameOverText = "It's a draw.";
            }

            //draw text centered within top border.
            g.setColor(LINE_COLOR);
            int fontSize = borderSize/4;
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
            GraphicsUtils.drawCenteredString(g, gameOverText, borderSize + (rowCount*squareSize)/2, borderSize/2);
        }

        g.dispose();
    }

    private static Color getColor(MnkBoardGamePieceType piece) {
        switch (piece.player) {
            case NOUGHTS_PLAYER:
                return NOUGHT_COLOR;
            case CROSSES_PLAYER:
                return CROSS_COLOR;
            default:
                throw new IllegalStateException("Unknown player " + piece.player);
        }
    }

    private static void drawPiece(Graphics2D g, int row, int column, MnkBoardGamePieceType piece, int squareSize, int borderSize, int lineWidth) {
        switch (piece.player) {
            case NOUGHTS_PLAYER:
                //draw nought.
                g.setColor(getColor(piece));
                g.drawOval(borderSize + column*squareSize + 2*lineWidth, borderSize + row*squareSize + 2*lineWidth,
                        squareSize - 4*lineWidth, squareSize - 4*lineWidth);
                break;
            case CROSSES_PLAYER:
                //draw cross.
                g.setColor(getColor(piece));
                g.drawLine(borderSize + column*squareSize + 2*lineWidth, borderSize + row*squareSize + 2*lineWidth,
                        borderSize + (column + 1)*squareSize - 2*lineWidth, borderSize + (row + 1)*squareSize - 2*lineWidth);
                g.drawLine(borderSize + (column + 1)*squareSize - 2*lineWidth, borderSize + row*squareSize + 2*lineWidth,
                        borderSize + column*squareSize + 2*lineWidth, borderSize + (row + 1)*squareSize - 2*lineWidth);
                break;
            default:
                throw new IllegalStateException("Unknown player " + piece.player);
        }
    }

    /**
     * Experimental feature.
     */
    private static void drawPreviousMoveProbabilities(Graphics2D g, float[][] previousMoveProbabilities, int rowCount, int columnCount, int squareSize, int borderSize) {
        if (previousMoveProbabilities == null) return;

        //get max probability.
        float maxProbability = Float.NEGATIVE_INFINITY;
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                float probability = previousMoveProbabilities[row][column];
                if (probability > maxProbability) maxProbability = probability;
            }
        }
        if (maxProbability <= 0) return;

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                float probability = previousMoveProbabilities[row][column];

                //use exponential scale for better visibility.
                int scaleFactor = (int) Math.floor(255 * Math.expm1(5 * probability) / Math.expm1(5 * maxProbability));
                if (scaleFactor > 255) throw new IllegalStateException("scaleFactor > 255");
                if (scaleFactor < 0) continue;

                g.setColor(new Color(255, 255, 255 - scaleFactor));//scales from white to yellow.
                g.fillRect(borderSize + column*squareSize, borderSize + row*squareSize, squareSize, squareSize);
            }
        }
    }

    @Override
    public int getRowCount() {
        return model.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return model.getColumnCount();
    }

    @Override
    public int getSquareSize() {
        return squareSize;
    }

    @Override
    public int getBorderSize() {
        return borderSize;
    }
}
