package Utilities.Records;

import Utilities.Enums.BeaconType;

import java.util.ArrayDeque;

public record Beacon(BeaconType type,
                     Integer startId,
                     Integer endId,
                     ArrayDeque<Integer> blockIndices
) {}