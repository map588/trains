package Utilities;

import javafx.beans.property.IntegerProperty;

public class staticBlockInfo {
    public IntegerProperty blockNumber;
    public int section;
    public int blockLength;
    public int blockGrade;
    public int speedLimit;
    public boolean isCrossing;
    public boolean hasInputTrafficLight;
    public boolean hasOutputTrafficLight;
    public final boolean isSwitch;
    public boolean isStation;
    public final BlockInfo switchBlock1;
    public final BlockInfo switchBlock2;
    public BlockInfo selectedSwitch;

    // Default constructor
    public staticBlockInfo() {
        isSwitch = false;
        switchBlock1 = null;
        switchBlock2 = null;
        selectedSwitch = null;
    }

    // Constructor with parameters to set switch
    public staticBlockInfo(BlockInfo switchBlock1, BlockInfo switchBlock2) {
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        selectedSwitch = switchBlock1;
    }

    // Constructor with parameters to set switch and define current switch
    public staticBlockInfo(BlockInfo switchBlock1, BlockInfo switchBlock2, BlockInfo selectedSwitch) {
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        this.selectedSwitch = selectedSwitch;
    }

    //public CheckBox select;
    public boolean isSwitch() {
        return isSwitch;
    }

    public IntegerProperty getBlockNumber() {
        return blockNumber;
    }
}
