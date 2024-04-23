package CTCOffice;

import Common.CTCOffice;
import Framework.Simulation.TrackSystem;
import Framework.Simulation.WaysideSystem;
import Framework.Support.BlockIDs;
import Framework.Support.Notifier;
import Utilities.BasicTrackMap;
import Utilities.Constants;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
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
            53, 54, 55, 56, 57));
    public final static ArrayList<Integer> RedTrackLayout = new ArrayList<>(List.of(
            9, 8, 7, 6, 5, 4, 3, 2, 1, 16, 17, 18, 19, 20, 21, 22,
            23, 24, 25));

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

    Map<Integer, BlockIDs> trainLocations = new HashMap<>();
    ArrayList<Integer> trainIDs = new ArrayList<>();
    Map<Double, TrainIdentity> trainSchedule = new HashMap<>();
    ArrayList<Double> dispatchTimes = new ArrayList<>();

    CTCOfficeSubject subject = new CTCOfficeSubject(this);
    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {
        time = 0;

        BasicTrackMap trackLineBlocks = GlobalBasicBlockParser.getInstance().getAllBasicLines();
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
        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(1, new TrainSchedule(1, Lines.GREEN.toString(), 240, 2, new ArrayList<>() {{
            add( new TrainStop(0, 65, convertClockTimeToDouble("6:05"), convertClockTimeToDouble("6:06"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(1, 73, convertClockTimeToDouble("6:09"), convertClockTimeToDouble("6:10"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(2, 9, convertClockTimeToDouble("6:12"), convertClockTimeToDouble("6:13"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));
        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(2, new TrainSchedule(2, Lines.RED.toString(), 300, 1, new ArrayList<>() {{
            add( new TrainStop(0, 7, convertClockTimeToDouble("6:05"), convertClockTimeToDouble("6:06"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(1, 21, convertClockTimeToDouble("6:09"), convertClockTimeToDouble("6:10"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add( new TrainStop(2, 25, convertClockTimeToDouble("6:12"), convertClockTimeToDouble("6:13"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));


    }

    public void setTrackSystem(TrackSystem trackSystem) {
        CTCOfficeImpl.trackSystem = trackSystem;
        logger.info("TrackSystem has been set");
    }

    public void     setBlockOccupancy(Lines line, int blockID, boolean occupied) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setOccupied(false, occupied);
        logger.info("Block {} on line {} has been set to occupied: {}", blockID, line, occupied);
    }

    //************************************************************************************************************************************

    public void     setSwitchState(Lines line, int blockID, boolean switchState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setSwitchState(false, switchState);
        logger.info("Switch {} on line {} has been set to {}", blockID, line, switchState);
    }

    public void     setLightState(Lines line, int blockID, boolean lightState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setSwitchLightState(false, lightState);
        logger.info("Light {} on line {} has been set to {}", blockID, line, lightState);
    }

    public void     setCrossingState(Lines line, int blockID, boolean crossingState) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setCrossingState(false, crossingState);
        logger.info("Crossing {} on line {} has been set to {}", blockID, line, crossingState);
    }

    public void setBlockMaintenance(Lines line, int blockID, boolean underMaintenance) {
        blockSubjectMap.getSubject(BlockIDs.of(blockID, line)).getBlockInfo().setUnderMaintenance(false, underMaintenance);
        logger.info("Block {} on line {} has been set to under maintenance: {}", blockID, line, underMaintenance);
    }

    public void setTime(double time) {
        this.time = time;
        notifyChange(TIME_PROPERTY, convertDoubleToClockTime(time));
    }

    public void incrementTime() {
        time += Constants.TIME_STEP_S;
        notifyChange(TIME_PROPERTY, convertDoubleToClockTime(time));
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
           trainSchedule.put(schedule.getDispatchTime(), new TrainIdentity(schedule.getTrainID(), Enum.valueOf(Lines.class, schedule.getLine()), schedule.getDispatchTime(), schedule.getCarCount()));
           dispatchTimes.add(schedule.getDispatchTime());
           dispatchTimes.sort(Comparator.naturalOrder());
        }

    }
    public void notifyTrainReturn(int trainID) {
        logger.info("Train {} has returned to the yard", trainID);
        trainLocations.remove(trainID);
        trainIDs.remove(trainID);
    }
    void DispatchTrain(Lines line , int trainID, int carCount, double dispatchTime){
        logger.info("CTC Dispatching train {} on line {}", trainID, line);
        trackSystem.dispatchTrain(line, trainID);
        trainSchedule.remove(dispatchTime);
        dispatchTimes.remove(0);
        trainIDs.add(trainID);
        trainLocations.put(trainID, BlockIDs.of(0, line));
        sendAuthority(line, 0, 1900);
        sendSpeed(line, 0, 40);
    }

    void sendSpeed(Lines line, int blockID, double speed) {
        logger.info("CTC sending speed {} to block {} on line {}", speed, blockID, line);
        WaysideSystem.getController(line, blockID).CTCSendSpeed(blockID, speed);
    }
 //TODO: change Send Authority to send in blocks
    void sendAuthority(Lines line, int blockID, int authority) {
        logger.info("CTC sending authority {} to block {} on line {}", authority, blockID, line);
        WaysideSystem.getController(line, blockID).CTCSendAuthority(blockID, authority);
    }

    public void notifyChange(String property, Object newValue) {
        if(!Objects.equals(property, TIME_PROPERTY)) {
            logger.info("Property {} has been changed to {}", property, newValue);
        }
        subject.setProperty(property, newValue);
    }
}

//TODO: send the wayside a list of blcisk to give gthe authity to instead of just one block
//TODO: send that list by calling send authoity on the occupied block and the block with a reative auth for each block

//TODO: send speed as meters per second

//TODO: make sure switch is shown currectly with divering nodes



