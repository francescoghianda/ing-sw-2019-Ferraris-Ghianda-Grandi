package it.polimi.se2019.controller;

public class CanceledActionException extends Exception
{
    public enum Cause
    {
        ERROR, IMPOSSIBLE_ACTION, CANCELED_BY_USER
    }

    private final Cause cause;

    public CanceledActionException(Cause cause)
    {
        super("Cause: "+cause);
        this.cause = cause;
    }

    public final Cause getCanceledCause()
    {
        return cause;
    }
}
