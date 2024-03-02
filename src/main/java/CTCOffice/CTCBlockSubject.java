package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.*;
import javafx.scene.paint.Paint;

/**
 * This class represents a subject in the Observer pattern for the CTCBlockInfo class.
 * It contains properties of the block that can be observed by other classes.
 * It also contains methods for getting and setting these properties.
 */
public class CTCBlockSubject implements AbstractSubject {
    private final IntegerProperty blockID, convergingBlockID, divergingBlockOneID, divergingBlockTwoID;
    private final BooleanProperty line, occupied, hasLight, hasSwitchCon, hasSwitchDiv, hasCrossing, switchLightState,
                                    crossingState, switchState, underMaintenance;
    private final DoubleProperty  speedLimit;
    private final IntegerProperty blockLength;
    private final ObjectProperty<Paint> switchLightColor, crossingLightColor, maintenanceLightColor;
    private final StringProperty switchStateString;

    CTCBlockInfo blockInfo;

    /**
     * Constructor for the CTCBlockSubject class.
     * Initializes the properties with the values from the given block.
     * Also sets up listeners for changes in the properties.
     */
    CTCBlockSubject(CTCBlockInfo block) {
        this.blockID = new SimpleIntegerProperty(this, "blockID", block.getBlockID());
        this.line = new SimpleBooleanProperty(this, "line", block.getLine());
        this.occupied = new SimpleBooleanProperty(this, "occupied", block.getOccupied());
        this.hasLight = new SimpleBooleanProperty(this, "hasLight", block.getHasLight());
        this.hasSwitchCon = new SimpleBooleanProperty(this, "hasSwitchCon", block.getHasSwitchCon());
        this.hasSwitchDiv = new SimpleBooleanProperty(this, "hasSwitchDiv", block.getHasSwitchDiv());
        this.hasCrossing = new SimpleBooleanProperty(this, "hasCrossing", block.getHasCrossing());
        this.switchLightState = new SimpleBooleanProperty(this, "switchLightState", block.getSwitchLightState());
        this.crossingState = new SimpleBooleanProperty(this, "crossingState", block.getCrossingState());
        this.speedLimit = new SimpleDoubleProperty(this, "speedLimit", block.getSpeedLimit());
        this.blockLength = new SimpleIntegerProperty(this, "blockLength", block.getBlockLength());
        this.switchLightColor = new SimpleObjectProperty<>(this, "switchLightColor", block.getSwitchLightColor());
        this.crossingLightColor = new SimpleObjectProperty<>(this, "crossingLightColor", block.getCrossingLightColor());
        this.maintenanceLightColor = new SimpleObjectProperty<>(this, "maintenanceLightColor", block.getMaintenanceLightColor());
        this.convergingBlockID = new SimpleIntegerProperty(this, "convergingBlockID", block.getConvergingBlockID());
        this.divergingBlockOneID = new SimpleIntegerProperty(this, "divergingBlockOneID", block.getDivergingBlockOneID());
        this.divergingBlockTwoID = new SimpleIntegerProperty(this, "divergingBlockTwoID", block.getDivergingBlockTwoID());
        this.switchState = new SimpleBooleanProperty(this, "switchState", block.getSwitchState());
        this.switchStateString = new SimpleStringProperty(this, "switchStateString", block.getSwitchStateString());
        this.underMaintenance = new SimpleBooleanProperty(this, "underMaintenance", block.getUnderMaintenance());
        this.blockInfo = block;

        occupied.addListener((observable, oldValue, newValue) -> block.setOccupied(newValue));

        switchLightState.addListener((observable, oldValue, newValue) -> {
            block.setSwitchLightState(newValue);
            block.updateSwitchLightColor();
        });

        switchState.addListener((observable, oldValue, newValue) -> block.setSwitchState(newValue));

        underMaintenance.addListener((observable, oldValue, newValue) -> {
            block.setUnderMaintenance(newValue);
            block.updateMaintenanceLightColor();
        });

        crossingState.addListener((observable, oldValue, newValue) -> {
            block.setCrossingState(newValue);
            block.updateCrossingLightColor();
        });
    }

    CTCBlockInfo getBlockInfo() {
        return blockInfo;
    }

    public BooleanProperty getBooleanProperty(String propertyName) {
        if(blockInfo == null) {
            System.err.println("Null value for property " + propertyName);
            return null;
        }
        return switch (propertyName) {
            case "line" -> line;
            case "occupied" -> occupied;
            case "hasLight" -> hasLight;
            case "hasSwitchCon" -> hasSwitchCon;
            case "hasSwitchDiv" -> hasSwitchDiv;
            case "hasCrossing" -> hasCrossing;
            case "switchLightState" -> switchLightState;
            case "crossingState" -> crossingState;
            case "switchState" -> switchState;
            case "underMaintenance" -> underMaintenance;
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
            case "convergingBlockID" -> convergingBlockID;
            case "divergingBlockOneID" -> divergingBlockOneID;
            case "divergingBlockTwoID" -> divergingBlockTwoID;
            default -> null;
        };
    }

    public ObjectProperty<Paint> getPaintProperty(String propertyName) {
        return switch (propertyName) {
            case "switchLightColor" -> switchLightColor;
            case "crossingLightColor" -> crossingLightColor;
            case "maintenanceLightColor" -> maintenanceLightColor;
            default -> null;
        };
    }

    public StringProperty getStringProperty(String propertyName) {
        return switch (propertyName) {
            case "switchStateString" -> switchStateString;
            default -> null;
        };
    }

    public void setStringProperty(String propertyName) {
        if(propertyName == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
                switchStateString.set(blockInfo.getSwitchStateString());
                System.out.println("setting new Switch state string for block " + blockID.get() + " to " + blockInfo.getSwitchStateString());

    }

    public void setPaintProperty(String propertyName) {
        switch (propertyName) {
            case "switchLightColor" -> switchLightColor.set(blockInfo.getSwitchLightColor());
            case "crossingLightColor" -> crossingLightColor.set(blockInfo.getCrossingLightColor());
            case "maintenanceLightColor" -> maintenanceLightColor.set(blockInfo.getMaintenanceLightColor());
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    /**
     * Sets the value of a property.
     * If the new value is null, it prints an error message.
     */
    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case "line"                 -> updateProperty(line, newValue);
            case "occupied"             -> updateProperty(occupied, newValue);
            case "hasLight"             -> updateProperty(hasLight, newValue);
            case "hasSwitchCon"         -> updateProperty(hasSwitchCon, newValue);
            case "hasSwitchDiv"         -> updateProperty(hasSwitchDiv, newValue);
            case "hasCrossing"          -> updateProperty(hasCrossing, newValue);
            case "switchLightState"     -> updateProperty(switchLightState, newValue);
            case "crossingState"        -> updateProperty(crossingState, newValue);
            case "speedLimit"           -> updateProperty(speedLimit, newValue);
            case "blockID"              -> updateProperty(blockID, newValue);
            case "blockLength"          -> updateProperty(blockLength, newValue);
            case "convergingBlockID"    -> updateProperty(convergingBlockID, newValue);
            case "divergingBlockOneID"  -> updateProperty(divergingBlockOneID, newValue);
            case "divergingBlockTwoID"  -> updateProperty(divergingBlockTwoID, newValue);
            case "switchState"          -> updateProperty(switchState, newValue);
            case "underMaintenance"     -> updateProperty(underMaintenance, newValue);

            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    public Property<?> getProperty(String propertyName) {
        return null;
    }


}

