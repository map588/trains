package trainController.ControllerBlocks;

import Utilities.Enums.BlockType;
import Utilities.Records.BasicBlock;

import java.util.Optional;

public record ControllerBlock(int blockNumber,
                              double blockLength,
                              double speedLimit,
                              boolean isUnderground,
                              Optional<String> Doorside,
                              Optional<String> stationName,
                              boolean isStation,
                              boolean isCrossing,
                              boolean isSwitch
) {
    public ControllerBlock(BasicBlock block) {
        this(block.blockNumber(), block.blockLength(),
                block.speedLimit(), block.isUnderground(),
                block.doorDirection(), block.stationName(),
                block.blockType() == BlockType.STATION,
                block.blockType() == BlockType.CROSSING,
                block.isSwitch());
    }
}