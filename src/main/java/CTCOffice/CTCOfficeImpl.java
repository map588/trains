package CTCOffice;

import CTCOffice.ScheduleInfo.ScheduleFile;
import CTCOffice.ScheduleInfo.ScheduleFileSubject;
import CTCOffice.ScheduleInfo.ScheduleLibrary;
import CTCOffice.ScheduleInfo.TrainStop;
import Common.CTCOffice;
import Framework.Simulation.TrackSystem;
import Framework.Simulation.WaysideSystem;
import Utilities.BasicLineMap;
import Utilities.Enums.Lines;
import Utilities.ParsedBasicBlocks;
import Utilities.Records.BasicBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

    private TrackSystem trackSystem;

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {

        BasicLineMap trackLineBlocks = ParsedBasicBlocks.getInstance().getAllBasicLines();
        ArrayList<CTCBlock> greenBlockSwitches = new ArrayList<>();
            ArrayList<CTCBlockSubject> greenBlockSubjects = new ArrayList<>();
            for(BasicBlock block : trackLineBlocks.get(Lines.GREEN).values()) {
                greenBlockSubjects.add(new CTCBlockSubject(new CTCBlock(block)));
                if(block.isSwitch()) {
                    greenBlockSwitches.add(CTCBlockSubjectMapGreen.getInstance().getSubject(block.blockNumber()).getBlockInfo());
                }
            }
            track.put(Lines.GREEN.toString(), greenBlockSubjects);
            for(CTCBlock block : greenBlockSwitches) {
                    CTCBlockSubjectMapGreen.getInstance().getSubject(block.getDivergingBlockOneID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
                    CTCBlockSubjectMapGreen.getInstance().getSubject(block.getDivergingBlockTwoID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
            }

        ArrayList<CTCBlock> redBlockSwitches = new ArrayList<>();
        ArrayList<CTCBlockSubject> redBlockSubjects = new ArrayList<>();
        for(BasicBlock block : trackLineBlocks.get(Lines.RED).values()) {
            redBlockSubjects.add(new CTCBlockSubject(new CTCBlock(block)));
            if(block.isSwitch()) {
                redBlockSwitches.add(CTCBlockSubjectMapRed.getInstance().getSubject(block.blockNumber()).getBlockInfo());
            }
        }
        track.put(Lines.RED.toString(), redBlockSubjects);
        for(CTCBlock block : redBlockSwitches) {
            CTCBlockSubjectMapRed.getInstance().getSubject(block.getDivergingBlockOneID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
            CTCBlockSubjectMapRed.getInstance().getSubject(block.getDivergingBlockTwoID()).getBlockInfo().setSwitchDivInformation(block.getConvergingBlockID(), block.getDivergingBlockOneID(), block.getDivergingBlockTwoID());
        }


        new ScheduleFileSubject(new ScheduleFile("Schedule1", "12/12/2019"));
        scheduleLibrary.getSubject("Schedule1").getSchedule().putTrainSchedule(1, new TrainSchedule(1, "BlueLine", 0, 2, new ArrayList<>() {{
            add(new TrainStop(5, 5, 6, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add(new TrainStop(7, 9, 10, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            add(new TrainStop(10, 12, 13, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }}));
    }

    public void setTrackSystem(TrackSystem trackSystem) {
        this.trackSystem = trackSystem;
    }

    public void     setBlockOccupancy(boolean line, int blockID, boolean occupied) {

    }
    public boolean  getBlockOccupancy(boolean line, int blockID) {
        return false;
    }

    public void     setSwitchState(boolean line, int blockID, boolean switchState) {

    }
    public boolean  getSwitchState(boolean line, int switchID) {
        return false;
    }

    public void     setLightState(boolean line, int blockID, boolean lightState) {

    }
    public boolean  getLightState(boolean line, int blockID) {
        return false;
    }

    public void     setCrossingState(boolean line, int blockID, boolean crossingState) {

    }
    public boolean  getCrossingState(boolean line, int blockID) {
        return false;
    }

    void     setUnderMaintenance(boolean line, int blockID, boolean underMaintenance) {

    }
    public boolean  getUnderMaintenance(boolean line, int blockID) {
        return false;
    }

    void setTime(int time) {
        this.time = time;
    }
    int getTime() {
        return time;
    }

    void setTicketSales(int ticketSales) {
        this.ticketSales = ticketSales;
    }
    int getTicketSales() {
        return ticketSales;
    }

    void setMode(int mode) {
        this.mode = mode;
    }
    int getMode() {
        return mode;
    }

    void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
    }
    boolean getManualMode() {
        return manualMode;
    }

    void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }
    boolean getMaintenanceMode() {
        return maintenanceMode;
    }

    void setAutoMode(boolean autoMode) {
        this.autoMode = autoMode;
    }
    boolean getAutoMode() {
        return autoMode;
    }
    void DispatchTrain(Lines line , int trainID) {
        trackSystem.dispatchTrain(line, trainID);
    }

    void sendSpeed(Lines line, int blockID, double speed) {
        WaysideSystem.getController(line, blockID).CTCSendSpeed(blockID, speed);
    }

    void sendAuthority(Lines line, int blockID, int authority) {
        WaysideSystem.getController(line, blockID).CTCSendAuthority(blockID, authority);
    }




//*********************************************************************************************************************************************8

}

