package Utilities;

public class staticBlockInfo {
    public int blockNumber;
    public int section;
    public int blockLength;
    public int blockGrade;
    public int speedLimit;

//    public String switchInfo;
    public boolean isSwitch;
//    public String stationInfo;
    public boolean isStation;
    public staticBlockInfo switchBlock1;
    public staticBlockInfo switchBlock2;

    public boolean isSwitch() {
        return isSwitch;
    }
    public boolean isStation() {return isStation; }
    public int getStation() { return this.section; }
    public int getSection() { return this.section; }
    public int getBlockLength() { return this.blockLength; }
    public int getBlockGrade() { return this.blockGrade; }
    public int getSpeedLimit(){ return this.speedLimit; }
    public int getBlockNumber() {
        return blockNumber;
    }
}
