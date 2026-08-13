package fonder.acad.games.bullsandcows.task7;

import fonder.acad.games.bullsandcows.IntegerPair;
import java.util.*;

/**
 * A computer that can keep track of its guesses.
 * @author Adrian
 */
public class ThinkingComputer extends PlayableComputer
{
    public ThinkingComputer(Scanner scan)
    {
        super(scan); memory = new ArrayList<>();
        codeHistory = new ArrayList<>(); maxLen = 4;
        initialiseComponent();
    }
    public ThinkingComputer(Scanner scan, String code)
    {
        super(scan, code); memory = new ArrayList<>();
        codeHistory = new ArrayList<>(); maxLen = 4;
        initialiseComponent();
    }
    public ThinkingComputer(Scanner scan, String[] prem)
    {
        super(scan, prem); memory = new ArrayList<>();
        codeHistory = new ArrayList<>(); maxLen = 4;
        initialiseComponent();
    }
    public ThinkingComputer(Scanner scan, String[] prem, String code)
    {
        super(scan, prem, code); memory = new ArrayList<>();
        codeHistory = new ArrayList<>(); maxLen = 4;
        initialiseComponent();
    }
    

    private static final String CHARSET = "0123456789"; // where the ruleset comes from
    
    private final ArrayList<Cattle> memory;
    private final int maxLen;
    private final ArrayList<String> codeHistory;
    private int turnCount;
    
    private void initialiseComponent()
    {
        for (char c: CHARSET.toCharArray()) memory.add(new Cattle(c));
    }
    
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
        
        System.out.println("Computer's turn!"); String g = "";
        String thing = think();
        IntegerPair b = scanOwn(thing);
        System.out.println("It thought of: "+thing);
        remember(thing, b);
        codeHistory.add(thing);
        
        System.out.printf("%d bull%s, %d cow%s\n", b.item1, b.item1 != 1? "s":"", b.item2, b.item2 != 1? "s":"");
                
        entries.add(String.format("The computer submitted %s, which revealed %s bull%s and %s cow%s.\n",
               thing,
               b.item1 == 0? "no":Integer.toString(b.item1),
               b.item1 != 1? "s":"",
               b.item2 == 0? "no":Integer.toString(b.item2),
               b.item2 != 1? "s":""));
        
        turnCount++;
        
        if (b.item1 == 4) return -1;
        return 0;
    }
    
    protected @Override String think()
    {
        // this is fine, as afterwards the computer will remember.
        // after which, this will essentially run never again
        if (codeHistory == null || codeHistory.isEmpty() || memory == null || memory.isEmpty()) return super.think();
        
        String ret; char[] temp = new char[maxLen]; boolean[] parity = new boolean[maxLen];
        ArrayList<Cattle> midPriority = new ArrayList<>(),
                          lowPriority = new ArrayList<>(memory);
        boolean sel;
        
        for (var thing: memory) if (thing.getStatus() >= 1) midPriority.add(thing);
        for (int i = 0; i < lowPriority.size(); i++)
            if (midPriority.contains(lowPriority.get(i))) lowPriority.remove(i);
        
        for (int i = 0; i < memory.size(); i++) if (memory.get(i).isBull())
        {
            temp[memory.get(i).getLocation()] = memory.get(i).getValue();
            parity[memory.get(i).getLocation()] = true; 
            midPriority.remove(memory.get(i));
        }
        
        int tempCount = 0;
        while (true)
        {
            Cattle select = null;
            sel = randomiser.nextInt(0,3) >= 1;
            
            if (parity[tempCount]) { tempCount++; if (tempCount == maxLen) break; else continue; }
            
            if (sel && !midPriority.isEmpty())
            {
                select = midPriority.get(randomiser.nextInt(0,midPriority.size()));
                midPriority.remove(select);
            }
            else if (!lowPriority.isEmpty())
            {
                select = lowPriority.get(randomiser.nextInt(0,lowPriority.size()));
                lowPriority.remove(select);
            }
            
            if (select == null) continue;
            if (!parity[tempCount]) { temp[tempCount] = select.getValue(); parity[tempCount] = true; tempCount++; }
            if (tempCount == maxLen) break;
        }
        ret = String.valueOf(temp);
        assert(verify(ret)): "computer apparently has learning disabilities.";
        if(!verify(ret)||codeHistory.contains(ret)) return think(); // lmao now do it again
        return ret;
    }

    private void remember(String code, IntegerPair result)
    {
        int bullAssert = result.item1, cowAssert = result.item2;
        
        if (codeHistory.isEmpty())
        {
            for (char c: code.toCharArray())
            {
                Cattle n = memory.get(memory.indexOf(new Cattle(c)));
                if (bullAssert > 0) { n.setStatus(2); bullAssert--;}
                else if (cowAssert > 0) { n.setStatus(0); cowAssert--;}
            }
            return;
        }
        
        String oldCode = this.codeHistory.get(turnCount-1);
        
        char[] newfarm = code.toCharArray(),
               oldfarm = oldCode.toCharArray();
        
        ArrayList<Cattle> reckon = new ArrayList<>(memory);
        
        for (int i = 0; i < this.maxLen; i++)
            if (!reckon.contains(new Cattle(newfarm[i])))
                reckon.add(memory.get(memory.indexOf(new Cattle(newfarm[i]))));
        
        for (int i = 0; i < this.maxLen; i++)
            if (!reckon.contains(new Cattle(oldfarm[i]))) 
               reckon.add(memory.get(memory.indexOf(new Cattle(oldfarm[i]))));
        
        for (int i = 0; i < this.maxLen; i++)
        {
            char[] testSeqOtNNew = new char[maxLen],
                   testSeqOtNOld = new char[maxLen],
                   testSeqNtONew = new char[maxLen],
                   testSeqNtOOld = new char[maxLen];
            char detectedNew = code.charAt(i),
                 detectedOld = oldCode.charAt(i);
            Cattle captureNew = reckon.get(reckon.indexOf(new Cattle(detectedNew))),
                   captureOld = reckon.get(reckon.indexOf(new Cattle(detectedOld)));
            
            if (captureNew.isBull() || captureOld.isBull()) continue;
            
            if (code.indexOf(detectedNew) != -1) testSeqOtNNew[code.indexOf(detectedNew)] = detectedNew;
            if (oldCode.indexOf(detectedNew) != -1) testSeqOtNOld[oldCode.indexOf(detectedNew)] = detectedNew;
            if (code.indexOf(detectedOld) != -1) testSeqNtONew[code.indexOf(detectedOld)] = detectedOld;
            if (oldCode.indexOf(detectedOld) != -1) testSeqNtOOld[oldCode.indexOf(detectedOld)] = detectedOld;
            
            IntegerPair otnA = scanOwn(String.valueOf(testSeqOtNNew)),
                        otnB = scanOwn(String.valueOf(testSeqOtNOld)),
                        ntoA = scanOwn(String.valueOf(testSeqNtONew)),
                        ntoB = scanOwn(String.valueOf(testSeqNtOOld));
            
            if (otnA.item1 > otnB.item1) { captureNew.makeBull(); captureNew.setLocation(code.indexOf(detectedNew)); }
            else if (otnA.sum() > otnB.sum()) captureNew.makeCow();
            else if (otnA.sum() == otnB.sum() && otnA.sum() == 0)
                captureNew.blacklist();
            if (ntoA.item1 < ntoB.item1) { captureOld.makeBull(); captureOld.setLocation(oldCode.indexOf(detectedOld));}
            else if (ntoA.sum() < ntoB.sum()) captureOld.makeCow();
            else if (ntoA.sum() == ntoB.sum() && ntoB.sum() == 0)
                captureOld.blacklist();

        }
    }
}


class Cattle
{
    // .ctor
    public Cattle(char c) { this.value = c; this.status = -1; }
    
    // backing fields
    private final char value;
    private int status; // -2 = blacklisted; -1 = nothing; 0 = heifer (unconfirmed cow); 1 = cow; 2 = steer (unconfirmed bull); 3 = bull
    private int location;
    
    // properties
    public char getValue() { return this.value; }
    public int getStatus() { return this.status; }                              public void setStatus(int value) { this.status = value; }
    public int getLocation() { return this.location; }                          public void setLocation(int loc) { this.location = loc; }
    
    // operations
    public void makeCow() { this.status = 1; }
    public boolean isBull() { return this.status == 3; }
    public void makeBull() { this.status = 3; }
    public void blacklist() { this.status = -2; }
    
    // java.lang.Object
    public @Override String toString() { return String.valueOf(getValue()); }
    public @Override boolean equals(Object other)
    {        
        return (other != null && other instanceof Cattle) 
               && (this.getValue() == ((Cattle)other).getValue());
    }
    public @Override int hashCode() { return 29 * 3 + this.getValue(); }
}