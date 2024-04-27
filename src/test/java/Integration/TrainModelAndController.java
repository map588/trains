package Integration;

import static Utilities.Enums.Lines.GREEN;

import Framework.Simulation.TrainSystem;
import Utilities.BasicBlockParser;
import Utilities.BasicTrackMap;
import org.junit.jupiter.api.BeforeEach;
import trackModel.TrackLine;

public class TrainModelAndController extends BaseTest {

  private final TrainSystem trainSystem = new TrainSystem();
  private final BasicTrackMap trackLines = BasicBlockParser.getInstance().getAllBasicLines();
  private TrackLine trackLine;

  @BeforeEach
  void setUp() {
    TrackLine line = new TrackLine(GREEN);
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
