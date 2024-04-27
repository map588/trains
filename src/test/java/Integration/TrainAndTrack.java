package Integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import Framework.Simulation.TrackSystem;
import Framework.Simulation.TrainSystem;
import Utilities.Enums.Lines;
import org.junit.jupiter.api.Test;

class TrainAndTrack extends BaseTest {

  private final TrainSystem trainSystem = new TrainSystem();
  private final TrackSystem trackSystem = new TrackSystem(trainSystem);

  @Test
  void DoesNotExplode() {
    boolean please = true;
    try {
      trackSystem.dispatchTrain(Lines.GREEN, 1);
    } catch (Exception e) {
      please = false;
      e.printStackTrace();
    }
    assertTrue(please);
  }
}
