package Model.tetris;

import java.awt.*;

public class FallingTetromino {
    TetrominoType type = TetrominoType.T;
    Rotation rotation = Rotation.NORTH;
    Point pos = new Point(0, 0);

    public FallingTetromino() {
        // type =
    }

    public void move() { pos.y++; }

    public Point getPos() { return pos; }

    // Получает координаты всех ячеек фигуры.
    // Координаты отсчитываются от левого верхнего угла рамки фигуры, начиная с 0,
    // ось x направлена вправо, y - вниз.
    public Point[] getAllCoordinates() {
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
