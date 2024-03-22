package Utilities;

import Utilities.Enums.Lines;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SwitchSectionParser {
    public static ArrayList<SwitchSection> parseSwitchSections(ArrayDeque<BasicBlock> track) {
       ArrayList<SwitchSection> switchSections = new ArrayList<>();

           BasicBlock previousSwitch = null;
           ArrayList<BasicBlock> currentSection = new ArrayList<>();

            for (BasicBlock block : track) {
                if (block.blockType() == BasicBlock.BlockType.SWITCH) {
                    if (previousSwitch != null) {
                        SwitchSection section = createSwitchSection(currentSection, previousSwitch, block);
                        switchSections.add(section);
                        currentSection.clear();
                    }
                    previousSwitch = block;
                } else {
                    currentSection.add(block);
                }
            }

            if (previousSwitch != null) {
                SwitchSection section = createSwitchSection(currentSection, previousSwitch, null);
                switchSections.add(section);
            }


        return switchSections;
    }

    private static SwitchSection createSwitchSection(ArrayList<BasicBlock> section, BasicBlock startSwitch, BasicBlock endSwitch) {
        SwitchSection.Direction direction;

        if (endSwitch != null) {
            BasicBlock.Direction startDirection = getDirection(startSwitch, section.get(0));
            BasicBlock.Direction endDirection = getDirection(endSwitch, section.get(section.size() - 1));

            if (startDirection == BasicBlock.Direction.TO_SWITCH && endDirection == BasicBlock.Direction.FROM_SWITCH
                    || startDirection == BasicBlock.Direction.FROM_SWITCH && endDirection == BasicBlock.Direction.TO_SWITCH) {
                direction = SwitchSection.Direction.UNIDIRECTIONAL;
            } else if (startDirection == endDirection) {
                direction = SwitchSection.Direction.BIDIRECTIONAL;
            } else {
                direction = SwitchSection.Direction.UNKNOWN;
            }
        } else {
            direction = SwitchSection.Direction.UNKNOWN;
        }

        return new SwitchSection(section, startSwitch, endSwitch, direction);
    }

    private static BasicBlock.Direction getDirection(BasicBlock switchBlock, BasicBlock connectedBlock) {
        Optional<BasicBlock.NodeConnection> connection = switchBlock.nodeConnection();
        if (connection.isPresent()) {
            BasicBlock.NodeConnection nodeConnection = connection.get();
            if (nodeConnection.defChildID() == connectedBlock.blockNumber()) {
                return nodeConnection.defDirection();
            } else if (nodeConnection.altChildID().isPresent() && nodeConnection.altChildID().get() == connectedBlock.blockNumber()) {
                return nodeConnection.altDirection().get();
            }
        }
        return BasicBlock.Direction.BIDIRECTIONAL;
    }
}