package it.polimi.se2019.player;

/**
 * launched when an action is not allowed. It also defines the cause of the impossibility
 */
public class ImpossibleActionException extends Exception
{

    public enum Cause
    {
        WEAPON_NOT_LOADED, EFFECT_NOT_ENABLED, INSUFFICIENT_AMMO, INVALID_POWER_UP, INVALID_PLAYER, INVALID_BLOCK, OTHER
    }

    private Cause cause;

    public ImpossibleActionException(Cause cause)
    {
        super();
        this.cause = cause;
    }

    public Cause cause()
    {
        return cause;
    }

}
