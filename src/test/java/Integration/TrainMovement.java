package Integration;

import Utilities.BasicBlockLine;
import Utilities.Enums.Lines;
import Utilities.ParsedBasicBlocks;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stubs.trainStub;
import trackModel.TrackLine;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

public class TrainMovement  extends BaseTest {

    private static TrackLine trackLine;
    private trainStub stub;
    private static BasicBlockLine basicBlockSkipList;
    private static int i = 10;
    Random r = new Random();
    private static ConcurrentSkipListSet<Integer> switches = new ConcurrentSkipListSet<>();
    private static ConcurrentSkipListSet<Integer> stations = new ConcurrentSkipListSet<>();

    @BeforeAll
    public static void setUpAll() {
        Platform.startup(() ->  {});
        basicBlockSkipList = ParsedBasicBlocks.getInstance().getBasicLine(Lines.GREEN);
        for( int i = 0; i < basicBlockSkipList.size(); i++) {
            if(basicBlockSkipList.get(i).isSwitch()) {
                switches.add(i);
            }
        }
    }

    @BeforeEach
    public void setup() throws InterruptedException {
        trackLine  = new TrackLine(Lines.GREEN, basicBlockSkipList);
        stub = new trainStub(trackLine, 1);
        trackLine.trainDispatch(stub);
        int randomSwitches = r.nextInt() >> 20;
        for(int i = 0; i < randomSwitches; i++) {
            int randomSwitch = r.nextInt() >> 20;
            if(switches.contains(randomSwitch)) {
                trackLine.setSwitchState(randomSwitch, true);
            }
        }
        Thread.sleep(1);
    }

    @Test
    public void move() {
        stub.go_Brr();
    }

    // test for train movement
    // test that the train moves to the next block
    // test that the train moves to the next block with the correct distance



    // test for train movement




    // test that the train moves to the next block




    // test that the train moves to the next block with the correct distance















}
