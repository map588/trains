package Utilities;

import java.util.ArrayList;

public record TrueBlockInfo(
        String line,
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
        boolean isOccupied,
        boolean underMaintenance,
        boolean switchState,
        boolean crossingState,
        boolean switchLightState,
        int convergingBlockID,
        int divergingBlockID_Main,
        int divergingBlockID_Alt) {

    public final static ArrayList<TrueBlockInfo> blockList = new ArrayList<>();

}

