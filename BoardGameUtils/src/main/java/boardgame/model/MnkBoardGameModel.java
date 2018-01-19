/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.model;

import util.model.ExposedObservable;
import util.model.Move;

import java.util.Observer;

/**
 * Stores the current state of an m,n,k-game.
 * In an m,n,k-game two players alternately place a piece of their own color on an m x n board.
 * The winner is the first player that gets k pieces of their own color in a row.
 * In this context Tic-tac-toe is the (3,3,3)-game.
 * For more information see https://en.wikipedia.org/wiki/M,n,k-game
 *
 * Note that this class can in principle also be used for playing an m,n,k-game with more than two players.
 *
 * @author A.C. Kockx
 */
public final class MnkBoardGameModel implements BoardGameModel {
    //wrapped object to handle observers.
    private final ExposedObservable observable = new ExposedObservable();

    private final int rowCount;//m
    private final int columnCount;//n
    private final int winningNumberOfPiecesInARow;//k

    private final MnkBoardGamePieceType[][] board;
    private final MnkBoardGamePieceType[] pieceTypes;
    private final String[] playerNames;

    private SquareMove previousMove = null;
    private float[][] previousMoveProbabilities = null;

    /**
     * Player that is allowed to make the next move.
     *
     * This is a 1-based index, i.e.:
     * 1 = player 1
     * 2 = player 2
     * etc.
     */
    private int numberOfPlayerToMoveNext = 1;
    private int indexOfPlayerThatMadeAnIllegalMove = -1;
    private int indexOfWinner = -1;
    private boolean gameOver = false;

    /**
     * Creates an empty board for an m,n,k-game that can be played by the given players.
     *
     * @param rowCount (m)
     * @param columnCount (n)
     * @param winningNumberOfPiecesInARow (k)
     */
    public MnkBoardGameModel(int rowCount, int columnCount, int winningNumberOfPiecesInARow, String[] playerNames) {
        if (rowCount <= 0) throw new IllegalArgumentException("rowCount <= 0");
        if (columnCount <= 0) throw new IllegalArgumentException("columnCount <= 0");
        if (winningNumberOfPiecesInARow <= 0) throw new IllegalArgumentException("winningNumberOfPiecesInARow <= 0");
        if (playerNames == null || playerNames.length <= 0) throw new IllegalArgumentException("playerNames is empty");

        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.winningNumberOfPiecesInARow = winningNumberOfPiecesInARow;
        this.playerNames = playerNames;

        //create one pieceType for each player.
        //pieces are re-used for multiple squares. This is possible as long as all pieces of the same player are identical.
        pieceTypes = new MnkBoardGamePieceType[playerNames.length];
        for (int player = 1; player <= playerNames.length; player++) {
            pieceTypes[player - 1] = new MnkBoardGamePieceType(player);
        }

        //create empty board.
        board = new MnkBoardGamePieceType[rowCount][columnCount];

        reset();
    }

    /**
     * Reset model to initial state of a game.
     */
    @Override
    public void reset() {
        //empty board.
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                board[row][column] = null;
            }
        }

        previousMove = null;
        previousMoveProbabilities = null;

        numberOfPlayerToMoveNext = 1;
        indexOfPlayerThatMadeAnIllegalMove = -1;
        indexOfWinner = -1;
        gameOver = false;

        setChangedAndNotifyObservers();
    }

    @Override
    public int getIndexOfCurrentPlayer() {
        if (gameOver) throw new IllegalStateException("Game is already over.");
        return numberOfPlayerToMoveNext - 1;
    }

    @Override
    public boolean isLegalMove(Move gameMove) {
        if (gameOver) throw new IllegalStateException("Game is already over.");
        if (gameMove == null) throw new IllegalArgumentException("gameMove == null");
        if (!(gameMove instanceof SquareMove)) throw new IllegalStateException("gameMove must be an instance of " + SquareMove.class.getSimpleName());
        SquareMove move = (SquareMove) gameMove;

        if (move.row < 0 || move.row >= rowCount || move.column < 0 || move.column >= columnCount) return false;//if outside board.
        if (board[move.row][move.column] != null) return false;//if square not empty.
        return true;//if empty square.
    }

    @Override
    public Move[] getLegalMoves() {
        if (gameOver) throw new IllegalStateException("Game is already over.");

        Move[] moves = new Move[rowCount*columnCount];

        int legalMoveCount = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (board[row][column] == null) {//if empty square.
                    moves[legalMoveCount++] = new SquareMove(row, column);
                }
            }
        }

        Move[] legalMoves = new Move[legalMoveCount];
        System.arraycopy(moves, 0, legalMoves, 0, legalMoveCount);
        return legalMoves;
    }

    /**
     * Changes the state of the model by making the given move for the current player.
     */
    @Override
    public void tryMove(Move gameMove, float[][] moveProbabilities) {
        if (gameOver) throw new IllegalStateException("Game is already over.");
        if (!(gameMove instanceof SquareMove)) throw new IllegalStateException("gameMove must be an instance of " + SquareMove.class.getSimpleName());
        SquareMove move = (SquareMove) gameMove;

        //store move.
        previousMove = move;
        previousMoveProbabilities = moveProbabilities;
        if (!isLegalMove(move)) {//if current player tries to make an illegal move.
            //current player loses the game immediately.
            indexOfPlayerThatMadeAnIllegalMove = getIndexOfCurrentPlayer();
            numberOfPlayerToMoveNext = -1;
            gameOver = true;
            setChangedAndNotifyObservers();
            return;
        }

        //if legal move.
        makeMove(move);
        int indexOfWinner = determineWinner();
        if (indexOfWinner != -1) {//if there is a winner.
            this.indexOfWinner = indexOfWinner;
            numberOfPlayerToMoveNext = -1;
            gameOver = true;
            setChangedAndNotifyObservers();
            return;
        }

        //if there is no winner yet.
        //move on to next player.
        numberOfPlayerToMoveNext = numberOfPlayerToMoveNext%playerNames.length + 1;
        if (getLegalMoves().length <= 0) {//if no moves left for next player.
            //if there is a draw.
            numberOfPlayerToMoveNext = -1;
            gameOver = true;
            setChangedAndNotifyObservers();
            return;
        }

        //notify observers.
        setChangedAndNotifyObservers();
    }

    private void makeMove(SquareMove move) {
        //add new piece to board.
        int newPieceTypeIndex = numberOfPlayerToMoveNext - 1;
        //pieces are re-used for multiple squares. This is possible as long as all pieces of the same player are identical.
        board[move.row][move.column] = pieceTypes[newPieceTypeIndex];
    }

    @Override
    public void undoMove() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support undoMove.");
    }

    /**
     * Returns the index of the winning player, or -1 if there is no winner (yet).
     */
    private int determineWinner() {
        //check for k in a row.
        int requiredK = winningNumberOfPiecesInARow;
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
                        return firstPiece.player - 1;
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
                        return firstPiece.player - 1;
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
                        return firstPiece.player - 1;
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
                        return firstPiece.player - 1;
                    }
                }
            }
        }

        //if there is no winner (yet).
        return -1;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public int getIndexOfWinner() {
        return indexOfWinner;
    }

    @Override
    public int getIndexOfPlayerThatMadeAnIllegalMove() {
        return indexOfPlayerThatMadeAnIllegalMove;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    public int getWinningNumberOfPiecesInARow() {
        return winningNumberOfPiecesInARow;
    }

    /**
     * Returns an m x n board of squares. Each square can be empty (null) or contain a piece (MnkBoardGamePieceType).
     */
    public MnkBoardGamePieceType[][] getBoard() {
        return board;
    }

    /**
     * Can be null.
     */
    public SquareMove getPreviousMove() {
        return previousMove;
    }

    @Override
    public float[][] getPreviousMoveProbabilities() {
        return previousMoveProbabilities;
    }

    @Override
    public String[] getPlayerNames() {
        return playerNames;
    }

    @Override
    public void addObserver(Observer o) {
        observable.addObserver(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observable.deleteObserver(o);
    }

    @Override
    public void setChangedAndNotifyObservers() {
        observable.setChanged();
        observable.notifyObservers();
    }
}
