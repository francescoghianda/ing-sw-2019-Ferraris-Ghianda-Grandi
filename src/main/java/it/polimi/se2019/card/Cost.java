package it.polimi.se2019.card;

public class Cost
{
    private final int red, blue, yellow;

    public Cost(int red, int blue, int yellow)
    {
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
