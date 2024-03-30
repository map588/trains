package Common;

import Utilities.Beacon;

public interface TrackModel {

    //Vital Setters
    void setSignalState(int block, boolean state);
    void setSwitchState(int block, boolean state);
    void setCrossing(int block, boolean state);
    void setBeacon(int block, Beacon beacon);
    void setTrainAuthority(int blockID, int authority);
    void setCommandedSpeed(int blockID, double commandedSpeed);

    //Vital Getters
    boolean getLightState(int block);
    boolean getSwitchState(int block);
    boolean getCrossingState(int block);

    //Failure Setters
    void setBrokenRail(Integer blockID, boolean state);
    void setPowerFailure(Integer blockID, boolean state);
    void setTrackCircuitFailure(Integer blockID, boolean state);

    //Failure Getters
    boolean getBrokenRail(Integer blockID);
    boolean getPowerFailure(Integer blockID);
    boolean getTrackCircuitFailure(Integer blockID);


    //Non-Vitals
    void setPassengersDisembarked(TrainModel train, int disembarked);

    int getTicketSales();
    int getPassengersEmbarked(TrainModel train, int embarked);

}
