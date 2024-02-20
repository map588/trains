package CTCOffice;


class CTCBlockInfo {
    private int blockID;
    private boolean line;
    private boolean occupied;
    private boolean hasLight;
    private boolean switchData;
    private boolean crossingData;
    private boolean lightState;
    private boolean switchState;
    private boolean crossingState;
    private int speedLimit;
    private int blockLength;

    CTCBlockInfo(int blockID, boolean line, boolean occupied, boolean hasLight, boolean switchData, boolean crossingData, boolean lightState, boolean switchState, boolean crossingState, int speedLimit, int blockLength) {
        this.blockID = blockID;
        this.line = line;
        this.occupied = occupied;
        this.hasLight = hasLight;
        this.switchData = switchData;
        this.crossingData = crossingData;
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

    void setLightData(boolean hasLight) {
        this.hasLight = hasLight;
    }

    void setSwitchData(boolean switchData) {
        this.switchData = switchData;
    }

    void setCrossingData(boolean crossingData) {
        this.crossingData = crossingData;
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

    boolean getLightData() {
        return hasLight;
    }

    boolean getSwitchData() {
        return switchData;
    }

    boolean getCrossingData() {
        return crossingData;
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
