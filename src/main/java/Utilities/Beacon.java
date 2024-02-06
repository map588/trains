package Utilities;


import java.util.ArrayList;

public class Beacon {
    private String beaconID;
    private String stationName;
    private int possibleRoutes;
    private Block[] blocks;

    //Dumb and incomplete constructor that needs to just parse the csv instead of taking in a bunch of parameters
    public Beacon(String beaconID, String stationName, Block[] blocks) {
        this.possibleRoutes = 1;
        this.beaconID = beaconID;
        this.stationName = stationName;
        int lastSwitch = 0;
        ArrayList<Block> tempBlocks = new ArrayList<Block>();
        for (Block block : blocks) {
            if (block.isSwitch()) {
                lastSwitch = block.getBlockNumber();
            }
            tempBlocks.add(block);
        }
    }
}