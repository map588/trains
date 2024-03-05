package CTCOffice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class FullScheduleFile {
    private HashMap<Integer, SingleTrainSchedule> multipleTrainSchedules = new HashMap<>();
    private HashMap<Integer, SingleTrainScheduleSubject> multipleTrainScheduleSubjects = new HashMap<>();
    private FullScheduleFileSubject subject;


    private String scheduleFileName;
    private String lastModified;

    public FullScheduleFile(String scheduleFileName, String lastModified) {
        this.scheduleFileName = scheduleFileName;
        this.lastModified = lastModified;

    }

    public void putTrainSchedule(int trainID, SingleTrainSchedule schedule) {
        multipleTrainSchedules.put(trainID, schedule);
        multipleTrainScheduleSubjects.put(trainID, schedule.getSubject());
    }

    public void removeTrainSchedule(int index) {
        multipleTrainSchedules.remove(index);
        multipleTrainScheduleSubjects.remove(index);
    }

    public SingleTrainSchedule getTrainSchedule(int trainID) {
        return multipleTrainSchedules.get(trainID);
    }
    public HashMap<Integer, SingleTrainSchedule> getMultipleTrainSchedules() {
        return this.multipleTrainSchedules;
    }

    public void setScheduleFileName(String scheduleFileName) {
        this.scheduleFileName = scheduleFileName;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getScheduleFileName() {
        return this.scheduleFileName;
    }

    public String getLastModified() {
        return this.lastModified;
    }

    public HashMap<Integer, SingleTrainScheduleSubject> getMultipleTrainScheduleSubjects() {
        return this.multipleTrainScheduleSubjects;
    }
}
