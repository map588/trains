package trackModel;

import javafx.beans.property.*;

public class TrackLayoutInfo {

    private final StringProperty section;
    private final StringProperty blockNumber;
    private final IntegerProperty blockLength;
    private final DoubleProperty blockGrade;
    private final IntegerProperty speedLimit;

    // boolean properties
    private final BooleanProperty isCrossing;
    private final BooleanProperty isUnderground;
    private final BooleanProperty isSignal;
    private final BooleanProperty isSwitch;
    private final BooleanProperty isStation;
    private final BooleanProperty isBeacon;
    private final BooleanProperty hasFailure;
    private final BooleanProperty isOccupied;

    //labels
    private final StringProperty passEmbarked;
    private final StringProperty passDisembarked;
    private final StringProperty ticketSales;
    private final StringProperty status;
    private final StringProperty switchBlockID;
    private final StringProperty switchMain;
    private final StringProperty switchAlt;
    private final StringProperty switchState;
    private final StringProperty signalID;
    private final StringProperty signalState;
    private final StringProperty crossingState;
    private final StringProperty tempDisplay;
    private final StringProperty setBeacon;
    private final StringProperty nameOfStation;
    private final StringProperty trackHeater;


    public TrackLayoutInfo() {
        blockNumber = new SimpleStringProperty();
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
        passEmbarked = new SimpleStringProperty();
        passDisembarked = new SimpleStringProperty();
        ticketSales = new SimpleStringProperty();
        status = new SimpleStringProperty();
        switchBlockID = new SimpleStringProperty();
        switchState = new SimpleStringProperty();
        signalID = new SimpleStringProperty();
        signalState = new SimpleStringProperty();
        crossingState = new SimpleStringProperty();
        tempDisplay = new SimpleStringProperty();
        setBeacon = new SimpleStringProperty();
        switchMain = new SimpleStringProperty();
        switchAlt = new SimpleStringProperty();
        nameOfStation = new SimpleStringProperty();
        trackHeater = new SimpleStringProperty("STATUS - OFF");

        switchState.addListener((observableValue, oldValue, newVal) -> setSwitchState(newVal));
        signalState.addListener((observableValue, oldValue, newVal) -> setSignalState(newVal));
        isOccupied.addListener((observableValue, oldValue, newVal) -> {
            if (newVal) {
                setStatus("OCCUPIED");
            } else {
                setStatus("UNOCCUPIED");
            }
        });

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

    public String getBlockNumber() {
        return blockNumber.get();
    }

    public StringProperty blockNumberProperty() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
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

    //LABELS

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

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getSwitchBlockID() {
        return switchBlockID.get();
    }

    public StringProperty switchBlockIDProperty() {
        return switchBlockID;
    }

    public void setSwitchBlockID(String switchBlockID) {
        this.switchBlockID.set(switchBlockID);
    }

    public String getSwitchState() {
        return switchState.get();
    }

    public StringProperty switchStateProperty() {
        return switchState;
    }

    public void setSwitchState(String switchState) {

        if (switchState.equals("Main")) {
            this.switchBlockID.set(switchMain.get());
        } else {
            this.switchBlockID.set(switchAlt.get());
        }


        this.switchState.set(switchState);
    }

    public String getSignalID() {
        return signalID.get();
    }

    public StringProperty signalIDProperty() {
        return signalID;
    }

    public void setSignalID(String signalID) {
        this.signalID.set(signalID);
    }

    public String getSignalState() {
        return signalState.get();
    }

    public StringProperty signalStateProperty() {
        return signalState;
    }

    public void setSignalState(String signalState) {
        this.signalState.set(signalState);
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

    public String getSetBeacon() {
        return setBeacon.get();
    }

    public StringProperty setBeaconProperty() {
        return setBeacon;
    }

    public void setSetBeacon(String setBeacon) {
        this.setBeacon.set(setBeacon);
    }

    public String getSwitchMain() {
        return switchMain.get();
    }

    public StringProperty switchMainProperty() {
        return switchMain;
    }

    public void setSwitchMain(String switchMain) {
        this.switchMain.set(switchMain);
    }

    public String getSwitchAlt() {
        return switchAlt.get();
    }

    public StringProperty switchAltProperty() {
        return switchAlt;
    }

    public void setSwitchAlt(String switchAlt) {
        this.switchAlt.set(switchAlt);
    }

    public String getNameOfStation() {
        return nameOfStation.get();
    }

    public StringProperty nameOfStationProperty() {
        return nameOfStation;
    }

    public void setNameOfStation(String nameOfStation) {
        this.nameOfStation.set(nameOfStation);
    }

    public String getTrackHeater() {
        return trackHeater.get();
    }

    public StringProperty trackHeaterProperty() {
        return trackHeater;
    }

    public void setTrackHeater(String trackHeater) {
        this.trackHeater.set(trackHeater);
    }

}