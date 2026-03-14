package Model.tetris;

import Model.minesweeper.Cell;
import Model.minesweeper.Field;

import java.awt.*;

public class FallingTetrimino {
    public static final int TETROMINO_SIZE = 4;

    TetriminoType type = TetriminoType.T;
    Rotation rotation = Rotation.NORTH;
    Point pos = new Point(0, 0);
    Cell[] cells = new Cell[TETROMINO_SIZE];
    Point[] cells_pos = new Point[TETROMINO_SIZE];

    public FallingTetrimino() {
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            cells[i] = new Cell(type);
        }
        cells_pos = setCellsPos();
    }

    public Point[] getCellsPos() { return cells_pos; }
    public Cell[] getCells() { return cells; }
    public Point getPos() { return pos; }

    public boolean haveCollisions(Field field) {
        Point[] points = getCellsPos();
        for (Point p : points) {
            if (
                    pos.y + p.y >= Field.FIELD_Y ||
                    pos.x + p.x >= Field.FIELD_X ||
                    pos.x + p.x < 0 ||
                    field.getCell(pos.x + p.x, pos.y + p.y) != null
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean move(Field field) {
        pos.y++;
        if (haveCollisions(field)) {
            pos.y--;
            setInField(field);
            return false;
        }
        return true;
    }
    private void setInField(Field field) {
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            field.setCell(pos.x + cells_pos[i].x, pos.y + cells_pos[i].y, cells[i]);
        }
    }

    // Устанавливает координаты всех ячеек фигуры.
    // Координаты отсчитываются от левого верхнего угла рамки фигуры, начиная с 0,
    // ось x направлена вправо, y - вниз.
    public Point[] setCellsPos() {
        return switch (type) {
            case T -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1)
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1)
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(2, 1)
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(1, 2)
                    };
                }
            };
        };
    }
}
