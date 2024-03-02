package trackModel;

import Common.TrackModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TrackModelSubject {
    private StringProperty tempProperty;
    private StringProperty comSpeed;
    private StringProperty trainAuthority;

    public TrackModelSubject(TrackModel model)  {
        tempProperty = new SimpleStringProperty();
        comSpeed = new SimpleStringProperty();
        trainAuthority = new SimpleStringProperty();

        tempProperty.addListener((observable, oldValue, newValue) -> {
            System.out.println("Temp change detected");
            model.setTrackHeaters(Integer.parseInt(newValue));
        }
        );

    }

    public StringProperty tempProperty() {
        return tempProperty;
    }

    public String getComSpeed() {
        return comSpeed.get();
    }

    public StringProperty comSpeedProperty() {
        return comSpeed;
    }

    public void setComSpeed(String comSpeed) {
        this.comSpeed.set(comSpeed);
    }

    public String getTrainAuthority() {
        return trainAuthority.get();
    }

    public StringProperty trainAuthorityProperty() {
        return trainAuthority;
    }

    public void setTrainAuthority(String trainAuthority) {
        this.trainAuthority.set(trainAuthority);
    }
}
