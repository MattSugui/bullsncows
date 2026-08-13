package fonder.acad.games.bullsandcows.task3;

import fonder.acad.games.bullsandcows.IntegerPair;

public class PlayableComputer extends Computer
{
    public PlayableComputer() { super(); }
    public PlayableComputer(String code) { super(); this.ownCode = code; }
    
    protected String ownCode;
    public String getOwnCode() { return this.ownCode; }
    
    public @Override int invoke()
    {
        System.out.println("Your turn!");
        IntegerPair a = scan(prompt(false));
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
    
    protected IntegerPair scanOwn(String seq)
    {
        int bulls = 0, cows = 0;
               
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++)
            if (seq.charAt(i) == this.ownCode.charAt(j))
                if (i == j) bulls++; else cows++;
        
        return new IntegerPair(bulls, cows);
    }
}
