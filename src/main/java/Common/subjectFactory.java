package Common;

/**
 * THe Type will be the type of subject that is being created
 * @param <T>
 *
 * @method assignSubject Adds to a list of previously created subjects
 * @method removeSubject Removes a subject from the list
 */
public interface subjectFactory<T> {

    public T    getSubject(int ID);

    public void addSubject(T subject);
    public void deleteSubject(int ID);

    public int getNumSubjects();
}
