package Model.tetris;

import game.Context;
import game.GameMode;
import game.GameState;
import Model.minesweeper.Cell;
import Model.minesweeper.Field;

import java.awt.*;
import java.util.Random;

public class FallingTetrimino {
    public static final int TETROMINO_SIZE = 4;
    public static final int SRS_POINTS_COUNT = 4;

    private TetriminoType type;
    private Direction direction = Direction.NORTH;
    private Point pos = new Point(Field.FIELD_X/2 - 1, -1);
    private Cell[] cells = new Cell[TETROMINO_SIZE];
    private Point[] cells_pos = new Point[TETROMINO_SIZE];

    public FallingTetrimino(Random rand) {
        type = TetriminoType.values()[rand.nextInt(TetriminoType.values().length)];
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            cells[i] = new Cell(type, rand);
        }
        cells_pos = setCellsPos();
    }

    public Point[] getCellsPos() { return cells_pos; }
    public Cell[] getCells() { return cells; }
    public Point getPos() { return pos; }
    public TetriminoType getType() { return type; }

    public void setToStart() {
        pos.x = Field.FIELD_X/2 - 1;
        pos.y = -1;
    }

    public boolean haveCollisions(Field field) {
        Point[] points = getCellsPos();
        for (Point p : points) {
            if (!(pos.y + p.y < 0 && pos.x + p.x < Field.FIELD_X && pos.x + p.x >= 0)) {
                if (
                        pos.y + p.y >= Field.FIELD_Y ||
                                pos.x + p.x >= Field.FIELD_X ||
                                pos.x + p.x < 0 ||
                                field.getCell(pos.x + p.x, pos.y + p.y) != null
                ) {
                    return true;
                }
            }
        }
        return false;
    }
    public static class MoveResult {
        public boolean landed;
        public boolean was_lines_cleared;

        public MoveResult(boolean landed, boolean lines) {
            this.landed = landed;
            this.was_lines_cleared = lines;
        }
    }
    public MoveResult moveDown(Context context, MoveCause cause) {
        pos.y++;
        if (haveCollisions(context.field)) {
            pos.y--;
            boolean was_lines_cleared = setInField(context);
            return new MoveResult(true, was_lines_cleared);
        }
        if (cause == MoveCause.SOFT_DROP) {
            context.score++;
        }
        if (cause == MoveCause.HARD_DROP) {
            context.score += 2;
        }
        return new MoveResult(false, false);
    }
    public boolean hardDrop(Context context) {
        MoveResult res;
        do {
            res = moveDown(context, MoveCause.HARD_DROP);
        }
        while (!res.landed);
        return res.was_lines_cleared;
    }
    private boolean setInField(Context context) {
        context.was_hold = false;
        for (int i = 0; i < TETROMINO_SIZE; i++) {
            if (pos.y + cells_pos[i].y < 0) {
                context.state = GameState.LOSE;
                return false;
            }
            context.field.setCell(pos.x + cells_pos[i].x, pos.y + cells_pos[i].y, cells[i]);
        }
        boolean was_lines_deleted = false;
        if (context.mode == GameMode.TETRIS) {
            was_lines_deleted = context.field.resolveLines(context);
        }
        else if (context.mode == GameMode.TETRISWEEPER) {
            was_lines_deleted = context.field.update(context);
        }
        return was_lines_deleted;
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
        Direction last_direction = direction;
        direction = Direction.Right(direction);
        cells_pos = setCellsPos();
        if (haveCollisions(context.field)) {
            if (context.super_rotation_system) {
                Point[] srs_shifts = getSuperRotationSystemShifts(last_direction,false);
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
            direction = last_direction;
            cells_pos = setCellsPos();
            return false;
        }
        return true;
    }
    public boolean rotateLeft(Context context) {
        Direction last_direction = direction;
        direction = Direction.Left(direction);
        cells_pos = setCellsPos();
        if (haveCollisions(context.field)) {
            if (context.super_rotation_system) {
                Point[] srs_shifts = getSuperRotationSystemShifts(last_direction, true);
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
            direction = last_direction;
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
            case T -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case O -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                            new Point(1, 0),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                    };
                }
            };
            case I -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(3, 1),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(2, 1),
                            new Point(2, 2),
                            new Point(2, 3),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(3, 2),
                            new Point(2, 2),
                            new Point(1, 2),
                            new Point(0, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(1, 3),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case S -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                            new Point(2, 0),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(2, 2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                            new Point(0, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 0),
                    };
                }
            };
            case Z -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(2, 1),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 2),
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(0, 2),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(1, 0),
                    };
                }
            };
            case L -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                            new Point(2, 0),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(1, 2),
                            new Point(2, 2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(0, 1),
                            new Point(0, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[]{
                            new Point(1, 2),
                            new Point(1, 1),
                            new Point(1, 0),
                            new Point(0, 0),
                    };
                }
            };
            case J -> switch (direction) {
                case Direction.NORTH -> {
                    yield new Point[]{
                            new Point(0, 0),
                            new Point(0, 1),
                            new Point(1, 1),
                            new Point(2, 1),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[]{
                            new Point(2, 0),
                            new Point(1, 0),
                            new Point(1, 1),
                            new Point(1, 2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[]{
                            new Point(2, 2),
                            new Point(2, 1),
                            new Point(1, 1),
                            new Point(0, 1),
                    };
                }
                case Direction.WEST -> {
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
    public Point[] getSuperRotationSystemShifts(Direction last_direction, boolean is_left_rotation) {
        return switch (type) {
            case I -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-2, 0),
                            is_left_rotation ? new Point(2, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -2) : new Point(-2, 1),
                            is_left_rotation ? new Point(2, 1) : new Point(1, -2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(2, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 0) : new Point(2, 0),
                            is_left_rotation ? new Point(2, -1) : new Point(-1, -2),
                            is_left_rotation ? new Point(-1, 2) : new Point(2, 1),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(2, 0),
                            is_left_rotation ? new Point(-2, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, 2) : new Point(2, -1),
                            is_left_rotation ? new Point(-2, -1) : new Point(-1, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-2, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 0) : new Point(-2, 0),
                            is_left_rotation ? new Point(-2, 1) : new Point(1, 2),
                            is_left_rotation ? new Point(1, -2) : new Point(-2, -1),
                    };
                }
            };
            case T -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 0) : new Point(0, 0),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(0, 0) : new Point(0, 0),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case L -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case J -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case S -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Direction.WEST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(-1, 1) : new Point(-1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(-1, -2) : new Point(-1, -2),
                    };
                }
            };
            case Z -> switch (last_direction) {
                case Direction.NORTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(-1, 0),
                            is_left_rotation ? new Point(1, -1) : new Point(-1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(1, 2) : new Point(-1, 2),
                    };
                }
                case Direction.EAST -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(1, 1) : new Point(1, 1),
                            is_left_rotation ? new Point(0, -2) : new Point(0, -2),
                            is_left_rotation ? new Point(1, -2) : new Point(1, -2),
                    };
                }
                case Direction.SOUTH -> {
                    yield new Point[] {
                            is_left_rotation ? new Point(-1, 0) : new Point(1, 0),
                            is_left_rotation ? new Point(-1, -1) : new Point(1, -1),
                            is_left_rotation ? new Point(0, 2) : new Point(0, 2),
                            is_left_rotation ? new Point(-1, 2) : new Point(1, 2),
                    };
                }
                case Direction.WEST -> {
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
