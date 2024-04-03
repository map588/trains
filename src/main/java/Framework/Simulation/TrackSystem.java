package Framework.Simulation;

import Utilities.BasicLineMap;
import Utilities.ParsedBasicBlocks;
import Utilities.Enums.Lines;
import trackModel.TrackLine;

import java.util.concurrent.*;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */
    private final ConcurrentMap<Lines, TrackLine> TrackLines = new ConcurrentHashMap<>();
    ExecutorService trackLineExecutor;
    ParsedBasicBlocks parsedBasicBlocks = ParsedBasicBlocks.getInstance();

    public TrackSystem() {
        BasicLineMap basicLines = parsedBasicBlocks.getAllBasicLines();
        trackLineExecutor = Executors.newFixedThreadPool(basicLines.size());
        for (Lines line : Lines.values()) {
            TrackLines.put(line, new TrackLine(line, basicLines.get(line)));
        }
    }

    public void trainDispatch(Lines line, int trainID) {
        TrackLines.get(line).trainDispatch(trainID);
    }

    public void update() {
        trackLineExecutor.submit(() -> {
            for (TrackLine line : TrackLines.values()) {
                line.update();
            }
        });
    }

    protected TrackLine getTrackLine(Lines line) {
        return TrackLines.get(line);
    }
}
