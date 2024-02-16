package CTCOffice;

import Common.CTCOffice;
import Common.trainModel;

class CTCOfficeImpl implements CTCOffice {


    public trainModel dispatch(int trainID, int blockID) {
        return null;
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
