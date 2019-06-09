package it.polimi.se2019.controller;

public class Match
{
    public enum State
    {
        WAITING_FOR_PLAYERS, RUNNING, ENDED
    }

    private GameController gameController;
    private State state;


    public Match()
    {

    }

    public State getState()
    {
        return this.state;
    }
}