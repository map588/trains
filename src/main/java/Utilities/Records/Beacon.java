package Utilities.Records;

import trainController.ControllerBlock;

import java.util.ArrayDeque;

public record Beacon(int startId,
                     int endId,
                     ArrayDeque<ControllerBlock> beaconEntries
)
{ }