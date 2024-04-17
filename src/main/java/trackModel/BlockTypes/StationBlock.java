package trackModel.BlockTypes;


import Utilities.Enums.Direction;
import Utilities.Records.BasicBlock;

public class StationBlock implements BlockFeature{
    private final String stationName;
    private final String doorDirection;

    int passengersWaiting;
    int passengersEmbarked;
    int passengersDisembarked;


    public StationBlock(String stationName, String doorDirection) {
        this.stationName = stationName;
        this.doorDirection = doorDirection;
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

    @Override
    public boolean isSwitch() {
        return false;
    }

    @Override
    public boolean isCrossing() {
        return false;
    }

    @Override
    public boolean isStation() {
        return true;
    }

    @Override
    public boolean getCrossingState() {
        return false;
    }

    @Override
    public void setCrossingState(boolean crossingState) {
        
    }

    @Override
    public BasicBlock.Connection getNorthDef() {
         return null;
    }

    @Override
    public BasicBlock.Connection getSouthDef() {
         return null;
    }

    @Override
    public BasicBlock.Connection getNorthAlt() {
        return null;
    }

    @Override
    public BasicBlock.Connection getSouthAlt() {
        return null;
    }

    @Override
    public boolean getSwitchState() {
        return false;
    }

    @Override
    public void setSwitchState(boolean switchState) {
    }

    @Override
    public boolean getAutoState() {
        return false;
    }

    @Override
    public void setSwitchStateAuto(boolean switchStateAuto) {
    }

    @Override
    public BasicBlock.Connection getNextBlock(Direction direction) {
        return null;
    }

    @Override
    public Direction getPrimarySwitchDir() {
        return null;
    }
}