package waysideController;

import Framework.Support.Notifier;
import Utilities.Enums.Direction;
import Utilities.Records.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Utilities.Enums.BlockType.CROSSING;
import static waysideController.Properties.*;

public class WaysideBlock implements Notifier {

    private final Logger logger = LoggerFactory.getLogger(WaysideBlock.class);

    private final int blockID;
    private final boolean hasSwitch;
    private final boolean hasLight;
    private final boolean hasCrossing;

    private int switchBlockParent;
    private int switchBlockDef;
    private int switchBlockAlt;

    private boolean occupied;
    private boolean switchState;
    private boolean lightState;
    private boolean crossingState;
    private boolean booleanAuth;
    private boolean inMaintenance;

    private boolean direction;
    private boolean dir_assigned;

    private WaysideBlockSubject subject;

    public WaysideBlock(BasicBlock block) {
        this.blockID = block.blockNumber();
        this.hasSwitch = block.isSwitch();
        this.hasLight = block.isLight();
        this.hasCrossing = block.blockType() == CROSSING;
        this.inMaintenance = false;

        if(this.hasSwitch) {
            this.switchBlockParent = this.blockID;

            if(block.nextBlock().primarySwitchDirection() == Direction.NORTH) {
                this.switchBlockDef = block.nextBlock().northDefault().blockNumber();
                this.switchBlockAlt = block.nextBlock().northAlternate().blockNumber();
            }
            else {
                this.switchBlockDef = block.nextBlock().southDefault().blockNumber();
                this.switchBlockAlt = block.nextBlock().southAlternate().blockNumber();
            }
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
            case inMaintenance_p -> setBlockMaintenanceState((boolean) newValue);
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
    public boolean inMaintenance() { return inMaintenance; }
    public void setOccupied(boolean occupied) {
       logger.info("Wayside block: {} -> Changed occupancy to {}", this.blockID, occupied);
        this.occupied = occupied;

        if(subject != null)
            subject.notifyChange(occupied_p, occupied);
    }


    public void setBlockMaintenanceState(boolean inMaintenance) {
       // System.out.println("Block Access State: " + inMaintenance);
        this.inMaintenance = inMaintenance;

        if(subject != null)
            subject.notifyChange(inMaintenance_p, inMaintenance);
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
            case inMaintenance_p -> setBlockMaintenanceState((boolean) newValue);
            default -> System.err.println("Property " + property + " not found in WaysideBlock");
        }
    }
}

