package Utilities;

import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentSkipListMap;

public class BasicSkipList extends ConcurrentSkipListMap<Integer, BasicBlock>{

    public BasicSkipList() {
        super();
    }

    public BasicSkipList(ConcurrentSkipListMap<Integer, BasicBlock> map) {
        super(map);
    }

    public ConcurrentSkipListMap<Integer, BasicBlock> toConcurrentSkipListMap() {
        return new ConcurrentSkipListMap<>(this);
    }

}
