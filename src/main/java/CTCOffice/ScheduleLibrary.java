package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

import java.io.*;

public class ScheduleLibrary extends SubjectMap<String, ScheduleFileSubject> {
    private static final ScheduleLibrary INSTANCE = new ScheduleLibrary();
    private ScheduleLibrary() {
        super();
    }
    public static ScheduleLibrary getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<String, ScheduleFileSubject> getSubjects() {
        return super.getSubjects();
    }
    public void removeScheduleFile(String scheduleFileName) {
        super.removeSubject(scheduleFileName);
    }

    public void saveScheduleFile(String fileName, ScheduleFile scheduleFile){
        try{
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            scheduleFile.setScheduleFileName(fileName);
            out.writeObject(scheduleFile);
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadScheduleFile(String fileName){
        ScheduleFile scheduleFile = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            scheduleFile = (ScheduleFile) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(scheduleFile != null) {
            ScheduleLibrary.getInstance().registerSubject(fileName, new ScheduleFileSubject(scheduleFile));
        }else{
            System.err.println("Failed to load schedule file: " + fileName);
        }
    }
}
