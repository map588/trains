package Utilities;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class staticBlockInfo {
    public IntegerProperty blockNumber;
    public IntegerProperty switchedBlockNumber;
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
    public ObjectProperty<BlockInfo> selectedSwitch;
    public BooleanProperty isSwitched;

    // Default constructor
    public staticBlockInfo(SimpleIntegerProperty val) {
        blockNumber = val;
        isSwitch = false;
        switchBlock1 = null;
        switchBlock2 = null;
        selectedSwitch = null;
        isSwitched = new SimpleBooleanProperty(false);
        switchedBlockNumber = null;
    }

    // Constructor with parameters to set switch
    public staticBlockInfo(SimpleIntegerProperty val, BlockInfo switchBlock1, BlockInfo switchBlock2) {
        blockNumber = val;
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        selectedSwitch = new SimpleObjectProperty<>(switchBlock1);
        isSwitched = new SimpleBooleanProperty(false);
        switchedBlockNumber = new SimpleIntegerProperty(switchBlock1.getStaticInfo().blockNumber.getValue());
        isSwitched.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                engageSwitch(newValue);
            }
        });
    }

    // Constructor with parameters to set switch and define current switch
    public staticBlockInfo(SimpleIntegerProperty val, BlockInfo switchBlock1, BlockInfo switchBlock2, BlockInfo sselectedSwitch) {
        blockNumber = val;
        isSwitch = true;
        this.switchBlock1 = switchBlock1;
        this.switchBlock2 = switchBlock2;
        this.selectedSwitch = new SimpleObjectProperty<>(sselectedSwitch);
        isSwitched = new SimpleBooleanProperty(selectedSwitch.equals(switchBlock2));
        switchedBlockNumber = new SimpleIntegerProperty(selectedSwitch.get().getStaticInfo().blockNumber.getValue());
        isSwitched.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                engageSwitch(newValue);
            }
        });
    }

    //public CheckBox select;
    public boolean isSwitch() {
        return isSwitch;
    }

    public IntegerProperty getBlockNumber() {
        return blockNumber;
    }

    /**
     * Engage the switch
     * @param engaged Whether the switch is engaged (True - Block 2, False - Block 1)
     */
    public void engageSwitch(boolean engaged) {
        if(engaged) {
            selectedSwitch.set(switchBlock2);
        } else {
            selectedSwitch.set(switchBlock1);
        }
        switchedBlockNumber.set(selectedSwitch.get().getStaticInfo().blockNumber.getValue());
    }
}
