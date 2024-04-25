package CTCOffice;

import Common.CTCOffice;
import Framework.Simulation.TrackSystem;
import Framework.Simulation.WaysideSystem;
import Framework.Support.BlockIDs;
import Framework.Support.Notifier;
import Utilities.BasicTrackMap;
import Utilities.Constants;
import Utilities.Enums.Lines;
import Utilities.BasicBlockParser;
import Utilities.Records.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static Utilities.TimeConvert.*;
import static CTCOffice.Properties.OfficeProperties.*;
import java.util.*;

/**
 * This class implements the CTCOffice interface and represents the office of the Centralized Traffic Control (CTC).
 * It manages the trains and the track blocks.
 */


public class CTCOfficeImpl implements CTCOffice, Notifier {
    public final static ArrayList<Integer> GreenTrackLayout = new ArrayList<>(List.of(
            63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
            78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
            93, 94, 95, 96, 97, 98, 99, 100, 85, 84, 83, 82, 81, 80, 79,
            78, 77, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
            114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128,
            129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
            144, 145, 146, 147, 148, 149, 150, 28, 27, 26, 25, 24, 23, 22, 21,
            20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6,
            5, 4, 3, 2, 1, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
            23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            53, 54, 55, 56, 57, 0));
    public final static ArrayList<Integer> RedTrackLayout = new ArrayList<>(List.of(
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
            25, 26, 27, 76, 75, 74, 73, 72, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 52, 51, 50,
            49, 48, 47, 46, 45, 44, 67, 68, 69, 70, 71, 38, 37, 36, 35,
            34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20,
            19, 18, 17, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0));

    private double  time; // in seconds
    private int     ticketSales;
    private int     mode;
    private boolean manualMode;
    private boolean maintenanceMode;
    private boolean autoMode;

    private static final Logger logger = LoggerFactory.getLogger(CTCOfficeImpl.class.getName());
    final static Map<String, ArrayList<CTCBlockSubject>> track = new HashMap<>();

    public static final ScheduleLibrary scheduleLibrary = ScheduleLibrary.getInstance();
    public static final CTCOfficeImpl OFFICE = new CTCOfficeImpl();

    private static TrackSystem trackSystem;
    private final CTCBlockSubjectMap blockSubjectMap = CTCBlockSubjectMap.getInstance();

    Map<TrainIdentity, BlockIDs>        trainLocations = new HashMap<>();
    Map<BlockIDs, TrainIdentity>        antiTrainLocations = new HashMap<>();
    Map<TrainIdentity, TrainSchedule>   trainSchedules = new HashMap<>();   //schedules indexed by their train identity
    Map<Double, TrainIdentity>          trainDispatches = new HashMap<>();  //trains indexed by their dispatch time
    ArrayList<Double>                   dispatchTimes = new ArrayList<>();  //dispatch times of the trains
    Map<Integer, TrainIdentity>         trainIDs = new HashMap<>();         //trains indexed by their train ID
    Queue<StopCallback>                 stopCallbacks = new LinkedList<>();
    CTCOfficeSubject subject = new CTCOfficeSubject(this);

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {
        time = 0;

        BasicTrackMap trackLineBlocks = BasicBlockParser.getInstance().getAllBasicLines();
        ArrayList<CTCBlock> greenBlockSwitches = new ArrayList<>();
            ArrayList<CTCBlockSubject> greenBlockSubjects = new ArrayList<>();
            for(BasicBlock block : trackLineBlocks.get(Lines.GREEN).values()) {
                greenBlockSubjects.add(new CTCBlockSubject(new CTCBlock(block)));
                if(block.isSwitch()) {
                    greenBlockSwitches.add(CTCBlockSubjectMap.getInstance().getSubject(BlockIDs.of(block.blockNumber(), Lines.GREEN)).getBlockInfo());
                }
            }
            track.put(Lines.GREEN.toString(), greenBlockSubjects);
            for(CTCBlock block : greenBlockSwitches) {
                    CTCBlockSubjectMap.getInstance().getSubject(block.getDivergingBlockOneID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
                    CTCBlockSubjectMap.getInstance().getSubject(block.getDivergingBlockTwoID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
            }

        ArrayList<CTCBlock> redBlockSwitches = new ArrayList<>();
        ArrayList<CTCBlockSubject> redBlockSubjects = new ArrayList<>();
        for(BasicBlock block : trackLineBlocks.get(Lines.RED).values()) {
            redBlockSubjects.add(new CTCBlockSubject(new CTCBlock(block)));
            if(block.isSwitch()) {
                redBlockSwitches.add(CTCBlockSubjectMap.getInstance().getSubject(BlockIDs.of(block.blockNumber(), Lines.RED)).getBlockInfo());
            }
        }
        track.put(Lines.RED.toString(), redBlockSubjects);
        for(CTCBlock block : redBlockSwitches) {
            CTCBlockSubjectMap.getInstance().getSubject(block.getDivergingBlockOneID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
            CTCBlockSubjectMap.getInstance().getSubject(block.getDivergingBlockTwoID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
        }


        new ScheduleFileSubject(new ScheduleFile("Schedule1", "12/12/2019"));
        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(1, new TrainSchedule(1, Lines.GREEN.toString(), 120, 2, new ArrayList<>() {{
            add( new TrainStop(0, 65, convertClockTimeToDouble("6:05"), convertClockTimeToDouble("6:06"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(1, 73, convertClockTimeToDouble("6:09"), convertClockTimeToDouble("6:10"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(2, 9, convertClockTimeToDouble("6:12"), convertClockTimeToDouble("6:13"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));

        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(2, new TrainSchedule(2, Lines.RED.toString(), 180, 1, new ArrayList<>() {{
            add( new TrainStop(0, 7, convertClockTimeToDouble("6:05"), convertClockTimeToDouble("6:06"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(1, 21, convertClockTimeToDouble("6:09"), convertClockTimeToDouble("6:10"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(2, 25, convertClockTimeToDouble("6:12"), convertClockTimeToDouble("6:13"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));
    }

    public void setTrackSystem(TrackSystem trackSystem) {
        CTCOfficeImpl.trackSystem = trackSystem;
        logger.info("TrackSystem has been set");
    }

    public void changeTrainLocation(int oldBlock, int blockID, Lines line) {
        TrainIdentity train = antiTrainLocations.get(BlockIDs.of(oldBlock, line));
        trainLocations.remove(train);
        trainLocations.put(train, BlockIDs.of(blockID, line));
        antiTrainLocations.remove(BlockIDs.of(oldBlock, line));
        antiTrainLocations.put(BlockIDs.of(blockID, line), train);
        int currentLocation = trainSchedules.get(train).getCurrentBlockIndex();
        trainSchedules.get(train).setCurrentBlockIndex(
                (getTrackLayout(line).indexOf(blockID) > currentLocation)
                        ? getTrackLayout(line).indexOf(blockID)
                        : getTrackLayout(line).lastIndexOf(blockID));
        logger.info("Train {} has moved from block {} to block {}", train.trainID(), oldBlock, blockID);
        if(oldBlock == 0){
            sendAuthority(train.trainID());
            sendSpeed(train.trainID());
        }else{
            if (trainSchedules.get(train).getStops().get(trainSchedules.get(train).getStopsCompleted()).incrementPassedBlocks()) {
                stopCallbacks.add(new StopCallback(train, time
                        + (trainSchedules.get(train).getStops().get(trainSchedules.get(train).getStopsCompleted()).getDepartureTime()
                        - trainSchedules.get(train).getStops().get(trainSchedules.get(train).getStopsCompleted()).getArrivalTime())));
                trainSchedules.get(train).incrementStopsCompleted();
            }
            else {
                sendAuthority(train.trainID());
                sendSpeed(train.trainID());
            }
        }
    }

    public void     setBlockOccupancy(Lines line, int blockID, boolean occupied) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setOccupied(false, occupied);
        //if the block is on the yard then do nothing
        if(occupied) {
            if(blockID == 0 ) {
                return;
            }
            ArrayList<Integer> trackLayout = (line.equals(Lines.GREEN)) ? GreenTrackLayout : RedTrackLayout;
            int blockIndex = trackLayout.indexOf(blockID);
            if (blockIndex == -1) {
                logger.error("Block {} on line {} does not exist", blockID, line);
                return;
            }
            if (blockIndex == 0) { //if the block is the first block then pull location and info from yard
                changeTrainLocation(0, blockID, line);
            }
            //if the block is only visited once in the track layout
            else if (blockIndex == trackLayout.lastIndexOf(blockID)) {
                // and the block before is occupied then the train is moving forward and move its location to this block
                if (trainLocations.containsValue(BlockIDs.of(trackLayout.get(blockIndex - 1), line))) {
                    changeTrainLocation(trackLayout.get(blockIndex - 1), blockID, line);

                } else {
                    boolean found = false;
                    for (int i = blockIndex - 1; i >= 0; i--) {
                        if (trainLocations.containsValue(BlockIDs.of(trackLayout.get(i), line))) {
                            changeTrainLocation(trackLayout.get(i), blockID, line);
                            logger.warn("Train lost then found from block {} to block {} on line {}", trackLayout.get(i), blockID, line);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        logger.error("Train out of nowhere on non-loop block {} on line {}", blockID, line);
                    }
                }
            } else { //block is visited more than once in the track layout
                //if the block is visited more than once in the track layout then find if this is the first visit
                if (trainLocations.containsValue(BlockIDs.of(trackLayout.get(blockIndex - 1), line))) {
                    changeTrainLocation(trackLayout.get(blockIndex - 1), blockID, line);
                } else if (trainLocations.containsValue(BlockIDs.of(trackLayout.get(trackLayout.lastIndexOf(blockID) - 1), line))) {
                    changeTrainLocation(trackLayout.get(trackLayout.lastIndexOf(blockID) - 1), blockID, line);
                } else {
                    logger.warn("Train out of nowhere on block {} on line {}", blockID, line);
                }
            }
        }
    }

    public void     setSwitchState(Lines line, int blockID, boolean switchState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setSwitchState(false, switchState);
        logger.info("Switch {} on line {} has been set to {}", blockID, line, switchState);
    }

    public void     setLightState(Lines line, int blockID, boolean lightState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setSwitchLightState(false, lightState);
    }

    public void     setCrossingState(Lines line, int blockID, boolean crossingState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setCrossingState(false, crossingState);
    }

    public void setBlockMaintenance(Lines line, int blockID, boolean underMaintenance) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setUnderMaintenance(false, underMaintenance);
       // logger.info("Block {} on line {} has been set to under maintenance: {}", blockID, line, underMaintenance);
    }

    public void setTime(double time) {
        this.time = time;
        notifyChange(TIME_PROPERTY, convertDoubleToClockTime(time));
    }

    public void incrementTime() {
        time += Constants.TIME_STEP_S;
        notifyChange(TIME_PROPERTY, convertDoubleToClockTime(time));
        if((stopCallbacks.peek() != null) && (time >= stopCallbacks.peek().disembarkTime())) {
            StopCallback stopCallback = stopCallbacks.poll();
            TrainIdentity train = stopCallback.train();
            sendAuthority(train.trainID());
            sendSpeed(train.trainID());
        }
    }

    double getTime() {
        return time;
    }

    void setTicketSales(int ticketSales) {
        logger.warn("setTicketSales() is not implemented");
        this.ticketSales = ticketSales;
    }
    int getTicketSales() {
        logger.warn("getTicketSales() is not implemented");
        return ticketSales;
    }

    void setMode(int mode) {
        logger.warn("setMode() is not implemented");
        this.mode = mode;
    }
    int getMode() {
        logger.warn("getMode() is not implemented");
        return mode;
    }

    void setManualMode(boolean manualMode) {
        logger.warn("setManualMode() is not implemented");
        this.manualMode = manualMode;
    }

    boolean getManualMode() {
        logger.warn("getManualMode() is not implemented");
        return manualMode;
    }

    void setMaintenanceMode(boolean maintenanceMode) {
        logger.warn("setMaintenanceMode() is not implemented");
        this.maintenanceMode = maintenanceMode;
    }

    boolean getMaintenanceMode() {
        logger.warn("getMaintenanceMode() is not implemented");
        return maintenanceMode;
    }

    void setAutoMode(boolean autoMode) {
        logger.warn("setAutoMode() is not implemented");
        this.autoMode = autoMode;
    }
    boolean getAutoMode() {
        logger.warn("getAutoMode() is not implemented");
        return autoMode;
    }

    void runSchedule(String scheduleName) {
        logger.info("Running schedule {}", scheduleName);
        ScheduleFile scheduleFile = scheduleLibrary.getSubject(scheduleName).getSchedule();
        for (TrainSchedule schedule : scheduleFile.getMultipleTrainSchedules().values()) {
           trainDispatches.put(schedule.getDispatchTime(), new TrainIdentity(schedule.getTrainID(), Enum.valueOf(Lines.class, schedule.getLine()),
                   schedule.getDispatchTime(), schedule.getCarCount()));
           dispatchTimes.add(schedule.getDispatchTime());
           dispatchTimes.sort(Comparator.naturalOrder());
           trainSchedules.put(TrainIdentity.of(schedule.getTrainID(), Enum.valueOf(Lines.class, schedule.getLine()),
                   schedule.getDispatchTime(), schedule.getCarCount()), schedule);
        }

    }

    public void notifyTrainReturn(int trainID) {
        logger.info("Train {} has returned to the yard", trainID);
        TrainIdentity train = trainIDs.get(trainID);
        antiTrainLocations.remove(trainLocations.get(train));
        trainLocations.remove(train);
        trainSchedules.remove(train);
        trainIDs.remove(trainID);
    }

    void DispatchTrain(Lines line , int trainID, int carCount, double dispatchTime){
        logger.info("CTC Dispatching train {} on line {}", trainID, line);
        trackSystem.dispatchTrain(line, trainID);
        trainDispatches.remove(dispatchTime);
        dispatchTimes.remove(0);
        trainIDs.put(trainID, TrainIdentity.of(trainID, line, dispatchTime, carCount));
        trainLocations.put(trainIDs.get(trainID), BlockIDs.of(0, line));
        antiTrainLocations.put(BlockIDs.of(0, line), trainIDs.get(trainID));
    }

    void sendSpeed(int trainID) {
        TrainIdentity train = trainIDs.get(trainID);
        BlockIDs location = trainLocations.get(train);
        TrainSchedule schedule = trainSchedules.get(train);
        TrainStop scheduledStop = schedule.getStop(schedule.getStopsCompleted());
        double speed = scheduledStop.getSpeedList().get(scheduledStop.getPassedBlocks());
        logger.info("CTC sending speed {} to block {} on line {}", speed, location.blockIdNum(), location.line());
        WaysideSystem.getController(location.line(), location.blockIdNum()).CTCSendSpeed(location.blockIdNum(), speed);
    }

    void sendAuthority(int trainID) {
        TrainIdentity train = trainIDs.get(trainID);
        BlockIDs location = trainLocations.get(train);
        TrainSchedule schedule = trainSchedules.get(train);
        TrainStop scheduledStop = schedule.getStop(schedule.getStopsCompleted());
        double authority = scheduledStop.getAuthorityList().get(scheduledStop.getPassedBlocks());
        logger.info("CTC sending authority {} to train {}", authority, trainID);
        WaysideSystem.getController(location.line(), location.blockIdNum()).CTCSendAuthority(location.blockIdNum(), (int) authority);
    }

    //TODO: Remove these methods
    void sendDumbAuthority(int trainID, Lines line, int blockID, int authority) {
        logger.info("CTC sending dumb authority to train {} on block {} on line {} with controller {}", trainID, blockID, line, WaysideSystem.getController(line, blockID));
        WaysideSystem.getController(line, blockID).CTCSendAuthority(blockID, authority);
    }
    void sendDumbSpeed(int trainID, Lines line, int blockID, double speed) {
        logger.info("CTC sending dumb speed to train {} on block {} on line {}", trainID, blockID, line);
        WaysideSystem.getController(line, blockID).CTCSendSpeed(blockID, speed);
    }
    void dispatchDumbTrain(int trainID, Lines line) {
        logger.info("CTC dispatching dumb train {} on line {}", trainID, line);
        trackSystem.dispatchTrain(line, trainID);
    }

    public void notifyChange(String property, Object newValue) {
        if(!Objects.equals(property, TIME_PROPERTY)) {
            logger.info("Property {} has been changed to {}", property, newValue);
        }
        subject.setProperty(property, newValue);
    }

    public ArrayList<Integer> getTrackLayout(Lines line){
        return (line.equals(Lines.GREEN)) ? GreenTrackLayout : RedTrackLayout;
    }
}

//TODO: send the wayside a list of blcisk to give gthe authity to instead of just one block
//TODO: send that list by calling send authoity on the occupied block and the block with a reative auth for each block

//TODO: make sure switch is shown currectly with divering nodes
//TODO: make train tracking and increment passed blocks and completed stops
//TODO: speed  unit convertion km/h to m/s


