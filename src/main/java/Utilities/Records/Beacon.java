package Utilities.Records;

import java.util.List;

public record Beacon(int startId,
                     int endId,
                     List<BeaconEntry> beaconEntries
)
{ }