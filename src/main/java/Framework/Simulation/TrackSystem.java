package Framework.Simulation;

import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trackModel.TrackBlock;
import trackModel.TrackLine;
import trackModel.TrackLineMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Utilities.Enums.Direction.NORTH;
import static Utilities.Enums.Direction.SOUTH;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */
    private final ExecutorService trackLineExecutor;

    private static final Logger logger = LoggerFactory.getLogger(TrackSystem.class);

    public TrackSystem() {
        trackLineExecutor = Executors.newFixedThreadPool(GlobalBasicBlockParser.getInstance().lineCount());
        for (Lines line : Lines.values()) {
            TrackLine track = new TrackLine(line);
            for(int i = 0; i < track.getTrack().size(); i++) {
                TrackBlock block = track.getTrack().get(i);
                logBlockInfo(block);
            }
            TrackLineMap.addTrackLine(line, new TrackLine(line));
            logger.info("TrackLine {} has been added to the TrackLineMap", line);
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

    private void logBlockInfo(TrackBlock block) {

        logger.info("Block: {} Type: {} Line: {}", block.getBlockID(), block.getBlockType().toString(), block.getLine().toString());
        logger.info("Next Block North: {}", block.getNextBlock(NORTH).toString());
        logger.info("Next Block South: {}", block.getNextBlock(SOUTH).toString());
        logger.info("Switch: {}", block.isSwitch());
    }
}
