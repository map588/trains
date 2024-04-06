package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BlockSkipListMap extends ConcurrentHashMap<Lines, BasicSkipList> {
    public BlockSkipListMap() {
        super();
    }

    public BlockSkipListMap(ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map) {
        map.forEach((k, v) -> this.put(k, new BasicSkipList(v)));
    }

    public ConcurrentHashMap<Lines, BasicSkipList> toConcurrentHashMap() {
        return new ConcurrentHashMap<>(this);
    }
}