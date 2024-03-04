package Utilities;


public record BasicBlockInfo(
    String trackLine,
    char section,
    int blockNumber,
    int blockLength,
    double blockGrade,
    int speedLimit,
    double elevation,
    double cumulativeElevation,
    boolean isUnderground,
    boolean hasStation,
    String stationName,
    boolean hasRightPlatform,
    boolean hasLeftPlatform,
    boolean isSwitchConvergingBlock,
    boolean isSwitchDivergingBlock,
    boolean hasCrossing,
    boolean hasSwitchLight,
    int convergingBlockID,
    int divergingBlockID_Main,
    int divergingBlockID_Alt,

    //TODO: The below fields are not constants, and should be constructed in a class that operates on them.
    boolean isOccupied,
    boolean underMaintenance,
    boolean switchState,
    boolean crossingState,
    boolean switchLightState) {}
//TODO: add non positional constructor

