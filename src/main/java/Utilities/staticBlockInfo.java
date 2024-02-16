package Utilities;

public class staticBlockInfo {
    public int blockNumber;
    public int section;
    public int blockLength;
    public int blockGrade;
    public int speedLimit;


    public boolean isSwitch;
    public boolean isStation;
    public staticBlockInfo switchBlock1;
    public staticBlockInfo switchBlock2;

    public boolean isSwitch() {
        return isSwitch;
    }
    public boolean isStation() {return isStation; }
    public int getStation() { return this.section; }
    public int getBlockLength() { return this.blockLength; }
    public int getBlockGrade() { return this.blockGrade; }
    public int getSpeedLimit(){ return this.speedLimit; }
    public int getBlockNumber() {
        return blockNumber;
    }
}
