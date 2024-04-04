package Integration;

import Framework.Simulation.TrainSystem;
import Utilities.BasicLineMap;
import Utilities.ParsedBasicBlocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trackModel.TrackLine;

import static Utilities.Enums.Lines.GREEN;

public class TrainModelAndController extends BaseTest  {

    private final TrainSystem trainSystem = new TrainSystem();
    private final BasicLineMap trackLines = ParsedBasicBlocks.getInstance().getAllBasicLines();
    private  TrackLine trackLine;

    @BeforeEach
    void setUp() {
        TrackLine line = new TrackLine(GREEN, trackLines.get(GREEN));
    }

//    @Test
//    public void DoesNotExplode() {
//        boolean please = true;
//        try{
//            trainSystem.dispatchTrain(trackLine, 1);
//            trainSystem.update();
//        } catch (Exception e) {
//            please = false;
//            e.printStackTrace();
//        }
//        assert(please);
//    }

}
