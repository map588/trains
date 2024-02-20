package CTCOffice;


class CTCBlockInfo {
    private int blockID;
    private boolean line;
    private boolean occupied;
    private boolean hasLight;
    private boolean hasSwitch;
    private boolean hasCrossing;
    private boolean lightState;
    private boolean switchState;
    private boolean crossingState;
    private int speedLimit;
    private int blockLength;

    CTCBlockInfo(int blockID, boolean line, boolean occupied, boolean hasLight, boolean hasSwitch, boolean hasCrossing, boolean lightState, boolean switchState, boolean crossingState, int speedLimit, int blockLength) {
        this.blockID = blockID;
        this.line = line;
        this.occupied = occupied;
        this.hasLight = hasLight;
        this.hasSwitch = hasSwitch;
        this.hasCrossing = hasCrossing;
        this.lightState = lightState;
        this.switchState = switchState;
        this.crossingState = crossingState;
        this.speedLimit = speedLimit;
        this.blockLength = blockLength;
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
    }

    void setHasLight(boolean hasLight) {
        this.hasLight = hasLight;
    }

    void setHasSwitch(boolean hasSwitch) {
        this.hasSwitch = hasSwitch;
    }

    void setHasCrossing(boolean hasCrossing) {
        this.hasCrossing = hasCrossing;
    }

    void setLightState(boolean lightState) {
        this.lightState = lightState;
    }

    void setSwitchState(boolean switchState) {
        this.switchState = switchState;
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

    boolean getHasSwitch() {
        return hasSwitch;
    }

    boolean getHasCrossing() {
        return hasCrossing;
    }

    boolean getLightState() {
        return lightState;
    }

    boolean getSwitchState() {
        return switchState;
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
}
