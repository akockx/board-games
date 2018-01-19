Board Games
===========

A framework for implementing board games in Java. This framework was made as an exercise in using the Model-View-Controller design pattern. A working example implementation of Tic-tac-toe is also included.

Author: A.C. Kockx



Description
===========

This framework makes it easy to implement a turn-based board game for which the board is a rectangle made of squares, like e.g. Chess or Tic-tac-toe. This framework is based on the Model-View-Controller design pattern. To implement your own board game using this framework, you only need to create three classes: a model, a view and a main class. A controller for playing turn-based games is already part of the framework and can be used as-is (see class TurnBasedGameEngine).

To create your own implementation of a turn-based, square-based board game using this framework, follow these steps:

1. Create a class that implements the interface BoardGameModel. This class should include the entire state of your board game and all the rules, i.e. all valid ways to change the state. Think of this as the Model in Model-View-Controller. See for example class MnkBoardGameModel.

2. Create a class that implements the interface SquaresDrawer. This class should include all the code for drawing an image that shows the current state of your board game model. Think of this as the View in Model-View-Controller. See for example class TicTacToeBoardDrawer.

3. Create a main class to bring everything together. This should create a model, a view and a controller, and start the game. See for example class TicTacToe.



Requirements
============

To build this framework Java SDK 8 (or higher) and Gradle need to be installed on your system.



Build
=====

To build this framework, run the following command on the command line in the folder that contains the file "settings.gradle":
  gradle jar

This will produce the following jar files:
  Utils\build\libs\Utils.jar
  BoardGameUtils\build\libs\BoardGameUtils.jar

To run the Tic-tac-toe example implementation, run the following command on the command line in the folder that contains the file "settings.gradle":
  gradle TicTacToe:run
