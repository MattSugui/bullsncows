package fonder.acad.games.bullsandcows.task6;

import fonder.acad.games.bullsandcows.IntegerPair;
import java.util.*;

/**
 * The base computer user.
 * @author Adrian
 */
public class Computer
{
    public Computer(Scanner scan) { this.randomiser = new Random(); this.code = think(); this.scan = scan; }
    public Computer(Scanner scan, String[] prem) { this.randomiser = new Random(); this.code = think(); this.scan = scan; this.premoves = prem; }
    
    protected Random randomiser;
    protected String code;
    protected Scanner scan;
    protected int index;
    protected String[] premoves;
    
    public String getCode() { return this.code; } 
    
    public Scanner getScanner() { return this.scan; } public void setScanner(Scanner scan) { this.scan = scan; }
    
    public static boolean verify(String seq)
    {
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == seq.charAt(j) && i != j) return false;
        return true;
    }
 
    public String respond()
    {
        if (premoves != null && index < premoves.length)
        {
            System.out.println("Reading from premove queue: "+premoves[index]);
            return premoves[index++];
        }
        return prompt(false);
    }
    
    public String prompt(boolean first)
    {
        String cod;
        while (true)
        {
            System.out.print("Enter your guess: ");
            cod = scan.next();
            try { int _ = Integer.parseInt(cod); } catch (NumberFormatException _) { System.out.println("\n? code must be purely numerical"); continue; }
            if (cod.length() != 4) { System.out.println("\n? code must have four digits"); continue; }
            else if (!verify(cod)) { System.out.println("\n? code must not have repeating digits"); continue; }
            
            return cod;
        }      
    }
    
    public int invoke()
    {
        System.out.println("Your turn!");
        IntegerPair a = scan(prompt(false));
        System.out.printf("%d bull%s, %d cow%s\n", a.item1, a.item1 != 1? "s":"", a.item2, a.item2 != 1? "s":"");
        if (a.item1 == 4) return 1; 

        return 0;
    }
    
    
    protected IntegerPair scan(String seq)
    {        
        int bulls = 0, cows = 0;
               
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == this.code.charAt(j))
                if (i == j) bulls++; else cows++;
        
        return new IntegerPair(bulls, cows);
    }

    protected String think()
    {
        while (true)
        {
            String blip = String.format("%04d", randomiser.nextInt(0,9999));
            if (verify(blip)) return blip;
        }
    }
}