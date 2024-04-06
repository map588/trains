package Utilities;

import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import Utilities.Records.Beacon;
import trainController.ControllerBlocks.ControllerBlock;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class BeaconParser {
    private static BlockSkipListMap blockLines = GlobalBasicBlockParser.getInstance().getAllBasicLines();

    private static ConcurrentSkipListSet<BasicBlock> switches = new ConcurrentSkipListSet<>();





    public static ConcurrentHashMap<Integer, Beacon> parseBeacons(Lines line) {
        ConcurrentSkipListSet<BasicBlock> switchIndices = getSwitches(line);
        ConcurrentSkipListMap<Integer, BasicBlock> trackList = blockLines.get(line);
        ConcurrentHashMap<Integer, Beacon> beacons = new ConcurrentHashMap<>();

        for (BasicBlock block : switchIndices) {
            int northDef = block.nextBlock().northDefault().blockNumber();
            for (BasicBlock sectionBlock : trackList.values()) {
                //North Default
                if (sectionBlock.blockNumber() == northDef) {
                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
                    BasicBlock nextBlock = sectionBlock;
                    Direction currentDirection = block.nextBlock().north().directionChange() ? Direction.NORTH : Direction.SOUTH;
                    while (!isNextBlockSwitch(nextBlock)) {
                        if(nextBlock.nextBlock().north().directionChange()) {
                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
                            currentDirection = Direction.SOUTH;
                        } else {
                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
                            currentDirection = Direction.NORTH;
                        }
                        section.add(new ControllerBlock(nextBlock));
                    }
                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
                     beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end , section));
                }
                //South Default
                if (sectionBlock.blockNumber() == block.nextBlock().southDefault().blockNumber()) {
                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
                    BasicBlock nextBlock = sectionBlock;
                    Direction currentDirection = block.nextBlock().south().directionChange() ? Direction.SOUTH : Direction.NORTH;
                    while (!isNextBlockSwitch(nextBlock)) {
                        if (nextBlock.nextBlock().north().directionChange()) {
                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
                            currentDirection = Direction.SOUTH;
                        } else {
                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
                            currentDirection = Direction.NORTH;
                        }
                        section.add(new ControllerBlock(nextBlock));
                    }
                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
                }
                //North Alternate
                if (sectionBlock.blockNumber() == block.nextBlock().northAlternate().blockNumber()) {
                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
                    BasicBlock nextBlock = sectionBlock;
                    Direction currentDirection = block.nextBlock().north().directionChange() ? Direction.NORTH : Direction.SOUTH;
                    while (!isNextBlockSwitch(nextBlock)) {
                        if (nextBlock.nextBlock().north().directionChange()) {
                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
                            currentDirection = Direction.SOUTH;
                        } else {
                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
                            currentDirection = Direction.NORTH;
                        }
                        section.add(new ControllerBlock(nextBlock));
                    }
                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
                }
                //South Alternate
                if (sectionBlock.blockNumber() == block.nextBlock().southAlternate().blockNumber()) {
                    ArrayDeque<ControllerBlock> section = new ArrayDeque();
                    BasicBlock nextBlock = sectionBlock;
                    Direction currentDirection = block.nextBlock().south().directionChange() ? Direction.SOUTH : Direction.NORTH;
                    while (!isNextBlockSwitch(nextBlock)) {
                        if (nextBlock.nextBlock().north().directionChange()) {
                            nextBlock = trackList.get(nextBlock.nextBlock().north().blockNumber());
                            currentDirection = Direction.SOUTH;
                        } else {
                            nextBlock = trackList.get(nextBlock.nextBlock().south().blockNumber());
                            currentDirection = Direction.NORTH;
                        }
                        section.add(new ControllerBlock(nextBlock));
                    }
                    int end = (currentDirection == Direction.NORTH) ? sectionBlock.nextBlock().north().blockNumber() : sectionBlock.nextBlock().south().blockNumber();
                    beacons.put(sectionBlock.blockNumber(), new Beacon(block.blockNumber(), end, section));
                }
            }
        }


        return null;
    }


    private static boolean isNextBlockSwitch(BasicBlock block) {
        return block.nextBlock().north().blockNumber() == block.nextBlock().northDefault().blockNumber() ||
                block.nextBlock().south().blockNumber() == block.nextBlock().southDefault().blockNumber();
    }


    private static ConcurrentSkipListSet<BasicBlock> getSwitches(Lines line) {
        blockLines.get(line).values().forEach(block -> {
            if (block.isSwitch()) switches.add(block);
        });
        return switches;
    }
}