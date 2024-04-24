package trackModel;

import Integration.BaseTest;
import Utilities.HelperObjects.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.BasicBlockParser;
import Utilities.Records.Beacon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class BeaconParseTest extends BaseTest {

    private ConcurrentHashMap<Lines, ConcurrentHashMap<Integer, Beacon>> beaconMap;

    private BeaconParser beaconParser = BeaconParser.getInstance();

    @BeforeEach
    public void setUp() {
        beaconMap = new ConcurrentHashMap<>();
        for (Lines line : Lines.values()) {
            ConcurrentHashMap<Integer, Beacon> beacons = BeaconParser.getBeaconLine(line);
            beaconMap.put(line, beacons);
        }
    }

    @Test
    void testParseBeacons() {
        for (Lines line : Lines.values()) {
            System.out.println("Line: " + line);
            ConcurrentHashMap<Integer, Beacon> beacons = beaconMap.get(line);
            BasicTrackLine trackLine = BasicBlockParser.getInstance().getBasicLine(line);
            HashMap<Integer, Integer> totalSourceBeacons = new HashMap<>();

            System.out.println("Number of Beacons: " + beacons.size());
            for (Integer beaconID : beacons.keySet()) {
                Beacon beacon = beacons.get(beaconID);
                totalSourceBeacons.put(beacon.sourceId(), totalSourceBeacons.getOrDefault(beacon.sourceId(), 0) + 1);
                System.out.print( beaconID + " ");
                if (trackLine.get(beacon.sourceId()).isStation()) {
                    System.out.println(trackLine.get(beacon.sourceId()).stationName().get() + ": " + beacon.sourceId() + " -> " + beacon.endId());
                } else {
                    System.out.println("SW: " + beacon.sourceId() + " -> " + beacon.endId());
                }
                System.out.println("Blocks: " + beacon.blockIndices());
                System.out.println();
            }

            for(Integer sourceID : totalSourceBeacons.keySet()) {
               if(trackLine.get(sourceID).isSwitch() && totalSourceBeacons.get(sourceID) != 3) {
                   System.out.println("Switch " + sourceID + " has "+ totalSourceBeacons.get(sourceID)  +" beacons");
               }else if(trackLine.get(sourceID).isStation() && totalSourceBeacons.get(sourceID) != 2) {
                   System.out.println("Station " + sourceID + " has "+ totalSourceBeacons.get(sourceID) +" beacons");
               }
            }
        }

        assertFalse(beaconMap.isEmpty());


    }
}