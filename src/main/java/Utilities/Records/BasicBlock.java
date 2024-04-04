package Utilities.Records;

import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;

import java.util.Optional;


public record BasicBlock(
        Lines trackLine,
        String section,
        int blockNumber,
        double blockLength,
        double blockGrade,
        double speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        boolean isSwitch,
        boolean isLight,
        BlockType blockType,
        Optional<String> stationName,
        Optional<String> doorDirection,
        NextBlock nextBlock
) {

    public record Connection(
            int blockNumber,
            boolean directionChange
    ) {
    }

    public record NextBlock(
            Connection north,
            Connection south,
            Connection northDefault,
            Connection northAlternate,
            Connection southDefault,
            Connection southAlternate,
            Direction primarySwitchDirection
    ) {
    }





}