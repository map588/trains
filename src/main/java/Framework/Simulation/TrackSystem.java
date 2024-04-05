package Framework.Simulation;

import Utilities.BasicLineMap;
import Utilities.GlobalBasicBlockParser;
import Utilities.Enums.Lines;
import trackModel.TrackBlock;
import trackModel.TrackBlogLog;
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
        GlobalBasicBlockParser parsedBasicBlocks = GlobalBasicBlockParser.getInstance();
        BasicLineMap basicLines = parsedBasicBlocks.getAllBasicLines();
        trackLineExecutor = Executors.newFixedThreadPool(basicLines.size());
        for (Lines line : Lines.values()) {
            TrackLine track = new TrackLine(line, basicLines.get(line));
            for(int i = 0; i < track.getTrack().size(); i++) {
                TrackBlock block = track.getTrack().get(i);
                new TrackBlogLog(block);
            }
            TrackLineMap.addTrackLine(line, new TrackLine(line, basicLines.get(line)));
        }
    }

    public void dispatchTrain(Lines line, int trainID) {
        TrackLineMap.getTrackLine(line).trainDispatch(trainID);
    }

    public void update() {
        trackLineExecutor.submit(() -> {
            for (TrackLine line : TrackLineMap.getValues()) {
                    line.update();
            }
        });
    }
}
