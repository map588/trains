package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BasicLineMap extends ConcurrentHashMap<Lines, BasicBlockLine> {
    public BasicLineMap() {
        super();
    }

    public BasicLineMap(ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map) {
        map.forEach((k, v) -> this.put(k, new BasicBlockLine(v)));
    }

    public ConcurrentHashMap<Lines, BasicBlockLine> toConcurrentHashMap() {
        return new ConcurrentHashMap<>(this);
    }
}