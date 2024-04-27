package Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeConvert {
  public static final int START_TIME = 21600; // 6:00 AM
  public static final int END_TIME = 79200; // 10:00 PM
  private static final Logger logger = LoggerFactory.getLogger(TimeConvert.class.getName());

  public static double covertTimeStampToDouble(String timeStamp) {
    String[] time = timeStamp.split(":");
    int hours = Integer.parseInt(time[0]);
    if (hours < 0) {
      logger.warn("Time called to convert is before the start of the day");
    }
    int minutes = Integer.parseInt(time[1]);
    int seconds = Integer.parseInt(time[2]);
    return ((hours * 60 * 60) + (minutes * 60) + seconds) - START_TIME;
  }

  public static String convertDoubleToTimeStamp(double time) {
    double calculatedTime = time + START_TIME;
    if (calculatedTime > END_TIME) {
      logger.warn("Time called to convert is after the end of the day");
    }
    int hours = (int) (calculatedTime / 3600);
    int minutes = (int) ((calculatedTime - (hours * 3600)) / 60);
    int seconds = (int) (calculatedTime - (hours * 3600) - (minutes * 60));
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public static String convertDoubleToClockTime(double time) {
    double calculatedTime = time + (START_TIME);
    int hours = (int) calculatedTime / (3600);
    int minutes = (int) ((calculatedTime - (hours * 3600)) / 60);
    return String.format("%02d:%02d", hours, minutes);
  }

  public static double convertClockTimeToDouble(String clockTime) {
    String[] time = clockTime.split(":");
    int hours = Integer.parseInt(time[0]);
    if (hours < 0) {
      logger.warn("Time called to convert is before the start of the day");
    }
    int minutes = Integer.parseInt(time[1]);
    return ((hours * 60 * 60) + (minutes * 60)) - START_TIME;
  }

  public static String convertClockTimeToTimeStamp(String clockTime) {
    String[] time = clockTime.split(":");
    int hours = Integer.parseInt(time[0]);
    int minutes = Integer.parseInt(time[1]);
    int seconds = 0;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public static String convertTimeStampToClockTime(String timeStamp) {
    String[] time = timeStamp.split(":");
    int hours = Integer.parseInt(time[0]);
    int minutes = Integer.parseInt(time[1]);
    return String.format("%02d:%02d", hours, minutes);
  }
}
