package CTCOffice;

import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static CTCOffice.Properties.BlockProperties.*;

/**
 * This class represents a subject in the Observer pattern for the CTCBlockInfo class.
 * It contains properties of the block that can be observed by other classes.
 * It also contains methods for getting and setting these properties.
 */
public class CTCBlockSubject implements AbstractSubject {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final CTCBlock block;

    /**
     * Constructor for the CTCBlockSubject class.
     * Initializes the properties with the values from the given block.
     * Also sets up listeners for changes in the properties.
     */
     CTCBlockSubject(CTCBlock block) {
        properties.put(BLOCK_ID_PROPERTY, new SimpleIntegerProperty(this, BLOCK_ID_PROPERTY, block.getBlockID()));
        properties.put(LINE_PROPERTY, new SimpleStringProperty(this, LINE_PROPERTY, block.getLine()));
        properties.put(OCCUPIED_PROPERTY, new SimpleBooleanProperty(this, OCCUPIED_PROPERTY, block.getOccupied()));
        properties.put(HAS_LIGHT_PROPERTY, new SimpleBooleanProperty(this, HAS_LIGHT_PROPERTY, block.getHasLight()));
        properties.put(HAS_SWITCH_CON_PROPERTY, new SimpleBooleanProperty(this, HAS_SWITCH_CON_PROPERTY, block.getSwitchCon()));
        properties.put(HAS_SWITCH_DIV_PROPERTY, new SimpleBooleanProperty(this, HAS_SWITCH_DIV_PROPERTY, block.getSwitchDiv()));
        properties.put(HAS_CROSSING_PROPERTY, new SimpleBooleanProperty(this, HAS_CROSSING_PROPERTY, block.getHasCrossing()));
        properties.put(SWITCH_LIGHT_STATE_PROPERTY, new SimpleBooleanProperty(this, SWITCH_LIGHT_STATE_PROPERTY, block.getSwitchLightState()));
        properties.put(CROSSING_STATE_PROPERTY, new SimpleBooleanProperty(this, CROSSING_STATE_PROPERTY, block.getCrossingState()));
        properties.put(SWITCH_STATE_PROPERTY, new SimpleBooleanProperty(this, SWITCH_STATE_PROPERTY, block.getSwitchState()));
        properties.put(UNDER_MAINTENANCE_PROPERTY, new SimpleBooleanProperty(this, UNDER_MAINTENANCE_PROPERTY, block.getUnderMaintenance()));
        properties.put(SWITCH_STATE_STRING_PROPERTY, new SimpleStringProperty(this, SWITCH_STATE_STRING_PROPERTY, block.getSwitchStateString()));
        this.block = block;

        getBooleanProperty(OCCUPIED_PROPERTY).addListener((observable, oldValue, newValue) -> block.setOccupied(newValue));
        getBooleanProperty(SWITCH_LIGHT_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setSwitchLightState(newValue));
        getBooleanProperty(SWITCH_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setSwitchState(newValue));
        getBooleanProperty(UNDER_MAINTENANCE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setUnderMaintenance(newValue));
        getBooleanProperty(CROSSING_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setCrossingState(newValue));
    }

    public BooleanProperty getBooleanProperty(String propertyName) {
        if(block == null) {
            System.err.println("Null value for property " + propertyName);
            return null;
        }
        return (BooleanProperty) getProperty(propertyName);
    }

    public void updateStringProperty(String propertyName) {
        if(propertyName == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case SWITCH_STATE_STRING_PROPERTY -> updateProperty(getProperty(SWITCH_STATE_STRING_PROPERTY), block.getSwitchStateString());
            case LINE_PROPERTY -> updateProperty(getProperty(LINE_PROPERTY), block.getLine());
        }
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }

    public StringProperty getStringProperty(String propertyName) {
        return (StringProperty) getProperty(propertyName);
    }

    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        updateProperty(getProperty(propertyName), newValue);
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    CTCBlock getBlockInfo() {
        return block;
    }

    boolean hasLight() {
        return block.getHasLight();
    }

    boolean hasCrossing() {
        return block.getHasCrossing();
    }

}
