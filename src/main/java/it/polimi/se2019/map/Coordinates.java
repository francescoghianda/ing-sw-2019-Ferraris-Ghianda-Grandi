package it.polimi.se2019.map;

import java.io.Serializable;

/**
 * Defines the cartesian coordinates in order to calculate manatthan distance
 */
public final class Coordinates implements Serializable
{
    public static final long serialVersionUID = 4L;

    private final int x;
    private final int y;

    public Coordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public final int getX()
    {
        return x;
    }

    public final int getY()
    {
        return y;
    }

    public int getManhattanDistanceFrom(Coordinates coordinates)
    {
        return Math.abs(getX() - coordinates.getX())+ Math.abs(getY() - coordinates.getY());
    }

    @Override
    public final boolean equals(Object coords)
    {
        if(!(coords instanceof Coordinates))return false;
        return ((Coordinates)coords).x == x && ((Coordinates)coords).y == y;
    }

    @Override
    public final int hashCode()
    {
        return x+y;
    }

    @Override
    public String toString()
    {
        return "("+x+", "+y+")";
    }
}
