package Framework.GUI.Managers;

import javafx.beans.property.*;

public class BlockInfo {
    private final SimpleIntegerProperty blockNumber;
    private final SimpleStringProperty occupationLight;
    private final SimpleStringProperty switchLightColor;
    private final SimpleStringProperty switchState;

    public BlockInfo(int blockNumber, Boolean occupied, Boolean switchLightColor, Boolean switchState) {
        this.blockNumber = new SimpleIntegerProperty(blockNumber);
        if (occupied) {
            this.occupationLight = new SimpleStringProperty("X");
        } else {
            this.occupationLight = new SimpleStringProperty(" ");
        }
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

    public int getBlockNumber() {
        return blockNumber.get();
    }
    public void setBlockNumber(int blockNumber) {
        this.blockNumber.set(blockNumber);
    }

    public String getOccupationLight() {
        return occupationLight.get();
    }

    public void setOccupationLight(Boolean occupied) {
        if (occupied) {
            this.occupationLight.set("X");
        } else {
            this.occupationLight.set("");
        }
    }

    public String getSwitchLightColor() {
        return switchLightColor.get();
    }
    public void setSwitchLightColor(boolean lightOn) {
        if (lightOn) {
            this.switchLightColor.set("Green");
        } else {
            this.switchLightColor.set("Red");
        }
    }

    public String getSwitchState() {
        return switchState.get();
    }
    public void setSwitchState(boolean straight) {
        if (straight) {
            this.switchState.set("Straight");
        } else {
            this.switchState.set("Diverging");
        }
    }
}
