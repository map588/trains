package Framework.GUI.Managers;

import javafx.beans.property.*;

public class CTCBlockInfoManager {
    private IntegerProperty blockNumber;
    private final BooleanProperty occupationLight;
    private final SimpleStringProperty switchLightColor;
    private final SimpleStringProperty switchState;

    public CTCBlockInfoManager(int blockNumber, Boolean occupied, Boolean switchLightColor, Boolean switchState) {
        this.blockNumber = new SimpleIntegerProperty(blockNumber);
        occupationLight = new SimpleBooleanProperty(occupied);
        if (switchLightColor) {
            this.switchLightColor = new SimpleStringProperty("Green");
        } else {
            this.switchLightColor = new SimpleStringProperty("Red");
        }
        if (switchState) {
            this.switchState = new SimpleStringProperty("Straight");
        } else {
            this.switchState = new SimpleStringProperty("Diverging");
        }
    }
    public CTCBlockInfoManager(int blockNumber) {
        this.blockNumber = new SimpleIntegerProperty(blockNumber);
        occupationLight = new SimpleBooleanProperty(false);
        this.switchLightColor = new SimpleStringProperty("Red");
        this.switchState = new SimpleStringProperty("Diverging");
    }

    public IntegerProperty getBlockNumber() {
        return blockNumber;
    }

    public BooleanProperty getOccupationLightProperty() {
        return occupationLight;
    }

    public void setOccupationLight(boolean occupied) {
        this.occupationLight.set(occupied);
    }

    public String getSwitchLightColor() {
        return switchLightColor.get();
    }
    public void setSwitchLightColor(Boolean lightOn) {
        if (lightOn) {
            this.switchLightColor.set("Green");
        } else {
            this.switchLightColor.set("Red");
        }
    }

    public String getSwitchState() {
        return switchState.get();
    }
    public void setSwitchState(Boolean straight) {
        if (straight) {
            this.switchState.set("Straight");
        } else {
            this.switchState.set("Diverging");
        }
    }
}
