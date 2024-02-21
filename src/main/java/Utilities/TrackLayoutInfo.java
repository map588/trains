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


    //public CheckBox select;

}
