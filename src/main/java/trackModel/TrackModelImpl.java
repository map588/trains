package trackModel;


import Common.TrackModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class TrackModelImpl implements TrackModel {
    public TrackModelImpl() {
        this.trainAuthorities = new HashMap<>();
        this.trainCommandSpeeds = new HashMap<>();
        this.beaconMap = new HashMap<>();
        this.switchStateMap = new HashMap<>();
        this.lightStateMap = new HashMap<>();
        this.blockOccupied = new HashMap<>();
        this.failureMap = new HashMap<>();
        this.stationMap = new HashMap<>();

        this.temperature = 0;

        this.lines = new ArrayList<>();
    }

    private HashMap<Integer, Integer> trainAuthorities;
    private HashMap<Integer, Integer> trainCommandSpeeds;
    private HashMap<Integer, Boolean> blockOccupied;
    private HashMap<Integer, Boolean> switchStateMap;
    private HashMap<Integer, Boolean> lightStateMap;
    private HashMap<Integer, String> failureMap;
    private HashMap<Integer, String> beaconMap;
    private HashMap<Integer, String> stationMap;
    private HashMap<Integer, Boolean> crossingMap;

    private ArrayList<String> lines = new ArrayList<>();
    private int temperature;

    private final List<TrackLayoutInfo> trackInfo = new ArrayList<>();

    public void addLine(String lines) {
        this.lines.add(lines);
    }

    public void setTrainAuthority(int trainID, int authority) {
        if (this.trainAuthorities.containsKey(trainID)) {
            this.trainAuthorities.replace(trainID, authority);
        } else {
            this.trainAuthorities.put(trainID, authority);
        }
    }

    public void setCommandedSpeed(int trainID, int commandedSpeed) {
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
        System.out.println("Setting Track Heaters: " + temp);
        for (TrackLayoutInfo trackProperties : trackInfo) {
            if (temp < 40) {
                trackProperties.trackHeaterProperty().set("STATUS - ON");
            } else {
                trackProperties.trackHeaterProperty().set("STATUS - OFF");
            }
        }
    }

    public void setLightState(int block, boolean state) {
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
        if (this.blockOccupied.containsKey(block)) {
            this.blockOccupied.replace(block, state);
        } else {
            this.blockOccupied.put(block, state);
        }
    }

    public void setBeacon(int block, String beacon) {
        if (this.beaconMap.containsKey(block)) {
            this.beaconMap.replace(block, beacon);
        } else {
            this.beaconMap.put(block, beacon);
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

    public String getBeacon(int block) {
        return this.beaconMap.get(block);
    }

    public int getAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public int getTrainAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public int getCommandedSpeed(int trainID) {
        return this.trainCommandSpeeds.get(trainID);
    }

    public boolean getBlockOccupied(int block) {
        return this.blockOccupied.get(block);
    }

    public boolean getCrossingState(int block) {
        return this.blockOccupied.get(block);
    }

    public String getFailures(int block) {
        return this.failureMap.get(block);
    }

    public boolean getLightState(int block) {
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
    public String getLine(int block) {
        return this.lines.get(block);
    }

    public int ticketSales(){
        return (int) Math.round(Math.random());
    }

    public int passengers(int disembarked){
        return (int) Math.round(Math.random());
    }




}



//    public List<TrackLayoutInfo> getTrackInfo() {
//
//        trackInfo.clear();
//
//        for(int i = 0; i <= 15; i++){
//            TrackLayoutInfo block = new TrackLayoutInfo();
//            block.setHasFailure(failures.contains(i));
//            block.setIsOccupied(blockOccupied.contains(i));
//            block.setBlockNumber("" + i);
//
//            if(i < 6){
//                block.setSection("A");
//            }
//            else if(i < 11){
//                block.setSection("B");
//            }
//            else{
//                block.setSection("C");
//            }
//
//            block.setBlockLength(50);
//            block.setBlockGrade(0);
//            block.setSpeedLimit(50);
//            block.setIsCrossing(i == 3);
//
//            if(i == 3){
//                block.setIsCrossing(true);
//                block.setCrossingState("TRUE");
//            }
//
//            block.setIsSignal(i == 6 || i == 11);
//            block.setIsSwitch(i == 5 || i == 6 || i == 11);
//            block.setIsUnderground(false);
//            block.setIsStation(i == 10 || i == 15);
//            block.setIsBeacon(i == 9 || i == 14);
//            if(i == 5){
//                block.setSwitchMain("6");
//                block.setSwitchAlt("11");
//                block.setSwitchBlockID("5");
//            }
//
//            if(i == 9){
//                beacons.add(9);
//            }
//
//            if (i == 14){
//                beacons.add(14);
//            }
//
//            if(i == 6){
//                block.setSignalID("6");
//            }
//
//            if(i == 11){
//                block.setSignalID("11");
//            }
//
//            if(i == 10) {
//                block.setNameOfStation("Station B");
//            }
//            if(i == 15) {
//                block.setNameOfStation("Station C");
//            }
//
//
//
//            this.trackInfo.add(block);
//        }
//
//        return this.trackInfo;
//    }
//}
