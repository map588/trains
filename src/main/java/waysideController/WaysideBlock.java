package waysideController;

public class WaysideBlock {
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
    }

    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }

    public boolean getSwitchRequest() {
        return switchRequest;
    }

    public void setSwitchRequest(boolean switchRequest) {
        this.switchRequest = switchRequest;
    }

    public boolean getLightState() {
        return lightState;
    }

    public void setLightState(boolean lightState) {
        this.lightState = lightState;
    }

    public boolean getCrossingState() {
        return crossingState;
    }

    public void setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;
    }

    public boolean getAuthority() {
        return authority;
    }

    public void setAuthority(boolean authority) {
        this.authority = authority;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

