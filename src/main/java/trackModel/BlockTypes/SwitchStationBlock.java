package trackModel.BlockTypes;

import Utilities.Records.BasicBlock.NextBlock;


public class SwitchStationBlock extends SwitchBlock implements BlockFeature{

    private final String stationName;
    private final String doorDirection;


    private int passengersWaiting;
    private int passengersEmbarked;
    private int passengersDisembarked;


    public SwitchStationBlock(String stationName, String doorDirection, NextBlock nextBlock) {
        super(nextBlock);
        this.stationName = stationName;
        this.doorDirection = doorDirection;
    }

    @Override
    public boolean isStation() {
        return true;
    }


    public String getStationName() {
        return stationName;
    }
    public String getDoorDirection() {
        return doorDirection;
    }
    public int getPassengersWaiting() {
        return passengersWaiting;
    }
    public void setPassengersWaiting(int passengersWaiting) {
        this.passengersWaiting = passengersWaiting;
    }
    public int getPassengersEmbarked() {
        return passengersEmbarked;
    }
    public void setPassengersEmbarked(int passengersEmbarked) {
        this.passengersEmbarked = passengersEmbarked;
    }
    public int getPassengersDisembarked() {
        return passengersDisembarked;
    }
    public void setPassengersDisembarked(int passengersDisembarked) {
        this.passengersDisembarked = passengersDisembarked;
    }

}
