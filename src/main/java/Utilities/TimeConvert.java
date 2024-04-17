package Utilities;

import CTCOffice.CTCOfficeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeConvert {
    final public static int START_TIME = 6; //6:00 AM
    final public static int END_TIME = 22; //10:00 PM
    private static final Logger logger = LoggerFactory.getLogger(TimeConvert.class.getName());

    public static double covertTimeStampToDouble(String timeStamp) {
        String[] time = timeStamp.split(":");
        int hours = Integer.parseInt(time[0]) - START_TIME;
        if(hours < 0) {
            logger.warn("Time called to convert is before the start of the day");
        }
        int minutes = Integer.parseInt(time[1]);
        int seconds = Integer.parseInt(time[2]);
        return (hours*60*60) + (minutes*60) + seconds;
    }

    public static String convertDoubleToTimeStamp(double time) {
        double calculatedTime = time + (START_TIME*60*60);
        if(calculatedTime > (END_TIME*60*60)) {
            logger.warn("Time called to convert is after the end of the day");
        }
        int hours = (int) (calculatedTime / 3600);
        int minutes = (int) ((calculatedTime - (hours*3600)) / 60);
        int seconds = (int) (calculatedTime - (hours*3600) - (minutes*60));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String convertDoubleToClockTime(double time) {
        double calculatedTime = time + (START_TIME*60);
        int hours = (int) calculatedTime/60;
        int minutes = (int) (calculatedTime - (hours*60));
        return String.format("%02d:%02d", hours, minutes);
    }

    public static double convertClockTimeToDouble(String clockTime) {
        String[] time = clockTime.split(":");
        int hours = Integer.parseInt(time[0]) - START_TIME;
        if(hours < 0) {
            logger.warn("Time called to convert is before the start of the day");
        }
        int minutes = Integer.parseInt(time[1]);
        return (hours*60) + minutes;
    }

}
