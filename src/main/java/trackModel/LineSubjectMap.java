package trackModel;

import java.util.concurrent.ConcurrentHashMap;


class LineSubjectMap {

    private static ConcurrentHashMap<String, TrackLineSubject> lineSubjects;

    public static void addLineSubject(String line, TrackLineSubject subject) {
        lineSubjects.put(line, subject);
    }

    public static TrackLineSubject getLineSubject(String line) {
        return lineSubjects.get(line);
    }

    public static ConcurrentHashMap<String, TrackLineSubject> getLineSubjects() {
        return lineSubjects;
    }

    private LineSubjectMap() {
        lineSubjects = new ConcurrentHashMap<>();
    }

    private static final LineSubjectMap INSTANCE = new LineSubjectMap();

    public static LineSubjectMap getInstance() {
        return INSTANCE;
    }
}

