package Utilities.Records;

import java.util.List;
import java.util.Optional;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;


public record BasicBlock(
        String trackLine,
        String section,
        int blockNumber,
        double blockLength,
        double blockGrade,
        double speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        boolean isSwitch,
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