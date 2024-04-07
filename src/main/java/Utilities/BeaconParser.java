package Utilities;

import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.Beacon;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

public class BeaconParser {
    public static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
        BasicTrackLine trackLine = GlobalBasicBlockParser.getInstance().getBasicLine(line);
        return createBeaconsForLine(trackLine);
    }

    private static ConcurrentHashMap<Integer, Beacon> createBeaconsForLine(BasicTrackLine trackLine) {
        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();

        for (BasicBlock block : trackLine.values()) {
            if (block.isSwitch()) {
                createBeaconForSwitch(block, trackLine, beacons);
            }
        }

        return beacons;
    }

    private static void createBeaconForSwitch(BasicBlock switchBlock, BasicTrackLine trackLine, ConcurrentHashMap<Integer, Beacon> beacons) {
        int switchBlockNumber = switchBlock.blockNumber();

        BasicBlock.NextBlock nextBlocks = switchBlock.nextBlock();
        createBeaconForConnection(nextBlocks.northDefault(), switchBlockNumber, trackLine, beacons);
        createBeaconForConnection(nextBlocks.southDefault(), switchBlockNumber, trackLine, beacons);
        createBeaconForConnection(nextBlocks.northAlternate(), switchBlockNumber, trackLine, beacons);
        createBeaconForConnection(nextBlocks.southAlternate(), switchBlockNumber, trackLine, beacons);
    }

    private static void createBeaconForConnection(BasicBlock.Connection connection, int switchBlockNumber, BasicTrackLine trackLine, ConcurrentHashMap<Integer, Beacon> beacons) {
        if (connection != null && connection.blockNumber() != -1) {
            int startBlockNumber = connection.blockNumber();
            ArrayDeque<Integer> blockIndices = new ArrayDeque<>();
            int currentBlockNumber = startBlockNumber;

            while (currentBlockNumber != -1 && !trackLine.get(currentBlockNumber).isSwitch()) {
                blockIndices.add(currentBlockNumber);
                currentBlockNumber = getNextBlockNumber(trackLine.get(currentBlockNumber));
            }

            int endBlockNumber = currentBlockNumber != -1 ? currentBlockNumber : 0;
            Beacon beacon = new Beacon(switchBlockNumber, endBlockNumber, blockIndices);
            beacons.put(startBlockNumber, beacon);
        }
    }

    private static int getNextBlockNumber(BasicBlock currentBlock) {
        BasicBlock.Connection nextConnection = currentBlock.nextBlock().north() != null ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
        return nextConnection != null ? nextConnection.blockNumber() : -1;
    }
}