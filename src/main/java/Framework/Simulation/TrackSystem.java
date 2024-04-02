package Framework.Simulation;

import Utilities.Records.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import trackModel.TrackLine;

import java.util.concurrent.*;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */

    ConcurrentHashMap<Lines, TrackLine> TrackLines = new ConcurrentHashMap<>();
    ExecutorService trackLineExecutor;

    public TrackSystem() {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> track = BlockParser.parseCSV();
        trackLineExecutor = Executors.newFixedThreadPool(track.size());
        for (Lines line : Lines.values()) {
            TrackLines.put(line, new TrackLine(line, track.get(line)));
        }
    }

    public void trainDispatch(Lines line, int trainID) {
        TrackLines.get(line).trainDispatch(trainID);
    }

    public void update() {
    }
}
