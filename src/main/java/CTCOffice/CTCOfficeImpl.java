package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;
import trainModel.trainModelImpl;
import trainModel.trainSubjectFactory;

import java.util.*;



class CTCOfficeImpl implements CTCOffice {
    public Map<Integer, TrainModel> trains = new HashMap<Integer, TrainModel>();
    private trainSubjectFactory trainSubjectMaker;
    private Map<Boolean, List<CTCBlockInfo>> track = new HashMap<Boolean, List<CTCBlockInfo>>();



    public CTCOfficeImpl() {
        // initialize the CTC
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


    public void setManualMode() {}

    public void setMaintenanceMode() {}

    public void setAuthority(int trainID, boolean line, int authority) {
    }

    public void setSuggestedSpeed(int trainID, int speed) {}

    public void setSelectedTrain(int trainID) {}

    public void setSchedule(int trainID, String schedule) {}

    public void setOccupancy(int blockID, boolean occupied) {

    }

    public void setLightState(int blockID, boolean lightState) {

    }

    public void setSwitchState(int switchID, boolean switchState) {

    }

    public void setOccupancy(int blockID,boolean line, boolean occupied) {
        track.get(line).get(blockID).setOccupied(occupied);
    }

    public void setLightState(boolean line, int blockID, boolean lightState) {
        track.get(line).get(blockID).setLightState(lightState);
    }

    public void setSwitchState(boolean line, int switchID, boolean switchState) {
        track.get(line).get(switchID).setSwitchState(switchState);
    }

    public void getMode() {}

    public boolean getOccupancy(boolean line, int blockID) {
        return track.get(line).get(blockID).getOccupied();
    }

    public boolean getSwitchState(boolean line, int switchID) {
        return track.get(line).get(switchID).getSwitchState();
    }

    public boolean getLightState(boolean line, int blockID) {
        return track.get(line).get(blockID).getLightState();
    }

    public void setTicketSales(boolean ticketSales) {}

}

