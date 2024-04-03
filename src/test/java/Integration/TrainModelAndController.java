package Integration;

import Framework.Simulation.TrainSystem;
import Utilities.BlockParser;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trackModel.TrackLine;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static Utilities.Enums.Lines.GREEN;

public class TrainModelAndController {

    private final TrainSystem trainSystem = new TrainSystem();
    private final ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> trackLines = BlockParser.parseCSV();
    private  TrackLine trackLine;


    @BeforeEach
    void setUp() {
        TrackLine line = new TrackLine(GREEN, trackLines.get(GREEN));
    }

    @Test
    public void DoesNotExplode() {
        boolean please = true;
        try{
            trainSystem.dispatchTrain(trackLine, 1);
            trainSystem.update();
        } catch (Exception e) {
            please = false;
            e.printStackTrace();
        }
        assert(please);
    }

}
