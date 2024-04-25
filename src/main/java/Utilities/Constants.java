package Utilities;

import Utilities.Enums.Direction;

public class Constants {

    // Constants for representing states on the track
    /**
     * SWITCH_MAIN represents the main line being used or lower block number
     */
    final public static boolean SWITCH_MAIN = false;
    /**
     * SWITCH_ALT represents the alternate line being used or higher block number
     */
    final public static boolean SWITCH_ALT = true;
    final public static boolean LIGHT_RED = false;
    final public static boolean LIGHT_GREEN = true;

    final public static boolean CROSSING_CLOSED = false;
    final public static boolean CROSSING_OPEN = true;

    final public static boolean RED_LINE = false;
    final public static boolean GREEN_LINE = true;

    //Time Step Interval
    final public static double TIME_STEP_MS = 125;
    final public static double TIME_STEP_S = TIME_STEP_MS / 1000;

    final public static Direction YARD_OUT_DIRECTION = Direction.NORTH;

    final public static int YARD_OUT_BLOCK = 0;

    //Physics Constants
    final public static double GRAVITY = 9.81; // m/s^2


    final public static double SERVICE_BRAKE_DECELERATION = 1.2; // m/s^2
    final public static double SERVICE_BRAKE_FORCE = 61724; // N
    final public static double EMERGENCY_BRAKE_DECELERATION = 2.73; // m/s^2
    final public static double EMERGENCY_BRAKE_FORCE = 140424; //N
    final public static double TRAIN_LENGTH = 32.23; // m
    final public static double TRAIN_HEIGHT = 3.42; // m
    final public static double TRAIN_WIDTH = 2.65; // m

    final public static double EMPTY_TRAIN_MASS = 37103.86; // kg
    final public static double LOADED_TRAIN_MASS = 51437.37; // kg
    final public static double PASSENGER_MASS = 83.9146; // kg
    final public static double MAX_SPEED = 19.44; // m/s
    final public static double MAX_POWER_KW = 480.0; // kW
    final public static double MAX_POWER_W = MAX_POWER_KW * 1000; // W
    final public static double MAX_POWER_HP = MAX_POWER_W / 745.7; // HP
    final public static double MAX_ENGINE_FORCE = 40000; //N
    final public static int MAX_PASSENGERS = 222;

    public static final int STOP_TRAIN_SIGNAL = -2;
    public static final int RESUME_TRAIN_SIGNAL = -3;
}

