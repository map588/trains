package Utilities;

import javafx.beans.property.*;

public class TrackLayoutInfo {

    private StringProperty section;
    private IntegerProperty blockNumber;
    private IntegerProperty blockLength;
    private DoubleProperty blockGrade;
    private IntegerProperty speedLimit;

    // boolean properties
    private BooleanProperty isCrossing;
    private BooleanProperty isUnderground;
    private BooleanProperty isSignal;
    private BooleanProperty isSwitch;
    private BooleanProperty isStation;
    private BooleanProperty isBeacon;
    private BooleanProperty hasFailure;
    private BooleanProperty isOccupied;

    //labels
    private StringProperty passEmbarked;
    private StringProperty passDisembarked;
    private StringProperty ticketSales;
    private StringProperty temperature;
    private StringProperty status;
    private StringProperty switchID;
    private StringProperty switchState;
    private StringProperty lightID;
    private StringProperty lightState;
    private StringProperty crossingState;
    private StringProperty tempDisplay;

    public TrackLayoutInfo(){
        blockNumber = new SimpleIntegerProperty();
        blockLength = new SimpleIntegerProperty();
        blockGrade = new SimpleDoubleProperty();
        speedLimit = new SimpleIntegerProperty();
        isCrossing = new SimpleBooleanProperty();
        isUnderground = new SimpleBooleanProperty();
        isSignal = new SimpleBooleanProperty();
        isSwitch = new SimpleBooleanProperty();
        isStation = new SimpleBooleanProperty();
        isBeacon = new SimpleBooleanProperty();
        isOccupied = new SimpleBooleanProperty();
        hasFailure = new SimpleBooleanProperty();
        section = new SimpleStringProperty();
    }

    public String getSection() {
        return section.get();
    }
    public StringProperty sectionProperty() {
        return section;
    }
    public void setSection(String section) {
        this.section.set(section);
    }

    public int getBlockNumber() {
        return blockNumber.get();
    }
    public IntegerProperty blockNumberProperty() {
        return blockNumber;
    }
    public void setBlockNumber(int blockNumber) {
        this.blockNumber.set(blockNumber);
    }

    public int getBlockLength() {
        return blockLength.get();
    }
    public IntegerProperty blockLengthProperty() {
        return blockLength;
    }
    public void setBlockLength(int blockLength) {
        this.blockLength.set(blockLength);
    }

    public double getBlockGrade() {
        return blockGrade.get();
    }
    public DoubleProperty blockGradeProperty() {
        return blockGrade;
    }
    public void setBlockGrade(double blockGrade) {
        this.blockGrade.set(blockGrade);
    }

    public int getSpeedLimit() {
        return speedLimit.get();
    }
    public IntegerProperty speedLimitProperty() {
        return speedLimit;
    }
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit.set(speedLimit);
    }

    public boolean isIsCrossing() {
        return isCrossing.get();
    }
    public BooleanProperty isCrossingProperty() {
        return isCrossing;
    }
    public void setIsCrossing(boolean isCrossing) {
        this.isCrossing.set(isCrossing);
    }

    public boolean isIsUnderground() {
        return isUnderground.get();
    }
    public BooleanProperty isUndergroundProperty() {
        return isUnderground;
    }
    public void setIsUnderground(boolean isUnderground) {
        this.isUnderground.set(isUnderground);
    }

    public boolean isIsSignal() {
        return isSignal.get();
    }
    public BooleanProperty isSignalProperty() {
        return isSignal;
    }
    public void setIsSignal(boolean isSignal) {
        this.isSignal.set(isSignal);
    }

    public boolean isIsSwitch() {
        return isSwitch.get();
    }
    public BooleanProperty isSwitchProperty() {
        return isSwitch;
    }
    public void setIsSwitch(boolean isSwitch) {
        this.isSwitch.set(isSwitch);
    }

    public boolean isIsStation() {
        return isStation.get();
    }
    public BooleanProperty isStationProperty() {
        return isStation;
    }
    public void setIsStation(boolean isStation) {
        this.isStation.set(isStation);
    }

    public boolean isIsBeacon() {
        return isBeacon.get();
    }
    public BooleanProperty isBeaconProperty() {
        return isBeacon;
    }
    public void setIsBeacon(boolean isBeacon) {
        this.isBeacon.set(isBeacon);
    }

    public boolean isHasFailure() {
        return hasFailure.get();
    }
    public BooleanProperty hasFailureProperty() {
        return hasFailure;
    }
    public void setHasFailure(boolean hasFailure) {
        this.hasFailure.set(hasFailure);
    }

    public boolean isIsOccupied() {
        return isOccupied.get();
    }

    public BooleanProperty isOccupiedProperty() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied.set(isOccupied);
    }

    public String getPassEmbarked() {
        return passEmbarked.get();
    }

    public StringProperty passEmbarkedProperty() {
        return passEmbarked;
    }

    public void setPassEmbarked(String passEmbarked) {
        this.passEmbarked.set(passEmbarked);
    }

    public String getPassDisembarked() {
        return passDisembarked.get();
    }

    public StringProperty passDisembarkedProperty() {
        return passDisembarked;
    }

    public void setPassDisembarked(String passDisembarked) {
        this.passDisembarked.set(passDisembarked);
    }

    public String getTicketSales() {
        return ticketSales.get();
    }

    public StringProperty ticketSalesProperty() {
        return ticketSales;
    }

    public void setTicketSales(String ticketSales) {
        this.ticketSales.set(ticketSales);
    }

    public String getTemperature() {
        return temperature.get();
    }

    public StringProperty temperatureProperty() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature.set(temperature);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getSwitchID() {
        return switchID.get();
    }

    public StringProperty switchIDProperty() {
        return switchID;
    }

    public void setSwitchID(String switchID) {
        this.switchID.set(switchID);
    }

    public String getSwitchState() {
        return switchState.get();
    }

    public StringProperty switchStateProperty() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState.set(switchState);
    }

    public String getLightID() {
        return lightID.get();
    }

    public StringProperty lightIDProperty() {
        return lightID;
    }

    public void setLightID(String lightID) {
        this.lightID.set(lightID);
    }

    public String getLightState() {
        return lightState.get();
    }

    public StringProperty lightStateProperty() {
        return lightState;
    }

    public void setLightState(String lightState) {
        this.lightState.set(lightState);
    }

    public String getCrossingState() {
        return crossingState.get();
    }

    public StringProperty crossingStateProperty() {
        return crossingState;
    }

    public void setCrossingState(String crossingState) {
        this.crossingState.set(crossingState);
    }

    public String getTempDisplay() {
        return tempDisplay.get();
    }

    public StringProperty tempDisplayProperty() {
        return tempDisplay;
    }

    public void setTempDisplay(String tempDisplay) {
        this.tempDisplay.set(tempDisplay);
    }


    //public CheckBox select;

}
