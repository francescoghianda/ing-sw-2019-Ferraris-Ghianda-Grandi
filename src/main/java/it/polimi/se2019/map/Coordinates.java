package it.polimi.se2019.map;

import java.io.Serializable;

public final class Coordinates implements Serializable
{
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
}
