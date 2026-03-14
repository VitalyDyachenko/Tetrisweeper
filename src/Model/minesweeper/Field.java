package Model.minesweeper;

import Controller.Context;

public class Field {
    public static final int FIELD_X = 10;
    public static final int FIELD_Y = 20;

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
                if (field[x][y] == null) {
                    is_line = false;
                    break;
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
}
