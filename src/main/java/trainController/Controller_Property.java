package trainController;

//TODO: Change this to an enum in the future
//public class Properties {

//}
public enum Controller_Property {
     AUTHORITY("authority"),
     OVERRIDE_SPEED("override_Speed"),
     COMMAND_SPEED("command_Speed"),
     AUTOMATIC_MODE("automatic_Mode"),
     CURRENT_SPEED("current_Speed"),
     SERVICE_BRAKE("service_Brake"),
     EMERGENCY_BRAKE("emergency_Brake"),
     KI("Ki"),
     KP("Kp"),
     POWER("power"),
     INT_LIGHTS("int_Lights"),
     EXT_LIGHTS("ext_Lights"),
     LEFT_DOORS("left_Doors"),
     RIGHT_DOORS("right_Doors"),
     CURRENT_TEMPERATURE("current_Temperature"),
     SET_TEMPERATURE("set_Temperature"),
     ANNOUNCEMENTS("announcements"),
     SIGNAL_FAILURE("signal_Failure"),
     BRAKE_FAILURE("brake_Failure"),
     POWER_FAILURE("power_Failure"),
     IN_TUNNEL("in_Tunnel"),
     LEFT_PLATFORM("left_Platform"),
     RIGHT_PLATFORM("right_Platform"),
     SAMPLING_PERIOD("sampling_Period"),
     SPEED_LIMIT("speed_Limit"),
     NEXT_STATION("next_Station"),
     GRADE("grade"),
     TRAIN_ID("train_ID"),
     ERROR("error");

     private final String propertyName;

     Controller_Property(String propertyName) {
          this.propertyName = propertyName;
     }

     public String getPropertyName() {
          return propertyName;
     }

     public Controller_Property getProperty(String propertyName) {
          return Controller_Property.valueOf(propertyName.toUpperCase());
     }
}
