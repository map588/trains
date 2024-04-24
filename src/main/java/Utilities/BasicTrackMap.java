package Utilities;

import Utilities.Enums.Lines;
import Utilities.HelperObjects.BasicTrackLine;
import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class BasicTrackMap extends ConcurrentHashMap<Lines, BasicTrackLine> {
    public BasicTrackMap() {
        super();
    }

    public BasicTrackMap(ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map) {
        map.forEach((k, v) -> this.put(k, new BasicTrackLine(v)));
    }

    public ConcurrentHashMap<Lines, BasicTrackLine> toConcurrentHashMap() {
        return new ConcurrentHashMap<>(this);
    }
}