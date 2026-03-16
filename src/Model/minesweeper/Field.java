package Model.minesweeper;

import game.Context;
import game.GameMode;

public class Field {
    public static final int FIELD_X = 10;
    public static final int FIELD_Y = 23;

    private Cell[][] field = new Cell[FIELD_X][FIELD_Y];

    public void clear() {
        for (int y = 0; y < FIELD_Y; y++) {
            for (int x = 0; x < FIELD_X; x++) {
                field[x][y] = null;
            }
        }
    }
    public void setCell(int x, int y, Cell cell) {
        field[x][y] = cell;
    }
    public Cell getCell(int x, int y) {
        return field[x][y];
    }
    public void removeLines(Context context) {
        int shift = 0;
        for (int y = 0; y < FIELD_Y; y++) {
            boolean is_line = true;
            for (int x = 0; x < FIELD_X; x++) {
                if (context.mode == GameMode.TETRIS) {
                    if (field[x][y] == null) {
                        is_line = false;
                        break;
                    }
                }
                if (context.mode == GameMode.TETRISWEEPER) {
                    if (field[x][y] == null || !field[x][y].isResolved()) {
                        is_line = false;
                        break;
                    }
                }
            }
            if (is_line) {
                shift++;
                for (int x = 0; x < FIELD_X; x++) field[x][y] = null;
                for (int i = y; i >= shift; i--) {
                    for (int x = 0; x < FIELD_X; x++) {
                        field[x][i] = field[x][i-1];
                    }
                }
                for (int x = 0; x < FIELD_X; x++) {
                    field[x][0] = null;
                }
            }
        }
        if (shift == 1) context.score += 100;
        else if (shift == 2) context.score += 300;
        else if (shift == 3) context.score += 500;
        else if (shift == 4) context.score += 800;
    }
    public void open(Context context) {
        for (int x = 0; x < FIELD_X; x++) {
            for (int y = 0; y < FIELD_Y; y++) {
                if (field[x][y] != null) {
                    if (field[x][y].isOpened()) {
                        field[x][y].close();
                        open(context, x, y, true);
                    }
                }
            }
        }
    }
    public void open(Context context, int x, int y, boolean root) {
        if (x >= 0 && x < FIELD_X && y >= 0 && y < FIELD_Y &&
                field[x][y] != null &&
                !field[x][y].haveFlag() && !field[x][y].isOpened() &&
                !isCellOnBorder(x, y)) {
            field[x][y].open();
            if (minesNextToMe(x, y) == 0) {
                for (int X = Math.max(0, x - 1); X <= Math.min(FIELD_X - 1, x + 1); X++) {
                    for (int Y = Math.max(0, y - 1); Y <= Math.min(FIELD_Y - 1, y + 1); Y++) {
                        if (X != x || Y != y) open(context, X, Y, false);
                    }
                }
                if (root) removeLines(context);
            }
        }
    }
    public void flag(Context context, int x, int y) {
        if (field[x][y] != null && !field[x][y].isOpened()) {
            field[x][y].changeFlag();
            open(context);
        }
    }
    public boolean isCellOnBorder(int X, int Y) {
        for (int x = Math.max(0, X - 1); x <= Math.min(FIELD_X - 1, X + 1); x++) {
            for (int y = Math.max(0, Y - 1); y <= Math.min(FIELD_Y - 1, Y + 1); y++) {
                if (field[x][y] == null) return true;
            }
        }
        return false;
    }
    public int minesNextToMe(int X, int Y) {
        int n = 0;
        for (int x = Math.max(0, X - 1); x <= Math.min(FIELD_X - 1, X + 1); x++) {
            for (int y = Math.max(0, Y - 1); y <= Math.min(FIELD_Y - 1, Y + 1); y++) {
                if (field[x][y] != null && field[x][y].haveMine()) n++;
            }
        }
        return n;
    }
}
