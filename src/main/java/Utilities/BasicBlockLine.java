package Utilities;

import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentSkipListMap;

public class BasicBlockLine extends ConcurrentSkipListMap<Integer, BasicBlock>{

    public BasicBlockLine() {
        super();
    }

    public BasicBlockLine(ConcurrentSkipListMap<Integer, BasicBlock> map) {
        super(map);
    }

    public ConcurrentSkipListMap<Integer, BasicBlock> toConcurrentSkipListMap() {
        return new ConcurrentSkipListMap<>(this);
    }

}
