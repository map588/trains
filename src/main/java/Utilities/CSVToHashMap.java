package Utilities;

import Utilities.Enums.Line;
import Utilities.ParsedBlock.Direction;
import Utilities.ParsedBlock.DoorSide;

import java.io.IOException;
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

import static Utilities.ParsedBlock.DoorSide.*;

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
                String doorSide = values[doorDirectionIndex].toUpperCase().strip();

                DoorSide doorDirection;

                doorSide = doorSide.toUpperCase().strip();

                if(!doorSide.isEmpty()) {
                    doorDirection = DoorSide.valueOf(doorSide);
                } else {
                    doorDirection = BOTH;
                }

                ParsedBlock blockInfo = parseInfrastructure(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground, infrastructure, doorDirection);

                map.computeIfAbsent(line, k -> new ArrayDeque<>()).add(blockInfo);
            }
        } catch (IOException e){
                throw new RuntimeException(e);
            }

        return map;
    }

        private static ParsedBlock parseInfrastructure(String trackLine, char section, int blockNumber, int blockLength,
                                                   double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                                   boolean isUnderground, String infrastructure, DoorSide doorSide) {

        if (infrastructure == null || infrastructure.isEmpty()) {
            return ParsedBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        } else if (infrastructure.contains("RAILWAY CROSSING")) {
            return ParsedBlock.ofCrossing(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        } else if (infrastructure.contains("SWITCH")) {
            Matcher matcher = parseSwitchMatcher(infrastructure);
            if (matcher.find()) {
                int switchBlock1 = Integer.parseInt(matcher.group(1));
                ParsedBlock.Direction switchDirection1 = parseDirection(matcher.group(2));
                int switchBlock2 = Integer.parseInt(matcher.group(3));
                int switchBlock3 = Integer.parseInt(matcher.group(4));
                ParsedBlock.Direction switchDirection2 = parseDirection(matcher.group(5));
                int switchBlock4 = Integer.parseInt(matcher.group(6));

                return ParsedBlock.ofSwitch(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground,
                        switchBlock1, switchDirection1, switchBlock3, switchDirection2);

            } else {
                throw new IllegalArgumentException("Invalid switch format: " + infrastructure);
            }
        } else if (infrastructure.contains("STATION;")) {
            //TODO: This does not work in all cases
            String stationName = infrastructure.split(";")[1].trim();
            return ParsedBlock.ofStation(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, stationName, doorSide); // Example, adjust doorSide accordingly
        } else if (infrastructure.contains("YARD")) {

            return ParsedBlock.ofYard(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        }
        return ParsedBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                blockGrade, speedLimit, elevation, cumulativeElevation,
                isUnderground);
    }


    private static int indexOf(String[] headers, String header) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                return i;
            }
        }
        return -1; // Header not found
    }

    private static Matcher parseSwitchMatcher(String infrastructure) {
        String regex = "SWITCH\\s*\\((\\d+)(->|<->)(\\d+);\\s*(\\d+)(->|<->)(\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(infrastructure);
    }

    private static ParsedBlock.Direction parseDirection(String arrow) {
        return switch (arrow) {
            case "->" -> ParsedBlock.Direction.OUT;
            case "<-" -> ParsedBlock.Direction.IN;
            case "<->" -> ParsedBlock.Direction.BIDIRECTIONAL;
            default -> throw new IllegalArgumentException("Invalid arrow: " + arrow);
        };
    }

}

