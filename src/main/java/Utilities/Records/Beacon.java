package Utilities.Records;

import java.util.ArrayDeque;

public record Beacon(Integer startId,
                     Integer endId,
                     ArrayDeque<Integer> blockIndices
) {}