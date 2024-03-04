package Utilities;

import java.util.Optional;

public record BasicBlockInfo(

//TODO: add non positional constructor

        String trackLine,
        char section,
        int prevBlock,
        int nextBlock,
        int blockNumber,
        int blockLength,
        double blockGrade,
        int speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        BlockType blockType, // Including the BlockType enum here
        Optional<Integer> alternativeBlock, // For switches; contains the alternative block ID or empty
        Optional<String> stationName, // For stations; contains the station name or empty
        Optional<DoorSide> doorSide // For stations; indicates the door side or empty
) {
    // Enum for door side at stations
    public enum DoorSide {
        LEFT,
        RIGHT,
        BOTH
    }

    public enum BlockType {
        REGULAR,
        SWITCH_IN,
        SWITCH_OUT,
        STATION,
        CROSSING,
        YARD,
        END
    }

    // Simplify object creation for different block types
    // Factory method for creating a switch block with an alternative switch position
    public static BasicBlockInfo ofSwitchIn(String trackLine, char section, int blockNumber, int blockLength,
                                     double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                     boolean isUnderground, int prevBlock, int nextBlock, int alternativeSwitchBlock) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.SWITCH_IN, Optional.of(alternativeSwitchBlock),
                Optional.empty(), Optional.empty());
    }

    public static BasicBlockInfo ofSwitchOut(String trackLine, char section, int blockNumber, int blockLength,
                                            double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                            boolean isUnderground, int prevBlock, int nextBlock, int alternativeSwitchBlock) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.SWITCH_OUT, Optional.of(alternativeSwitchBlock),
                Optional.empty(), Optional.empty());
    }

    // Factory method for creating a station block with a station name and door side
    public static BasicBlockInfo ofStation(String trackLine, char section, int blockNumber, int blockLength,
                                      double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                      boolean isUnderground, int prevBlock, int nextBlock, String stationName, DoorSide doorSide) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.STATION, Optional.empty(),
                Optional.of(stationName), Optional.of(doorSide));
    }

    // Factory method for creating a regular block
    public static BasicBlockInfo ofRegular(String trackLine, char section, int blockNumber, int blockLength,
                                      double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                      boolean isUnderground, int prevBlock, int nextBlock) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.REGULAR, Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    // Factory method for creating a crossing block
    public static BasicBlockInfo ofCrossing(String trackLine, char section, int blockNumber, int blockLength,
                                       double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                       boolean isUnderground, int prevBlock, int nextBlock) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.CROSSING, Optional.empty(),
                Optional.empty(), Optional.empty());
    }

    // Factory method for creating a yard block
    public static BasicBlockInfo ofYard(String trackLine, char section, int blockNumber, int blockLength,
                                    double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                    boolean isUnderground, int prevBlock, int nextBlock) {
        return new BasicBlockInfo(trackLine, section, prevBlock, nextBlock, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, BlockType.YARD, Optional.empty(),
                Optional.empty(), Optional.empty());
    }

}


