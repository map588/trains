package Integration;

import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.Records.Beacon;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class BeaconTest extends BaseTest {

    @Test
    public void setUp() {
        ConcurrentHashMap<Integer, Beacon> beacons = BeaconParser.parseBeacons(Lines.GREEN);
        beacons.forEach((k, v) -> {
            System.out.println("Beacon: " + k);
            System.out.println("Switch: " + v.startId());
            v.blockIndices().forEach((i) -> System.out.println("Block: " + i.toString()));
            System.out.println("End: " + v.endId());
            System.out.println();
        });
        assertFalse(beacons.isEmpty());
    }
}