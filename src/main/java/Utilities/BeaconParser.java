package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.Beacon;
import Utilities.Records.BeaconEntry;
import Utilities.Records.TrackSegment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BeaconParser {
    public static ConcurrentHashMap<Lines, Map<Integer, Beacon>> createBeacons() {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> basicBlocks = BlockParser.parseCSV();
        ConcurrentHashMap<Lines, Map<Integer, Beacon>> lineBeacons = new ConcurrentHashMap<>();

        for (Lines line : Lines.values()) {
            List<TrackSegment> trackSegments = TrackSegmentParser.parseTrackSegments(basicBlocks.get(line));
            Map<Integer, Beacon> beacons = createBeaconsForLine(trackSegments);
            lineBeacons.put(line, beacons);
        }

        return lineBeacons;
    }

    private static Map<Integer, Beacon> createBeaconsForLine(List<TrackSegment> trackSegments) {
        Map<Integer, Beacon> beacons = new HashMap<>();

        for (TrackSegment segment : trackSegments) {
            ArrayDeque<BeaconEntry> beaconEntries = new ArrayDeque<>();
            int startId = segment.blocks().getFirst().blockNumber();
            int endId = segment.blocks().getLast().blockNumber();

            for (BasicBlock block : segment.blocks()) {
                beaconEntries.add(new BeaconEntry(block));
            }

            Beacon beacon = new Beacon(startId, endId, beaconEntries);
            beacons.put(startId, beacon);
        }

        return beacons;
    }
}