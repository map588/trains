package Utilities;

import Utilities.Enums.Direction;

public class Constants {

  // Constants for representing states on the track
  /** SWITCH_MAIN represents the main line being used or lower block number */
  public static final boolean SWITCH_MAIN = false;

  /** SWITCH_ALT represents the alternate line being used or higher block number */
  public static final boolean SWITCH_ALT = true;

  public static final boolean LIGHT_RED = false;
  public static final boolean LIGHT_GREEN = true;

  public static final boolean CROSSING_CLOSED = false;
  public static final boolean CROSSING_OPEN = true;

  public static final boolean RED_LINE = false;
  public static final boolean GREEN_LINE = true;

  // Time Step Interval
  public static final double TIME_STEP_MS = 125;
  public static final double TIME_STEP_S = TIME_STEP_MS / 1000;

  public static final Direction YARD_OUT_DIRECTION = Direction.NORTH;

  public static final int YARD_OUT_BLOCK = 0;

  // Physics Constants
  public static final double GRAVITY = 9.81; // m/s^2

  public static final double SERVICE_BRAKE_DECELERATION = 1.2; // m/s^2
  public static final double SERVICE_BRAKE_FORCE = 61724; // N
  public static final double EMERGENCY_BRAKE_DECELERATION = 2.73; // m/s^2
  public static final double EMERGENCY_BRAKE_FORCE = 140424; // N
  public static final double TRAIN_LENGTH = 32.23; // m
  public static final double TRAIN_HEIGHT = 3.42; // m
  public static final double TRAIN_WIDTH = 2.65; // m

  public static final double EMPTY_TRAIN_MASS = 37103.86; // kg
  public static final double LOADED_TRAIN_MASS = 51437.37; // kg
  public static final double PASSENGER_MASS = 83.9146; // kg
  public static final double MAX_SPEED = 19.44; // m/s
  public static final double MAX_POWER_KW = 480.0; // kW
  public static final double MAX_POWER_W = MAX_POWER_KW * 1000; // W
  public static final double MAX_POWER_HP = MAX_POWER_W / 745.7; // HP
  public static final double MAX_ENGINE_FORCE = 40000; // N
  public static final int MAX_PASSENGERS = 222;

  public static final int STOP_TRAIN_SIGNAL = -2;
  public static final int RESUME_TRAIN_SIGNAL = -3;
}
