package trackModel;

import Utilities.HelperObjects.TrackBlockLine;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrackLineSubject {
  private StringProperty blockNumber;
  private IntegerProperty blockLength;
  private DoubleProperty blockGrade;
  private DoubleProperty speedLimit;
  private DoubleProperty blockElevation;

  // boolean properties
  private BooleanProperty isCrossing;
  private BooleanProperty isUnderground;
  private BooleanProperty isSignal;
  private BooleanProperty isSwitch;
  private BooleanProperty isStation;
  private BooleanProperty isBeacon;
  private StringProperty failure;
  private BooleanProperty trackCircuitFailure;
  private BooleanProperty powerFailure;
  private BooleanProperty brokenRail;
  private BooleanProperty isOccupied;

  // labels
  private BooleanProperty direction;
  private StringProperty passEmbarked;
  private StringProperty passDisembarked;
  private StringProperty ticketSales;
  private StringProperty status;
  private StringProperty switchBlockID;
  private StringProperty switchState;
  private StringProperty signalID;
  private StringProperty signalState;
  private StringProperty crossingState;
  private StringProperty tempDisplay;
  private StringProperty setBeacon;
  private StringProperty nameOfStation;
  private StringProperty trackHeater;
  private IntegerProperty outsideTemp;

  private ObservableList<TrackBlockSubject> blockList;

  private void initializeValues() {
    blockNumber = new SimpleStringProperty();
    blockLength = new SimpleIntegerProperty();
    blockGrade = new SimpleDoubleProperty();
    speedLimit = new SimpleDoubleProperty();
    isCrossing = new SimpleBooleanProperty();
    isUnderground = new SimpleBooleanProperty();
    isSignal = new SimpleBooleanProperty();
    isSwitch = new SimpleBooleanProperty();
    isStation = new SimpleBooleanProperty();
    isBeacon = new SimpleBooleanProperty();
    isOccupied = new SimpleBooleanProperty();
    failure = new SimpleStringProperty();
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
    nameOfStation = new SimpleStringProperty();
    trackHeater = new SimpleStringProperty();
    trackCircuitFailure = new SimpleBooleanProperty();
    powerFailure = new SimpleBooleanProperty();
    brokenRail = new SimpleBooleanProperty();
    blockElevation = new SimpleDoubleProperty();
    outsideTemp = new SimpleIntegerProperty();
    direction = new SimpleBooleanProperty();
    blockList = FXCollections.observableArrayList();
  }

  private TrackLine trackLine;

  public TrackLineSubject() {
    initializeValues();
    initializeListeners();
  }

  public TrackLineSubject(TrackLine line, TrackBlockLine trackBlockLine) {
    this.trackLine = line;
    initializeValues();
    initializeListeners();
    trackBlockLine.forEach(
        (blockID, block) -> {
          //            blockList.add(new TrackBlockSubject(block));
        });
  }

  private void initializeListeners() {

    //        switchState.addListener((observableValue, oldValue, newVal) ->
    // setSwitchState(newVal));
    //        signalState.addListener((observableValue, oldValue, newVal) ->
    // setSignalState(newVal));
    //        isOccupied.addListener((observableValue, oldValue, newVal) -> {
    //            if (newVal) {
    //                setStatus("OCCUPIED");
    //            } else {
    //                setStatus("UNOCCUPIED");
    //            }
    //        });
    //
    //        tempProperty.addListener((observable, oldValue, newValue) -> {
    //                    System.out.println("Temp change detected");
    //                    if (Double.parseDouble(newValue) < 40) {
    //                        setTrackHeater("STATUS - ON");
    //                    } else {
    //                        setTrackHeater("STATUS - OFF");
    //                    }
    //                }
    //        );
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

  public double getSpeedLimit() {
    return speedLimit.get();
  }

  public DoubleProperty speedLimitProperty() {
    return speedLimit;
  }

  public void setSpeedLimit(double speedLimit) {
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

  public String getFailure() {
    return failure.get();
  }

  public StringProperty failureProperty() {
    return failure;
  }

  public void setFailure(String failure) {
    this.failure.set(failure);
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

  // LABELS

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

  public boolean isTrackCircuitFailure() {
    return trackCircuitFailure.get();
  }

  public BooleanProperty trackCircuitFailureProperty() {
    return trackCircuitFailure;
  }

  public void setTrackCircuitFailure(boolean trackCircuitFailure) {
    this.trackCircuitFailure.set(trackCircuitFailure);
  }

  public boolean isPowerFailure() {
    return powerFailure.get();
  }

  public BooleanProperty powerFailureProperty() {
    return powerFailure;
  }

  public void setPowerFailure(boolean powerFailure) {
    this.powerFailure.set(powerFailure);
  }

  public boolean isBrokenRail() {
    return brokenRail.get();
  }

  public BooleanProperty brokenRailProperty() {
    return brokenRail;
  }

  public void setBrokenRail(boolean brokenRail) {
    this.brokenRail.set(brokenRail);
  }

  public double getBlockElevation() {
    return blockElevation.get();
  }

  public DoubleProperty blockElevationProperty() {
    return blockElevation;
  }

  public void setBlockElevation(double blockElevation) {
    this.blockElevation.set(blockElevation);
  }

  public int getOutsideTemp() {
    return outsideTemp.get();
  }

  public IntegerProperty outsideTempProperty() {
    return outsideTemp;
  }

  public void setOutsideTemp(int outsideTemp) {
    this.outsideTemp.set(outsideTemp);
  }

  public boolean isDirection() {
    return direction.get();
  }

  public BooleanProperty directionProperty() {
    return direction;
  }

  public void setDirection(boolean direction) {
    this.direction.set(direction);
  }
}
