package Utilities;

import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Records.Beacon;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeaconParser {
    private static final HashMap<Integer, ArrayList<Integer>> switchBeaconIndices = new HashMap<>();

    public static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
        BasicTrackLine trackLine = GlobalBasicBlockParser.getInstance().getBasicLine(line);
        return createBeaconsForLine(trackLine);
    }

    private static ConcurrentHashMap<Integer, Beacon> createBeaconsForLine(BasicTrackLine trackLine) {
        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();

        for (BasicBlock block : trackLine.values()) {
            if (block.isSwitch()) {
                Integer switchID = block.blockNumber();
                ArrayList<Integer> switchBeacons = new ArrayList<>();

                switchBeacons.add(block.nextBlock().northDefault().blockNumber());
                switchBeacons.add(block.nextBlock().southDefault().blockNumber());
                switchBeacons.add(block.nextBlock().northAlternate().blockNumber());
                switchBeacons.add(block.nextBlock().southAlternate().blockNumber());

                switchBeacons.removeIf(switchBeacon -> switchBeacon == -1);

                switchBeaconIndices.put(switchID, switchBeacons);

                createBeaconsForSwitch(block, trackLine, beacons);
            }
        }
        return beacons;
    }

    private static void createBeaconsForSwitch(BasicBlock switchBlock, BasicTrackLine trackLine,
                                               ConcurrentHashMap<Integer, Beacon> beacons) {
        int switchBlockNumber = switchBlock.blockNumber();
        ArrayList<Integer> beaconIndices = switchBeaconIndices.get(switchBlockNumber);

        Set<Integer> visitedBlocks = new HashSet<>();
        for (Integer beaconIndex : beaconIndices) {
            BasicBlock startBlock = trackLine.get(beaconIndex);
            createBeaconForPath(startBlock, switchBlockNumber, trackLine, beacons, visitedBlocks, Direction.NORTH);
            createBeaconForPath(startBlock, switchBlockNumber, trackLine, beacons, visitedBlocks, Direction.SOUTH);
        }
    }

    private static void createBeaconForPath(BasicBlock startBlock, int switchBlockNumber,
                                            BasicTrackLine trackLine, ConcurrentHashMap<Integer, Beacon> beacons,
                                            Set<Integer> visitedBlocks, Direction entryDirection) {
        ArrayDeque<Integer> blockIndices = new ArrayDeque<>();
        int startBlockNumber = startBlock.blockNumber();
        int endBlockNumber = traverseTrack(startBlockNumber, trackLine, blockIndices, switchBlockNumber, visitedBlocks, entryDirection);

        if (endBlockNumber != -1) {
            Beacon beacon = new Beacon(startBlockNumber, endBlockNumber, blockIndices);
            beacons.put(startBlockNumber, beacon);
        }
    }

    private static int traverseTrack(int currentBlockNumber, BasicTrackLine trackLine,
                                     ArrayDeque<Integer> blockIndices,
                                     int parentSwitch,
                                     Set<Integer> visitedBlocks,
                                     Direction entryDirection) {
        if (currentBlockNumber == -1 || visitedBlocks.contains(currentBlockNumber)) {
            return -1;
        }

        visitedBlocks.add(currentBlockNumber);
        BasicBlock currentBlock = trackLine.get(currentBlockNumber);
        blockIndices.add(currentBlockNumber);

        if (currentBlock.isSwitch()) {
            if (currentBlock.blockNumber() == parentSwitch) {
                return blockIndices.removeLast();
            } else {
                return currentBlock.blockNumber();
            }
        }

        Connection nextConnection = getNextConnection(currentBlock, entryDirection);

        if (nextConnection != null) {
            Direction nextDirection = nextConnection.directionChange() ? entryDirection.opposite() : entryDirection;
            return traverseTrack(nextConnection.blockNumber(), trackLine, blockIndices, parentSwitch, visitedBlocks, nextDirection);
        }

        return -1;
    }

    private static Connection getNextConnection(BasicBlock currentBlock, Direction entryDirection) {
        return (entryDirection == Direction.NORTH) ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
    }
}