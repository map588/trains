package Utilities;

import Framework.Support.ObservableHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVTokenizer {

    public final static ObservableHashMap<String, ArrayList<BasicBlockInfo>> blockList = new ObservableHashMap<>();
    public final static ArrayList<String> lineNames = new ArrayList<>();

    private static String CSVFileName;

    public void setCSVFile(String fileName) {
        CSVFileName = fileName;
    }
    public String getCSVFile(){
        return CSVFileName;
    }

    public static void parseCSVToTrueBlockInfo(String lineName) {
        lineNames.add(lineName);
        try (BufferedReader br = new BufferedReader(new FileReader(CSVFileName))) {
            blockList.put(lineName, new ArrayList<>() {
                {
                    add(new BasicBlockInfo(
                        lineName,
                        'A',
                        1,
                        100,
                        0.0,
                        1000,
                        0.0,
                        0.0,
                        false,
                        false,
                        "Yard",
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        0,
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        false)
                    );
                }});

            String line;
                        br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                blockList.get(lineName).add(new BasicBlockInfo(
                                lineName,
                                values[1].charAt(0),
                                Integer.parseInt(values[2]),
                                Integer.parseInt(values[3]),
                                Double.parseDouble(values[4]),
                                Integer.parseInt(values[5]),
                                Double.parseDouble(values[6]),
                                Double.parseDouble(values[7]),
                                Boolean.parseBoolean(values[8]),
                                Boolean.parseBoolean(values[9]),
                                values[10],
                                Boolean.parseBoolean(values[11]),
                                Boolean.parseBoolean(values[12]),
                                Boolean.parseBoolean(values[13]),
                                Boolean.parseBoolean(values[14]),
                                Boolean.parseBoolean(values[15]),
                                Boolean.parseBoolean(values[16]),
                        Integer.parseInt(values[22]),
                        Integer.parseInt(values[23]),
                        Integer.parseInt(values[24]),
                                Boolean.parseBoolean(values[17]),
                                Boolean.parseBoolean(values[18]),
                                Boolean.parseBoolean(values[19]),
                                Boolean.parseBoolean(values[20]),
                                Boolean.parseBoolean(values[21])
                                ));
                }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}