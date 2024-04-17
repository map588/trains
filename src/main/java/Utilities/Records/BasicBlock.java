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
) implements Comparable<BasicBlock> {

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

    public BasicBlock () {
        this(Lines.GREEN, "", 0, 0, 0, 0, 0, 0,
                false, false, false, BlockType.REGULAR, Optional.empty(), Optional.empty(),
                new NextBlock(new Connection(-1, false), new Connection(-1, false), new Connection(-1, false),
                        new Connection(-1, false), new Connection(-1, false), new Connection(-1, false), Direction.NORTH));
    }

    public boolean isStation() {
        return blockType == BlockType.STATION;
    }

    public boolean isCrossing() {
        return blockType == BlockType.CROSSING;
    }

    public boolean isYard() {
        return blockType == BlockType.YARD;
    }

    @Override
    public int compareTo(BasicBlock o) {
        return Integer.compare(this.blockNumber, o.blockNumber);
    }

}