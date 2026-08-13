package fonder.acad.games.bullsandcows;

/**
 * A simple integer tuple of two items.
 * @author Adrian
 */

public class IntegerPair
{
    public IntegerPair(int a, int b) { this.item1 = a; this.item2 = b; }
    
    public int item1; public int item2;
    
    public int sum() { return this.item1 + this.item2; }
}