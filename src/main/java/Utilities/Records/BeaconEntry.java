package Utilities.Records;

public record BeaconEntry(int blockNumber, double blockLength, double speedLimit, boolean isSwitch) {
    public BeaconEntry(BasicBlock block) {
        this(block.blockNumber(), block.blockLength(), block.speedLimit(), block.isSwitch());
    }
}