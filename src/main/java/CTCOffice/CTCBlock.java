package CTCOffice;

import Framework.Simulation.WaysideSystem;
import Framework.Support.BlockIDs;
import Framework.Support.Notifier;
import Utilities.Records.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CTCOffice.Properties.BlockProperties.SWITCH_STATE_STRING_PROPERTY;
import static Utilities.Enums.BlockType.CROSSING;
import static Utilities.Enums.Direction.NORTH;


/**
 * This class represents a block of the track in the Centralized Traffic Control (CTC) system.
 * It contains information about the block such as its ID, occupancy status, presence of light, switch and crossing,
 * their states, speed limit, block length, IDs of converging and diverging blocks, and the state of the switch.
 */
class CTCBlock implements Notifier {

    private static final Logger logger = LoggerFactory.getLogger(CTCBlock.class.getName());

    private final BlockIDs blockID;
    private final boolean hasLight, hasCrossing;
    private boolean isSwitchCon, isSwitchDiv;
    private boolean occupied, underMaintenance, direction, directionAssigned;

    private BlockIDs convergingBlockID, divergingBlockOneID, divergingBlockTwoID;
    private boolean lightState, switchState, crossingState;
    private final double speedLimit, blockLength;

    CTCBlockSubjectMap subMap = CTCBlockSubjectMap.getInstance();

    /**
     * Constructor for the CTCBlockInfo class.
     * Initializes the block properties and registers the block with the CTCBlockSubjectFactory.
     */
    CTCBlock(BasicBlock block) {
        this.blockID = new BlockIDs(block.blockNumber(), block.trackLine());
        this.occupied = false;
        this.hasLight = block.isLight();
        this.isSwitchCon = block.isSwitch();

        this.hasCrossing = (block.blockType() == CROSSING);
        this.lightState = false;
        this.crossingState = false;
        this.speedLimit = block.speedLimit();
        this.blockLength = block.blockLength();

        this.switchState = false;

        this.underMaintenance = false;

        if(this.isSwitchCon) {
            this.convergingBlockID = blockID;
            this.divergingBlockOneID = BlockIDs.of(((block.nextBlock().primarySwitchDirection() == NORTH) ?
                    block.nextBlock().northDefault().blockNumber() : block.nextBlock().southDefault().blockNumber()), blockID.line());
            this.divergingBlockTwoID = BlockIDs.of(((block.nextBlock().primarySwitchDirection() == NORTH) ?
                    block.nextBlock().northAlternate().blockNumber() : block.nextBlock().southAlternate().blockNumber()), blockID.line());
        }
            subMap.registerSubject(blockID, new CTCBlockSubject(this));
    }

    void setSwitchDivInformation(BlockIDs convergingBlockID, BlockIDs divergingBlockOneID, BlockIDs divergingBlockTwoID) {
        this.isSwitchDiv = true;
        this.convergingBlockID = convergingBlockID;
        this.divergingBlockOneID = divergingBlockOneID;
        this.divergingBlockTwoID = divergingBlockTwoID;
        CTCBlockSubjectMap.getInstance().getSubject(this.blockID).updateStringProperty(SWITCH_STATE_STRING_PROPERTY);
    }
    BlockIDs getDivergingBlockOneID() {
        return divergingBlockOneID;
    }
    BlockIDs getDivergingBlockTwoID() {
        return divergingBlockTwoID;
    }
    BlockIDs getConvergingBlockID() {
        return convergingBlockID;
    }

    /**
     * Sets the state of the switch and updates the switch state of the converging and diverging blocks.
     */
    void setSwitchState(boolean GUI, boolean state) {
        if(!underMaintenance && GUI){
            setUnderMaintenance(true, true);
        }
        if(!GUI){
            this.switchState = state;
            notifyChange("switchState", state);if (!(isSwitchCon || isSwitchDiv)) {
                return;
            }
            if ((convergingBlockID == blockID) && ((state != subMap.getSubject(BlockIDs.of(divergingBlockOneID.blockIdNum(), blockID.line())).getBlockInfo().getSwitchState())
                    ||  (state != subMap.getSubject(BlockIDs.of(divergingBlockTwoID.blockIdNum(), blockID.line())).getBlockInfo().getSwitchState()))){
                subMap.getSubject(BlockIDs.of(divergingBlockOneID.blockIdNum(), blockID.line())).getBlockInfo().setSwitchState(false, state);
                subMap.getSubject(BlockIDs.of(divergingBlockTwoID.blockIdNum(), blockID.line())).getBlockInfo().setSwitchState(false, state);
            }
            subMap.getSubject(convergingBlockID).updateStringProperty("switchStateString");
            subMap.getSubject(divergingBlockOneID).updateStringProperty("switchStateString");
            subMap.getSubject(divergingBlockTwoID).updateStringProperty("switchStateString");
            if ((divergingBlockOneID.blockIdNum() == blockID.blockIdNum() || divergingBlockTwoID.blockIdNum() == blockID.blockIdNum()) &&
                    (subMap.getSubject(BlockIDs.of(convergingBlockID.blockIdNum(), blockID.line())).getBlockInfo().getSwitchState() != state)) {
                subMap.getSubject(convergingBlockID).getBlockInfo().setSwitchState(false, state);
            }
        }
        else{WaysideSystem.getController(blockID.line(), blockID.blockIdNum()).maintenanceSetSwitch(convergingBlockID.blockIdNum(), state);
            logger.info("Wayside called to set switch state to " + state + " for block " + convergingBlockID.blockIdNum() + " on line " + blockID.line());
        }
    }

    /**
     * Returns a string representation of the switch state.
     */
    String getSwitchStateString() {
        if(subMap.getSubject(blockID) != null) {
            if (!(isSwitchCon || isSwitchDiv)) {
                return "";
            }
        }
        else{return null;}

        if(isSwitchCon){
            if(switchState) {
                return ( divergingBlockOneID.blockIdNum() +"  ( "   + convergingBlockID.blockIdNum() + " == " + divergingBlockTwoID.blockIdNum() + " )");
            }else{
                return ( "( " + divergingBlockOneID.blockIdNum() + " == "  + convergingBlockID.blockIdNum() + " )  " + divergingBlockTwoID.blockIdNum());
            }
        }else if(isSwitchDiv) {
            if(switchState) {
                if(divergingBlockTwoID.blockIdNum() == blockID.blockIdNum()) {
                    return ( divergingBlockTwoID.blockIdNum() + " ==== " + convergingBlockID.blockIdNum());
                }else {
                    return ( divergingBlockOneID.blockIdNum() + "\t\t" + convergingBlockID.blockIdNum());
                }
            }else {
                if(divergingBlockOneID.blockIdNum() == blockID.blockIdNum()) {
                    return ( divergingBlockOneID.blockIdNum() + " ==== " + convergingBlockID.blockIdNum());
                }else {
                    return ( divergingBlockTwoID.blockIdNum() + "\t\t" + convergingBlockID.blockIdNum());
                }
            }
        }else {
            return "";
        }
    }


    // Simple setters and getters
    int     getBlockID      () {
        return blockID.blockIdNum();
    }
    String getLine() {
        return blockID.line().toString();
    }
    double     getSpeedLimit   () {
        return speedLimit;
    }
    double     getLength  () {
        return blockLength;
    }

    void    setOccupied     (boolean GUI, boolean occupied) {
        this.occupied = occupied;
        if(!GUI){notifyChange("occupied", occupied);}
    }
    boolean getOccupied     () {
        return occupied;
    }
    boolean getUnderMaintenance () {
        return underMaintenance;
    }
    void    setUnderMaintenance (boolean GUI, boolean underMaintenance) {
        if(!GUI){this.underMaintenance = underMaintenance; notifyChange("underMaintenance", underMaintenance);}
        else{
            if(isSwitchCon || isSwitchDiv){
                logger.info("Wayside called to set maintenance state to " + underMaintenance + " for switch from block " + convergingBlockID.blockIdNum() + " on line " + blockID.line());
                WaysideSystem.getController(convergingBlockID.line(), convergingBlockID.blockIdNum()).CTCChangeBlockMaintenanceState(convergingBlockID.blockIdNum(), underMaintenance);
                WaysideSystem.getController(divergingBlockOneID.line(), divergingBlockOneID.blockIdNum()).CTCChangeBlockMaintenanceState(divergingBlockOneID.blockIdNum(), underMaintenance);
                WaysideSystem.getController(divergingBlockTwoID.line(), divergingBlockTwoID.blockIdNum()).CTCChangeBlockMaintenanceState(divergingBlockTwoID.blockIdNum(), underMaintenance);
            } else {
                logger.info("Wayside called to set maintenance state to " + underMaintenance + " for block " + blockID.blockIdNum() + " on line " + blockID.line());
                WaysideSystem.getController(blockID.line(), blockID.blockIdNum()).CTCChangeBlockMaintenanceState(blockID.blockIdNum(), underMaintenance);
            }
        }
    }

    void setSwitchLightState(boolean GUI, boolean switchLightState) {
        if(!underMaintenance && GUI){
            setUnderMaintenance(true, true);
        }
        if(!GUI){this.lightState = switchLightState; notifyChange("switchLightState", switchLightState);}
        else{
            logger.info("Wayside called to set light state to " + switchLightState + " for block " + blockID.blockIdNum() + " on line " + blockID.line());
            WaysideSystem.getController(blockID.line(), blockID.blockIdNum()).maintenanceSetTrafficLight(blockID.blockIdNum(), switchLightState);
        }
    }
    boolean getHasLight     () {
        return hasLight;
    }
    boolean getSwitchLightState() {
        return lightState;
    }

    void    setCrossingState(boolean GUI, boolean crossingState) {
        if(!underMaintenance && GUI){
            setUnderMaintenance(true, true);
        }
        this.crossingState = crossingState;
        if(!GUI){notifyChange("crossingState", crossingState);}
        else{ WaysideSystem.getController(blockID.line(), blockID.blockIdNum()).maintenanceSetCrossing(blockID.blockIdNum(), crossingState);
        logger.info("Wayside called to set crossing state to " + crossingState + " for block " + blockID.blockIdNum() + " on line " + blockID.line());
        }
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

    public void notifyChange(String property, Object newValue) {
        subMap.getSubject(blockID).setProperty(property, newValue);
    }
}

