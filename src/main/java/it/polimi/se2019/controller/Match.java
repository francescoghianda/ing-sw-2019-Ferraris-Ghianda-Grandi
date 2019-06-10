package it.polimi.se2019.controller;

public class Match
{
    public enum State
    {
        WAITING_FOR_PLAYERS, RUNNING, ENDED
    }

    private final GameController gameController;
    private State state;

    public Match()
    {
        gameController = new GameController(this);
        state = State.WAITING_FOR_PLAYERS;
    }

    public void startGame()
    {
        gameController.startGameTimer();
        state = State.RUNNING;
    }

    public void gameEnded()
    {
        state = State.ENDED;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public GameController getGameController()
    {
        return gameController;
    }

    public State getState()
    {
        return this.state;
    }
}