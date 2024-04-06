package Utilities;

import Utilities.Records.BasicBlock;

public class BlockLog {
    public BlockLog(BasicBlock block) {
        System.out.println("Block: " + block.blockNumber() + " Line: " + block.trackLine() + " Type: " + block.blockType());
    }
}
