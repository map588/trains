package Utilities.HelperObjects;

import java.util.concurrent.ConcurrentSkipListMap;
import trackModel.TrackBlock;

public class TrackBlockLine extends ConcurrentSkipListMap<Integer, TrackBlock> {

  public TrackBlockLine() {
    super();
  }

  public TrackBlockLine(ConcurrentSkipListMap<Integer, TrackBlock> map) {
    super(map);
  }

  public ConcurrentSkipListMap<Integer, TrackBlock> toConcurrentSkipListMap() {
    return new ConcurrentSkipListMap<>(this);
  }
}
