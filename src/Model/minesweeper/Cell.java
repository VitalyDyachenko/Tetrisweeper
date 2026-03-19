package Model.minesweeper;

import Model.tetris.TetriminoType;

import java.util.Random;

public class Cell {
    public static final double MINE_CHANCE = 0.075;

    private TetriminoType type;
    private boolean have_mine = false;
    private boolean have_flag = false;
    private boolean is_opened = false;

    public boolean haveMine() { return have_mine; }
    public boolean haveFlag() { return have_flag; }
    public boolean isOpened() { return is_opened; }

    public Cell(TetriminoType t, Random rand) {
        type = t;
        have_mine = (rand.nextDouble() <= MINE_CHANCE);
    }
    public TetriminoType getType() { return type; }
    public void changeFlag() {
        have_flag = !have_flag;
    }
    public void open() {
        is_opened = true;
    }
    public void close() {
        is_opened = false;
    }
    public boolean isResolved() {
        if (haveMine()) return haveFlag();
        else return isOpened();
    }
}
