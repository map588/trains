package Framework.GUI.Managers;

import javafx.beans.property.SimpleIntegerProperty;

public class BlockInfo {
    private final SimpleIntegerProperty blockNumber;
    //private final SimpleBooleanProperty occupationLight;
//    private SimpleBooleanProperty switchLightColor;
//    private SimpleBooleanProperty switchState;

    public BlockInfo(int blockNumber/*, Boolean occupationLight, boolean switchLightColor, boolean switchState*/) {
        this.blockNumber = new SimpleIntegerProperty(blockNumber);
      //  this.occupationLight = new SimpleBooleanProperty(occupationLight);
//        this.switchLightColor = new SimpleBooleanProperty(switchLightColor);
//        this.switchState = new SimpleBooleanProperty(switchState);
    }

    public int getBlockNumber() {
        return blockNumber.get();
    }
    public void setBlockNumber(int blockNumber) {
        this.blockNumber.set(blockNumber);
    }
//    public Boolean getOccupationLight() {
//        return occupationLight.get();
//    }
//    public void setOccupationLight(Boolean occupationLight) {
//        this.occupationLight.set(occupationLight);
//    }
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
