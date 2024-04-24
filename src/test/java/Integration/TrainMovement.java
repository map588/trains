//package Integration;
//
//import Utilities.HelperObjects.BasicTrackLine;
//import Utilities.Enums.Lines;
//import Utilities.GlobalBasicBlockParser;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import stubs.trainStub;
//import trackModel.TrackBlock;
//import trackModel.TrackLine;
//
//import java.util.Random;
//import java.util.concurrent.ConcurrentSkipListSet;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class TrainMovement  extends BaseTest {
//
//    private static TrackLine trackLine;
//    private trainStub stub;
//    private static BasicTrackLine basicBlockSkipList;
//    private static int i = 10;
//    Random r = new Random();
//    private static ConcurrentSkipListSet<Integer> switches = new ConcurrentSkipListSet<>();
//    private static ConcurrentSkipListSet<Integer> stations = new ConcurrentSkipListSet<>();
//
//    @BeforeAll
//    public static void setUpAll() {
//        basicBlockSkipList = GlobalBasicBlockParser.getInstance().getBasicLine(Lines.GREEN);
//        TrackLine trackLine = new TrackLine(Lines.GREEN);
//        TrackBlock block;
//        for(Integer blkID : trackLine.getTrack().keySet()) {
//            block = trackLine.getTrack().get(blkID);
//            if(block.isSwitch()) {
//                switches.add(i);
//            }else if(block.isStation()) {
//                stations.add(i);
//            }
//        }
//    }
//
//    @BeforeEach
//    public void setup() throws InterruptedException {
//        trackLine  = new TrackLine(Lines.GREEN, basicBlockSkipList);
//        stub = new trainStub(trackLine, 1);
//        trackLine.trainDispatch(stub);
//
//        int randomSwitches = r.nextInt() >> 20;
//        int bitTest = 1;
//        bitTest = ~bitTest;
//        for(TrackBlock trackBlock : trackLine.getTrack().values()) {;
//            if(switches.contains(trackBlock)) {
//                if((randomSwitches | bitTest) != 0) {
//                    trackLine.setSwitchState(trackBlock.getBlockID(), true);
//                } else {
//                    trackLine.setSwitchState(trackBlock.getBlockID(), false);
//                }
//                bitTest = bitTest << 1;
//            }
//        }
//        Thread.sleep(1);
//    }
//
//    @Test
//    public void move() {
//        stub.go_Brr();
//
//        assertTrue(stub.getNumCars() > 0);
//    }
//
//    // test for train movement
//    // test that the train moves to the next block
//    // test that the train moves to the next block with the correct distance
//
//
//
//    // test for train movement
//
//
//
//
//    // test that the train moves to the next block
//
//
//
//
//    // test that the train moves to the next block with the correct distance
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
