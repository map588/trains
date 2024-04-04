package trackModel;

import Integration.BaseTest;
import Utilities.BasicLineMap;
import Utilities.Enums.Lines;
import Utilities.ParsedBasicBlocks;
import Utilities.Records.BasicBlock;
import Utilities.Records.TrackSegment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.TrackSegmentParser.parseTrackSegments;

public class SectionParseTest extends BaseTest {

    private final ConcurrentHashMap<Lines, ArrayList<TrackSegment>> TrackSegments = new ConcurrentHashMap<>();

    @BeforeEach
    public void setUp() {
        BasicLineMap sectionBlocks = ParsedBasicBlocks.getInstance().getAllBasicLines();
        for (Lines line : sectionBlocks.keySet()) {
            ArrayList<TrackSegment> sections = parseTrackSegments(sectionBlocks.get(line));
            TrackSegments.put(line, sections);
        }
    }

    @Test
    void testParseTrackSegments() {
        for (Lines line : Lines.values()) {
            System.out.println("Line: " + line);
            ArrayList<TrackSegment> sections = TrackSegments.get(line);
            int previousSwitch = 0;
            if (sections != null) {
                System.out.println("Number of Switch Sections: " + sections.size());
                for (TrackSegment section : sections) {
                    System.out.println("Switch Section: " + previousSwitch);
                    for (BasicBlock block : section.blocks()) {
                        if (block.isSwitch()) {
                            System.out.println(block.blockNumber() + " Block Type: SWITCH");
                            System.out.println("North Default: " + getConnectionInfo(block.nextBlock().northDefault()) + ", ");
                            System.out.println("North Alternate: " + getConnectionInfo(block.nextBlock().northAlternate()));
                            System.out.println("South Default: " + getConnectionInfo(block.nextBlock().southDefault()) + ", ");
                            System.out.println("South Alternate: " + getConnectionInfo(block.nextBlock().southAlternate()));
                            System.out.println();
                            previousSwitch = block.blockNumber();
                        }
                        else{
                            System.out.println(block.blockNumber());
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    private int indexOf(ArrayList<TrackSegment> sections, TrackSegment section) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).equals(section)) {
                return i;
            }
        }
        return -1;
    }

    private String getConnectionInfo(BasicBlock.Connection connection) {
        if (connection != null) {
            return String.format("%d ", connection.blockNumber());
        }
        return "N/A";
    }
}