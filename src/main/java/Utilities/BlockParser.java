package Utilities;

import Utilities.Enums.Lines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentHashMap;

public class BlockParser {

    private BlockParser() {
        throw new IllegalStateException("Utility class");
    }

    public static ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> parseCSV(String filePath) {
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> map = new ConcurrentHashMap<>();

        String line = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String[] headers = lines.get(0).split(",");

            for (int i = 1; i < lines.size(); i++) {
                //initialize values array to size of headers, fill with values from csv
                String[] correctedValues = new String[headers.length];
                String[] values = lines.get(i).split(",");
                line = lines.get(i);
                //if the values array is not the same size as the headers array, fill in the missing values with empty strings

                for(int j = 0; j < values.length; j++) {
                    correctedValues[j] = values[j];
                }
                if(values.length != headers.length){
                    int diff = headers.length - values.length;
                    for(int j = values.length; j < diff; j++) {
                        correctedValues[j] = "";
                        }
                    }
                BasicBlock block = BasicBlock.fromCsv(correctedValues, headers);
                map.computeIfAbsent(Lines.valueOf(block.trackLine().toUpperCase()), k -> new ConcurrentSkipListMap<>())
                        .put(block.blockNumber(), block);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> parseCSV(){
        return parseCSV("src/main/resources/Framework/working_track_layout.csv");
    }
}