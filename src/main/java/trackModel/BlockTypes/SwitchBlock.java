package trackModel.BlockTypes;

import Utilities.Enums.Direction;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Records.BasicBlock.NextBlock;

public class SwitchBlock implements BlockFeature {

     final Connection northDef;
     final Connection southDef;
     final Connection northAlt;
     final Connection southAlt;

     boolean switchState;
     boolean switchStateAuto;

    public SwitchBlock(NextBlock nextBlock) {
        this.northDef = nextBlock.northDefault();
        this.southDef = nextBlock.southDefault();
        this.northAlt = nextBlock.northAlternate().blockNumber() != -1 ? nextBlock.northAlternate() : null;
        this.southAlt = nextBlock.southAlternate().blockNumber() != -1 ? nextBlock.southAlternate() : null;
    }

    @Override
    public Connection getNorthDef() {
        return northDef;
    }
    @Override
    public Connection getSouthDef() {
        return southDef;
    }
    @Override
    public Connection getNorthAlt() {
        return northAlt;
    }
    @Override
    public Connection getSouthAlt() {
        return southAlt;
    }
    @Override
    public boolean getSwitchState() {
        return switchState;
    }
    @Override
    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }
    @Override
    public boolean getAutoState() {return switchStateAuto;}
    @Override
    public void setSwitchStateAuto(boolean switchStateAuto) {
        this.switchStateAuto = switchStateAuto;
    }

    @Override
    public Connection getNextBlock(Direction direction) {
        if(switchState) {
            return (direction == Direction.NORTH) ? northAlt : southAlt;
        } else {
            return (direction == Direction.NORTH) ? northDef : southDef;
        }
    }

    @Override
    public boolean isSwitch() {
        return true;
    }

    @Override
    public boolean isCrossing() {
        return false;
    }

    @Override
    public boolean isStation() {
        return false;
    }

    @Override
    public boolean getCrossingState() {
        return false;
    }

    @Override
    public void setCrossingState(boolean crossingState) {
    }

    @Override
    public String getStationName() {
        return null;
    }

    @Override
    public String getDoorDirection() {
        return null;
    }

    @Override
    public int getPassengersWaiting() {
        return 0;
    }

    @Override
    public void setPassengersWaiting(int passengersWaiting) {
    }

    @Override
    public int getPassengersEmbarked() {
        return 0;
    }

    @Override
    public void setPassengersEmbarked(int passengersEmbarked) {
    }

    @Override
    public int getPassengersDisembarked() {
        return 0;
    }

    @Override
    public void setPassengersDisembarked(int passengersDisembarked) {
    }
}
