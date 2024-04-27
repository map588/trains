package Utilities.HelperObjects;

import Utilities.Records.BasicBlock;
import java.util.concurrent.ConcurrentSkipListMap;

public final class BasicTrackLine extends ConcurrentSkipListMap<Integer, BasicBlock> {

  public BasicTrackLine() {
    super();
  }

  public BasicTrackLine(ConcurrentSkipListMap<Integer, BasicBlock> map) {
    super(map);
  }

  public ConcurrentSkipListMap<Integer, BasicBlock> toConcurrentSkipListMap() {
    return new ConcurrentSkipListMap<>(this);
  }
}
