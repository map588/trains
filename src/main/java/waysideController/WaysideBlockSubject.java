package waysideController;

import Framework.Support.AbstractSubject;
import Framework.Support.Notifications;
import Utilities.TrafficLightState;
import javafx.beans.property.*;

public class WaysideBlockSubject implements AbstractSubject, Notifications {
    private final ReadOnlyIntegerProperty blockID;
    private final ReadOnlyBooleanProperty hasSwitch;
    private final ReadOnlyBooleanProperty hasCrossing;

    private ReadOnlyIntegerProperty switchBlockMain;
    private ReadOnlyIntegerProperty switchBlockAlt;

    private final BooleanProperty occupation;
    private final BooleanProperty switchState;
    private final BooleanProperty authority;
    private final DoubleProperty speed;

    private final BooleanProperty switchRequestedState;
    private IntegerProperty switchedBlockID;
    private final TrafficLightState lightState;
    private final BooleanProperty crossingState;

    public WaysideBlockSubject(WaysideBlock block) {
        this.blockID = new ReadOnlyIntegerWrapper(block.getBlockID());
        this.hasSwitch = new ReadOnlyBooleanWrapper(block.hasSwitch());
        this.hasCrossing = new ReadOnlyBooleanWrapper(block.hasCrossing());

        this.occupation = new SimpleBooleanProperty(block.isOccupied());
        this.switchState = new SimpleBooleanProperty(block.getSwitchState());
        this.switchRequestedState = new SimpleBooleanProperty(block.getSwitchRequest());
        this.crossingState = new SimpleBooleanProperty(block.getCrossingState());
        this.authority = new SimpleBooleanProperty(block.getAuthority());
        this.speed = new SimpleDoubleProperty(block.getSpeed());

        this.lightState = new TrafficLightState(block.hasLight());

        this.switchBlockMain = new ReadOnlyIntegerWrapper(block.getSwitchBlockMain());
        this.switchBlockAlt = new ReadOnlyIntegerWrapper(block.getSwitchBlockAlt());
        this.switchedBlockID = new SimpleIntegerProperty(block.getSwitchBlockMain());
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

    public boolean hasCrossing() {
        return hasCrossing.get();
    }

    public ReadOnlyBooleanProperty hasCrossingProperty() {
        return hasCrossing;
    }

    public boolean isOccupation() {
        return occupation.get();
    }

    public void setOccupation(boolean occupation) {
        this.occupation.set(occupation);
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

    public boolean getSwitchRequestedState() {
        return switchRequestedState.get();
    }

    public void setSwitchRequestedState(boolean switchRequestedState) {
        this.switchRequestedState.set(switchRequestedState);
    }

    public BooleanProperty switchRequestedStateProperty() {
        return switchRequestedState;
    }

    public boolean getAuthority() {
        return authority.get();
    }

    public void setAuthority(boolean authority) {
        this.authority.set(authority);
    }

    public BooleanProperty authorityProperty() {
        return authority;
    }

    public double getSpeed() {
        return speed.get();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed.set(speed);
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {

    }

    @Override
    public Property<?> getProperty(String propertyName) {
        return null;
    }

    @Override
    public void notifyChange(String property, Object newValue) {

    }
}
