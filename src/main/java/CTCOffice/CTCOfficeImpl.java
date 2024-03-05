package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Framework.Support.ObservableHashMap;

import static Utilities.CSVTokenizer.blockList;
import static Utilities.CSVTokenizer.lineNames;

/**
 * This class implements the CTCOffice interface and represents the office of the Centralized Traffic Control (CTC).
 * It manages the trains and the track blocks.
 */
public class CTCOfficeImpl implements CTCOffice {

    private int time;
    private int ticketSales;
    private int mode;
    private boolean manualMode;
    private boolean maintenanceMode;
    private boolean autoMode;


    public Map<Integer, TrainModel> trains = new HashMap<>();

    final static Map<String, ArrayList<CTCBlockSubject>> track = new HashMap<>();

    public static final CTCOfficeImpl OFFICE = new CTCOfficeImpl();

    public static final ScheduleLibrary scheduleLibrary = new ScheduleLibrary();

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {

        track.put(lineNames.get(0), new ArrayList<>() {{
            add(new CTCBlockSubject(new CTCBlockInfo(blockList.get(lineNames.get(0)).get(0))));
        }});
        for(int i = 1; i < blockList.get(lineNames.get(0)).size(); i++) {
            track.get(lineNames.get(0)).add(new CTCBlockSubject(new CTCBlockInfo(blockList.get(lineNames.get(0)).get(i))));
        }
        scheduleLibrary.addFullSchedule("Schedule1", new FullScheduleFile("Schedule1", "12/12/2019"));

        scheduleLibrary.getFullSchedule("Schedule1").putTrainSchedule(1, new SingleTrainSchedule(1,
                "BlueLine", 0, 1, new ArrayList<>() {{add(new SubRoute(10,
                0, 0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));}}));

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

    public void     setUnderMaintenance(boolean line, int blockID, boolean underMaintenance) {

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








//*********************************************************************************************************************************************8

}

