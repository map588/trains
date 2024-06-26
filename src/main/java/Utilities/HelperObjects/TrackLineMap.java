package Utilities.HelperObjects;

import Utilities.Enums.Lines;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import trackModel.TrackLine;

public class TrackLineMap {
  private static final ConcurrentMap<Lines, TrackLine> TrackLines = new ConcurrentHashMap<>();

  public static void addTrackLine(Lines line, TrackLine trackLine) {
    TrackLines.put(line, trackLine);
  }

  public static TrackLine getTrackLine(Lines line) {
    return TrackLines.get(line);
  }

  public static Collection<TrackLine> getValues() {
    return TrackLines.values();
  }

  public static ConcurrentMap<Lines, TrackLine> getMap() {
    return TrackLines;
  }
}
