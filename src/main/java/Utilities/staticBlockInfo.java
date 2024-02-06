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

    public int getBlockNumber() {
        return blockNumber;
    }
}
