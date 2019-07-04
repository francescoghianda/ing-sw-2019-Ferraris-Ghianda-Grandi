package it.polimi.se2019.controller;

import it.polimi.se2019.utils.constants.GameColor;

import java.io.Serializable;

/**
 * describes the deaath with its parameters killercolor, deadcolor, overkill
 */
public class Death implements Serializable
{
    private final GameColor killerColor;
    private final GameColor deadColor;
    private final boolean overKill;

    public Death(GameColor killerColor, GameColor deadColor, boolean overKill)
    {
        this.killerColor = killerColor;
        this.deadColor = deadColor;
        this.overKill = overKill;
    }

    public GameColor getKillerColor()
    {
        return killerColor;
    }

    public GameColor getDeadColor()
    {
        return deadColor;
    }

    /**
     *
     * @return overkill if it is overkill
     */
    public boolean isOverKill()
    {
        return overKill;
    }
}
