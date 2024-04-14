package CTCOffice;

import CTCOffice.ScheduleInfo.ScheduleFile;
import CTCOffice.ScheduleInfo.ScheduleFileSubject;
import CTCOffice.ScheduleInfo.ScheduleLibrary;
import CTCOffice.ScheduleInfo.TrainStop;
import Common.CTCOffice;
import Framework.Simulation.TrackSystem;
import Framework.Simulation.WaysideSystem;
import Utilities.BasicTrackMap;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import Utilities.Records.BasicBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the CTCOffice interface and represents the office of the Centralized Traffic Control (CTC).
 * It manages the trains and the track blocks.
 */
public class CTCOfficeImpl implements CTCOffice {

    private int     time;
    private int     ticketSales;
    private int     mode;
    private boolean manualMode;
    private boolean maintenanceMode;
    private boolean autoMode;

    final static Map<String, ArrayList<CTCBlockSubject>> track = new HashMap<>();

    public static final ScheduleLibrary scheduleLibrary = ScheduleLibrary.getInstance();
    public static final CTCOfficeImpl OFFICE = new CTCOfficeImpl();

    private static TrackSystem trackSystem;

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {

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
        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(1, new TrainSchedule(1, "BlueLine", 0, 2, new ArrayList<>() {{
            add(new TrainStop(5, 5, 6, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add(new TrainStop(7, 9, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add(new TrainStop(10, 12, 13, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));
    }

    private static final Logger logger = LoggerFactory.getLogger(CTCOfficeImpl.class.getName());

    public void setTrackSystem(TrackSystem trackSystem) {
        CTCOfficeImpl.trackSystem = trackSystem;
        logger.info("TrackSystem has been set");
    }

    public void     setBlockOccupancy(boolean line, int blockID, boolean occupied) {
        logger.warn("setBlockOccupancy() is not implemented");
    }
    public boolean  getBlockOccupancy(boolean line, int blockID) {
        return false;
    }

    public void     setSwitchState(boolean line, int blockID, boolean switchState) {
        logger.warn("setSwitchState() is not implemented");
    }
    public boolean  getSwitchState(boolean line, int switchID) {
        logger.warn("getSwitchState() is not implemented");
        return false;
    }

    public void     setLightState(boolean line, int blockID, boolean lightState) {
        logger.warn("setLightState() is not implemented");
    }
    public boolean  getLightState(boolean line, int blockID) {
        logger.warn("getLightState() is not implemented");
        return false;
    }

    public void     setCrossingState(boolean line, int blockID, boolean crossingState) {
        logger.warn("setCrossingState() is not implemented");
    }
    public boolean  getCrossingState(boolean line, int blockID) {
        logger.warn("getCrossingState() is not implemented");
        return false;
    }

    void     setUnderMaintenance(boolean line, int blockID, boolean underMaintenance) {
        logger.warn("setUnderMaintenance() is not implemented");
    }
    public boolean  getUnderMaintenance(boolean line, int blockID) {
        logger.warn("getUnderMaintenance() is not implemented");
        return false;
    }

    void setTime(int time) {
        logger.warn("setTime() is not implemented");
        this.time = time;
    }
    int getTime() {
        logger.warn("getTime() is not implemented");
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

    void DispatchTrain(Lines line , int trainID) {
        logger.info("CTC Dispatching train {} on line {}", trainID, line);
        trackSystem.dispatchTrain(line, trainID);
    }

    void sendSpeed(Lines line, int blockID, double speed) {
        logger.info("CTC sending speed {} to block {} on line {}", speed, blockID, line);
        WaysideSystem.getController(line, blockID).CTCSendSpeed(blockID, speed);
    }

    void sendAuthority(Lines line, int blockID, int authority) {
        logger.info("CTC sending authority {} to block {} on line {}", authority, blockID, line);
        WaysideSystem.getController(line, blockID).CTCSendAuthority(blockID, authority);
    }

}

