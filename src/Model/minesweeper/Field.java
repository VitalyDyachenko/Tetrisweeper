package Model.minesweeper;

public class Field {
    public static final int FIELD_X = 10;
    public static final int FIELD_Y = 20;

    private Cell[][] field = new Cell[FIELD_X][FIELD_Y];

    public void setCell(int x, int y, Cell cell) {
        field[x][y] = cell;
    }
    public Cell getCell(int x, int y) {
        return field[x][y];
    }
}
