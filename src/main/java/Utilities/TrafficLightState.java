package Utilities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class TrafficLightState {
    private BooleanProperty hasLight;
    private BooleanProperty lightState;
    private ObjectProperty<Paint> lightColor;

    public TrafficLightState(boolean hasLight) {
        this.hasLight = new SimpleBooleanProperty(hasLight);
        this.lightState = new SimpleBooleanProperty(false);
        this.lightColor = new SimpleObjectProperty<>(Color.TRANSPARENT);
        this.lightState.addListener(event -> updateColor());
        updateColor();
    }

    public boolean hasLight() {
        return hasLight.get();
    }

    public BooleanProperty hasLightProperty() {
        return hasLight;
    }

    public boolean getLightState() {
        return lightState.get();
    }

    public BooleanProperty lightStateProperty() {
        return lightState;
    }

    public Paint getLightColor() {
        return lightColor.get();
    }

    public ObjectProperty<Paint> lightColorProperty() {
        return lightColor;
    }

    public void setLightState(boolean lightState) {
        this.lightState.set(lightState);
    }

    private void updateColor() {
        if(hasLight.get()) {
            if(lightState.get())
                lightColor.set(Color.GREEN);
            else
                lightColor.set(Color.RED);
        }
        else {
            lightColor.set(Color.TRANSPARENT);
        }
    }
}
