package it.polimi.se2019.card.cost;

import java.io.Serializable;

/**
 * Defines the cost of a card
 */
public class Cost implements Serializable
{
    public static final long serialVersionUID = 6L;

    private final int red, blue, yellow;

    /**
     * Check if the cost in negative or too high
     * Constructs a new Cost for a card with a specific color
     * @param red
     * @param blue
     * @param yellow
     */
    public Cost(int red, int blue, int yellow)
    {
        if(red < 0 || blue < 0 || yellow < 0)throw new NegativeCostException();
        if(red > 3 || blue > 3 || yellow > 3)throw new CostTooHighException();
        this.red = red;
        this.blue = blue;
        this.yellow = yellow;
    }

    public int getRedAmmo()
    {
        return this.red;
    }

    public int getBlueAmmo()
    {
        return this.blue;
    }

    public int getYellowAmmo()
    {
        return this.yellow;
    }
}
