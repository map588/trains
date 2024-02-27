package waysideController;

import Utilities.TrafficLightState;
import javafx.beans.property.*;
import javafx.scene.paint.Paint;

public class WaysideBlockInfo {
    private final ReadOnlyIntegerProperty blockID;
    private final ReadOnlyBooleanProperty hasSwitch;
    private final ReadOnlyBooleanProperty hasCrossing;

    private ReadOnlyIntegerProperty switchBlockMain;
    private ReadOnlyIntegerProperty switchBlockAlt;

    private final BooleanProperty occupation;
    private final BooleanProperty switchState;
    private final BooleanProperty authorityState;

    private final BooleanProperty switchRequestedState;
    private IntegerProperty switchedBlockID;
    private final TrafficLightState lightState;
    private final BooleanProperty crossingState;

    public WaysideBlockInfo(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing) {
        this.blockID = new ReadOnlyIntegerWrapper(blockID);
        this.hasSwitch = new ReadOnlyBooleanWrapper(hasSwitch);
        this.hasCrossing = new ReadOnlyBooleanWrapper(hasCrossing);

        this.occupation = new SimpleBooleanProperty(false);
        this.switchState = new SimpleBooleanProperty(false);
        this.switchRequestedState = new SimpleBooleanProperty(false);
        this.crossingState = new SimpleBooleanProperty(false);
        this.authorityState = new SimpleBooleanProperty(false);

        this.lightState = new TrafficLightState(hasLight);
    }

    public WaysideBlockInfo(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing, int switchBlockMain, int switchBlockAlt) {
        this(blockID, hasSwitch, hasLight, hasCrossing);
        this.switchBlockMain = new ReadOnlyIntegerWrapper(switchBlockMain);
        this.switchBlockAlt = new ReadOnlyIntegerWrapper(switchBlockAlt);
        this.switchedBlockID = new SimpleIntegerProperty(switchBlockMain);

        this.switchState.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                this.switchedBlockID.set(this.getSwitchBlockAlt());
            else
                this.switchedBlockID.set(this.getSwitchBlockMain());
        });
    }

    public int getBlockID() {
        return blockID.get();
    }

    public ReadOnlyIntegerProperty blockIDProperty() {
        return blockID;
    }

    public boolean isHasSwitch() {
        return hasSwitch.get();
    }

    public ReadOnlyBooleanProperty hasSwitchProperty() {
        return hasSwitch;
    }

    public boolean hasLight() {
        return lightState.hasLight();
    }

    public boolean isHasCrossing() {
        return hasCrossing.get();
    }

    public ReadOnlyBooleanProperty hasCrossingProperty() {
        return hasCrossing;
    }

    public boolean isOccupation() {
        return occupation.get();
    }

    public BooleanProperty occupationProperty() {
        return occupation;
    }

    public boolean isSwitchState() {
        return switchState.get();
    }

    public BooleanProperty switchStateProperty() {
        return switchState;
    }

    public boolean getLightState() {
        return lightState.getLightState();
    }

    public void setLightState(boolean lightState) {
        this.lightState.setLightState(lightState);
    }

    public void setSwitchState(boolean switchState) {
        this.switchState.set(switchState);
    }

    public void setCrossingState(boolean crossingState) {
        this.crossingState.set(crossingState);
    }

    public TrafficLightState lightStateProperty() {
        return lightState;
    }

    public boolean isCrossingState() {
        return crossingState.get();
    }

    public BooleanProperty crossingStateProperty() {
        return crossingState;
    }

    public int getSwitchBlockMain() {
        return switchBlockMain.get();
    }

    public ReadOnlyIntegerProperty switchBlockMainProperty() {
        return switchBlockMain;
    }

    public int getSwitchBlockAlt() {
        return switchBlockAlt.get();
    }

    public ReadOnlyIntegerProperty switchBlockAltProperty() {
        return switchBlockAlt;
    }

    public int getSwitchedBlockID() {
        return switchedBlockID.get();
    }

    public IntegerProperty switchedBlockIDProperty() {
        return switchedBlockID;
    }

    public boolean isSwitchRequestedState() {
        return switchRequestedState.get();
    }

    public BooleanProperty switchRequestedStateProperty() {
        return switchRequestedState;
    }

    public boolean isAuthorityState() {
        return authorityState.get();
    }

    public BooleanProperty authorityStateProperty() {
        return authorityState;
    }
}
