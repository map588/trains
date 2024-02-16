package CTCOffice;

import Common.CTCOffice;
import Common.trainModel;

public class CTCOfficeSubject implements CTCOffice {
    @Override
    public trainModel dispatch(int trainID, int blockID) {
        return null;
    }


    /*private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    DoubleProperty currentSpeed;
    DoubleProperty overrideSpeed;
    private DoubleProperty maxSpeed;


    private IntegerProperty trainID;

    private trainModel train;
    private controlSystem controlSystem;

    public testCtcOfficeImpl(int trainID) {
        this.commandSpeed = new SimpleDoubleProperty(35.0);
        this.automaticMode = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(2000);
        controlSystem = new controlSystem(this);
        Thread controlThread = new Thread(controlSystem);
        controlThread.start();
    }

    public void setAutomaticMode(boolean mode) {
        this.automaticMode.set(mode);
    }

    public void setAuthority(int authority) {
        this.authority.set(authority);
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed.set(speed);
    }*/
}

