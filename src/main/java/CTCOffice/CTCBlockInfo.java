package CTCOffice;

import Utilities.BasicBlock;

import static Utilities.Enums.BlockType.*;
import static Utilities.Enums.Direction.NORTH;
import static Utilities.Enums.Direction.SOUTH;

/**
 * This class represents a block of the track in the Centralized Traffic Control (CTC) system.
 * It contains information about the block such as its ID, occupancy status, presence of light, switch and crossing,
 * their states, speed limit, block length, IDs of converging and diverging blocks, and the state of the switch.
 */
class CTCBlockInfo {
    private final double speedLimit, blockLength;
    private final String line;
    private final boolean hasLight, hasCrossing, hasSwitch;
    private final boolean hasSwitchCon, hasSwitchDiv;
    private final int blockID;
    private final int convergingBlockID, divergingBlockOneID, divergingBlockTwoID;


    private boolean occupied, underMaintenance;
    private boolean switchLightState, crossingState, switchState;
    CTCBlockSubjectMap map = CTCBlockSubjectMap.getInstance();

    /**
     * Constructor for the CTCBlockInfo class.
     * Initializes the block properties and registers the block with the CTCBlockSubjectFactory.
     */
    CTCBlockInfo(BasicBlock block) {

        this.blockID = block.blockNumber();
        this.line = block.trackLine();
        this.occupied = false;
        this.hasLight = false;//tbd
        this.hasSwitch = block.isSwitch();
        this.hasSwitchCon = block.nextBlock().primarySwitchDirection() == NORTH;
        this.hasSwitchDiv = block.nextBlock().primarySwitchDirection() == SOUTH;
        this.hasCrossing = (block.blockType() == CROSSING);
        this.switchLightState = false;
        this.crossingState = false;
        this.speedLimit = block.speedLimit();
        this.blockLength = block.blockLength();
        this.convergingBlockID = block.blockNumber();
        this.divergingBlockOneID = block.nextBlock().northDefault().blockNumber();
        this.divergingBlockTwoID = block.nextBlock().northAlternate().blockNumber();
        this.switchState = false;
        this.underMaintenance = false;

       map.registerSubject(blockID, new CTCBlockSubject(this));
    }


    /**
     * Sets the state of the switch and updates the switch state of the converging and diverging blocks.
     */
    void setSwitchState(boolean state) {
        this.switchState = state;
        if(convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0){
            return;
        }
        if(convergingBlockID == blockID) {
            map.getSubject(divergingBlockOneID).setProperty("switchState", state);
            map.getSubject(divergingBlockTwoID).setProperty("switchState", state);
        }
        System.out.println("Switch State: " + switchState + " \n");
        map.getSubject(convergingBlockID).updateStringProperty("switchStateString");
        map.getSubject(divergingBlockOneID).updateStringProperty("switchStateString");
        map.getSubject(divergingBlockTwoID).updateStringProperty("switchStateString");
        if(divergingBlockOneID == blockID || divergingBlockTwoID == blockID) {
            map.getSubject(convergingBlockID).setProperty("switchState", state);
        }
    }

    /**
     * Returns a string representation of the switch state.
     */
    String getSwitchStateString() {
        if(map.getSubject(getBlockID()) != null) {
            if (convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0) {
                return "";
            }
        }
        else{return null;}
        if(hasSwitchCon && !switchState) {
            return ( "( " + divergingBlockOneID + " == "  + convergingBlockID + " )  " + divergingBlockTwoID);
        }
        else if(hasSwitchCon) {
            return ( divergingBlockOneID +"  ( "   + convergingBlockID + " == " + divergingBlockTwoID + " )");
        }
        else if(hasSwitchDiv && !switchState) {
            if(divergingBlockOneID == blockID) {
                return ( divergingBlockOneID + " ==== " + convergingBlockID);
            }else {
                return ( divergingBlockTwoID + "\t\t" + convergingBlockID);
            }
        }else if(hasSwitchDiv) {
            if(divergingBlockTwoID == blockID) {
                return ( divergingBlockTwoID + " ==== " + convergingBlockID);
            }else {
                return ( divergingBlockOneID + "\t\t" + convergingBlockID);
            }
        }else {
            return "";
        }
    }


    // Simple setters and getters
    int     getBlockID      () {
        return blockID;
    }
    String getLine         () {
        return line;
    }
    double     getSpeedLimit   () {
        return speedLimit;
    }
    double     getBlockLength  () {
        return blockLength;
    }

    void    setOccupied     (boolean occupied) {
        this.occupied = occupied;
    }
    boolean getOccupied     () {
        return occupied;
    }
    boolean getUnderMaintenance () {
        return underMaintenance;
    }
    void    setUnderMaintenance (boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    void setSwitchLightState(boolean switchLightState) {
        this.switchLightState = switchLightState;
    }
    boolean getHasLight     () {
        return hasLight;
    }
    boolean getSwitchLightState() {
        return switchLightState;
    }

    void    setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;
    }
    boolean getHasCrossing  () {
        return hasCrossing;
    }
    boolean getCrossingState() {
        return crossingState;
    }

    public boolean getSwitchState           () {
        return switchState;
    }
    boolean        getHasSwitchCon          () {
        return hasSwitchCon;
    }
    boolean        getHasSwitchDiv          () {
        return hasSwitchDiv;
    }

}

