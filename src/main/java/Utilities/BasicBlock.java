package Utilities;

import java.util.Optional;

public record BasicBlock(
        String trackLine,
        char section,
        int blockNumber,
        int blockLength,
        double blockGrade,
        int speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        BlockType blockType,
        Optional<String> stationName,
        Optional<DoorSide> doorSide,
        Optional<NodeConnection> nodeConnection
) {
    public enum Direction {
        TO_NODE,
        FROM_NODE,
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
        YARD
    }

    public static BasicBlock ofSwitch(String trackLine, char section, int blockNumber, int blockLength,
                                      double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                      boolean isUnderground, Optional<String> stationName, Optional<DoorSide> doorSide,
                                      int parentID, Direction parentDirection,
                                      int defChildID, Direction defDirection,
                                      Optional<Integer> altChildID, Optional<Direction> altDirection) {
        NodeConnection nodeConnection = new NodeConnection(parentID, parentDirection, defChildID, defDirection, altChildID, altDirection);
        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.SWITCH, stationName, doorSide, Optional.of(nodeConnection));
    }

    public static BasicBlock ofStation(String trackLine, char section, int blockNumber, int blockLength,
                                       double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                       boolean isUnderground, String stationName, DoorSide doorSide,
                                       int parentID, Direction parentDirection, int defChildID, Direction defDirection) {
        NodeConnection nodeConnection = new NodeConnection(parentID, parentDirection, defChildID, defDirection, Optional.empty(), Optional.empty());
        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.STATION, Optional.of(stationName), Optional.of(doorSide), Optional.of(nodeConnection));
    }

    public static BasicBlock ofRegular(String trackLine, char section, int blockNumber, int blockLength,
                                       double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                       boolean isUnderground) {
        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.REGULAR, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static BasicBlock ofCrossing(String trackLine, char section, int blockNumber, int blockLength,
                                        double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                        boolean isUnderground) {
        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.CROSSING, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static BasicBlock ofYard(String trackLine, char section, int blockNumber, int blockLength,
                                    double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                    boolean isUnderground) {
        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.YARD, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public record NodeConnection(
            int parentID, Direction parentDirection,
            int defChildID, Direction defDirection,
            Optional<Integer> altChildID, Optional<Direction> altDirection) {}
}