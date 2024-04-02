package trackModel;

import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Enums.BlockType;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;

import java.util.Optional;

import static Utilities.Enums.Direction.NORTH;

class TrackBlock {
    // Block Information
     final int blockID;
     final boolean isUnderground;
     final boolean isSwitch;
     final BlockType blockType;
     final Lines line;

     final Connection northID;
     final Connection southID;

    // Physical properties
     final double grade;
     final double elevation;
     final double cumulativeElevation;
     final double speedLimit;
     final double length;

    // Specific Block Information
     final Optional<SwitchState> switchInfo;
     final Optional<CrossingState> crossingInfo;
     final Optional<StationInfo> stationInfo;
     final FailureInfo failureInfo;

     boolean maintenanceMode;
     boolean lightState;

    /**
     * Constructs a new TrackBlock object based on the provided BasicBlock information.
     *
     * @param blockInfo the BasicBlock object containing the block information
     * @throws IllegalArgumentException if the provided block information is invalid
     */
    TrackBlock(BasicBlock blockInfo) {
        validateBlockInfo(blockInfo);

        this.blockID = blockInfo.blockNumber();
        this.isUnderground = blockInfo.isUnderground();
        this.isSwitch = blockInfo.isSwitch();
        this.blockType = blockInfo.blockType();
        this.grade = blockInfo.blockGrade();
        this.elevation = blockInfo.elevation();
        this.cumulativeElevation = blockInfo.cumulativeElevation();
        this.speedLimit = blockInfo.speedLimit();
        this.length = blockInfo.blockLength();
        this.line = Lines.valueOf(blockInfo.trackLine());

        this.switchInfo = blockInfo.isSwitch() ? Optional.of(new SwitchState(blockInfo.nextBlock())) : Optional.empty();
        this.crossingInfo = blockInfo.blockType() == BlockType.CROSSING ? Optional.of(new CrossingState(false)) : Optional.empty();
        this.stationInfo = blockInfo.blockType() == BlockType.STATION ? Optional.of(new StationInfo(blockInfo.stationName().get(), blockInfo.doorDirection().get())) : Optional.empty();

        if (isSwitch) {
            this.northID = new Connection(-1, false);
            this.southID = new Connection(-1, false);
        } else {
            this.northID = blockInfo.nextBlock().north();
            this.southID = blockInfo.nextBlock().south();
        }

        this.failureInfo = new FailureInfo();
    }

    /**
     * Validates the provided BasicBlock object to ensure it contains valid data.
     *
     * @param blockInfo the BasicBlock object to validate
     * @throws IllegalArgumentException if the block information is invalid
     */
     void validateBlockInfo(BasicBlock blockInfo) {
        if (blockInfo.blockType() == BlockType.STATION) {
            if (blockInfo.stationName().isEmpty() || blockInfo.doorDirection().isEmpty()) {
                throw new IllegalArgumentException("Station block must have a station name and door direction");
            }
        }else if(blockInfo.isSwitch()){
            if(blockInfo.nextBlock().northDefault() == null || blockInfo.nextBlock().southDefault() == null){
                throw new IllegalArgumentException("Switch block must have a default connection for both directions");
            }
        }
    }

    /**
     * Returns the next block connection based on the provided direction.
     *
     * @param direction the direction of the next block
     * @return the Connection object representing the next block
     */
     Connection getNextBlock(Direction direction) {
        if (!isSwitch) {
            return direction == NORTH ? northID : southID;
        } else {
            return switchInfo.map(info -> {
                if (info.isSwitchState()) {
                    return direction == NORTH ? info.getNorthAlt().orElse(null) : info.getSouthAlt().orElse(null);
                } else {
                    return direction == NORTH ? info.getNorthDef() : info.getSouthDef();
                }
            }).orElse(null);
        }
    }

     void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }



    /**
     * Sets the state of the switch.
     *
     * @param state the new state of the switch
     * @throws IllegalArgumentException if the block is not a switch
     */
    void setSwitchState(boolean state) {
        switchInfo.ifPresent(info -> info.setSwitchState(state));
    }

    /**
     * Sets the automatic state of the switch.
     *
     * @param state the new automatic state of the switch
     * @throws IllegalArgumentException if the block is not a switch
     */
     void setSwitchStateAuto(boolean state) {
        switchInfo.ifPresent(info -> info.setSwitchStateAuto(state));
    }

    /**
     * Sets the state of the crossing.
     *
     * @param state the new state of the crossing
     * @throws IllegalArgumentException if the block is not a crossing
     */
     void setCrossingState(boolean state) {
        crossingInfo.ifPresent(info -> info.setCrossingState(state));
    }

    /**
     * Sets the power failure state of the block.
     *
     * @param state the new power failure state
     */
     void setPowerFailure(boolean state) {
        failureInfo.setPowerFailure(state);
    }

    /**
     * Sets the track circuit failure state of the block.
     *
     * @param state the new track circuit failure state
     */
     void setTrackCircuitFailure(boolean state) {
        failureInfo.setTrackCircuitFailure(state);
    }

    /**
     * Sets the broken rail state of the block.
     *
     * @param state the new broken rail state
     */
     void setBrokenRail(boolean state) {
        failureInfo.setBrokenRail(state);
    }

    /**
     * Sets the maintenance mode of the block.
     *
     * @param state the new maintenance mode state
     */
     void setUnderMaintenance(boolean state) {
        maintenanceMode = state;
    }

    // Inner classes for specific block information

    static class SwitchState {
        private final Connection northDef;
        private final Connection southDef;
        private final Optional<Connection> northAlt;
        private final Optional<Connection> southAlt;

        private boolean switchState;
        private boolean switchStateAuto;

        SwitchState(BasicBlock.NextBlock nextBlock) {
            this.northDef = nextBlock.northDefault();
            this.southDef = nextBlock.southDefault();
            this.northAlt = Optional.ofNullable(nextBlock.northAlternate().blockNumber() != -1 ? nextBlock.northAlternate() : null);
            this.southAlt = Optional.ofNullable(nextBlock.southAlternate().blockNumber() != -1 ? nextBlock.southAlternate() : null);
        }

         Connection getNorthDef() {
            return northDef;
        }

         Connection getSouthDef() {
            return southDef;
        }

         Optional<Connection> getNorthAlt() {
            return northAlt;
        }

         Optional<Connection> getSouthAlt() {
            return southAlt;
        }

         boolean isSwitchState() {
            return switchState;
        }

         void setSwitchState(boolean switchState) {
            this.switchState = switchState;
        }

         boolean isSwitchStateAuto() {
            return switchStateAuto;
        }

         void setSwitchStateAuto(boolean switchStateAuto) {
            this.switchStateAuto = switchStateAuto;
        }
    }

     static class StationInfo {
        private final String stationName;
        private final String doorDirection;

        int passengersWaiting;
        int passengersEmbarked;
        int passengersDisembarked;


        StationInfo(String stationName, String doorDirection) {
            this.stationName = stationName;
            this.doorDirection = doorDirection;
        }

        String getStationName() {
            return stationName;
        }

        String getDoorDirection() {
            return doorDirection;
        }

        int getPassengersWaiting() {
            return passengersWaiting;
        }

        void setPassengersWaiting(int passengersWaiting) {
            this.passengersWaiting = passengersWaiting;
        }


        int getPassengersEmbarked() {
            return passengersEmbarked;
        }

        void setPassengersEmbarked(int passengersEmbarked) {
            this.passengersEmbarked = passengersEmbarked;
        }

        int getPassengersDisembarked() {
            return passengersDisembarked;
        }

        void setPassengersDisembarked(int passengersDisembarked) {
            this.passengersDisembarked = passengersDisembarked;
        }
    }

     static class CrossingState {
        private boolean crossingState;

        CrossingState(boolean crossingState) {
            this.crossingState = crossingState;
        }

        boolean isCrossingState() {
            return crossingState;
        }

        void setCrossingState(boolean crossingState) {
            this.crossingState = crossingState;
        }
    }

     static class FailureInfo {
        private boolean hasFailure;
        private boolean brokenRail;
        private boolean trackCircuitFailure;
        private boolean powerFailure;

         boolean hasFailure() {
            return brokenRail || trackCircuitFailure || powerFailure;
        }

         boolean isBrokenRail() {
            return brokenRail;
        }

         void setBrokenRail(boolean brokenRail) {
            this.brokenRail = brokenRail;
        }

         boolean isTrackCircuitFailure() {
            return trackCircuitFailure;
        }

         void setTrackCircuitFailure(boolean trackCircuitFailure) {
            this.trackCircuitFailure = trackCircuitFailure;
        }

         boolean isPowerFailure() {
            return powerFailure;
        }

         void setPowerFailure(boolean powerFailure) {
            this.powerFailure = powerFailure;
        }
    }
}