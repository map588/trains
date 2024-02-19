package Framework.GUI.Managers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BlockInfo {
    private final SimpleIntegerProperty blockNumber;
    private final SimpleStringProperty occupationLight;
//    private SimpleBooleanProperty switchLightColor;
//    private SimpleBooleanProperty switchState;

    public BlockInfo(int blockNumber, Boolean occupied/*, boolean switchLightColor, boolean switchState*/) {
        this.blockNumber = new SimpleIntegerProperty(blockNumber);
        if (occupied) {
            this.occupationLight = new SimpleStringProperty("X");
        } else {
            this.occupationLight = new SimpleStringProperty(" ");
        }
//        this.switchLightColor = new SimpleBooleanProperty(switchLightColor);
//        this.switchState = new SimpleBooleanProperty(switchState);
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

//    public boolean getSwitchLightColor() {
//        return switchLightColor.get();
//    }
//    public void setSwitchLightColor(boolean switchLightColor) {
//        this.switchLightColor.set(switchLightColor);
//    }
//    public boolean getSwitchState() {
//        return switchState.get();
//    }
//    public void setSwitchState(boolean switchState) {
//        this.switchState.set(switchState);
//    }
}
