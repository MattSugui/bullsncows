package fonder.acad.games.bullsandcows.task6;

import fonder.acad.games.bullsandcows.IntegerPair;
import java.util.*;

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
        IntegerPair a = scan(respond());
        System.out.printf("%d bull%s, %d cow%s\n", a.item1, a.item1 != 1? "s":"", a.item2, a.item2 != 1? "s":"");
        if (a.item1 == 4) return 1; 
        
        System.out.println();
        
        System.out.println("Computer's turn!");
        String thing = this.think();
        IntegerPair b = scanOwn(thing);
        System.out.println("It thought of: "+thing);
        System.out.printf("%d bull%s, %d cow%s\n", b.item1, b.item1 != 1? "s":"", b.item2, b.item2 != 1? "s":"");
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
    
    protected IntegerPair scanOwn(String seq)
    {
        int bulls = 0, cows = 0;
               
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == this.ownCode.charAt(j))
                if (i == j) bulls++; else cows++;
        
        return new IntegerPair(bulls, cows);
    }
}
