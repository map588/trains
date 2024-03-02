package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.*;
import javafx.scene.paint.Paint;
import Framework.Support.ObservableHashMap;


import static CTCOffice.Properties.BlockProperties.*;

/**
 * This class represents a subject in the Observer pattern for the CTCBlockInfo class.
 * It contains properties of the block that can be observed by other classes.
 * It also contains methods for getting and setting these properties.
 */
public class CTCBlockSubject implements AbstractSubject {
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
//
//    private final IntegerProperty blockID;
//    private final BooleanProperty line, occupied, hasLight, hasSwitchCon, hasSwitchDiv, hasCrossing, switchLightState,
//                                    crossingState, switchState, underMaintenance;
//    private final ObjectProperty<Paint> switchLightColor, crossingLightColor, maintenanceLightColor;
//    private final StringProperty switchStateString;

    CTCBlockInfo blockInfo;

    /**
     * Constructor for the CTCBlockSubject class.
     * Initializes the properties with the values from the given block.
     * Also sets up listeners for changes in the properties.
     */
    /*
    CTCBlockSubject(CTCBlockInfo block) {
        this.blockID = new SimpleIntegerProperty(this, BLOCK_ID_PROPERTY, block.getBlockID());
        this.line = new SimpleBooleanProperty(this, LINE_PROPERTY, block.getLine());
        this.occupied = new SimpleBooleanProperty(this, OCCUPIED_PROPERTY, block.getOccupied());
        this.hasLight = new SimpleBooleanProperty(this, HAS_LIGHT_PROPERTY, block.getHasLight());
        this.hasSwitchCon = new SimpleBooleanProperty(this, HAS_SWITCH_CON_PROPERTY, block.getHasSwitchCon());
        this.hasSwitchDiv = new SimpleBooleanProperty(this, HAS_SWITCH_DIV_PROPERTY, block.getHasSwitchDiv());
        this.hasCrossing = new SimpleBooleanProperty(this, HAS_CROSSING_PROPERTY, block.getHasCrossing());
        this.switchLightState = new SimpleBooleanProperty(this, SWITCH_LIGHT_STATE_PROPERTY, block.getSwitchLightState());
        this.crossingState = new SimpleBooleanProperty(this, CROSSING_STATE_PROPERTY, block.getCrossingState());
        this.switchLightColor = new SimpleObjectProperty<>(this, SWITCH_LIGHT_COLOR_PROPERTY, block.getSwitchLightColor());
        this.crossingLightColor = new SimpleObjectProperty<>(this, CROSSING_LIGHT_COLOR_PROPERTY, block.getCrossingLightColor());
        this.maintenanceLightColor = new SimpleObjectProperty<>(this, MAINTENANCE_LIGHT_COLOR_PROPERTY, block.getMaintenanceLightColor());
        this.switchState = new SimpleBooleanProperty(this, SWITCH_STATE_PROPERTY, block.getSwitchState());
        this.switchStateString = new SimpleStringProperty(this, SWITCH_STATE_STRING_PROPERTY, block.getSwitchStateString());
        this.underMaintenance = new SimpleBooleanProperty(this, UNDER_MAINTENANCE_PROPERTY, block.getUnderMaintenance());
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
            case LINE_PROPERTY -> line;
            case OCCUPIED_PROPERTY -> occupied;
            case HAS_LIGHT_PROPERTY -> hasLight;
            case HAS_SWITCH_CON_PROPERTY -> hasSwitchCon;
            case HAS_SWITCH_DIV_PROPERTY -> hasSwitchDiv;
            case HAS_CROSSING_PROPERTY -> hasCrossing;
            case SWITCH_LIGHT_STATE_PROPERTY -> switchLightState;
            case CROSSING_STATE_PROPERTY -> crossingState;
            case SWITCH_STATE_PROPERTY -> switchState;
            case UNDER_MAINTENANCE_PROPERTY -> underMaintenance;
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case BLOCK_ID_PROPERTY -> blockID;
            default -> null;
        };
    }

    public ObjectProperty<Paint> getPaintProperty(String propertyName) {
        return switch (propertyName) {
            case SWITCH_LIGHT_COLOR_PROPERTY -> switchLightColor;
            case CROSSING_LIGHT_COLOR_PROPERTY -> crossingLightColor;
            case MAINTENANCE_LIGHT_COLOR_PROPERTY -> maintenanceLightColor;
            default -> null;
        };
    }

    public StringProperty getStringProperty(String propertyName) {
        return switch (propertyName) {
            case SWITCH_STATE_STRING_PROPERTY -> switchStateString;
            default -> null;
        };
    }

    public void setStringProperty(String propertyName) {
        if(propertyName == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
                switchStateString.set(blockInfo.getSwitchStateString());
                System.out.println("setting new Switch state string for block " + blockID.get()
                        + " to " + blockInfo.getSwitchStateString());

    }

    public void setPaintProperty(String propertyName) {
        switch (propertyName) {
            case SWITCH_LIGHT_COLOR_PROPERTY -> switchLightColor.set(blockInfo.getSwitchLightColor());
            case CROSSING_LIGHT_COLOR_PROPERTY -> crossingLightColor.set(blockInfo.getCrossingLightColor());
            case MAINTENANCE_LIGHT_COLOR_PROPERTY -> maintenanceLightColor.set(blockInfo.getMaintenanceLightColor());
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    /**
     * Sets the value of a property.
     * If the new value is null, it prints an error message.
     */
//    public void setProperty(String propertyName, Object newValue) {
//        if (newValue == null) {
//            System.err.println("Null value for property " + propertyName);
//            return;
//        }
//        switch (propertyName) {
//            case LINE_PROPERTY -> updateProperty(line, newValue);
//            case OCCUPIED_PROPERTY -> updateProperty(occupied, newValue);
//            case HAS_LIGHT_PROPERTY -> updateProperty(hasLight, newValue);
//            case HAS_SWITCH_CON_PROPERTY -> updateProperty(hasSwitchCon, newValue);
//            case HAS_SWITCH_DIV_PROPERTY -> updateProperty(hasSwitchDiv, newValue);
//            case HAS_CROSSING_PROPERTY -> updateProperty(hasCrossing, newValue);
//            case SWITCH_LIGHT_STATE_PROPERTY -> updateProperty(switchLightState, newValue);
//            case CROSSING_STATE_PROPERTY -> updateProperty(crossingState, newValue);
//            case BLOCK_ID_PROPERTY -> updateProperty(blockID, newValue);
//            case SWITCH_STATE_PROPERTY -> updateProperty(switchState, newValue);
//            case UNDER_MAINTENANCE_PROPERTY -> updateProperty(underMaintenance, newValue);
//
//            default -> System.err.println("Unknown property " + propertyName);
//        }
//    }
//
//    public Property<?> getProperty(String propertyName) {
//        return null;
//    }



//***********************************************************************************************************************

     CTCBlockSubject(CTCBlockInfo block) {
        properties.put(BLOCK_ID_PROPERTY, new SimpleIntegerProperty(this, BLOCK_ID_PROPERTY, block.getBlockID()));
        properties.put(LINE_PROPERTY, new SimpleBooleanProperty(this, LINE_PROPERTY, block.getLine()));
        properties.put(OCCUPIED_PROPERTY, new SimpleBooleanProperty(this, OCCUPIED_PROPERTY, block.getOccupied()));
        properties.put(HAS_LIGHT_PROPERTY, new SimpleBooleanProperty(this, HAS_LIGHT_PROPERTY, block.getHasLight()));
        properties.put(HAS_SWITCH_CON_PROPERTY, new SimpleBooleanProperty(this, HAS_SWITCH_CON_PROPERTY, block.getHasSwitchCon()));
        properties.put(HAS_SWITCH_DIV_PROPERTY, new SimpleBooleanProperty(this, HAS_SWITCH_DIV_PROPERTY, block.getHasSwitchDiv()));
        properties.put(HAS_CROSSING_PROPERTY, new SimpleBooleanProperty(this, HAS_CROSSING_PROPERTY, block.getHasCrossing()));
        properties.put(SWITCH_LIGHT_STATE_PROPERTY, new SimpleBooleanProperty(this, SWITCH_LIGHT_STATE_PROPERTY, block.getSwitchLightState()));
        properties.put(CROSSING_STATE_PROPERTY, new SimpleBooleanProperty(this, CROSSING_STATE_PROPERTY, block.getCrossingState()));
        properties.put(SWITCH_STATE_PROPERTY, new SimpleBooleanProperty(this, SWITCH_STATE_PROPERTY, block.getSwitchState()));
        properties.put(UNDER_MAINTENANCE_PROPERTY, new SimpleBooleanProperty(this, UNDER_MAINTENANCE_PROPERTY, block.getUnderMaintenance()));
        properties.put(SWITCH_LIGHT_COLOR_PROPERTY, new SimpleObjectProperty<>(this, SWITCH_LIGHT_COLOR_PROPERTY, block.getSwitchLightColor()));
        properties.put(CROSSING_LIGHT_COLOR_PROPERTY, new SimpleObjectProperty<>(this, CROSSING_LIGHT_COLOR_PROPERTY, block.getCrossingLightColor()));
        properties.put(MAINTENANCE_LIGHT_COLOR_PROPERTY, new SimpleObjectProperty<>(this, MAINTENANCE_LIGHT_COLOR_PROPERTY, block.getMaintenanceLightColor()));
        properties.put(SWITCH_STATE_STRING_PROPERTY, new SimpleStringProperty(this, SWITCH_STATE_STRING_PROPERTY, block.getSwitchStateString()));
        this.blockInfo = block;

        getBooleanProperty(OCCUPIED_PROPERTY).addListener((observable, oldValue, newValue) -> block.setOccupied(newValue));

        getBooleanProperty(SWITCH_LIGHT_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setSwitchLightState(newValue));

        getBooleanProperty(SWITCH_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setSwitchState(newValue));

        getBooleanProperty(UNDER_MAINTENANCE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setUnderMaintenance(newValue));

        getBooleanProperty(CROSSING_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> block.setCrossingState(newValue));
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
            case LINE_PROPERTY -> (BooleanProperty) getProperty(LINE_PROPERTY);
            case OCCUPIED_PROPERTY -> (BooleanProperty) getProperty(OCCUPIED_PROPERTY);
            case HAS_LIGHT_PROPERTY -> (BooleanProperty) getProperty(HAS_LIGHT_PROPERTY);
            case HAS_CROSSING_PROPERTY -> (BooleanProperty) getProperty(HAS_CROSSING_PROPERTY);
            case HAS_SWITCH_DIV_PROPERTY -> (BooleanProperty) getProperty(HAS_SWITCH_DIV_PROPERTY);
            case HAS_SWITCH_CON_PROPERTY -> (BooleanProperty) getProperty(HAS_SWITCH_CON_PROPERTY);
            case SWITCH_LIGHT_STATE_PROPERTY -> (BooleanProperty) getProperty(SWITCH_LIGHT_STATE_PROPERTY);
            case CROSSING_STATE_PROPERTY -> (BooleanProperty) getProperty(CROSSING_STATE_PROPERTY);
            case SWITCH_STATE_PROPERTY -> (BooleanProperty) getProperty(SWITCH_STATE_PROPERTY);
            case UNDER_MAINTENANCE_PROPERTY -> (BooleanProperty) getProperty(UNDER_MAINTENANCE_PROPERTY);
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case BLOCK_ID_PROPERTY                -> (IntegerProperty) getProperty(BLOCK_ID_PROPERTY);
            default -> null;
        };
    }

    public ObjectProperty<Paint> getPaintProperty(String propertyName) {
        return switch (propertyName) {
            case SWITCH_LIGHT_COLOR_PROPERTY      -> (ObjectProperty<Paint>) getProperty(SWITCH_LIGHT_COLOR_PROPERTY);
            case CROSSING_LIGHT_COLOR_PROPERTY    -> (ObjectProperty<Paint>) getProperty(CROSSING_LIGHT_COLOR_PROPERTY);
            case MAINTENANCE_LIGHT_COLOR_PROPERTY -> (ObjectProperty<Paint>) getProperty(MAINTENANCE_LIGHT_COLOR_PROPERTY);
            default -> null;
        };
    }

    public StringProperty getStringProperty(String propertyName) {
        return switch (propertyName) {
            case SWITCH_STATE_STRING_PROPERTY -> (StringProperty) getProperty(SWITCH_STATE_STRING_PROPERTY);
            default -> null;
        };
    }

    public void setStringProperty(String propertyName) {
        if(propertyName == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
                getStringProperty(SWITCH_STATE_STRING_PROPERTY).set(blockInfo.getSwitchStateString());
                System.out.println("setting new Switch state string for block " + getIntegerProperty(BLOCK_ID_PROPERTY).get()
                        + " to " + blockInfo.getSwitchStateString());

    }

    public void setPaintProperty(String propertyName) {
        switch (propertyName) {
            case SWITCH_LIGHT_COLOR_PROPERTY      -> updateProperty(getProperty(SWITCH_LIGHT_COLOR_PROPERTY), blockInfo.getSwitchLightColor());
            case CROSSING_LIGHT_COLOR_PROPERTY    -> updateProperty(getProperty(CROSSING_LIGHT_COLOR_PROPERTY), blockInfo.getCrossingLightColor());
            case MAINTENANCE_LIGHT_COLOR_PROPERTY -> updateProperty(getProperty(MAINTENANCE_LIGHT_COLOR_PROPERTY), blockInfo.getMaintenanceLightColor());
        }
    }


    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case LINE_PROPERTY -> updateProperty(getProperty(LINE_PROPERTY), newValue);
            case OCCUPIED_PROPERTY -> updateProperty(getProperty(OCCUPIED_PROPERTY), newValue);
            case HAS_LIGHT_PROPERTY -> updateProperty(getProperty(HAS_LIGHT_PROPERTY), newValue);
            case HAS_SWITCH_CON_PROPERTY -> updateProperty(getProperty(HAS_SWITCH_CON_PROPERTY), newValue);
            case HAS_SWITCH_DIV_PROPERTY -> updateProperty(getProperty(HAS_SWITCH_DIV_PROPERTY), newValue);
            case HAS_CROSSING_PROPERTY -> updateProperty(getProperty(HAS_CROSSING_PROPERTY), newValue);
            case SWITCH_LIGHT_STATE_PROPERTY -> updateProperty(getProperty(SWITCH_LIGHT_STATE_PROPERTY), newValue);
            case CROSSING_STATE_PROPERTY -> updateProperty(getProperty(CROSSING_STATE_PROPERTY), newValue);
            case SWITCH_STATE_PROPERTY -> updateProperty(getProperty(SWITCH_STATE_PROPERTY), newValue);
            case UNDER_MAINTENANCE_PROPERTY -> updateProperty(getProperty(UNDER_MAINTENANCE_PROPERTY), newValue);
            case BLOCK_ID_PROPERTY -> updateProperty(getProperty(BLOCK_ID_PROPERTY), newValue);

            default -> System.err.println("Unknown property " + propertyName);
        }
    }


    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }
    @Override
    public<T> void updateProperty(Property<T> property, Object newValue){
        if (property instanceof DoubleProperty && newValue instanceof Number) {
            ((DoubleProperty) property).set(((Number) newValue).doubleValue());
        } else if (property instanceof IntegerProperty && newValue instanceof Number) {
            ((IntegerProperty) property).set(((Number) newValue).intValue());
        } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
            ((BooleanProperty) property).set((Boolean) newValue);
        } else if(property instanceof StringProperty && newValue instanceof String) {
            ((StringProperty) property).set((String) newValue);
        }else if(property instanceof ObjectProperty && newValue instanceof Paint){
            ((ObjectProperty<Paint>) property).set((Paint) newValue);
        } else{
            System.err.println("Mismatch in property type and value type for " + property.getName());
        }
        System.out.println("Property " + property.getName() + " updated to " + newValue + " in Subject");
    }

}
