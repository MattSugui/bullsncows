package fonder.acad.games.bullsandcows.task2;

import fonder.acad.games.bullsandcows.IntegerPair;
import java.util.*;

/**
 * The base computer user.
 * @author Adrian
 */
public class Computer
{
    public Computer() { this.randomiser = new Random(); this.code = think(); }
    
/*    public Computer(String code)
    {
        this.randomiser = new Random();
        this.code = think();
        this.ownCode = code;
    }
*/
    
    protected Random randomiser;
    protected String code;
//    protected String ownCode;
    
    public String getCode() { return this.code; }
    
    public static String prompt(boolean first)
    {
        Scanner scan = new Scanner(System.in); String cod;
        while (true)
        {
            System.out.print(first?"Enter your secret code: ":"Enter your guess: ");
            cod = scan.next();
            try { int _ = Integer.parseInt(cod); } catch (NumberFormatException _) { System.out.println("\n? code must be purely numerical"); continue; }
            if (cod.length() != 4) { System.out.println("\n? code must have four digits"); continue; }
            else if (!verify(cod)) { System.out.println("\n? code must not have repeating digits"); continue; }
            
            return cod;
        }
    }
    
    protected static boolean verify(String seq)
    {
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == seq.charAt(j) && i != j) return false;
        return true;
    }
    
        
    public int invoke()
    {
        System.out.println("Your turn!");
        IntegerPair a = scan(prompt(false));
        System.out.printf("%d bull%s, %d cow%s\n", a.item1, a.item1 != 1? "s":"", a.item2, a.item2 != 1? "s":"");
        if (a.item1 == 4) return 1; 
        
        /*
        System.out.println("Computer's turn!");
        IntegerPair b = scan(this.think());
        System.out.printf("%d bull%s, %d cow%s\n", b.Item1, b.Item1 != 1? "s":"", b.Item2, b.Item2 != 1? "s":"");
        if (b.Item1 == 4) return -1;
        */
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
//            System.err.println(blip);
            if (verify(blip)) return blip;
        }
    }
}
