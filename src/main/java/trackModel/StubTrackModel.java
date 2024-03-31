package trackModel;

import Common.TrackModel;

import java.util.List;

public class StubTrackModel {


    
    public void setLightState(int block, boolean state) {
    }

    
    public void setSwitchState(int block, boolean state) {
    }

    
    public void setCrossing(int block, boolean state) {
    }

    
    public void setBeacon(int block, String beacon) {
    }

    
    public void setBlockOccupied(int block, boolean state) {
    }

    
    public void setTrainAuthority(int trainID, int authority) {
    }

    
    public void setCommandedSpeed(int trainID, double commandedSpeed) {
    }

    
    public void setTemperature(int temp) {
    }

    
    public void setLine(String lines) {
    }

    
    public void setFailure(int block, String failure) {
    }

    
    public void setStation(int block, String station) {
    }

    
    public int getTrainAuthority(int trainID) {
        return 0;
    }

    
    public double getCommandedSpeed(int trainID) {
        return 0;
    }

    
    public boolean getLightState(int block) {
        return false;
    }

    
    public boolean getSwitchState(int block) {
        return false;
    }

    
    public boolean getCrossingState(int block) {
        return false;
    }

    
    public String getBeacon(int block) {
        return null;
    }

    
    public boolean getBlockOccupied(int block) {
        return false;
    }

    
    public String getFailures(int block) {
        return null;
    }

    
    public int getTemperature() {
        return 0;
    }

    
    public String getStation(int block) {
        return null;
    }

    
    public String getLine() {
        return null;
    }

    
    public int getTicketSales() {
        return 0;
    }

    
    public int getPassengersDisembarked(int disembarked) {
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
