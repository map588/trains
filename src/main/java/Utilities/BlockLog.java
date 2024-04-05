package Utilities;

import Utilities.Records.BasicBlock;

public class BlockLog {

    public BlockLog(BasicBlock block) {
        System.out.println("Block: " + block.blockNumber() + " Line: " + block.trackLine() + " Type: " + block.blockType() + " Length: " + block.blockLength() + " Grade: " + block.blockGrade() + " Speed Limit: " + block.speedLimit() + " Elevation: " + block.elevation() + " Cumulative Elevation: " + block.cumulativeElevation());
    }
}
