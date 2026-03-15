package Model.tetris;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;
    public static Direction Right(Direction cur) {
        return switch (cur) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
    public static Direction Left(Direction cur) {
        return switch (cur) {
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
        };
    }
}
