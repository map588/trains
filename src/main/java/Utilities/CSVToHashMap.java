package Utilities;

import Utilities.Enums.Line;
import Utilities.ParsedBlock.Direction;
import Utilities.ParsedBlock.DoorSide;

import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
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
            int doorDirectionIndex = indexOf(headers, "Door Direction");
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
                Line line = Line.valueOf(values[lineIndex].toUpperCase().strip());
                // Parse the Infrastructure column to determine the block type and additional data
                String infrastructure = values[infrastructureIndex];
                String doorSide = values[doorDirectionIndex];
                if(doorSide.contains("/")) {doorSide = doorSide.replace("/", "_");}
                doorSide = doorSide.toUpperCase().strip();

                DoorSide doorDirection;
                switch(doorSide) {
                    case "LEFT" -> doorDirection = DoorSide.LEFT;
                    case "RIGHT" -> doorDirection = DoorSide.RIGHT;
                    case "LEFT_RIGHT" -> doorDirection = DoorSide.BOTH;
                    default -> doorDirection = DoorSide.BOTH;
                }

                ParsedBlock blockInfo = parseInfrastructure(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground, infrastructure, doorDirection);

                map.computeIfAbsent(line, k -> new ArrayDeque<>()).add(blockInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static ParsedBlock parseInfrastructure(String trackLine, char section, int blockNumber, int blockLength,
                                                   double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                                   boolean isUnderground, String infrastructure, DoorSide doorSide) {
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
            boolean directionality = parseSwitch(infrastructure);


        } else if (infrastructure.contains("STATION;")) {
            //TODO: This does not work in all cases
            String stationName = infrastructure.split(";")[1].trim();
            return ParsedBlock.ofStation(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock, stationName, doorSide); // Example, adjust doorSide accordingly
        } else if (infrastructure.contains("YARD")) {

            return ParsedBlock.ofYard(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, prevBlock, nextBlock);
        }
        return ParsedBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                blockGrade, speedLimit, elevation, cumulativeElevation,
                isUnderground, prevBlock, nextBlock);
    }

    private static boolean parseSwitch(String infrastructure) {
        String regex = "\\((-?\\d+)-(-?\\d+);\\s*(-?\\d+)-(-?\\d+)\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(infrastructure);

        if (matcher.find()) {
            int A1 = Integer.parseInt(matcher.group(1));
            int A2 = Integer.parseInt(matcher.group(2));
            int B1 = Integer.parseInt(matcher.group(3));
            int B2 = Integer.parseInt(matcher.group(4));
            return new int[]{A1, A2, B1, B2};




        } else {
            System.out.println("No match found");
        }
        return new int[]{-1, -1, -1, -1};
    }

    private static void trackSwitchRefs(ParsedBlock block, int[] switchIDs) {
        block.setSwitchIDs(switchIDs);
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

