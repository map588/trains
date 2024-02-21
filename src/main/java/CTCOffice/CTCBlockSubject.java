package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.*;


public class CTCBlockSubject implements AbstractSubject {
    private final IntegerProperty blockID;
    private final BooleanProperty line;
    private final BooleanProperty occupied;
    private final BooleanProperty hasLight;
    private final BooleanProperty hasSwitchCon;
    private final BooleanProperty hasSwitchDiv;
    private final BooleanProperty hasCrossing;
    private final BooleanProperty lightState;
    private final BooleanProperty switchConState;
    private final BooleanProperty switchDivState;
    private final BooleanProperty crossingState;
    private final DoubleProperty  speedLimit;
    private final IntegerProperty blockLength;


    CTCBlockSubject(CTCBlockInfo block) {
        this.blockID = new SimpleIntegerProperty(this, "blockID", block.getBlockID());
        this.line = new SimpleBooleanProperty(this, "line", block.getLine());
        this.occupied = new SimpleBooleanProperty(this, "occupied", block.getOccupied());
        this.hasLight = new SimpleBooleanProperty(this, "hasLight", block.getHasLight());
        this.hasSwitchCon = new SimpleBooleanProperty(this, "hasSwitchCon", block.getHasSwitchCon());
        this.hasSwitchDiv = new SimpleBooleanProperty(this, "hasSwitchDiv", block.getHasSwitchDiv());
        this.hasCrossing = new SimpleBooleanProperty(this, "hasCrossing", block.getHasCrossing());
        this.lightState = new SimpleBooleanProperty(this, "lightState", block.getLightState());
        this.switchConState = new SimpleBooleanProperty(this, "switchConState", block.getSwitchConState());
        this.switchDivState = new SimpleBooleanProperty(this, "switchDivState", block.getSwitchDivState());
        this.crossingState = new SimpleBooleanProperty(this, "crossingState", block.getCrossingState());
        this.speedLimit = new SimpleDoubleProperty(this, "speedLimit", block.getSpeedLimit());
        this.blockLength = new SimpleIntegerProperty(this, "blockLength", block.getBlockLength());
        CTCBlockSubjectFactory.getInstance().registerSubject(block.getBlockID(), this);
        occupied.addListener((observable, oldValue, newValue) -> block.setOccupied(newValue));
        lightState.addListener((observable, oldValue, newValue) -> block.setLightState(newValue));
        switchConState.addListener((observable, oldValue, newValue) -> block.setSwitchConState(newValue));
        switchDivState.addListener((observable, oldValue, newValue) -> block.setSwitchDivState(newValue));
        crossingState.addListener((observable, oldValue, newValue) -> block.setCrossingState(newValue));
    }
    public BooleanProperty getBooleanProperty(String propertyName) {
        return switch (propertyName) {
            case "line" -> line;
            case "occupied" -> occupied;
            case "hasLight" -> hasLight;
            case "hasSwitchCon" -> hasSwitchCon;
            case "hasSwitchDiv" -> hasSwitchDiv;
            case "hasCrossing" -> hasCrossing;
            case "lightState" -> lightState;
            case "switchConState" -> switchConState;
            case "switchDivState" -> switchDivState;
            case "crossingState" -> crossingState;
            default -> null;
        };
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        return switch (propertyName) {
            case "speedLimit" -> speedLimit;
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case "blockID" -> blockID;
            case "blockLength" -> blockLength;
            default -> null;
        };
    }

    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case "line" -> {
                updateProperty(line, newValue);
            }
            case "occupied" -> {
                updateProperty(occupied, newValue);
                occupied.set((Boolean) newValue);
            }
            case "hasLight" -> {
                updateProperty(hasLight, newValue);
                hasLight.set((Boolean) newValue);
            }
            case "hasSwitchCon" -> {
                updateProperty(hasSwitchCon, newValue);
                hasSwitchCon.set((Boolean) newValue);
            }
            case "hasSwitchDiv" -> {
                updateProperty(hasSwitchDiv, newValue);
                hasSwitchDiv.set((Boolean) newValue);
            }
            case "hasCrossing" -> {
                updateProperty(hasCrossing, newValue);
                hasCrossing.set((Boolean) newValue);
            }
            case "lightState" -> {
                updateProperty(lightState, newValue);
                lightState.set((Boolean) newValue);
            }
            case "switchConState" -> {
                updateProperty(switchConState, newValue);
                switchConState.set((Boolean) newValue);
            }
            case "switchDivState" -> {
                updateProperty(switchDivState, newValue);
                switchDivState.set((Boolean) newValue);
            }
            case "crossingState" -> {
                updateProperty(crossingState, newValue);
                crossingState.set((Boolean) newValue);
            }
            case "speedLimit" -> {
                updateProperty(speedLimit, newValue);
                speedLimit.set((Double) newValue);
            }
            case "blockLength" -> {
                updateProperty(blockLength, newValue);
                blockLength.set((Integer) newValue);
            }
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

}

