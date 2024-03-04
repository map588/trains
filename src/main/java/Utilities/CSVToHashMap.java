package Utilities;

import Utilities.ParsedBlock;
import Utilities.ParsedBlock.DoorSide;
import Utilities.ParsedBlock.BlockType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CSVToHashMap {

    public static HashMap<String, ArrayDeque<ParsedBlock>> parseCSV(String filePath) {
        HashMap<String, ArrayDeque<ParsedBlock>> map = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            // Extracting header to determine column indices
            String[] headers = lines.get(0).split(",");
            int lineIndex = indexOf(headers, "Line");
            int sectionIndex = indexOf(headers, "Section");
            int blockNumberIndex = indexOf(headers, "Block Number");
            int blockLengthIndex = indexOf(headers, "Block Length (m)");
            int blockGradeIndex = indexOf(headers, "Block Grade (%)");
            int speedLimitIndex = indexOf(headers, "Speed Limit (Km/Hr)");
            int infrastructureIndex = indexOf(headers, "Infrastructure");
            int elevationIndex = indexOf(headers, "ELEVATION (M)");
            int cumulativeElevationIndex = indexOf(headers, "CUMALTIVE ELEVATION (M)");

            // Processing each row starting from index 1 to skip header
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",");
                String trackLine = values[lineIndex];
                char section = values[sectionIndex].charAt(0);
                int blockNumber = Integer.parseInt(values[blockNumberIndex]);
                int blockLength = (int) Double.parseDouble(values[blockLengthIndex]);
                double blockGrade = Double.parseDouble(values[blockGradeIndex]);
                int speedLimit = Integer.parseInt(values[speedLimitIndex]);
                double elevation = Double.parseDouble(values[elevationIndex]);
                double cumulativeElevation = Double.parseDouble(values[cumulativeElevationIndex]);
                boolean isUnderground = values[infrastructureIndex].toLowerCase().contains("underground");

                // Parse the Infrastructure column to determine the block type and additional data
                String infrastructure = values[infrastructureIndex];
                ParsedBlock blockInfo = parseInfrastructure(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground, infrastructure);

                map.computeIfAbsent(trackLine, k -> new ArrayDeque<>()).add(blockInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static ParsedBlock parseInfrastructure(String trackLine, char section, int blockNumber, int blockLength,
                                                      double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                                      boolean isUnderground, String infrastructure) {
        // Default values for prevBlock and nextBlock as an example
        int prevBlock = -1; // These should be determined based on actual logic
        int nextBlock = -1; // These should be determined based on actual logic

        if (infrastructure == null || infrastructure.isEmpty()) {
            return ParsedBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock);
        } else if (infrastructure.contains("RAILWAY CROSSING")) {
            return ParsedBlock.ofCrossing(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock);
        } else if (infrastructure.contains("Switch")) {
            // Determine if it's SWITCH_IN or SWITCH_OUT based on specific logic
            // This is simplified; actual parsing of the infrastructure string is needed to extract switch details
            return ParsedBlock.ofSwitchIn(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock, -1); // Example, adjust alternativeSwitchBlock accordingly
        } else if (infrastructure.contains("STATION;")) {
            String stationName = infrastructure.split(";")[1].trim();
            return ParsedBlock.ofStation(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock, stationName, DoorSide.BOTH); // Example, adjust doorSide accordingly
        }
        // Default to REGULAR if no specific type is matched
        return ParsedBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                blockGrade, speedLimit, elevation, cumulativeElevation,
                isUnderground, prevBlock, nextBlock);
    }

    private static int indexOf(String[] headers, String header) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                return i;
            }
        }
        return -1; // Header not found
    }

    public static void main(String[] args) {
        String filePath = "path/to/track_layout.csv";
        HashMap<String, ArrayDeque<ParsedBlock>> trackLines = parseCSV(filePath);
        // Further processing...
    }
}

