package waysideController;

import Framework.Support.AbstractSubject;
import Framework.Support.Notifier;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static waysideController.Properties.*;

public class WaysideBlockSubject implements AbstractSubject, Notifier {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final WaysideBlock block;
    public boolean isLogicUpdate = false;
    public boolean isGUIUpdate = false;

    public WaysideBlockSubject(WaysideBlock block) {
        this.block = block;
        block.setSubject(this);

        properties.put(blockID_p, new ReadOnlyIntegerWrapper(block.getBlockID()));
        properties.put(hasSwitch_p, new ReadOnlyBooleanWrapper(block.hasSwitch()));
        properties.put(hasLight_p, new ReadOnlyBooleanWrapper(block.hasLight()));
        properties.put(hasCrossing_p, new ReadOnlyBooleanWrapper(block.hasCrossing()));

        properties.put(occupied_p, new SimpleBooleanProperty(occupied_p, occupied_p, block.isOccupied()));
        properties.put(switchState_p, new SimpleBooleanProperty(block.getSwitchState()));
        properties.put(lightState_p, new SimpleBooleanProperty(block.getLightState()));
        properties.put(crossingState_p, new SimpleBooleanProperty(block.getCrossingState()));
        properties.put(authority_p, new SimpleBooleanProperty(block.getBooleanAuth()));
        properties.put(inMaintenance_p, new SimpleBooleanProperty(block.inMaintenance()));

        properties.put(switchBlockParent_p, new ReadOnlyIntegerWrapper(block.getSwitchBlockParent()));
        properties.put(switchBlockDef_p, new ReadOnlyIntegerWrapper(block.getSwitchBlockDef()));
        properties.put(switchBlockAlt_p, new ReadOnlyIntegerWrapper(block.getSwitchBlockAlt()));
        properties.put(switchedBlockID_p, new SimpleIntegerProperty(block.getSwitchBlockDef()));
        getBooleanProperty(switchState_p).addListener((observable, oldValue, newValue) ->
                getIntegerProperty(switchedBlockID_p).set(newValue ? block.getSwitchBlockAlt() : block.getSwitchBlockDef())
        );
    }

    public WaysideBlock getBlock() {
        return block;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        Runnable updateTask = () -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            block.setValue(propertyName, newValue);
     //       System.out.println("Property " + propertyName + " updated to " + newValue + " in Wayside Subject");
        };

        if (isLogicUpdate) {
            executorService.scheduleWithFixedDelay(() -> {
                if (!isLogicUpdate) {
   //                 System.out.println("Delayed setProperty from GUI");
                    Platform.runLater(() -> updateFromGUI(updateTask));
                }
            }, 0, 10, TimeUnit.MILLISECONDS);
        } else {
            Platform.runLater(() -> updateFromGUI(updateTask));
        }
    }

    public void updateFromGUI(Runnable updateLogic) {
   //     System.out.println("Called from updateFromGUI.");
        isGUIUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGUIUpdate = false;
        }
    }

    public void updateFromLogic(Runnable updateLogic) {
        isLogicUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isLogicUpdate = false;
        }
    }

    @Override
    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }
    public BooleanProperty getBooleanProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (BooleanProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not a BooleanProperty");
        }
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (IntegerProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not an IntegerProperty");
        }
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (DoubleProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not an DoubleProperty");
        }
    }

    @Override
    public void notifyChange(String propertyName, Object newValue) {
        // Update property from controller, Internal Logic takes precedence over GUI updates
        if (!isGUIUpdate) {
            updateFromLogic(() -> {
                Property<?> property = properties.get(propertyName);
                updateProperty(property, newValue);
   //             System.out.println("Property " + propertyName + " updated to " + newValue + " in Wayside Subject");
            });
        }
    }
}
