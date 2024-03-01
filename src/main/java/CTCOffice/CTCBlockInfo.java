package CTCOffice;

/**
 * This class represents a block of the track in the Centralized Traffic Control (CTC) system.
 * It contains information about the block such as its ID, occupancy status, presence of light, switch and crossing,
 * their states, speed limit, block length, IDs of converging and diverging blocks, and the state of the switch.
 */
class CTCBlockInfo {
    private final int blockID, blockLength, speedLimit;
    private final boolean line;
    private boolean occupied, underMaintenance;
    private final boolean hasLight, hasSwitchCon, hasSwitchDiv, hasCrossing;
    private boolean lightState, crossingState, switchState;
    private final int convergingBlockID, divergingBlockOneID, divergingBlockTwoID;
    private javafx.scene.paint.Paint lightColor;
    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();

    /**
     * Constructor for the CTCBlockInfo class.
     * Initializes the block properties and registers the block with the CTCBlockSubjectFactory.
     */
    CTCBlockInfo(int blockID, boolean line, boolean occupied, boolean hasLight, boolean hasSwitchCon,
                boolean hasSwitchDiv, boolean hasCrossing, boolean lightState, boolean crossingState, int speedLimit,
                int blockLength, int convergingBlockID, int divergingBlockOneID, int divergingBlockTwoID,
                boolean switchState, boolean underMaintenance) {

        this.blockID = blockID;
        this.line = line;
        this.occupied = occupied;
        this.hasLight = hasLight;
        this.hasSwitchCon = hasSwitchCon;
        this.hasSwitchDiv = hasSwitchDiv;
        this.hasCrossing = hasCrossing;
        this.lightState = lightState;
        this.crossingState = crossingState;
        this.speedLimit = speedLimit;
        this.blockLength = blockLength;
        this.convergingBlockID = convergingBlockID;
        this.divergingBlockOneID = divergingBlockOneID;
        this.divergingBlockTwoID = divergingBlockTwoID;
        this.switchState = switchState;
        this.underMaintenance = underMaintenance;
        updateLightColor();
       factory.registerSubject(blockID, new CTCBlockSubject(this));
    }

    // Complex setters and getters

    /**
     * Sets the state of the switch and updates the switch state of the converging and diverging blocks.
     */
    void setSwitchState(boolean state) {
        this.switchState = state;
        if(convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0){
            return;
        }
        if(convergingBlockID == blockID) {
            factory.getSubjects().get(divergingBlockOneID).setProperty("switchState", state);
            factory.getSubjects().get(divergingBlockTwoID).setProperty("switchState", state);
        }
        System.out.println("Switch State: " + switchState + " \n");
        factory.getSubjects().get(convergingBlockID).setStringProperty("switchStateString");
        factory.getSubjects().get(divergingBlockOneID).setStringProperty("switchStateString");
        factory.getSubjects().get(divergingBlockTwoID).setStringProperty("switchStateString");
    }

    /**
     * Returns a string representation of the switch state.
     */
    public String getSwitchStateString() {
        if(factory.getSubjects().get(getBlockID()) != null) {
            if (convergingBlockID == 0 || divergingBlockOneID == 0 || divergingBlockTwoID == 0) {
                return "";
            }
        }else{return null;}
        if(hasSwitchCon && !switchState) {
            return ( "( " + divergingBlockOneID + " == "  + convergingBlockID + " )  " + divergingBlockTwoID);
        }else if(hasSwitchCon) {
            return ( divergingBlockOneID +"  ( "   + convergingBlockID + " == " + divergingBlockTwoID + " )");
        }else if(hasSwitchDiv && !switchState) {
            if(divergingBlockOneID == blockID) {
                return ( divergingBlockOneID + " ==== " + convergingBlockID);
            }else {
                return ( divergingBlockTwoID + "\t\t" + convergingBlockID);
            }
        }else if(hasSwitchDiv) {
            if(divergingBlockTwoID == blockID) {
                return ( divergingBlockTwoID + " ==== " + convergingBlockID);
            }else {
                return ( divergingBlockOneID + "\t\t" + convergingBlockID);
            }
        }else {
            return "";
        }
    }

    /**
     * Updates the color of the light based on its state.
     */
    void updateLightColor() {
        if(factory.getSubjects().get(getBlockID()) != null) {
            return;
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

    public javafx.scene.paint.Paint getLightColor() {
        updateLightColor();
        return lightColor;
    }

    // Simple setters and getters
    int     getBlockID      () {
        return blockID;
    }
    boolean getLine         () {
        return line;
    }
    int     getSpeedLimit   () {
        return speedLimit;
    }
    int     getBlockLength  () {
        return blockLength;
    }

    void    setOccupied     (boolean occupied) {
        this.occupied = occupied;
    }
    boolean getOccupied     () {
        return occupied;
    }
    boolean getUnderMaintenance () {
        return underMaintenance;
    }
    void    setUnderMaintenance (boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
    }

    void    setLightState   (boolean lightState) {
        this.lightState = lightState;
        updateLightColor();
    }
    boolean getHasLight     () {
        return hasLight;
    }
    boolean getLightState   () {
        return lightState;
    }

    void    setCrossingState(boolean crossingState) {
        this.crossingState = crossingState;
    }
    boolean getHasCrossing  () {
        return hasCrossing;
    }
    boolean getCrossingState() {
        return crossingState;
    }

    public boolean getSwitchState           () {
        return switchState;
    }
    public int     getConvergingBlockID     () {
        return convergingBlockID;
    }
    public int     getDivergingBlockOneID   () {
        return divergingBlockOneID;
    }
    public int     getDivergingBlockTwoID   () {
        return divergingBlockTwoID;
    }

    boolean        getHasSwitchCon          () {
        return hasSwitchCon;
    }
    boolean        getHasSwitchDiv          () {
        return hasSwitchDiv;
    }

}

