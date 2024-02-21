package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WaysideBlockInfo {
    private final ReadOnlyIntegerProperty blockID;
    private final ReadOnlyBooleanProperty hasSwitch;
    private final ReadOnlyBooleanProperty hasLight;
    private final ReadOnlyBooleanProperty hasCrossing;

    private ReadOnlyIntegerProperty switchBlockMain;
    private ReadOnlyIntegerProperty switchBlockAlt;

    private final BooleanProperty occupation;
    private final BooleanProperty switchState;

    private final BooleanProperty switchRequestedState;
    private IntegerProperty switchedBlockID;
    private final BooleanProperty lightState;

    private final ObjectProperty<Paint> lightStateColor;
    private final BooleanProperty crossingState;

    public WaysideBlockInfo(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing) {
        this.blockID = new ReadOnlyIntegerWrapper(blockID);
        this.hasSwitch = new ReadOnlyBooleanWrapper(hasSwitch);
        this.hasLight = new ReadOnlyBooleanWrapper(hasLight);
        this.hasCrossing = new ReadOnlyBooleanWrapper(hasCrossing);

        this.occupation = new SimpleBooleanProperty(false);
        this.switchState = new SimpleBooleanProperty(false);
        this.switchRequestedState = new SimpleBooleanProperty(false);
        this.lightState = new SimpleBooleanProperty(false);
        this.lightStateColor = new SimpleObjectProperty<>(Color.TRANSPARENT);
        this.crossingState = new SimpleBooleanProperty(false);

//        this.lightState.addListener((observable, oldValue, newValue) -> {
//            System.out.println("Listener");
//            if(isHasLight()) {
//                if(isLightState())
//                    lightStateColor.set(Color.GREEN);
//                else
//                    lightStateColor.set(Color.RED);
//            }
//            else {
//                lightStateColor.set(Color.TRANSPARENT);
//            }
//        });
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

    public void setLightState(boolean lightState) {
        this.lightState.set(lightState);
        if(isHasLight()) {
            if(isLightState())
                lightStateColor.set(Color.GREEN);
            else
                lightStateColor.set(Color.RED);
        }
        else {
            lightStateColor.set(Color.TRANSPARENT);
        }
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

    public boolean isSwitchRequestedState() {
        return switchRequestedState.get();
    }

    public BooleanProperty switchRequestedStateProperty() {
        return switchRequestedState;
    }

    public Paint getLightStateColor() {
        return lightStateColor.get();
    }

    public ObjectProperty<Paint> lightStateColorProperty() {
        return lightStateColor;
    }
}
