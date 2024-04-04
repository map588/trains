package Utilities.Records;

public record UpdatedTrainValues(
        double    power,
        boolean   serviceBrake,
        boolean   emergencyBrake,

        //Non-Vital
        double    setTemperature,
        boolean   interiorLights,
        boolean   exteriorLights,
        boolean   leftDoors,
        boolean   rightDoors
) {
}
