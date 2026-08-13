package fonder.acad.games.bullsandcows.task7;

import fonder.acad.games.bullsandcows.IntegerPair;
import java.util.*;
import java.io.*;
import java.time.*;
import java.time.temporal.*;

/**
 * A computer that can play against the player.
 * @author Adrian
 */
public class PlayableComputer extends Computer
{
    public PlayableComputer(Scanner scan) { super(scan); }
    public PlayableComputer(Scanner scan, String code) { super(scan); this.ownCode = code; }
    public PlayableComputer(Scanner scan, String[] prem) { super(scan,prem); }
    public PlayableComputer(Scanner scan, String[] prem, String code) { super(scan,prem); this.ownCode = code; }
    
    protected String ownCode;
    public String getOwnCode() { return this.ownCode; } protected void setOwnCode(String code) { this.ownCode = code; }
    
    
    public @Override int invoke()
    {
        System.out.println("Your turn!");
        String p = respond();
        IntegerPair a = scan(p);
        System.out.printf("%d bull%s, %d cow%s\n", a.item1, a.item1 != 1? "s":"", a.item2, a.item2 != 1? "s":"");
        
        entries.add(String.format("You submitted %s, which revealed %s bull%s and %s cow%s.\n",
               p,
               a.item1 == 0? "no":Integer.toString(a.item1),
               a.item1 != 1? "s":"",
               a.item2 == 0? "no":Integer.toString(a.item2),
               a.item2 != 1? "s":""));
        
        if (a.item1 == 4) return 1; 
        
        System.out.println();
        
        System.out.println("Computer's turn!");
        String thing = this.think();
        IntegerPair b = scanOwn(thing);
        System.out.println("It thought of: "+thing);
        System.out.printf("%d bull%s, %d cow%s\n", b.item1, b.item1 != 1? "s":"", b.item2, b.item2 != 1? "s":"");
        
        entries.add(String.format("The computer submitted %s, which revealed %s bull%s and %s cow%s.\n",
               thing,
               b.item1 == 0? "no":Integer.toString(b.item1),
               b.item1 != 1? "s":"",
               b.item2 == 0? "no":Integer.toString(b.item2),
               b.item2 != 1? "s":""));
        
        if (b.item1 == 4) return -1;
        
        return 0;
    }
    
    public @Override String prompt(boolean first)
    {
        String cod;
        while (true)
        {
            System.out.print(first?"Enter your secret code: ":"Enter your guess: ");
            cod = scan.next();
            try { int _ = Integer.parseInt(cod); } catch (NumberFormatException _) { System.out.println("\n? code must be purely numerical"); continue; }
            if (cod.length() != 4) { System.out.println("\n? code must have four digits"); continue; }
            else if (!verify(cod)) { System.out.println("\n? code must not have repeating digits"); continue; }
            
            if (first) { this.setOwnCode(cod); return null; } else return cod;
        }      
    }
    
    public @Override boolean print(String path)
    {
        try (FileWriter logger = new FileWriter(path))
        {
            logger.write(String.format("""
                                       Matt's Bulls and Cows Game!
                                       Created 2025. Released under the terms of the data privacy guidelines of the University of Santo Tomas (Do not redistribute!)
                                       
                                       Game results receipt
                                       Session no. %sw%d-%s
                                       
                                       Your secret code against the computer was %s.
                                       The computer's secret code against you was %s.
                                       ==================================================
                                       """,
                        Integer.toString(LocalDate.now().getYear()).substring(1, 4),
                        LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()),
                        Long.toHexString(System.currentTimeMillis()),
                        ownCode,code));
            for (String line: entries.toArray(String[]::new)) logger.write(line);
        }
        catch (IOException e)
        {
            System.out.printf("! error occurred whilst writing file: %s\n", e.getMessage());
            return false;
        }
        return true;
    }
    
    protected IntegerPair scanOwn(String seq)
    {
        int bulls = 0, cows = 0;
               
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == this.ownCode.charAt(j))
                if (i == j) bulls++; else cows++;
        
        return new IntegerPair(bulls, cows);
    }
}
