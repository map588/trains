package trainController;

//TODO: Change this to an enum in the future
//public class Properties {
//     static final String AUTHORITY_PROPERTY = "authority";
//     static final String OVERRIDE_SPEED_PROPERTY = "overrideSpeed";
//     static final String COMMAND_SPEED_PROPERTY = "commandSpeed";
//     static final String AUTOMATIC_MODE_PROPERTY = "automaticMode";
//     static final String CURRENT_SPEED_PROPERTY = "currentSpeed";
//     static final String SERVICE_BRAKE_PROPERTY = "serviceBrake";
//     static final String EMERGENCY_BRAKE_PROPERTY = "emergencyBrake";
//     static final String KI_PROPERTY = "Ki";
//     static final String KP_PROPERTY = "Kp";
//     static final String POWER_PROPERTY = "power";
//     static final String INT_LIGHTS_PROPERTY = "intLights";
//     static final String EXT_LIGHTS_PROPERTY = "extLights";
//     static final String LEFT_DOORS_PROPERTY = "leftDoors";
//     static final String RIGHT_DOORS_PROPERTY = "rightDoors";
//     static final String SET_TEMPERATURE_PROPERTY = "setTemperature";
//     static final String CURRENT_TEMPERATURE_PROPERTY ="currentTemperature";
//     static final String ANNOUNCEMENTS_PROPERTY = "announcements";
//     static final String SIGNAL_FAILURE_PROPERTY = "signalFailure";
//     static final String BRAKE_FAILURE_PROPERTY = "brakeFailure";
//     static final String POWER_FAILURE_PROPERTY = "powerFailure";
//     static final String IN_TUNNEL_PROPERTY = "inTunnel";
//     static final String LEFT_PLATFORM_PROPERTY = "leftPlatform";
//     static final String RIGHT_PLATFORM_PROPERTY = "rightPlatform";
//     static final String SAMPLING_PERIOD_PROPERTY = "samplingPeriod";
//     static final String SPEED_LIMIT_PROPERTY = "speedLimit";
//     static final String NEXT_STATION_PROPERTY = "nextStationName";
//     static final String GRADE_PROPERTY = "grade";
//     static final String TRAIN_ID_PROPERTY = "trainID";
//
//     static final String ERROR_PROPERTY = "error";
//}
public enum Controller_Property {
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

Controller_Property(String propertyName) {
     this.propertyName = propertyName;
}

public String getPropertyName() {
     return propertyName;
}
}
