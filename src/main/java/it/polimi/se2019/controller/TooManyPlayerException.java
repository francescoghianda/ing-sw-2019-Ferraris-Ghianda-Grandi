package it.polimi.se2019.controller;

/**
 * exception launched when the game starts with too many players
 */
public class TooManyPlayerException extends RuntimeException
{
    public TooManyPlayerException()
    {
        super();
    }
}
