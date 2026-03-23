package game;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

public class Context {
    public GameMode mode = GameMode.TETRIS;
    public GameState state = GameState.MENU;
    public int score = 0;
    public boolean super_rotation_system = true;
    public int level = 1;
    public int lines_cleared = 0;

    public Field field = new Field();
    public FallingTetrimino tet;
    public FallingTetrimino hold_tet;
    public boolean was_hold = false;
    public FallingTetrimino next_tet;
}
