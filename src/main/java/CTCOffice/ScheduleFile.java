package CTCOffice;

import static CTCOffice.Properties.ScheduleProperties.*;

import Framework.Support.Notifier;
import java.util.HashMap;

public class ScheduleFile implements Notifier {
  private final HashMap<Integer, TrainSchedule> multipleTrainSchedules = new HashMap<>();
  private final HashMap<Integer, TrainScheduleSubject> multipleTrainScheduleSubjects =
      new HashMap<>();
  private int trainNum;
  private final ScheduleFileSubject subject;

  private String scheduleFileName;
  private String lastModified;

  public ScheduleFile(String scheduleFileName, String lastModified) {
    this.scheduleFileName = scheduleFileName;
    this.lastModified = lastModified;
    this.trainNum = 0;
    subject = new ScheduleFileSubject(this);
  }

  public ScheduleFile(CompressedScheduleFile compressedScheduleFile) {
    this.scheduleFileName = compressedScheduleFile.scheduleFileName();
    this.lastModified = compressedScheduleFile.lastModified();
    this.trainNum = compressedScheduleFile.multipleTrainSchedules().size();
    for (CompressedTrainSchedule compressedTrainSchedule :
        compressedScheduleFile.multipleTrainSchedules()) {
      TrainSchedule trainSchedule = new TrainSchedule(compressedTrainSchedule);
      multipleTrainSchedules.put(compressedTrainSchedule.trainID(), trainSchedule);
      multipleTrainScheduleSubjects.put(
          compressedTrainSchedule.trainID(), trainSchedule.getSubject());
    }
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
