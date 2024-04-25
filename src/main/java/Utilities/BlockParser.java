package Utilities;

import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static Utilities.Enums.BlockType.YARD;
import static Utilities.Enums.Direction.*;
import static Utilities.Records.BasicBlock.Connection;
import static Utilities.Records.BasicBlock.NextBlock;

class BlockParser {

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

                System.arraycopy(values, 0, correctedValues, 0, values.length);
                if(values.length != headers.length){
                    int diff = headers.length - values.length;
                    for(int j = values.length; j < diff; j++) {
                        correctedValues[j] = "";
                        }
                    }
                BasicBlock block = fromCsv(correctedValues, headers);
                map.computeIfAbsent(block.trackLine(), k -> new ConcurrentSkipListMap<>())
                        .put(block.blockNumber(), block);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> parseCSV(){
        return parseCSV("src/main/resources/Framework/red_yard_experiment.csv");
    }

    public static BasicBlock fromCsv(String[] values, String[] headers) {
        String trackLineString = values[indexOf(headers, "Line")];
        String section = values[indexOf(headers, "Section")];
        int blockNumber = Integer.parseInt(values[indexOf(headers, "Block Number")]);
        double blockLength = Double.parseDouble(values[indexOf(headers, "Block Length (m)")]);
        double blockGrade = Double.parseDouble(values[indexOf(headers, "Block Grade (%)")]);
        double speedLimit = Double.parseDouble(values[indexOf(headers, "Speed Limit (Km/Hr)")]);
        String infrastructure = values[indexOf(headers, "Infrastructure")];
        String doorString = values[indexOf(headers, "Door Direction")].toUpperCase();
        Optional<String> doorDirection = Optional.of(doorString);
        if(doorDirection.get().isEmpty()){
            doorDirection = Optional.empty();
        }
        double elevation = Double.parseDouble(values[indexOf(headers, "ELEVATION (M)")]);
        double cumulativeElevation = Double.parseDouble(values[indexOf(headers, "CUMUALTIVE ELEVATION (M)")]);
        boolean isUnderground = infrastructure.contains("UNDERGROUND");
        boolean isSwitch = infrastructure.contains("SWITCH");
        boolean isLight = infrastructure.contains("LIGHT");

        BasicBlock.NextBlock nextBlock = parseNextBlock(isSwitch, values[indexOf(headers, "North Bound")], values[indexOf(headers, "South Bound")]);

        BlockType blockType = parseBlockType(infrastructure);
        String stationName = parseStationName(infrastructure);

        Lines line = Lines.valueOf(trackLineString.toUpperCase());

        if(blockType == YARD){
            stationName = "YARD";
            blockLength = Constants.TRAIN_LENGTH * 4;
        }

        return new BasicBlock(line, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, isSwitch, isLight, blockType, Optional.ofNullable(stationName),
                doorDirection, nextBlock);
    }

    private static String parseStationName(String infrastructure) {
        if (infrastructure.contains("STATION")) {
            int startIndex = infrastructure.indexOf("STATION") + "STATION".length();
            int endIndex = infrastructure.indexOf(";", startIndex);
            if (endIndex != -1) {
                return infrastructure.substring(startIndex, endIndex).trim().replace(":", "");
            }
        }
        return null;
    }



    private static BlockType parseBlockType(String infrastructure) {
        if (infrastructure.contains("STATION")) {
            return BlockType.STATION;
        } else if (infrastructure.contains("CROSSING")) {
            return BlockType.CROSSING;
        } else if (infrastructure.contains("YARD")) {
            return YARD;
        } else {
            return BlockType.REGULAR;
        }
    }

    private static NextBlock parseNextBlock(boolean isSwitch, String northBoundString, String southBoundString) {
        Connection north = null;
        Connection south = null;
        Connection northDefault = null;
        Connection northAlternate = null;
        Connection southDefault = null;
        Connection southAlternate = null;
        Direction primarySwitchDirection = null;

        if (isSwitch) {

//            if(northBoundString.contains("/") && southBoundString.contains("/")){
//                String[] northParts = northBoundString.split("/");
//                String[] southParts = southBoundString.split("/");
//                northDefault = parseConnection(northParts[0]);
//                northAlternate = parseConnection(northParts[1]);
//                southDefault = parseConnection(southParts[0]);
//                southAlternate = parseConnection(southParts[1]);
//
//            } else
            if (northBoundString.contains("/")) {
                String[] northParts = northBoundString.split("/");
                northDefault = parseConnection(northParts[0]);
                northAlternate = parseConnection(northParts[1]);
                southDefault = parseConnection(southBoundString);
                southAlternate = new Connection(-1, false);

                primarySwitchDirection = NORTH;

            } else if (southBoundString.contains("/")) {
                String[] southParts = southBoundString.split("/");
                southDefault = parseConnection(southParts[0]);
                southAlternate = parseConnection(southParts[1]);
                northDefault = parseConnection(northBoundString);
                northAlternate = new Connection(-1, false);

                primarySwitchDirection = SOUTH;

            } else {
                north = parseConnection(northBoundString);
                south = parseConnection(southBoundString);

                primarySwitchDirection = NORTH;
            }

        }else {
            north = parseConnection(northBoundString);
            south = parseConnection(southBoundString);
        }

        return new NextBlock(north, south, northDefault, northAlternate, southDefault, southAlternate, primarySwitchDirection);
    }

    private static Connection parseConnection(String connectionString) {
        if (connectionString == null || connectionString.isEmpty() || connectionString.equals("~")) {
            return new Connection(-1, false);
        }

        boolean switchDirection = connectionString.endsWith("<->");
        int blockNumber = Integer.parseInt(connectionString.replace("<->", ""));

        return new Connection(blockNumber, switchDirection);
    }

    private static int indexOf(String[] headers, String header) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Header not found: " + header);
    }
}