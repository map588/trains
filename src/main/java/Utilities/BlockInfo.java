package Utilities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BlockInfo {

    public static final int TRAFFIC_LIGHT_RED = 1;
    public static final int TRAFFIC_LIGHT_YELLOW = 2;
    public static final int TRAFFIC_LIGHT_GREEN = 3;

    private final staticBlockInfo staticInfo;
    private BooleanProperty trackCircuitState;
    private BooleanProperty crossingClosed;
    private IntegerProperty lightInState;
    private IntegerProperty lightOutState;

    public BlockInfo(staticBlockInfo staticInfo) {
        this.staticInfo = staticInfo;
        trackCircuitState = new SimpleBooleanProperty(false);
        crossingClosed = new SimpleBooleanProperty(false);
        lightInState = new SimpleIntegerProperty(TRAFFIC_LIGHT_RED);
        lightOutState = new SimpleIntegerProperty(TRAFFIC_LIGHT_RED);
    }

    public staticBlockInfo getStaticInfo() {
        return staticInfo;
    }

    public boolean isTrackCircuitState() {
        return trackCircuitState.get();
    }

    public void setTrackCircuitState(boolean trackCircuitState) {
        this.trackCircuitState.set(trackCircuitState);
    }

    public BooleanProperty getTrackCircuitStateProperty() {
        return trackCircuitState;
    }

    public boolean isCrossingClosed() {
        return crossingClosed.get();
    }

    public boolean isOccupied() {
        return trackCircuitState.get();
    }

    public void setCrossingClosed(boolean crossingClosed) {
        this.crossingClosed.set(crossingClosed);
    }

    public BooleanProperty crossingClosedProperty() {
        return crossingClosed;
    }

    public int getLightInState() {
        return lightInState.get();
    }

    public void setLightInState(int lightInState) {
        this.lightInState.set(lightInState);
    }

    public IntegerProperty lightInStateProperty() {
        return lightInState;
    }

    public int getLightOutState() {
        return lightOutState.get();
    }

    public void setLightOutState(int lightOutState) {
        this.lightOutState.set(lightOutState);
    }

    public IntegerProperty lightOutStateProperty() {
        return lightOutState;
    }
}
