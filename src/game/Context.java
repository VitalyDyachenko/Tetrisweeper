package game;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;

public class Context {
    public GameMode mode = GameMode.TETRIS;
    public GameState state = GameState.MENU;
    public int score = 0;
    public boolean super_rotation_system = true;

    public Field field = new Field();
    public FallingTetrimino tet;
    public FallingTetrimino next_tet;
}
