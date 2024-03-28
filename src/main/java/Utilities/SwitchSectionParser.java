package Utilities;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class SwitchSectionParser {
    public static ArrayList<SwitchSection> parseSwitchSections(ConcurrentSkipListMap<Integer, BasicBlock> blockSkipList) {
        ArrayList<BasicBlock> blocks = new ArrayList<>(blockSkipList.values());
        ArrayList<SwitchSection> switchSections = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            if (block.isSwitch()) {
                SwitchSection northDefaultSection = createSwitchSection(blocks, i, block.nextBlock().northDefault(), true);
                if (northDefaultSection != null) {
                    switchSections.add(northDefaultSection);
                }
                SwitchSection northAlternateSection = createSwitchSection(blocks, i, block.nextBlock().northAlternate(), true);
                if (northAlternateSection != null) {
                    switchSections.add(northAlternateSection);
                }
                SwitchSection southDefaultSection = createSwitchSection(blocks, i, block.nextBlock().southDefault(), false);
                if (southDefaultSection != null) {
                    switchSections.add(southDefaultSection);
                }
                SwitchSection southAlternateSection = createSwitchSection(blocks, i, block.nextBlock().southAlternate(), false);
                if (southAlternateSection != null) {
                    switchSections.add(southAlternateSection);
                }
            }
        }

        return switchSections;
    }

    private static SwitchSection createSwitchSection(ArrayList<BasicBlock> blocks, int startIndex, BasicBlock.Connection connection, boolean isNorthbound) {
        if (connection == null) {
            return null;
        }

        ArrayList<BasicBlock> section = new ArrayList<>();
        int currentIndex = getBlockIndex(blocks, connection.blockNumber());
        Set<Integer> visitedBlocks = new HashSet<>();

        while (currentIndex >= 0 && currentIndex < blocks.size() && !visitedBlocks.contains(currentIndex)) {
            BasicBlock currentBlock = blocks.get(currentIndex);
            section.add(currentBlock);
            visitedBlocks.add(currentIndex);

            if (currentBlock.isSwitch() && currentIndex != startIndex) {
                break;
            }

            BasicBlock.Connection nextConnection = isNorthbound ? currentBlock.nextBlock().north() : currentBlock.nextBlock().south();
            if (nextConnection == null) {
                break;
            }

            currentIndex = getBlockIndex(blocks, nextConnection.blockNumber());
        }

        if (section.size() > 1) {
            return new SwitchSection(section);
        } else {
            return null;
        }
    }

    private static int getBlockIndex(ArrayList<BasicBlock> blocks, int blockNumber) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).blockNumber() == blockNumber) {
                return i;
            }
        }
        return -1;
    }


    public record SwitchSection(ArrayList<BasicBlock> blocks) {}
}
