package CTCOffice;


class CTCBlockInfo {
    private int blockID;
    private boolean line;
    private boolean occupied;
    private boolean hasLight;
    private boolean hasSwitchCon;
    private boolean hasSwitchDiv;
    private boolean hasCrossing;
    private boolean lightState;
    private boolean switchConState;
    private boolean switchDivState;
    private boolean crossingState;
    private int speedLimit;
    private int blockLength;
    private javafx.scene.paint.Paint lightColor;

    CTCBlockInfo(int blockID, boolean line, boolean occupied, boolean hasLight, boolean hasSwitchCon, boolean hasSwitchDiv, boolean hasCrossing, boolean lightState, boolean switchConState, boolean switchDivState, boolean crossingState, int speedLimit, int blockLength) {
        this.blockID = blockID;
        this.line = line;
        this.occupied = occupied;
        this.hasLight = hasLight;
        this.hasSwitchCon = hasSwitchCon;
        this.hasSwitchDiv = hasSwitchDiv;
        this.hasCrossing = hasCrossing;
        this.lightState = lightState;
        this.switchConState = switchConState;
        this.switchDivState = switchDivState;
        this.crossingState = crossingState;
        this.speedLimit = speedLimit;
        this.blockLength = blockLength;
        updateLightColor();
        CTCBlockSubjectFactory.getInstance().registerSubject(blockID, new CTCBlockSubject(this));
    }
    void setBlockID (int number){
        this.blockID = number;
    }

    void setLine(boolean line) {
        this.line = line;
    }

    void setOccupied(boolean occupied) {
        this.occupied = occupied;
        System.out.println("Block " + blockID + " on line " + line + " is occupied: " + occupied);
    }

    void setHasLight(boolean hasLight) {
        this.hasLight = hasLight;
    }

    void setHasSwitchCon(boolean hasSwitchCon) {
        this.hasSwitchCon = hasSwitchCon;
    }

    void setHasSwitchDiv(boolean hasSwitchDiv) {
        this.hasSwitchDiv = hasSwitchDiv;
    }

    void setHasCrossing(boolean hasCrossing) {
        this.hasCrossing = hasCrossing;
    }

    void setLightState(boolean lightState) {
        this.lightState = lightState;
    }

    void setSwitchConState(boolean switchConState) {
        this.switchConState = switchConState;
    }

    void setSwitchDivState(boolean switchDivState) {
        this.switchDivState = switchDivState;
    }

    void setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;
    }

    void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    void setBlockLength(int blockLength) {
        this.blockLength = blockLength;
    }

    void updateLightColor() {
        if(lightState) {
            this.lightColor = javafx.scene.paint.Color.GREEN;
        }else {
            this.lightColor = javafx.scene.paint.Color.RED;
        }
    }

    int getBlockID() {
        return blockID;
    }

    boolean getLine() {
        return line;
    }

    boolean getOccupied() {
        return occupied;
    }

    boolean getHasLight() {
        return hasLight;
    }

    boolean getHasSwitchCon() {
        return hasSwitchCon;
    }

    boolean getHasSwitchDiv() {
        return hasSwitchDiv;
    }

    boolean getHasCrossing() {
        return hasCrossing;
    }

    boolean getLightState() {
        return lightState;
    }

    boolean getSwitchConState() {
        return switchConState;
    }

    boolean getSwitchDivState() {
        return switchDivState;
    }

    boolean getCrossingState() {
        return crossingState;
    }

    int getSpeedLimit() {
        return speedLimit;
    }

    int getBlockLength() {
        return blockLength;
    }

    javafx.scene.paint.Paint getLightColor() {
        return lightColor;
    }

}
