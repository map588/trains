package Utilities.Records;

import Utilities.Enums.BlockType;

import java.util.Optional;

public record BeaconEntry(int blockNumber,
                          double blockLength,
                          double speedLimit,
                          boolean isUnderground,
                          Optional<String> Doorside,
                          Optional<String> stationName,
                          boolean isStation,
                          boolean isCrossing,
                          boolean isSwitch
) {
    public BeaconEntry(BasicBlock block) {
        this(block.blockNumber(), block.blockLength(),
                block.speedLimit(), block.isUnderground(),
                block.doorDirection(), block.stationName(),
                block.blockType() == BlockType.STATION,
                block.blockType() == BlockType.CROSSING,
                block.isSwitch());
    }
}