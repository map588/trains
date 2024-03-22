package trackModel;

import Utilities.BasicBlock;
import Utilities.Enums.Lines;
import Utilities.SwitchSection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.BlockParser.parseCSV;
import static Utilities.SwitchSectionParser.parseSwitchSection;

public class SectionParseTest {

    private final ConcurrentHashMap<Lines, ArrayList<SwitchSection>> switchSections = new ConcurrentHashMap<>();

    @BeforeEach
    public void setUp() {
        ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> sectionBlocks = parseCSV();
        for (Lines line : sectionBlocks.keySet()) {
            ArrayList<SwitchSection> sections = parseSwitchSection(sectionBlocks.get(line));
            switchSections.put(line, sections);
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
