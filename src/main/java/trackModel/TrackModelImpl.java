package trackModel;


import Common.TrackModel;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


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
        this.crossingMap = new HashMap<>();

        this.temperature = 0;

        this.blockInfo = new List<TrackLayoutInfo>() {
              @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(Object o) {
                    return false;
                }

                @Override
                public Iterator<TrackLayoutInfo> iterator() {
                    return null;
                }

                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public <T> T[] toArray(T[] a) {
                    return null;
                }

                @Override
                public boolean add(TrackLayoutInfo trackLayoutInfo) {
                    return false;
                }

                @Override
                public boolean remove(Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(Collection<? extends TrackLayoutInfo> c) {
                    return false;
                }

                @Override
                public boolean addAll(int index, Collection<? extends TrackLayoutInfo> c) {
                    return false;
                }

                @Override
                public boolean removeAll(Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }

                @Override
                public TrackLayoutInfo get(int index) {
                    return null;
                }

                @Override
                public TrackLayoutInfo set(int index, TrackLayoutInfo element) {
                    return null;
                }

                @Override
                public void add(int index, TrackLayoutInfo element) {

                }

                @Override
                public TrackLayoutInfo remove(int index) {
                    return null;
                }

                @Override
                public int indexOf(Object o) {
                    return 0;
                }

                @Override
                public int lastIndexOf(Object o) {
                    return 0;
                }

                @Override
                public ListIterator<TrackLayoutInfo> listIterator() {
                    return null;
                }

                @Override
                public ListIterator<TrackLayoutInfo> listIterator(int index) {
                    return null;
                }

                @Override
                public List<TrackLayoutInfo> subList(int fromIndex, int toIndex) {
                    return null;
                }
        };
        this.lines = new ArrayList<>();
    }

    private HashMap<Integer, Integer> trainAuthorities;
    private HashMap<Integer, Double> trainCommandSpeeds;
    private HashMap<Integer, Boolean> blockOccupied;
    private HashMap<Integer, Boolean> switchStateMap;
    private HashMap<Integer, Boolean> lightStateMap;
    private HashMap<Integer, String> failureMap;
    private HashMap<Integer, String> beaconMap;
    private HashMap<Integer, String> stationMap;
    private HashMap<Integer, Boolean> crossingMap;

    //should lines just be a string?
    private ArrayList<String> lines = new ArrayList<>();
    private final int temperature;

    private List<TrackLayoutInfo> blockInfo = new ArrayList<>();


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
        System.out.println("Setting Track Heaters: " + temp);
        for (TrackLayoutInfo trackProperties : blockInfo) {
            if (temp < 40) {
                trackProperties.trackHeaterProperty().set("STATUS - ON");
            } else {
                trackProperties.trackHeaterProperty().set("STATUS - OFF");
            }
        }
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
        return (int) Math.round(Math.random());
    }

    public int passengers(int disembarked){
        return (int) Math.round(Math.random());
    }

    public void csvParser(String file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line = "";
            String lineName = "";
            String infrastructure = "";
            this.setLine(br.readLine());
            while((line = br.readLine()) != null){
                String[] values = line.split(",");
                TrackLayoutInfo block = new TrackLayoutInfo();
                lineName = values[0];
                block.setSection(values[1]);
                block.setBlockNumber(values[2]);
                block.setBlockLength(Integer.parseInt(values[3]));
                block.setBlockGrade(Integer.parseInt(values[4]));
                block.setSpeedLimit(Integer.parseInt(values[5]));

                //interpret the infrastructure
                infrastructure = values[6];

                block.setIsCrossing(infrastructure.equals("RAILWAY CROSSING"));

                if(infrastructure.contains("STATION")){
                    block.setIsStation(true);
                    block.setNameOfStation(infrastructure);
                }
                else{
                    block.setIsStation(false);
                    block.setNameOfStation("No Station Present");
                }

                if(infrastructure.contains("SWITCH")){
                    block.setIsSwitch(true);
                    block.setSwitchBlockID(infrastructure);
                }
                else{
                    block.setIsSwitch(false);
                    block.setSwitchBlockID("No Switch Present");
                }

                block.setIsUnderground(infrastructure.contains("UNDERGROUND"));

                this.blockInfo.add(block);

                //figure out what to do with station side, elevation
                //and traversal time back to yard for green line
            }

            this.setLine(lineName);


        }
        catch (IOException e){
            e.printStackTrace();
        }

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
