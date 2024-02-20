package CTCOffice;


class CTCBlockInfo {
    private int blockID;
    private int line;
    private boolean occupied;
    private boolean lightData;
    private boolean switchData;
    private boolean crossingData;
    private boolean lightState;
    private boolean switchState;
    private boolean crossingState;
    private int speedLimit;
    private int blockLength;

    void setBlockID (int number){
        this.blockID = number;
    }

    void setLine(int line) {
        this.line = line;
    }

    void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    void setLightData(boolean lightData) {
        this.lightData = lightData;
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

    int getLine() {
        return line;
    }

    boolean getOccupied() {
        return occupied;
    }

    boolean getLightData() {
        return lightData;
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
