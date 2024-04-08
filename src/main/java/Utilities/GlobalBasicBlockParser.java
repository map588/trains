package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class GlobalBasicBlockParser {

    private static final GlobalBasicBlockParser INSTANCE = new GlobalBasicBlockParser();

    private final ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> basicBlocks = new ConcurrentHashMap<>();

    private GlobalBasicBlockParser() {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map = BlockParser.parseCSV();
        map.forEach(this::addLines);
    }

    private void addLines(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> blocks) {
        basicBlocks.put(line, blocks);
    }

    public int lineCount() {
        return basicBlocks.size();
    }

    public static GlobalBasicBlockParser getInstance() {
        return INSTANCE;
    }

    public BasicTrackLine getBasicLine(Lines line) {
        return new BasicTrackLine(basicBlocks.get(line));
    }

    public BasicTrackMap getAllBasicLines() {
        return new BasicTrackMap(basicBlocks);
    }

    public void updateFile(String filePath) {
        basicBlocks.clear();
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map = BlockParser.parseCSV(filePath);
        map.forEach(this::addLines);
    }
}
