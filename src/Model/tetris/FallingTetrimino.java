package Model.tetris;

import Controller.Context;
import Controller.GameState;
import Model.minesweeper.Cell;
import Model.minesweeper.Field;
import View.FieldDrawer;

import java.awt.*;
import java.util.Random;

public class FallingTetrimino {
    public static final int TETROMINO_SIZE = 4;
    public static final int SRS_POINTS_COUNT = 4;

    TetriminoType type;
    Rotation rotation = Rotation.NORTH;
    Point pos = new Point(Field.FIELD_X/2 - 1, -1);
    Cell[] cells = new Cell[TETROMINO_SIZE];
    Point[] cells_pos = new Point[TETROMINO_SIZE];

    public FallingTetrimino(Random rand) {
        type = TetriminoType.values()[rand.nextInt(TetriminoType.values().length)];
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            cells[i] = new Cell(type);
        }
        cells_pos = setCellsPos();
    }

    public Point[] getCellsPos() { return cells_pos; }
    public Cell[] getCells() { return cells; }
    public Point getPos() { return pos; }
    public TetriminoType getType() { return type; }

    public boolean haveCollisions(Field field) {
        Point[] points = getCellsPos();
        for (Point p : points) {
            if (pos.y + p.y < 0) return true;
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

    public boolean moveDown(Context context, MoveCause cause) {
        pos.y++;
        if (haveCollisions(context.field)) {
            pos.y--;
            setInField(context);
            return false;
        }
        if (cause == MoveCause.SOFT_DROP) {
            context.score++;
        }
        if (cause == MoveCause.HARD_DROP) {
            context.score += 2;
        }
        return true;
    }
    private void setInField(Context context) {
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            if (pos.y + cells_pos[i].y < 0) {
                context.state = GameState.LOOSE;
                return;
            }
            context.field.setCell(pos.x + cells_pos[i].x, pos.y + cells_pos[i].y, cells[i]);
        }
        context.field.removeLines(context);
    }
    public boolean moveLeft(Field field) {
        pos.x--;
        if (haveCollisions(field)) {
            pos.x++;
            return false;
        }
        return true;
    }
    public boolean moveRight(Field field) {
        pos.x++;
        if (haveCollisions(field)) {
            pos.x--;
            return false;
        }
        return true;
    }
    public boolean rotateRight(Context context) {
        rotation = Rotation.Right(rotation);
        cells_pos = setCellsPos();
        if (haveCollisions(context.field)) {
            if (context.super_rotation_system) {
                Point[] srs_shifts = getSuperRotationSystemShifts(Rotation.Left(rotation),false);
                int last_x = pos.x;
                int last_y = pos.y;
                for (int i = 0; i < SRS_POINTS_COUNT; i++) {
                    pos.x = last_x + srs_shifts[i].x;
                    pos.y = last_y + srs_shifts[i].y;
                    if (!haveCollisions(context.field)) {
                        return true;
                    }
                }
                pos.x = last_x;
                pos.y = last_y;
            }
            rotation = Rotation.Left(rotation);
            cells_pos = setCellsPos();
            return false;
        }
        return true;
    }
    public boolean rotateLeft(Context context) {
        rotation = Rotation.Left(rotation);
        cells_pos = setCellsPos();
        if (haveCollisions(context.field)) {
            if (context.super_rotation_system) {
                Point[] srs_shifts = getSuperRotationSystemShifts(Rotation.Right(rotation), true);
                int last_x = pos.x;
                int last_y = pos.y;
                for (int i = 0; i < SRS_POINTS_COUNT; i++) {
                    pos.x = last_x + srs_shifts[i].x;
                    pos.y = last_y + srs_shifts[i].y;
                    if (!haveCollisions(context.field)) {
                        return true;
                    }
                }
                pos.x = last_x;
                pos.y = last_y;
            }
            rotation = Rotation.Right(rotation);
            cells_pos = setCellsPos();
            return false;
        }
        return true;
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
                            new Point(2, 1),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case O -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                            new Point(1, 0),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                    };
                }
            };
            case I -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(3, 1),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(2, 1),
                            new Point(2, 2),
                            new Point(2, 3),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(3, 2),
                            new Point(2, 2),
                            new Point(1, 2),
                            new Point(0, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(1, 3),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case S -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                            new Point(2, 0),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(2, 2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                            new Point(0, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                    };
                }
            };
            case Z -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 2),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(0, 2),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case L -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(2, 0),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(1, 2),
                            new Point(2, 2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(1, 0),
                            new Point(0, 0),
                    };
                }
            };
            case J -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 2),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[]{
                            new Point(0, 2),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
        };
    }

    public Point[] getSuperRotationSystemShifts(Rotation last_direction, boolean is_left_rotation) {
        return switch (type) {
            case I -> switch (last_direction) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-2, 0),
                            is_left_rotation ? new Point(2, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -2) : new Point(-2, 1),
                            is_left_rotation ? new Point(2, 1) : new Point(1, -2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(2, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 0) : new Point(2, 0),
                            is_left_rotation ? new Point(2, -1) : new Point(-1, -2),
                            is_left_rotation ? new Point(-1, 2) : new Point(2, 1),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(2, 0),
                            is_left_rotation ? new Point(-2, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, 2) : new Point(2, -1),
                            is_left_rotation ? new Point(-2, -1) : new Point(-1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-2, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 0) : new Point(-2, 0),
                            is_left_rotation ? new Point(-2, 1) : new Point(1, 2),
                            is_left_rotation ? new Point(1, -2) : new Point(-2, -1),
                    };
                }
            };
            case T -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 0) : new Point(0, 0),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(0, 0) : new Point(0, 0),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case L -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case J -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case S -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case Z -> switch (rotation) {
                case Rotation.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Rotation.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Rotation.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Rotation.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case O -> new Point[] {
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
            };
        };
    }
}
