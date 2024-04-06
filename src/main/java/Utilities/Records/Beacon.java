package Utilities.Records;

import trainController.ControllerBlocks.ControllerBlock;

import java.util.ArrayDeque;

public record Beacon(int startId,
                     int endId,
                     ArrayDeque<ControllerBlock> blockIndices
)
{
}