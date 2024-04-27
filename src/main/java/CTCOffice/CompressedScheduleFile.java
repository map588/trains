package CTCOffice;

import java.io.Serializable;
import java.util.ArrayList;

public record CompressedScheduleFile(
    String scheduleFileName,
    String lastModified,
    ArrayList<CompressedTrainSchedule> multipleTrainSchedules)
    implements Serializable {}

record CompressedTrainSchedule(
    int trainID,
    String trainLine,
    double dispatchTime,
    int carCount,
    ArrayList<CompressedStop> stops)
    implements Serializable {}

record CompressedStop(
    int stopBlockID,
    double arrivalTime,
    double departureTime,
    ArrayList<Double> speedList,
    ArrayList<Integer> routePath,
    ArrayList<Double> authorityList)
    implements Serializable {}
