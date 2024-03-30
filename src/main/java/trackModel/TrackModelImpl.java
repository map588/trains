package trackModel;


import Common.TrackModel;
import Utilities.BasicBlock;
import Utilities.Beacon;
import Utilities.BlockParser;
import Utilities.Enums.Lines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class TrackModelImpl implements TrackModel {
    public TrackModelImpl() {
        //this.trainAuthorities = new HashMap<>();
        //this.trainCommandSpeeds = new HashMap<>();
        this.beaconMap = new HashMap<>();
        this.switchStateMap = new HashMap<>();
        this.lightStateMap = new HashMap<>();
        this.blockOccupied = new HashMap<>();
        this.failureMap = new HashMap<>();
        this.stationMap = new HashMap<>();
        this.crossingMap = new HashMap<>();
        this.temperature = 0;
        this.lines = new ArrayList<>();
    }

    private  HashMap<Integer, Integer> trainAuthorities;
    private  HashMap<Integer, Double> trainCommandSpeeds;
    private final HashMap<Integer, Boolean> blockOccupied;
    private final HashMap<Integer, Boolean> switchStateMap;
    private final HashMap<Integer, Boolean> lightStateMap;
    private final HashMap<Integer, Boolean> crossingMap;

    private final HashMap<Integer, String> failureMap;
    private final HashMap<Integer, String> stationMap;

    private final HashMap<Integer, Beacon> beaconMap;
    //should lines just be a string?
    private ArrayList<String> lines = new ArrayList<>();
    private int temperature;

    public void setLine(String lines) {
        this.lines.add(lines);
    }

    public void setTrainAuthority(int trainID, int authority) {
        if (this.trainAuthorities.containsKey(trainID)) {
            this.trainAuthorities.replace(trainID, authority);
        } else {
            this.trainAuthorities.put(trainID, authority);
        }
    }

    public void setCommandedSpeed(int trainID, double commandedSpeed) {
        if (this.trainCommandSpeeds.containsKey(trainID)) {
            this.trainCommandSpeeds.replace(trainID, commandedSpeed);
        } else {
            this.trainCommandSpeeds.put(trainID, commandedSpeed);
        }
    }

    public void setFailure(int block, String failure) {
//        if (failure.equals("Broken Rail") || failure.equals("Track Circuit Failure") || failure.equals("Power Failure")) {
//            //failures.add(block);
//            this.trackInfo.get(block).setHasFailure(true);
//        }
//        if (failure.equals("Fix Track Failure")) {
//            //failures.remove(block);
//            this.trackInfo.get(block).setHasFailure(false);
//        }
        if (this.failureMap.containsKey(block)) {
            this.failureMap.replace(block, failure);
        } else {
            this.failureMap.put(block, failure);
        }
    }

    @Override
    public void setTemperature(int temp) {
        this.temperature = temp;
    }

    public void setSignalState(int block, boolean state) {
        if (this.lightStateMap.containsKey(block)) {
            this.lightStateMap.replace(block, state);
        } else {
            this.lightStateMap.put(block, state);
        }
    }

    public void setSwitchState(int block, boolean state) {
        if (this.switchStateMap.containsKey(block)) {
            this.switchStateMap.replace(block, state);
        } else {
            this.switchStateMap.put(block, state);
        }
    }

    public void setCrossing(int block, boolean state) {
        if (this.crossingMap.containsKey(block)) {
            this.crossingMap.replace(block, state);
        } else {
            this.crossingMap.put(block, state);
        }
    }

    @Override
    public void setBeacon(int block, Beacon beacon) {
        Beacon newBeacon = beacon;
        if (this.beaconMap.containsKey(block)) {
            this.beaconMap.replace(block, newBeacon);
        } else {
            this.beaconMap.put(block, newBeacon);
        }
    }


    public void setBlockOccupied(int block, boolean state) {
        if (this.blockOccupied.containsKey(block)) {
            this.blockOccupied.replace(block, state);
        } else {
            this.blockOccupied.put(block, state);
        }
    }

    public void setStation(int block, String station) {
        if (this.stationMap.containsKey(block)) {
            this.stationMap.replace(block, station);
        } else {
            this.stationMap.put(block, station);
        }
    }

    public int getTemperature() {
        return this.temperature;
    }

    public Beacon getBeacon(int block) {
        return this.beaconMap.get(block);
    }

    public int getAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public int getTrainAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public double getCommandedSpeed(int trainID) {
        return this.trainCommandSpeeds.get(trainID);
    }

    public boolean getBlockOccupied(int block) {
        return this.blockOccupied.get(block);
    }

    public boolean getCrossingState(int block) {
        return this.crossingMap.get(block);
    }

    public String getFailures(int block) {
        return this.failureMap.get(block);
    }

    public boolean getSignalState(int block) {
        if(this.lightStateMap.containsKey(block)){
            return this.lightStateMap.get(block);
        } else {
            return false;
        }
    }

    public boolean getSwitchState(int block) {
        if(this.switchStateMap.containsKey(block)){
            return this.switchStateMap.get(block);
        } else {
            return false;
        }
    }

    public String getStation(int block) {
        return this.stationMap.get(block);
    }
    public String getLine() {
        return this.lines.get(lines.size() - 1);
    }

    public int ticketSales(){
        return (int) Math.round(Math.random()) * 100;
    }

    public int passengers(int disembarked){
        return (int) Math.round(Math.random()) * 100;
    }


}
