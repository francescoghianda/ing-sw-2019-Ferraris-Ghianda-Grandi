package it.polimi.se2019.controller.action;

import it.polimi.se2019.controller.CanceledActionException;
import it.polimi.se2019.controller.GameController;
import it.polimi.se2019.player.ImpossibleActionException;

/**
 * abstract cladd that defines the gamecontroller
 */
public abstract class ControllerAction
{
    protected GameController gameController;

    ControllerAction(GameController gameController)
    {
        this.gameController = gameController;
    }

    public abstract void execute() throws CanceledActionException, ImpossibleActionException;
}
