package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.*;


public class CTCBlockSubject implements AbstractSubject {
    private IntegerProperty blockID;
    private BooleanProperty line;
    private BooleanProperty occupied;
    private BooleanProperty hasLight;
    private BooleanProperty hasSwitch;
    private BooleanProperty hasCrossing;
    private BooleanProperty lightState;
    private BooleanProperty switchState;
    private BooleanProperty crossingState;
    private DoubleProperty  speedLimit;
    private IntegerProperty blockLength;

    CTCBlockSubject(CTCBlockInfo block) {
        this.blockID = new SimpleIntegerProperty(this, "blockID", block.getBlockID());
        this.line = new SimpleBooleanProperty(this, "line", block.getLine());
        this.occupied = new SimpleBooleanProperty(this, "occupied", block.getOccupied());
        this.hasLight = new SimpleBooleanProperty(this, "hasLight", block.getHasLight());
        this.hasSwitch = new SimpleBooleanProperty(this, "hasSwitch", block.getHasSwitch());
        this.hasCrossing = new SimpleBooleanProperty(this, "hasCrossing", block.getHasCrossing());
        this.lightState = new SimpleBooleanProperty(this, "lightState", block.getLightState());
        this.switchState = new SimpleBooleanProperty(this, "switchState", block.getSwitchState());
        this.crossingState = new SimpleBooleanProperty(this, "crossingState", block.getCrossingState());
        this.speedLimit = new SimpleDoubleProperty(this, "speedLimit", block.getSpeedLimit());
        this.blockLength = new SimpleIntegerProperty(this, "blockLength", block.getBlockLength());
        CTCBlockSubjectFactory.getInstance().registerSubject(block.getBlockID(), this);
    }
    public BooleanProperty getBooleanProperty(String propertyName) {
        return switch (propertyName) {
            case "line" -> line;
            case "occupied" -> occupied;
            case "hasLight" -> hasLight;
            case "hasSwitch" -> hasSwitch;
            case "hasCrossing" -> hasCrossing;
            case "lightState" -> lightState;
            case "switchState" -> switchState;
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
            case "line" -> updateProperty(line, newValue);
            case "occupied" -> updateProperty(occupied, newValue);
            case "hasLight" -> updateProperty(hasLight, newValue);
            case "hasSwitch" -> updateProperty(hasSwitch, newValue);
            case "hasCrossing" -> updateProperty(hasCrossing, newValue);
            case "lightState" -> updateProperty(lightState, newValue);
            case "switchState" -> updateProperty(switchState, newValue);
            case "crossingState" -> updateProperty(crossingState, newValue);
            case "speedLimit" -> updateProperty(speedLimit, newValue);
            case "blockID" -> updateProperty(blockID, newValue);
            case "blockLength" -> updateProperty(blockLength, newValue);
            default -> System.err.println("Invalid property name: " + propertyName);

        }
    }

}

