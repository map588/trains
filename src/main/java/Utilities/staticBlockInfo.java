package Utilities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class staticBlockInfo {
    public IntegerProperty blockNumber;
    public String section;
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
    public BooleanProperty isSwitched;

    // Default constructor
    public staticBlockInfo(SimpleIntegerProperty val) {
        blockNumber = val;
        isSwitch = false;
        switchBlock1 = null;
        switchBlock2 = null;
        selectedSwitch = null;
        isSwitched = new SimpleBooleanProperty(false);
    }

    // Constructor with parameters to set switch
    public staticBlockInfo(SimpleIntegerProperty val, BlockInfo switchBlock1, BlockInfo switchBlock2) {
        blockNumber = val;
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        selectedSwitch = switchBlock1;
        isSwitched = new SimpleBooleanProperty(false);
    }

    // Constructor with parameters to set switch and define current switch
    public staticBlockInfo(SimpleIntegerProperty val, BlockInfo switchBlock1, BlockInfo switchBlock2, BlockInfo selectedSwitch) {
        blockNumber = val;
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        this.selectedSwitch = selectedSwitch;
        isSwitched = new SimpleBooleanProperty(selectedSwitch.equals(switchBlock2));
    }

    //public CheckBox select;
    public boolean isSwitch() {
        return isSwitch;
    }

    public IntegerProperty getBlockNumber() {
        return blockNumber;
    }
}
