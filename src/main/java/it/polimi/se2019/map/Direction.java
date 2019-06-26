package it.polimi.se2019.map;

public enum Direction
{
    NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, NaD;


    public boolean isCardinalDirection()
    {
        return this == NORTH || this == SOUTH || this == EAST || this == WEST;
    }
}
