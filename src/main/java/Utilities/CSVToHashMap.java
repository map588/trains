package Utilities;

import Utilities.Enums.Line;
import Utilities.ParsedBlock.Direction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CSVToHashMap {

    public static HashMap<Line, ArrayDeque<ParsedBlock>> parseCSV(String filePath) {
        HashMap<Line, ArrayDeque<ParsedBlock>> map = new HashMap<>();

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
            int directionIndex = indexOf(headers, "Direction");
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
                Direction direction = Direction.valueOf(values[directionIndex].toUpperCase().strip());
                Line line = Line.valueOf(values[lineIndex].toUpperCase().strip());

                // Parse the Infrastructure column to determine the block type and additional data
                String infrastructure = values[infrastructureIndex];

                ParsedBlock blockInfo = parseInfrastructure(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground, infrastructure);

                map.computeIfAbsent(line, k -> new ArrayDeque<>()).add(blockInfo);
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
        } else if (infrastructure.contains("SWITCH")) {
            // Determine if it's SWITCH_IN or SWITCH_OUT based on specific logic
            String regex = "\\((-?\\d+)-(-?\\d+);\\s*(-?\\d+)-(-?\\d+)\\)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(infrastructure);

            if (matcher.find()) {
                int A1 = Integer.parseInt(matcher.group(1));
                int A2 = Integer.parseInt(matcher.group(2));
                int B1 = Integer.parseInt(matcher.group(3));
                int B2 = Integer.parseInt(matcher.group(4));
            }else{
                System.out.println("No match found");
            }
        } else if (infrastructure.contains("STATION;")) {
            //TODO: This does not work in all cases
            String stationName = infrastructure.split(";")[1].trim();
            return ParsedBlock.ofStation(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock, stationName, Direction.BOTH); // Example, adjust doorSide accordingly
        }else if(infrastructure.contains("YARD")){

            return ParsedBlock.ofYard(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock);
        }
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

}

