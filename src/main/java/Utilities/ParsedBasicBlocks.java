package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ParsedBasicBlocks{

    private static final ParsedBasicBlocks INSTANCE = new ParsedBasicBlocks();

    private final ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> basicBlocks = new ConcurrentHashMap<>();

    private ParsedBasicBlocks() {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map = BlockParser.parseCSV();
        map.forEach(this::addLines);
    }

    public static ParsedBasicBlocks getInstance() {
        return INSTANCE;
    }

    private void addLines(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> blocks) {
        basicBlocks.put(line, blocks);
    }

    public BasicBlockLine getBasicLine(Lines line) {
        return new BasicBlockLine(basicBlocks.get(line));
    }

    public BasicLineMap getAllBasicLines() {
        return new BasicLineMap(basicBlocks);
    }

}
