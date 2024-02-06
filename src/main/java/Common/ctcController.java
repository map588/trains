package Common;

public interface ctcController {
    trainModel dispatch(int trainID, int blockID, int initialAuthority, int initialSpeed, int crewCount, int passengerCount, int carCount, int line);
}
