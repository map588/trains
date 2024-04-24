package Utilities;

import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.HelperObjects.BasicTrackLine;
import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Records.Beacon;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.Enums.BeaconType.SWITCH;

public class BeaconParser {

    /**A map of switch block numbers to the indices(directional next blocks) of the beacons that are created for that switch*/
    private static final ConcurrentHashMap<Lines, ConcurrentHashMap<Integer, Beacon>> allBeacons = new ConcurrentHashMap<>();


    public static ConcurrentHashMap<Integer, Beacon> getBeaconLine(Lines line) {
        return allBeacons.get(line);
    }

    private static final BeaconParser INSTANCE = new BeaconParser();


    public static BeaconParser getInstance() {
        return INSTANCE;
    }

    private BeaconParser(){
        for (Lines line : Lines.values()) {
            ConcurrentHashMap<Integer, Beacon> beacons = parseBeacons(line);
            allBeacons.put(line, beacons);
        }
    }


    private static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
        BasicTrackLine trackLine = BasicBlockParser.getInstance().getBasicLine(line);
        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();

        createBeacons(trackLine, beacons);

        beacons.forEach((key, value) -> {
            if(value.blockIndices().isEmpty()){
                beacons.remove(key);
            }
        });

        return beacons;
    }

    private static void createBeacons(BasicTrackLine trackLine, ConcurrentHashMap<Integer, Beacon> beacons) {
        for (BasicBlock block : trackLine.values()) {
            if (block.isSwitch() || block.isStation()) {
                Integer nodeID = block.blockNumber();

                if (block.isSwitch()) {
                    createBeaconsForSwitch(block, trackLine, beacons);
                } else {
                    createBeaconsForStation(block, trackLine, beacons);
                }

            }
        }
    }


    private static void createBeaconsForSwitch(BasicBlock switchBlock, BasicTrackLine trackLine,
                                               ConcurrentHashMap<Integer, Beacon> beacons) {
        int switchBlockNumber = switchBlock.blockNumber();

        createBeaconForPath(switchBlock, switchBlockNumber, trackLine, beacons, switchBlock.nextBlock().northDefault(), Direction.NORTH);
        createBeaconForPath(switchBlock, switchBlockNumber, trackLine, beacons, switchBlock.nextBlock().southDefault(), Direction.SOUTH);
        createBeaconForPath(switchBlock, switchBlockNumber, trackLine, beacons, switchBlock.nextBlock().northAlternate(), Direction.NORTH);
        createBeaconForPath(switchBlock, switchBlockNumber, trackLine, beacons, switchBlock.nextBlock().southAlternate(), Direction.SOUTH);
    }

    private static void createBeaconForPath(BasicBlock startBlock, int sourceBlockNumber,
                                            BasicTrackLine trackLine, ConcurrentHashMap<Integer, Beacon> beacons,
                                            Connection startConnection, Direction startDirection) {
        if (startConnection == null || startConnection.blockNumber() == -1) {
            return;
        }

        ArrayDeque<Integer> blockIndices = new ArrayDeque<>();
        int endBlockNumber = traverseTrack(startConnection.blockNumber(), trackLine, blockIndices, startDirection);

        if (endBlockNumber != -1) {
            Beacon beacon = new Beacon(SWITCH, sourceBlockNumber, endBlockNumber, blockIndices);
            beacons.put(startConnection.blockNumber(), beacon);
        }
    }

    private static void createBeaconsForStation(BasicBlock stationBlock, BasicTrackLine trackLine,
                                                ConcurrentHashMap<Integer, Beacon> beacons) {
        int stationBlockNumber = stationBlock.blockNumber();

        createBeaconForPath(stationBlock, stationBlockNumber, trackLine, beacons, stationBlock.nextBlock().north(), Direction.NORTH);
        createBeaconForPath(stationBlock, stationBlockNumber, trackLine, beacons, stationBlock.nextBlock().south(), Direction.SOUTH);
    }

    private static int traverseTrack(int currentBlockNumber, BasicTrackLine trackLine,
                                     ArrayDeque<Integer> blockIndices, Direction direction) {
        if (currentBlockNumber == -1) {
            return -1;
        }

        BasicBlock currentBlock = trackLine.get(currentBlockNumber);

        if (currentBlock.isSwitch() || currentBlock.isStation()) {
            return currentBlock.blockNumber();
        }

        blockIndices.add(currentBlockNumber);

        Connection nextConnection = (direction == Direction.NORTH) ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();

        if (nextConnection != null && nextConnection.blockNumber() != -1) {
            Direction nextDirection = nextConnection.directionChange() ? direction.opposite() : direction;
            return traverseTrack(nextConnection.blockNumber(), trackLine, blockIndices, nextDirection);
        }

        return currentBlockNumber;
    }
}