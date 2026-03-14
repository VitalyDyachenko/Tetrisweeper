package Model.minesweeper;

import Model.tetris.TetriminoType;

public class Cell {
    TetriminoType type;
    public Cell(TetriminoType t) {
        type = t;
    }
    public TetriminoType getType() { return type; }
}
