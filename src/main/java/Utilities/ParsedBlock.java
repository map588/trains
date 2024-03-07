
package Utilities;

import java.util.Optional;

public record ParsedBlock(
        String trackLine,
        char section,
        int blockNumber,
        int blockLength,
        double blockGrade,
        int speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        BlockType blockType, // Including the BlockType enum here
        Optional<String> stationName, // For stations; contains the station name or empty
        Optional<DoorSide> doorSide, // For stations; indicates the door side or empty
        Optional<Integer> defaultBlock, // New field for switch block 1
        Optional<Direction> defaultDirection, // New field for switch direction 1
        Optional<Integer> altBlock, // New field for switch block 2
        Optional<Direction> altDirection // New field for switch direction 2
) {
    // Enum for door side at stations
    public enum Direction {
        IN,
        OUT,
        BIDIRECTIONAL
    }

    public enum DoorSide {
        LEFT,
        RIGHT,
        BOTH
    }

    public enum BlockType {
        REGULAR,
        SWITCH,
        STATION,
        CROSSING,
        YARD,
        END
    }

    // Simplify object creation for different block types
    // Factory method for creating a switch block with an alternative switch position
    public static ParsedBlock ofSwitch(String trackLine, char section, int blockNumber, int blockLength,
                                         double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                         boolean isUnderground, int defBlock, Direction defDirection, int altBlock, Direction altDirection) {
        return new ParsedBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground,
                BlockType.SWITCH, Optional.empty(), Optional.empty(),
                Optional.of(defBlock), Optional.of(defDirection), Optional.of(altBlock), Optional.of(altDirection));
    }

    // Factory method for creating a station block with a station name and door side
    public static ParsedBlock ofStation(String trackLine, char section, int blockNumber, int blockLength,
                                           double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                           boolean isUnderground, String stationName, DoorSide doorSide) {
        return new ParsedBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground,
                BlockType.STATION, Optional.of(stationName), Optional.of(doorSide),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    // Factory method for creating a regular block
    public static ParsedBlock ofRegular(String trackLine, char section, int blockNumber, int blockLength,
                                           double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                           boolean isUnderground) {
        return new ParsedBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground,
                BlockType.REGULAR, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    // Factory method for creating a crossing block
    public static ParsedBlock ofCrossing(String trackLine, char section, int blockNumber, int blockLength,
                                            double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                            boolean isUnderground) {
        return new ParsedBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground,
                BlockType.CROSSING, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    // Factory method for creating a yard block
    public static ParsedBlock ofYard(String trackLine, char section, int blockNumber, int blockLength,
                                        double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                        boolean isUnderground) {
        return new ParsedBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground,
                BlockType.YARD, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

}


