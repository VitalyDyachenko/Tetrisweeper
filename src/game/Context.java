package game;

import Model.minesweeper.Field;
import Model.tetris.FallingTetrimino;
import View.music.MusicPlayer;

public class Context {
    public GameMode mode = GameMode.TETRIS;
    public GameState state = GameState.MENU;
    public int score = 0;
    public boolean super_rotation_system = true;

    public Field field = new Field();
    public FallingTetrimino tet;
    public FallingTetrimino hold_tet;
    public boolean was_hold = false;
    public FallingTetrimino next_tet;

    public MusicPlayer music_player = new MusicPlayer(true);
    public MusicPlayer sound_player = new MusicPlayer(false);
}
