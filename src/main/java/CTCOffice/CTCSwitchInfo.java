package CTCOffice;

class CTCSwitchInfo {
    private int convergingBlockID;
    private int divergingBlockOneID;
    private int divergingBlockTwoID;
    private boolean switchState;

    CTCSwitchInfo(int convergingBlockID, int divergingBlockOneID, int divergingBlockTwoID, boolean switchState) {
        this.convergingBlockID = convergingBlockID;
        this.divergingBlockOneID = divergingBlockOneID;
        this.divergingBlockTwoID = divergingBlockTwoID;
        this.switchState = switchState;
        setSwitchState(switchState);
    }
    void setSwitchState(boolean state) {
        this.switchState = state;
        CTCBlockSubjectFactory.getInstance().getSubjects().get(convergingBlockID).setProperty("switchConState", state);
        if(!state){
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockOneID).setProperty("switchDivState", true);
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockTwoID).setProperty("switchDivState", false);
        } else {
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockTwoID).setProperty("switchDivState", false);
            CTCBlockSubjectFactory.getInstance().getSubjects().get(divergingBlockOneID).setProperty("switchDivState", true);
        }
    }
}
