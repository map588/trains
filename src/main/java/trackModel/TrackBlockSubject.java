package trackModel;

import javafx.beans.property.*;


public class TrackBlockSubject {

    private Integer blockID;

    private final StringProperty blockNumber = new SimpleStringProperty();
    private final DoubleProperty blockLength = new SimpleDoubleProperty();
    private final DoubleProperty blockGrade = new SimpleDoubleProperty();
    private final DoubleProperty elevation = new SimpleDoubleProperty();
    private final DoubleProperty speedLimit = new SimpleDoubleProperty();


    private final BooleanProperty isOccupied = new SimpleBooleanProperty();
    private final StringProperty failure = new SimpleStringProperty();
    private final StringProperty direction = new SimpleStringProperty();


    public TrackBlockSubject(TrackBlock block) {
        this.blockID = block.blockID;
        this.blockNumber.set(blockID.toString());
        this.blockLength.set(block.getLength());
        this.blockGrade.set(block.getGrade());
        this.elevation.set(block.getElevation());
        this.speedLimit.set(block.getSpeedLimit());
        this.isOccupied.set(block.isOccupied());
        this.failure.set(block.hasFailure() ? "FAILURE" : "");
        this.direction.set((block.isOccupied()) ? block.getOccupiedBy().getDirection().toString() : "");
    }








}
