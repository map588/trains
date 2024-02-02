package Utilities;

public class Conversion {
    public enum distanceUnit {
        MILES,
        KILOMETERS,
        METERS,
        FEET
    }
    public enum velocityUnit {
        MPH,
        KPH,
        MPS,
        FPS
    }
    public enum temperatureUnit {
        FAHRENHEIT,
        CELSIUS,
        KELVIN
    }
    public enum accelerationUnit {
        MPS2,
        FPS2
    }

    public static double convertDistance(double distance, distanceUnit from, distanceUnit to) {
        if (from == to) {
            return distance;
        }
        if (from == distanceUnit.MILES) {
            if (to == distanceUnit.KILOMETERS) {
                return distance * 1.60934;
            }
            if (to == distanceUnit.METERS) {
                return distance * 1609.34;
            }
            if (to == distanceUnit.FEET) {
                return distance * 5280;
            }
        }
        if (from == distanceUnit.KILOMETERS) {
            if (to == distanceUnit.MILES) {
                return distance / 1.60934;
            }
            if (to == distanceUnit.METERS) {
                return distance * 1000;
            }
            if (to == distanceUnit.FEET) {
                return distance * 3280.84;
            }
        }
        if (from == distanceUnit.METERS) {
            if (to == distanceUnit.MILES) {
                return distance / 1609.34;
            }
            if (to == distanceUnit.KILOMETERS) {
                return distance / 1000;
            }
            if (to == distanceUnit.FEET) {
                return distance * 3.28084;
            }
        }
        if (from == distanceUnit.FEET) {
            if (to == distanceUnit.MILES) {
                return distance / 5280;
            }
            if (to == distanceUnit.KILOMETERS) {
                return distance / 3280.84;
            }
            if (to == distanceUnit.METERS) {
                return distance / 3.28084;
            }
        }
        return 0;
    }

    public static double convertVelocity(double velocity, velocityUnit from, velocityUnit to) {
        if (from == to) {
            return velocity;
        }
        if (from == velocityUnit.MPH) {
            if (to == velocityUnit.KPH) {
                return velocity * 1.60934;
            }
            if (to == velocityUnit.MPS) {
                return velocity * 0.44704;
            }
            if (to == velocityUnit.FPS) {
                return velocity * 1.46667;
            }
        }
        if (from == velocityUnit.KPH) {
            if (to == velocityUnit.MPH) {
                return velocity / 1.60934;
            }
            if (to == velocityUnit.MPS) {
                return velocity / 3.6;
            }
            if (to == velocityUnit.FPS) {
                return velocity / 1.09728;
            }
        }
        if (from == velocityUnit.MPS) {
            if (to == velocityUnit.MPH) {
                return velocity / 0.44704;
            }
            if (to == velocityUnit.KPH) {
                return velocity * 3.6;
            }
            if (to == velocityUnit.FPS) {
                return velocity * 3.28084;
            }
        }
        if (from == velocityUnit.FPS) {
            if (to == velocityUnit.MPH) {
                return velocity / 1.46667;
            }
            if (to == velocityUnit.KPH) {
                return velocity * 1.09728;
            }
            if (to == velocityUnit.MPS) {
                return velocity / 3.28084;
            }
        }
        return 0;
    }

    public static double convertTemperature(double temperature, temperatureUnit from, temperatureUnit to) {
        if (from == to) {
            return temperature;
        }
        if (from == temperatureUnit.FAHRENHEIT) {
            if (to == temperatureUnit.CELSIUS) {
                return (temperature - 32) * 5 / 9;
            }
            if (to == temperatureUnit.KELVIN) {
                return (temperature - 32) * 5 / 9 + 273.15;
            }
        }
        if (from == temperatureUnit.CELSIUS) {
            if (to == temperatureUnit.FAHRENHEIT) {
                return temperature * 9 / 5 + 32;
            }
            if (to == temperatureUnit.KELVIN) {
                return temperature + 273.15;
            }
        }
        if (from == temperatureUnit.KELVIN) {
            if (to == temperatureUnit.FAHRENHEIT) {
                return (temperature - 273.15) * 9 / 5 + 32;
            }
            if (to == temperatureUnit.CELSIUS) {
                return temperature - 273.15;
            }
        }
        return 0;
    }


}

