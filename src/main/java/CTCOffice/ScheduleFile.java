package CTCOffice;

import java.util.HashMap;


public class ScheduleFile {
    private HashMap<Integer, TrainSchedule> multipleTrainSchedules = new HashMap<>();
    private HashMap<Integer, TrainScheduleSubject> multipleTrainScheduleSubjects = new HashMap<>();
    private ScheduleFileSubject subject;

    private String scheduleFileName;
    private String lastModified;

    public ScheduleFile(String scheduleFileName, String lastModified) {
        this.scheduleFileName = scheduleFileName;
        this.lastModified = lastModified;
    }

    public void putTrainSchedule(int trainID, TrainSchedule schedule) {
        multipleTrainSchedules.put(trainID, schedule);
        multipleTrainScheduleSubjects.put(trainID, schedule.getSubject());
    }

    public void removeTrainSchedule(int index) {
        multipleTrainSchedules.remove(index);
        multipleTrainScheduleSubjects.remove(index);
    }

    public TrainSchedule getTrainSchedule(int trainID) {
        return multipleTrainSchedules.get(trainID);
    }

    public HashMap<Integer, TrainSchedule> getMultipleTrainSchedules() {
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

    public HashMap<Integer, TrainScheduleSubject> getMultipleTrainScheduleSubjects() {
        return this.multipleTrainScheduleSubjects;
    }
}
