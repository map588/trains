package CTCOffice;

import Framework.Support.Notifier;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import static CTCOffice.Properties.ScheduleProperties.*;


public class ScheduleFile implements Notifier, Serializable {
    private final HashMap<Integer, TrainSchedule> multipleTrainSchedules = new HashMap<>();
    private final HashMap<Integer, TrainScheduleSubject> multipleTrainScheduleSubjects = new HashMap<>();
    private int trainNum;
    private final ScheduleFileSubject subject;

    private String scheduleFileName;
    private String lastModified;

    public ScheduleFile(String scheduleFileName, String lastModified){
        this.scheduleFileName = scheduleFileName;
        this.lastModified = lastModified;
        this.trainNum = 0;
        subject = new ScheduleFileSubject(this);
    }

    public void putTrainSchedule(int trainID, TrainSchedule schedule) {
        multipleTrainSchedules.put(trainID, schedule);
        multipleTrainScheduleSubjects.put(trainID, schedule.getSubject());
        trainNum++;
    }

    public void removeTrainSchedule(int index) {
        multipleTrainSchedules.remove(index);
        multipleTrainScheduleSubjects.remove(index);
        trainNum--;
    }

    public TrainSchedule getTrainSchedule(int trainID) {
        return multipleTrainSchedules.get(trainID);
    }

    public HashMap<Integer, TrainSchedule> getMultipleTrainSchedules() {
        return this.multipleTrainSchedules;
    }

    public void setScheduleFileName(String scheduleFileName) {
        this.scheduleFileName = scheduleFileName;
        notifyChange(SCHEDULE_FILE_NAME_PROPERTY, scheduleFileName);
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
        notifyChange(LAST_MODIFIED_PROPERTY, lastModified);
    }

    public String getScheduleFileName() {
        return this.scheduleFileName;
    }

    public String getLastModified() {
        return this.lastModified;
    }

    public HashMap<Integer, TrainScheduleSubject> getMultipleTrainScheduleSubjects() {
        return this.multipleTrainScheduleSubjects;
    }

    public int getNumTrains() {
        return this.trainNum;
    }

    public ScheduleFileSubject getSubject() {
        return this.subject;
    }

    public void notifyChange(String property, Object newValue) {
        subject.setProperty(property, newValue);
    }
}
