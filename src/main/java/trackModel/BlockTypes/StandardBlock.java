package trackModel.BlockTypes;

import Utilities.Enums.Direction;
import Utilities.Records.BasicBlock;

public class StandardBlock implements BlockFeature {

  public StandardBlock() {}

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
    return false;
  }

  @Override
  public boolean getCrossingState() {
    return false;
  }

  @Override
  public void setCrossingState(boolean crossingState) {}

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
  public void setPassengersWaiting(int passengersWaiting) {}

  @Override
  public int getPassengersEmbarked() {
    return 0;
  }

  @Override
  public void setPassengersEmbarked(int passengersEmbarked) {}

  @Override
  public int getPassengersDisembarked() {
    return 0;
  }

  @Override
  public void setPassengersDisembarked(int passengersDisembarked) {}

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
  public void setSwitchState(boolean switchState) {}

  @Override
  public boolean getAutoState() {
    return false;
  }

  @Override
  public void setSwitchStateAuto(boolean switchStateAuto) {}

  @Override
  public BasicBlock.Connection getNextBlock(Direction direction) {
    return null;
  }

  @Override
  public Direction getPrimarySwitchDir() {
    return null;
  }
}
