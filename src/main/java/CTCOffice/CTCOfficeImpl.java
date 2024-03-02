package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;
import trainModel.trainModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Framework.Support.ObservableHashMap;
import static Utilities.Constants.*;

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

    final static Map<Boolean, ArrayList<CTCBlockSubject>> track = new ObservableHashMap<>();
    final static Map<Integer, Schedule> schedules = new ObservableHashMap<>();
    public static final CTCOfficeImpl OFFICE = new CTCOfficeImpl();

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {
        track.put(false, new ArrayList<>() {{
        add(new CTCBlockSubject( new CTCBlockInfo(0,  false, false, false, false, false,false, false,       false,         1000, 0,  0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(1,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(2,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(3,  false, true , false, false, false,true , false,       CROSSING_OPEN, 50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(4,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(5,  false, false, false, true , false,false, false,       false,         50,   50, 5, 6, 11, false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(6,  false, false, true , false, true ,false, LIGHT_GREEN, false,         50,   50, 5, 6, 11, false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(7,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(8,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(9,  false, true , false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(10, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(11, false, false, true , false, true ,false, LIGHT_RED,   false,         50,   50, 5, 6, 11, false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(12, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(13, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(14, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        add(new CTCBlockSubject( new CTCBlockInfo(15, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false)));
        }});

        ArrayList<Integer> blank = new ArrayList<>();

        schedules.put(1, new Schedule(1, 0, 1, new ArrayList<>() {{add(new SingleStop(10, 0, 0, blank, blank, blank));}}));
        schedules.put(2, new Schedule(2, 0, 1, new ArrayList<>() {{add(new SingleStop(15, 0, 0, blank, blank, blank));}}));

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

