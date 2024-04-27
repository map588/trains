package CTCOffice;

import Utilities.Enums.Lines;

public record TrainIdentity(int trainID, Lines line, double dispatchTime, int carCount) {

  // Static factory method
  public static TrainIdentity of(int trainID, Lines line, double dispatchTime, int carCount) {
    return new TrainIdentity(trainID, line, dispatchTime, carCount);
  }

  @Override
  public String toString() {
    return "Train " + trainID + " on the " + line + " line";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TrainIdentity id = (TrainIdentity) o;
    return trainID == id.trainID;
  }

  @Override
  public int hashCode() {
    return trainID;
  }
}
