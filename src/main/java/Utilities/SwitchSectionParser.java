package Utilities;

import Utilities.Enums.Lines;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

public class SwitchSectionParser {
    public static ArrayList<SwitchSection> parseSwitchSection(ArrayDeque<BasicBlock> blockDeque) {
        ArrayList<BasicBlock> blocks = new ArrayList<>(blockDeque);
        ArrayList<SwitchSection> switchSections = new ArrayList<>();

            for (int i = 0; i < blocks.size(); i++) {
                BasicBlock block = blocks.get(i);
                if (block.blockType() == BasicBlock.BlockType.SWITCH) {
                    Optional<BasicBlock.NodeConnection> connection = block.nodeConnection();
                    if (connection.isPresent()) {
                        BasicBlock.NodeConnection nodeConnection = connection.get();
                        int defChildIndex = getChildIndex(blocks, nodeConnection.defChildID());
                        if (defChildIndex != -1) {
                            SwitchSection defSection = createSwitchSection(blocks, i, defChildIndex, nodeConnection.defDirection());
                            switchSections.add(defSection);
                        }
                        if (nodeConnection.altChildID().isPresent()) {
                            int altChildIndex = getChildIndex(blocks, nodeConnection.altChildID().get());
                            if (altChildIndex != -1) {
                                SwitchSection altSection = createSwitchSection(blocks, i, altChildIndex, nodeConnection.altDirection().get());
                                switchSections.add(altSection);
                            }
                        }
                    }
                }
            }

        return switchSections;
    }

    private static int getChildIndex(ArrayList<BasicBlock> blocks, int childID) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).blockNumber() == childID) {
                return i;
            }
        }
        return -1;
    }

    private static SwitchSection createSwitchSection(ArrayList<BasicBlock> blocks, int startIndex, int endIndex, BasicBlock.Direction startDirection) {
        ArrayList<BasicBlock> section = new ArrayList<>();
        for (int i = startIndex + 1; i <= endIndex; i++) {
            section.add(blocks.get(i));
        }
        BasicBlock startSwitch = blocks.get(startIndex);
        BasicBlock endSwitch = blocks.get(endIndex);
        SwitchSection.Direction direction = getDirection(startDirection, endSwitch.nodeConnection());
        return new SwitchSection(section, startSwitch, endSwitch, direction);
    }

    private static SwitchSection.Direction getDirection(BasicBlock.Direction startDirection, Optional<BasicBlock.NodeConnection> endConnection) {
        if (endConnection.isPresent()) {
            BasicBlock.NodeConnection nodeConnection = endConnection.get();
            BasicBlock.Direction endDirection = nodeConnection.defDirection();
            if (startDirection == BasicBlock.Direction.TO_SWITCH && endDirection == BasicBlock.Direction.FROM_SWITCH
                    || startDirection == BasicBlock.Direction.FROM_SWITCH && endDirection == BasicBlock.Direction.TO_SWITCH) {
                return SwitchSection.Direction.UNIDIRECTIONAL;
            } else if (startDirection == endDirection) {
                return SwitchSection.Direction.BIDIRECTIONAL;
            }
        }
        return SwitchSection.Direction.UNKNOWN;
    }
}