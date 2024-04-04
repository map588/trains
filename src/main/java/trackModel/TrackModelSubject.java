package trackModel;

import Common.TrackModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import Framework.Support.Notifier;
import javafx.application.Platform;
import javafx.beans.property.*;

import static trackModel.TrackProperty.*;

public class TrackModelSubject {
    private final StringProperty tempProperty = new SimpleStringProperty();
    private final StringProperty comSpeed = new SimpleStringProperty();
    private final StringProperty trainAuthority = new SimpleStringProperty();
    private final StringProperty heaterProperty = new SimpleStringProperty();


//        tempProperty.addListener((observable, oldValue, newValue) -> {
//            System.out.println("Temp change detected");
//            model.setTemperature(Integer.parseInt(newValue));
//        });

        private ObservableHashMap<TrackProperty, Property<?>> properties = new ObservableHashMap<>();

        private TrackModel model;

        private void initializeValues () {
            properties.put(BLOCKNUMBER, new SimpleStringProperty());
            properties.put(BLOCKLENGTH, new SimpleIntegerProperty());
            properties.put(BLOCKGRADE, new SimpleDoubleProperty());
            properties.put(SPEEDLIMIT, new SimpleDoubleProperty());
            properties.put(ISCROSSING, new SimpleBooleanProperty());
            properties.put(ISUNDERGROUND, new SimpleBooleanProperty());
            properties.put(ISSIGNAL, new SimpleBooleanProperty());
            properties.put(ISSWITCH, new SimpleBooleanProperty());
            properties.put(ISSTATION, new SimpleBooleanProperty());
            properties.put(ISBEACON, new SimpleBooleanProperty());
            properties.put(ISOCCUPIED, new SimpleBooleanProperty());
            properties.put(HASFAILURE, new SimpleBooleanProperty());
            properties.put(SECTION, new SimpleStringProperty());
            properties.put(PASSEMBARKED, new SimpleStringProperty());
            properties.put(PASSDISEMBARKED, new SimpleStringProperty());
            properties.put(TICKETSALES, new SimpleStringProperty());
            properties.put(STATUS, new SimpleStringProperty());
            properties.put(SWITCHBLOCKID, new SimpleStringProperty());
            properties.put(SWITCHSTATE, new SimpleStringProperty());
            properties.put(SIGNALID, new SimpleStringProperty());
            properties.put(SIGNALSTATE, new SimpleStringProperty());
            properties.put(CROSSINGSTATE, new SimpleStringProperty());
            properties.put(TEMPDISPLAY, new SimpleStringProperty());
            properties.put(SETBEACON, new SimpleStringProperty());
            properties.put(SWITCHMAIN, new SimpleStringProperty());
            properties.put(SWITCHALT, new SimpleStringProperty());
            properties.put(NAMEOFSTATION, new SimpleStringProperty());
            properties.put(TRACKHEATER, new SimpleStringProperty("STATUS - OFF"));
        }

        public TrackModelSubject() {
            initializeValues();
            // initializeListeners();
        }

        public TrackModelSubject(TrackModel model) {
            this.model = model;
            initializeValues();
            //  initializeListeners();
        }


//        private void initializeListeners () {
//
//            switchState.addListener((observableValue, oldValue, newVal) -> setSwitchState(newVal));
//            signalState.addListener((observableValue, oldValue, newVal) -> setSignalState(newVal));
//            isOccupied.addListener((observableValue, oldValue, newVal) -> {
//                if (newVal) {
//                    setStatus("OCCUPIED");
//                } else {
//                    setStatus("UNOCCUPIED");
//                }
//            });
//
//        }


        //Set property is for calls from the GUI.
        //It yields to the logic side to update the model

        // LogicUpdate notification still needs implemented, but it will
        // be put in the below function where false is in the execeuteUpdate call.

        //Change coming from the GUI side
        public void setProperty (TrackProperty propertyName, Object newValue){
            Property<?> property = properties.get(propertyName);
            //        if(maintenanceMode){
            //            System.out.println("Maintenance mode is on. Cannot update property " + propertyName);
            //            return;
            //        }
            executeUpdate(() -> {
                updateProperty(property, newValue);
              //  model.setValue(propertyName, newValue);
            }, false);
        }

        public void setProperty (TrackProperty propertyName, Object newValue, Integer blockID){
            Property<?> property = properties.get(propertyName);
            executeUpdate(() -> {
                updateProperty(property, newValue);
              //  model.setValue(propertyName, newValue, blockID);
            }, false);
        }


        private void executeUpdate (Runnable updateTask,boolean runImmediately){
            if (runImmediately) {
                updateTask.run();
            } else {
                Platform.runLater(updateTask);
            }
        }

        Property<?> getProperty (TrackProperty propertyName){
            return properties.get(propertyName);
        }

        void updateProperty (Property <?> property, Object newValue){
            if (property instanceof DoubleProperty && newValue instanceof Number) {
                ((DoubleProperty) property).set(((Number) newValue).doubleValue());
            } else if (property instanceof IntegerProperty && newValue instanceof Number) {
                ((IntegerProperty) property).set(((Number) newValue).intValue());
            } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
                ((BooleanProperty) property).set((Boolean) newValue);
            } else if (property instanceof StringProperty && newValue instanceof String) {
                ((StringProperty) property).set((String) newValue);

            } else if (property == null) {
                System.err.println("Attempted null property update with value " + newValue);
                return;
            } else if (newValue == null) {
                System.err.println("Null value for property" + property.getName());
                return;
            } else {
                System.err.println("Mismatch in property type and value type for " + property.getName());
                return;
            }

        }

        public StringProperty tempProperty () {
            return tempProperty;
        }

        public String getComSpeed () {
            return comSpeed.get();
        }

        public StringProperty comSpeedProperty () {
            return comSpeed;
        }

        public void setComSpeed (String comSpeed){
            this.comSpeed.set(comSpeed);
        }

        public String getTrainAuthority () {
            return trainAuthority.get();
        }

        public StringProperty trainAuthorityProperty () {
            return trainAuthority;
        }

        public void setTrainAuthority (String trainAuthority){
            this.trainAuthority.set(trainAuthority);
//            System.out.println("Property " + property.getName() + " updated to " + newValue);
        }

        public StringProperty trackHeaterProperty () {
            return heaterProperty;
        }

        public String getTrackHeater () {
            return heaterProperty.get();
        }

}

