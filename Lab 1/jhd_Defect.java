public class jhd_Defect {
    
    private static int maxCars = 7;

    //This function determines if the number of cars
    //on a train exceeds the maximum number of cars
    public static boolean Defect_1(int numCars) {
        if(numCars < maxCars) {
            return true;
        }
        else {
            return false;
        }
    }

    //This functions returns the distance traveled in mph
    //(yes I know this one is silly)
    public static float Defect_2(float seconds, float meters) {
        return seconds*meters;
    }

    //This function gives the All Aboard call when it
    //is time to leave the station
    public static void Defect_3(boolean timeToLeave) {
        if(!timeToLeave) {
            System.out.println("ALL ABOARD");
        }
    }

    public static void main(String[] args) {
        Defect_3(false);
        System.out.println("The train is travelling " + Defect_2(3, 5) + " miles per hour");
        System.out.println("7 cars are within the maximum number of cars: " + Defect_1(7));
    }
}
