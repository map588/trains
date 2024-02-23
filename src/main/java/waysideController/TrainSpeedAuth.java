package waysideController;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TrainSpeedAuth {
    private IntegerProperty trainID;
    private DoubleProperty speedIn;
    private DoubleProperty speedOut;
    private IntegerProperty authorityIn;
    private IntegerProperty authorityOut;

    public TrainSpeedAuth(int trainID) {
        this.trainID = new SimpleIntegerProperty(trainID);
        this.speedIn = new SimpleDoubleProperty();
        this.speedOut = new SimpleDoubleProperty();
        this.authorityIn = new SimpleIntegerProperty();
        this.authorityOut = new SimpleIntegerProperty();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TrainSpeedAuth) && (this.trainID.get() == ((TrainSpeedAuth)(other)).getTrainID());
    }

    public int getTrainID() {
        return trainID.get();
    }

    public IntegerProperty trainIDProperty() {
        return trainID;
    }

    public double getSpeedIn() {
        return speedIn.get();
    }

    public DoubleProperty speedInProperty() {
        return speedIn;
    }

    public double getSpeedOut() {
        return speedOut.get();
    }

    public DoubleProperty speedOutProperty() {
        return speedOut;
    }

    public int getAuthorityIn() {
        return authorityIn.get();
    }

    public IntegerProperty authorityInProperty() {
        return authorityIn;
    }

    public int getAuthorityOut() {
        return authorityOut.get();
    }

    public IntegerProperty authorityOutProperty() {
        return authorityOut;
    }
}
