package waysideController;

import Framework.Support.Notifier;
import Utilities.BasicBlock;
import Utilities.Enums.Direction;

import static Utilities.Enums.BlockType.CROSSING;
import static Utilities.Enums.BlockType.STATION;
import static waysideController.Properties.*;

public class WaysideBlock implements Notifier {
    private final int blockID;
    private int nextBlockIDNorth;
    private int nextBlockIDSouth;
    private boolean nextDirectionNorth;
    private boolean nextDirectionSouth;
    private final boolean hasSwitch;
    private boolean switchBranchDir;
    private final boolean hasLight;
    private final boolean hasCrossing;

    private int switchBlockParent;
    private int switchBlockDef;
    private int switchBlockAlt;

    private boolean occupied;
    private int trainID;
    private boolean switchState;
    private boolean lightState;
    private boolean crossingState;
    private int authority;
    private boolean booleanAuth;
    private double speed;
    private final double speedLimit;
    private boolean open;

    private boolean direction;
    private boolean dir_assigned;

    private WaysideBlockSubject subject;

    public WaysideBlock(BasicBlock block) {
        this.blockID = block.blockNumber();
        this.hasSwitch = block.isSwitch();
        this.hasLight = block.blockType() == STATION;
        this.hasCrossing = block.blockType() == CROSSING;
        this.speedLimit = block.speedLimit();
        this.open = true;
        this.trainID = -1;

        if(this.hasSwitch) {
            System.out.println("North: " + block.nextBlock().north());
            System.out.println("South: " + block.nextBlock().south());
            System.out.println("NorthDef: " + block.nextBlock().northDefault());
            System.out.println("NorthAlt: " + block.nextBlock().northAlternate());
            System.out.println("SouthDef: " + block.nextBlock().southDefault());
            System.out.println("SouthAlt: " + block.nextBlock().southAlternate());

            this.switchBlockParent = this.blockID;

            switchBranchDir = block.nextBlock().primarySwitchDirection() == Direction.NORTH;
            if(switchBranchDir) {
                this.switchBlockDef = block.nextBlock().northDefault().blockNumber();
                this.switchBlockAlt = block.nextBlock().northAlternate().blockNumber();

                nextBlockIDNorth = block.nextBlock().northDefault().blockNumber();
                nextDirectionNorth = !block.nextBlock().northDefault().flipDirection();
                nextBlockIDSouth = block.nextBlock().southDefault().blockNumber();
            }
            else {
                this.switchBlockDef = block.nextBlock().southDefault().blockNumber();
                this.switchBlockAlt = block.nextBlock().southAlternate().blockNumber();

                nextBlockIDSouth = block.nextBlock().southDefault().blockNumber();
                nextDirectionSouth = block.nextBlock().southDefault().flipDirection();
                nextBlockIDNorth = block.nextBlock().northDefault().blockNumber();
            }
        }
        else {
            this.nextBlockIDNorth = block.nextBlock().north().blockNumber();
            this.nextBlockIDSouth = block.nextBlock().south().blockNumber();
            this.nextDirectionNorth = !block.nextBlock().north().flipDirection();
            this.nextDirectionSouth = block.nextBlock().south().flipDirection();
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
            case open_p -> setBlockMaintenanceState((boolean) newValue);
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

    public int getSwitchBlockParent() {
        return switchBlockParent;
    }

    public int getSwitchBlockDef() {
        return switchBlockDef;
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

    public void setBlockMaintenanceState(boolean open) {
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

        // TODO: fix switches to also change direction swapping based on switch state
        if(switchBranchDir) {
            nextBlockIDNorth = switchState ? switchBlockAlt : switchBlockDef;
        }
        else {
            nextBlockIDSouth = switchState ? switchBlockAlt : switchBlockDef;
        }

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

    public double getSpeedLimit() {
        return speedLimit;
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
            case open_p -> setBlockMaintenanceState((boolean) newValue);
            default -> System.err.println("Property " + property + " not found in WaysideBlock");
        }
    }

    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public void removeTrainID() {
        this.trainID = -1;
    }

    public boolean hasTrain() {
        return trainID != -1;
    }

    public int nextBlock() {
        return direction ? nextBlockIDNorth : nextBlockIDSouth;
    }

    public boolean nextDirection() {
        return direction ? nextDirectionNorth : nextDirectionSouth;
    }
}

