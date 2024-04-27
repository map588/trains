// package Utilities;
//
// import Utilities.Enums.Direction;
// import Utilities.Enums.Lines;
// import Utilities.Records.BasicBlock;
// import Utilities.Records.Beacon;
//
// import java.util.ArrayDeque;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.concurrent.ConcurrentHashMap;
//
// import static Utilities.Enums.BeaconType.SWITCH;
//
// @SuppressWarnings("ALL")
// public class SectionParser {
//
//    /**A map of switch block numbers to the indices(directional next blocks) of the beacons that
// are created for that switch*/
//    private static final HashMap<Integer, ArrayList<Integer>> switchBeaconIndices = new
// HashMap<>();
//    private static final ConcurrentHashMap<Lines, ConcurrentHashMap<Integer, Beacon>> allBeacons =
// new ConcurrentHashMap<>();
//
//
//    public static ConcurrentHashMap<Integer, Beacon> getBeaconLine(Lines line) {
//        return allBeacons.get(line);
//    }
//
//    private static final SectionParser INSTANCE = new SectionParser();
//
//
//    public static SectionParser getInstance() {
//        return INSTANCE;
//    }
//
//    private SectionParser(){
//        for (Lines line : Lines.values()) {
//            ConcurrentHashMap<Integer, Beacon> beacons = parseSections(line);
//            allBeacons.put(line, beacons);
//        }
//    }
//
//
//    private static ConcurrentHashMap<Integer, Beacon> parseSections(Lines line) {
//        BasicTrackLine trackLine = GlobalBasicBlockParser.getInstance().getBasicLine(line);
//        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();
//
//        createSwitchBeacons(trackLine, beacons);
//
//        beacons.forEach((key, value) -> {
//            if(value.blockIndices().isEmpty()) {
//                beacons.remove(key);
//            }
//        });
//
//        return beacons;
//    }
//
//
//
//    private static void createSwitchBeacons(BasicTrackLine trackLine, ConcurrentHashMap<Integer,
// Beacon> beacons) {
//        for (BasicBlock block : trackLine.values()) {
//            if (block.isSwitch()) {
//                Integer switchID = block.blockNumber();
//                ArrayList<Integer> switchBeacons = new ArrayList<>();
//
//                switchBeacons.add(block.nextBlock().northDefault().blockNumber());
//                switchBeacons.add(block.nextBlock().southDefault().blockNumber());
//                switchBeacons.add(block.nextBlock().northAlternate().blockNumber());
//                switchBeacons.add(block.nextBlock().southAlternate().blockNumber());
//
//                switchBeacons.removeIf(switchBeacon -> switchBeacon == -1);
//
//                switchBeaconIndices.put(switchID, switchBeacons);
//
//                createBeaconsForSwitch(block, trackLine, beacons);
//            }
//        }
//    }
//
//    private static void createBeaconsForSwitch(BasicBlock switchBlock, BasicTrackLine trackLine,
//                                               ConcurrentHashMap<Integer, Beacon> beacons) {
//        int switchBlockNumber = switchBlock.blockNumber();
//        ArrayList<Integer> beaconIndices = switchBeaconIndices.get(switchBlockNumber);
//
//        for (Integer beaconIndex : beaconIndices) {
//            BasicBlock startBlock = trackLine.get(beaconIndex);
//            createBeaconForPath(startBlock, switchBlockNumber, trackLine, beacons,
// Direction.NORTH);
//            createBeaconForPath(startBlock, switchBlockNumber, trackLine, beacons,
// Direction.SOUTH);
//        }
//    }
//
//    private static void createBeaconForPath(BasicBlock startBlock, int switchBlockNumber,
//                                            BasicTrackLine trackLine, ConcurrentHashMap<Integer,
// Beacon> beacons,
//                                            Direction entryDirection) {
//        ArrayDeque<Integer> blockIndices = new ArrayDeque<>();
//        int startBlockNumber = startBlock.blockNumber();
//        int endBlockNumber = traverseTrackForSwitchBeacon(startBlockNumber, trackLine,
// blockIndices, switchBlockNumber, entryDirection);
//
//        if (endBlockNumber != -1) {
//            Beacon existingBeacon = beacons.get(startBlockNumber);
//            if (existingBeacon == null || blockIndices.size() >
// existingBeacon.blockIndices().size()) {
//                Beacon beacon = new Beacon(SWITCH, switchBlockNumber, endBlockNumber,
// blockIndices);
//                beacons.put(startBlockNumber, beacon);
//            }
//        }
//    }
//
//    private static int traverseTrackForSwitchBeacon(int currentBlockNumber, BasicTrackLine
// trackLine,
//                                                    ArrayDeque<Integer> blockIndices,
//                                                    int parentSwitch,
//                                                    Direction entryDirection) {
//        if (currentBlockNumber == -1) {
//            return -1;
//        }
//
//        BasicBlock currentBlock = trackLine.get(currentBlockNumber);
//
//        if (currentBlock.isSwitch()) {
//            return currentBlock.blockNumber();
//        }
//
//        blockIndices.add(currentBlockNumber);
//
//        BasicBlock.Connection nextConnection = getNextConnection(currentBlock, entryDirection);
//
//        if (nextConnection != null) {
//            Direction nextDirection = nextConnection.directionChange() ? entryDirection.opposite()
// : entryDirection;
//            return traverseTrackForSwitchBeacon(nextConnection.blockNumber(), trackLine,
// blockIndices, parentSwitch, nextDirection);
//        }
//
//        return -1;
//    }
//
//    private static BasicBlock.Connection getNextConnection(BasicBlock currentBlock, Direction
// entryDirection) {
//        return (entryDirection == Direction.NORTH) ? currentBlock.nextBlock().north() :
// currentBlock.nextBlock().south();
//    }
// }
