package Utilities;

import java.util.Optional;

public record BasicBlock(
        String trackLine,
        String section,
        int blockNumber,
        double blockLength,
        double blockGrade,
        double speedLimit,
        double elevation,
        double cumulativeElevation,
        boolean isUnderground,
        boolean isSwitch,
        BlockType blockType,
        Optional<String> stationName,
        Optional<String> doorDirection,
        NextBlock nextBlock
) {
    public enum BlockType {
        REGULAR,
        STATION,
        CROSSING,
        YARD
    }

    public static BasicBlock fromCsv(String[] values, String[] headers) {
        String trackLine = values[indexOf(headers, "Line")];
        String section = values[indexOf(headers, "Section")];
        int blockNumber = Integer.parseInt(values[indexOf(headers, "Block Number")]);
        double blockLength = Double.parseDouble(values[indexOf(headers, "Block Length (m)")]);
        double blockGrade = Double.parseDouble(values[indexOf(headers, "Block Grade (%)")]);
        double speedLimit = Double.parseDouble(values[indexOf(headers, "Speed Limit (Km/Hr)")]);
        String infrastructure = values[indexOf(headers, "Infrastructure")];
        Optional<String> doorDirection = Optional.ofNullable(values[indexOf(headers, "Door Direction")]);
        double elevation = Double.parseDouble(values[indexOf(headers, "ELEVATION (M)")]);
        double cumulativeElevation = Double.parseDouble(values[indexOf(headers, "CUMALTIVE ELEVATION (M)")]);

        boolean isUnderground = infrastructure.contains("UNDERGROUND");
        boolean isSwitch = infrastructure.contains("SWITCH");

        NextBlock nextBlock = parseNextBlock(isSwitch, values[indexOf(headers, "North Bound")], values[indexOf(headers, "South Bound")]);

        BlockType blockType = parseBlockType(infrastructure);
        String stationName = parseStationName(infrastructure);

        return new BasicBlock(trackLine, section, blockNumber, blockLength, blockGrade, speedLimit,
                elevation, cumulativeElevation, isUnderground, isSwitch, blockType, Optional.ofNullable(stationName),
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

    private static int indexOf(String[] headers, String header) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Header not found: " + header);
    }

    private static BlockType parseBlockType(String infrastructure) {
        if (infrastructure.contains("STATION")) {
            return BlockType.STATION;
        } else if (infrastructure.contains("CROSSING")) {
            return BlockType.CROSSING;
        } else if (infrastructure.contains("YARD")) {
            return BlockType.YARD;
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

        if (isSwitch) {

            if(northBoundString.contains("/") && southBoundString.contains("/")){
                String[] northParts = northBoundString.split("/");
                String[] southParts = southBoundString.split("/");
                northDefault = parseConnection(northParts[0]);
                northAlternate = parseConnection(northParts[1]);
                southDefault = parseConnection(southParts[0]);
                southAlternate = parseConnection(southParts[1]);

            } else if (northBoundString.contains("/")) {
                String[] northParts = northBoundString.split("/");
                northDefault = parseConnection(northParts[0]);
                northAlternate = parseConnection(northParts[1]);
                southDefault = parseConnection(southBoundString);
                southAlternate = new Connection(-1, false);

            } else if (southBoundString.contains("/")) {
                String[] southParts = southBoundString.split("/");
                southDefault = parseConnection(southParts[0]);
                southAlternate = parseConnection(southParts[1]);
                northDefault = parseConnection(northBoundString);
                northAlternate = new Connection(-1, false);

            } else {
                north = parseConnection(northBoundString);
                south = parseConnection(southBoundString);
            }

        }else {
            north = parseConnection(northBoundString);
            south = parseConnection(southBoundString);
        }

        return new NextBlock(north, south, northDefault, northAlternate, southDefault, southAlternate);
    }

    private static Connection parseConnection(String connectionString) {
        if (connectionString == null || connectionString.isEmpty() || connectionString.equals("~")) {
            return new Connection(-1, false);
        }

        boolean switchDirection = connectionString.endsWith("<->");
        int blockNumber = Integer.parseInt(connectionString.replace("<->", ""));

        return new Connection(blockNumber, switchDirection);
    }

    public record NextBlock(
            Connection north,
            Connection south,
            Connection northDefault,
            Connection northAlternate,
            Connection southDefault,
            Connection southAlternate
    ) {
    }

    public record Connection(
            int blockNumber,
            boolean flipDirection
    ) {
    }
}