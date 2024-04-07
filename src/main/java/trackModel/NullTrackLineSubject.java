package trackModel;

public class NullTrackLineSubject extends TrackLineSubject {
    private static final NullTrackLineSubject INSTANCE = new NullTrackLineSubject();

    private NullTrackLineSubject() {
        // Initialize with default or empty values
    }

    public static NullTrackLineSubject getInstance() {
        return INSTANCE;
    }

    @Override
    public void setBrokenRail(boolean isBroken) {
        // Log, ignore, or handle lightly since it's a null object
    }

    // Override other methods similarly
}
