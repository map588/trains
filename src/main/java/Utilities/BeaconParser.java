package Utilities;

import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Records.Beacon;
import trainController.ControllerBlocks.ControllerBlock;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
//
//public class BeaconParser {
//    private static BasicTrackMap blockLines = GlobalBasicBlockParser.getInstance().getAllBasicLines();
//
//    private static ConcurrentSkipListSet<BasicBlock> switches = new ConcurrentSkipListSet<>();
//
//
//
//
//
//    public static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
//        ConcurrentSkipListSet<BasicBlock> switchIndices = getSwitches(line);
//        ConcurrentSkipListMap<Integer, BasicBlock> trackList = blockLines.get(line);
//        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();
//
//        for (BasicBlock block : switchIndices) {
//            int northDef = block.nextBlock().northDefault().blockNumber();
//            for (BasicBlock sectionBlock : trackList.values()) {
//                //North Default
//                if (sectionBlock.blockNumber() == northDef) {
//                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
//                    BasicBlock nextBlock = sectionBlock;
//                    Direction currentDirection = block.nextBlock().north().directionChange() ? Direction.NORTH : Direction.SOUTH;
//                    while (!isNextBlockSwitch(nextBlock)) {
//                        if(nextBlock.nextBlock().north().directionChange()) {
//                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
//                            currentDirection = Direction.SOUTH;
//                        } else {
//                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
//                            currentDirection = Direction.NORTH;
//                        }
//                        section.add(new ControllerBlock(nextBlock));
//                    }
//                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
//                     beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end , section));
//                }
//                //South Default
//                if (sectionBlock.blockNumber() == block.nextBlock().southDefault().blockNumber()) {
//                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
//                    BasicBlock nextBlock = sectionBlock;
//                    Direction currentDirection = block.nextBlock().south().directionChange() ? Direction.SOUTH : Direction.NORTH;
//                    while (!isNextBlockSwitch(nextBlock)) {
//                        if (nextBlock.nextBlock().north().directionChange()) {
//                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
//                            currentDirection = Direction.SOUTH;
//                        } else {
//                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
//                            currentDirection = Direction.NORTH;
//                        }
//                        section.add(new ControllerBlock(nextBlock));
//                    }
//                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
//                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
//                }
//                //North Alternate
//                if (sectionBlock.blockNumber() == block.nextBlock().northAlternate().blockNumber()) {
//                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
//                    BasicBlock nextBlock = sectionBlock;
//                    Direction currentDirection = block.nextBlock().north().directionChange() ? Direction.NORTH : Direction.SOUTH;
//                    while (!isNextBlockSwitch(nextBlock)) {
//                        if (nextBlock.nextBlock().north().directionChange()) {
//                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
//                            currentDirection = Direction.SOUTH;
//                        } else {
//                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
//                            currentDirection = Direction.NORTH;
//                        }
//                        section.add(new ControllerBlock(nextBlock));
//                    }
//                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
//                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
//                }
//                //South Alternate
//                if (sectionBlock.blockNumber() == block.nextBlock().southAlternate().blockNumber()) {
//                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
//                    BasicBlock nextBlock = sectionBlock;
//                    Direction currentDirection = block.nextBlock().south().directionChange() ? Direction.SOUTH : Direction.NORTH;
//                    while (!isNextBlockSwitch(nextBlock)) {
//                        if (nextBlock.nextBlock().north().directionChange()) {
//                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
//                            currentDirection = Direction.SOUTH;
//                        } else {
//                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
//                            currentDirection = Direction.NORTH;
//                        }
//                        section.add(new ControllerBlock(nextBlock));
//                    }
//                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
//                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
//                }
//            }
//        }
//
//
//        return beacons;
//    }
//
//
//    private static boolean isNextBlockSwitch(BasicBlock block) {
//        return block.nextBlock().north().blockNumber() == block.nextBlock().northDefault().blockNumber() ||
//                block.nextBlock().south().blockNumber() == block.nextBlock().southDefault().blockNumber();
//    }
//
//
//    private static ConcurrentSkipListSet<BasicBlock> getSwitches(Lines line) {
//        blockLines.get(line).values().forEach(block -> {
//            if (block.isSwitch()) switches.add(block);
//        });
//        return switches;
//    }
//}
public class BeaconParser {
    private static BasicTrackMap blockLines = GlobalBasicBlockParser.getInstance().getAllBasicLines();

    public static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
        ConcurrentSkipListSet<BasicBlock> switchBlocks = getSwitches(line);
        ConcurrentSkipListMap<Integer, BasicBlock> trackList = blockLines.get(line);
        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();

        for (BasicBlock switchBlock : switchBlocks) {
            processBeaconSection(switchBlock, trackList, beacons, switchBlock.nextBlock().northDefault(), Direction.NORTH);
            processBeaconSection(switchBlock, trackList, beacons, switchBlock.nextBlock().southDefault(), Direction.SOUTH);
            processBeaconSection(switchBlock, trackList, beacons, switchBlock.nextBlock().northAlternate(), Direction.NORTH);
            processBeaconSection(switchBlock, trackList, beacons, switchBlock.nextBlock().southAlternate(), Direction.SOUTH);
        }

        return beacons;
    }

    private static void processBeaconSection(BasicBlock switchBlock, ConcurrentSkipListMap<Integer, BasicBlock> trackList,
                                             ConcurrentHashMap<Integer, Beacon> beacons, Connection connection, Direction direction) {
        if (connection != null) {
            BasicBlock startBlock = trackList.get(connection.blockNumber());
            if (startBlock != null) {
                ArrayDeque<ControllerBlock> section = new ArrayDeque<>();
                BasicBlock currentBlock = startBlock;
                Direction currentDirection = direction;

                while (!isNextBlockSwitch(currentBlock)) {
                    section.add(new ControllerBlock(currentBlock));
                    BasicBlock nextBlock = getNextBlock(currentBlock, currentDirection, trackList);
                    if (nextBlock == null) {
                        break;
                    }
                    currentBlock = nextBlock;
                    currentDirection = getNextDirection(currentBlock, currentDirection);
                }

                int endBlockNumber = 0;
                if (currentDirection == Direction.NORTH && currentBlock.nextBlock().north() != null) {
                    endBlockNumber = currentBlock.nextBlock().north().blockNumber();
                } else if (currentDirection == Direction.SOUTH && currentBlock.nextBlock().south() != null) {
                    endBlockNumber = currentBlock.nextBlock().south().blockNumber();
                }
                beacons.put(startBlock.blockNumber(), new Beacon(switchBlock.blockNumber(), endBlockNumber, section));
            }
        }
    }

    private static BasicBlock getNextBlock(BasicBlock currentBlock, Direction currentDirection, ConcurrentSkipListMap<Integer, BasicBlock> trackList) {
        BasicBlock.Connection nextConnection = currentDirection == Direction.NORTH ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
        return nextConnection != null ? trackList.get(nextConnection.blockNumber()) : null;
    }

    private static Direction getNextDirection(BasicBlock currentBlock, Direction currentDirection) {
        if (currentBlock == null) {
            return currentDirection;
        }

        Connection nextConnection = currentDirection == Direction.NORTH ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
        return nextConnection != null && nextConnection.directionChange() ? oppositeDirection(currentDirection) : currentDirection;
    }

    private static Direction oppositeDirection(Direction direction) {
        return direction == Direction.NORTH ? Direction.SOUTH : Direction.NORTH;
    }

    private static boolean isNextBlockSwitch(BasicBlock block) {
        return block.nextBlock().northDefault() != null || block.nextBlock().southDefault() != null;
    }

    private static ConcurrentSkipListSet<BasicBlock> getSwitches(Lines line) {
        ConcurrentSkipListSet<BasicBlock> switches = new ConcurrentSkipListSet<>();
        blockLines.get(line).values().forEach(block -> {
            if (block.isSwitch()) switches.add(block);
        });
        return switches;
    }
}
