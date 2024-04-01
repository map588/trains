package Utilities;

import Utilities.Enums.Lines;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TrackSegment {

    ConcurrentHashMap<Lines, ArrayList<BasicBlock>> switchSections;

    public TrackSegment() {
      //  ArrayList<SwitchSectionParser.SwitchSection> switchSections = SwitchSectionParser.parseSwitchSections(BlockParser.parseCSV("src/main/resources/Framework/working_track_layout.csv"));
//        for(Lines line : Lines.values()) {
//            ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map = BlockParser.parseCSV("src/main/resources/Framework/working_track_layout.csv");
//            blocks = new ArrayList<>(map.get(line).values());
//            switchSections = SwitchSectionParser.parseSwitchSections(map.get(line));
//        }
    }




}
