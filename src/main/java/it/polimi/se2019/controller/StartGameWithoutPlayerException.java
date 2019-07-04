package it.polimi.se2019.controller;

/**
 *exception launched when the game starts without players
 */
public class StartGameWithoutPlayerException extends RuntimeException
{
    public StartGameWithoutPlayerException()
    {
        super();
    }
}
