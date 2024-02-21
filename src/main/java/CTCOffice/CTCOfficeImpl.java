package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;
import trainModel.trainModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Utilities.Constants.*;


public class CTCOfficeImpl implements CTCOffice {
    public Map<Integer, TrainModel> trains = new HashMap<Integer, TrainModel>();
    //private trainSubjectFactory trainSubjectMaker;

    private Map<Boolean, ArrayList<CTCBlockInfo>> track = new HashMap<Boolean, ArrayList<CTCBlockInfo>>();
    private Map<Boolean ,Map<Integer, CTCSwitchInfo>> switches = new HashMap<Boolean, Map<Integer, CTCSwitchInfo>>();


    public CTCOfficeImpl() {
        CTCBlockInfo block0  = new CTCBlockInfo(0,  false, false, false, false, false,false, false,       false,       false, false,         1000, 0);
        CTCBlockInfo block1  = new CTCBlockInfo(1,  false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block2  = new CTCBlockInfo(2,  false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block3  = new CTCBlockInfo(3,  false, true , false, false, false,true , false,       false,       false, CROSSING_OPEN, 50,   50);
        CTCBlockInfo block4  = new CTCBlockInfo(4,  false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block5  = new CTCBlockInfo(5,  false, false, false, true , false,false, false,       SWITCH_MAIN, false, false,         50,   50);
        CTCBlockInfo block6  = new CTCBlockInfo(6,  false, false, true , false, true ,false, LIGHT_GREEN, false,       true , false,         50,   50);
        CTCBlockInfo block7  = new CTCBlockInfo(7,  false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block8  = new CTCBlockInfo(8,  false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block9  = new CTCBlockInfo(9,  false, true , false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block10 = new CTCBlockInfo(10, false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block11 = new CTCBlockInfo(11, false, false, true , false, true ,false, LIGHT_RED, false,       false, false,         50,   50);
        CTCBlockInfo block12 = new CTCBlockInfo(12, false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block13 = new CTCBlockInfo(13, false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block14 = new CTCBlockInfo(14, false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCBlockInfo block15 = new CTCBlockInfo(15, false, false, false, false, false,false, false,       false,       false, false,         50,   50);
        CTCSwitchInfo switch1 = new CTCSwitchInfo(5, 6, 11, false);


        switches.put(false, new HashMap<Integer, CTCSwitchInfo>() {{
            put(5, switch1);
        }});

        ArrayList<CTCBlockInfo> line1 = new ArrayList<>() {{
            add(block0);add(block1);add(block2);add(block3);add(block4);add(block5);add(block6);add(block7);add(block8);add(block9);add(block10);add(block11);add(block12);add(block13);add(block14);add(block15);
        }};
        track.put(true, line1);
        System.out.println("Track 1: " + track.get(true).size());
    }

    public TrainModel dispatch(int trainID, int blockID) {
        if(trains.containsKey(trainID)) {
            return trains.get(trainID);
        }
        else {
            TrainModel newTrain = new trainModelImpl(trainID);
            trains.put(trainID, newTrain);
            return newTrain;
        }
    }

    public void setOccupancy(int blockID,boolean line, boolean occupied) {
        track.get(line).get(blockID).setOccupied(occupied);
    }

    public void setLightState(boolean line, int blockID, boolean lightState) {
        track.get(line).get(blockID).setLightState(lightState);
    }

    public void setSwitchState(boolean line, int blockID, boolean switchConState) {
        switches.get(line).get(blockID).setSwitchState(switchConState);
    }
//*********************************************************************************************************************************************
    public void setManualMode() {

    }

    public void setMaintenanceMode() {

    }

    public void setSuggestedSpeed(int trainID, int speed) {

    }

    public void setSelectedTrain(int trainID) {

    }

    public void setSchedule(int trainID, String schedule) {

    }

    public void setOccupancy(boolean line, int blockID, boolean occupied) {

    }

    public boolean getOccupancy(boolean line, int blockID) {
        return false;
    }

    public boolean getSwitchState(boolean line, int switchID) {
        return false;
    }

    public boolean getLightState(boolean line, int blockID) {
        return false;
    }

    public void getMode() {

    }
//*********************************************************************************************************************************************8

}

