package fonder.acad.games.bullsandcows;

import fonder.acad.games.bullsandcows.task8.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Program
{
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        PlayableComputer com; char mode = 'G', choice = 'G';
        String[] contents = null;
        
        // Task 5+
        
        do
        {
            System.out.print("""
                                         Use a premove file (newline-separated value file)?
                                         [Y]: Yes (enter a valid file name)
                                         [N]: No (manually enter guesses live)
                                         : """);
            mode = scan.next().toUpperCase().charAt(0);
        }
        while (mode != 'Y' && mode != 'N');
        
        scan.nextLine(); // a little sneaky
        
        if (mode == 'Y') while (true)
        {
            System.out.print("""
                                         Enter a valid filename (absolute or relative)
                                         : """);
            
            try (BufferedReader fs = new BufferedReader(new FileReader(scan.nextLine().replace("\"",""))))
            {
                ArrayList<String> list = new ArrayList<>();
                int index=0;
                do
                {
                    String l = fs.readLine(); 
                    if (Computer.verify(l)) {list.add(l); index++; }
                    else System.out.printf("?%d line \"%s\" does not match set code format; will be ignored.\n", index,l);
                } 
                while (fs.ready());
                contents = list.toArray(new String[index]);
            }
            catch (FileNotFoundException ffs) { System.out.println("? file does not exist or path is in an incorrect format"); continue; }
            catch (IOException a) { System.out.printf("? file reading error: %s\n", a.getMessage()); continue; }
            break;
        }
        
        
        // Task 4+
        
        do
        {
            System.out.print("""
                                         Choose difficulty:
                                         [E]: Easy (the computer randomly guesses everytime, and you can customise the game!)
                                         [M]: Medium (the computer is a bit smarter, and the game is locked to 4 numeric characters and 7 turns.)
                                         : """);
            choice = scan.next().toUpperCase().charAt(0);
        }
        while (choice != 'E' && choice != 'M');
        
        
        // Task 8+
        
        char config = 'G'; int size = 4, turns = 7; String chset = "0123456789";
        if (choice == 'E') do
        {
            System.out.print("""
                                         Load configuration file?
                                         [Y]: Yes (the program will load an INI file with the settings)
                                         [N]: No (load with defaults: code length 4, 7 turns and numeric character set)
                                         : """);
            config = scan.next().toUpperCase().charAt(0);
        }
        while (config != 'Y' && config != 'N');  
        scan.nextLine(); // a little sneaky
        
        if (config == 'Y') while (true)
        {
            System.out.print("""
                                         Enter a valid filename (absolute or relative) [*.ini]
                                         : """);
            try (BufferedReader fs = new BufferedReader(new FileReader(scan.nextLine().replace("\"",""))))
            {
                boolean sz = false, tn = false, cs = false;
                do
                {
                    String ln = fs.readLine();
                    if (ln.startsWith(";")) continue;
                    if (ln.contains(";")) ln = ln.substring(0, ln.indexOf(';'));
                    
                    String[] keyVal = ln.split("=");
                    
                    switch (keyVal[0].trim().toUpperCase())
                    {
                        case "TURNS" -> { tn = true; turns = Integer.parseInt(keyVal[1].trim()); }
                        case "SIZE" -> { sz = true; size = Integer.parseInt(keyVal[1].trim()); }
                        case "CHARSET" -> { cs = true; chset = keyVal[1].trim(); }
                    }
                }
                while (fs.ready() && !(sz&&tn&&cs));
            }
            catch (NumberFormatException e) { System.out.println("? file reading error: invalid arguments"); continue; }
            catch (FileNotFoundException ffs) { System.out.println("? file does not exist or path is in an incorrect format"); continue; }
            catch (IOException a) { System.out.printf("? file reading error: %s\n", a.getMessage()); continue; }
            break;
        }
        
        
        // Task 5
        
        if (choice == 'M') com = new ThinkingComputer(scan, contents);
        else com = new PlayableComputer(scan, contents, size, chset);
        com.prompt(true);
        
        
        // Task 4
        /*
        if (choice == 'M') com = new ThinkingComputer(scan);
        else com = new PlayableComputer(scan);
        com.prompt(true);
        */
        
        // Task 3
        //com = new PlayableComputer(Computer.prompt(true));
        
        int res = 0;
        // Outcomes
        // 0 = nobody wins (default state; continue)
        // 1 = Player wins
        // -1 = Player loses
        
        for (int i = 0; i < turns; i++)
        {
            System.out.printf("--------------------------------------------------\nTurn %d:\n",i+1);
            com.entries.add(String.format("--------------------------------------------------\nTurn %d:\n",i+1));
            res = com.invoke(); if (res != 0) break;
        }
        com.entries.add("==================================================\n");
        System.out.println("==================================================");
        switch (res)
        {
            case 0 -> System.out.println("Draw... (._.)");
            case 1 -> System.out.println("You win! (^O^)");
            case -1 -> System.out.println("Computer wins! (o_0)");
        }
        
        System.out.println("You tried to crack: " + com.getCode());
        System.out.println("The computer tried to crack: " + com.getOwnCode());
        
        System.out.println("==================================================");
        switch (res)
        {
            case 0 -> com.entries.add("Neither you nor the computer cracked the code successfully. (._.)");
            case 1 -> com.entries.add("You had successfully cracked the code. (^O^)");
            case -1 -> com.entries.add("The computer had successfully cracked the code. (o_0)");
        }
        
        
        // Task 7+
        
        char fin = 'g';
        do
        {
            System.out.print("""
                                         Would you like to have these results printed?
                                         [Y]: Yes (enter a valid file name)
                                         [N]: No (bye! have a beautiful time!!)
                                         : """);
            fin = scan.next().toUpperCase().charAt(0);
        }
        while (fin != 'Y' && fin != 'N');
        
        scan.nextLine(); // a little sneaky
        
        if (fin == 'Y') while (true)
        {
            System.out.print("""
                                         Enter a valid filename (absolute or relative)
                                         : """);
            
            String path = scan.nextLine().replace("\"","");
            try { Paths.get(path); } catch (InvalidPathException | NullPointerException e)
            { System.out.println("? path is in an incorrect format"); continue; }
            
            boolean succ = com.print(path);
            if (succ) System.out.println("* receipt printed successfully!");
            else System.out.println("* receipt printing failure. Output not saved");
            
            break;
        }
        
        scan.close();
    }
}
