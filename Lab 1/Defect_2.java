import java.util.Scanner;

// This is suppose to demonstrate the Defect as a Reproducible

public class Defect_2 {
    public static void Defect_2(){

        // User Input
        Scanner input = new Scanner(System.in);

        System.out.println("\n\nWelcome to the J.A.M.E.S Express, would you like a bottle of water?");
        System.out.print("Y/N: ");

        String answer = input.nextLine();

        boolean isYes = answer.contains("Y"); // Note the cause of the error is the ! in front of the answer.contains

        // Intended: Yes = buys bottle, No = don't buy
        // Logic Error: Yes = Don't buy, No = Buys Bottle
        if (isYes)
            System.out.println("Okay then that will be $10. Thank you and have a nice day!\n\n");

        else
            System.out.println("Oh wells that's too bad. Have a nice day!\n\n");
    }

    public static void main(String[] args){
        Defect_2();
    }

}
