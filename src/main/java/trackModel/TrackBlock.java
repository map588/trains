package trackModel;

import Utilities.BasicBlock;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;

import java.util.Optional;


class TrackBlock {


    //Block Information
    private final int blockID;
    private final boolean isUnderground;
    private final boolean isSwitch;
    private final BlockType blockType;
    private final Lines line;

    private final Integer northID;
    private final Integer southID;

    //Physical properties
    private final double grade;
    private final double elevation;
    private final double cumulativeElevation;
    private final double speedLimit;
    private final double length;

    //Specific Block Information
    private SwitchState switchInfo;
    private CrossingState crossingInfo;
    private StationInfo stationInfo;


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
                this.switchInfo = new SwitchState();
                this.stationInfo = new StationInfo();
                this.crossingInfo = new CrossingState();
                break;
        }
        if(isSwitch){
            this.switchInfo = new SwitchState(blockInfo.nextBlock());
            this.northID = -1;
            this.southID = -1;
        }else{
            this.northID = blockInfo.nextBlock().north().blockNumber();
            this.southID = blockInfo.nextBlock().south().blockNumber();
        }
    }


    public Integer getNextBlock(Direction direction) {
        if(!isSwitch) {
            if (direction == Direction.NORTH)
                return northID;
            else
                return southID;
        }else{
            if(switchInfo.switchState){
                if (direction == Direction.NORTH)
                    return switchInfo.northDefault;
                else
                    return switchInfo.southDefault;
            }else{
                if (direction == Direction.NORTH)
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


     static class SwitchState{
        private final Integer northDefault;
        private final Integer southDefault;

        private final Integer northAlt;
        private final Integer southAlt;

        private Boolean switchState = false;
        private Boolean switchStateAuto = false;

        //Null constructor
        SwitchState(){
            this.northDefault = -1;
            this.southDefault = -1;
            this.northAlt = -1;
            this.southAlt = -1;
        }

        SwitchState(BasicBlock.NextBlock nextBlock){
            this.northDefault = nextBlock.northDefault().blockNumber();
            this.southDefault = nextBlock.southDefault().blockNumber();
            this.northAlt = nextBlock.northAlternate().blockNumber();
            this.southAlt = nextBlock.southAlternate().blockNumber();
        }

    }

     static class StationInfo{
        private final String stationName;
        private final String doorDirection;

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

     static class CrossingState{
        private Boolean crossingState;

        //Null constructor
        CrossingState(){
            this.crossingState = false;
        }

        CrossingState(Boolean crossingState){
            this.crossingState = crossingState;
        }


    }
}
