package Model.tetris;

public enum Rotation {
    NORTH, EAST, SOUTH, WEST;
    public static Rotation Right(Rotation cur) {
        return switch (cur) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
    public static Rotation Left(Rotation cur) {
        return switch (cur) {
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
        };
    }
}
