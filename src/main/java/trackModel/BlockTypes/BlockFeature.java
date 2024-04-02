package trackModel.BlockTypes;


public interface BlockFeature extends Switch, Crossing, Station {
    boolean isSwitch();
    boolean isCrossing();
    boolean isStation();
}
