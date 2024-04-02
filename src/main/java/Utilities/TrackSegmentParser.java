package Utilities;

import Utilities.Records.BasicBlock;
import Utilities.Records.TrackSegment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrackSegmentParser {

    public static ArrayList<TrackSegment> parseTrackSegments(ConcurrentSkipListMap<Integer, BasicBlock> blockSkipList) {
        ArrayList<TrackSegment> trackSegments = new ArrayList<>();
        Set<BasicBlock> visitedBlocks = new HashSet<>();

        for (BasicBlock startBlock : blockSkipList.values()) {
            if (!visitedBlocks.contains(startBlock)) {
                TrackSegment segment = createTrackSegment(blockSkipList, startBlock, visitedBlocks);
                if (segment != null) {
                    trackSegments.add(segment);
                }
            }
        }

        return trackSegments;
    }

    private static TrackSegment createTrackSegment(ConcurrentSkipListMap<Integer, BasicBlock> blockSkipList,
                                                   BasicBlock startBlock, Set<BasicBlock> visitedBlocks) {
        ArrayList<BasicBlock> segmentBlocks = new ArrayList<>();
        BasicBlock currentBlock = startBlock;
        boolean changedDirections = false;

        while (currentBlock != null && !visitedBlocks.contains(currentBlock)) {
            segmentBlocks.add(currentBlock);
            visitedBlocks.add(currentBlock);

            if (currentBlock.isSwitch()) {
                BasicBlock.Connection nextConnection = changedDirections ? currentBlock.nextBlock().northDefault() : currentBlock.nextBlock().southDefault();
                if (nextConnection != null && nextConnection.directionChange()) {
                    changedDirections = !changedDirections;
                }
            }

            BasicBlock.Connection nextConnection = changedDirections ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
            currentBlock = nextConnection != null ? blockSkipList.get(nextConnection.blockNumber()) : null;
        }

        if (segmentBlocks.size() > 1) {
            return new TrackSegment(new ArrayDeque<>(segmentBlocks));
        }

        return null;
    }
}