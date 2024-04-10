package trackModel;

public class NullTrackLine extends TrackLine {
    private static final NullTrackLine INSTANCE = new NullTrackLine();

    private NullTrackLine() {
        super();
    }

    public static NullTrackLine getInstance() {
        return INSTANCE;
    }
}
