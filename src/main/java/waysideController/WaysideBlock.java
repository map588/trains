package waysideController;

import Framework.Support.Notifications;

import static waysideController.Properties.*;

public class WaysideBlock implements Notifications {
    private final int blockID;
    private final boolean hasSwitch;
    private final boolean hasLight;
    private final boolean hasCrossing;
    private int switchBlockMain;
    private int switchBlockAlt;

    private boolean occupied;
    private boolean switchState;
    private boolean switchRequest;
    private boolean lightState;
    private boolean crossingState;
    private boolean authority;
    private double speed;

    private WaysideBlockSubject subject;

    public WaysideBlock(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing) {
        this.blockID = blockID;
        this.hasSwitch = hasSwitch;
        this.hasLight = hasLight;
        this.hasCrossing = hasCrossing;
    }

    public WaysideBlock(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing, int switchBlockMain, int switchBlockAlt) {
        this(blockID, hasSwitch, hasLight, hasCrossing);
        this.switchBlockMain = switchBlockMain;
        this.switchBlockAlt = switchBlockAlt;
    }

    public void setSubject(WaysideBlockSubject subject) {
        this.subject = subject;
    }

    void setValue(String propertyName, Object newValue) {
        switch (propertyName) {
            case occupied_p -> setOccupied((boolean) newValue);
            case switchState_p -> setSwitchState((boolean) newValue);
            case switchRequest_p -> setSwitchRequest((boolean) newValue);
            case lightState_p -> setLightState((boolean) newValue);
            case crossingState_p -> setCrossingState((boolean) newValue);
            case authority_p -> setAuthority((boolean) newValue);
            case speed_p -> setSpeed((double) newValue);
            default -> System.err.println("Property " + propertyName + " not found in WaysideBlock");
        }
    }

    public int getBlockID() {
        return blockID;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public boolean hasLight() {
        return hasLight;
    }

    public boolean hasCrossing() {
        return hasCrossing;
    }

    public int getSwitchBlockMain() {
        return switchBlockMain;
    }

    public int getSwitchBlockAlt() {
        return switchBlockAlt;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        subject.notifyChange(occupied_p, occupied);
    }

    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
        subject.notifyChange(switchState_p, switchState);
    }

    public boolean getSwitchRequest() {
        return switchRequest;
    }

    public void setSwitchRequest(boolean switchRequest) {
        this.switchRequest = switchRequest;
        subject.notifyChange(switchRequest_p, switchRequest);
    }

    public boolean getLightState() {
        return lightState;
    }

    public void setLightState(boolean lightState) {
        this.lightState = lightState;
        subject.notifyChange(lightState_p, lightState);
    }

    public boolean getCrossingState() {
        return crossingState;
    }

    public void setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;
        subject.notifyChange(crossingState_p, crossingState);
    }

    public boolean getAuthority() {
        return authority;
    }

    public void setAuthority(boolean authority) {
        this.authority = authority;
        subject.notifyChange(authority_p, authority);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        subject.notifyChange(speed_p, speed);
    }

    @Override
    public void notifyChange(String property, Object newValue) {
        switch (property) {
            case occupied_p -> setOccupied((boolean) newValue);
            case switchState_p -> setSwitchState((boolean) newValue);
            case switchRequest_p -> setSwitchRequest((boolean) newValue);
            case lightState_p -> setLightState((boolean) newValue);
            case crossingState_p -> setCrossingState((boolean) newValue);
            case authority_p -> setAuthority((boolean) newValue);
            case speed_p -> setSpeed((double) newValue);
            default -> System.err.println("Property " + property + " not found in WaysideBlock");
        }
    }
}

