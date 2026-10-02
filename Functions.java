// Some logic in this file (CSV parsing regex, exception handling
// patterns, and code review) was developed with assistance from
// Claude (Anthropic).

import java.util.*;   
import java.io.*;
                    
public class Functions {

    public static void loadData(Map<Integer, ArrayList<String>> TVList){
        String fileName = "tv.csv";

        // PRE: TVList must be an initialized (non-null) empty or partially-filled map.
        //      tv.csv must exist in the working directory.
        // POST: TVList is populated with duration -> show names from tv.csv.
        //       Malformed lines are skipped and logged, program continues.
    
        //read the file & load the map
        try{
            Scanner inFile = new Scanner(new File(fileName));
            
            while (inFile.hasNext()){
                String inputRecord = inFile.nextLine();
                try{
                //set up data 
                // Split on commas that are NOT inside double quotes.
                // (?=...) is a lookahead: "only split here if everything
                // After this point has balanced quote pairs before EOL")

                String[] fields = inputRecord.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // field[0] = duration (the key), field[1] = show name
                int duration = Integer.parseInt(fields[0].trim());
                String showName = fields[1].trim();

                // If the name got wrapped in quotes (because it had a
                // comma inside), strip the leading/trailing quote chars
                if (showName.startsWith("\"") && showName.endsWith("\"")) {
                    showName = showName.substring(1, showName.length() - 1);
                }

                // computeIfAbsent = "if this key has no ArrayList yet,
                // make one" — then add the name to it either way
                TVList.computeIfAbsent(duration, k -> new ArrayList<>()).add(showName);
            }
            catch (Exception e){
                System.out.println("Error in input record");
            }
        }
        inFile.close();

                //add to map
        }
        catch (Exception e){
            System.out.println("Error in input record");
        }
            }
    

    public static String getMenuItem(Scanner input) {
        String choice = " ";
        System.out.println("\nACTIONS FOR TVSHOW MAP");
        System.out.println("A: Add a Show ");
        System.out.println("D: Delete a Show ");
        System.out.println("K: Print All Keys (Durations) to Report");
        System.out.println("P: Print Map Listing to Report ");
        System.out.println("S: Print Specific Key (Duration) Listing to Report ");
        System.out.println("Q: Quit ");
        System.out.print("Please enter your choice: ");
        choice = input.nextLine().toUpperCase().trim();

        while (!( choice.equals("A") || choice.equals("D") ||
                  choice.equals("K") || choice.equals("P") ||
                  choice.equals("S") || choice.equals("Q"))){
            System.out.print("You entered an invalid value. Please enter a valid choice: ");
            choice = input.nextLine().toUpperCase().trim();
        }
        System.out.println();
        return choice;
    }
    // PRE:  TVList is initialized; input/out are open and ready.
    // POST: Prompts user for duration + show name, adds to TVList,
    //       prints confirmation to screen and report file.

    //    TEST CASE 1: Adding a show with a duration that already exists 
    //       unless that exact show al ready exists at that duration,
    //       in which case it's rejected. Prints confirmation or
    //       rejection message to screen and report file.
 
    public static void addShow(Map<Integer, ArrayList<String>> TVList, Scanner input, PrintWriter out) {
        int duration = -1;          // sentinel value: -1 means "not parsed yet"
    boolean validInput = false; // loop control flag

    while (!validInput) {
        System.out.print("Please enter the duration (in years): ");
        String durationStr = input.nextLine().trim();

        try {
            duration = Integer.parseInt(durationStr);
            validInput = true;   // only reached if parseInt didn't throw
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid duration. Please enter a whole number.");
            // validInput stays false -> loop runs again
        }
    }

    // At this point duration is GUARANTEED valid — the loop cannot
    // exit any other way. No need to re-check it below.
    System.out.print("Enter the name of the show: ");
    String showName = input.nextLine().trim();

    // computeIfAbsent still creates the list on first use, but now we
    // check its contents before adding, not just whether the key exists.
    ArrayList<String> shows = TVList.computeIfAbsent(duration, k -> new ArrayList<>());

    String message;
    if (shows.contains(showName)) {
        // Duplicate — same name already under this exact duration.
        // Reject instead of silently doubling the entry.
        message = "A: \"" + showName + "\" already exists at duration " +
                   duration + " years. Not added again.";
    }
    else {
        shows.add(showName);
        message = "A: This new show was added to the map: " + showName +
                   " with the duration of " + duration + " years.";
    }

    System.out.println(message);
    out.println(message);
    }
    
    
    // PRE:  TVList is initialized; input/out are open and ready.
    // POST:  Prompts for a show name and removes it from whichever
    //       duration's list contains it.


    //    TEST CASE 2: Deleting a show that exists in the map, which may or may not
    //       leave the duration's list empty. If the list becomes empty,
    //       the key itself is removed from the map. If the show doesn't    
    //       exist, a rejection message is printed to screen and report file.   

    public static void deleteShow(Map<Integer, ArrayList<String>> TVList, Scanner input, PrintWriter out) {
         System.out.print("Please enter the show name to delete: ");
    String showName = input.nextLine().trim();

    boolean found = false;

    // Check every key's list, since we don't know which duration
    // this show lives under. keyS
    // et() gives us every key currently
    // in the map to loop over.
    for (Integer key : TVList.keySet()) {
        ArrayList<String> shows = TVList.get(key);

        if (shows.contains(showName)) {
            shows.remove(showName);
            found = true;
            if (shows.isEmpty()) {
                TVList.remove(key);
            }

            break;
        }
    }

    String message;
    if (found) {
        message = "R: Deleted item " + showName + " from the map";
    } else {
        message = "R: Unable to delete " + showName + ". Item is not in the map";
    }

    System.out.println(message);
    out.println(message);
    }

    // PRE:  TVList is initialized; out is open and ready.
    // POST: Writes all keys (durations) in TVList to the report file.
    public static void printKeys(Map<Integer, ArrayList<String>> TVList, PrintWriter out) {
        Map<Integer, ArrayList<String>> sorted = new TreeMap<>(TVList);

    out.println("Keys (Durations) in the map:");
    for (Integer key : sorted.keySet()) {
        out.println(key);    
    }

    System.out.println("K: Key listing was printed to the report file.");
}

    // PRE:  TVList is initialized; out is open and ready.
    // POST: Writes the full map (all keys + their show lists) to the report file.
    public static void printMap(Map<Integer, ArrayList<String>> TVList, PrintWriter out) {
        Map<Integer, ArrayList<String>> sorted = new TreeMap<>(TVList);
        out.println("Full map listing (Duration -> Show Names):");

        // Outer loop: walk each duration in ascending order
    for (Integer key : sorted.keySet()) {
        ArrayList<String> shows = sorted.get(key);

        out.println("Duration: " + key + " years");

        // Inner loop: print every show name under this duration
        for (String show : shows) {
            out.println("   - " + show);
        }   
    }

    out.println("P: Map listing was printed to the report file.");
}


    // PRE:  TVList is initialized; input/out are open and ready.
    // POST: Prompts user for a duration, writes all shows with that
    //       duration to the report file.
    public static void printSpecificKey(Map<Integer, ArrayList<String>> TVList, Scanner input, PrintWriter out) {
        
    
    int duration = -1;
    boolean validInput = false;

    while (!validInput) {
        System.out.print("Please enter the duration to list: ");
        String durationStr = input.nextLine().trim();

        try {
            duration = Integer.parseInt(durationStr);
            validInput = true;
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid duration. Please enter a whole number.");
        }
    }

    String message; // built once, printed to both destinations either way

    if (TVList.containsKey(duration)) {
        ArrayList<String> shows = TVList.get(duration);

        out.println("Shows with duration of " + duration + " years:");
        for (String show : shows) {
            out.println("   - " + show);
        }

        message = "S: Shows with the duration of " + duration +
                   " were written to the report file.";
    }
    else {
        message = "S: No shows found with the duration of " + duration + " years.";
    }

    System.out.println(message);
    out.println(message);
    }

}
