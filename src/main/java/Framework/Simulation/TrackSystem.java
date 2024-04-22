package Framework.Simulation;

import Common.TrainModel;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trackModel.TrackLine;
import trackModel.TrackLineMap;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackSystem {

    private static final Logger logger = LoggerFactory.getLogger(TrackSystem.class);
    private static final GlobalBasicBlockParser blockParser = GlobalBasicBlockParser.getInstance();
    private static final HashSet<Lines> lines = blockParser.getLines();

    private final ExecutorService trackLineExecutor;

    private static TrainSystem trainSystem;

    public TrackSystem(TrainSystem trainSystem) {
        trackLineExecutor = Executors.newFixedThreadPool(blockParser.lineCount());
        TrackSystem.trainSystem = trainSystem;

        for (Lines line : lines) {
            if(line.equals(Lines.NULL)){
                continue;
            }
            TrackLineMap.addTrackLine(line, new TrackLine(line));
            logger.info("TrackLine {} has been added to the TrackLineMap", line);
        }
    }
    //For Now
    private static final TrackLine greenLine = TrackLineMap.getTrackLine(Lines.GREEN);


    public void dispatchTrain(Lines line, int trainID) {
        TrainModel newTrain = TrackLineMap.getTrackLine(line).trainDispatch(trainID);
        trainSystem.addTrainProcess(newTrain);
    }

    public void update() {
            for (TrackLine line : TrackLineMap.getValues()) {
                trackLineExecutor.submit(line::update);
            }
    }

}
