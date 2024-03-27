package trackModel;

import Common.TrackModel;

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

    @Override
    public int ticketSales() {
        return 0;
    }

    @Override
    public int passengers(int disembarked) {
        return 0;
    }

 // potential unit tests


//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//    public class TrackModelImplTest {
//
//        private TrackModelImpl trackModel;
//
//        @BeforeEach
//        public void setup() {
//            trackModel = new TrackModelImpl();
//        }
//
//        @Test
//        public void setAndGetTrainAuthority() {
//            trackModel.setTrainAuthority(1, 10);
//            assertEquals(10, trackModel.getTrainAuthority(1));
//        }
//
//        @Test
//        public void setAndGetCommandedSpeed() {
//            trackModel.setCommandedSpeed(1, 50.0);
//            assertEquals(50.0, trackModel.getCommandedSpeed(1));
//        }
//
//        @Test
//        public void setAndGetFailure() {
//            trackModel.setFailure(1, "Broken Rail");
//            assertEquals("Broken Rail", trackModel.getFailures(1));
//        }
//
//        @Test
//        public void setAndGetSignalState() {
//            trackModel.setSignalState(1, true);
//            assertTrue(trackModel.getSignalState(1));
//        }
//
//        @Test
//        public void setAndGetSwitchState() {
//            trackModel.setSwitchState(1, true);
//            assertTrue(trackModel.getSwitchState(1));
//        }
//
//        @Test
//        public void setAndGetCrossing() {
//            trackModel.setCrossing(1, true);
//            assertTrue(trackModel.getCrossingState(1));
//        }
//
//        @Test
//        public void setAndGetBeacon() {
//            trackModel.setBeacon(1, "Beacon1");
//            assertEquals("Beacon1", trackModel.getBeacon(1));
//        }
//
//        @Test
//        public void setAndGetBlockOccupied() {
//            trackModel.setBlockOccupied(1, true);
//            assertTrue(trackModel.getBlockOccupied(1));
//        }
//
//        @Test
//        public void setAndGetStation() {
//            trackModel.setStation(1, "Station1");
//            assertEquals("Station1", trackModel.getStation(1));
//        }
//
//        @Test
//        public void getTemperature() {
//            assertEquals(0, trackModel.getTemperature());
//        }
//
//        @Test
//        public void getLine() {
//            trackModel.setLine("Line1");
//            assertEquals("Line1", trackModel.getLine());
//        }
//
//        @Test
//        public void ticketSales() {
//            assertTrue(trackModel.ticketSales() >= 0 && trackModel.ticketSales() <= 1);
//        }
//
//        @Test
//        public void passengers() {
//            assertTrue(trackModel.passengers(1) >= 0 && trackModel.passengers(1) <= 1);
//        }
//    }


}
