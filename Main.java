// Jason Nguyen, September 28th 2026, CS303 Program 2: Maps
import java.io.*;
import java.util.*;

public class Main {


    public static void main(String[] args) {

        System.out.println("Welcome to Program 2: Maps");
        Map<Integer, ArrayList<String>> TVList = new HashMap<>();

        Scanner input = new Scanner(System.in);

        //open output file
        try{
            PrintWriter out = new PrintWriter("report.txt");
            
            //load data into map
            Functions.loadData(TVList);

            //sort the map
            Map<Integer, ArrayList<String>> sortedTVList = new TreeMap<>(TVList);

            String menuItem = Functions.getMenuItem(input);

            while (!menuItem.equals("Q")){
                //test for valid menu options & call appropriate functions
                switch (menuItem) {
            case "A":
                Functions.addShow(TVList, input, out);
                break;
            case "D":
                Functions.deleteShow(TVList, input, out);
                break;
            case "K":
                Functions.printKeys(TVList, out);
                break;
            case "P":
                Functions.printMap(TVList, out);
                break;
            case "S":
                Functions.printSpecificKey(TVList, input, out);
                break;
            // no default needed — getMenuItem() already guarantees
            // menuItem is one of A/D/K/P/S/Q before we ever get here
        }
        
        out.flush(); // force any buffered writes to disk after every
                 // action, so report.txt is always current even if
                 // the program exits abnormally before reaching Q

                menuItem = Functions.getMenuItem(input);
            }

            //close files
            input.close();
            out.close();

        }
        catch (Exception e){
            System.out.println("Error in input record");
            return;
        }
    }

}
