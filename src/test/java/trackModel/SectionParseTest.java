package trackModel;

import Utilities.BasicBlock;
import Utilities.Enums.Lines;
import Utilities.SwitchSection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.BlockParser.parseCSV;
import static Utilities.SwitchSectionParser.parseSwitchSections;

public class SectionParseTest {

    private static ConcurrentHashMap<Lines, ArrayList<SwitchSection>> switchSections;

    @BeforeAll
    static void setUp() {
        switchSections = new ConcurrentHashMap<>();
        ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> trackLines = new ConcurrentHashMap<>();
        try {
            trackLines = parseCSV();
        }
        finally {
            for (Lines line : Lines.values()) {
                ArrayDeque<BasicBlock> lineBlocks = trackLines.get(line);
                switchSections.put(line, parseSwitchSections(lineBlocks));
            }
        }
    }


    @Test
    void testParseSwitchSections() {

        for (Lines line : Lines.values()) {
            System.out.println("Line: " + line);
            ArrayList<SwitchSection> sections = switchSections.get(line);
            for (SwitchSection section : sections) {
                System.out.println("Start Switch: " + section.startSwitch().blockNumber());
                System.out.println("End Switch: " + (section.endSwitch() != null ? section.endSwitch().blockNumber() : "N/A"));
                System.out.println("Direction: " + section.direction());
                System.out.println("Blocks:");
                for (BasicBlock block : section.blocks()) {
                    System.out.println("- Block Number: " + block.blockNumber());
                }
                System.out.println();
            }
        }
    }

}
