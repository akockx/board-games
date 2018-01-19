/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package boardgame.model;

import util.model.TurnBasedGameModel;

/**
 * Interface to be implemented by classes that store the current state of a board game with a rectangular board that consists of squares.
 *
 * @author A.C. Kockx
 */
public interface BoardGameModel extends TurnBasedGameModel {

    int getRowCount();

    int getColumnCount();
}
