package Common;

public interface CTCOffice{
    TrainModel dispatch(int trainID, int blockID);
//    void resetDay();
//    void getTime();
    void setManualMode();
    void setMaintenanceMode();
//    void setAuthority(int trainID, int authority);
    void setSuggestedSpeed(int trainID, int speed);
    void setSelectedTrain(int trainID);
    void setSchedule(int trainID, String schedule);
    void setOccupancy(boolean line, int blockID, boolean occupied);
    void setLightState(boolean line, int blockID, boolean lightState);
    void setSwitchState(boolean line, int switchID, boolean switchState);
    boolean getOccupancy(boolean line, int blockID);
    boolean getSwitchState(boolean line, int switchID);
    boolean getLightState(boolean line, int blockID);
    void getMode();
//    void setTicketSales(int ticketSales);
//    void setTime(int time);


}
