package Utilities.Enums;

public enum Direction {
    NORTH,
    SOUTH,
    EITHER;


    public Direction opposite() {
        if (this == NORTH) {
            return SOUTH;
        } else if (this == SOUTH) {
            return NORTH;
        } else {
            return EITHER;
        }
    }
}


