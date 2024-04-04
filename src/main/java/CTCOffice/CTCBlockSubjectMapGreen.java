package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;
/**
 * This class represents a map of CTCBlockSubjects, with each subject associated with an Integer key.
 * It extends the SubjectMap class, inheriting its methods and properties.
 * It follows the Singleton design pattern, ensuring that only one instance of this class can exist.
 * @see SubjectMap
 * @see CTCBlockSubject
 */
public class CTCBlockSubjectMapGreen extends SubjectMap<Integer, CTCBlockSubject> {
    private static final CTCBlockSubjectMapGreen INSTANCE = new CTCBlockSubjectMapGreen();
    private CTCBlockSubjectMapGreen() {
        super();
    }
    public static CTCBlockSubjectMapGreen getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, CTCBlockSubject> getSubjects() {
        return super.getSubjects();
    }

    public CTCBlockSubject getSubject(int ID) {
        return super.getSubject(ID);
    }
}
