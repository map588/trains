package Utilities.Records;

import java.util.ArrayDeque;
import java.util.ArrayList;

public record Beacon(int startId,
                     int endId,
                     ArrayDeque<BeaconEntry> beaconEntries
)
{ }