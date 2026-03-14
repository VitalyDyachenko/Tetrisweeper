package Controller;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

public class Context {
    public GameMode mode = GameMode.TETRIS;
    public GameState state = GameState.MENU;
    public int score = 0;

    public Field field = new Field();
    public FallingTetrimino tet;
}
