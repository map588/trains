package Utilities.Records;

import Utilities.Enums.BeaconType;
import java.util.ArrayDeque;

public record Beacon(
    BeaconType type, Integer sourceId, Integer endId, ArrayDeque<Integer> blockIndices) {

  public String toString() {
    return "Beacon Type = "
        + type
        + "\n"
        + "Source Block ID = "
        + sourceId
        + "\n"
        + "Terminal Block ID = "
        + endId
        + "\n"
        + "Block Indices:\n"
        + blockIndices
        + "\n";
  }

  public Integer getIndex() {
    return blockIndices.peekFirst();
  }
}
