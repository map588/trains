package Utilities;

import Utilities.Enums.Lines;
import Utilities.BasicBlock.DoorSide;

import java.io.IOException;
import java.lang.Double;
import java.lang.Integer;
import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static Utilities.BasicBlock.DoorSide.*;
import static Utilities.BasicBlock.Direction.*;
import static Utilities.BasicBlock.NodeConnection;

public class BlockParser {


    private BlockParser() {
        throw new IllegalStateException("Utility class");
    }


    public static ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> parseCSV(String filePath) {
        ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> map = new ConcurrentHashMap<>();

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
                Lines line = Lines.valueOf(values[lineIndex].toUpperCase().strip());
                // Parse the Infrastructure column to determine the block type and additional data
                String doorSide = values[doorDirectionIndex].toUpperCase().strip();
                DoorSide doorDirection;
                doorSide = doorSide.toUpperCase().strip();

                if(doorSide.equals("LEFT/RIGHT")) {
                    doorSide = "BOTH";
                }

                if(!doorSide.isEmpty()) {
                    doorDirection = DoorSide.valueOf(doorSide);
                } else{
                    doorDirection = BOTH;
                }

                String infrastructure = values[infrastructureIndex];

                BasicBlock blockInfo = parseInfrastructure(trackLine, section, blockNumber, blockLength,
                        blockGrade, speedLimit, elevation, cumulativeElevation,
                        isUnderground, infrastructure, doorDirection);

                map.computeIfAbsent(line, k -> new ArrayDeque<>()).add(blockInfo);
            }
        } catch (IOException e){
                throw new RuntimeException(e);
            }

        return map;
    }

    public static ConcurrentHashMap<Lines, ArrayDeque<BasicBlock>> parseCSV(){
        return parseCSV("src/main/resources/Framework/experimental_track_layout.csv");
    }

    private static BasicBlock parseInfrastructure(String trackLine, char section, int blockNumber, int blockLength,
                                                  double blockGrade, int speedLimit, double elevation, double cumulativeElevation,
                                                  boolean isUnderground, String infrastructure, DoorSide doorSide) {
        if (infrastructure.contains("SWITCH")) {
            NodeConnection nodeConnection = parseSwitch(infrastructure, blockNumber);
            return BasicBlock.ofSwitch(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, Optional.empty(), Optional.empty(),
                    nodeConnection.parentID(), nodeConnection.parentDirection(),
                    nodeConnection.defChildID(), nodeConnection.defDirection(),
                    nodeConnection.altChildID(), nodeConnection.altDirection());
        } else if (infrastructure.contains("STATION")) {
            String[] stationInfo = infrastructure.split(";");
            String stationName = stationInfo[0].split(":")[1].trim();
            NodeConnection nodeConnection = parseStation(stationInfo[1].trim());
            return BasicBlock.ofStation(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground, stationName, doorSide,
                    nodeConnection.parentID(), nodeConnection.parentDirection(),
                    nodeConnection.defChildID(), nodeConnection.defDirection());
        } else if (infrastructure.contains("RAILWAY CROSSING")) {
            return BasicBlock.ofCrossing(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        } else if (infrastructure.contains("YARD")) {
            return BasicBlock.ofYard(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        } else {
            return BasicBlock.ofRegular(trackLine, section, blockNumber, blockLength,
                    blockGrade, speedLimit, elevation, cumulativeElevation,
                    isUnderground);
        }
    }

    private static NodeConnection parseStation(String connectionInfo) {
        String regex = "\\(\\s*(\\d+)\\s*(<->|->|<-)\\s*(\\d+)\\s*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(connectionInfo);

        if (matcher.find()) {
            int parentID = Integer.parseInt(matcher.group(1));
            BasicBlock.Direction parentDirection = parseDirection(matcher.group(2));
            int defChildID = Integer.parseInt(matcher.group(3));
            BasicBlock.Direction defDirection;
                if(parentDirection == TO_NODE) {
                    defDirection = FROM_NODE;
                } else if(parentDirection == FROM_NODE) {
                    defDirection = TO_NODE;
                } else {
                    defDirection = BIDIRECTIONAL;
                }
            

            return new NodeConnection(parentID, parentDirection, defChildID, defDirection, Optional.empty(), Optional.empty());
        } else {
            throw new IllegalArgumentException("Invalid station format: " + connectionInfo);
        }
    }

    private static NodeConnection parseSwitch(String infrastructure, int blockNumber) {
        String regex = "SWITCH\\s*\\((\\d+)\\s*(<->|->|<-)\\s*(\\d+)\\s*;\\s*(\\d+)\\s*(<->|->|<-)\\s*(\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(infrastructure);

        if (matcher.find()) {
            int defChildID1 = Integer.parseInt(matcher.group(1));
            String defDirection1 = matcher.group(2);
            int defChildID2 = Integer.parseInt(matcher.group(3));
            int altChildID1 = Integer.parseInt(matcher.group(4));
            String altDirection1 = matcher.group(5);
            int altChildID2 = Integer.parseInt(matcher.group(6));

            BasicBlock.Direction defDirection;
            BasicBlock.Direction altDirection;

            if (defChildID1 == blockNumber) {
                defDirection = parseDirection(defDirection1);
            } else {
                defDirection = parseDirection(reverseDirection(defDirection1));
            }

            if (altChildID1 == blockNumber) {
                altDirection = parseDirection(altDirection1);
            } else {
                altDirection = parseDirection(reverseDirection(altDirection1));
            }

            return new NodeConnection(0, BasicBlock.Direction.TO_NODE, defChildID2, defDirection, Optional.of(altChildID2), Optional.of(altDirection));
        } else {
            throw new IllegalArgumentException("Invalid switch format: " + infrastructure);
        }
    }

    private static String reverseDirection(String arrow) {
        return switch (arrow) {
            case "->" -> "<-";
            case "<-" -> "->";
            default -> arrow;
        };
    }

    private static BasicBlock.Direction parseDirection(String arrow) {
        return switch (arrow) {
            case "->" -> TO_NODE;
            case "<-" -> FROM_NODE;
            case "<->" -> BIDIRECTIONAL;
            default -> throw new IllegalArgumentException("Invalid arrow: " + arrow);
        };
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

