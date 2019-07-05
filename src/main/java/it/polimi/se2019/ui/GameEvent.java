package it.polimi.se2019.ui;

/**
 * manages an event during the game
 */
public class GameEvent
{
    public static final int IS_YOUR_ROUND = 0;

    private final int eventCode;

    public GameEvent(int eventCode)
    {
        this.eventCode = eventCode;
    }

    public int getEventCode()
    {
        return this.eventCode;
    }

}
