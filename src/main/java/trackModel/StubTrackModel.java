package trackModel;

import Common.TrackModel;
import trackModel.TrackLayoutInfo;

import java.util.List;

public class StubTrackModel implements TrackModel {

    TrackModelImpl trackModel;

    @Override
    public void setSignalState(int block, boolean state) {
    }

    @Override
    public void setSwitchState(int block, boolean state) {
    }

    @Override
    public void setCrossing(int block, boolean state) {
    }

    @Override
    public void setBeacon(int block, String beacon) {
    }

    @Override
    public void setBlockOccupied(int block, boolean state) {
    }

    @Override
    public void setTrainAuthority(int trainID, int authority) {
    }

    @Override
    public void setCommandedSpeed(int trainID, double commandedSpeed) {
    }

    @Override
    public void setTemperature(int temp) {
    }

    @Override
    public void setLine(String lines) {
    }

    @Override
    public void setFailure(int block, String failure) {
    }

    @Override
    public void setStation(int block, String station) {
    }

    @Override
    public int getTrainAuthority(int trainID) {
        return 0;
    }

    @Override
    public double getCommandedSpeed(int trainID) {
        return 0;
    }

    @Override
    public boolean getSignalState(int block) {
        return false;
    }

    @Override
    public boolean getSwitchState(int block) {
        return false;
    }

    @Override
    public boolean getCrossingState(int block) {
        return false;
    }

    @Override
    public String getBeacon(int block) {
        return null;
    }

    @Override
    public boolean getBlockOccupied(int block) {
        return false;
    }

    @Override
    public String getFailures(int block) {
        return null;
    }

    @Override
    public int getTemperature() {
        return 0;
    }

    @Override
    public String getStation(int block) {
        return null;
    }

    @Override
    public String getLine() {
        return null;
    }

    public List<TrackLayoutInfo> getBlockInfo() {
        return null;
    }

    @Override
    public int ticketSales() {
        return 0;
    }

    @Override
    public int passengers(int disembarked) {
        return 0;
    }

    @Override
    public void csvParser(String file) {
    }

}
