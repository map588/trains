package trainController.ControllerBlocks;

import Utilities.Enums.BlockType;
import Utilities.Records.BasicBlock;

public record ControllerBlock(int blockNumber,
                              double blockLength,
                              double speedLimit,
                              boolean isUnderground,
                              String Doorside,
                              String stationName,
                              boolean isStation,
                              boolean isCrossing,
                              boolean isSwitch
) {

    public ControllerBlock(BasicBlock block) {
        this(block.blockNumber(), block.blockLength(),
                block.speedLimit(), block.isUnderground(),
                block.doorDirection().orElse("N/A"), block.stationName().orElse("N/A"),
                block.blockType() == BlockType.STATION,
                block.blockType() == BlockType.CROSSING,
                block.isSwitch());
    }
}