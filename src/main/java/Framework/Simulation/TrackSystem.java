package Framework.Simulation;

import Common.TrainModel;
import Utilities.BasicBlockParser;
import Utilities.Enums.Lines;
import Utilities.HelperObjects.TrackLineMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trackModel.TrackLine;

import java.util.HashSet;

public class TrackSystem {

    private static final Logger logger = LoggerFactory.getLogger(TrackSystem.class);
    private static final BasicBlockParser blockParser = BasicBlockParser.getInstance();
    private static final HashSet<Lines> lines = blockParser.getLines();


    private static TrainSystem trainSystem;

    public TrackSystem(TrainSystem trainSystem) {
        TrackSystem.trainSystem = trainSystem;

        for (Lines line : lines) {
            if(line.equals(Lines.NULL)){
                continue;
            }
            TrackLineMap.addTrackLine(line, new TrackLine(line));
            logger.info("TrackLine {} has been added to the TrackLineMap", line);
        }
    }


    public void dispatchTrain(Lines line, int trainID) {
        TrainModel newTrain = TrackLineMap.getTrackLine(line).trainDispatch(trainID);
        trainSystem.addTrainProcess(newTrain);
    }

    public void update() {
        for (TrackLine line : TrackLineMap.getValues()) {
            line.update();
        }
    }

}
