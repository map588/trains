package Utilities.Records;

import java.util.ArrayDeque;

public record TrackSegment(
        ArrayDeque<BasicBlock> blocks
) {
}
