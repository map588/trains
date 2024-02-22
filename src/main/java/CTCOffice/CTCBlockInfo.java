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
    private int convergingBlockID;
    private int divergingBlockOneID;
    private int divergingBlockTwoID;
    private boolean switchState;
    private javafx.scene.paint.Paint lightColor;
    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();

    CTCBlockInfo(int blockID, boolean line, boolean occupied, boolean hasLight, boolean hasSwitchCon, boolean hasSwitchDiv, boolean hasCrossing, boolean lightState, boolean switchConState, boolean switchDivState, boolean crossingState, int speedLimit, int blockLength, int convergingBlockID, int divergingBlockOneID, int divergingBlockTwoID, boolean switchState) {
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
        this.convergingBlockID = convergingBlockID;
        this.divergingBlockOneID = divergingBlockOneID;
        this.divergingBlockTwoID = divergingBlockTwoID;
        this.switchState = switchState;
        updateLightColor();
       factory.registerSubject(blockID, new CTCBlockSubject(this));
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
        updateLightColor();
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
        if(factory.getSubjects().get(getBlockID()) != null) {
        }
        if(this.lightState) {
            this.lightColor = javafx.scene.paint.Color.GREEN;
        }else {
            this.lightColor = javafx.scene.paint.Color.RED;
        }
        if(factory.getSubjects().get(getBlockID()) != null){
            factory.getSubjects().get(getBlockID()).setPaint( lightColor);
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

    void setSwitchState(boolean state) {
        this.switchState = state;
        if(convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0){
            return;
        }
        factory.getSubjects().get(convergingBlockID).setProperty("switchConState", state);
        if(!state){
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockOneID).setProperty("switchDivState", true);
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockTwoID).setProperty("switchDivState", false);
        } else {
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockTwoID).setProperty("switchDivState", false);
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockOneID).setProperty("switchDivState", true);
        }
    }

    public int getConvergingBlockID() {
        return convergingBlockID;
    }

    public int getDivergingBlockOneID() {
            return divergingBlockOneID;
        }

    public int getDivergingBlockTwoID() {
        return divergingBlockTwoID;
    }

    public javafx.scene.paint.Paint getLightColor() {
        updateLightColor();
        return lightColor;
    }

    public boolean getSwitchState() {
        return switchState;
    }
}

