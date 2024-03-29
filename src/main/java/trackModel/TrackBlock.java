package trackModel;

import Utilities.BasicBlock;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;

import java.util.Optional;

import static Utilities.Enums.Direction.NORTH;


class TrackBlock {


    //Block Information
    final int blockID;
    final boolean isUnderground;
    final boolean isSwitch;
    final BlockType blockType;
    final Lines line;

    final Integer northID;
    final Integer southID;

    //Physical properties
    final double grade;
    final double elevation;
    final double cumulativeElevation;
    final double speedLimit;
    final double length;

    //Specific Block Information
    public SwitchState switchInfo;
    public CrossingState crossingInfo;
    public StationInfo stationInfo;
    public FailureInfo failureInfo;

    public boolean maintenanceMode;


    TrackBlock(BasicBlock blockInfo) {
        this.blockID = blockInfo.blockNumber();
        this.isUnderground = blockInfo.isUnderground();
        this.isSwitch = blockInfo.isSwitch();
        this.blockType = blockInfo.blockType();
        this.grade = blockInfo.blockGrade();
        this.elevation = blockInfo.elevation();
        this.cumulativeElevation = blockInfo.cumulativeElevation();
        this.speedLimit = blockInfo.speedLimit();
        this.length = blockInfo.blockLength();
        this.line = Lines.valueOf(blockInfo.trackLine());

        switch (blockType){

            case STATION :
                if(blockInfo.stationName().equals(Optional.empty()) || blockInfo.doorDirection().equals(Optional.empty()))
                    throw new IllegalArgumentException("Station block must have a station name and door direction");

                this.stationInfo = new StationInfo(blockInfo.stationName().get(), blockInfo.doorDirection().get());
                this.crossingInfo = new CrossingState();
                break;

            case CROSSING :
                this.crossingInfo = new CrossingState(false);
                this.stationInfo = new StationInfo();
                break;

            default :
                this.stationInfo = new StationInfo();
                this.crossingInfo = new CrossingState();
                break;
        }
        if(isSwitch){
            this.switchInfo = new SwitchState(blockInfo.nextBlock());
            this.northID = -1;
            this.southID = -1;
        }else{
            this.switchInfo = new SwitchState();
            this.northID = blockInfo.nextBlock().north().blockNumber();
            this.southID = blockInfo.nextBlock().south().blockNumber();
        }

        FailureInfo failureInfo = new FailureInfo();
    }


     Integer getNextBlock(Direction direction) {
        if(!isSwitch) {
            if (direction == NORTH)
                return northID;
            else
                return southID;
        }else{
            if(switchInfo.switchState){
                if (direction == NORTH)
                    return switchInfo.northDefault;
                else
                    return switchInfo.southDefault;
            }else{
                if (direction == NORTH)
                    return switchInfo.northAlt;
                else
                    return switchInfo.southAlt;
            }
        }
    }

    public void setSwitchState(Boolean state){
        if(isSwitch)
            switchInfo.switchState = state;
        else
            throw new IllegalArgumentException("Block is not a switch");
    }

    public void setSwitchStateAuto(Boolean state){
        if(isSwitch)
            switchInfo.switchStateAuto = state;
        else
            throw new IllegalArgumentException("Block is not a switch");
    }

    public void setCrossingState(Boolean state){
        if(blockType == BlockType.CROSSING)
            crossingInfo.crossingState = state;
        else
            throw new IllegalArgumentException("Block is not a crossing");
    }

    public void setPowerFailure(Boolean state) {
        failureInfo.powerFailure = state;
    }

    public void setTrackCircuitFailure(Boolean state) {
        failureInfo.trackCircuitFailure = state;
    }

    public void setBrokenRail(Boolean state) {
        failureInfo.brokenRail = state;
    }

    public void setUnderMaintenance(Boolean state) {
        maintenanceMode = state;
    }



     public static class SwitchState{

        private final BasicBlock.Connection northDef;
        private final BasicBlock.Connection southDef;
        private final BasicBlock.Connection northAlt;
        private final BasicBlock.Connection southAlt;

        private Boolean switchState = false;
        private Boolean switchStateAuto = false;

        //Null constructor
        SwitchState(){
            this.northDef = new BasicBlock.Connection(-1, false);
            this.southDef = new BasicBlock.Connection(-1, false);
            this.northAlt = new BasicBlock.Connection(-1, false);
            this.southAlt = new BasicBlock.Connection(-1, false);
        }

        SwitchState(BasicBlock.NextBlock nextBlock){
            this.northDef = nextBlock.northDefault();
            this.southDef = nextBlock.southDefault();
            this.northAlt = nextBlock.northAlternate();
            this.southAlt = nextBlock.southAlternate();
        }

    }

     public static class StationInfo{
        final String stationName;
        final String doorDirection;

        //Null constructor
        StationInfo(){
            this.stationName = "";
            this.doorDirection = "";
        }

        StationInfo(String stationName, String doorDirection){
            this.stationName = stationName;
            this.doorDirection = doorDirection;
        }


    }

     public static class CrossingState{
        Boolean crossingState;

        //Null constructor
        CrossingState(){
            this.crossingState = false;
        }

        CrossingState(Boolean crossingState){
            this.crossingState = crossingState;
        }


    }

    public static class FailureInfo{
        boolean hasFailure;
        boolean brokenRail;
        boolean trackCircuitFailure;
        boolean powerFailure;

        FailureInfo(){
            this.hasFailure = false;
            this.brokenRail = false;
            this.trackCircuitFailure = false;
            this.powerFailure = false;
        }
    }
}
