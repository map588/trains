package Framework.Simulation;

import Utilities.BasicLineMap;
import Utilities.ParsedBasicBlocks;
import Utilities.Enums.Lines;
import trackModel.TrackLine;
import trackModel.TrackLineMap;

import java.util.concurrent.*;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */
    private final ExecutorService trackLineExecutor;

    public TrackSystem() {
        ParsedBasicBlocks parsedBasicBlocks = ParsedBasicBlocks.getInstance();
        BasicLineMap basicLines = parsedBasicBlocks.getAllBasicLines();
        trackLineExecutor = Executors.newFixedThreadPool(basicLines.size());
        for (Lines line : Lines.values()) {
            TrackLineMap.addTrackLine(line, new TrackLine(line, basicLines.get(line)));
        }
    }

    public void dispatchTrain(Lines line, int trainID) {
        TrackLineMap.getTrackLine(line).trainDispatch(trainID);
    }

    public void update() {
        trackLineExecutor.submit(() -> {
            for (TrackLine line : TrackLineMap.getValues()) {
                try {
                    line.update();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
