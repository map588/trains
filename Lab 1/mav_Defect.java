
public class mav_Defect {

    private static float maxLoad = 100.0;
    
    // This function sounds the horn three times
    public static void Defect_1() {
        for(int i = 0; i <= 3; i++) {
            System.out.println("Honk!");
        }
    }
    
    // This function determines whether the train should continue given
    // whether the track ahead is clear
    public static boolean Defect_2(boolean isTrackAheadClear) {
        if(isTrackAheadClear) {
            return false;
        }
        else {
            return true;
        }
    }
    
    // Returns whether the given load is within the maximum load of the train
    public static boolean Defect_3(float load) {
        if(maxLoad <= load) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public static void main(String[] args){
        Defect_1();
        System.out.println("Train continues given track clear: " + Defect_2(true));
        System.out.println("Train continues given track not clear: " + Defect_2(false));
        System.out.println("50lb load is within maximum load: " + Defect_3(50.0));
    }
}
