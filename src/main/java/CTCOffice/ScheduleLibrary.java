package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;
import java.io.*;
import java.util.ArrayList;

public class ScheduleLibrary extends SubjectMap<String, ScheduleFileSubject> {
  private static final ScheduleLibrary INSTANCE = new ScheduleLibrary();

  private ScheduleLibrary() {
    super();
  }

  public static ScheduleLibrary getInstance() {
    return INSTANCE;
  }

  public ObservableHashMap<String, ScheduleFileSubject> getSubjects() {
    return super.getSubjects();
  }

  public void removeScheduleFile(String scheduleFileName) {
    super.removeSubject(scheduleFileName);
  }

  public void saveScheduleFile(String fileName, ScheduleFile scheduleFile) {
    ArrayList<CompressedTrainSchedule> compressedTrainSchedules = new ArrayList<>();
    for (int i = 1; i <= scheduleFile.getMultipleTrainSchedules().size(); i++) {
      ArrayList<CompressedStop> compressedStops = new ArrayList<>();
      for (int j = 0; j < scheduleFile.getMultipleTrainSchedules().get(i).getStops().size(); j++) {
        ArrayList<Double> speedList = new ArrayList<>();
        ArrayList<Integer> routePath = new ArrayList<>();
        ArrayList<Double> authorityList = new ArrayList<>();
        for (int k = 0;
            k
                < scheduleFile
                    .getMultipleTrainSchedules()
                    .get(i)
                    .getStops()
                    .get(j)
                    .getRoutePath()
                    .size();
            k++) {
          routePath.add(
              scheduleFile
                  .getMultipleTrainSchedules()
                  .get(i)
                  .getStops()
                  .get(j)
                  .getRoutePath()
                  .get(k));
          speedList.add(
              scheduleFile
                  .getMultipleTrainSchedules()
                  .get(i)
                  .getStops()
                  .get(j)
                  .getSpeedList()
                  .get(k));
          authorityList.add(
              scheduleFile
                  .getMultipleTrainSchedules()
                  .get(i)
                  .getStops()
                  .get(j)
                  .getAuthorityList()
                  .get(k));
        }
        compressedStops.add(
            new CompressedStop(
                scheduleFile
                    .getMultipleTrainSchedules()
                    .get(i)
                    .getStops()
                    .get(j)
                    .getStationBlockID(),
                scheduleFile.getMultipleTrainSchedules().get(i).getStops().get(j).getArrivalTime(),
                scheduleFile
                    .getMultipleTrainSchedules()
                    .get(i)
                    .getStops()
                    .get(j)
                    .getDepartureTime(),
                speedList,
                routePath,
                authorityList));
      }
      compressedTrainSchedules.add(
          new CompressedTrainSchedule(
              scheduleFile.getMultipleTrainSchedules().get(i).getTrainID(),
              scheduleFile.getMultipleTrainSchedules().get(i).getLine(),
              scheduleFile.getMultipleTrainSchedules().get(i).getDispatchTime(),
              scheduleFile.getMultipleTrainSchedules().get(i).getCarCount(),
              compressedStops));
    }
    CompressedScheduleFile compressedScheduleFile =
        new CompressedScheduleFile(
            fileName, scheduleFile.getLastModified(), compressedTrainSchedules);
    try {

      FileOutputStream fileOut =
          new FileOutputStream(
              System.getProperty("user.dir")
                  + "/src/main/java/CTCOffice/schedule_files/"
                  + fileName);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(compressedScheduleFile);
      out.close();
      fileOut.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadScheduleFile(String fileName) {
    CompressedScheduleFile c = null;
    try {
      FileInputStream fileIn = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fileIn);
      c = (CompressedScheduleFile) in.readObject();
      in.close();
      fileIn.close();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (c != null) {
      ScheduleFile scheduleFile = new ScheduleFile(c);
      ScheduleLibrary.getInstance()
          .registerSubject(fileName, new ScheduleFileSubject(scheduleFile));
    } else {
      System.err.println("Failed to load schedule file: " + fileName);
    }
  }
}
