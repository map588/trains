package CTCOffice;

import Common.CTCOffice;
import Common.TrainModel;

import trainModel.trainSubjectFactory;
import trainModel.trainModelImpl;

import java.util.HashMap;
import java.util.Map;


class CTCOfficeImpl implements CTCOffice {
    public Map<Integer, TrainModel> trains = new HashMap<Integer, TrainModel>();
    private trainSubjectFactory trainSubjectMaker;

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

    void resetDay() {
        // re-initializes the CTC such as a new day
    }

    void getTime() {}

    void setManualMode() {}

    void setMaintenanceMode() {}

    void setAuthority(int trainID, int authority) {}

    void setSuggestedSpeed(int trainID, int speed) {}

    void setSelectedTrain(int trainID) {}

    void setSchedule(int trainID, String schedule) {}

    public void setOccupancy(int blockID, boolean occupied) {}

    public void setLightState(int blockID, boolean lightState) {
        // for setLightState, true is green, false is red
    }

    public void setSwitchState(int switchID, boolean switchState) {
        // for setSwitchState, true is straight, false is diverging
    }

    public void getMode() {}

    public void getOccupancy(int blockID) {}

    public void getSwitchState(int switchID) {}

    public void getLightState(int blockID) {}

    public void setTicketSales(int ticketSales) {}

    public void setTime(int time) {}

}
