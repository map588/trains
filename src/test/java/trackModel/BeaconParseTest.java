package trackModel;

import Integration.BaseTest;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.Records.Beacon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeaconParseTest extends BaseTest {

    private ConcurrentHashMap<Lines, ConcurrentHashMap<Integer, Beacon>> beaconMap;

    @BeforeEach
    public void setUp() {
        beaconMap = new ConcurrentHashMap<>();
        for (Lines line : Lines.values()) {
            ConcurrentHashMap<Integer, Beacon> beacons = BeaconParser.parseBeacons(line);
            beaconMap.put(line, beacons);
        }
    }

    @Test
    void testParseBeacons() {
        for (Lines line : Lines.values()) {
            System.out.println("Line: " + line);
            ConcurrentHashMap<Integer, Beacon> beacons = beaconMap.get(line);


            System.out.println("Number of Beacons: " + beacons.size());
            for (Beacon beacon : beacons.values()) {
                System.out.println("Start Block: " + beacon.startId());
                System.out.println("End Block: " + beacon.endId());
                System.out.println("Block Indices: " + beacon.blockIndices());
                System.out.println();
            }
        }

        assertEquals(15, beaconMap.get(Lines.GREEN).size());
        assertEquals(21, beaconMap.get(Lines.RED).size());
    }
}