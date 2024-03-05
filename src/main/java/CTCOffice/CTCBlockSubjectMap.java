package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

public class CTCBlockSubjectMap extends SubjectMap<Integer, CTCBlockSubject> {
    private static final CTCBlockSubjectMap INSTANCE = new CTCBlockSubjectMap();
    private CTCBlockSubjectMap() {
        super();
    }
    public static CTCBlockSubjectMap getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, CTCBlockSubject> getSubjects() {
        return super.getSubjects();
    }

    public CTCBlockSubject getSubject(int ID) {
        return super.getSubject(ID);
    }
}
