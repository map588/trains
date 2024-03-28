package Framework.Simulation;

import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import trackModel.TrackLine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */

    ConcurrentHashMap<Lines, TrackLine> TrackLines = new ConcurrentHashMap<>();

    public TrackSystem() {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> track = BlockParser.parseCSV();

        for (Lines line : Lines.values()) {
            TrackLines.put(line, new TrackLine(line, track.get(line)));
        }
    }

    public void trainDispatch(Lines line, int trainID) {
        TrackLines.get(line).trainDispatch(trainID);
    }

}
