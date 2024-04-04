package CTCOffice;

import Framework.Support.Notifier;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import static CTCOffice.Properties.BlockProperties.SWITCH_STATE_STRING_PROPERTY;
import static Utilities.Enums.BlockType.*;
import static Utilities.Enums.Direction.NORTH;

/**
 * This class represents a block of the track in the Centralized Traffic Control (CTC) system.
 * It contains information about the block such as its ID, occupancy status, presence of light, switch and crossing,
 * their states, speed limit, block length, IDs of converging and diverging blocks, and the state of the switch.
 */
class CTCBlock implements Notifier {
    private final int blockID;
    private final Lines line;
    private final boolean hasLight, hasCrossing;
    private boolean isSwitchCon, isSwitchDiv;
    private boolean occupied, underMaintenance, direction, directionAssigned;

    private int convergingBlockID, divergingBlockOneID, divergingBlockTwoID;
    private boolean lightState, switchState, crossingState;
    private final double speedLimit, blockLength;

    CTCBlockSubjectMapGreen mapGreen = CTCBlockSubjectMapGreen.getInstance();
    CTCBlockSubjectMapRed mapRed = CTCBlockSubjectMapRed.getInstance();

    /**
     * Constructor for the CTCBlockInfo class.
     * Initializes the block properties and registers the block with the CTCBlockSubjectFactory.
     */
    CTCBlock(BasicBlock block) {

        this.blockID = block.blockNumber();
        this.line = block.trackLine();
        this.occupied = false;
        this.hasLight = block.blockType() == STATION;
        this.isSwitchCon = block.isSwitch();

        this.hasCrossing = (block.blockType() == CROSSING);
        this.lightState = false;
        this.crossingState = false;
        this.speedLimit = block.speedLimit();
        this.blockLength = block.blockLength();

        this.switchState = false;

        this.underMaintenance = false;

        if(this.isSwitchCon) {
            this.convergingBlockID = block.blockNumber();
            this.divergingBlockOneID = (block.nextBlock().primarySwitchDirection() == NORTH) ?
                    block.nextBlock().northDefault().blockNumber() : block.nextBlock().southDefault().blockNumber();
            this.divergingBlockTwoID = (block.nextBlock().primarySwitchDirection() == NORTH) ?
                    block.nextBlock().northAlternate().blockNumber() : block.nextBlock().southAlternate().blockNumber();
        }
        if(line.equals(Lines.GREEN)) {
            mapGreen.registerSubject(blockID, new CTCBlockSubject(this));
        } else {
            mapRed.registerSubject(blockID, new CTCBlockSubject(this));
        }
    }

    void setSwitchDivInformation(int convergingBlockID, int divergingBlockOneID, int divergingBlockTwoID) {
        this.isSwitchDiv = true;
        this.convergingBlockID = convergingBlockID;
        this.divergingBlockOneID = divergingBlockOneID;
        this.divergingBlockTwoID = divergingBlockTwoID;
        if(this.line.equals(Lines.GREEN)) {
            CTCBlockSubjectMapGreen.getInstance().getSubject(this.blockID).updateStringProperty(SWITCH_STATE_STRING_PROPERTY);
        } else {
            CTCBlockSubjectMapRed.getInstance().getSubject(this.blockID).updateStringProperty(SWITCH_STATE_STRING_PROPERTY);
        }
    }
    int getDivergingBlockOneID() {
        return divergingBlockOneID;
    }
    int getDivergingBlockTwoID() {
        return divergingBlockTwoID;
    }
    int getConvergingBlockID() {
        return convergingBlockID;
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
            if(line.equals(Lines.RED)) {
                mapRed.getSubject(divergingBlockOneID).setProperty("switchState", state);
                mapRed.getSubject(divergingBlockTwoID).setProperty("switchState", state);
            } else {
                mapGreen.getSubject(divergingBlockOneID).setProperty("switchState", state);
                mapGreen.getSubject(divergingBlockTwoID).setProperty("switchState", state);
            }
        }
        System.out.println("Switch State: " + switchState + " \n");
        if(line.equals(Lines.RED)) {
            mapRed.getSubject(convergingBlockID).updateStringProperty("switchStateString");
            mapRed.getSubject(divergingBlockOneID).updateStringProperty("switchStateString");
            mapRed.getSubject(divergingBlockTwoID).updateStringProperty("switchStateString");
            if(divergingBlockOneID == blockID || divergingBlockTwoID == blockID) {
                mapRed.getSubject(convergingBlockID).setProperty("switchState", state);
            }
        } else {
            mapGreen.getSubject(convergingBlockID).updateStringProperty("switchStateString");
            mapGreen.getSubject(divergingBlockOneID).updateStringProperty("switchStateString");
            mapGreen.getSubject(divergingBlockTwoID).updateStringProperty("switchStateString");
            if(divergingBlockOneID == blockID || divergingBlockTwoID == blockID) {
                mapGreen.getSubject(convergingBlockID).setProperty("switchState", state);
            }
        }
    }

    /**
     * Returns a string representation of the switch state.
     */
    String getSwitchStateString() {
        if(mapGreen.getSubject(getBlockID()) != null) {
            if (convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0) {
                return "";
            }
        }
        else{return null;}
        if(isSwitchCon && !switchState) {
            return ( "( " + divergingBlockOneID + " == "  + convergingBlockID + " )  " + divergingBlockTwoID);
        }
        else if(isSwitchCon) {
            return ( divergingBlockOneID +"  ( "   + convergingBlockID + " == " + divergingBlockTwoID + " )");
        }
        else if(isSwitchDiv && !switchState) {
            if(divergingBlockOneID == blockID) {
                return ( divergingBlockOneID + " ==== " + convergingBlockID);
            }else {
                return ( divergingBlockTwoID + "\t\t" + convergingBlockID);
            }
        }else if(isSwitchDiv) {
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
    String getLine() {
        return line.toString();
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
        this.lightState = switchLightState;
    }
    boolean getHasLight     () {
        return hasLight;
    }
    boolean getSwitchLightState() {
        return lightState;
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
    boolean getSwitchCon() {
        return isSwitchCon;
    }
    boolean getSwitchDiv() {
        return isSwitchDiv;
    }

    public void notifyChange(String property, Object newValue) {

    }
}

