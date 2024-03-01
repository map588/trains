package Common;

public interface TrackModel {

    void setLightState(int block, boolean state);
    void setSwitchState(int block, boolean state);
    void setCrossing(int block, boolean state);
    void setBeacon(int block, String beacon);
    void setBlockOccupied(int block, boolean state);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, int commandedSpeed);
    void setTemperature(int temp);
    void addLine(String lines);
    void setFailure(int block, String failure);

    int getTrainAuthority(int trainID);
    int getCommandedSpeed(int trainID);
    boolean getLightState(int block);
    boolean getSwitchState(int block);
    boolean getCrossingState(int block);
    String getBeacon(int block);
    boolean getBlockOccupied(int block);
    String getFailures(int block);
    int getTemperature();
    String getStation(int block);
    String getLine(int block);
    int ticketSales();
    int passengers(int disembarked);


}
