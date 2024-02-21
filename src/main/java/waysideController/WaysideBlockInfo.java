package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;

public class WaysideBlockInfo {
    private final ReadOnlyIntegerProperty blockID;
    private final ReadOnlyBooleanProperty hasSwitch;
    private final ReadOnlyBooleanProperty hasLight;
    private final ReadOnlyBooleanProperty hasCrossing;

    private ReadOnlyIntegerProperty switchBlockMain;
    private ReadOnlyIntegerProperty switchBlockAlt;

    private final BooleanProperty occupation;
    private final BooleanProperty switchState;
    private final IntegerProperty switchedBlockID;
    private final BooleanProperty lightState;
    private final BooleanProperty crossingState;

    public WaysideBlockInfo(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing) {
        this.blockID = new ReadOnlyIntegerWrapper(blockID);
        this.hasSwitch = new ReadOnlyBooleanWrapper(hasSwitch);
        this.hasLight = new ReadOnlyBooleanWrapper(hasLight);
        this.hasCrossing = new ReadOnlyBooleanWrapper(hasCrossing);

        this.occupation = new SimpleBooleanProperty(false);
        this.switchState = new SimpleBooleanProperty(false);
        this.switchedBlockID = new SimpleIntegerProperty();
        this.lightState = new SimpleBooleanProperty(false);
        this.crossingState = new SimpleBooleanProperty(false);
    }

    public WaysideBlockInfo(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing, int switchBlockMain, int switchBlockAlt) {
        this(blockID, hasSwitch, hasLight, hasCrossing);
        this.switchBlockMain = new ReadOnlyIntegerWrapper(switchBlockMain);
        this.switchBlockAlt = new ReadOnlyIntegerWrapper(switchBlockAlt);
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

    public boolean isHasLight() {
        return hasLight.get();
    }

    public ReadOnlyBooleanProperty hasLightProperty() {
        return hasLight;
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

    public boolean isLightState() {
        return lightState.get();
    }

    public BooleanProperty lightStateProperty() {
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
}
