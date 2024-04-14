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
public class CTCBlockSubjectMap extends SubjectMap<BlockIDs, CTCBlockSubject> {
    private static final CTCBlockSubjectMap INSTANCE = new CTCBlockSubjectMap();
    private CTCBlockSubjectMap() {
        super();
    }
    public static CTCBlockSubjectMap getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<BlockIDs, CTCBlockSubject> getSubjects() {
        return super.getSubjects();
    }

    public CTCBlockSubject getSubject(BlockIDs ID) {
        return super.getSubject(ID);
    }
}
