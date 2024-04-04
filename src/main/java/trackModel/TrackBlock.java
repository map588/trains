package trackModel;

import Common.TrainModel;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import trackModel.BlockTypes.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TrackBlock {
    // Block Information
     final int blockID;
     final boolean isUnderground;
     final boolean isSwitch;
     final BlockType blockType;
     final Lines line;

     final Connection northConnect;
     final Connection southConnect;

    // Physical properties
     final double grade;
     final double elevation;
     final double cumulativeElevation;
     final double speedLimit;
     final double length;

     boolean maintenanceMode;
     boolean lightState;
     int     authority;
     double  commandSpeed;
     boolean hasFailure;
     boolean brokenRail;
     boolean trackCircuitFailure;
     boolean powerFailure;
     boolean occupied;
     TrainModel occupiedBy;
    private static final Logger logger = Logger.getLogger(TrackBlock.class.getName());

     BlockFeature feature;

    /**
     * Constructs a new TrackBlock object based on the provided BasicBlock information.
     *
     * @throws IllegalArgumentException if the provided block information is invalid
     */
    public TrackBlock(){
        this.blockID = 0;
        this.isUnderground = false;
        this.isSwitch = false;
        this.blockType = BlockType.REGULAR;
        this.grade = 0;
        this.elevation = 0;
        this.cumulativeElevation = 0;
        this.speedLimit = 0;
        this.length = 0;
        this.line = Lines.GREEN;
        this.northConnect = null;
        this.southConnect = null;
        this.feature = new StandardBlock();
        this.hasFailure = false;
        this.brokenRail = false;
        this.trackCircuitFailure = false;
        this.powerFailure = false;
    }

    TrackBlock(BasicBlock blockInfo) {
        validateBlockInfo(blockInfo);

        this.blockID = blockInfo.blockNumber();
        this.isUnderground = blockInfo.isUnderground();
        this.isSwitch = blockInfo.isSwitch();
        this.blockType = blockInfo.blockType();
        this.grade = blockInfo.blockGrade();
        this.elevation = blockInfo.elevation();
        this.cumulativeElevation = blockInfo.cumulativeElevation();
        this.speedLimit = blockInfo.speedLimit();
        this.length = blockInfo.blockLength();
        this.line =blockInfo.trackLine();


        if (isSwitch && blockType == BlockType.STATION) {
            this.northConnect = null;
            this.southConnect = null;
            this.feature = new SwitchStationBlock(blockInfo.stationName().get(), blockInfo.doorDirection().get(), blockInfo.nextBlock());
        } else if(isSwitch) {
            this.northConnect = null;
            this.southConnect = null;
            this.feature = new SwitchBlock(blockInfo.nextBlock());
        }else if(blockType == BlockType.STATION) {
            this.northConnect = blockInfo.nextBlock().north();
            this.southConnect = blockInfo.nextBlock().south();
            this.feature = new StationBlock(blockInfo.stationName().get(), blockInfo.doorDirection().get());
        }else if(blockType == BlockType.CROSSING){
            this.northConnect = blockInfo.nextBlock().north();
            this.southConnect = blockInfo.nextBlock().south();
            this.feature = new CrossingBlock(false);
        }else{
            this.northConnect = blockInfo.nextBlock().north();
            this.southConnect = blockInfo.nextBlock().south();
            this.feature = new StandardBlock();
        }

        this.hasFailure = false;
        this.brokenRail = false;
        this.trackCircuitFailure = false;
        this.powerFailure = false;
    }

    public void setFailure(boolean brokenRail, boolean trackCircuitFailure, boolean powerFailure) {
        this.brokenRail = brokenRail;
        this.trackCircuitFailure = trackCircuitFailure;
        this.powerFailure = powerFailure;
        this.hasFailure = brokenRail || trackCircuitFailure || powerFailure;
    }

    public boolean hasFailure() {
        return brokenRail || trackCircuitFailure || powerFailure;
    }

     Connection getNextBlock(Direction direction) {
        if (isSwitch) {
            return feature.getNextBlock(direction);
        } else {
            return (direction == Direction.NORTH) ? northConnect : southConnect;
        }
    }

    public double getLength() {
        return length;
    }

    public Integer getBlockID() {
        return blockID;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public void setCommandSpeed(double commandSpeed) {
        this.commandSpeed = commandSpeed;
    }

    public void setUnderMaintenance (boolean state){
        maintenanceMode = state;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public boolean isStation() {
        return blockType == BlockType.STATION;
    }

    public boolean isCrossing() {
        return blockType == BlockType.CROSSING;
    }

    public boolean isYard() {
        return blockType == BlockType.YARD;
    }

    public boolean isUnderground() {
        return isUnderground;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public boolean getLightState() {
        return lightState;
    }

    public void setLightState(boolean state) {
        lightState = state;
    }

    public int getAuthority() {
        return authority;
    }

    public double getCommandSpeed() {
        return commandSpeed;
    }

    public double getGrade() {
        return grade;
    }

    public double getElevation() {
        return elevation;
    }

    public double getCumulativeElevation() {
        return cumulativeElevation;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public boolean isOccupied() {return occupied;}

    public void setOccupied(boolean occupied) {this.occupied = occupied;}

    public Lines getLine() {
        return line;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public TrainModel getOccupiedBy() {
        return occupiedBy;
    }

    String getStationName() {
        if (feature.isStation()) {
            return feature.getStationName();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getStationName", "station"));
            return null;
        }
    }

    String getDoorDirection() {
        if (feature.isStation()) {
            return feature.getDoorDirection();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getDoorDirection", "station"));
            return null;
        }
    }

    public int getPassengersWaiting() {
        if (feature.isStation()) {
            return feature.getPassengersWaiting();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getPassengersWaiting", "station"));
            return 0;
        }
    }

    public void setPassengersWaiting(int passengersWaiting) {
        if (feature.isStation()) {
            feature.setPassengersWaiting(passengersWaiting);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setPassengersWaiting", "station"));
        }
    }

    public int getPassengersEmbarked() {
        if (feature.isStation()) {
            return feature.getPassengersEmbarked();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getPassengersEmbarked", "station"));
            return 0;
        }
    }

    public void setPassengersEmbarked(int passengersEmbarked) {
        if (feature.isStation()) {
            feature.setPassengersEmbarked(passengersEmbarked);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setPassengersEmbarked", "station"));
        }
    }

    public int getPassengersDisembarked() {
        if (feature.isStation()) {
            return feature.getPassengersDisembarked();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getPassengersDisembarked", "station"));
            return 0;
        }
    }

    public void setPassengersDisembarked(int passengersDisembarked) {
        if (feature.isStation()) {
            feature.setPassengersDisembarked(passengersDisembarked);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setPassengersDisembarked", "station"));
        }
    }

    public void setSwitchState(boolean state) {
        if (feature.isSwitch()) {
            feature.setSwitchState(state);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setSwitchState", "switch"));
        }
    }

    public boolean getSwitchState() {
        if (feature.isSwitch()) {
            return feature.getSwitchState();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getSwitchState", "switch"));
            return false;
        }
    }

    boolean getSwitchStateAuto() {
        if (feature.isSwitch()) {
            return feature.getAutoState();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getSwitchStateAuto", "switch"));
            return false;
        }
    }

    public void setSwitchStateAuto(boolean state) {
        if (feature.isSwitch()) {
            feature.setSwitchStateAuto(state);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setSwitchStateAuto", "switch"));
        }
    }

    public void setCrossingState(boolean state) {
        if (feature.isCrossing()) {
            feature.setCrossingState(state);
        } else {
            logger.log(Level.SEVERE, generateLogMessage("setCrossingState", "crossing"));
        }
    }

    public boolean getCrossingState() {
        if (feature.isCrossing()) {
            return feature.getCrossingState();
        } else {
            logger.log(Level.SEVERE, generateLogMessage("getCrossingState", "crossing"));
            return false;
        }
    }



    void validateBlockInfo(BasicBlock blockInfo) {
        if (blockInfo.blockType() == BlockType.STATION) {
            if (blockInfo.stationName().isEmpty() || blockInfo.doorDirection().isEmpty()) {
                throw new IllegalArgumentException("Block: " + blockInfo.blockNumber() + "Station block must have a station name and door direction");
            }
        }else if(blockInfo.isSwitch()){
            if(blockInfo.nextBlock().northDefault() == null || blockInfo.nextBlock().southDefault() == null) {
                throw new IllegalArgumentException("Block: " + blockInfo.blockNumber() + "Switch block must have a default connection for both directions.");
            }
            if(blockInfo.nextBlock().northAlternate() == null && blockInfo.nextBlock().southAlternate() == null){
                throw new IllegalArgumentException("Block: " + blockInfo.blockNumber() + "Switch block must have an alternate connection for at least one direction.");
            }
        }
    }

    private String generateLogMessage(String methodName, String blockType) {
        return methodName + " called on Block: " + this.blockID + ", which is not a " + blockType + " block";
    }
}