package it.polimi.se2019.controller;

/**
 * expcetion when an action is deleted. It at first establishes the cause of that problem (with an enum value which can be Earaaaror.canceled_by_user
 *
 */
public class CanceledActionException extends Exception
{
    public enum Cause
    {
        ERROR, CANCELED_BY_USER
    }

    private final Cause cause;
    private final String message;

    public CanceledActionException(Cause cause)
    {
        super("Cause: "+cause);
        this.message = "";
        this.cause = cause;
    }

    public CanceledActionException(Cause cause, String message)
    {
        super("Cause: "+cause);
        this.message = message;
        this.cause = cause;
    }

    public String getCauseMessage()
    {
        return this.message;
    }

    public final Cause getCanceledCause()
    {
        return cause;
    }
}
