package trackModel;

import Integration.BaseTest;
import Utilities.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.BeaconType;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
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
            BasicTrackLine trackLine = GlobalBasicBlockParser.getInstance().getBasicLine(line);


            System.out.println("Number of Beacons: " + beacons.size());
            for (Integer beaconID : beacons.keySet()) {
                Beacon beacon = beacons.get(beaconID);
                System.out.print( beaconID + " ");
                if (beacon.type() == BeaconType.STATION) {
                    System.out.println(trackLine.get(beacon.startId()).stationName().get() + ": " + beacon.startId() + " -> " + beacon.endId());
                } else {
                    System.out.println("SW: " + beacon.startId() + " -> " + beacon.endId());
                }
                System.out.println("Blocks: " + beacon.blockIndices());
                System.out.println();
            }
        }

        assertEquals(15, beaconMap.get(Lines.GREEN).size());
        assertEquals(21, beaconMap.get(Lines.RED).size());
    }
}