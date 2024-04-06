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
        System.out.print("Key: " + k);
        System.out.println("  Start: " + v.startId());
        for(Beacon b : beacons.values()){
            b.blockIndices().forEach((i) -> System.out.println("Block: " + i.blockNumber()));
        }
        System.out.println("End: " + v.endId());
    });

        assertFalse(beacons.isEmpty());
    }
}
