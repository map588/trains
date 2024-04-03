package trainController;

public enum Property_enum {
        AUTHORITY("authority"),
        OVERRIDE_SPEED("overrideSpeed"),
        COMMAND_SPEED("commandSpeed"),
        AUTOMATIC_MODE("automaticMode"),
        CURRENT_SPEED("currentSpeed"),
        SERVICE_BRAKE("serviceBrake"),
        EMERGENCY_BRAKE("emergencyBrake"),
        KI("Ki"),
        KP("Kp"),
        POWER("power"),
        INT_LIGHTS("intLights"),
        EXT_LIGHTS("extLights"),
        LEFT_DOORS("leftDoors"),
        RIGHT_DOORS("rightDoors"),
        TEMPERATURE("temperature"),
        ANNOUNCEMENTS("announcements"),
        SIGNAL_FAILURE("signalFailure"),
        BRAKE_FAILURE("brakeFailure"),
        POWER_FAILURE("powerFailure"),
        IN_TUNNEL("inTunnel"),
        LEFT_PLATFORM("leftPlatform"),
        RIGHT_PLATFORM("rightPlatform"),
        SAMPLING_PERIOD("samplingPeriod"),
        SPEED_LIMIT("speedLimit"),
        NEXT_STATION("nextStationName"),
        GRADE("grade"),
        TRAIN_ID("trainID");
     

     private final String propertyName;

     Property_enum(String propertyName) {
          this.propertyName = propertyName;
     }

     public String getPropertyName() {
          return propertyName;
     }
     }
