package waysideController;

import Framework.Support.Notifier;
import Utilities.BasicBlock;
import Utilities.BasicBlockInfo;

import static waysideController.Properties.*;

public class WaysideBlock implements Notifier {
    private final int blockID;
    private final boolean hasSwitch;
    private final boolean hasLight;
    private final boolean hasCrossing;
    private int switchBlockMain;
    private int switchBlockAlt;

    private boolean occupied;
    private boolean switchState;
    private boolean lightState;
    private boolean crossingState;
    private int authority;
    private boolean booleanAuth;
    private double speed;
    private boolean open;

    private boolean direction;
    private boolean dir_assigned;

    private WaysideBlockSubject subject;

    public WaysideBlock(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing) {
        this.blockID = blockID;
        this.hasSwitch = hasSwitch;
        this.hasLight = hasLight;
        this.hasCrossing = hasCrossing;
        this.open = true;
    }

    public WaysideBlock(int blockID, boolean hasSwitch, boolean hasLight, boolean hasCrossing, int switchBlockMain, int switchBlockAlt) {
        this(blockID, hasSwitch, hasLight, hasCrossing);
        this.switchBlockMain = switchBlockMain;
        this.switchBlockAlt = switchBlockAlt;
    }

    //TODO: Found the needle in the haystack vv
    //BasicBlockInfo is deprecated, it will be replaced by what is currently named parsedBlock
    public WaysideBlock(BasicBlockInfo blockInfo) {
        this(blockInfo.blockNumber(), blockInfo.isSwitchConvergingBlock(), blockInfo.hasSwitchLight(), blockInfo.hasCrossing(), blockInfo.divergingBlockID_Main(), blockInfo.divergingBlockID_Alt());
    }

    public WaysideBlock(BasicBlock block) {
        this.blockID = block.blockNumber();
        this.hasSwitch = block.blockType() == BasicBlock.BlockType.SWITCH;
        this.hasLight = block.blockType() == BasicBlock.BlockType.STATION;
        this.hasCrossing = block.blockType() == BasicBlock.BlockType.CROSSING;
        this.open = true;

        if(this.hasSwitch && block.nodeConnection().isPresent()) {
            this.switchBlockMain = block.nodeConnection().get().defChildID();
            this.switchBlockAlt = block.nodeConnection().get().altChildID().orElse(-1);
        }
    }

    public void setSubject(WaysideBlockSubject subject) {
        this.subject = subject;
    }

    void setValue(String propertyName, Object newValue) {
        switch (propertyName) {
            case occupied_p -> setOccupied((boolean) newValue);
            case switchState_p -> setSwitchState((boolean) newValue);
            case lightState_p -> setLightState((boolean) newValue);
            case crossingState_p -> setCrossingState((boolean) newValue);
            case authority_p -> setBooleanAuth((boolean) newValue);
            case speed_p -> setSpeed((double) newValue);
            case open_p -> setBlockmaintenanceStateState((boolean) newValue);
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
    public boolean isOpen() { return open; }
    public void setOccupied(boolean occupied) {
        System.out.println("Occupied: " + occupied);
        this.occupied = occupied;

        if(subject != null)
            subject.notifyChange(occupied_p, occupied);
    }

    public void setBlockmaintenanceStateState(boolean open) {
        System.out.println("Block Access State: " + open);
        this.open = open;

        if(subject != null)
            subject.notifyChange(open_p, open);
    }
    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        System.out.println("Switch State: " + switchState);
        this.switchState = switchState;

        if(subject != null)
            subject.notifyChange(switchState_p, switchState);
    }

    public boolean getLightState() {
        return lightState;
    }

    public void setLightState(boolean lightState) {
        this.lightState = lightState;

        if(subject != null)
            subject.notifyChange(lightState_p, lightState);
    }

    public boolean getCrossingState() {
        return crossingState;
    }

    public void setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;

        if(subject != null)
            subject.notifyChange(crossingState_p, crossingState);
    }

    public boolean getBooleanAuth() {
        return booleanAuth;
    }

    public void setBooleanAuth(boolean booleanAuth) {
        this.booleanAuth = booleanAuth;

        if(subject != null)
            subject.notifyChange(authority_p, booleanAuth);
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;

        if(subject != null)
            subject.notifyChange(booleanAuth_p, booleanAuth);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;

        if(subject != null)
            subject.notifyChange(speed_p, speed);
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public boolean isDir_assigned() {
        return dir_assigned;
    }

    public void setDir_assigned(boolean dir_assigned) {
        this.dir_assigned = dir_assigned;
    }

    @Override
    public void notifyChange(String property, Object newValue) {
        switch (property) {
            case occupied_p -> setOccupied((boolean) newValue);
            case switchState_p -> setSwitchState((boolean) newValue);
            case lightState_p -> setLightState((boolean) newValue);
            case crossingState_p -> setCrossingState((boolean) newValue);
            case authority_p -> setBooleanAuth((boolean) newValue);
            case speed_p -> setSpeed((double) newValue);
            case open_p -> setBlockmaintenanceStateState((boolean) newValue);
            default -> System.err.println("Property " + property + " not found in WaysideBlock");
        }
    }
}

