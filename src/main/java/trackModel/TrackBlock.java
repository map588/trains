package trackModel;

import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import trackModel.BlockTypes.*;

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

     BlockFeature feature;

    /**
     * Constructs a new TrackBlock object based on the provided BasicBlock information.
     *
     * @param blockInfo the BasicBlock object containing the block information
     * @throws IllegalArgumentException if the provided block information is invalid
     */
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
        this.line = Lines.valueOf(blockInfo.trackLine().toUpperCase());


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

    boolean hasFailure() {
        return brokenRail || trackCircuitFailure || powerFailure;
    }

    boolean isBrokenRail() {
        return brokenRail;
    }

    void setBrokenRail(boolean brokenRail) {
        this.brokenRail = brokenRail;
    }

    boolean isTrackCircuitFailure() {
        return trackCircuitFailure;
    }

    void setTrackCircuitFailure(boolean trackCircuitFailure) {
        this.trackCircuitFailure = trackCircuitFailure;
    }

    boolean isPowerFailure() {
        return powerFailure;
    }

    void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
    }

    /**
     * Validates the provided BasicBlock object to ensure it contains valid data.
     *
     * @param blockInfo the BasicBlock object to validate
     * @throws IllegalArgumentException if the block information is invalid
     */
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

    void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    void setAuthority(int authority) {
        this.authority = authority;
    }

    void setCommandSpeed(double commandSpeed) {
        this.commandSpeed = commandSpeed;
    }

    void setUnderMaintenance (boolean state){
        maintenanceMode = state;
    }

    String getStationName() {
        if (feature.isStation()) {
            return feature.getStationName();
        }else{
            throw new UnsupportedOperationException("getStationName called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    String getDoorDirection() {
        if (feature.isStation()) {
            return feature.getDoorDirection();
        }else{
            throw new UnsupportedOperationException("getDoorDirection called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public int getPassengersWaiting() {
        if (feature.isStation()) {
            return feature.getPassengersWaiting();
        }else{
            throw new UnsupportedOperationException("getPassengersWaiting called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public void setPassengersWaiting(int passengersWaiting) {
        if (feature.isStation()) {
            feature.setPassengersWaiting(passengersWaiting);
        }else{
            throw new UnsupportedOperationException("setPassengersWaiting called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public int getPassengersEmbarked() {
        if (feature.isStation()) {
            return feature.getPassengersEmbarked();
        }else{
            throw new UnsupportedOperationException("getPassengersEmbarked called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public void setPassengersEmbarked(int passengersEmbarked) {
        if (feature.isStation()) {
            feature.setPassengersEmbarked(passengersEmbarked);
        }else{
            throw new UnsupportedOperationException("setPassengersEmbarked called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public int getPassengersDisembarked() {
        if (feature.isStation()) {
            return feature.getPassengersDisembarked();
        }else{
            throw new UnsupportedOperationException("getPassengersDisembarked called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    public void setPassengersDisembarked(int passengersDisembarked) {
        if (feature.isStation()) {
            feature.setPassengersDisembarked(passengersDisembarked);
        }else{
            throw new UnsupportedOperationException("setPassengersDisembarked called on Block: " + this.blockID + ", which is not a station block");
        }
    }

    void setSwitchState(boolean state) {
        if (feature.isSwitch()) {
            feature.setSwitchState(state);
        }else{
            throw new UnsupportedOperationException("setSwitchState called on Block: " + this.blockID + ", which is not a switch block");
        }
    }

    public boolean getSwitchState() {
        if (feature.isSwitch()) {
            return feature.getSwitchState();
        }else{
            throw new UnsupportedOperationException("getSwitchState called on Block: " + this.blockID + ", which is not a switch block");
        }
    }

    boolean getSwitchStateAuto() {
        if (feature.isSwitch()) {
            return feature.getAutoState();
        }else{
            throw new UnsupportedOperationException("getSwitchStateAuto called on Block: " + this.blockID + ", which is not a switch block");
        }
    }

    void setSwitchStateAuto (boolean state){
        if (feature.isSwitch()) {
            feature.setSwitchStateAuto(state);
        }else
            throw new UnsupportedOperationException("setSwitchStateAuto called on Block: " + this.blockID + ", which is not a switch block");
    }


    void setCrossingState ( boolean state){
        if (feature.isCrossing()) {
            feature.setCrossingState(state);
        }else
            throw new UnsupportedOperationException("setCrossingState called on Block: " + this.blockID + ", which is not a crossing block");
    }

    public boolean getCrossingState() {
        if (feature.isCrossing()) {
            return feature.getCrossingState();
        }else{
            throw new UnsupportedOperationException("getCrossingState called on Block: " + this.blockID + ", which is not a crossing block");
        }
    }

}