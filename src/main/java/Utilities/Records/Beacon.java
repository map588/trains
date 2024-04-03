package Utilities.Records;

import java.util.ArrayDeque;

public record Beacon(int startId,
                     int endId,
                     ArrayDeque<BeaconEntry> beaconEntries
)
{ }