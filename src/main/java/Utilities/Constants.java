package Utilities;

public class Constants {

    // Constants for representing states on the track
    final public static boolean SWITCH_MAIN = false;
    final public static boolean SWITCH_ALT = true;

    final public static boolean LIGHT_RED = false;
    final public static boolean LIGHT_GREEN = true;

    final public static boolean CROSSING_CLOSED = false;
    final public static boolean CROSSING_OPEN = true;

    final public static boolean RED_LINE = false;
    final public static boolean GREEN_LINE = true;

    //Standard gravity
    final public static double GRAVITY = 9.81; // m/s^2

    final public static double SERVICE_BRAKE_DECELERATION = 1.2; // m/s^2
    final public static double EMERGENCY_BRAKE_DECELERATION = 2.73;
    final public static double TRAIN_LENGTH = 32.23;

    final public static double EMPTY_TRAIN_MASS_KG = 37103.86;
    final public static double PASSENGER_MASS_KG = 83.9146;


    final public static double MAX_POWER_KW = 480.0;
}
