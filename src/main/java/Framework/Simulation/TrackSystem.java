package Framework.Simulation;

import Common.TrainModel;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trackModel.TrackLine;
import trackModel.TrackLineMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackSystem {

    /**
     * RED   -> {TrackLine}
     * GREEN -> {TrackLine}
     * ....
     */
    private final ExecutorService trackLineExecutor;

    private static final Logger logger = LoggerFactory.getLogger(TrackSystem.class);
    private static TrainSystem trainSystem;

    public TrackSystem(TrainSystem trainSystem) {
        trackLineExecutor = Executors.newFixedThreadPool(GlobalBasicBlockParser.getInstance().lineCount());
        TrackSystem.trainSystem = trainSystem;
        for (Lines line : Lines.values()) {
            TrackLineMap.addTrackLine(line, new TrackLine(line));
            logger.info("TrackLine {} has been added to the TrackLineMap", line);
        }
    }

    public void dispatchTrain(Lines line, int trainID) {
        TrainModel newTrain = TrackLineMap.getTrackLine(line).trainDispatch(trainID);
        trainSystem.addTrainProcess(newTrain);
    }

    public void update() {
        trackLineExecutor.submit(() -> {
            for (TrackLine line : TrackLineMap.getValues()) {
                    line.update();
            }
        });
    }

//    private void logBlockInfo(TrackBlock block) {
//
//        logger.info("Block: {} Type: {} Line: {}", block.getBlockID(), block.getBlockType().toString(), block.getLine().toString());
//        logger.info("Next Block North: {}", block.getNextBlock(NORTH).toString());
//        logger.info("Next Block South: {}", block.getNextBlock(SOUTH).toString());
//        logger.info("Switch: {}", block.isSwitch());
//    }
}
