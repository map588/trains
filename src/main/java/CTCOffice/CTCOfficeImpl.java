package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;
import trainModel.trainModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


    public Map<Integer, TrainModel> trains = new HashMap<Integer, TrainModel>();
    //private trainSubjectFactory trainSubjectMaker;

    private Map<Boolean, ArrayList<CTCBlockInfo>> track = new HashMap<Boolean, ArrayList<CTCBlockInfo>>();

    public static final CTCOfficeImpl OFFICE = new CTCOfficeImpl();

    /**
     * Constructor for the CTCOfficeImpl class.
     * Initializes the track blocks and the schedule.
     */
    private CTCOfficeImpl() {
        CTCBlockInfo block0  = new CTCBlockInfo(0,  false, false, false, false, false,false, false,       false,         1000, 0,  0, 0, 0,  false, false);
        CTCBlockInfo block1  = new CTCBlockInfo(1,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block2  = new CTCBlockInfo(2,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block3  = new CTCBlockInfo(3,  false, true , false, false, false,true , false,       CROSSING_OPEN, 50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block4  = new CTCBlockInfo(4,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block5  = new CTCBlockInfo(5,  false, false, false, true , false,false, false,       false,         50,   50, 5, 6, 11, false, false);
        CTCBlockInfo block6  = new CTCBlockInfo(6,  false, false, true , false, true ,false, LIGHT_GREEN, false,         50,   50, 5, 6, 11, false, false);
        CTCBlockInfo block7  = new CTCBlockInfo(7,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block8  = new CTCBlockInfo(8,  false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block9  = new CTCBlockInfo(9,  false, true , false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block10 = new CTCBlockInfo(10, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block11 = new CTCBlockInfo(11, false, false, true , false, true ,false, LIGHT_RED,   false,         50,   50, 5, 6, 11, false, false);
        CTCBlockInfo block12 = new CTCBlockInfo(12, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block13 = new CTCBlockInfo(13, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block14 = new CTCBlockInfo(14, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);
        CTCBlockInfo block15 = new CTCBlockInfo(15, false, false, false, false, false,false, false,       false,         50,   50, 0, 0, 0,  false, false);

        ArrayList<CTCBlockInfo> line1 = new ArrayList<>() {{
            add(block0);add(block1);add(block2);add(block3);add(block4);add(block5);add(block6);add(block7);add(block8);add(block9);add(block10);add(block11);add(block12);add(block13);add(block14);add(block15);
        }};
        track.put(true, line1);
        ArrayList<Integer> blank = new ArrayList<Integer>();
        SingleStop stop1 = new SingleStop(10, 0, 0, blank, blank, blank);
        SingleStop stop2 = new SingleStop(15, 0, 0, blank, blank, blank);
        Schedule schedule = new Schedule(1, 0, 1, new ArrayList<SingleStop>() {{
            add(stop1);add(stop2);
        }});

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

