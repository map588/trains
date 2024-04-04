package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlockLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Records.Beacon;
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.concurrent.*;

import static Utilities.Constants.MAX_PASSENGERS;

public class TrackLine implements TrackModel {

    Lines line;
    
    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();


    //calls all listeners when a train enters or exits a block
    private final ConcurrentHashMap<TrainModel, Integer> trackOccupancyMap;

    //maps blocks to block numbers
    private final TrackBlockLine trackBlocks;
    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks;
    private int ticketSales = 0;
    private TrackLineSubject subject;
    private BeaconParser beaconParser;
    public int outsideTemperature = 40;
    private WaysideSystem waysideSystem;

    public TrackLine(Lines line, BasicBlockLine basicTrackLayout) {

        trackBlocks = new TrackBlockLine();
        beaconBlocks = new ConcurrentHashMap<>();
        trackOccupancyMap = new ObservableHashMap<>();

        ArrayList<Integer> blockIndices = new ArrayList<>(basicTrackLayout.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicTrackLayout.get(blockIndex));
            trackBlocks.put(block.blockID, block);
        }

        this.subject = new TrackLineSubject(this, trackBlocks);

        //TODO: Add beacons to the beaconBlocks map

        this.line = line;
    }

    public TrackLineSubject getSubject() {
        return this.subject;
    }

    public TrainModel trainDispatch(int trainID) {
        System.out.println("Train: " + trainID + " dispatched");
        TrainModel train = new TrainModelImpl(this, trainID);
        asyncTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
        });
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        asyncTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
        });
    }

    public void setWaysideSystem(WaysideSystem waysideSystem) {
        this.waysideSystem = waysideSystem;
    }

    //////////////////////////

    private void handleTrainEntry(TrainModel train, Integer blockID) {
        if(blockID == 0){
            train.delete();
            return;
        }
        TrackBlock block = trackBlocks.get(blockID);
        if (block.isOccupied()) {
            throw new IllegalArgumentException("Block: " + blockID + " is already occupied");
        }
        block.setOccupied(true);
        block.occupiedBy = train;
        WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, true);
    }


    private void handleTrainExit(TrainModel train, Integer blockID) {
        TrackBlock block = trackBlocks.get(blockID);
        if (!block.isOccupied()) {
            throw new IllegalArgumentException("Block: " + blockID + " is not occupied");
        }
        block.setOccupied(false);
        block.occupiedBy = null;
        WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, false);
    }

    ///////////////////////////


    private final ConcurrentLinkedQueue<Callable<Void>> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    // Used to add a task to the work queue
    private void asyncTrackUpdate(Runnable task) {
        trackUpdateExecutor.submit(task);
    }

    public void update() throws InterruptedException {

    }

    /**
     * Updates the location of a train on the track
     * @param train
     * @return the block the train is moving to
     */
    public TrackBlock updateTrainLocation(TrainModel train) {
            Integer currentBlockID = trackOccupancyMap.getOrDefault(train, -1);
            if (currentBlockID == -1) {
                throw new IllegalArgumentException("Train: " + train.getTrainNumber() + " is not on the track");
            }
            Connection next = trackBlocks.get(currentBlockID).getNextBlock(train.getDirection());
            if (next.directionChange()) {
                train.changeDirection();
            }
            Integer nextBlockID = next.blockNumber();

            System.out.println("Train: " + train.getTrainNumber() + " ->  " + nextBlockID);
            trackOccupancyMap.replace(train, currentBlockID, next.blockNumber());

            asyncTrackUpdate(() -> {
                handleTrainExit(train, currentBlockID);
                handleTrainEntry(train, nextBlockID);
            });


            return trackBlocks.get(nextBlockID);
    }



    //------------------From interface------------------

    //TODO: We don't have light states figured out yet
    //light blocks will be wherever there is a beacon
    //false red, true is green
    @Override
    public void setLightState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if(beaconBlocks.contains(block)) {
                trackBlocks.get(block).lightState = state;
            } else {
                throw new IllegalArgumentException("Block: " + block + " does not have a light");
            }
        });
    }

//TODO: Beacon communication with the train

//    public void setBeacon(int block, Beacon beacon) {
//        asyncTrackUpdate( () -> {
//            beaconBlocks.put(block, beacon);
//        });
//    }

    public Beacon beaconTransmit(int block) {
        if(beaconBlocks.containsKey(block)) {
            return beaconBlocks.get(block);
        } else{
            throw new IllegalArgumentException("Block: " + block + " does not have a beacon");
        }
    }

//TODO: Maybe refactor set to pass for clearer communication.

    @Override
    public void setSwitchState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if(!trackBlocks.get(block).isSwitch){
                throw new IllegalArgumentException("Block: " + block + " is not a switch");
            }
            trackBlocks.get(block).setSwitchState(state);
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if (trackBlocks.get(block).feature.isCrossing()) {
                trackBlocks.get(block).setCrossingState(state);
            } else {
                throw new IllegalArgumentException("Block: " + block + " is not a crossing");
            }
        });
    }

    @Override
    public void setTrainAuthority(Integer blockID, int authority) {
        asyncTrackUpdate( () -> {
            trackBlocks.get(blockID).setAuthority(authority);
            trackBlocks.get(blockID).getOccupiedBy().setAuthority(authority);
        });
    }

    @Override
    public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        asyncTrackUpdate( () -> {
            System.out.println("Setting speed to: " + commandedSpeed + " at block " + blockID);
            trackBlocks.get(blockID).setCommandSpeed(commandedSpeed);
            trackBlocks.get(blockID).getOccupiedBy().setCommandSpeed(commandedSpeed);
        });
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
        asyncTrackUpdate( () -> {
            TrackBlock brokenBlock = this.trackBlocks.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setFailure(true,false,false);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        asyncTrackUpdate( () -> {
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,false,true);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
        asyncTrackUpdate( () -> {
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,true,false);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    //TODO: make sure occupancies are set in the map and to wayside for failures
    @Override
    public boolean getBrokenRail(Integer blockID) {
        return this.trackBlocks.get(blockID).brokenRail;
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return this.trackBlocks.get(blockID).powerFailure;
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return this.trackBlocks.get(blockID).trackCircuitFailure;
    }

    //This operation is being done on the block, not the train
    @Override
    public void setPassengersDisembarked(TrainModel train, int disembarked) {
        asyncTrackUpdate( () -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
                if (block.feature.isStation()) {
                    block.feature.setPassengersDisembarked(disembarked);
                    Integer newTotal = train.getPassengerCount() - disembarked;
                } else {
                    throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
                }
            } else {
                throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on the track");
            }
        });
    }

    @Override
    public int getPassengersEmbarked(TrainModel train) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if(trackOccupancyMap.containsKey(train)){
            TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
            if (block.feature.isStation()) {
                int embarked = (random.nextInt(0, MAX_PASSENGERS - train.getPassengerCount()));
                block.feature.setPassengersEmbarked(embarked);
                this.ticketSales += embarked;
                return embarked;
            } else {
                throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
            }
        } else {
            throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on the track");
        }
    }

//TODO: make sure to reset ticket sales when needed
    //every tick is a second to ticket sales will reset every 3600 seconds
    @Override
    public int getTicketSales() {
        return this.ticketSales;
    }


    public void resetTicketSales() {
        this.ticketSales = 0;
    }

    public void newTemperature(){
        int newTemp = ThreadLocalRandom.current().nextInt(-5, 5);
        this.outsideTemperature += newTemp;
    }

    //Testing purposes
    public TrackBlock getBlock(int blockID) {
        return trackBlocks.get(blockID);
    }

    public TrackBlockLine getTrack(){
        return trackBlocks;
    }
    public void moveTrain(TrainModel train, int blockID) {
        trackOccupancyMap.remove(train);
        trackOccupancyMap.put(train, blockID);
    }
}
