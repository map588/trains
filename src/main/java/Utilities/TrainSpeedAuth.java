package Utilities;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TrainSpeedAuth {
    private IntegerProperty trainID;
    private DoubleProperty speed;
    private IntegerProperty authority;

    public TrainSpeedAuth(int trainID, double speed, int authority) {
        this.trainID = new SimpleIntegerProperty(trainID);
        this.speed = new SimpleDoubleProperty(speed);
        this.authority = new SimpleIntegerProperty(authority);
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

    public double getSpeed() {
        return speed.get();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public int getAuthority() {
        return authority.get();
    }

    public IntegerProperty authorityProperty() {
        return authority;
    }
}
