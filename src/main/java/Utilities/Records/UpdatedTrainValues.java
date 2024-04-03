package Utilities.Records;

public record UpdatedTrainValues(
        double   power,
        boolean  serviceBrake,
        boolean  emergencyBrake,
        double   setTemperature
) {
}
