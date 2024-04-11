package Common;

import Utilities.Enums.Lines;
public interface TrackModel {

    //Vital Setters
    void setLightState(int block, boolean state);
    void setSwitchState(int block, boolean state);
    void setCrossing(int block, boolean state);

    //Train Commands
    void setTrainAuthority(Integer blockID, int authority);
    void setCommandedSpeed(Integer blockID, double commandedSpeed);


    //Failure Setters
    void setBrokenRail(Integer blockID, boolean state);
    void setPowerFailure(Integer blockID, boolean state);
    void setTrackCircuitFailure(Integer blockID, boolean state);
    void fixTrackFailure(Integer blockID);

    //Failure Getters
    boolean getBrokenRail(Integer blockID);
    boolean getPowerFailure(Integer blockID);
    boolean getTrackCircuitFailure(Integer blockID);


    //Non-Vitals
    void disembarkPassengers(TrainModel train, int disembarked);
    int embarkPassengers(TrainModel train);
    int getTicketSales();

    Lines getLine();

}
