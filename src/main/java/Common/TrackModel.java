package Common;

public interface TrackModel {

    void setSignalState(int block, boolean state);
    void setSwitchState(int block, boolean state);
    void setCrossing(int block, boolean state);
    void setBeacon(int block, String beacon);
    void setBlockOccupied(int block, boolean state);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, double commandedSpeed);
    void setTemperature(int temp);
    void setLine(String lines);
    void setFailure(int block, String failure);
    void setStation(int block, String station);

    int getTrainAuthority(int trainID);
    double getCommandedSpeed(int trainID);
    boolean getSignalState(int block);
    boolean getSwitchState(int block);
    boolean getCrossingState(int block);
    String getBeacon(int block);
    boolean getBlockOccupied(int block);
    String getFailures(int block);
    int getTemperature();
    String getStation(int block);
    String getLine();
    int ticketSales();
    int passengers(int disembarked);
    void csvParser(String file);

}
